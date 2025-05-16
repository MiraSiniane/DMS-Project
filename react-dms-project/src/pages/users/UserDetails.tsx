// src/pages/UserDetails.tsx
import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { FiArrowLeft } from 'react-icons/fi';
import { getUser } from '../../services/userService';
import '../css/UserDetails.css';

export default function UserDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [user, setUser] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const token = localStorage.getItem('authToken');
        if (!token) return;
        
        const userData = await getUser(token, Number(id));
        setUser(userData);
      } catch (error) {
        console.error('Failed to fetch user:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, [id]);

  if (loading) return <div className="loading">Loading user...</div>;
  if (!user) return <div className="error">User not found</div>;

  const formatRole = (role: string) => {
    switch (role) {
      case 'SUPERADMIN': return 'Super Admin';
      case 'ADMIN': return 'Admin';
      case 'USER': return 'User';
      default: return role;
    }
  };

  return (
    <div className="user-details-container">
      <button onClick={() => navigate(-1)} className="back-button">
        <FiArrowLeft /> Back to Users
      </button>
      
      
      <div className="user-details-card">
        <div className="detail-row">
          <span className="detail-label">Name:</span>
          <span className="detail-value">{user.name}</span>
        </div>
        
        <div className="detail-row">
          <span className="detail-label">Email:</span>
          <span className="detail-value">{user.email}</span>
        </div>
        
        <div className="detail-row">
          <span className="detail-label">Phone:</span>
          <span className="detail-value">{user.phone || '-'}</span>
        </div>
        
        <div className="detail-row">
          <span className="detail-label">Address:</span>
          <span className="detail-value">{user.address || '-'}</span>
        </div>
        
        <div className="detail-row">
          <span className="detail-label">Role:</span>
          <span className="detail-value">{formatRole(user.role.name)}</span>
        </div>
        
        <div className="detail-row">
          <span className="detail-label">Position:</span>
          <span className="detail-value">{user.position || '-'}</span>
        </div>
        
        <div className="detail-row">
          <span className="detail-label">Status:</span>
          <span className="detail-value">
            <span className={`status-dot ${user.status}`} />
            {user.status.charAt(0).toUpperCase() + user.status.slice(1)}
          </span>
        </div>
        
        <div className="detail-row">
          <span className="detail-label">Last Login:</span>
          <span className="detail-value">
            {user.lastLogin ? new Date(user.lastLogin).toLocaleString() : 'Never'}
          </span>
        </div>
        
        <div className="detail-row">
          <span className="detail-label">Departments:</span>
          <span className="detail-value">
            {user.departments?.map((d: any) => d.name).join(', ') || '-'}
          </span>
        </div>
      </div>
    </div>
  );
}