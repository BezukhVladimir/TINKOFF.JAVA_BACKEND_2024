package edu.java.bot.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowableOfType;
import edu.java.bot.api.models.LinkUpdateRequest;
import edu.java.bot.exceptions.UpdateAlreadyExistsException;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


public class UpdateServiceTest {
    @Test
    public void addUpdateTwice() throws URISyntaxException {
        // Arrange
        UpdateService updateService = new UpdateService();
        LinkUpdateRequest update = new LinkUpdateRequest(
            1L,
            new URI("123"),
            "123",
            List.of(1L, 2L)
        );

        // Act
        updateService.addUpdate(update);
        Throwable actualException = catchThrowableOfType(
            () -> updateService.addUpdate(update),
            UpdateAlreadyExistsException.class
        );

        // Assert
        assertThat(actualException)
            .isInstanceOf(UpdateAlreadyExistsException.class);
    }
}
