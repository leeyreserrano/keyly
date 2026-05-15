package com.example.keyly_projecte_intermodular.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keyly_projecte_intermodular.dao.Departament;
import com.example.keyly_projecte_intermodular.dao.Rol;
import com.example.keyly_projecte_intermodular.dao.Sucursal;
import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.dto.DepartamentDTO;
import com.example.keyly_projecte_intermodular.dto.RolDTO;
import com.example.keyly_projecte_intermodular.dto.SucursalDTO;
import com.example.keyly_projecte_intermodular.dto.UsuariDTO;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionsUsuaris {
    public static void obtenirSucursalUUID(UUID uuid, LinearLayout llSucursal, TextView txtSucursal,
                                           Spinner spinner, ArrayList<Sucursal> sucursals) {
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
                        txtSucursal.setText("Sense sucursal");
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
                                              Spinner spinner, ArrayList<Departament> departaments) {
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
                        txtDepartament.setText("Sense departament");
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
                                      Spinner spinner, ArrayList<Rol> rols) {
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
                        txtRol.setText("Sense rol");
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
