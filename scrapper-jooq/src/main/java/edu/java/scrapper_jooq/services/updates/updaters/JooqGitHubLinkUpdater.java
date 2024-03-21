package edu.java.scrapper_jooq.services.updates.updaters;

import edu.java.scrapper_jooq.api.models.LinkUpdateRequest;
import edu.java.scrapper_jooq.clients.BotWebClient;
import edu.java.scrapper_jooq.clients.github.Client;
import edu.java.scrapper_jooq.dto.github.Response;
import edu.java.scrapper_jooq.models.Chat;
import edu.java.scrapper_jooq.models.Link;
import edu.java.scrapper_jooq.repositories.chats.ChatRepository;
import edu.java.scrapper_jooq.repositories.links.LinkRepository;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class JooqGitHubLinkUpdater implements LinkUpdater {
    private final Client gitHubRegularWebClient;
    private final ChatRepository jooqChatRepository;
    private final LinkRepository jooqLinkRepository;
    private final BotWebClient botWebClient;

    @Override
    public int process(Link link) {
        String[] args = processLink(link.url());

        Response gitHubResponse = gitHubRegularWebClient
            .fetchLatestModified(args[0], args[1]);

        if (gitHubResponse.createdAt().isAfter(link.lastUpdate())) {
            List<Long> chatIds = jooqChatRepository
                .findAllChatsByUrl(link.url())
                .stream()
                .map(Chat::id)
                .toList();

            try {
                botWebClient.sendUpdate(new LinkUpdateRequest(
                    link.id(),
                    link.url(),
                    getDescription(gitHubResponse),
                    chatIds
                ));
            } catch (Exception ignored) {
            }

            jooqLinkRepository.setLastUpdate(link.url(), gitHubResponse.createdAt());
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
        String[] args = processLink(link.url());

        Response gitHubResponse =
            gitHubRegularWebClient.fetchLatestModified(args[0], args[1]);

        jooqLinkRepository.setLastUpdate(link.url(), gitHubResponse.createdAt());
    }

    private String getDescription(Response gitHubResponse) {
        return
            gitHubResponse.type() + System.lineSeparator()
            + "репозиторий " + gitHubResponse.repo()
            + " создан " + gitHubResponse.actor();
    }
}
