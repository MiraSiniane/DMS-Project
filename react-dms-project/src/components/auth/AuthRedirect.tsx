// src/components/AuthRedirect.tsx
import { Navigate } from 'react-router-dom';

export default function AuthRedirect() {
  const isAuthenticated = localStorage.getItem('authToken');
  
  if (isAuthenticated) {
    return <Navigate to="/home" replace />;
  }

  return null;
}