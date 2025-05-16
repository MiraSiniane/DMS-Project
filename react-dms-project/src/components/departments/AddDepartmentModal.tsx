// src/components/AddDepartmentModal.tsx
import { useState, type FormEvent } from 'react';
import { FiX } from 'react-icons/fi';
import { createDepartment } from '../../services/departmentService';
import '../css/AddDepartmentModal.css';
import '../../components/css/FloatingButton.css'
interface AddDepartmentModalProps {
  onClose: () => void;
  onSuccess: () => void;
}

export default function AddDepartmentModal({ onClose, onSuccess }: AddDepartmentModalProps) {
  const [name, setName] = useState('');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const token = localStorage.getItem('authToken');
      if (!token) return;
      
      await createDepartment(token, name);
      onSuccess();
      onClose();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to create department');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <button className="close-button" onClick={onClose}>
          <FiX />
        </button>
        
        <div className="modal-header">
          <h2>Add New Department</h2>
        </div>

        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label>Department Name</label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
              autoFocus
            />
          </div>

          <button 
            type="submit" 
            className="submit-button"
            disabled={isSubmitting}
          >
            {isSubmitting ? 'Creating...' : 'Create Department'}
          </button>
        </form>
      </div>
    </div>
  );
}