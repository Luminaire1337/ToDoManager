package io.github.luminaire1337.todomanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.github.luminaire1337.todomanager.todo.Todo;

public class RemoteDatabaseHelper {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public Future<List<Todo>> getTodoListAsync() {
        return executor.submit(() -> {
            try (Connection connection = DriverManager.getConnection(BuildConfig.DATABASE_URL)) {
                try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM todos;")) {
                    try (ResultSet resultSet = statement.executeQuery()) {
                        List<Todo> todoList = new ArrayList<>();
                        while (resultSet.next()) {
                            Todo todo = new Todo();
                            todo.setId(resultSet.getInt("id"));
                            todo.setName(resultSet.getString("title"));
                            todo.setDone(resultSet.getBoolean("is_done"));
                            todoList.add(todo);
                        }
                        return todoList;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Database error occurred.", e);
            }
        });
    }

    public Future<Integer> executeUpdateAsync(String query, Object... params) throws RuntimeException {
        return executor.submit(() -> {
            try (Connection connection = DriverManager.getConnection(BuildConfig.DATABASE_URL)) {
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    for (int i = 0; i < params.length; i++) {
                        statement.setObject(i + 1, params[i]);
                    }
                    return statement.executeUpdate();
                }
            } catch (Exception e) {
                throw new RuntimeException("Database error occurred.", e);
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}
