import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../lib/axios';
import { Bookmark, Clock, CheckCircle2, XCircle, AlertCircle } from 'lucide-react';

interface Booking {
    id: number;
    source: string;
    destination: string;
    departureTime: string;
    seatNumbers: string[];
    status: string;
    totalAmount: number;
    bookingDate: string;
}

export default function MyBookings() {
    const [bookings, setBookings] = useState<Booking[]>([]);
    const [loading, setLoading] = useState(true);

    const fetchBookings = async () => {
        try {
            const response = await api.get('/bookings/my-bookings');
            setBookings(response.data);
        } catch (err) {
            console.error('Failed to fetch bookings');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchBookings();
    }, []);

    const handleCancel = async (id: number) => {
        if (!confirm('Are you sure you want to cancel this booking? This action cannot be undone.')) return;
        try {
            await api.delete(`/bookings/${id}`);
            fetchBookings();
        } catch (err) {
            alert('Failed to cancel booking');
        }
    };

    if (loading) return (
        <div className="flex justify-center items-center p-20">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>
    );

    return (
        <div className="max-w-4xl mx-auto space-y-8 pb-12">
            <div className="glass-panel p-8 flex items-center gap-4 bg-gradient-to-r from-white to-indigo-50/30">
                <div className="bg-indigo-100 p-4 rounded-xl text-indigo-600">
                    <Bookmark size={32} />
                </div>
                <div>
                    <h2 className="text-3xl font-extrabold text-slate-800">My Travel History</h2>
                    <p className="text-slate-500 font-medium mt-1">Manage and view your upcoming and past journeys.</p>
                </div>
            </div>

            {bookings.length === 0 ? (
                <div className="glass-panel text-center p-16">
                    <div className="bg-slate-50 w-24 h-24 rounded-full flex items-center justify-center mx-auto mb-6">
                        <Bookmark size={48} className="text-slate-300" />
                    </div>
                    <h3 className="text-2xl font-bold text-slate-700 mb-2">No bookings found</h3>
                    <p className="text-slate-500">You haven't booked any bus tickets yet. Ready to travel?</p>
                    <Link to="/search" className="btn-primary inline-block mt-8 !w-auto">Search for Buses</Link>
                </div>
            ) : (
                <div className="space-y-6">
                    {bookings.map(booking => {
                        const isConfirmed = booking.status === 'CONFIRMED';
                        const isCancelled = booking.status === 'CANCELLED';
                        const StatusIcon = isConfirmed ? CheckCircle2 : isCancelled ? XCircle : AlertCircle;

                        return (
                            <div key={booking.id} className="glass-panel overflow-hidden transition-all duration-300 hover:shadow-lg hover:-translate-y-1">
                                <div className={`h-2 w-full ${isConfirmed ? 'bg-green-500' : isCancelled ? 'bg-red-500' : 'bg-amber-500'}`}></div>

                                <div className="p-6 md:p-8 flex flex-col md:flex-row justify-between gap-6">
                                    <div className="flex-1 space-y-4">
                                        <div className="flex items-center gap-3">
                                            <span className={`flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider
                        ${isConfirmed ? 'bg-green-100 text-green-700 border border-green-200' :
                                                    isCancelled ? 'bg-red-100 text-red-700 border border-red-200' :
                                                        'bg-amber-100 text-amber-700 border border-amber-200'}`}>
                                                <StatusIcon size={14} strokeWidth={3} />
                                                {booking.status}
                                            </span>
                                            <span className="text-slate-400 text-sm font-medium">Booking #{booking.id.toString().padStart(6, '0')}</span>
                                        </div>

                                        <div>
                                            <h3 className="text-2xl font-extrabold text-slate-800 tracking-tight flex items-center gap-2">
                                                {booking.source}
                                                <span className="text-slate-300 mx-1">→</span>
                                                {booking.destination}
                                            </h3>
                                        </div>

                                        <div className="flex flex-wrap gap-4 text-sm bg-slate-50 p-4 rounded-xl border border-slate-100">
                                            <div className="flex items-center gap-2">
                                                <div className="bg-indigo-100 p-1.5 rounded-lg text-indigo-600"><Clock size={16} /></div>
                                                <div>
                                                    <p className="text-xs font-bold text-slate-400 uppercase">Departure</p>
                                                    <p className="font-semibold text-slate-700">{new Date(booking.departureTime).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' })}</p>
                                                </div>
                                            </div>
                                            <div className="w-px bg-slate-200 hidden sm:block"></div>
                                            <div>
                                                <p className="text-xs font-bold text-slate-400 uppercase">Selected Seats</p>
                                                <div className="flex gap-1 mt-0.5">
                                                    {booking.seatNumbers.map(s => <span key={s} className="bg-white border border-slate-200 shadow-sm px-2 py-0.5 rounded text-indigo-700 font-bold tracking-widest">{s}</span>)}
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div className="flex flex-col items-start md:items-end justify-between border-t md:border-t-0 md:border-l border-slate-100 pt-6 md:pt-0 md:pl-8 md:min-w-[200px]">
                                        <div className="text-left md:text-right w-full">
                                            <p className="text-sm font-bold text-slate-400 uppercase tracking-widest mb-1">Total Paid</p>
                                            <p className={`text-4xl font-black ${isCancelled ? 'text-slate-400 line-through' : 'text-slate-800'}`}>₹{booking.totalAmount.toFixed(2)}</p>
                                            <p className="text-xs text-slate-500 mt-2">Booked: {new Date(booking.bookingDate).toLocaleDateString()}</p>
                                        </div>

                                        {!isCancelled && new Date(booking.departureTime) > new Date() && (
                                            <button
                                                onClick={() => handleCancel(booking.id)}
                                                className="mt-6 w-full text-center bg-white border-2 border-red-100 text-red-600 font-bold py-2.5 rounded-xl hover:bg-red-50 hover:border-red-200 transition-colors shadow-sm"
                                            >
                                                Cancel Ticket
                                            </button>
                                        )}
                                    </div>
                                </div>
                            </div>
                        );
                    })}
                </div>
            )}
        </div>
    );
}
