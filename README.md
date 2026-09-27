# Railway Management System

Backend for a railway ticket booking system — train search, seat-level booking with
concurrency-safe reservation, JWT authentication with role-based access control, and
Razorpay payment integration.

## Tech Stack
- Java 17, Spring Boot 3.3
- Spring Data JPA (Hibernate) + MySQL
- Spring Security + JWT
- Razorpay Payment Gateway
- Maven

## Package Structure
```
com.railwaymanagement
├── entity        # JPA entities (User, Train, Station, Route, Schedule, Coach, Seat, Booking, Payment)
├── enums         # Role, CoachType, SeatType, BookingStatus, PaymentStatus, ScheduleStatus
├── repository    # Spring Data JPA repositories
├── service        # Business logic
├── controller     # REST controllers
├── dto           # Request/response DTOs
├── security      # JWT filter, auth provider, security config
├── config        # Beans, CORS, Razorpay client config
└── exception     # Custom exceptions + global exception handler
```

## Setup

1. Create a MySQL database:
   ```sql
   CREATE DATABASE railway_db;
   ```

2. Environment variables — two options:

   **Option A (recommended): VS Code launch config.** A `.vscode/launch.json`
   is already included with `DB_URL`, `JWT_SECRET`, etc. wired up. It's
   gitignored, so it never gets committed. Just open
   `RailwayManagementSystemApplication.java` and open `.vscode/launch.json`,
   replace `PUT_YOUR_MYSQL_ROOT_PASSWORD_HERE` with your actual MySQL root
   password, then run via Run and Debug (Ctrl+Shift+D) → select
   "RailwayManagementSystemApplication" → ▶. This is what keeps secrets out
   of your commits permanently — no need to revert anything before pushing.

   **Option B: system/OS environment variables**, if you're not using VS
   Code's launch config:
   ```
   DB_URL=jdbc:mysql://localhost:3307/railway_db
   DB_USERNAME=root
   DB_PASSWORD=your_password
   JWT_SECRET=<generate with: openssl rand -base64 64>
   RAZORPAY_KEY_ID=your_key_id
   RAZORPAY_KEY_SECRET=your_key_secret
   ```

   Note the default DB port here is set to **3307** — adjust in
   `application.properties` or `launch.json` if your MySQL runs on the
   standard 3306.

3. Run via VS Code's Run and Debug panel (or `mvn spring-boot:run` if you have
   Maven on your PATH).

App runs on `http://localhost:8081` by default.

## Auth Endpoints
| Method | Endpoint             | Access | Description                     |
|--------|-----------------------|--------|----------------------------------|
| POST   | `/api/auth/register`  | Public | Create a USER account, returns JWT |
| POST   | `/api/auth/login`     | Public | Authenticate, returns JWT       |

Include the token on subsequent requests: `Authorization: Bearer <token>`

## RBAC Rules (configured in `SecurityConfig`)
- `/api/auth/**` — public
- `/api/trains/**` — public (search/browse doesn't require login)
- `/api/admin/**` — `ADMIN` only
- `/api/staff/**` — `STAFF` or `ADMIN`
- `/api/bookings/**`, `/api/payments/**` — any authenticated user

ADMIN/STAFF accounts are not self-registerable — provision them directly in
the DB or via a future admin-only endpoint. `/api/auth/register` always
creates a plain `USER`.

## Status
- [x] Entity layer (schema locked)
- [x] JWT auth + RBAC
- [x] User repository
- [ ] Train/Route/Schedule/Coach/Seat repositories
- [ ] Booking flow (concurrency-safe seat reservation)
- [ ] Razorpay integration
- [ ] Admin CRUD endpoints
