# Book Management API

## Overview

The **Book Management API** is a RESTful web service built using **Spring Boot**, following a **layered architecture**. It provides functionalities for managing books and user authentication.

## Technologies Used

- **Spring Boot** (Backend Framework)
- **Spring Data JPA** (Database Interaction)
- **Spring Security** (Authentication & Authorization)
- **Swagger** (API Documentation)
- **PostgreSQL** (Database)
- **Maven** (Dependency Management)

## Project Architecture

The project follows a **layered architecture**, consisting of:

- **Controller Layer**: Handles API requests and responses.
- **Service Layer**: Contains business logic.
- **Repository Layer**: Communicates with the database using JPA.
- **Entity Layer**: Defines the data models.
- **Security Layer**: Manages authentication and authorization.

---

## API Endpoints

### Book Controller

| Method | Endpoint                       | Description                  |
| ------ | ------------------------------ | ---------------------------- |
| GET    | `/api/books/{id}`              | Retrieve a book by its ID    |
| PUT    | `/api/books/{id}`              | Update a book by its ID      |
| DELETE | `/api/books/{id}`              | Delete a book by its ID      |
| GET    | `/api/books`                   | Retrieve a list of all books |
| POST   | `/api/books`                   | Add a new book               |
| GET    | `/api/books/author/{authorId}` | Retrieve books by author ID  |

### Authentication Controller

| Method | Endpoint                | Description                              |
| ------ | ----------------------- | ---------------------------------------- |
| POST   | `/api/v1/auth/register` | Register a new user                      |
| POST   | `/api/v1/auth/login`    | Authenticate a user and generate a token |

---

## How to Run the Project

### Prerequisites

- Install **Java 17+**
- Install **Maven**
- Setup **PostgreSQL** and configure `application.properties`

### Steps to Run

1. Clone the repository:
   ```sh
   git clone https://github.com/batuakdogan/book-management-api.git
   cd BookManagement
   ```
2. Configure database settings in `src/main/resources/application.properties`.
3. Build and run the project:
   ```sh
   mvn spring-boot:run
   ```
4. Access API documentation via Swagger at:
   ```
   http://localhost:8080/swagger-ui.html
   ```


