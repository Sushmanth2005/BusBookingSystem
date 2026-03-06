import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import { useEffect, useState } from 'react';
import api from './lib/axios';

import Login from './pages/Login';
import Register from './pages/Register';
import SearchSchedules from './pages/SearchSchedules';
import MyBookings from './pages/MyBookings';
import Booking from './pages/Booking';
import AdminDashboard from './pages/AdminDashboard';

import { BusFront, User as UserIcon, Bell, X } from 'lucide-react';
import { Link } from 'react-router-dom';

interface NotificationData {
  id: number;
  message: string;
  type: string;
  isRead: boolean;
  createdAt: string;
}

const Navigation = () => {
  const { user, isAdmin, logout } = useAuth();
  const [showNotif, setShowNotif] = useState(false);
  const [notifications, setNotifications] = useState<NotificationData[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);

  const fetchUnread = async () => {
    try {
      const res = await api.get('/notifications/unread-count');
      setUnreadCount(res.data.count);
    } catch (_) { /* silent */ }
  };

  useEffect(() => {
    if (user) {
      // eslint-disable-next-line react-hooks/set-state-in-effect
      fetchUnread();
      const interval = setInterval(fetchUnread, 30000);
      return () => clearInterval(interval);
    }
  }, [user]);

  const openNotifications = async () => {
    setShowNotif(true);
    try {
      const res = await api.get('/notifications');
      setNotifications(res.data);
    } catch (_) { /* silent */ }
  };

  const markAllRead = async () => {
    try {
      await api.put('/notifications/read-all');
      setUnreadCount(0);
      setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
    } catch (_) { /* silent */ }
  };

  return (
    <>
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

                {/* Notification Bell */}
                <button onClick={openNotifications} className="relative text-slate-500 hover:text-indigo-600 transition p-1">
                  <Bell size={20} />
                  {unreadCount > 0 && (
                    <span className="absolute -top-1 -right-1 bg-red-500 text-white text-[10px] font-bold w-5 h-5 rounded-full flex items-center justify-center shadow-md">
                      {unreadCount > 9 ? '9+' : unreadCount}
                    </span>
                  )}
                </button>

                <div className="flex items-center gap-3 ml-2 pl-4 border-l border-slate-200">
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

      {/* Notification Panel */}
      {showNotif && (
        <div className="fixed inset-0 z-[60] flex justify-end" onClick={() => setShowNotif(false)}>
          <div className="absolute inset-0 bg-black/20 backdrop-blur-sm" />
          <div className="relative w-full max-w-md bg-white shadow-2xl h-full overflow-y-auto animate-in slide-in-from-right"
            onClick={e => e.stopPropagation()}>
            <div className="sticky top-0 bg-white border-b border-slate-100 p-4 flex justify-between items-center z-10">
              <h3 className="text-lg font-bold text-slate-800 flex items-center gap-2">
                <Bell size={18} className="text-indigo-500" /> Notifications
              </h3>
              <div className="flex items-center gap-3">
                {unreadCount > 0 && (
                  <button onClick={markAllRead} className="text-xs bg-indigo-50 text-indigo-600 font-bold px-3 py-1.5 rounded-lg hover:bg-indigo-100 transition">
                    Mark all read
                  </button>
                )}
                <button onClick={() => setShowNotif(false)} className="text-slate-400 hover:text-red-500 transition">
                  <X size={20} />
                </button>
              </div>
            </div>
            <div className="p-4 space-y-3">
              {notifications.length === 0 ? (
                <div className="text-center py-16 text-slate-400">
                  <Bell size={40} className="mx-auto mb-4 opacity-30" />
                  <p className="font-medium">No notifications yet</p>
                </div>
              ) : (
                notifications.map(n => (
                  <div key={n.id} className={`p-4 rounded-xl border transition-colors ${n.isRead ? 'bg-white border-slate-100' : 'bg-indigo-50 border-indigo-100'}`}>
                    <div className="flex items-start gap-3">
                      <div className={`w-2 h-2 rounded-full mt-2 shrink-0 ${n.isRead ? 'bg-slate-300' : 'bg-indigo-500'}`} />
                      <div>
                        <p className="text-sm text-slate-700 leading-relaxed">{n.message}</p>
                        <p className="text-xs text-slate-400 mt-2">{new Date(n.createdAt).toLocaleString()}</p>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      )}
    </>
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
