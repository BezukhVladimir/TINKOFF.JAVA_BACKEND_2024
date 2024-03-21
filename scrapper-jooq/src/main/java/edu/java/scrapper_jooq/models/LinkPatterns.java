package edu.java.scrapper_jooq.models;

import lombok.Getter;

@Getter
public enum LinkPatterns {
    GITHUB("https://github\\.com/[a-zA-Z0-9-]+/[a-zA-Z0-9-]+"),
    STACKOVERFLOW("https://stackoverflow\\.com/questions/\\d+");

    private final String regex;

    LinkPatterns(String regex) {
        this.regex = regex;
    }
}
