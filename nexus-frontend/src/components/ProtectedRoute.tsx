import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

interface ProtectedRouteProps {
  allowedRoles?: string[];
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ allowedRoles }) => {
  const { user, isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  // Agar user logged in nahi hai, toh Login page par bhejo
  if (!isAuthenticated || !user) {
    return <Navigate to="/login" replace />;
  }

  // Agar role-based protection hai aur user ke paas required role nahi hai
  if (allowedRoles && !allowedRoles.some(role => user.roles.includes(role))) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-slate-50 text-slate-800 p-6 text-center">
        <h2 className="text-2xl font-bold text-red-600">403 - Access Denied</h2>
        <p className="mt-2 text-slate-600">You do not have permission to view this section.</p>
      </div>
    );
  }

  return <Outlet />;
};