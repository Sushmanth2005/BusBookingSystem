import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../lib/axios';
import { Search, Bus, Clock, MapPin, ArrowRight } from 'lucide-react';
import { CITIES } from '../constants';

interface Schedule {
    id: number;
    busId: number;
    busName: string;
    routeId: number;
    source: string;
    destination: string;
    departureTime: string;
    arrivalTime: string;
    price: number;
}

export default function SearchSchedules() {
    const [source, setSource] = useState('Hyderabad');
    const [destination, setDestination] = useState('Vijayawada');
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const [date, setDate] = useState(tomorrow.toISOString().split('T')[0]);

    const [schedules, setSchedules] = useState<Schedule[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [hasSearched, setHasSearched] = useState(false);

    const handleSearch = async (e: React.FormEvent) => {
        e.preventDefault();
        if (source === destination) {
            setError('Source and destination cannot be the same.');
            return;
        }
        setLoading(true);
        setError('');
        setHasSearched(true);
        try {
            const response = await api.get(`/schedules/search?source=${source}&destination=${destination}&date=${date}`);
            setSchedules(response.data);
        } catch (_) {
            setError('Failed to fetch schedules.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="space-y-8 max-w-6xl mx-auto pb-12">

            {/* Search Header Hero */}
            <div className="glass-panel p-8 md:p-10 relative overflow-hidden">
                <div className="absolute top-0 right-0 -mt-16 -mr-16 text-indigo-50/50 pointer-events-none">
                    <Bus size={300} strokeWidth={1} />
                </div>

                <h2 className="text-3xl font-extrabold mb-8 flex items-center gap-3 text-slate-800 relative z-10">
                    <div className="bg-gradient-to-br from-blue-600 to-indigo-600 p-3 rounded-xl shadow-lg">
                        <Search className="text-white" size={24} />
                    </div>
                    Where are you traveling next?
                </h2>

                <form onSubmit={handleSearch} className="grid grid-cols-1 md:grid-cols-4 gap-5 items-end relative z-10">
                    <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-2 flex items-center gap-1"><MapPin size={16} className="text-indigo-500" /> From</label>
                        <select className="input-field font-medium text-lg" value={source} onChange={e => setSource(e.target.value)} required>
                            {CITIES.map(city => (
                                <option key={city} value={city}>{city}</option>
                            ))}
                        </select>
                    </div>
                    <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-2 flex items-center gap-1"><MapPin size={16} className="text-indigo-500" /> To</label>
                        <select className="input-field font-medium text-lg" value={destination} onChange={e => setDestination(e.target.value)} required>
                            {CITIES.filter(c => c !== source).map(city => (
                                <option key={city} value={city}>{city}</option>
                            ))}
                        </select>
                    </div>
                    <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-2 flex items-center gap-1"><Clock size={16} className="text-indigo-500" /> Date</label>
                        <input type="date" className="input-field font-medium text-lg" value={date} onChange={e => setDate(e.target.value)} required />
                    </div>
                    <button type="submit" disabled={loading} className="btn-primary flex items-center justify-center gap-2">
                        {loading ? 'Searching...' : <>Search Buses <ArrowRight size={20} /></>}
                    </button>
                </form>
            </div>

            {error && <div className="bg-red-50 border-l-4 border-red-500 text-red-700 p-4 rounded-r-xl shadow-sm">{error}</div>}

            {/* Results Grid */}
            {hasSearched && (
                <div className="mt-8">
                    <h3 className="text-xl font-bold text-slate-800 mb-6 px-2">Available Journeys <span className="text-slate-400 font-medium text-sm ml-2">({schedules.length} found)</span></h3>

                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {schedules.map(schedule => (
                            <div key={schedule.id} className="glass-panel hover:border-indigo-200 transition-all duration-300 hover:shadow-2xl hover:-translate-y-1">

                                {/* Card Header */}
                                <div className="p-6 border-b border-slate-100 flex justify-between items-start bg-gradient-to-r from-white to-slate-50/50 rounded-t-2xl">
                                    <div>
                                        <h3 className="text-xl font-bold text-slate-800 flex items-center gap-2 mb-1">
                                            <div className="bg-indigo-100 p-1.5 rounded-lg text-indigo-700"><Bus size={18} /></div>
                                            {schedule.busName}
                                        </h3>
                                        <p className="text-slate-500 font-medium text-sm flex items-center gap-1">
                                            {schedule.source} <ArrowRight size={12} /> {schedule.destination}
                                        </p>
                                    </div>
                                    <div className="bg-green-100 text-green-700 font-black px-4 py-2 rounded-xl text-xl shadow-sm border border-green-200">
                                        ₹{schedule.price}
                                    </div>
                                </div>

                                {/* Card Body */}
                                <div className="p-6 space-y-4">
                                    <div className="flex justify-between items-center bg-slate-50 p-3 rounded-xl border border-slate-100">
                                        <div className="flex flex-col">
                                            <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">Departure</span>
                                            <span className="text-slate-800 font-semibold">{new Date(schedule.departureTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                                            <span className="text-xs text-slate-500">{new Date(schedule.departureTime).toLocaleDateString()}</span>
                                        </div>
                                        <div className="h-[2px] w-12 bg-slate-200 relative">
                                            <div className="absolute -top-1.5 left-1/2 -ml-1.5 w-3 h-3 border-2 border-slate-300 rounded-full bg-white"></div>
                                        </div>
                                        <div className="flex flex-col text-right">
                                            <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">Arrival</span>
                                            <span className="text-slate-800 font-semibold">{new Date(schedule.arrivalTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                                            <span className="text-xs text-slate-500">{new Date(schedule.arrivalTime).toLocaleDateString()}</span>
                                        </div>
                                    </div>

                                    <Link to={`/book/${schedule.id}`} className="mt-4 flex items-center justify-center w-full bg-indigo-50 text-indigo-700 font-bold py-3 rounded-xl border border-indigo-100 hover:bg-indigo-600 hover:text-white transition-all duration-300 hover:shadow-md">
                                        Select Seats
                                    </Link>
                                </div>
                            </div>
                        ))}

                        {schedules.length === 0 && !loading && !error && (
                            <div className="col-span-full glass-panel p-12 flex flex-col items-center justify-center text-center">
                                <div className="bg-slate-100 p-6 rounded-full mb-4 text-slate-400">
                                    <Search size={48} strokeWidth={1.5} />
                                </div>
                                <h3 className="text-2xl font-bold text-slate-700 mb-2">No schedules found</h3>
                                <p className="text-slate-500 max-w-md mx-auto">We couldn't find any buses for this route on the selected date. Try Hyderabad → Vijayawada for tomorrow!</p>
                            </div>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}
