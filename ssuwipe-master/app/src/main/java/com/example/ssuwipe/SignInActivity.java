package com.example.ssuwipe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in);

        Button signInBtn = findViewById(R.id.home_log_in_btn);
        Button signUpBtn = findViewById(R.id.home_sign_up_btn);


        signInBtn.setOnClickListener(view -> signIn());

        signUpBtn.setOnClickListener(view -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void signIn() {
        EditText emailEditText = findViewById(R.id.sign_in_email);
        EditText passwordEditText = findViewById(R.id.sign_in_password);
        CheckBox autoLoginCheckBox = findViewById(R.id.sign_in_auto_login);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        boolean autoLogin = autoLoginCheckBox.isChecked();

        ServerAPI.login(this, email, password, autoLogin);
    }
}
