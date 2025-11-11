package ch.ladral;

import ch.ladral.controller.TodoController;
import io.javalin.Javalin;

public class App {
    public static void main(String[] args) {
        var app = Javalin.create(/*config*/);

        app.get("/api/todos", ctx -> {
            TodoController todoController = new TodoController();

            todoController.getTodos(ctx);
        });

        app.start(7070);
    }
}