import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../lib/axios';
import { useAuth } from '../context/AuthContext';
import { Ticket } from 'lucide-react';

export default function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const response = await api.post('/auth/login', { email, password });
            login(response.data.token);
            navigate('/');
        } catch (err) {
            setError('Invalid credentials. Please try again.');
        }
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-[75vh]">
            <div className="flex items-center gap-3 mb-10 text-indigo-700">
                <Ticket size={56} strokeWidth={2} />
                <h1 className="text-5xl font-extrabold tracking-tight">BusEase</h1>
            </div>

            <div className="glass-panel p-10 w-full max-w-md">
                <h2 className="text-3xl font-bold mb-2 text-center text-slate-800 tracking-tight">Welcome Back</h2>
                <p className="text-center text-slate-500 mb-8 font-medium">Log in to manage your travels</p>

                {error && <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6 text-sm">{error}</div>}

                <form onSubmit={handleLogin} className="space-y-5">
                    <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-2">Email Address</label>
                        <input
                            type="email" required placeholder="name@example.com"
                            className="input-field"
                            value={email} onChange={e => setEmail(e.target.value)}
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-2">Password</label>
                        <input
                            type="password" required placeholder="••••••••"
                            className="input-field"
                            value={password} onChange={e => setPassword(e.target.value)}
                        />
                    </div>
                    <div className="pt-2">
                        <button type="submit" className="btn-primary w-full">Sign In</button>
                    </div>
                </form>

                <p className="mt-8 text-center text-slate-500 font-medium text-sm">
                    Don't have an account? <Link to="/register" className="text-indigo-600 font-bold hover:text-indigo-800 transition">Register here</Link>
                </p>
            </div>
        </div>
    );
}
