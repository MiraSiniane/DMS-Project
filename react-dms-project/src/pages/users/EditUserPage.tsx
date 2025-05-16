// src/pages/EditUserPage.tsx
import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { FiSave, FiArrowLeft } from 'react-icons/fi';
import { updateUser, getUser } from '../../services/userService';
import { assignDepartment, getAllDepartments, unassignDepartment } from '../../services/departmentService';
import { useAuth } from '../../hooks/useAuth';
import type { User } from '../../types/User';
import '../css/EditUserPage.css'; // Create this CSS file for styling

type Department = {
  id: number;
  name: string;
};

export default function EditUserPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [user, setUser] = useState<User | null>(null);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { currentUser } = useAuth();
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const token = localStorage.getItem('authToken');
        if (!token) {
          navigate('/login');
          return;
        }

        const [userData, depts] = await Promise.all([
          getUser(token, Number(id)),
          getAllDepartments(token)
        ]);

        setDepartments(depts as Department[]);
        setUser({
          ...userData,
          departments: userData.departments || []
        });
        setError(null);
      } catch (err) {
        setError('Failed to load user data');
        console.error('Error fetching data:', err);
        navigate('/users');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id, navigate]);

  useEffect(() => {
    if (!loading && user && currentUser) {
      const canEdit =
        currentUser.role.name === 'SUPERADMIN' ||
        (currentUser.role.name === 'ADMIN' && user.role.name === 'USER');

      if (!canEdit) {
        navigate('/users');
      }
    }
  }, [user, currentUser, loading, navigate]);

  const handleDepartmentChange = async (deptId: number) => {
  if (!user || isSaving) return;

  const token = localStorage.getItem('authToken');
  if (!token) {
    navigate('/login');
    return;
  }

  const isCurrentlyAssigned = (user.departments || []).some(d => d.id === deptId);
  
  try {
    setIsSaving(true);
    
    if (isCurrentlyAssigned) {
      // Unassign department
      await unassignDepartment(token, {
        userId: user.id,
        departmentId: deptId
      });
      setUser(prev => prev ? {
        ...prev,
        departments: (prev.departments || []).filter(d => d.id !== deptId)
      } : prev);
    } else {
      // Assign department
      await assignDepartment(token, {
        userId: user.id,
        departmentId: deptId
      });
      const newDept = departments.find(d => d.id === deptId);
      if (newDept) {
        setUser(prev => prev ? {
          ...prev,
          departments: [...(prev.departments || []), newDept]
        } : prev);
      }
    }
  } catch (err) {
    setError(`Failed to ${isCurrentlyAssigned ? 'unassign' : 'assign'} department`);
    console.error('Error updating department:', err);
  } finally {
    setIsSaving(false);
  }
};

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user || isSaving) return;

    try {
      setIsSaving(true);
      const token = localStorage.getItem('authToken');
      if (!token) {
        navigate('/login');
        return;
      }

      await updateUser(token, user.id, {
        name: user.name,
        email: user.email,
        position: user.position,
        address: user.address,
        phone: user.phone
      });

      navigate('/users');
    } catch (err) {
      setError('Failed to update user');
      console.error('Error updating user:', err);
    } finally {
      setIsSaving(false);
    }
  };

  if (loading) {
    return <div className="loading-container">Loading user data...</div>;
  }

  if (!user) {
    return <div className="error-container">User not found</div>;
  }

  return (
    <div className="edit-user-page">
      <div className="page-header">
        <button onClick={() => navigate('/users')} className="back-button">
          <FiArrowLeft /> Back to Users
        </button>
        <h1>Edit User: {user.name}</h1>
      </div>

      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit} className="user-form">
        <div className="form-group">
          <label>Full Name</label>
          <input
            type="text"
            value={user.name}
            onChange={(e) => setUser({...user, name: e.target.value})}
            required
            disabled={isSaving}
          />
        </div>

        <div className="form-group">
          <label>Email</label>
          <input
            type="email"
            value={user.email}
            onChange={(e) => setUser({...user, email: e.target.value})}
            required
            disabled={isSaving}
          />
        </div>

        <div className="form-group">
          <label>Position</label>
          <input
            type="text"
            value={user.position || ''}
            onChange={(e) => setUser({...user, position: e.target.value})}
            disabled={isSaving}
          />
        </div>

        <div className="form-group">
          <label>Address</label>
          <input
            type="text"
            value={user.address || ''}
            onChange={(e) => setUser({...user, address: e.target.value})}
            disabled={isSaving}
          />
        </div>

        <div className="form-group">
          <label>Phone</label>
          <input
            type="text"
            value={user.phone || ''}
            onChange={(e) => setUser({...user, phone: e.target.value})}
            disabled={isSaving}
          />
        </div>

        <div className="form-group">
          <label>Departments</label>
          <div className="department-checkboxes">
            {departments.map((dept) => (
              <label key={dept.id} className="checkbox-label">
                <input
                  type="checkbox"
                  checked={(user.departments || []).some(d => d.id === dept.id)}
                  onChange={() => handleDepartmentChange(dept.id)}
                  disabled={isSaving}
                />
                <span>{dept.name}</span>
              </label>
            ))}
          </div>
        </div>

        <button type="submit" className="save-button" disabled={isSaving}>
          <FiSave /> {isSaving ? 'Saving...' : 'Save Changes'}
        </button>
      </form>
    </div>
  );
}