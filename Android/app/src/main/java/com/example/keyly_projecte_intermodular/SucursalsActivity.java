package com.example.keyly_projecte_intermodular;

import static com.example.keyly_projecte_intermodular.utils.LogOutService.logOut;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.dao.Sucursal;
import com.example.keyly_projecte_intermodular.dto.SucursalDTO;
import com.example.keyly_projecte_intermodular.request.SucursalRequest;
import com.example.keyly_projecte_intermodular.adapters.SDRDAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SucursalsActivity extends AppCompatActivity {

    private RecyclerView recyclerSucursals;
    private LinearLayout layoutError;
    private SDRDAdapter sucursalAdapter;
    private EditText etCercar;
    private ImageButton imgBtnLogOut, imgBtnBack, imgBtnAfegirSucursal;
    private ArrayList<Sucursal> sucursals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sucursals);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgBtnLogOut = findViewById(R.id.imgBtnLogOut);
        imgBtnLogOut.setOnClickListener(v -> {
            logOut(this);
        });

        recyclerSucursals = findViewById(R.id.recyclerSucursals);
        recyclerSucursals.setLayoutManager(new LinearLayoutManager(this));

        carregarSucursals();

        etCercar = findViewById(R.id.aCTVCercarSucursals);
        etCercar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrar departaments
                String nomSucursal = s.toString();
                resultatsCerca(nomSucursal);
            }
        });

        imgBtnBack = findViewById(R.id.imgBtnBack);
        imgBtnBack.setOnClickListener(v -> {
            finish();
        });

        imgBtnAfegirSucursal = findViewById(R.id.imgBtnAfegirSucursal);
        imgBtnAfegirSucursal.setOnClickListener(v -> {
            // TODO acceder como edición para añadir departament
            veureCrearEditarSucursal(1, null);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarSucursals();
    }

    private void resultatsCerca(String nomSucursal) {
        ArrayList<Sucursal> llistaFiltradaSucursal = new ArrayList<>();

        for (Sucursal sucursal : sucursals) {
            // Comporovar si coincideix algún nom amb el nom d'alguna sucursal
            if (sucursal.getNom().toLowerCase().contains(nomSucursal.toLowerCase())) {
                llistaFiltradaSucursal.add(sucursal);
            }
        }

        actulitzarSucursals(llistaFiltradaSucursal);
    }

    private void carregarSucursals() {
        SucursalDTO.RequestSucursal requestSucursal = SucursalDTO.obtenirJSONSucursal().create(SucursalDTO.RequestSucursal.class);
        requestSucursal.getAllSucursals().enqueue(new Callback<ArrayList<Sucursal>>() {
            @Override
            public void onResponse(Call<ArrayList<Sucursal>> call, Response<ArrayList<Sucursal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sucursals.clear();
                    sucursals.addAll(response.body());
                    actulitzarSucursals(sucursals);
                    recyclerSucursals.setVisibility(View.VISIBLE);
                } else {
                    // TODO mostrar error con layout
                    recyclerSucursals.setVisibility(View.GONE);
                    Log.d("ERROR_RESPONSE", response.message());

                    try {
                        Log.d("ERROR_BODY", response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Sucursal>> call, Throwable t) {
                Log.d("ERROR_FAILURE", t.getMessage());
                Log.e("ERROR_FAILURE", "Error complet", t);
            }
        });
    }

    private void actulitzarSucursals(ArrayList<Sucursal> sucursals) {
        sucursalAdapter = new SDRDAdapter(sucursals, sucursal -> {
            // TODO mostrar alert dialog amb la informació de la sucursal
            veureCrearEditarSucursal(0, sucursal);
        }, SucursalsActivity.this);
        recyclerSucursals.setAdapter(sucursalAdapter);
    }

    private void veureCrearEditarSucursal(int view_add_edit, Sucursal sucursal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_sucursal, null);

        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Elements del AlertDialog
        TextView txtNomSucursal = view.findViewById(R.id.txtNomSucursal);
        EditText etNomSucursal = view.findViewById(R.id.etNomSucursal);
        ImageButton imgBtnEditar = view.findViewById(R.id.imgBtnEditar);
        View includeSurcursal = view.findViewById(R.id.includeSucursal);
        LinearLayout llSucursalView = includeSurcursal.findViewById(R.id.llSucursalView);
        TextView txtSucursal = includeSurcursal.findViewById(R.id.txtNomSucursal);
        TextView txtDireccioSucursal = includeSurcursal.findViewById(R.id.txtDireccioSucursal);
        TextView txtCiutatSucursal = includeSurcursal.findViewById(R.id.txtCiutatSucursal);
        TextView txtPaisSucursal = includeSurcursal.findViewById(R.id.txtPaisSucursal);
        TextView txtTlfSucursal = includeSurcursal.findViewById(R.id.txtTlfSucursal);
        TextView txtCorreuSucursal = includeSurcursal.findViewById(R.id.txtCorreuSucursal);
        LinearLayout llSucursalEdit = view.findViewById(R.id.llSucursalEdit);
        EditText etDireccioSucursal = view.findViewById(R.id.etDireccioSucursal);
        EditText etCiutatSucursal = view.findViewById(R.id.etCiutatSucursal);
        EditText etPaisSucursal = view.findViewById(R.id.etPaisSucursal);
        EditText etTlfSucursal = view.findViewById(R.id.etTlfSucursal);
        EditText etCorreuSucursal = view.findViewById(R.id.etCorreuSucursal);
        Button btnGuardarEliminarSucursal = view.findViewById(R.id.btnGuardarEliminarSucursal);
        Button btnBackCancelar = view.findViewById(R.id.btnBackCancelar);

        txtSucursal.setVisibility(View.GONE);

        imgBtnEditar.setOnClickListener(v -> {
            alertDialog.dismiss();
            veureCrearEditarSucursal(2, sucursal);
        });

        if (view_add_edit == 0) { // Veure sucursal

            // Ocultar elements
            etNomSucursal.setVisibility(View.GONE);
            llSucursalEdit.setVisibility(View.GONE);

            // Mostrar elements
            txtNomSucursal.setVisibility(View.VISIBLE);
            imgBtnEditar.setVisibility(View.VISIBLE);
            llSucursalView.setVisibility(View.VISIBLE);

            // Nom sucursal
            if (sucursal.getNom() != null && !sucursal.getNom().isEmpty()) {
                txtNomSucursal.setText(sucursal.getNom());
            } else {
                txtNomSucursal.setText("No té nom");
            }

            // Direcció sucursal
            if (sucursal.getDireccio() != null && !sucursal.getDireccio().isEmpty()) {
                String direccioSucursal = txtDireccioSucursal.getText().toString() + " " + sucursal.getDireccio();
                txtDireccioSucursal.setText(direccioSucursal);
            }

            // Ciutat sucursal
            if (sucursal.getCiutat() != null && !sucursal.getCiutat().isEmpty()) {
                String ciutatSucursal = txtCiutatSucursal.getText().toString() + " " + sucursal.getCiutat();
                txtCiutatSucursal.setText(ciutatSucursal);
            }

            // Pais sucursal
            if (sucursal.getPais() != null && !sucursal.getPais().isEmpty()) {
                String paisSucursal = txtPaisSucursal.getText().toString() + " " + sucursal.getPais();
                txtPaisSucursal.setText(paisSucursal);
            }

            // Telèfon sucursal
            if (sucursal.getTelefon() != null && !sucursal.getTelefon().isEmpty()) {
                String tlfSucursal = txtTlfSucursal.getText().toString() + " " + sucursal.getTelefon();
                txtTlfSucursal.setText(tlfSucursal);
            }

            // Correu sucursal
            if (sucursal.getCorreu() != null && !sucursal.getCorreu().isEmpty()) {
                String correuSucursal = txtCorreuSucursal.getText().toString() + " " + sucursal.getCorreu();
                txtCorreuSucursal.setText(correuSucursal);
            }

            btnGuardarEliminarSucursal.setText("Eliminar");
            btnGuardarEliminarSucursal.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnGuardarEliminarSucursal.setBackground(ContextCompat.getDrawable(this, R.drawable.background_button_eliminar));
            btnGuardarEliminarSucursal.setOnClickListener(v -> {
                alertDialog.dismiss();

                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                LayoutInflater inflater2 = getLayoutInflater();
                View view2 = inflater2.inflate(R.layout.layout_eliminar, null);

                builder2.setView(view2);

                AlertDialog alertDialog2 = builder2.create();
                alertDialog2.show();

                // Elements del AlertDialog
                TextView txtPregunta = view2.findViewById(R.id.txtPregunta);
                Button btnEliminar = view2.findViewById(R.id.btnEliminar);
                Button btnCancelar = view2.findViewById(R.id.btnCancelar);

                txtPregunta.setText("Desitja eliminar el departament \"" + sucursal.getNom() + "\" ?");
                btnEliminar.setOnClickListener(c -> {
                    eliminarSucursal(sucursal, alertDialog2);
                    alertDialog.dismiss();
                    alertDialog2.dismiss();
                });

                btnCancelar.setOnClickListener(c -> {
                    alertDialog2.dismiss();
                    veureCrearEditarSucursal(0, sucursal);
                });
            });

            btnBackCancelar.setText("Enrere");
            btnBackCancelar.setOnClickListener(v -> {
                alertDialog.dismiss();
            });

        } else { // Crear o editar sucursal
            // Ocultar elements
            txtNomSucursal.setVisibility(View.GONE);
            imgBtnEditar.setVisibility(View.GONE);
            llSucursalView.setVisibility(View.GONE);

            // Mostrar elements
            etNomSucursal.setVisibility(View.VISIBLE);
            llSucursalEdit.setVisibility(View.VISIBLE);

            if (view_add_edit == 2) {
                if (sucursal.getNom() != null && !sucursal.getNom().isEmpty()) {
                    etNomSucursal.setText(sucursal.getNom());
                }

                if (sucursal.getDireccio() != null && !sucursal.getDireccio().isEmpty()) {
                    etDireccioSucursal.setText(sucursal.getDireccio());
                }

                if (sucursal.getCiutat() != null && !sucursal.getCiutat().isEmpty()) {
                    etCiutatSucursal.setText(sucursal.getCiutat());
                }

                if (sucursal.getPais() != null && !sucursal.getPais().isEmpty()) {
                    etPaisSucursal.setText(sucursal.getPais());
                }

                if (sucursal.getTelefon() != null && !sucursal.getTelefon().isEmpty()) {
                    etTlfSucursal.setText(sucursal.getTelefon());
                }

                if (sucursal.getCorreu() != null && !sucursal.getCorreu().isEmpty()) {
                    etCorreuSucursal.setText(sucursal.getCorreu());
                }
            }

            // Botó guardar sucursal
            btnGuardarEliminarSucursal.setText("Guardar");
            btnGuardarEliminarSucursal.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnGuardarEliminarSucursal.setBackground(ContextCompat.getDrawable(this, R.drawable.background_button_purple));
            btnGuardarEliminarSucursal.setOnClickListener(v -> {
                String nomSurcursal = etNomSucursal.getText().toString().trim();
                if (nomSurcursal.isEmpty()) {
                    etNomSucursal.setError("Nom sucursal obligatori");
                    return;
                }

                String direccioSucursal = etDireccioSucursal.getText().toString().trim();
                String ciutatSucursal = etCiutatSucursal.getText().toString().trim();
                String paisSucursal = etPaisSucursal.getText().toString().trim();
                String tlfSucursal = etTlfSucursal.getText().toString().trim();
                String correuSucursal = etCorreuSucursal.getText().toString().trim();

                SucursalRequest sucursalRequest = new SucursalRequest(nomSurcursal,
                        direccioSucursal, ciutatSucursal, paisSucursal, tlfSucursal, correuSucursal);

                if (view_add_edit == 1) { // Crear sucursal
                    afegirSurcursal(sucursalRequest, alertDialog);
                } else { // Editar sucursal
                    editarSucursal(sucursalRequest, alertDialog, sucursal.getUuid().toString());
                }
            });

            btnBackCancelar.setText("Cancel·lar");
            btnBackCancelar.setOnClickListener(v -> alertDialog.dismiss());
        }
    }

    private void afegirSurcursal(SucursalRequest sucursalRequest, AlertDialog alertDialog) {
        String nomSucursal = sucursalRequest.getNom();
        Call<Sucursal> call = SucursalDTO.obtenirJSONSucursal().create(SucursalDTO.RequestSucursal.class).crearSucursal(sucursalRequest);
        call.enqueue(new Callback<Sucursal>() {
            @Override
            public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
                if (response.isSuccessful()) {
                    sucursals.add(response.body());
                    sucursalAdapter.notifyDataSetChanged();
                    Toast.makeText(SucursalsActivity.this, "Sucursal " + nomSucursal + " afegida", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    onResume();
                } else {
                    Toast.makeText(SucursalsActivity.this, "No s'ha pogut afegir la sucursal" + nomSucursal, Toast.LENGTH_SHORT).show();
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Sucursal> call, Throwable t) {
                Toast.makeText(SucursalsActivity.this, "No s'ha pogut afegir la sucursal" + nomSucursal, Toast.LENGTH_SHORT).show();
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    private void editarSucursal(SucursalRequest sucursalRequest, AlertDialog alertDialog, String uuid) {
        String nomSucursal = sucursalRequest.getNom();
        Call<Sucursal> call = SucursalDTO.obtenirJSONSucursal().create(SucursalDTO.RequestSucursal.class).actualitzaSucursal(uuid, sucursalRequest);
        call.enqueue(new Callback<Sucursal>() {
            @Override
            public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
                if (response.isSuccessful()) {
                    sucursalAdapter.notifyDataSetChanged();
                    Toast.makeText(SucursalsActivity.this, "Sucursal " + nomSucursal + " actualitzada", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    veureCrearEditarSucursal(0, response.body());
                } else {
                    Toast.makeText(SucursalsActivity.this, "No s'ha pogut actualitzar la sucursal" + nomSucursal, Toast.LENGTH_SHORT).show();
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Sucursal> call, Throwable t) {
                Toast.makeText(SucursalsActivity.this, "No s'ha pogut actualitzar la sucursal" + nomSucursal, Toast.LENGTH_SHORT).show();
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    private void eliminarSucursal(Sucursal sucursal, AlertDialog alertDialog) {
        String nomSucursal = sucursal.getNom();
        Call<Sucursal> call = SucursalDTO.obtenirJSONSucursal().create(SucursalDTO.RequestSucursal.class).eliminarSucursal(sucursal.getUuid().toString());
        call.enqueue(new Callback<Sucursal>() {
            @Override
            public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
                if (response.isSuccessful()) {
                    sucursals.remove(sucursal);
                    sucursalAdapter.notifyDataSetChanged();
                    Toast.makeText(SucursalsActivity.this, "Sucursal " + nomSucursal + " eliminada", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    onResume();
                } else {
                    Toast.makeText(SucursalsActivity.this, "No s'ha pogut eliminar la sucursal" + nomSucursal, Toast.LENGTH_SHORT).show();
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Sucursal> call, Throwable t) {
                Toast.makeText(SucursalsActivity.this, "No s'ha pogut eliminar la sucursal" + nomSucursal, Toast.LENGTH_SHORT).show();
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });
    }
}