package com.example.keyly_projecte_intermodular;

import static com.example.keyly_projecte_intermodular.gestions.GestionsCarpetes.actualitzarCarpetes;
import static com.example.keyly_projecte_intermodular.gestions.GestionsCarpetes.crearCarpeta;
import static com.example.keyly_projecte_intermodular.gestions.GestionsCarpetes.obtenirCarpetes;
import static com.example.keyly_projecte_intermodular.utils.LogOutService.logOut;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.TextView;

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
import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.request.UsuariCompartitRequest;
import com.example.keyly_projecte_intermodular.adapters.CarpetaAdapter;
import com.example.keyly_projecte_intermodular.gestions.GestionsIdiomes;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class CarpetesActivity extends AppCompatActivity {

    private View includeLogsCarpetes;
    private RecyclerView recyclerView;
    private LinearLayout layoutError;
    private CarpetaAdapter carpetesAdapter;
    private TextView txtTitolError, txtDescripcioError;
    private EditText etCercar;
    private ImageView imgVError, btnFiltres, btnAfegirCarpeta;
    private ImageButton imgBtnAjuda, imgBtnIdioma, imgBtnLogOut;
    private BottomNavigationView menu;
    private int posItemCompartit = 0;
    private static int filtreActual = 0;
    private boolean filtrat = false;
    private Carpeta carpetaCreada;
    private ArrayList<Carpeta> carpetes = new ArrayList<>(), carpetesFiltrades = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Item> itemsSeleccionats = new ArrayList<>();
    private ArrayList<Usuari> usuaris = new ArrayList<>(), usuarisSeleccionats = new ArrayList<>();
    private ArrayList<UsuariCompartitRequest> usuarisCompartitRequest = new ArrayList<>();
    private ArrayList<String> permisos = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(GestionsIdiomes.aplicarIdioma(newBase));
    }

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

        includeLogsCarpetes = findViewById(R.id.includeLogsCarpetes);
        layoutError = (LinearLayout) includeLogsCarpetes;
        txtTitolError = includeLogsCarpetes.findViewById(R.id.txtTitolError);
        txtDescripcioError = includeLogsCarpetes.findViewById(R.id.txtDescripcioError);
        imgVError = includeLogsCarpetes.findViewById(R.id.imgVError);
        recyclerView = findViewById(R.id.recyclerCarpetes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        recyclerView = findViewById(R.id.recyclerCarpetes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Mostrar carpetes
        obtenirCarpetes(
                carpetes,
                recyclerView,
                filtreActual,
                false,
                layoutError,
                txtTitolError,
                txtDescripcioError,
                imgVError,
                carpetesAdapter,
                CarpetesActivity.this
        );

        btnFiltres = findViewById(R.id.imgBtnFiltresAmbMi);
        btnFiltres.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_filtres, null);

            builder.setView(view);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Elements del AlertDialog

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
                        //obtenirDades(0, true);
                        obtenirCarpetes(
                                carpetes,
                                recyclerView,
                                0,
                                true,
                                layoutError,
                                txtTitolError,
                                txtDescripcioError,
                                imgVError,
                                carpetesAdapter,
                                CarpetesActivity.this
                        );
                    } else {
                        obtenirCarpetes(
                                carpetes,
                                recyclerView,
                                0,
                                false,
                                layoutError,
                                txtTitolError,
                                txtDescripcioError,
                                imgVError,
                                carpetesAdapter,
                                CarpetesActivity.this
                        );
                    }
                } else if (cbUltimsUsats.isChecked()) {
                    if (cbFavorits.isChecked()) {
                        //obtenirDades(2, true);
                        obtenirCarpetes(
                                carpetes,
                                recyclerView,
                                2,
                                true,
                                layoutError,
                                txtTitolError,
                                txtDescripcioError,
                                imgVError,
                                carpetesAdapter,
                                CarpetesActivity.this
                        );
                    } else {
                        //obtenirDades(2, false);
                        obtenirCarpetes(
                                carpetes,
                                recyclerView,
                                2,
                                false,
                                layoutError,
                                txtTitolError,
                                txtDescripcioError,
                                imgVError,
                                carpetesAdapter,
                                CarpetesActivity.this
                        );
                    }
                } else if (cbMesUsats.isChecked()) {
                    if (cbFavorits.isChecked()) {
                        //obtenirDades(1, true);
                        obtenirCarpetes(
                                carpetes,
                                recyclerView,
                                1,
                                true,
                                layoutError,
                                txtTitolError,
                                txtDescripcioError,
                                imgVError,
                                carpetesAdapter,
                                CarpetesActivity.this
                        );
                    } else {
                        //obtenirDades(1, false);
                        obtenirCarpetes(
                                carpetes,
                                recyclerView,
                                1,
                                false,
                                layoutError,
                                txtTitolError,
                                txtDescripcioError,
                                imgVError,
                                carpetesAdapter,
                                CarpetesActivity.this
                        );
                    }
                }

                alertDialog.dismiss();
            });

            // Cancelar filtres
            btnCancelar.setOnClickListener(c -> {
                alertDialog.dismiss();
            });

        });

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
            crearCarpeta(
                    itemsSeleccionats,
                    usuarisSeleccionats,
                    CarpetesActivity.this,
                    items,
                    usuaris,
                    usuarisCompartitRequest,
                    carpetaCreada,
                    carpetes,
                    recyclerView,
                    carpetesAdapter,
                    false,
                    //() -> obtenirDades(0, false),
                    () -> obtenirCarpetes(
                                carpetes,
                                recyclerView,
                                0,
                                false,
                                layoutError,
                                txtTitolError,
                                txtDescripcioError,
                                imgVError,
                                carpetesAdapter,
                                CarpetesActivity.this
                            ),
                    layoutError,
                    txtTitolError,
                    txtDescripcioError,
                    imgVError
            );
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //obtenirDades(filtreActual, false);
        obtenirCarpetes(
                carpetes,
                recyclerView,
                0,
                false,
                layoutError,
                txtTitolError,
                txtDescripcioError,
                imgVError,
                carpetesAdapter,
                CarpetesActivity.this
        );
    }

    private void resultatsCerca(String nomCarpeta) {
        ArrayList<Carpeta> llistaFiltradaCarpetes = new ArrayList<>();

        for (Carpeta carpeta : carpetesFiltrades) {
            // Comporovar si coincideix algún nom amb la el títol del ítem
            if (carpeta.getNom().toLowerCase().contains(nomCarpeta.toLowerCase())) {
                llistaFiltradaCarpetes.add(carpeta);
            }
        }

        actualitzarCarpetes(llistaFiltradaCarpetes, carpetesAdapter,
                CarpetesActivity.this, recyclerView);
    }
}