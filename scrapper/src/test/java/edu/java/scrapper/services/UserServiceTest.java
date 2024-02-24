package edu.java.scrapper.services;


import edu.java.scrapper.api.models.LinkResponse;
import edu.java.scrapper.exceptions.BadRequestException;
import edu.java.scrapper.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowableOfType;

public class UserServiceTest {
    private UserService userService;

    @BeforeEach
    public void init(){
        userService = new UserService();
    }

    @Test
    public void registerChatTwice() {
        // Act
        userService.registerChat(1L);
        Throwable actualException = catchThrowableOfType(
            () -> userService.registerChat(1L),
            BadRequestException.class
        );

        // Assert
        assertThat(actualException)
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    public void deleteChatTwice() {
        // Arrange
        userService.registerChat(1L);

        // Act
        userService.deleteChat(1L);
        Throwable actualException = catchThrowableOfType(
            () -> userService.deleteChat(1L),
            NotFoundException.class
        );

        // Assert
        assertThat(actualException)
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void linksNotFound() {
        // Act
        Throwable actualException = catchThrowableOfType(
            () -> userService.getLinks(1L),
            NotFoundException.class
        );

        // Assert
        assertThat(actualException)
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void getLinks() {
        // Arrange
        userService.registerChat(1L);

        // Act
        List<LinkResponse> links = userService.getLinks(1L);

        // Assert
        assertThat(links).isNotNull().isEmpty();
    }

    @Test
    public void addLink() throws URISyntaxException {
        // Arrange
        userService.registerChat(1L);

        // Act
        LinkResponse addedLink =
            userService.addLink(1L, new URI("123"));

        // Assert
        assertThat(addedLink).isNotNull();
    }

    @Test
    public void addLinkBadRequest() throws URISyntaxException {
        // Arrange
        userService.registerChat(1L);
        userService.addLink(1L, new URI("123"));

        // Act
        Throwable actualException = catchThrowableOfType(
            () -> userService.addLink(1L, new URI("123")),
            BadRequestException.class
        );

        // Assert
        assertThat(actualException)
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    public void linkNotFound() throws URISyntaxException {
        // Arrange
        userService.registerChat(1L);

        // Act
        Throwable actualException = catchThrowableOfType(
            () -> userService.removeLink(1L, new URI("123")),
            NotFoundException.class
        );

        // Assert
        assertThat(actualException)
            .isInstanceOf(NotFoundException.class);
    }
    @Test
    public void removeLinkTwice() throws URISyntaxException {
        // Arrange
        userService.registerChat(1L);
        userService.addLink(1L, new URI("123"));

        // Act
        LinkResponse removedLink =
            userService.removeLink(1L, new URI("123"));
        Throwable actualException = catchThrowableOfType(
            () -> userService.removeLink(1L, new URI("123")),
            NotFoundException.class
        );

        // Assert
        assertThat(removedLink).isNotNull();
        assertThat(removedLink.url()).isEqualTo(new URI("123"));
        assertThat(actualException)
            .isInstanceOf(NotFoundException.class);
    }
}
