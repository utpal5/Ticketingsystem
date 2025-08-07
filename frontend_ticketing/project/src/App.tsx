import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import MainLayout from './components/Layout/MainLayout';
import LoginForm from './components/Auth/LoginForm';
import Dashboard from './pages/Dashboard';
import MyTickets from './pages/MyTickets';
import AssignedTickets from './pages/AssignedTickets';
import AllTickets from './pages/AllTickets';
import CreateTicketForm from './components/Tickets/CreateTicketForm';
import TicketDetail from './components/Tickets/TicketDetail';
import UserManagement from './components/Admin/UserManagement';
import Signup from './pages/Signup';

const AppContent: React.FC = () => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!user) {
    return <LoginForm />;
  }

  return (
    <Routes>
      <Route path="/" element={<MainLayout />}>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="my-tickets" element={<MyTickets />} />
        <Route path="create-ticket" element={<CreateTicketForm />} />
        <Route path="tickets/:id" element={<TicketDetail />} />
        
        <Route 
          path="assigned-tickets" 
          element={
            <ProtectedRoute roles={['SUPPORT_AGENT', 'ADMIN']}>
              <AssignedTickets />
            </ProtectedRoute>
          } 
        />
        
        <Route 
          path="all-tickets" 
          element={
            <ProtectedRoute roles={['ADMIN']}>
              <AllTickets />
            </ProtectedRoute>
          } 
        />
        
        <Route 
          path="users" 
          element={
            <ProtectedRoute roles={['ADMIN']}>
              <UserManagement />
            </ProtectedRoute>
          } 
        />
      </Route>
      
      <Route path="/login" element={<Navigate to="/dashboard" replace />} />
      <Route path="/signup" element={<Signup />} />
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <AppContent />
      </Router>
    </AuthProvider>
  );
}

export default App;