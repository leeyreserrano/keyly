package com.example.keyly_projecte_intermodular;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.dao.Carpeta;
import com.example.keyly_projecte_intermodular.dao.Compartit;
import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.dto.CompartitDTO;
import com.example.keyly_projecte_intermodular.request.UsuariRequest;
import com.example.keyly_projecte_intermodular.resources.CompartitAdapter;
import com.example.keyly_projecte_intermodular.utils.TipusEntitat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompartitActivity extends AppCompatActivity {

    private BottomNavigationView menu;
    private RecyclerView recyclerView;
    private ImageButton imgBtnCompartir, imgBtnCompartirItems, imgBtnCompartirCarpetes;
    private CompartitAdapter compartitAdapter;
    private FrameLayout main;
    private Carpeta carpetaCreada;
    private ArrayList<Compartit> compartits = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Item> itemsSeleccionats = new ArrayList<>();
    private ArrayList<Carpeta> carpetes = new ArrayList<>();
    private ArrayList<Carpeta> carpetesSeleccionades = new ArrayList<>();
    private ArrayList<Usuari> usuaris = new ArrayList<>(), usuarisSeleccionats = new ArrayList<>();
    private ArrayList<UsuariRequest> usuarisRequest = new ArrayList<>();
    private ArrayAdapter<String> adapterItemsCarpetes;
    private ArrayList<String> titolItems = new ArrayList<>(), nomsCarpetes = new ArrayList<>(), nomsUsuaris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_compartit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        main = findViewById(R.id.main);
        EdgeToEdge.enable(this);

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
                Intent intent = new Intent(this, PerfilActivity.class);
                intent.putExtra("usuariPropi", true);
                startActivity(intent);
                return true;
            }
            return false;
        });

        recyclerView = findViewById(R.id.recyclerItemsCarpetes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (imgBtnCompartirItems.getVisibility() == View.VISIBLE
//                        && imgBtnCompartirCarpetes.getVisibility() == View.VISIBLE) {
//                    imgBtnCompartirItems.setVisibility(View.GONE);
//                    imgBtnCompartirCarpetes.setVisibility(View.GONE);
//                }
//                return true;
//            }
//        });

        // Mostrar ítems i carpetes compartits
        obtenirDades();

        imgBtnCompartir = findViewById(R.id.imgBtnCompartirItemsCarpetes);
        imgBtnCompartirItems = findViewById(R.id.imgBtnCompartirItems);
        imgBtnCompartirCarpetes = findViewById(R.id.imgBtnCompartirCarpetes);
        imgBtnCompartirItems.setVisibility(View.GONE);
        imgBtnCompartirCarpetes.setVisibility(View.GONE);

        imgBtnCompartir.setOnClickListener(v -> {
            if (imgBtnCompartirItems.getVisibility() == View.GONE
                    && imgBtnCompartirCarpetes.getVisibility() == View.GONE) {
                imgBtnCompartirItems.setVisibility(View.VISIBLE);
                imgBtnCompartirCarpetes.setVisibility(View.VISIBLE);
            } else {
                imgBtnCompartirItems.setVisibility(View.GONE);
                imgBtnCompartirCarpetes.setVisibility(View.GONE);
            }
        });

        main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (imgBtnCompartirItems.getVisibility() == View.VISIBLE
                        && imgBtnCompartirCarpetes.getVisibility() == View.VISIBLE) {
                    imgBtnCompartirItems.setVisibility(View.GONE);
                    imgBtnCompartirCarpetes.setVisibility(View.GONE);
                }
                return true;
            }
        });

        imgBtnCompartirItems.setOnClickListener(v -> {
            // TODO ir a item en modo crear y obligar a que se comparta
            imgBtnCompartirItems.setVisibility(View.GONE);
            Intent intent = new Intent(this, ItemActivity.class);
            intent.putExtra("add_edit", 1);
            intent.putExtra("compartirObligatori", true);
            startActivity(intent);
        });


        imgBtnCompartirCarpetes.setOnClickListener(v -> {
            // TODO hacer la ventana de carpeta
            imgBtnCompartirCarpetes.setVisibility(View.GONE);
        });


    }

    private void obtenirDades() {
        CompartitDTO.RequestCompartit  resquestCompartit = CompartitDTO.obtenirJSONCompartit().create(CompartitDTO.RequestCompartit.class);
        resquestCompartit.getAllCompartit().enqueue(new Callback<ArrayList<Compartit>>() {
            @Override
            public void onResponse(Call<ArrayList<Compartit>> call, Response<ArrayList<Compartit>> response) {
                if (response.isSuccessful()) {
                    compartits.clear();
                    compartits.addAll(response.body());
                    Log.d("COMPARTITS", response.body().toString());
                    actualitzarCompartits(compartits);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Compartit>> call, Throwable t) {
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    private void actualitzarCompartits(ArrayList<Compartit> compartits) {
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
        recyclerView.setAdapter(compartitAdapter);
    }

    private void compartir() {

    }
}