package edu.java.scrapper.services;


import edu.java.scrapper.exceptions.BadRequestException;
import edu.java.scrapper.exceptions.EntityNotFoundException;
import edu.java.scrapper.exceptions.NotFoundException;
import edu.java.scrapper.repositories.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public void register(Long chatId) {
        try {
            chatRepository.add(chatId);
        } catch (DuplicateKeyException e) {
            throw new BadRequestException("Чат уже зарегистрирован", "Нельзя повторно зарегистрировать чат");
        }
    }

    public void unregister(Long chatId) {
        try {
            chatRepository.remove(chatId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Чат не был зарегистрирован", "Нельзя удалить незарегистрированный чат");
        }
    }
}

