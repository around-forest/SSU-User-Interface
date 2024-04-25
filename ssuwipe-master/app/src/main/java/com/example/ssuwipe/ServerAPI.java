package com.example.ssuwipe;

import android.app.Activity;
import android.content.Intent;
import android.util.Patterns;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

public class ServerAPI {
    private static final String SERVER_URL = "https://userinterface-4b044-default-rtdb.firebaseio.com";
    private static JSONArray restaurantJson = null; // get only once
    private static JSONObject currentSwipeLog = null; // get only once
    private static JSONObject banList = null, saveList = null;

    private static String userID;
    private static ArrayList<Restaurant> restaurantArrayList = null;
    private static final String emptyUserInfo = "{\"block\":[false],\"log\":[{\"bottom\": 0,\"left\": 0,\"right\": 0}],\"save\":[false]}";

    // Auth
    public static String getUserID(){ return userID; }
    public static void setUserID(String userID){
        if(userID == null) ServerAPI.userID = null;
        else ServerAPI.userID = userID.replaceAll("@", "__at__").replaceAll("\\.", "__dot__");
    }
    public static void login(Activity activity, String email, String password, boolean autoLogin) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(activity, "비어 있으면 안 됨", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity, task -> {
            if(task.isSuccessful()) {
                setUserID(email);
                Intent intent = new Intent(activity, HomeActivity.class);
                Toast.makeText(activity, "로그인 성공", Toast.LENGTH_SHORT).show();
                if (autoLogin) SaveSharedPreference.setLoginInfo(activity, email, password);
                activity.startActivity(intent);
                activity.finish();
            }
            else{
                Toast.makeText(activity, "로그인 실패", Toast.LENGTH_SHORT).show();
                SaveSharedPreference.clearPreferences(activity.getApplicationContext());
                Intent intent = new Intent(activity, SignInActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }
    public static void logout(Activity activity) {
        setUserID(null);
        currentSwipeLog = null;
        banList = null;
        saveList = null;
        FirebaseAuth.getInstance().signOut();
        SaveSharedPreference.clearPreferences(activity.getApplicationContext());
        Intent intent = new Intent(activity.getApplicationContext(), SignInActivity.class);
        Toast.makeText(activity, "로그아웃", Toast.LENGTH_SHORT).show();
        HomeActivity hm = (HomeActivity)HomeActivity._Home_Activity;
        hm.finish();
        activity.startActivity(intent);
        activity.finish();
    }
    public static void newUserData(String email) {
        Thread thread = new Thread(){
            public void run(){
                try {
                    JSONObject data = new JSONObject();
                    String key = email.replaceAll("@", "__at__").replaceAll("\\.", "__dot__");
                    data.put(key, new JSONObject(emptyUserInfo));
                    HttpUtility.sendRequest(SERVER_URL+"/api/user.json", data.toString(), "PATCH");
                } catch (JSONException e) { e.printStackTrace(); }
            }
        };
        thread.start();
    }
    public static void register(Activity activity, String email, String password, String passwordCheck) {
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(activity, "비어 있으면 안 됨", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(passwordCheck)) {
            Toast.makeText(activity, "비밀번호가 일치하지 않음", Toast.LENGTH_SHORT).show();
            return;
        }
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        if(!emailPattern.matcher(email).matches()) {
            Toast.makeText(activity, "올바르지 않은 이메일", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) {
                newUserData(email);
                Toast.makeText(activity, "가입 완료", Toast.LENGTH_SHORT).show();
                activity.finish();
            }
            else {
                Toast.makeText(activity, task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void updatePassword(Activity activity, String password, String passwordCheck) {
        if(password == null || password.isEmpty() || !password.equals(passwordCheck)) {
            Toast.makeText(activity, "비밀번호가 비어있거나 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseAuth.getInstance().getCurrentUser().updatePassword(password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) Toast.makeText(activity, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
            else Toast.makeText(activity, task.getException().toString(), Toast.LENGTH_SHORT).show();
        });
    }
    public static void quit(Activity activity) {
        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Toast.makeText(activity, "탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(activity, SignInActivity.class);
                activity.startActivity(intent);
                HomeActivity._Home_Activity.finish();
                activity.finish();
            }
            else {
                Toast.makeText(activity, task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // load data
    private static void getRestaurantLocation(int index) {
        try {
            Thread thread = new Thread() {
                public void run() {
                    try {
                        JSONObject restaurant = restaurantJson.getJSONObject(index);
                        String url = "https://maps.google.com/maps/api/geocode/json?key=AIzaSyBNJvDUvkkzoSFiIBD8WOY-BjlSXkDZukM&address=" + restaurant.getString("location");
                        JSONObject json = new JSONObject(HttpUtility.sendRequest(url, "", "GET"));
                        JSONObject result = json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                        restaurant.put("lat", result.getDouble("lat"));
                        restaurant.put("lng", result.getDouble("lng"));
                        restaurantJson.put(index, restaurant);

                        JSONObject data = new JSONObject();
                        data.put(String.valueOf(index), restaurant);
                        HttpUtility.sendRequest(SERVER_URL + "/api/view/restaurant.json", data.toString(), "PATCH");
                    } catch (JSONException e) { e.printStackTrace(); }
                }
            };
            thread.start();
            thread.join();
        } catch (InterruptedException e) { e.printStackTrace(); }
    }
    private static void getRestaurantInfoFromServer() {
        try{
            if(restaurantJson == null){
                Thread thread = new Thread(){
                    public void run(){
                        try {
                            restaurantJson = new JSONArray(HttpUtility.sendRequest(SERVER_URL + "/api/view/restaurant.json", "", "GET"));
                        } catch (JSONException e) { e.printStackTrace(); }
                    }
                };
                thread.start();
                thread.join();
                restaurantArrayList = new ArrayList<>();
                for(int i=0; i<restaurantJson.length(); i++) {
                    if(!restaurantJson.getJSONObject(i).has("lat")) getRestaurantLocation(i);
                    restaurantArrayList.add(new Restaurant((JSONObject) restaurantJson.get(i)));
                }
            }
        }
        catch (InterruptedException | JSONException e){ e.printStackTrace(); }
    }
    public static void preLoadData() {
        if(restaurantJson == null) getRestaurantInfoFromServer();
    }

    private static JSONObject JSONArrayToObject(JSONArray array) {
        JSONObject result = new JSONObject();
        for(int i=0; i<array.length(); i++) {
            try {
                if(array.getString(i).equals("null")) result.put(String.valueOf(i), false);
                else result.put(String.valueOf(i), array.get(i));
            } catch (JSONException e) { e.printStackTrace(); }
        }
        return result;
    }
    private static JSONObject makeObject(String string) {
        try {
            if(string == null || string.isEmpty()) return new JSONObject();
            if(string.charAt(0) == '{') return new JSONObject(string);
            else if(string.charAt(0) == '[') return JSONArrayToObject(new JSONArray(string));
            else return new JSONObject();
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }
    private static void getUserDataFromServer() {
        try {
            if(currentSwipeLog == null) {
                Thread thread = new Thread(){
                    public void run(){
                        currentSwipeLog = makeObject(HttpUtility.sendRequest(SERVER_URL+"/api/user/"+userID+"/log.json","", "GET"));
                        banList = makeObject(HttpUtility.sendRequest(SERVER_URL+"/api/user/"+userID+"/block.json","", "GET"));
                        saveList = makeObject(HttpUtility.sendRequest(SERVER_URL+"/api/user/"+userID+"/save.json","", "GET"));
                    }
                };
                thread.start();
                thread.join();
            }
        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    // public method
    public static ArrayList<Restaurant> getAllRestaurantList() {
        preLoadData();
        return restaurantArrayList;
    }

    public static Restaurant getRestaurantById(int id) {
        preLoadData();
        return restaurantArrayList.get(id);
    }

    public static ArrayList<Restaurant> getShowRestaurantList() {
        preLoadData();
        getUserDataFromServer();
        ArrayList<Restaurant> list = new ArrayList<>();
        for(int i=0; i<restaurantArrayList.size(); i++) {
            try {
                if(!banList.has(String.valueOf(i))) list.add(restaurantArrayList.get(i));
                else if(!banList.getBoolean(String.valueOf(i))) list.add(restaurantArrayList.get(i));
            } catch (JSONException e) { e.printStackTrace(); }
        }
        return list;
    }

    public static ArrayList<Restaurant> getRestaurantBySuggestionPolicy() {
        ArrayList<Restaurant> list = getShowRestaurantList();
        Collections.sort(list, (r1, r2) -> {
            int[] log1 = getSwipeLog(r1.getId()), log2 = getSwipeLog(r2.getId());
            double v1 = 1.0 * (log1[1] + 1) / (log1[0] + log1[1] + 2);
            double v2 = 1.0 * (log2[1] + 1) / (log2[0] + log2[1] + 2);
            return Double.compare(v2, v1);
        });
        for(int i=1; i<list.size(); i++) {
            if(Math.random() < 0.3) {
                Restaurant tmp = list.get(i-1);
                list.add(i-1, list.get(i));
                list.add(i, tmp);
            }
        }
        return list;
    }

    public static ArrayList<Restaurant> getSaveRestaurantList() {
        preLoadData();
        getUserDataFromServer();
        ArrayList<Restaurant> list = new ArrayList<>();
        for(int i=0; i<restaurantArrayList.size(); i++) {
            try {
                if(saveList.has(String.valueOf(i)) && saveList.getBoolean(String.valueOf(i))) list.add(restaurantArrayList.get(i));
            } catch (JSONException e) { e.printStackTrace(); }
        }
        return list;
    }

    public static ArrayList<Restaurant> getBlockRestaurantList() {
        preLoadData();
        getUserDataFromServer();
        ArrayList<Restaurant> list = new ArrayList<>();
        for(int i=0; i<restaurantArrayList.size(); i++) {
            try {
                if(banList.has(String.valueOf(i)) && banList.getBoolean(String.valueOf(i))) list.add(restaurantArrayList.get(i));
            } catch (JSONException e) { e.printStackTrace(); }
        }
        return list;
    }

    public static void setBan(int restaurantID, boolean ban) {
        getUserDataFromServer();
        try {
            String id = String.valueOf(restaurantID);
            banList.put(id, ban);
            JSONObject data = new JSONObject();
            data.put(id, banList.getBoolean(id));
            Thread thread = new Thread(){
                public void run(){
                    HttpUtility.sendRequest(SERVER_URL+"/api/user/"+userID+"/block.json", data.toString(), "PATCH");
                }
            };
            thread.start();
            thread.join();
        } catch (JSONException | InterruptedException e) { e.printStackTrace(); }
    }

    public static boolean getBan(int restaurantID) {
        getUserDataFromServer();
        try {
            String id = String.valueOf(restaurantID);
            if(banList.has(id)) return banList.getBoolean(id);
            else return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setSave(int restaurantID, boolean save) {
        getUserDataFromServer();
        try {
            saveList.put(String.valueOf(restaurantID), save);
            String id = String.valueOf(restaurantID);
            saveList.put(id, save);
            JSONObject data = new JSONObject();
            data.put(id, saveList.getBoolean(id));
            Thread thread = new Thread(){
                public void run(){
                    HttpUtility.sendRequest(SERVER_URL+"/api/user/"+userID+"/save.json", data.toString(), "PATCH");
                }
            };
            thread.start();
            thread.join();
        } catch (JSONException | InterruptedException e) { e.printStackTrace(); }
    }

    public static boolean getSave(int restaurantID) {
        getUserDataFromServer();
        try {
            String id = String.valueOf(restaurantID);
            if(saveList.has(id)) return saveList.getBoolean(id);
            else return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int[] getSwipeLog(Integer restaurantID) {
        String id = String.valueOf(restaurantID);
        int[] res = new int[2];
        try {
            if(currentSwipeLog != null && currentSwipeLog.has(id)) {
                res[0] = currentSwipeLog.getJSONObject(id).getInt("left");
                res[1] = currentSwipeLog.getJSONObject(id).getInt("right");
            }
        }  catch (JSONException e) { e.printStackTrace(); }
        return res;
    }

    public static void sendSwipeLog(Integer restaurantID, String direction) {
        getUserDataFromServer();
        try {
            String id = String.valueOf(restaurantID);
            JSONObject swipeLog = currentSwipeLog;
            if(!swipeLog.has(id) || !(swipeLog.get(id) instanceof JSONObject)) {
                JSONObject emptyLog = new JSONObject();
                emptyLog.put("left", 0);
                emptyLog.put("right", 0);
                emptyLog.put("bottom", 0);
                swipeLog.put(id, emptyLog);
            }

            JSONObject restaurantLog = (JSONObject) swipeLog.get(id);
            if (direction.compareToIgnoreCase("left") == 0) {
                restaurantLog.put("left", restaurantLog.getInt("left") + 1);
            }
            if (direction.compareToIgnoreCase("right") == 0) {
                restaurantLog.put("right", restaurantLog.getInt("right") + 1);
            }
            if (direction.compareToIgnoreCase("bottom") == 0) {
                restaurantLog.put("bottom", restaurantLog.getInt("right") + 1);
            }

            swipeLog.put(id, restaurantLog);
            JSONObject data = new JSONObject();
            data.put(id, restaurantLog);
            currentSwipeLog = swipeLog;

            Thread thread = new Thread(){
                public void run(){
                    HttpUtility.sendRequest(SERVER_URL+"/api/user/"+userID+"/log.json", data.toString(), "PATCH");
                }
            };
            thread.start();

        } catch (JSONException e) { e.printStackTrace(); }
    }
}