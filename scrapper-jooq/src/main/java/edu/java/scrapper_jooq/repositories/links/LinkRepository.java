package edu.java.scrapper_jooq.repositories.links;


import edu.java.scrapper_jooq.models.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkRepository {
    Link add(Long chatId, URI url);

    void remove(Long linkId);

    void remove(Long chatId, URI url);

    void removeAll();

    void removeUnusedLinks();

    Link findByUrl(URI url);

    List<Link> findAll();

    List<Link> findAllLinksByChatId(Long chatId);

    List<Link> findByOldestUpdates(int count);

    void setLastUpdate(URI url, OffsetDateTime time);
}
