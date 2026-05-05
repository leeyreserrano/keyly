package com.example.keyly_projecte_intermodular.resources;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.R;
import com.example.keyly_projecte_intermodular.dao.Compartit;
import com.example.keyly_projecte_intermodular.utils.TipusEntitat;

import java.util.List;

public class ItemCarpetaAdapter extends RecyclerView.Adapter<ItemCarpetaAdapter.ViewHolder> {

    private List<Compartit> CompartitList;
    //private List<Tot> totList;
    private ItemCarpetaAdapter.OnItemClickListener listener;

    // Interfície per al click
    public interface OnItemClickListener {
        void onItemClick(Compartit compartitList);
    }

    public ItemCarpetaAdapter(List<Compartit> compartitList, ItemCarpetaAdapter.OnItemClickListener listener) {
        this.CompartitList = compartitList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemCarpetaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout de cada carpeta de la lista
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_carpeta, parent, false);
        return new ItemCarpetaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemCarpetaAdapter.ViewHolder holder, int position) {
        Compartit compartit = CompartitList.get(position);
        if (compartit.getEC() == TipusEntitat.CARPETA) {
            holder.itemCarpetaTextView.setText(compartit.getCarpeta().getNom());

            String propietari = compartit.getCarpeta().getBagul().getUsuari().getNom();
            holder.nameUserTextView.setText(propietari);

            // Click Listener
//            holder.itemView.setOnClickListener(v -> {
//                if (listener != null) {
//                    listener.onItemClick(compartit.getCarpeta());
//                }
//            });
        } else if (compartit.getEC() == TipusEntitat.ITEM) {
            holder.itemCarpetaTextView.setText(compartit.getItem().getTitol());

            String propietari = compartit.getItem().getNomUsuari();
            holder.nameUserTextView.setText(propietari);

            // Click Listener
//            holder.itemView.setOnClickListener(v -> {
//                if (listener != null) {
//                    listener.onItemClick(compartit.getItem());
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return CompartitList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //EntitatCompartit entitatCompartit;
        TextView itemCarpetaTextView;
        TextView nameUserTextView;

        public ViewHolder(@NonNull View itemCarpetaView) {
            super(itemCarpetaView);
            itemCarpetaTextView = itemCarpetaView.findViewById(R.id.txtNameItemCarpeta);
            nameUserTextView = itemCarpetaView.findViewById(R.id.txtNameUser);
        }
    }
}
