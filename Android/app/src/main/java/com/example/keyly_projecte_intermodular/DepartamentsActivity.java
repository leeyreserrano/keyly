package com.example.keyly_projecte_intermodular;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import com.example.keyly_projecte_intermodular.dao.Departament;
import com.example.keyly_projecte_intermodular.dao.Sucursal;
import com.example.keyly_projecte_intermodular.dto.DepartamentDTO;
import com.example.keyly_projecte_intermodular.dto.SucursalDTO;
import com.example.keyly_projecte_intermodular.request.DepartamentRequest;
import com.example.keyly_projecte_intermodular.resources.SucursalDepartamentRolAdapter;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepartamentsActivity extends AppCompatActivity {

    private RecyclerView recyclerDepartaments;
    private LinearLayout layoutError;
    private SucursalDepartamentRolAdapter departamentAdapter;
    private EditText etCercar;
    private ImageButton imgBtnBack, imgBtnAfegirDepartament;
    private UUID uuidSucursal = null;
    private Sucursal sucursalDepartament = null;
    private ArrayList<Departament> departaments = new ArrayList<>();
    private ArrayList<Sucursal> sucursals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_departaments);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerDepartaments = findViewById(R.id.recyclerDepartaments);
        recyclerDepartaments.setLayoutManager(new LinearLayoutManager(this));

        carregarDepartaments();

        etCercar = findViewById(R.id.aCTVCercarDepartaments);
        etCercar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrar departaments
                String nomDepartament = s.toString();
                resultatsCerca(nomDepartament);
            }
        });

        imgBtnBack = findViewById(R.id.imgBtnBack);
        imgBtnBack.setOnClickListener(v -> {
            finish();
        });

        imgBtnAfegirDepartament = findViewById(R.id.imgBtnAfegirDepartament);
        imgBtnAfegirDepartament.setOnClickListener(v -> {
            // TODO acceder como edición para añadir departament
            veureCrearEditarDepartament(1, null);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDepartaments();
    }

    private void resultatsCerca(String nomDepartament) {
        ArrayList<Departament> llistaFiltradaDepartament = new ArrayList<>();

        for (Departament departament : departaments) {
            // Comporovar si coincideix algún nom amb el nom d'algún departament
            if (departament.getNom().toLowerCase().contains(nomDepartament.toLowerCase())) {
                llistaFiltradaDepartament.add(departament);
            }
        }

        actulitzarDepartaments(llistaFiltradaDepartament);
    }

    private void carregarDepartaments() {
        DepartamentDTO.RequestDepartament requestDepartament = DepartamentDTO.obtenirJSONDepartament().create(DepartamentDTO.RequestDepartament.class);
        requestDepartament.getAllDepartaments().enqueue(new Callback<ArrayList<Departament>>() {
            @Override
            public void onResponse(Call<ArrayList<Departament>> call, Response<ArrayList<Departament>> response) {

                Log.d("CODE", String.valueOf(response.code()));

                if (response.isSuccessful() && response.body() != null) {
                    departaments.clear();
                    departaments.addAll(response.body());
                    actulitzarDepartaments(departaments);
                    recyclerDepartaments.setVisibility(View.VISIBLE);
                } else {
                    // TODO mostrar error con layout
                    recyclerDepartaments.setVisibility(View.GONE);
                    Log.d("ERROR_RESPONSE", response.message());

                    try {
                        Log.d("ERROR_BODY", response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Departament>> call, Throwable t) {
                recyclerDepartaments.setVisibility(View.GONE);
                Log.d("ERROR_FAILURE", t.getMessage());
                Log.e("ERROR_FAILURE", "Error complet", t);
            }
        });
    }

    private void actulitzarDepartaments(ArrayList<Departament> departaments) {
        departamentAdapter = new SucursalDepartamentRolAdapter(departaments, departament -> {
            veureCrearEditarDepartament(0, departament);
        }, DepartamentsActivity.this);

        recyclerDepartaments.setAdapter(departamentAdapter);
    }

    private void veureCrearEditarDepartament(int view_add_edit, Departament departament) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_departament, null);

        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Elements del AlertDialog
        TextView txtNomDepartament = view.findViewById(R.id.txtNomDepartament);
        EditText etNomDepartament = view.findViewById(R.id.etNomDepartament);
        ImageButton imgBtnEditar = view.findViewById(R.id.imgBtnEditar);
        LinearLayout llSucursalNormal = view.findViewById(R.id.llSucursal);
        View includeSurcursal = view.findViewById(R.id.includeSucursal);
        LinearLayout llSucursalView = includeSurcursal.findViewById(R.id.llSucursalView);
        TextView txtSucursal = includeSurcursal.findViewById(R.id.txtNomSucursal);
        TextView txtDireccioSucursal = includeSurcursal.findViewById(R.id.txtDireccioSucursal);
        TextView txtCiutatSucursal = includeSurcursal.findViewById(R.id.txtCiutatSucursal);
        TextView txtPaisSucursal = includeSurcursal.findViewById(R.id.txtPaisSucursal);
        TextView txtTlfSucursal = includeSurcursal.findViewById(R.id.txtTlfSucursal);
        TextView txtCorreuSucursal = includeSurcursal.findViewById(R.id.txtCorreuSucursal);
        LinearLayout llSucursalEdit = includeSurcursal.findViewById(R.id.llSucursalEdit);
        TextView txtSucursals = view.findViewById(R.id.txtSucursals);
        Spinner spSucursals = view.findViewById(R.id.spSucursals);
        Button btnGuardarEliminarDepartament = view.findViewById(R.id.btnGuardarEliminarDepartament);
        Button btnBackCancelar = view.findViewById(R.id.btnBackCancelar);

        txtSucursal.setVisibility(View.VISIBLE);
        llSucursalView.setVisibility(View.VISIBLE);
        llSucursalEdit.setVisibility(View.GONE);

        imgBtnEditar.setOnClickListener(v -> {
            alertDialog.dismiss();
            veureCrearEditarDepartament(2, departament);
        });

        if (view_add_edit == 0) { // Veure departament

            txtNomDepartament.setVisibility(View.VISIBLE);
            imgBtnEditar.setVisibility(View.VISIBLE);
            etNomDepartament.setVisibility(View.GONE);
            txtSucursals.setVisibility(View.GONE);
            spSucursals.setVisibility(View.GONE);

            // Nom departament
            if (departament.getNom() != null && !departament.getNom().isEmpty()) {
                txtNomDepartament.setText(departament.getNom());
            } else {
                txtNomDepartament.setText("No té nom");
            }

            if (departament.getSucursal() != null) {
                llSucursalNormal.setVisibility(View.VISIBLE);
                includeSurcursal.setVisibility(View.VISIBLE);

                Sucursal sucursal = departament.getSucursal();

                // Obtenir la sucursal
                Call<Sucursal> call = SucursalDTO.obtenirJSONSucursal().create(SucursalDTO.RequestSucursal.class).getSucursal(sucursal.getUuid().toString());
                call.enqueue(new Callback<Sucursal>() {
                    @Override
                    public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
                        if (response.isSuccessful()) {
                            sucursalDepartament = response.body();

                            // Nom sucursal
                            if (sucursalDepartament.getNom() != null && !sucursalDepartament.getNom().isEmpty()) {
                                txtSucursal.setText(sucursalDepartament.getNom());
                            }

                            // Direcció sucursal
                            if (sucursalDepartament.getDireccio() != null && !sucursalDepartament.getDireccio().isEmpty()) {
                                String direccioSucursal = txtDireccioSucursal.getText().toString() + " " + sucursalDepartament.getDireccio();
                                txtDireccioSucursal.setText(direccioSucursal);
                            }

                            // Ciutat sucursal
                            if (sucursalDepartament.getCiutat() != null && !sucursalDepartament.getCiutat().isEmpty()) {
                                String ciutatSucursal = txtCiutatSucursal.getText().toString() + " " + sucursalDepartament.getCiutat();
                                txtCiutatSucursal.setText(ciutatSucursal);
                            }

                            // Pais sucursal
                            if (sucursalDepartament.getPais() != null && !sucursalDepartament.getPais().isEmpty()) {
                                String paisSucursal = txtPaisSucursal.getText().toString() + " " + sucursalDepartament.getPais();
                                txtPaisSucursal.setText(paisSucursal);
                            }

                            // Telèfon sucursal
                            if (sucursalDepartament.getTelefon() != null && !sucursalDepartament.getTelefon().isEmpty()) {
                                String tlfSucursal = txtTlfSucursal.getText().toString() + " " + sucursalDepartament.getTelefon();
                                txtTlfSucursal.setText(tlfSucursal);
                            }

                            // Correu sucursal
                            if (sucursalDepartament.getCorreu() != null && !sucursalDepartament.getCorreu().isEmpty()) {
                                String correuSucursal = txtCorreuSucursal.getText().toString() + " " + sucursalDepartament.getCorreu();
                                txtCorreuSucursal.setText(correuSucursal);
                            }
                        } else {
                            Log.d("ERROR_RESPONSE", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Sucursal> call, Throwable t) {
                        Log.d("ERROR_FAILURE", t.getMessage());
                    }
                });

            } else {
                llSucursalNormal.setVisibility(View.GONE);
                includeSurcursal.setVisibility(View.GONE);
            }

            btnGuardarEliminarDepartament.setText("Eliminar");
            btnGuardarEliminarDepartament.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnGuardarEliminarDepartament.setBackground(ContextCompat.getDrawable(this, R.drawable.background_button_eliminar));
            btnGuardarEliminarDepartament.setOnClickListener(v -> {
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

                txtPregunta.setText("Desitja eliminar el departament \"" + departament.getNom() + "\" ?");
                btnEliminar.setOnClickListener(c -> {
                    eliminarDepartament(departament, alertDialog2);
                    alertDialog.dismiss();
                    alertDialog2.dismiss();
                });

                btnCancelar.setOnClickListener(c -> {
                    alertDialog2.dismiss();
                    veureCrearEditarDepartament(0, departament);
                });
            });

            btnBackCancelar.setText("Enrere");
            btnBackCancelar.setOnClickListener(v -> {
                alertDialog.dismiss();
            });

        } else { // Crear o editar departament
            // Ocultar elements
            txtNomDepartament.setVisibility(View.GONE);
            imgBtnEditar.setVisibility(View.GONE);
            includeSurcursal.setVisibility(View.GONE);

            // Mostrar elements
            etNomDepartament.setVisibility(View.VISIBLE);
            txtSucursals.setVisibility(View.VISIBLE);
            spSucursals.setVisibility(View.VISIBLE);

            // Botó guardar departament
            btnGuardarEliminarDepartament.setText("Guardar");
            btnGuardarEliminarDepartament.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnGuardarEliminarDepartament.setBackground(ContextCompat.getDrawable(this, R.drawable.background_button_purple));

            // Obtenir totes les sucursals
            obtenirSucursals(spSucursals);

            if (view_add_edit == 2) {
                if (departament.getNom() != null && !departament.getNom().isEmpty()) {
                    etNomDepartament.setText(departament.getNom());
                }
                if (departament.getSucursal() != null) {
                    String uuidActual = departament.getSucursal().getUuid().toString();
                    for (int i = 0; i < sucursals.size(); i++) {
                        if (sucursals.get(i).getUuid().toString().equals(uuidActual)) {
                            spSucursals.setSelection(i);
                            break;
                        }
                    }
                }
            }

            spSucursals.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Sucursal s = sucursals.get(position);
                    uuidSucursal = s.getUuid();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            btnGuardarEliminarDepartament.setOnClickListener(v -> {
                String nomDepartament = etNomDepartament.getText().toString().trim();
                if (nomDepartament.isEmpty()) {
                    etNomDepartament.setError("Nom departament obligatori");
                    return;
                }
                DepartamentRequest departamentRequest = new DepartamentRequest(uuidSucursal, nomDepartament);
                if (view_add_edit == 1) {
                    afegirDepartament(departamentRequest, alertDialog);
                } else {
                    editarDepartament(departamentRequest, alertDialog, departament.getUuid().toString());
                }
            });

            btnBackCancelar.setText("Cancel·lar");
            btnBackCancelar.setOnClickListener(v -> alertDialog.dismiss());
        }
    }

    private void afegirDepartament(DepartamentRequest departamentRequest, AlertDialog alertDialog){
        String nomDepartament = departamentRequest.getNom();
        Call<Departament> call = DepartamentDTO.obtenirJSONDepartament().create(DepartamentDTO.RequestDepartament.class).crearDepartament(departamentRequest);
        call.enqueue(new Callback<Departament>() {
            @Override
            public void onResponse(Call<Departament> call, Response<Departament> response) {
                if (response.isSuccessful()) {
                    departaments.add(response.body());
                    departamentAdapter.notifyDataSetChanged();
                    Toast.makeText(DepartamentsActivity.this, "Departament " + nomDepartament + " afegit", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    onResume();
                } else {
                    Toast.makeText(DepartamentsActivity.this, "No s'ha pogut afegir el departament" + nomDepartament, Toast.LENGTH_SHORT).show();
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Departament> call, Throwable t) {
                Toast.makeText(DepartamentsActivity.this, "No s'ha pogut afegir el departament" + nomDepartament, Toast.LENGTH_SHORT).show();
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    private void editarDepartament(DepartamentRequest departamentRequest, AlertDialog alertDialog, String uuid) {
        String nomDepartament = departamentRequest.getNom();
        Call<Departament> call = DepartamentDTO.obtenirJSONDepartament().create(DepartamentDTO.RequestDepartament.class).actualitzaDepartament(uuid, departamentRequest);
        call.enqueue(new Callback<Departament>() {
            @Override
            public void onResponse(Call<Departament> call, Response<Departament> response) {
                if (response.isSuccessful()) {
                    departamentAdapter.notifyDataSetChanged();
                    Toast.makeText(DepartamentsActivity.this, "Departament " + nomDepartament + " actualitzat", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    veureCrearEditarDepartament(0, response.body());
                } else {
                    Toast.makeText(DepartamentsActivity.this, "No s'ha pogut actualitzar el departament" + nomDepartament, Toast.LENGTH_SHORT).show();
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Departament> call, Throwable t) {
                Toast.makeText(DepartamentsActivity.this, "No s'ha pogut actualitzar el departament" + nomDepartament, Toast.LENGTH_SHORT).show();
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    private void eliminarDepartament(Departament departament, AlertDialog alertDialog) {
        String nomDepartament = departament.getNom();
        Call<Departament> call = DepartamentDTO.obtenirJSONDepartament().create(DepartamentDTO.RequestDepartament.class).eliminarDepartament(departament.getUuid().toString());
        call.enqueue(new Callback<Departament>() {
            @Override
            public void onResponse(Call<Departament> call, Response<Departament> response) {
                if (response.isSuccessful()) {
                    departaments.remove(departament);
                    departamentAdapter.notifyDataSetChanged();
                    Toast.makeText(DepartamentsActivity.this, "Departament " + nomDepartament + " eliminat", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    onResume();
                } else {
                    Toast.makeText(DepartamentsActivity.this, "No s'ha pogut eliminar el departament" + nomDepartament, Toast.LENGTH_SHORT).show();
                    Log.e("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Departament> call, Throwable t) {
                Toast.makeText(DepartamentsActivity.this, "No s'ha pogut eliminar el departament" + nomDepartament, Toast.LENGTH_SHORT).show();
                Log.e("ERROR_FAILURE", t.getMessage());
                Log.e("ERROR_FAILURE", "Error complet", t);
            }
        });
    }

    private void obtenirSucursals(Spinner spSucursals) {
        Call<ArrayList<Sucursal>> call = SucursalDTO.obtenirJSONSucursal().create(SucursalDTO.RequestSucursal.class).getAllSucursals();
        call.enqueue(new Callback<ArrayList<Sucursal>>() {
            @Override
            public void onResponse(Call<ArrayList<Sucursal>> call, Response<ArrayList<Sucursal>> response) {
                if (response.isSuccessful()) {
                    sucursals.clear();
                    sucursals.addAll(response.body());
                    Log.d("SUCURSALS_DEP", sucursals.toString());
                    ArrayList<String> nomSucursals = new ArrayList<>();
                    for (Sucursal s : sucursals) {
                        nomSucursals.add(s.getNom());
                    }
                    // Inserir les sucursals en l'spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DepartamentsActivity.this, android.R.layout.simple_spinner_item, nomSucursals);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSucursals.setAdapter(adapter);
                } else {
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Sucursal>> call, Throwable t) {
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });
    }
}