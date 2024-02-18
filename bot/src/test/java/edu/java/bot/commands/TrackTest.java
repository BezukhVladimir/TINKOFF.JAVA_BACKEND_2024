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
class TrackTest {
    static final long CHAT_ID = 21L;

    Command trackCommand;
    UserService userService;

    @Autowired
    TrackTest(Command trackCommand, UserService userService) {
        this.trackCommand = trackCommand;
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
    @DisplayName("Смена состояния")
    void waitingLinkForTrackingSessionState() {
        // Arrange
        registerUser();
        String expectedText = TrackCommand.TRACK_MESSAGE;
        SessionState expectedSessionState = SessionState.WAITING_LINK_FOR_TRACKING;


        // Act
        String actualText = trackCommand.handle(update);

        // Assert
        assertThat(userService.findById(CHAT_ID)).isPresent();
        assertThat(actualText).isEqualTo(expectedText);
        assertThat(userService.findById(CHAT_ID).get().getState()).isEqualTo(expectedSessionState);
    }

    @Test
    @DisplayName("Пользователь ещё не был зарегистрирован")
    void unknownUser() {
        // Arrange
        String expectedText = TrackCommand.UNKNOWN_USER_MESSAGE;

        // Act
        String actualText = trackCommand.handle(update);

        // Assert
        assertThat(userService.findById(CHAT_ID)).isEmpty();
        assertThat(actualText).isEqualTo(expectedText);
    }

    private void registerUser() {
        registerUser(List.of());
    }

    private void registerUser(List<URI> links) {
        userService.addUser(new User(CHAT_ID, links, SessionState.DEFAULT));
    }
}
