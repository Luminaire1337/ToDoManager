package io.github.luminaire1337.todomanager.todo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.luminaire1337.todomanager.R;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private final Context context;
    private final TodoActionsListener listener;
    private List<Todo> todoList;

    public TodoAdapter(Context context, List<Todo> todoList, TodoActionsListener listener) {
        this.context = context;
        this.todoList = todoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_list_item, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = todoList.get(position);
        holder.title.setText(todo.getName());

        // Update style if marked as done
        if (todo.isDone()) {
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.title.setAlpha(0.6f); // Dim the text
        } else {
            holder.title.setPaintFlags(0);
            holder.title.setAlpha(1.0f);
        }

        // Set click listeners for buttons
        holder.btnMarkDone.setOnClickListener(v -> listener.onMarkDone(todo));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(todo));
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTodoList(List<Todo> newTodoList) {
        this.todoList = newTodoList;
        notifyDataSetChanged();
    }

    public interface TodoActionsListener {
        void onMarkDone(Todo todo);

        void onDelete(Todo todo);
    }

    static class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageButton btnMarkDone, btnDelete;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.todo_title);
            btnMarkDone = itemView.findViewById(R.id.btn_mark_done);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}

