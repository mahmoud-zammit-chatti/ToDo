📌 Project Overview

This project is a backend REST API built with Spring Boot, focused on user authentication, authorization, and task management.

The application follows a layered architecture and implements JWT-based authentication with refresh tokens to ensure stateless access control while supporting secure session management.

The goal of this project is to practice and demonstrate backend engineering fundamentals, including clean architecture, security, and REST API design.

✨ Features

User registration and login

JWT authentication (stateless)

Refresh token mechanism for session management

Role-based access control (USER / ADMIN)

Secure logout with refresh token revocation

Task management (CRUD operations)

Centralized exception handling

Input validation using DTOs

Clean separation of concerns (Controller / Service / Repository / Security)

🛠 Tech Stack

Java

Spring Boot

Spring Security

JWT (JSON Web Tokens)

PostgreSQL / Oracle

JPA / Hibernate

Maven

Linux development environment

🔐 Authentication Flow (High-Level)

User registers or logs in using username and password.

Server returns:

Access Token (JWT – short-lived)

Refresh Token (stored in database)

JWT is used to access protected endpoints.

When the JWT expires:

Client sends the refresh token to /auth/refresh

Server validates it and issues a new JWT.

On logout:

Refresh token is revoked in the database.

This approach keeps the API stateless while allowing secure session control.

🗂 Project Architecture

The project is organized using a layered architecture:

Controllers – Handle HTTP requests

Services – Business logic

Repositories – Database access

Security – JWT filters, authentication, authorization

DTOs – Request/response models

Entities – JPA entities

Each domain (users, tasks, security) is separated into its own package.

🚧 Project Status

This project is actively under development.

Planned improvements:

Advanced task features

Pagination and filtering

Better error responses

Testing (unit & integration)

API documentation (Swagger)

📚 What I’m Learning

Spring Security internals (filters, authentication flow)

Stateless vs stateful authentication

Refresh token design and security trade-offs

Clean backend architecture

REST API best practices
