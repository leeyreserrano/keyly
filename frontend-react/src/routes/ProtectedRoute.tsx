import { Navigate } from 'react-router';

function isTokenExpired(token: string): boolean {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.exp * 1000 < Date.now();
  } catch {
    return true;
  }
}

export default function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const token = localStorage.getItem('jwtToken') || sessionStorage.getItem('jwtToken');

  if (!token || isTokenExpired(token)) {
    localStorage.clear();
    sessionStorage.clear();
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
}