package edu.java.scrapper.repositories;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.exceptions.EntityNotFoundException;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.jdbc.JdbcChatRepository;
import edu.java.scrapper.repositories.jdbc.JdbcLinkRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class JdbcLinkRepositoryTest extends IntegrationTest {
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
        String linkUrl = "https://first.com";

        chatRepository.add(chatId);

        // Act
        Link link = linkRepository.add(chatId, linkUrl);
        List<Link> links = linkRepository.findAll();

        // Assert
        assertThat(links).containsOnly(
            link
        );
    }

    @Test
    void addDuplicate() {
        // Arrange
        Long chatId = 1L;
        String linkUrl = "https://first.com";

        chatRepository.add(chatId);
        linkRepository.add(chatId, linkUrl);

        // Act
        Throwable thrown = catchThrowable(() -> {
            linkRepository.add(chatId, linkUrl);
        });

        // Assert
        assertThat(thrown).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void remove() {
        // Arrange
        Long chatId = 1L;
        String linkUrl = "https://first.com";

        chatRepository.add(chatId);
        Link addedLink = linkRepository.add(chatId, linkUrl);

        // Act
        linkRepository.remove(addedLink.id());
        List<Link> links = linkRepository.findAll();

        // Assert
        assertThat(links).isEmpty();
    }

    @Test
    void removeEntityNotFound() {
        // Arrange
        Long chatId = 1L;
        String linkUrl = "https://first.com";

        // Act
        Throwable thrown = catchThrowable(() -> {
            linkRepository.remove(chatId, linkUrl);
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
        linkRepository.remove(chat1Id, link1Url);
        linkRepository.remove(chat1Id, link2Url);
        List<Link> links = linkRepository.findAll();

        // Assert
        assertThat(links).containsOnly(
            link21, link22
        );
    }

    @Test
    void removeUnusedLinks() {
        // Arrange
        Long chat1Id = 1L;
        String link1Url = "https://first.com";
        String link2Url = "https://second.com";
        String link3Url = "https://third.com";

        chatRepository.add(chat1Id);

        linkRepository.add(chat1Id, link1Url);
        linkRepository.add(chat1Id, link2Url);
        Link link3 = linkRepository.add(chat1Id, link3Url);

        linkRepository.remove(chat1Id, link1Url);
        linkRepository.remove(chat1Id, link2Url);

        // Act
        linkRepository.removeUnusedLinks();
        List<Link> links = linkRepository.findAll();

        // Assert
        assertThat(links).containsOnly(
            link3
        );
    }

    @Test
    void findByUrl() {
        // Arrange
        Long chat1Id = 1L;
        String link1Url = "https://first.com";
        String link2Url = "https://second.com";

        chatRepository.add(chat1Id);

        Link expectedLink = linkRepository.add(chat1Id, link1Url);
        linkRepository.add(chat1Id, link2Url);

        // Act
        Link actualLink = linkRepository.findByUrl(link1Url);

        // Assert
        assertThat(expectedLink).isEqualTo(actualLink);
    }

    @Test
    void findAll() {
        // Arrange
        Long chat1Id = 1L;
        Long chat2Id = 2L;
        String link1Url = "https://first.com";
        String link2Url = "https://second.com";

        chatRepository.add(chat1Id);
        chatRepository.add(chat2Id);

        Link link1 = linkRepository.add(chat1Id, link1Url);
        Link link2 = linkRepository.add(chat1Id, link2Url);
        Link link3 = linkRepository.add(chat2Id, link1Url);
        Link link4 = linkRepository.add(chat2Id, link2Url);

        // Act
        List<Link> links = linkRepository.findAll();

        // Assert
        assertThat(links).containsOnly(
            link1, link2, link3, link4
        );
    }

    @Test
    void findAllLinksByChatId() {
        // Arrange
        Long chat1Id = 1L;
        Long chat2Id = 2L;
        String link1Url = "https://first.com";
        String link2Url = "https://second.com";
        String link3Url = "https://third.com";

        chatRepository.add(chat1Id);
        chatRepository.add(chat2Id);

        Link link1 = linkRepository.add(chat1Id, link1Url);
        Link link2 = linkRepository.add(chat1Id, link2Url);
        linkRepository.add(chat2Id, link2Url);
        linkRepository.add(chat2Id, link3Url);

        // Act
        List<Link> links = linkRepository.findAllLinksByChatId(chat1Id);

        // Assert
        assertThat(links).containsOnly(
            link1, link2
        );
    }

    @Test
    void findByOldestUpdates() throws InterruptedException {
        // Arrange
        Long chat1Id = 1L;
        Long chat2Id = 2L;
        String link1Url = "https://first.com";
        String link2Url = "https://second.com";

        chatRepository.add(chat1Id);
        chatRepository.add(chat2Id);


        linkRepository.add(chat1Id, link1Url);

        sleep(100);
        linkRepository.add(chat1Id, link2Url);

        sleep(100);
        Link link3 = linkRepository.add(chat2Id, link1Url);

        sleep(100);
        Link link4 = linkRepository.add(chat2Id, link2Url);

        // Act
        List<Link> links = linkRepository.findByOldestUpdates(2);

        // Assert
        assertThat(links).containsOnly(
            link3, link4
        );
    }
}
