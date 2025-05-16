// src/services/departmentService.ts
const API_BASE_URL = 'http://localhost:8080/api';

export interface Department {
  id: number;
  name: string;
  createdAt: string;
  userCount: number;
  documentCount: number;
}

export async function getAllDepartments(token: string): Promise<Department[]> {
  const response = await fetch(`${API_BASE_URL}/departments/getalldepartments`, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });

  if (!response.ok) {
    throw new Error('Failed to fetch departments');
  }

  return await response.json();
}

export async function createDepartment(token: string, name: string): Promise<Department> {
  const response = await fetch(`${API_BASE_URL}/departments`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: name
  });

  if (!response.ok) {
    throw new Error('Failed to create department');
  }

  return await response.json();
}

export async function deleteDepartment(token: string, id: number): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/departments/${id}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });

  if (!response.ok) {
    throw new Error('Failed to delete department');
  }
}

// src/services/departmentService.ts

export interface DepartmentAssignment {
  userId: number;
  departmentId: number;
}

export async function assignDepartment(
  token: string,
  assignment: DepartmentAssignment
): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/departments/assign`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      userId: assignment.userId,
      departmentId: assignment.departmentId
    })
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Failed to assign department');
  }
}

export async function unassignDepartment(
  token: string,
  assignment: DepartmentAssignment
): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/departments/unassign`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      userId: assignment.userId,
      departmentId: assignment.departmentId
    })
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Failed to unassign department');
  }
}