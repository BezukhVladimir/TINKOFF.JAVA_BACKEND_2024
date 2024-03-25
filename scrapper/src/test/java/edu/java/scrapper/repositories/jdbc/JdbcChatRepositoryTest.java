package edu.java.scrapper.repositories.jdbc;

import edu.java.scrapper.exceptions.EntityNotFoundException;
import edu.java.scrapper.models.Chat;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.JdbcIntegrationTest;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


class JdbcChatRepositoryTest extends JdbcIntegrationTest {
    @Test
    void add() {
        // Arrange
        Long chatId = 1L;

        // Act
        Chat chat1 = jdbcChatRepository.add(chatId);
        List<Chat> chats = jdbcChatRepository.findAll();

        // Assert
        assertThat(chats).containsOnly(chat1);
    }

    @Test
    void addDuplicate() {
        // Arrange
        Long chatId = 1L;
        jdbcChatRepository.add(chatId);

        // Act
        Throwable thrown = catchThrowable(() -> {
            jdbcChatRepository.add(chatId);
        });

        // Assert
        assertThat(thrown).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void remove() {
        // Arrange
        Long chatId = 1L;
        jdbcChatRepository.add(chatId);

        // Act
        jdbcChatRepository.remove(chatId);
        List<Chat> chats = jdbcChatRepository.findAll();

        // Assert
        assertThat(chats).isEmpty();
    }

    @Test
    void removeEntityNotFound() {
        // Arrange
        Long chatId = 1L;

        // Act
        Throwable thrown = catchThrowable(() -> {
            jdbcChatRepository.remove(chatId);
        });

        // Assert
        assertThat(thrown).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void removeFromChatsLinks() {
        // Arrange
        Long chat1Id = 1L;
        Long chat2Id = 2L;
        URI link1Url = URI.create("https://first.com");
        URI link2Url = URI.create("https://second.com");

        jdbcChatRepository.add(chat1Id);
        jdbcChatRepository.add(chat2Id);

        jdbcLinkRepository.add(chat1Id, link1Url);
        jdbcLinkRepository.add(chat1Id, link2Url);
        Link link21 = jdbcLinkRepository.add(chat2Id, link1Url);
        Link link22 = jdbcLinkRepository.add(chat2Id, link2Url);

        // Act
        jdbcChatRepository.remove(chat1Id);
        List<Link> links = jdbcLinkRepository.findAll();

        // Assert
        assertThat(links).containsOnly(
            link21, link22
        );
    }

    @Test
    void removeAll() {
        // Arrange
        Long chat1Id = 1L;
        Long chat2Id = 2L;
        Long chat3Id = 3L;

        jdbcChatRepository.add(chat1Id);
        jdbcChatRepository.add(chat2Id);
        jdbcChatRepository.add(chat3Id);

        // Act
        jdbcChatRepository.removeAll();
        List<Chat> chats = jdbcChatRepository.findAll();

        // Assert
        assertThat(chats).isEmpty();
    }

    @Test
    void findById() {
        // Arrange
        Long chat1Id = 1L;
        Long chat2Id = 2L;
        Long chat3Id = 3L;

        Chat expectedChat1 = jdbcChatRepository.add(chat1Id);
        jdbcChatRepository.add(chat2Id);
        jdbcChatRepository.add(chat3Id);

        // Act
        Chat actualChat1 = jdbcChatRepository.findById(chat1Id);

        // Assert
        assertThat(actualChat1).isEqualTo(expectedChat1);
    }

    @Test
    void findAll() {
        // Arrange
        Long chat1Id = 1L;
        Long chat2Id = 2L;
        Long chat3Id = 3L;

        Chat chat1 = jdbcChatRepository.add(chat1Id);
        Chat chat2 = jdbcChatRepository.add(chat2Id);
        Chat chat3 = jdbcChatRepository.add(chat3Id);

        // Act
        List<Chat> chats = jdbcChatRepository.findAll();

        // Assert
        assertThat(chats).containsOnly(
            chat1, chat2, chat3
        );
    }

    @Test
    void findAllChatsByUrl() {
        // Arrange
        Long chat1Id = 1L;
        Long chat2Id = 2L;
        Long chat3Id = 3L;
        URI link1Url = URI.create("https://first.com");
        URI link2Url = URI.create("https://second.com");
        URI link3Url = URI.create("https://third.com");

        Chat chat1 = jdbcChatRepository.add(chat1Id);
        Chat chat2 = jdbcChatRepository.add(chat2Id);
        jdbcChatRepository.add(chat3Id);

        jdbcLinkRepository.add(chat1Id, link1Url);
        jdbcLinkRepository.add(chat2Id, link1Url);
        jdbcLinkRepository.add(chat2Id, link2Url);
        jdbcLinkRepository.add(chat3Id, link3Url);

        // Act
        List<Chat> chats = jdbcChatRepository.findAllChatsByUrl(link1Url);

        // Assert
        assertThat(chats).containsOnly(
            chat1, chat2
        );
    }
}
