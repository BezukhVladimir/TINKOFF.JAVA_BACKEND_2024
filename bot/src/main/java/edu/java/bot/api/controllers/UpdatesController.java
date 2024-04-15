package edu.java.bot.api.controllers;

import edu.java.bot.api.models.requests.LinkUpdateRequest;
import edu.java.bot.services.UpdatesListenersService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
public class UpdatesController {
    private final UpdatesListenersService updatesListenersService;
    Counter updatesProcessed = Metrics.counter("updates.processed");

    @PostMapping
    public String processUpdate(@RequestBody @Valid LinkUpdateRequest linkUpdateRequest) {
        updatesListenersService.process(linkUpdateRequest);
        updatesProcessed.increment();
        return "Обновление обработано";
    }
}
