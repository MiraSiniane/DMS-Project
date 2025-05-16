// src/pages/Home.tsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiEye, FiEyeOff, FiEdit } from 'react-icons/fi';
import { getUserInfo, updatePassword } from '../services/authService';
import { useAuth } from '../hooks/useAuth';
import './css/Home.css';

interface User {
  id: number;
  name: string;
  email: string;
  position?: string;
  phone?: string;
  address?: string;
  role: {
    id: number;
    name: string;
  };
  status: string;
  lastLogin?: string;
  departments?: Department[];
}

interface Department {
  id: number;
  name: string;
  createdAt?: string;
  documentCount?: number;
}

export default function Home() {
  const navigate = useNavigate();
  const { logout } = useAuth();
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showPassword, setShowPassword] = useState(false);
  const [isEditingPassword, setIsEditingPassword] = useState(false);
  const [passwords, setPasswords] = useState({
    old: '',
    new: ''
  });

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const token = localStorage.getItem('authToken');
        if (!token) {
          navigate('/login');
          return;
        }

        const userData = await getUserInfo(token);
        setUser({
          ...userData,
          lastLogin: userData.lastLogin || new Date().toLocaleString()
        });
      } catch (error) {
        setError(error instanceof Error ? error.message : 'Failed to load user data');
        console.error('Error fetching user data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, [navigate]);

  const handlePasswordUpdate = async () => {
    try {
      const token = localStorage.getItem('authToken');
      if (!token) {
        navigate('/login');
        return;
      }

      await updatePassword(token, passwords.old, passwords.new);
      setIsEditingPassword(false);
      setPasswords({ old: '', new: '' });
      alert('Password updated successfully');
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Failed to update password');
      console.error('Password update error:', error);
    }
  };

  if (loading) {
    return <div className="loading">Loading user data...</div>;
  }

  if (error) {
    return (
      <div className="error-container">
        <h2>Error</h2>
        <p>{error}</p>
        <button onClick={() => window.location.reload()}>Try Again</button>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="error-container">
        <h2>No User Data</h2>
        <p>Unable to load user profile</p>
        <button onClick={logout}>Return to Login</button>
      </div>
    );
  }

  return (
    <div className="profile-container">
      <h1>User Profile</h1>
      
      <div className="profile-section">
        <div className="profile-row">
          <span className="profile-label">Name:</span>
          <span className="profile-value">{user.name}</span>
        </div>
        
        <div className="profile-row">
          <span className="profile-label">Email:</span>
          <span className="profile-value">{user.email}</span>
        </div>
        
        <div className="profile-row">
          <span className="profile-label">Password:</span>
          <div className="password-field">
            {isEditingPassword ? (
              <>
                <input
                  type="password"
                  value={passwords.old}
                  onChange={(e) => setPasswords({...passwords, old: e.target.value})}
                  placeholder="Current password"
                />
                <input
                  type={showPassword ? "text" : "password"}
                  value={passwords.new}
                  onChange={(e) => setPasswords({...passwords, new: e.target.value})}
                  placeholder="New password"
                />
                <button onClick={() => setShowPassword(!showPassword)}>
                  {showPassword ? <FiEyeOff /> : <FiEye />}
                </button>
                <button onClick={handlePasswordUpdate}>Save</button>
                <button onClick={() => setIsEditingPassword(false)}>Cancel</button>
              </>
            ) : (
              <>
                <span>••••••••</span>
                <button onClick={() => setShowPassword(!showPassword)}>
                  {showPassword ? <FiEyeOff /> : <FiEye />}
                </button>
                <button onClick={() => setIsEditingPassword(true)}>
                  <FiEdit />
                </button>
              </>
            )}
          </div>
        </div>

        {user.position && (
          <div className="profile-row">
            <span className="profile-label">Position:</span>
            <span className="profile-value">{user.position}</span>
          </div>
        )}

        {user.phone && (
          <div className="profile-row">
            <span className="profile-label">Phone:</span>
            <span className="profile-value">{user.phone}</span>
          </div>
        )}

        {user.address && (
          <div className="profile-row">
            <span className="profile-label">Address:</span>
            <span className="profile-value">{user.address}</span>
          </div>
        )}

        <div className="profile-row">
          <span className="profile-label">Role:</span>
          <span className="profile-value role-badge">{user.role.name}</span>
        </div>

        {user.departments && user.departments.length > 0 && (
          <div className="profile-row">
            <span className="profile-label">Departments:</span>
            <div className="departments-list">
              {user.departments.map((dept, index) => (
                <span key={index} className="department-badge">
                  {typeof dept === 'string' ? dept : dept.name}
                </span>
              ))}
            </div>
          </div>
        )}

        <div className="profile-row">
          <span className="profile-label">Status:</span>
          <span className="profile-value">
            <span className={`status-dot ${user.status}`}></span>
            {user.status.charAt(0).toUpperCase() + user.status.slice(1)}
          </span>
        </div>

        <div className="profile-row">
          <span className="profile-label">Last Login:</span>
          <span className="profile-value">{user.lastLogin}</span>
        </div>
      </div>
    </div>
  );
}