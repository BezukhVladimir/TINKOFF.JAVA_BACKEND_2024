package edu.java.scrapper.services;

import edu.java.scrapper.api.models.response.LinkResponse;
import edu.java.scrapper.exceptions.BadRequestException;
import edu.java.scrapper.exceptions.EntityNotFoundException;
import edu.java.scrapper.exceptions.NotFoundException;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.models.LinkPatterns;
import edu.java.scrapper.repositories.ChatRepository;
import edu.java.scrapper.repositories.LinkRepository;
import edu.java.scrapper.services.updates.LinkHolder;
import edu.java.scrapper.services.updates.updaters.LinkUpdater;
import edu.java.scrapper.utils.LinkUtils;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@SuppressWarnings({"MultipleStringLiterals"})
public class LinkService {
    private final ChatRepository chatRepository;
    private final LinkRepository linkRepository;
    private final LinkHolder linkHolder;

    public LinkResponse add(Long chatId, URI url) {
        try {
            chatRepository.findById(chatId);
        } catch (DataAccessException e) {
            throw new NotFoundException(
                "Чат не был зарегистрирован",
                "Нельзя добавить ссылку для незарегистрированного чата"
            );
        }

        if (!isValidUrl(url.toString())) {
            throw new BadRequestException(
                "Неизвестная ссылка",
                "Указанная ссылка не поддерживается"
            );
        }

        Link link;

        try {
            link = linkRepository.add(chatId, url);
        } catch (DuplicateKeyException e) {
            throw new BadRequestException(
                "Ссылка уже отслеживается",
                "Нельзя добавить уже отслеживаемую ссылку"
            );
        }

        if (link.getLastUpdate().equals(OffsetDateTime.MIN)) {
            String domain = LinkUtils.extractDomainFromUrl(url);

            LinkUpdater updater = linkHolder.getUpdaterByDomain(domain);

            updater.setLastUpdate(link);
        }

        return mapToLinkResponse(link);
    }

    public LinkResponse remove(Long chatId, URI url) {
        try {
            chatRepository.findById(chatId);
        } catch (DataAccessException e) {
            throw new NotFoundException(
                "Чат не был зарегистрирован",
                "Нельзя удалить ссылку для незарегистрированного чат"
            );
        }

        try {
            linkRepository.remove(chatId, url);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(
                "Ссылка отсутствует",
                "Нельзя удалить ненайденную ссылку"
            );
        }

        return mapToLinkResponse(new Link().setId(chatId).setUrl(url));
    }

    public List<LinkResponse> listAll(Long chatId) {
        return linkRepository.findAllLinksByChatId(chatId)
            .stream()
            .map(this::mapToLinkResponse)
            .toList();
    }

    private boolean isValidUrl(String urlString) {
        for (LinkPatterns pattern : LinkPatterns.values()) {
            if (urlString.matches(pattern.getRegex())) {
                return true;
            }
        }

        return false;
    }

    private LinkResponse mapToLinkResponse(Link link) {
        try {
            return new LinkResponse(link.getId(), link.getUrl());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
