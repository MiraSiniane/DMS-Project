// src/services/authService.ts
const API_BASE_URL = 'http://localhost:8080/api/auth';

export async function login(email: string, password: string) {
  const response = await fetch(`${API_BASE_URL}/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ email, password }),
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Login failed');
  }

  return await response.json();
}

export async function registerSuperadmin(name: string, email: string, password: string) {
  const response = await fetch(`${API_BASE_URL}/register-superadmin`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ name, email, password, role: 'SUPERADMIN' }),
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Registration failed');
  }

  return await response.json();
}

export async function checkServerStatus() {
  try {
    const response = await fetch(`${API_BASE_URL}/login`, {
      method: 'OPTIONS',
    });
    return response.ok;
  } catch (error) {
    console.error('Server connection error:', error);
    return false;
  }
}

export async function getUserInfo(token: string) {
  const response = await fetch(`${API_BASE_URL}/user-info`, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json' // Ensure this is included
    },
    credentials: 'include' // Important for cookies if using them
  });

  if (!response.ok) {
    // Improved error handling
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || `HTTP ${response.status}: Failed to fetch user info`);
  }

  return await response.json();
}

export async function updateUserStatus(token: string, status: 'active' | 'inactive') {
  const response = await fetch(`${API_BASE_URL}/update-status`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ status }) // Send as JSON body
  });

  if (!response.ok) {
    const error = await response.text();
    throw new Error(error);
  }
  return await response.text();
}

export async function updatePassword(
  token: string, 
  oldPassword: string, 
  newPassword: string
) {
  const response = await fetch(`${API_BASE_URL}/update-password`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ oldPassword, newPassword })
  });

  if (!response.ok) {
    const error = await response.text();
    throw new Error(error);
  }
  return await response.text();
}

export async function createUser(token: string, userData: {
  name: string;
  email: string;
  password: string;
  role: string;
  position?: string;
  departmentIds: number[] ;
}) {
  const response = await fetch(`${API_BASE_URL}/admin/create-user`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(userData)
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Failed to create user');
  }

  return await response.json();
}