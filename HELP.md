# NEXUS · API académica

Backend Spring Boot 4.1.x con Java 21, Spring MVC, Spring Data JPA y PostgreSQL 16. Las tareas, proyectos, documentos e historial del asistente quedan en PostgreSQL. El frontend React consume la API en `http://localhost:8080/api`.

## Requisitos

- JDK 21 o 25
- Node.js 18 o superior y npm
- Docker Desktop con Docker Compose

## Iniciar PostgreSQL y la aplicación (Windows)

Desde la carpeta raíz del repositorio, crea el archivo local `.env` con una clave ficticia de desarrollo. El archivo está ignorado por Git.

```powershell
@"
DB_NAME=nexus
DB_USER=nexus_user
DB_PASSWORD=pon_una_clave_local
DB_PORT=5432
"@ | Set-Content .env
docker compose up -d --wait
cd frontend
npm ci
npm run build
cd ..
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

Spring Boot lee las mismas variables desde `.env`. En macOS/Linux usa `./mvnw` y `npm` con los mismos pasos. Si solo quieres trabajar en el frontend, ejecuta `npm run dev` dentro de `frontend`; Vite corre en `http://localhost:5173`.

Para detener el servidor, usa Ctrl+C. Para pausar PostgreSQL sin borrar sus datos: `docker compose stop`. El volumen `nexus-postgres-data` conserva los datos.

## Contrato REST

El contrato OpenAPI 3.1 está en [docs/openapi.yaml](docs/openapi.yaml). Colección Postman y entorno exportados v2.1:

- `postman/PA-Lab2-Nexus.json`
- `postman/PA-Lab2-local.json`

Importa ambos en Postman, selecciona el entorno `PA-Lab2-local` y ejecuta la colección completa en orden.

| Método | Ruta | Resultado |
|---|---|---|
| GET | `/api/tasks?page=0&size=10&sort=id` | Página con `content` y metadatos `page` |
| GET | `/api/tasks/search?name=informe&page=0&size=10` | Búsqueda insensible a mayúsculas por título |
| GET | `/api/tasks/{id}` | Consulta por ID |
| POST | `/api/tasks` | 201, cuerpo creado y cabecera `Location` |
| PUT | `/api/tasks/{id}` | Reemplazo de la tarea |
| DELETE | `/api/tasks/{id}` | 204 sin cuerpo |

Proyectos y documentos tienen listados y rutas para crear, consultar y eliminar; proyectos también admiten actualización. El historial de `/api/assistant/messages` también se persiste. Los errores comparten `timestamp`, `status`, `error`, `message`, `path` y `errors`; se cubren 400, 404, 405, 409 y 422.

Cada tarea admite código único, título, materia, fecha, hora, prioridad enumerada, estado activo/completado, color y horas estimadas. Si no se envía `code`, la API lo genera. La regla de negocio de ejemplo impide completar una tarea inactiva.

## Verificar PostgreSQL

```powershell
docker compose exec postgres psql -U nexus_user -d nexus -c "\d study_tasks"
docker compose exec postgres psql -U nexus_user -d nexus -c "SELECT id, code, title, created_at, updated_at FROM study_tasks ORDER BY id;"
```

Las tablas usan `spring.jpa.hibernate.ddl-auto=update`; las claves y valores se suministran por entorno, y `open-in-view` está deshabilitado. El perfil de pruebas usa H2 aislado y no requiere Docker.

## Alcance

PDF.js extrae texto localmente de hasta ocho páginas de cada PDF, que luego se guarda en PostgreSQL. El asistente responde con un texto de demostración; no hay un proveedor de modelo de IA configurado.
