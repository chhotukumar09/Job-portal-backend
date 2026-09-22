# 🚀 Mini Job Portal

A backend job portal built with **Spring Boot**, **PostgreSQL**, **JWT authentication**. Job seekers can search and apply to jobs, recruiters can post jobs and review applicants, and admins can manage the whole platform.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

---

## ✨ Features

**Job Seeker**
- Register / login
- Create & update profile, manage skills
- Search and filter jobs
- Apply to jobs, track application status
- Withdraw a pending application

**Recruiter**
- Register / login
- Company profile
- Post, update, close, and delete jobs
- View applicants per job
- Shortlist, accept, or reject applications

**Admin**
- Manage users (enable/disable/delete)
- Manage recruiters
- Manage all jobs
- Platform-wide statistics dashboard

---

## 🏗️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3, Spring Data JPA, Spring Security, JWT (jjwt) |
| Database | PostgreSQL |
| API Docs | springdoc-openapi (Swagger UI) |
| Frontend | React 18, Vite, React Router, Axios |
| Auth | Stateless JWT, role-based authorization |
| Deployment | Docker, Docker Compose |

---

## 📁 Project Structure

```
job-portal/
├── job-portal-backend/          # Spring Boot REST API
│   ├── pom.xml
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── src/main/
│       ├── resources/
│       │   └── application.yml
│       └── java/com/jobportal/
│           ├── JobPortalApplication.java
│           │
│           ├── config/          → SecurityConfig, OpenApiConfig, DataInitializer (seeds default admin)
│           ├── security/        → JwtService, JwtAuthFilter, CustomUserDetailsService, CurrentUser
│           │
│           ├── entity/          → User, JobSeekerProfile, RecruiterProfile, Job, Application
│           ├── enums/           → Role, JobType, ApplicationStatus
│           ├── repository/      → Spring Data JPA repositories
│           ├── specification/   → JobSpecification (dynamic search & filter)
│           │
│           ├── dto/             → Request/response records with validation annotations
│           ├── mapper/          → EntityMapper (entity → DTO)
│           │
│           ├── service/         → AuthService, JobSeekerService, RecruiterService,
│           │                      JobService, ApplicationService, AdminService
│           ├── controller/      → AuthController, JobController (public), JobSeekerController,
│           │                      RecruiterController, AdminController
│           └── exception/       → GlobalExceptionHandler, ErrorResponse, custom exceptions
│
└── job-portal-frontend/         # React (Vite) client
    ├── package.json
    ├── .env.example
    └── src/
        ├── api/                 → client.js (axios + JWT interceptor), services.js
        ├── context/             → AuthContext.jsx
        ├── components/          → Navbar, Footer, JobCard, JobList, Pagination,
        │                          StatusBadge, SubNav, ProtectedRoute
        ├── pages/
        │   ├── Home.jsx, Jobs.jsx, JobDetails.jsx, Login.jsx, Register.jsx
        │   ├── seeker/          → Profile.jsx, MyApplications.jsx
        │   ├── recruiter/       → MyJobs.jsx, JobForm.jsx, Applicants.jsx, Company.jsx
        │   └── admin/           → AdminDashboard.jsx
        └── utils/format.js
```

---

## 🗃️ Data Model

```
User ──1:1── JobSeekerProfile ──1:N── Application ──N:1── Job ──N:1── RecruiterProfile ──1:1── User
```

- A `User` has exactly one role: `JOB_SEEKER`, `RECRUITER`, or `ADMIN`.
- A `JobSeekerProfile` / `RecruiterProfile` is auto-created at registration, based on role.
- An `Application` links one `JobSeekerProfile` to one `Job`, with a unique constraint so a seeker can't apply twice to the same job.

---

## ⚙️ Getting Started

### Prerequisites
- Java 17+
- Maven
- PostgreSQL 14+
- Node.js 18+
- (Optional) Docker & Docker Compose

### 1. Backend

```bash
cd job-portal-backend

# Create the database
psql -U postgres -c "CREATE DATABASE jobportal;"

# Configure src/main/resources/application.yml (or use env vars)
#   DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET

mvn spring-boot:run
```

Backend runs at **http://localhost:8080**
Swagger UI: **http://localhost:8080/swagger-ui.html**

A default admin account is seeded on first run:
```
email:    admin@jobportal.com
password: Admin@123
```

### 2. Frontend

```bash
cd job-portal-frontend
cp .env.example .env     # set VITE_API_URL if backend isn't on localhost:8080
npm install
npm run dev
```

Frontend runs at **http://localhost:5173**

### 3. Run everything with Docker

```bash
cd job-portal-backend
docker compose up --build
```

This starts PostgreSQL and the backend together. Run the frontend separately with `npm run dev`.

---

## 🔑 Environment Variables

**Backend** (`application.yml` or env vars)

| Variable | Default | Description |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/jobportal` | Database URL |
| `DB_USERNAME` | `postgres` | Database user |
| `DB_PASSWORD` | `postgres` | Database password |
| `JWT_SECRET` | (generated) | Base64 HMAC key for signing JWTs |
| `ADMIN_EMAIL` | `admin@jobportal.com` | Default admin email |
| `ADMIN_PASSWORD` | `Admin@123` | Default admin password |

**Frontend** (`.env`)

| Variable | Default | Description |
|---|---|---|
| `VITE_API_URL` | `http://localhost:8080` | Backend base URL |

> ⚠️ Don't commit real secrets. Use `.env` / environment variables in production and keep `JWT_SECRET` out of version control.

---

## 📡 API Overview

All endpoints are prefixed with `/api`. Full interactive docs are in Swagger UI.

| Module | Method & Path |
|---|---|
| Auth | `POST /auth/register`, `POST /auth/login` |
| Public Jobs | `GET /jobs` (search + filter + pagination + sort), `GET /jobs/{id}` |
| Job Seeker | `GET/PUT /seeker/profile`, `POST /seeker/skills`, `DELETE /seeker/skills/{skill}`, `POST /seeker/jobs/{id}/apply`, `GET /seeker/applications`, `DELETE /seeker/applications/{id}` |
| Recruiter | `GET/PUT /recruiter/company`, `POST/GET /recruiter/jobs`, `PUT/DELETE /recruiter/jobs/{id}`, `PATCH /recruiter/jobs/{id}/active`, `GET /recruiter/jobs/{id}/applications`, `PATCH /recruiter/applications/{id}/status` |
| Admin | `GET /admin/users`, `GET /admin/recruiters`, `PATCH /admin/users/{id}/status`, `DELETE /admin/users/{id}`, `GET /admin/jobs`, `PATCH/DELETE /admin/jobs/{id}`, `GET /admin/stats` |

**Authorization:** send `Authorization: Bearer <token>` on every protected request. Tokens are issued by `/api/auth/login` and `/api/auth/register`.

---

## 🧪 Example Flow

1. Register as a recruiter → `POST /api/auth/register`
2. Post a job → `POST /api/recruiter/jobs`
3. Register as a job seeker → `POST /api/auth/register`
4. Search jobs → `GET /api/jobs?keyword=java&location=delhi`
5. Apply → `POST /api/seeker/jobs/{id}/apply`
6. Recruiter reviews applicants → `GET /api/recruiter/jobs/{id}/applications`
7. Recruiter updates status → `PATCH /api/recruiter/applications/{id}/status`
8. Admin checks platform stats → `GET /api/admin/stats`

MIT — free to use and modify.
