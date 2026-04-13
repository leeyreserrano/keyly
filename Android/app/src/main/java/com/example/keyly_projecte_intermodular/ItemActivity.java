package com.example.keyly_projecte_intermodular;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ItemActivity extends AppCompatActivity {

    private TextView txtTitle, txtUrl, txtPropietari;
    private EditText txtPassword, txtNotes;
    private ImageButton imgBtnStar;
    private Button btnBack;

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

        // Otenir dades de l'item
        String title = getIntent().getStringExtra("title");
        String url = getIntent().getStringExtra("url");
        String nom_usuari = getIntent().getStringExtra("propietari");
        String password = getIntent().getStringExtra("password");
        String notes = getIntent().getStringExtra("notes");
        boolean fav = getIntent().getBooleanExtra("fav", false);

        // Mostrar dades
        txtTitle = findViewById(R.id.txtNomItem);
        txtTitle.setText(title);

        txtUrl = findViewById(R.id.txtLlocItem);
        txtUrl.setText("Lloc: " + url);

        txtPropietari = findViewById(R.id.txtPropietariItem);
        txtPropietari.setText("Propietari: " + nom_usuari);

        txtPassword = findViewById(R.id.editTextTextPassword2);
        txtPassword.setText(password);

        imgBtnStar = findViewById(R.id.imgBtnStar);
        if (fav) {
            imgBtnStar.setImageResource(R.drawable.filled_star);
        } else {
            imgBtnStar.setImageResource(R.drawable.star);
        }

        txtNotes = findViewById(R.id.txtNotes);
        if(notes == null || notes.equals("")) {
            txtNotes.setHint("Notes...");
        } else {
            txtNotes.setText(notes);
        }

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        });
    }
}