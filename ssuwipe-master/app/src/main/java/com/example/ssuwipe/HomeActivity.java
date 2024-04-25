package com.example.ssuwipe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static Activity _Home_Activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        _Home_Activity = HomeActivity.this;

        ServerAPI.preLoadData();

        ImageView myInfoView = findViewById(R.id.home_my_info_show_btn);
        myInfoView.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, MyInfoActivity.class);
            startActivity(intent);
        });


        Button swipeBtn = findViewById(R.id.home_swipe_btn);
        swipeBtn.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, SsuwipeActivity.class);
            startActivity(intent);
        });

        Button listShowBtn = findViewById(R.id.home_list_show_btn);
        listShowBtn.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, RestaurantListActivity.class);
            startActivity(intent);
        });

        Button mapBtn = findViewById(R.id.home_map_show_btn);
        mapBtn.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, MapActivity.class);
            startActivity(intent);
        });

        Button saveShowBtn = findViewById(R.id.home_save_show_btn);
        saveShowBtn.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, SaveRestaurantActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        if(ServerAPI.getUserID() == null) finish();

    }
}