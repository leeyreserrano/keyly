import { apiRequest } from './client';

export interface PwnedHash {
  hash: string;
  count: string;
}

export const pwnedApi = {
  checkPassword: (prefix: string, suffix: string): Promise<PwnedHash[] | null> =>
    apiRequest<PwnedHash[]>(`/utils/pwned/password/${prefix}/${suffix}`, {
      method: 'GET',
    }),
};