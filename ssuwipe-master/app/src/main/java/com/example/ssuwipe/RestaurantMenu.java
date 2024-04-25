package com.example.ssuwipe;

import org.json.JSONException;
import org.json.JSONObject;

public class RestaurantMenu {
    private String menu_name;
    private String menu_content;
    private String menu_photo;
    private String menu_price;

    public String getName() { return menu_name; }
    public String getContent() { return menu_content; }
    public String getPhoto() { return menu_photo; }
    public String getPrice() { return menu_price; }

    public void setName(String menu_name) { this.menu_name = menu_name; }
    public void setContent(String menu_content) { this.menu_content = menu_content; }
    public void setPhoto(String menu_photo) { this.menu_photo = menu_photo; }
    public void setPrice(String menu_price) { this.menu_price = menu_price; }

    public RestaurantMenu(JSONObject json) {
        try{
            this.menu_name = json.getString("menu_name");
            this.menu_content = json.getString("menu_content");
            this.menu_photo = json.getString("menu_photo");
            this.menu_price = json.getString("menu_price");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
