import Box from '@mui/material/Box';
import Stack from '@mui/material/Stack';
import { KeylyLogo } from './CustomIcons'; // tu componente SVG

export default function Content() {
  return (
    <Stack
      sx={{
        flexDirection: 'column',
        alignSelf: 'center',
        maxWidth: 450,
        alignItems: 'center', // centra horizontalmente
      }}
    >
      <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2, maxWidth: 250, maxHeight: 250 }}>
        {/* Logo de Keyly */}
        <KeylyLogo
          sx={{
            width: 400, // ancho del logo
            height: 400, // alto del logo
            color: 'primary.main', 
          }}
        />
      </Box>

      <Box>
        <h1>Benvinguda a Keyly!</h1>
        <p>Accede al teu compte per continuar</p>
      </Box>
    </Stack>
  );
}