package com.example.keyly_projecte_intermodular;

import static com.example.keyly_projecte_intermodular.gestions.GestionsCarpetes.crearCarpeta;
import static com.example.keyly_projecte_intermodular.gestions.GestionsCompartits.obtenirCompartits;
import static com.example.keyly_projecte_intermodular.utils.LogOutService.logOut;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.keyly_projecte_intermodular.request.UsuariCompartitRequest;
import com.example.keyly_projecte_intermodular.adapters.CompartitAdapter;
import com.example.keyly_projecte_intermodular.gestions.GestionsIdiomes;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class CompartitActivity extends AppCompatActivity {

    private BottomNavigationView menu;
    private RecyclerView recyclerView;
    private ImageView imgBtnFiltres;
    private ImageButton imgBtnAjuda, imgBtnIdioma, imgBtnLogOut, imgBtnCompartir,
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
        obtenirCompartits(CompartitActivity.this, compartits, 0, false,
                compartitAdapter, recyclerView, true, null, null,
                null, null);

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
                        obtenirCompartits(CompartitActivity.this, compartits, 0, true,
                                compartitAdapter, recyclerView, true, null, null,
                                null, null);
                    } else {
                        obtenirCompartits(CompartitActivity.this, compartits, 0, false,
                                compartitAdapter, recyclerView, true, null, null,
                                null, null);
                    }
                } else if (cbUltimsUsats.isChecked()) { // Mostrar els últims ítems utilitzats
                    if (cbFavorits.isChecked()) { // Mostrar els ítems favorits
                        obtenirCompartits(CompartitActivity.this, compartits, 1, true,
                                compartitAdapter, recyclerView, true, null, null,
                                null, null);
                    } else {
                        obtenirCompartits(CompartitActivity.this, compartits, 1, false,
                                compartitAdapter, recyclerView, true, null, null,
                                null, null);
                    }
                } else if (cbMesUsats.isChecked()) { // Mostrar els ítems més usats
                    if (cbFavorits.isChecked()) { // Mostrar els ítems favorits
                        obtenirCompartits(CompartitActivity.this, compartits, 2, true,
                                compartitAdapter, recyclerView, true, null, null,
                                null, null);
                    } else {
                        obtenirCompartits(CompartitActivity.this, compartits, 2, false,
                                compartitAdapter, recyclerView, true, null, null,
                                null, null);
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
                    () -> obtenirCompartits(CompartitActivity.this, compartits, 0, false,
                            compartitAdapter, recyclerView, true, null, null,
                            null, null),
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
        obtenirCompartits(CompartitActivity.this, compartits, 0, false,
                compartitAdapter, recyclerView, true, null, null,
                null, null);
    }
}