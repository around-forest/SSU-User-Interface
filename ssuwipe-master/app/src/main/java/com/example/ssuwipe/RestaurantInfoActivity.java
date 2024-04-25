package com.example.ssuwipe;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RestaurantInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);

        ViewUtility.initToolbar(findViewById(R.id.info_toolbar), this, "Restaurant", false);

        Intent intent = getIntent();
        int restaurant_id = intent.getExtras().getInt("restaurant_id");
        Restaurant restaurant = ServerAPI.getRestaurantById(restaurant_id);

        ViewUtility.setRestaurantInfoLayout(this, findViewById(R.id.restaurant_info_layout), restaurant);
    }
}