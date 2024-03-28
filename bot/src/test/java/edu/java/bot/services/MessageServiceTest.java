package edu.java.bot.services;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.BotApplication;
import edu.java.bot.models.SessionState;
import edu.java.bot.models.User;
import java.net.URI;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = {BotApplication.class})
class MessageServiceTest {
    @MockBean
    Update update;

    UserService userService;
    MessageService messageService;

    @Autowired
    MessageServiceTest(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    private static final long CHAT_ID = 21L;

    private void setUpMock(String text) {
        Message messageMock = mock(Message.class);
        Chat chatMock = mock(Chat.class);

        when(update.message()).thenReturn(messageMock);
        when(messageMock.chat()).thenReturn(chatMock);

        when(chatMock.id()).thenReturn(CHAT_ID);
        when(update.message().chat().id()).thenReturn(CHAT_ID);

        when(messageMock.text()).thenReturn(text);
        when(update.message().text()).thenReturn(text);
    }

    @AfterEach
    void clearDatabase() {
        userService.clear();
    }

    private Stream<Arguments> notRegisteredUserCommands() {
        return Stream.of(
            Arguments.of("/команда", messageService.UNKNOWN_USER_MESSAGE),
            Arguments.of("приятный набор слов", messageService.UNKNOWN_USER_MESSAGE)
        );
    }

    @ParameterizedTest
    @MethodSource("notRegisteredUserCommands")
    @DisplayName("Пользователь не зарегистрирован, попытка ввести некорректные команды")
    void unknownUser(String text, String expectedText) {
        // Arrange
        setUpMock(text);

        // Act
        String actualText = messageService.createResponseText(update);

        // Assert
        assertThat(actualText).isEqualTo(expectedText);
    }

    private Stream<Arguments> registeredUserCommands() {
        return Stream.of(
            Arguments.of(List.of(), SessionState.DEFAULT, "/команда", messageService.INVALID_COMMAND_MESSAGE),
            Arguments.of(List.of(), SessionState.WAITING_LINK_FOR_TRACKING, "12345 54321", messageService.INVALID_LINK_MESSAGE),
            Arguments.of(List.of(), SessionState.WAITING_LINK_FOR_TRACKING, "https://habr.com/", messageService.NOT_SUPPORTED_LINK_MESSAGE),
            Arguments.of(List.of(), SessionState.WAITING_LINK_FOR_TRACKING, "https://github.com/", messageService.SUCCESSFUL_TRACKING_MESSAGE),
            Arguments.of(List.of(URI.create("https://github.com/")), SessionState.WAITING_LINK_FOR_TRACKING, "https://github.com/", messageService.DUPLICATE_TRACKING_MESSAGE),
            Arguments.of(List.of(URI.create("https://github.com/")), SessionState.WAITING_LINK_FOR_UNTRACKING, "https://stackoverflow.com/", messageService.ABSENT_UNTRACKING_MESSAGE),
            Arguments.of(List.of(URI.create("https://github.com/")), SessionState.WAITING_LINK_FOR_UNTRACKING, "https://github.com/", messageService.SUCCESSFUL_UNTRACKING_MESSAGE)
        );
    }

    @Disabled // нужен сервер
    @ParameterizedTest
    @MethodSource("registeredUserCommands")
    @DisplayName("Пользователь зарегистрирован, попытка ввести некорректные команды")
    void registeredUser(List<URI> links, SessionState sessionState, String text, String expectedText) {
        // Arrange
        setUpMock(text);
        registerUser(links, sessionState);

        // Act
        String actualText = messageService.createResponseText(update);

        // Assert
        assertThat(actualText).isEqualTo(expectedText);
    }

    private void registerUser(List<URI> links, SessionState sessionState) {
        userService.addUser(new User(CHAT_ID, links, sessionState));
    }
}
