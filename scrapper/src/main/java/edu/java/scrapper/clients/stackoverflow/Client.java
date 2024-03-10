package edu.java.scrapper.clients.stackoverflow;

import edu.java.scrapper.dto.stackoverflow.Response;
import java.util.Optional;


public interface Client {
    Response fetchLatestModified(Long questionNumber);
}
