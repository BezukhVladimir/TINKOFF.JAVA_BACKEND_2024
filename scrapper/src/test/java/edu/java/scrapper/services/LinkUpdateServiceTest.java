package edu.java.scrapper.services;

import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.jdbc.JdbcLinkRepository;
import edu.java.scrapper.repositories.LinkRepository;
import edu.java.scrapper.services.updaters.GitHubLinkUpdater;
import edu.java.scrapper.services.updaters.LinkHolder;
import edu.java.scrapper.services.updaters.LinkUpdater;
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
        LinkRepository linkRepository = mock(JdbcLinkRepository.class);
        LinkHolder linkHolder = mock(LinkHolder.class);
        LinkUpdater linkUpdater = mock(GitHubLinkUpdater.class);

        when(linkRepository.findByOldestUpdates(3))
            .thenReturn(List.of(
                new Link(1L, "1", OffsetDateTime.MAX),
                new Link(2L, "2", OffsetDateTime.MAX),
                new Link(3L, "3", OffsetDateTime.MAX)
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
