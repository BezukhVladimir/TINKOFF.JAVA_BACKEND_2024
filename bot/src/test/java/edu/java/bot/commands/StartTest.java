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
class StartTest {
    static final long CHAT_ID = 21L;

    Command startCommand;
    UserService userService;

    @Autowired
    StartTest(Command startCommand, UserService userService) {
        this.startCommand = startCommand;
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
    @DisplayName("Регистрация пользователя, затем повторная регистрация")
    void registerUserTwice() {
        // Arrange
        String expectedText1 = StartCommand.SUCCESSFUL_REGISTRATION_MESSAGE;
        String expectedText2 = StartCommand.ALREADY_REGISTERED_MESSAGE;

        // Act
        String actualText1 = startCommand.handle(update);
        String actualText2 = startCommand.handle(update);

        // Assert
        assertThat(userService.findById(CHAT_ID)).isPresent();
        assertThat(actualText1).isEqualTo(expectedText1);
        assertThat(actualText2).isEqualTo(expectedText2);
    }
}
