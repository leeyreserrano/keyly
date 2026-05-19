package com.example.keyly_projecte_intermodular;

import static com.example.keyly_projecte_intermodular.resources.Varis.usuariPropi;
import static com.example.keyly_projecte_intermodular.utils.LogOutService.logOut;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.dto.UsuariDTO;
import com.example.keyly_projecte_intermodular.adapters.UsuariAdpater;
import com.example.keyly_projecte_intermodular.utils.GestionsIdiomes;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarisActivity extends AppCompatActivity {

    private RecyclerView recyclerUsuaris;
    private LinearLayout layoutError;
    private UsuariAdpater usuariAdpater;
    private EditText etCercar;
    private ImageButton imgBtnIdioma, imgBtnLogOut, imgBtnBack, imgBtnAfegirUsuari;
    private ArrayList<Usuari> usuaris = new ArrayList<>(), totalUsuaris = new ArrayList<>(),
            usuarisSeleccionats = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(GestionsIdiomes.aplicarIdioma(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_usuaris);
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

        recyclerUsuaris = findViewById(R.id.recyclerCompartir);
        recyclerUsuaris.setLayoutManager(new LinearLayoutManager(this));

        carregarUsuaris();

        etCercar = findViewById(R.id.aCTVCercarCompartir);
        etCercar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrar usuaris
                String nomUsuari = s.toString();
                resultatsCerca(nomUsuari);
            }
        });

        imgBtnBack = findViewById(R.id.imgBtnBack);
        imgBtnBack.setOnClickListener(v -> {
            finish();
        });

        imgBtnAfegirUsuari = findViewById(R.id.imgBtnAfegirUsuari);
        imgBtnAfegirUsuari.setOnClickListener(v -> {
            // TODO acceder como edición para añadir usuario
            Log.d("DEBUG", "Botó afegir usuari clicat"); // ← añade esto
            Intent intent = new Intent(this, PerfilActivity.class);
            intent.putExtra("usuariPropi", false);
            intent.putExtra("esCreant", true);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarUsuaris();
    }

    private void carregarUsuaris() {
        UsuariDTO.RequestUsuari requestUsuari = UsuariDTO.obtenirJSONUsuari().create(UsuariDTO.RequestUsuari.class);
        requestUsuari.getAllUsuarisAdmin().enqueue(new Callback<ArrayList<Usuari>>() {
            @Override
            public void onResponse(Call<ArrayList<Usuari>> call, Response<ArrayList<Usuari>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usuaris.clear();
                    usuaris.addAll(response.body());
                    // Per no mostrar el usuari propi
                    for (Usuari usuari : usuaris) {
                        if (usuariPropi.getUuid().equals(usuari.getUuid())) {
                            usuaris.remove(usuari);
                            break;
                        }
                    }
                    actulitzarUsuaris(usuaris);
                    recyclerUsuaris.setVisibility(View.VISIBLE);
                } else {
                    // TODO mostrar error con layout
                    recyclerUsuaris.setVisibility(View.GONE);
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Usuari>> call, Throwable t) {
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    private void resultatsCerca(String titolUsuari) {
        ArrayList<Usuari> llistaFiltradaUsuari = new ArrayList<>();

        for (Usuari usuari : usuaris) {
            // Comporovar si coincideix algún nom amb el nom d'algún usuari
            if (usuari.getNom().toLowerCase().contains(titolUsuari.toLowerCase())) {
                llistaFiltradaUsuari.add(usuari);
            }
        }

        actulitzarUsuaris(llistaFiltradaUsuari);
    }

    private void actulitzarUsuaris(ArrayList<Usuari> usuaris) {
        usuariAdpater = new UsuariAdpater(usuaris, usuari -> {
            Intent intent = new Intent(this, PerfilActivity.class);
            // TODO acceder como admin view
            intent.putExtra("usuariPropi", false);
            intent.putExtra("esCreant", false);
            intent.putExtra("usuari", usuari);
            startActivity(intent);
        }, UsuarisActivity.this);
        recyclerUsuaris.setAdapter(usuariAdpater);
    }
}