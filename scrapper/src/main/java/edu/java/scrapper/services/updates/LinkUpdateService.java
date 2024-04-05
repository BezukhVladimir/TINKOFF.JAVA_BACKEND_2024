package edu.java.scrapper.services.updates;

import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.LinkRepository;
import edu.java.scrapper.services.updates.updaters.LinkUpdater;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static edu.java.scrapper.utils.LinkUtils.extractDomainFromUrl;

@Service
@RequiredArgsConstructor
public class LinkUpdateService {
    private static final int UPDATES = 3;

    private final LinkRepository linkRepository;
    private final LinkHolder linkHolder;

    public int update() {
        List<Link> links = linkRepository.findByOldestUpdates(UPDATES);

        int count = 0;
        for (Link link : links) {
            String domain = extractDomainFromUrl(link.getUrl());
            LinkUpdater linkUpdater = linkHolder.getUpdaterByDomain(domain);

            if (linkUpdater.supports(link.getUrl())) {
                count += linkUpdater.process(link);
            }
        }

        return count;
    }

    public void removeUnusedLinks() {
        linkRepository.removeUnusedLinks();
    }
}
