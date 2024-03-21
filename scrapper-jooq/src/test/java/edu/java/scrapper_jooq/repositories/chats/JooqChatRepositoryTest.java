package edu.java.scrapper_jooq.repositories.chats;

import edu.java.scrapper_jooq.exceptions.EntityNotFoundException;
import edu.java.scrapper_jooq.models.Chat;
import edu.java.scrapper_jooq.models.Link;
import edu.java.scrapper_jooq.repositories.IntegrationTest;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class JooqChatRepositoryTest extends IntegrationTest {
    @Test
    void add() {
        // Arrange
        Long chatId = 1L;

        // Act
        Chat chat1 = jooqChatRepository.add(chatId);
        List<Chat> chats = jooqChatRepository.findAll();

        // Assert
        assertThat(chats).containsOnly(chat1);
    }

    @Test
    void addDuplicate() {
        // Arrange
        Long chatId = 1L;
        jooqChatRepository.add(chatId);

        // Act
        Throwable thrown = catchThrowable(() -> {
            jooqChatRepository.add(chatId);
        });

        // Assert
        assertThat(thrown).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void remove() {
        // Arrange
        Long chatId = 1L;
        jooqChatRepository.add(chatId);

        // Act
        jooqChatRepository.remove(chatId);
        List<Chat> chats = jooqChatRepository.findAll();

        // Assert
        assertThat(chats).isEmpty();
    }

    @Test
    void removeEntityNotFound() {
        // Arrange
        Long chatId = 1L;

        // Act
        Throwable thrown = catchThrowable(() -> {
            jooqChatRepository.remove(chatId);
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

        jooqChatRepository.add(chat1Id);
        jooqChatRepository.add(chat2Id);

        jooqLinkRepository.add(chat1Id, link1Url);
        jooqLinkRepository.add(chat1Id, link2Url);
        Link link21 = jooqLinkRepository.add(chat2Id, link1Url);
        Link link22 = jooqLinkRepository.add(chat2Id, link2Url);

        // Act
        jooqChatRepository.remove(chat1Id);
        List<Link> links = jooqLinkRepository.findAll();

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

        jooqChatRepository.add(chat1Id);
        jooqChatRepository.add(chat2Id);
        jooqChatRepository.add(chat3Id);

        // Act
        jooqChatRepository.removeAll();
        List<Chat> chats = jooqChatRepository.findAll();

        // Assert
        assertThat(chats).isEmpty();
    }

    @Test
    void findById() {
        // Arrange
        Long chat1Id = 1L;
        Long chat2Id = 2L;
        Long chat3Id = 3L;

        Chat expectedChat1 = jooqChatRepository.add(chat1Id);
        jooqChatRepository.add(chat2Id);
        jooqChatRepository.add(chat3Id);

        // Act
        Chat actualChat1 = jooqChatRepository.findById(chat1Id);

        // Assert
        assertThat(actualChat1).isEqualTo(expectedChat1);
    }

    @Test
    void findAll() {
        // Arrange
        Long chat1Id = 1L;
        Long chat2Id = 2L;
        Long chat3Id = 3L;

        Chat chat1 = jooqChatRepository.add(chat1Id);
        Chat chat2 = jooqChatRepository.add(chat2Id);
        Chat chat3 = jooqChatRepository.add(chat3Id);

        // Act
        List<Chat> chats = jooqChatRepository.findAll();

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

        Chat chat1 = jooqChatRepository.add(chat1Id);
        Chat chat2 = jooqChatRepository.add(chat2Id);
        jooqChatRepository.add(chat3Id);

        jooqLinkRepository.add(chat1Id, link1Url);
        jooqLinkRepository.add(chat2Id, link1Url);
        jooqLinkRepository.add(chat2Id, link2Url);
        jooqLinkRepository.add(chat3Id, link3Url);

        // Act
        List<Chat> chats = jooqChatRepository.findAllChatsByUrl(link1Url);

        // Assert
        assertThat(chats).containsOnly(
            chat1, chat2
        );
    }
}
