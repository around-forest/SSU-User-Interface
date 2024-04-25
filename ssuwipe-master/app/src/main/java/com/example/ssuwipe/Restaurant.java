package com.example.ssuwipe;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Restaurant {
    private Integer id;
    private String location;
    private String name;
    private String photoUrl;
    private Double rating;
    private String type;
    private ArrayList<RestaurantMenu> menu;
    private RestaurantPhoto photo;
    private Double lat, lng;
    private String share;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public ArrayList<RestaurantMenu> getMenu() { return menu; }
    public void setMenu(ArrayList<RestaurantMenu> menu) { this.menu = menu; }
    public RestaurantPhoto getPhoto() { return photo; }
    public void setMenu(RestaurantPhoto photo) { this.photo = photo; }
    public Double getLat(){ return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
    public String getShare() { return share; }
    public void setShare(String share) { this.share = share; }

    @SuppressLint("DefaultLocale")
    public String getRatingString() { return String.format("%.2f", rating); }

    public Restaurant(JSONObject json) {
        try{
            this.id = json.getInt("id");
            this.name = json.getString("name");
            this.location = json.getString("location");
            this.photoUrl = json.getString("photo_url");
            this.rating = json.getDouble("rating");
            this.type = json.getString("type");
            this.share = json.getString("share");

            JSONArray menuInfo = json.getJSONArray("menu");
            this.menu = new ArrayList<>();
            for (int i = 0; i < menuInfo.length(); i++) {
                this.menu.add(new RestaurantMenu((JSONObject) menuInfo.get(i)));
            }
            this.photo = new RestaurantPhoto(json.getJSONArray("photo"));
            this.lat = json.getDouble("lat");
            this.lng = json.getDouble("lng");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
