const API_URL = 'http://localhost:5000/api/files';

// Get all files
export const getFiles = async (folderId = null) => {
  try {
    const response = await fetch(`${API_URL}?folderId=${folderId || ''}`);
    if (!response.ok) throw new Error('Network response was not ok');
    return await response.json();
  } catch (error) {
    console.error('Error fetching files:', error);
    throw error;
  }
};

// Get file details
export const getFileDetails = async (fileId) => {
  try {
    const response = await fetch(`${API_URL}/${fileId}`);
    if (!response.ok) throw new Error('Network response was not ok');
    return await response.json();
  } catch (error) {
    console.error('Error fetching file details:', error);
    throw error;
  }
};

// Upload file
export const uploadFile = async (file, folderId = null) => {
  const formData = new FormData();
  formData.append('file', file);
  if (folderId) formData.append('folderId', folderId);

  try {
    const response = await fetch(API_URL, {
      method: 'POST',
      body: formData,
    });
    if (!response.ok) throw new Error('Network response was not ok');
    return await response.json();
  } catch (error) {
    console.error('Error uploading file:', error);
    throw error;
  }
};

// Create folder
export const createFolder = async (name, parentFolderId = null) => {
  try {
    const response = await fetch(`${API_URL}/folders`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ name, parentFolderId }),
    });
    if (!response.ok) throw new Error('Network response was not ok');
    return await response.json();
  } catch (error) {
    console.error('Error creating folder:', error);
    throw error;
  }
};

// Delete file
export const deleteFile = async (fileId) => {
  try {
    const response = await fetch(`${API_URL}/${fileId}`, {
      method: 'DELETE',
    });
    if (!response.ok) throw new Error('Network response was not ok');
    return await response.json();
  } catch (error) {
    console.error('Error deleting file:', error);
    throw error;
  }
};

// Record file access
export const recordFileAccess = async (fileId, userId, actionType = 'view') => {
  try {
    const response = await fetch(`${API_URL}/${fileId}/access`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify({ userId, action: actionType })
    });
    return await response.json();
  } catch (error) {
    console.error('Error recording access:', error);
    throw error;
  }
};

// Get file access history
export const getFileAccessHistory = async (fileId) => {
  try {
    const response = await fetch(`${API_URL}/${fileId}/visits`, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });
    if (!response.ok) throw new Error(await response.text());
    return await response.json();
  } catch (error) {
    console.error('Error fetching access history:', error);
    throw error;
  }
};

// Get recent activities
export const getRecentActivities = async (limit = 20) => {
  try {
    const response = await fetch(`${API_URL}/activity/recent?limit=${limit}`, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });
    if (!response.ok) throw new Error(await response.text());
    return await response.json();
  } catch (error) {
    console.error('Error fetching recent activities:', error);
    throw error;
  }
};

// Get file preview url
export const getFilePreviewUrl = (fileId) => {
  return `${API_URL}/preview/${fileId}?token=${localStorage.getItem('token')}`;
};

// Get file download url
export const getFileDownloadUrl = (fileId) => {
  return `${API_URL}/download/${fileId}?token=${localStorage.getItem('token')}`;
};