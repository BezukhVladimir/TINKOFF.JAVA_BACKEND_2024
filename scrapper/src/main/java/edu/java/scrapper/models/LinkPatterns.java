package edu.java.scrapper.models;

import lombok.Getter;

@Getter
public enum LinkPatterns {
    GITHUB("https://github\\.com/[._a-zA-Z0-9-]+/[._a-zA-Z0-9-]+/?"),
    STACKOVERFLOW("https://stackoverflow\\.com/questions/\\d+/?");

    private final String regex;

    LinkPatterns(String regex) {
        this.regex = regex;
    }
}
