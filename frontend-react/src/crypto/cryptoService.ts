// ─── Helpers base64 ──────────────────────────────────────────────────────────

export function bytesToBase64(bytes: Uint8Array): string {
  return btoa(String.fromCharCode(...bytes));
}

export function base64ToBytes(b64: string): Uint8Array {
  return Uint8Array.from(atob(b64), (c) => c.charCodeAt(0));
}

// Garantiza ArrayBuffer limpio (nunca SharedArrayBuffer) para Web Crypto
function buf(bytes: Uint8Array): Uint8Array<ArrayBuffer> {
  const clean = new ArrayBuffer(bytes.length);
  new Uint8Array(clean).set(bytes);
  return new Uint8Array(clean);
}

// ─── KDF ─────────────────────────────────────────────────────────────────────

export async function deriveKey(password: string, saltB64: string): Promise<string> {
  const enc = new TextEncoder();
  const saltBytes = buf(base64ToBytes(saltB64));

  const keyMaterial = await crypto.subtle.importKey(
    'raw',
    enc.encode(password),
    'PBKDF2',
    false,
    ['deriveBits']
  );

  const derivedBits = await crypto.subtle.deriveBits(
    {
      name: 'PBKDF2',
      salt: saltBytes,
      iterations: 310_000,
      hash: 'SHA-256',
    },
    keyMaterial,
    256
  );

  return bytesToBase64(new Uint8Array(derivedBits));
}

// ─── Importar derived key bytes como CryptoKey AES-GCM ───────────────────────

async function derivedKeyBytesToCryptoKey(
  derivedKeyB64: string,
  usage: KeyUsage[]
): Promise<CryptoKey> {
  return crypto.subtle.importKey('raw', buf(base64ToBytes(derivedKeyB64)), { name: 'AES-GCM' }, false, usage);
}

// ─── Descifrar private key (formato "ivB64:ciphertextB64") ───────────────────

export async function decryptPrivateKey(
  encryptedPrivateKey: string,
  derivedKeyB64: string
): Promise<string> {
  const [ivB64, ciphertextB64] = encryptedPrivateKey.split(':');
  const iv = buf(base64ToBytes(ivB64));
  const ciphertext = buf(base64ToBytes(ciphertextB64));

  const key = await derivedKeyBytesToCryptoKey(derivedKeyB64, ['decrypt']);

  const decryptedBuffer = await crypto.subtle.decrypt(
    { name: 'AES-GCM', iv },
    key,
    ciphertext
  );

  return bytesToBase64(new Uint8Array(decryptedBuffer));
}

// ─── Cifrar private key (registro y cambio de master) ────────────────────────

export async function encryptPrivateKey(
  privateKeyB64: string,
  derivedKeyB64: string
): Promise<string> {
  const privateKeyBytes = buf(base64ToBytes(privateKeyB64));
  const iv = buf(crypto.getRandomValues(new Uint8Array(12)));
  const key = await derivedKeyBytesToCryptoKey(derivedKeyB64, ['encrypt']);

  const encrypted = await crypto.subtle.encrypt(
    { name: 'AES-GCM', iv },
    key,
    privateKeyBytes
  );

  return `${bytesToBase64(iv)}:${bytesToBase64(new Uint8Array(encrypted))}`;
}

// ─── Par de claves RSA-OAEP ──────────────────────────────────────────────────

export async function generateKeyPair(): Promise<{
  publicKeyB64: string;
  privateKeyB64: string;
}> {
  const keyPair = await crypto.subtle.generateKey(
    {
      name: 'RSA-OAEP',
      modulusLength: 2048,
      publicExponent: new Uint8Array([1, 0, 1]),
      hash: 'SHA-256',
    },
    true,
    ['encrypt', 'decrypt']
  );

  const publicKeyDer = await crypto.subtle.exportKey('spki', keyPair.publicKey);
  const privateKeyDer = await crypto.subtle.exportKey('pkcs8', keyPair.privateKey);

  return {
    publicKeyB64: bytesToBase64(new Uint8Array(publicKeyDer)),
    privateKeyB64: bytesToBase64(new Uint8Array(privateKeyDer)),
  };
}

export async function importPublicKey(publicKeyB64: string): Promise<CryptoKey> {
  return crypto.subtle.importKey(
    'spki',
    buf(base64ToBytes(publicKeyB64)),
    { name: 'RSA-OAEP', hash: 'SHA-256' },
    false,
    ['encrypt']
  );
}

export async function importPrivateKey(privateKeyB64: string): Promise<CryptoKey> {
  return crypto.subtle.importKey(
    'pkcs8',
    buf(base64ToBytes(privateKeyB64)),
    { name: 'RSA-OAEP', hash: 'SHA-256' },
    false,
    ['decrypt']
  );
}

// ─── RSA-OAEP ─────────────────────────────────────────────────────────────────

export async function rsaEncrypt(publicKey: CryptoKey, data: Uint8Array): Promise<string> {
  const encrypted = await crypto.subtle.encrypt({ name: 'RSA-OAEP' }, publicKey, buf(data));
  return bytesToBase64(new Uint8Array(encrypted));
}

export async function rsaDecrypt(privateKey: CryptoKey, encryptedB64: string): Promise<Uint8Array> {
  const decrypted = await crypto.subtle.decrypt(
    { name: 'RSA-OAEP' },
    privateKey,
    buf(base64ToBytes(encryptedB64))
  );
  return new Uint8Array(decrypted);
}

// ─── DataKey AES-GCM ──────────────────────────────────────────────────────────

export function generateDataKey(): Uint8Array {
  return crypto.getRandomValues(new Uint8Array(32));
}

async function dataKeyToCryptoKey(dataKeyBytes: Uint8Array, usage: KeyUsage[]): Promise<CryptoKey> {
  return crypto.subtle.importKey('raw', buf(dataKeyBytes), { name: 'AES-GCM' }, false, usage);
}

export async function encryptPasswordWithDataKey(
  dataKeyBytes: Uint8Array,
  plainPassword: string
): Promise<{ encrypted: string; iv: string }> {
  const enc = new TextEncoder();
  const iv = buf(crypto.getRandomValues(new Uint8Array(12)));
  const key = await dataKeyToCryptoKey(dataKeyBytes, ['encrypt']);

  const encrypted = await crypto.subtle.encrypt({ name: 'AES-GCM', iv }, key, enc.encode(plainPassword));

  return {
    encrypted: bytesToBase64(new Uint8Array(encrypted)),
    iv: bytesToBase64(iv),
  };
}

export async function decryptPasswordWithDataKey(
  dataKeyBytes: Uint8Array,
  encryptedB64: string,
  ivB64: string
): Promise<string> {
  const iv = buf(base64ToBytes(ivB64));
  const ciphertext = buf(base64ToBytes(encryptedB64));
  const key = await dataKeyToCryptoKey(dataKeyBytes, ['decrypt']);

  const decrypted = await crypto.subtle.decrypt({ name: 'AES-GCM', iv }, key, ciphertext);
  return new TextDecoder().decode(decrypted);
}