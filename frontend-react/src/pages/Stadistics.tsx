import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import CssBaseline from '@mui/material/CssBaseline';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Avatar from '@mui/material/Avatar';
import Paper from '@mui/material/Paper';
import Skeleton from '@mui/material/Skeleton';
import Tooltip from '@mui/material/Tooltip';
import LinearProgress from '@mui/material/LinearProgress';
import Chip from '@mui/material/Chip';
import Divider from '@mui/material/Divider';
import BarChartRoundedIcon from '@mui/icons-material/BarChartRounded';
import ShieldOutlinedIcon from '@mui/icons-material/ShieldOutlined';
import WarningAmberRoundedIcon from '@mui/icons-material/WarningAmberRounded';
import RepeatRoundedIcon from '@mui/icons-material/RepeatRounded';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import TrendingUpRoundedIcon from '@mui/icons-material/TrendingUpRounded';
import AccessTimeRoundedIcon from '@mui/icons-material/AccessTimeRounded';
import OpenInNewRoundedIcon from '@mui/icons-material/OpenInNewRounded';
import {
    PieChart,
    Pie,
    Cell,
    Tooltip as ReTooltip,
    Legend,
    ResponsiveContainer,
} from 'recharts';
import AppTheme from '../theme/AppTheme';
import Sidebar from '../components/Sidebar';
import SecurityAlert from '../components/SecurityAlert';
import { useDashboardStats } from '../hooks/useDashboardstats';
import { itemsApi } from '../api/itemsapi';
import type { Item } from '../api/itemsapi';
import Header from '../components/Header';
import { getTimeAgo } from '../utils/timeUtils';
import { useTimeRefresh } from '../components/UseTimeRefresh';

const LAVENDER = '#EEE5FF';

const CHART_COLORS = {
    secure: '#7F77DD',
    weak: '#7E0FC2',
    compromised: '#E24B4A',
};

function getScoreColor(score: number): string {
    if (score >= 70) return '#1D9E75';
    if (score >= 40) return '#7E0FC2';
    return '#E24B4A';
}

function getScoreLabel(score: number): string {
    if (score >= 70) return 'Buena';
    if (score >= 40) return 'Regular';
    return 'Débil';
}

interface StatCardProps {
    icon: React.ReactNode;
    label: string;
    value: string | number;
    color?: string;
    loading?: boolean;
    tooltip?: string;
}

function StatCard({ icon, label, value, color, loading, tooltip }: StatCardProps) {
    const card = (
        <Paper
            variant="outlined"
            sx={{
                p: 2.5,
                borderRadius: 3,
                display: 'flex',
                flexDirection: 'column',
                gap: 1.5,
                height: '100%',
                transition: 'box-shadow 150ms ease, transform 150ms ease',
                '&:hover': { boxShadow: 3, transform: 'translateY(-2px)' },
            }}
        >
            <Stack direction="row" justifyContent="space-between" alignItems="flex-start">
                <Box
                    sx={{
                        width: 40,
                        height: 40,
                        borderRadius: 2,
                        bgcolor: color ? `${color}18` : LAVENDER,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        color: color ?? 'primary.main',
                    }}
                >
                    {icon}
                </Box>
            </Stack>

            {loading ? (
                <>
                    <Skeleton variant="text" width="60%" height={40} />
                    <Skeleton variant="text" width="80%" />
                </>
            ) : (
                <>
                    <Typography variant="h4" sx={{ fontWeight: 800, color: color ?? 'text.primary', lineHeight: 1 }}>
                        {value}
                    </Typography>
                    <Typography variant="body2" sx={{ color: 'text.secondary', fontWeight: 500 }}>
                        {label}
                    </Typography>
                </>
            )}
        </Paper>
    );

    return tooltip ? (
        <Tooltip title={tooltip} placement="top" arrow>
            {card}
        </Tooltip>
    ) : (
        card
    );
}

export default function Stadistics() {
    const navigate = useNavigate();
    const [items, setItems] = useState<Item[]>([]);
    const [loading, setLoading] = useState(true);

    const stats = useDashboardStats(items);
    const now = useTimeRefresh(60000);

    useEffect(() => {
        itemsApi
            .fetchItems()
            .then((result) => setItems(result ?? []))
            .catch(console.error)
            .finally(() => setLoading(false));
    }, []);

    const chartData = [
        { name: 'Seguras', value: stats.secureCount, color: CHART_COLORS.secure },
        {
            name: 'Débiles',
            value: stats.weakCount,
            color: CHART_COLORS.weak,
        },
        {
            name: 'Comprometidas',
            value: stats.compromisedCount,
            color: CHART_COLORS.compromised,
        },
    ].filter((d) => d.value > 0);

    const scoreColor = getScoreColor(stats.avgSecurityScore);
    const scoreLabel = getScoreLabel(stats.avgSecurityScore);

    return (
        <AppTheme>
            <CssBaseline enableColorScheme />
            <Stack direction="row" sx={{ minHeight: '100vh', width: '100%', overflow: 'hidden' }}>
                <Sidebar />

                <Stack sx={{ flex: 1, bgcolor: 'background.default', overflow: 'auto', minWidth: 0 }}>
                    {/* ── Topbar ── */}
                    <Header
                        title={'Estadistiques'}
                        icon={<BarChartRoundedIcon sx={{ fontSize: 30, color: 'text.primary' }} />}
                    />

                    {/* ── Contenido principal ── */}
                    <Box sx={{ px: 4, py: 4 }}>
                        {/* ── Bienvenida ── */}
                        <Stack direction="row" justifyContent="space-between" alignItems="center" mb={3}>
                            <Box>
                                <Typography variant="h4" sx={{ fontWeight: 800 }}>
                                    Resumen de seguretat
                                </Typography>
                                <Typography variant="body2" sx={{ color: 'text.secondary', mt: 0.5 }}>
                                    Aquí tienes un vistazo rápido al estado de tus credenciales.
                                </Typography>
                            </Box>
                        </Stack>

                        {/* ── Tarjetas de estadísticas ── */}
                        <Grid container spacing={2.5} mb={4}>
                            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                                <StatCard
                                    icon={<LockOutlinedIcon fontSize="small" />}
                                    label="Total de ítems"
                                    value={stats.totalItems}
                                    loading={loading}
                                />
                            </Grid>
                            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                                <StatCard
                                    icon={<WarningAmberRoundedIcon fontSize="small" />}
                                    label="Contraseñas comprometidas"
                                    value={stats.compromisedCount}
                                    color={stats.compromisedCount > 0 ? '#E24B4A' : undefined}
                                    loading={loading}
                                    tooltip="Contraseñas que coinciden con listas de contraseñas filtradas o muy comunes"
                                />
                            </Grid>
                            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                                <StatCard
                                    icon={<RepeatRoundedIcon fontSize="small" />}
                                    label="Contraseñas reutilizadas"
                                    value={stats.reusedCount}
                                    color={stats.reusedCount > 0 ? '#7E0FC2' : undefined}
                                    loading={loading}
                                    tooltip="Cuentas que comparten la misma contraseña"
                                />
                            </Grid>
                            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                                <StatCard
                                    icon={<TrendingUpRoundedIcon fontSize="small" />}
                                    label="Nivel medio de seguridad"
                                    value={loading ? '—' : `${stats.avgSecurityScore} / 100`}
                                    color={scoreColor}
                                    loading={loading}
                                    tooltip="Puntuación media basada en longitud, mayúsculas, números y símbolos"
                                />
                            </Grid>
                        </Grid>

                        {/* ── Fila central: Security Score + Gráfico ── */}
                        <Grid container spacing={2.5} mb={4}>
                            {/* Security Score */}
                            <Grid size={{ xs: 12, md: 4 }}>
                                <Paper
                                    variant="outlined"
                                    sx={{
                                        p: 3,
                                        borderRadius: 3,
                                        height: '100%',
                                        display: 'flex',
                                        flexDirection: 'column',
                                        gap: 2,
                                        transition: 'box-shadow 150ms ease',
                                        '&:hover': { boxShadow: 2 },
                                    }}
                                >
                                    <Stack direction="row" alignItems="center" gap={1}>
                                        <ShieldOutlinedIcon sx={{ color: scoreColor, fontSize: 22 }} />
                                        <Typography variant="h6" sx={{ fontWeight: 700 }}>
                                            Security Score
                                        </Typography>
                                    </Stack>

                                    {loading ? (
                                        <Skeleton variant="rounded" height={120} />
                                    ) : (
                                        <>
                                            <Stack alignItems="center" gap={1} py={1}>
                                                <Typography
                                                    sx={{
                                                        fontSize: '4rem',
                                                        fontWeight: 900,
                                                        lineHeight: 1,
                                                        color: scoreColor,
                                                    }}
                                                >
                                                    {stats.avgSecurityScore}
                                                </Typography>
                                                <Chip
                                                    label={scoreLabel}
                                                    size="small"
                                                    sx={{
                                                        fontWeight: 700,
                                                        bgcolor: `${scoreColor}18`,
                                                        color: scoreColor,
                                                        border: `1px solid ${scoreColor}40`,
                                                    }}
                                                />
                                            </Stack>

                                            <Box>
                                                <LinearProgress
                                                    variant="determinate"
                                                    value={stats.avgSecurityScore}
                                                    sx={{
                                                        height: 10,
                                                        borderRadius: 5,
                                                        bgcolor: `${scoreColor}20`,
                                                        '& .MuiLinearProgress-bar': {
                                                            bgcolor: scoreColor,
                                                            borderRadius: 5,
                                                        },
                                                    }}
                                                />
                                                <Stack direction="row" justifyContent="space-between" mt={0.75}>
                                                    <Typography variant="caption" sx={{ color: 'text.secondary' }}>
                                                        0
                                                    </Typography>
                                                    <Typography variant="caption" sx={{ color: 'text.secondary' }}>
                                                        100
                                                    </Typography>
                                                </Stack>
                                            </Box>

                                            <Divider />

                                            <Stack spacing={0.75}>
                                                <Stack direction="row" justifyContent="space-between">
                                                    <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                                                        Contraseñas seguras
                                                    </Typography>
                                                    <Typography variant="body2" sx={{ fontWeight: 700, color: CHART_COLORS.secure }}>
                                                        {stats.secureCount}
                                                    </Typography>
                                                </Stack>
                                                <Stack direction="row" justifyContent="space-between">
                                                    <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                                                        Contraseñas débiles
                                                    </Typography>
                                                    <Typography variant="body2" sx={{ fontWeight: 700, color: CHART_COLORS.weak }}>
                                                        {stats.weakCount}
                                                    </Typography>
                                                </Stack>
                                                <Stack direction="row" justifyContent="space-between">
                                                    <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                                                        Comprometidas
                                                    </Typography>
                                                    <Typography variant="body2" sx={{ fontWeight: 700, color: CHART_COLORS.compromised }}>
                                                        {stats.compromisedCount}
                                                    </Typography>
                                                </Stack>
                                            </Stack>
                                        </>
                                    )}
                                </Paper>
                            </Grid>

                            {/* Gráfico de distribución */}
                            <Grid size={{ xs: 12, md: 8 }}>
                                <Paper
                                    variant="outlined"
                                    sx={{
                                        p: 3,
                                        borderRadius: 3,
                                        height: '100%',
                                        display: 'flex',
                                        flexDirection: 'column',
                                        gap: 2,
                                        transition: 'box-shadow 150ms ease',
                                        '&:hover': { boxShadow: 2 },
                                    }}
                                >
                                    <Typography variant="h6" sx={{ fontWeight: 700 }}>
                                        Distribución de contraseñas
                                    </Typography>

                                    {loading ? (
                                        <Skeleton variant="rounded" height={240} />
                                    ) : chartData.length === 0 ? (
                                        <Stack alignItems="center" justifyContent="center" flex={1} py={4}>
                                            <LockOutlinedIcon sx={{ fontSize: 48, color: 'text.disabled', mb: 1 }} />
                                            <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                                                No hay datos suficientes para mostrar el gráfico.
                                            </Typography>
                                        </Stack>
                                    ) : (
                                        <ResponsiveContainer width="100%" height={260}>
                                            <PieChart>
                                                <Pie
                                                    data={chartData}
                                                    cx="50%"
                                                    cy="50%"
                                                    innerRadius={70}
                                                    outerRadius={110}
                                                    paddingAngle={3}
                                                    dataKey="value"
                                                >
                                                    {chartData.map((entry, index) => (
                                                        <Cell key={`cell-${index}`} fill={entry.color} />
                                                    ))}
                                                </Pie>
                                                <ReTooltip
                                                    formatter={(value) => (typeof value === 'number' ? value.toString() : value)}
                                                    contentStyle={{
                                                        borderRadius: 8,
                                                        border: '1px solid #e0e0e0',
                                                        fontSize: 13,
                                                    }}
                                                />
                                                <Legend
                                                    iconType="circle"
                                                    iconSize={10}
                                                    formatter={(value) => (
                                                        <span style={{ fontSize: 13, color: 'var(--mui-palette-text-secondary)' }}>
                                                            {value}
                                                        </span>
                                                    )}
                                                />
                                            </PieChart>
                                        </ResponsiveContainer>
                                    )}
                                </Paper>
                            </Grid>
                        </Grid>

                        {/* ── Alertas de seguridad ── */}
                        {!loading && (stats.compromisedCount > 0 || stats.reusedCount > 0) && (
                            <Stack spacing={1.5} mb={4}>
                                <Typography variant="h6" sx={{ fontWeight: 700 }}>
                                    Alertas de seguridad
                                </Typography>
                                {stats.compromisedCount > 0 && (
                                    <SecurityAlert
                                        type="compromised"
                                        count={stats.compromisedCount}
                                        onReview={() => navigate('/Items')}
                                    />
                                )}
                                {stats.reusedCount > 0 && (
                                    <SecurityAlert
                                        type="reused"
                                        count={stats.reusedCount}
                                        onReview={() => navigate('/Duplicats')}
                                    />
                                )}
                            </Stack>
                        )}

                        {/* ── Última actividad ── */}
                        <Paper variant="outlined" sx={{ borderRadius: 3, overflow: 'hidden' }}>
                            <Stack
                                direction="row"
                                justifyContent="space-between"
                                alignItems="center"
                                sx={{ px: 3, py: 2, borderBottom: '1px solid', borderColor: 'divider' }}
                            >
                                <Stack direction="row" alignItems="center" gap={1}>
                                    <AccessTimeRoundedIcon sx={{ fontSize: 20, color: 'text.secondary' }} />
                                    <Typography variant="h6" sx={{ fontWeight: 700 }}>
                                        Actividad reciente
                                    </Typography>
                                </Stack>
                                <Button
                                    size="small"
                                    endIcon={<OpenInNewRoundedIcon sx={{ fontSize: 14 }} />}
                                    onClick={() => navigate('/Items')}
                                    sx={{ textTransform: 'none', fontWeight: 600, color: 'primary.main' }}
                                >
                                    Ver todos
                                </Button>
                            </Stack>

                            {loading ? (
                                <Stack spacing={0}>
                                    {[...Array(4)].map((_, i) => (
                                        <Box key={i} sx={{ px: 3, py: 2, borderBottom: '1px solid', borderColor: 'divider' }}>
                                            <Skeleton variant="text" width="40%" />
                                            <Skeleton variant="text" width="25%" />
                                        </Box>
                                    ))}
                                </Stack>
                            ) : stats.recentItems.length === 0 ? (
                                <Stack alignItems="center" justifyContent="center" py={5}>
                                    <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                                        Aún no tienes ítems guardados.
                                    </Typography>
                                    <Button
                                        variant="text"
                                        sx={{ mt: 1, textTransform: 'none', fontWeight: 600 }}
                                        onClick={() => navigate('/ChooseType')}
                                    >
                                        Añade tu primer ítem
                                    </Button>
                                </Stack>
                            ) : (
                                stats.recentItems.map((item, i) => (
                                    <Stack
                                        key={item.uuid}
                                        direction="row"
                                        justifyContent="space-between"
                                        alignItems="center"
                                        onClick={() => navigate('/Item', { state: { uuid: item.uuid } })}
                                        sx={{
                                            px: 3,
                                            py: 1.75,
                                            borderBottom: i < stats.recentItems.length - 1 ? '1px solid' : 'none',
                                            borderColor: 'divider',
                                            cursor: 'pointer',
                                            transition: 'background 120ms ease',
                                            '&:hover': { bgcolor: 'action.hover' },
                                        }}
                                    >
                                        <Stack direction="row" alignItems="center" gap={2}>
                                            <Avatar
                                                sx={{
                                                    width: 34,
                                                    height: 34,
                                                    bgcolor: LAVENDER,
                                                    color: 'primary.main',
                                                    fontSize: 14,
                                                    fontWeight: 700,
                                                }}
                                            >
                                                {item.titol.charAt(0).toUpperCase()}
                                            </Avatar>
                                            <Box>
                                                <Typography variant="body2" sx={{ fontWeight: 600 }}>
                                                    {item.titol}
                                                </Typography>
                                                <Typography variant="caption" sx={{ color: 'text.secondary' }}>
                                                    {item.nomUsuari}
                                                </Typography>
                                            </Box>
                                        </Stack>
                                        <Typography variant="caption" sx={{ color: 'text.secondary' }}>
                                            Modificat: {getTimeAgo(item.dataEditat, now)}
                                        </Typography>
                                    </Stack>
                                ))
                            )}
                        </Paper>
                    </Box>
                </Stack>
            </Stack>
        </AppTheme>
    );
}