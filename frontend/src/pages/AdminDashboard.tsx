import { useEffect, useState } from 'react';
import api from '../lib/axios';
import { Bus, Map, CalendarClock, Plus, X } from 'lucide-react';

export default function AdminDashboard() {
    const [buses, setBuses] = useState<any[]>([]);
    const [routes, setRoutes] = useState<any[]>([]);
    const [schedules, setSchedules] = useState<any[]>([]);

    // Add Bus Modal State
    const [showBusModal, setShowBusModal] = useState(false);
    const [newBusName, setNewBusName] = useState('');
    const [newBusCapacity, setNewBusCapacity] = useState(40);
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            const [busRes, routeRes, schedRes] = await Promise.all([
                api.get('/admin/buses'),
                api.get('/admin/routes'),
                api.get('/admin/schedules')
            ]);
            setBuses(busRes.data);
            setRoutes(routeRes.data);
            setSchedules(schedRes.data);
        } catch (err) {
            console.error('Failed to load admin data');
        }
    };

    const handleAddBus = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);
        try {
            await api.post('/admin/buses', {
                busName: newBusName,
                capacity: newBusCapacity
            });
            setShowBusModal(false);
            setNewBusName('');
            setNewBusCapacity(40);
            fetchData(); // Refresh data
        } catch (err) {
            alert('Failed to add bus');
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="space-y-8 max-w-7xl mx-auto pb-12">
            <div className="glass-panel p-8 bg-gradient-to-r from-slate-800 to-indigo-900 border-none !shadow-2xl flex justify-between items-center">
                <div>
                    <h1 className="text-4xl font-extrabold text-white tracking-tight">Admin System Dashboard</h1>
                    <p className="text-indigo-200 mt-2 font-medium text-lg">Manage platform resources and metrics</p>
                </div>
                <button
                    onClick={() => setShowBusModal(true)}
                    className="bg-white/10 hover:bg-white/20 text-white border border-white/20 px-6 py-3 rounded-xl font-bold flex items-center gap-2 transition-all shadow-lg backdrop-blur-sm"
                >
                    <Plus size={20} /> Add New Bus
                </button>
            </div>

            {/* Stat Cards */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="glass-panel p-6 border-t-4 border-t-indigo-500 relative overflow-hidden group">
                    <div className="absolute -right-6 -bottom-6 text-indigo-50 group-hover:scale-110 transition-transform duration-500">
                        <Bus size={120} strokeWidth={1} />
                    </div>
                    <div className="relative z-10">
                        <div className="text-sm font-bold text-indigo-500 uppercase tracking-widest mb-1">Fleet</div>
                        <h2 className="text-lg font-bold text-slate-700 mb-2">Total Buses</h2>
                        <p className="text-5xl font-black text-slate-800 tracking-tighter">{buses.length}</p>
                    </div>
                </div>

                <div className="glass-panel p-6 border-t-4 border-t-emerald-500 relative overflow-hidden group">
                    <div className="absolute -right-6 -bottom-6 text-emerald-50 group-hover:scale-110 transition-transform duration-500">
                        <Map size={120} strokeWidth={1} />
                    </div>
                    <div className="relative z-10">
                        <div className="text-sm font-bold text-emerald-500 uppercase tracking-widest mb-1">Network</div>
                        <h2 className="text-lg font-bold text-slate-700 mb-2">Active Routes</h2>
                        <p className="text-5xl font-black text-slate-800 tracking-tighter">{routes.length}</p>
                    </div>
                </div>

                <div className="glass-panel p-6 border-t-4 border-t-amber-500 relative overflow-hidden group">
                    <div className="absolute -right-6 -bottom-6 text-amber-50 group-hover:scale-110 transition-transform duration-500">
                        <CalendarClock size={120} strokeWidth={1} />
                    </div>
                    <div className="relative z-10">
                        <div className="text-sm font-bold text-amber-500 uppercase tracking-widest mb-1">Ops</div>
                        <h2 className="text-lg font-bold text-slate-700 mb-2">Scheduled Routes</h2>
                        <p className="text-5xl font-black text-slate-800 tracking-tighter">{schedules.length}</p>
                    </div>
                </div>
            </div>

            {/* Platform Fleets Table */}
            <div className="glass-panel overflow-hidden">
                <div className="p-6 border-b border-slate-100 bg-white/50 flex justify-between items-center">
                    <h2 className="text-xl font-bold text-slate-800 flex items-center gap-2">
                        <Bus className="text-indigo-500" />
                        Active Bus Fleet Status
                    </h2>
                </div>

                <div className="overflow-x-auto">
                    <table className="w-full text-left border-collapse">
                        <thead>
                            <tr className="bg-slate-50/80 border-b border-slate-200">
                                <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">Bus Vehicle</th>
                                <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">Capacity</th>
                                <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">Status</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100 bg-white/30">
                            {buses.map(b => (
                                <tr key={b.id} className="hover:bg-indigo-50/50 transition-colors">
                                    <td className="p-4 font-bold text-slate-700 flex items-center gap-3">
                                        <div className="bg-indigo-100 text-indigo-600 p-2 rounded-lg">
                                            <Bus size={16} />
                                        </div>
                                        {b.busName} <span className="text-xs font-medium text-slate-400">#B-{b.id}</span>
                                    </td>
                                    <td className="p-4">
                                        <div className="font-semibold text-slate-800">{b.capacity} Seats</div>
                                    </td>
                                    <td className="p-4">
                                        <span className="bg-green-100 text-green-700 border border-green-200 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider">Active</span>
                                    </td>
                                </tr>
                            ))}
                            {buses.length === 0 && (
                                <tr>
                                    <td colSpan={3} className="p-8 text-center text-slate-500 font-medium">No buses registered.</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Latest Schedules Table */}
            <div className="glass-panel overflow-hidden">
                <div className="p-6 border-b border-slate-100 bg-white/50">
                    <h2 className="text-xl font-bold text-slate-800 flex items-center gap-2">
                        <CalendarClock className="text-indigo-500" />
                        Upcoming Logistics Schedule
                    </h2>
                </div>

                <div className="overflow-x-auto">
                    <table className="w-full text-left border-collapse">
                        <thead>
                            <tr className="bg-slate-50/80 border-b border-slate-200">
                                <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">Bus Vehicle</th>
                                <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">Route Path</th>
                                <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest">Departure Time</th>
                                <th className="p-4 text-xs font-bold text-slate-500 uppercase tracking-widest text-right">Seat Price</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100 bg-white/30">
                            {schedules.map(s => (
                                <tr key={s.id} className="hover:bg-indigo-50/50 transition-colors">
                                    <td className="p-4 font-bold text-slate-700 flex items-center gap-3">
                                        <div className="bg-indigo-100 text-indigo-600 p-2 rounded-lg">
                                            <Bus size={16} />
                                        </div>
                                        {s.busName}
                                    </td>
                                    <td className="p-4">
                                        <div className="font-semibold text-slate-800">{s.source}</div>
                                        <div className="text-xs text-slate-500 font-medium tracking-wide">→ {s.destination}</div>
                                    </td>
                                    <td className="p-4">
                                        <div className="font-semibold text-slate-800">{new Date(s.departureTime).toLocaleDateString()}</div>
                                        <div className="text-sm font-medium text-slate-500">{new Date(s.departureTime).toLocaleTimeString([], { timeStyle: 'short' })}</div>
                                    </td>
                                    <td className="p-4 font-black text-lg text-slate-800 text-right">
                                        ${s.price.toFixed(2)}
                                    </td>
                                </tr>
                            ))}

                            {schedules.length === 0 && (
                                <tr>
                                    <td colSpan={4} className="p-8 text-center text-slate-500 font-medium">
                                        No schedules have been generated yet.
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Add Bus Modal */}
            {showBusModal && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 backdrop-blur-sm p-4">
                    <div className="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden animate-in fade-in zoom-in-95 duration-200">
                        <div className="p-6 border-b border-slate-100 flex justify-between items-center bg-slate-50">
                            <h3 className="text-2xl font-bold flex items-center gap-2 text-slate-800">
                                <div className="bg-indigo-100 text-indigo-600 p-2 rounded-lg"><Bus size={20} /></div>
                                Register New Bus
                            </h3>
                            <button onClick={() => setShowBusModal(false)} className="text-slate-400 hover:text-red-500 transition border border-transparent hover:bg-red-50 p-2 rounded-lg">
                                <X size={20} />
                            </button>
                        </div>
                        <div className="p-6">
                            <form onSubmit={handleAddBus} className="space-y-5">
                                <div>
                                    <label className="block text-sm font-bold text-slate-700 mb-2">Bus Name / Model</label>
                                    <input
                                        type="text" required
                                        placeholder="e.g. Volvo XC90 Express"
                                        className="input-field shadow-none border-2 bg-slate-50 focus:bg-white"
                                        value={newBusName}
                                        onChange={e => setNewBusName(e.target.value)}
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-bold text-slate-700 mb-2">Seating Capacity</label>
                                    <select
                                        className="input-field shadow-none border-2 bg-slate-50 focus:bg-white"
                                        value={newBusCapacity}
                                        onChange={e => setNewBusCapacity(Number(e.target.value))}
                                    >
                                        <option value="20">20 Seats (Mini)</option>
                                        <option value="40">40 Seats (Standard)</option>
                                        <option value="52">52 Seats (Large)</option>
                                    </select>
                                </div>
                                <div className="pt-2">
                                    <button type="submit" disabled={isSubmitting} className="btn-primary w-full flex justify-center items-center gap-2 py-3 shadow-indigo-500/25">
                                        {isSubmitting ? 'Registering...' : <><Plus size={20} /> Register Fleet Vehicle</>}
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
