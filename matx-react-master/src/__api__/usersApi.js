const API_URL = 'http://localhost:5000/api/users';

// Get all users
export const getUsers = async () => {
  try {
    const response = await fetch(API_URL, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });
    if (!response.ok) throw new Error(await response.text());
    return await response.json();
  } catch (error) {
    console.error('Error fetching users:', error);
    throw error;
  }
};

// Get user by id
export const getUser = async (userId) => {
  try {
    const response = await fetch(`${API_URL}/${userId}`, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });
    if (!response.ok) throw new Error(await response.text());
    return await response.json();
  } catch (error) {
    console.error('Error fetching user:', error);
    throw error;
  }
};

// Create new user
export const createUser = async (userData) => {
  try {
    const response = await fetch(API_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify(userData)
    });
    if (!response.ok) throw new Error(await response.text());
    return await response.json();
  } catch (error) {
    console.error('Error creating user:', error);
    throw error;
  }
};

// Update user
export const updateUser = async (userId, updates) => {
  try {
    const response = await fetch(`${API_URL}/${userId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify(updates)
    });
    if (!response.ok) throw new Error(await response.text());
    return await response.json();
  } catch (error) {
    console.error('Error updating user:', error);
    throw error;
  }
};

// Delete user
export const deleteUser = async (userId) => {
  try {
    const response = await fetch(`${API_URL}/${userId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });
    if (!response.ok) throw new Error(await response.text());
    return await response.json();
  } catch (error) {
    console.error('Error deleting user:', error);
    throw error;
  }
};

// Check admin password (temporary)
export const checkAdminPassword = async (password) => {
    try {
        // In a real app, you'd verify this server-side
        return password === "adminpassword";
    } catch (error) {
        console.error('Password verification failed:', error);
        throw error;
    }
};

// Get user activity
export const getUserActivity = async (userId) => {
    try {
      const response = await fetch(`${API_URL}/${userId}/activity`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      if (!response.ok) throw new Error(await response.text());
      return await response.json();
    } catch (error) {
      console.error('Error fetching user activity:', error);
      throw error;
    }
  };