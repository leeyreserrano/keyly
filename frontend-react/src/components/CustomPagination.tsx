// src/components/CustomPagination.tsx
import { Pagination, Stack } from '@mui/material';

type Props = {
  count: number;
  page: number;
  onChange: (page: number) => void;
};

export default function CustomPagination({ count, page, onChange }: Props) {
  return (
    <Stack sx={{ px: 4, pb: 4, alignItems: 'flex-end' }}>
      <Pagination
        count={count}
        page={page}
        onChange={(_, val) => onChange(val)}
        color="primary"
        siblingCount={1}
        boundaryCount={2}
        sx={{ '& .MuiPaginationItem-root': { borderRadius: '8px' } }}
      />
    </Stack>
  );
}