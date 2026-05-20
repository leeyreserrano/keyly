package com.example.keyly_projecte_intermodular;

import static com.example.keyly_projecte_intermodular.gestions.GestionsCompartits.actualitzarCompartits;
import static com.example.keyly_projecte_intermodular.resources.Varis.obtenirTotalVulnerades;
import static com.example.keyly_projecte_intermodular.utils.LogOutService.logOut;
import static com.example.keyly_projecte_intermodular.gestions.GestionsCarpetes.actualitzarCarpetes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.example.keyly_projecte_intermodular.gestions.GestionsIdiomes;
import com.example.keyly_projecte_intermodular.utils.TipusEntitat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private View includeLogsItems, includeLogsCarpetes, includeLogsCompartits;
    private RecyclerView recyclerViewItems, recyclerViewCarpetes, recyclerViewCompartits;
    private LinearLayout layoutErrorItems, layoutErrorCarpetes, layoutErrorCompartits;
    private TextView txtTotalsQty, txtVulneradesQty, txtRepetidesQty, txtTitolErrorItems,
            txtDescripcioErrorItems, txtTitolErrorCarpetes, txtDescripcioErrorCarpetes,
            txtTitolErrorCompartits, txtDescripcioErrorCompartits;
    private ImageView imgVErrorItems, imgVErrorCarpetes, imgVErrorCompartits;
    private Button btnFiltrarTot, btnFiltrarMesUsats, btnFiltrarFavorits;
    private ImageButton imgBtnAjuda, imgBtnIdioma, imgBtnLogOut;
    private BottomNavigationView menu;
    private ItemAdapter itemAdapter;
    private CarpetaAdapter carpetaAdapter;
    private CompartitAdapter compartitAdapter;
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Carpeta> carpetes = new ArrayList<>();
    private ArrayList<Compartit> compartits = new ArrayList<>();
    private final int TIPUS_ITEM = 1, TIPUS_CARPETA = 2, TIPUS_COMPARTIT = 3;
    private static int filtreActual = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(GestionsIdiomes.aplicarIdioma(newBase));
    }

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

        imgBtnAjuda = findViewById(R.id.imgBtnAjuda);
        imgBtnAjuda.setOnClickListener(v -> {
            String url = "https://10.147.17.250:8081/docs/";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
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

        // Resum
        txtTotalsQty = findViewById(R.id.txtTotalsQty);
        txtVulneradesQty = findViewById(R.id.txtVulneradesQty);
        txtRepetidesQty = findViewById(R.id.txtRepetidesQty);

        obtenirTotalVulnerades(HomeActivity.this, null, txtVulneradesQty);

        // Filtres
        btnFiltrarTot = findViewById(R.id.btnFiltrarTot);
        btnFiltrarMesUsats = findViewById(R.id.btnFiltrarMesUsats);
        btnFiltrarFavorits = findViewById(R.id.btnFiltrarFavorits);

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

//        obtenirDades(filtreActual);

        if (filtreActual == 0) {
            btnFiltrarTot.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.background_btn_filtres_home_selected));
            btnFiltrarMesUsats.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.background_btn_filtres_home));
            btnFiltrarFavorits.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.background_btn_filtres_home));
        } else if (filtreActual == 1) {
            btnFiltrarTot.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.background_btn_filtres_home));
            btnFiltrarMesUsats.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.background_btn_filtres_home_selected));
            btnFiltrarFavorits.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.background_btn_filtres_home));
        } else if (filtreActual == 2) {
            btnFiltrarTot.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.background_btn_filtres_home));
            btnFiltrarMesUsats.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.background_btn_filtres_home));
            btnFiltrarFavorits.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.background_btn_filtres_home_selected));
        }

        btnFiltrarTot.setOnClickListener(v -> {
            btnFiltrarTot.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.background_btn_filtres_home_selected));
            btnFiltrarMesUsats.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.background_btn_filtres_home));
            btnFiltrarFavorits.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.background_btn_filtres_home));
            filtreActual = 0;
            obtenirDades(filtreActual);
        });

        btnFiltrarMesUsats.setOnClickListener(v -> {
            btnFiltrarTot.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.background_btn_filtres_home));
            btnFiltrarMesUsats.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.background_btn_filtres_home_selected));
            btnFiltrarFavorits.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.background_btn_filtres_home));
            filtreActual = 1;
            obtenirDades(filtreActual);
        });

        btnFiltrarFavorits.setOnClickListener(v -> {
            btnFiltrarTot.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.background_btn_filtres_home));
            btnFiltrarMesUsats.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.background_btn_filtres_home));
            btnFiltrarFavorits.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.background_btn_filtres_home_selected));
            filtreActual = 2;
            obtenirDades(filtreActual);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenirDades(filtreActual);
    }

    private void obtenirDades(int filtre) {
        // Obtenir ítems
        ItemDTO.RequestItem requestItem = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class);
        requestItem.getAllItems().enqueue(new Callback<ArrayList<Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RESPONSE", response.toString());
                    items.clear();
                    items.addAll(response.body());
                    txtTotalsQty.setText(String.valueOf(items.size())); // Mostrar total d'ítems
                    if (items.size() > 0) {
                        if (filtre == 0) { // Mostrar tots els ítems
                            actualitzarItems(items);
                        } else if (filtre == 1) { // Mostrar els ítems més usats
                            items.sort(Comparator.comparing(Item::getComptadorAccess).reversed());
                            actualitzarItems(items);
                        } else if (filtre == 2) { // Mostrar els ítems favorits
                            ArrayList<Item> itemsFavorits = new ArrayList<>();
                            for (Item item : items) {
                                if (item.isFavorit()) {
                                    itemsFavorits.add(item);
                                }
                            }
                            actualitzarItems(itemsFavorits);
                        }
                        itemAdapter.notifyDataSetChanged();
                        recyclerViewItems.setVisibility(RecyclerView.VISIBLE);
                        layoutErrorItems.setVisibility(View.GONE);
                    } else {
                        recyclerViewItems.setVisibility(View.GONE);
                        layoutErrorItems.setVisibility(View.VISIBLE);
                        layoutErrorItems.setBackground(ContextCompat.getDrawable(
                                HomeActivity.this, R.drawable.background_log_empty));
                        txtTitolErrorItems.setText(getString(R.string.titolItemsBuits));
                        txtDescripcioErrorItems.setText(getString(R.string.etiquetaItemsBuits));
                        imgVErrorItems.setImageResource(R.drawable.key_negra);
                    }
                } else {
                    recyclerViewItems.setVisibility(View.GONE);
                    layoutErrorItems.setVisibility(View.VISIBLE);
                    layoutErrorItems.setBackground(ContextCompat.getDrawable(
                            HomeActivity.this, R.drawable.background_log_error));
                    txtTitolErrorItems.setText("ERROR");
                    txtDescripcioErrorItems.setText(getString(R.string.etiquetaItemsError));
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
                txtDescripcioErrorItems.setText(getString(R.string.etiquetaItemsError));
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
                        if (filtre == 0) { // Mostrar tots les carpetes
                            actualitzarCarpetes(carpetes, carpetaAdapter, HomeActivity.this,
                                    recyclerViewCarpetes);
                        } else if (filtre == 1) { // Mostrar les carpetes més utilitzades
                            carpetes.sort(Comparator.comparing(Carpeta::getComptadorAccess).reversed());
                            actualitzarCarpetes(carpetes, carpetaAdapter, HomeActivity.this,
                                    recyclerViewCarpetes);
                        } else if (filtre == 2) { // Mostrar les carpetes utilitzades
                            ArrayList<Carpeta> carpetesFavorit = new ArrayList<>();
                            for (Carpeta carpeta : carpetes) {
                                if (carpeta.isFavorit()) {
                                    carpetesFavorit.add(carpeta);
                                }
                            }
                            actualitzarCarpetes(carpetes, carpetaAdapter, HomeActivity.this,
                                    recyclerViewCarpetes);
                        }
                        //carpetaAdapter.notifyDataSetChanged();
                        recyclerViewCarpetes.setVisibility(RecyclerView.VISIBLE);
                        layoutErrorCarpetes.setVisibility(View.GONE);
                    } else {
                        recyclerViewCarpetes.setVisibility(RecyclerView.GONE);
                        layoutErrorCarpetes.setVisibility(View.VISIBLE);
                        layoutErrorCarpetes.setBackground(ContextCompat.getDrawable(
                                HomeActivity.this, R.drawable.background_log_empty));
                        txtTitolErrorCarpetes.setText(getString(R.string.titolCarpetesBuides));
                        txtDescripcioErrorCarpetes.setText(getString(R.string.etiquetaCarpetesBuides));
                        imgVErrorCarpetes.setImageResource(R.drawable.carpeta_negra);
                    }
                } else {
                    recyclerViewCarpetes.setVisibility(RecyclerView.GONE);
                    layoutErrorCarpetes.setVisibility(View.VISIBLE);
                    layoutErrorCarpetes.setBackground(ContextCompat.getDrawable(
                            HomeActivity.this, R.drawable.background_log_error));
                    txtTitolErrorCarpetes.setText("ERROR");
                    txtDescripcioErrorCarpetes.setText(getString(R.string.etiquetaCarpetesError));
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
                txtDescripcioErrorCarpetes.setText(getString(R.string.etiquetaCarpetesError));
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
                    for (Compartit compartit : compartits) {
                        if (compartit.getTipusEntitat() == TipusEntitat.ITEM) {
                            if (compartit.getItem() != null) {
                                if (compartit.getItem().getComptadorAccess() != 0) {
                                    compartit.setComptadorAccess(compartit.getItem().getComptadorAccess());
                                }
                            }
                        } else if (compartit.getTipusEntitat() == TipusEntitat.CARPETA) {
                            if (compartit.getCarpeta() != null) {
                                if (compartit.getCarpeta().getComptadorAccess() != 0) {
                                    compartit.setComptadorAccess(compartit.getCarpeta().getComptadorAccess());
                                }
                            }
                        }
                    }
                    if (compartits.size() > 0) {
                        if (filtre == 0) { // Mostrar tots els compartits
                            actualitzarCompartits(HomeActivity.this, compartits,
                                    compartitAdapter, recyclerViewCompartits, false);
                        } else if (filtre == 1) { // Mostrar els compartits més usats
                            compartits.sort(Comparator.comparing(Compartit::getComptadorAccess).reversed());
                            actualitzarCompartits(HomeActivity.this, compartits,
                                    compartitAdapter, recyclerViewCompartits, false);
                        } else if (filtre == 2) { // Mostrar els compartits favorits
                            ArrayList<Compartit> compartitsFavorits = new ArrayList<>();
                            for (Compartit compartit : compartits) {
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
                            actualitzarCompartits(HomeActivity.this, compartitsFavorits,
                                    compartitAdapter, recyclerViewCompartits, false);
                        }
                        //compartitAdapter.notifyDataSetChanged();
                        recyclerViewCompartits.setVisibility(RecyclerView.VISIBLE);
                        layoutErrorCompartits.setVisibility(View.GONE);
                    } else {
                        recyclerViewCompartits.setVisibility(RecyclerView.GONE);
                        layoutErrorCompartits.setVisibility(View.VISIBLE);
                        layoutErrorCompartits.setBackground(ContextCompat.getDrawable(
                                HomeActivity.this, R.drawable.background_log_empty));
                        txtTitolErrorCompartits.setText(getString(R.string.titolCompartitsBuits));
                        txtDescripcioErrorCompartits.setText(getString(R.string.etiquetaCompartitsBuits));
                        imgVErrorCompartits.setImageResource(R.drawable.compartit_negre);
                    }
                } else {
                    recyclerViewCompartits.setVisibility(RecyclerView.GONE);
                    layoutErrorCompartits.setVisibility(View.VISIBLE);
                    layoutErrorCompartits.setBackground(ContextCompat.getDrawable(
                            HomeActivity.this, R.drawable.background_log_error));
                    txtTitolErrorCompartits.setText("ERROR");
                    txtDescripcioErrorCompartits.setText(getString(R.string.etiquetaCompartitsError));
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
                txtDescripcioErrorCompartits.setText(getString(R.string.etiquetaCompartitsError));
                imgVErrorCompartits.setImageResource(R.drawable.error);
            }
        });
    }

    private void actualitzarItems(ArrayList<Item> items) {
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
        }, HomeActivity.this);
        recyclerViewItems.setAdapter(itemAdapter);
    }

//    private void actualizarCarpetes(ArrayList<Carpeta> carpetes) {
//        // Mostrar carpetes
//        carpetaAdapter = new CarpetaAdapter(carpetes, carpeta -> {
//            Intent intent = new Intent(this, CarpetaActivity.class);
//            intent.putExtra("carpeta", carpeta);
//            intent.putExtra("uuid", carpeta.getUuid().toString());
//            intent.putExtra("nom", carpeta.getNom());
//            intent.putExtra("favorit", carpeta.isFavorit());
//            intent.putExtra("items", new ArrayList<>(carpeta.getItems()));
//            intent.putExtra("data_creacio", carpeta.getDataCreacio());
//            startActivity(intent);
//        }, HomeActivity.this);
//        recyclerViewCarpetes.setAdapter(carpetaAdapter);
//    }

//    private void actualitzarCompartits(ArrayList<Compartit> compartits) {
//        // Mostrar compartits
//        compartitAdapter = new CompartitAdapter(compartits, compartit -> {
//            if (compartit.getTipusEntitat() == TipusEntitat.CARPETA) {
//                Carpeta carpeta = compartit.getCarpeta();
//                Intent intentCarpeta = new Intent(this, CarpetaActivity.class);
//                intentCarpeta.putExtra("carpeta", carpeta);
//                intentCarpeta.putExtra("uuid", carpeta.getUuid().toString());
//                intentCarpeta.putExtra("nom", carpeta.getNom());
//                intentCarpeta.putExtra("favorit", carpeta.isFavorit());
//                intentCarpeta.putExtra("items", new ArrayList<>(carpeta.getItems()));
//                intentCarpeta.putExtra("data_creacio", carpeta.getDataCreacio());
//                startActivity(intentCarpeta);
//            } else if (compartit.getTipusEntitat() == TipusEntitat.ITEM) {
//                Item item = compartit.getItem();
//                Intent intentItem = new Intent(this, ItemActivity.class);
//                intentItem.putExtra("uuid", item.getUuid().toString());
//                intentItem.putExtra("title", item.getTitol());
//                intentItem.putExtra("url", item.getUrl());
//                intentItem.putExtra("nom_usuari", item.getNomUsuari());
//                intentItem.putExtra("password", item.getContrasenya());
//                intentItem.putExtra("notes", item.getNotes());
//                intentItem.putExtra("fav", item.isFavorit());
//                intentItem.putExtra("add_edit", 0);
//                intentItem.putExtra("iv", item.getIv());
//                intentItem.putExtra("edk", item.getEncryptedDataKey().getEncryptedDataKey());
//                startActivity(intentItem);
//            }
//        }, HomeActivity.this);
//        recyclerViewCompartits.setAdapter(compartitAdapter);
//    }
}