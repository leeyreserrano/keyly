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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarpetaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout layoutError;
    private ItemAdapter itemAdapter;
    private TextView nomCarpeta, dataCreacio;
    private EditText etCercar;
    private ImageView btnFiltres;
    private BottomNavigationView menu;
    private boolean filtrat = false;
    private ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_carpeta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        items = (ArrayList<Item>) getIntent().getSerializableExtra("items");

        String nom = getIntent().getStringExtra("nom");
        String data_creacio = getIntent().getStringExtra("data_creacio");

        // Formatejar data
        String dataFormatejada = formatDataCreacio(data_creacio);
        Log.d("DATA", dataFormatejada);

        nomCarpeta = findViewById(R.id.nomCarpeta);
        nomCarpeta.setText(nom);

        dataCreacio = findViewById(R.id.dataCreacio);
        dataCreacio.setText(data_creacio);


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

        recyclerView = findViewById(R.id.recyclerItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        actulitzarItems(items);

        etCercar = findViewById(R.id.et_search);
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

        btnFiltres = findViewById(R.id.btn_filtres);
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
    }

    //TODO arreglar el formateo de la fecha
    private String formatDataCreacio(String dataCreacio) {

//        Calendar calendar = Calendar.getInstance();
        int segons = Integer.parseInt(dataCreacio.substring(17, 18)); // 16:27:(38)
        int minuts = Integer.parseInt(dataCreacio.substring(14, 15)); // 16:(27):38
        int hora = Integer.parseInt(dataCreacio.substring(11, 12)); // (16):27:38
        int dia = Integer.parseInt(dataCreacio.substring(9, 10)); // 2026-04-(13)
        int mes = Integer.parseInt(dataCreacio.substring(6, 7)); // 2026-(04)-13
        int any = Integer.parseInt(dataCreacio.substring(0, 4)); // (2026)-04-13

        LocalDateTime data = LocalDateTime.of(any, mes, dia, hora, minuts, segons);
        LocalDateTime dataActual = LocalDateTime.now();

        Duration duracio = Duration.between(data, dataActual);
        Period period = Period.between(data.toLocalDate(), dataActual.toLocalDate());

        String temps = "Va ser creada fa ";

        if (duracio.toSeconds() < 60) {
            temps += duracio.toSeconds() + " segons";
        } else if (duracio.toMinutes() < 60) {
            temps += duracio.toMinutes() + " minuts";
        } else if (duracio.toHours() < 24) {
            temps += duracio.toHours() + " hores";
        } else if (duracio.toDays() < 7) {
            temps += duracio.toDays() + " dies";
        } else if (duracio.toDays() < 30) {
            if (duracio.toDays() < 14) {
                temps += "1 setmana";
            } else if (duracio.toDays() < 21) {
                temps += "2 setmanes";
            } else if (duracio.toDays() < 28) {
                temps += "3 setmanes";
            }
        } else if (duracio.toDays() < 365) {
            temps += duracio.toDays() + " mesos";
        } else {

        }

        temps = "";

        temps += period.getDays();

        return temps;
    }

    private void resultatsCerca(String titolItem) {
        ArrayList<Item> llistaFiltradaItems = new ArrayList<>();

        for (Item item : items) {
            // Comporovar si coincideix algún nom amb la el títol del ítem
            if (item.getTitol().toLowerCase().contains(titolItem.toLowerCase())) {
                llistaFiltradaItems.add(item);
            }
        }

        itemAdapter = new ItemAdapter(llistaFiltradaItems, item -> {
            Intent intent = new Intent(this, ItemActivity.class);
            intent.putExtra("title", item.getTitol());
            intent.putExtra("url", item.getUrl());
            intent.putExtra("propietari", item.getNomUsuari());
            intent.putExtra("password", item.getContrasenya());
            intent.putExtra("notes", item.getNotes());
            intent.putExtra("fav", item.isFavorit());
            startActivity(intent);
        });
        recyclerView.setAdapter(itemAdapter);
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
            intent.putExtra("title", item.getTitol());
            intent.putExtra("url", item.getUrl());
            intent.putExtra("propietari", item.getNomUsuari());
            intent.putExtra("password", item.getContrasenya());
            intent.putExtra("notes", item.getNotes());
            intent.putExtra("fav", item.isFavorit());
            startActivity(intent);
        });
        recyclerView.setAdapter(itemAdapter);
    }
}