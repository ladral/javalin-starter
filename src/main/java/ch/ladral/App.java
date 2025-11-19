package ch.ladral;

import ch.ladral.controller.TodoController;
import ch.ladral.repository.InMemoryTodoRepository;
import io.javalin.Javalin;

public class App {
    public static void main(String[] args) {

        InMemoryTodoRepository repository = new InMemoryTodoRepository();
        TodoController todoController = new TodoController(repository);

        Javalin app = Javalin.create().start(7070);

        app.before( ctx -> {
            String accept = ctx.header("Accept");
            if (accept != null && !accept.contains("application/json") && !accept.contains("*/*")) {
                ctx.status(406).result("Not Acceptable - application/json required");
                ctx.skipRemainingHandlers();
            }
        });


        app.get("/api/todos", todoController::getTodos);
        app.get("/api/todos/{id}", todoController::getTodoById);
        app.post("/api/todos", todoController::createTodo);
        app.put("/api/todos/{id}", todoController::updateTodo);
        app.delete("/api/todos/{id}", todoController::deleteTodo);
    }
}