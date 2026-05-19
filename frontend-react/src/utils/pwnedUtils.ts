import { pwnedApi } from '../api/pwnedapi';

export async function isPasswordPwned(plainPassword: string): Promise<boolean> {
  const msgBuffer = new TextEncoder().encode(plainPassword);
  const hashBuffer = await crypto.subtle.digest('SHA-1', msgBuffer);
  const hashArray = Array.from(new Uint8Array(hashBuffer));
  const hashHex = hashArray.map((b) => b.toString(16).padStart(2, '0')).join('').toLowerCase();

  const prefix = hashHex.slice(0, 5);
  const suffix = hashHex.slice(5, 7);
  const fullHash = hashHex;

  const results = await pwnedApi.checkPassword(prefix, suffix);
  if (!results) return false;

  return results.some((entry) => entry.hash.toLowerCase() === fullHash);
}