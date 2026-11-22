# Vincula POA

Patient and healthcare unit management and monitoring system for the municipality of Porto Alegre.

**Vincula POA** aims to improve continuity of healthcare by identifying patients who initiate care at Primary Health Units (UBS) and assisting in tracking demands, treatments, and health indicators.

## Architecture

```
Frontend (React)  ←→  Backend (Spring Boot API)  ←→  PostgreSQL
```

## Technologies Used

### Frontend
- JavaScript
- React
- Axios
- Leaflet

### Backend
- Java 17+
- Spring Boot
- Spring Security
- Spring Data JPA / Hibernate
- JWT
- JUnit
- OpenStreetMap
- GeoJSON

### Database
- PostgreSQL

## Features

- Healthcare staff authentication and profile management
- Role-based access control (municipal management and standard users)
- Registration and listing of:
  - Patients/Users
  - Primary Health Units (UBS)
  - Healthcare Staff
  - Healthcare Demands
- Interactive map of Primary Health Units (UBS)
- Monitoring indicators
- System action auditing

## How to Run the Project

### 1. Clone the repository
```bash
git clone https://github.com/lucassoller/vincula-poa.git
```

### 2. Backend

```bash
cd backend
./mvnw spring-boot:run
```

Configure `application.properties`:

```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
spring.datasource.username=your_user
spring.datasource.password=your_password
frontend.url=http://localhost:5173
mail.token=(generated on the Resend website)
jwt.secret=(secret key used to sign/encrypt authentication tokens, can be any text string)
```

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

or

```bash
npm start
```

Configure `.env`:

```env
VITE_API_URL=http://localhost:8080
```

### 4. Docker Compose

The project includes a Docker Compose configuration to simplify running the system, including the backend, frontend, and PostgreSQL database.

With Docker installed, run the following command in the project root:

```bash
docker compose up --build
```

### 5. Database

A local installation and configuration of PostgreSQL is required (if running outside Docker).

---

## Deployment

- Frontend → Vercel
- Backend → Railway
- Database → PostgreSQL (Railway)

Note: In production, update the frontend API URL via `VITE_API_URL` and configure the environment variables in the backend's `application.properties` file.  
Full step-by-step instructions are available in `steb_by_step.txt`.

## Author

**Lucas Soller**

Project developed as a full-stack system for the Bachelor's Thesis (TCC) in Computer Science at UFRGS – 2026.

---
