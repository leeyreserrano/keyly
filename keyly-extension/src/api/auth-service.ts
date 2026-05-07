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

    const privateKey = await decryptPrivateKey(encryptedPrivateKey, derivedKey)

    localStorage.setItem("jwtToken", token)
    localStorage.setItem("publicKey", usuari.publicKey)
    localStorage.setItem("privateKey", privateKey)

    return { user: usuari, token }
  } catch (err: any) {
    throw new Error(err.message || "Error de conexión")
  }
}

async function decryptPrivateKey(
  encryptedPrivateKey: string,
  derivedKeyB64: string
): Promise<string> {
  try {
    const [ivB64, ciphertextB64] = encryptedPrivateKey.split(":")

    const iv = Uint8Array.from(atob(ivB64), (c) => c.charCodeAt(0))

    const ciphertext = Uint8Array.from(atob(ciphertextB64), (c) =>
      c.charCodeAt(0)
    )
    const derivedKeyBytes = Uint8Array.from(atob(derivedKeyB64), (c) =>
      c.charCodeAt(0)
    )

    const key = await crypto.subtle.importKey(
      "raw",
      derivedKeyBytes,
      { name: "AES-GCM" },
      false,
      ["decrypt"]
    )

    const decryptedBuffer = await crypto.subtle.decrypt(
      { name: "AES-GCM", iv },
      key,
      ciphertext
    )

    return btoa(String.fromCharCode(...new Uint8Array(decryptedBuffer)))
  } catch (err) {
    console.error(err)
    throw new Error("Error al desencriptar la clave privada")
  }
}

export async function getPublicKey(): Promise<CryptoKey> {
  const token =
    localStorage.getItem("jwtToken") || sessionStorage.getItem("jwtToken")

  if (!token) throw new Error("No autenticado")

  const publicKeyString = localStorage.getItem("publicKey")
  if (!publicKeyString) throw new Error("No public key")

  const binary = atob(publicKeyString)
  const bytes = Uint8Array.from(binary, (c) => c.charCodeAt(0))

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

async function deriveKey(password: string, saltB64: string) {
  const enc = new TextEncoder()

  const saltBytes = Uint8Array.from(atob(saltB64), (c) => c.charCodeAt(0))

  const keyMaterial = await crypto.subtle.importKey(
    "raw",
    enc.encode(password),
    "PBKDF2",
    false,
    ["deriveBits"]
  )

  const derivedBits = await crypto.subtle.deriveBits(
    {
      name: "PBKDF2",
      salt: saltBytes,
      iterations: 310000,
      hash: "SHA-256"
    },
    keyMaterial,
    256
  )

  const bytes = new Uint8Array(derivedBits)
  return btoa(String.fromCharCode(...bytes))
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
