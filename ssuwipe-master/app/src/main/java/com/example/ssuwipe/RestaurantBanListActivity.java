package com.example.ssuwipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;

public class RestaurantBanListActivity extends AppCompatActivity {

    ArrayList<Restaurant> restaurantList = ServerAPI.getBlockRestaurantList();
    ArrayList<Restaurant> filteredList = new ArrayList<>();
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_ban_list);

        editText = findViewById(R.id.searchText);

        ViewUtility.initToolbar(findViewById(R.id.ban_toolbar), this, "Block", false);

        ListRecyclerAdapter adapter = new ListRecyclerAdapter(this, restaurantList);
        adapter.setOnItemClickListener((v, position) -> {
            Intent intent = new Intent(RestaurantBanListActivity.this, RestaurantInfoActivity.class);
            intent.putExtra("restaurant_id", restaurantList.get(position).getId());
            startActivity(intent);
        });

        RecyclerView recyclerView = findViewById(R.id.ban_list_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editText.getText().toString();
                filteredList.clear();

                if(searchText.equals(""))   filteredList = restaurantList;
                else {
                    for(int i = 0; i < restaurantList.size(); i++){
                        if(restaurantList.get(i).getName().contains(searchText)){
                            filteredList.add(restaurantList.get(i));
                        }
                    }
                }

                ListRecyclerAdapter adapter = new ListRecyclerAdapter(RestaurantBanListActivity.this, filteredList);
                adapter.setOnItemClickListener((v, position) -> {
                    Intent intent = new Intent(RestaurantBanListActivity.this, RestaurantInfoActivity.class);
                    intent.putExtra("restaurant_id", restaurantList.get(position).getId());
                    startActivity(intent);
                });

                RecyclerView recyclerView = findViewById(R.id.ban_list_recycler);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RestaurantBanListActivity.this);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);
            }
        });

    }
}