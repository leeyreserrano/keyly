package com.example.keyly_projecte_intermodular;

import static com.example.keyly_projecte_intermodular.utils.LogOutService.logOut;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.example.keyly_projecte_intermodular.adapters.SDRDAdapter;
import com.example.keyly_projecte_intermodular.gestions.GestionsIdiomes;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepartamentsActivity extends AppCompatActivity {

    private RecyclerView recyclerDepartaments;
    private LinearLayout layoutError;
    private SDRDAdapter departamentAdapter;
    private EditText etCercar;
    private ImageButton imgBtnAjuda, imgBtnIdioma, imgBtnLogOut, imgBtnBack, imgBtnAfegirDepartament;
    private UUID uuidSucursal = null;
    private Sucursal sucursalDepartament = null;
    private ArrayList<Departament> departaments = new ArrayList<>();
    private ArrayList<Sucursal> sucursals = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(GestionsIdiomes.aplicarIdioma(newBase));
    }

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
        departamentAdapter = new SDRDAdapter(departaments, departament -> {
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
                                String direccioSucursal = getString(R.string.etiquetaAdrecaOmplerta) + " " + sucursalDepartament.getDireccio();
                                txtDireccioSucursal.setText(direccioSucursal);
                            } else {
                                txtDireccioSucursal.setText(getString(R.string.etiquetaAdrecaBuida));
                            }

                            // Ciutat sucursal
                            if (sucursalDepartament.getCiutat() != null && !sucursalDepartament.getCiutat().isEmpty()) {
                                String ciutatSucursal = getString(R.string.etiquetaCiutatOmplerta) + " " + sucursalDepartament.getCiutat();
                                txtCiutatSucursal.setText(ciutatSucursal);
                            } else {
                                txtCiutatSucursal.setText(getString(R.string.etiquetaCiutatBuida));
                            }

                            // Pais sucursal
                            if (sucursalDepartament.getPais() != null && !sucursalDepartament.getPais().isEmpty()) {
                                String paisSucursal = getString(R.string.etiquetaPaisOmplert) + " " + sucursalDepartament.getPais();
                                txtPaisSucursal.setText(paisSucursal);
                            } else {
                                txtPaisSucursal.setText(getString(R.string.etiquetaPaisBuit));
                            }

                            // Telèfon sucursal
                            if (sucursalDepartament.getTelefon() != null && !sucursalDepartament.getTelefon().isEmpty()) {
                                String tlfSucursal = getString(R.string.etiquetaTlfOmplert) + " " + sucursalDepartament.getTelefon();
                                txtTlfSucursal.setText(tlfSucursal);
                            } else {
                                txtTlfSucursal.setText(getString(R.string.etiquetaTlfBuit));
                            }

                            // Correu sucursal
                            if (sucursalDepartament.getCorreu() != null && !sucursalDepartament.getCorreu().isEmpty()) {
                                String correuSucursal = getString(R.string.etiquetaCorreuOmplert) + " " + sucursalDepartament.getCorreu();
                                txtCorreuSucursal.setText(correuSucursal);
                            } else {
                                txtCorreuSucursal.setText(getString(R.string.etiquetaCorreuBuit));
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

            btnGuardarEliminarDepartament.setText(getString(R.string.btnEliminar));
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

                txtPregunta.setText(getString(R.string.etiquetaEliminarDepartament) + " " + departament.getNom() + "\" ?");
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

            btnBackCancelar.setText(getString(R.string.btnEnrere));
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
            btnGuardarEliminarDepartament.setText(getString(R.string.btnGuardar));
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

            btnBackCancelar.setText(getString(R.string.btnCancelar));
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
                    Toast.makeText(DepartamentsActivity.this, getString(R.string.toastDepartamentCreat, nomDepartament), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    onResume();
                } else {
                    Toast.makeText(DepartamentsActivity.this, getString(R.string.toastDepartamentNoCreat, nomDepartament), Toast.LENGTH_SHORT).show();
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Departament> call, Throwable t) {
                Toast.makeText(DepartamentsActivity.this, getString(R.string.toastDepartamentNoCreat, nomDepartament), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(DepartamentsActivity.this, getString(R.string.toastDepartamentEditat, nomDepartament), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    veureCrearEditarDepartament(0, response.body());
                } else {
                    Toast.makeText(DepartamentsActivity.this, getString(R.string.toastDepartamentNoEditat, nomDepartament), Toast.LENGTH_SHORT).show();
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Departament> call, Throwable t) {
                Toast.makeText(DepartamentsActivity.this, getString(R.string.toastDepartamentNoEditat, nomDepartament), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(DepartamentsActivity.this, getString(R.string.toastDepartamentEliminat, nomDepartament), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    onResume();
                } else {
                    Toast.makeText(DepartamentsActivity.this, getString(R.string.toastDepartamentNoEliminat, nomDepartament), Toast.LENGTH_SHORT).show();
                    Log.e("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Departament> call, Throwable t) {
                Toast.makeText(DepartamentsActivity.this, getString(R.string.toastDepartamentNoEliminat, nomDepartament), Toast.LENGTH_SHORT).show();
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