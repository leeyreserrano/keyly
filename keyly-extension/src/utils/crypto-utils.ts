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