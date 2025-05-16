// src/components/AuthLayout.tsx
import { Link, Outlet } from 'react-router-dom';
import '../css/AuthLayout.css';

import AuthRedirect from './AuthRedirect';

export default function AuthLayout() {
  return (
    
    <div className="auth-container">
      <AuthRedirect />
      <div className="auth-card">
        <div className="auth-tabs">
          <Link to="/login" className="auth-tab">Login</Link>
          <Link to="/signup" className="auth-tab">Sign Up</Link>
        </div>
        <div className="auth-content">
          <Outlet />
        </div>
      </div>
    </div>
  );
}