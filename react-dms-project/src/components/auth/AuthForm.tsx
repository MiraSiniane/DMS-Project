// src/components/AuthForm.tsx
import type { FormEvent } from 'react';
import { useState } from 'react';
import '../css/AuthForm.css';

type AuthFormType = 'login' | 'signup';

interface AuthFormProps {
  type: AuthFormType;
  onSubmit: (email: string, password: string, name?: string) => Promise<void>;
}

export default function AuthForm({ type, onSubmit }: AuthFormProps) {
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    setIsLoading(true);
    
    try {
      const formData = new FormData(e.currentTarget);
      const email = formData.get('email') as string;
      const password = formData.get('password') as string;
      const name = type === 'signup' ? (formData.get('name') as string) : undefined;
      
      await onSubmit(email, password, name);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An unknown error occurred');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {error && <div className="error-message">{error}</div>}
      
      {type === 'signup' && (
        <div className="form-group">
          <label htmlFor="name">Full Name</label>
          <input type="text" id="name" name="name" required placeholder=" " />
        </div>
      )}
      
      <div className="form-group">
        <label htmlFor="email">Email</label>
        <input type="email" id="email" name="email" required placeholder=" " />
      </div>
      
      <div className="form-group">
        <label htmlFor="password">Password</label>
        <input type="password" id="password" name="password" required minLength={6} placeholder=" " />
      </div>
      
      <button type="submit" disabled={isLoading}>
        {isLoading ? 'Processing...' : type === 'login' ? 'Login' : 'Sign Up'}
      </button>
    </form>
  );
}