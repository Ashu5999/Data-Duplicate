# DDAS (Data Duplication Alert System)

A full-stack web application that prevents duplicate file uploads by generating a SHA-256 hash of file content and comparing it with existing records.

## Tech Stack

* **Frontend:** HTML, Tailwind CSS, JavaScript
* **Backend:** Spring Boot (Java)
* **API:** REST APIs
* **Hashing:** SHA-256 (crypto.subtle)
* **Data Storage:** In-memory repository (can be extended to MySQL)

## Core Features

* Upload real files (PDF, images, etc.)
* Automatic SHA-256 hash generation in browser
* Duplicate detection based on file content (not file name)
* Backend validation of duplicates
* Clean UI with dashboard and upload system
* Error handling (network, server, timeout)

## Project Structure

* `Index.html` - Login UI
* `Dashboard.html` - UI dashboard
* `Upload.html` - File upload, hashing, and API integration
* **Backend (Spring Boot)** - `FileController`, `FileRepository`, `FileData`

## How It Works

1. User selects a file.
2. SHA-256 hash is generated in the frontend.
3. File and hash are sent to the backend using `FormData`.
4. Backend checks for duplicates using the generated hash.
5. If a duplicate is found, the upload is rejected.
6. If no duplicate is found, the file is stored successfully.

## API Endpoint

**POST /api/files**

### Input Parameters
* `file` (MultipartFile)
* `fileName` (String)
* `fileHash` (String)
* `uploadedBy` (String)

### Output Response

```json
{
  "status": "success" | "error",
  "message": "File uploaded successfully" | "Duplicate file detected"
}
```

## Setup Instructions

1. Clone the repository.
2. Run the Spring Boot backend application.
3. Ensure the backend is running on `http://localhost:8080`.
4. Open the frontend (`Index.html` or `Upload.html`) in a modern web browser.

## Future Improvements

* Store files in a database or cloud storage
* Add robust user authentication and authorization
* Improve UI/UX
* Deploy the application using Docker
