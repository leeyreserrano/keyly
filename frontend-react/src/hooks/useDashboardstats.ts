import { useMemo } from 'react';
import type { Item } from '../api/itemsapi';

export interface DashboardStats {
  totalItems: number;
  pwnedCount: number;
  reusedCount: number;
  weakCount: number;
  secureCount: number;
  avgSecurityScore: number;
  recentItems: Item[];
}

function getPasswordStrengthScore(password: string): number {
  if (!password) return 0;
  let score = 0;
  if (password.length >= 8) score += 20;
  if (password.length >= 12) score += 10;
  if (password.length >= 16) score += 10;
  if (/[a-z]/.test(password)) score += 10;
  if (/[A-Z]/.test(password)) score += 10;
  if (/[0-9]/.test(password)) score += 15;
  if (/[^a-zA-Z0-9]/.test(password)) score += 25;
  return Math.min(score, 100);
}

export function useDashboardStats(
  items: Item[],
  decryptedPasswords: Map<string, string>,
  pwnedUuids: Set<string>
): DashboardStats {
  return useMemo(() => {
    if (!items.length) {
      return {
        totalItems: 0,
        pwnedCount: 0,
        reusedCount: 0,
        weakCount: 0,
        secureCount: 0,
        avgSecurityScore: 0,
        recentItems: [],
      };
    }

    const passwordFrequency: Record<string, number> = {};
    for (const item of items) {
      const plain = decryptedPasswords.get(item.uuid);
      if (plain) {
        passwordFrequency[plain] = (passwordFrequency[plain] || 0) + 1;
      }
    }

    let totalScore = 0;
    let weakCount = 0;
    let secureCount = 0;

    for (const item of items) {
      const plain = decryptedPasswords.get(item.uuid) ?? '';
      const score = getPasswordStrengthScore(plain);
      totalScore += score;
      if (score >= 70) secureCount++;
      else weakCount++;
    }

    const reusedCount = items.filter((item) => {
      const plain = decryptedPasswords.get(item.uuid);
      return plain && passwordFrequency[plain] > 1;
    }).length;

    const avgSecurityScore = Math.round(totalScore / items.length);

    const recentItems = [...items]
      .sort((a, b) => new Date(b.dataEditat).getTime() - new Date(a.dataEditat).getTime())
      .slice(0, 5);

    return {
      totalItems: items.length,
      pwnedCount: pwnedUuids.size,
      reusedCount,
      weakCount,
      secureCount,
      avgSecurityScore,
      recentItems,
    };
  }, [items, decryptedPasswords, pwnedUuids]);
}