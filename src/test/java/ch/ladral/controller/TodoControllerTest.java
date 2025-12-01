package ch.ladral.controller;


import ch.ladral.model.todo.Todo;
import ch.ladral.repository.InMemoryTodoRepository;
import io.javalin.http.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class TodoControllerTest {
    private final Context ctx = mock(Context.class);
    private final InMemoryTodoRepository repository = mock(InMemoryTodoRepository.class);
    TodoController controller = new TodoController(repository);


    @BeforeEach
    void setUp() {
        reset(ctx);
        reset(repository);
    }

    @Test
    void getTodos_emptyTodoRepository_returnsEmptyList() {
        // arrange
        when(repository.findAll()).thenReturn(List.of());

        // act
        controller.getTodos(ctx);

        // assert
        verify(ctx).json(List.of());
        verify(repository, times(1)).findAll();
    }


    @Test
    void getTodos_nonEmptyTodoRepository_returnsTodos() {
        // arrange
        Todo expectedTodo = new Todo(1L, "Learn Javalin");
        when(repository.findAll()).thenReturn(List.of(expectedTodo));

        // act
        controller.getTodos(ctx);

        // assert
        verify(ctx).json(List.of(expectedTodo));
        verify(repository, times(1)).findAll();
    }


    @Test
    void getTodos_givenSearchText_searchesForTodosContainingSearchText() {
        // arrange
        final String expectedSearchText = "javalin";
        when(ctx.queryParam("search")).thenReturn(expectedSearchText);

        // act
        controller.getTodos(ctx);

        // assert
        verify(repository, times(1)).findByDescription(expectedSearchText);
    }
}