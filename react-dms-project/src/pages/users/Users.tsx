// src/pages/Users.tsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiEye, FiEdit, FiTrash2, FiSearch } from 'react-icons/fi';
import { getAllUsers } from '../../services/userService';
import { subMinutes, subHours, subDays, subWeeks, subMonths, subYears } from 'date-fns';
import '../css/Users.css';
import SearchBar from '../../components/users/UserSearchBar';
import UserFilters from '../../components/users/UserFilters';
import AddUserButton from '../../components/users/AddUserButton';
import { useAuth } from '../../hooks/useAuth';
import type { User } from '../../types/User';

const truncateText = (text: string, maxLength: number = 10) => {
  if (!text) return '-';
  return text.length > maxLength ? `${text.substring(0, maxLength)}...` : text;
};

export default function Users() {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [filteredUsers, setFilteredUsers] = useState<User[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [filters, setFilters] = useState({
    role: '',
    position: '',
    department: '',
    status: '',
    lastLogin: ''
  });
  const { currentUser } = useAuth();
  const {isSuperAdmin} = useAuth();
  const{isAdmin} = useAuth();
  const navigate = useNavigate();
  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const token = localStorage.getItem('authToken');
        if (!token) return;
        
        const allUsers = await getAllUsers(token);
        setUsers(allUsers);
        setFilteredUsers(allUsers);
      } catch (error) {
        console.error('Failed to fetch users:', error);
        setUsers([]); 
        setFilteredUsers([]);
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, []);

  const applyFilters = (users: User[], query: string, filters: any) => {
    return users.filter(user => {
      const matchesSearch = 
        user.name.toLowerCase().includes(query) ||
        user.email.toLowerCase().includes(query) ||
        user.role.name.toLowerCase().includes(query) ||
        (user.position && user.position.toLowerCase().includes(query)) ||
        (user.departments && user.departments.some(dept => 
          dept.name.toLowerCase().includes(query)));

      const matchesRole = !filters.role || user.role.name === filters.role;
      const matchesStatus = !filters.status || user.status === filters.status;
      const matchesPosition = !filters.position || 
        (user.position && user.position.toLowerCase().includes(filters.position.toLowerCase()));
      const matchesDepartment = !filters.department ||
        (user.departments && user.departments.some(dept => 
          dept.name.toLowerCase().includes(filters.department.toLowerCase())));
      
      let matchesLastLogin = true;
      if (filters.lastLogin && user.lastLogin) {
        const loginDate = new Date(user.lastLogin);
        const now = new Date();
        
        switch(filters.lastLogin) {
          case '30m': matchesLastLogin = loginDate > subMinutes(now, 30); break;
          case '1h': matchesLastLogin = loginDate > subHours(now, 1); break;
          case '24h': matchesLastLogin = loginDate > subDays(now, 1); break;
          case '1w': matchesLastLogin = loginDate > subWeeks(now, 1); break;
          case '1m': matchesLastLogin = loginDate > subMonths(now, 1); break;
          case '3m': matchesLastLogin = loginDate > subMonths(now, 3); break;
          case '1y': matchesLastLogin = loginDate > subYears(now, 1); break;
        }
      }
      
      return matchesSearch && matchesRole && matchesStatus && 
             matchesPosition && matchesDepartment && matchesLastLogin;
    });
  };

  const handleSearch = (query: string) => {
    setSearchQuery(query.toLowerCase());
    setFilteredUsers(applyFilters(users, query.toLowerCase(), filters));
  };

  const handleFilterChange = (newFilters: any) => {
    setFilters(newFilters);
    setFilteredUsers(applyFilters(users, searchQuery, newFilters));
  };

  const canEditUser = (targetUser: User) => {
    if (!currentUser) return false;
    
    const targetRole = targetUser.role.name;
    const currentRole = currentUser.role.name;
    
    if (currentRole === 'SUPERADMIN') return true;
    if (currentRole === 'ADMIN' && targetRole === 'USER') return true;
    return false;
  };

  const formatRole = (role: string) => {
    switch (role) {
      case 'SUPERADMIN': return 'Super Admin';
      case 'ADMIN': return 'Admin';
      case 'USER': return 'User';
      default: return role;
    }
  };

  const formatLastLogin = (dateString?: string) => {
    if (!dateString) return 'Never';
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  if (loading) return <div className="loading">Loading users...</div>;

  return (
    <div className="users-container">
      <h1>Users</h1>
      
      <div className="controls">
        <SearchBar onSearch={handleSearch} />
        <UserFilters onFilter={handleFilterChange} />
      </div>

      {filteredUsers.length === 0 && users.length > 0 ? (
        <div className="no-results">
          <FiSearch size={24} />
          <p>No matching users found</p>
        </div>
      ) : filteredUsers.length === 0 ? (
        <div className="no-results">
          <p>No users available</p>
        </div>
      ) : (
        <table className="users-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Role</th>
              <th>Position</th>
              <th>Departments</th>
              <th>Last Login</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredUsers.map((user) => (
              <tr key={user.id}>
                <td>{truncateText(user.name)}</td>
                <td>{truncateText(user.role.name)}</td>
                <td>{user.position || '-'}</td>
                <td title={user.departments?.map(dept => dept.name).join(', ') || ''}>
                  {user.departments?.length 
                    ? truncateText(user.departments.map(dept => dept.name).join(', ')) 
                    : '-'}
                </td>
                <td>{formatLastLogin(user.lastLogin)}</td>
                <td>
                  <span className={`status-dot ${user.status}`} />
                  {user.status.charAt(0).toUpperCase() + user.status.slice(1)}
                </td>
                <td className="actions">
                  <button 
                    className="icon-button" 
                    onClick={() => navigate(`/users/${user.id}`)}
                  >
                    <FiEye className="icon" title="View" />
                  </button>
                  <button 
                    className={`icon-button ${canEditUser(user) ? '' : 'disabled'}`}
                    onClick={() => canEditUser(user) && navigate(`/users/edit/${user.id}`)}
                    disabled={!canEditUser(user)}
                    title={canEditUser(user) ? 'Edit user' : 'No permission to edit'}
                  >
                    <FiEdit className="icon" />
                  </button>
                  <button className="icon-button" onClick={() => console.log('Delete', user.id)}>
                    <FiTrash2 className="icon" title="Delete" />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        
      )}
      {(isAdmin||isSuperAdmin)? (<AddUserButton/>) : (<></>)}
      
    </div>
  );
}