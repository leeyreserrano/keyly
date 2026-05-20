package com.example.keyly_projecte_intermodular.gestions;

import static com.example.keyly_projecte_intermodular.gestions.GestionsCompartits.compartirCarpeta;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keyly_projecte_intermodular.CarpetaActivity;
import com.example.keyly_projecte_intermodular.R;
import com.example.keyly_projecte_intermodular.adapters.CarpetaAdapter;
import com.example.keyly_projecte_intermodular.adapters.RecercaAdapter;
import com.example.keyly_projecte_intermodular.dao.Carpeta;
import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.dto.CarpetaDTO;
import com.example.keyly_projecte_intermodular.dto.ItemDTO;
import com.example.keyly_projecte_intermodular.dto.UsuariDTO;
import com.example.keyly_projecte_intermodular.request.UsuariCompartitRequest;
import com.example.keyly_projecte_intermodular.utils.Permisos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionsCarpetes {

    private static ArrayList<Item> itemsG;
    private static ArrayList<Carpeta> carpetesG;
    private static ArrayList<Usuari> usuarisG;
    private static ArrayList<String> permisosG = new ArrayList<>();
    private static Carpeta carpetaCreadaG;
    public static void crearCarpeta(ArrayList<Item> itemsSeleccionats, ArrayList<Usuari> usuarisSeleccionats,
                                    Context context, ArrayList<Item> items, ArrayList<Usuari> usuaris,
                                    ArrayList<UsuariCompartitRequest> usuarisCompartitRequest, Carpeta carpetaCreada,
                                    ArrayList<Carpeta> carpetes, RecyclerView recyclerView,
                                    CarpetaAdapter carpetesAdapter, boolean esCompartint,
                                    Runnable onSuccess, LinearLayout layoutError,
                                    TextView txtTitolError, TextView txtDescripcioError,
                                    ImageView imgVError) {
        itemsSeleccionats.clear();
        usuarisSeleccionats.clear();
        itemsG = items;
        usuarisG = usuaris;
        carpetaCreadaG = carpetaCreada;
        carpetesG = carpetes;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_carpeta_editar, null);

        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Elements del AlertDialog
        LinearLayout llNomCarpeta = view.findViewById(R.id.llNomCarpeta);
        llNomCarpeta.setVisibility(View.VISIBLE);

        View vSeparador1 = view.findViewById(R.id.vSeparador1);
        vSeparador1.setVisibility(View.VISIBLE);

        LinearLayout llAfegirItems = view.findViewById(R.id.llDesplegableItems);
        llAfegirItems.setVisibility(View.VISIBLE);
        LinearLayout llContingutItems = view.findViewById(R.id.llContigutItems);
        llContingutItems.setVisibility(View.GONE);

        View vSeparador2 = view.findViewById(R.id.vSeparador2);
        vSeparador2.setVisibility(View.VISIBLE);

        LinearLayout llAfegirUsuaris = view.findViewById(R.id.llDesplegableUsuaris);
        llAfegirUsuaris.setVisibility(View.VISIBLE);
        LinearLayout llContingutUsuaris = view.findViewById(R.id.llContigutCompartir);
        llContingutUsuaris.setVisibility(View.GONE);

        View vSeparador3 = view.findViewById(R.id.vSeparador3);
        vSeparador3.setVisibility(View.VISIBLE);

        // Mostrar per afegir ítems a la carpeta
        llAfegirItems.setOnClickListener(c -> {
            if (llContingutUsuaris.getVisibility() == View.VISIBLE) {
                llContingutUsuaris.setVisibility(View.GONE);
                llContingutItems.setVisibility(View.VISIBLE);
            } else if (llContingutUsuaris.getVisibility() == View.GONE) {
                if (llContingutItems.getVisibility() == View.VISIBLE) {
                    llContingutItems.setVisibility(View.GONE);
                } else {
                    llContingutItems.setVisibility(View.VISIBLE);
                }
            }
        });

        // Mostrar per compartir a usuaris
        llAfegirUsuaris.setOnClickListener(c -> {
            if (llContingutItems.getVisibility() == View.VISIBLE) {
                llContingutItems.setVisibility(View.GONE);
                llContingutUsuaris.setVisibility(View.VISIBLE);
            } else if (llContingutItems.getVisibility() == View.GONE) {
                if (llContingutUsuaris.getVisibility() == View.VISIBLE) {
                    llContingutUsuaris.setVisibility(View.GONE);
                } else {
                    llContingutUsuaris.setVisibility(View.VISIBLE);
                }
            }
        });

        ImageButton imgBtnStarEdit = view.findViewById(R.id.imgBtnStar);
        EditText etNomCarpeta = view.findViewById(R.id.etNomCarpeta);
        AutoCompleteTextView aCTVCercarItems = view.findViewById(R.id.aCTVCercarItems);
        AutoCompleteTextView aCTVCercarUsuaris = view.findViewById(R.id.aCTVCercarCompartir);
        RecyclerView recyclerItems = view.findViewById(R.id.recyclerItems);
        RecyclerView recyclerUsuaris = view.findViewById(R.id.recyclerCompartir);
        Button btnGuardarCarpeta = view.findViewById(R.id.btnGuardarCarpeta);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        recyclerItems.setLayoutManager(new LinearLayoutManager(context));
        RecercaAdapter recercaAdapterItems = new RecercaAdapter(itemsSeleccionats, null, null, null, context);
        recyclerItems.setAdapter(recercaAdapterItems);

        recyclerUsuaris.setLayoutManager(new LinearLayoutManager(context));
        RecercaAdapter recercaAdapterUsuaris = new RecercaAdapter(null, null, usuarisSeleccionats, permisosG, context);
        recyclerUsuaris.setAdapter(recercaAdapterUsuaris);

        AtomicBoolean favActual = new AtomicBoolean(false);
        imgBtnStarEdit.setOnClickListener(c -> {
            if (favActual.get()) {
                imgBtnStarEdit.setImageResource(R.drawable.star);
                favActual.set(false);
            } else {
                imgBtnStarEdit.setImageResource(R.drawable.filled_star);
                favActual.set(true);
            }
        });

        /* *************************************** Ítems *************************************** */
        // Carregar ítems
        ItemDTO.RequestItem requestItem = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class);
        requestItem.getAllItems().enqueue(new Callback<ArrayList<Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    itemsG = new ArrayList<>();
                    itemsG.addAll(response.body());
                    //Log.d("ITEMS_CARPETA", items.toString());

                    // Cercador d'ítems
                    ArrayList<String> titols = new ArrayList<>();

                    for (Item item : itemsG) {
                        titols.add(item.getTitol());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, titols);
                    aCTVCercarItems.setAdapter(adapter);
                } else {
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Item>> call, Throwable t) {
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });

        // Cercador d'ítems
        aCTVCercarItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                aCTVCercarItems.showDropDown();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        aCTVCercarItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String seleccionat = parent.getItemAtPosition(position).toString();

                for (Item item : itemsG) {
                    if (item.getTitol().equals(seleccionat) && !itemsSeleccionats.contains(item)) {
                        itemsSeleccionats.add(item);
                    }
                }

                recercaAdapterItems.notifyDataSetChanged();
                recyclerItems.setAdapter(recercaAdapterItems);
            }
        });
        /* ************************************************************************************* */



        /* ************************************** Usuaris ************************************** */
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

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, noms);
                    aCTVCercarUsuaris.setAdapter(adapter);
                } else {
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Usuari>> call, Throwable t) {
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });

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
                        permisosG.add(Permisos.LECTURA.name());
                    }
                }
                recercaAdapterUsuaris.notifyDataSetChanged();
                recyclerUsuaris.setAdapter(recercaAdapterUsuaris);
            }
        });
        /* ************************************************************************************* */

        btnGuardarCarpeta.setText(context.getString(R.string.btnAfegirCarpetes));
        btnGuardarCarpeta.setOnClickListener(c -> {

            if (esCompartint && usuarisSeleccionats.isEmpty()) {
                Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                return;
            }

            String nomCarpeta = etNomCarpeta.getText().toString();
            boolean isFavorit = favActual.get();

            Carpeta carpeta = new Carpeta(nomCarpeta, isFavorit);

            Call<Carpeta> call = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).crearCarpeta(carpeta);
            call.enqueue(new Callback<Carpeta>() {
                @Override
                public void onResponse(Call<Carpeta> call, Response<Carpeta> response) {
                    if (response.isSuccessful()) {
                        carpetaCreadaG = response.body();
                        Toast.makeText(context, context.getString(R.string.toastCarpetaCreada, nomCarpeta), Toast.LENGTH_SHORT).show();
                        if (usuarisSeleccionats.size() > 0) {
                            usuarisCompartitRequest.clear();
                            for (int i = 0; i < usuarisSeleccionats.size(); i++) {
                                Permisos permis = (!permisosG.isEmpty() && i < permisosG.size())
                                        ? Permisos.valueOf(permisosG.get(i))
                                        : Permisos.LECTURA;

                                usuarisCompartitRequest.add(new UsuariCompartitRequest(
                                        usuarisSeleccionats.get(i).getUuid(),
                                        permis,
                                        new ArrayList<>()   // carpeta sin dataKey propia
                                ));
                            }
                            try {
//                                compartirCarpeta(
//                                        itemsSeleccionats,
//                                        usuarisCompartitRequest,
//                                        usuarisSeleccionats,
//                                        carpetaCreadaG,
//                                        recyclerView
//                                );
                                compartirCarpeta(
                                        carpetaCreadaG,
                                        usuarisCompartitRequest,
                                        itemsSeleccionats,
                                        usuarisSeleccionats,
                                        permisosG,
                                        recyclerView
                                );
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        } else if (itemsSeleccionats.size() > 0) {
                            for (int i = 0; i < itemsSeleccionats.size(); i++) {
                                Call<Item> callAddItem = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class).afegirItemCarpeta(carpetaCreada.getUuid().toString(), itemsSeleccionats.get(i).getUuid().toString());
                                callAddItem.enqueue(new Callback<Item>() {
                                    @Override
                                    public void onResponse(Call<Item> callAddItem, Response<Item> response) {
                                        if (response.isSuccessful()) {
                                            Log.e("ITEMS_AFEGIT_CARPETES", response.body().toString());
                                            Log.d("ITEMS_AFEGITS", itemsSeleccionats.toString());

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
                        alertDialog.dismiss();
                        if (!esCompartint) {
                            obtenirCarpetes(
                                    carpetesG,
                                    recyclerView,
                                    0,
                                    false,
                                    layoutError,
                                    txtTitolError,
                                    txtDescripcioError,
                                    imgVError,
                                    carpetesAdapter,
                                    context
                            );
                            actualitzarCarpetes(carpetesG, carpetesAdapter, context, recyclerView);
                        }
                        if (onSuccess != null) onSuccess.run();
                    } else {
                        Toast.makeText(context, context.getString(R.string.toastCarpetaNoCreada, nomCarpeta), Toast.LENGTH_SHORT).show();
                        Log.d("ERROR_RESPONSE", response.message());
                    }
                }

                @Override
                public void onFailure(Call<Carpeta> call, Throwable t) {
                    Toast.makeText(context, context.getString(R.string.toastCarpetaNoCreada, nomCarpeta), Toast.LENGTH_SHORT).show();
                    Log.d("ERROR_FAILURE", t.getMessage());
                }
            });
        });

        btnCancelar.setOnClickListener(c -> {
            alertDialog.dismiss();
        });
    }

    public static void obtenirCarpetes(ArrayList<Carpeta> carpetes, RecyclerView recyclerView,
                                        int filtre, boolean fav, LinearLayout layoutError,
                                        TextView txtTitolError, TextView txtDescripcioError,
                                        ImageView imgVError, CarpetaAdapter carpetesAdapter,
                                       Context context) {
        CarpetaDTO.RequestCarpeta resquestCarpeta = CarpetaDTO.obtenirJSONCarpeta().create(CarpetaDTO.RequestCarpeta.class);
        resquestCarpeta.getAllCarpetes().enqueue(new Callback<ArrayList<Carpeta>>() {

            @Override
            public void onResponse(Call<ArrayList<Carpeta>> call, Response<ArrayList<Carpeta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    carpetes.clear();
                    carpetes.addAll(response.body());
                    if (carpetes.size() > 0) {
                        ArrayList<Carpeta> carpetesF = new ArrayList<>();
                        if (filtre == 0) {
                            carpetesF = carpetes;
                            actualitzarCarpetes(carpetesF, carpetesAdapter, context, recyclerView);
                        } else if (filtre == 1) { // Mostrar les carpetes més utilitzades
                            carpetesF = carpetes;
                            carpetesF.sort(Comparator.comparing(Carpeta::getComptadorAccess).reversed());
                            actualitzarCarpetes(carpetesF, carpetesAdapter, context, recyclerView);
                        } else if (filtre == 2) { // Mostrar les carpetes últimes accedides
                            DateTimeFormatter formatter =
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                            for (Carpeta carpeta : carpetes) {
                                if (carpeta.getUltimAccess() != null) {
                                    carpetesF.add(carpeta);
                                }
                            }
                            carpetesF.sort(
                                    Comparator.comparing(
                                            (Carpeta c) ->
                                                    LocalDateTime.parse(c.getUltimAccess(), formatter)
                                    ).reversed()
                            );
                            actualitzarCarpetes(carpetesF, carpetesAdapter, context, recyclerView);
                        }

                        if (fav) { // Mostrar les carpetes favorites
                            ArrayList<Carpeta> carpetesFav = new ArrayList<>();
                            for (Carpeta carpeta : carpetesF) {
                                if (carpeta.isFavorit()) {
                                    carpetesFav.add(carpeta);
                                }
                            }
                            actualitzarCarpetes(carpetesFav, carpetesAdapter, context, recyclerView);
                        }
                        //carpetesAdapter.notifyDataSetChanged();
                        recyclerView.setVisibility(RecyclerView.VISIBLE);
                        layoutError.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(RecyclerView.GONE);
                        layoutError.setVisibility(View.VISIBLE);
                        layoutError.setBackground(ContextCompat.getDrawable(
                                context, R.drawable.background_log_empty));
                        txtTitolError.setText(context.getString(R.string.titolCarpetesBuides));
                        txtDescripcioError.setText(context.getString(R.string.etiquetaCarpetesBuides));
                        imgVError.setImageResource(R.drawable.carpeta_negra);
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    layoutError.setVisibility(View.VISIBLE);
                    layoutError.setBackground(ContextCompat.getDrawable(
                            context, R.drawable.background_log_error));
                    txtTitolError.setText("ERROR");
                    txtDescripcioError.setText(context.getString(R.string.etiquetaCarpetesError));
                    imgVError.setImageResource(R.drawable.error);
                    Log.d("ERROR_RESPONSE", response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Carpeta>> call, Throwable t) {
                Log.d("ERROR_FAILURE", t.getMessage());
            }
        });
    }

    public static void actualitzarCarpetes(ArrayList<Carpeta> carpetes, CarpetaAdapter carpetesAdapter,
                                            Context context, RecyclerView recyclerView){
        carpetesAdapter = new CarpetaAdapter(carpetes, carpeta -> {
            Intent intent = new Intent(context, CarpetaActivity.class);
            intent.putExtra("carpeta", carpeta);
            intent.putExtra("uuid", carpeta.getUuid().toString());
            intent.putExtra("nom", carpeta.getNom());
            intent.putExtra("favorit", carpeta.isFavorit());
            intent.putExtra("items", new ArrayList<>(carpeta.getItems()));
            intent.putExtra("data_creacio", carpeta.getDataCreacio());
            intent.putExtra("data_edicio", carpeta.getDataEditat());
            Log.d("DATA_CREACIO", carpeta.getDataCreacio());
            context.startActivity(intent);
        }, context);
        recyclerView.setAdapter(carpetesAdapter);
    }
}
