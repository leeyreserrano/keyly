const PBKDF2_ITERATIONS = 100_000;
const PBKDF2_HASH = 'SHA-256';
const KEY_LENGTH = 256;

export async function deriveKey(password: string, saltB64: string): Promise<CryptoKey> {
  const enc = new TextEncoder();
  const salt = base64ToBytes(saltB64).buffer as ArrayBuffer;

  const keyMaterial = await crypto.subtle.importKey(
    'raw',
    enc.encode(password),
    'PBKDF2',
    false,
    ['deriveKey']
  );

  return crypto.subtle.deriveKey(
    {
      name: 'PBKDF2',
      salt,
      iterations: PBKDF2_ITERATIONS,
      hash: PBKDF2_HASH,
    },
    keyMaterial,
    { name: 'AES-GCM', length: KEY_LENGTH },
    false,
    ['encrypt', 'decrypt']
  );
}

export async function encryptPassword(
  masterKey: CryptoKey,
  plainPassword: string
): Promise<{ encrypted: string; iv: string }> {
  const enc = new TextEncoder();
  const iv = crypto.getRandomValues(new Uint8Array(12));

  const encrypted = await crypto.subtle.encrypt(
    { name: 'AES-GCM', iv },
    masterKey,
    enc.encode(plainPassword)
  );

  return {
    encrypted: bytesToBase64(new Uint8Array(encrypted)),
    iv: bytesToBase64(iv),
  };
}

export async function decryptPassword(
  masterKey: CryptoKey,
  encryptedB64: string,
  ivB64: string
): Promise<string> {
  const iv = base64ToBytes(ivB64).buffer as ArrayBuffer;
  const encryptedBytes = base64ToBytes(encryptedB64).buffer as ArrayBuffer;

  const decrypted = await crypto.subtle.decrypt(
    { name: 'AES-GCM', iv },
    masterKey,
    encryptedBytes
  );

  return new TextDecoder().decode(decrypted);
}

function base64ToBytes(b64: string): Uint8Array {
  const binary = atob(b64);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) {
    bytes[i] = binary.charCodeAt(i);
  }
  return bytes;
}

function bytesToBase64(bytes: Uint8Array): string {
  let binary = '';
  for (let i = 0; i < bytes.length; i++) {
    binary += String.fromCharCode(bytes[i]);
  }
  return btoa(binary);
}