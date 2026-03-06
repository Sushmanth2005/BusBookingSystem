const API_BASE = '/api';
const AUTH_URL = `${API_BASE}/auth`;
const ADMIN_URL = `${API_BASE}/admin`;

let jwtToken = localStorage.getItem('jwtToken') || null;

document.addEventListener('DOMContentLoaded', () => {
    bindEvents();
    checkAuthStatus();
});

function bindEvents() {
    // Auth UI toggles
    document.getElementById('btn-show-login').addEventListener('click', () => toggleAuthModes('login'));
    document.getElementById('btn-show-register').addEventListener('click', () => toggleAuthModes('register'));
    document.getElementById('btn-logout').addEventListener('click', handleLogout);

    // Forms
    document.getElementById('login-form').addEventListener('submit', handleLogin);
    document.getElementById('register-form').addEventListener('submit', handleRegister);

    // Dashboard Tabs
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.panel').forEach(p => p.classList.add('hidden'));

            e.target.classList.add('active');
            document.getElementById(e.target.dataset.target).classList.remove('hidden');
        });
    });

    // Create Forms
    document.getElementById('add-bus-form').addEventListener('submit', handleAddBus);
    document.getElementById('add-route-form').addEventListener('submit', handleAddRoute);
    document.getElementById('add-schedule-form').addEventListener('submit', handleAddSchedule);
}

function checkAuthStatus() {
    if (jwtToken) {
        document.getElementById('auth-section').classList.add('hidden');
        document.getElementById('dashboard-section').classList.remove('hidden');
        document.getElementById('btn-logout').classList.remove('hidden');
        document.getElementById('btn-show-login').classList.add('hidden');
        document.getElementById('btn-show-register').classList.add('hidden');
        loadDashboardData();
    } else {
        document.getElementById('auth-section').classList.remove('hidden');
        document.getElementById('dashboard-section').classList.add('hidden');
        document.getElementById('btn-logout').classList.add('hidden');
        document.getElementById('btn-show-login').classList.remove('hidden');
        document.getElementById('btn-show-register').classList.remove('hidden');
    }
}

function toggleAuthModes(mode) {
    if (mode === 'login') {
        document.getElementById('login-form-container').classList.remove('hidden');
        document.getElementById('register-form-container').classList.add('hidden');
    } else {
        document.getElementById('register-form-container').classList.remove('hidden');
        document.getElementById('login-form-container').classList.add('hidden');
    }
}

function getAuthHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwtToken}`
    };
}

async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    try {
        const res = await fetch(`${AUTH_URL}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (res.ok) {
            const data = await res.json();
            jwtToken = data.token;
            localStorage.setItem('jwtToken', jwtToken);
            checkAuthStatus();
        } else {
            alert('Login failed. Check credentials.');
        }
    } catch (err) {
        console.error(err);
        alert('Server error');
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const name = document.getElementById('register-name').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;
    const role = document.getElementById('register-role').value;

    try {
        const res = await fetch(`${AUTH_URL}/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password, role })
        });

        if (res.ok) {
            alert('Registration successful! Please login.');
            toggleAuthModes('login');
        } else {
            alert('Registration failed.');
        }
    } catch (err) {
        console.error(err);
        alert('Server error');
    }
}

function handleLogout() {
    jwtToken = null;
    localStorage.removeItem('jwtToken');
    checkAuthStatus();
}

// DASHBOARD LOADERS
function loadDashboardData() {
    fetchBuses();
    fetchRoutes();
    fetchSchedules();
}

// --- BUSES ---
async function fetchBuses() {
    try {
        const res = await fetch(`${ADMIN_URL}/buses`, { headers: getAuthHeaders() });
        if (res.ok) {
            const buses = await res.json();
            const tbody = document.querySelector('#buses-table tbody');
            tbody.innerHTML = '';

            // Populate select for schedules
            const busSelect = document.getElementById('schedule-bus-id');
            busSelect.innerHTML = '<option value="">Select Bus</option>';

            buses.forEach(b => {
                tbody.innerHTML += `
                    <tr>
                        <td>${b.id}</td>
                        <td>${b.busName}</td>
                        <td>${b.totalSeats}</td>
                        <td>${b.type}</td>
                        <td><button class="danger" onclick="deleteBus(${b.id})">Delete</button></td>
                    </tr>
                `;
                busSelect.innerHTML += `<option value="${b.id}">${b.busName} (${b.type})</option>`;
            });
        }
    } catch (err) { console.error('Failed to fetch buses', err); }
}

async function handleAddBus(e) {
    e.preventDefault();
    const data = {
        busName: document.getElementById('bus-name').value,
        totalSeats: parseInt(document.getElementById('bus-seats').value),
        type: document.getElementById('bus-type').value
    };

    try {
        const res = await fetch(`${ADMIN_URL}/buses`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(data)
        });
        if (res.ok) {
            fetchBuses();
            e.target.reset();
        } else alert('Failed to add bus');
    } catch (err) { console.error(err); }
}

async function deleteBus(id) {
    if (!confirm('Are you sure you want to delete this bus?')) return;
    try {
        const res = await fetch(`${ADMIN_URL}/buses/${id}`, { method: 'DELETE', headers: getAuthHeaders() });
        if (res.ok) fetchBuses();
    } catch (err) { console.error(err); }
}

// --- ROUTES ---
async function fetchRoutes() {
    try {
        const res = await fetch(`${ADMIN_URL}/routes`, { headers: getAuthHeaders() });
        if (res.ok) {
            const routes = await res.json();
            const tbody = document.querySelector('#routes-table tbody');
            tbody.innerHTML = '';

            // Populate select for schedules
            const routeSelect = document.getElementById('schedule-route-id');
            routeSelect.innerHTML = '<option value="">Select Route</option>';

            routes.forEach(r => {
                tbody.innerHTML += `
                    <tr>
                        <td>${r.id}</td>
                        <td>${r.source}</td>
                        <td>${r.destination}</td>
                        <td>${r.distance}</td>
                        <td><button class="danger" onclick="deleteRoute(${r.id})">Delete</button></td>
                    </tr>
                `;
                routeSelect.innerHTML += `<option value="${r.id}">${r.source} to ${r.destination}</option>`;
            });
        }
    } catch (err) { console.error('Failed to fetch routes', err); }
}

async function handleAddRoute(e) {
    e.preventDefault();
    const data = {
        source: document.getElementById('route-source').value,
        destination: document.getElementById('route-destination').value,
        distance: parseFloat(document.getElementById('route-distance').value)
    };

    try {
        const res = await fetch(`${ADMIN_URL}/routes`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(data)
        });
        if (res.ok) {
            fetchRoutes();
            e.target.reset();
        } else alert('Failed to add route');
    } catch (err) { console.error(err); }
}

async function deleteRoute(id) {
    if (!confirm('Are you sure you want to delete this route?')) return;
    try {
        const res = await fetch(`${ADMIN_URL}/routes/${id}`, { method: 'DELETE', headers: getAuthHeaders() });
        if (res.ok) fetchRoutes();
    } catch (err) { console.error(err); }
}

// --- SCHEDULES ---
async function fetchSchedules() {
    try {
        const res = await fetch(`${ADMIN_URL}/schedules`, { headers: getAuthHeaders() });
        if (res.ok) {
            const schedules = await res.json();
            const tbody = document.querySelector('#schedules-table tbody');
            tbody.innerHTML = '';

            schedules.forEach(s => {
                tbody.innerHTML += `
                    <tr>
                        <td>${s.id}</td>
                        <td>${s.busName}</td>
                        <td>${s.source} to ${s.destination}</td>
                        <td>${new Date(s.departureTime).toLocaleString()}</td>
                        <td>${new Date(s.arrivalTime).toLocaleString()}</td>
                        <td>$${s.price}</td>
                        <td><button class="danger" onclick="deleteSchedule(${s.id})">Delete</button></td>
                    </tr>
                `;
            });
        }
    } catch (err) { console.error('Failed to fetch schedules', err); }
}

async function handleAddSchedule(e) {
    e.preventDefault();
    const data = {
        busId: parseInt(document.getElementById('schedule-bus-id').value),
        routeId: parseInt(document.getElementById('schedule-route-id').value),
        departureTime: document.getElementById('schedule-departure').value,
        arrivalTime: document.getElementById('schedule-arrival').value,
        price: parseFloat(document.getElementById('schedule-price').value)
    };

    try {
        const res = await fetch(`${ADMIN_URL}/schedules`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(data)
        });
        if (res.ok) {
            fetchSchedules();
            e.target.reset();
        } else alert('Failed to add schedule');
    } catch (err) { console.error(err); }
}

async function deleteSchedule(id) {
    if (!confirm('Are you sure you want to delete this schedule?')) return;
    try {
        const res = await fetch(`${ADMIN_URL}/schedules/${id}`, { method: 'DELETE', headers: getAuthHeaders() });
        if (res.ok) fetchSchedules();
    } catch (err) { console.error(err); }
}
