package edu.java.scrapper_jooq.clients.github;

import edu.java.scrapper_jooq.dto.github.Response;


public interface Client {
    Response fetchLatestModified(String repositoryName, String authorName);
}
