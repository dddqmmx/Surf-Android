package com.dd.surf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DeveloperOption extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_option);

        findViewById(R.id.toMainActivityButton).setOnClickListener(v -> {
            startActivity(new Intent(this, Main.class));
        });
    }
}