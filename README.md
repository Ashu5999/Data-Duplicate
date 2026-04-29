# DDAS (Data Duplication Alert System)

A full-stack web application that prevents duplicate file uploads by generating a SHA-256 hash of file content and comparing it with existing records.

## Tech Stack

* **Frontend:** HTML, Tailwind CSS, Vanilla JavaScript
* **Backend:** Spring Boot (Java)
* **API:** REST APIs
* **Hashing:** SHA-256 (crypto.subtle)
* **Data Storage:** MySQL Database (via Spring Data JPA / Hibernate)

## Core Features

* **User Authentication:** Secure user registration and login endpoints.
* **Smart File Uploads:** Upload files (PDF, images, text, etc.) with automatic SHA-256 hash generation directly in the browser before sending to the backend.
* **Duplicate Detection:** Checks for duplicates based on file content hash rather than the file name. 
* **Duplicate Lookup / Check:** Manual search functionality to verify if a file hash or filename has already been registered in the system.
* **Clean UI:** Includes login, registration, upload system, and a dashboard for viewing records.
* **Error Handling:** Robust network, server, and timeout error handling on the frontend.

## Project Structure

* `Index.html` - Login UI
* `Register.html` - Registration UI
* `Dashboard.html` - Dashboard to view existing files
* `Upload.html` - File upload, hashing, and API integration
* **Backend (Spring Boot)** - Contains controllers (`AuthController`, `FileController`), models, and repositories.

## How It Works

1. **Authentication:** User registers and logs in.
2. **File Selection:** User selects a file to upload.
3. **Frontend Hashing:** A SHA-256 hash is generated on the client side.
4. **Transmission:** The file data and hash are sent to the Spring Boot backend.
5. **Backend Validation:** The backend connects to the MySQL database to check if the generated hash already exists.
6. **Result:** If a duplicate is found, the upload is rejected. Otherwise, it is stored successfully.

## API Endpoints

### Authentication
* **POST /api/auth/register**
  * **Input:** `email`, `password` (form-data)
  * **Output:** Success or error message.
* **POST /api/auth/login**
  * **Input:** `email`, `password` (form-data)
  * **Output:** Success or error message.

### Files
* **GET /api/files**
  * **Output:** List of all uploaded files.
* **POST /api/files**
  * **Input:** `file` (MultipartFile), `fileName`, `fileHash`, `uploadedBy`
  * **Output:** File uploaded successfully or Duplicate file detected.
* **POST /api/files/check**
  * **Input:** JSON payload `{"keyword": "..."}`
  * **Output:** Details on whether the given keyword (hash or filename) matches any existing records.

## Setup Instructions

### Backend (Spring Boot)
1. Ensure you have **Java 17+** and **MySQL** installed.
2. Create a MySQL database named `ddas_db`.
3. Open `ddas-backend/src/main/resources/application.properties` and verify your database credentials (`spring.datasource.username` and `spring.datasource.password`).
4. Run the Spring Boot application (e.g., using your IDE or `mvn spring-boot:run`).
5. Ensure the backend is running on `http://localhost:8080`.

### Frontend
1. Open the frontend files (`Index.html`, `Register.html`, etc.) in a modern web browser.
2. Make sure your browser allows local CORS requests if you are opening files directly via the `file://` protocol, or use a local development server (like VS Code Live Server).

## Future Improvements

* Store actual files in cloud storage (e.g., AWS S3).
* Add JWT-based robust authorization.
* Implement role-based access control (Admin vs User).
* Containerize the application using Docker for easier deployment.
