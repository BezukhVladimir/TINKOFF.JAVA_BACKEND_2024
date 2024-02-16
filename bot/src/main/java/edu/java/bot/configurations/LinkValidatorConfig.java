package edu.java.bot.configurations;

import edu.java.bot.link_validators.GitHubLinkValidator;
import edu.java.bot.link_validators.LinkValidator;
import edu.java.bot.link_validators.StackOverflowLinkValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LinkValidatorConfig {
    @Bean
    public LinkValidator linkValidator() {
        return LinkValidator.link(
            new GitHubLinkValidator(),
            new StackOverflowLinkValidator()
        );
    }
}
