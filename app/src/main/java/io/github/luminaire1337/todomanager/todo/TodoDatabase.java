package io.github.luminaire1337.todomanager.todo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Todo.class}, version = 1)
public abstract class TodoDatabase extends RoomDatabase {
    private static TodoDatabase instance;

    public static TodoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            TodoDatabase.class, "todo-db")
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract TodoDao todoDao();
}
