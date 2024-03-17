package edu.java.scrapper.services.jooq.updaters;

import edu.java.scrapper.clients.BotWebClient;
import edu.java.scrapper.clients.github.RegularWebClient;
import edu.java.scrapper.dto.github.Response;
import edu.java.scrapper.models.Chat;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.ChatRepository;
import edu.java.scrapper.repositories.LinkRepository;
import edu.java.scrapper.repositories.jooq.JooqChatRepository;
import edu.java.scrapper.repositories.jooq.JooqLinkRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class JooqGitHubLinkUpdaterTest {
    private final RegularWebClient gitHubRegularWebClient = mock(RegularWebClient.class);
    private final ChatRepository jooqChatRepository = mock(JooqChatRepository.class);
    private final LinkRepository jooqLinkRepository = mock(JooqLinkRepository.class);
    private final BotWebClient botWebClient = mock(BotWebClient.class);
    private final JooqGitHubLinkUpdater jooqGitHubLinkUpdater = new JooqGitHubLinkUpdater(
        gitHubRegularWebClient, jooqChatRepository, jooqLinkRepository, botWebClient
    );


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
        when(jooqChatRepository.findAllChatsByUrl(link.url()))
            .thenReturn(List.of(new Chat(chatId, OffsetDateTime.MIN)));

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
