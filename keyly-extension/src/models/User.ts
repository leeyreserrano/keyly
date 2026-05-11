export type User = {
  uuid: string
  nom: string
  correu: string
  imatge: string
  kdfSalt: string
  publicKey: string

  getPublicKey: () => Promise<CryptoKey>
}

export async function importPublicKey(publicKey: string): Promise<CryptoKey> {
  let cleaned = publicKey.trim()
  
  while (cleaned.startsWith('"') && cleaned.endsWith('"')) {
    cleaned = cleaned.slice(1, -1)
  }

  const binary = atob(cleaned)
  const bytes = Uint8Array.from(binary, (c) => c.charCodeAt(0))
  return await crypto.subtle.importKey(
    "spki",
    bytes,
    { name: "RSA-OAEP", hash: "SHA-256" },
    false,
    ["encrypt"]
  )
}