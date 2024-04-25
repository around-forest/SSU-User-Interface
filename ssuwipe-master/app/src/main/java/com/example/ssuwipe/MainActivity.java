package com.example.ssuwipe;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.splashscreen.SplashScreen;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // load image while splash image
        SplashScreen.installSplashScreen(this);
        ServerAPI.preLoadData();

        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);

        // support warm start
        ServerAPI.preLoadData();

        if (!Objects.requireNonNull(SaveSharedPreference.getLoginInfo(this).get("email")).isEmpty()) {
            // load auto login data
            Map<String, String> loginInfo = SaveSharedPreference.getLoginInfo(this);
            String email    = loginInfo.get("email");
            String password = loginInfo.get("password");
            if(email == null || email.isEmpty() || password == null || password.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                SaveSharedPreference.setLoginInfo(this, email, password);
                ServerAPI.setUserID(email);
                ServerAPI.login(this, email, password, true);
            }
        }
        else {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }

    }

}