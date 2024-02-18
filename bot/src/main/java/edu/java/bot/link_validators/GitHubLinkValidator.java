package edu.java.bot.link_validators;

public class GitHubLinkValidator extends LinkValidator {
    @Override
    protected String getHostName() {
        return "github.com";
    }
}
