// src/pages/Signup.tsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthForm from '../../components/auth/AuthForm';
import { useAuth } from '../../hooks/useAuth';
import { checkServerStatus } from '../../services/authService';
import '../../components/css/AuthLayout.css';

export default function Signup() {
  const { signup } = useAuth();
  const [error, setError] = useState<string | null>(null);
  const [serverOnline, setServerOnline] = useState<boolean | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const checkServer = async () => {
      const isOnline = await checkServerStatus();
      setServerOnline(isOnline);
    };
    checkServer();
  }, []);

  const handleSubmit = async (email: string, password: string, name?: string) => {
    if (!name) {
      setError('Name is required');
      return;
    }

    try {
      await signup(name, email, password);
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Registration failed');
    }
  };

  if (serverOnline === null) {
    return <div>Checking server connection...</div>;
  }

  if (!serverOnline) {
    return (
      <div className="error-container">
        <h2>Server Connection Error</h2>
        <p>Unable to connect to the authentication server. Please try again later.</p>
      </div>
    );
  }

  return (
    <div className="auth-content">
      {error && <div className="error-message">{error}</div>}
      <AuthForm type="signup" onSubmit={handleSubmit} />
      <p className="auth-switch">
        Already have an account?{' '}
        <span onClick={() => navigate('/login')}>Login</span>
      </p>
    </div>
  );
}