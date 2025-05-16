// src/components/Drawer.tsx
import { Link } from 'react-router-dom';
import '../css/Drawer.css';

interface DrawerProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function Drawer({ isOpen, onClose }: DrawerProps) {
  return (
    <>
      {/* Overlay that closes drawer when clicked */}
      {isOpen && (
        <div className="drawer-overlay" onClick={onClose} />
      )}
      
      {/* Drawer container */}
      <div className={`drawer ${isOpen ? 'open' : ''}`}>
        <div className="drawer-header">
          <h3>Menu</h3>
          <button onClick={onClose} className="close-btn">
            &times;
          </button>
        </div>
        
        <nav className="drawer-nav">
          <ul>
            <li>
              <Link to="#" onClick={onClose}>Dashboard</Link>
            </li>
            <li>
              <Link to="/Home" onClick={onClose}>Profile</Link>
            </li>
            <li>
              <Link to="/users" onClick={onClose}>Users</Link>
            </li>
            <li>
              <Link to="#" onClick={onClose}>Documents</Link>
            </li>
            <li>
              <Link to="/departments" onClick={onClose}>Departments</Link>
            </li>
            <li>
              <Link to="#" onClick={onClose}>Settings</Link>
            </li>
          </ul>
        </nav>
      </div>
    </>
  );
}