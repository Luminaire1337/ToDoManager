package io.github.luminaire1337.todomanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle main menu buttons
        // Local database, button id @+id/btn_local
        findViewById(R.id.btn_local).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TodoListActivity.class);
            intent.putExtra("type", "local");
            startActivity(intent);
        });
        // Remote database, button id @+id/btn_remote
        findViewById(R.id.btn_remote).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TodoListActivity.class);
            intent.putExtra("type", "remote");
            startActivity(intent);
        });
    }
}
