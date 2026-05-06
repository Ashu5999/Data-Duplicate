# 🔒 DDAS — Data Duplication Alert System

> A full-stack web application that detects and prevents duplicate file uploads using **SHA-256 hash comparison**. Built with a vanilla HTML/JS frontend and a Spring Boot (Java) backend.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Screenshots](#screenshots)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

DDAS solves a common enterprise problem: **preventing duplicate files from cluttering storage**. When a user uploads a file, the system computes its SHA-256 hash and checks it against all previously stored hashes. If a match is found, the upload is blocked with a clear warning — saving bandwidth, storage, and confusion.

The login page features an **interactive animated character system** that tracks the user's cursor, responds to input focus, and displays emotions based on login outcomes.

---

## ✨ Features

### 🎭 Interactive Login Page
- **Cursor tracking** — Four animated characters follow your mouse in real-time
- **Email focus** — Characters lean forward and watch you type
- **Password focus** — Characters look away shyly (privacy!)
- **Login success** — Happy bounce animation with smile
- **Login failure** — Sad droop animation with frown
- Purple & white themed UI with DDAS branding

### 📁 File Management
- **Upload files** with automatic SHA-256 hash computation (client-side)
- **Duplicate detection** — Server rejects files with matching hashes
- **Download files** directly from the dashboard
- **Delete files** with confirmation dialog
- **View files** in browser (PDF, images, text)

### 🔐 Authentication
- User **registration** with email & security key
- Secure **login** with session management
- **Password reset** without email server (direct database update)
- Auth guards on protected pages (Dashboard, Upload)

### 📊 Dashboard
- Real-time file statistics (total, clean, duplicates blocked)
- Backend health status indicator (online/offline)
- Search & filter files
- Operational status panel

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|-----------|
| **Frontend** | HTML5, CSS3, Vanilla JavaScript |
| **Backend** | Java 17, Spring Boot 3.x |
| **Database** | H2 (embedded, file-based) |
| **Build Tool** | Maven (with Maven Wrapper) |
| **Hashing** | SHA-256 (Web Crypto API + server-side) |
| **Fonts** | Google Fonts (Manrope, Inter) |

---

## 📂 Project Structure

```
DDAS project/
├── Index.html              # Login page (animated characters)
├── Register.html           # User registration page
├── ResetPassword.html      # Password reset page
├── Dashboard.html          # Main dashboard (file list, stats)
├── Upload.html             # File upload & duplicate check
├── README.md               # This file
├── CHEATSHEET.md           # Quick reference guide
│
└── ddas-backend/           # Spring Boot backend
    ├── pom.xml             # Maven dependencies
    ├── mvnw / mvnw.cmd     # Maven Wrapper scripts
    ├── data/
    │   └── ddas_db.mv.db   # H2 database file (auto-created)
    │
    └── src/main/java/com/ddas/backend/
        ├── DdasBackendApplication.java     # Spring Boot entry point
        ├── controller/
        │   ├── AuthController.java         # Login, Register, Reset APIs
        │   └── FileController.java         # Upload, Download, Delete APIs
        ├── model/
        │   ├── User.java                   # User entity (email, password)
        │   └── FileData.java               # File entity (name, hash, size)
        └── repository/
            ├── UserRepository.java         # User JPA repository
            └── FileRepository.java         # File JPA repository
```

---


## 📡 API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Login with email & password |
| `POST` | `/api/auth/reset-password` | Reset security key |

### File Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/files` | List all files (optional `?uploadedBy=`) |
| `POST` | `/api/files` | Upload a file (multipart form) |
| `GET` | `/api/files/download/{id}` | Download a file |
| `GET` | `/api/files/view/{id}` | View file in browser |
| `DELETE` | `/api/files/{id}` | Delete a file |
| `POST` | `/api/files/check` | Check for duplicate by keyword |

---

## 📸 Screenshots

### Login Page — Interactive Animated Characters
The login page features four animated characters that track your cursor, react to input focus, and show emotions based on login results.

### Dashboard
Enterprise-grade dashboard with file statistics, health monitoring, and a file management table.

### Upload Page
Drag-and-drop file upload with SHA-256 hash computation and real-time duplicate detection.

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is for educational purposes. Built as a minor project for academic submission.

---

<p align="center">
  <strong>DDAS</strong> — Data Duplication Alert System<br/>
  Made with 💜 by <a href="https://github.com/Ashu5999">Ashu5999</a>
</p>
