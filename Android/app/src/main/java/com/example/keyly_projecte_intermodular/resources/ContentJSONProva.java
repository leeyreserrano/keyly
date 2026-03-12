package com.example.keyly_projecte_intermodular.resources;

import android.content.Context;
import android.util.Log;

import com.example.keyly_projecte_intermodular.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ContentJSONProva {

    public static String carregarJSON (Context context, int nomArxiu) {

        String json = "[]";
        String urlString = "http://172.21.130.207:8080/api/items";

        try {
            /*InputStream is = context.getResources().openRawResource(nomArxiu);
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");*/


            //URL url = new URL("http://172.21.130.207:8080/api/items");
            URL url = new URL("http://10.147.17.250:8082/api/items");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStream isP = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(isP));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            json = sb.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.d("Error", ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Error", e.getMessage());
        }

        return json;

    }

    public static Item[] getItems(String json) {
        Gson gson = new Gson();
        Item[] itemList = gson.fromJson(json, Item[].class);
        return itemList;
    }
}
