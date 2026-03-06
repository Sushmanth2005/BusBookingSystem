import { useEffect, useState } from 'react';
import api from '../lib/axios';
import { Bus, CalendarClock, Plus, X, Trash2, BarChart3, Users, IndianRupee, TrendingUp, Ticket } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, Legend } from 'recharts';

interface BusData {
    id: number;
    busName: string;
    totalSeats: number;
    type: string;
}

interface ScheduleData {
    id: number;
    busName: string;
    source: string;
    destination: string;
    departureTime: string;
    price: number;
}

interface BookingData {
    id: number;
    userName: string;
    source: string;
    destination: string;
    busName: string;
    departureTime: string;
    seatNumbers: string[];
    status: string;
    totalAmount: number;
    bookingDate: string;
    passengerName: string;
}

interface SummaryData {
    totalBookings: number;
    confirmedBookings: number;
    cancelledBookings: number;
    totalRevenue: number;
    totalBuses: number;
    totalUsers: number;
}

interface RouteBookingData {
    route: string;
    bookings: number;
}

const TABS = ['Overview', 'Buses', 'Bookings', 'Schedules'] as const;
type Tab = typeof TABS[number];

const CHART_COLORS = ['#6366f1', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899', '#06b6d4', '#f97316'];

export default function AdminDashboard() {
    const [activeTab, setActiveTab] = useState<Tab>('Overview');
    const [buses, setBuses] = useState<BusData[]>([]);
    const [schedules, setSchedules] = useState<ScheduleData[]>([]);
    const [allBookings, setAllBookings] = useState<BookingData[]>([]);
    const [summary, setSummary] = useState<SummaryData | null>(null);
    const [routeBookings, setRouteBookings] = useState<RouteBookingData[]>([]);

    // Modals
    const [showBusModal, setShowBusModal] = useState(false);
    const [showDeleteConfirm, setShowDeleteConfirm] = useState<number | null>(null);
    const [newBusName, setNewBusName] = useState('');
    const [newBusCapacity, setNewBusCapacity] = useState(40);
    const [newBusType, setNewBusType] = useState('AC');
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => { fetchData(); }, []);

    const fetchData = async () => {
        try {
            const [busRes, schedRes, summaryRes, routeRes, bookingsRes] = await Promise.all([
                api.get('/admin/buses'),
                api.get('/admin/schedules'),
                api.get('/admin/analytics/summary'),
                api.get('/admin/analytics/bookings-by-route'),
                api.get('/admin/analytics/bookings-all'),
            ]);
            setBuses(busRes.data);
            setSchedules(schedRes.data);
            setSummary(summaryRes.data);
            setRouteBookings(routeRes.data);
            setAllBookings(bookingsRes.data);
        } catch (_) {
            console.error('Failed to load admin data');
        }
    };

    const handleAddBus = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);
        try {
            await api.post('/admin/buses', { busName: newBusName, totalSeats: newBusCapacity, type: newBusType });
            setShowBusModal(false);
            setNewBusName('');
            setNewBusCapacity(40);
            fetchData();
        } catch (_) {
            alert('Failed to add bus');
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleDeleteBus = async (busId: number) => {
        setIsSubmitting(true);
        try {
            await api.delete(`/admin/buses/${busId}`);
            setShowDeleteConfirm(null);
            fetchData();
        } catch (_) {
            alert('Failed to remove bus. It may have active dependencies.');
        } finally {
            setIsSubmitting(false);
        }
    };

    const pieData = summary ? [
        { name: 'Confirmed', value: summary.confirmedBookings },
        { name: 'Cancelled', value: summary.cancelledBookings },
    ].filter(d => d.value > 0) : [];

    return (
        <div className="space-y-6 max-w-7xl mx-auto pb-12">
            {/* Header */}
            <div className="glass-panel p-8 bg-gradient-to-r from-slate-800 to-indigo-900 border-none !shadow-2xl">
                <h1 className="text-4xl font-extrabold text-white tracking-tight">Admin Dashboard</h1>
                <p className="text-indigo-200 mt-2 font-medium">Real-time platform analytics and management</p>
            </div>

            {/* Tab Navigation */}
            <div className="flex gap-2 bg-white/80 backdrop-blur-sm rounded-xl p-1.5 shadow-sm border border-slate-200">
                {TABS.map(tab => (
                    <button key={tab} onClick={() => setActiveTab(tab)}
                        className={`flex-1 py-2.5 px-4 rounded-lg font-bold text-sm transition-all ${activeTab === tab
                            ? 'bg-indigo-600 text-white shadow-md' : 'text-slate-600 hover:bg-slate-100'}`}>
                        {tab}
                    </button>
                ))}
            </div>

            {/* ===== OVERVIEW TAB ===== */}
            {activeTab === 'Overview' && summary && (
                <div className="space-y-6">
                    {/* KPI Cards */}
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                        {[
                            { label: 'Total Revenue', value: `₹${summary.totalRevenue.toLocaleString()}`, icon: IndianRupee, color: 'emerald' },
                            { label: 'Bookings', value: summary.totalBookings, icon: Ticket, color: 'indigo' },
                            { label: 'Buses', value: summary.totalBuses, icon: Bus, color: 'amber' },
                            { label: 'Users', value: summary.totalUsers, icon: Users, color: 'blue' },
                        ].map(kpi => (
                            <div key={kpi.label} className="glass-panel p-5 group hover:shadow-lg transition-shadow">
                                <div className={`bg-${kpi.color}-100 text-${kpi.color}-600 p-2 rounded-lg w-fit mb-3`}>
                                    <kpi.icon size={20} />
                                </div>
                                <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">{kpi.label}</p>
                                <p className="text-3xl font-black text-slate-800 mt-1">{kpi.value}</p>
                            </div>
                        ))}
                    </div>

                    {/* Charts Row */}
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                        {/* Bookings by Route Bar Chart */}
                        <div className="glass-panel p-6">
                            <h3 className="text-lg font-bold text-slate-800 mb-4 flex items-center gap-2">
                                <BarChart3 size={20} className="text-indigo-500" /> Bookings by Route
                            </h3>
                            {routeBookings.length > 0 ? (
                                <ResponsiveContainer width="100%" height={300}>
                                    <BarChart data={routeBookings} margin={{ top: 5, right: 20, bottom: 60, left: 0 }}>
                                        <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                                        <XAxis dataKey="route" tick={{ fontSize: 11 }} angle={-35} textAnchor="end" />
                                        <YAxis tick={{ fontSize: 12 }} />
                                        <Tooltip />
                                        <Bar dataKey="bookings" fill="#6366f1" radius={[6, 6, 0, 0]} />
                                    </BarChart>
                                </ResponsiveContainer>
                            ) : (
                                <div className="h-[300px] flex items-center justify-center text-slate-400">No booking data yet</div>
                            )}
                        </div>

                        {/* Booking Status Pie Chart */}
                        <div className="glass-panel p-6">
                            <h3 className="text-lg font-bold text-slate-800 mb-4 flex items-center gap-2">
                                <TrendingUp size={20} className="text-emerald-500" /> Booking Status
                            </h3>
                            {pieData.length > 0 ? (
                                <ResponsiveContainer width="100%" height={300}>
                                    <PieChart>
                                        <Pie data={pieData} cx="50%" cy="50%" innerRadius={60} outerRadius={100} dataKey="value" label>
                                            {pieData.map((_, i) => (
                                                <Cell key={i} fill={CHART_COLORS[i % CHART_COLORS.length]} />
                                            ))}
                                        </Pie>
                                        <Tooltip />
                                        <Legend />
                                    </PieChart>
                                </ResponsiveContainer>
                            ) : (
                                <div className="h-[300px] flex items-center justify-center text-slate-400">No data yet</div>
                            )}
                        </div>
                    </div>
                </div>
            )}

            {/* ===== BUSES TAB ===== */}
            {activeTab === 'Buses' && (
                <div className="glass-panel overflow-hidden">
                    <div className="p-6 border-b border-slate-100 bg-white/50 flex justify-between items-center">
                        <h2 className="text-xl font-bold text-slate-800 flex items-center gap-2">
                            <Bus className="text-indigo-500" /> Fleet Management
                        </h2>
                        <button onClick={() => setShowBusModal(true)}
                            className="bg-indigo-600 hover:bg-indigo-700 text-white px-5 py-2.5 rounded-xl font-bold flex items-center gap-2 transition-all shadow-md text-sm">
                            <Plus size={18} /> Add Bus
                        </button>
                    </div>
                    <div className="overflow-x-auto">
                        <table className="w-full text-left border-collapse">
                            <thead>
                                <tr className="bg-slate-50/80 border-b border-slate-200">
                                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">ID</th>
                                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">Bus Name</th>
                                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">Seats</th>
                                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">Type</th>
                                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest text-right">Actions</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 bg-white/30">
                                {buses.map(b => (
                                    <tr key={b.id} className="hover:bg-indigo-50/50 transition-colors">
                                        <td className="p-4 text-slate-500 font-mono text-sm">#{b.id}</td>
                                        <td className="p-4 font-bold text-slate-700 flex items-center gap-3">
                                            <div className="bg-indigo-100 text-indigo-600 p-2 rounded-lg"><Bus size={16} /></div>
                                            {b.busName}
                                        </td>
                                        <td className="p-4 font-semibold text-slate-800">{b.totalSeats}</td>
                                        <td className="p-4">
                                            <span className={`px-3 py-1 rounded-full text-xs font-bold uppercase ${b.type === 'AC' ? 'bg-blue-100 text-blue-700' : 'bg-orange-100 text-orange-700'}`}>
                                                {b.type}
                                            </span>
                                        </td>
                                        <td className="p-4 text-right">
                                            <button onClick={() => setShowDeleteConfirm(b.id)}
                                                className="text-red-500 hover:bg-red-50 p-2 rounded-lg transition-colors" title="Remove bus">
                                                <Trash2 size={18} />
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* ===== BOOKINGS TAB ===== */}
            {activeTab === 'Bookings' && (
                <div className="glass-panel overflow-hidden">
                    <div className="p-6 border-b border-slate-100 bg-white/50">
                        <h2 className="text-xl font-bold text-slate-800 flex items-center gap-2">
                            <Ticket className="text-indigo-500" /> All Bookings ({allBookings.length})
                        </h2>
                    </div>
                    <div className="overflow-x-auto">
                        <table className="w-full text-left border-collapse">
                            <thead>
                                <tr className="bg-slate-50/80 border-b border-slate-200">
                                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">Booking ID</th>
                                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">Passenger</th>
                                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">Route</th>
                                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">Bus</th>
                                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">Seats</th>
                                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">Status</th>
                                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest text-right">Amount</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 bg-white/30">
                                {allBookings.map(b => (
                                    <tr key={b.id} className="hover:bg-indigo-50/50 transition-colors">
                                        <td className="p-4 font-mono text-sm text-slate-600">#{String(b.id).padStart(6, '0')}</td>
                                        <td className="p-4">
                                            <div className="font-bold text-slate-700">{b.passengerName || b.userName}</div>
                                            <div className="text-xs text-slate-400">(by {b.userName})</div>
                                        </td>
                                        <td className="p-4 font-semibold text-slate-800">{b.source} → {b.destination}</td>
                                        <td className="p-4 text-slate-600">{b.busName}</td>
                                        <td className="p-4">
                                            <div className="flex gap-1 flex-wrap">
                                                {b.seatNumbers.map(s => (
                                                    <span key={s} className="bg-indigo-100 text-indigo-700 px-2 py-0.5 rounded text-xs font-bold">{s}</span>
                                                ))}
                                            </div>
                                        </td>
                                        <td className="p-4">
                                            <span className={`px-3 py-1 rounded-full text-xs font-bold uppercase ${b.status === 'CONFIRMED' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                                                {b.status}
                                            </span>
                                        </td>
                                        <td className="p-4 font-bold text-slate-800 text-right">₹{b.totalAmount.toFixed(2)}</td>
                                    </tr>
                                ))}
                                {allBookings.length === 0 && (
                                    <tr><td colSpan={7} className="p-8 text-center text-slate-500">No bookings yet.</td></tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* ===== SCHEDULES TAB ===== */}
            {activeTab === 'Schedules' && (
                <div className="glass-panel overflow-hidden">
                    <div className="p-6 border-b border-slate-100 bg-white/50">
                        <h2 className="text-xl font-bold text-slate-800 flex items-center gap-2">
                            <CalendarClock className="text-indigo-500" /> Active Schedules ({schedules.length})
                        </h2>
                    </div>
                    <div className="overflow-x-auto">
                        <table className="w-full text-left border-collapse">
                            <thead>
                                <tr className="bg-slate-50/80 border-b border-slate-200">
                                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">Bus</th>
                                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">Route</th>
                                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">Departure</th>
                                    <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest text-right">Price</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 bg-white/30">
                                {schedules.map(s => (
                                    <tr key={s.id} className="hover:bg-indigo-50/50 transition-colors">
                                        <td className="p-4 font-bold text-slate-700">{s.busName}</td>
                                        <td className="p-4 font-semibold text-slate-800">{s.source} → {s.destination}</td>
                                        <td className="p-4">
                                            <div className="font-semibold text-slate-800">{new Date(s.departureTime).toLocaleDateString()}</div>
                                            <div className="text-sm text-slate-500">{new Date(s.departureTime).toLocaleTimeString([], { timeStyle: 'short' })}</div>
                                        </td>
                                        <td className="p-4 font-black text-lg text-slate-800 text-right">₹{s.price.toFixed(2)}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* ===== ADD BUS MODAL ===== */}
            {showBusModal && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 backdrop-blur-sm p-4">
                    <div className="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden">
                        <div className="p-6 border-b border-slate-100 flex justify-between items-center bg-slate-50">
                            <h3 className="text-2xl font-bold flex items-center gap-2 text-slate-800">
                                <div className="bg-indigo-100 text-indigo-600 p-2 rounded-lg"><Bus size={20} /></div>
                                Add New Bus
                            </h3>
                            <button onClick={() => setShowBusModal(false)} className="text-slate-400 hover:text-red-500 p-2 rounded-lg"><X size={20} /></button>
                        </div>
                        <div className="p-6">
                            <form onSubmit={handleAddBus} className="space-y-5">
                                <div>
                                    <label className="block text-sm font-bold text-slate-700 mb-2">Bus Name</label>
                                    <input type="text" required placeholder="e.g. APSRTC Garuda Plus" className="input-field"
                                        value={newBusName} onChange={e => setNewBusName(e.target.value)} />
                                </div>
                                <div>
                                    <label className="block text-sm font-bold text-slate-700 mb-2">Seating Capacity</label>
                                    <select className="input-field" value={newBusCapacity} onChange={e => setNewBusCapacity(Number(e.target.value))}>
                                        <option value="20">20 Seats (Mini)</option>
                                        <option value="30">30 Seats (Standard)</option>
                                        <option value="40">40 Seats (Large)</option>
                                        <option value="52">52 Seats (Mega)</option>
                                    </select>
                                </div>
                                <div>
                                    <label className="block text-sm font-bold text-slate-700 mb-2">Type</label>
                                    <select className="input-field" value={newBusType} onChange={e => setNewBusType(e.target.value)}>
                                        <option value="AC">AC</option>
                                        <option value="NON_AC">Non-AC</option>
                                    </select>
                                </div>
                                <button type="submit" disabled={isSubmitting} className="btn-primary w-full flex justify-center items-center gap-2 py-3">
                                    {isSubmitting ? 'Adding...' : <><Plus size={20} /> Add Bus</>}
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            )}

            {/* ===== DELETE CONFIRM MODAL ===== */}
            {showDeleteConfirm !== null && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 backdrop-blur-sm p-4">
                    <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm overflow-hidden">
                        <div className="p-6 text-center">
                            <div className="bg-red-100 text-red-600 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                                <Trash2 size={28} />
                            </div>
                            <h3 className="text-xl font-bold text-slate-800 mb-2">Remove Bus?</h3>
                            <p className="text-slate-500 text-sm mb-6">
                                This will cancel all active bookings on this bus and notify affected passengers. This action cannot be undone.
                            </p>
                            <div className="flex gap-3">
                                <button onClick={() => setShowDeleteConfirm(null)}
                                    className="flex-1 py-2.5 rounded-xl border-2 border-slate-200 font-bold text-slate-600 hover:bg-slate-50 transition-colors">
                                    Cancel
                                </button>
                                <button onClick={() => handleDeleteBus(showDeleteConfirm)} disabled={isSubmitting}
                                    className="flex-1 py-2.5 rounded-xl bg-red-600 text-white font-bold hover:bg-red-700 transition-colors shadow-md">
                                    {isSubmitting ? 'Removing...' : 'Yes, Remove'}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
