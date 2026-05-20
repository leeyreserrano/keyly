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

import com.example.keyly_projecte_intermodular.CarpetaActivity;
import com.example.keyly_projecte_intermodular.R;
import com.example.keyly_projecte_intermodular.dao.Carpeta;
import com.example.keyly_projecte_intermodular.dto.CarpetaDTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Data
@AllArgsConstructor
public class CarpetaAdapter extends RecyclerView.Adapter<CarpetaAdapter.ViewHolder> {

    private List<Carpeta> carpetaList;
    private OnItemClickListener listener;
    private Context context;

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

            txtPregunta.setText(context.getString(R.string.etiquetaEliminarCarpeta) + " " + carpeta.getNom() + "\" ?");

            btnEliminar.setOnClickListener(c -> {
                Call<Void> call = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).eliminarCarpeta(carpeta.getUuid().toString());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            carpetaList.remove(carpeta);
                            notifyDataSetChanged();
                            alertDialog.dismiss();
                            Toast.makeText(context, context.getString(R.string.toastCarpetaEliminada, carpeta.getNom()), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.toastCarpetaNoEliminada, carpeta.getNom()), Toast.LENGTH_SHORT).show();
                            Log.d("ERROR_RESPONSE", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, context.getString(R.string.toastCarpetaNoEliminada, carpeta.getNom()), Toast.LENGTH_SHORT).show();
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
        return carpetaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView carpetaTextView;
        TextView nameUserTextView;
        ImageButton imgBtnEliminar;

        public ViewHolder(@NonNull View carpetaView) {
            super(carpetaView);
            imageView = carpetaView.findViewById(R.id.imgView);
            carpetaTextView = carpetaView.findViewById(R.id.txtNom);
            nameUserTextView = carpetaView.findViewById(R.id.txtDescripcio);
            imgBtnEliminar = carpetaView.findViewById(R.id.imgBtnEliminar);
        }
    }
}
