package edu.java.clients.stackoverflow;

import edu.java.dto.stackoverflow.Response;
import java.util.Optional;


public interface Client {
    Optional<Response> fetchLatestModified(Long questionNumber);
}
