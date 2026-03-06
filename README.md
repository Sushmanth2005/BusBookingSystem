# 🚌 BusEase — Modern Bus Booking System

A full-stack bus ticket booking and management system built with **Spring Boot** (Java 17) and **React** (TypeScript/Vite).

---

## 📋 Modules

This project implements **7 core modules** as defined in the project requirements:

| # | Module | Description | Key Files |
|---|--------|-------------|-----------|
| 1 | **User Authentication** | Login/Register with JWT tokens, role-based access (ADMIN/USER) | `AuthController`, `AuthService`, `JwtUtil`, `Login.tsx`, `Register.tsx` |
| 2 | **Bus Search** | Search available buses by source city, destination city, and date | `ScheduleController.search()`, `SearchSchedules.tsx` |
| 3 | **Seat Selection** | Interactive seat map with real-time availability, prevents double-booking | `SeatController`, `Booking.tsx` (visual seat grid) |
| 4 | **Ticket Booking** | Confirm booking with mock payment, generates booking ID | `BookingService.createBooking()`, `Booking.tsx` |
| 5 | **View Booking** | View all bookings with status, seats, amounts, dates | `BookingController.getMyBookings()`, `MyBookings.tsx` |
| 6 | **Ticket Cancellation** | Cancel bookings and auto-release seats | `BookingController.cancelBooking()`, `MyBookings.tsx` |
| 7 | **Database Management** | H2 file-based storage, JPA entities, data initialization | Entities: `User`, `Bus`, `Route`, `Schedule`, `Seat`, `Booking` |

---

## 🏗️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Java 17, Spring Boot 3.2.5, Spring Security, Spring Data JPA |
| **Frontend** | React 19, TypeScript, Vite, Tailwind CSS v4, React Router v7 |
| **Database** | H2 (file-based, persists across restarts) |
| **Auth** | JWT (JSON Web Tokens) with BCrypt password hashing |
| **API Docs** | Swagger / OpenAPI 3 (SpringDoc) |
| **CI/CD** | GitHub Actions (Maven + Node.js build/test/lint) |

---

## 🚀 Getting Started

### Prerequisites

- [Java 17+](https://adoptium.net/) (JDK)
- [Maven 3.8+](https://maven.apache.org/)
- [Node.js 18+](https://nodejs.org/)

### Running the Backend

```bash
# From the project root directory
mvn spring-boot:run
```

The backend starts on **http://localhost:8080**.

> **Note:** On first startup, the database is automatically seeded with test data (users, buses, routes, schedules). Data persists in the `./data/` folder.

### Running the Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend starts on **http://localhost:5173**.

---

## 🔑 Test Credentials

| Role | Email | Password |
|------|-------|----------|
| **Admin** | `admin@busease.com` | `admin123` |
| **User** | `user@busease.com` | `user123` |
| **User** | `ravi@busease.com` | `ravi123` |

---

## 🗺️ Test Data — Cities & Routes

The system is pre-loaded with **10 Indian cities** and **20 routes** (10 city pairs × 2 directions):

**Cities:** Hyderabad, Vijayawada, Visakhapatnam, Chennai, Bangalore, Tirupati, Mumbai, Delhi, Pune, Kolkata

| Route | Distance | Price Range (₹) |
|-------|----------|----------------|
| Hyderabad ↔ Vijayawada | 275 km | 300 – 450 |
| Hyderabad ↔ Bangalore | 570 km | 800 – 1,200 |
| Hyderabad ↔ Chennai | 625 km | 900 – 1,400 |
| Vijayawada ↔ Visakhapatnam | 350 km | 500 – 750 |
| Hyderabad ↔ Tirupati | 550 km | 700 – 1,100 |
| Chennai ↔ Bangalore | 345 km | 600 – 900 |
| Mumbai ↔ Pune | 150 km | 350 – 500 |
| Hyderabad ↔ Mumbai | 710 km | 1,000 – 1,600 |
| Delhi ↔ Mumbai | 1,400 km | 1,500 – 2,500 |
| Bangalore ↔ Visakhapatnam | 800 km | 1,100 – 1,800 |

**Buses:** 20 operators (10 AC + 10 Non-AC), including APSRTC, TSRTC, Orange Travels, SRS, VRL, KPN, and more.

---

## 📡 API Endpoints

### Public
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/register` | Register new user |
| `POST` | `/api/auth/login` | Login (returns JWT) |
| `GET` | `/actuator/health` | Health check |
| `GET` | `/swagger-ui.html` | Interactive API docs |

### Authenticated (requires JWT)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/schedules/search?source=X&destination=Y&date=YYYY-MM-DD` | Search schedules |
| `GET` | `/api/schedules/{id}` | Get schedule by ID |
| `GET` | `/api/seats/bus/{busId}` | Get seat layout for a bus |
| `POST` | `/api/bookings` | Create booking |
| `GET` | `/api/bookings/my-bookings` | View user's bookings |
| `DELETE` | `/api/bookings/{id}` | Cancel booking |

### Admin Only
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/admin/buses` | Add a new bus |
| `POST` | `/api/admin/routes` | Add a new route |
| `POST` | `/api/admin/schedules` | Add a new schedule |

---

## 🧪 Running Tests

```bash
# Backend tests (from project root)
mvn clean test

# Frontend lint
cd frontend && npm run lint

# Frontend build check
cd frontend && npm run build
```

---

## 🗄️ Database

The application uses **H2 file-based database** (`./data/buseasedb`). Data persists across application restarts.

- **H2 Console** available at: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./data/buseasedb`
  - Username: `sa`
  - Password: *(empty)*

To **reset all data**, stop the application and delete the `./data/` folder.

---

## 🔄 CI/CD (GitHub Actions)

The project includes a CI workflow (`.github/workflows/ci.yml`) that runs on push/PR to `main`/`master`:

1. **Backend Job** — Builds and tests with Maven (Java 17)
2. **Frontend Job** — Installs, lints, and builds with Node.js 18
3. **Merge Gate** — Both jobs must pass before merge is allowed

---

## 📁 Project Structure

```
BUSBOOKINGSYSTEM/
├── src/main/java/com/busease/
│   ├── BusEaseApplication.java          # Spring Boot entry point
│   ├── config/
│   │   ├── SecurityConfig.java          # JWT + CORS + Security rules
│   │   └── DataInitializer.java         # Seeds test data on first run
│   ├── controller/                      # REST API endpoints
│   ├── dto/                             # Request/Response DTOs
│   ├── entity/                          # JPA entities
│   ├── enums/                           # Role, BusType, BookingStatus
│   ├── exception/                       # Global error handling
│   ├── repository/                      # Spring Data JPA repositories
│   ├── security/                        # JWT filter, utils, UserDetails
│   └── service/                         # Business logic
├── src/main/resources/
│   └── application.yml                  # App configuration
├── src/test/java/com/busease/           # Backend tests
├── frontend/
│   ├── src/
│   │   ├── pages/                       # React page components
│   │   ├── context/AuthContext.tsx       # Auth state management
│   │   ├── lib/axios.ts                 # API client
│   │   └── App.tsx                      # Router + Navigation
│   └── package.json
├── .github/workflows/ci.yml            # CI pipeline
├── pom.xml                              # Maven config
└── README.md
```

---

## 🏛️ Architecture Highlights

- **Pessimistic Locking** — Seat booking uses `PESSIMISTIC_WRITE` locks to prevent double-booking during concurrent requests
- **JWT Stateless Auth** — No server-side sessions; tokens carry user identity and role
- **Global Exception Handler** — `@RestControllerAdvice` provides consistent error JSON responses
- **Role-Based Access Control** — Admin endpoints protected by `ROLE_ADMIN` check
- **Glassmorphism UI** — Modern frosted-glass aesthetic with smooth animations
