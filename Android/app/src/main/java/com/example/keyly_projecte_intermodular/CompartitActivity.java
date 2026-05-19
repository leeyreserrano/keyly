package com.example.keyly_projecte_intermodular;

import static com.example.keyly_projecte_intermodular.utils.GestionsCarpetes.crearCarpeta;
import static com.example.keyly_projecte_intermodular.utils.LogOutService.logOut;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
import com.example.keyly_projecte_intermodular.request.UsuariCompartitRequest;
import com.example.keyly_projecte_intermodular.adapters.CompartitAdapter;
import com.example.keyly_projecte_intermodular.utils.GestionsIdiomes;
import com.example.keyly_projecte_intermodular.utils.TipusEntitat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompartitActivity extends AppCompatActivity {

    private BottomNavigationView menu;
    private RecyclerView recyclerView;
    private ImageView imgBtnFiltres;
    private ImageButton imgBtnIdioma, imgBtnLogOut, imgBtnCompartir,
            imgBtnCompartirItems, imgBtnCompartirCarpetes;
    private CompartitAdapter compartitAdapter;
    private FrameLayout main;
    private Carpeta carpetaCreada;
    private boolean filtrat = false;
    private ArrayList<Compartit> compartits = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Item> itemsSeleccionats = new ArrayList<>();
    private ArrayList<Carpeta> carpetes = new ArrayList<>();
    private ArrayList<Carpeta> carpetesSeleccionades = new ArrayList<>();
    private ArrayList<Usuari> usuaris = new ArrayList<>(), usuarisSeleccionats = new ArrayList<>();
    private ArrayList<UsuariCompartitRequest> usuarisCompartitRequest = new ArrayList<>();
    private ArrayAdapter<String> adapterItemsCarpetes;
    private ArrayList<String> titolItems = new ArrayList<>(), nomsCarpetes = new ArrayList<>(), nomsUsuaris = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(GestionsIdiomes.aplicarIdioma(newBase));
    }

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

        imgBtnIdioma = findViewById(R.id.imgBtnIdioma);
        imgBtnIdioma.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_idiomes, null);

            builder.setView(view);

            AlertDialog alertDialog = builder.create();
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

        // Mostrar ítems i carpetes compartits
        obtenirDades(0, false);

        imgBtnFiltres = findViewById(R.id.imgBtnFiltres);
        imgBtnFiltres.setOnClickListener(v -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_filtres, null);

            builder.setView(view);

            androidx.appcompat.app.AlertDialog alertDialog = builder.create();
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
                if (cbTots.isChecked()) {
                    if (cbFavorits.isChecked()) {
                        obtenirDades(0, true);
                    } else {
                        obtenirDades(0, false);
                    }
                } else if (cbUltimsUsats.isChecked()) { // Mostrar els últims ítems utilitzats
                    if (cbFavorits.isChecked()) { // Mostrar els ítems favorits
                        obtenirDades(1, true);
                    } else {
                        obtenirDades(1, false);
                    }
                } else if (cbMesUsats.isChecked()) { // Mostrar els ítems més usats
                    if (cbFavorits.isChecked()) { // Mostrar els ítems favorits
                        obtenirDades(2, true);
                    } else {
                        obtenirDades(2, false);
                    }
                }
                alertDialog.dismiss();
            });

            // Cancelar filtres
            btnCancelar.setOnClickListener(c -> {
                alertDialog.dismiss();
            });
        });

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
            crearCarpeta(
                    itemsSeleccionats,
                    usuarisSeleccionats,
                    CompartitActivity.this,
                    items,
                    usuaris,
                    usuarisCompartitRequest,
                    carpetaCreada,
                    carpetes,
                    recyclerView,
                    null,
                    true,
                    () -> obtenirDades(0, false),
                    null,
                    null,
                    null,
                    null
            );
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenirDades(0, false);
    }

    private void obtenirDades(int filtre, boolean fav) {
        CompartitDTO.RequestCompartit  resquestCompartit = CompartitDTO.obtenirJSONCompartit().create(CompartitDTO.RequestCompartit.class);
        resquestCompartit.getAllCompartit().enqueue(new Callback<ArrayList<Compartit>>() {
            @Override
            public void onResponse(Call<ArrayList<Compartit>> call, Response<ArrayList<Compartit>> response) {
                if (response.isSuccessful()) {
                    compartits.clear();
                    compartits.addAll(response.body());
                    for (Compartit compartit : compartits) {
                        if (compartit.getTipusEntitat() == TipusEntitat.ITEM) {
                            if (compartit.getItem() != null) {
                                compartit.setComptadorAccess(compartit.getItem().getComptadorAccess());
                                compartit.setUltimAccess(compartit.getItem().getUltimAccess());
                            }
                        } else if (compartit.getTipusEntitat() == TipusEntitat.CARPETA) {
                            if (compartit.getCarpeta() != null) {
                                compartit.setComptadorAccess(compartit.getCarpeta().getComptadorAccess());
                                compartit.setUltimAccess(compartit.getCarpeta().getUltimAccess());
                            }
                        }
                    }
                    Log.d("COMPARTITS", response.body().toString());
                    ArrayList<Compartit> compartitsF = new ArrayList<>();
                    if (filtre == 0) { // Mostrar tots els compartits
                        compartitsF = compartits;
                        actualitzarCompartits(compartitsF);
                    } else if (filtre == 1) { // Mostrar els útltims compartits usats
                        for (Compartit compartit : compartits) {
                            if (compartit.getUltimAccess() != null) {
                                compartitsF.add(compartit);
                            }
                        }
                        compartitsF.sort(
                                Comparator.comparing(
                                        (Compartit c) ->
                                                LocalDateTime.parse(c.getUltimAccess())
                                ).reversed()
                        );
                        actualitzarCompartits(compartitsF);
                    } else if (filtre == 2) { // Mostrar els compartits utilitzats
                        compartitsF = compartits;
                        compartitsF.sort(Comparator.comparing(Compartit::getComptadorAccess).reversed());
                        actualitzarCompartits(compartitsF);
                    }
                    if (fav) { // Mostrar els compartits favorits
                        ArrayList<Compartit> compartitsFavorits = new ArrayList<>();
                        for (Compartit compartit : compartitsF) {
                            if (compartit.getTipusEntitat() == TipusEntitat.ITEM) {
                                if (compartit.getItem().isFavorit()) {
                                    compartitsFavorits.add(compartit);
                                }
                            } else if (compartit.getTipusEntitat() == TipusEntitat.CARPETA) {
                                if (compartit.getCarpeta().isFavorit()) {
                                    compartitsFavorits.add(compartit);
                                }
                            }
                        }
                        actualitzarCompartits(compartitsFavorits);
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    Log.d("ERROR_RESPONSE_COMPARTITS", response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Compartit>> call, Throwable t) {
                Log.d("ERROR_FAILURE_COMPARTITS", t.getMessage());
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
                intentCarpeta.putExtra("esCompartit", true);
                intentCarpeta.putExtra("uuidCompartit", compartit.getUuid().toString());
                startActivity(intentCarpeta);
            } else if (compartit.getTipusEntitat() == TipusEntitat.ITEM) {
                Log.d("ITEM_COMPARTIT_RECEPTOR", compartit.toString());
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
                intentItem.putExtra("esCompartit", true);
                intentItem.putExtra("uuidCompartit", compartit.getUuid().toString());
                startActivity(intentItem);
            }
        }, CompartitActivity.this);
        recyclerView.setAdapter(compartitAdapter);
    }

//    private void obtenirUsuarisCompartits(ArrayList<>) {
//
//    }

    private void modificarPermisos() {

    }
}