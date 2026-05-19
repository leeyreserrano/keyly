package com.example.keyly_projecte_intermodular.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.R;
import com.example.keyly_projecte_intermodular.dao.Carpeta;
import com.example.keyly_projecte_intermodular.dao.Compartit;
import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.dto.CarpetaDTO;
import com.example.keyly_projecte_intermodular.dto.CompartitDTO;
import com.example.keyly_projecte_intermodular.utils.TipusEntitat;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompartitAdapter extends RecyclerView.Adapter<CompartitAdapter.ViewHolder> {

    private List<Compartit> compartitList;
    //private List<Tot> totList;
    private OnItemClickListener listener;
    private Context context;
    private String nomItem = "", nomCarpeta = "";

    // Interfície per al click
    public interface OnItemClickListener {
        void onItemClick(Compartit compartitList);
    }

    public CompartitAdapter(List<Compartit> compartitList, CompartitAdapter.OnItemClickListener listener, Context context) {
        this.compartitList = compartitList;
        this.listener = listener;
        this.context = context;
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
            holder.imageView.setImageResource(R.drawable.carpeta_negra);
            Log.d("CARPETA_COMPARTIDA", compartit.toString());
            if (compartit.getCarpeta() != null) {
                if (compartit.getCarpeta().getNom() != null) {
                    holder.itemCarpetaTextView.setText(compartit.getCarpeta().getNom());
                }

                String propietari = compartit.getCarpeta().getBagul().getUsuari().getNom();
                holder.nameUserTextView.setText(propietari);
            } else {
                holder.nameUserTextView.setText("");
            }
        } else if (compartit.getTipusEntitat() == TipusEntitat.ITEM) {
            holder.imageView.setImageResource(R.drawable.key_negra);
            if (compartit.getItem() != null) {
                holder.itemCarpetaTextView.setText(compartit.getItem().getTitol());
                String propietari = compartit.getItem().getNomUsuari();
                holder.nameUserTextView.setText(propietari);
            } else {
                holder.itemCarpetaTextView.setText("");
                holder.nameUserTextView.setText("");
            }
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(compartit);
            }
        });

        // Eliminar compartit
        holder.imgBtnEliminar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.layout_eliminar, null);

            builder.setView(view);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Elements del AlertDialog
            TextView txtPregunta = view.findViewById(R.id.txtPregunta);
            Button btnEliminar = view.findViewById(R.id.btnEliminar);
            Button btnCancelar = view.findViewById(R.id.btnCancelar);

            if (compartit.getTipusEntitat() == TipusEntitat.CARPETA) {
                Carpeta carpeta = compartit.getCarpeta();
                nomCarpeta = carpeta.getNom();
                txtPregunta.setText(context.getString(R.string.etiquetaEliminarCarpeta) + " " + nomCarpeta + "\" ?");
            } else if (compartit.getTipusEntitat() == TipusEntitat.ITEM) {
                Item item = compartit.getItem();
                nomItem = item.getTitol();
                txtPregunta.setText(context.getString(R.string.etiquetaEliminarItem) + " " + nomItem + "\" ?");
            }

            btnEliminar.setOnClickListener(c -> {
                Call<Void> call = CompartitDTO.obtenirJSONCompartit().create(CompartitDTO.RequestCompartit.class).eliminarCompartit(compartit.getUuid().toString());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            compartitList.remove(compartit);
                            notifyDataSetChanged();
                            alertDialog.dismiss();
                            if (compartit.getTipusEntitat() == TipusEntitat.CARPETA) {
                                Toast.makeText(context, context.getString(R.string.toastCarpetaEliminada, nomCarpeta), Toast.LENGTH_SHORT).show();
                            } else if (compartit.getTipusEntitat() == TipusEntitat.ITEM) {
                                Toast.makeText(context, context.getString(R.string.toastItemEliminat, nomItem), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (compartit.getTipusEntitat() == TipusEntitat.CARPETA) {
                                Toast.makeText(context, context.getString(R.string.toastCarpetaNoEliminada, nomCarpeta), Toast.LENGTH_SHORT).show();
                            } else if (compartit.getTipusEntitat() == TipusEntitat.ITEM) {
                                Toast.makeText(context, context.getString(R.string.toastItemNoEliminat, nomItem), Toast.LENGTH_SHORT).show();
                            }
                            Log.d("ERROR_RESPONSE", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (compartit.getTipusEntitat() == TipusEntitat.CARPETA) {
                            Toast.makeText(context, context.getString(R.string.toastCarpetaNoEliminada, nomCarpeta), Toast.LENGTH_SHORT).show();
                        } else if (compartit.getTipusEntitat() == TipusEntitat.ITEM) {
                            Toast.makeText(context, context.getString(R.string.toastItemNoEliminat, nomItem), Toast.LENGTH_SHORT).show();
                        }
                        Log.d("ERROR_FAILURE", t.getMessage());
                    }
                });
            });

            btnCancelar.setOnClickListener(c -> {
                alertDialog.dismiss();
            });
        });
    }

    @Override
    public int getItemCount() {
        return compartitList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //EntitatCompartit entitatCompartit;
        ImageView imageView;
        TextView itemCarpetaTextView;
        TextView nameUserTextView;
        ImageButton imgBtnEliminar;

        public ViewHolder(@NonNull View itemCarpetaView) {
            super(itemCarpetaView);
            imageView = itemCarpetaView.findViewById(R.id.imgView);
            itemCarpetaTextView = itemCarpetaView.findViewById(R.id.txtNom);
            nameUserTextView = itemCarpetaView.findViewById(R.id.txtDescripcio);
            imgBtnEliminar = itemCarpetaView.findViewById(R.id.imgBtnEliminar);
        }
    }
}
