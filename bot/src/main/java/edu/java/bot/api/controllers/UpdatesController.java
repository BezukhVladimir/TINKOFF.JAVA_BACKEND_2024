package edu.java.bot.api.controllers;

import edu.java.bot.api.models.LinkUpdateRequest;
import edu.java.bot.listeners.BotUpdatesListener;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
public class UpdatesController {
    private final BotUpdatesListener botUpdatesListener;

    @PostMapping
    public String processUpdate(@RequestBody @Valid LinkUpdateRequest linkUpdateRequest) {
        for (Long chat : linkUpdateRequest.tgChatIds()) {
            botUpdatesListener.sendMessage(String.valueOf(chat), linkUpdateRequest.description());
        }

        return "Обновление обработано";
    }
}
