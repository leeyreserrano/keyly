package com.example.keyly_projecte_intermodular;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.dto.ItemDTO;
import com.example.keyly_projecte_intermodular.utils.Encrypt;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
    private String uuid;
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

        carregarInfo();

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
                    Log.d("ERROR", response.message());
                }
            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {

            }
        });
    }

    private void carregarInfo() {

        // Otenir dades de l'item
        uuid = getIntent().getStringExtra("uuid");
        String title = getIntent().getStringExtra("title");
        String url = getIntent().getStringExtra("url");
        String nom_usuari = getIntent().getStringExtra("nom_usuari");
        String password = getIntent().getStringExtra("password");
        String notes = getIntent().getStringExtra("notes");
        boolean fav = getIntent().getBooleanExtra("fav", false);

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

        if (password == null || password.equals("")) {
            etPassword.setHint("Contrasenya");
        } else {
            etPassword.setText(password);
        }

        imgButtonCopy.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("contrasenya", etPassword.getText());
            clipboardManager.setPrimaryClip(clipData);

            Toast.makeText(this, "Contrasenya copiada", Toast.LENGTH_SHORT).show();
        });

        isPasswordVisible = false;
        imgBtnEye.setOnClickListener(v -> {
            // TODO guardar si es favorito o no
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
                // TODO guardar si es favorito o no
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
            btnGuardarEliminarItem.setOnClickListener(v -> {
                //TODO hacer que favoritos se guarde
                String titol = etTitolItem.getText().toString();
                String nou_NomUsuari = etNomUsuariItem.getText().toString();
                String novaContrasenya = etPassword.getText().toString();
                String nouUrl = etLlocItem.getText().toString();
                String novesNotes = etNotes.getText().toString();
                boolean nouFav = favActual.get();

                // TODO encriptar contrasenya
                byte[] encrypted;
                try {
                    encrypted = Encrypt.encriptarContrasenya(novaContrasenya);
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
                } catch (BadPaddingException e) {
                    throw new RuntimeException(e);
                }

                Log.d("CONTRASENYA_ENCRYPT", encrypted.toString());

                Item item = new Item(titol, nou_NomUsuari, encrypted.toString(), nouUrl, novesNotes, nouFav);

                if (add_edit == 1) {
                    Call<Item> call = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class).addItem(item);
                    call.enqueue(new Callback<Item>() {
                        @Override
                        public void onResponse(Call<Item> call, Response<Item> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ItemActivity.this, "Ítem afegit", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ItemActivity.this, "No s'ha pogut afegir ítem", Toast.LENGTH_SHORT).show();
                                Log.d("ERROR", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<Item> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                    finish();
                } else if (add_edit == 2) {
                    Call<Item> call = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class).updateItem(uuid, item);
                    call.enqueue(new Callback<Item>() {
                        @Override
                        public void onResponse(Call<Item> call, Response<Item> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ItemActivity.this, "Ítem actualitzat", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ItemActivity.this, "No s'ha pogut actualitzar ítem", Toast.LENGTH_SHORT).show();
                                Log.d("ERROR", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<Item> call, Throwable t) {
                            t.printStackTrace();
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
            btnGuardarEliminarItem.setOnClickListener(v -> {
                Call<Item> call = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class).deleteItem(uuid);
                call.enqueue(new Callback<Item>() {
                    @Override
                    public void onResponse(Call<Item> call, Response<Item> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ItemActivity.this, "Ítem eliminat", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ItemActivity.this, "No s'ha pogut eliminar ítem", Toast.LENGTH_SHORT).show();
                            Log.d("ERROR", response.message());
                            finish();
                        }
                    }
                    @Override
                    public void onFailure(Call<Item> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            });

            // Botó Back
            btnBack.setOnClickListener(v -> {
                finish();
            });
        }
    }
}