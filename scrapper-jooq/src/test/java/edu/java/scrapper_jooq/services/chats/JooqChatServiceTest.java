package edu.java.scrapper_jooq.services.chats;

import edu.java.scrapper_jooq.exceptions.BadRequestException;
import edu.java.scrapper_jooq.exceptions.EntityNotFoundException;
import edu.java.scrapper_jooq.exceptions.NotFoundException;
import edu.java.scrapper_jooq.repositories.chats.ChatRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@SpringBootTest
class JooqChatServiceTest {
    @Autowired
    private ChatService jooqChatService;
    @MockBean
    private ChatRepository jooqChatRepository;

    @Test
    public void register() {
        // Act
        jooqChatService.register(1L);

        // Assert
        verify(jooqChatRepository).add(1L);
    }

    @Test
    public void registerChatTwice() {
        // Arrange
        doAnswer(invocation -> {
            throw new DuplicateKeyException("");
        }).when(jooqChatRepository).add(1L);

        // Act
        Throwable thrown = catchThrowable(() -> {
            jooqChatService.register(1L);
        });

        // Assert
        assertThat(thrown)
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Чат уже зарегистрирован");
    }

    @Test
    public void unregister() {
        // Act
        jooqChatService.unregister(1L);

        // Assert
        verify(jooqChatRepository).remove(1L);
    }

    @Test
    public void unregisterChatTwice(){
        // Arrange
        doAnswer(invocation -> {
            throw new EntityNotFoundException("");
        }).when(jooqChatRepository).remove(1L);

        // Act
        Throwable thrown = catchThrowable(() -> {
            jooqChatService.unregister(1L);
        });

        // Assert
        assertThat(thrown)
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Чат не был зарегистрирован");
    }
}