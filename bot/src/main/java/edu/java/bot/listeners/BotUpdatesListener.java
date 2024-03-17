package edu.java.bot.listeners;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import edu.java.bot.services.MessageService;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BotUpdatesListener implements UpdatesListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final TelegramBot telegramBot;
    private final MessageService messageService;

    @Autowired
    public BotUpdatesListener(TelegramBot telegramBot, MessageService messageService) {
        telegramBot.setUpdatesListener(this);

        this.telegramBot = telegramBot;
        this.messageService = messageService;
    }

    @Override
    public int process(List<Update> list) {
        for (Update update : list) {
            long chatId = update.message().chat().id();

            SendMessage message = new SendMessage(
                chatId, messageService.createResponseText(update)
            );

            telegramBot.execute(message, getCallback());
        }

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void sendMessage(String chatId, String message) {
        telegramBot.execute(new SendMessage(chatId, message));
    }

    private static Callback<SendMessage, SendResponse> getCallback() {
        return new Callback<>() {
            @Override
            public void onResponse(SendMessage request, SendResponse response) {
                LOGGER.info("На запрос (%s) был выслан ответ (%s)".formatted(
                    request.toString(),
                    response.message().text()
                ));
            }

            @Override
            public void onFailure(SendMessage request, IOException e) {
                LOGGER.error("Запрос не был выполнен: " + e.getMessage());
            }
        };
    }
}
