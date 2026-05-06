function bytesToBase64(bytes) {
  return btoa(String.fromCharCode(...new Uint8Array(bytes)))
}

export async function encryptItemPassword(
  contrasenya: string,
  rawDataKey: ArrayBuffer
): Promise<{ contrasenyaEncriptada: string; ivB64: string }> {
  const iv = crypto.getRandomValues(new Uint8Array(12))
  const dataKey = await crypto.subtle.importKey(
    "raw",
    rawDataKey,
    { name: "AES-GCM" },
    false,
    ["encrypt"]
  )
  const encrypted = await crypto.subtle.encrypt(
    { name: "AES-GCM", iv },
    dataKey,
    new TextEncoder().encode(contrasenya)
  )
  return {
    contrasenyaEncriptada: bytesToBase64(encrypted),
    ivB64: bytesToBase64(iv)
  }
}

const EXPIRATION_MARGIN = 60 * 1000;

export function isTokenExpired(token: string): boolean {
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));

    if (!payload.exp) return true;

    return Date.now() >= payload.exp * 1000 - EXPIRATION_MARGIN;
  } catch {
    return true;
  }
}