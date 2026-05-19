package com.example.keyly_projecte_intermodular;

import static com.example.keyly_projecte_intermodular.resources.Varis.privateKeyDecrypt;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.desencriptarDataKey;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.encriptarDataKey;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.stringToPublicKey;
import static com.example.keyly_projecte_intermodular.utils.GestionsCompartits.obtenirUsuaris;
import static com.example.keyly_projecte_intermodular.utils.LogOutService.logOut;

import android.content.Context;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
import com.example.keyly_projecte_intermodular.adapters.ItemAdapter;
import com.example.keyly_projecte_intermodular.adapters.RecercaAdapter;
import com.example.keyly_projecte_intermodular.utils.GestionsIdiomes;
import com.example.keyly_projecte_intermodular.utils.Permisos;
import com.example.keyly_projecte_intermodular.utils.TipusEntitat;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarpetaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout layoutError;
    private ItemAdapter itemAdapter;
    private TextView nomCarpeta, dataCreacio;
    private EditText etCercar;
    private ImageView imgBtnFiltres;
    private ImageButton imgBtnCompartir, imgBtnIdioma, imgBtnLogOut, imgBtnStar, imgBtnEditar, imgBtnEliminar, imgBtnBack, imgBtnAfegirItem;
    private int itemAfegit = 0;
    private String uuid;
    private boolean filtrat = false;
    private Carpeta carpetaActual;
    private ArrayList<Item> items, totalItems = new ArrayList<>(), itemsSeleccionats = new ArrayList<>();
    private ArrayList<Usuari> usuaris = new ArrayList<>(), usuarisSeleccionats = new ArrayList<>(),
        usuarisActuals = new ArrayList<>(), getUsuarisActualsSeleccionats = new ArrayList<>();
    private ArrayList<UsuariCompartitRequest> usuarisCompartitRequest = new ArrayList<>();
    private ArrayList<String> permisos = new ArrayList<>(), permisosActuals = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(GestionsIdiomes.aplicarIdioma(newBase));
    }

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

        imgBtnCompartir = findViewById(R.id.imgBtnCompartir);

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

        uuid = getIntent().getStringExtra("uuid");
        String nom = getIntent().getStringExtra("nom");

        carpetaActual = new Carpeta(UUID.fromString(uuid), nom);

        boolean favorit = getIntent().getBooleanExtra("favorit", false);
        String data_creacio = getIntent().getStringExtra("data_creacio");

        items = (ArrayList<Item>) getIntent().getSerializableExtra("items");
        Log.e("ITEMS_CARPETA" + nom, items.toString());

        AtomicBoolean favActual = new AtomicBoolean(favorit);

        // Formatejar data
        String dataFormatejada = formatDataCreacio(data_creacio);
        Log.d("DATA", dataFormatejada);

        nomCarpeta = findViewById(R.id.nomCarpeta);
        nomCarpeta.setText(nom);

        dataCreacio = findViewById(R.id.dataCreacio);
        // TODO hacer que muestre hace cuanto se creó/editó la carpeta
        dataCreacio.setText(data_creacio);

        imgBtnStar = findViewById(R.id.imgBtnStar);
        if (favActual.get()) {
            imgBtnStar.setImageResource(R.drawable.filled_star);
        } else {
            imgBtnStar.setImageResource(R.drawable.star);
        }
        imgBtnStar.setOnClickListener(v -> {
            // TODO hacer que se guarde el favoritos
            if (favActual.get()) {
                imgBtnStar.setImageResource(R.drawable.star);
                favActual.set(false);
            } else {
                imgBtnStar.setImageResource(R.drawable.filled_star);
                favActual.set(true);
            }
        });

        imgBtnEditar = findViewById(R.id.imgBtnEdit);
        imgBtnEditar.setOnClickListener(v -> {
            // TODO editar la carpeta
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_carpeta_editar, null);

            builder.setView(view);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Elements del AlertDialog
            LinearLayout llNomCarpeta = view.findViewById(R.id.llNomCarpeta);
            llNomCarpeta.setVisibility(View.VISIBLE);

            View vSeparador = view.findViewById(R.id.vSeparador1);
            vSeparador.setVisibility(View.GONE);

            LinearLayout llAfegirItems = view.findViewById(R.id.llDesplegableItems);
            llAfegirItems.setVisibility(View.GONE);

            View vSeparador2 = view.findViewById(R.id.vSeparador2);
            vSeparador2.setVisibility(View.GONE);

            LinearLayout llAfegirUsuaris = view.findViewById(R.id.llDesplegableUsuaris);
            llAfegirUsuaris.setVisibility(View.GONE);

            View vSeparador3 = view.findViewById(R.id.vSeparador3);
            vSeparador3.setVisibility(View.GONE);

            ImageButton imgBtnStarEdit = view.findViewById(R.id.imgBtnStar);
            EditText etNomCarpeta = view.findViewById(R.id.etNomCarpeta);
            Button btnGuardarCarpeta = view.findViewById(R.id.btnGuardarCarpeta);
            Button btnCancelar = view.findViewById(R.id.btnCancelar);

            etNomCarpeta.setText(nom);

            if (favActual.get()) {
                imgBtnStarEdit.setImageResource(R.drawable.filled_star);
            } else {
                imgBtnStarEdit.setImageResource(R.drawable.star);
            }
            imgBtnStarEdit.setOnClickListener(c -> {
                // TODO hacer que se guarde el favoritos
                if (favActual.get()) {
                    imgBtnStarEdit.setImageResource(R.drawable.star);
                    favActual.set(false);
                } else {
                    imgBtnStarEdit.setImageResource(R.drawable.filled_star);
                    favActual.set(true);
                }
            });

            btnGuardarCarpeta.setText(getString(R.string.btnGuardar));
            btnGuardarCarpeta.setOnClickListener(c -> {
                Carpeta carpeta = new Carpeta(etNomCarpeta.getText().toString(), favActual.get());

                Call<Carpeta> call = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).editarCarpeta(uuid, carpeta);
                call.enqueue(new Callback<Carpeta>() {
                    @Override
                    public void onResponse(Call<Carpeta> call, Response<Carpeta> response) {
                        if (response.isSuccessful()) {

                            nomCarpeta.setText(response.body().getNom());

                            AtomicBoolean favActual = new AtomicBoolean(response.body().isFavorit());

                            if (favActual.get()) {
                                imgBtnStar.setImageResource(R.drawable.filled_star);
                            } else {
                                imgBtnStar.setImageResource(R.drawable.star);
                            }
                            imgBtnStar.setOnClickListener(v -> {
                                // TODO hacer que se guarde el favoritos
                                if (favActual.get()) {
                                    imgBtnStar.setImageResource(R.drawable.star);
                                    favActual.set(false);
                                } else {
                                    imgBtnStar.setImageResource(R.drawable.filled_star);
                                    favActual.set(true);
                                }
                            });

                            // TODO borrar lo comentado si funciona
                            //Toast.makeText(CarpetaActivity.this, "Carpeta " + etNomCarpeta.getText().toString() + " editada", Toast.LENGTH_SHORT).show();
                            Toast.makeText(CarpetaActivity.this, getString(R.string.toastCarpetaEditada, etNomCarpeta.getText().toString()), Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(CarpetaActivity.this, getString(R.string.toastCarpetaNoEditada, etNomCarpeta.getText().toString()), Toast.LENGTH_SHORT).show();
                            Log.d("ERROR_RESPONSE", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Carpeta> call, Throwable t) {
                        Toast.makeText(CarpetaActivity.this, getString(R.string.toastCarpetaNoEditada, etNomCarpeta.getText().toString()), Toast.LENGTH_SHORT).show();
                        Log.d("ERROR_FAILURE",t.getMessage());
                    }
                });
            });

            btnCancelar.setOnClickListener(c -> {
                alertDialog.dismiss();
            });
        });

        imgBtnEliminar = findViewById(R.id.imgBtnEliminar);
        imgBtnEliminar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_eliminar, null);

            builder.setView(view);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Elements del AlertDialog
            TextView txtPregunta = view.findViewById(R.id.txtPregunta);
            Button btnEliminar = view.findViewById(R.id.btnEliminar);
            Button btnCancelar = view.findViewById(R.id.btnCancelar);

            txtPregunta.setText(getString(R.string.etiquetaEliminarCarpeta) + " " + nom + "\" ?");

            btnEliminar.setOnClickListener(e -> {
                boolean esCompartit = getIntent().getBooleanExtra("esCompartit", false);
                String uuidCompartit = getIntent().getStringExtra("uuidCompartit");
                if (esCompartit) {
                    Call<Void> call = CompartitDTO.obtenirJSONCompartit().create(CompartitDTO.RequestCompartit.class).eliminarCompartit(uuidCompartit);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                // TODO borrar lo comentado si funciona la traducción
                                //Toast.makeText(CarpetaActivity.this, "Carpeta " + nom + " eliminada", Toast.LENGTH_SHORT).show();
                                Toast.makeText(CarpetaActivity.this, getString(R.string.toastCarpetaEliminada, nom), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(CarpetaActivity.this, getString(R.string.toastCarpetaNoEliminada, nom), Toast.LENGTH_SHORT).show();
                                Log.d("ERROR_RESPONSE", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.d("ERROR_FAILURE", t.getMessage());
                            Toast.makeText(CarpetaActivity.this, getString(R.string.toastCarpetaNoEliminada, nom), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Call<Void> call = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).eliminarCarpeta(uuid);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                // TODO borrar lo comentado si funciona la traducción
                                //Toast.makeText(CarpetaActivity.this, "Carpeta " + nom + " eliminada", Toast.LENGTH_SHORT).show();
                                Toast.makeText(CarpetaActivity.this, getString(R.string.toastCarpetaEliminada, nom), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(CarpetaActivity.this, getString(R.string.toastCarpetaNoEliminada, nom), Toast.LENGTH_SHORT).show();
                                Log.d("ERROR_RESPONSE", response.message());
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.d("ERROR_FAILURE", t.getMessage());
                            Toast.makeText(CarpetaActivity.this, getString(R.string.toastCarpetaNoEliminada, nom) + nom, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            btnCancelar.setOnClickListener(c -> {
                alertDialog.dismiss();
            });
        });

        imgBtnBack = findViewById(R.id.imgBtnBack);
        imgBtnBack.setOnClickListener(v -> {
            finish();
        });

        imgBtnAfegirItem = findViewById(R.id.imgBtnAfegirItem);
        imgBtnAfegirItem.setOnClickListener(v -> {
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
            llNomCarpeta.setVisibility(View.GONE);

            View vSeparador = view.findViewById(R.id.vSeparador1);
            vSeparador.setVisibility(View.GONE);

            LinearLayout llAfegirItems = view.findViewById(R.id.llDesplegableItems);
            llAfegirItems.setVisibility(View.VISIBLE);
            LinearLayout llContingutItems = view.findViewById(R.id.llContigutItems);
            llContingutItems.setVisibility(View.GONE);

            View vSeparador2 = view.findViewById(R.id.vSeparador2);
            vSeparador2.setVisibility(View.VISIBLE);

            LinearLayout llAfegirUsuaris = view.findViewById(R.id.llDesplegableUsuaris);
            llAfegirUsuaris.setVisibility(View.VISIBLE);
            LinearLayout llContingutUsuaris = view.findViewById(R.id.llContigutCompartir);
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

            AutoCompleteTextView aCTVCercarItems = view.findViewById(R.id.aCTVCercarItems);
            AutoCompleteTextView aCTVCercarUsuaris = view.findViewById(R.id.aCTVCercarCompartir);
            RecyclerView recyclerItems = view.findViewById(R.id.recyclerItems);
            RecyclerView recyclerUsuaris = view.findViewById(R.id.recyclerCompartir);
            Button btnGuardarCarpeta = view.findViewById(R.id.btnGuardarCarpeta);
            Button btnCancelar = view.findViewById(R.id.btnCancelar);

            recyclerItems.setLayoutManager(new LinearLayoutManager(CarpetaActivity.this));
            RecercaAdapter recercaAdapterItems = new RecercaAdapter(itemsSeleccionats, null, null, null, this);
            recyclerItems.setAdapter(recercaAdapterItems);

            recyclerUsuaris.setLayoutManager(new LinearLayoutManager(CarpetaActivity.this));
            RecercaAdapter recercaAdapterUsuaris = new RecercaAdapter(null, null, usuarisSeleccionats, null, this);
            recyclerUsuaris.setAdapter(recercaAdapterUsuaris);

            /* *************************************** Ítems *************************************** */
            // Carregar ítems
            ItemDTO.RequestItem requestItem = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class);
            requestItem.getAllItems().enqueue(new Callback<ArrayList<Item>>() {
                @Override
                public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ArrayList<Item> todosLosItems = new ArrayList<>(response.body());

                        ArrayList<String> titols = new ArrayList<>();
                        for (Item item : todosLosItems) {
                            titols.add(item.getTitol());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                CarpetaActivity.this,
                                android.R.layout.simple_dropdown_item_1line,
                                titols
                        );
                        aCTVCercarItems.setAdapter(adapter);

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

                        // Actualitza el listener per utilitzar todosLosItems
                        aCTVCercarItems.setOnItemClickListener((parent, v, position, id) -> {
                            String seleccionat = parent.getItemAtPosition(position).toString();
                            for (Item item : todosLosItems) {
                                if (item.getTitol().equals(seleccionat) && !itemsSeleccionats.contains(item)) {
                                    itemsSeleccionats.add(item);
                                }
                            }
                            recercaAdapterItems.notifyDataSetChanged();
                            recyclerItems.setAdapter(recercaAdapterItems);
                        });
                    } else {
                        Log.d("ERROR_RESPONSE", response.message());
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Item>> call, Throwable t) {
                    Log.d("ERROR_FAILURE", t.getMessage());
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

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(CarpetaActivity.this, android.R.layout.simple_dropdown_item_1line, noms);
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


            permisos.clear();
            aCTVCercarUsuaris.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String seleccionat = parent.getItemAtPosition(position).toString();
                    for (Usuari usuari : usuaris) {
                        if (usuari.getNom().equals(seleccionat) && !usuarisSeleccionats.contains(usuari)) {
                            usuarisSeleccionats.add(usuari);
                            // Afegir permisos per defecte
                            permisos.add(Permisos.LECTURA.toString());
                        }
                    }
                    recercaAdapterUsuaris.notifyDataSetChanged();
                    recyclerUsuaris.setAdapter(recercaAdapterUsuaris);
                }
            });
            /* ************************************************************************************* */

            btnGuardarCarpeta.setText(getString(R.string.btnAfegirItems));
            btnGuardarCarpeta.setOnClickListener(c -> {

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
                        compartirCarpeta(carpetaActual);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else if (itemsSeleccionats.size() > 0) {
                    for (int i = 0; i < itemsSeleccionats.size(); i++) {
                        Call<Item> callAddItem = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).afegirItemCarpeta(carpetaActual.getUuid().toString(), itemsSeleccionats.get(i).getUuid().toString());
                        callAddItem.enqueue(new Callback<Item>() {
                            @Override
                            public void onResponse(Call<Item> callAddItem, Response<Item> response) {
                                if (response.isSuccessful()) {
                                    Log.e("ITEMS_AFEGIT_CARPETES", response.body().toString());
                                    Log.d("ITEMS_AFEGITS", itemsSeleccionats.toString());
                                    items.add(itemsSeleccionats.get(itemAfegit));
                                    if (itemAfegit == itemsSeleccionats.size() - 1) {
                                        runOnUiThread(() -> actualitzarItems(items, uuid));
                                    }
                                    itemAfegit++;
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
            });

            btnCancelar.setOnClickListener(c -> {
                alertDialog.dismiss();
            });
        });

        recyclerView = findViewById(R.id.recyclerItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        actualitzarItems(items, uuid);

        imgBtnCompartir.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_compartir_item, null);

            builder.setView(view);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Elements del AlertDialog
            LinearLayout llDesplegableUsuaris = view.findViewById(R.id.llDesplegableCompartir);
            AutoCompleteTextView aCTVCercarUsuaris = view.findViewById(R.id.aCTVCercarUsuaris);
            RecyclerView recyclerUsuaris = view.findViewById(R.id.recyclerUsuaris);

            LinearLayout llDesplegableCompartir = view.findViewById(R.id.llDesplegableCompartir);
            AutoCompleteTextView aCTVCercarCompartir = view.findViewById(R.id.aCTVCercarCompartir);
            RecyclerView recyclerCompartir = view.findViewById(R.id.recyclerCompartir);

            Button btnGuardar = view.findViewById(R.id.btnGuardarUsuaris);
            Button btnCancelar = view.findViewById(R.id.btnCancelar);

            // Mostrar els usuaris compartits
            llDesplegableUsuaris.setOnClickListener(c -> {
                if (llDesplegableCompartir.getVisibility() == View.VISIBLE) {
                    llDesplegableCompartir.setVisibility(View.GONE);
                    llDesplegableUsuaris.setVisibility(View.VISIBLE);
                } else if (llDesplegableCompartir.getVisibility() == View.GONE) {
                    if (llDesplegableUsuaris.getVisibility() == View.VISIBLE) {
                        llDesplegableUsuaris.setVisibility(View.GONE);
                    } else {
                        llDesplegableUsuaris.setVisibility(View.VISIBLE);
                    }
                }
            });

            // Mostrar el cercador d'usuaris per compartir
            llDesplegableCompartir.setOnClickListener(c -> {
                if (llDesplegableUsuaris.getVisibility() == View.VISIBLE) {
                    llDesplegableUsuaris.setVisibility(View.GONE);
                    llDesplegableCompartir.setVisibility(View.VISIBLE);
                } else if (llDesplegableUsuaris.getVisibility() == View.GONE) {
                    if (llDesplegableCompartir.getVisibility() == View.VISIBLE) {
                        llDesplegableCompartir.setVisibility(View.GONE);
                    } else {
                        llDesplegableCompartir.setVisibility(View.VISIBLE);
                    }
                }
            });

            permisos.clear();

            // Obtenir usuaris compartits
            recyclerUsuaris.setLayoutManager(new LinearLayoutManager(CarpetaActivity.this));
            RecercaAdapter recercaAdapterUsuaris = new RecercaAdapter(null, null,
                    getUsuarisActualsSeleccionats, permisosActuals, this);
            recyclerUsuaris.setAdapter(recercaAdapterUsuaris);

            obtenirUsuaris(usuarisActuals, getUsuarisActualsSeleccionats, permisosActuals, recyclerUsuaris,
                    recercaAdapterUsuaris, aCTVCercarCompartir, CarpetaActivity.this, true);

            // Cercar usuaris per compartir
            recyclerCompartir.setLayoutManager(new LinearLayoutManager(CarpetaActivity.this));
            RecercaAdapter recercaAdapterCompartir = new RecercaAdapter(null, null,
                    usuarisSeleccionats, permisos, this);
            recyclerCompartir.setAdapter(recercaAdapterCompartir);

            obtenirUsuaris(usuaris, usuarisSeleccionats, permisos, recyclerCompartir,
                    recercaAdapterCompartir, aCTVCercarCompartir, CarpetaActivity.this, false);
        });

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
                resultatsCerca(titolItem, uuid);
            }
        });

        imgBtnFiltres = findViewById(R.id.imgBtnFiltres);
        imgBtnFiltres.setOnClickListener(v -> {
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
                        actualitzarItems(itemsFav, uuid);
                    } else {
                        actualitzarItems(filtres, uuid);
                    }
                } else if (cbUltimsUsats.isChecked()) {
                    DateTimeFormatter formatter =
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
                    if (cbFavorits.isChecked()) {
                        ArrayList<Item> itemsFav = new ArrayList<>();
                        for (Item item : filtres) {
                            if (item.isFavorit()) {
                                itemsFav.add(item);
                            }
                        }
                        actualitzarItems(itemsFav, uuid);
                    } else {
                        actualitzarItems(filtres, uuid);
                    }
                } else if (cbMesUsats.isChecked()) {
                    filtres = items;
                    filtres.sort(Comparator.comparing(Item::getComptadorAccess).reversed());
                    if (cbFavorits.isChecked()) {
                        ArrayList<Item> itemsFav = new ArrayList<>();
                        for (Item item : filtres) {
                            if (item.isFavorit()) {
                                itemsFav.add(item);
                            }
                        }
                        actualitzarItems(itemsFav, uuid);
                    } else {
                        actualitzarItems(filtres, uuid);
                    }
                }

                alertDialog.dismiss();
            });

            // Cancelar filtres
            btnCancelar.setOnClickListener(c -> {
                alertDialog.dismiss();
            });

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualitzarItems(items, uuid);
    }

    private void compartirCarpeta(Carpeta carpeta) {
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
                            Call<Item> callAddItem = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).afegirItemCarpeta(carpetaActual.getUuid().toString(), item.getUuid().toString());
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

    private void resultatsCerca(String titolItem, String uuidCarpeta) {
        ArrayList<Item> llistaFiltradaItems = new ArrayList<>();

        for (Item item : items) {
            // Comporovar si coincideix algún nom amb la el títol del ítem
            if (item.getTitol().toLowerCase().contains(titolItem.toLowerCase())) {
                llistaFiltradaItems.add(item);
            }
        }

        actualitzarItems(llistaFiltradaItems, uuidCarpeta);
    }

    private void actualitzarItems(ArrayList<Item> items, String uuidCarpeta) {
        itemAdapter = new ItemAdapter(items, uuidCarpeta, item -> {
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
            intent.putExtra("item_carpeta", true);
            intent.putExtra("uuidCarpeta", uuidCarpeta);
            startActivity(intent);
        }, CarpetaActivity.this);
        recyclerView.setAdapter(itemAdapter);
    }
}