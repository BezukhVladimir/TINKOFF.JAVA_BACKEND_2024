package edu.java.scrapper.repositories.jpa;

import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.LinkRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@SuppressWarnings({"MultipleStringLiterals"})
public class JpaLinkRepository implements LinkRepository {
    private final JpaLinkRepositoryInterface jpaLinkRepositoryInterface;

    @Override
    @Transactional
    public Link add(Long chatId, URI url) {
        Link addedLink;
        Link existingLink;

        try {
            existingLink = findByUrl(url);
        } catch (DataAccessException e) {
            existingLink = null;
        }

        Link link = new Link().setUrl(url);
        addedLink = Objects.requireNonNullElseGet(existingLink, () -> jpaLinkRepositoryInterface.save(
            link
        ));

        jpaLinkRepositoryInterface.saveByChatIdAndLinkId(chatId, addedLink.getId());

        return addedLink;
    }

    @Override
    @Transactional
    public void remove(Long linkId) {
        jpaLinkRepositoryInterface.deleteById(linkId);
    }

    @Override
    @Transactional
    public void remove(Long chatId, URI url) {
        jpaLinkRepositoryInterface.remove(chatId, url.toString());
    }

    @Override
    @Transactional
    public void removeAll() {
        jpaLinkRepositoryInterface.deleteAll();
    }

    @Override
    public void removeUnusedLinks() {
        jpaLinkRepositoryInterface.removeUnusedLinks();
    }

    @Override
    public Link findByUrl(URI url) throws DataAccessException {
        return jpaLinkRepositoryInterface.findByUrl(url);
    }

    @Override
    public List<Link> findAll() {
        return jpaLinkRepositoryInterface.findAll();
    }

    @Override
    public List<Link> findAllLinksByChatId(Long chatId) {
        return jpaLinkRepositoryInterface.findAllLinksByChatId(chatId);
    }

    @Override
    public List<Link> findByOldestUpdates(int count) {
        return jpaLinkRepositoryInterface.findTopNByOrderByLastUpdateAsc(count);
    }

    @Override
    public void setLastUpdate(URI url, OffsetDateTime time) {
        jpaLinkRepositoryInterface.setLastUpdateByUrl(url, time);
    }
}
