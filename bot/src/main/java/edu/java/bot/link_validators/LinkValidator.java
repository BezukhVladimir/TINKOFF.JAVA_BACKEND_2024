package edu.java.bot.link_validators;

import java.net.URI;

public abstract class LinkValidator {
    private LinkValidator next;

    public static LinkValidator link(LinkValidator first, LinkValidator... chain) {
        LinkValidator head = first;

        for (LinkValidator nextInChain: chain) {
            head.next = nextInChain;
            head = nextInChain;
        }

        return first;
    }

    protected abstract String getHostName();

    public final boolean isValid(URI uri) {
        if (uri.getHost().equals(getHostName())) {
            return true;
        }

        if (next != null) {
            return next.isValid(uri);
        }

        return false;
    }
}
