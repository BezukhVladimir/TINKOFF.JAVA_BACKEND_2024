package edu.java.scrapper_jooq.services.updates;

import edu.java.scrapper_jooq.services.updates.updaters.LinkUpdater;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class LinkHolder {
    private final Map<String, LinkUpdater> updatersMap;

    @Autowired
    public LinkHolder(List<LinkUpdater> updaters) {
        updatersMap = updaters.stream().collect(Collectors.toMap(
            LinkUpdater::getDomain, Function.identity())
        );
    }

    public LinkUpdater getUpdaterByDomain(String domain) {
        return updatersMap.get(domain);
    }
}
