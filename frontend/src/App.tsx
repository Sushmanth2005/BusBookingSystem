import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';

import Login from './pages/Login';
import Register from './pages/Register';
import SearchSchedules from './pages/SearchSchedules';
import MyBookings from './pages/MyBookings';
import Booking from './pages/Booking';
import AdminDashboard from './pages/AdminDashboard';

import { BusFront, User as UserIcon } from 'lucide-react';

import { Link } from 'react-router-dom';

const Navigation = () => {
  const { user, isAdmin, logout } = useAuth();
  return (
    <nav className="sticky top-0 z-50 glass-panel !rounded-none !border-x-0 !border-t-0 px-6 py-4 mb-8">
      <div className="container mx-auto flex justify-between items-center">
        <Link to="/" className="flex items-center gap-2 text-indigo-700">
          <BusFront size={28} strokeWidth={2.5} />
          <span className="font-extrabold text-2xl tracking-tight bg-clip-text text-transparent bg-gradient-to-r from-blue-700 to-indigo-700">
            BusEase
          </span>
        </Link>
        <div className="flex gap-6 items-center font-medium">
          {user ? (
            <>
              <Link to="/search" className="text-slate-600 hover:text-indigo-600 transition">Search</Link>
              <Link to="/bookings" className="text-slate-600 hover:text-indigo-600 transition">My Bookings</Link>
              {isAdmin && <Link to="/admin" className="text-amber-500 font-bold hover:text-amber-600 transition">Admin Panel</Link>}
              <div className="flex items-center gap-3 ml-4 pl-4 border-l border-slate-200">
                <span className="text-sm font-semibold flex items-center gap-2 text-slate-700">
                  <UserIcon size={16} className="text-indigo-500" />
                  {user.sub.split('@')[0]}
                </span>
                <button onClick={logout} className="text-sm text-slate-500 hover:text-red-500 transition font-semibold">Logout</button>
              </div>
            </>
          ) : (
            <Link to="/login" className="btn-primary !py-2 !px-5 !text-sm">Log In</Link>
          )}
        </div>
      </div>
    </nav>
  );
};

// Protected Route Component
const ProtectedRoute = ({ children, requireAdmin = false }: { children: React.ReactNode, requireAdmin?: boolean }) => {
  const { user, isAdmin } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (requireAdmin && !isAdmin) return <Navigate to="/" replace />;
  return children;
};

export default function App() {
  return (
    <div className="min-h-screen bg-gray-50">
      <Navigation />
      <main className="container mx-auto p-4 mt-4">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/" element={<Navigate to="/search" replace />} />
          <Route path="/search" element={
            <ProtectedRoute>
              <SearchSchedules />
            </ProtectedRoute>
          } />
          <Route path="/book/:scheduleId" element={
            <ProtectedRoute>
              <Booking />
            </ProtectedRoute>
          } />
          <Route path="/bookings" element={
            <ProtectedRoute>
              <MyBookings />
            </ProtectedRoute>
          } />
          <Route path="/admin/*" element={
            <ProtectedRoute requireAdmin={true}>
              <AdminDashboard />
            </ProtectedRoute>
          } />
        </Routes>
      </main>
    </div>
  );
}
