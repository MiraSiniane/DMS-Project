// src/components/AddUserButton.tsx
import { useState } from 'react';
import { FiPlus } from 'react-icons/fi';
import { useAuth } from '../../hooks/useAuth';
import AddUserModal from './AddUserModal';
import '../css/AddButton.css'

export default function AddUserButton() {
  const [showModal, setShowModal] = useState(false);
  const { isAdmin, isLoading } = useAuth();

  if (isLoading || !isAdmin) return null;

  return (
    <>
      <button 
        className="floating-add-button"
        onClick={() => setShowModal(true)}
        aria-label="Add new user"
      >
        <FiPlus className="plus-icon" />
      </button>
      
      {showModal && (
        <AddUserModal 
          onClose={() => setShowModal(false)}
          isSuperAdmin={isAdmin}
        />
      )}
    </>
  );
}
