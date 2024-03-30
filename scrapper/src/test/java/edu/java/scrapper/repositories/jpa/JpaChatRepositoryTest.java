package edu.java.scrapper.repositories.jpa;

import edu.java.scrapper.models.Chat;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.JpaIntegrationTest;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class JpaChatRepositoryTest extends JpaIntegrationTest {
    @Test
    void add() {
        // Arrange
        Long chatId = 1L;

        // Act
        Chat chat1 = jpaChatRepository.add(chatId);
        List<Chat> chats = jpaChatRepository.findAll();

        // Assert
        assertThat(chats).containsOnly(chat1);
    }

    @Test
    void remove() {
        // Arrange
        Long chatId = 1L;
        jpaChatRepository.add(chatId);

        // Act
        jpaChatRepository.remove(chatId);
        List<Chat> chats = jpaChatRepository.findAll();

        // Assert
        assertThat(chats).isEmpty();
    }

    @Test
    void removeFromChatsLinks() {
        // Arrange
        Long chat1Id = 1L;
        Long chat2Id = 2L;
        URI link1Url = URI.create("https://first.com");
        URI link2Url = URI.create("https://second.com");

        jpaChatRepository.add(chat1Id);
        jpaChatRepository.add(chat2Id);

        jpaLinkRepository.add(chat1Id, link1Url);
        jpaLinkRepository.add(chat1Id, link2Url);
        Link link21 = jpaLinkRepository.add(chat2Id, link1Url);
        Link link22 = jpaLinkRepository.add(chat2Id, link2Url);

        // Act
        jpaChatRepository.remove(chat1Id);
        List<Link> links = jpaLinkRepository.findAll();

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

        jpaChatRepository.add(chat1Id);
        jpaChatRepository.add(chat2Id);
        jpaChatRepository.add(chat3Id);

        // Act
        jpaChatRepository.removeAll();
        List<Chat> chats = jpaChatRepository.findAll();

        // Assert
        assertThat(chats).isEmpty();
    }

    @Test
    void findById() {
        // Arrange
        Long chat1Id = 1L;
        Long chat2Id = 2L;
        Long chat3Id = 3L;

        Chat expectedChat1 = jpaChatRepository.add(chat1Id);
        jpaChatRepository.add(chat2Id);
        jpaChatRepository.add(chat3Id);

        // Act
        Chat actualChat1 = jpaChatRepository.findById(chat1Id);

        // Assert
        assertThat(actualChat1).isEqualTo(expectedChat1);
    }

    @Test
    void findAll() {
        // Arrange
        Long chat1Id = 1L;
        Long chat2Id = 2L;
        Long chat3Id = 3L;

        Chat chat1 = jpaChatRepository.add(chat1Id);
        Chat chat2 = jpaChatRepository.add(chat2Id);
        Chat chat3 = jpaChatRepository.add(chat3Id);

        // Act
        List<Chat> chats = jpaChatRepository.findAll();

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

        Chat chat1 = jpaChatRepository.add(chat1Id);
        Chat chat2 = jpaChatRepository.add(chat2Id);
        jpaChatRepository.add(chat3Id);

        jpaLinkRepository.add(chat1Id, link1Url);
        jpaLinkRepository.add(chat2Id, link1Url);
        jpaLinkRepository.add(chat2Id, link2Url);
        jpaLinkRepository.add(chat3Id, link3Url);

        // Act
        List<Chat> chats = jpaChatRepository.findAllChatsByUrl(link1Url);

        // Assert
        assertThat(chats).containsOnly(
            chat1, chat2
        );
    }
}
