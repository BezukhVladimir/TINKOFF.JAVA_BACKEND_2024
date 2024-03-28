package edu.java.scrapper.services.updates.updaters.github;

import edu.java.scrapper.clients.BotWebClient;
import edu.java.scrapper.clients.github.RegularWebClient;
import edu.java.scrapper.dto.github.Response;
import edu.java.scrapper.models.Chat;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.ChatRepository;
import edu.java.scrapper.repositories.LinkRepository;
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
class GitHubLinkUpdaterTest {
    @Mock
    private RegularWebClient gitHubRegularWebClient;
    @Mock
    private ChatRepository jdbcChatRepository;
    @Mock
    private LinkRepository jdbcLinkRepository;
    @Mock
    private BotWebClient botWebClient;
    @InjectMocks
    private GitHubLinkUpdater jdbcGitHubLinkUpdater;


    @Test
    public void process() {
        // Arrange
        Long chatId = 1L;
        URI url = URI.create("https://github.com/author/repo");

        Link link = new Link().setId(chatId).setUrl(url).setLastUpdate(OffsetDateTime.MIN);

        when(gitHubRegularWebClient.fetchLatestModified("author", "repo"))
            .thenReturn(new Response(chatId, "1", null, null, OffsetDateTime.MAX));
        when(jdbcChatRepository.findAllChatsByUrl(link.getUrl()))
            .thenReturn(List.of(new Chat().setId(chatId).setCreatedAt(OffsetDateTime.MIN)));

        // Act
        int code = jdbcGitHubLinkUpdater.process(link);

        // Assert
        assertThat(code).isEqualTo(1);
    }

    @Test
    public void processWithoutUpdating() {
        // Arrange
        Long chatId = 1L;
        URI url = URI.create("https://github.com/author/repo");

        Link link = new Link().setId(chatId).setUrl(url).setLastUpdate(OffsetDateTime.MAX);

        when(gitHubRegularWebClient.fetchLatestModified("author", "repo"))
            .thenReturn(new Response(chatId, "1", null, null, OffsetDateTime.MIN));

        // Act
        int code = jdbcGitHubLinkUpdater.process(link);

        // Assert
        assertThat(code).isEqualTo(0);
    }

    @Test
    public void supports() {
        // Arrange
        URI supportedUrl = URI.create("https://github.com/");
        URI unsupportedUrl = URI.create("https://gitlab.com/");

        // Act
        boolean result1 = jdbcGitHubLinkUpdater.supports(supportedUrl);
        boolean result2 = jdbcGitHubLinkUpdater.supports(unsupportedUrl);

        // Assert
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }

    @Test
    public void processLink() {
        // Arrange
        URI url = URI.create("https://github.com/BezukhVladimir/Tinkoff");

        // Act
        String[] args = jdbcGitHubLinkUpdater.processLink(url);

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
        String actualDomain = jdbcGitHubLinkUpdater.getDomain();

        // Assert
        assertThat(actualDomain).isEqualTo(expectedDomain);
    }
}
