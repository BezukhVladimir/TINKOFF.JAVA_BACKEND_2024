package edu.java.bot.services;

import edu.java.bot.api.models.LinkUpdateRequest;
import edu.java.bot.exceptions.UpdateAlreadyExistsException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;


@Service
public class UpdateService {
    private final Set<LinkUpdateRequest> updates = new HashSet<>();

    public void addUpdate(LinkUpdateRequest linkUpdateRequest) {
        if (!updates.add(linkUpdateRequest)) {
            throw new UpdateAlreadyExistsException("Update уже существует");
        }
    }
}
