package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.BotApplication;
import edu.java.bot.models.SessionState;
import edu.java.bot.models.User;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static edu.java.bot.services.UserService.addUser;
import static edu.java.bot.services.UserService.clear;
import static edu.java.bot.services.UserService.findById;
import static edu.java.bot.utils.CommandUtils.findByName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {BotApplication.class})
final class CommandsTest {
    @MockBean
    Update update;

    private static final long CHAT_ID = 21L;

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
        clear();
    }

    private void registerUser() {
        registerUser(List.of());
    }

    private void registerUser(List<URI> links) {
        addUser(new User(CommandsTest.CHAT_ID, links, SessionState.DEFAULT));
    }

    @Nested
    @DisplayName("Тест команды /start")
    final class StartCommandTest {
        private final Command start = findByName("/start").get();

        @Test
        @DisplayName("Регистрация пользователя, затем повторная регистрация")
        void registerUserTwice() {
            // Arrange
            String expectedText1 = StartCommand.SUCCESSFUL_REGISTRATION_MESSAGE;
            String expectedText2 = StartCommand.ALREADY_REGISTERED_MESSAGE;

            // Act
            String actualText1 = start.handle(update);
            String actualText2 = start.handle(update);

            // Assert
            assertThat(findById(CHAT_ID)).isPresent();
            assertThat(actualText1).isEqualTo(expectedText1);
            assertThat(actualText2).isEqualTo(expectedText2);
        }
    }

    @Nested
    @DisplayName("Тест команды /track")
    final class TrackCommandTest {
        private final Command track = findByName("/track").get();

        @Test
        @DisplayName("Смена состояния")
        void waitingLinkForTrackingSessionState() {
            // Arrange
            registerUser();
            String expectedText = TrackCommand.TRACK_MESSAGE;
            SessionState expectedSessionState = SessionState.WAITING_LINK_FOR_TRACKING;


            // Act
            String actualText = track.handle(update);

            // Assert
            assertThat(findById(CHAT_ID)).isPresent();
            assertThat(actualText).isEqualTo(expectedText);
            assertThat(findById(CHAT_ID).get().getState()).isEqualTo(expectedSessionState);
        }

        @Test
        @DisplayName("Пользователь ещё не был зарегистрирован")
        void unknownUser() {
            // Arrange
            String expectedText = TrackCommand.UNKNOWN_USER_MESSAGE;

            // Act
            String actualText = track.handle(update);

            // Assert
            assertThat(findById(CHAT_ID)).isEmpty();
            assertThat(actualText).isEqualTo(expectedText);
        }
    }

    @Nested
    @DisplayName("Тест команды /untrack")
    final class UntrackCommandTest {
        private final Command untrack = findByName("/untrack").get();

        @Test
        @DisplayName("Список отслеживаемых ссылок пустой")
        void emptyLinkList() {
            // Arrange
            registerUser();
            String expectedText = UntrackCommand.EMPTY_LIST_MESSAGE;
            SessionState expectedSessionState = SessionState.DEFAULT;

            // Act
            String actualText = untrack.handle(update);

            // Assert
            assertThat(findById(CHAT_ID)).isPresent();
            assertThat(actualText).isEqualTo(expectedText);
            assertThat(findById(CHAT_ID).get().getState()).isEqualTo(expectedSessionState);
        }

        @Test
        @DisplayName("Смена состояния")
        void waitingLinkForUntrackingSessionState() {
            // Arrange
            registerUser(List.of(URI.create("https://github.com/")));
            String expectedText = UntrackCommand.UNTRACK_MESSAGE;
            SessionState expectedSessionState = SessionState.WAITING_LINK_FOR_UNTRACKING;

            // Act
            String actualText = untrack.handle(update);

            // Assert
            assertThat(findById(CHAT_ID)).isPresent();
            assertThat(actualText).isEqualTo(expectedText);
            assertThat(findById(CHAT_ID).get().getState()).isEqualTo(expectedSessionState);
        }

        @Test
        @DisplayName("Пользователь ещё не был зарегистрирован")
        void unknownUser() {
            // Arrange
            String expectedText = UntrackCommand.UNKNOWN_USER_MESSAGE;

            // Act
            String actualText = untrack.handle(update);

            // Assert
            assertThat(findById(CHAT_ID)).isEmpty();
            assertThat(actualText).isEqualTo(expectedText);
        }
    }

    @Nested
    @DisplayName("Тест команды /help")
    class TestHelpCommand {
        private final Command help = findByName("/help").get();

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
            String actualText = help.handle(update);

            // Assert
            assertThat(actualText).isEqualTo(expectedText);
        }
    }

    @Nested
    @DisplayName("Тест команды /list")
    class ListCommandTest {
        private final Command list = findByName("/list").get();

        @Test
        @DisplayName("Список отслеживаемых ссылок пустой")
        void emptyLinkList() {
            // Arrange
            registerUser();
            String expectedText = ListCommand.EMPTY_LIST_MESSAGE;

            // Act
            String actualText = list.handle(update);

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
            String actualText = list.handle(update);

            // Assert
            assertThat(actualText).isEqualTo(exceptedText);
        }

        @Test
        @DisplayName("Пользователь ещё не был зарегистрирован")
        void unknownUser() {
            // Arrange
            String exceptedText = ListCommand.UNKNOWN_USER_MESSAGE;

            // Act
            String actualText = list.handle(update);

            // Assert
            assertThat(actualText).isEqualTo(exceptedText);
        }
    }
}
