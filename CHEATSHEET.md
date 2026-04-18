# DDAS Project — Quick Reference

---

## ▶️ START Backend
```
cd ~/Downloads/ddas-backend
./mvnw spring-boot:run
```
Wait for: `Started DdasBackendApplication in X seconds`
**Leave this Terminal open.**

---

## ⏹️ STOP Backend
In the Terminal where backend is running:
```
Ctrl + C
```

---

## 🔍 Check if Backend is Running
```
lsof -i :8080
```
- Shows output → Running ✅
- Nothing → Not running ❌

---

## 💀 Force Stop (if Terminal is closed)
```
kill -9 $(lsof -t -i:8080)
```

---

## 🌐 Open the App
1. Start backend (above)
2. Double-click `Index.html` on Desktop → DDAS folder

---

## 🔑 Login Accounts
| Email                  | Password  |
|------------------------|-----------|
| admin@ddas.com         | admin123  |
| user@institute.edu     | pass1234  |

Or register a new account from the login page.

---

## 📁 Project Files
| File            | What it does         |
|-----------------|----------------------|
| Index.html      | Login page           |
| Register.html   | Sign up page         |
| Dashboard.html  | View uploaded files  |
| Upload.html     | Upload new files     |

---

## 🔗 Backend API (runs on localhost:8080)
| Action          | Method | URL                        |
|-----------------|--------|----------------------------|
| Login           | POST   | /api/auth/login            |
| Register        | POST   | /api/auth/register         |
| Upload file     | POST   | /api/files                 |
| Get all files   | GET    | /api/files                 |
| Check duplicate | POST   | /api/files/check           |

---

## 🚀 Push to GitHub
```
cd ~/Desktop/DDAS\ project
git add .
git commit -m "your message here"
git push origin main
```

---

## ⚠️ Most Common Fix
**"Cannot reach backend"** = backend not running.
Just run the START command above.
