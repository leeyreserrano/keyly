package com.example.keyly_projecte_intermodular.resources;

import static com.example.keyly_projecte_intermodular.config.TokenForEver.getImatgeUUID;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
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

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.keyly_projecte_intermodular.R;
import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.dto.UsuariDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuariAdpater extends RecyclerView.Adapter<UsuariAdpater.ViewHolder> {

    private List<Usuari> usuariList;
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(Usuari usuari);
    }

    public UsuariAdpater(List<Usuari> usuariList, OnItemClickListener listener, Context context) {
        this.usuariList = usuariList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public UsuariAdpater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout de cada usuari de la lista
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_carpeta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuariAdpater.ViewHolder holder, int position) {
        holder.rolUsuariTextView.setVisibility(View.VISIBLE);

        Usuari usuari = usuariList.get(position);

        // Netejar imatge anterior
        holder.imgVFotoUsuari.setImageDrawable(null);

        String uuid = usuari.getUuid().toString();

        // TODO mostrar foto perfil usuari
        // Imatge de perfil usuari
        getImatgeUUID(usuari, imatge -> {

            ((android.app.Activity) context).runOnUiThread(() -> {

                int currentPosition = holder.getBindingAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return;
                if (!usuariList.get(currentPosition).getUuid().toString().equals(uuid)) return;

                if (imatge == null || imatge.isEmpty()) {
                    Log.e("SVG_DEBUG", "Imagen vacía o nula");
                    return;
                }

                try {
                    SVG svg = SVG.getFromString(imatge);
                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                    holder.imgVFotoUsuari.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    holder.imgVFotoUsuari.setImageDrawable(drawable);
                } catch (SVGParseException e) {
                    Log.e("SVG_ERROR", e.getMessage());
                }
            });
        });

        // Nom d'usuari
        holder.usuariTextView.setText(usuari.getNom());

        // Correu d'usuari
        holder.rolUsuariTextView.setText(usuari.getCorreu());

        // Click Listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(usuari);
            }
        });

        int pos = holder.getBindingAdapterPosition();

        holder.imgBtnEliminar.setOnClickListener(v -> {
            // TODO eliminar usuari + mirar si devuelve void o usuari
            Call<Usuari> call = UsuariDTO.obtenirJSONUsuari().create(UsuariDTO.RequestUsuari.class).eliminarUsuari(usuari.getUuid().toString());
            call.enqueue(new Callback<Usuari>() {
                @Override
                public void onResponse(Call<Usuari> call, Response<Usuari> response) {
                    if (response.isSuccessful()) {
                        usuariList.remove(pos);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, usuariList.size());
                        Toast.makeText(context, "Usuari eliminat", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "No s'ha pogut eliminar l'usuari", Toast.LENGTH_SHORT).show();
                        Log.d("ERROR_RESPONSE", response.message());
                    }
                }

                @Override
                public void onFailure(Call<Usuari> call, Throwable t) {
                    Toast.makeText(context, "No s'ha pogut eliminar l'usuari", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR_FAILURE", t.getMessage());
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return usuariList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgVFotoUsuari;
        TextView usuariTextView;
        TextView rolUsuariTextView;
        ImageButton imgBtnEliminar;

        public ViewHolder(@NonNull View usuariView) {
            super(usuariView);
            imgVFotoUsuari = usuariView.findViewById(R.id.imgView);
            usuariTextView = usuariView.findViewById(R.id.txtNom);
            rolUsuariTextView = usuariView.findViewById(R.id.txtDescripcio);
            imgBtnEliminar = usuariView.findViewById(R.id.imgBtnEliminar);
        }
    }

}
