package edu.java.scrapper.services;

import edu.java.scrapper.api.models.LinkResponse;
import edu.java.scrapper.exceptions.BadRequestException;
import edu.java.scrapper.exceptions.NotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final Map<Long, List<LinkResponse>> chatLinks = new HashMap<>();

    public void registerChat(long chatId) {
        if (chatLinks.containsKey(chatId)) {
            throw new BadRequestException("Чат уже зарегистрирован", "Нельзя повторно зарегистрировать чат");
        }

        chatLinks.put(chatId, new ArrayList<>());
    }

    public void deleteChat(Long chatId) {
        checkChatNotFound(chatId, "Нельзя удалить незарегистрированный чат");
        chatLinks.remove(chatId);
    }

    public List<LinkResponse> getLinks(Long chatId) {
        checkChatNotFound(chatId, "Нельзя получить ссылки для незарегистрированного чата");
        return chatLinks.get(chatId);
    }

    public LinkResponse addLink(Long chatId, URI link) {
        checkChatNotFound(chatId, "Нельзя добавить ссылку для незарегистрированного чат");

        List<LinkResponse> linkResponses = chatLinks.get(chatId);
        for (LinkResponse linkResponse : linkResponses) {
            if (linkResponse.url().getPath().equals(link.getPath())) {
                throw new BadRequestException("Ссылка уже отслеживается", "Нельзя добавить уже отслеживаемую ссылку");
            }
        }

        LinkResponse linkResponse = new LinkResponse((long) (linkResponses.size() + 1), link);
        linkResponses.add(linkResponse);

        return linkResponse;
    }

    public LinkResponse removeLink(Long chatId, URI link) {
        checkChatNotFound(chatId, "Нельзя удалить ссылку для незарегистрированного чат");

        List<LinkResponse> linkResponses = chatLinks.get(chatId);
        for (LinkResponse linkResponse : linkResponses) {
            if (linkResponse.url().getPath().equals(link.getPath())) {
                linkResponses.remove(linkResponse);
                return linkResponse;
            }
        }

        throw new NotFoundException("Ссылка отсутствует", "Нельзя удалить ненайденную ссылку");
    }

    private void checkChatNotFound(Long chatId, String message) {
        if (!chatLinks.containsKey(chatId)) {
            throw new NotFoundException("Чат не был зарегистрирован", message);
        }
    }
}
