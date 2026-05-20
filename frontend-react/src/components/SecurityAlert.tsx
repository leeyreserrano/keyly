import { useTranslation } from 'react-i18next';
import Alert from '@mui/material/Alert';
import AlertTitle from '@mui/material/AlertTitle';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';

interface SecurityAlertProps {
  type: 'compromised' | 'reused';
  count: number;
  onReview: () => void;
}

export default function SecurityAlert({ type, count, onReview }: SecurityAlertProps) {
  const { t } = useTranslation('stats');

  const config = {
    compromised: {
      severity: 'error' as const,
      title: t('alerts.compromised.title'),
      description: t('alerts.compromised.description', { count }),
      buttonLabel: t('alerts.compromised.button'),
    },
    reused: {
      severity: 'warning' as const,
      title: t('alerts.reused.title'),
      description: t('alerts.reused.description', { count }),
      buttonLabel: t('alerts.reused.button'),
    },
  }[type];

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
          <span style={{ fontSize: '0.85rem' }}>{config.description}</span>
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