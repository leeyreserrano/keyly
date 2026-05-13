package com.example.keyly_projecte_intermodular.resources;

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
import com.example.keyly_projecte_intermodular.dao.Departament;
import com.example.keyly_projecte_intermodular.dao.Rol;
import com.example.keyly_projecte_intermodular.dao.Sucursal;
import com.example.keyly_projecte_intermodular.dto.DepartamentDTO;
import com.example.keyly_projecte_intermodular.dto.RolDTO;
import com.example.keyly_projecte_intermodular.dto.SucursalDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SucursalDepartamentRolAdapter extends RecyclerView.Adapter<SucursalDepartamentRolAdapter.ViewHolder> {

    private List<Sucursal> sucursalList;
    private List<Departament> departamentList;
    private List<Rol> rolList;
    private OnItemClickListenerS listenerS;
    private OnItemClickListenerD listenerD;
    private OnItemClickListenerR listenerR;
    private Context context;

    public interface OnItemClickListenerS {
        void onItemClick(Sucursal sucursal);
    }

    public interface OnItemClickListenerD {
        void onItemClick(Departament departament);
    }

    public interface OnItemClickListenerR {
        void onItemClick(Rol rol);
    }

    public SucursalDepartamentRolAdapter(List<Sucursal> sucursalList, SucursalDepartamentRolAdapter.OnItemClickListenerS listener, Context context) {
        this.sucursalList = sucursalList;
        this.listenerS = listener;
        this.context = context;
    }

    public SucursalDepartamentRolAdapter(List<Departament> departamentList, SucursalDepartamentRolAdapter.OnItemClickListenerD listener, Context context) {
        this.departamentList = departamentList;
        this.listenerD = listener;
        this.context = context;
    }

    public SucursalDepartamentRolAdapter(List<Rol> rolList, SucursalDepartamentRolAdapter.OnItemClickListenerR listener, Context context) {
        this.rolList = rolList;
        this.listenerR = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public SucursalDepartamentRolAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout de cada sucursal o departament de la lista
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_carpeta, parent, false);
        return new SucursalDepartamentRolAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SucursalDepartamentRolAdapter.ViewHolder holder, int position) {
        holder.imgView.setVisibility(View.GONE);
        holder.txtDescripcio.setVisibility(View.GONE);

        if (sucursalList != null) {
            Sucursal sucursal = sucursalList.get(position);
            holder.txtNom.setText(sucursal.getNom());
            holder.itemView.setOnClickListener(v -> {
                if (listenerS != null) {
                    listenerS.onItemClick(sucursal);
                }
            });
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

                String nomSucursal = sucursal.getNom();
                txtPregunta.setText("Desitja eliminar el departament \"" + nomSucursal + "\" ?");

                btnEliminar.setOnClickListener(v1 -> {
                   alertDialog2.dismiss();

                    Call<Sucursal> call = SucursalDTO.obtenirJSONSucursal().create(SucursalDTO.RequestSucursal.class).eliminarSucursal(sucursal.getUuid().toString());
                    call.enqueue(new Callback<Sucursal>() {
                        @Override
                        public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
                            if (response.isSuccessful()) {
                                sucursalList.remove(sucursal);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, sucursalList.size());
                                Toast.makeText(context, "Sucursal " + nomSucursal + " eliminada", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "No s'ha pogut eliminar la sucursal" + nomSucursal, Toast.LENGTH_SHORT).show();
                                Log.d("ERROR_RESPONSE", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<Sucursal> call, Throwable t) {
                            Toast.makeText(context, "No s'ha pogut eliminar la sucursal" + nomSucursal, Toast.LENGTH_SHORT).show();
                            Log.d("ERROR_FAILURE", t.getMessage());
                        }
                    });
                });
            });
        } else if (departamentList != null) {
            Departament departament = departamentList.get(position);
            holder.txtNom.setText(departament.getNom());
            holder.itemView.setOnClickListener(v -> {
                if (listenerD != null) {
                    listenerD.onItemClick(departament);
                }
            });
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

                String nomDepartament = departament.getNom();
                txtPregunta.setText("Desitja eliminar el departament \"" + nomDepartament + "\" ?");

                btnEliminar.setOnClickListener(v1 -> {
                    Call<Departament> call = DepartamentDTO.obtenirJSONDepartament().create(DepartamentDTO.RequestDepartament.class).eliminarDepartament(departament.getUuid().toString());
                    call.enqueue(new Callback<Departament>() {
                        @Override
                        public void onResponse(Call<Departament> call, Response<Departament> response) {
                            if (response.isSuccessful()) {
                                departamentList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, departamentList.size());
                                alertDialog2.dismiss();
                                Toast.makeText(context, "Departament " + nomDepartament + " eliminat", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "No s'ha pogut eliminar el departament " + nomDepartament, Toast.LENGTH_SHORT).show();
                                Log.d("ERROR_RESPONSE", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<Departament> call, Throwable t) {
                            Toast.makeText(context, "No s'ha pogut eliminar el departament " + nomDepartament, Toast.LENGTH_SHORT).show();
                            Log.d("ERROR_FAILURE", t.getMessage());
                        }
                    });
                });

                btnCancelar.setOnClickListener(v1 -> {
                    alertDialog2.dismiss();
                });
            });
        } else if (rolList != null) {
            Rol rol = rolList.get(position);
            holder.txtNom.setText(rol.getNom());
            holder.itemView.setOnClickListener(v -> {
                if (listenerR != null) {
                    listenerR.onItemClick(rol);
                }
            });
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

                String nomRol = rol.getNom();
                txtPregunta.setText("Desitja eliminar el departament \"" + nomRol + "\" ?");

                btnEliminar.setOnClickListener(c -> {
                    Call<Rol> call = RolDTO.obtenirJSONRol().create(RolDTO.RequestRol.class).eliminarRol(rol.getUuid().toString());
                    call.enqueue(new Callback<Rol>() {
                        @Override
                        public void onResponse(Call<Rol> call, Response<Rol> response) {
                            if (response.isSuccessful()) {
                                rolList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, rolList.size());
                                alertDialog2.dismiss();
                                Toast.makeText(context, "Rol " + nomRol + " eliminat", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "No s'ha pogut eliminar el rol " + nomRol, Toast.LENGTH_SHORT).show();
                                Log.d("ERROR_RESPONSE", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<Rol> call, Throwable t) {
                            Toast.makeText(context, "No s'ha pogut eliminar el rol " + nomRol, Toast.LENGTH_SHORT).show();
                            Log.d("ERROR_FAILURE", t.getMessage());
                        }
                    });
                });

                btnCancelar.setOnClickListener(c -> {
                    alertDialog2.dismiss();
                });
            });
        }
    }

    @Override
    public int getItemCount() {
        if (sucursalList != null) return sucursalList.size();
        else if (departamentList != null) return departamentList.size();
        else if (rolList != null) return rolList.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgView;
        TextView txtNom;
        TextView txtDescripcio;
        ImageButton imgBtnEliminar;

        public ViewHolder(@NonNull View SDView) {
            super(SDView);
            imgView = SDView.findViewById(R.id.imgView);
            txtNom = SDView.findViewById(R.id.txtNom);
            txtDescripcio = SDView.findViewById(R.id.txtDescripcio);
            imgBtnEliminar = SDView.findViewById(R.id.imgBtnEliminar);
        }
    }
}
