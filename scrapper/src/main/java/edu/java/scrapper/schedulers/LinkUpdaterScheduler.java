package edu.java.scrapper.schedulers;

import edu.java.scrapper.services.updates.LinkUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@ConditionalOnProperty(value = "app.scheduler.enable", havingValue = "true")
@RequiredArgsConstructor
public class LinkUpdaterScheduler {
    private final LinkUpdateService linkUpdateService;

    @Scheduled(fixedDelayString = "${app.scheduler.interval}")
    public void update() {
        log.info("Running update");

        try {
            linkUpdateService.update();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedDelayString = "${app.scheduler.removeUnusedLinksInterval}")
    public void removeUnusedLinks() {
        log.info("Remove unused links");

        try {
            linkUpdateService.removeUnusedLinks();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
