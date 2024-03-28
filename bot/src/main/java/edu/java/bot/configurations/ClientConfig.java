package edu.java.bot.configurations;

import edu.java.bot.clients.ScrapperWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ClientConfig {

    @Value(value = "${api.scrapper.baseUrl}")
    public String scrapperBaseUrl;

    @Bean
    public ScrapperWebClient scrapperWebClient() {
        return new ScrapperWebClient(scrapperBaseUrl);
    }
}
