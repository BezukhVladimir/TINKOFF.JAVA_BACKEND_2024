package edu.java.scrapper.services.updates.updaters.github;

import edu.java.scrapper.api.models.request.LinkUpdateRequest;
import edu.java.scrapper.clients.github.Client;
import edu.java.scrapper.dto.github.Response;
import edu.java.scrapper.models.Chat;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.ChatRepository;
import edu.java.scrapper.repositories.LinkRepository;
import edu.java.scrapper.services.updates.NotificationService;
import edu.java.scrapper.services.updates.updaters.LinkUpdater;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GitHubLinkUpdater implements LinkUpdater {
    private final NotificationService notificationService;
    private final Client gitHubRegularWebClient;
    private final ChatRepository chatRepository;
    private final LinkRepository linkRepository;

    @Override
    public int process(Link link) {
        String[] args = processLink(link.getUrl());

        Response gitHubResponse =
            gitHubRegularWebClient.retryFetchLatestModified(args[0], args[1]);

        if (gitHubResponse.createdAt().isAfter(link.getLastUpdate())) {
            List<Long> chatIds = chatRepository
                .findAllChatsByUrl(link.getUrl())
                .stream()
                .map(Chat::getId)
                .toList();

            try {
                notificationService.sendUpdate(new LinkUpdateRequest(
                    link.getId(),
                    link.getUrl(),
                    getDescription(gitHubResponse),
                    chatIds
                ));
            } catch (Exception ignored) {
            }

            linkRepository.setLastUpdate(link.getUrl(), gitHubResponse.createdAt());
            return 1;
        }

        return 0;
    }

    @Override
    public boolean supports(URI url) {
        return url.toString().startsWith("https://github.com/");
    }

    @Override
    public String[] processLink(URI url) {
        String[] parts = url.toString().split("/");

        String accountName = parts[parts.length - 2];
        String repositoryName = parts[parts.length - 1];

        return new String[] {accountName, repositoryName};
    }

    @Override
    public String getDomain() {
        return "github.com";
    }

    @Override
    public void setLastUpdate(Link link) {
        String[] args = processLink(link.getUrl());

        Response gitHubResponse =
            gitHubRegularWebClient.retryFetchLatestModified(args[0], args[1]);

        linkRepository.setLastUpdate(link.getUrl(), gitHubResponse.createdAt());
    }

    private String getDescription(Response gitHubResponse) {
        return
            gitHubResponse.type() + System.lineSeparator()
            + "репозиторий " + gitHubResponse.repo()
            + " создан " + gitHubResponse.actor();
    }
}
