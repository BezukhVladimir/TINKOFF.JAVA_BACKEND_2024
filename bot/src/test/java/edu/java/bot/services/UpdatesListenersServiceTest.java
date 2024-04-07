package edu.java.bot.services;

import edu.java.bot.api.models.requests.LinkUpdateRequest;
import edu.java.bot.listeners.BotUpdatesListener;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UpdatesListenersServiceTest {
    @Mock
    private BotUpdatesListener botUpdatesListener;
    @InjectMocks
    private UpdatesListenersService updatesListenersService;

    @Test
    void process() {
        // Arrange
        LinkUpdateRequest update = new LinkUpdateRequest(
            1L,
            URI.create("https://github.com/author/repo/"),
            "test",
            List.of(1L, 2L, 3L)
        );
        doNothing().when(botUpdatesListener).sendMessage(any(), any());

        // Act
        updatesListenersService.process(update);

        // Assert
        verify(botUpdatesListener, times(3)).sendMessage(any(), any());
    }
}
