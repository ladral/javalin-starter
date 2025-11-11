package ch.ladral.controller;

import ch.ladral.model.Todo;
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


    public void getTodos(@NotNull Context ctx) {
        ctx.json(this.todos);
    }
}
