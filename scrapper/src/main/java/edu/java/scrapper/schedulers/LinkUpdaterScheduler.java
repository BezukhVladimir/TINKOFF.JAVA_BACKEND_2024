package edu.java.scrapper.schedulers;

import edu.java.scrapper.services.updates.LinkUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "app.scheduler.enable", havingValue = "true")
public class LinkUpdaterScheduler {
    private final LinkUpdateService jdbcLinkUpdateService;

    @Scheduled(fixedDelayString = "${app.scheduler.interval}")
    public void update() {
        log.info("Running update");

        try {
            jdbcLinkUpdateService.update();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedDelayString = "${app.scheduler.removeUnusedLinksInterval}")
    public void removeUnusedLinks() {
        log.info("Remove unused links");

        try {
            jdbcLinkUpdateService.removeUnusedLinks();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
