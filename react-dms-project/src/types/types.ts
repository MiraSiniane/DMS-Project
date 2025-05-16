// src/types.ts
export interface User {
  id: number;
  name: string;
  email: string;
  position?: string;
  role: {
    id: number;
    name: string;
  };
  status: string;
  lastLogin?: string;
  // Add other fields as needed
}