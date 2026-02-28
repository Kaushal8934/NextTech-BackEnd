# NextTech Backend

## Technology Stack

- Java 17
- Spring Boot 4.0.2
- Spring Web MVC
- Spring Security (JWT)
- Spring Data JPA (Hibernate)
- PostgreSQL
- Spring Mail
- Maven
- Lombok
- Docker

## Setup Guide

### 1. Prerequisites

- Java 17+
- Maven (or use `./mvnw`)
- PostgreSQL

### 2. Configure Environment Variables

- `PORT` (default: `8080`)
- `DATABASE_URL` (default: `jdbc:postgresql://localhost:5432/next_tech_db`)
- `DB_USERNAME` (default: `postgres`)
- `DB_PASSWORD` (default: `postgres`)

### 3. Run Locally

```bash
./mvnw spring-boot:run
```

### 4. Build Jar

```bash
./mvnw clean package -DskipTests
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### 5. Run with Docker

```bash
docker build -t nexttech-backend .
docker run -p 8080:8080 --env PORT=8080 nexttech-backend
```

