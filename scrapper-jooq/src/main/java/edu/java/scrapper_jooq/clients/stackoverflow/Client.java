package edu.java.scrapper_jooq.clients.stackoverflow;

import edu.java.scrapper_jooq.dto.stackoverflow.Response;


public interface Client {
    Response fetchLatestModified(Long questionNumber);
}
