package com.example.keyly_projecte_intermodular.resources;

import android.util.Log;
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

public class CompartitAdapter extends RecyclerView.Adapter<CompartitAdapter.ViewHolder> {

    private List<Compartit> compartitList;
    //private List<Tot> totList;
    private OnItemClickListener listener;

    // Interfície per al click
    public interface OnItemClickListener {
        void onItemClick(Compartit compartitList);
    }

    public CompartitAdapter(List<Compartit> compartitList, CompartitAdapter.OnItemClickListener listener) {
        this.compartitList = compartitList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CompartitAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout de cada carpeta de la lista
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_carpeta, parent, false);
        return new CompartitAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompartitAdapter.ViewHolder holder, int position) {
        holder.nameUserTextView.setVisibility(View.VISIBLE);

        Compartit compartit = compartitList.get(position);
        if (compartit.getTipusEntitat() == TipusEntitat.CARPETA) {
            Log.d("CARPETA_COMPARTIDA", compartit.toString());
            if (compartit.getCarpeta().getNom() != null) {
                holder.itemCarpetaTextView.setText(compartit.getCarpeta().getNom());
            } else {
                holder.itemCarpetaTextView.setText("Sense nom");
            }

            String propietari = compartit.getCarpeta().getBagul().getUsuari().getNom();
            holder.nameUserTextView.setText(propietari);
        } else if (compartit.getTipusEntitat() == TipusEntitat.ITEM) {
            holder.itemCarpetaTextView.setText(compartit.getItem().getTitol());

            String propietari = compartit.getItem().getNomUsuari();
            holder.nameUserTextView.setText(propietari);
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(compartit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return compartitList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //EntitatCompartit entitatCompartit;
        TextView itemCarpetaTextView;
        TextView nameUserTextView;

        public ViewHolder(@NonNull View itemCarpetaView) {
            super(itemCarpetaView);
            itemCarpetaTextView = itemCarpetaView.findViewById(R.id.txtNom);
            nameUserTextView = itemCarpetaView.findViewById(R.id.txtDescripcio);
        }
    }
}
