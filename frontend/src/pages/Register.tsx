import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../lib/axios';
import { UserPlus } from 'lucide-react';

export default function Register() {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await api.post('/auth/register', { name, email, password });
            alert('Registration successful! Please login.');
            navigate('/login');
        } catch (err: unknown) {
            const error = err as { response?: { data?: { message?: string } } };
            setError(error.response?.data?.message || 'Registration failed.');
        }
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-[75vh]">
            <div className="glass-panel p-10 w-full max-w-md">
                <div className="flex justify-center mb-6 text-indigo-600">
                    <div className="bg-indigo-50 p-4 rounded-full">
                        <UserPlus size={40} strokeWidth={2} />
                    </div>
                </div>

                <h2 className="text-3xl font-bold mb-2 text-center text-slate-800 tracking-tight">Create an Account</h2>
                <p className="text-center text-slate-500 mb-8 font-medium">Join BusEase to start exploring</p>

                {error && <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6 text-sm">{error}</div>}

                <form onSubmit={handleRegister} className="space-y-4">
                    <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-1">Full Name</label>
                        <input
                            type="text" required placeholder="John Doe"
                            className="input-field"
                            value={name} onChange={e => setName(e.target.value)}
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-1">Email Address</label>
                        <input
                            type="email" required placeholder="name@example.com"
                            className="input-field"
                            value={email} onChange={e => setEmail(e.target.value)}
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-1">Password</label>
                        <input
                            type="password" required placeholder="••••••••"
                            className="input-field"
                            value={password} onChange={e => setPassword(e.target.value)}
                        />
                    </div>
                    <div className="pt-4">
                        <button type="submit" className="btn-primary w-full">Register Account</button>
                    </div>
                </form>

                <p className="mt-8 text-center text-slate-500 font-medium text-sm">
                    Already have an account? <Link to="/login" className="text-indigo-600 font-bold hover:text-indigo-800 transition">Log in here</Link>
                </p>
            </div>
        </div>
    );
}
