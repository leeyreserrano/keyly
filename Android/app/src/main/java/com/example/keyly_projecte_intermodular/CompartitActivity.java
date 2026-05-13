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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompartitActivity extends AppCompatActivity {

    private BottomNavigationView menu;
    private RecyclerView recyclerView;
    private ImageButton imgBtnCompartirItemsCarpetes;
    private ItemCarpetaAdapter itemCarpetaAdapter;
    private Carpeta carpetaCreada;
    private ArrayList<Compartit> compartits = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Item> itemSeleccionats = new ArrayList<>();
    private ArrayList<Carpeta> carpetes = new ArrayList<>();
    private ArrayList<Carpeta> carpetesSeleccionades = new ArrayList<>();
    private ArrayList<Usuari> usuaris = new ArrayList<>();
    private ArrayList<UsuariRequest> usuarisSeleccionats = new ArrayList<>();
    private ArrayAdapter<String> adapterItemsCarpetes;
    private ArrayList<String> titolsNoms = new ArrayList<>();

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
        obtenirDades();

        imgBtnCompartirItemsCarpetes = findViewById(R.id.imgBtnCompartirItemsCarpetes);
        imgBtnCompartirItemsCarpetes.setOnClickListener(v -> {
            compartir();
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
        itemCarpetaAdapter = new ItemCarpetaAdapter(compartits, compartit -> {

        });
        recyclerView.setAdapter(itemCarpetaAdapter);
    }

    private void compartir() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_compartir, null);

        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Elements del AlertDialog
        LinearLayout llAfegirItemsCarpetes = view.findViewById(R.id.llDesplegableItemsCarpetes);
        llAfegirItemsCarpetes.setVisibility(View.VISIBLE);
        LinearLayout llContingutItemsCarpetes = view.findViewById(R.id.llContigutItemsCarpetes);
        llContingutItemsCarpetes.setVisibility(View.GONE);

        View vSeparador2 = view.findViewById(R.id.vSeparador1);
        vSeparador2.setVisibility(View.GONE);

        LinearLayout llAfegirUsuaris = view.findViewById(R.id.llDesplegableUsuaris);
        llAfegirUsuaris.setVisibility(View.VISIBLE);
        LinearLayout llContingutUsuaris = view.findViewById(R.id.llContigutUsuaris);
        llContingutUsuaris.setVisibility(View.GONE);

        View vSeparador3 = view.findViewById(R.id.vSeparador2);
        vSeparador3.setVisibility(View.VISIBLE);

        // Mostrar per afegir ítems a la carpeta
        llAfegirItemsCarpetes.setOnClickListener(v -> {
            if (llContingutUsuaris.getVisibility() == View.VISIBLE) {
                llContingutUsuaris.setVisibility(View.GONE);
                llContingutItemsCarpetes.setVisibility(View.VISIBLE);
            } else if (llContingutUsuaris.getVisibility() == View.GONE) {
                if (llContingutItemsCarpetes.getVisibility() == View.VISIBLE) {
                    llContingutItemsCarpetes.setVisibility(View.GONE);
                } else {
                    llContingutItemsCarpetes.setVisibility(View.VISIBLE);
                }
            }
        });

        // Mostrar per compartir a usuaris
        llAfegirUsuaris.setOnClickListener(v -> {
            if (llContingutItemsCarpetes.getVisibility() == View.VISIBLE) {
                llContingutItemsCarpetes.setVisibility(View.GONE);
                llContingutUsuaris.setVisibility(View.VISIBLE);
            } else if (llContingutItemsCarpetes.getVisibility() == View.GONE) {
                if (llContingutUsuaris.getVisibility() == View.VISIBLE) {
                    llContingutUsuaris.setVisibility(View.GONE);
                } else {
                    llContingutUsuaris.setVisibility(View.VISIBLE);
                }
            }
        });

        AutoCompleteTextView aCTVCercarItemsCarpetes = view.findViewById(R.id.aCTVCercarItemsCarpetes);
        AutoCompleteTextView aCTVCercarUsuaris = view.findViewById(R.id.aCTVCercarUsuaris);
        RecyclerView recyclerItemsCarpetes = view.findViewById(R.id.recyclerItemsCarpetes);
        RecyclerView recyclerUsuaris = view.findViewById(R.id.recyclerUsuaris);
        Button btnCompartirItemsCarpetes = view.findViewById(R.id.btnCompartirItemsCarpetes);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        recyclerItemsCarpetes.setLayoutManager(new LinearLayoutManager(CompartitActivity.this));
        RecercaAdapter recercaAdapterItemsCarpetes = new RecercaAdapter(itemSeleccionats, null, this);
        recyclerItemsCarpetes.setAdapter(recercaAdapterItemsCarpetes);

        recyclerUsuaris.setLayoutManager(new LinearLayoutManager(CompartitActivity.this));
        RecercaAdapter recercaAdapterUsuaris = new RecercaAdapter(null, usuaris, this);
        recyclerUsuaris.setAdapter(recercaAdapterUsuaris);

        titolsNoms.clear();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CompartitActivity.this, android.R.layout.simple_dropdown_item_1line, titolsNoms);
        aCTVCercarItemsCarpetes.setAdapter(adapter);

        /* *********************************** Ítems/Carpetes *********************************** */
        // Carregar ítems
        ItemDTO.RequestItem requestItem = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class);
        requestItem.getAllItems().enqueue(new Callback<ArrayList<Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    items = new ArrayList<>();
                    items.addAll(response.body());

                    // Cercador d'ítems
                    for (Item item : items) {
                        titolsNoms.add("🔑" + item.getTitol());
                    }
                    adapter.notifyDataSetChanged();

                } else {
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Item>> call, Throwable t) {
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });

        CarpetaDTO.RequestCarpeta requestCarpeta = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class);
        requestCarpeta.getAllCarpetes().enqueue(new Callback<ArrayList<Carpeta>>() {
            @Override
            public void onResponse(Call<ArrayList<Carpeta>> call, Response<ArrayList<Carpeta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    carpetes = new ArrayList<>();
                    carpetes.addAll(response.body());

                    // Cercador de carpetes
                    for (Carpeta carpeta : carpetes) {
                        titolsNoms.add("📁" + carpeta.getNom());
                    }
                    adapter.notifyDataSetChanged();

                } else {
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Carpeta>> call, Throwable t) {
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });

        // Cercador d'ítems/carpetes
        aCTVCercarItemsCarpetes.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                aCTVCercarItemsCarpetes.showDropDown();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        aCTVCercarItemsCarpetes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String seleccionat = parent.getItemAtPosition(position).toString();

                if (seleccionat.contains("🔑")) {
                    for (Item item : items) {
                        if (item.getTitol().equals(seleccionat)) {
                            itemSeleccionats.add(item);
                        }
                    }
                }

                if (seleccionat.contains("📁")) {
                    for (Carpeta carpeta : carpetes) {
                        if (carpeta.getNom().equals(seleccionat)) {
                            carpetesSeleccionades.add(carpeta);
                        }
                    }
                }

                recercaAdapterItemsCarpetes.notifyDataSetChanged();
                recyclerItemsCarpetes.setAdapter(recercaAdapterItemsCarpetes);
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

        btnCompartirItemsCarpetes.setText("Afegir carpeta");
        btnCompartirItemsCarpetes.setOnClickListener(c -> {
            // TODO que se guarde la carpeta
            String nomCarpeta = "etNomCarpeta.getText().toString()";
            boolean isFavorit = false;//favActual.get();

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
                                Call<Item> callAddItem = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).afegirItemCarpeta(carpetaCreada.getUuid().toString(), itemSeleccionats.get(i).getUuid().toString());
                                callAddItem.enqueue(new Callback<Item>() {
                                    @Override
                                    public void onResponse(Call<Item> callAddItem, Response<Item> response) {
                                        if (response.isSuccessful()) {
                                            Log.d("ITEMS_AFEGITS", itemSeleccionats.toString());
                                        } else {
                                            Log.d("ERROR_RESPONSE", response.message());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Item> callAddItem, Throwable t) {
                                        Log.d("ERROR_FAILURE", t.getMessage());
                                    }
                                });
                            }
                        }

                        if (usuarisSeleccionats.size() > 0) {
                            for (int i = 0; i < usuarisSeleccionats.size(); i++) {
                                // TODO compartir la carpeta
                                //Call<Compartit> callAddUsuari = CompartitDTO.obtenirJSONCompartit().create(CompartitDTO.RequestCompartit.class).compartirCarpeta(carpetaCreada.getUuid().toString(), );
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