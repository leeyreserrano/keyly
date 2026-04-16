import { useMemo } from 'react';
import type { Item } from '../api/itemsapi';

export interface DashboardStats {
  totalItems: number;
  compromisedCount: number;
  reusedCount: number;
  avgSecurityScore: number;
  secureCount: number;
  weakCount: number;
  recentItems: Item[];
}

function evaluatePasswordStrength(password: string): number {
  if (!password) return 0;
  let score = 0;
  if (password.length >= 12) score += 30;
  else if (password.length >= 8) score += 15;
  if (/[A-Z]/.test(password)) score += 20;
  if (/[0-9]/.test(password)) score += 20;
  if (/[^A-Za-z0-9]/.test(password)) score += 30;
  return Math.min(score, 100);
}

const COMMON_WEAK_PASSWORDS = [
  '123456', 'password', '123456789', 'qwerty', 'abc123',
  '111111', 'password1', 'iloveyou', '1q2w3e4r', 'admin',
];

function isCompromised(password: string): boolean {
  return COMMON_WEAK_PASSWORDS.includes(password.toLowerCase());
}

export function useDashboardStats(items: Item[]): DashboardStats {
  return useMemo(() => {
    if (items.length === 0) {
      return {
        totalItems: 0,
        compromisedCount: 0,
        reusedCount: 0,
        avgSecurityScore: 0,
        secureCount: 0,
        weakCount: 0,
        recentItems: [],
      };
    }

    const passwords = items.map((i) => i.contrasenya).filter(Boolean);
    const passwordFrequency: Record<string, number> = {};
    passwords.forEach((p) => {
      passwordFrequency[p] = (passwordFrequency[p] || 0) + 1;
    });

    const scores = items.map((i) => evaluatePasswordStrength(i.contrasenya));
    const avgScore = Math.round(scores.reduce((a, b) => a + b, 0) / scores.length);

    const compromisedCount = items.filter((i) => isCompromised(i.contrasenya)).length;
    const reusedCount = items.filter((i) => passwordFrequency[i.contrasenya] > 1).length;
    const secureCount = scores.filter((s) => s >= 70).length;
    const weakCount = scores.filter((s) => s < 40).length;

    const recentItems = [...items]
      .sort((a, b) => new Date(b.dataEditat).getTime() - new Date(a.dataEditat).getTime())
      .slice(0, 5);

    return {
      totalItems: items.length,
      compromisedCount,
      reusedCount,
      avgSecurityScore: avgScore,
      secureCount,
      weakCount,
      recentItems,
    };
  }, [items]);
}