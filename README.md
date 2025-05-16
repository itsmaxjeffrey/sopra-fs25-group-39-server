# SoPra FS25 Group 39 – Backend

## Introduction

This project is the backend for a collaborative moving platform, developed as part of the SoPra (Software Engineering and Project Management) course at the University of Zurich. The goal is to connect people who need help moving with drivers who can offer transportation services, providing a secure, user-friendly, and feature-rich experience for both parties.

The backend exposes a RESTful API for all core business logic, user management, contract and offer handling, and ratings. It is designed for extensibility, security, and ease of deployment.

---

## Technologies Used

- **Java 17** – Modern, robust, and type-safe programming language.
- **Spring Boot** – For REST API, dependency injection, and application configuration.
- **Gradle** – Build automation and dependency management.
- **JUnit 5** & **Mockito** – Unit and integration testing.
- **Docker** – Containerization for easy deployment.
- **GitHub Actions** – Continuous Integration/Continuous Deployment (CI/CD).
- **Apache License 2.0** – Open source license.

---

## High-Level Components

The backend is organized into several main components, each with a clear responsibility and well-defined API endpoints.

### 1. Authentication & Registration

Handles user registration, login, and session management.  
- **Main classes:**  
  - [`AuthController`](src/main/java/ch/uzh/ifi/hase/soprafs24/security/authentication/controller/AuthController.java)  
  - [`RegistrationController`](src/main/java/ch/uzh/ifi/hase/soprafs24/security/registration/controller/RegistrationController.java)  
- **Features:**  
  - Register as a requester or driver
  - Login/logout with token-based authentication
  - Secure password storage and validation

### 2. User Management

Manages user profiles, updates, and account deletion.  
- **Main classes:**  
  - [`UserController`](src/main/java/ch/uzh/ifi/hase/soprafs24/user/controller/UserController.java)  
  - [`AccountSecurityController`](src/main/java/ch/uzh/ifi/hase/soprafs24/security/account/controller/AccountSecurityController.java)  
- **Features:**  
  - View and update user profiles
  - Delete account with email verification
  - Public/private profile distinction

### 3. Contract Management

Allows requesters to create, update, and manage moving contracts.  
- **Main class:** [`ContractController`](src/main/java/ch/uzh/ifi/hase/soprafs24/controller/ContractController.java)  
- **Features:**  
  - Create contracts with detailed requirements (weight, volume, locations, etc.)
  - View, update, cancel, and delete contracts
  - Filter/search contracts by location, price, and other criteria

### 4. Offer Management

Enables drivers to make offers on contracts and manage their offers.  
- **Main class:** [`OfferController`](src/main/java/ch/uzh/ifi/hase/soprafs24/controller/OfferController.java)  
- **Features:**  
  - Drivers can make, update, and delete offers on contracts
  - Requesters can accept or reject offers
  - Automatic status transitions for contracts and offers

### 5. Ratings & Feedback

Lets users rate each other after contract completion.  
- **Main class:** [`RatingController`](src/main/java/ch/uzh/ifi/hase/soprafs24/controller/RatingController.java)  
- **Features:**  
  - Rate users after a contract is completed
  - View ratings and average scores
  - Only participants of a contract can rate each other

> For a full list of endpoints, request/response formats, and error codes, see the [API documentation](doc/api/api_documentation.md).

---

## Launch & Deployment

### Prerequisites

- Java 17
- [Gradle](https://gradle.org/) (or use the included Gradle Wrapper)
- (Optional) Docker for containerized deployment

### Local Development

**Clone the repository:**
```bash
git clone <your-repo-url>
cd sopra-fs25-group-39-server
```

**Build the project:**
```bash
./gradlew build
```

**Run the backend:**
```bash
./gradlew bootRun
```
The server will be available at [http://localhost:8080](http://localhost:8080).

**Run tests:**
```bash
./gradlew test
```

**Development mode (auto-reload):**
Open two terminals:
```bash
./gradlew build --continuous
```
and in the other:
```bash
./gradlew bootRun
```
To skip tests during continuous build:
```bash
./gradlew build --continuous -xtest
```

### Docker Deployment

1. Build the Docker image:
   ```bash
   docker build -t sopra-fs25-group-39-server .
   ```
2. Run the container:
   ```bash
   docker run -p 8080:8080 sopra-fs25-group-39-server
   ```

### External Dependencies

- No external database is required; the application uses an in-memory database by default.
- For production, configure your own database in `application.properties`.

### Release & CI/CD

- All changes to the main branch are automatically built and pushed to DockerHub via GitHub Actions.
- See `.github/workflows/` for CI/CD configuration.

---

## API Overview

- **Authentication:** Register, login, and logout with secure token-based authentication.
- **User Management:** View, update, and delete user profiles. Public/private profile distinction.
- **Contracts:** Create, update, cancel, delete, and search for contracts. Only requesters can create contracts.
- **Offers:** Drivers can make offers on contracts. Requesters can accept or reject offers. Status transitions are enforced.
- **Ratings:** After contract completion, users can rate each other. Only participants can rate.

See [API documentation](doc/api/api_documentation.md) for detailed endpoint descriptions, request/response examples, and error codes.

---

## Testing

- All major endpoints are covered by unit and integration tests using JUnit 5 and Mockito.
- To run all tests:
  ```bash
  ./gradlew test
  ```
- Test coverage includes authentication, user management, contracts, offers, and ratings.

---

## Roadmap

Here are the top features that could be added by new contributors:

1. **Payment Integration:** Implement wallet, payment, and refund endpoints.
2. **Notifications:** Add real-time notifications for contract and offer updates.
3. **Map Features:** Implement backend support for map-based contract search and visualization.

---

## Authors & Acknowledgments

- [Your Team Members Here]
- Special thanks to the SoPra teaching team at the University of Zurich.

---

## License

This project is licensed under the [Apache License 2.0](LICENSE). 