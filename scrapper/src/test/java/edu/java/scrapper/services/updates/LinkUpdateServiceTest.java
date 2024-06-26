package edu.java.scrapper.services.updates;

import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.LinkRepository;
import edu.java.scrapper.services.updates.updaters.LinkUpdater;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LinkUpdateServiceTest {
    @Mock
    private LinkRepository jdbcLinkRepository;
    @Mock
    private LinkHolder linkHolder;
    @Mock
    private LinkUpdater linkUpdater;

    @Test
    public void process() {
        // Arrange
        when(jdbcLinkRepository.findByOldestUpdates(3))
            .thenReturn(List.of(
                new Link().setId(1L).setUrl(URI.create("1")).setLastUpdate(OffsetDateTime.MAX),
                new Link().setId(2L).setUrl(URI.create("2")).setLastUpdate(OffsetDateTime.MAX),
                new Link().setId(3L).setUrl(URI.create("3")).setLastUpdate(OffsetDateTime.MAX)
            ));
        when(linkHolder.getUpdaterByDomain(any())).thenReturn(linkUpdater);
        when(linkUpdater.supports(any())).thenReturn(true);
        when(linkUpdater.process(any())).thenReturn(1);

        var linkUpdateService = new LinkUpdateService(jdbcLinkRepository, linkHolder);

        // Act
        int count = linkUpdateService.update();

        // Assert
        assertThat(count).isEqualTo(3);
    }
}
