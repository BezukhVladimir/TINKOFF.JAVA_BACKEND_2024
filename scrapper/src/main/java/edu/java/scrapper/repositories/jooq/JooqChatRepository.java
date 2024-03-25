package edu.java.scrapper.repositories.jooq;

import edu.java.scrapper.exceptions.EntityNotFoundException;
import edu.java.scrapper.models.Chat;
import edu.java.scrapper.repositories.ChatRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.scrapper.repositories.jooq.link_tracker_db.tables.Chat.CHAT;
import static edu.java.scrapper.repositories.jooq.link_tracker_db.tables.ChatsLinks.CHATS_LINKS;
import static edu.java.scrapper.repositories.jooq.link_tracker_db.tables.Link.LINK;


@RequiredArgsConstructor
@SuppressWarnings({"MultipleStringLiterals"})
public class JooqChatRepository implements ChatRepository {
    private final DSLContext dslContext;

    @Override
    @Transactional
    public Chat add(Long id) {
        dslContext
            .insertInto(CHAT, CHAT.ID, CHAT.CREATED_AT)
            .values(id, OffsetDateTime.now())
            .execute();

        return findById(id);
    }

    @Override
    @Transactional
    public void remove(Long id) {
        int rowsAffected = dslContext.deleteFrom(CHAT)
            .where(CHAT.ID.eq(id))
            .execute();

        if (rowsAffected == 0) {
            throw new EntityNotFoundException("Чат " + id + " не найден");
        }
    }

    @Override
    @Transactional
    public void removeAll() {
        dslContext.deleteFrom(CHAT).execute();
    }

    @Override
    public Chat findById(Long id) throws DataAccessException {
        return Objects.requireNonNull(dslContext.selectFrom(CHAT)
                .where(CHAT.ID.eq(id))
                .fetchOne())
            .map(r -> new Chat()
                .setId(r.get(CHAT.ID))
                .setCreatedAt(r.get(CHAT.CREATED_AT))
            );
    }

    @Override
    public List<Chat> findAll() {
        return dslContext.selectFrom(CHAT)
            .fetch()
            .map(r -> new Chat()
                .setId(r.get(CHAT.ID))
                .setCreatedAt(r.get(CHAT.CREATED_AT))
            );
    }

    @Override
    public List<Chat> findAllChatsByUrl(URI url) {
        return dslContext.selectDistinct(CHAT.ID, CHAT.CREATED_AT)
            .from(CHATS_LINKS)
            .join(CHAT).on(CHATS_LINKS.ID_CHAT.eq(CHAT.ID))
            .join(LINK).on(CHATS_LINKS.ID_LINK.eq(LINK.ID))
            .where(LINK.URL.eq(url.toString()))
            .fetch()
            .map(r -> new Chat()
                .setId(r.get(CHAT.ID))
                .setCreatedAt(r.get(CHAT.CREATED_AT))
            );
    }
}
