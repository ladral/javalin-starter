package ch.ladral.controller;

import ch.ladral.model.todo.Todo;
import ch.ladral.model.todo.TodoRequest;
import ch.ladral.model.exceptions.NotFoundException;
import ch.ladral.repository.TodoRepository;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

public class TodoController {

    TodoRepository repository;

    public TodoController(TodoRepository repository) {
        this.repository = repository;
    }

    public void getTodos(@NotNull Context context) {
        String searchText = context.queryParam("search");
        if (searchText != null) {
            context.json(this.repository.findByDescription(searchText));
        } else {
            context.json(this.repository.findAll());
        }
    }

    public void getTodoById(@NotNull Context context) {
        Long id = Long.parseLong(context.pathParam("id"));
        this.repository.findById(id)
                .ifPresentOrElse(
                        context::json,
                        () -> context.status(404).result("Todo not found")
                );
    }

    public void createTodo(@NotNull Context context) {
        TodoRequest request = context.bodyValidator(TodoRequest.class)
                .check(obj -> obj.description() != null, "Description cannot be null")
                .check(obj -> !obj.description().isBlank(), "Description cannot be blank")
                .check(obj -> obj.description().length() <= 200, "Description must be 200 characters or less")
                .get();

        Todo newTodo = this.repository.save(request.description());
        context.status(201).json(newTodo);
    }

    public void updateTodo(@NotNull Context context) {
        Long id = Long.parseLong(context.pathParam("id"));
        String description = context.bodyAsClass(TodoRequest.class).description();

        try {
        context.json(repository.update(id, description));

        } catch (NotFoundException e) {
            System.out.println("could not update todo");
            context.status(404).result("Todo Not found");
        }
    }

    public void deleteTodo(@NotNull Context context) {
        Long id = Long.parseLong(context.pathParam("id"));

        boolean removed = repository.deleteById(id);

        if (removed) {
            context.status(204);
        } else {
            context.status(404).result("Todo not found");
        }
    }

}

