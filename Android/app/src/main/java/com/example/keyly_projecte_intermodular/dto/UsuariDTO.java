package com.example.keyly_projecte_intermodular.dto;

import android.content.Context;
import android.util.Log;

import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UsuariDTO {

    public static String carregarJSONUsuari (Context context, int nomArxiu) {

        String json = "[]";

        try {
            URL url = new URL("https://10.147.17.250:8081/api/login/id/1");
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

    public static Usuari[] getUsuaris(String json) {
        Gson gson = new Gson();
        Usuari[] usuarisLlista = gson.fromJson(json, Usuari[].class);
        return usuarisLlista;
    }
}
