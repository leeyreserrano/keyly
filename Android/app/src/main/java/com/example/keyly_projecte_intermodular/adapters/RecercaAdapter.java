package com.example.keyly_projecte_intermodular.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
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

import java.util.List;

public class RecercaAdapter extends RecyclerView.Adapter<RecercaAdapter.ViewHolder> {

    private List<Item> itemList;
    private List<Carpeta> carpetaList;
    private Context context;
    private List<Usuari> usuariList;
    private List<String> permisos;

    public RecercaAdapter(List<Item> itemList, List<Carpeta> carpetaList, List<Usuari> usuariList,
                          List<String> permisos, Context context) {
        this.itemList = itemList;
        this.carpetaList = carpetaList;
        this.context = context;
        this.usuariList = usuariList;
        this.permisos = permisos;
    }

    @NonNull
    @Override
    public RecercaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (itemList != null) {
            // Infla el layout de cada ítem de la llista
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_cerques, parent, false);
            return new RecercaAdapter.ViewHolder(view);
        }
        if (carpetaList != null) {
            // Infla el layout de cada carpeta de la llista
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_cerques, parent, false);
            return new RecercaAdapter.ViewHolder(view);
        }
        if (usuariList != null) {
            // Infla el layout de cada usuari de la llista
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
            holder.itemCarpetaUsuariTextView.setText(item.getTitol());
            holder.flPermisos.setVisibility(View.GONE);
            holder.imgBtnX.setOnClickListener(v -> {
                // Eliminar ítem de la llista
                itemList.remove(position);
                notifyItemRemoved(position);
            });
        } else if (carpetaList != null) {
            Carpeta carpeta = carpetaList.get(position);
            holder.itemCarpetaUsuariTextView.setText(carpeta.getNom());
            holder.flPermisos.setVisibility(View.GONE);
            holder.imgBtnX.setOnClickListener(v -> {
                // Eliminar carpeta de la llista
                carpetaList.remove(position);
                notifyItemRemoved(position);
            });
        } else if (usuariList != null) {
            Usuari usuari = usuariList.get(position);
            holder.itemCarpetaUsuariTextView.setText(usuari.getNom());

            // Permisos usuaris
            holder.flPermisos.setVisibility(View.VISIBLE);
            ArrayAdapter<Permisos> adapter =
                    (ArrayAdapter<Permisos>) holder.spPermisos.getAdapter();

            int spinnerPosition = adapter.getPosition(
                    Permisos.valueOf(permisos.get(position))
            );
            holder.spPermisos.setSelection(spinnerPosition);
            // Guardar cambios del spinner
            holder.spPermisos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    int adapterPosition = holder.getBindingAdapterPosition();

                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        permisos.set(
                                adapterPosition,
                                parent.getItemAtPosition(pos).toString()
                        );
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            holder.imgBtnX.setOnClickListener(v -> {
                // Eliminar ítem de la llista
                int adapterPosition = holder.getBindingAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    usuariList.remove(adapterPosition);
                    permisos.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);
                }
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
        TextView itemCarpetaUsuariTextView;
        FrameLayout flPermisos;
        Spinner spPermisos;
        ImageButton imgBtnX;

        public ViewHolder(@NonNull View recercaView) {
            super(recercaView);
            itemCarpetaUsuariTextView = recercaView.findViewById(R.id.txtNom);
            flPermisos = recercaView.findViewById(R.id.flPermisos);
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
