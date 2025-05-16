// src/App.tsx
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import PrivateRoute from './components/routes/PrivateRoute';
import AuthLayout from './components/auth/AuthLayout';
import Navbar from './components/layout/Navbar';
import Home from './pages/Home';
import Login from './pages/auth/Login';
import Signup from './pages/auth/Signup';
import ErrorBoundary from './ErrorBoundary';
import Users from './pages/users/Users';
import Departments from './pages/departments/Department';
import EditUserPage from './pages/users/EditUserPage';
import UserDetails from './pages/users/UserDetails';

export default function App() {
  return (
    <Router>
      <ErrorBoundary>
      <Routes>
        <Route element={<AuthLayout />}>
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
        </Route>
        <Route
          path="/home"
          element={
            <>
              <Navbar />
              <PrivateRoute>
                <Home />
              </PrivateRoute>
            </>
          }
        />
        <Route 
          path="/users" 
          element={
            <>
              <Navbar />
              <PrivateRoute>
                <Users />
              </PrivateRoute>
            </>
          } 
        />
        <Route path="/departments" element={
          <>
            <Navbar />
            <PrivateRoute>
              <Departments />
            </PrivateRoute>
          </>
          } 
        />
        <Route path="/users/edit/:id" element={
          <>
            <Navbar/>
            <PrivateRoute>
              <EditUserPage />
            </PrivateRoute>
          </>
        } 
        />
        <Route path="/users/:id" element={
          <>
            <Navbar/>
            <UserDetails />
          </>
        } 
        />
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
      </ErrorBoundary>
    </Router>
  );
}