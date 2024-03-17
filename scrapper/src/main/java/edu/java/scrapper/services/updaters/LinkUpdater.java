package edu.java.scrapper.services.updaters;

import edu.java.scrapper.models.Link;


public interface LinkUpdater {
    int process(Link link);

    boolean supports(String url);

    String[] processLink(String link);

    String getDomain();

    void setLastUpdate(Link link);
}
