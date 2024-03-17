package edu.java.scrapper.services;

import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.LinkRepository;
import edu.java.scrapper.services.updaters.LinkHolder;
import edu.java.scrapper.services.updaters.LinkUpdater;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static edu.java.scrapper.utils.LinkUtils.extractDomainFromUrl;

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