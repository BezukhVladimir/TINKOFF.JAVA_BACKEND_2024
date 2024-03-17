package edu.java.scrapper.services.jdbc;


import edu.java.scrapper.exceptions.BadRequestException;
import edu.java.scrapper.exceptions.EntityNotFoundException;
import edu.java.scrapper.exceptions.NotFoundException;
import edu.java.scrapper.repositories.ChatRepository;
import edu.java.scrapper.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcChatService implements ChatService {
    private final ChatRepository jdbcChatRepository;

    public void register(Long chatId) {
        try {
            jdbcChatRepository.add(chatId);
        } catch (DuplicateKeyException e) {
            throw new BadRequestException("Чат уже зарегистрирован", "Нельзя повторно зарегистрировать чат");
        }
    }

    public void unregister(Long chatId) {
        try {
            jdbcChatRepository.remove(chatId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Чат не был зарегистрирован", "Нельзя удалить незарегистрированный чат");
        }
    }
}

