# HARI Exercise Tracker Backend

A RESTful backend API for the HARI (Health Assessment and Rehabilitation Initiative) Exercise Tracker application. This Spring Boot-based service manages user data, exercise records, and health coach interactions, connected to a MySQL database.

## Overview

The HARI Exercise Tracker Backend provides a comprehensive API for tracking exercise activities, managing user accounts, and facilitating health coach-patient interactions. The system supports secure authentication with JWT tokens and offers detailed exercise reporting capabilities.

## Technology Stack

- **Framework**: Spring Boot 3.5.7
- **Language**: Java 17
- **Database**: MySQL
- **Security**: Spring Security with JWT authentication
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Maven
- **Container**: Docker support included

### Key Dependencies

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Security
- MySQL Connector
- Lombok
- JSON Web Tokens (jjwt)

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Docker (optional, for containerized deployment)

## Database Setup

The application requires a MySQL database. Configure your database connection in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3308/hari_exercise_tracker_prod
spring.datasource.username=prod
spring.datasource.password=your_password
```

Create a `secrets.properties` file in the same directory for sensitive configuration:

```properties
SECRET_KEY=your_jwt_secret_key_here
```

The application uses Hibernate's `ddl-auto=update` to automatically create/update database schema.

## Installation & Running

### Local Development

1. Clone the repository:
```bash
git clone https://github.com/TrentChen27/HARI-TEST-Backend.git
cd HARI-TEST-Backend
```

2. Build the project:
```bash
./mvnw clean install
```

3. Run the application:
```bash
./mvnw spring-boot:run
```

The server will start on `http://localhost:8080`.

### Docker Deployment

1. Build the application JAR:
```bash
./mvnw clean package
```

2. Build the Docker image:
```bash
docker build -t hari-exercise-tracker .
```

3. Run the container:
```bash
docker run -p 8080:8080 hari-exercise-tracker
```

## API Endpoints

### User Management

- `POST /api/users/register` - Register a new user
- `POST /api/users/login` - Login with username/password
- `POST /api/users/login-uuid` - Login with device UUID (remember me)

### Exercise Records

- `POST /api/records/{userId}` - Create a new exercise record
- `PUT /api/records/{recordId}` - Update an exercise record
- `GET /api/records/user/{userId}/today` - Get today's exercise records
- `GET /api/records/user/{userId}/history` - Get exercise history
- `GET /api/records/user/{userId}/all` - Get all records for a user
- `GET /api/records/user/{userId}/report/7-days` - Get 7-day exercise report
- `DELETE /api/records/{recordId}` - Delete an exercise record

### Health Coach

- Health coach management endpoints available via `/api/coaches`

### Dashboard

- Dashboard and analytics endpoints available via `/api/dashboard`

## Security

The application uses JWT (JSON Web Tokens) for authentication. All protected endpoints require a valid JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

Security is configured using Spring Security with custom filters for JWT validation.

## Project Structure

```
src/main/java/com/pitt/hari_exercise_tracker/
├── config/              # Security and application configuration
├── controller/          # REST API controllers
├── dto/                 # Data Transfer Objects
├── mapper/              # Entity-DTO mappers
├── models/              # JPA entity models
├── repository/          # JPA repositories
├── service/             # Business logic services
└── util/                # Utility classes
```

## Development

- The application uses Lombok to reduce boilerplate code. Ensure your IDE has Lombok plugin installed.
- JPA entities are automatically mapped to database tables.
- All API responses use DTOs for data transfer and security.
