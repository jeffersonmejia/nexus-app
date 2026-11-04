# NEXUS AI · Frontend

Interfaz académica responsive construida con React, Vite y Axios. Incluye datos de demostración persistidos en el navegador; no requiere un backend para explorar las pantallas.

## Requisitos

- Node.js 18 o superior

## Desarrollo

```bash
cd frontend
npm install
npm run dev
```

## Compilar

```bash
npm run build
npm run preview
```

Para conectar el backend, crea `frontend/.env` con `VITE_API_URL=http://localhost:8080/api`. El cliente Axios adjunta `nexus_token` como bearer en cada solicitud y limpia ese token al recibir HTTP 401.

### Alcance de integración

El flujo de usuario actual funciona en el navegador con `localStorage`; el inicio de sesión y la generación de respuestas IA quedan listos para conectarse a endpoints Spring Boot. Los PDF se leen localmente en el navegador mediante PDF.js (hasta ocho páginas para extraer texto).
