package com.example.keyly_projecte_intermodular;

import static com.example.keyly_projecte_intermodular.gestions.GestionsItems.actualitzarItems;
import static com.example.keyly_projecte_intermodular.utils.LogOutService.logOut;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
import com.example.keyly_projecte_intermodular.adapters.ItemAdapter;
import com.example.keyly_projecte_intermodular.gestions.GestionsIdiomes;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout layoutError;
    private ItemAdapter itemAdapter;
    private EditText etCercar;
    private ImageButton imgBtnAjuda, imgBtnIdioma, imgBtnLogOut, btnAddItem;
    private ImageView btnFiltres;
    private BottomNavigationView menu;
    private boolean filtrat = false;
    private ArrayList<Item> items = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(GestionsIdiomes.aplicarIdioma(newBase));
    }

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

        imgBtnAjuda = findViewById(R.id.imgBtnAjuda);
        imgBtnAjuda.setOnClickListener(v -> {
            String url = "https://10.147.17.250:8081/docs/";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        imgBtnIdioma = findViewById(R.id.imgBtnIdioma);
        imgBtnIdioma.setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_idiomes, null);

            builder.setView(view);

            android.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Elements del AlertDialog
            RadioGroup rgIdioma = view.findViewById(R.id.rgIdioma);
            RadioButton rbCA = view.findViewById(R.id.rbCA); // Català
            RadioButton rbEN = view.findViewById(R.id.rbEN); // Anglès
            RadioButton rbES = view.findViewById(R.id.rbES); // Castellà/Espanyol
            Button btnTraduccio = view.findViewById(R.id.btnTraduccio);
            Button btnCancelar = view.findViewById(R.id.btnCancelar);

            String idiomaActual = GestionsIdiomes.obtenirIdioma(this);
            if (idiomaActual.equals("ca")) {
                rbCA.setChecked(true);
            } else if (idiomaActual.equals("en")) {
                rbEN.setChecked(true);
            } else if (idiomaActual.equals("es")) {
                rbES.setChecked(true);
            }

            btnTraduccio.setOnClickListener(c -> {
                // TODO traducir
                if (rgIdioma.getCheckedRadioButtonId() == R.id.rbCA) {
                    GestionsIdiomes.canviarIdioma(this, "ca");
                    recreate();
                } else if (rgIdioma.getCheckedRadioButtonId() == R.id.rbEN) {
                    GestionsIdiomes.canviarIdioma(this, "en");
                    recreate();
                } else if (rgIdioma.getCheckedRadioButtonId() == R.id.rbES) {
                    GestionsIdiomes.canviarIdioma(this, "es");
                    recreate();
                }
                alertDialog.dismiss();
            });

            btnCancelar.setOnClickListener(c -> {
                alertDialog.dismiss();
            });
        });

        imgBtnLogOut = findViewById(R.id.imgBtnLogOut);
        imgBtnLogOut.setOnClickListener(v -> {
            logOut(this);
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
                Intent intent = new Intent(this, PerfilActivity.class);
                intent.putExtra("usuariPropi", true);
                startActivity(intent);
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
                    //itemAdapter.notifyDataSetChanged();
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

        actualitzarItems(items, itemAdapter, ItemsActivity.this, recyclerView);

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
            View view = inflater.inflate(R.layout.layout_filtres, null);

            builder.setView(view);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Elements del AlertDialog
            RadioButton cbTots = view.findViewById(R.id.cbTots);
            RadioButton cbUltimsUsats = view.findViewById(R.id.cbUltimsUsats);
            RadioButton cbMesUsats = view.findViewById(R.id.cbMesUsats);
            CheckBox cbFavorits = view.findViewById(R.id.cbFavorits);
            Button btnFiltrar = view.findViewById(R.id.btnFiltrar);
            Button btnCancelar = view.findViewById(R.id.btnCancelar);

            // Obtenir resultats
            btnFiltrar.setOnClickListener(f -> {
                ArrayList<Item> filtres = new ArrayList<>();

                if (cbTots.isChecked()) {
                    filtres = items;
                    if (cbFavorits.isChecked()) { // Mostrar els ítems favorits
                        ArrayList<Item> itemsFav = new ArrayList<>();
                        for (Item item : filtres) {
                            if (item.isFavorit()) {
                                itemsFav.add(item);
                            }
                        }
                        actualitzarItems(itemsFav, itemAdapter, ItemsActivity.this, recyclerView);
                    } else {
                        actualitzarItems(filtres, itemAdapter, ItemsActivity.this, recyclerView);
                    }
                } else if (cbUltimsUsats.isChecked()) { // Mostrar els últims ítems utilitzats
                    for (Item item : items) {
                        if (item.getUltimAccess() != null) {
                            filtres.add(item);
                        }
                    }
                    filtres.sort(
                            Comparator.comparing(
                                    (Item i) ->
                                            LocalDateTime.parse(i.getUltimAccess())
                            ).reversed()
                    );
                    if (cbFavorits.isChecked()) { // Mostrar els ítems favorits
                        ArrayList<Item> itemsFav = new ArrayList<>();
                        for (Item item : filtres) {
                            if (item.isFavorit()) {
                                itemsFav.add(item);
                            }
                        }
                        actualitzarItems(itemsFav, itemAdapter, ItemsActivity.this, recyclerView);
                    } else {
                        actualitzarItems(filtres, itemAdapter, ItemsActivity.this, recyclerView);
                    }
                } else if (cbMesUsats.isChecked()) { // Mostrar els ítems més usats
                    filtres = items;
                    filtres.sort(Comparator.comparing(Item::getComptadorAccess).reversed());
                    if (cbFavorits.isChecked()) { // Mostrar els ítems favorits
                        ArrayList<Item> itemsFav = new ArrayList<>();
                        for (Item item : filtres) {
                            if (item.isFavorit()) {
                                itemsFav.add(item);
                            }
                        }
                        actualitzarItems(itemsFav, itemAdapter, ItemsActivity.this, recyclerView);
                    } else {
                        actualitzarItems(filtres, itemAdapter, ItemsActivity.this, recyclerView);
                    }
                }

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
                    //itemAdapter.notifyDataSetChanged();
                    actualitzarItems(items, itemAdapter, ItemsActivity.this, recyclerView);
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

        actualitzarItems(llistaFiltradaItems, itemAdapter, ItemsActivity.this, recyclerView);
    }
}