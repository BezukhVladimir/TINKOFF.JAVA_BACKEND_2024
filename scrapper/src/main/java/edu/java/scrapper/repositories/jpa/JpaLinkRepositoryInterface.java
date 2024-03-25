package edu.java.scrapper.repositories.jpa;

import edu.java.scrapper.models.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface JpaLinkRepositoryInterface extends JpaRepository<Link, Long> {
    @Modifying
    @Query(value = """
        INSERT INTO link_tracker_db.chats_links (id_chat, id_link)
        VALUES (:chatId, :linkId)
    """, nativeQuery = true)
    void insert(Long chatId, Long linkId);

    @Modifying
    @Query(value = """
        DELETE FROM link_tracker_db.link l
         WHERE l.id = :linkId
    """, nativeQuery = true)
    void remove(Long linkId);

    @Modifying
    @Query(value = """
        DELETE FROM link_tracker_db.chats_links cl
         USING link_tracker_db.link l
         WHERE cl.id_chat = :chatId
           AND cl.id_link = l.id
           AND l.url = :url
    """, nativeQuery = true)
    void remove(Long chatId, URI url);

    @Modifying
    @Query(value = """
        DELETE FROM link_tracker_db.link l
         WHERE l.id NOT IN (SELECT cl.id_link
                              FROM link_tracker_db.chats_links cl)
    """, nativeQuery = true)
    void removeUnusedLinks();

    @Query(value = """
        SELECT *
         FROM link_tracker_db.link
        WHERE url = :url
    """, nativeQuery = true)
    Link findByUrl(URI url);

    @Query(value = """
        SELECT l.*
          FROM link_tracker_db.link l
          JOIN link_tracker_db.chats_links cl
            ON l.id = cl.id_link
         WHERE cl.id_chat = :chatId
    """, nativeQuery = true)
    List<Link> findAllLinksByChatId(Long chatId);

    @Query(value = """
        SELECT *
          FROM link
         ORDER BY lastUpdate
         LIMIT :count
     """, nativeQuery = true)
    List<Link> findByOldestUpdates(int count);

    @Modifying
    @Query(value = """
         UPDATE link_tracker_db.link
            SET last_update = :time
          WHERE url = :url
    """, nativeQuery = true)
    void setLastUpdate(URI url, OffsetDateTime time);
}
