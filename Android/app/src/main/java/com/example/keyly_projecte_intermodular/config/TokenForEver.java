package com.example.keyly_projecte_intermodular.config;

import java.security.PrivateKey;
import java.security.PublicKey;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenForEver {

    public static String tokenFE = "eyJhbGciOiJIUzI1NiJ9.eyJyb2wiOiJBRE1JTiIsInN1YiI6IjZkYTlkNDkyLTI3OGMtMTFmMS1hMmE0LTcyZWFiYWUzNTI3MCIsImlhdCI6MTc3NDM2NTYzMSwiZXhwIjoxOTc3NDM2NTYzMX0.iGgCPYvGGgig2FAy8uFvarD6N1GvC5Bq21oR8Emheo8";
    // https://10.147.17.250:8081 (REAL)
    // 192.168.137.60 (local gerard hoy)
    public static final String BASE_URL = "https://10.147.17.250:8081";

    public static String tokenNou = "";
    public static String privateKeyEncrypt = "";
    public static PrivateKey privateKeyDecrypt;
    public static PublicKey publicKey;
    public static byte[] dataKey;
}
