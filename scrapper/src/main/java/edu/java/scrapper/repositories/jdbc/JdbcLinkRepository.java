package edu.java.scrapper.repositories.jdbc;

import edu.java.scrapper.exceptions.EntityNotFoundException;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.LinkRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@SuppressWarnings({"MultipleStringLiterals"})
public class JdbcLinkRepository implements LinkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Link add(Long chatId, URI url) {
        Link addedLink;

        try {
            addedLink = findByUrl(url);
        } catch (DataAccessException e) {
            jdbcTemplate.update(
                """
                INSERT INTO link_tracker_db.link (id, url, last_update)
                VALUES (DEFAULT, ?, ?)
                """,
                url.toString(), OffsetDateTime.now()
            );

            addedLink = findByUrl(url);
        }

        jdbcTemplate.update(
                """
                INSERT INTO link_tracker_db.chats_links (id_chat, id_link)
                VALUES (?, ?)
                """,
            chatId, addedLink.getId()
        );

        return addedLink;
    }

    @Override
    @Transactional
    public void remove(Long linkId) {
        int rowsAffected = jdbcTemplate.update(
            """
            DELETE FROM link_tracker_db.link l
             WHERE l.id = ?
            """,
            linkId
        );

        if (rowsAffected == 0) {
            throw new EntityNotFoundException("Link with id " + linkId + "not found");
        }
    }

    @Override
    @Transactional
    public void remove(Long chatId, URI url) {
        int rowsAffected = jdbcTemplate.update(
                """
                DELETE FROM link_tracker_db.chats_links cl
                 USING link_tracker_db.link l
                 WHERE cl.id_chat = ?
                   AND cl.id_link = l.id
                   AND l.url = ?
                """,
            chatId, url.toString()
        );

        if (rowsAffected == 0) {
            throw new EntityNotFoundException("Chat with " + chatId + " doesn't track a Link " + url);
        }
    }

    @Override
    @Transactional
    public void removeAll() {
        jdbcTemplate.update(
            """
            DELETE FROM link_tracker_db.link
            """
        );
    }

    @Override
    public void removeUnusedLinks() {
        jdbcTemplate.update(
            """
            DELETE FROM link_tracker_db.link l
            WHERE l.id NOT IN (
                       SELECT cl.id_link
                         FROM link_tracker_db.chats_links cl)
            """
        );
    }

    @Override
    public Link findByUrl(URI url) throws DataAccessException {
        return jdbcTemplate.queryForObject(
            """
            SELECT *
              FROM link_tracker_db.link
             WHERE url = ?
            """,
            (rs, rowNum) -> new Link()
                .setId(rs.getLong("id"))
                .setUrl(URI.create(rs.getString("url")))
                .setLastUpdate(rs.getObject("last_update", OffsetDateTime.class)
            ),
            url.toString()
        );
    }

    @Override
    public List<Link> findAll() {
        return jdbcTemplate.query(
            """
            SELECT *
              FROM link_tracker_db.link
            """,
            (rs, rowNum) -> new Link()
                .setId(rs.getLong("id"))
                .setUrl(URI.create(rs.getString("url")))
                .setLastUpdate(rs.getObject("last_update", OffsetDateTime.class)
            )
        );
    }

    @Override
    public List<Link> findAllLinksByChatId(Long chatId) {
        return jdbcTemplate.query(
            """
            SELECT l.*
              FROM link_tracker_db.link l
              JOIN link_tracker_db.chats_links cl
                ON l.id = cl.id_link
             WHERE cl.id_chat = ?
            """,
            (rs, rowNum) -> new Link()
                .setId(rs.getLong("id"))
                .setUrl(URI.create(rs.getString("url")))
                .setLastUpdate(rs.getObject("last_update", OffsetDateTime.class)
            ),
            chatId
        );
    }

    @Override
    public List<Link> findByOldestUpdates(int count) {
        return jdbcTemplate.query(
            """
            SELECT *
              FROM link_tracker_db.link
             ORDER BY last_update
             LIMIT ?
            """,
            (rs, rowNum) -> new Link()
                .setId(rs.getLong("id"))
                .setUrl(URI.create(rs.getString("url")))
                .setLastUpdate(rs.getObject("last_update", OffsetDateTime.class)
            ),
            count
        );
    }

    @Override
    public void setLastUpdate(URI url, OffsetDateTime time) {
        jdbcTemplate.update(
            """
            UPDATE link_tracker_db.link
               SET last_update = ?
             WHERE url = ?
            """,
            time, url.toString()
        );
    }
}
