package com.example.keyly_projecte_intermodular.adapters;

import static com.example.keyly_projecte_intermodular.resources.Varis.getImatgeUUID;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
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

import com.caverock.androidsvg.SVG;
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

        // Imatge de perfil usuari
        getImatgeUUID(usuari, body -> {

            ((android.app.Activity) context).runOnUiThread(() -> {

                int currentPosition = holder.getBindingAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return;
                if (!usuariList.get(currentPosition).getUuid().toString().equals(uuid)) return;

                try {
                    String contentType = body.contentType().toString();

                    if (contentType.contains("svg")) { // SVG
                        SVG svg = SVG.getFromInputStream(body.byteStream());
                        Drawable drawable =
                                new PictureDrawable(svg.renderToPicture());
                        holder.imgVFotoUsuari.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        holder.imgVFotoUsuari.setImageDrawable(drawable);
                    } else { // PNG / JPG / JPEG
                        Bitmap bitmap = BitmapFactory.decodeStream(body.byteStream());
                        holder.imgVFotoUsuari.setImageBitmap(bitmap);
                    }

                } catch (Exception e) {
                    Log.e("IMG_ERROR", e.getMessage());
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
            AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
            LayoutInflater inflater2 = LayoutInflater.from(context);
            View view2 = inflater2.inflate(R.layout.layout_eliminar, null);

            builder2.setView(view2);

            AlertDialog alertDialog2 = builder2.create();
            alertDialog2.show();

            // Elements del AlertDialog
            TextView txtPregunta = view2.findViewById(R.id.txtPregunta);
            Button btnEliminar = view2.findViewById(R.id.btnEliminar);
            Button btnCancelar = view2.findViewById(R.id.btnCancelar);

            String nomUsuari = usuari.getNom();
            txtPregunta.setText(context.getString(R.string.etiquetaEliminarUsuari) + nomUsuari + "\" ?");

            btnEliminar.setOnClickListener(v2 -> {
                Call<Usuari> call = UsuariDTO.obtenirJSONUsuari().create(UsuariDTO.RequestUsuari.class).eliminarUsuari(usuari.getUuid().toString());
                call.enqueue(new Callback<Usuari>() {
                    @Override
                    public void onResponse(Call<Usuari> call, Response<Usuari> response) {
                        if (response.isSuccessful()) {
                            usuariList.remove(pos);
                            notifyItemRemoved(pos);
                            notifyItemRangeChanged(pos, usuariList.size());
                            alertDialog2.dismiss();
                            Toast.makeText(context, context.getString(R.string.toastUsuariEliminat, nomUsuari), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.toastUsuariNoEliminat, nomUsuari), Toast.LENGTH_SHORT).show();
                            Log.d("ERROR_RESPONSE", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Usuari> call, Throwable t) {
                        Toast.makeText(context, context.getString(R.string.toastUsuariNoEliminat, nomUsuari), Toast.LENGTH_SHORT).show();
                        Log.d("ERROR_FAILURE", t.getMessage());
                    }
                });
            });

            btnCancelar.setOnClickListener(v2 -> {
                alertDialog2.dismiss();
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
