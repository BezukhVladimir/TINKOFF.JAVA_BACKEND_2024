package edu.java.scrapper.repositories.jpa;

import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.JpaIntegrationTest;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JpaLinkRepositoryTest extends JpaIntegrationTest {
    @Autowired
    public JpaLinkRepositoryTest(
        JpaChatRepositoryInterface jpaChatRepositoryInterface,
        JpaLinkRepositoryInterface jpaLinkRepositoryInterface
    ) {
        jpaChatRepository = new JpaChatRepository(jpaChatRepositoryInterface);
        jpaLinkRepository = new JpaLinkRepository(jpaLinkRepositoryInterface);
    }

    @Test
    void add() {
        // Arrange
        Long chatId = 1L;
        URI linkUrl = URI.create("https://first.com");

        jpaChatRepository.add(chatId);

        // Act
        Link link = jpaLinkRepository.add(chatId, linkUrl);
        List<Link> links = jpaLinkRepository.findAll();

        // Assert
        assertThat(links).containsOnly(
            link
        );
    }

    @Test
    void remove() {
        // Arrange
        Long chatId = 1L;
        URI linkUrl = URI.create("https://first.com");

        jpaChatRepository.add(chatId);
        Link addedLink = jpaLinkRepository.add(chatId, linkUrl);

        // Act
        jpaLinkRepository.remove(addedLink.getId());
        List<Link> links = jpaLinkRepository.findAll();

        // Assert
        assertThat(links).isEmpty();
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
        jpaLinkRepository.remove(chat1Id, link1Url);
        jpaLinkRepository.remove(chat1Id, link2Url);
        List<Link> links = jpaLinkRepository.findAll();

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

        jpaChatRepository.add(chat1Id);

        jpaLinkRepository.add(chat1Id, link1Url);
        jpaLinkRepository.add(chat1Id, link2Url);
        Link link3 = jpaLinkRepository.add(chat1Id, link3Url);

        jpaLinkRepository.remove(chat1Id, link1Url);
        jpaLinkRepository.remove(chat1Id, link2Url);

        // Act
        jpaLinkRepository.removeUnusedLinks();
        List<Link> links = jpaLinkRepository.findAll();

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

        jpaChatRepository.add(chat1Id);

        Link expectedLink = jpaLinkRepository.add(chat1Id, link1Url);
        jpaLinkRepository.add(chat1Id, link2Url);

        // Act
        Link actualLink = jpaLinkRepository.findByUrl(link1Url);

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

        jpaChatRepository.add(chat1Id);
        jpaChatRepository.add(chat2Id);

        Link link1 = jpaLinkRepository.add(chat1Id, link1Url);
        Link link2 = jpaLinkRepository.add(chat1Id, link2Url);
        Link link3 = jpaLinkRepository.add(chat2Id, link1Url);
        Link link4 = jpaLinkRepository.add(chat2Id, link2Url);

        // Act
        List<Link> links = jpaLinkRepository.findAll();

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

        jpaChatRepository.add(chat1Id);
        jpaChatRepository.add(chat2Id);

        Link link1 = jpaLinkRepository.add(chat1Id, link1Url);
        Link link2 = jpaLinkRepository.add(chat1Id, link2Url);
        jpaLinkRepository.add(chat2Id, link2Url);
        jpaLinkRepository.add(chat2Id, link3Url);

        // Act
        List<Link> links = jpaLinkRepository.findAllLinksByChatId(chat1Id);

        // Assert
        assertThat(links).containsOnly(
            link1, link2
        );
    }

    @Test
    void findByOldestUpdates() {
        // Arrange
        Long chat1Id = 1L;
        Long chat2Id = 2L;
        URI link1Url = URI.create("https://first.com");
        URI link2Url = URI.create("https://second.com");

        jpaChatRepository.add(chat1Id);
        jpaChatRepository.add(chat2Id);


        jpaLinkRepository.add(chat1Id, link1Url);
        jpaLinkRepository.add(chat1Id, link2Url);
        Link link3 = jpaLinkRepository.add(chat2Id, link1Url);
        Link link4 = jpaLinkRepository.add(chat2Id, link2Url);

        // Act
        List<Link> links = jpaLinkRepository.findByOldestUpdates(2);

        // Assert
        assertThat(links).containsOnly(
            link3, link4
        );
    }
}
