import Alert from '@mui/material/Alert';
import AlertTitle from '@mui/material/AlertTitle';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';

interface SecurityAlertProps {
  type: 'compromised' | 'reused';
  count: number;
  onReview: () => void;
}

const ALERT_CONFIG = {
  compromised: {
    severity: 'error' as const,
    title: 'Contraseñas comprometidas detectadas',
    description: (n: number) =>
      `${n} de tus contraseñas coinciden con contraseñas conocidas como inseguras. Cámbialas lo antes posible.`,
    buttonLabel: 'Revisar ahora',
  },
  reused: {
    severity: 'warning' as const,
    title: 'Contraseñas reutilizadas',
    description: (n: number) =>
      `Estás usando la misma contraseña en ${n} cuentas distintas. Usar contraseñas únicas mejora tu seguridad.`,
    buttonLabel: 'Ver duplicadas',
  },
};

export default function SecurityAlert({ type, count, onReview }: SecurityAlertProps) {
  const config = ALERT_CONFIG[type];

  return (
    <Alert
      severity={config.severity}
      sx={{
        borderRadius: 2,
        alignItems: 'flex-start',
        '& .MuiAlert-message': { width: '100%' },
      }}
      action={null}
    >
      <Stack direction="row" justifyContent="space-between" alignItems="flex-start" flexWrap="wrap" gap={1}>
        <Stack>
          <AlertTitle sx={{ fontWeight: 700, mb: 0.25 }}>{config.title}</AlertTitle>
          <span style={{ fontSize: '0.85rem' }}>{config.description(count)}</span>
        </Stack>
        <Button
          variant="outlined"
          size="small"
          color={config.severity}
          onClick={onReview}
          sx={{ textTransform: 'none', fontWeight: 600, whiteSpace: 'nowrap', flexShrink: 0 }}
        >
          {config.buttonLabel}
        </Button>
      </Stack>
    </Alert>
  );
}