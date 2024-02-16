package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.BotApplication;
import edu.java.bot.models.SessionState;
import edu.java.bot.models.User;
import edu.java.bot.services.UserService;
import java.net.URI;
import java.util.List;
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
class ListTest {
    static final long CHAT_ID = 21L;

    Command listCommand;
    UserService userService;

    @Autowired ListTest(Command listCommand, UserService userService) {
        this.listCommand = listCommand;
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
    @DisplayName("Список отслеживаемых ссылок пустой")
    void emptyLinkList() {
        // Arrange
        registerUser();
        String expectedText = ListCommand.EMPTY_LIST_MESSAGE;

        // Act
        String actualText = listCommand.handle(update);

        // Assert
        assertThat(actualText).isEqualTo(expectedText);
    }

    @Test
    @DisplayName("Не пустой список отслеживаемых ссылок")
    void nonEmptyLinkList() {
        // Arrange
        String link = "https://github.com/";
        registerUser(List.of(URI.create(link)));
        String exceptedText = ListCommand.LIST_MESSAGE + link + System.lineSeparator();

        // Act
        String actualText = listCommand.handle(update);

        // Assert
        assertThat(actualText).isEqualTo(exceptedText);
    }

    @Test
    @DisplayName("Пользователь ещё не был зарегистрирован")
    void unknownUser() {
        // Arrange
        String exceptedText = ListCommand.UNKNOWN_USER_MESSAGE;

        // Act
        String actualText = listCommand.handle(update);

        // Assert
        assertThat(actualText).isEqualTo(exceptedText);
    }

    private void registerUser() {
        registerUser(List.of());
    }

    private void registerUser(List<URI> links) {
        userService.addUser(new User(CHAT_ID, links, SessionState.DEFAULT));
    }
}
