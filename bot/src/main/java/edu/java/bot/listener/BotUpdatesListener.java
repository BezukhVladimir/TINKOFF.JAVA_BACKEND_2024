package edu.java.bot.listener;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static edu.java.bot.services.MessageService.createResponseText;

@Component
public class BotUpdatesListener implements UpdatesListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final TelegramBot bot;

    @Autowired
    public BotUpdatesListener(TelegramBot bot) {
        bot.setUpdatesListener(this);
        this.bot = bot;
    }

    @Override
    public int process(List<Update> list) {
        for (Update update : list) {
            long chatId = update.message().chat().id();

            SendMessage message = new SendMessage(chatId, createResponseText(update));
            bot.execute(message, getCallback());
        }

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
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
