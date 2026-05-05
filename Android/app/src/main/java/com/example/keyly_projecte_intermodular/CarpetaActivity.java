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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.dto.CarpetaDTO;
import com.example.keyly_projecte_intermodular.dto.ItemDTO;
import com.example.keyly_projecte_intermodular.resources.ItemAdapter;
import com.example.keyly_projecte_intermodular.resources.RecercaAdapter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
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
    private ImageButton imgBtnStar, imgBtnEditar, imgBtnEliminar, imgBtnBack, imgBtnAfegirItem;
    private String uuid;
    private boolean filtrat = false;
    private ArrayList<Item> items, totalItems = new ArrayList<>(), itemSeleccionats = new ArrayList<>();

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

        uuid = getIntent().getStringExtra("uuid");
        String nom = getIntent().getStringExtra("nom");
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

            btnGuardarCarpeta.setText("Guardar carpeta");
            btnGuardarCarpeta.setOnClickListener(c -> {
                Carpeta carpeta = new Carpeta(etNomCarpeta.getText().toString(), favActual.get());

                Call<Carpeta> call = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).editarCarpeta(uuid, carpeta);
                call.enqueue(new Callback<Carpeta>() {
                    @Override
                    public void onResponse(Call<Carpeta> call, Response<Carpeta> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(CarpetaActivity.this, "Carpeta " + etNomCarpeta.getText().toString() + " editada", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(CarpetaActivity.this, "No s'ha pogut editar la carpeta", Toast.LENGTH_SHORT).show();
                            Log.d("ERROR_RESPONSE", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Carpeta> call, Throwable t) {
                        Toast.makeText(CarpetaActivity.this, "No s'ha pogut editar la carpeta", Toast.LENGTH_SHORT).show();
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

            txtPregunta.setText("Desitja eliminar la carpeta \"" + nom + "\" ?");

            btnEliminar.setOnClickListener(e -> {
                Call<Void> call = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).eliminarCarpeta(uuid);
                call.enqueue(new Callback<Void>() {
                   @Override
                   public void onResponse(Call<Void> call, Response<Void> response) {
                       if (response.isSuccessful()) {
                           Toast.makeText(CarpetaActivity.this, "Carpeta " + nom + " eliminada", Toast.LENGTH_SHORT).show();
                           finish();
                       } else {
                           Toast.makeText(CarpetaActivity.this, "No s'ha pogut eliminar la carpeta " + nom, Toast.LENGTH_SHORT).show();
                           Log.d("ERROR_RESPONSE", response.message());
                       }
                   }
                   @Override
                   public void onFailure(Call<Void> call, Throwable t) {
                       Log.d("ERROR_FAILURE", t.getMessage());
                       Toast.makeText(CarpetaActivity.this, "No s'ha pogut eliminar la carpeta " + nom, Toast.LENGTH_SHORT).show();
                   }
                });
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

            View vSeparador2 = view.findViewById(R.id.vSeparador2);
            vSeparador2.setVisibility(View.VISIBLE);

            LinearLayout llAfegirUsuaris = view.findViewById(R.id.llDesplegableUsuaris);
            llAfegirUsuaris.setVisibility(View.VISIBLE);

            View vSeparador3 = view.findViewById(R.id.vSeparador3);
            vSeparador3.setVisibility(View.VISIBLE);

            AutoCompleteTextView aCTVCercar = view.findViewById(R.id.aCTVCercarItems);
            RecyclerView recyclerItems = view.findViewById(R.id.recyclerItems);
            Button btnGuardarCarpeta = view.findViewById(R.id.btnGuardarCarpeta);
            Button btnCancelar = view.findViewById(R.id.btnCancelar);

            recyclerItems.setLayoutManager(new LinearLayoutManager(CarpetaActivity.this));
            RecercaAdapter recercaAdapter = new RecercaAdapter(itemSeleccionats, null);
            recyclerItems.setAdapter(recercaAdapter);

            // Carregar ítems
            ItemDTO.RequestItem requestItem = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class);
            requestItem.getAllItems().enqueue(new Callback<ArrayList<Item>>() {
                @Override
                public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        totalItems = new ArrayList<>();
                        totalItems.addAll(response.body());
                        //Log.d("ITEMS_CARPETA", items.toString());

                        // Cercador d'ítems
                        ArrayList<String> titols = new ArrayList<>();

                        for (Item item : totalItems) {
                            titols.add(item.getTitol());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(CarpetaActivity.this, android.R.layout.simple_dropdown_item_1line, titols);
                        aCTVCercar.setAdapter(adapter);
                        aCTVCercar.setThreshold(1);
                    } else {
                        Log.d("ERROR_RESPONSE", response.message());
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Item>> call, Throwable t) {
                    Log.d("ERROR_FAILURE", t.getMessage());
                }
            });

            aCTVCercar.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    aCTVCercar.showDropDown();
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
            });

            aCTVCercar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String seleccionat = parent.getItemAtPosition(position).toString();

                    for (Item item : totalItems) {
                        if (item.getTitol().equals(seleccionat)) {
                            itemSeleccionats.add(item);
                        }
                    }

                    recercaAdapter.notifyDataSetChanged();
                    recyclerItems.setAdapter(recercaAdapter);
                }
            });

            btnGuardarCarpeta.setText("Afegir ítems");
            btnGuardarCarpeta.setOnClickListener(c -> {
                for (int i = 0; i < itemSeleccionats.size(); i++) {
                    Call<Carpeta> callAddItem = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).afegirItemCarpeta(uuid, itemSeleccionats.get(i).getUuid().toString());
                    callAddItem.enqueue(new Callback<Carpeta>() {
                        @Override
                        public void onResponse(Call<Carpeta> callAddItem, Response<Carpeta> response) {
                            if (response.isSuccessful()) {
                                Log.d("ITEMS_AFEGITS", itemSeleccionats.toString());
                                alertDialog.dismiss();
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
            });

            btnCancelar.setOnClickListener(c -> {
                alertDialog.dismiss();
            });
        });

        recyclerView = findViewById(R.id.recyclerItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        actulitzarItems(items, uuid);

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

                actulitzarItems(itemsFiltrats, uuid);

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
        actulitzarItems(items, uuid);
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

        actulitzarItems(llistaFiltradaItems, uuidCarpeta);
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

    private void actulitzarItems(ArrayList<Item> items, String uuidCarpeta) {
        itemAdapter = new ItemAdapter(items, uuidCarpeta, item -> {
            Intent intent = new Intent(this, ItemActivity.class);
            intent.putExtra("uuid", item.getUuid());
            intent.putExtra("title", item.getTitol());
            intent.putExtra("url", item.getUrl());
            intent.putExtra("propietari", item.getNomUsuari());
            intent.putExtra("password", item.getContrasenya());
            intent.putExtra("notes", item.getNotes());
            intent.putExtra("fav", item.isFavorit());
            startActivity(intent);
        }, CarpetaActivity.this);
        recyclerView.setAdapter(itemAdapter);
    }
}