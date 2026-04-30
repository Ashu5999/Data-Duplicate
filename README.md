# DDAS — Data Duplication Alert System

A full-stack web application that prevents duplicate file uploads by generating a **SHA-256 hash** of file content on the client side and comparing it against existing records in the database.

---

## Tech Stack

| Layer | Technology |
|---|---|
| **Frontend** | HTML, Tailwind CSS, Vanilla JavaScript |
| **Backend** | Spring Boot (Java 17+) |
| **Database** | H2 Embedded Database (via Spring Data JPA / Hibernate) |
| **Hashing** | SHA-256 (`crypto.subtle` Web API) |
| **API** | RESTful endpoints |

---

## Core Features

- 🔐 **User Authentication** — Secure registration and login with session-based access control.
- 🔑 **Password Reset** — Direct password recovery page allowing users to reset their security key using their registered email.
- 📤 **Smart File Uploads** — Upload files (PDF, images, text, etc.) with automatic SHA-256 hash generation in the browser before transmission.
- 🔍 **Duplicate Detection** — Detects duplicates based on file content hash (not file name), preventing identical files from being stored twice.
- 🗂️ **Dashboard** — View all uploaded file records with metadata.
- 🔎 **Manual Lookup** — Search by hash or filename to check if a file already exists in the system.
- 🛡️ **Auth Guard** — Protected pages redirect unauthenticated users to the login page.
- ⚠️ **Error Handling** — Robust network, server, and timeout error handling on the frontend.

---

## Project Structure

```
DDAS project/
├── Index.html              # Login page
├── Register.html           # Registration page
├── Dashboard.html          # File records dashboard
├── Upload.html             # File upload with hash generation
├── ResetPassword.html      # Password reset page
├── .gitignore              # Root-level ignores (.DS_Store, data/, etc.)
└── ddas-backend/
    ├── .gitignore          # Backend-level ignores (target/, .idea/, etc.)
    ├── pom.xml
    └── src/main/java/com/ddas/backend/
        ├── controller/
        │   ├── AuthController.java   # /api/auth/* endpoints
        │   └── FileController.java   # /api/files/* endpoints
        ├── model/
        │   ├── User.java             # User entity with JPA annotations
        │   └── FileData.java         # File metadata entity with checksum field
        ├── repository/
        │   ├── UserRepository.java
        │   └── FileRepository.java
        └── resources/
            └── application.properties
```

---

## How It Works

1. **Register / Login** — User creates an account or logs in.
2. **File Selection** — User selects a file on the Upload page.
3. **Client-Side Hashing** — A SHA-256 hash of the file content is generated in the browser using the Web Crypto API.
4. **Transmission** — The file data, hash, filename, and uploader info are sent to the Spring Boot backend.
5. **Backend Validation** — The backend queries the H2 database to check if the hash already exists.
6. **Result** — Duplicate files are rejected with a clear message; unique files are stored successfully.
7. **Password Reset** — Users can navigate to the Reset Password page, enter their registered email and new password, and update their credentials directly.

---

## API Endpoints

### Authentication — `/api/auth`

| Method | Endpoint | Input | Response |
|---|---|---|---|
| `POST` | `/api/auth/register` | `email`, `password` (form-data) | Success / error message |
| `POST` | `/api/auth/login` | `email`, `password` (form-data) | Success / error message |
| `POST` | `/api/auth/reset-password` | `email`, `newPassword` (form-data) | Success / error message |

### Files — `/api/files`

| Method | Endpoint | Input | Response |
|---|---|---|---|
| `GET` | `/api/files` | — | List of all uploaded file records |
| `POST` | `/api/files` | `file` (MultipartFile), `fileName`, `fileHash`, `uploadedBy` | Upload success or duplicate detected |
| `POST` | `/api/files/check` | JSON `{"keyword": "..."}` | Match result for given hash or filename |

---

## Setup Instructions

### Prerequisites
- Java 17+
- Maven

### 1. Database Setup

The project uses an embedded **H2 Database**. No separate database installation is required! The database file is automatically created in the `ddas-backend/data/` folder when the app runs.

### 2. Configure the Backend

Open `ddas-backend/src/main/resources/application.properties`. It is already configured for H2:

```properties
spring.datasource.url=jdbc:h2:file:./data/ddas_db
spring.datasource.username=sa
spring.datasource.password=password
```

**H2 Console:** You can access the database UI while the app is running by visiting `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/ddas_db`, Username: `sa`, Password: `password`).

### 3. Run the Backend

```bash
cd ddas-backend
mvn spring-boot:run
```

The backend will start at `http://localhost:8080`.

### 4. Open the Frontend

Open any of the HTML files in your browser, or use a local development server (e.g., VS Code Live Server) to avoid `file://` CORS restrictions.

> **Tip:** With VS Code Live Server, right-click `Index.html` → *Open with Live Server*.

---

## Future Improvements

- [ ] JWT-based stateless authentication & authorization
- [ ] Role-based access control (Admin vs User)
- [ ] Cloud file storage integration (e.g., AWS S3)
- [ ] Containerise the application with Docker & Docker Compose
- [ ] Email-based password reset with OTP verification

---

## Author

**Ashutosh Tiwari** · [GitHub](https://github.com/Ashu5999)
