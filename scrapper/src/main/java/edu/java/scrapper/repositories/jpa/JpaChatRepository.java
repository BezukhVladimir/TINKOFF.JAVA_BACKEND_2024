package edu.java.scrapper.repositories.jpa;

import edu.java.scrapper.models.Chat;
import edu.java.scrapper.repositories.ChatRepository;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaChatRepository implements ChatRepository {
    private final JpaChatRepositoryInterface jpaChatRepositoryInterface;

    @Override
    public Chat add(Long id) {
        return jpaChatRepositoryInterface.save(new Chat().setId(id));
    }

    @Override
    public void remove(Long id) {
        jpaChatRepositoryInterface.deleteById(id);
    }

    @Override
    public void removeAll() {
        jpaChatRepositoryInterface.deleteAll();
    }

    @Override
    public Chat findById(Long id) {
        return jpaChatRepositoryInterface.findById(id).orElse(null);
    }

    @Override
    public List<Chat> findAll() {
        return jpaChatRepositoryInterface.findAll();
    }

    @Override
    public List<Chat> findAllChatsByUrl(URI url) {
        return jpaChatRepositoryInterface.findAllChatsByUrl(url);
    }
}
