package edu.java.scrapper.services.updates.updaters.stackoverflow;

import edu.java.scrapper.api.models.request.LinkUpdateRequest;
import edu.java.scrapper.clients.BotWebClient;
import edu.java.scrapper.clients.stackoverflow.Client;
import edu.java.scrapper.dto.stackoverflow.Response;
import edu.java.scrapper.models.Chat;
import edu.java.scrapper.models.Link;
import edu.java.scrapper.repositories.ChatRepository;
import edu.java.scrapper.repositories.LinkRepository;
import edu.java.scrapper.services.updates.updaters.LinkUpdater;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class StackOverflowLinkUpdater implements LinkUpdater {
    private final BotWebClient botWebClient;
    private final Client stackOverflowRegularWebClient;
    private final ChatRepository chatRepository;
    private final LinkRepository linkRepository;

    @Override
    public int process(Link link) {
        String[] args = processLink(link.getUrl());

        long number = Long.parseLong(args[args.length - 1]);

        Response stackOverflowResponse =
            stackOverflowRegularWebClient.retryFetchLatestModified(number);

        if (stackOverflowResponse.lastActivityDate().isAfter(link.getLastUpdate())) {
            List<Long> chatIds = chatRepository
                .findAllChatsByUrl(link.getUrl())
                .stream()
                .map(Chat::getId)
                .toList();

            try {
                botWebClient.retrySendUpdate(new LinkUpdateRequest(
                    link.getId(),
                    link.getUrl(),
                    getDescription(stackOverflowResponse),
                    chatIds
                ));
            } catch (Exception ignored) {
            }

            linkRepository.setLastUpdate(link.getUrl(), stackOverflowResponse.lastActivityDate());
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
        String[] args = processLink(link.getUrl());

        long number = Long.parseLong(args[args.length - 1]);

        Response stackOverflowResponse =
            stackOverflowRegularWebClient.retryFetchLatestModified(number);

        linkRepository.setLastUpdate(link.getUrl(), stackOverflowResponse.lastActivityDate());
    }

    private String getDescription(Response stackOverflowResponse) {
        return
            "Новый вопрос с номером №" + stackOverflowResponse.questionId() + System.lineSeparator()
            + "от " + stackOverflowResponse.owner().displayName();
    }
}
