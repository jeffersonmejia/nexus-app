# NEXUS AI

Proyecto con un frontend React + Vite y una aplicación Spring Boot configurada con Maven.

## Requisitos

- JDK 17 o superior
- Node.js 18 o superior para el frontend

## Backend Spring Boot

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

En macOS o Linux:

```bash
./mvnw spring-boot:run
```

El wrapper descarga Apache Maven 3.10.0 la primera vez. Para empaquetar el backend ejecuta `./mvnw package` (en Windows, `./mvnw.cmd package`).

## Frontend React

En otra terminal:

```bash
cd frontend
npm install
npm run dev
```

El frontend corre de forma independiente en Vite. Para configurar la URL de la API, crea `frontend/.env` con `VITE_API_URL=http://localhost:8080/api`.
