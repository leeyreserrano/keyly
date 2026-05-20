package com.example.keyly_projecte_intermodular;

import static com.example.keyly_projecte_intermodular.resources.Varis.comprovarVulnerabilitatContrasenya;
import static com.example.keyly_projecte_intermodular.resources.Varis.dataKey;
import static com.example.keyly_projecte_intermodular.resources.Varis.privateKeyDecrypt;
import static com.example.keyly_projecte_intermodular.resources.Varis.publicKey;
import static com.example.keyly_projecte_intermodular.resources.Varis.tempsCreatEditat;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.cypherIV;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.desencriptarContrasenya2;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.desencriptarDataKey;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.encriptarDataKey;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.stringToPublicKey;
import static com.example.keyly_projecte_intermodular.utils.LogOutService.logOut;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
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

import com.example.keyly_projecte_intermodular.dao.EncryptedDataKey;
import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.dao.GeneradorContrasenya;
import com.example.keyly_projecte_intermodular.dao.Contrasenya;
import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.dto.CarpetaDTO;
import com.example.keyly_projecte_intermodular.dto.CompartitDTO;
import com.example.keyly_projecte_intermodular.dto.ItemDTO;
import com.example.keyly_projecte_intermodular.dto.UsuariDTO;
import com.example.keyly_projecte_intermodular.dto.UtilsDTO;
import com.example.keyly_projecte_intermodular.request.CompartitRequest;
import com.example.keyly_projecte_intermodular.request.ItemRequest;
import com.example.keyly_projecte_intermodular.request.UsuariCompartitRequest;
import com.example.keyly_projecte_intermodular.adapters.RecercaAdapter;
import com.example.keyly_projecte_intermodular.utils.Encrypt;
import com.example.keyly_projecte_intermodular.gestions.GestionsIdiomes;
import com.example.keyly_projecte_intermodular.utils.Permisos;
import com.example.keyly_projecte_intermodular.utils.TipusEntitat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemActivity extends AppCompatActivity {

    private View includeItemVulnerat, includeItemRepetit;
    private LinearLayout llNomItem, llPassword, llActions;
    private TextView txtTitolAvisVulnerat, txtTitolAvisRepetit, txtTitle, txtData, txtUrl, txtNomUsuari, txtNotes;
    private EditText etTitolItem, etLlocItem, etNomUsuariItem, etPassword, etNotes;
    private ImageView imgVAvisVulnerat, imgVAvisRepetit;
    private ImageButton imgBtnAjuda, imgBtnIdioma, imgBtnLogOut, imgBtnStar, imgBtnEditStar, imgButtonCopy, imgBtnEye, imgBtnGenerate;
    // TODO añadir botón de editar y eliminar
    private Button btnCompartir, btnGuardarEliminarItem, btnBack;
    private int edit = 0;
    private String uuid, contrasenyaGenerada;
    private boolean isPasswordVisible = false, compartirObligatori = false;
    private AtomicBoolean favActual;
    private Item itemCreat, itemActual;
    private ArrayList<Usuari> usuaris = new ArrayList<>();
    private ArrayList<Usuari> usuarisSeleccionats = new ArrayList<>();
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
        setContentView(R.layout.activity_item);
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

        /* Camps */
        // Layouts
        includeItemVulnerat = findViewById(R.id.includeAvisVulnerada);
        imgVAvisVulnerat = includeItemVulnerat.findViewById(R.id.imgVAvis);
        txtTitolAvisVulnerat = includeItemVulnerat.findViewById(R.id.txtTitolAvis);
        includeItemRepetit = findViewById(R.id.includeAvisRepetida);
        imgVAvisRepetit = includeItemRepetit.findViewById(R.id.imgVAvis);
        txtTitolAvisRepetit = includeItemRepetit.findViewById(R.id.txtTitolAvis);
        llNomItem = findViewById(R.id.ll_nom_item);
        llPassword = findViewById(R.id.ll_password);
        llActions = findViewById(R.id.llActions);

        // Nom Item
        txtTitle = findViewById(R.id.txtTitleItem);
        etTitolItem = findViewById(R.id.etNomItem);

        // Data
        txtData = findViewById(R.id.txtData);
        String dataCreacio = getIntent().getStringExtra("data_creacio");
        String dataEdicio = getIntent().getStringExtra("data_edicio");
        if (dataCreacio != null) {
            if (dataEdicio == null) {
                String dataFormatejada = tempsCreatEditat(dataCreacio, false, false);
                Log.d("DATA", dataFormatejada);
                txtData.setText(dataFormatejada);
            } else {
                String dataFormatejada = tempsCreatEditat(dataEdicio, true, false);
                Log.d("DATA", dataFormatejada);
                txtData.setText(dataFormatejada);
            }
        } else {
            txtData.setText("");
        }

        // Lloc/URL Item
        txtUrl = findViewById(R.id.txtLlocItem);
        etLlocItem = findViewById(R.id.etLlocItem);

        // Nom usuari Item
        txtNomUsuari = findViewById(R.id.txtNomUsuariItem);
        etNomUsuariItem = findViewById(R.id.etNomUsuariItem);

        // Botó Favorit
        imgBtnStar = findViewById(R.id.imgBtnStar);

        // Botó Editar Favorit
        imgBtnEditStar = findViewById(R.id.imgBtnEditStar);

        // Contrasenya Item
        etPassword = findViewById(R.id.et_password);

        // Botó Copiar Contrasenya
        imgButtonCopy = findViewById(R.id.imbBtnCopy);

        // Botó Mostrar/Ocultar Contrasenya
        imgBtnEye = findViewById(R.id.imgBtnEye);

        // Botó Generar Contrasenya
        imgBtnGenerate = findViewById(R.id.imgBtnGenerate);

        // Botó Compartir Ítem
        btnCompartir = findViewById(R.id.btnCompartir);

        // Botó Guardar Eliminar Item
        btnGuardarEliminarItem = findViewById(R.id.btnGuardarItem);

        // Botó Back
        btnBack = findViewById(R.id.btnBack);

        // Notes/Descripció Item
        etNotes = findViewById(R.id.etNotes);

        int add_edit = getIntent().getIntExtra("add_edit", 0);
        compartirObligatori = getIntent().getBooleanExtra("compartirObligatori", false);

        try {
            carregarInfo();
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Actualitzar pantalla
        actualitzarPantalla(add_edit, favActual, uuid);

        // Accés ítem
        String titolItem = getIntent().getStringExtra("title");
        Call<Item> call = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class).accessItem(uuid);
        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ItemActivity.this, "Ítem accés", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ItemActivity.this, getString(R.string.toastItemNoAcces, titolItem), Toast.LENGTH_SHORT).show();
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {
                Toast.makeText(ItemActivity.this, getString(R.string.toastItemNoAcces, titolItem), Toast.LENGTH_SHORT).show();
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    private void carregarInfo() throws Exception {
        // Otenir dades de l'item
        uuid = getIntent().getStringExtra("uuid");
        String title = getIntent().getStringExtra("title");
        String url = getIntent().getStringExtra("url");
        String nom_usuari = getIntent().getStringExtra("nom_usuari");
        String contrasenya = getIntent().getStringExtra("password");
        String notes = getIntent().getStringExtra("notes");
        boolean fav = getIntent().getBooleanExtra("fav", false);
        String iv = getIntent().getStringExtra("iv");
        String edk = getIntent().getStringExtra("edk");

        favActual = new AtomicBoolean(fav);

        if (title != null && !title.equals("")) {
            txtTitle.setText(title);
            etTitolItem.setText(title);
        }

        if (url != null && !url.equals("")) {
            txtUrl.setText(getString(R.string.etiquetaLlocWebOmplerta) + " " + url);
            etLlocItem.setText(url);
        } else {
            txtUrl.setText(getString(R.string.etiquetaLlocWebBuida));
        }

        if (nom_usuari != null && !nom_usuari.equals("")) {
            txtNomUsuari.setText(getString(R.string.etiquetaNomUsuariOmplert) + " " + nom_usuari);
            etNomUsuariItem.setText(nom_usuari);
        } else {
            txtNomUsuari.setText(getString(R.string.etiquetaNomUsuariBuit));
        }

        if (favActual.get()) {
            imgBtnStar.setImageResource(R.drawable.filled_star);
        } else {
            imgBtnStar.setImageResource(R.drawable.star);
        }
        imgBtnStar.setOnClickListener(v -> {
            if (!favActual.get()) {
                imgBtnStar.setImageResource(R.drawable.filled_star);
                favActual.set(true);
            } else {
                imgBtnStar.setImageResource(R.drawable.star);
                favActual.set(false);
            }
        });

        if (contrasenya == null || contrasenya.equals("")) {
            etPassword.setHint("Contrasenya");
        } else if (edk != null && !edk.equals("")) {
            // Desencriptar contrasenya
            byte[] dataKey = desencriptarDataKey(privateKeyDecrypt, edk);
//            byte[] passwordByte = Base64.decode(contrasenya, Base64.DEFAULT);
//            byte[] ivByte = Base64.decode(iv, Base64.DEFAULT);
            String combined64 = cypherIV(iv, contrasenya);
            byte[] constrasenyaDesencriptada = desencriptarContrasenya2(combined64, dataKey);
            etPassword.setText(new String(constrasenyaDesencriptada, StandardCharsets.UTF_8));
        }

        imgButtonCopy.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("contrasenya", etPassword.getText());
            clipboardManager.setPrimaryClip(clipData);

            Toast.makeText(this, getString(R.string.toastContrasenyaCopiada), Toast.LENGTH_SHORT).show();
        });

        isPasswordVisible = false;
        imgBtnEye.setOnClickListener(v -> {
            if (isPasswordVisible) {
                isPasswordVisible = false;
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                isPasswordVisible = true;
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });

        // TODO mostrar si la contraseña es vulnerada o no
        comprovarVulnerabilitatContrasenya(etPassword.getText().toString(), ItemActivity.this,
                includeItemVulnerat, false, null, 0);

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
                generarContrasenya(gContrasenya, etPassword);
                alertDialog.dismiss();
            });

            // Botó Complexitat Mitjana
            btnMitjana.setOnClickListener(c -> {
                GeneradorContrasenya gContrasenya = new GeneradorContrasenya(12, true, 2, true, 2, true, 2);
                generarContrasenya(gContrasenya, etPassword);
                alertDialog.dismiss();
            });

            // Botó Complexitat Alta
            btnAlta.setOnClickListener(c -> {
                GeneradorContrasenya gContrasenya = new GeneradorContrasenya(20, true, 5, true, 5, true, 5);
                generarContrasenya(gContrasenya, etPassword);
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

        // Notes/Descripció Item
        if(notes == null || notes.equals("")) {
            etNotes.setHint("Notes...");
        } else {
            etNotes.setText(notes);
        }
    }

    public void actualitzarPantalla(int add_edit, AtomicBoolean favActual, String uuid) {
        if (add_edit == 1 || add_edit == 2) {
            // Layout Nom Item
            llNomItem.setGravity(Gravity.LEFT);
            ViewGroup.MarginLayoutParams params =
                    (ViewGroup.MarginLayoutParams) llNomItem.getLayoutParams();
            params.setMargins(0, 5, 55, 0);
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            llNomItem.setLayoutParams(params);

            // Nom Item
            txtTitle.setVisibility(View.GONE);
            etTitolItem.setVisibility(View.VISIBLE);

            // ImageButton Star
            imgBtnStar.setVisibility(View.GONE);

            // ImageButton Editar Favorit
            if (favActual.get()) {
                imgBtnEditStar.setImageResource(R.drawable.filled_star);
            } else {
                imgBtnEditStar.setImageResource(R.drawable.star);
            }
            imgBtnEditStar.setOnClickListener(v -> {
                if (favActual.get()) {
                    imgBtnEditStar.setImageResource(R.drawable.star);
                    favActual.set(false);
                } else {
                    imgBtnEditStar.setImageResource(R.drawable.filled_star);
                    favActual.set(true);
                }
            });

            // Data
            txtData.setVisibility(View.GONE);

            // Lloc i Propietari Item
            txtUrl.setVisibility(View.GONE);
            etLlocItem.setVisibility(View.VISIBLE);
            txtNomUsuari.setVisibility(View.GONE);
            etNomUsuariItem.setVisibility(View.VISIBLE);

            // Layout Contrasenya
            llPassword.setBackground(ContextCompat.getDrawable(this, R.drawable.background_text_notes));

            // Contrasenya
            ViewGroup.MarginLayoutParams paramsETPassword =
                    (ViewGroup.MarginLayoutParams) etPassword.getLayoutParams();
            paramsETPassword.width = ViewGroup.LayoutParams.MATCH_PARENT;
            etPassword.setLayoutParams(paramsETPassword);
            etPassword.setPadding(40, 0, 40, 0);
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            etPassword.setEnabled(true);
            etPassword.setTextColor(ContextCompat.getColor(this, R.color.black));

            // Layout Accions
            llActions.setVisibility(View.GONE);
            imgBtnGenerate.setVisibility(View.VISIBLE);

            // Notes
            etNotes.setTextColor(ContextCompat.getColor(this, R.color.black));
            etNotes.setEnabled(true);
            etNotes.setBackground(ContextCompat.getDrawable(this, R.drawable.background_text_notes));

            // Botó Compartir Ítem
            if (add_edit == 1) {
                btnCompartir.setOnClickListener(v -> {
                    // TODO compartir item
                    afegirUsuaris(2);
                });
            } else if (add_edit == 2) {
                // TODO compartir item
                btnCompartir.setOnClickListener(v -> {
                    // TODO compartir item
                    afegirUsuaris(3);
                });
                //afegirUsuaris(3);
            }

            // Botó Guardar Eliminar Item
            btnGuardarEliminarItem.setText(getString(R.string.btnGuardar));
            btnGuardarEliminarItem.setBackground(ContextCompat.getDrawable(this, R.drawable.background_button_purple));
            btnGuardarEliminarItem.setOnClickListener(v -> {

                if (compartirObligatori && usuarisSeleccionats.size() <= 0){
                    Toast.makeText(ItemActivity.this, getString(R.string.toastUsuarisNoSeleccionats), Toast.LENGTH_SHORT).show();
                    return;
                }

                String titol = etTitolItem.getText().toString();
                String nou_NomUsuari = etNomUsuariItem.getText().toString();
                String novaContrasenya = etPassword.getText().toString();
                String nouUrl = etLlocItem.getText().toString();
                String novesNotes = etNotes.getText().toString();
                boolean nouFav = favActual.get();

                SecureRandom random = new SecureRandom();
                byte[] iv = new byte[12];
                random.nextBytes(iv);

                byte[] encrypted = null;
                byte[] encrypted2 = null;
                byte[] encryptedDataKey = null;

                try {
                    encrypted = Encrypt.encriptarContrasenya(novaContrasenya, iv);
                    encrypted2 = Encrypt.encriptarContrasenya2(novaContrasenya, publicKey, iv);
                    encryptedDataKey = encriptarDataKey(publicKey, dataKey);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeySpecException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchPaddingException e) {
                    throw new RuntimeException(e);
                } catch (InvalidAlgorithmParameterException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException(e);
                } catch (IllegalBlockSizeException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                String encryptedBase64 = Base64.encodeToString(encrypted, Base64.DEFAULT);
                String encryptedBase642 = Base64.encodeToString(encrypted2, Base64.DEFAULT);
                String encryptedDataKeyBase64 = Base64.encodeToString(encryptedDataKey, Base64.DEFAULT);
                String ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP);

                Log.d("IV_BYTES", String.valueOf(iv.length)); // → 12
                Log.d("IV_BASE64_LEN", String.valueOf(ivBase64.length())); // → ~16

                Log.d("CONTRASENYA_ENCRYPT", encryptedBase64);
                Log.d("CONTRASENYA_ENCRYPT2", encryptedBase642);
                Log.d("DATA_KEY_ENCRYPT", encryptedDataKeyBase64);
                Log.d("IV", ivBase64);

                byte[] ivBytes = Base64.decode(ivBase64, Base64.NO_WRAP);
                Log.d("IV_BYTES", String.valueOf(ivBytes.length)); // → 12

                Item item = new Item(titol, nou_NomUsuari, encryptedBase64, ivBase64, nouUrl, novesNotes, nouFav);
                ItemRequest itemR = new ItemRequest(titol, nou_NomUsuari, encryptedBase642, ivBase64, encryptedDataKeyBase64, nouUrl, novesNotes, nouFav);

                if (add_edit == 1) {
                    //Call<Item> call = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class).addItem(item);
                    Call<Item> call = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class).addItem(itemR);
                    call.enqueue(new Callback<Item>() {
                        @Override
                        public void onResponse(Call<Item> call, Response<Item> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ItemActivity.this, getString(R.string.toastItemCreat, titol), Toast.LENGTH_SHORT).show();
                                itemCreat = response.body();
                                if (usuarisSeleccionats.size() > 0) {
                                    ArrayList<EncryptedDataKey> encryptedDataKeys = new ArrayList<>();
                                    // TODO compartir item
                                    for (Usuari usuari : usuarisSeleccionats) {
                                        // TODO desencriptar datakey
                                        byte[] dataKeyDecrypted = null;
                                        byte[] dataKeyEncrypted = null;
                                        try {
                                            dataKeyDecrypted = desencriptarDataKey(privateKeyDecrypt, itemCreat.getEncryptedDataKey().getEncryptedDataKey());
                                            dataKeyEncrypted = encriptarDataKey(stringToPublicKey(usuari.getPublicKey()), dataKeyDecrypted);
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }

                                        String encryptedDataKeyBase64 = Base64.encodeToString(dataKeyEncrypted, Base64.DEFAULT);
                                        EncryptedDataKey edk = new EncryptedDataKey(null, encryptedDataKeyBase64);
                                        encryptedDataKeys.add(edk);
                                    }

                                    for (int i = 0; i < usuarisSeleccionats.size(); i++) {
                                        Permisos permis = Permisos.valueOf(permisos.get(i));
                                        usuarisCompartitRequest.add(new UsuariCompartitRequest(
                                                usuarisSeleccionats.get(i).getUuid(),
                                                permis,
                                                encryptedDataKeys));
                                    }

                                    try {
                                        compartirItem(itemCreat, false);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                finish();
                            } else {
                                Toast.makeText(ItemActivity.this, getString(R.string.toastItemNoCreat, titol), Toast.LENGTH_SHORT).show();
                                Log.d("ERROR_RESPONSE", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<Item> call, Throwable t) {
                            Toast.makeText(ItemActivity.this, getString(R.string.toastItemNoCreat, titol), Toast.LENGTH_SHORT).show();
                            Log.d("ERROR_FAILURE", t.getMessage());
                        }
                    });
                } else if (add_edit == 2) {
                    Call<Item> call = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class).updateItem(uuid, itemR);
                    call.enqueue(new Callback<Item>() {
                        @Override
                        public void onResponse(Call<Item> call, Response<Item> response) {
                            if (response.isSuccessful()) {
                                itemActual = response.body();
                                if (itemActual.getDataEditat() != null) {
                                    String dataFormatejada = tempsCreatEditat(itemActual.getDataEditat(), true, true);
                                    Log.d("DATA_ITEM", dataFormatejada);
                                    txtData.setText(dataFormatejada);
                                }
                                Toast.makeText(ItemActivity.this, getString(R.string.toastItemEditat, titol), Toast.LENGTH_SHORT).show();
                                if (usuarisSeleccionats.size() > 0) {
                                    ArrayList<EncryptedDataKey> encryptedDataKeys = new ArrayList<>();
                                    // TODO compartir item
                                    for (Usuari usuari : usuarisSeleccionats) {
                                        // TODO desencriptar datakey
                                        byte[] dataKeyDecrypted = null;
                                        byte[] dataKeyEncrypted = null;
                                        try {
                                            dataKeyDecrypted = desencriptarDataKey(privateKeyDecrypt, itemActual.getEncryptedDataKey().getEncryptedDataKey());
                                            dataKeyEncrypted = encriptarDataKey(stringToPublicKey(usuari.getPublicKey()), dataKeyDecrypted);
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }

                                        String encryptedDataKeyBase64 = Base64.encodeToString(dataKeyEncrypted, Base64.DEFAULT);
                                        EncryptedDataKey edk = new EncryptedDataKey(null, encryptedDataKeyBase64);
                                        encryptedDataKeys.add(edk);
                                    }

                                    for (int i = 0; i < usuarisSeleccionats.size(); i++) {
                                        Permisos permis = Permisos.valueOf(permisos.get(i));
                                        usuarisCompartitRequest.add(new UsuariCompartitRequest(
                                                usuarisSeleccionats.get(i).getUuid(),
                                                permis,
                                                encryptedDataKeys));
                                    }

                                    try {
                                        compartirItem(itemActual, false);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            } else {
                                Toast.makeText(ItemActivity.this, getString(R.string.toastItemNoEditat, titol), Toast.LENGTH_SHORT).show();
                                Log.d("ERROR_RESPONSE", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<Item> call, Throwable t) {
                            Toast.makeText(ItemActivity.this, getString(R.string.toastItemNoEditat, titol), Toast.LENGTH_SHORT).show();
                            Log.d("ERROR_FAILURE", t.getMessage());
                        }
                    });

                    txtTitle.setText(titol);
                    txtUrl.setText(getString(R.string.etiquetaLlocWebOmplerta) + nouUrl);
                    txtNomUsuari.setText(getString(R.string.etiquetaNomUsuariOmplert) + nou_NomUsuari);
                    etNotes.setText(novesNotes);
                    etPassword.setText(novaContrasenya);

                    actualitzarPantalla(0, favActual, uuid);
                }
            });

            // Botó Back
            if (add_edit == 2) {
                btnBack.setOnClickListener(v -> {
                    actualitzarPantalla(0, favActual, uuid);
                });
            } else {
                btnBack.setOnClickListener(v -> {
                    finish();
                });
            }

        } else if (add_edit == 0) { // Mode visualització
            // Layout Nom Item
            llNomItem.setGravity(Gravity.CENTER);
            ViewGroup.MarginLayoutParams params =
                    (ViewGroup.MarginLayoutParams) llNomItem.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            llNomItem.setLayoutParams(params);

            // Nom Item
            txtTitle.setVisibility(View.VISIBLE);
            etTitolItem.setVisibility(View.GONE);

            // ImageButton Star
            imgBtnStar.setVisibility(View.VISIBLE);
            if (favActual.get()) {
                imgBtnStar.setImageResource(R.drawable.filled_star);
            } else {
                imgBtnStar.setImageResource(R.drawable.star);
            }
            imgBtnStar.setOnClickListener(v -> {
                // TODO que se guarde el favoritos
                if (favActual.get()) {
                    imgBtnStar.setImageResource(R.drawable.star);
                    favActual.set(false);
                } else {
                    imgBtnStar.setImageResource(R.drawable.filled_star);
                    favActual.set(true);
                }
            });

            // ImageButton Editar Favorit
            imgBtnEditStar.setImageResource(R.drawable.editar);
            imgBtnEditStar.setOnClickListener(v -> {
                Log.d("EDITAR", "EDITAR");
                edit = 2;
                actualitzarPantalla(2, favActual, uuid);
                // TODO ver qué hace esto para quitarlo
                Toast.makeText(ItemActivity.this, "Edició", Toast.LENGTH_SHORT);
            });

            // Data
            txtData.setVisibility(View.VISIBLE);

            // Lloc i Propietari Item
            txtUrl.setVisibility(View.VISIBLE);
            etLlocItem.setVisibility(View.GONE);
            txtNomUsuari.setVisibility(View.VISIBLE);
            etNomUsuariItem.setVisibility(View.GONE);

            // Layout Contrasenya
            llPassword.setBackground(ContextCompat.getDrawable(this, R.drawable.backgroung_edit_text_password));

            // Contrasenya
            ViewGroup.MarginLayoutParams paramsETPassword =
                    (ViewGroup.MarginLayoutParams) etPassword.getLayoutParams();
            paramsETPassword.width = 700;
            etPassword.setLayoutParams(paramsETPassword);
            etPassword.setPadding(40, 0, 0, 0);
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setEnabled(false);
            etPassword.setTextColor(ContextCompat.getColor(this, R.color.not_enabled));

            // Layout Accions
            llActions.setVisibility(View.VISIBLE);
            imgBtnGenerate.setVisibility(View.GONE);

            // Notes
            etNotes.setTextColor(ContextCompat.getColor(this, R.color.not_enabled));
            etNotes.setEnabled(false);
            etNotes.setBackground(ContextCompat.getDrawable(this, R.drawable.backgroung_edit_text_password));

            // Botó Guardar Eliminar Item
            btnGuardarEliminarItem.setText(getString(R.string.btnEliminar));
            btnGuardarEliminarItem.setBackground(ContextCompat.getDrawable(this, R.drawable.background_button_eliminar));
            btnGuardarEliminarItem.setOnClickListener(v -> {
                String titolItem = txtTitle.getText().toString();
                boolean esCompartit = getIntent().getBooleanExtra("esCompartit", false);
                String uuidCompartit = getIntent().getStringExtra("uuidCompartit");
                if (esCompartit) {
                    Call<Void> call = CompartitDTO.obtenirJSONCompartit().create(CompartitDTO.RequestCompartit.class).eliminarCompartit(uuidCompartit);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ItemActivity.this, getString(R.string.toastItemEliminat, titolItem), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ItemActivity.this, getString(R.string.toastItemNoEliminat, titolItem), Toast.LENGTH_SHORT).show();
                                Log.d("ERROR_RESPONSE", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(ItemActivity.this, getString(R.string.toastItemNoEliminat, titolItem), Toast.LENGTH_SHORT).show();
                            Log.d("ERROR_FAILURE", t.getMessage());
                        }
                    });
                } else {
                    boolean item_carpeta = getIntent().getBooleanExtra("item_carpeta", false);
                    if (item_carpeta) {
                        String uuidCarpeta = getIntent().getStringExtra("uuidCarpeta");
                        Call<Void> call = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).eliminarItemCarpeta(uuidCarpeta, uuid);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(ItemActivity.this, getString(R.string.toastItemEliminat, titolItem), Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(ItemActivity.this, getString(R.string.toastItemNoEliminat, titolItem), Toast.LENGTH_SHORT).show();
                                    Log.d("ERROR_RESPONSE", response.message());
                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(ItemActivity.this, getString(R.string.toastItemNoEliminat, titolItem), Toast.LENGTH_SHORT).show();
                                Log.d("ERROR_FAILURE", t.getMessage());
                            }
                        });
                    } else {
                        Call<Void> call = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class).deleteItem(uuid);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(ItemActivity.this, getString(R.string.toastItemEliminat, titolItem), Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(ItemActivity.this, getString(R.string.toastItemNoEliminat, titolItem), Toast.LENGTH_SHORT).show();
                                    Log.d("ERROR_RESPONSE", response.message());
                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(ItemActivity.this, getString(R.string.toastItemNoEliminat, titolItem), Toast.LENGTH_SHORT).show();
                                Log.d("ERROR_FAILURE", t.getMessage());
                            }
                        });
                    }
                }
            });

            // Botó compartir ítem
            btnCompartir.setOnClickListener(v -> {
                afegirUsuaris(1);
            });

            // Botó Back
            btnBack.setOnClickListener(v -> {
                finish();
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
            ClipData clipData = ClipData.newPlainText("contrasenya", etPassword.getText());
            clipboardManager.setPrimaryClip(clipData);

            Toast.makeText(this, getString(R.string.toastContrasenyaCopiada), Toast.LENGTH_SHORT).show();
        });

        btnUsar.setOnClickListener(v -> {
            etPassword.setText(etContrasenyaGenerada.getText());
            alertDialog.dismiss();
        });

        btnGenerar.setOnClickListener(v -> {
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

    private void obtenirItemUUID(String uuid) {
        Call<Item> call = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class).getItem(uuid);
        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                if (response.isSuccessful()) {
                    itemActual = response.body();
                    if (usuarisSeleccionats.size() > 0) {
                        ArrayList<EncryptedDataKey> encryptedDataKeys = new ArrayList<>();
                        // TODO compartir item
                        for (Usuari usuari : usuarisSeleccionats) {
                            // TODO desencriptar datakey
                            byte[] dataKeyDecrypted = null;
                            byte[] dataKeyEncrypted = null;
                            try {
                                dataKeyDecrypted = desencriptarDataKey(privateKeyDecrypt, itemActual.getEncryptedDataKey().getEncryptedDataKey());
                                dataKeyEncrypted = encriptarDataKey(stringToPublicKey(usuari.getPublicKey()), dataKeyDecrypted);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }

                            String encryptedDataKeyBase64 = Base64.encodeToString(dataKeyEncrypted, Base64.DEFAULT);
                            EncryptedDataKey edk = new EncryptedDataKey(null, encryptedDataKeyBase64);
                            encryptedDataKeys.add(edk);
                        }

                        for (int i = 0; i < usuarisSeleccionats.size(); i++) {
                            Permisos permis = Permisos.valueOf(permisos.get(i));
                            usuarisCompartitRequest.add(new UsuariCompartitRequest(
                                    usuarisSeleccionats.get(i).getUuid(),
                                    permis,
                                    encryptedDataKeys));
                        }

                        try {
                            compartirItem(itemActual, true);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {

            }
        });
    }

    private void afegirUsuaris(int view_add_edit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_compartir_item, null);

        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Elements del AlertDialog
        AutoCompleteTextView aCTVCercarUsuaris = view.findViewById(R.id.aCTVCercarCompartir);
        RecyclerView recyclerUsuaris = view.findViewById(R.id.recyclerCompartir);
        Button btnGuardar = view.findViewById(R.id.btnGuardarUsuaris);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        recyclerUsuaris.setLayoutManager(new LinearLayoutManager(ItemActivity.this));
        RecercaAdapter recercaAdapterUsuaris = new RecercaAdapter(null, null, usuarisSeleccionats, permisos, this);
        recyclerUsuaris.setAdapter(recercaAdapterUsuaris);

        // Carregar usuaris
        UsuariDTO.RequestUsuari requestUsuari = UsuariDTO.obtenirJSONUsuari().create(UsuariDTO.RequestUsuari.class);
        requestUsuari.getAllUsuaris().enqueue(new Callback<ArrayList<Usuari>>() {
            @Override
            public void onResponse(Call<ArrayList<Usuari>> call, Response<ArrayList<Usuari>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usuaris = new ArrayList<>();
                    usuaris.addAll(response.body());

                    // Cercador d'usuaris
                    ArrayList<String> noms = new ArrayList<>();

                    for (Usuari usuari : usuaris) {
                        noms.add(usuari.getNom());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ItemActivity.this, android.R.layout.simple_dropdown_item_1line, noms);
                    aCTVCercarUsuaris.setAdapter(adapter);
                } else {
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Usuari>> call, Throwable t) {
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });

        aCTVCercarUsuaris.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                aCTVCercarUsuaris.showDropDown();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        aCTVCercarUsuaris.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String seleccionat = parent.getItemAtPosition(position).toString();

                for (Usuari usuari : usuaris) {
                    if (usuari.getNom().equals(seleccionat) && !usuarisSeleccionats.contains(usuari)) {
                        usuarisSeleccionats.add(usuari);
                        // Afegir permisos per defecte
                        permisos.add(Permisos.LECTURA.toString());
                    }
                }

                recercaAdapterUsuaris.notifyDataSetChanged();
                recyclerUsuaris.setAdapter(recercaAdapterUsuaris);
            }
        });


        btnGuardar.setOnClickListener(v -> {
            // TODO guarda ítem en carpeta si es desitja
            if (view_add_edit == 1) { // Mode visualitzar ítem existent
                obtenirItemUUID(uuid);
            }

            if (compartirObligatori && usuarisSeleccionats.size() <= 0){
                Toast.makeText(ItemActivity.this, getString(R.string.toastUsuarisNoSeleccionats), Toast.LENGTH_SHORT).show();
                return;
            }
            alertDialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    private void compartirItem(Item item, boolean view) throws Exception {
        CompartitRequest compartitRequest = new CompartitRequest(item.getUuid(), TipusEntitat.ITEM, usuarisCompartitRequest);

        Call<Void> call = CompartitDTO.obtenirJSONCompartit().create(CompartitDTO.RequestCompartit.class).compartir(compartitRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    if (view) {
                        Toast.makeText(ItemActivity.this, getString(R.string.toastItemCompartit), Toast.LENGTH_SHORT).show();
                    }
                    Log.e("ITEM_COMPARTIT", "Item " + item.getTitol() + " compartit");
                } else {
                    Log.e("ERROR_RESPONSE", response.message());
                    try {
                        Log.e("ERROR_BODY_RESPONSE", response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ERROR_FAILURE", t.getMessage());
            }
        });
    }
}