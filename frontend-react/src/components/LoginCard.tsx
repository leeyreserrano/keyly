import React from 'react';
import { useNavigate } from 'react-router';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import FormLabel from '@mui/material/FormLabel';
import FormControl from '@mui/material/FormControl';
import FormControlLabel from '@mui/material/FormControlLabel';
import IconButton from '@mui/material/IconButton';
import InputAdornment from '@mui/material/InputAdornment';
import Link from '@mui/material/Link';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import ForgotPassword from './ForgotPassword';
import { loginUser } from '../api/loginapi';

export default function LoginCard() {
  const navigate = useNavigate();
  const [emailError, setEmailError] = React.useState(false);
  const [emailErrorMessage, setEmailErrorMessage] = React.useState('');
  const [passwordError, setPasswordError] = React.useState(false);
  const [passwordErrorMessage, setPasswordErrorMessage] = React.useState('');
  const [showPassword, setShowPassword] = React.useState(false);
  const [open, setOpen] = React.useState(false);
  const [rememberMe, setRememberMe] = React.useState(true);

  const handleClickOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);
  const handleTogglePassword = () => setShowPassword(prev => !prev);

  const validateInputs = () => {
    const email = (document.getElementById('email') as HTMLInputElement).value;
    const password = (document.getElementById('password') as HTMLInputElement).value;
    let isValid = true;

    if (!email || !/\S+@\S+\.\S+/.test(email)) {
      setEmailError(true);
      setEmailErrorMessage('Please enter a valid email address.');
      isValid = false;
    } else {
      setEmailError(false);
      setEmailErrorMessage('');
    }

    if (!password || password.length < 1) {
      setPasswordError(true);
      setPasswordErrorMessage('Password is required.');
      isValid = false;
    } else {
      setPasswordError(false);
      setPasswordErrorMessage('');
    }

    return isValid;
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
  event.preventDefault();
  if (!validateInputs()) return;

  const correu = (document.getElementById('email') as HTMLInputElement).value;
  const contrasenya = (document.getElementById('password') as HTMLInputElement).value;

  try {
    const { token } = await loginUser(correu, contrasenya);
    localStorage.setItem("jwtToken", token); // ✅ guardar token
    navigate("/home");
  } catch (err: any) {
    setPasswordError(true);
    setPasswordErrorMessage(err.message || "Error al iniciar sesión");
  }
};

  return (
    <Stack sx={{ width: '100%', maxWidth: '420px', gap: 3 }}>
      <Typography component="h1" variant="h4" sx={{ fontWeight: 700 }}>
        Login
      </Typography>

      <Box component="form" onSubmit={handleSubmit} noValidate sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>
        <FormControl>
          <FormLabel htmlFor="email" sx={{ fontWeight: 600, mb: 0.5 }}>Email</FormLabel>
          <TextField
            error={emailError}
            helperText={emailErrorMessage}
            id="email"
            type="email"
            autoComplete="email"
            autoFocus
            required
            fullWidth
            variant="outlined"
            color={emailError ? 'error' : 'primary'}
          />
        </FormControl>

        <FormControl>
          <FormLabel htmlFor="password" sx={{ fontWeight: 600, mb: 0.5 }}>Password</FormLabel>
          <TextField
            error={passwordError}
            helperText={passwordErrorMessage}
            id="password"
            type={showPassword ? 'text' : 'password'}
            autoComplete="current-password"
            required
            fullWidth
            variant="outlined"
            color={passwordError ? 'error' : 'primary'}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton onClick={handleTogglePassword} edge="end">
                    {showPassword ? <VisibilityOff /> : <Visibility />}
                  </IconButton>
                </InputAdornment>
              )
            }}
          />
        </FormControl>

        <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'center' }}>
          <FormControlLabel
            control={<Checkbox checked={rememberMe} onChange={e => setRememberMe(e.target.checked)} color="primary" />}
            label="Remember me"
          />
          <Link component="button" onClick={handleClickOpen} variant="body2" color="primary">Forgot Password?</Link>
        </Stack>

        <ForgotPassword open={open} handleClose={handleClose} />

        <Button type="submit" fullWidth variant="contained" size="large" sx={{ py: 1.5, borderRadius: '8px' }}>Login</Button>
      </Box>
    </Stack>
  );
}