# NEXUS AI · Frontend

Interfaz académica responsive construida con React, Vite y Axios. Para la configuración de Docker, Spring Boot y PostgreSQL, consulta [HELP.md](../HELP.md).

## Desarrollo

```bash
npm ci
npm run dev
```

El frontend corre en `http://localhost:5173` y llama a `http://localhost:8080/api`. Puedes definir otra URL con `VITE_API_URL` en un archivo `frontend/.env` local. El cliente Axios adjunta `nexus_token` si existe, aunque esta versión de la API no incluye autenticación.

## Compilar

```bash
npm run build
npm run preview
```

Vite genera `dist/`; Maven copia ese directorio a los recursos estáticos de Spring Boot cuando ejecutas o empaquetas el backend.

## Documentos y asistente

PDF.js extrae texto localmente de hasta ocho páginas por documento. El texto se guarda en PostgreSQL junto a los datos del documento. El historial del asistente también se conserva en PostgreSQL; sus respuestas son de demostración porque no hay un proveedor de IA configurado.
