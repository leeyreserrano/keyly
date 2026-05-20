import { Stack, TextField, MenuItem, Button } from '@mui/material';

export type AdminFilter = string;

type FilterOption = {
  value: AdminFilter;
  label: string;
};

type Props = {
  search: string;
  setSearch: (v: string) => void;

  filter?: AdminFilter;
  setFilter?: (v: AdminFilter) => void;

  filters?: FilterOption[];

  onAdd?: () => void;
  addLabel?: string;
};

export default function AdminToolbar({
  search,
  setSearch,
  filter,
  setFilter,
  filters = [],
  onAdd,
  addLabel = '+ Afegir',
}: Props) {
  return (
    <Stack
      direction={{ xs: 'column', sm: 'row' }}
      sx={{
        px: 4,
        py: 3,
        justifyContent: 'flex-start',
        alignItems: 'center',
        gap: 1,
      }}
    >
      <TextField
        placeholder="Cercar..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        size="small"
        sx={{ width: { xs: '100%', sm: filter ? 900 : '100%' } }}
      />

      {filter !== undefined && setFilter && (
        <TextField
          select
          value={filter}
          onChange={(e) => setFilter(e.target.value)}
          size="small"
          sx={{ width: { xs: '100%', sm: 200 } }}
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
          sx={{
            borderRadius: '8px',
            textTransform: 'none',
            fontWeight: 600,
          }}
        >
          {addLabel}
        </Button>
      )}
    </Stack>
  );
}