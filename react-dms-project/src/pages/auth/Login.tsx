// src/pages/Login.tsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthForm from '../../components/auth/AuthForm';
import { useAuth } from '../../hooks/useAuth';
import '../../components/css/AuthLayout.css';

export default function Login() {
  const { login } = useAuth();
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const handleSubmit = async (email: string, password: string) => {
    try {
      await login(email, password);
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Login failed');
    }
  };

  return (
    <div className="auth-content">
      {error && <div className="error-message">{error}</div>}
      <AuthForm type="login" onSubmit={handleSubmit} />
    </div>
  );
}