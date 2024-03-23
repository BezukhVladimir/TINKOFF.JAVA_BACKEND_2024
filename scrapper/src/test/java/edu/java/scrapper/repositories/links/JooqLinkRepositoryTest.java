package edu.java.scrapper.repositories.links;

import edu.java.scrapper.exceptions.EntityNotFoundException;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.JooqIntegrationTest;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class JooqLinkRepositoryTest extends JooqIntegrationTest {
    @Test
    void add() {

        // Arrange
        Long chatId = 1L;
        URI linkUrl = URI.create("https://first.com");

        jooqChatRepository.add(chatId);

        // Act
        Link link = jooqLinkRepository.add(chatId, linkUrl);
        List<Link> links = jooqLinkRepository.findAll();

        // Assert
        assertThat(links).containsOnly(
            link
        );
    }

    @Test
    void addDuplicate() {
        // Arrange
        Long chatId = 1L;
        URI linkUrl = URI.create("https://first.com");

        jooqChatRepository.add(chatId);
        jooqLinkRepository.add(chatId, linkUrl);

        // Act
        Throwable thrown = catchThrowable(() -> {
            jooqLinkRepository.add(chatId, linkUrl);
        });

        // Assert
        assertThat(thrown).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void remove() {
        // Arrange
        Long chatId = 1L;
        URI linkUrl = URI.create("https://first.com");

        jooqChatRepository.add(chatId);
        Link addedLink = jooqLinkRepository.add(chatId, linkUrl);

        // Act
        jooqLinkRepository.remove(addedLink.id());
        List<Link> links = jooqLinkRepository.findAll();

        // Assert
        assertThat(links).isEmpty();
    }

    @Test
    void removeEntityNotFound() {
        // Arrange
        Long chatId = 1L;
        URI linkUrl = URI.create("https://first.com");

        // Act
        Throwable thrown = catchThrowable(() -> {
            jooqLinkRepository.remove(chatId, linkUrl);
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
        jooqLinkRepository.remove(chat1Id, link1Url);
        jooqLinkRepository.remove(chat1Id, link2Url);
        List<Link> links = jooqLinkRepository.findAll();

        // Assert
        assertThat(links).containsOnly(
            link21, link22
        );
    }

    @Test
    void removeUnusedLinks() {
        // Arrange
        Long chat1Id = 1L;
        URI link1Url = URI.create("https://first.com");
        URI link2Url = URI.create("https://second.com");
        URI link3Url = URI.create("https://third.com");

        jooqChatRepository.add(chat1Id);

        jooqLinkRepository.add(chat1Id, link1Url);
        jooqLinkRepository.add(chat1Id, link2Url);
        Link link3 = jooqLinkRepository.add(chat1Id, link3Url);

        jooqLinkRepository.remove(chat1Id, link1Url);
        jooqLinkRepository.remove(chat1Id, link2Url);

        // Act
        jooqLinkRepository.removeUnusedLinks();
        List<Link> links = jooqLinkRepository.findAll();

        // Assert
        assertThat(links).containsOnly(
            link3
        );
    }

    @Test
    void findByUrl() {
        // Arrange
        Long chat1Id = 1L;
        URI link1Url = URI.create("https://first.com");
        URI link2Url = URI.create("https://second.com");

        jooqChatRepository.add(chat1Id);

        Link expectedLink = jooqLinkRepository.add(chat1Id, link1Url);
        jooqLinkRepository.add(chat1Id, link2Url);

        // Act
        Link actualLink = jooqLinkRepository.findByUrl(link1Url);

        // Assert
        assertThat(expectedLink).isEqualTo(actualLink);
    }

    @Test
    void findAll() {
        // Arrange
        Long chat1Id = 1L;
        Long chat2Id = 2L;
        URI link1Url = URI.create("https://first.com");
        URI link2Url = URI.create("https://second.com");

        jooqChatRepository.add(chat1Id);
        jooqChatRepository.add(chat2Id);

        Link link1 = jooqLinkRepository.add(chat1Id, link1Url);
        Link link2 = jooqLinkRepository.add(chat1Id, link2Url);
        Link link3 = jooqLinkRepository.add(chat2Id, link1Url);
        Link link4 = jooqLinkRepository.add(chat2Id, link2Url);

        // Act
        List<Link> links = jooqLinkRepository.findAll();

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
        URI link1Url = URI.create("https://first.com");
        URI link2Url = URI.create("https://second.com");
        URI link3Url = URI.create("https://third.com");

        jooqChatRepository.add(chat1Id);
        jooqChatRepository.add(chat2Id);

        Link link1 = jooqLinkRepository.add(chat1Id, link1Url);
        Link link2 = jooqLinkRepository.add(chat1Id, link2Url);
        jooqLinkRepository.add(chat2Id, link2Url);
        jooqLinkRepository.add(chat2Id, link3Url);

        // Act
        List<Link> links = jooqLinkRepository.findAllLinksByChatId(chat1Id);

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
        URI link1Url = URI.create("https://first.com");
        URI link2Url = URI.create("https://second.com");

        jooqChatRepository.add(chat1Id);
        jooqChatRepository.add(chat2Id);


        jooqLinkRepository.add(chat1Id, link1Url);
        jooqLinkRepository.add(chat1Id, link2Url);
        Link link3 = jooqLinkRepository.add(chat2Id, link1Url);
        Link link4 = jooqLinkRepository.add(chat2Id, link2Url);

        // Act
        List<Link> links = jooqLinkRepository.findByOldestUpdates(2);

        System.out.println(links);//чекаю  что он выбрал и всё ок
        // Assert
        assertThat(links).containsOnly(
            link3, link4
        );
    }
}
