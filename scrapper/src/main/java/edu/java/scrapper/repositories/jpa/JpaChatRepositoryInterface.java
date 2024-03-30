package edu.java.scrapper.repositories.jpa;

import edu.java.scrapper.models.Chat;
import java.net.URI;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaChatRepositoryInterface extends JpaRepository<Chat, Long> {
    @Query(value = """
        SELECT c
          FROM Chat c
          JOIN c.links l
         WHERE l.url = :url
    """)
    List<Chat> findAllChatsByUrl(@Param("url") URI url);
}
