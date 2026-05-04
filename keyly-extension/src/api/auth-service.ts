const API_BASE = "https://10.147.17.250:8081/api"

export async function loginUser(correu: string, contrasenya: string) {
  try {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ correu, contrasenya })
    })

    if (!res.ok) {
      const errorData = await res.json()
      throw new Error(errorData.message || "Error en login")
    }

    const data = await res.json()
    const { token, usuari, kdfSalt, encryptedPrivateKey } = data

    const derivedKey = await deriveKey(contrasenya, kdfSalt)
    const decryptedPrivateKey = await decryptPrivateKey(
      encryptedPrivateKey,
      derivedKey
    )

    localStorage.setItem("jwtToken", token)
    localStorage.setItem("publicKey", usuari.publicKey)
    localStorage.setItem("privateKey", decryptedPrivateKey)

    return { usuari, token }
  } catch (err: any) {
    console.error("Login error:", err)
    throw new Error(err.message || "Error de conexión")
  }
}

export async function getPublicKey(): Promise<CryptoKey> {
  const publicKeyString = localStorage.getItem("publicKey")

  if (!publicKeyString) throw new Error("No public key")

  const binary = atob(publicKeyString)
  const bytes = Uint8Array.from(binary, c => c.charCodeAt(0))

  return await crypto.subtle.importKey(
    "spki",
    bytes,
    {
      name: "RSA-OAEP",
      hash: "SHA-256"
    },
    false,
    ["encrypt"]
  )
}

export async function getStoredKey() {
  const keyBase64 = localStorage.getItem("derivedKey")
  if (!keyBase64) throw new Error("No key found")

  const binary = atob(keyBase64)
  const bytes = Uint8Array.from(binary, (c) => c.charCodeAt(0))

  return await crypto.subtle.importKey(
    "raw",
    bytes,
    { name: "AES-GCM" },
    false,
    ["encrypt", "decrypt"]
  )
}

async function decryptPrivateKey(
  encryptedPrivateKey: string,
  derivedKey: CryptoKey
) {
  try {
    const [ivBase64, dataBase64] = encryptedPrivateKey.split(":")

    if (!ivBase64 || !dataBase64) {
      throw new Error("Formato inválido de encryptedPrivateKey")
    }
    const iv = Uint8Array.from(atob(ivBase64), (c) => c.charCodeAt(0))
    const encryptedData = Uint8Array.from(atob(dataBase64), (c) =>
      c.charCodeAt(0)
    )

    const decrypted = await crypto.subtle.decrypt(
      {
        name: "AES-GCM",
        iv: iv
      },
      derivedKey,
      encryptedData
    )

    const decoder = new TextDecoder()
    return decoder.decode(decrypted)
  } catch (err) {
    throw new Error("Error al desencriptar la private key")
  }
}

async function deriveKey(password: string, salt: string): Promise<CryptoKey> {
  const enc = new TextEncoder()

  const keyMaterial = await crypto.subtle.importKey(
    "raw",
    enc.encode(password),
    { name: "PBKDF2" },
    false,
    ["deriveKey"]
  )

  const derivedKey = await crypto.subtle.deriveKey(
    {
      name: "PBKDF2",
      salt: enc.encode(salt),
      iterations: 100000,
      hash: "SHA-256"
    },
    keyMaterial,
    {
      name: "AES-GCM",
      length: 256
    },
    false,
    ["encrypt", "decrypt"]
  )

  return derivedKey
}

export function getCurrentUser() {
  const token =
    localStorage.getItem("jwtToken") || sessionStorage.getItem("jwtToken")
  if (!token) return null
  return { token }
}

export function logout() {
  localStorage.removeItem("jwtToken")
  sessionStorage.removeItem("jwtToken")
}

export async function apiGet(endpoint: string) {
  const token =
    localStorage.getItem("jwtToken") || sessionStorage.getItem("jwtToken")
  if (!token) throw new Error("No autenticado")

  const res = await fetch(`${API_BASE}${endpoint}`, {
    headers: { Authorization: `Bearer ${token}` }
  })

  if (!res.ok) throw new Error("Error en la petición GET")
  return await res.json()
}

export default loginUser
