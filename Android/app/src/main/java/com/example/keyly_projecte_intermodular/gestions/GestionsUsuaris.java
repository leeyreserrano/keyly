package com.example.keyly_projecte_intermodular.gestions;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.R;
import com.example.keyly_projecte_intermodular.adapters.RecercaAdapter;
import com.example.keyly_projecte_intermodular.dao.Departament;
import com.example.keyly_projecte_intermodular.dao.Rol;
import com.example.keyly_projecte_intermodular.dao.Sucursal;
import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.dto.DepartamentDTO;
import com.example.keyly_projecte_intermodular.dto.RolDTO;
import com.example.keyly_projecte_intermodular.dto.SucursalDTO;
import com.example.keyly_projecte_intermodular.dto.UsuariDTO;
import com.example.keyly_projecte_intermodular.utils.Permisos;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionsUsuaris {
    private static ArrayList<Usuari> usuarisG, usuarisSeleccionatsG;

    public static void obtenirUsuaris(ArrayList<Usuari> usuaris, ArrayList<Usuari> usuarisSeleccionats,
                                      ArrayList<String> permisos, RecyclerView recyclerUsuaris,
                                      RecercaAdapter recercaAdapterUsuaris, AutoCompleteTextView aCTVCercarUsuaris,
                                      Context context, boolean usuarisCompartits, Runnable onSuccess) {
        usuarisG = usuaris;
        usuarisSeleccionatsG = usuarisSeleccionats;
        // Carregar usuaris
        UsuariDTO.RequestUsuari requestUsuari = UsuariDTO.obtenirJSONUsuari().create(UsuariDTO.RequestUsuari.class);
        requestUsuari.getAllUsuaris().enqueue(new Callback<ArrayList<Usuari>>() {
            @Override
            public void onResponse(Call<ArrayList<Usuari>> call, Response<ArrayList<Usuari>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usuarisG = new ArrayList<>();
                    usuarisG.addAll(response.body());

                    // Cercador d'usuaris
                    ArrayList<String> noms = new ArrayList<>();
                    permisos.clear();

                    for (Usuari usuari : usuarisG) {
                        noms.add(usuari.getNom());
                    }

                    if (!usuarisCompartits) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, noms);
                        aCTVCercarUsuaris.setAdapter(adapter);
                    } else {
                        if (onSuccess != null) onSuccess.run();
                    }
                } else {
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Usuari>> call, Throwable t) {
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });

        if (!usuarisCompartits) {
            cercarUsuaris(recyclerUsuaris, recercaAdapterUsuaris, aCTVCercarUsuaris, context, permisos, usuarisSeleccionats);
        } else {
            if (onSuccess != null) onSuccess.run();
        }
    }

    public static void cercarUsuaris(RecyclerView recyclerUsuaris, RecercaAdapter recercaAdapterUsuaris,
                                     AutoCompleteTextView aCTVCercarUsuaris, Context context, ArrayList<String> permisos,
                                     ArrayList<Usuari> usuarisSeleccionats) {
        aCTVCercarUsuaris.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                aCTVCercarUsuaris.showDropDown();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        aCTVCercarUsuaris.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String seleccionat = parent.getItemAtPosition(position).toString();
                for (Usuari usuari : usuarisG) {
                    if (usuari.getNom().equals(seleccionat) && !usuarisSeleccionats.contains(usuari)) {
                        usuarisSeleccionats.add(usuari);
                        // Afegir permisos per defecte
                        permisos.add(Permisos.LECTURA.toString());
                    }
                }
                recercaAdapterUsuaris.notifyDataSetChanged();
                recyclerUsuaris.setAdapter(recercaAdapterUsuaris);
            }
        });
    }

    public static void obtenirSucursalUUID(UUID uuid, LinearLayout llSucursal, TextView txtSucursal,
                                           Spinner spinner, ArrayList<Sucursal> sucursals, Context context) {
        Call<Sucursal> call = SucursalDTO.obtenirJSONSucursal().create(SucursalDTO.RequestSucursal.class).getSucursal(uuid.toString());
        call.enqueue(new Callback<Sucursal>() {
            @Override
            public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
                if (response.isSuccessful()) {
                    Sucursal sucursal = response.body();
                    if (sucursal != null) {
                        llSucursal.setVisibility(View.VISIBLE);
                        txtSucursal.setText(sucursal.getNom());
                        for (Sucursal s : sucursals) {
                            if (s.getUuid().equals(sucursal.getUuid())) {
                                spinner.setSelection(sucursals.indexOf(s) + 1);
                                break;
                            }
                        }
                    } else {
                        txtSucursal.setText(context.getString(R.string.etiqeutaSenseSucursal));
                        spinner.setSelection(0);
                    }
                }
            }

            @Override
            public void onFailure(Call<Sucursal> call, Throwable t) {

            }
        });
    }

    public static void obtenirDepartamentUUID(UUID uuid, LinearLayout llDepartament, TextView txtDepartament,
                                              Spinner spinner, ArrayList<Departament> departaments, Context context) {
        Call<Departament> call = DepartamentDTO.obtenirJSONDepartament().create(DepartamentDTO.RequestDepartament.class).getDepartament(uuid.toString());
        call.enqueue(new Callback<Departament>() {
            @Override
            public void onResponse(Call<Departament> call, Response<Departament> response) {
                if (response.isSuccessful()) {
                    Departament departament = response.body();
                    if (departament != null) {
                        llDepartament.setVisibility(View.VISIBLE);
                        txtDepartament.setText(departament.getNom());
                        for (Departament d : departaments) {
                            if (d.getUuid().equals(departament.getUuid())) {
                                spinner.setSelection(departaments.indexOf(d) + 1);
                                break;
                            }
                        }
                    } else {
                        txtDepartament.setText(context.getString(R.string.etiqeutaSenseDepartament));
                        spinner.setSelection(0);
                    }
                }
            }

            @Override
            public void onFailure(Call<Departament> call, Throwable t) {

            }
        });
    }

    public static void obtenirRolUUID(UUID uuid, LinearLayout llRol, TextView txtRol,
                                      Spinner spinner, ArrayList<Rol> rols, Context context) {
        Call<Rol> call = RolDTO.obtenirJSONRol().create(RolDTO.RequestRol.class).getRol(uuid.toString());
        call.enqueue(new Callback<Rol>() {
            @Override
            public void onResponse(Call<Rol> call, Response<Rol> response) {
                if (response.isSuccessful()) {
                    Rol rol = response.body();
                    if (rol != null) {
                        llRol.setVisibility(View.VISIBLE);
                        txtRol.setText(rol.getNom());
                        for (Rol r : rols) {
                            if (r.getUuid().equals(rol.getUuid())) {
                                spinner.setSelection(rols.indexOf(r) + 1);
                                break;
                            }
                        }
                    } else {
                        txtRol.setText(context.getString(R.string.etiqeutaSenseRol));
                        spinner.setSelection(0);
                    }
                }
            }

            @Override
            public void onFailure(Call<Rol> call, Throwable t) {

            }
        });
    }
}
