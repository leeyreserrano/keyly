package com.example.keyly_projecte_intermodular;

import static com.example.keyly_projecte_intermodular.config.TokenForEver.dataKey;
import static com.example.keyly_projecte_intermodular.config.TokenForEver.privateKeyDecrypt;
import static com.example.keyly_projecte_intermodular.config.TokenForEver.publicKey;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.cypherIV;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.desencriptarContrasenya2;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.desencriptarDataKey;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.dao.GeneradorContrasenya;
import com.example.keyly_projecte_intermodular.dao.Contrasenya;
import com.example.keyly_projecte_intermodular.dto.ItemDTO;
import com.example.keyly_projecte_intermodular.dto.UtilsDTO;
import com.example.keyly_projecte_intermodular.request.ItemRequest;
import com.example.keyly_projecte_intermodular.utils.Encrypt;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemActivity extends AppCompatActivity {

    private LinearLayout llNomItem, llPassword, llActions;
    private TextView txtTitle, txtUrl, txtNomUsuari, txtNotes;
    private EditText etTitolItem, etLlocItem, etNomUsuariItem, etPassword, etNotes;
    private ImageButton imgBtnStar, imgBtnEditStar, imgButtonCopy, imgBtnEye, imgBtnGenerate;
    // TODO añadir botón de editar y eliminar
    private Button btnGuardarEliminarItem, btnBack;
    private int edit = 0;
    private String uuid, contrasenyaGenerada;
    private boolean isPasswordVisible = false;
    private AtomicBoolean favActual;

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


        /* Camps */

        // Layouts
        llNomItem = findViewById(R.id.ll_nom_item);
        llPassword = findViewById(R.id.ll_password);
        llActions = findViewById(R.id.ll_actions);

        // Nom Item
        txtTitle = findViewById(R.id.txtTitleItem);
        etTitolItem = findViewById(R.id.etNomItem);

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

        // Botó Guardar Eliminar Item
        btnGuardarEliminarItem = findViewById(R.id.btnGuardarItem);

        // Botó Back
        btnBack = findViewById(R.id.btnBack);

        // Notes/Descripció Item
        etNotes = findViewById(R.id.etNotes);

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

        int add_edit = getIntent().getIntExtra("add_edit", 0);

        // Actualitzar pantalla
        actualitzarPantalla(add_edit, favActual, uuid);

        // Accés ítem
        Call<Item> call = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class).accessItem(uuid);
        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ItemActivity.this, "Ítem accés", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ItemActivity.this, "No s'ha pogut accedir a l'ítem", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {
                Toast.makeText(ItemActivity.this, "No s'ha pogut accedir a l'ítem", Toast.LENGTH_SHORT).show();
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
            txtUrl.setText("Lloc: " + url);
            etLlocItem.setText(url);
        } else {
            txtUrl.setText("Lloc:");
        }

        if (nom_usuari != null && !nom_usuari.equals("")) {
            txtNomUsuari.setText("Nom d'usuari: " + nom_usuari);
            etNomUsuariItem.setText(nom_usuari);
        } else {
            txtNomUsuari.setText("Nom d'usuari:");
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

            Toast.makeText(this, "Contrasenya copiada", Toast.LENGTH_SHORT).show();
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

        imgBtnGenerate.setOnClickListener(v -> {
            // TODO generar contrasenya
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_generate_password, null);

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
                // TODO cambiar d'alertDialog a altre
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
//        txtNotes = findViewById(R.id.txtNotes);
//        if(notes == null || notes.equals("")) {
//            txtNotes.setHint("Notes...");
//        } else {
//            txtNotes.setText(notes);
//        }

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

            // Botó Guardar Eliminar Item
            btnGuardarEliminarItem.setText("Guardar");
            btnGuardarEliminarItem.setBackground(ContextCompat.getDrawable(this, R.drawable.background_button_purple));
            btnGuardarEliminarItem.setOnClickListener(v -> {
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
                    encryptedDataKey = Encrypt.encriptarDataKey(publicKey, dataKey);
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
                    Call<Item> call = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class).addItem2(itemR);
                    call.enqueue(new Callback<Item>() {
                        @Override
                        public void onResponse(Call<Item> call, Response<Item> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ItemActivity.this, "Ítem afegit", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ItemActivity.this, "No s'ha pogut afegir l'ítem", Toast.LENGTH_SHORT).show();
                                Log.d("ERROR_RESPONSE", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<Item> call, Throwable t) {
                            Toast.makeText(ItemActivity.this, "No s'ha pogut afegir l'ítem", Toast.LENGTH_SHORT).show();
                            Log.d("ERROR_FAILURE", t.getMessage());
                        }
                    });
                } else if (add_edit == 2) {
                    Call<Item> call = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class).updateItem2(uuid, itemR);
                    call.enqueue(new Callback<Item>() {
                        @Override
                        public void onResponse(Call<Item> call, Response<Item> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ItemActivity.this, "Ítem actualitzat", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ItemActivity.this, "No s'ha pogut actualitzar l'ítem", Toast.LENGTH_SHORT).show();
                                Log.d("ERROR_RESPONSE", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<Item> call, Throwable t) {
                            Toast.makeText(ItemActivity.this, "No s'ha pogut actualitzar l'ítem", Toast.LENGTH_SHORT).show();
                            Log.d("ERROR_FAILURE", t.getMessage());
                        }
                    });

                    txtTitle.setText(titol);
                    txtUrl.setText("Lloc: " + nouUrl);
                    txtNomUsuari.setText("Nom d'usuari: " + nou_NomUsuari);
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

        } else if (add_edit == 0) {
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
                Toast.makeText(ItemActivity.this, "Edicó", Toast.LENGTH_SHORT);
            });

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
            btnGuardarEliminarItem.setText("Eliminar");
            btnGuardarEliminarItem.setBackground(ContextCompat.getDrawable(this, R.drawable.background_button_eliminar));
            btnGuardarEliminarItem.setOnClickListener(v -> {
                Call<Item> call = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class).deleteItem(uuid);
                call.enqueue(new Callback<Item>() {
                    @Override
                    public void onResponse(Call<Item> call, Response<Item> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ItemActivity.this, "Ítem eliminat", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ItemActivity.this, "No s'ha pogut eliminar l'ítem", Toast.LENGTH_SHORT).show();
                            Log.d("ERROR_RESPONSE", response.message());
                        }
                    }
                    @Override
                    public void onFailure(Call<Item> call, Throwable t) {
                        Toast.makeText(ItemActivity.this, "No s'ha pogut eliminar l'ítem", Toast.LENGTH_SHORT).show();
                        Log.d("ERROR_FAILURE", t.getMessage());
                    }
                });
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

            Toast.makeText(this, "Contrasenya copiada", Toast.LENGTH_SHORT).show();
        });

        btnUsar.setOnClickListener(v -> {
            // TODO guardar contrasenya
            etPassword.setText(etContrasenyaGenerada.getText());
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
}