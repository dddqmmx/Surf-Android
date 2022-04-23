package com.dd.surf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Spinner;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageButton imageButton = findViewById(R.id.login_button);
        imageButton.setOnClickListener((view)->{
            startActivity(new Intent(this,Main.class));
            finish();
        });
    }
}