import type { User } from "../types/User";

// src/services/userService.ts
const API_BASE_URL = 'http://localhost:8080/api/users';


export interface UserUpdateData {
  name?: string;
  email?: string;
  position?: string;
  address?: string;
  phone?: string;
  departmentIds?: number[];
}

export async function updateUser(
  token: string,
  userId: number,
  userData: UserUpdateData
): Promise<User> {
  const response = await fetch(`${API_BASE_URL}/${userId}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(userData)
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Failed to update user');
  }

  return await response.json();
}


export async function getAllUsers(token: string): Promise<User[]> {
  const response = await fetch(`${API_BASE_URL}/getallusers`, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });

  if (!response.ok) {
    throw new Error('Failed to fetch users');
  }

  return await response.json();
}


// src/services/userService.ts
export async function getUser(token: string, userId: number): Promise<User> {
  const response = await fetch(`${API_BASE_URL}/${userId}`, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });

  if (!response.ok) {
    throw new Error('Failed to fetch user');
  }

  return await response.json();
}