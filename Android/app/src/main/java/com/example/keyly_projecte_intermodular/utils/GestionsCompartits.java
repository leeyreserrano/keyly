package com.example.keyly_projecte_intermodular.utils;

import static com.example.keyly_projecte_intermodular.resources.Varis.privateKeyDecrypt;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.desencriptarDataKey;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.encriptarDataKey;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.stringToPublicKey;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.CarpetaActivity;
import com.example.keyly_projecte_intermodular.dao.Carpeta;
import com.example.keyly_projecte_intermodular.dao.Compartit;
import com.example.keyly_projecte_intermodular.dao.EncryptedDataKey;
import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.dto.CarpetaDTO;
import com.example.keyly_projecte_intermodular.dto.CompartitDTO;
import com.example.keyly_projecte_intermodular.dto.UsuariDTO;
import com.example.keyly_projecte_intermodular.request.CompartitRequest;
import com.example.keyly_projecte_intermodular.request.UsuariCompartitRequest;
import com.example.keyly_projecte_intermodular.adapters.CarpetaAdapter;
import com.example.keyly_projecte_intermodular.adapters.RecercaAdapter;
import com.example.keyly_projecte_intermodular.request.UsuariRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionsCompartits {
    private static ArrayList<Item> itemsG;
    private static ArrayList<Carpeta> carpetesG;
    private static ArrayList<Usuari> usuarisG, usuarisSeleccionatsG;
    private static ArrayList<String> permisosG = new ArrayList<>();
    private static Carpeta carpetaCreadaG;
    private static int posItemCompartit = 0;

    public static void obtenirCompartits(Context context, ArrayList<Compartit> compartits,
                                         int filtre, boolean fav, RecyclerView recyclerView) {
        CompartitDTO.RequestCompartit  resquestCompartit = CompartitDTO.obtenirJSONCompartit().create(CompartitDTO.RequestCompartit.class);
        resquestCompartit.getAllCompartit().enqueue(new Callback<ArrayList<Compartit>>() {
            @Override
            public void onResponse(Call<ArrayList<Compartit>> call, Response<ArrayList<Compartit>> response) {
                if (response.isSuccessful()) {
                    compartits.clear();
                    compartits.addAll(response.body());
                    for (Compartit compartit : compartits) {
                        if (compartit.getTipusEntitat() == TipusEntitat.ITEM) {
                            compartit.setComptadorAccess(compartit.getItem().getComptadorAccess());
                            compartit.setUltimAccess(compartit.getItem().getUltimAccess());
                        } else if (compartit.getTipusEntitat() == TipusEntitat.CARPETA) {
                            compartit.setComptadorAccess(compartit.getCarpeta().getComptadorAccess());
                            compartit.setUltimAccess(compartit.getCarpeta().getUltimAccess());
                        }
                    }
                    Log.d("COMPARTITS", response.body().toString());
                    ArrayList<Compartit> compartitsF = new ArrayList<>();
                    if (filtre == 0) { // Mostrar tots els compartits
                        compartitsF = compartits;
                        //actualitzarCompartits(compartitsF);
                    } else if (filtre == 1) { // Mostrar els útltims compartits usats
                        for (Compartit compartit : compartits) {
                            if (compartit.getUltimAccess() != null) {
                                compartitsF.add(compartit);
                            }
                        }
                        compartitsF.sort(
                                Comparator.comparing(
                                        (Compartit c) ->
                                                LocalDateTime.parse(c.getUltimAccess())
                                ).reversed()
                        );
                        //actualitzarCompartits(compartitsF);
                    } else if (filtre == 2) { // Mostrar els compartits utilitzats
                        compartitsF = compartits;
                        compartitsF.sort(Comparator.comparing(Compartit::getComptadorAccess).reversed());
                        //actualitzarCompartits(compartitsF);
                    }
                    if (fav) { // Mostrar els compartits favorits
                        ArrayList<Compartit> compartitsFavorits = new ArrayList<>();
                        for (Compartit compartit : compartitsF) {
                            if (compartit.getTipusEntitat() == TipusEntitat.ITEM) {
                                if (compartit.getItem().isFavorit()) {
                                    compartitsFavorits.add(compartit);
                                }
                            } else if (compartit.getTipusEntitat() == TipusEntitat.CARPETA) {
                                if (compartit.getCarpeta().isFavorit()) {
                                    compartitsFavorits.add(compartit);
                                }
                            }
                        }
                        //actualitzarCompartits(compartitsFavorits);
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    Log.d("ERROR_RESPONSE_COMPARTITS", response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Compartit>> call, Throwable t) {
                Log.d("ERROR_FAILURE_COMPARTITS", t.getMessage());
            }
        });
    }

    public static void obtenirUsuaris(ArrayList<Usuari> usuaris, ArrayList<Usuari> usuarisSeleccionats,
                                       ArrayList<String> permisos, RecyclerView recyclerUsuaris,
                                      RecercaAdapter recercaAdapterUsuaris, AutoCompleteTextView aCTVCercarUsuaris,
                                      Context context, boolean usuarisCompartits) {
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

                    for (Usuari usuari : usuarisG) {
                        noms.add(usuari.getNom());
                    }

                    if (usuarisCompartits) {

                    } else {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, noms);
                        aCTVCercarUsuaris.setAdapter(adapter);
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

        if (usuarisCompartits) {

        } else {
            cercarUsuaris(recyclerUsuaris, recercaAdapterUsuaris, aCTVCercarUsuaris, context, permisos, usuarisSeleccionats);
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

    public static void compartirCarpeta(Carpeta carpeta, ArrayList<UsuariCompartitRequest> usuarisCompartitRequest,
                                  ArrayList<Item> itemsSeleccionats, ArrayList<Usuari> usuarisSeleccionats,
                                  ArrayList<String> permisos, RecyclerView recyclerView) {

        posItemCompartit = 0;

        Log.d("CARPETA_CREADA_COMPARTIR", carpeta.toString());

        CompartitRequest compartitRequestC = new CompartitRequest(carpeta.getUuid(), TipusEntitat.CARPETA, usuarisCompartitRequest);

        Call<Void> callC = CompartitDTO.obtenirJSONCompartit().create(CompartitDTO.RequestCompartit.class).compartir(compartitRequestC);
        callC.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.e("CARPETA_COMPARTIDA", "Carpeta " + carpeta.getNom() + " compartida");
                    if (itemsSeleccionats.size() > 0) {
                        usuarisCompartitRequest.clear();
                        for (Item item : itemsSeleccionats) {
                            Call<Item> callAddItem = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).afegirItemCarpeta(carpeta.getUuid().toString(), item.getUuid().toString());
                            callAddItem.enqueue(new Callback<Item>() {
                                @Override
                                public void onResponse(Call<Item> callAddItem, Response<Item> response) {
                                    if (response.isSuccessful()) {
                                        Log.e("ITEMS_AFEGIT_CARPETES", response.body().toString());
                                        Log.d("ITEMS_AFEGITS", itemsSeleccionats.toString());

                                        ArrayList<EncryptedDataKey> encryptedDataKeysI = new ArrayList<>();

                                        for (Usuari usuari : usuarisSeleccionats) {
                                            // TODO desencriptar datakey
                                            byte[] dataKeyDecrypted = null;
                                            byte[] dataKeyEncrypted = null;
                                            try {
                                                dataKeyDecrypted = desencriptarDataKey(privateKeyDecrypt, item.getEncryptedDataKey().getEncryptedDataKey());
                                                dataKeyEncrypted = encriptarDataKey(stringToPublicKey(usuari.getPublicKey()), dataKeyDecrypted);
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }

                                            String encryptedDataKeyBase64 = Base64.encodeToString(dataKeyEncrypted, Base64.DEFAULT);
                                            EncryptedDataKey edk = new EncryptedDataKey(null, encryptedDataKeyBase64);
                                            encryptedDataKeysI.add(edk);
                                        }

                                        Permisos permis = Permisos.valueOf(permisos.get(posItemCompartit));
                                        for (Usuari usuari : usuarisSeleccionats) {
                                            usuarisCompartitRequest.add(new UsuariCompartitRequest(
                                                    usuari.getUuid(),
                                                    permis,
                                                    encryptedDataKeysI));
                                        }

                                        try {
                                            compartirItem(item, usuarisCompartitRequest, recyclerView);
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else {
                                        Log.d("ERROR_RESPONSE", response.message());
                                    }
                                }

                                @Override
                                public void onFailure(Call<Item> callAddItem, Throwable t) {
                                    Log.d("ERROR_FAILURE", t.getMessage());
                                }
                            });
                        }
                    }
                } else {
                    Log.e("ERROR_RESPONSE", response.message());
                    try {
                        Log.e("ERROR_BODY_RESPONSE", response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    public static void compartirItem(Item item, ArrayList<UsuariCompartitRequest> usuarisCompartitRequest, RecyclerView recyclerView) {
        CompartitRequest compartitRequestI = new CompartitRequest(item.getUuid(), TipusEntitat.ITEM, usuarisCompartitRequest);
        Call<Void> callI = CompartitDTO.obtenirJSONCompartit().create(CompartitDTO.RequestCompartit.class).compartir(compartitRequestI);
        callI.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> callI, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.e("CARPETA_COMPARTIDA", "Ítem " + item.getTitol() + " compartit");
                } else {
                    Log.e("ERROR_RESPONSE", response.message());
                    try {
                        Log.e("ERROR_BODY_RESPONSE", response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> callI, Throwable t) {
                Log.e("ERROR_FAILURE", t.getMessage());
            }
        });
    }
}
