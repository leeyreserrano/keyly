package com.example.keyly_projecte_intermodular;

import static com.example.keyly_projecte_intermodular.config.TokenForEver.clauMestra;
import static com.example.keyly_projecte_intermodular.config.TokenForEver.getImage;
import static com.example.keyly_projecte_intermodular.config.TokenForEver.usuariPropi;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.encriptarClauPrivada;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.generarClauDerivada;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.generarKdfSalt;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.generarKeyPair;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.keyly_projecte_intermodular.dao.Contrasenya;
import com.example.keyly_projecte_intermodular.dao.Departament;
import com.example.keyly_projecte_intermodular.dao.GeneradorContrasenya;
import com.example.keyly_projecte_intermodular.dao.Rol;
import com.example.keyly_projecte_intermodular.dao.Sucursal;
import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.dto.DepartamentDTO;
import com.example.keyly_projecte_intermodular.dto.RolDTO;
import com.example.keyly_projecte_intermodular.dto.SucursalDTO;
import com.example.keyly_projecte_intermodular.dto.UsuariDTO;
import com.example.keyly_projecte_intermodular.dto.UtilsDTO;
import com.example.keyly_projecte_intermodular.request.UsuariRequest;
import com.example.keyly_projecte_intermodular.utils.Encrypt;
import com.example.keyly_projecte_intermodular.utils.RolIntern;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilActivity extends AppCompatActivity {

    private ImageView imgVPerfil;
    private LinearLayout llSucursal, llDepartament, llRol, llPassword, llActions, llGestions,
            llGestioUsuaris, llGestioSucursals, llGestioDepartaments, llGestioRols, llGestioDominis,
            llTempsExpItems;
    private FrameLayout flRolIntern, flSucursal, flDepartament, flRol;
    private TextView txtNomUsuari, txtCorreu, txtSucursal, txtDepartament, txtRol;
    private EditText etNomUsuari, etCorreu, etClauMestra;
    private Spinner spRolIntern, spSucursal, spDepartament, spRol, spTempsExpItems;
    private CheckBox cbAdministrar;
    private ImageButton imgBtnAddImg, imgBtnEditarUsuari, imgBtnCopy, imgBtnEye, imgBtnGenerate,
            imgBtnGuardarTempsExpItems;
    private Button btnModificarGuardarContra, btnCancelarPass, btnGuardar, btnCancelar, btnEliminar;
    private BottomNavigationView menu;
    private String clauMestraActual = clauMestra;
    private boolean isPasswordVisible = false, esCreant = false, departamentsObtinguts = false,
            rolsObtinguts = false;
    private Usuari usuariActual;
    private ArrayList<String> nomDepartaments = new ArrayList<>();
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
        flRolIntern = findViewById(R.id.flRolIntern);
        spRolIntern = findViewById(R.id.spRolIntern);
        cbAdministrar = findViewById(R.id.cbAdministrar);
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

        if (esCreant) {
            menu.setVisibility(View.GONE);
            btnEliminar.setVisibility(View.GONE);
            modeEdicio(true);
            return;
        } else {
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
        }

        if (usuariP) {
            usuariActual = usuariPropi;
            menu.setVisibility(View.VISIBLE);
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

//        Log.d("USUARI_ACTUAL", usuariActual.toString());

//        if (usuariActual.getRolIntern().equals("ADMIN") && !esCreant) {
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
//        } else if (!esCreant) {
//            actualitzarPantalla(false);
//        }

        if (usuariActual != null) {
            if (usuariActual.getRolIntern().equals("ADMIN")) {
                btnEliminar.setVisibility(usuariP ? View.GONE : View.VISIBLE);
                actualitzarPantalla(true);
            } else {
                btnEliminar.setVisibility(View.GONE);
                actualitzarPantalla(false);
            }
        }

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
        flRolIntern.setVisibility(View.GONE);
        spRolIntern.setVisibility(View.GONE);
        cbAdministrar.setVisibility(View.GONE);
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
        imgBtnEditarUsuari.setVisibility(View.GONE);
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
        flRolIntern.setVisibility(View.VISIBLE);
        spRolIntern.setVisibility(View.VISIBLE);
        cbAdministrar.setVisibility(View.GONE); // TODO solo mostrar si es cap el rolintern
        flSucursal.setVisibility(View.VISIBLE);
        flDepartament.setVisibility(View.VISIBLE);
        flRol.setVisibility(View.VISIBLE);
        imgBtnGenerate.setVisibility(View.VISIBLE);
        btnGuardar.setVisibility(View.VISIBLE);
        btnCancelar.setVisibility(View.VISIBLE);

        imgVPerfil.setImageResource(R.drawable.foto_perfil);

        llPassword.setBackground(ContextCompat.getDrawable(this, R.drawable.background_text_notes));

        etClauMestra.setInputType(InputType.TYPE_CLASS_TEXT);
        etClauMestra.setTextColor(ContextCompat.getColor(this, R.color.black));
        etClauMestra.setEnabled(true);

        imgBtnGenerate.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_generar_contrasenya, null);

            builder.setView(view);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Elements del AlertDialog
            Button btnBaixa = view.findViewById(R.id.btnBaixa);
            Button btnMitjana = view.findViewById(R.id.btnMitjana);
            Button btnAlta = view.findViewById(R.id.btnAlta);
            Button btnPersonalitzada = view.findViewById(R.id.btnPersonalitzada);
            Button btnCancelar = view.findViewById(R.id.btnCancelar);

            // Botó Complexitat Baixa
            btnBaixa.setOnClickListener(c -> {
                GeneradorContrasenya gContrasenya = new GeneradorContrasenya(8, true, 1, true, 1, true, 1);
                generarContrasenya(gContrasenya, etClauMestra);
                alertDialog.dismiss();
            });

            // Botó Complexitat Mitjana
            btnMitjana.setOnClickListener(c -> {
                GeneradorContrasenya gContrasenya = new GeneradorContrasenya(12, true, 2, true, 2, true, 2);
                generarContrasenya(gContrasenya, etClauMestra);
                alertDialog.dismiss();
            });

            // Botó Complexitat Alta
            btnAlta.setOnClickListener(c -> {
                GeneradorContrasenya gContrasenya = new GeneradorContrasenya(20, true, 5, true, 5, true, 5);
                generarContrasenya(gContrasenya, etClauMestra);
                alertDialog.dismiss();
            });

            // Botó Complexitat Personalitzada
            btnPersonalitzada.setOnClickListener(c -> {
                alertDialog.dismiss();

                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                LayoutInflater inflater2 = getLayoutInflater();
                View view2 = inflater2.inflate(R.layout.layout_contrasenya_personalitzada, null);

                builder2.setView(view2);

                AlertDialog alertDialog2 = builder2.create();
                alertDialog2.show();

                contrasenyaPersonalitzada(alertDialog2, view2);
            });

            // Botó Cancelar
            btnCancelar.setOnClickListener(c -> {
                alertDialog.dismiss();
            });
        });


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

            etClauMestra.setText(clauMestraActual);
        } else {
            // Inserir els rols interns en l'spinner
            ArrayList<String> rolsInterns = new ArrayList<>();
            rolsInterns.add("Sense rol intern");
            for (RolIntern rol : RolIntern.values()) {
                rolsInterns.add(rol.name());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(PerfilActivity.this, android.R.layout.simple_spinner_item, rolsInterns);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spRolIntern.setAdapter(adapter);

            spRolIntern.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (spRolIntern.getSelectedItem().toString().equals("CAP")) {
                        cbAdministrar.setVisibility(View.VISIBLE);
                    } else {
                        cbAdministrar.setVisibility(View.GONE);
                        cbAdministrar.setChecked(false);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            obtenirSucursals(spSucursal, spDepartament, spRol);
            spSucursal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Obtenir els altres
                    omplirSpDepartaments(spDepartament, spSucursal);
                    obtenirRols(spRol, spSucursal);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spDepartament.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // TODO moverlo a un listener del spinner departamentos y que se haga cuando no sea "Sense departament"
                    if (!spDepartament.getSelectedItem().toString().equals("Sense departament")) {
                        if (spSucursal.getSelectedItem().toString().equals("Sense sucursal")) {
                            // TODO seleccionar sucursal por uuid
                            int positionDepartament = spDepartament.getSelectedItemPosition();
                            Departament departamentTriat = departaments.get(positionDepartament - 1);
                            Sucursal sucursalTriada = departamentTriat.getSucursal();
                            for (int i = 0; i < sucursals.size(); i++) {
                                if (sucursals.get(i).getUuid().equals(sucursalTriada.getUuid())) {
                                    spSucursal.setSelection(i + 1);
                                    break;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spRol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // TODO moverlo a un listener del spinner departamentos y que se haga cuando no sea "Sense departament"
                    if (!spRol.getSelectedItem().toString().equals("Sense rol")) {
                        if (spSucursal.getSelectedItem().toString().equals("Sense sucursal")) {
                            // TODO seleccionar sucursal por uuid
                            int positionDepartament = spRol.getSelectedItemPosition();
                            Rol rolTriat = rols.get(positionDepartament - 1);
                            Sucursal sucursalTriada = rolTriat.getSucursal();
                            for (int i = 0; i < sucursals.size(); i++) {
                                if (sucursals.get(i).getUuid().equals(sucursalTriada.getUuid())) {
                                    spSucursal.setSelection(i + 1);
                                    break;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            btnGuardar.setOnClickListener(v -> {
                // TODO poner obligación en nombre, contraseña y correo para rellenar
                String nomUsuari = etNomUsuari.getText().toString().trim();
                String correu = etCorreu.getText().toString().trim();
                Sucursal sucursal = null;
                Departament departament = null;
                Rol rol = null;

                if (nomUsuari.isEmpty()) {
                    Toast.makeText(this, "Has d'omplir el nom d'usuari!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (correu.isEmpty()) {
                    Toast.makeText(this, "Has d'omplir el correu!", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Sucursal sucursal1 : sucursals) {
                    if (sucursal1.getNom().equals(spSucursal.getSelectedItem().toString())) {
                        sucursal = sucursal1;
                        break;
                    }
                }

                if (sucursal == null) {
                    Toast.makeText(this, "Has d'omplir la sucursal!", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Departament departament1 : departaments) {
                    if (departament1.getNom().equals(spDepartament.getSelectedItem().toString())) {
                        departament = departament1;
                        break;
                    }
                }

                if (departament == null) {
                    Toast.makeText(this, "Has d'omplir el departament!", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Rol rol1 : rols) {
                    if (rol1.getNom().equals(spRol.getSelectedItem().toString())) {
                        rol = rol1;
                        break;
                    }
                }

                if (rol == null) {
                    Toast.makeText(this, "Has d'omplir el rol!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (spRolIntern.getSelectedItem().toString().equals("Sense rol intern")) {
                    Toast.makeText(this, "Has d'omplir el rol intern!", Toast.LENGTH_SHORT).show();
                    return;
                }

                RolIntern rolIntern = RolIntern.valueOf(spRolIntern.getSelectedItem().toString());

                boolean potAdministrar = cbAdministrar.isChecked();

                String novaClauMestra = null;
                if (etClauMestra.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Has d'omplir la clau mestra!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    novaClauMestra = etClauMestra.getText().toString();
                }

                // TODO generar par claves
                Encrypt.ParellClaus parellClaus = null;
                try {
                    parellClaus = generarKeyPair();
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }

                // TODO generar kdfSalt
                byte[] kdfSalt = generarKdfSalt();
                // Convertir a Base64 el kdfSalt
                String kdfSaltB64 = Base64.encodeToString(kdfSalt, Base64.NO_WRAP);

                // TODO obtenir clau pública
                String publicKey = parellClaus.getPublicKeyB64();

                // TODO generar clau derivada
                SecretKey secretKey = null;
                try {
                    secretKey = generarClauDerivada(novaClauMestra, kdfSaltB64);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeySpecException e) {
                    throw new RuntimeException(e);
                }

                // TODO cifrar privateKey
                PrivateKey privateKey = parellClaus.getKeyPair().getPrivate();
                String clauPrivadaEncriptada = null;
                try {
                    clauPrivadaEncriptada = encriptarClauPrivada(secretKey, privateKey);
                } catch (NoSuchPaddingException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (IllegalBlockSizeException e) {
                    throw new RuntimeException(e);
                } catch (BadPaddingException e) {
                    throw new RuntimeException(e);
                } catch (InvalidAlgorithmParameterException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException(e);
                }

                // Crear usuari nou
                UsuariRequest usuariRequest = new UsuariRequest(
                        sucursal.getUuid(), // UUID de sucursal
                        departament.getUuid(), // UUID de departament
                        rol.getUuid(), // UUID de rol
                        nomUsuari, // Nom usuari
                        correu, // Correu usuari
                        clauMestraActual, // Clau mestra usuari
                        kdfSaltB64, // kdfSalt
                        publicKey, // publicKey
                        clauPrivadaEncriptada, // encryptedPrivateKey
                        rolIntern, // Rol intern (ADMIN, CAP, USUARI)
                        potAdministrar // potAdministrar
                );

                Log.d("USUARI_REQUEST", usuariRequest.toString());

                // Pujar-lo al servidor
                pujarUsuari(usuariRequest);
            });
        }
    }

    private void generarContrasenya(GeneradorContrasenya gPassword, EditText etPassword) {
        Call<Contrasenya> call = UtilsDTO.obtenirJSONPassword().create(UtilsDTO.RequestUtils.class).generatePassword(gPassword);
        call.enqueue(new Callback<Contrasenya>() {
            @Override
            public void onResponse(Call<Contrasenya> call, Response<Contrasenya> response) {
                if (response.isSuccessful()) {
                    Contrasenya contrasenyaResponse = response.body();
                    etPassword.setText(contrasenyaResponse.getContrasenya());
                } else {
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Contrasenya> call, Throwable t) {
                Log.d("ERROR_RESPONSE", t.getMessage());
            }
        });
    }

    private void contrasenyaPersonalitzada(AlertDialog alertDialog, View view) {
        // Elements del AlertDialog
        TextView txtLongitud = view.findViewById(R.id.txtLongitud);
        SeekBar sbLongitud = view.findViewById(R.id.sbLongitud);
        CheckBox cbMin = view.findViewById(R.id.cbMin);
        CheckBox cbMay = view.findViewById(R.id.cbMay);
        CheckBox cbNum = view.findViewById(R.id.cbNum);
        CheckBox cbEspcials = view.findViewById(R.id.cbEspcials);
        EditText etContrasenyaGenerada = view.findViewById(R.id.etContrasenyaGenerada);
        ImageButton imbBtnCopy = view.findViewById(R.id.imbBtnCopy);
        Button btnUsar = view.findViewById(R.id.btnUsar);
        Button btnGenerar = view.findViewById(R.id.btnGenerar);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        sbLongitud.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtLongitud.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        imbBtnCopy.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("contrasenya", etClauMestra.getText());
            clipboardManager.setPrimaryClip(clipData);

            Toast.makeText(this, "Contrasenya copiada", Toast.LENGTH_SHORT).show();
        });

        btnUsar.setOnClickListener(v -> {
            // TODO guardar contrasenya
            etClauMestra.setText(etContrasenyaGenerada.getText());
            alertDialog.dismiss();
        });

        btnGenerar.setOnClickListener(v -> {
            // TODO generar contrasenya
            int longitut = Integer.parseInt(txtLongitud.getText().toString());
            int longitutRestant = longitut;
            int qtyMin = 0, qtyMay = 0, qtyNum = 0, qtyEspcials = 0;
            boolean may = false, num = false, espcials = false;

            HashMap<String, Integer> qty = new HashMap<>();

            if (cbMin.isChecked()) {
                longitutRestant--;
                qty.put("min", 1);
            }

            if (cbMay.isChecked()) {
                longitutRestant--;
                qty.put("may", 1);
                may = true;
            }

            if (cbNum.isChecked()) {
                longitutRestant--;
                qty.put("num", 1);
                num = true;
            }

            if (cbEspcials.isChecked()) {
                qtyEspcials++;
                longitutRestant--;
                qty.put("esp", 1);
                espcials = true;
            }

            int i = 1;

            for (HashMap.Entry<String, Integer> entry : qty.entrySet()) {
                if (i < qty.size()) {
                    int random = (int) (Math.random() * longitutRestant);
                    entry.setValue(random);
                    longitutRestant -= random;
                    Log.d("QTY", String.valueOf(qty.get(i)));
                    i++;
                } else {
                    entry.setValue(longitutRestant);
                }
            }

            if (qty.containsKey("may")) qtyMay = qty.get("may");
            if (qty.containsKey("num")) qtyNum = qty.get("num");
            if (qty.containsKey("esp")) qtyEspcials = qty.get("esp");

            GeneradorContrasenya gContrasenya = new GeneradorContrasenya(longitut, may, qtyMay, num, qtyNum, espcials, qtyEspcials);
            generarContrasenya(gContrasenya, etContrasenyaGenerada);
        });

        btnCancelar.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

    }

    private void pujarUsuari(UsuariRequest usuariRequest) {
        Call<Usuari> call = UsuariDTO.obtenirJSONUsuari().create(UsuariDTO.RequestUsuari.class).crearUsuari(usuariRequest);
        call.enqueue(new Callback<Usuari>() {
            @Override
            public void onResponse(Call<Usuari> call, Response<Usuari> response) {
                if (response.isSuccessful()) {
                    //usuariActual = response.body();
                    if (esCreant) {
                        finish();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("ERROR_RESPONSE", "Codi: " + response.code() + " | " + errorBody);
                        Toast.makeText(PerfilActivity.this, "Error: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e("ERROR_RESPONSE", "No s'ha pogut llegir l'error: " + e.getMessage());
                    }
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

    private void omplirSpDepartaments(Spinner spDepartaments, Spinner spSucursals) {
        if (departamentsObtinguts && !spDepartament.getSelectedItem().toString().equals("Sense departament")) {
                // TODO obtener departamento para comprobar si es de sucursal o no
                // Obtenir departament triat d'abans
                int posDepartamentTriat = spDepartament.getSelectedItemPosition();
                Departament departamentTriat = null;
                for (int i = 0; i < departaments.size(); i++) {
                    if (departaments.get(i).getUuid().equals(departamentsFiltrats.get(posDepartamentTriat - 1).getUuid())) {
                        departamentTriat = departaments.get(i);
                        break;
                    }
                }

                // Obtenir sucursal triat
                int posSucursalTriada = spSucursal.getSelectedItemPosition();
                Sucursal sucursalTriada = null;
                for (int i = 0; i < sucursals.size(); i++) {
                    if (sucursals.get(i).getUuid().equals(sucursals.get(posSucursalTriada - 1).getUuid())) {
                        sucursalTriada = sucursals.get(i);
                        break;
                    }
                }

                if (!departamentTriat.getSucursal().getUuid().equals(sucursalTriada.getUuid())) {
                    Call<ArrayList<Departament>> call = DepartamentDTO.obtenirJSONDepartament().create(DepartamentDTO.RequestDepartament.class).getAllDepartaments();
                    call.enqueue(new Callback<ArrayList<Departament>>() {
                        @Override
                        public void onResponse(Call<ArrayList<Departament>> call, Response<ArrayList<Departament>> response) {
                            if (response.isSuccessful()) {
                                departaments.clear();
                                departamentsFiltrats.clear();
                                departaments.addAll(response.body());
                                if (!spSucursals.getSelectedItem().toString().equals("Sense sucursal")) {
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
                                nomDepartaments.clear();
                                nomDepartaments.add("Sense departament");
                                for (Departament d : departamentsFiltrats) {
                                    nomDepartaments.add(d.getNom());
                                }
                                // Inserir els departaments en l'spinner
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(PerfilActivity.this, android.R.layout.simple_spinner_item, nomDepartaments);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spDepartaments.setAdapter(adapter);
                                departamentsObtinguts = true;
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<Departament>> call, Throwable t) {

                        }
                    });
                }
        } else {
            Call<ArrayList<Departament>> call = DepartamentDTO.obtenirJSONDepartament().create(DepartamentDTO.RequestDepartament.class).getAllDepartaments();
            call.enqueue(new Callback<ArrayList<Departament>>() {
                @Override
                public void onResponse(Call<ArrayList<Departament>> call, Response<ArrayList<Departament>> response) {
                    if (response.isSuccessful()) {
                        departaments.clear();
                        departamentsFiltrats.clear();
                        departaments.addAll(response.body());
                        if (!spSucursals.getSelectedItem().toString().equals("Sense sucursal")) {
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
                        nomDepartaments.clear();
                        nomDepartaments.add("Sense departament");
                        for (Departament d : departamentsFiltrats) {
                            nomDepartaments.add(d.getNom());
                        }
                        // Inserir els departaments en l'spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(PerfilActivity.this, android.R.layout.simple_spinner_item, nomDepartaments);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spDepartaments.setAdapter(adapter);
                        departamentsObtinguts = true;
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Departament>> call, Throwable t) {

                }
            });
        }
    }

    private void obtenirRols (Spinner spRols, Spinner spSucursals) {
        if (rolsObtinguts && !spRols.getSelectedItem().toString().equals("Sense rol")) {
            // TODO obtener rol para comprobar si es de sucursal o no
            // Obtenir rol triat d'abans
            int posRolTriat = spRols.getSelectedItemPosition();
            Rol rolTriat = null;
            for (int i = 0; i < rols.size(); i++) {
                if (rols.get(i).getUuid().equals(rolsFiltrats.get(posRolTriat - 1).getUuid())) {
                    rolTriat = rols.get(i);
                    break;
                }
            }

            // Obtenir sucursal triat
            int posSucursalTriada = spSucursal.getSelectedItemPosition();
            Sucursal sucursalTriada = null;
            for (int i = 0; i < sucursals.size(); i++) {
                if (sucursals.get(i).getUuid().equals(sucursals.get(posSucursalTriada - 1).getUuid())) {
                    sucursalTriada = sucursals.get(i);
                    break;
                }
            }

            if (!rolTriat.getSucursal().getUuid().equals(sucursalTriada.getUuid())) {
                Call<ArrayList<Rol>> call = RolDTO.obtenirJSONRol().create(RolDTO.RequestRol.class).getAllRols();
                call.enqueue(new Callback<ArrayList<Rol>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Rol>> call, Response<ArrayList<Rol>> response) {
                        if (response.isSuccessful()) {
                            rols.clear();
                            rolsFiltrats.clear();
                            rols.addAll(response.body());
                            if (!spSucursals.getSelectedItem().toString().equals("Sense sucursal")) {
                                int positionSucursal = spSucursals.getSelectedItemPosition();
                                UUID uuidSucursalTriada = sucursals.get(positionSucursal - 1).getUuid();
                                for (Rol r : rols) {
                                    if (r.getSucursal().getUuid().equals(uuidSucursalTriada)) {
                                        rolsFiltrats.add(r);
                                    }
                                }
                            } else {
                                rolsFiltrats.addAll(rols);
                            }

                            // Obtenir noms rols
                            ArrayList<String> nomsRols = new ArrayList<>();
                            nomsRols.add("Sense rol");
                            for (Rol r : rolsFiltrats) {
                                nomsRols.add(r.getNom());
                            }
                            // Inserir els departaments en l'spinner
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(PerfilActivity.this, android.R.layout.simple_spinner_item, nomsRols);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spRols.setAdapter(adapter);
                            rolsObtinguts = true;
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Rol>> call, Throwable t) {

                    }
                });
            }
        } else {
            Call<ArrayList<Rol>> call = RolDTO.obtenirJSONRol().create(RolDTO.RequestRol.class).getAllRols();
            call.enqueue(new Callback<ArrayList<Rol>>() {
                @Override
                public void onResponse(Call<ArrayList<Rol>> call, Response<ArrayList<Rol>> response) {
                    if (response.isSuccessful()) {
                        rols.clear();
                        rolsFiltrats.clear();
                        rols.addAll(response.body());
                        if (!spSucursals.getSelectedItem().toString().equals("Sense sucursal")) {
                            int positionSucursal = spSucursals.getSelectedItemPosition();
                            UUID uuidSucursalTriada = sucursals.get(positionSucursal - 1).getUuid();
                            for (Rol r : rols) {
                                if (r.getSucursal().getUuid().equals(uuidSucursalTriada)) {
                                    rolsFiltrats.add(r);
                                }
                            }
                        } else {
                            rolsFiltrats.addAll(rols);
                        }

                        // Obtenir noms rols
                        ArrayList<String> nomsRols = new ArrayList<>();
                        nomsRols.add("Sense rol");
                        for (Rol r : rolsFiltrats) {
                            nomsRols.add(r.getNom());
                        }
                        // Inserir els departaments en l'spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(PerfilActivity.this, android.R.layout.simple_spinner_item, nomsRols);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spRols.setAdapter(adapter);
                        rolsObtinguts = true;
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Rol>> call, Throwable t) {

                }
            });
        }
    }
}