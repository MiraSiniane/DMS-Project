// src/types/User.ts
export interface User {
  id: number;
  name: string;
  email: string;
  phone: string;
  address: string;
  position?: string;
  role: {
    id: number;
    name: 'SUPERADMIN' | 'ADMIN' | 'USER';
  };
  status: 'active' | 'inactive';
  lastLogin?: string;
  departments?: { id: number; name: string }[];
}