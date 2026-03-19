package com.example.keyly_projecte_intermodular.DTO;

import android.content.Context;
import android.util.Log;

import com.example.keyly_projecte_intermodular.DAO.Item;
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

        try {
            URL url = new URL("http://10.147.17.250:8081/api/items");
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
