package edu.java.clients.github;

import edu.java.dto.github.Response;
import java.util.Optional;


public interface Client {
    Optional<Response> fetchLatestModified(String repositoryName, String authorName);
}
