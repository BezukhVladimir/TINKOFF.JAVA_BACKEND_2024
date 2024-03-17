package edu.java.scrapper.services.jdbc;

import edu.java.scrapper.exceptions.BadRequestException;
import edu.java.scrapper.exceptions.EntityNotFoundException;
import edu.java.scrapper.exceptions.NotFoundException;
import edu.java.scrapper.repositories.ChatRepository;
import edu.java.scrapper.repositories.jdbc.JooqChatRepository;
import edu.java.scrapper.services.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


class JdbcChatServiceTest {
    private ChatRepository jdbcChatRepository;
    private ChatService jdbcChatService;

    @BeforeEach
    void setUp() {
        jdbcChatRepository = mock(JooqChatRepository.class);
        jdbcChatService = new JdbcChatService(jdbcChatRepository);
    }

    @Test
    public void register() {
        // Act
        jdbcChatService.register(1L);

        // Assert
        verify(jdbcChatRepository).add(1L);
    }

    @Test
    public void registerChatTwice() {
        // Arrange
        doAnswer(invocation -> {
            throw new DuplicateKeyException("");
        }).when(jdbcChatRepository).add(1L);

        // Act
        Throwable thrown = catchThrowable(() -> {
            jdbcChatService.register(1L);
        });

        // Assert
        assertThat(thrown)
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Чат уже зарегистрирован");
    }

    @Test
    public void unregister() {
        // Act
        jdbcChatService.unregister(1L);

        // Assert
        verify(jdbcChatRepository).remove(1L);
    }

    @Test
    public void unregisterChatTwice(){
        // Arrange
        doAnswer(invocation -> {
            throw new EntityNotFoundException("");
        }).when(jdbcChatRepository).remove(1L);

        // Act
        Throwable thrown = catchThrowable(() -> {
            jdbcChatService.unregister(1L);
        });

        // Assert
        assertThat(thrown)
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Чат не был зарегистрирован");
    }
}
