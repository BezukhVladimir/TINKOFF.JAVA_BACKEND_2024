package edu.java.scrapper.services.updates.updaters;

import edu.java.scrapper.models.Link;
import java.net.URI;

public interface LinkUpdater {
    int process(Link link);

    boolean supports(URI url);

    String[] processLink(URI url);

    String getDomain();

    void setLastUpdate(Link link);
}
