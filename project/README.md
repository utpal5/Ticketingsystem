# Ticketing System Backend

A comprehensive ticketing system backend built with Spring Boot, providing full support for IT help desk and customer service scenarios.

## Features

### Must-Have Features ✅

1. **Authentication & Authorization**
   - JWT-based login/logout
   - Role-based access control (User, Support Agent, Admin)
   - Secure password encoding with BCrypt

2. **User Dashboard**
   - Create tickets with subject, description, and priority
   - View personal tickets with status tracking
   - Add comments to tickets
   - Track ticket lifecycle (Open → In Progress → Resolved → Closed)

3. **Ticket Management**
   - Complete ticket lifecycle management
   - Assignment system for support agents
   - Comment threads with timestamps
   - Priority and status tracking

4. **Admin Panel**
   - Complete user management (CRUD operations)
   - Role assignment capabilities
   - System-wide ticket oversight
   - Force reassignment and status changes

5. **Access Control**
   - Users can only access their own tickets
   - Support agents can manage assigned tickets
   - Admins have full system access

### Good-to-Have Features ✅

1. **Email Notifications**
   - Ticket creation notifications
   - Status change alerts
   - Assignment notifications
   - Comment notifications

2. **Search & Filter**
   - Advanced search by subject, description
   - Filter by status, priority, creator, assignee
   - Pagination support

3. **Ticket Prioritization**
   - Four priority levels (Low, Medium, High, Urgent)
   - Priority-based sorting and filtering

4. **File Attachments**
   - Secure file upload/download
   - File management per ticket
   - Access control for attachments

5. **Rating System**
   - 5-star ticket resolution rating
   - Optional feedback collection
   - Rating analytics

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** with JWT
- **Spring Data JPA**
- **PostgreSQL**
- **Maven**
- **JavaMailSender** for email notifications

## API Endpoints

### Authentication
- `POST /api/auth/signin` - User login
- `POST /api/auth/signup` - User registration

### Tickets
- `GET /api/tickets/my-tickets` - Get user's tickets
- `GET /api/tickets/{id}` - Get ticket by ID
- `POST /api/tickets` - Create new ticket
- `PUT /api/tickets/{id}` - Update ticket
- `PUT /api/tickets/{id}/status` - Update ticket status
- `PUT /api/tickets/{id}/assign` - Assign ticket
- `DELETE /api/tickets/{id}` - Delete ticket

### Comments
- `GET /api/tickets/{ticketId}/comments` - Get ticket comments
- `POST /api/tickets/{ticketId}/comments` - Add comment

### Attachments
- `GET /api/tickets/{ticketId}/attachments` - Get attachments
- `POST /api/tickets/{ticketId}/attachments` - Upload attachment
- `GET /api/tickets/{ticketId}/attachments/{attachmentId}/download` - Download attachment
- `DELETE /api/tickets/{ticketId}/attachments/{attachmentId}` - Delete attachment

### Ratings
- `GET /api/tickets/{ticketId}/rating` - Get ticket rating
- `POST /api/tickets/{ticketId}/rating` - Rate ticket

### Admin Endpoints
- `GET /api/admin/users` - Manage users
- `POST /api/admin/users` - Create user
- `PUT /api/admin/users/{id}/role` - Update user role
- `PUT /api/admin/users/{id}/toggle-status` - Toggle user status
- `DELETE /api/admin/users/{id}` - Delete user
- `GET /api/admin/tickets` - View all tickets
- `GET /api/admin/dashboard/stats` - Dashboard statistics

## Configuration

Update `application.yml` with your database and email settings:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ticketing_system
    username: your_username
    password: your_password
  
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password

jwt:
  secret: your-jwt-secret-key
  expiration: 86400000  # 24 hours
```

## Setup Instructions

1. **Prerequisites**
   - Java 17 or higher
   - PostgreSQL database
   - Maven

2. **Database Setup**
   ```sql
   CREATE DATABASE ticketing_system;
   ```

3. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

4. **Default Admin User**
   After first run, create an admin user through the `/api/auth/signup` endpoint, then manually update the role in the database to 'ADMIN'.

## Security Features

- JWT token-based authentication
- Role-based access control
- Password encryption with BCrypt
- CORS configuration for frontend integration
- Request validation and sanitization

## Email Configuration

The system supports email notifications for:
- New ticket creation
- Ticket status changes
- Ticket assignments
- New comments (non-internal)

Configure your SMTP settings in `application.yml` for email functionality.

## File Upload

Files are stored in the configured upload directory (`uploads/` by default). The system supports:
- Secure file storage with unique filenames
- Content type validation
- File size limits (configurable)
- Access control for downloads

## Role Permissions

### User (Regular)
- Create tickets
- View own tickets
- Comment on own tickets
- Rate resolved/closed tickets

### Support Agent
- View assigned tickets
- Update ticket status
- Assign tickets to other agents
- Add comments (including internal)
- Upload attachments

### Admin
- Full system access
- User management
- Override any ticket operations
- View all system statistics
- Force assignments and status changes

## Development Notes

This is a production-ready backend that can be integrated with any frontend framework. The API is designed to be RESTful and includes comprehensive error handling, validation, and security measures.

For frontend integration, ensure CORS is properly configured for your domain in the `WebSecurityConfig` class.