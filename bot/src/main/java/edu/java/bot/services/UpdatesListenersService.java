package edu.java.bot.services;

import edu.java.bot.api.models.requests.LinkUpdateRequest;
import edu.java.bot.listeners.BotUpdatesListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdatesListenersService {
    private final BotUpdatesListener botUpdatesListener;

    public void process(LinkUpdateRequest linkUpdateRequest) {
        for (Long chat : linkUpdateRequest.tgChatIds()) {
            botUpdatesListener.sendMessage(String.valueOf(chat), linkUpdateRequest.description());
        }
    }
}
