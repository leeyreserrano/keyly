package com.example.keyly_projecte_intermodular.resources;

import static com.example.keyly_projecte_intermodular.utils.Encrypt.clauDerivada;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.cypherIV;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.desencriptarContrasenya2;
import static com.example.keyly_projecte_intermodular.utils.Encrypt.desencriptarDataKey;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keyly_projecte_intermodular.R;
import com.example.keyly_projecte_intermodular.dao.Hash;
import com.example.keyly_projecte_intermodular.dao.Item;
import com.example.keyly_projecte_intermodular.dao.Usuari;
import com.example.keyly_projecte_intermodular.dto.ItemDTO;
import com.example.keyly_projecte_intermodular.dto.UsuariDTO;
import com.example.keyly_projecte_intermodular.dto.UtilsDTO;
import com.example.keyly_projecte_intermodular.rest_api.ApiService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lombok.AllArgsConstructor;
import lombok.Data;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Data
@AllArgsConstructor
public class Varis {

    public static String tokenFE = "eyJhbGciOiJIUzI1NiJ9.eyJyb2wiOiJBRE1JTiIsInN1YiI6IjZkYTlkNDkyLTI3OGMtMTFmMS1hMmE0LTcyZWFiYWUzNTI3MCIsImlhdCI6MTc3NDM2NTYzMSwiZXhwIjoxOTc3NDM2NTYzMX0.iGgCPYvGGgig2FAy8uFvarD6N1GvC5Bq21oR8Emheo8";
    // https://10.147.17.250:8081 (REAL)
    // 192.168.137.60 (local gerard hoy)
    public static final String BASE_URL = "https://10.147.17.250:8081";

    public static String tokenNou = "";
    public static String privateKeyEncrypt = "";
    public static PrivateKey privateKeyDecrypt;
    public static PublicKey publicKey;
    public static byte[] dataKey;

    public static Usuari usuariPropi;
    public static String clauMestra;
    private static int qtyVulnerablesResum = 0;
    public static Drawable svgPerfilAltre = null;
    public static Bitmap imatgePerfilAltre = null;

    public static String tempsCreatEditat(String data, boolean editat, boolean editatAlMoment) {
//        LocalDateTime dataCreacio = LocalDateTime.parse(data);
//        LocalDateTime dataActual = LocalDateTime.now();
//
//        long segundos = Duration.between(dataCreacio, dataActual).getSeconds();
//
//        if (segundos < 60) {
//            return "Creado hace " + segundos + " segundos";
//        }
//
//        long minutos = segundos / 60;
//
//        if (minutos < 60) {
//            return "Creado hace " + minutos + " minutos";
//        }
//
//        long horas = minutos / 60;
//
//        if (horas < 24) {
//            return "Creado hace " + horas + " horas";
//        }
//
//        long dias = horas / 24;
//
//        if (dias < 7) {
//            return "Creado hace " + dias + " días";
//        }
//
//        long semanas = dias / 7;
//
//        if (semanas < 4) {
//            return "Creado hace " + semanas + " semanas";
//        }
//
//        long meses = ChronoUnit.MONTHS.between(dataCreacio, dataActual);
//
//        if (meses < 12) {
//            return "Creado hace " + meses + " meses";
//        }
//
//        long años = ChronoUnit.YEARS.between(dataCreacio, dataActual);
//
//        return "Creado hace " + años + " años";

//        LocalDateTime fechaCreacion = LocalDateTime.parse(data);

        if (data.contains(".")) {
            String[] parts = data.split("\\.");
            String decimals = parts[1].length() > 3
                    ? parts[1].substring(0, 3)
                    : parts[1];

            data = parts[0] + "." + decimals;
        }

        DateTimeFormatter formatter;

        if (editatAlMoment) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        }

        // Parsear com UTC
        LocalDateTime utcDateTime =
                LocalDateTime.parse(data, formatter);

        // Convertir a ZonedDateTime UTC
        ZonedDateTime utcZoned =
                utcDateTime.atZone(ZoneOffset.UTC);

        // Convertir a hora local del dispositiu
        ZonedDateTime localDateTime =
                utcZoned.withZoneSameInstant(
                        ZoneId.systemDefault()
                );

        // Hora actual local
        ZonedDateTime ahora =
                ZonedDateTime.now();

        long segundos =
                Duration.between(localDateTime, ahora)
                        .getSeconds();

        Log.d("Fecha UTC", utcZoned.toString());
        Log.d("Fecha Local", localDateTime.toString());
        Log.d("Ahora", ahora.toString());
        Log.d("Segundos", segundos + "");

        if (segundos < 60) {
            if (editat) {
                return "Editado hace " + segundos + " segundos";
            } else {
                return "Creado hace " + segundos + " segundos";
            }
        }

        long minutos = segundos / 60;

        if (minutos < 60) {
            if (editat) {
                return "Editado hace " + minutos + " minutos";
            } else {
                return "Creado hace " + minutos + " minutos";
            }
        }

        long horas = minutos / 60;

        if (horas < 24) {
            if (editat) {
                return "Editado hace " + horas + " horas";
            } else {
                return "Creado hace " + horas + " horas";
            }
        }

        long dias = horas / 24;

        if (editat) {
            return "Editado hace " + dias + " días";
        } else {
            return "Creado hace " + dias + " días";
        }
    }

    public static void getImage(Consumer<ResponseBody> onResult) {
        // TODO obtenir imatge perfil
        Call<ResponseBody> call = UsuariDTO.obtenirJSONUsuari().create(UsuariDTO.RequestUsuari.class).getImage();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    onResult.accept(response.body());
                } else {
                    Log.e("ERROR_RESPONSE_IMG", response.message());
                    try {
                        Log.e("ERROR_RESPONSE_IMG", "Body error: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("ERROR_RESPONSE_IMG", "No s'ha pogut llegir el errorBody");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERROR_FAILURE_IMG", t.getMessage());
            }
        });
    }

    public static void getImatgeUUID(Usuari usuari, Consumer<ResponseBody> onResult) {
        Call<ResponseBody> call = UsuariDTO.obtenirJSONUsuari().create(UsuariDTO.RequestUsuari.class).getImageUUID(usuari.getUuid().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        onResult.accept(response.body());
                    } catch (Exception e) {
                        Log.e("ERROR_RESPONSE_IMG", "Error llegint body: " + e.getMessage());
                    }
                } else {
                    Log.e("ERROR_RESPONSE_IMG_HTTP", "HTTP CODE: " + response.code());
                    Log.e("ERROR_RESPONSE_IMG", response.message());
                    try {
                        Log.e("ERROR_RESPONSE_IMG", "Body error: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("ERROR_RESPONSE_IMG", "No s'ha pogut llegir el errorBody");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERROR_FAILURE_IMG", t.getMessage());
            }
        });
    }

    public static void pujarImatgeAPI(Uri uri, boolean esCreant, Context context) {
        try {
            // Cargar el bitmap desde la Uri
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            // Redimensionar y comprimir
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 800, 800, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG, 60, baos);

            // Escribir en el File temporal ya comprimido
            File tempFile = File.createTempFile("perfil", ".jpg", context.getCacheDir());
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(baos.toByteArray());
            fos.close();

            // Crear RequestBody multipart
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), tempFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", tempFile.getName(), requestFile);

            Call<String> call = UsuariDTO.obtenirJSONUsuari().create(UsuariDTO.RequestUsuari.class).pujarImatge(body);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        if (!esCreant) {
                            Toast.makeText(context, context.getString(R.string.toastImatgePerfil), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, context.getString(R.string.toastNoImatgePerfil), Toast.LENGTH_SHORT).show();
                        //Log.e("ERROR_RESPONSE_IMG", response.message());
                        try {

                            Log.e("ERROR_RESPONSE_IMG",
                                    "Code: " + response.code());

                            Log.e("ERROR_RESPONSE_IMG",
                                    "Message: " + response.message());

                            if (response.errorBody() != null) {

                                Log.e("ERROR_RESPONSE_IMG",
                                        "Body: " + response.errorBody().string());
                            }

                        } catch (Exception e) {

                            Log.e("ERROR_RESPONSE_IMG",
                                    "Error llegint errorBody",
                                    e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(context, context.getString(R.string.toastNoImatgePerfil), Toast.LENGTH_SHORT).show();
                    Log.e("ERROR_FAILURE_IMG", t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("ERROR_FAILURE_IMG_CATCH", e.getMessage());
        }
    }

    public static void comprovarVulnerabilitatContrasenya(String contrasenya,
                                                          Context context,
                                                          View includeVulnerable,
                                                          boolean resum,
                                                          TextView txtVulneradesQty,
                                                          int qtyVulnerables) {
        try {
            // Generar el hash SHA-1 de la contrasenya
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = digest.digest(contrasenya.getBytes(StandardCharsets.UTF_8));

            // Convertir a string hexadecimal en majúscules
            StringBuilder hexHash = new StringBuilder();
            for (byte b : hashBytes) {
                hexHash.append(String.format("%02X", b));
            }
            String hash = hexHash.toString().toLowerCase();

            // Dividir en prefix (5 primers chars) i sufix (els 2 últims chars)
            String prefix = hash.substring(0, 5);
            String suffix = hash.substring(5, 7);
            Log.d("PREFIX", prefix);
            Log.d("SUFFIX", suffix);

            qtyVulnerablesResum = qtyVulnerables;

            // Cridar a la API
            Call<ArrayList<Hash>> call = UtilsDTO.obtenirJSONPassword().create(UtilsDTO.RequestUtils.class).comprovarContrasenya(prefix, suffix);
            call.enqueue(new Callback<ArrayList<Hash>>() {
                @Override
                public void onResponse(Call<ArrayList<Hash>> call, Response<ArrayList<Hash>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ArrayList<Hash> hashes = response.body();
                        Log.d("HASHES", hashes.toString());

                        // Comprovar si és vulnerable la contrasenya
                        boolean esVulnerable = false;
                        for (Hash h : hashes) {
                            if (h.getHash().equalsIgnoreCase(hash)) {
                                esVulnerable = true;
                                break;
                            }
                        }

                        if (esVulnerable) {
                            if (resum) {
                                qtyVulnerablesResum++;
                                txtVulneradesQty.setText(String.valueOf(qtyVulnerablesResum));
                            } else {
                                includeVulnerable.setVisibility(View.VISIBLE);
                                TextView txtTitolAvisVulnerat = includeVulnerable.findViewById(R.id.txtTitolAvis);
                                txtTitolAvisVulnerat.setText("Contrasenya vulnerable");
                            }
                        } else {
                            if (!resum) {
                                includeVulnerable.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        Log.e("ERROR_RESPONSE_HASH", response.message());
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Hash>> call, Throwable t) {
                    Log.e("ERROR_FAILURE_HASH", t.getMessage());
                }
            });

        } catch (NoSuchAlgorithmException e) {
            Log.e("ERROR_CATCH_HASH", e.getMessage());
        }
    }

    public static void obtenirTotalVulnerades(Context context, View includeVulnerable, TextView txtVulneradesQty) {
        Call<ArrayList<Item>> call = ItemDTO.obtenirJSONItem().create(ItemDTO.RequestItem.class).getAllItems();
        call.enqueue(new Callback<ArrayList<Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Item> items = response.body();
                    for (Item item : items) {
                        String edk = item.getEncryptedDataKey().getEncryptedDataKey();
                        byte[] dataKey = null;
                        try {
                            dataKey = desencriptarDataKey(privateKeyDecrypt, edk);
                        } catch (NoSuchPaddingException e) {
                            throw new RuntimeException(e);
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        } catch (InvalidKeyException e) {
                            throw new RuntimeException(e);
                        } catch (BadPaddingException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalBlockSizeException e) {
                            throw new RuntimeException(e);
                        } catch (InvalidAlgorithmParameterException e) {
                            throw new RuntimeException(e);
                        }
                        String combined64 = cypherIV(item.getIv(), item.getContrasenya());
                        byte[] contrasenyaByte = null;
                        try {
                            contrasenyaByte = desencriptarContrasenya2(combined64, dataKey);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        String contrasenya = new String(contrasenyaByte, StandardCharsets.UTF_8);
                        comprovarVulnerabilitatContrasenya(contrasenya, context, includeVulnerable,
                                true, txtVulneradesQty, 0);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Item>> call, Throwable t) {

            }
        });
    }
}
