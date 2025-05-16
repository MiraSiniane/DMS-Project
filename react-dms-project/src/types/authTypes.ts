// src/types/authTypes.ts
export interface User {
  id: number;
  name: string;
  email: string;
  role: {
    name: 'SUPERADMIN' | 'ADMIN' | 'USER';
  };
  position?: string;
  lastLogin?: string;
  status: 'active' | 'inactive';
  departments?: { name: string }[];
}

export interface AuthResponse {
  token: string;
  email: string;
  role: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface SignupRequest {
  name: string;
  email: string;
  password: string;
  role?: string;
  position?: string;
}