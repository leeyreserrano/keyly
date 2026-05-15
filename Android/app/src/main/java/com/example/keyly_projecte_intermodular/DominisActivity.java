package com.example.keyly_projecte_intermodular;

import static com.example.keyly_projecte_intermodular.utils.LogOutService.logOut;

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

import com.example.keyly_projecte_intermodular.dao.Domini;
import com.example.keyly_projecte_intermodular.dao.Sucursal;
import com.example.keyly_projecte_intermodular.dto.DominiDTO;
import com.example.keyly_projecte_intermodular.dto.SucursalDTO;
import com.example.keyly_projecte_intermodular.request.DominiRequest;
import com.example.keyly_projecte_intermodular.adapters.SDRDAdapter;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DominisActivity extends AppCompatActivity {

    private RecyclerView recyclerDominis;
    private LinearLayout layoutError;
    private SDRDAdapter dominiAdapter;
    private EditText etCercar;
    private ImageButton imgBtnLogOut, imgBtnBack, imgBtnAfegirDomini;
    private UUID uuidSucursal = null;
    private Sucursal sucursalDomini = null;
    private ArrayList<Domini> dominis = new ArrayList<>();
    private ArrayList<Sucursal> sucursals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dominis);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgBtnLogOut = findViewById(R.id.imgBtnLogOut);
        imgBtnLogOut.setOnClickListener(v -> {
            logOut(this);
        });

        recyclerDominis = findViewById(R.id.recyclerDominis);
        recyclerDominis.setLayoutManager(new LinearLayoutManager(this));

        carregarDominis();

        etCercar = findViewById(R.id.aCTVCercarDominis);
        etCercar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrar dominis
                String nomDominis = s.toString();
                resultatsCerca(nomDominis);
            }
        });

        imgBtnBack = findViewById(R.id.imgBtnBack);
        imgBtnBack.setOnClickListener(v -> {
            finish();
        });

        imgBtnAfegirDomini = findViewById(R.id.imgBtnAfegirDomini);
        imgBtnAfegirDomini.setOnClickListener(v -> {
            // TODO acceder como edición para añadir domini
            veureCrearEditarDomini(1, null);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDominis();
    }

    private void resultatsCerca(String nomDomini) {
        ArrayList<Domini> llistaFiltradaDomini = new ArrayList<>();

        for (Domini domini : dominis) {
            // Comporovar si coincideix algún nom amb el nom d'algún domini
            if (domini.getDomini().toLowerCase().contains(nomDomini.toLowerCase())) {
                llistaFiltradaDomini.add(domini);
            }
        }

        actulitzarDominis(llistaFiltradaDomini);
    }

    private void carregarDominis() {
        DominiDTO.RequestDomini requestDomini = DominiDTO.obtenirJSONDomini().create(DominiDTO.RequestDomini.class);
        requestDomini.getAllDominis().enqueue(new Callback<ArrayList<Domini>>() {
            @Override
            public void onResponse(Call<ArrayList<Domini>> call, Response<ArrayList<Domini>> response) {

                Log.d("CODE", String.valueOf(response.code()));

                if (response.isSuccessful() && response.body() != null) {
                    dominis.clear();
                    dominis.addAll(response.body());
                    actulitzarDominis(dominis);
                    recyclerDominis.setVisibility(View.VISIBLE);
                } else {
                    // TODO mostrar error con layout
                    recyclerDominis.setVisibility(View.GONE);
                    Log.d("ERROR_RESPONSE", response.message());

                    try {
                        Log.d("ERROR_BODY", response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Domini>> call, Throwable t) {
                recyclerDominis.setVisibility(View.GONE);
                Log.d("ERROR_FAILURE", t.getMessage());
                Log.e("ERROR_FAILURE", "Error complet", t);
            }
        });
    }

    private void actulitzarDominis(ArrayList<Domini> dominis) {
        dominiAdapter = new SDRDAdapter(dominis, domini -> {
            veureCrearEditarDomini(0, domini);
        }, DominisActivity.this);

        recyclerDominis.setAdapter(dominiAdapter);
    }

    private void veureCrearEditarDomini(int view_add_edit, Domini domini) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_domini, null);

        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Elements del AlertDialog
        TextView txtNomDomini = view.findViewById(R.id.txtNomDomini);
        EditText etNomDomini = view.findViewById(R.id.etNomDomini);
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
        Button btnGuardarEliminarDomini = view.findViewById(R.id.btnGuardarEliminarDomini);
        Button btnBackCancelar = view.findViewById(R.id.btnBackCancelar);

        txtSucursal.setVisibility(View.VISIBLE);
        llSucursalView.setVisibility(View.VISIBLE);
        llSucursalEdit.setVisibility(View.GONE);

        imgBtnEditar.setOnClickListener(v -> {
            alertDialog.dismiss();
            veureCrearEditarDomini(2, domini);
        });

        if (view_add_edit == 0) { // Veure domini

            txtNomDomini.setVisibility(View.VISIBLE);
            imgBtnEditar.setVisibility(View.VISIBLE);
            etNomDomini.setVisibility(View.GONE);
            txtSucursals.setVisibility(View.GONE);
            spSucursals.setVisibility(View.GONE);

            // Nom domini
            if (domini.getDomini() != null && !domini.getDomini().isEmpty()) {
                txtNomDomini.setText(domini.getDomini());
            } else {
                txtNomDomini.setText("No té nom");
            }

            if (domini.getSucursal() != null) {
                llSucursalNormal.setVisibility(View.VISIBLE);
                includeSurcursal.setVisibility(View.VISIBLE);

                Sucursal sucursal = domini.getSucursal();

                // Obtenir la sucursal
                Call<Sucursal> call = SucursalDTO.obtenirJSONSucursal().create(SucursalDTO.RequestSucursal.class).getSucursal(sucursal.getUuid().toString());
                call.enqueue(new Callback<Sucursal>() {
                    @Override
                    public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
                        if (response.isSuccessful()) {
                            sucursalDomini = response.body();

                            // Nom sucursal
                            if (sucursalDomini.getNom() != null && !sucursalDomini.getNom().isEmpty()) {
                                txtSucursal.setText(sucursalDomini.getNom());
                            }

                            // Direcció sucursal
                            if (sucursalDomini.getDireccio() != null && !sucursalDomini.getDireccio().isEmpty()) {
                                String direccioSucursal = txtDireccioSucursal.getText().toString() + " " + sucursalDomini.getDireccio();
                                txtDireccioSucursal.setText(direccioSucursal);
                            }

                            // Ciutat sucursal
                            if (sucursalDomini.getCiutat() != null && !sucursalDomini.getCiutat().isEmpty()) {
                                String ciutatSucursal = txtCiutatSucursal.getText().toString() + " " + sucursalDomini.getCiutat();
                                txtCiutatSucursal.setText(ciutatSucursal);
                            }

                            // Pais sucursal
                            if (sucursalDomini.getPais() != null && !sucursalDomini.getPais().isEmpty()) {
                                String paisSucursal = txtPaisSucursal.getText().toString() + " " + sucursalDomini.getPais();
                                txtPaisSucursal.setText(paisSucursal);
                            }

                            // Telèfon sucursal
                            if (sucursalDomini.getTelefon() != null && !sucursalDomini.getTelefon().isEmpty()) {
                                String tlfSucursal = txtTlfSucursal.getText().toString() + " " + sucursalDomini.getTelefon();
                                txtTlfSucursal.setText(tlfSucursal);
                            }

                            // Correu sucursal
                            if (sucursalDomini.getCorreu() != null && !sucursalDomini.getCorreu().isEmpty()) {
                                String correuSucursal = txtCorreuSucursal.getText().toString() + " " + sucursalDomini.getCorreu();
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

            btnGuardarEliminarDomini.setText("Eliminar");
            btnGuardarEliminarDomini.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnGuardarEliminarDomini.setBackground(ContextCompat.getDrawable(this, R.drawable.background_button_eliminar));
            btnGuardarEliminarDomini.setOnClickListener(v -> {
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

                txtPregunta.setText("Desitja eliminar el domini \"" + domini.getDomini() + "\" ?");
                btnEliminar.setOnClickListener(c -> {
                    eliminarDomini(domini, alertDialog2);
                    alertDialog.dismiss();
                    alertDialog2.dismiss();
                });

                btnCancelar.setOnClickListener(c -> {
                    alertDialog2.dismiss();
                    veureCrearEditarDomini(0, domini);
                });
            });

            btnBackCancelar.setText("Enrere");
            btnBackCancelar.setOnClickListener(v -> {
                alertDialog.dismiss();
            });

        } else { // Crear o editar domini
            // Ocultar elements
            txtNomDomini.setVisibility(View.GONE);
            imgBtnEditar.setVisibility(View.GONE);
            includeSurcursal.setVisibility(View.GONE);

            // Mostrar elements
            etNomDomini.setVisibility(View.VISIBLE);
            txtSucursals.setVisibility(View.VISIBLE);
            spSucursals.setVisibility(View.VISIBLE);

            // Botó guardar domini
            btnGuardarEliminarDomini.setText("Guardar");
            btnGuardarEliminarDomini.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnGuardarEliminarDomini.setBackground(ContextCompat.getDrawable(this, R.drawable.background_button_purple));

            // Obtenir totes les sucursals
            obtenirSucursals(spSucursals);

            if (view_add_edit == 2) {
                if (domini.getDomini() != null && !domini.getDomini().isEmpty()) {
                    etNomDomini.setText(domini.getDomini());
                }
                if (domini.getSucursal() != null) {
                    String uuidActual = domini.getSucursal().getUuid().toString();
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

            btnGuardarEliminarDomini.setOnClickListener(v -> {
                String nomDomini = etNomDomini.getText().toString().trim();
                if (nomDomini.isEmpty()) {
                    etNomDomini.setError("Nom domini obligatori");
                    return;
                }
                DominiRequest dominiRequest = new DominiRequest(uuidSucursal, nomDomini);
                if (view_add_edit == 1) {
                    afegirDomini(dominiRequest, alertDialog);
                } else {
                    editarDomini(dominiRequest, alertDialog, domini.getUuid().toString());
                }
            });

            btnBackCancelar.setText("Cancel·lar");
            btnBackCancelar.setOnClickListener(v -> alertDialog.dismiss());
        }
    }

    private void afegirDomini(DominiRequest dominiRequest, AlertDialog alertDialog){
        String nomDomini = dominiRequest.getDomini();
        Call<Domini> call = DominiDTO.obtenirJSONDomini().create(DominiDTO.RequestDomini.class).crearDomini(dominiRequest);
        call.enqueue(new Callback<Domini>() {
            @Override
            public void onResponse(Call<Domini> call, Response<Domini> response) {
                if (response.isSuccessful()) {
                    dominis.add(response.body());
                    dominiAdapter.notifyDataSetChanged();
                    Toast.makeText(DominisActivity.this, "Domini " + nomDomini + " afegit", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    onResume();
                } else {
                    Toast.makeText(DominisActivity.this, "No s'ha pogut afegir el domini" + nomDomini, Toast.LENGTH_SHORT).show();
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Domini> call, Throwable t) {
                Toast.makeText(DominisActivity.this, "No s'ha pogut afegir el domini" + nomDomini, Toast.LENGTH_SHORT).show();
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    private void editarDomini(DominiRequest dominiRequest, AlertDialog alertDialog, String uuid) {
        String nomDomini = dominiRequest.getDomini();
        Call<Domini> call = DominiDTO.obtenirJSONDomini().create(DominiDTO.RequestDomini.class).actualitzaDomini(uuid, dominiRequest);
        call.enqueue(new Callback<Domini>() {
            @Override
            public void onResponse(Call<Domini> call, Response<Domini> response) {
                if (response.isSuccessful()) {
                    dominiAdapter.notifyDataSetChanged();
                    Toast.makeText(DominisActivity.this, "Domini " + nomDomini + " actualitzat", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    veureCrearEditarDomini(0, response.body());
                } else {
                    Toast.makeText(DominisActivity.this, "No s'ha pogut actualitzar el domini" + nomDomini, Toast.LENGTH_SHORT).show();
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Domini> call, Throwable t) {
                Toast.makeText(DominisActivity.this, "No s'ha pogut actualitzar el domini" + nomDomini, Toast.LENGTH_SHORT).show();
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    private void eliminarDomini(Domini domini, AlertDialog alertDialog) {
        String nomDomini = domini.getDomini();
        Call<Domini> call = DominiDTO.obtenirJSONDomini().create(DominiDTO.RequestDomini.class).eliminarDomini(domini.getUuid().toString());
        call.enqueue(new Callback<Domini>() {
            @Override
            public void onResponse(Call<Domini> call, Response<Domini> response) {
                if (response.isSuccessful()) {
                    dominis.remove(domini);
                    dominiAdapter.notifyDataSetChanged();
                    Toast.makeText(DominisActivity.this, "Domini " + nomDomini + " eliminat", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    onResume();
                } else {
                    Toast.makeText(DominisActivity.this, "No s'ha pogut eliminar el domini" + nomDomini, Toast.LENGTH_SHORT).show();
                    Log.e("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Domini> call, Throwable t) {
                Toast.makeText(DominisActivity.this, "No s'ha pogut eliminar el domini" + nomDomini, Toast.LENGTH_SHORT).show();
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DominisActivity.this, android.R.layout.simple_spinner_item, nomSucursals);
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