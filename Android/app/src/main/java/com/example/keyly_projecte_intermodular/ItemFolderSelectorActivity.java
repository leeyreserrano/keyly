package com.example.keyly_projecte_intermodular;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ItemFolderSelectorActivity extends AppCompatActivity {

    LinearLayout items_button, carpetes_button;
    private BottomNavigationView menu;
    private boolean afegir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_folder_selector);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        afegir = getIntent().getBooleanExtra("afegir", false);

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

        items_button = findViewById(R.id.items_button);
        carpetes_button = findViewById(R.id.carpetes_button);

        items_button.setOnClickListener(v -> {
            Intent intent = new Intent(this, ItemsActivity.class);
            if (afegir) {
                intent.putExtra("afegir", true);
            }
            startActivity(intent);
        });

        carpetes_button.setOnClickListener(v -> {
            Intent intent = new Intent(this, CarpetesActivity.class);
            if (afegir) {
                intent.putExtra("afegir", true);
            }
            startActivity(intent);
        });


    }
}