package edu.java.scrapper.services.links;

import edu.java.scrapper.api.models.LinkResponse;
import java.net.URI;
import java.util.List;

public interface LinkService {
    LinkResponse add(Long chatId, URI url);

    LinkResponse remove(Long chatId, URI url);

    List<LinkResponse> listAll(Long chatId);
}
