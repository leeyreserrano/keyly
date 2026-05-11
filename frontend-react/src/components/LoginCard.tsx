import React from 'react';
import { useNavigate } from 'react-router';
import { useTranslation } from 'react-i18next';
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
import { useAuth } from '../context/AuthContext';
import { useCrypto } from '../context/CryptoContext';
import { deriveKey, decryptPrivateKey, importPrivateKey, importPublicKey } from '../crypto/cryptoService';

export default function LoginCard() {
  const navigate = useNavigate();
  const { t } = useTranslation('auth');
  const { login } = useAuth();
  const { setPrivateKey, setPublicKey } = useCrypto();

  const [emailError, setEmailError] = React.useState(false);
  const [emailErrorMessage, setEmailErrorMessage] = React.useState('');
  const [passwordError, setPasswordError] = React.useState(false);
  const [passwordErrorMessage, setPasswordErrorMessage] = React.useState('');
  const [showPassword, setShowPassword] = React.useState(false);
  const [open, setOpen] = React.useState(false);
  const [rememberMe, setRememberMe] = React.useState(true);
  const [loading, setLoading] = React.useState(false);

  const handleClickOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);
  const handleTogglePassword = () => setShowPassword((prev) => !prev);

  const validateInputs = () => {
    const email = (document.getElementById('email') as HTMLInputElement).value;
    const password = (document.getElementById('password') as HTMLInputElement).value;
    let isValid = true;

    if (!email || !/\S+@\S+\.\S+/.test(email)) {
      setEmailError(true);
      setEmailErrorMessage(t('validation.email_required'));
      isValid = false;
    } else {
      setEmailError(false);
      setEmailErrorMessage('');
    }

    if (!password) {
      setPasswordError(true);
      setPasswordErrorMessage(t('validation.password_required'));
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
      setLoading(true);

      const { token, usuari, kdfSalt, encryptedPrivateKey, publicKeyB64 } =
        await loginUser(correu, contrasenya, rememberMe);

      const derivedKeyB64 = await deriveKey(contrasenya, kdfSalt);
      const privateKeyB64 = await decryptPrivateKey(encryptedPrivateKey, derivedKeyB64);
      const privateKey = await importPrivateKey(privateKeyB64);
      setPrivateKey(privateKey);
      sessionStorage.setItem('privateKey', privateKeyB64);

      if (publicKeyB64) {
        const publicKey = await importPublicKey(publicKeyB64);
        setPublicKey(publicKey);
        sessionStorage.setItem('publicKey', publicKeyB64);
      }

      login(usuari, token, rememberMe);
      navigate('/home');
    } catch (err: any) {
      setPasswordError(true);
      setPasswordErrorMessage(err.message || t('error.login'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Stack sx={{ width: '100%', maxWidth: '420px', gap: 3 }}>
      <Typography component="h1" variant="h4" sx={{ fontWeight: 700 }}>
        {t('login.title')}
      </Typography>

      <Box
        component="form"
        onSubmit={handleSubmit}
        noValidate
        sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}
      >
        <FormControl>
          <FormLabel>{t('field.email')}</FormLabel>
          <TextField
            error={emailError}
            helperText={emailErrorMessage}
            id="email"
            type="email"
            required
            fullWidth
          />
        </FormControl>

        <FormControl>
          <FormLabel>{t('field.password')}</FormLabel>
          <TextField
            error={passwordError}
            helperText={passwordErrorMessage}
            id="password"
            type={showPassword ? 'text' : 'password'}
            required
            fullWidth
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton onClick={handleTogglePassword}>
                    {showPassword ? <VisibilityOff /> : <Visibility />}
                  </IconButton>
                </InputAdornment>
              ),
            }}
          />
        </FormControl>

        <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'center' }}>
          <FormControlLabel
            control={
              <Checkbox checked={rememberMe} onChange={(e) => setRememberMe(e.target.checked)} />
            }
            label={t('remember_me')}
          />
          <Link component="button" onClick={handleClickOpen}>
            {t('forgot_password')}
          </Link>
        </Stack>

        <ForgotPassword open={open} handleClose={handleClose} />

        <Button
          type="submit"
          fullWidth
          variant="contained"
          size="large"
          disabled={loading}
          sx={{ py: 1.5, borderRadius: '8px' }}
        >
          {loading ? t('button.loading') : t('button.login')}
        </Button>
      </Box>
    </Stack>
  );
}