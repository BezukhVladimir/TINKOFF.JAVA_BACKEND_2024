package edu.java.bot.models;

import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private long chatId;
    private List<URI> links;
    private SessionState state;

    public boolean waitingLinkForTracking() {
        return state.equals(SessionState.WAITING_LINK_FOR_TRACKING);
    }

    public boolean waitingLinkForUntracking() {
        return state.equals(SessionState.WAITING_LINK_FOR_UNTRACKING);
    }
}
