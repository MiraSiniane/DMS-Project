// src/components/AddUserModal.tsx
import { type FormEvent, useState, useEffect } from 'react';
import { FiX} from 'react-icons/fi';
import { createUser } from '../../services/authService';
import { useAuth } from '../../hooks/useAuth';
import { getAllDepartments } from '../../services/departmentService'; // Create this service
import '../css/AddUserModal.css';

interface AddUserModalProps {
  onClose: () => void;
  isSuperAdmin: boolean;
}

interface Department {
  id: number;
  name: string;
}

export default function AddUserModal({ onClose, isSuperAdmin }: AddUserModalProps) {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    role: isSuperAdmin ? 'ADMIN' : 'USER',
    position: '',
    departmentIds: [] as number[]
  });
  const [departments, setDepartments] = useState<Department[]>([]);
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLoadingDepts, setIsLoadingDepts] = useState(true);
  const { currentUser } = useAuth();

  useEffect(() => {
    const fetchDepartments = async () => {
      try {
        const token = localStorage.getItem('authToken');
        if (!token) return;
        
        const depts = await getAllDepartments(token);
        setDepartments(depts);
      } catch (err) {
        console.error('Failed to fetch departments:', err);
      } finally {
        setIsLoadingDepts(false);
      }
    };

    fetchDepartments();
  }, []);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const token = localStorage.getItem('authToken');
      if (!token || !currentUser) return;
      
      await createUser(token, {
        ...formData,
        departmentIds: formData.departmentIds
      });
      onClose();
      window.location.reload();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to create user');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDepartmentChange = (deptId: number) => {
    setFormData(prev => {
      const newDepts = prev.departmentIds.includes(deptId)
        ? prev.departmentIds.filter(id => id !== deptId)
        : [...prev.departmentIds, deptId];
      return { ...prev, departmentIds: newDepts };
    });
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <button className="close-button" onClick={onClose}>
          <FiX />
        </button>
        
        <div className="modal-header">
          <h2>Create New User</h2>
        </div>

        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <input
              type="text"
              placeholder="Full Name"
              value={formData.name}
              onChange={(e) => setFormData({...formData, name: e.target.value})}
              required
            />
          </div>

          <div className="input-group">
            <input
              type="email"
              placeholder="Email Address"
              value={formData.email}
              onChange={(e) => setFormData({...formData, email: e.target.value})}
              required
            />
          </div>

          <div className="input-group">
            <input
              type="password"
              placeholder="Password (min 6 characters)"
              value={formData.password}
              onChange={(e) => setFormData({...formData, password: e.target.value})}
              required
              minLength={6}
            />
          </div>

          
          <div className="input-group">
            {(currentUser?.role.name == 'SUPERADMIN')? (
              <select
                value={formData.role}
                onChange={(e) => setFormData({...formData, role: e.target.value})}
                className="role-select"
              >
                <option value="ADMIN">Admin</option>
                <option value="USER">User</option>
              </select>
            ):(
                <select
                value={formData.role}
                onChange={(e) => setFormData({...formData, role: e.target.value})}
                className="role-select"
              >
                <option value="USER">User</option>
              </select>
            )}
          </div>

          <div className="input-group">
            {isLoadingDepts ? (
              <div>Loading departments...</div>
            ) : (
              <div className="department-checkboxes">
                {departments.map(dept => (
                  <label key={dept.id} className="checkbox-label">
                    <input
                      type="checkbox"
                      checked={formData.departmentIds.includes(dept.id)}
                      onChange={() => handleDepartmentChange(dept.id)}
                    />
                    <span>{dept.name}</span>
                  </label>
                ))}
              </div>
            )}
          </div>

          <div className="input-group">
            <input
              type="text"
              placeholder="Position (optional)"
              value={formData.position}
              onChange={(e) => setFormData({...formData, position: e.target.value})}
            />
          </div>

          <button 
            type="submit" 
            className="submit-button"
            disabled={isSubmitting}
          >
            {isSubmitting ? 'Creating...' : 'Create User'}
          </button>
        </form>
      </div>
    </div>
  );
}