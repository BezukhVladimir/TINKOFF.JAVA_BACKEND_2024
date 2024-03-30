package edu.java.scrapper.clients.stackoverflow;

import edu.java.scrapper.dto.stackoverflow.Response;


public interface Client {
    Response fetchLatestModified(Long questionNumber);
    Response retryFetchLatestModified(Long questionNumber);
}
