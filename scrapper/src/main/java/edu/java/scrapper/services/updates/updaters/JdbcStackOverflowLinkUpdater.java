package edu.java.scrapper.services.updates.updaters;

import edu.java.scrapper.api.models.LinkUpdateRequest;
import edu.java.scrapper.clients.BotWebClient;
import edu.java.scrapper.clients.stackoverflow.Client;
import edu.java.scrapper.dto.stackoverflow.Response;
import edu.java.scrapper.models.Chat;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.chats.ChatRepository;
import edu.java.scrapper.repositories.links.LinkRepository;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class JdbcStackOverflowLinkUpdater implements LinkUpdater {
    private final Client stackOverflowRegularWebClient;
    private final ChatRepository chatRepository;
    private final LinkRepository linkRepository;
    private final BotWebClient botWebClient;

    @Override
    public int process(Link link) {
        String[] args = processLink(link.url());

        long number = Long.parseLong(args[args.length - 1]);

        Response stackOverflowResponse =
            stackOverflowRegularWebClient.fetchLatestModified(number);

        if (stackOverflowResponse.lastActivityDate().isAfter(link.lastUpdate())) {
            List<Long> chatIds = chatRepository
                .findAllChatsByUrl(link.url())
                .stream()
                .map(Chat::id)
                .toList();

            try {
                botWebClient.sendUpdate(new LinkUpdateRequest(
                    link.id(),
                    link.url(),
                    getDescription(stackOverflowResponse),
                    chatIds
                ));
            } catch (Exception ignored) {
            }

            linkRepository.setLastUpdate(link.url(), stackOverflowResponse.lastActivityDate());
            return 1;
        }
        return 0;
    }

    @Override
    public boolean supports(URI url) {
        return url.toString().startsWith("https://stackoverflow.com/questions/");
    }

    @Override
    public String[] processLink(URI url) {
        return url.toString().split("/");
    }

    @Override
    public String getDomain() {
        return "stackoverflow.com";
    }

    @Override
    public void setLastUpdate(Link link) {
        String[] args = processLink(link.url());

        long number = Long.parseLong(args[args.length - 1]);

        Response stackOverflowResponse =
            stackOverflowRegularWebClient.fetchLatestModified(number);

        linkRepository.setLastUpdate(link.url(), stackOverflowResponse.lastActivityDate());
    }

    private String getDescription(Response stackOverflowResponse) {
        return
            "Новый вопрос с номером №" + stackOverflowResponse.questionId() + System.lineSeparator()
            + "от " + stackOverflowResponse.owner().displayName();
    }
}
