package com.example.ssuwipe;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpUtility {
    /**
     * http request
     * @param urlString request url
     * @param parameter request parameter, expect query string(GET) or json string(POST, PUT)
     * @param method "GET", "POST", "PUT", ...
     * @return return response as string
     */
    public static String sendRequest(String urlString, String parameter, String method){
        Log.d("http-request", "[" + method + "] " + urlString + "?" + (parameter != null ? parameter : ""));
        if(method.equals("GET") && parameter != null) urlString += "?" + parameter;
        try{
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Accept", "application/json");

            if(!method.equals("GET")){
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStream os = conn.getOutputStream();
                os.write(parameter.getBytes(StandardCharsets.UTF_8));
                os.close();
            }

            conn.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) sb.append(line).append("\n");
            Log.d("http-response", sb.toString());
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("http-response", "{{ empty string }}");
            return "";
        }
    }
}
