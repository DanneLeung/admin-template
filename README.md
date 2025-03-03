# Admin template

## Project Overview

This is a modern admin template built with Vue 3 (Frontend) and Spring Boot (Backend), featuring user authentication, role-based access control.

## Architecture

### Frontend

- **Framework**: Vue 3 with Vite

- **UI Components**: Modern component-based architecture
- **State Management**: Vuex store implementation
- **Routing**: Vue Router for navigation

### Backend

- **Framework**: Spring Boot 3.1.0
- **Security**: Spring Security with JWT authentication
- **Database**: MySQL with JPA/Hibernate
- **Cache**: Redis implementation
- **API Documentation**: SpringDoc (Swagger UI)

## Key Features

### User Management

- User authentication and authorization
- Role-based access control
- Menu permission management
- Password policy enforcement
- Login attempt monitoring

### System Configuration

#### Backend Configuration

##### Development Environment

- Database: MySQL 8.0
- Redis for caching
- Hibernate with auto DDL generation
- Detailed SQL logging

##### Production Environment

- Optimized database connection pool
- Disabled auto DDL generation
- Production-grade logging configuration
- Disabled Swagger UI

#### Security Features

- JWT-based authentication
- CORS configuration for frontend integration
- Password policy enforcement
- Login attempt limiting

## Technical Stack

### Backend Dependencies

- Spring Boot Starters (Web, Security, Data JPA, Validation, AOP, Cache, Redis)
- MySQL Connector
- JWT Implementation (jjwt)
- Lombok for boilerplate reduction
- MapStruct for object mapping

### Frontend Setup

- Vue 3 with Vite build tool
- Modern JavaScript features
- Responsive design implementation

## Configuration

### Application Properties

- Server runs on port 8080
- API context path: `/api`
- Configurable CORS settings
- Customizable password policies
- Adjustable login attempt limits

### Development Setup

1. Configure MySQL database
2. Set up Redis instance
3. Configure application-dev.yml for local development
4. Start backend server
5. Launch frontend development server

## API Documentation

Swagger UI is available at `/api/swagger-ui.html` in development environment

## Security Considerations

- Secure password storage
- JWT token management
- Rate limiting implementation
- Environment-specific security configurations

## Performance Optimizations

- Connection pooling with HikariCP
- Redis caching implementation
- Optimized production configurations
- Efficient resource management
