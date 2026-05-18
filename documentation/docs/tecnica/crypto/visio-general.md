# Criptografia — Visió general

Keyly implementa un model de xifrat d'extrem a extrem al navegador. El servidor mai rep ni emmagatzema contrasenyes en text pla.

## Algorismes

| Ús | Algorisme |
|---|---|
| Derivació de clau a partir de contrasenya | PBKDF2 + SHA-256, 310.000 iteracions |
| Xifrat de la clau privada | AES-GCM 256 bits |
| Xifrat de la clau de dades (DataKey) | RSA-OAEP 2048 bits, SHA-256 |
| Xifrat de les contrasenyes | AES-GCM 256 bits |

## Model de claus

Cada usuari té un parell de claus RSA generat en el moment del registre. La clau privada es xifra amb una clau derivada de la seva contrasenya mestra (PBKDF2) i s'emmagatzema al servidor en format xifrat. La clau pública es guarda en clar.

Cada ítem té una clau de dades aleatòria (DataKey) de 256 bits. Aquesta DataKey xifra la contrasenya de l'ítem. Quan l'ítem es comparteix amb un altre usuari, la DataKey es xifra amb la clau pública del destinatari.

## Flux de login

```
contrasenya mestra + kdfSalt → PBKDF2 → clau derivada
clau derivada + encryptedPrivateKey → AES-GCM decrypt → clau privada RSA
clau privada RSA → sessionStorage (memòria de sessió)
```

## Flux de desxifrat d'una credencial

```
encryptedDataKey → RSA-OAEP decrypt (clau privada) → DataKey
DataKey + encrypted + iv → AES-GCM decrypt → contrasenya en text pla
```

Tot això passa al navegador. El servidor proporciona les dades xifrades però no té accés a cap clau que permeti desxifrar-les.
