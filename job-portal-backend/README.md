# Mini Job Portal Backend

Spring Boot 3 + PostgreSQL + JWT + Swagger + Docker

## Run (local)
1. PostgreSQL me DB banao: `CREATE DATABASE jobportal;`
2. `src/main/resources/application.yml` me DB username/password check karo
3. `mvn spring-boot:run`
4. Swagger UI: http://localhost:8080/swagger-ui.html

## Run (Docker)
```
docker compose up --build
```

## Default Admin
`admin@jobportal.com` / `Admin@123`  (app start pe auto-create hota hai)

## Roles
`JOB_SEEKER`, `RECRUITER`, `ADMIN`  (register me sirf JOB_SEEKER / RECRUITER allowed)

## Test flow (Swagger / Postman)
1. `POST /api/auth/register` (RECRUITER) -> token copy
2. Swagger me **Authorize** -> `Bearer <token>` (sirf token paste karo)
3. `POST /api/recruiter/jobs` -> job post
4. Naya JOB_SEEKER register karo -> token
5. `GET /api/jobs?keyword=java` -> job search
6. `POST /api/seeker/jobs/{id}/apply` -> apply
7. Recruiter token se: `GET /api/recruiter/jobs/{id}/applications` -> `PATCH /api/recruiter/applications/{id}/status` `{ "status": "ACCEPTED" }`
8. Admin login -> `GET /api/admin/stats`

## API Summary
| Module | Endpoint |
|---|---|
| Auth | POST /api/auth/register, POST /api/auth/login |
| Public | GET /api/jobs (search+filter+page+sort), GET /api/jobs/{id} |
| Seeker | GET/PUT /api/seeker/profile, POST /api/seeker/skills, DELETE /api/seeker/skills/{skill}, POST /api/seeker/jobs/{id}/apply, GET /api/seeker/applications, DELETE /api/seeker/applications/{id} |
| Recruiter | GET/PUT /api/recruiter/company, POST/GET /api/recruiter/jobs, PUT/DELETE /api/recruiter/jobs/{id}, PATCH /api/recruiter/jobs/{id}/active?value=, GET /api/recruiter/jobs/{id}/applications, PATCH /api/recruiter/applications/{id}/status |
| Admin | GET /api/admin/users?role=, GET /api/admin/recruiters, PATCH /api/admin/users/{id}/status?enabled=, DELETE /api/admin/users/{id}, GET /api/admin/jobs, PATCH/DELETE /api/admin/jobs/{id}, GET /api/admin/stats |
