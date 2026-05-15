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
import com.example.keyly_projecte_intermodular.dao.Departament;
import com.example.keyly_projecte_intermodular.dao.Domini;
import com.example.keyly_projecte_intermodular.dao.Rol;
import com.example.keyly_projecte_intermodular.dao.Sucursal;
import com.example.keyly_projecte_intermodular.dto.DepartamentDTO;
import com.example.keyly_projecte_intermodular.dto.DominiDTO;
import com.example.keyly_projecte_intermodular.dto.RolDTO;
import com.example.keyly_projecte_intermodular.dto.SucursalDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SDRDAdapter extends RecyclerView.Adapter<SDRDAdapter.ViewHolder> {

    private List<Sucursal> sucursalList;
    private List<Departament> departamentList;
    private List<Rol> rolList;
    private List<Domini> dominiList;
    private OnItemClickListenerS listenerS;
    private OnItemClickListenerDp listenerDp;
    private OnItemClickListenerR listenerR;

    private OnItemClickListenerDm listenerDm;
    private Context context;

    public interface OnItemClickListenerS {
        void onItemClick(Sucursal sucursal);
    }

    public interface OnItemClickListenerDp {
        void onItemClick(Departament departament);
    }

    public interface OnItemClickListenerR {
        void onItemClick(Rol rol);
    }

    public interface OnItemClickListenerDm {
        void onItemClick(Domini domini);
    }

    public SDRDAdapter(List<Sucursal> sucursalList, SDRDAdapter.OnItemClickListenerS listener, Context context) {
        this.sucursalList = sucursalList;
        this.listenerS = listener;
        this.context = context;
    }

    public SDRDAdapter(List<Departament> departamentList, OnItemClickListenerDp listener, Context context) {
        this.departamentList = departamentList;
        this.listenerDp = listener;
        this.context = context;
    }

    public SDRDAdapter(List<Rol> rolList, SDRDAdapter.OnItemClickListenerR listener, Context context) {
        this.rolList = rolList;
        this.listenerR = listener;
        this.context = context;
    }

    public SDRDAdapter(List<Domini> dominiList, SDRDAdapter.OnItemClickListenerDm listener, Context context) {
        this.dominiList = dominiList;
        this.listenerDm = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public SDRDAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout de cada sucursal o departament de la lista
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_carpeta, parent, false);
        return new SDRDAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SDRDAdapter.ViewHolder holder, int position) {
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
                if (listenerDp != null) {
                    listenerDp.onItemClick(departament);
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
        } else if (dominiList != null) {
            Domini domini = dominiList.get(position);
            holder.txtNom.setText(domini.getDomini());
            holder.itemView.setOnClickListener(v -> {
                if (listenerDm != null) {
                    listenerDm.onItemClick(domini);
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

                String nomDomini = domini.getDomini();
                txtPregunta.setText("Desitja eliminar el departament \"" + nomDomini + "\" ?");

                btnEliminar.setOnClickListener(c -> {
                    Call<Domini> call = DominiDTO.obtenirJSONDomini().create(DominiDTO.RequestDomini.class).eliminarDomini(domini.getUuid().toString());
                    call.enqueue(new Callback<Domini>() {
                        @Override
                        public void onResponse(Call<Domini> call, Response<Domini> response) {
                            if (response.isSuccessful()) {
                                dominiList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, dominiList.size());
                                alertDialog2.dismiss();
                                Toast.makeText(context, "Domini " + nomDomini + " eliminat", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "No s'ha pogut eliminar el domini " + nomDomini, Toast.LENGTH_SHORT).show();
                                Log.d("ERROR_RESPONSE", response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<Domini> call, Throwable t) {
                            Toast.makeText(context, "No s'ha pogut eliminar el domini " + nomDomini, Toast.LENGTH_SHORT).show();
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
        else if (dominiList != null) return dominiList.size();
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
