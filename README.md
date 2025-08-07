# Ticketing System - Full Stack Application

## 💪 Objective

Build a comprehensive full-stack **Ticketing System** that simulates real-world IT support or customer service scenarios. This system supports:

* Ticket raising, tracking, commenting
* Role-based access for Users, Admins, and Support Agents
* Admin panel for user and ticket management

---

## 🔧 Tech Stack

### Backend

* **Language**: Java/Kotlin
* **Framework**: Spring Boot
* **Database**: PostgreSQL

### Frontend

* **Framework**: Next.js (React)

---

## ✅ Must-Have Features

### 1. Authentication & Authorization

* User login/logout
* Role-based access control:

  * **User**: Raise & manage own tickets
  * **Support Agent**: Assigned tickets, comment, update statuses
  * **Admin**: Manage users & all tickets

### 2. User Dashboard

* Raise new tickets (subject, description, priority)
* View own tickets and their statuses
* Add comments to own tickets
* View full ticket history

### 3. Ticket Management

* Ticket lifecycle:

  * `Open → In Progress → Resolved → Closed`
* Reassign ticket (based on role permissions)
* Comment thread with timestamps & user info
* Track ticket owner and assignee

### 4. Admin Panel

* **User Management**:

  * Add/remove users
  * Assign roles (Admin, Support Agent, User)
* **Ticket Management**:

  * View all tickets
  * Force reassign or close any ticket
  * Monitor all ticket activity

### 5. Access Control

* **Admin**: Full access to all system features
* **Support Agent**: Assigned tickets only
* **User**: Own tickets only

---

Have Features

### 1. Email Notifications

* Ticket creation, assignment, status changes
* Templated messages (e.g., "Ticket #123 assigned to you")

### 2. Search & Filter

* Search tickets by subject, status, priority, or user
* Filter by status or assigned agent

### 3. Ticket Prioritization

* Priority levels: Low, Medium, High, Urgent
* Sort or filter by priority

### 4. File Attachments

* Attach screenshots or files to tickets
* Secure upload/download

### 5. Rate Ticket Resolution

* Rate resolution (1-5 stars)
* Optional feedback form

---

## 🛋️ Running the Project Locally

### Backend (Spring Boot)

1. Navigate to the Spring Boot project directory
2. Set up PostgreSQL and update `application.properties`
3. Run using:

```bash
./mvnw spring-boot:run
# OR
./gradlew bootRun
```

### Frontend (Next.js)

1. Navigate to the frontend project
2. Install dependencies:

```bash
npm install
```

3. Start development server:

```bash
npm run dev
```

> Default port: `5173`

### Configure CORS (Spring Boot)

Ensure port `5173` is allowed in Spring Boot CORS config:

```java
.allowedOrigins("http://localhost:5173")
```

---

## 🚀 Future Improvements

* Dockerize frontend & backend
* Deploy with Nginx or cloud services
* Real-time updates using WebSocket/SSE
* Integration with external ticketing tools (e.g., JIRA)

---

## 📖 License

MIT License
