package com.example.keyly_projecte_intermodular;

import static com.example.keyly_projecte_intermodular.config.TokenForEver.clauMestra;
import static com.example.keyly_projecte_intermodular.config.TokenForEver.getImage;
import static com.example.keyly_projecte_intermodular.config.TokenForEver.usuariPropi;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.keyly_projecte_intermodular.dao.Departament;
import com.example.keyly_projecte_intermodular.dao.Rol;
import com.example.keyly_projecte_intermodular.dao.Sucursal;
import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.dto.DepartamentDTO;
import com.example.keyly_projecte_intermodular.dto.SucursalDTO;
import com.example.keyly_projecte_intermodular.dto.UsuariDTO;
import com.example.keyly_projecte_intermodular.request.UsuariRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilActivity extends AppCompatActivity {

    private ImageView imgVPerfil;
    private LinearLayout llSucursal, llDepartament, llRol, llPassword, llActions, llGestions,
            llGestioUsuaris, llGestioSucursals, llGestioDepartaments, llGestioRols, llGestioDominis,
            llTempsExpItems;
    private FrameLayout flSucursal, flDepartament, flRol;
    private TextView txtNomUsuari, txtCorreu, txtSucursal, txtDepartament, txtRol;
    private EditText etNomUsuari, etCorreu, etClauMestra;
    private Spinner spSucursal, spDepartament, spRol, spTempsExpItems;
    private ImageButton imgBtnAddImg, imgBtnEditarUsuari, imgBtnCopy, imgBtnEye, imgBtnGenerate,
            imgBtnGuardarTempsExpItems;
    private Button btnModificarGuardarContra, btnCancelarPass, btnGuardar, btnCancelar, btnEliminar;
    private BottomNavigationView menu;
    private String clauMestraActual = clauMestra;
    private boolean isPasswordVisible = false, esCreant = false;
    private Usuari usuariActual;
    private ArrayList<Sucursal> sucursals = new ArrayList<>();
    private ArrayList<Departament> departaments = new ArrayList<>(), departamentsFiltrats = new ArrayList<>();
    private ArrayList<Rol> rols = new ArrayList<>(), rolsFiltrats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /*CAMPS*/
        imgVPerfil = findViewById(R.id.imgVPerfil);
        imgBtnAddImg = findViewById(R.id.imgBtnAddImg);
        txtNomUsuari = findViewById(R.id.txtNomUsuari);
        etNomUsuari = findViewById(R.id.etNomUsuari);
        imgBtnEditarUsuari = findViewById(R.id.imgBtnEditarUsuari);
        txtCorreu = findViewById(R.id.txtCorreu);
        etCorreu = findViewById(R.id.etCorreu);
        llSucursal = findViewById(R.id.llSucursal);
        txtSucursal = findViewById(R.id.txtSucursal);
        flSucursal = findViewById(R.id.flSucursal);
        spSucursal = findViewById(R.id.spSucursal);
        llDepartament = findViewById(R.id.llDepartament);
        txtDepartament = findViewById(R.id.txtDepartament);
        flDepartament = findViewById(R.id.flDepartament);
        spDepartament = findViewById(R.id.spDepartament);
        llRol = findViewById(R.id.llRol);
        txtRol = findViewById(R.id.txtRol);
        flRol = findViewById(R.id.flRol);
        spRol = findViewById(R.id.spRol);
        llPassword = findViewById(R.id.ll_password);
        etClauMestra = findViewById(R.id.etClauMestra);
        llActions = findViewById(R.id.llActions);
        imgBtnCopy = findViewById(R.id.imgBtnCopy);
        imgBtnEye = findViewById(R.id.imgBtnEye);
        imgBtnGenerate = findViewById(R.id.imgBtnGenerate);
//        btnModificarGuardarContra = findViewById(R.id.btnModificarGuardarContra);
//        btnCancelarPass = findViewById(R.id.btnCancelarPass);
        llGestions = findViewById(R.id.llGestions);
        llGestioUsuaris = findViewById(R.id.llGestioUsuaris);
        llGestioSucursals = findViewById(R.id.llGestioSucursals);
        llGestioDepartaments = findViewById(R.id.llGestioDepartaments);
        llGestioRols = findViewById(R.id.llGestioRols);
        llGestioDominis = findViewById(R.id.llGestioDominis);
        llTempsExpItems = findViewById(R.id.llTempsExpItems);
        spTempsExpItems = findViewById(R.id.spTempsExpItems);
        imgBtnGuardarTempsExpItems = findViewById(R.id.imgBtnGuardarTempsExpItems);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnEliminar = findViewById(R.id.btnEliminar);
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
                return true;
            }
            return false;
        });

        btnEliminar.setOnClickListener(v -> {
            // TODO eliminar usuari
        });

        // Obtenir la imatge de perfil
        getImage(imatge -> {
            try {
                SVG svg = SVG.getFromString(imatge);
                Drawable drawable = new PictureDrawable(svg.renderToPicture());
                imgVPerfil.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                imgVPerfil.setImageDrawable(drawable);
            } catch (SVGParseException e) {
                Log.e("SVG_ERROR", e.getMessage());
            }
        });

        imgBtnEditarUsuari.setOnClickListener(v -> {
            modeEdicio(false);
        });

        imgBtnCopy.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("Clau maestra", etClauMestra.getText());
            clipboardManager.setPrimaryClip(clipData);

            Toast.makeText(this, "Clau mestra copiada", Toast.LENGTH_SHORT).show();
        });

        isPasswordVisible = false;
        imgBtnEye.setOnClickListener(v -> {
            if (isPasswordVisible) {
                isPasswordVisible = false;
                etClauMestra.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                isPasswordVisible = true;
                etClauMestra.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });

//        btnCancelarPass.setOnClickListener(v -> {
//            actualitzarPantalla(usuariActual.isPotAdministrar());
//        });

        llGestioUsuaris.setOnClickListener(v -> {
            // TODO mostrar pantalla de gestió d'usuaris
            Intent intent = new Intent(this, UsuarisActivity.class);
            startActivity(intent);
        });

        llGestioSucursals.setOnClickListener(v -> {
            Intent intent = new Intent(this, SucursalsActivity.class);
            startActivity(intent);
        });

        llGestioDepartaments.setOnClickListener(v -> {
            Intent intent = new Intent(this, DepartamentsActivity.class);
            startActivity(intent);
        });

        llGestioRols.setOnClickListener(v -> {
            // TODO mostrar pantalla de gestió de rols
            Intent intent = new Intent(this, RolsActivity.class);
            startActivity(intent);
        });

        llGestioDominis.setOnClickListener(v -> {
            // TODO mostrar pantalla de gestió de dominis
        });

        imgBtnGuardarTempsExpItems.setOnClickListener(v -> {
            // TODO guardar temps d'expedició d'items
        });

        btnGuardar.setOnClickListener(v -> {
            // TODO guardar usuari
            //pujarUsuari(nouUsuari());
        });

        btnCancelar.setOnClickListener(v -> {
            if (esCreant) {
                finish();
            } else {
                actualitzarPantalla(false);
            }
        });

        boolean usuariP = getIntent().getBooleanExtra("usuariPropi", false);
        esCreant = getIntent().getBooleanExtra("esCreant", false);
        if (usuariP) {
            usuariActual = usuariPropi;
            menu.setVisibility(View.VISIBLE);
        } else if (esCreant) {
            usuariActual = usuariPropi;
            btnEliminar.setVisibility(View.GONE);
            modeEdicio(true);
        } else {
            usuariActual = (Usuari) getIntent().getSerializableExtra("usuari");
            menu.setVisibility(View.GONE);
        }

//        if (usuariActual == null) {
////            Log.e("PerfilActivity", "usuariActual es null");
////            finish();
////            return;
//            usuariActual = usuariPropi;
//        }
//
//        Log.d("USUARI_ACTUAL", usuariActual.toString());

//        if (usuariActual.getRolIntern().equals("ADMIN")) {
//
//            Log.d("ROL_INTER", usuariActual.getRolIntern());
//
//            if (usuariP) {
//                btnEliminar.setVisibility(View.GONE);
//                actualitzarPantalla(true);
//            } else {
//                btnEliminar.setVisibility(View.VISIBLE);
//                actualitzarPantalla(true);
//            }
//
//        } else {
//            actualitzarPantalla(false);
//        }
//
//        if (usuariPropi.isPotAdministrar()) {
//            if (usuariP) {
//                usuariActual = usuariPropi;
//                // Actualitzar la pantalla segons si és usuari o administrador
//                actualitzarPantalla(usuariActual.isPotAdministrar());
//                btnEliminar.setVisibility(View.GONE);
//            } else {
//                if (esCreant) {
//                    btnEliminar.setVisibility(View.GONE);
//                    modeEdicio(true);
//                } else {
//                    btnEliminar.setVisibility(View.VISIBLE);
//                }
//            }
//        } else {
//            usuariActual = (Usuari) getIntent().getSerializableExtra("usuari");
//            actualitzarPantalla(false);
//        }
    }

    private void actualitzarPantalla(boolean admin) {

        // Ocultar elements
        etNomUsuari.setVisibility(View.GONE);
        etCorreu.setVisibility(View.GONE);
        flSucursal.setVisibility(View.GONE);
        flDepartament.setVisibility(View.GONE);
        flRol.setVisibility(View.GONE);

        // Mostrar elements
        imgBtnAddImg.setVisibility(View.VISIBLE);
        txtNomUsuari.setVisibility(View.VISIBLE);
        imgBtnEditarUsuari.setVisibility(View.VISIBLE);
        txtCorreu.setVisibility(View.VISIBLE);
        txtSucursal.setVisibility(View.VISIBLE);
        txtDepartament.setVisibility(View.VISIBLE);
        txtRol.setVisibility(View.VISIBLE);
        llActions.setVisibility(View.VISIBLE);

        // Nom usuari
        txtNomUsuari.setText(usuariActual.getNom());

        // Nom correu
        txtCorreu.setText(usuariActual.getCorreu());

        // Sucursal usuari
        if (usuariActual.getSucursal().getNom() != null && usuariActual.getSucursal().getNom() != "") {
            llSucursal.setVisibility(View.VISIBLE);
            txtSucursal.setText(usuariActual.getSucursal().getNom());
        } else {
            llSucursal.setVisibility(View.GONE);
        }

        // Departament usuari
        if (usuariActual.getDepartament().getNom() != null && usuariActual.getDepartament().getNom() != "") {
            llDepartament.setVisibility(View.VISIBLE);
            txtDepartament.setText(usuariActual.getDepartament().getNom());
        } else {
            llDepartament.setVisibility(View.GONE);
        }

        // Rol usuari
        if (usuariActual.getRol().getNom() != null && usuariActual.getRol().getNom() != "") {
            llRol.setVisibility(View.VISIBLE);
            txtRol.setText(usuariActual.getRol().getNom());
        } else {
            llRol.setVisibility(View.GONE);
        }

        // Clau mestra usuari
        llPassword.setBackground(ContextCompat.getDrawable(this, R.drawable.backgroung_edit_text_password));
        etClauMestra.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etClauMestra.setTextColor(ContextCompat.getColor(this, R.color.not_enabled));
        etClauMestra.setEnabled(false);
        etClauMestra.setText(clauMestraActual);
        imgBtnGenerate.setVisibility(View.GONE);

//            btnModificarGuardarContra.setVisibility(View.VISIBLE);
//            btnModificarGuardarContra.setText("Modificar");
//            btnModificarGuardarContra.setOnClickListener(v -> {
//                // TODO modificar clave maestra
//                UsuariRequest usuariRequest = null;
//                try {
//                    usuariRequest = nouUsuari(etClauMestra.getText().toString());
//                } catch (NoSuchAlgorithmException e) {
//                    throw new RuntimeException(e);
//                } catch (InvalidKeySpecException e) {
//                    throw new RuntimeException(e);
//                } catch (InvalidAlgorithmParameterException e) {
//                    throw new RuntimeException(e);
//                } catch (NoSuchPaddingException e) {
//                    throw new RuntimeException(e);
//                } catch (IllegalBlockSizeException e) {
//                    throw new RuntimeException(e);
//                } catch (BadPaddingException e) {
//                    throw new RuntimeException(e);
//                } catch (InvalidKeyException e) {
//                    throw new RuntimeException(e);
//                }
//
//                pujarUsuari(usuariRequest);
//            });

        btnGuardar.setVisibility(View.GONE);
        btnCancelar.setVisibility(View.GONE);

        if (admin) { // Vista d'administrador
            llGestions.setVisibility(View.VISIBLE);
            llTempsExpItems.setVisibility(View.VISIBLE);
        } else { // Vista d'usuari normal
            llGestions.setVisibility(View.GONE);
            llTempsExpItems.setVisibility(View.GONE);
        }
    }

    private void modeEdicio(boolean esCreant) {
        // Ocultar elements
        imgBtnAddImg.setVisibility(View.VISIBLE);
        txtNomUsuari.setVisibility(View.GONE);
        txtCorreu.setVisibility(View.GONE);
        txtSucursal.setVisibility(View.GONE);
        txtDepartament.setVisibility(View.GONE);
        txtRol.setVisibility(View.GONE);
        llActions.setVisibility(View.GONE);
        llGestions.setVisibility(View.GONE);
        llTempsExpItems.setVisibility(View.GONE);

        // Mostrar elements
        etNomUsuari.setVisibility(View.VISIBLE);
        etCorreu.setVisibility(View.VISIBLE);
        flSucursal.setVisibility(View.VISIBLE);
        flDepartament.setVisibility(View.VISIBLE);
        flRol.setVisibility(View.VISIBLE);
        imgBtnGenerate.setVisibility(View.VISIBLE);
        btnGuardar.setVisibility(View.VISIBLE);
        btnCancelar.setVisibility(View.VISIBLE);


        if (!esCreant) {
            if (usuariActual.getNom() != null && usuariActual.getNom() != "")
                txtNomUsuari.setText(usuariActual.getNom());

            if (usuariActual.getCorreu() != null && usuariActual.getCorreu() != "")
            txtCorreu.setText(usuariActual.getCorreu());

            if (usuariActual.getSucursal().getNom() != null && usuariActual.getSucursal().getNom() != "") {
                txtSucursal.setText(usuariActual.getSucursal().getNom());
            } else {
                txtSucursal.setText("Sense sucursal");
            }

            if (usuariActual.getDepartament().getNom() != null && usuariActual.getDepartament().getNom() != "") {
                txtDepartament.setText(usuariActual.getDepartament().getNom());
            } else {
                txtDepartament.setText("Sense departament");
            }

            if (usuariActual.getRol().getNom() != null && usuariActual.getRol().getNom() != "") {
                txtRol.setText(usuariActual.getRol().getNom());
            } else {
                txtRol.setText("Sense rol");
            }

            llPassword.setBackground(ContextCompat.getDrawable(this, R.drawable.background_text_notes));

            etClauMestra.setInputType(InputType.TYPE_CLASS_TEXT);
            etClauMestra.setTextColor(ContextCompat.getColor(this, R.color.black));
            etClauMestra.setEnabled(true);
            etClauMestra.setText(clauMestraActual);
        } else {
            obtenirSucursals(spSucursal, spDepartament, spRol);
        }
    }

//    private UsuariRequest nouUsuari(String novaClauMestra) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
//        // TODO modificar clau mestra
////        modeEdicio(false);
//
//        // TODO generar par claves
//        Encrypt.ParellClaus parellClaus = generarKeyPair();
//
//        // TODO generar kdfSalt
//        byte[] kdfSalt = generarKdfSalt();
//        // Convertir a Base64 el kdfSalt
//        String kdfSaltB64 = Base64.encodeToString(kdfSalt, Base64.NO_WRAP);
//
//        // TODO generar clau derivada
//        SecretKey secretKey = generarClauDerivada(novaClauMestra, kdfSaltB64);
//
//        // TODO cifrar privateKey
//        PrivateKey privateKey = parellClaus.getKeyPair().getPrivate();
//        String clauPrivadaEncriptada = encriptarClauPrivada(secretKey, privateKey);
//
//        return new UsuariRequest(
//                usuariActual.getSucursal().getUuid(),
//                usuariActual.getDepartament().getUuid(),
//                usuariActual.getRol().getUuid(),
//                usuariActual.getNom(),
//                usuariActual.getCorreu(),
//                novaClauMestra,
//                kdfSaltB64,
//                parellClaus.getKeyPair().getPublic(),
//                clauPrivadaEncriptada,
//                usuariActual.isPotAdministrar()
//        );
//    }

    private void pujarUsuari(UsuariRequest usuariRequest) {
        Call<Usuari> call = UsuariDTO.obtenirJSONUsuari().create(UsuariDTO.RequestUsuari.class).actualitzarUsuari(usuariRequest);
        call.enqueue(new Callback<Usuari>() {
            @Override
            public void onResponse(Call<Usuari> call, Response<Usuari> response) {
                if (response.isSuccessful()) {
                    usuariActual = response.body();
                    actualitzarPantalla(false);
                } else {
                    Log.e("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Usuari> call, Throwable t) {
                Log.e("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    private void obtenirSucursals(Spinner spSucursals, Spinner spDepartaments, Spinner spRols) {
        Call<ArrayList<Sucursal>> call = SucursalDTO.obtenirJSONSucursal().create(SucursalDTO.RequestSucursal.class).getAllSucursals();
        call.enqueue(new Callback<ArrayList<Sucursal>>() {
            @Override
            public void onResponse(Call<ArrayList<Sucursal>> call, Response<ArrayList<Sucursal>> response) {
                if (response.isSuccessful()) {
                    sucursals.clear();
                    sucursals.addAll(response.body());
                    Log.d("SUCURSALS_DEP", sucursals.toString());
                    ArrayList<String> nomSucursals = new ArrayList<>();
                    nomSucursals.add("Sense sucursal");
                    for (Sucursal s : sucursals) {
                        nomSucursals.add(s.getNom());
                    }
                    // Inserir les sucursals en l'spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(PerfilActivity.this, android.R.layout.simple_spinner_item, nomSucursals);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSucursals.setAdapter(adapter);

                    // Obtenir els altres
                    obtenirDepartaments(spDepartaments, spSucursals);
                    obtenirRols(spRols, spSucursals);
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

    private void obtenirDepartaments(Spinner spDepartaments, Spinner spSucursals) {
        Call<ArrayList<Departament>> call = DepartamentDTO.obtenirJSONDepartament().create(DepartamentDTO.RequestDepartament.class).getAllDepartaments();
        call.enqueue(new Callback<ArrayList<Departament>>() {
            @Override
            public void onResponse(Call<ArrayList<Departament>> call, Response<ArrayList<Departament>> response) {
                if (response.isSuccessful()) {
                    departaments.clear();
                    departamentsFiltrats.clear();
                    departaments.addAll(response.body());
                    if (spSucursals.getSelectedItem().toString().equals("Sense sucursal")) {
                        // TODO obtener departametos de sucursal seleccionada
                        int positionSucursal = spSucursals.getSelectedItemPosition();
                        UUID uuidSucursalTriada = sucursals.get(positionSucursal - 1).getUuid();
                        for (Departament d : departaments) {
                            if (d.getSucursal().getUuid().equals(uuidSucursalTriada)) {
                                departamentsFiltrats.add(d);
                            }
                        }
                    } else {
                        departamentsFiltrats.addAll(departaments);
                    }

                    // Obtenir noms departamens
                    ArrayList<String> nomDepartaments = new ArrayList<>();
                    nomDepartaments.add("Sense departament");
                    for (Departament d : departamentsFiltrats) {
                        nomDepartaments.add(d.getNom());
                    }
                    // Inserir els departaments en l'spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(PerfilActivity.this, android.R.layout.simple_spinner_item, nomDepartaments);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDepartaments.setAdapter(adapter);

//                    if (!spSucursals.getSelectedItem().toString().equals("Sense sucursal")) {
//                        // TODO seleccionar sucursal por uuid
//                        Sucursal sucursalTriada = departamentsFiltrats.get(1).getSucursal();
//                        for (int i = 0; i < sucursals.size(); i++) {
//                            if (sucursals.get(i).getUuid().equals(sucursalTriada.getUuid())) {
//                                spSucursals.setSelection(i);
//                                break;
//                            }
//                        }
//                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Departament>> call, Throwable t) {

            }
        });
    }

    private void obtenirRols (Spinner spRols, Spinner spSucursals) {

    }
}