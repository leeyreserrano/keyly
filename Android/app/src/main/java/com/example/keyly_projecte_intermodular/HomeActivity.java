package com.example.keyly_projecte_intermodular;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.resources.ContentJSONProva;
import com.example.keyly_projecte_intermodular.resources.Item;
import com.example.keyly_projecte_intermodular.resources.ItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout layoutError;
    private ItemAdapter itemAdapter;
    private String json;
    private ArrayList<Item> items = new ArrayList<>();
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        layoutError = findViewById(R.id.layoutError);


        // Carregar el JSON
        Thread t = new Thread(() -> {
            json = ContentJSONProva.carregarJSON(this, R.raw.prueba);

            if (json == null || json.equals("[]")) {
                // Mostrar Toast en el hilo principal
                runOnUiThread(() -> {
                    layoutError.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                });
            } else {
                // Parsear JSON
                Gson gson = new Gson();
                Item[] itemsArray = gson.fromJson(json, Item[].class);
                ArrayList<Item> itemsList = new ArrayList<>(Arrays.asList(itemsArray));

                // Actualizar UI en hilo principal
                runOnUiThread(() -> {
                    items.clear();
                    items.addAll(itemsList);
                    itemAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(RecyclerView.VISIBLE);
                });
            }
        });

        t.start();

        /*try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        itemAdapter = new ItemAdapter(items, item -> {
            Intent intent = new Intent(this, ItemActivity.class);
            intent.putExtra("title", item.getTitle());
            startActivity(intent);
        });
        recyclerView.setAdapter(itemAdapter);


    }
}