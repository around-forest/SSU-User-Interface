package com.example.ssuwipe;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        Button signUpBtn = findViewById(R.id.home_sign_up_btn);
        signUpBtn.setOnClickListener(view -> signUp());

        Button goBackBtn = findViewById(R.id.home_goback_btn);
        goBackBtn.setOnClickListener(view -> finish());
    }

    private void signUp() {
        EditText emailEditText = findViewById(R.id.sign_up_email);
        EditText passwordEditText = findViewById(R.id.sign_up_password);
        EditText passwordCheckEditText = findViewById(R.id.sign_up_password_check);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordCheck = passwordCheckEditText.getText().toString();

        ServerAPI.register(this, email, password, passwordCheck);
    }
}
