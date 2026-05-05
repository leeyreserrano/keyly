package com.example.keyly_projecte_intermodular.resources;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.R;
import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.dao.Usuari;

import java.util.List;

public class RecercaAdapter extends RecyclerView.Adapter<RecercaAdapter.ViewHolder> {

    private List<Item> itemList;
    private List<Usuari> usuariList;

    public RecercaAdapter(List<Item> itemList, List<Usuari> usuariList) {
        this.itemList = itemList;
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
        ImageButton imgBtnX;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemUsuariTextView = itemView.findViewById(R.id.txtNameItemCarpeta);
            imgBtnX = itemView.findViewById(R.id.imgBtnX);
        }
    }
}
