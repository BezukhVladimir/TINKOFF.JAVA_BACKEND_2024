package edu.java.scrapper.clients.github;

import edu.java.scrapper.dto.github.Response;


public interface Client {
    Response fetchLatestModified(String repositoryName, String authorName);
}
