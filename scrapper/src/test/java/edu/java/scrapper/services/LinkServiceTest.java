package edu.java.scrapper.services;

import edu.java.scrapper.api.models.LinkResponse;
import edu.java.scrapper.exceptions.BadRequestException;
import edu.java.scrapper.exceptions.NotFoundException;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.ChatRepository;
import edu.java.scrapper.repositories.jdbc.JdbcChatRepository;
import edu.java.scrapper.repositories.jdbc.JdbcLinkRepository;
import edu.java.scrapper.repositories.LinkRepository;
import edu.java.scrapper.services.updaters.LinkHolder;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class LinkServiceTest {
    private final ChatRepository chatRepository = mock(JdbcChatRepository.class);
    private final LinkRepository linkRepository = mock(JdbcLinkRepository.class);
    private final LinkHolder linkHolder = mock(LinkHolder.class);
    private final LinkService linkService = new LinkService(
        chatRepository, linkRepository, linkHolder
    );

    @Test
    public void listAll() {
        // Arrange
        Link link1 = new Link(1L, "", OffsetDateTime.now());
        Link link2 = new Link(2L, "", OffsetDateTime.now());
        Link link3 = new Link(3L, "", OffsetDateTime.now());

        when(linkRepository.findAllLinksByChatId(1L))
            .thenReturn(List.of(link1, link2, link3));

        // Act
        List<LinkResponse> links = linkService.listAll(1L);

        // Assert
        assertThat(links).hasSize(3);
        verify(linkRepository).findAllLinksByChatId(1L);
    }

    @Test
    public void chatNotFound() {
        // Arrange
        Long chatId = 1L;
        String url = "https://github.com/author/repo";
        when(chatRepository.findById(chatId))
            .thenThrow(new DataIntegrityViolationException(""));

        // Act
        Throwable thrown = catchThrowable(() -> {
            linkService.add(chatId, new URI(url));
        });

        // Assert
        assertThat(thrown)
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Чат не был зарегистрирован");
    }

    @Test
    public void addNonValidLink() {
        // Arrange
        Long chatId = 1L;
        String url = "https://abcde.com";

        // Act
        Throwable thrown = catchThrowable(() -> {
            linkService.add(chatId, new URI(url));
        });

        // Assert
        assertThat(thrown)
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Неизвестная ссылка");
    }

    @Test
    public void linkAlreadyExist() {
        // Arrange
        Long chatId = 1L;
        String url = "https://github.com/author/repo";
        when(linkRepository.add(chatId, url))
            .thenThrow(new DuplicateKeyException(""));

        // Act
        Throwable thrown = catchThrowable(() -> {
            linkService.add(chatId, new URI(url));
        });

        // Assert
        assertThat(thrown)
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Ссылка уже отслеживается");
    }

    @Test
    public void correctAdd() throws URISyntaxException {
        // Arrange
        Long chatId = 1L;
        String url = "https://github.com/author/repo";
        when(linkRepository.add(chatId, url))
            .thenReturn(new Link(chatId, url, OffsetDateTime.now()));

        // Act
        LinkResponse link = linkService.add(chatId, new URI(url));

        // Assert
        assertThat(link.id()).isEqualTo(chatId);
        assertThat(link.url().toString()).isEqualTo(url);
    }

    @Test
    public void incorrectRemove() {
        // Arrange
        Long chatId = 1L;
        String url = "https://github.com/author/repo";
        when(chatRepository.findById(chatId))
            .thenThrow(new DataIntegrityViolationException(""));


        // Act
        Throwable thrown = catchThrowable(() -> {
            linkService.add(chatId, new URI(url));
        });

        // Assert
        assertThat(thrown)
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Чат не был зарегистрирован");
    }

    @Test
    public void correctRemove() throws URISyntaxException {
        // Arrange
        Long chatId = 1L;
        String url = "https://github.com/author/repo";

        // Act
        LinkResponse link = linkService.remove(chatId, new URI(url));


        // Assert
        assertThat(link.id()).isEqualTo(chatId);
        assertThat(link.url().toString()).isEqualTo(url);
        verify(linkRepository).remove(chatId, url);
    }
}
