package edu.java.scrapper.clients.github;

import edu.java.scrapper.dto.github.Response;
import java.util.Optional;


public interface Client {
    Response fetchLatestModified(String repositoryName, String authorName);
}
