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
import com.example.keyly_projecte_intermodular.dao.Rol;
import com.example.keyly_projecte_intermodular.dao.Sucursal;
import com.example.keyly_projecte_intermodular.dto.RolDTO;
import com.example.keyly_projecte_intermodular.dto.SucursalDTO;
import com.example.keyly_projecte_intermodular.request.DepartamentRequest;
import com.example.keyly_projecte_intermodular.request.RolRequest;
import com.example.keyly_projecte_intermodular.resources.SucursalDepartamentRolAdapter;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RolsActivity extends AppCompatActivity {

    private RecyclerView recyclerRols;
    private LinearLayout layoutError;
    private SucursalDepartamentRolAdapter rolAdapter;
    private EditText etCercar;
    private ImageButton imgBtnBack, imgBtnAfegirRol;
    private UUID uuidSucursal = null;
    private Sucursal sucursalRol = null;
    private ArrayList<Rol> rols = new ArrayList<>();
    private ArrayList<Sucursal> sucursals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rols);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerRols = findViewById(R.id.recyclerRols);
        recyclerRols.setLayoutManager(new LinearLayoutManager(this));

        carregarRols();

        etCercar = findViewById(R.id.aCTVCercarRols);
        etCercar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrar departaments
                String nomRol = s.toString();
                resultatsCerca(nomRol);
            }
        });

        imgBtnBack = findViewById(R.id.imgBtnBack);
        imgBtnBack.setOnClickListener(v -> {
            finish();
        });

        imgBtnAfegirRol = findViewById(R.id.imgBtnAfegirRol);
        imgBtnAfegirRol.setOnClickListener(v -> {
            // TODO acceder como edición para añadir rol
            veureCrearEditarRol(1, null);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarRols();
    }

    private void resultatsCerca(String nomRol) {
        ArrayList<Rol> llistaFiltradaRol = new ArrayList<>();

        for (Rol rol : rols) {
            // Comporovar si coincideix algún nom amb el nom d'algún rol
            if (rol.getNom().toLowerCase().contains(nomRol.toLowerCase())) {
                llistaFiltradaRol.add(rol);
            }
        }

        actulitzarRols(llistaFiltradaRol);
    }

    private void carregarRols() {
        RolDTO.RequestRol requestRol = RolDTO.obtenirJSONRol().create(RolDTO.RequestRol.class);
        requestRol.getAllRols().enqueue(new Callback<ArrayList<Rol>>() {
            @Override
            public void onResponse(Call<ArrayList<Rol>> call, Response<ArrayList<Rol>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rols.clear();
                    rols.addAll(response.body());
                    actulitzarRols(rols);
                    recyclerRols.setVisibility(View.VISIBLE);
                } else {
                    // TODO mostrar error con layout
                    recyclerRols.setVisibility(View.GONE);
                    Log.d("ERROR_RESPONSE", response.message());

                    try {
                        Log.d("ERROR_BODY", response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Rol>> call, Throwable t) {
                recyclerRols.setVisibility(View.GONE);
                Log.d("ERROR_FAILURE", t.getMessage());
                Log.e("ERROR_FAILURE", "Error complet", t);
            }
        });
    }

    private void actulitzarRols(ArrayList<Rol> rols) {
        rolAdapter = new SucursalDepartamentRolAdapter(rols, rol -> {
            veureCrearEditarRol(0, rol);
        }, RolsActivity.this);

        recyclerRols.setAdapter(rolAdapter);
    }

    private void veureCrearEditarRol(int view_add_edit, Rol rol) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_rol, null);

        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Elements del AlertDialog
        TextView txtNomRol = view.findViewById(R.id.txtNomRol);
        EditText etNomRol = view.findViewById(R.id.etNomRol);
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
        Button btnGuardarEliminarRol = view.findViewById(R.id.btnGuardarEliminarRol);
        Button btnBackCancelar = view.findViewById(R.id.btnBackCancelar);

        txtSucursal.setVisibility(View.VISIBLE);
        llSucursalView.setVisibility(View.VISIBLE);
        llSucursalEdit.setVisibility(View.GONE);

        imgBtnEditar.setOnClickListener(v -> {
            alertDialog.dismiss();
            veureCrearEditarRol(2, rol);
        });

        if (view_add_edit == 0) { // Veure rol

            txtNomRol.setVisibility(View.VISIBLE);
            imgBtnEditar.setVisibility(View.VISIBLE);
            etNomRol.setVisibility(View.GONE);
            txtSucursals.setVisibility(View.GONE);
            spSucursals.setVisibility(View.GONE);

            // Nom rol
            if (rol.getNom() != null && !rol.getNom().isEmpty()) {
                txtNomRol.setText(rol.getNom());
            } else {
                txtNomRol.setText("No té nom");
            }

            if (rol.getSucursal() != null) {
                llSucursalNormal.setVisibility(View.VISIBLE);
                includeSurcursal.setVisibility(View.VISIBLE);

                Sucursal sucursal = rol.getSucursal();

                // Obtenir la sucursal
                Call<Sucursal> call = SucursalDTO.obtenirJSONSucursal().create(SucursalDTO.RequestSucursal.class).getSucursal(sucursal.getUuid().toString());
                call.enqueue(new Callback<Sucursal>() {
                    @Override
                    public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
                        if (response.isSuccessful()) {
                            sucursalRol = response.body();

                            // Nom sucursal
                            if (sucursalRol.getNom() != null && !sucursalRol.getNom().isEmpty()) {
                                txtSucursal.setText(sucursalRol.getNom());
                            }

                            // Direcció sucursal
                            if (sucursalRol.getDireccio() != null && !sucursalRol.getDireccio().isEmpty()) {
                                String direccioSucursal = txtDireccioSucursal.getText().toString() + " " + sucursalRol.getDireccio();
                                txtDireccioSucursal.setText(direccioSucursal);
                            }

                            // Ciutat sucursal
                            if (sucursalRol.getCiutat() != null && !sucursalRol.getCiutat().isEmpty()) {
                                String ciutatSucursal = txtCiutatSucursal.getText().toString() + " " + sucursalRol.getCiutat();
                                txtCiutatSucursal.setText(ciutatSucursal);
                            }

                            // Pais sucursal
                            if (sucursalRol.getPais() != null && !sucursalRol.getPais().isEmpty()) {
                                String paisSucursal = txtPaisSucursal.getText().toString() + " " + sucursalRol.getPais();
                                txtPaisSucursal.setText(paisSucursal);
                            }

                            // Telèfon sucursal
                            if (sucursalRol.getTelefon() != null && !sucursalRol.getTelefon().isEmpty()) {
                                String tlfSucursal = txtTlfSucursal.getText().toString() + " " + sucursalRol.getTelefon();
                                txtTlfSucursal.setText(tlfSucursal);
                            }

                            // Correu sucursal
                            if (sucursalRol.getCorreu() != null && !sucursalRol.getCorreu().isEmpty()) {
                                String correuSucursal = txtCorreuSucursal.getText().toString() + " " + sucursalRol.getCorreu();
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

            btnGuardarEliminarRol.setText("Eliminar");
            btnGuardarEliminarRol.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnGuardarEliminarRol.setBackground(ContextCompat.getDrawable(this, R.drawable.background_button_eliminar));
            btnGuardarEliminarRol.setOnClickListener(v -> {
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

                txtPregunta.setText("Desitja eliminar el departament \"" + rol.getNom() + "\" ?");
                btnEliminar.setOnClickListener(c -> {
                    eliminarRol(rol, alertDialog2);
                    alertDialog.dismiss();
                    alertDialog2.dismiss();
                });

                btnCancelar.setOnClickListener(c -> {
                    alertDialog2.dismiss();
                    veureCrearEditarRol(0, rol);
                });
            });

            btnBackCancelar.setText("Enrere");
            btnBackCancelar.setOnClickListener(v -> {
                alertDialog.dismiss();
            });

        } else { // Crear i/o editar rol
            // Ocultar elements
            txtNomRol.setVisibility(View.GONE);
            imgBtnEditar.setVisibility(View.GONE);
            includeSurcursal.setVisibility(View.GONE);

            // Mostrar elements
            etNomRol.setVisibility(View.VISIBLE);
            txtSucursals.setVisibility(View.VISIBLE);
            spSucursals.setVisibility(View.VISIBLE);

            // Botó guardar departament
            btnGuardarEliminarRol.setText("Guardar");
            btnGuardarEliminarRol.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnGuardarEliminarRol.setBackground(ContextCompat.getDrawable(this, R.drawable.background_button_purple));

            // Obtenir totes les sucursals
            obtenirSucursals(spSucursals);

            if (view_add_edit == 2) {
                if (rol.getNom() != null && !rol.getNom().isEmpty()) {
                    etNomRol.setText(rol.getNom());
                }
                if (rol.getSucursal() != null) {
                    String uuidActual = rol.getSucursal().getUuid().toString();
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

            btnGuardarEliminarRol.setOnClickListener(v -> {
                String nomRol = etNomRol.getText().toString().trim();
                if (nomRol.isEmpty()) {
                    etNomRol.setError("Nom rol obligatori");
                    return;
                }
                RolRequest rolRequest = new RolRequest(uuidSucursal, nomRol);
                if (view_add_edit == 1) {
                    afegirRol(rolRequest, alertDialog);
                } else {
                    editarRol(rolRequest, alertDialog, rol.getUuid().toString());
                }
            });

            btnBackCancelar.setText("Cancel·lar");
            btnBackCancelar.setOnClickListener(v -> alertDialog.dismiss());
        }
    }

    private void afegirRol(RolRequest rolRequest, AlertDialog alertDialog) {
        String nomRol = rolRequest.getNom();
        Call<Rol> call = RolDTO.obtenirJSONRol().create(RolDTO.RequestRol.class).afegirRol(rolRequest);
        call.enqueue(new Callback<Rol>() {
            @Override
            public void onResponse(Call<Rol> call, Response<Rol> response) {
                if (response.isSuccessful()) {
                    rols.add(response.body());
                    rolAdapter.notifyDataSetChanged();
                    Toast.makeText(RolsActivity.this, "Rol " + nomRol + " afegit", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    onResume();
                } else {
                    Toast.makeText(RolsActivity.this, "No s'ha pogut afegir el rol" + nomRol, Toast.LENGTH_SHORT).show();
                    Log.e("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Rol> call, Throwable t) {
                Toast.makeText(RolsActivity.this, "No s'ha pogut afegir el rol" + nomRol, Toast.LENGTH_SHORT).show();
                Log.e("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    private void editarRol(RolRequest rolRequest, AlertDialog alertDialog, String uuid) {
        String nomRol = rolRequest.getNom();
        Call<Rol> call = RolDTO.obtenirJSONRol().create(RolDTO.RequestRol.class).actualitzarRol(uuid, rolRequest);
        call.enqueue(new Callback<Rol>() {
            @Override
            public void onResponse(Call<Rol> call, Response<Rol> response) {
                if (response.isSuccessful()) {
                    rolAdapter.notifyDataSetChanged();
                    Toast.makeText(RolsActivity.this, "Rol " + nomRol + " actualitzat", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    veureCrearEditarRol(0, response.body());
                } else {
                    Toast.makeText(RolsActivity.this, "No s'ha pogut actualitzar el rol" + nomRol, Toast.LENGTH_SHORT).show();
                    Log.e("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Rol> call, Throwable t) {
                Toast.makeText(RolsActivity.this, "No s'ha pogut actualitzar el rol" + nomRol, Toast.LENGTH_SHORT).show();
                Log.e("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    private void eliminarRol(Rol rol, AlertDialog alertDialog) {
        String nomRol = rol.getNom();
        Call<Rol> call = RolDTO.obtenirJSONRol().create(RolDTO.RequestRol.class).eliminarRol(rol.getUuid().toString());
        call.enqueue(new Callback<Rol>() {
            @Override
            public void onResponse(Call<Rol> call, Response<Rol> response) {
                if (response.isSuccessful()) {
                    rols.remove(rol);
                    rolAdapter.notifyDataSetChanged();
                    Toast.makeText(RolsActivity.this, "Rol " + nomRol + " eliminat", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    onResume();
                } else {
                    Toast.makeText(RolsActivity.this, "No s'ha pogut eliminar el rol" + nomRol, Toast.LENGTH_SHORT).show();
                    Log.e("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Rol> call, Throwable t) {
                Toast.makeText(RolsActivity.this, "No s'ha pogut eliminar el rol" + nomRol, Toast.LENGTH_SHORT).show();
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RolsActivity.this, android.R.layout.simple_spinner_item, nomSucursals);
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