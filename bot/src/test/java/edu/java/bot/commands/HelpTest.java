package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.BotApplication;
import edu.java.bot.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {BotApplication.class})
class HelpTest {
    static final long CHAT_ID = 21L;

    Command helpCommand;
    UserService userService;

    @Autowired
    HelpTest(Command helpCommand, UserService userService) {
        this.helpCommand = helpCommand;
        this.userService = userService;
    }

    @MockBean
    Update update;

    @BeforeEach
    void setUpMock() {
        Message messageMock = mock(Message.class);
        Chat chatMock = mock(Chat.class);

        when(update.message()).thenReturn(messageMock);
        when(messageMock.chat()).thenReturn(chatMock);

        when(chatMock.id()).thenReturn(CHAT_ID);
        when(update.message().chat().id()).thenReturn(CHAT_ID);
    }

    @AfterEach
    void clearDatabase() {
        userService.clear();
    }

    @Test
    @DisplayName("Вывод справки")
    void help() {
        // Arrange
        String expectedText = HelpCommand.HELP_MESSAGE
            + "/start — зарегистрироваться в LinkTracker'е" + System.lineSeparator()
            + "/track — начать отслеживание ссылки" + System.lineSeparator()
            + "/untrack — прекратить отслеживание ссылки" + System.lineSeparator()
            + "/list — показать список отслеживаемых ссылок" + System.lineSeparator();

        // Act
        String actualText = helpCommand.handle(update);

        // Assert
        assertThat(actualText).isEqualTo(expectedText);
    }
}
