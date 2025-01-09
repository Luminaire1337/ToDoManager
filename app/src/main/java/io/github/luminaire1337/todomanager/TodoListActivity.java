package io.github.luminaire1337.todomanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.luminaire1337.todomanager.todo.Todo;
import io.github.luminaire1337.todomanager.todo.TodoAdapter;
import io.github.luminaire1337.todomanager.todo.TodoDatabase;

public class TodoListActivity extends AppCompatActivity {
    private TodoAdapter adapter;

    private void handleLocalDatabase() {
        // Get current view elements
        RecyclerView recyclerView = findViewById(R.id.recycler_todos);
        EditText inputTodo = findViewById(R.id.input_todo);
        Button btnAddTodo = findViewById(R.id.btn_add_todo);

        // Init database
        TodoDatabase database = TodoDatabase.getInstance(this);

        // Init RecyclerView
        List<Todo> todoList = database.todoDao().getAllTodos();
        adapter = new TodoAdapter(this, todoList, new TodoAdapter.TodoActionsListener() {
            @Override
            public void onMarkDone(Todo todo) {
                todo.setDone(!todo.isDone());
                database.todoDao().updateTodo(todo);
                todoList.set(todoList.indexOf(todo), todo);
                adapter.setTodoList(todoList);
            }

            @Override
            public void onDelete(Todo todo) {
                database.todoDao().deleteTodo(todo);
                todoList.remove(todo);
                adapter.setTodoList(todoList);
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
                database.todoDao().insertTodo(todo);
                todoList.add(todo);
                adapter.setTodoList(todoList);

                // Clear input
                inputTodo.setText("");
            }
        });
    }

    private void handleRemoteDatabase() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list);

        TodoDatabaseOptions type = TodoDatabaseOptions.LOCAL;
        if (getIntent().getExtras() != null) {
            String typeString = getIntent().getExtras().getString("type");
            if (typeString != null) {
                type = TodoDatabaseOptions.valueOf(typeString.toUpperCase());
            }
        }

        // Handle the database type
        switch (type) {
            case LOCAL:
                this.handleLocalDatabase();
                break;
            case REMOTE:
                this.handleRemoteDatabase();
                break;
        }
    }
}