// src/components/MainLayout.tsx
import { Stack, CssBaseline } from '@mui/material';
import AppTheme from '../theme/AppTheme';
import Sidebar from './Sidebar';
import type { ReactNode } from 'react';

type MainLayoutProps = {
  children: ReactNode;
};

export default function MainLayout({ children }: MainLayoutProps) {
  return (
    <AppTheme>
      <CssBaseline enableColorScheme />
      <Stack direction="row" sx={{ minHeight: '100vh', width: '100%' }}>
        <Sidebar />
        <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>
          {children}
        </Stack>
      </Stack>
    </AppTheme>
  );
}