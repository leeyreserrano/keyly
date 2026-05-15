package com.example.keyly_projecte_intermodular.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.R;
import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.dto.CarpetaDTO;
import com.example.keyly_projecte_intermodular.dto.ItemDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> itemList;
    private String uuidCarpeta;
    private OnItemClickListener listener;
    private Context context;

    // Interfície per al click
    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public ItemAdapter(List<Item> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
        this.uuidCarpeta = null;
    }

    public ItemAdapter(List<Item> itemList, String uuidCarpeta, OnItemClickListener listener, Context context) {
        this.itemList = itemList;
        this.uuidCarpeta = uuidCarpeta;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout de cada ítem de la lista
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_carpeta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(R.drawable.key_negra);

        holder.nameUserTextView.setVisibility(View.VISIBLE);

        Item item = itemList.get(position);

        holder.itemTextView.setText(item.getTitol());
        holder.nameUserTextView.setText(item.getNomUsuari());

        // Click Listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });

        int pos = holder.getBindingAdapterPosition();

        if (item.isDinsDeCarpeta() && uuidCarpeta != null) {
            holder.imgBtnEliminar.setOnClickListener(v -> {
                Call<Void> call = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).eliminarItemCarpeta(uuidCarpeta, item.getUuid().toString());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            itemList.remove(pos);
                            notifyItemRemoved(pos);
                            notifyItemRangeChanged(pos, itemList.size());
                            Toast.makeText(context, "Ítem eliminat", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "No s'ha pogut eliminar l'ítem", Toast.LENGTH_SHORT).show();
                            Log.d("ERROR_RESPONSE", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "No s'ha pogut eliminar l'ítem", Toast.LENGTH_SHORT).show();
                        Log.d("ERROR_FAILURE", t.getMessage());
                    }
                });
            });
        } else {
            holder.imgBtnEliminar.setOnClickListener(v -> {
                String nomItem = item.getTitol();
                Call<Void> call = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class).deleteItem(item.getUuid().toString());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            itemList.remove(pos);
                            notifyItemRemoved(pos);
                            notifyItemRangeChanged(pos, itemList.size());
                            Toast.makeText(context, "Ítem " + nomItem + " eliminat", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "No s'ha pogut eliminar l'ítem " + nomItem, Toast.LENGTH_SHORT).show();
                            Log.d("ERROR_RESPONSE", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "No s'ha pogut eliminar l'ítem " + nomItem, Toast.LENGTH_SHORT).show();
                        Log.d("ERROR_FAILURE", t.getMessage());
                    }
                });
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView itemTextView;
        TextView nameUserTextView;
        ImageButton imgBtnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgView);
            itemTextView = itemView.findViewById(R.id.txtNom);
            nameUserTextView = itemView.findViewById(R.id.txtDescripcio);
            imgBtnEliminar = itemView.findViewById(R.id.imgBtnEliminar);
        }
    }
}
