package com.example.keyly_projecte_intermodular.utils;

import static com.example.keyly_projecte_intermodular.config.TokenForEver.dataKey;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt {
    public static SecretKey clauDerivada;
    // FUNCIONA
    public static SecretKey clauDerivada(String contrasenyaLogin, String kdfSaltUsuari) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] saltBytes = Base64.decode(kdfSaltUsuari, Base64.DEFAULT);

        KeySpec spec = new PBEKeySpec(
                contrasenyaLogin.toCharArray(),
                saltBytes,
                //kdfSaltUsuari.getBytes(),
                310000,
                256
        );

        SecretKey tmp = factory.generateSecret(spec);
        //SecretKey derivedKey = new SecretKeySpec(tmp.getEncoded(), "AES");
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    public static byte[] encriptarContrasenya(String passwordItem, byte[] iv) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        GCMParameterSpec spec = new GCMParameterSpec(128, iv);

        cipher.init(Cipher.ENCRYPT_MODE, clauDerivada, spec);

        byte[] encrypted = cipher.doFinal(passwordItem.getBytes(StandardCharsets.UTF_8));

        return encrypted;
    }

    // FUNCIONA
    public static byte[] encriptarContrasenya2(String password, PublicKey publicKey, byte[] iv) throws Exception {
        dataKey = generarDataKey();
        new SecureRandom().nextBytes(iv);

        // Xifrar contrasenya amb AES-GCM
        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        aesCipher.init(Cipher.ENCRYPT_MODE,
                new SecretKeySpec(dataKey, "AES"),
                new GCMParameterSpec(128, iv));
        byte[] encPsw = aesCipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
        return encPsw;
    }
    // FUNCIONA
    public static byte[] encriptarDataKey(PublicKey publicKey, byte[] dataKey) throws Exception {
        // Xifrar dataKey amb la publicKey RSA
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encDataKey = rsaCipher.doFinal(dataKey);
        return encDataKey;
    }

    // FUNCIONA
    public static byte[] desencriptarDataKey(PrivateKey privateKey, String edk) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] dataKeyRaw = rsaCipher.doFinal(Base64.decode(edk, Base64.DEFAULT));
        return dataKeyRaw;
    }

    // FUNCIONA
    public static String cypherIV(String iv, String contrasenya) {
        byte[] ivBytes = Base64.decode(iv, Base64.DEFAULT);
        byte[] ctBytes = Base64.decode(contrasenya, Base64.DEFAULT);
        byte[] combined = new byte[ivBytes.length + ctBytes.length];
        System.arraycopy(ivBytes, 0, combined, 0, ivBytes.length);
        System.arraycopy(ctBytes, 0, combined, ivBytes.length, ctBytes.length);
        String combinedB64 = Base64.encodeToString(combined, Base64.NO_WRAP);
        return combinedB64;
    }

    public static String desencriptarContrasenya(byte[] encrypted, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        GCMParameterSpec spec = new GCMParameterSpec(128, iv);

        cipher.init(Cipher.DECRYPT_MODE, clauDerivada, spec);

        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // FUNCIONA
    public static byte[] desencriptarContrasenya2(String priKeyEncrypt, byte[] clauDerivada) throws Exception {
        byte[] data = Base64.decode(priKeyEncrypt, Base64.NO_WRAP);
        byte[] iv = Arrays.copyOfRange(data, 0, 12);
        byte[] ct = Arrays.copyOfRange(data, 12, data.length);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE,
                new SecretKeySpec(clauDerivada, "AES"),
                new GCMParameterSpec(128, iv));
        return cipher.doFinal(ct);
    }

    // FUNCIONA
    public static byte[] generarDataKey() {
        byte[] dataKey = new byte[32]; // AES-256
        new SecureRandom().nextBytes(dataKey);
        return dataKey;
    }
}
