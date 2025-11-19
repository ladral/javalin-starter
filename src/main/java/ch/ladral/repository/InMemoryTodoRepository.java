package ch.ladral.repository;

import ch.ladral.model.Todo.Todo;
import ch.ladral.model.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTodoRepository implements TodoRepository{

    private final List<Todo> todos = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public InMemoryTodoRepository() {
        todos.add(new Todo(idCounter.getAndIncrement(), "Learn Javalin"));
        todos.add(new Todo(idCounter.getAndIncrement(), "Build REST API"));

    }

    @Override
    public List<Todo> findAll() {
        return this.todos;
    }

    @Override
    public  List<Todo> findByDescription(String description) {
        return this.todos.stream().filter(todo -> todo.description().contains(description)).toList();
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return todos.stream()
                .filter(todo -> todo.id().equals(id))
                .findFirst();
    }

    @Override
    public Todo save(String description) {
        Todo newTodo = new Todo(idCounter.getAndIncrement(), description);
        todos.add(newTodo);
        return newTodo;
    }

    @Override
    public Todo update(Long id, String description) throws NotFoundException {
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).id().equals(id)) {
                Todo updatedTodo = new Todo(id, description);
                todos.set(i, updatedTodo);
                return updatedTodo;

            }
        }

        throw new NotFoundException();
    }

    @Override
    public boolean deleteById(Long id) {
        return todos.removeIf(todo -> todo.id().equals(id));
    }
}
