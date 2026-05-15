package com.example.keyly_projecte_intermodular;

import static com.example.keyly_projecte_intermodular.utils.LogOutService.logOut;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.dao.Carpeta;
import com.example.keyly_projecte_intermodular.dao.Compartit;
import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.dto.CarpetaDTO;
import com.example.keyly_projecte_intermodular.dto.CompartitDTO;
import com.example.keyly_projecte_intermodular.dto.ItemDTO;

import com.example.keyly_projecte_intermodular.adapters.CarpetaAdapter;
import com.example.keyly_projecte_intermodular.adapters.CompartitAdapter;
import com.example.keyly_projecte_intermodular.adapters.ItemAdapter;
import com.example.keyly_projecte_intermodular.utils.TipusEntitat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private View includeLogsItems, includeLogsCarpetes, includeLogsCompartits;
    private RecyclerView recyclerViewItems, recyclerViewCarpetes, recyclerViewCompartits;
    private LinearLayout layoutErrorItems, layoutErrorCarpetes, layoutErrorCompartits;
    private TextView txtTitolErrorItems, txtDescripcioErrorItems,
            txtTitolErrorCarpetes, txtDescripcioErrorCarpetes,
            txtTitolErrorCompartits, txtDescripcioErrorCompartits;
    private ImageView imgVErrorItems, imgVErrorCarpetes, imgVErrorCompartits;
    private ItemAdapter itemAdapter;
    private CarpetaAdapter carpetaAdapter;
    private CompartitAdapter compartitAdapter;
    private ImageButton imgBtnLogOut;
    private BottomNavigationView menu;
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Carpeta> carpetes = new ArrayList<>();
    private ArrayList<Compartit> compartits = new ArrayList<>();
    private final int TIPUS_ITEM = 1, TIPUS_CARPETA = 2, TIPUS_COMPARTIT = 3;


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

        imgBtnLogOut = findViewById(R.id.imgBtnLogOut);
        imgBtnLogOut.setOnClickListener(v -> {
            logOut(this);
        });

        // Items
        includeLogsItems = findViewById(R.id.includeLogsItems);
        layoutErrorItems = (LinearLayout) includeLogsItems;
        txtTitolErrorItems = includeLogsItems.findViewById(R.id.txtTitolError);
        txtDescripcioErrorItems = includeLogsItems.findViewById(R.id.txtDescripcioError);
        imgVErrorItems = includeLogsItems.findViewById(R.id.imgVError);
        recyclerViewItems = findViewById(R.id.recyclerItems);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));

// Carpetes
        includeLogsCarpetes = findViewById(R.id.includeLogsCarpetes);
        layoutErrorCarpetes = (LinearLayout) includeLogsCarpetes;
        txtTitolErrorCarpetes = includeLogsCarpetes.findViewById(R.id.txtTitolError);
        txtDescripcioErrorCarpetes = includeLogsCarpetes.findViewById(R.id.txtDescripcioError);
        imgVErrorCarpetes = includeLogsCarpetes.findViewById(R.id.imgVError);
        recyclerViewCarpetes = findViewById(R.id.recyclerCarpetes);
        recyclerViewCarpetes.setLayoutManager(new LinearLayoutManager(this));

// Compartits
        includeLogsCompartits = findViewById(R.id.includeLogsCompartits);
        layoutErrorCompartits = (LinearLayout) includeLogsCompartits;
        txtTitolErrorCompartits = includeLogsCompartits.findViewById(R.id.txtTitolError);
        txtDescripcioErrorCompartits = includeLogsCompartits.findViewById(R.id.txtDescripcioError);
        imgVErrorCompartits = includeLogsCompartits.findViewById(R.id.imgVError);
        recyclerViewCompartits = findViewById(R.id.recyclerCompartits);
        recyclerViewCompartits.setLayoutManager(new LinearLayoutManager(this));

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
                Intent intent = new Intent(this, CompartitActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(this, PerfilActivity.class);
                intent.putExtra("usuariPropi", true);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenirDades();
    }

    private void obtenirDades() {
        // Obtenir ítems
        ItemDTO.RequestItem requestItem = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class);
        requestItem.getAllItems().enqueue(new Callback<ArrayList<Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RESPONSE", response.toString());
                    items.clear();
                    items.addAll(response.body());
                    if (items.size() > 0) {
                        actualizarInfo(1);
                        itemAdapter.notifyDataSetChanged();
                        recyclerViewItems.setVisibility(RecyclerView.VISIBLE);
                        layoutErrorItems.setVisibility(View.GONE);
                    } else {
                        recyclerViewItems.setVisibility(View.GONE);
                        layoutErrorItems.setVisibility(View.VISIBLE);
                        layoutErrorItems.setBackground(ContextCompat.getDrawable(
                                HomeActivity.this, R.drawable.background_log_empty));
                        txtTitolErrorItems.setText("Ítems buït");
                        txtDescripcioErrorItems.setText("Aquest usuari no té cap ítem");
                        imgVErrorItems.setImageResource(R.drawable.key_negra);
                    }
                } else {
                    recyclerViewItems.setVisibility(View.GONE);
                    layoutErrorItems.setVisibility(View.VISIBLE);
                    layoutErrorItems.setBackground(ContextCompat.getDrawable(
                            HomeActivity.this, R.drawable.background_log_error));
                    txtTitolErrorItems.setText("ERROR");
                    txtDescripcioErrorItems.setText("No s'han pogut obtenir els ítems");
                    imgVErrorItems.setImageResource(R.drawable.error);
                    Log.e("ERROR_RESPONSE", response.toString());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Item>> call, Throwable t) {
                recyclerViewItems.setVisibility(View.GONE);
                layoutErrorItems.setVisibility(View.VISIBLE);
                layoutErrorItems.setBackground(ContextCompat.getDrawable(
                        HomeActivity.this, R.drawable.background_log_error));
                txtTitolErrorItems.setText("ERROR");
                txtDescripcioErrorItems.setText("No s'han pogut obtenir els ítems");
                imgVErrorItems.setImageResource(R.drawable.error);
                Log.e("ERROR_FAILURE", t.getMessage());
            }
        });

        // Obtenir carpetes
        CarpetaDTO.RequestCarpeta requestCarpeta = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class);
        requestCarpeta.getAllCarpetes().enqueue(new Callback<ArrayList<Carpeta>>() {
            @Override
            public void onResponse(Call<ArrayList<Carpeta>> call, Response<ArrayList<Carpeta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RESPONSE", response.toString());
                    carpetes.clear();
                    carpetes.addAll(response.body());
                    if (carpetes.size() > 0) {
                        actualizarInfo(2);
                        carpetaAdapter.notifyDataSetChanged();
                        recyclerViewCarpetes.setVisibility(RecyclerView.VISIBLE);
                        layoutErrorCarpetes.setVisibility(View.GONE);
                    } else {
                        recyclerViewCarpetes.setVisibility(RecyclerView.GONE);
                        layoutErrorCarpetes.setVisibility(View.VISIBLE);
                        layoutErrorCarpetes.setBackground(ContextCompat.getDrawable(
                                HomeActivity.this, R.drawable.background_log_empty));
                        txtTitolErrorCarpetes.setText("Carpetes buïtes");
                        txtDescripcioErrorCarpetes.setText("Aquest usuari no té cap carpeta");
                        imgVErrorCarpetes.setImageResource(R.drawable.carpeta_negra);
                    }
                } else {
                    recyclerViewCarpetes.setVisibility(RecyclerView.GONE);
                    layoutErrorCarpetes.setVisibility(View.VISIBLE);
                    layoutErrorCarpetes.setBackground(ContextCompat.getDrawable(
                            HomeActivity.this, R.drawable.background_log_error));
                    txtTitolErrorCarpetes.setText("ERROR");
                    txtDescripcioErrorCarpetes.setText("No s'han pogut obtenir les carpetes");
                    imgVErrorCarpetes.setImageResource(R.drawable.error);
                    Log.e("ERROR_RESPONSE", response.toString());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Carpeta>> call, Throwable t) {
                recyclerViewCarpetes.setVisibility(RecyclerView.GONE);
                layoutErrorCarpetes.setVisibility(View.VISIBLE);
                layoutErrorCarpetes.setBackground(ContextCompat.getDrawable(
                        HomeActivity.this, R.drawable.background_log_error));
                txtTitolErrorCarpetes.setText("ERROR");
                txtDescripcioErrorCarpetes.setText("No s'han pogut obtenir les carpetes");
                imgVErrorCarpetes.setImageResource(R.drawable.error);
                Log.e("ERROR_FAILURE", t.getMessage());
            }
        });

        // Obtenir compartits
        CompartitDTO.RequestCompartit requestCompartit = CompartitDTO.obtenirJSONCompartit().create(CompartitDTO.RequestCompartit.class);
        requestCompartit.getAllCompartit().enqueue(new Callback<ArrayList<Compartit>>() {
            @Override
            public void onResponse(Call<ArrayList<Compartit>> call, Response<ArrayList<Compartit>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RESPONSE", response.toString());
                    compartits.clear();
                    compartits.addAll(response.body());
                    if (compartits.size() > 0) {
                        actualizarInfo(3);
                        compartitAdapter.notifyDataSetChanged();
                        recyclerViewCompartits.setVisibility(RecyclerView.VISIBLE);
                        layoutErrorCompartits.setVisibility(View.GONE);
                    } else {
                        recyclerViewCompartits.setVisibility(RecyclerView.GONE);
                        layoutErrorCompartits.setVisibility(View.VISIBLE);
                        layoutErrorCompartits.setBackground(ContextCompat.getDrawable(
                                HomeActivity.this, R.drawable.background_log_empty));
                        txtTitolErrorCompartits.setText("Compartits buïts");
                        txtDescripcioErrorCompartits.setText("Aquest usuari no té cap compartit");
                        imgVErrorCompartits.setImageResource(R.drawable.compartit_negre);
                    }
                } else {
                    recyclerViewCompartits.setVisibility(RecyclerView.GONE);
                    layoutErrorCompartits.setVisibility(View.VISIBLE);
                    layoutErrorCompartits.setBackground(ContextCompat.getDrawable(
                            HomeActivity.this, R.drawable.background_log_error));
                    txtTitolErrorCompartits.setText("ERROR");
                    txtDescripcioErrorCompartits.setText("No s'han pogut obtenir els compartits");
                    imgVErrorCompartits.setImageResource(R.drawable.error);
                    Log.e("ERROR_RESPONSE", response.toString());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Compartit>> call, Throwable t) {
                recyclerViewCompartits.setVisibility(RecyclerView.GONE);
                layoutErrorCompartits.setVisibility(View.VISIBLE);
                layoutErrorCompartits.setBackground(ContextCompat.getDrawable(
                        HomeActivity.this, R.drawable.background_log_error));
                txtTitolErrorCompartits.setText("ERROR");
                txtDescripcioErrorCompartits.setText("No s'han pogut obtenir els compartits");
                imgVErrorCompartits.setImageResource(R.drawable.error);
            }
        });
    }

    private void actualizarInfo(int tipus) {
        switch (tipus) {
            case TIPUS_ITEM:
                // Mostrar ítems
                itemAdapter = new ItemAdapter(items, item -> {
                    Intent intent = new Intent(this, ItemActivity.class);
                    intent.putExtra("uuid", item.getUuid().toString());
                    intent.putExtra("title", item.getTitol());
                    intent.putExtra("url", item.getUrl());
                    intent.putExtra("nom_usuari", item.getNomUsuari());
                    intent.putExtra("password", item.getContrasenya());
                    intent.putExtra("notes", item.getNotes());
                    intent.putExtra("fav", item.isFavorit());
                    intent.putExtra("add_edit", 0);
                    intent.putExtra("iv", item.getIv());
                    intent.putExtra("edk", item.getEncryptedDataKey().getEncryptedDataKey());
                    startActivity(intent);
                });
                recyclerViewItems.setAdapter(itemAdapter);
                break;
            case TIPUS_CARPETA:
                // Mostrar carpetes
                carpetaAdapter = new CarpetaAdapter(carpetes, carpeta -> {
                    Intent intent = new Intent(this, CarpetaActivity.class);
                    intent.putExtra("carpeta", carpeta);
                    intent.putExtra("uuid", carpeta.getUuid().toString());
                    intent.putExtra("nom", carpeta.getNom());
                    intent.putExtra("favorit", carpeta.isFavorit());
                    intent.putExtra("items", new ArrayList<>(carpeta.getItems()));
                    intent.putExtra("data_creacio", carpeta.getDataCreacio());
                    startActivity(intent);
                });
                recyclerViewCarpetes.setAdapter(carpetaAdapter);
                break;
            case TIPUS_COMPARTIT:
                // Mostrar compartits
                compartitAdapter = new CompartitAdapter(compartits, compartit -> {
                    if (compartit.getTipusEntitat() == TipusEntitat.CARPETA) {
                        Carpeta carpeta = compartit.getCarpeta();
                        Intent intentCarpeta = new Intent(this, CarpetaActivity.class);
                        intentCarpeta.putExtra("carpeta", carpeta);
                        intentCarpeta.putExtra("uuid", carpeta.getUuid().toString());
                        intentCarpeta.putExtra("nom", carpeta.getNom());
                        intentCarpeta.putExtra("favorit", carpeta.isFavorit());
                        intentCarpeta.putExtra("items", new ArrayList<>(carpeta.getItems()));
                        intentCarpeta.putExtra("data_creacio", carpeta.getDataCreacio());
                        startActivity(intentCarpeta);
                    } else if (compartit.getTipusEntitat() == TipusEntitat.ITEM) {
                        Item item = compartit.getItem();
                        Intent intentItem = new Intent(this, ItemActivity.class);
                        intentItem.putExtra("uuid", item.getUuid().toString());
                        intentItem.putExtra("title", item.getTitol());
                        intentItem.putExtra("url", item.getUrl());
                        intentItem.putExtra("nom_usuari", item.getNomUsuari());
                        intentItem.putExtra("password", item.getContrasenya());
                        intentItem.putExtra("notes", item.getNotes());
                        intentItem.putExtra("fav", item.isFavorit());
                        intentItem.putExtra("add_edit", 0);
                        intentItem.putExtra("iv", item.getIv());
                        intentItem.putExtra("edk", item.getEncryptedDataKey().getEncryptedDataKey());
                        startActivity(intentItem);
                    }
                });
                recyclerViewCompartits.setAdapter(compartitAdapter);
                break;
        }
    }
}