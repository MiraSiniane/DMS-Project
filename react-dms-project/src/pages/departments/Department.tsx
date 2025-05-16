// src/pages/Departments.tsx
import { useState, useEffect } from 'react';
import { FiPlus, FiTrash2 } from 'react-icons/fi';
import { useAuth } from '../../hooks/useAuth';
import { getAllDepartments, deleteDepartment } from '../../services/departmentService';
import AddDepartmentModal from '../../components/departments/AddDepartmentModal';
import '../css/Department.css';
import '../../components/css/AddButton.css'

interface Department {
  id: number;
  name: string;
  createdAt: string;
  userCount: number;
  documentCount: number;
}

export default function Departments() {
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(true);
  const [showAddModal, setShowAddModal] = useState(false);
  const { isSuperAdmin } = useAuth();

  const fetchDepartments = async () => {
    try {
      const token = localStorage.getItem('authToken');
      if (!token) return;
      
      const data = await getAllDepartments(token);
      setDepartments(data);
    } catch (error) {
      console.error('Failed to fetch departments:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDepartments();
  }, []);

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this department?')) return;
    
    try {
      const token = localStorage.getItem('authToken');
      if (!token) return;
      
      await deleteDepartment(token, id);
      fetchDepartments(); // Refresh the list
    } catch (error) {
      console.error('Failed to delete department:', error);
    }
  };

  if (loading) return <div className="loading">Loading departments...</div>;

  return (
    <div className="departments-container">
      <h1>Departments</h1>
      
      <table className="departments-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Date Created</th>
            <th>Members</th>
            <th>Documents</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {departments.map(dept => (
            <tr key={dept.id}>
              <td>{dept.name}</td>
              <td>{new Date(dept.createdAt).toLocaleDateString()}</td>
              <td>{dept.userCount}</td>
              <td>{dept.documentCount}</td>
              <td className="actions">
                {isSuperAdmin && (
                  <button 
                    className="icon-button" 
                    onClick={() => handleDelete(dept.id)}
                    title="Delete department"
                  >
                    <FiTrash2 />
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {departments.length === 0 && (
        <div className="no-results">No departments found</div>
      )}

      {isSuperAdmin && (
        <>
          <button 
            className="floating-add-button"
            onClick={() => setShowAddModal(true)}
            title="Add new department"
          >
            <FiPlus size={24} />
          </button>
          
          {showAddModal && (
            <AddDepartmentModal
              onClose={() => setShowAddModal(false)}
              onSuccess={fetchDepartments}
            />
          )}
        </>
      )}
    </div>
  );
}