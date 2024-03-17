package edu.java.scrapper.services;

import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.LinkRepository;
import edu.java.scrapper.repositories.jdbc.JooqLinkRepository;
import edu.java.scrapper.services.jdbc.updaters.JdbcGitHubLinkUpdater;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class LinkUpdateServiceTest {
    @Test
    public void process() {
        // Arrange
        LinkRepository linkRepository = mock(JooqLinkRepository.class);
        LinkHolder linkHolder = mock(LinkHolder.class);
        LinkUpdater linkUpdater = mock(JdbcGitHubLinkUpdater.class);

        when(linkRepository.findByOldestUpdates(3))
            .thenReturn(List.of(
                new Link(1L, URI.create("1"), OffsetDateTime.MAX),
                new Link(2L, URI.create("2"), OffsetDateTime.MAX),
                new Link(3L, URI.create("3"), OffsetDateTime.MAX)
            ));
        when(linkHolder.getUpdaterByDomain(any())).thenReturn(linkUpdater);
        when(linkUpdater.supports(any())).thenReturn(true);
        when(linkUpdater.process(any())).thenReturn(1);

        var linkUpdateService = new LinkUpdateService(linkRepository, linkHolder);

        // Act
        int count = linkUpdateService.update();

        // Assert
        assertThat(count).isEqualTo(3);
    }
}
