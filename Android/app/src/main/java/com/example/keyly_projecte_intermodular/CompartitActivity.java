package com.example.keyly_projecte_intermodular;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
import com.example.keyly_projecte_intermodular.dto.CarpetaDTO;
import com.example.keyly_projecte_intermodular.dto.CompartitDTO;
import com.example.keyly_projecte_intermodular.dto.ItemDTO;
import com.example.keyly_projecte_intermodular.dto.UsuariDTO;
import com.example.keyly_projecte_intermodular.request.UsuariRequest;
import com.example.keyly_projecte_intermodular.resources.ItemCarpetaAdapter;
import com.example.keyly_projecte_intermodular.resources.RecercaAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompartitActivity extends AppCompatActivity {

    private BottomNavigationView menu;
    private RecyclerView recyclerView;
    private ImageButton imgBtnAgefirItemCarpeta;
    private ItemCarpetaAdapter itemCarpetaAdapter;
    private Carpeta carpetaCreada;
    private ArrayList<Compartit> compartits = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Item> itemSeleccionats = new ArrayList<>();
    private ArrayList<Usuari> usuaris = new ArrayList<>();
    private ArrayList<UsuariRequest> usuarisSeleccionats = new ArrayList<>();

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

        recyclerView = findViewById(R.id.recyclerItemsCarpetes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Mostrar ítems i carpetes compartits
        obtenirDades();

        imgBtnAgefirItemCarpeta = findViewById(R.id.imgBtnAgefirItemCarpeta);
        imgBtnAgefirItemCarpeta.setOnClickListener(v -> {
            // TODO dialog para escoger entre carpetas e ítems
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_item_folder_selector, null);

            builder.setView(view);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            LinearLayout llItem = view.findViewById(R.id.llItem);
            llItem.setOnClickListener(c -> {
                // TODO añadir ítems compartidos
                alertDialog.dismiss();
            });

            LinearLayout llCarpeta = view.findViewById(R.id.llCarpeta);
            llCarpeta.setOnClickListener(c -> {
                // TODO añadir carpetas compartidos
                alertDialog.dismiss();
                afegirCarpetes();
            });

            Button btnCancelarCarpeta = view.findViewById(R.id.btnCancelarCarpeta);
            btnCancelarCarpeta.setOnClickListener(c -> {
                alertDialog.dismiss();
            });
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
        itemCarpetaAdapter = new ItemCarpetaAdapter(compartits, compartit -> {

        });
        recyclerView.setAdapter(itemCarpetaAdapter);
    }

    private void afegirCarpetes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_carpeta_editar, null);

        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Elements del AlertDialog
        LinearLayout llNomCarpeta = view.findViewById(R.id.llNomCarpeta);
        llNomCarpeta.setVisibility(View.VISIBLE);

        View vSeparador1 = view.findViewById(R.id.vSeparador1);
        vSeparador1.setVisibility(View.VISIBLE);

        LinearLayout llAfegirItems = view.findViewById(R.id.llDesplegableItems);
        llAfegirItems.setVisibility(View.VISIBLE);
        LinearLayout llContingutItems = view.findViewById(R.id.llContigutItems);
        llContingutItems.setVisibility(View.GONE);

        View vSeparador2 = view.findViewById(R.id.vSeparador2);
        vSeparador2.setVisibility(View.VISIBLE);

        LinearLayout llAfegirUsuaris = view.findViewById(R.id.llDesplegableUsuaris);
        llAfegirUsuaris.setVisibility(View.VISIBLE);
        LinearLayout llContingutUsuaris = view.findViewById(R.id.llContigutUsuaris);
        llContingutUsuaris.setVisibility(View.GONE);

        View vSeparador3 = view.findViewById(R.id.vSeparador3);
        vSeparador3.setVisibility(View.VISIBLE);

        // Mostrar per afegir ítems a la carpeta
        llAfegirItems.setOnClickListener(v -> {
            if (llContingutUsuaris.getVisibility() == View.VISIBLE) {
                llContingutUsuaris.setVisibility(View.GONE);
                llContingutItems.setVisibility(View.VISIBLE);
            } else if (llContingutUsuaris.getVisibility() == View.GONE) {
                if (llContingutItems.getVisibility() == View.VISIBLE) {
                    llContingutItems.setVisibility(View.GONE);
                } else {
                    llContingutItems.setVisibility(View.VISIBLE);
                }
            }
        });

        // Mostrar per compartir a usuaris
        llAfegirUsuaris.setOnClickListener(v -> {
            if (llContingutItems.getVisibility() == View.VISIBLE) {
                llContingutItems.setVisibility(View.GONE);
                llContingutUsuaris.setVisibility(View.VISIBLE);
            } else if (llContingutItems.getVisibility() == View.GONE) {
                if (llContingutUsuaris.getVisibility() == View.VISIBLE) {
                    llContingutUsuaris.setVisibility(View.GONE);
                } else {
                    llContingutUsuaris.setVisibility(View.VISIBLE);
                }
            }
        });

        ImageButton imgBtnStarEdit = view.findViewById(R.id.imgBtnStar);
        EditText etNomCarpeta = view.findViewById(R.id.etNomCarpeta);
        AutoCompleteTextView aCTVCercarItems = view.findViewById(R.id.aCTVCercarItems);
        AutoCompleteTextView aCTVCercarUsuaris = view.findViewById(R.id.aCTVCercarUsuaris);
        RecyclerView recyclerItems = view.findViewById(R.id.recyclerItems);
        RecyclerView recyclerUsuaris = view.findViewById(R.id.recyclerUsuaris);
        Button btnGuardarCarpeta = view.findViewById(R.id.btnGuardarCarpeta);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        recyclerItems.setLayoutManager(new LinearLayoutManager(CompartitActivity.this));
        RecercaAdapter recercaAdapterItems = new RecercaAdapter(itemSeleccionats, null);
        recyclerItems.setAdapter(recercaAdapterItems);

        recyclerUsuaris.setLayoutManager(new LinearLayoutManager(CompartitActivity.this));
        RecercaAdapter recercaAdapterUsuaris = new RecercaAdapter(null, usuarisSeleccionats);
        recyclerUsuaris.setAdapter(recercaAdapterUsuaris);

        AtomicBoolean favActual = new AtomicBoolean(false);
        imgBtnStarEdit.setOnClickListener(c -> {
            if (favActual.get()) {
                imgBtnStarEdit.setImageResource(R.drawable.star);
                favActual.set(false);
            } else {
                imgBtnStarEdit.setImageResource(R.drawable.filled_star);
                favActual.set(true);
            }
        });

        /* *************************************** Ítems *************************************** */
        // Carregar ítems
        ItemDTO.RequestItem requestItem = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class);
        requestItem.getAllItems().enqueue(new Callback<ArrayList<Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    items = new ArrayList<>();
                    items.addAll(response.body());
                    //Log.d("ITEMS_CARPETA", items.toString());

                    // Cercador d'ítems
                    ArrayList<String> titols = new ArrayList<>();

                    for (Item item : items) {
                        titols.add(item.getTitol());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CompartitActivity.this, android.R.layout.simple_dropdown_item_1line, titols);
                    aCTVCercarItems.setAdapter(adapter);
                } else {
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Item>> call, Throwable t) {
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });

        // Cercador d'ítems
        aCTVCercarItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                aCTVCercarItems.showDropDown();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        aCTVCercarItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String seleccionat = parent.getItemAtPosition(position).toString();

                for (Item item : items) {
                    if (item.getTitol().equals(seleccionat)) {
                        itemSeleccionats.add(item);
                    }
                }

                recercaAdapterItems.notifyDataSetChanged();
                recyclerItems.setAdapter(recercaAdapterItems);
            }
        });
        /* ************************************************************************************* */



        /* ************************************** Usuaris ************************************** */
        // Carregar usuaris
        UsuariDTO.RequestUsuari requestUsuari = UsuariDTO.obtenirJSONUsuari().create(UsuariDTO.RequestUsuari.class);
        requestUsuari.getAllUsuaris().enqueue(new Callback<ArrayList<Usuari>>() {
            @Override
            public void onResponse(Call<ArrayList<Usuari>> call, Response<ArrayList<Usuari>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usuaris = new ArrayList<>();
                    usuaris.addAll(response.body());

                    // Cercador d'usuaris
                    ArrayList<String> noms = new ArrayList<>();

                    for (Usuari usuari : usuaris) {
                        noms.add(usuari.getNom());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CompartitActivity.this, android.R.layout.simple_dropdown_item_1line, noms);
                    aCTVCercarUsuaris.setAdapter(adapter);
                } else {
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Usuari>> call, Throwable t) {
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });

        aCTVCercarUsuaris.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                aCTVCercarUsuaris.showDropDown();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        aCTVCercarUsuaris.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String seleccionat = parent.getItemAtPosition(position).toString();

                // TODO afegir usuaris al recycler

//                for (Usuari usuari : usuaris) {
//                    if (item.getTitol().equals(seleccionat)) {
//                        itemSeleccionats.add(item);
//                    }
//                }

                recercaAdapterUsuaris.notifyDataSetChanged();
                recyclerUsuaris.setAdapter(recercaAdapterUsuaris);
            }
        });
        /* ************************************************************************************* */

        btnGuardarCarpeta.setText("Afegir carpeta");
        btnGuardarCarpeta.setOnClickListener(c -> {
            // TODO que se guarde la carpeta
            String nomCarpeta = etNomCarpeta.getText().toString();
            boolean isFavorit = favActual.get();

            Carpeta carpeta = new Carpeta(nomCarpeta, isFavorit);

            Call<Carpeta> call = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).crearCarpeta(carpeta);
            call.enqueue(new Callback<Carpeta>() {
                @Override
                public void onResponse(Call<Carpeta> call, Response<Carpeta> response) {
                    if (response.isSuccessful()) {
                        carpetaCreada = response.body();
                        Toast.makeText(CompartitActivity.this, "Carpeta " + nomCarpeta + " afegida", Toast.LENGTH_SHORT).show();

                        if (itemSeleccionats.size() > 0) {
                            for (int i = 0; i < itemSeleccionats.size(); i++) {
                                Call<Carpeta> callAddItem = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).afegirItemCarpeta(carpetaCreada.getUuid().toString(), itemSeleccionats.get(i).getUuid().toString());
                                callAddItem.enqueue(new Callback<Carpeta>() {
                                    @Override
                                    public void onResponse(Call<Carpeta> callAddItem, Response<Carpeta> response) {
                                        if (response.isSuccessful()) {
                                            Log.d("ITEMS_AFEGITS", itemSeleccionats.toString());
                                        } else {
                                            Log.d("ERROR_RESPONSE", response.message());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Carpeta> callAddItem, Throwable t) {
                                        Log.d("ERROR_FAILURE", t.getMessage());
                                    }
                                });
                            }
                        }

                        if (usuarisSeleccionats.size() > 0) {
                            for (int i = 0; i < usuarisSeleccionats.size(); i++) {
                                // TODO compartir la carpeta
                                Call<Compartit> callAddUsuari = CompartitDTO.obtenirJSONCompartit().create(CompartitDTO.RequestCompartit.class).compartirCarpeta(carpetaCreada.getUuid().toString(), );
                            }
                        }

                        alertDialog.dismiss();
                        obtenirDades();
                        actualitzarCompartits(compartits);
                    } else {
                        Toast.makeText(CompartitActivity.this, "No s'ha pogut afegir la carpeta " + nomCarpeta , Toast.LENGTH_SHORT).show();
                        Log.d("ERROR_RESPONSE", response.message());
                    }
                }

                @Override
                public void onFailure(Call<Carpeta> call, Throwable t) {
                    Toast.makeText(CompartitActivity.this, "No s'ha pogut eliminar la carpeta " + nomCarpeta, Toast.LENGTH_SHORT).show();
                    Log.d("ERROR_FAILURE", t.getMessage());
                }
            });
        });

        btnCancelar.setOnClickListener(c -> {
            alertDialog.dismiss();
        });
    }
}