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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static edu.java.bot.services.UserService.addUser;
import static edu.java.bot.services.UserService.clear;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {BotApplication.class})
final class MessageServiceTest {
    @MockBean
    Update update;

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
        clear();
    }

    private void registerUser(List<URI> links, SessionState sessionState) {
        addUser(new User(CHAT_ID, links, sessionState));
    }

    private static Stream<Arguments> notRegisteredUserCommands() {
        return Stream.of(
            Arguments.of("/команда", MessageService.UNKNOWN_USER_MESSAGE),
            Arguments.of("приятный набор слов", MessageService.UNKNOWN_USER_MESSAGE)
        );
    }

    @ParameterizedTest
    @MethodSource("notRegisteredUserCommands")
    @DisplayName("Пользователь не зарегистрирован, попытка ввести некорректные команды")
    void unknownUser(String text, String exceptedText) {
        // Arrange
        setUpMock(text);

        // Act
        String actualText = MessageService.createResponseText(update);

        // Assert
        assertThat(actualText).isEqualTo(exceptedText);
    }

    private static Stream<Arguments> registeredUserCommands() {
        return Stream.of(
            Arguments.of(List.of(), SessionState.DEFAULT, "/команда", MessageService.INVALID_COMMAND_MESSAGE),
            Arguments.of(List.of(), SessionState.WAITING_LINK_FOR_TRACKING, "12345 54321", MessageService.INVALID_LINK_MESSAGE),
            Arguments.of(List.of(), SessionState.WAITING_LINK_FOR_TRACKING, "https://habr.com/", MessageService.NOT_SUPPORTED_LINK_MESSAGE),
            Arguments.of(List.of(), SessionState.WAITING_LINK_FOR_TRACKING, "https://github.com/", MessageService.SUCCESSFUL_TRACKING_MESSAGE),
            Arguments.of(List.of(URI.create("https://github.com/")), SessionState.WAITING_LINK_FOR_TRACKING, "https://github.com/", MessageService.DUPLICATE_TRACKING_MESSAGE),
            Arguments.of(List.of(URI.create("https://github.com/")), SessionState.WAITING_LINK_FOR_UNTRACKING, "https://stackoverflow.com/", MessageService.ABSENT_UNTRACKING_MESSAGE),
            Arguments.of(List.of(URI.create("https://github.com/")), SessionState.WAITING_LINK_FOR_UNTRACKING, "https://github.com/", MessageService.SUCCESSFUL_UNTRACKING_MESSAGE)
        );
    }

    @ParameterizedTest
    @MethodSource("registeredUserCommands")
    @DisplayName("Пользователь зарегистрирован, попытка ввести некорректные команды")
    void registeredUser(List<URI> links, SessionState sessionState, String text, String exceptedText) {
        // Arrange
        setUpMock(text);
        registerUser(links, sessionState);

        // Act
        String actualText = MessageService.createResponseText(update);

        // Assert
        assertThat(actualText).isEqualTo(exceptedText);
    }
}
