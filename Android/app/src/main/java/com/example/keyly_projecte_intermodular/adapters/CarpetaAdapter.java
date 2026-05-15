package com.example.keyly_projecte_intermodular.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.R;
import com.example.keyly_projecte_intermodular.dao.Carpeta;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarpetaAdapter extends RecyclerView.Adapter<CarpetaAdapter.ViewHolder> {

    private List<Carpeta> carpetaList;
    private OnItemClickListener listener;

    // Interfície per al click
    public interface OnItemClickListener {
        void onItemClick(Carpeta carpeta);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout de cada carpeta de la lista
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_carpeta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(R.drawable.carpeta_negra);

        holder.nameUserTextView.setVisibility(View.VISIBLE);

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
        ImageView imageView;
        TextView carpetaTextView;
        TextView nameUserTextView;

        public ViewHolder(@NonNull View carpetaView) {
            super(carpetaView);
            imageView = carpetaView.findViewById(R.id.imgView);
            carpetaTextView = carpetaView.findViewById(R.id.txtNom);
            nameUserTextView = carpetaView.findViewById(R.id.txtDescripcio);
        }
    }
}
