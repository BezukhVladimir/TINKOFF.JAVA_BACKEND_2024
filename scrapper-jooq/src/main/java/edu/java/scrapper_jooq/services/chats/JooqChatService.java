package edu.java.scrapper_jooq.services.chats;

import edu.java.scrapper_jooq.exceptions.BadRequestException;
import edu.java.scrapper_jooq.exceptions.EntityNotFoundException;
import edu.java.scrapper_jooq.exceptions.NotFoundException;
import edu.java.scrapper_jooq.repositories.chats.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JooqChatService implements ChatService {
    private final ChatRepository jooqChatRepository;

    public void register(Long chatId) {
        try {
            jooqChatRepository.add(chatId);
        } catch (DuplicateKeyException e) {
            throw new BadRequestException("Чат уже зарегистрирован", "Нельзя повторно зарегистрировать чат");
        }
    }

    public void unregister(Long chatId) {
        try {
            jooqChatRepository.remove(chatId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Чат не был зарегистрирован", "Нельзя удалить незарегистрированный чат");
        }
    }
}

