import { Stack } from '@mui/material';
import CssBaseline from '@mui/material/CssBaseline';
import type { ReactNode } from 'react';
import AppTheme from '../theme/AppTheme';
import Sidebar from './Sidebar';

type LayoutProps = {
  children: ReactNode;
};

export default function Layout({ children }: LayoutProps) {
  return (
    <AppTheme>
      <CssBaseline enableColorScheme />
      <Stack
        direction="row"
        sx={{
          height: '100vh',
          width: '100%',
          overflow: 'hidden',
        }}
      >
        <Sidebar />

        <Stack
          sx={{
            flex: 1,
            height: '100vh',
            overflow: 'hidden',
            minWidth: 0,
            bgcolor: 'background.default',
          }}
        >
          {children}
        </Stack>
      </Stack>
    </AppTheme>
  );
}