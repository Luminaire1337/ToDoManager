package io.github.luminaire1337.todomanager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.luminaire1337.todomanager.todo.Todo;
import io.github.luminaire1337.todomanager.todo.TodoAdapter;
import io.github.luminaire1337.todomanager.todo.TodoDatabase;

public class TodoListActivity extends AppCompatActivity {
    private TodoDatabaseOptions databaseType = TodoDatabaseOptions.LOCAL;
    private RemoteDatabaseHelper remoteDatabaseHelper;
    private TodoAdapter adapter;

    private List<Todo> getTodoList() {
        if (databaseType == TodoDatabaseOptions.REMOTE) {
            try {
                return remoteDatabaseHelper.getTodoListAsync().get();
            } catch (Exception e) {
                Log.e("TodoListActivity", "Failed to get todos from remote database: " + e.getMessage() + " Type of exception: " + e.getClass());
                return new ArrayList<>();
            }
        }

        return TodoDatabase.getInstance(this).todoDao().getAllTodos();
    }

    private void updateTodo(Todo todo) {
        if (databaseType == TodoDatabaseOptions.REMOTE) {
            try {
                remoteDatabaseHelper.executeUpdateAsync("UPDATE todos SET title = ?, is_done = ? WHERE id = ?;",
                        todo.getName(), todo.isDone(), todo.getId()).get();
            } catch (Exception e) {
                Log.e("TodoListActivity", "Failed to update remote database: " + e.getMessage() + " Type of exception: " + e.getClass());
            }

            return;
        }

        TodoDatabase.getInstance(this).todoDao().updateTodo(todo);
    }

    private void deleteTodo(Todo todo) {
        if (databaseType == TodoDatabaseOptions.REMOTE) {
            try {
                remoteDatabaseHelper.executeUpdateAsync("DELETE FROM todos WHERE id = ?;", todo.getId()).get();
            } catch (Exception e) {
                Log.e("TodoListActivity", "Failed to delete from remote database: " + e.getMessage() + " Type of exception: " + e.getClass());
            }

            return;
        }

        TodoDatabase.getInstance(this).todoDao().deleteTodo(todo);
    }

    private void insertTodo(Todo todo) {
        if (databaseType == TodoDatabaseOptions.REMOTE) {
            try {
                remoteDatabaseHelper.executeUpdateAsync("INSERT INTO todos (title) VALUES (?);", todo.getName()).get();
            } catch (Exception e) {
                Log.e("TodoListActivity", "Failed to insert into remote database: " + e.getMessage() + " Type of exception: " + e.getClass());
            }

            return;
        }

        TodoDatabase.getInstance(this).todoDao().insertTodo(todo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list);

        if (getIntent().getExtras() != null) {
            String typeString = getIntent().getExtras().getString("type");
            if (typeString != null) {
                databaseType = TodoDatabaseOptions.valueOf(typeString.toUpperCase());
            }
        }

        if (databaseType == TodoDatabaseOptions.REMOTE) {
            remoteDatabaseHelper = new RemoteDatabaseHelper();

            try {
                remoteDatabaseHelper.executeUpdateAsync("""
                        CREATE TABLE IF NOT EXISTS todos (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            title VARCHAR(255) NOT NULL,
                            is_done BOOLEAN DEFAULT FALSE
                        );
                        """).get();
            } catch (Exception e) {
                Log.e("TodoListActivity", "Failed to create remote database table: " + e.getMessage() + " Type of exception: " + e.getClass());
                // Create a toast to inform the user
                Toast.makeText(this, "Failed to connect to remote database!", Toast.LENGTH_SHORT).show();
                // Go back to main activity
                finish();
            }
        }

        // Get current view elements
        RecyclerView recyclerView = findViewById(R.id.recycler_todos);
        EditText inputTodo = findViewById(R.id.input_todo);
        Button btnAddTodo = findViewById(R.id.btn_add_todo);

        // Init RecyclerView
        List<Todo> todoList = getTodoList();
        adapter = new TodoAdapter(this, todoList, new TodoAdapter.TodoActionsListener() {
            @Override
            public void onMarkDone(Todo todo) {
                todo.setDone(!todo.isDone());
                updateTodo(todo);
                todoList.set(todoList.indexOf(todo), todo);
                adapter.setTodoList(todoList);

                // Create a Toast
                Toast.makeText(TodoListActivity.this, "Todo updated!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(Todo todo) {
                deleteTodo(todo);
                todoList.remove(todo);
                adapter.setTodoList(todoList);

                // Create a Toast
                Toast.makeText(TodoListActivity.this, "Todo deleted!", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Set click listeners
        btnAddTodo.setOnClickListener(v -> {
            String todoName = inputTodo.getText().toString();
            if (!todoName.isEmpty()) {
                Todo todo = new Todo();
                todo.setName(todoName);
                insertTodo(todo);
                todoList.add(todo);
                adapter.setTodoList(todoList);

                // Clear input
                inputTodo.setText("");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseType == TodoDatabaseOptions.REMOTE) {
            remoteDatabaseHelper.shutdown();
        }
    }
}