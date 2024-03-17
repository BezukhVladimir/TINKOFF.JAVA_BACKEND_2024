package edu.java.scrapper.services.updaters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class LinkHolder {
    private final Map<String, LinkUpdater> updaters;

    @Autowired
    public LinkHolder(List<LinkUpdater> updaters) {
        this.updaters = new HashMap<>();

        for (LinkUpdater linkUpdater : updaters) {
            this.updaters.put(linkUpdater.getDomain(), linkUpdater);
        }
    }

    public LinkUpdater getUpdaterByDomain(String domain) {
        return updaters.get(domain);
    }
}
