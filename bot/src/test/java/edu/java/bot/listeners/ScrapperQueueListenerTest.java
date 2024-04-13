package edu.java.bot.listeners;

import edu.java.bot.api.models.requests.LinkUpdateRequest;
import edu.java.bot.services.UpdatesListenersService;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ScrapperQueueListenerTest {
    @Mock
    private UpdatesListenersService updatesListenersService;
    @InjectMocks
    private ScrapperQueueListener scrapperQueueListener;

    @Test
    public void listen() {
        // Arrange
        LinkUpdateRequest update = new LinkUpdateRequest(
            1L,
            URI.create("https://github.com/author/repo/"),
            "test",
            List.of(1L, 2L, 3L)
        );
        doNothing().when(updatesListenersService).process(any());

        // Act
        scrapperQueueListener.listen(update);

        // Assert
        verify(updatesListenersService).process(update);
    }
}
