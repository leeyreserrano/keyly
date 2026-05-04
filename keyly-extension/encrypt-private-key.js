/**
 * Script para encriptar la private key con el KDF del usuario
 * Necesitas: contraseña, salt (mismo que el KDF), y la private key
 */

const { webcrypto } = require("crypto")
const crypto = require("crypto")

async function deriveKey(password, kdfSalt) {
  const encoder = new TextEncoder()
  const saltBytes = Buffer.from(kdfSalt, "base64")
  const passwordBytes = encoder.encode(password)

  const derivedKey = await webcrypto.subtle.deriveKey(
    {
      name: "PBKDF2",
      salt: saltBytes,
      iterations: 100000,
      hash: "SHA-256"
    },
    await webcrypto.subtle.importKey("raw", passwordBytes, "PBKDF2", false, [
      "deriveKey"
    ]),
    { name: "AES-GCM", length: 256 },
    true,
    ["encrypt", "decrypt"]
  )

  return derivedKey
}

async function encryptPrivateKey(privateKeyBase64, password, kdfSalt) {
  const derivedKey = await deriveKey(password, kdfSalt)

  const iv = webcrypto.getRandomValues(new Uint8Array(12))
  const privateKeyBytes = Buffer.from(privateKeyBase64, "base64")

  const encryptedData = await webcrypto.subtle.encrypt(
    { name: "AES-GCM", iv },
    derivedKey,
    privateKeyBytes
  )

  const ivBase64 = Buffer.from(iv).toString("base64")
  const encryptedBase64 = Buffer.from(encryptedData).toString("base64")

  return `${ivBase64}:${encryptedBase64}`
}

// REEMPLAZA ESTOS VALORES
const PASSWORD = "1234"
const KDF_SALT = "|l­ 5 â ºÍâ¥  ­!Põ ´«7V)ÿ%I6   @" // El mismo salt que usas en login
const PRIVATE_KEY_BASE64 =
"MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDSV9cOsQME1UBcHCy/vZL4AOb9v7bUGxD3njLiBzTELRyzD7HAwrytjc1zPbLhYO6SYqdX65lUAvdw/DMOCL53k/nNLSaLo3vXGXmr3ltHdfAdQy6GOcHYctwdXnv3u/Hu5lCfw75W/VXAbhqH7R9NOf55Y4Ua9pDdpWJwkj+gguVhrDjisDB0mOmoCZGA6NnFZquZr75eK/pi/h82P7yZ8W7R45QhVOq6Y6H3a9uxYpCs1J5/SFR6w3vIoMfor1OwIn1+Ljlucuo0nhVpXeb6a5ubJs2NerEl22FSgs66OpeoV++YvmEElwo3p8VyO0OWzIDUIkoTozJT+i+QaeXPAgMBAAECggEAECsDU1RE3KK9qzbZM7XRLbH4D2oklgIsmDP3SktugWPMUHE04xG06gw7zg9giBVo0SS3IzlvVDdUKMGh9QK0DUH5Eg3V6CbdVofVQ+I4FvKYTc+DrHqVDVuq0rEYC09NheT7LoSfxVT8uelcrbUK0mdSSadeLFfmg7r6KZf8PwydAU8/pJ2qfPj1PSZxPBS/T8CoSdv6BJs7BOWgoNVb031nR5GzlD2feNGeB7uGy9NANJtFkX3h5bzEaB+g1CR06rL1xRtUZxpSbfsJkczeYIwSvpi1mqZZyAIvgvLq74jHAbpm0e41eSsHe7fNQgbdTx8I/TX4D4WAoVf/oWZuRQKBgQDtUBYLiyXlUE0Q1wICKBtbMt9p+fg9YW1hB60twnEJJuHFaaVDRAqFA6AbjZiTCCg3L+LVYGlz73MRdZlwSKcz8/kdWE2P7oUhAhOOZzUUf65h9swY/K4oR3qnd2NoQSyQ/yh5V3XPu8D+a80NCDJEdWSb/Vm4Fts4b8qECAYqfQKBgQDi6BSD5LMFATVmUO5vlXs5YevzqrskxbHwU6KVF1FZIGsFNn4jB1u9vDJk4Wy0Wo7VmE/EJwEO9gz/LaZOXMIrNk+eS8iMTsaNV8SCV2LFpuc8zz9uSlDE0RCia+9BWiVmjkgOScTMkirn59dRFS/Unwm4kDK+EsHRwJTABJZ3OwKBgQCvWIVxchdGINdQog1x2oCcGjLJ81FgySQwlaN4NDuhL4GShEUANr4vGkarFSyvN0+/tzo7v0kryLmVJPeC56vjz1k6PYzR1MO5Z/dbORcXVmBbMigMLmfgizAtnSFuXvm3CVG2ltEN9QTc+HpCLtQ1kSgNul0YkIraYH+anvMvYQKBgQCxHLwes131ExtucM+cc46KnN+DBAotys54q5+lRl0t93rxx7Yve2flsseXLMogAOuq18ezcUqHNhGxAR3l3suE1VDSW47/zeF3pEF0B+fGJeAq/A4j9YgUCKg2T4+icE3vPjIKTRR7JcCt4a6U0LfT/Aunb47cBGIbannxY+NazwKBgBZzUDcjqYagfJEq02Povsm98jsWkI/nBE3140+rp4clOhNCrwnjTubCxBnbEif8/Ly4OiXyiMFoS8of4Tz8LfOikFFqG+PdIZcZ3sQUR+ZAEJaqBw+voahoxg4KsUkpY5f9IQV3xdJYe4/asJEkGVRbG8+M3hXK7I61CLPpDQ+W"
async function main() {
  if (PASSWORD === "tu_contraseña_aqui" || KDF_SALT === "tu_salt_base64_aqui") {
    console.error(
      "ERROR: Debes reemplazar PASSWORD y KDF_SALT con valores reales"
    )
    console.error("PASSWORD: Tu contraseña de usuario")
    console.error("KDF_SALT: El salt que ves en la BD (base64)")
    process.exit(1)
  }

  try {
    const encryptedPrivateKey = await encryptPrivateKey(
      PRIVATE_KEY_BASE64,
      PASSWORD,
      KDF_SALT
    )

    console.log("\n=== ENCRYPTED PRIVATE KEY ===")
    console.log("Para guardar en la BD como 'encryptedPrivateKey':")
    console.log(encryptedPrivateKey)

    console.log("\n=== DATOS PARA ACTUALIZAR EN LA BD ===")
    console.log(
      JSON.stringify(
        {
          publicKey:
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5CVhBtXIrOg4lM9qLVfpW75B9WGFneSNQ0oG/EOul/kPwQbBym+ByWsn+vQGTP3vRC18neGbDVWXdYh8/YJnEeEvlinkM7ipIlfYV5PIzSXPfx6ZDe+NQDEveviVkSC6UCUafRWyphiXUuZZHSXH5C5lIQhuwh8UHrfZbXMNIpMZsqS11byutt/JdvIpxtYFQsqGA7cxrrCZKgDWsXQX4xN0YwIWpGv9FpDyVf1kh3BzgTmkt76/1M+29fgAT7hSsPA+Fu7tgHB3XRzz2M7yVFHS8XYm7mqJLpZR5QIJUdegQ8aa+zrJgNB/cy69s6MZ/jkY2VRtvF3CrIv7Y5uzjQIDAQAB",
          encryptedPrivateKey: encryptedPrivateKey
        },
        null,
        2
      )
    )
  } catch (err) {
    console.error("Error:", err.message)
  }
}

main()
