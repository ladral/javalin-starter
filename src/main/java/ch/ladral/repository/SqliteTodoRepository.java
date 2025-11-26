package ch.ladral.repository;

import ch.ladral.model.exceptions.NotFoundException;
import ch.ladral.model.todo.Todo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteTodoRepository implements TodoRepository {


    private static final String URL = "jdbc:sqlite:src/main/resources/db/todos.sqlite";


    public SqliteTodoRepository() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String filePath = URL.replace("jdbc:sqlite:", "");
        java.io.File dbFile = new java.io.File(filePath);
        if (dbFile.getParentFile() != null) {
            dbFile.getParentFile().mkdirs();
        }

        String sql = "CREATE TABLE IF NOT EXISTS todos (" +
                "id INTEGER PRIMARY KEY, " +
                "description TEXT" +
                ");";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    @Override
    public List<Todo> findAll() {
        String sql = "SELECT id, description FROM todos";
        List<Todo> todos = new ArrayList<>();

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             var resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                todos.add(new Todo(
                        resultSet.getLong("id"),
                        resultSet.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return todos;
    }

    @Override
    public List<Todo> findByDescription(String description) {
        String sql = "SELECT id, description FROM todos WHERE description LIKE ?";
        List<Todo> todos = new ArrayList<>();

        try (Connection connection = getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, "%" + description + "%");
            var resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                todos.add(new Todo(
                        resultSet.getLong("id"),
                        resultSet.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return todos;
    }

    @Override
    public Optional<Todo> findById(Long id) {
        String sql = "SELECT id, description FROM todos WHERE id = ?";

        try (Connection connection = getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(new Todo(
                        resultSet.getLong("id"),
                        resultSet.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Todo save(String description) {
        String sql = "INSERT INTO todos(description) VALUES(?)";

        try (Connection connection = getConnection();
             var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, description);
            preparedStatement.executeUpdate();

            try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getLong(1);
                    return new Todo(id, description);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }


    @Override
    public Todo update(Long id, String description) throws NotFoundException {
        String sql = "UPDATE todos SET description = ? WHERE id = ?";

        try (Connection connection = getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, description);
            preparedStatement.setLong(2, id);
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new NotFoundException();
            }

            return new Todo(id, description);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM todos WHERE id = ?";

        try (Connection conn = getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
