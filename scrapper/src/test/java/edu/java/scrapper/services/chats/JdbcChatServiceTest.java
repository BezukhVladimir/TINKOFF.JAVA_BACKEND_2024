package edu.java.scrapper.services.chats;

import edu.java.scrapper.exceptions.BadRequestException;
import edu.java.scrapper.exceptions.EntityNotFoundException;
import edu.java.scrapper.exceptions.NotFoundException;
import edu.java.scrapper.repositories.chats.ChatRepository;
import edu.java.scrapper.repositories.chats.JdbcChatRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@SpringBootTest//тут тоже надо отдельно БД настроить
class JdbcChatServiceTest {
    @Autowired
    private ChatService jdbcChatService;
    @MockBean(JdbcChatRepository.class)
    private ChatRepository jdbcChatRepository;

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
