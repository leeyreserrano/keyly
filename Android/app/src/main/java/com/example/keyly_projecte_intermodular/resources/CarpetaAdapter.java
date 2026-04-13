package com.example.keyly_projecte_intermodular.resources;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.R;
import com.example.keyly_projecte_intermodular.dao.Carpeta;
import com.example.keyly_projecte_intermodular.dao.Item;

import java.util.List;

public class CarpetaAdapter extends RecyclerView.Adapter<CarpetaAdapter.ViewHolder> {

    private List<Carpeta> carpetaList;
    private OnItemClickListener listener;

    // Interfície per al click
    public interface OnItemClickListener {
        void onItemClick(Carpeta carpeta);
    }

    public CarpetaAdapter(List<Carpeta> carpetaList, OnItemClickListener listener) {
        this.carpetaList = carpetaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout de cada carpeta de la lista
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Carpeta carpeta = carpetaList.get(position);

        holder.carpetaTextView.setText(carpeta.getNom());

        String propietari = carpeta.getBagul().getUsuari().getNom();
        holder.nameUserTextView.setText(propietari);

        // Click Listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(carpeta);
            }
        });
    }

    @Override
    public int getItemCount() {
        return carpetaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //ImageView imageView;
        TextView carpetaTextView;
        TextView nameUserTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            carpetaTextView = itemView.findViewById(R.id.txtNameItem);
            nameUserTextView = itemView.findViewById(R.id.txtNameUser);
        }
    }
}
