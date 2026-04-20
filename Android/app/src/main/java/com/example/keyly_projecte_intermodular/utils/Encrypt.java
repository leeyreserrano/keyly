package com.example.keyly_projecte_intermodular.utils;

import com.example.keyly_projecte_intermodular.dao.Usuari;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

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
    private static byte[] iv = new byte[12];
    public static SecretKey clauDerivada(String contrasenyaLogin, String kdfSaltUsuari) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        KeySpec spec = new PBEKeySpec(
                contrasenyaLogin.toCharArray(),
                kdfSaltUsuari.getBytes(), // byte[]
                100000,                  // iteraciones (mínimo decente)
                256                     // bits
        );

        SecretKey tmp = factory.generateSecret(spec);
        //SecretKey derivedKey = new SecretKeySpec(tmp.getEncoded(), "AES");
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    public static byte[] encriptarContrasenya(String passwordItem) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        GCMParameterSpec spec = new GCMParameterSpec(128, iv);

        cipher.init(Cipher.ENCRYPT_MODE, clauDerivada, spec);

        byte[] encrypted = cipher.doFinal(passwordItem.getBytes(StandardCharsets.UTF_8));

        return encrypted;
    }

    public static void desencriptarContrasenya(byte[] encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        GCMParameterSpec spec = new GCMParameterSpec(128, iv);

        cipher.init(Cipher.DECRYPT_MODE, clauDerivada, spec);

        byte[] decrypted = cipher.doFinal(encrypted);

        String password = new String(decrypted, StandardCharsets.UTF_8);
    }
}
