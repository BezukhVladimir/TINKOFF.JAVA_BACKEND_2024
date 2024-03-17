package edu.java.scrapper.services.jooq;

import edu.java.scrapper.exceptions.BadRequestException;
import edu.java.scrapper.exceptions.EntityNotFoundException;
import edu.java.scrapper.exceptions.NotFoundException;
import edu.java.scrapper.repositories.ChatRepository;
import edu.java.scrapper.repositories.jdbc.JooqChatRepository;
import edu.java.scrapper.services.ChatService;
import edu.java.scrapper.services.jdbc.JdbcChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


class JooqChatServiceTest {
    private ChatRepository jooqChatRepository;
    private ChatService jooqChatService;

    @BeforeEach
    void setUp() {
        jooqChatRepository = mock(JooqChatRepository.class);
        jooqChatService = new JdbcChatService(jooqChatRepository);
    }

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
