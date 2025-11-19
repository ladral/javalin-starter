package ch.ladral.repository;

import ch.ladral.model.todo.Todo;
import ch.ladral.model.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {
    List<Todo> findAll();
    List<Todo> findByDescription(String description);
    Optional<Todo> findById(Long id);
    Todo save(String description);
    Todo update(Long id, String description) throws NotFoundException;
    boolean deleteById(Long id);
}
