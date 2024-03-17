package edu.java.scrapper.repositories.jdbc;

import edu.java.scrapper.exceptions.EntityNotFoundException;
import edu.java.scrapper.models.Chat;
import edu.java.scrapper.repositories.ChatRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@RequiredArgsConstructor
@SuppressWarnings({"MultipleStringLiterals"})
public class JooqChatRepository implements ChatRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Chat add(Long id) {
        jdbcTemplate.update(
                """
                INSERT INTO link_tracker_db.chat (id, created_at)
                VALUES (?, ?)
                """,
            id, OffsetDateTime.now()
        );

        return findById(id);
    }

    @Override
    @Transactional
    public void remove(Long id) {
        int rowsAffected = jdbcTemplate.update(
            """
            DELETE FROM link_tracker_db.chat
             WHERE id = ?
            """,
            id
        );

        if (rowsAffected == 0) {
            throw new EntityNotFoundException("Chat with id " + id + " not found");
        }
    }

    @Override
    @Transactional
    public void removeAll() {
        jdbcTemplate.update(
            """
            DELETE FROM link_tracker_db.chat
            """
        );
    }

    @Override
    public Chat findById(Long id) throws DataAccessException {
        return jdbcTemplate.queryForObject(
            """
            SELECT *
              FROM link_tracker_db.chat
             WHERE id = ?
            """,
            (rs, rowNum) -> new Chat(
                rs.getLong("id"),
                rs.getObject("created_at", OffsetDateTime.class)
            ),
            id
        );
    }

    @Override
    public List<Chat> findAll() {
        return jdbcTemplate.query(
                """
                SELECT *
                  FROM link_tracker_db.chat
                """,
            (rs, rowNum) -> new Chat(
                rs.getLong("id"),
                rs.getObject("created_at", OffsetDateTime.class)
            )
        );
    }

    @Override
    public List<Chat> findAllChatsByUrl(URI url) {
        return jdbcTemplate.query(
            """
            SELECT c.id, c.created_at
              FROM link_tracker_db.chats_links cl
              JOIN link_tracker_db.chat c ON cl.id_chat = c.id
              JOIN link_tracker_db.link l ON cl.id_link = l.id
             WHERE l.url = ?
             """,
            (rs, rowNum) -> new Chat(
                rs.getLong("id"),
                rs.getObject("created_at", OffsetDateTime.class)
            ),
            url.toString()
        );
    }
}
