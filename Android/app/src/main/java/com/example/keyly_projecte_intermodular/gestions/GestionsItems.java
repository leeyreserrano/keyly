package com.example.keyly_projecte_intermodular.gestions;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.ItemActivity;
import com.example.keyly_projecte_intermodular.ItemsActivity;
import com.example.keyly_projecte_intermodular.adapters.ItemAdapter;
import com.example.keyly_projecte_intermodular.dao.Item;

import java.util.ArrayList;

public class GestionsItems {
    public static void actualitzarItems(ArrayList<Item> items, ItemAdapter itemAdapter,
                                        Context context, RecyclerView recyclerView,
                                        boolean esCompartit) {
        itemAdapter = new ItemAdapter(items, item -> {
            Intent intent = new Intent(context, ItemActivity.class);
            intent.putExtra("uuid", item.getUuid().toString());
            intent.putExtra("title", item.getTitol());
            intent.putExtra("url", item.getUrl());
            intent.putExtra("nom_usuari", item.getNomUsuari());
            intent.putExtra("password", item.getContrasenya());
            intent.putExtra("notes", item.getNotes());
            intent.putExtra("fav", item.isFavorit());
            intent.putExtra("add_edit", 0);
            intent.putExtra("iv", item.getIv());
            intent.putExtra("edk", item.getEncryptedDataKey().getEncryptedDataKey());
            intent.putExtra("esCompartit", esCompartit);
            intent.putExtra("esMeu", esMeu);
            intent.putExtra("data_creacio", item.getDataCreacio());
            intent.putExtra("data_edicio", item.getDataEditat());
            context.startActivity(intent);
        }, context);
        recyclerView.setAdapter(itemAdapter);
    }
}
