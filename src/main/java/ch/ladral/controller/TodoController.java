package ch.ladral.controller;

import ch.ladral.model.Todo;
import ch.ladral.model.TodoRequest;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class TodoController {

    private final List<Todo> todos;
    private final AtomicLong idCounter;

    public TodoController() {
        this.todos = new ArrayList<>();
        this.idCounter = new AtomicLong(1);
        todos.add(new Todo(idCounter.getAndIncrement(), "Learn Javalin"));
        todos.add(new Todo(idCounter.getAndIncrement(), "Build REST API"));
    }

    public void getTodos(@NotNull Context context) {
        String searchText = context.queryParam("search");
        if (searchText != null) {
            var filteredTodos = todos.stream().filter(todo -> todo.description().contains(searchText)).toList();
            context.json(filteredTodos);
        } else {
            context.json(todos);
        }
    }

    public void getTodoById(@NotNull Context context) {
        Long id = Long.parseLong(context.pathParam("id"));
        todos.stream()
                .filter(todo -> todo.id().equals(id))
                .findFirst()
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

        Todo newTodo = new Todo(idCounter.getAndIncrement(), request.description());
        todos.add(newTodo);
        context.status(201).json(newTodo);
    }

    public void updateTodo(@NotNull Context context) {
        Long id = Long.parseLong(context.pathParam("id"));
        String description = context.bodyAsClass(TodoRequest.class).description();

        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).id().equals(id)) {
                Todo updatedTodo = new Todo(id, description);
                todos.set(i, updatedTodo);
                context.json(updatedTodo);
                return;

            }
        }
        context.status(404).result("Todo Not found");
    }

    public void deleteTodo(@NotNull Context context) {
        Long id = Long.parseLong(context.pathParam("id"));
        boolean removed = todos.removeIf(todo -> todo.id().equals(id));

        if (removed) {
            context.status(204);
        } else {
            context.status(404).result("Todo not found");
        }
    }

}

