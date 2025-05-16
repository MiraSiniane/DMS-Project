// src/components/EditUserModal.tsx
import { type FormEvent, useState, useEffect } from 'react';
import { FiX, FiUser, FiMail, FiBriefcase, FiHome, FiPhone } from 'react-icons/fi';
import { updateUser } from '../../services/userService';
import { getAllDepartments, type Department } from '../../services/departmentService';
import '../css/EditUserModal.css';
import type { User } from '../../types/User';

interface EditUserModalProps {
  user: User;
  onClose: () => void;
  onUpdate: (updatedUser: User) => void;
}

export default function EditUserModal({ user, onClose, onUpdate }: EditUserModalProps) {
  const [formData, setFormData] = useState({
    name: user.name,
    email: user.email,
    position: user.position || '',
    address: user.address || '',
    phone: user.phone || '',
    departmentIds: user.departments?.map(d => d.id) || []
  });
  const [departments, setDepartments] = useState<Department[]>([]);
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    const fetchDepartments = async () => {
      try {
        const token = localStorage.getItem('authToken');
        if (!token) return;
        
        const depts = await getAllDepartments(token);
        setDepartments(depts);
      } catch (err) {
        console.error('Failed to fetch departments:', err);
      }
    };

    fetchDepartments();
  }, []);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
        const token = localStorage.getItem('authToken');
        if (!token) return;
        
        // Only send fields that can be updated
        const updateData = {
        name: formData.name,
        email: formData.email,
        position: formData.position,
        address: formData.address,
        phone: formData.phone,
        departmentIds: formData.departmentIds
        };
        
        const updatedUser = await updateUser(token, user.id, updateData);
        onUpdate(updatedUser);
        onClose();
    } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to update user');
    } finally {
        setIsSubmitting(false);
    }
  };

  const handleDepartmentChange = (deptId: number) => {
    setFormData(prev => ({
      ...prev,
      departmentIds: prev.departmentIds.includes(deptId)
        ? prev.departmentIds.filter(id => id !== deptId)
        : [...prev.departmentIds, deptId]
    }));
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <button className="close-button" onClick={onClose}>
          <FiX />
        </button>
        
        <div className="modal-header">
          <h2>Edit {user.name}</h2>
        </div>

        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <FiUser className="input-icon" />
            <input
              type="text"
              placeholder="Full Name"
              value={formData.name}
              onChange={(e) => setFormData({...formData, name: e.target.value})}
              required
            />
          </div>

          <div className="input-group">
            <FiMail className="input-icon" />
            <input
              type="email"
              placeholder="Email Address"
              value={formData.email}
              onChange={(e) => setFormData({...formData, email: e.target.value})}
              required
            />
          </div>

          <div className="input-group">
            <FiBriefcase className="input-icon" />
            <input
              type="text"
              placeholder="Position"
              value={formData.position}
              onChange={(e) => setFormData({...formData, position: e.target.value})}
            />
          </div>

          <div className="input-group">
            <FiHome className="input-icon" />
            <input
              type="text"
              placeholder="Address"
              value={formData.address}
              onChange={(e) => setFormData({...formData, address: e.target.value})}
            />
          </div>

          <div className="input-group">
            <FiPhone className="input-icon" />
            <input
              type="text"
              placeholder="Phone"
              value={formData.phone}
              onChange={(e) => setFormData({...formData, phone: e.target.value})}
            />
          </div>

          <div className="input-group">
            <label>Departments</label>
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
          </div>

          <button 
            type="submit" 
            className="submit-button"
            disabled={isSubmitting}
          >
            {isSubmitting ? 'Updating...' : 'Update User'}
          </button>
        </form>
      </div>
    </div>
  );
}