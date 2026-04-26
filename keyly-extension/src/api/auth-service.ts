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
    const { token, user, kdfSalt } = data

    const derivedKey = await deriveKey(contrasenya, kdfSalt)

    localStorage.setItem("jwtToken", token)
    localStorage.setItem("derivedKey", derivedKey)

    console.log("KDF " + kdfSalt)
    console.log("DERIVED " + localStorage.getItem("derivedKey"))

    return { user, token }
  } catch (err: any) {
    throw new Error(err.message || "Error de conexión")
  }
}

async function deriveKey(password: string, salt: string) {
  const enc = new TextEncoder()

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
      salt: enc.encode(salt),
      iterations: 100000,
      hash: "SHA-256"
    },
    keyMaterial,
    256
  )

  return Array.from(new Uint8Array(derivedBits))
    .map((b) => b.toString(16).padStart(2, "0"))
    .join("")
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
