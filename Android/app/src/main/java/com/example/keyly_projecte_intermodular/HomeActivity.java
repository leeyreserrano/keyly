package com.example.keyly_projecte_intermodular;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.dto.ItemDTO;

import org.json.JSONArray;

import com.example.keyly_projecte_intermodular.resources.ItemAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout layoutError;
    private ItemAdapter itemAdapter;
    private BottomNavigationView menu;
    private String json;
    private ArrayList<Item> items = new ArrayList<>();
    private JSONArray jsonArray;

    /*interface RequestItem {
        // Obtenir tots els items
        @GET("/api/items")
        Call<ArrayList<Item>> getAllItems();

        // Obtenir un item en concret
        //@GET("/api/items/{uuid}")
        //Call<ItemData> getItem(@Path("uuid") String uuid);
    }*/

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

        /*OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .hostnameVerifier((hostname, session) -> true)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://10.147.17.250:8081")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();*/

        actualizarInfo();


        // Carregar el JSON
//        Thread t = new Thread(() -> {
//            json = ItemDTO.carregarJSONItem(this, R.raw.prueba);
//
//            if (json == null || json.equals("[]")) {
//                // Mostrar Toast en el hilo principal
//                runOnUiThread(() -> {
//                    layoutError.setVisibility(View.VISIBLE);
//                    recyclerView.setVisibility(View.GONE);
//                });
//            } else {
//                // Parsear JSON
//                Gson gson = new Gson();
//                Item[] itemsArray = gson.fromJson(json, Item[].class);
//                ArrayList<Item> itemsList = new ArrayList<>(Arrays.asList(itemsArray));
//
//                // Actualizar UI en hilo principal
//                runOnUiThread(() -> {
//                    items.clear();
//                    items.addAll(itemsList);
//                    itemAdapter.notifyDataSetChanged();
//                    recyclerView.setVisibility(RecyclerView.VISIBLE);
//                });
//            }
//        });
//
//        t.start();

        /*try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        menu = findViewById(R.id.menu_app);
        menu.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_items_folders) {
                Intent intent = new Intent(this, ItemFolderSelectorActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_shared) {
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarInfo();
    }

    private void actualizarInfo() {
        itemAdapter = new ItemAdapter(items, item -> {
            Intent intent = new Intent(this, ItemActivity.class);
            intent.putExtra("uuid", item.getUuid().toString());
            intent.putExtra("title", item.getTitol());
            intent.putExtra("url", item.getUrl());
            intent.putExtra("nom_usuari", item.getNomUsuari());
            intent.putExtra("password", item.getContrasenya());
            intent.putExtra("notes", item.getNotes());
            intent.putExtra("fav", item.isFavorit());
            startActivity(intent);
        });
        recyclerView.setAdapter(itemAdapter);

        ItemDTO.RequestItem requestItem = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class);

        // Obtenir un item en concret
        //requestItem.getItem("1").enqueue(new Callback<ItemData>() {
        requestItem.getAllItems().enqueue(new Callback<ArrayList<Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RESPONSE", response.toString());
                    items.clear();
                    items.addAll(response.body());
                    itemAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(RecyclerView.VISIBLE);
                    layoutError.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Item>> call, Throwable t) {
                layoutError.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }
}