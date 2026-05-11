import { useTranslation } from 'react-i18next';
import { Stack, TextField, MenuItem, Button } from '@mui/material';
import type { FilterValue } from '../pages/Items/Items';

type Props = {
  search: string;
  sx?: Record<string, unknown>;
  setSearch: (v: string) => void;
  filter?: FilterValue;
  setFilter?: (v: FilterValue) => void;
  onAdd?: () => void;
};

export default function ItemsToolbar({ search, setSearch, filter, setFilter, onAdd }: Props) {
  const { t } = useTranslation('toolbar');

  const filters: { value: FilterValue; label: string }[] = [
    { value: 'latest', label: t('filter.latest') },
    { value: 'most_used', label: t('filter.most_used') },
    { value: 'favorites', label: t('filter.favorites') },
  ];

  return (
    <Stack
      direction={{ xs: 'column', sm: 'row' }}
      sx={{ px: 4, py: 3, justifyContent: 'flex-start', alignItems: 'center', gap: 1 }}
    >
      <TextField
        placeholder={t('search_placeholder')}
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        size="small"
        sx={{ width: { xs: '100%', sm: setFilter ? 1090 : '100%' } }}
      />

      {filter !== undefined && setFilter && (
        <TextField
          select
          value={filter}
          onChange={(e) => setFilter(e.target.value as FilterValue)}
          size="small"
          sx={{ width: { xs: '100%', sm: 180 } }}
        >
          {filters.map((f) => (
            <MenuItem key={f.value} value={f.value}>
              {f.label}
            </MenuItem>
          ))}
        </TextField>
      )}

      {onAdd && (
        <Button
          variant="contained"
          onClick={onAdd}
          sx={{ borderRadius: '8px', textTransform: 'none', fontWeight: 600 }}
        >
          {t('add_new')}
        </Button>
      )}
    </Stack>
  );
}