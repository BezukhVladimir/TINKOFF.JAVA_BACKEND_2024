package edu.java.scrapper.services.updaters;

import edu.java.scrapper.api.models.LinkUpdateRequest;
import edu.java.scrapper.clients.BotWebClient;
import edu.java.scrapper.clients.stackoverflow.Client;
import edu.java.scrapper.dto.stackoverflow.Response;
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
public class StackOverflowLinkUpdater implements LinkUpdater {
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
                .collect(Collectors.toList());

            try {
                botWebClient.sendUpdate(new LinkUpdateRequest(
                    link.id(),
                    new URI(link.url()),
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
    public boolean supports(String url) {
        return url.startsWith("https://stackoverflow.com/questions/");
    }

    @Override
    public String[] processLink(String url) {
        return url.split("/");
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
            "New answer for question №" + stackOverflowResponse.questionId() + System.lineSeparator()
            + "owner " + stackOverflowResponse.owner().displayName();
    }
}
