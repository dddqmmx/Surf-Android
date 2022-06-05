package com.dd.surf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.dd.surf.util.Control;
import com.dd.surf.view.util.Out;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Control control = (Control) getApplication();

        EditText userName = findViewById(R.id.user_name);
        EditText password = findViewById(R.id.password);

        ImageButton imageButton = findViewById(R.id.login_button);
        imageButton.setOnClickListener((view)->{
            String userNameText = userName.getText().toString();
            String passwordText = password.getText().toString();
            if (control.login(userNameText,passwordText)) {
                control.setUserName(userNameText);
                control.setPassword(passwordText);
                control.setId(control.getUserId(userNameText,passwordText));
                startActivity(new Intent(this,Main.class));
                finish();
            } else {
                Out.print(this,"登录失败");
            }
        });
    }
}