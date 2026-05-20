import React from "react";
import { Navigate } from "react-router-dom";
import { isTokenExpired } from "./crypto-utils";

export function AuthGate() {
  const token = localStorage.getItem("jwtToken");

  if (token && !isTokenExpired(token)) {
    return <Navigate to="/home" replace />;
  }

  return <Navigate to="/login" replace />;
}

export function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const token = localStorage.getItem("jwtToken");

  if (!token || isTokenExpired(token)) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
}