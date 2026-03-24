import CssBaseline from '@mui/material/CssBaseline';
import Stack from '@mui/material/Stack';
import AppTheme from '../theme/AppTheme';
import SignInCard from '../components/LoginCard';
import Content from '../components/Content';

function Login(props: { disableCustomTheme?: boolean }) {
  return (
    <AppTheme {...props}>
      <CssBaseline enableColorScheme />
      <Stack
        direction={{ xs: 'column', md: 'row' }}
        sx={{ minHeight: '100vh', width: '100%' }}
      >
        {/* Left branded panel */}
        <Stack
          sx={{
            width: { xs: '100%', md: '50%' },
            minHeight: { xs: '300px', md: '100vh' },
            bgcolor: '#EEE5FF',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          <Content />
        </Stack>

        {/* Right form panel */}
        <Stack
          sx={{
            width: { xs: '100%', md: '50%' },
            minHeight: { xs: 'auto', md: '100vh' },
            bgcolor: 'background.default',
            alignItems: 'center',
            justifyContent: 'center',
            p: { xs: 4, sm: 8 },
          }}
        >
          <SignInCard />
        </Stack>
      </Stack>
    </AppTheme>
  );
}

export default Login;
