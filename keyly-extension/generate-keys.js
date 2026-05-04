/**
 * Script para generar un par de claves RSA válidas
 * Ejecutar: node generate-keys.js
 */

const { webcrypto } = require("crypto")

async function generateAndExportKeys() {
  // Generar par de claves RSA
  const keyPair = await webcrypto.subtle.generateKey(
    {
      name: "RSA-OAEP",
      modulusLength: 2048,
      publicExponent: new Uint8Array([1, 0, 1]), // 65537
      hash: "SHA-256"
    },
    true, // extractable
    ["encrypt", "decrypt"]
  )

  // Exportar public key en formato SPKI (SubjectPublicKeyInfo)
  const publicKeySpki = await webcrypto.subtle.exportKey("spki", keyPair.publicKey)
  const publicKeyBase64 = Buffer.from(publicKeySpki).toString("base64")

  // Exportar private key en formato PKCS8
  const privateKeyPkcs8 = await webcrypto.subtle.exportKey("pkcs8", keyPair.privateKey)
  const privateKeyBase64 = Buffer.from(privateKeyPkcs8).toString("base64")

  console.log("=== PUBLIC KEY (SPKI Format - Base64) ===")
  console.log("Para guardar en la BD:")
  console.log(publicKeyBase64)
  console.log()
  console.log("=== PRIVATE KEY (PKCS8 Format - Base64) ===")
  console.log("Para encriptar y guardar en la BD:")
  console.log(privateKeyBase64)
  console.log()
  console.log("=== JSON Export ===")
  console.log(JSON.stringify({
    publicKey: publicKeyBase64,
    privateKey: privateKeyBase64
  }, null, 2))
}

generateAndExportKeys().catch(console.error)
