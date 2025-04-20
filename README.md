# 🧩 User & Journal Management Microservices

This project is a **microservice-based system** built with **Java 21**, **Spring Boot 3**, and **Kafka**, supporting user management and journaling capabilities. It is secured via **JWT** and exposes APIs via **Swagger**. All services are containerized using **Docker**.

---

## 🏗️ Tech Stack

- Java 21
- Spring Boot 3
- Spring Security (JWT)
- Apache Kafka
- MySQL
- Docker & Docker Compose
- Swagger UI for testing

---

## 📦 Prerequisites

- Docker Desktop installed and running
- Ports **3037**, **8080**, **8081**, **9092** should be free
- Java 21 & Maven (for local jar builds)

---

## 🚀 Getting Started

### Step 1: Package Services

Run the following to generate jar files (without running tests):

```bash
mvn clean package -DskipTests
```

### Step 2: Run with Docker Compose

```bash
docker-compose up --build
```

### Step 3: To Stop / Clean Up

- 🛑 Graceful shutdown:
  ```bash
  docker-compose down
  ```

- 🧹 Full cleanup:
  ```bash
  docker-compose down -v --remove-orphans
  ```

---

## 🔐 Authentication Flow

1. Register a user via `/auth/register`
2. Login using `/auth/login`
3. Cope the token generated without inverted commas to access protected APIs  
   Paste token in Swagger UI's **Authorize** section in top right corner using:
   ```
   <your_token> 
   ```

---

## 🧪 Sample Test Data

### 🛡️ Admin User

```json
{
  "name": "Admin Bhai",
  "email": "admin@example.com",
  "password": "admin123",
  "roles": ["ROLE_ADMIN"]
}
```

### 👤 Normal Users

```json
{
  "name": "User One",
  "email": "user1@example.com",
  "password": "user123",
  "roles": ["ROLE_USER"]
}
```

```json
{
  "name": "User Two",
  "email": "user2@example.com",
  "password": "user123",
  "roles": ["ROLE_USER"]
}
```

---

## 📘 API Overview

### 📦 User Management Service

#### 🔐 AuthController

- `POST /auth/register` – Register user
- `POST /auth/login` – Login and get JWT

#### 👥 UserController

- `GET /users` – List all users (**ADMIN only**)
- `GET /users/email/{email}` – Get user by email (User or Admin)
- `PUT /users/email/{email}` – Update user (ADMIN only)
- `DELETE /users/email/{email}` – Delete user (ADMIN only)

---

### 📒 Journal Service

#### 🗃️ JournalController

- `GET /events` – All journal entries (**ADMIN only**)
- `GET /events/email/self` – Logged-in user's entries
- `GET /events/email?email=user@example.com` – Admin/User fetch by email
- `GET /events/type?type=USER_CREATED` – Filter by event type

---

## 🌐 Swagger Access

- User Management Swagger: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Journal Service Swagger: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

---

> Thanks For Reading!!