package edu.java.scrapper_jooq.services.updates;

import edu.java.scrapper_jooq.models.Link;
import edu.java.scrapper_jooq.repositories.links.LinkRepository;
import edu.java.scrapper_jooq.services.updates.updaters.LinkUpdater;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static edu.java.scrapper_jooq.utils.LinkUtils.extractDomainFromUrl;

@Service
@RequiredArgsConstructor
public class LinkUpdateService {
    private final LinkRepository linkRepository;
    private final LinkHolder linkHolder;
    private final static int UPDATES = 3;

    public int update() {
        List<Link> links = linkRepository.findByOldestUpdates(UPDATES);

        int count = 0;
        for (Link link : links) {
            String domain = extractDomainFromUrl(link.url());
            LinkUpdater linkUpdater = linkHolder.getUpdaterByDomain(domain);

            if (linkUpdater.supports(link.url())) {
                count += linkUpdater.process(link);
            }
        }

        return count;
    }

    public void removeUnusedLinks() {
        linkRepository.removeUnusedLinks();
    }
}
