package edu.java.scrapper;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class LiquibaseIntegrationTest extends IntegrationTest {

    @Test
    public void example() {
        // Assert
        long expectedChatId = 1000L;
        long expectedLinkId = 2000L;
        String expectedUrl = "https://bezukh.wixsite.com/";

        // Act
        jdbcTemplate.update("INSERT INTO link_tracker_db.chat (id) VALUES (?)", expectedChatId);
        jdbcTemplate.update("INSERT INTO link_tracker_db.link (id, url) VALUES (?, ?)", expectedLinkId, expectedUrl);
        jdbcTemplate.update("INSERT INTO link_tracker_db.chats_links (id_chat, id_link) VALUES (?, ?)", expectedChatId, expectedLinkId);

        Integer actualChatId = jdbcTemplate.queryForObject("SELECT id FROM link_tracker_db.chat WHERE id = ?", Integer.class, expectedChatId);
        String  actualUrl    = jdbcTemplate.queryForObject("SELECT url FROM link_tracker_db.link WHERE id = ?", String.class, expectedLinkId);
        Integer actualLinkId = jdbcTemplate.queryForObject("SELECT id_link FROM link_tracker_db.chats_links WHERE id_chat = ?", Integer.class, expectedChatId);

        // Assert
        assertThat(actualChatId).isEqualTo(expectedChatId);
        assertThat(actualUrl).isEqualTo(expectedUrl);
        assertThat(actualLinkId).isEqualTo(expectedLinkId);
    }
}
