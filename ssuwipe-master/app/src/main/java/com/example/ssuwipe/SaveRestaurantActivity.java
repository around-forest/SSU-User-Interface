package com.example.ssuwipe;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SaveRestaurantActivity extends AppCompatActivity {
    ArrayList<Restaurant> restaurantList = ServerAPI.getSaveRestaurantList();
    ArrayList<Restaurant> filteredList = new ArrayList<>();
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_restaurant);

        ViewUtility.initToolbar(findViewById(R.id.save_toolbar), this, "Save", false);

        editText = findViewById(R.id.searchText);

        ListRecyclerAdapter adapter = new ListRecyclerAdapter(SaveRestaurantActivity.this, restaurantList);
        adapter.setOnItemClickListener((v, position) -> {
            Intent intent = new Intent(SaveRestaurantActivity.this, RestaurantInfoActivity.class);
            intent.putExtra("restaurant_id", restaurantList.get(position).getId());
            startActivity(intent);
        });

        RecyclerView recyclerView = findViewById(R.id.save_list_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SaveRestaurantActivity.this);
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
                        if(restaurantList.get(i).getName().contains(searchText))
                            filteredList.add(restaurantList.get(i));
                    }
                }

                ListRecyclerAdapter adapter = new ListRecyclerAdapter(SaveRestaurantActivity.this, filteredList);
                adapter.setOnItemClickListener((v, position) -> {
                    Intent intent = new Intent(SaveRestaurantActivity.this, RestaurantInfoActivity.class);
                    intent.putExtra("restaurant_id", filteredList.get(position).getId());
                    startActivity(intent);
                });

                RecyclerView recyclerView = findViewById(R.id.save_list_recycler);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SaveRestaurantActivity.this);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);
            }
        });
    }
}