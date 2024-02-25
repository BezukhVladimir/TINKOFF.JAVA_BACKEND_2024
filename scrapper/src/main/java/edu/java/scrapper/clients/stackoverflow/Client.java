package edu.java.scrapper.clients.stackoverflow;

import edu.java.scrapper.dto.stackoverflow.Response;
import java.util.Optional;


public interface Client {
    Optional<Response> fetchLatestModified(Long questionNumber);
}
