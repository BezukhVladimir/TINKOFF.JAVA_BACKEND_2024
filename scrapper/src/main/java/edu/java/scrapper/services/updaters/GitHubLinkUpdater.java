package edu.java.scrapper.services.updaters;

import edu.java.scrapper.api.models.LinkUpdateRequest;
import edu.java.scrapper.clients.BotWebClient;
import edu.java.scrapper.clients.github.Client;
import edu.java.scrapper.dto.github.Response;
import edu.java.scrapper.models.Chat;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.ChatRepository;
import edu.java.scrapper.repositories.LinkRepository;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GitHubLinkUpdater implements LinkUpdater {
    private final Client gitHubRegularWebClient;
    private final ChatRepository chatRepository;
    private final LinkRepository linkRepository;
    private final BotWebClient botWebClient;

    @Override
    public int process(Link link) {
        String[] args = processLink(link.url());

        Response gitHubResponse = gitHubRegularWebClient
            .fetchLatestModified(args[0], args[1]);

        if (gitHubResponse.createdAt().isAfter(link.lastUpdate())) {
            List<Long> chatIds = chatRepository
                .findAllChatsByUrl(link.url())
                .stream()
                .map(Chat::id)
                .collect(Collectors.toList());

            try {
                botWebClient.sendUpdate(new LinkUpdateRequest(
                    link.id(),
                    new URI(link.url()),
                    getDescription(gitHubResponse),
                    chatIds
                ));
            } catch (Exception ignored) {
            }

            linkRepository.setLastUpdate(link.url(), gitHubResponse.createdAt());
            return 1;
        }

        return 0;
    }

    @Override
    public boolean supports(String url) {
        return url.startsWith("https://github.com/");
    }

    @Override
    public String[] processLink(String url) {
        String[] parts = url.split("/");

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
        String[] args = processLink(link.url());

        Response gitHubResponse =
            gitHubRegularWebClient.fetchLatestModified(args[0], args[1]);

        linkRepository.setLastUpdate(link.url(), gitHubResponse.createdAt());
    }

    private String getDescription(Response gitHubResponse) {
        return
            gitHubResponse.type() + System.lineSeparator()
            + "repo " + gitHubResponse.repo()
            + " by " + gitHubResponse.actor();
    }
}
