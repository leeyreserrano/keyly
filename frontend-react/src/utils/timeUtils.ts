export const getTimeAgo = (date: string, now: number): string => {
  if (!date) return '';

  const past = new Date(date).getTime();
  const diffMs = now - past;

  const seconds = Math.floor(diffMs / 1000);
  const minutes = Math.floor(seconds / 60);
  const hours   = Math.floor(minutes / 60);
  const days    = Math.floor(hours / 24);
  const weeks   = Math.floor(days / 7);
  const months  = Math.floor(days / 30);
  const years   = Math.floor(days / 365);

  if (seconds < 60)  return `Fa ${seconds} s`;
  if (minutes < 60)  return `Fa ${minutes} min`;
  if (hours   < 24)  return `Fa ${hours} h`;
  if (days    < 7)   return `Fa ${days} dies`;
  if (weeks   < 4)   return `Fa ${weeks} setmanes`;
  if (months  < 12)  return `Fa ${months} mesos`;
  return `Fa ${years} anys`;
};

export const formatDate = (date: string): string => {
  if (!date) return '';
  return new Intl.DateTimeFormat('ca-ES', {
    day: '2-digit',
    month: 'long',
    year: 'numeric',
  }).format(new Date(date));
};