package edu.java.scrapper.repositories.jooq;

import edu.java.scrapper.exceptions.EntityNotFoundException;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.LinkRepository;
import edu.java.scrapper.repositories.jooq.link_tracker_db.tables.records.LinkRecord;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.scrapper.repositories.jooq.link_tracker_db.Tables.CHATS_LINKS;
import static edu.java.scrapper.repositories.jooq.link_tracker_db.Tables.LINK;
import static org.jooq.impl.DSL.select;


@RequiredArgsConstructor
@SuppressWarnings({"MultipleStringLiterals"})
public class JooqLinkRepository implements LinkRepository {
    private final DSLContext dslContext;

    @Override
    @Transactional
    public Link add(Long chatId, URI url) {
        LinkRecord addedLinkRecord = dslContext
            .selectFrom(LINK)
            .where(LINK.URL.eq(url.toString()))
            .fetchOne();

        if (addedLinkRecord == null) {
            addedLinkRecord = dslContext
                .insertInto(LINK, LINK.URL, LINK.LAST_UPDATE)
                .values(url.toString(), OffsetDateTime.now())
                .returning()
                .fetchOne();
        }

        dslContext
            .insertInto(CHATS_LINKS, CHATS_LINKS.ID_CHAT, CHATS_LINKS.ID_LINK)
            .values(chatId, addedLinkRecord.getId())
            .execute();

        return new Link()
            .setId(addedLinkRecord.getId())
            .setUrl(URI.create(addedLinkRecord.getUrl()))
            .setLastUpdate(addedLinkRecord.getLastUpdate());
    }

    @Override
    @Transactional
    public void remove(Long linkId) {
        int rowsAffected = dslContext
            .deleteFrom(LINK)
            .where(LINK.ID.eq(linkId))
            .execute();

        if (rowsAffected == 0) {
            throw new EntityNotFoundException("Ссылка с id " + linkId + " не найдена");
        }
    }

    @Override
    @Transactional
    public void remove(Long chatId, URI url) {
        int rowsAffected = dslContext
            .delete(CHATS_LINKS)
            .where(CHATS_LINKS.ID_CHAT.eq(chatId)
                .and(CHATS_LINKS.ID_LINK.in(dslContext
                    .select(LINK.ID)
                    .from(LINK)
                    .where(LINK.URL.eq(url.toString()))
                ))
            )
            .execute();

        if (rowsAffected == 0) {
            throw new EntityNotFoundException("Чат " + chatId + " не отслеживает ссылку " + url);
        }
    }

    @Override
    @Transactional
    public void removeAll() {
        dslContext.delete(LINK).execute();
    }

    @Override
    public void removeUnusedLinks() {
        dslContext
            .delete(LINK)
            .where(LINK.ID.notIn(
                select(CHATS_LINKS.ID_LINK)
                    .from(CHATS_LINKS)
            ))
            .execute();
    }

    @Override
    public Link findByUrl(URI url) throws DataAccessException {
        return dslContext
            .selectFrom(LINK)
            .where(LINK.URL.eq(url.toString()))
            .fetchOneInto(Link.class);
    }

    @Override
    public List<Link> findAll() {
        return dslContext
            .selectFrom(LINK)
            .fetchInto(Link.class);
    }

    @Override
    public List<Link> findAllLinksByChatId(Long chatId) {
        return dslContext
            .select(LINK.fields())
            .from(LINK)
            .where(LINK.ID.in(
                dslContext.select(CHATS_LINKS.ID_LINK)
                    .from(CHATS_LINKS)
                    .where(CHATS_LINKS.ID_CHAT.eq(chatId))
            ))
            .fetchInto(Link.class);
    }

    @Override
    public List<Link> findByOldestUpdates(int count) {
        return dslContext
            .selectFrom(LINK)
            .orderBy(LINK.LAST_UPDATE)
            .limit(count)
            .fetchInto(Link.class);
    }

    @Override
    public void setLastUpdate(URI url, OffsetDateTime time) {
        dslContext
            .update(LINK)
            .set(LINK.LAST_UPDATE, time)
            .where(LINK.URL.eq(url.toString()))
            .execute();
    }
}
