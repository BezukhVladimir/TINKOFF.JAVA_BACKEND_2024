package edu.java.scrapper.services.updaters;

import edu.java.scrapper.clients.BotWebClient;
import edu.java.scrapper.clients.stackoverflow.RegularWebClient;
import edu.java.scrapper.dto.stackoverflow.Response;
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


class StackOverflowLinkUpdaterTest {
    private final RegularWebClient stackOverflowRegularWebClient = mock(RegularWebClient.class);
    private final ChatRepository chatRepository = mock(JdbcChatRepository.class);
    private final LinkRepository linkRepository = mock(JdbcLinkRepository.class);
    private final BotWebClient botWebClient = mock(BotWebClient.class);
    private final StackOverflowLinkUpdater stackOverflowLinkUpdater = new StackOverflowLinkUpdater(
        stackOverflowRegularWebClient, chatRepository, linkRepository, botWebClient
    );


    @Test
    public void process() {
        // Arrange
        Long chatId = 1L;
        String url = "https://stackoverflow.com/questions/1337";

        Link link = new Link(chatId, url, OffsetDateTime.MIN);

        when(stackOverflowRegularWebClient.fetchLatestModified(1337L))
            .thenReturn(new Response(null, OffsetDateTime.MAX, null, null));
        when(chatRepository.findAllChatsByUrl(link.url()))
            .thenReturn(List.of(new Chat(chatId, OffsetDateTime.MIN)));

        // Act
        int code = stackOverflowLinkUpdater.process(link);

        // Assert
        assertThat(code).isEqualTo(1);
    }

    @Test
    public void processWithoutUpdating() {
        // Arrange
        Long chatId = 1L;
        String url = "https://stackoverflow.com/questions/1337";

        Link link = new Link(chatId, url, OffsetDateTime.MAX);

        when(stackOverflowRegularWebClient.fetchLatestModified(1337L))
            .thenReturn(new Response(null, OffsetDateTime.MIN, null, null));
        when(chatRepository.findAllChatsByUrl(link.url()))
            .thenReturn(List.of(new Chat(chatId, OffsetDateTime.MIN)));

        // Act
        int code = stackOverflowLinkUpdater.process(link);

        // Assert
        assertThat(code).isEqualTo(0);
    }

    @Test
    public void supports() {
        // Arrange
        String supportedUrl = "https://stackoverflow.com/questions/1337";
        String unsupportedUrl = "https://otvet.mail.ru/";

        // Act
        boolean result1 = stackOverflowLinkUpdater.supports(supportedUrl);
        boolean result2 = stackOverflowLinkUpdater.supports(unsupportedUrl);

        // Assert
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }

    @Test
    public void processLink() {
        // Arrange
        String url = "https://stackoverflow.com/questions/1337";

        // Act
        String[] args = stackOverflowLinkUpdater.processLink(url);

        // Assert
        assertThat(args).contains("1337");
    }

    @Test
    public void getDomain() {
        // Arrange
        String expectedDomain = "stackoverflow.com";

        // Act
        String actualDomain = stackOverflowLinkUpdater.getDomain();

        // Assert
        assertThat(actualDomain).isEqualTo(expectedDomain);
    }
}
