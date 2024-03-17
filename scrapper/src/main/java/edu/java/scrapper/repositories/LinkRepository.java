package edu.java.scrapper.repositories;


import edu.java.scrapper.models.Link;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkRepository {
    Link add(Long chatId, String url);

    void remove(Long linkId);

    void remove(Long chatId, String url);

    void removeAll();

    void removeUnusedLinks();

    Link findByUrl(String url);

    List<Link> findAll();

    List<Link> findAllLinksByChatId(Long chatId);

    List<Link> findByOldestUpdates(int count);

    void setLastUpdate(String url, OffsetDateTime time);
}
