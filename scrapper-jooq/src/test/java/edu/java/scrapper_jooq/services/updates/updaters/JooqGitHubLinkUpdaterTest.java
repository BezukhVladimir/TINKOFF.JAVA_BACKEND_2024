package edu.java.scrapper_jooq.services.updates.updaters;

import edu.java.scrapper_jooq.clients.BotWebClient;
import edu.java.scrapper_jooq.clients.github.RegularWebClient;
import edu.java.scrapper_jooq.dto.github.Response;
import edu.java.scrapper_jooq.models.Chat;
import edu.java.scrapper_jooq.models.Link;
import edu.java.scrapper_jooq.repositories.chats.ChatRepository;
import edu.java.scrapper_jooq.repositories.links.LinkRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JooqGitHubLinkUpdaterTest {
    @Mock
    private RegularWebClient gitHubRegularWebClient;
    @Mock
    private ChatRepository jooqChatRepository;
    @Mock
    private LinkRepository jooqLinkRepository;
    @Mock
    private BotWebClient botWebClient;
    @InjectMocks
    private JooqGitHubLinkUpdater jooqGitHubLinkUpdater;


    @Test
    public void process() {
        // Arrange
        Long chatId = 1L;
        URI url = URI.create("https://github.com/author/repo");

        Link link = new Link(chatId, url, OffsetDateTime.MIN);

        when(gitHubRegularWebClient.fetchLatestModified("author", "repo"))
            .thenReturn(new Response(chatId, "1", null, null, OffsetDateTime.MAX));
        when(jooqChatRepository.findAllChatsByUrl(link.url()))
            .thenReturn(List.of(new Chat(chatId, OffsetDateTime.MIN)));

        // Act
        int code = jooqGitHubLinkUpdater.process(link);

        // Assert
        assertThat(code).isEqualTo(1);
    }

    @Test
    public void processWithoutUpdating() {
        // Arrange
        Long chatId = 1L;
        URI url = URI.create("https://github.com/author/repo");

        Link link = new Link(chatId, url, OffsetDateTime.MAX);

        when(gitHubRegularWebClient.fetchLatestModified("author", "repo"))
            .thenReturn(new Response(chatId, "1", null, null, OffsetDateTime.MIN));

        // Act
        int code = jooqGitHubLinkUpdater.process(link);

        // Assert
        assertThat(code).isEqualTo(0);
    }

    @Test
    public void supports() {
        // Arrange
        URI supportedUrl = URI.create("https://github.com/");
        URI unsupportedUrl = URI.create("https://gitlab.com/");

        // Act
        boolean result1 = jooqGitHubLinkUpdater.supports(supportedUrl);
        boolean result2 = jooqGitHubLinkUpdater.supports(unsupportedUrl);

        // Assert
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }

    @Test
    public void processLink() {
        // Arrange
        URI url = URI.create("https://github.com/BezukhVladimir/Tinkoff");

        // Act
        String[] args = jooqGitHubLinkUpdater.processLink(url);

        // Assert
        assertThat(args).containsOnly(
            "BezukhVladimir", "Tinkoff"
        );
    }

    @Test
    public void getDomain() {
        // Arrange
        String expectedDomain = "github.com";

        // Act
        String actualDomain = jooqGitHubLinkUpdater.getDomain();

        // Assert
        assertThat(actualDomain).isEqualTo(expectedDomain);
    }
}
