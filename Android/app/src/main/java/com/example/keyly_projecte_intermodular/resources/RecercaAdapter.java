package com.example.keyly_projecte_intermodular.resources;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.R;
import com.example.keyly_projecte_intermodular.dao.Carpeta;
import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.utils.Permisos;

import java.util.ArrayList;
import java.util.List;

public class RecercaAdapter extends RecyclerView.Adapter<RecercaAdapter.ViewHolder> {

    private List<Item> itemList;
    private List<Carpeta> carpetaList;
    private Context context;
    private List<Usuari> usuariList;

    public RecercaAdapter(List<Item> itemList, List<Carpeta> carpetaList, List<Usuari> usuariList, Context context) {
        this.itemList = itemList;
        this.carpetaList = carpetaList;
        this.context = context;
        this.usuariList = usuariList;
    }

    @NonNull
    @Override
    public RecercaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (itemList != null) {
            // Infla el layout de cada ítem de la lista
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_cerques, parent, false);
            return new RecercaAdapter.ViewHolder(view);
        }
        if (usuariList != null) {
            // Infla el layout de cada usuari de la lista
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_cerques, parent, false);
            return new RecercaAdapter.ViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecercaAdapter.ViewHolder holder, int position) {
        if (itemList != null) {
            Item item = itemList.get(position);
            holder.itemUsuariTextView.setText(item.getTitol());
            holder.imgBtnX.setOnClickListener(v -> {
                // Eliminar ítem de la llista
                itemList.remove(position);
                notifyItemRemoved(position);
            });
        } else if (usuariList != null) {
            Usuari usuari = usuariList.get(position);
            holder.itemUsuariTextView.setText(usuari.getNom());
            holder.imgBtnX.setOnClickListener(v -> {
                // Eliminar ítem de la llista
                usuariList.remove(position);
                notifyItemRemoved(position);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (itemList != null) {
            return itemList.size();
        } else if (usuariList != null) {
            return usuariList.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemUsuariTextView;
        Spinner spPermisos;
        ImageButton imgBtnX;

        public ViewHolder(@NonNull View recercaView) {
            super(recercaView);
            itemUsuariTextView = recercaView.findViewById(R.id.txtNom);
            spPermisos = recercaView.findViewById(R.id.spPermisos);
            imgBtnX = recercaView.findViewById(R.id.imgBtnX);

            ArrayAdapter<Permisos> adapter = new ArrayAdapter<>(
                    recercaView.getContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    Permisos.values());
            spPermisos.setAdapter(adapter);
        }
    }
}
