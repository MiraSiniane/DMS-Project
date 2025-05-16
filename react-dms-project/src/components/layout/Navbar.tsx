// src/components/Navbar.tsx
import { useState } from 'react';
import { useAuth } from '../../hooks/useAuth';
import Drawer from './Drawer';
import '../css/Navbar.css';

export default function Navbar() {
  const { logout } = useAuth();
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);

  return (
    <>
      <nav className="navbar">
        <div className="navbar-container">
          <button 
            className="menu-btn"
            onClick={() => setIsDrawerOpen(true)}
          >
            ☰
          </button>
          <span className="navbar-brand">My App</span>
          <button onClick={logout} className="navbar-logout">
            Logout
          </button>
        </div>
      </nav>
      
      <Drawer 
        isOpen={isDrawerOpen} 
        onClose={() => setIsDrawerOpen(false)} 
      />
    </>
  );
}