package edu.java.scrapper.services;

import edu.java.scrapper.api.models.response.LinkResponse;
import edu.java.scrapper.exceptions.BadRequestException;
import edu.java.scrapper.exceptions.NotFoundException;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.ChatRepository;
import edu.java.scrapper.repositories.LinkRepository;
import edu.java.scrapper.services.updates.LinkHolder;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {
    @InjectMocks
    private LinkService linkService;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private LinkRepository linkRepository;
    @Mock
    private LinkHolder linkHolder;

    @Test
    public void listAll() {
        // Arrange
        Link link1 = new Link().setId(1L).setUrl(URI.create("")).setLastUpdate(OffsetDateTime.now());
        Link link2 = new Link().setId(2L).setUrl(URI.create("")).setLastUpdate(OffsetDateTime.now());
        Link link3 = new Link().setId(3L).setUrl(URI.create("")).setLastUpdate(OffsetDateTime.now());

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
        URI url = URI.create("https://github.com/author/repo");
        when(chatRepository.findById(chatId))
            .thenThrow(new DataIntegrityViolationException(""));

        // Act
        Throwable thrown = catchThrowable(() -> {
            linkService.add(chatId, url);
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
        URI url = URI.create("https://abcde.com");

        // Act
        Throwable thrown = catchThrowable(() -> {
            linkService.add(chatId, url);
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
        URI url = URI.create("https://github.com/author/repo");
        when(linkRepository.add(chatId, url))
            .thenThrow(new DuplicateKeyException(""));

        // Act
        Throwable thrown = catchThrowable(() -> {
            linkService.add(chatId, url);
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
        URI url = URI.create("https://github.com/author/repo");
        when(linkRepository.add(chatId, url))
            .thenReturn(new Link().setId(chatId).setUrl(url).setLastUpdate(OffsetDateTime.now()));

        // Act
        LinkResponse link = linkService.add(chatId, url);

        // Assert
        assertThat(link.id()).isEqualTo(chatId);
        assertThat(link.url()).isEqualTo(url);
    }

    @Test
    public void incorrectRemove() {
        // Arrange
        Long chatId = 1L;
        URI url = URI.create("https://github.com/author/repo");
        when(chatRepository.findById(chatId))
            .thenThrow(new DataIntegrityViolationException(""));


        // Act
        Throwable thrown = catchThrowable(() -> {
            linkService.add(chatId, url);
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
        URI url = URI.create("https://github.com/author/repo");

        // Act
        LinkResponse link = linkService.remove(chatId, url);


        // Assert
        assertThat(link.id()).isEqualTo(chatId);
        assertThat(link.url()).isEqualTo(url);
        verify(linkRepository).remove(chatId, url);
    }
}
