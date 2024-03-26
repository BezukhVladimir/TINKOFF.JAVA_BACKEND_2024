package edu.java.scrapper.api.controllers;

import edu.java.scrapper.api.models.request.AddLinkRequest;
import edu.java.scrapper.api.models.request.RemoveLinkRequest;
import edu.java.scrapper.api.models.response.LinkResponse;
import edu.java.scrapper.api.models.response.ListLinksResponse;
import edu.java.scrapper.services.ChatService;
import edu.java.scrapper.services.LinkService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ScrapperController {
    private final ChatService chatService;
    private final LinkService linkService;

    @PostMapping("/tg-chat/{id}")
    public String registerChat(@PathVariable("id") Long id) {
        chatService.register(id);
        return "Чат зарегистрирован";
    }

    @DeleteMapping("/tg-chat/{id}")
    public String deleteChat(@PathVariable("id") Long id) {
        chatService.unregister(id);
        return "Чат успешно удалён";
    }

    @GetMapping("/links")
    public ListLinksResponse getLinks(@RequestHeader("Tg-Chat-Id") Long chatId) {
        List<LinkResponse> links = linkService.listAll(chatId);
        return new ListLinksResponse(links, links.size());
    }

    @PostMapping("/links")
    public LinkResponse addLink(
        @RequestHeader("Tg-Chat-Id") Long chatId,
        @RequestBody @Valid AddLinkRequest request
    ) {
        return linkService.add(chatId, request.link());
    }

    @DeleteMapping("/links")
    public LinkResponse removeLink(
        @RequestHeader("Tg-Chat-Id") Long chatId,
        @RequestBody @Valid RemoveLinkRequest request
    ) {
        return linkService.remove(chatId, request.link());
    }
}
