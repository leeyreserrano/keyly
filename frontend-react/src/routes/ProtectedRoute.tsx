import { Navigate } from 'react-router';
import { useAuth } from '../context/AuthContext';
import type { ReactNode } from 'react';

export default function ProtectedRoute({ children }: { children: ReactNode }) {
  const { token, loadingAuth } = useAuth();
  console.log("TOKEN:", token);
  console.log("LOADING:", loadingAuth);
  if (loadingAuth) return null; 

  if (!token) return <Navigate to="/" replace />;

  return <>{children}</>;
}