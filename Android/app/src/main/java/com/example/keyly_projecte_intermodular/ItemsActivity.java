package com.example.keyly_projecte_intermodular;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.dto.ItemDTO;
import com.example.keyly_projecte_intermodular.resources.ItemAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout layoutError;
    private ItemAdapter itemAdapter;
    private EditText etCercar;
    private ImageButton btnAddItem;
    private ImageView btnFiltres;
    private BottomNavigationView menu;
    private boolean filtrat = false;
    private ArrayList<Item> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_items);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
                return true;
            }
            return false;
        });

        recyclerView = findViewById(R.id.recyclerItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemDTO.RequestItem requestItem = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class);
        requestItem.getAllItems().enqueue(new Callback<ArrayList<Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    items.clear();
                    items.addAll(response.body());
                    itemAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(RecyclerView.VISIBLE);
                } else {
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Item>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });

        Log.d("ITEMS_JSON", new Gson().toJson(items));

        actulitzarItems(items);

        etCercar = findViewById(R.id.aCTVCercarItems);
        etCercar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrar items
                String titolItem = s.toString();
                resultatsCerca(titolItem);
            }
        });

        btnFiltres = findViewById(R.id.imgBtnFiltres);
        btnFiltres.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.alert_dialog_filtre, null);

            builder.setView(view);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Elements del AlertDialog
            CheckBox cbTots = view.findViewById(R.id.cbTots);
            CheckBox cbUltimsUsats = view.findViewById(R.id.cbUltimsUsats);
            CheckBox cbMesUsats = view.findViewById(R.id.cbMesUsats);
            CheckBox cbFavorits = view.findViewById(R.id.cbFavorits);
            Button btnFiltrar = view.findViewById(R.id.btnFiltrar);
            Button btnCancelar = view.findViewById(R.id.btnCancelar);

            // Marcar i descarmar tots els filtres a l'hora
            cbTots.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (filtrat) return;
                filtrat = true;

                cbUltimsUsats.setChecked(isChecked);
                cbMesUsats.setChecked(isChecked);
                cbFavorits.setChecked(isChecked);

                filtrat = false;
            });

            // Marcar i desmarcar l'opció Tots, segons els filtres marcats
            CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
                if (filtrat) return;
                filtrat = true;

                // Si alguno se desmarca → quitar "Todos"
                if (!cbUltimsUsats.isChecked() || !cbMesUsats.isChecked() || !cbFavorits.isChecked()) {
                    cbTots.setChecked(false);
                }

                // Si los 3 están marcados → activar "Todos"
                if (cbUltimsUsats.isChecked() && cbMesUsats.isChecked() && cbFavorits.isChecked()) {
                    cbTots.setChecked(true);
                }

                filtrat = false;
            };

            cbUltimsUsats.setOnCheckedChangeListener(listener);
            cbMesUsats.setOnCheckedChangeListener(listener);
            cbFavorits.setOnCheckedChangeListener(listener);

            // Obtenir resultats
            btnFiltrar.setOnClickListener(f -> {
                ArrayList<Integer> filtres = new ArrayList<>();

                if (cbTots.isChecked()) {
                    filtres.add(0);
                } else if (cbUltimsUsats.isChecked()) {
                    filtres.add(1);
                } else if (cbMesUsats.isChecked()) {
                    filtres.add(2);
                } else if (cbFavorits.isChecked()) {
                    filtres.add(3);
                }

                ArrayList<Item> itemsFiltrats = filtrarItems(filtres);

                actulitzarItems(itemsFiltrats);

                alertDialog.dismiss();
            });

            // Cancelar filtres
            btnCancelar.setOnClickListener(c -> {
                alertDialog.dismiss();
            });

        });

        btnAddItem = findViewById(R.id.add_item);
        btnAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(this, ItemActivity.class);
            intent.putExtra("add_edit", 1);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ItemDTO.RequestItem requestItem = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class);
        requestItem.getAllItems().enqueue(new Callback<ArrayList<Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RAW_JSON", response.body().toString());
                    items.clear();
                    items.addAll(response.body());
                    itemAdapter.notifyDataSetChanged();
                    actulitzarItems(items);
                    recyclerView.setVisibility(RecyclerView.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Item>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    private void resultatsCerca(String titolItem) {
        ArrayList<Item> llistaFiltradaItems = new ArrayList<>();

        for (Item item : items) {
            // Comporovar si coincideix algún nom amb la el títol del ítem
            if (item.getTitol().toLowerCase().contains(titolItem.toLowerCase())) {
                llistaFiltradaItems.add(item);
            }
        }

        actulitzarItems(llistaFiltradaItems);
    }

    private ArrayList<Item> filtrarItems(ArrayList<Integer> filtres) {
        ArrayList<Item> llistaFiltradaItems = new ArrayList<>();

        for (int i = 0; i < filtres.size(); i++) {
            for (int j = 0; j < items.size(); j++) {
                switch (filtres.get(i)) {
                    case 0:
                        return items;
                    case 1:
                        Log.d("Filtre 1", "Últims usats");
                        break;
                    case 2:
                        Log.d("Filtre 2", "Més usats");
                        break;
                    case 3:
                        Log.d("Filtre 3", "Favorits");
                        if (items.get(j).isFavorit()) {
                            llistaFiltradaItems.add(items.get(j));
                        }
                        break;
                    default:
                        Log.e("ERROR", "No existeix aquesta opció");

                }
            }
        }

        return llistaFiltradaItems;
    }

    private void actulitzarItems(ArrayList<Item> items) {
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
            intent.putExtra("edk", item.getEncryptedDataKey().getEncryptedDatakey());
            Log.d("EDK_DEBUG", "edk: " + (item.getEncryptedDataKey() != null ? item.getEncryptedDataKey().getEncryptedDatakey() : "NULL"));
            Log.d("EDK_DEBUG", "item json: " + new Gson().toJson(item));
            startActivity(intent);
        });
        recyclerView.setAdapter(itemAdapter);
    }
}