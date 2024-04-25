package com.example.ssuwipe;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class RestaurantPhoto {
    private ArrayList<String> urlList;

    public String get(int index) { return urlList.get(index % urlList.size()); }

    public RestaurantPhoto(JSONArray list) {
        try{
            urlList = new ArrayList<>();
            for(int i = 0; i < list.length(); i++) {
                urlList.add(list.getString(i));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
