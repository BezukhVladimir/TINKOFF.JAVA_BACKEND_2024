package edu.java.scrapper.repositories;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.exceptions.EntityNotFoundException;
import edu.java.scrapper.models.Chat;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.jdbc.JdbcChatRepository;
import edu.java.scrapper.repositories.jdbc.JdbcLinkRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class JdbcChatRepositoryTest extends IntegrationTest {
    private static JdbcChatRepository chatRepository;
    private static JdbcLinkRepository linkRepository;

    @BeforeAll
    static void setUp() {
        chatRepository = new JdbcChatRepository(jdbcTemplate);
        linkRepository = new JdbcLinkRepository(jdbcTemplate);
    }

    @AfterEach
    void clear() {
        chatRepository.removeAll();
        linkRepository.removeAll();
    }

    @Test
    void add() {
        // Arrange
        Long chatId = 1L;

        // Act
        Chat chat1 = chatRepository.add(chatId);
        List<Chat> chats = chatRepository.findAll();

        // Assert
        assertThat(chats).containsOnly(chat1);
    }

    @Test
    void addDuplicate() {
        // Arrange
        Long chatId = 1L;
        chatRepository.add(chatId);

        // Act
        Throwable thrown = catchThrowable(() -> {
            chatRepository.add(chatId);
        });

        // Assert
        assertThat(thrown).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void remove() {
        // Arrange
        Long chatId = 1L;
        chatRepository.add(chatId);

        // Act
        chatRepository.remove(chatId);
        List<Chat> chats = chatRepository.findAll();

        // Assert
        assertThat(chats).isEmpty();
    }

    @Test
    void removeEntityNotFound() {
        // Arrange
        Long chatId = 1L;

        // Act
        Throwable thrown = catchThrowable(() -> {
            chatRepository.remove(chatId);
        });

        // Assert
        assertThat(thrown).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void removeFromChatsLinks() {
        // Arrange
        Long chat1Id = 1L;
        Long chat2Id = 2L;
        String link1Url = "https://first.com";
        String link2Url = "https://second.com";

        chatRepository.add(chat1Id);
        chatRepository.add(chat2Id);

        linkRepository.add(chat1Id, link1Url);
        linkRepository.add(chat1Id, link2Url);
        Link link21 = linkRepository.add(chat2Id, link1Url);
        Link link22 = linkRepository.add(chat2Id, link2Url);

        // Act
        chatRepository.remove(chat1Id);
        List<Link> links = linkRepository.findAll();

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

        chatRepository.add(chat1Id);
        chatRepository.add(chat2Id);
        chatRepository.add(chat3Id);

        // Act
        chatRepository.removeAll();
        List<Chat> chats = chatRepository.findAll();

        // Assert
        assertThat(chats).isEmpty();
    }

    @Test
    void findById() {
        // Arrange
        Long chat1Id = 1L;
        Long chat2Id = 2L;
        Long chat3Id = 3L;

        Chat expectedChat1 = chatRepository.add(chat1Id);
        chatRepository.add(chat2Id);
        chatRepository.add(chat3Id);

        // Act
        Chat actualChat1 = chatRepository.findById(chat1Id);

        // Assert
        assertThat(actualChat1).isEqualTo(expectedChat1);
    }

    @Test
    void findAll() {
        // Arrange
        Long chat1Id = 1L;
        Long chat2Id = 2L;
        Long chat3Id = 3L;

        Chat chat1 = chatRepository.add(chat1Id);
        Chat chat2 = chatRepository.add(chat2Id);
        Chat chat3 = chatRepository.add(chat3Id);

        // Act
        List<Chat> chats = chatRepository.findAll();

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
        String link1Url = "https://first.com";
        String link2Url = "https://second.com";
        String link3Url = "https://third.com";

        Chat chat1 = chatRepository.add(chat1Id);
        Chat chat2 = chatRepository.add(chat2Id);
        chatRepository.add(chat3Id);

        linkRepository.add(chat1Id, link1Url);
        linkRepository.add(chat2Id, link1Url);
        linkRepository.add(chat2Id, link2Url);
        linkRepository.add(chat3Id, link3Url);

        // Act
        List<Chat> chats = chatRepository.findAllChatsByUrl(link1Url);

        // Assert
        assertThat(chats).containsOnly(
            chat1, chat2
        );
    }
}
