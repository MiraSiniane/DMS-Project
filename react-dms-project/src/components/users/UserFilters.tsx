// src/components/UserFilters.tsx
import { type ChangeEvent, useState } from 'react';
import '../css/UserFilters.css'
interface UserFiltersProps {
  onFilter: (filters: {
    role: string;
    position: string;
    department: string;
    status: string;
    lastLogin: string;
  }) => void;
}

export default function UserFilters({ onFilter }: UserFiltersProps) {
  const [filters, setFilters] = useState({
    role: '',
    position: '',
    department: '',
    status: '',
    lastLogin: ''
  });

  const handleChange = (e: ChangeEvent<HTMLSelectElement | HTMLInputElement>) => {
    const newFilters = {
      ...filters,
      [e.target.name]: e.target.value
    };
    setFilters(newFilters);
    onFilter(newFilters);
  };

  return (
    <div className="user-filters">
      <select name="role" value={filters.role} onChange={handleChange}>
        <option value="">All Roles</option>
        <option value="SUPERADMIN">Super Admin</option>
        <option value="ADMIN">Admin</option>
        <option value="USER">User</option>
      </select>

      <select name="status" value={filters.status} onChange={handleChange}>
        <option value="">All Statuses</option>
        <option value="active">Active</option>
        <option value="inactive">Inactive</option>
      </select>

      <select name="position" value={filters.position} onChange={handleChange}>
        <option value="">All Positions</option>
        <option value="System Owner">System Owner</option>
        <option value="Frontend Developer">Frontend Developer</option>
        <option value="Backend Developer">Backend Developer</option>
      </select>

      <select name="department" value={filters.department} onChange={handleChange}>
        <option value="">All Department</option>
        <option value="Direction">Direction</option>
        <option value="Finance">Finance</option>
        <option value="Human Ressource">Human Ressource</option>
      </select>

      <select name="lastLogin" value={filters.lastLogin} onChange={handleChange}>
        <option value="">Any Last Login</option>
        <option value="30m">Last 30 minutes</option>
        <option value="1h">Last 1 hour</option>
        <option value="24h">Last 24 hours</option>
        <option value="1w">Last 1 week</option>
        <option value="1m">Last 1 month</option>
        <option value="3m">Last 3 months</option>
        <option value="1y">Last 1 year</option>
      </select>
    </div>
  );
}