package edu.java.scrapper.services.updaters;

import edu.java.scrapper.clients.BotWebClient;
import edu.java.scrapper.clients.github.RegularWebClient;
import edu.java.scrapper.dto.github.Response;
import edu.java.scrapper.models.Chat;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.ChatRepository;
import edu.java.scrapper.repositories.jdbc.JdbcChatRepository;
import edu.java.scrapper.repositories.jdbc.JdbcLinkRepository;
import edu.java.scrapper.repositories.LinkRepository;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class GitHubLinkUpdaterTest {
    private final RegularWebClient gitHubRegularWebClient = mock(RegularWebClient.class);
    private final ChatRepository chatRepository = mock(JdbcChatRepository.class);
    private final LinkRepository linkRepository = mock(JdbcLinkRepository.class);
    private final BotWebClient botWebClient = mock(BotWebClient.class);
    private final GitHubLinkUpdater gitHubLinkUpdater = new GitHubLinkUpdater(gitHubRegularWebClient, chatRepository, linkRepository, botWebClient);


    @Test
    public void process() {
        // Arrange
        Long chatId = 1L;
        String url = "https://github.com/author/repo";

        Link link = new Link(chatId, url, OffsetDateTime.MIN);

        when(gitHubRegularWebClient.fetchLatestModified("author", "repo"))
            .thenReturn(new Response(chatId, "1", null, null, OffsetDateTime.MAX));
        when(chatRepository.findAllChatsByUrl(link.url()))
            .thenReturn(List.of(new Chat(chatId, OffsetDateTime.MIN)));

        // Act
        int code = gitHubLinkUpdater.process(link);

        // Assert
        assertThat(code).isEqualTo(1);
    }

    @Test
    public void processWithoutUpdating() {
        // Arrange
        Long chatId = 1L;
        String url = "https://github.com/author/repo";

        Link link = new Link(chatId, url, OffsetDateTime.MAX);

        when(gitHubRegularWebClient.fetchLatestModified("author", "repo"))
            .thenReturn(new Response(chatId, "1", null, null, OffsetDateTime.MIN));
        when(chatRepository.findAllChatsByUrl(link.url()))
            .thenReturn(List.of(new Chat(chatId, OffsetDateTime.MIN)));

        // Act
        int code = gitHubLinkUpdater.process(link);

        // Assert
        assertThat(code).isEqualTo(0);
    }

    @Test
    public void supports() {
        // Arrange
        String supportedUrl = "https://github.com/";
        String unsupportedUrl = "https://gitlab.com/";

        // Act
        boolean result1 = gitHubLinkUpdater.supports(supportedUrl);
        boolean result2 = gitHubLinkUpdater.supports(unsupportedUrl);

        // Assert
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }

    @Test
    public void processLink() {
        // Arrange
        String url = "https://github.com/BezukhVladimir/Tinkoff";

        // Act
        String[] args = gitHubLinkUpdater.processLink(url);

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
        String actualDomain = gitHubLinkUpdater.getDomain();

        // Assert
        assertThat(actualDomain).isEqualTo(expectedDomain);
    }
}
