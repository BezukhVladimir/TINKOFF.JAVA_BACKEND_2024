package edu.java.bot.api.controllers;

import edu.java.bot.api.models.LinkUpdateRequest;
import edu.java.bot.services.UpdateService;
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
    private final UpdateService updateService;

    @PostMapping
    public String processUpdate(@RequestBody @Valid LinkUpdateRequest linkUpdateRequest) {
        updateService.addUpdate(linkUpdateRequest);
        return "Обновление обработано";
    }
}
