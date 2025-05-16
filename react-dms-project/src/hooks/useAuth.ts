// src/hooks/useAuth.ts
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  login as apiLogin,
  registerSuperadmin as apiSignup,
  getUserInfo,
  updateUserStatus
} from '../services/authService';
import type { User } from '../types/authTypes';

export function useAuth() {
  const navigate = useNavigate();
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const checkAuth = async () => {
      const token = localStorage.getItem('authToken');
      if (token) {
        try {
          const userInfo = await getUserInfo(token);
          setCurrentUser(userInfo);
        } catch (error) {
          console.error('Auth check failed:', error);
        }
      }
      setIsLoading(false);
    };
    
    checkAuth();
  }, []);

  const login = async (email: string, password: string) => {
    try {
      const response = await apiLogin(email, password);
      localStorage.setItem('authToken', response.token);
      
      const userInfo = await getUserInfo(response.token);
      setCurrentUser(userInfo);
      
      await updateUserStatus(response.token, 'active');
      navigate('/home');
    } catch (error) {
      console.error("Login failed:", error);
      throw error;
    }
  };

  const signup = async (name: string, email: string, password: string) => {
    try {
      const response = await apiSignup(name, email, password);
      localStorage.setItem('authToken', response.token);
      
      const userInfo = await getUserInfo(response.token);
      setCurrentUser(userInfo);
      
      await updateUserStatus(response.token, 'active');
      navigate('/home');
    } catch (error) {
      throw error;
    }
  };

  const logout = async () => {
    const token = localStorage.getItem('authToken');
    if (token) {
      try {
        await updateUserStatus(token, 'inactive');
      } catch (error) {
        console.error('Logout update failed:', error);
      }
    }
    
    localStorage.removeItem('authToken');
    setCurrentUser(null);
    navigate('/login');
  };

  return {
    isLoading,
    login, 
    logout, 
    signup, 
    currentUser,
    isSuperAdmin: currentUser?.role.name === 'SUPERADMIN',
    isAdmin: currentUser?.role.name === 'ADMIN' || currentUser?.role.name === 'SUPERADMIN'
  };
}