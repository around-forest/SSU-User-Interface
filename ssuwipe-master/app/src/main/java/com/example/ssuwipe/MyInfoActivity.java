package com.example.ssuwipe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MyInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        ViewUtility.initToolbar(findViewById(R.id.info_toolbar), this, "Info", true);

        TextView emailText = findViewById(R.id.info_email);
        emailText.setText(ServerAPI.getUserID().replaceAll("__at__", "@").replaceAll("__dot__", "."));

        EditText pwText = findViewById(R.id.info_new_password);
        EditText pwCheckText = findViewById(R.id.info_new_password_check);
        TextView changePasswordBtn = findViewById(R.id.info_change_password_btn);

        changePasswordBtn.setOnClickListener(view -> ServerAPI.updatePassword(this, pwText.getText().toString(), pwCheckText.getText().toString()));

        LinearLayout historyBtn = findViewById(R.id.history_list_btn);
        LinearLayout blockBtn = findViewById(R.id.block_list_btn);
        LinearLayout settingBtn = findViewById(R.id.setting_btn);

        historyBtn.setOnClickListener(view -> Toast.makeText(this, "미구현 기능입니다.", Toast.LENGTH_SHORT).show());
        settingBtn.setOnClickListener(view -> Toast.makeText(this, "미구현 기능입니다.", Toast.LENGTH_SHORT).show());
        blockBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MyInfoActivity.this, RestaurantBanListActivity.class);
            startActivity(intent);
        });

        TextView logoutBtn = findViewById(R.id.info_logout_btn);
        TextView quitBtn = findViewById(R.id.info_quit_btn);

        logoutBtn.setOnClickListener(view -> ServerAPI.logout(this));
        quitBtn.setOnClickListener(view -> ServerAPI.quit(this));
    }
}