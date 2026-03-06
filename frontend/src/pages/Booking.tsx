import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../lib/axios';
import { Check, X, ShieldCheck } from 'lucide-react';

interface Seat {
    id: number;
    seatNumber: string;
    isBooked: boolean;
}

interface Schedule {
    id: number;
    busId: number;
    busName: string;
    source: string;
    destination: string;
    price: number;
}

export default function Booking() {
    const { scheduleId } = useParams();
    const navigate = useNavigate();

    const [schedule, setSchedule] = useState<Schedule | null>(null);
    const [seats, setSeats] = useState<Seat[]>([]);
    const [selectedSeats, setSelectedSeats] = useState<string[]>([]);
    const [loading, setLoading] = useState(true);
    const [bookingLoading, setBookingLoading] = useState(false);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const schedRes = await api.get(`/schedules/${scheduleId}`);
                setSchedule(schedRes.data);

                if (schedRes.data) {
                    const seatRes = await api.get(`/seats/bus/${schedRes.data.busId}`);
                    setSeats(seatRes.data);
                }
            } catch (err) {
                console.error("Failed to load booking info");
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [scheduleId]);

    const toggleSeat = (seatNumber: string) => {
        setSelectedSeats(prev =>
            prev.includes(seatNumber)
                ? prev.filter(s => s !== seatNumber)
                : [...prev, seatNumber]
        );
    };

    const handleBook = async () => {
        if (selectedSeats.length === 0) return alert('Select at least one seat');
        setBookingLoading(true);
        try {
            await api.post('/bookings', {
                scheduleId: Number(scheduleId),
                seatNumbers: selectedSeats
            });
            // Navigate instantly
            navigate('/bookings');
        } catch (err: any) {
            alert(err.response?.data?.message || 'Booking failed. Seats might be taken.');
            const seatRes = await api.get(`/seats/bus/${schedule?.busId}`);
            setSeats(seatRes.data);
            setSelectedSeats([]);
        } finally {
            setBookingLoading(false);
        }
    };

    if (loading) return (
        <div className="flex justify-center items-center p-20">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
        </div>
    );

    if (!schedule) return <div className="p-8 text-center bg-red-50 text-red-600 rounded-xl font-bold">Schedule not found or unavailable.</div>;

    const totalAmount = schedule.price * selectedSeats.length;

    return (
        <div className="max-w-6xl mx-auto grid grid-cols-1 lg:grid-cols-3 gap-8 pb-12">

            {/* Left Column: Seat Layout */}
            <div className="lg:col-span-2">
                <div className="glass-panel p-8 min-h-[500px]">
                    <div className="border-b border-slate-100 pb-6 mb-8">
                        <h2 className="text-3xl font-extrabold text-slate-800 mb-2">Select Your Seats</h2>
                        <div className="flex items-center gap-2 text-slate-500 font-medium">
                            <span className="bg-indigo-100 text-indigo-700 px-3 py-1 rounded-md text-sm font-bold tracking-wide">{schedule.busName}</span>
                            <span>•</span>
                            <span>{schedule.source} to {schedule.destination}</span>
                        </div>
                    </div>

                    <div className="flex gap-6 mb-10 justify-center bg-slate-50 py-4 rounded-xl border border-slate-100">
                        <div className="flex items-center gap-2 text-sm font-semibold text-slate-600"><div className="w-5 h-5 rounded-md border-2 border-slate-200 bg-white"></div> Available</div>
                        <div className="flex items-center gap-2 text-sm font-semibold text-slate-600"><div className="w-5 h-5 rounded-md bg-slate-200 flex items-center justify-center text-slate-400"><X size={14} strokeWidth={3} /></div> Booked</div>
                        <div className="flex items-center gap-2 text-sm font-semibold text-slate-600"><div className="w-5 h-5 rounded-md bg-indigo-500 border-2 border-indigo-600 flex items-center justify-center text-white"><Check size={14} strokeWidth={3} /></div> Selected</div>
                    </div>

                    <div className="bg-slate-100 rounded-[3rem] p-10 max-w-md mx-auto relative border-4 border-slate-300 shadow-inner">
                        {/* Steering wheel visual block */}
                        <div className="absolute top-4 right-10 w-8 h-8 border-4 border-slate-300 rounded-full opacity-50"></div>

                        <div className="grid grid-cols-4 gap-4 mt-8">
                            {seats.map(seat => {
                                const isSelected = selectedSeats.includes(seat.seatNumber);
                                return (
                                    <button
                                        key={seat.id}
                                        disabled={seat.isBooked}
                                        onClick={() => toggleSeat(seat.seatNumber)}
                                        className={`
                      relative w-14 h-14 flex justify-center items-center rounded-xl font-bold transition-all duration-200
                      ${seat.isBooked
                                                ? 'bg-slate-200 text-slate-400 cursor-not-allowed border-2 border-slate-300 opacity-60'
                                                : isSelected
                                                    ? 'bg-indigo-500 text-white shadow-lg shadow-indigo-500/40 border-b-4 border-indigo-700 -translate-y-1'
                                                    : 'bg-white text-slate-600 hover:bg-indigo-50 hover:border-indigo-300 border-2 border-slate-200 shadow-sm hover:shadow active:translate-y-0 active:border-t-2'}
                    `}
                                    >
                                        {seat.isBooked ? <X size={20} /> : isSelected ? <Check size={20} /> : seat.seatNumber}
                                    </button>
                                )
                            })}
                        </div>
                    </div>
                </div>
            </div>

            {/* Right Column: Summary & Checkout */}
            <div>
                <div className="glass-panel p-8 sticky top-28 border-t-4 border-t-indigo-500">
                    <h2 className="text-xl font-bold mb-6 text-slate-800 flex items-center gap-2">
                        Trip Summary
                    </h2>

                    <div className="bg-slate-50 rounded-xl p-5 border border-slate-100 mb-6 space-y-4">
                        <div className="flex justify-between items-start">
                            <span className="text-slate-500 font-medium">Selected Seats</span>
                            <span className="font-bold text-slate-800 text-right max-w-[120px] break-words">
                                {selectedSeats.length > 0 ? (
                                    <div className="flex flex-wrap gap-1 justify-end">
                                        {selectedSeats.map(s => <span key={s} className="bg-indigo-100 text-indigo-700 px-2 py-0.5 rounded text-sm">{s}</span>)}
                                    </div>
                                ) : (
                                    <span className="text-slate-400 italic font-normal text-sm">No seats selected</span>
                                )}
                            </span>
                        </div>
                        <div className="flex justify-between items-center border-t border-slate-200 pt-4">
                            <span className="text-slate-500 font-medium">Price per seat</span>
                            <span className="font-bold text-slate-800">₹{schedule.price.toFixed(2)}</span>
                        </div>

                        <div className="flex justify-between items-center bg-indigo-50 p-4 rounded-lg border-2 border-indigo-100 mt-4">
                            <span className="text-sm font-bold text-indigo-900 uppercase tracking-widest">Total Pay</span>
                            <span className="text-3xl font-black text-indigo-600">₹{totalAmount.toFixed(2)}</span>
                        </div>
                    </div>

                    <div className="bg-green-50 text-green-700 text-sm p-3 rounded-lg flex items-center gap-2 font-medium mb-6">
                        <ShieldCheck size={18} className="shrink-0" /> Automatic secure payment confirmation
                    </div>

                    <button
                        onClick={handleBook}
                        disabled={selectedSeats.length === 0 || bookingLoading}
                        className="btn-primary w-full text-lg shadow-indigo-500/25 flex justify-center items-center gap-2"
                    >
                        {bookingLoading ? (
                            <><span className="animate-spin h-5 w-5 border-2 border-white/30 border-t-white rounded-full"></span> Processing...</>
                        ) : 'Confirm & Pay'}
                    </button>
                </div>
            </div>
        </div>
    );
}
