package com.example.keyly_projecte_intermodular;

import static com.example.keyly_projecte_intermodular.config.TokenForEver.privateKeyDecrypt;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.desencriptarDataKey;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.encriptarDataKey;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.stringToPublicKey;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.example.keyly_projecte_intermodular.dao.EncryptedDataKey;
import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.dto.CarpetaDTO;
import com.example.keyly_projecte_intermodular.dto.CompartitDTO;
import com.example.keyly_projecte_intermodular.dto.ItemDTO;
import com.example.keyly_projecte_intermodular.dto.UsuariDTO;
import com.example.keyly_projecte_intermodular.request.CompartitRequest;
import com.example.keyly_projecte_intermodular.request.UsuariCompartitRequest;
import com.example.keyly_projecte_intermodular.resources.CarpetaAdapter;
import com.example.keyly_projecte_intermodular.resources.RecercaAdapter;
import com.example.keyly_projecte_intermodular.utils.Permisos;
import com.example.keyly_projecte_intermodular.utils.TipusEntitat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarpetesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout layoutError;
    private CarpetaAdapter carpetesAdapter;
    private EditText etCercar;
    private ImageView btnFiltres, btnAfegirCarpeta;
    private BottomNavigationView menu;
    private boolean filtrat = false;
    private Carpeta carpetaCreada;
    private ArrayList<Carpeta> carpetes = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Item> itemsSeleccionats = new ArrayList<>();
    private ArrayList<Usuari> usuaris = new ArrayList<>(), usuarisSeleccionats = new ArrayList<>();
    private ArrayList<UsuariCompartitRequest> usuarisCompartitRequest = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_carpetes);
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
                Intent intent = new Intent(this, PerfilActivity.class);
                intent.putExtra("usuariPropi", true);
                startActivity(intent);
                return true;
            }
            return false;
        });

        recyclerView = findViewById(R.id.recyclerCarpetes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Mostrar carpetes
        obtenirDades();
        actulitzarCarpetes(carpetes);

        etCercar = findViewById(R.id.aCTVCercarItems);
        etCercar.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrar carpetes
                String nomCarpeta = s.toString();
                resultatsCerca(nomCarpeta);
            }
        });

        btnAfegirCarpeta = findViewById(R.id.imgBtnAgefirCarpeta);
        btnAfegirCarpeta.setOnClickListener(v -> {
            itemsSeleccionats.clear();
            usuarisSeleccionats.clear();

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
            llAfegirItems.setOnClickListener(c -> {
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
            llAfegirUsuaris.setOnClickListener(c -> {
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

            recyclerItems.setLayoutManager(new LinearLayoutManager(CarpetesActivity.this));
            RecercaAdapter recercaAdapterItems = new RecercaAdapter(itemsSeleccionats, null, null, this);
            recyclerItems.setAdapter(recercaAdapterItems);

            recyclerUsuaris.setLayoutManager(new LinearLayoutManager(CarpetesActivity.this));
            RecercaAdapter recercaAdapterUsuaris = new RecercaAdapter(null, null, usuarisSeleccionats, this);
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

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(CarpetesActivity.this, android.R.layout.simple_dropdown_item_1line, titols);
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
                        if (item.getTitol().equals(seleccionat) && !itemsSeleccionats.contains(item)) {
                            itemsSeleccionats.add(item);
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

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(CarpetesActivity.this, android.R.layout.simple_dropdown_item_1line, noms);
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
                    for (Usuari usuari : usuaris) {
                        if (usuari.getNom().equals(seleccionat) && !usuarisSeleccionats.contains(usuari)) {
                            usuarisSeleccionats.add(usuari);
                        }
                    }
                    recercaAdapterUsuaris.notifyDataSetChanged();
                    recyclerUsuaris.setAdapter(recercaAdapterUsuaris);
                }
            });
            /* ************************************************************************************* */

            btnGuardarCarpeta.setText("Afegir carpeta");
            btnGuardarCarpeta.setOnClickListener(c -> {
                String nomCarpeta = etNomCarpeta.getText().toString();
                boolean isFavorit = favActual.get();

                Carpeta carpeta = new Carpeta(nomCarpeta, isFavorit);

                Call<Carpeta> call = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).crearCarpeta(carpeta);
                call.enqueue(new Callback<Carpeta>() {
                    @Override
                    public void onResponse(Call<Carpeta> call, Response<Carpeta> response) {
                        if (response.isSuccessful()) {
                            carpetaCreada = response.body();
                            Toast.makeText(CarpetesActivity.this, "Carpeta " + nomCarpeta + " afegida", Toast.LENGTH_SHORT).show();
                            if (usuarisSeleccionats.size() > 0) {
                                usuarisCompartitRequest.clear();
                                ArrayList<EncryptedDataKey> encryptedDataKeysC = new ArrayList<>();
                                for (Usuari usuari : usuarisSeleccionats) {
                                    usuarisCompartitRequest.add(new UsuariCompartitRequest(
                                            usuari.getUuid(),
                                            Permisos.LECTURA,
                                            encryptedDataKeysC));
                                }
                                try {
                                    compartirCarpeta(carpetaCreada);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            } else if (itemsSeleccionats.size() > 0) {
                                for (int i = 0; i < itemsSeleccionats.size(); i++) {
                                    Call<Item> callAddItem = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).afegirItemCarpeta(carpetaCreada.getUuid().toString(), itemsSeleccionats.get(i).getUuid().toString());
                                    callAddItem.enqueue(new Callback<Item>() {
                                        @Override
                                        public void onResponse(Call<Item> callAddItem, Response<Item> response) {
                                            if (response.isSuccessful()) {
                                                Log.e("ITEMS_AFEGIT_CARPETES", response.body().toString());
                                                Log.d("ITEMS_AFEGITS", itemsSeleccionats.toString());

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
                            alertDialog.dismiss();
                            obtenirDades();
                            actulitzarCarpetes(carpetes);
                        } else {
                            Toast.makeText(CarpetesActivity.this, "No s'ha pogut afegir la carpeta " + nomCarpeta , Toast.LENGTH_SHORT).show();
                            Log.d("ERROR_RESPONSE", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Carpeta> call, Throwable t) {
                        Toast.makeText(CarpetesActivity.this, "No s'ha pogut eliminar la carpeta " + nomCarpeta, Toast.LENGTH_SHORT).show();
                        Log.d("ERROR_FAILURE", t.getMessage());
                    }
                });
            });

            btnCancelar.setOnClickListener(c -> {
                alertDialog.dismiss();
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenirDades();
        actulitzarCarpetes(carpetes);
    }

//          for (Item item : itemSeleccionats) {
//            Log.d("ITEM_ACTUAL_CARPETA", item.toString());
//            for (Usuari usuari : usuarisSeleccionats) {
//                // TODO desencriptar datakey
//                byte[] dataKeyDecrypted = null;
//                byte[] dataKeyEncrypted = null;
//                try {
//                    dataKeyDecrypted = desencriptarDataKey(privateKeyDecrypt, item.getEncryptedDataKey().getEncryptedDataKey());
//                    dataKeyEncrypted = encriptarDataKey(stringToPublicKey(usuari.getPublicKey()), dataKeyDecrypted);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//
//                String encryptedDataKeyBase64 = Base64.encodeToString(dataKeyEncrypted, Base64.DEFAULT);
//                EncryptedDataKey edk = new EncryptedDataKey(null, encryptedDataKeyBase64);
//                encryptedDataKeys.add(edk);
//            }

    private void compartirCarpeta(Carpeta carpeta) {

        Log.d("CARPETA_CREADA_COMPARTIR", carpeta.toString());

        CompartitRequest compartitRequestC = new CompartitRequest(carpeta.getUuid(), TipusEntitat.CARPETA, usuarisCompartitRequest);

        Call<Void> callC = CompartitDTO.obtenirJSONCompartit().create(CompartitDTO.RequestCompartit.class).compartir(compartitRequestC);
        callC.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.e("CARPETA_COMPARTIDA", "Carpeta " + carpeta.getNom() + " compartida");
                    if (itemsSeleccionats.size() > 0) {
                        usuarisCompartitRequest.clear();
                        for (Item item : itemsSeleccionats) {
                            Call<Item> callAddItem = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).afegirItemCarpeta(carpetaCreada.getUuid().toString(), item.getUuid().toString());
                            callAddItem.enqueue(new Callback<Item>() {
                                @Override
                                public void onResponse(Call<Item> callAddItem, Response<Item> response) {
                                    if (response.isSuccessful()) {
                                        Log.e("ITEMS_AFEGIT_CARPETES", response.body().toString());
                                        Log.d("ITEMS_AFEGITS", itemsSeleccionats.toString());

                                        ArrayList<EncryptedDataKey> encryptedDataKeysI = new ArrayList<>();

                                        for (Usuari usuari : usuarisSeleccionats) {
                                            // TODO desencriptar datakey
                                            byte[] dataKeyDecrypted = null;
                                            byte[] dataKeyEncrypted = null;
                                            try {
                                                dataKeyDecrypted = desencriptarDataKey(privateKeyDecrypt, item.getEncryptedDataKey().getEncryptedDataKey());
                                                dataKeyEncrypted = encriptarDataKey(stringToPublicKey(usuari.getPublicKey()), dataKeyDecrypted);
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }

                                            String encryptedDataKeyBase64 = Base64.encodeToString(dataKeyEncrypted, Base64.DEFAULT);
                                            EncryptedDataKey edk = new EncryptedDataKey(null, encryptedDataKeyBase64);
                                            encryptedDataKeysI.add(edk);
                                        }

                                        for (Usuari usuari : usuarisSeleccionats) {
                                            usuarisCompartitRequest.add(new UsuariCompartitRequest(
                                                    usuari.getUuid(),
                                                    Permisos.LECTURA,
                                                    encryptedDataKeysI));
                                        }

                                        try {
                                            compartirItem(item);
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
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
                } else {
                    Log.e("ERROR_RESPONSE", response.message());
                    try {
                        Log.e("ERROR_BODY_RESPONSE", response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    private void compartirItem(Item item) {
        CompartitRequest compartitRequestI = new CompartitRequest(item.getUuid(), TipusEntitat.ITEM, usuarisCompartitRequest);
        Call<Void> callI = CompartitDTO.obtenirJSONCompartit().create(CompartitDTO.RequestCompartit.class).compartir(compartitRequestI);
        callI.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> callI, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.e("CARPETA_COMPARTIDA", "Ítem " + item.getTitol() + " compartit");
                } else {
                    Log.e("ERROR_RESPONSE", response.message());
                    try {
                        Log.e("ERROR_BODY_RESPONSE", response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> callI, Throwable t) {
                Log.e("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    private void resultatsCerca(String nomCarpeta) {
        ArrayList<Carpeta> llistaFiltradaCarpetes = new ArrayList<>();

        for (Carpeta carpeta : carpetes) {
            // Comporovar si coincideix algún nom amb la el títol del ítem
            if (carpeta.getNom().toLowerCase().contains(nomCarpeta.toLowerCase())) {
                llistaFiltradaCarpetes.add(carpeta);
            }
        }

        actulitzarCarpetes(llistaFiltradaCarpetes);
    }

    private void obtenirDades() {
        CarpetaDTO.RequestCarpeta resquestCarpeta = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class);
        resquestCarpeta.getAllCarpetes().enqueue(new Callback<ArrayList<Carpeta>>() {

            @Override
            public void onResponse(Call<ArrayList<Carpeta>> call, Response<ArrayList<Carpeta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    carpetes.clear();
                    carpetes.addAll(response.body());
                    carpetesAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    Log.d("ERROR_RESPONSE", response.message());
                }

            }

            @Override
            public void onFailure(Call<ArrayList<Carpeta>> call, Throwable t) {
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    private void actulitzarCarpetes(ArrayList<Carpeta> carpetes) {
        carpetesAdapter = new CarpetaAdapter(carpetes, carpeta -> {
            Intent intent = new Intent(this, CarpetaActivity.class);
            intent.putExtra("carpeta", carpeta);
            intent.putExtra("uuid", carpeta.getUuid().toString());
            intent.putExtra("nom", carpeta.getNom());
            intent.putExtra("favorit", carpeta.isFavorit());
            intent.putExtra("items", new ArrayList<>(carpeta.getItems()));
            intent.putExtra("data_creacio", carpeta.getDataCreacio());
            startActivity(intent);
        });
        recyclerView.setAdapter(carpetesAdapter);
    }
}