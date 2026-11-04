# NEXUS AI

NEXUS AI es un espacio académico web para que estudiantes organicen sus actividades y materiales de estudio. La interfaz reúne un panel de seguimiento, tareas, planificación semanal, proyectos, una biblioteca de documentos PDF y un asistente de estudio.

## Funcionalidades

- Consultar un resumen de tareas pendientes, completadas y vencidas.
- Crear, completar, buscar y eliminar actividades académicas.
- Revisar una planificación semanal y actividades por fecha.
- Crear proyectos y actualizar su progreso.
- Cargar archivos PDF y extraer texto localmente en el navegador con PDF.js (hasta ocho páginas por documento).
- Explorar una interfaz adaptable a pantallas pequeñas y un tema claro u oscuro.
- Conservar tareas, proyectos, documentos y preferencia de tema en `localStorage`.

> **Estado de integración:** el frontend aún usa datos de demostración y almacenamiento del navegador. El asistente devuelve respuestas de ejemplo. El backend no tiene todavía endpoints ni modelos para guardar los datos académicos en PostgreSQL; la base queda preparada para esa siguiente etapa.

## Tecnologías

- **Frontend:** React 18, Vite, Axios, Lucide React y PDF.js.
- **Backend:** Java 17, Spring Boot 4, Spring JDBC y PostgreSQL.
- **Desarrollo local:** Docker Compose y PostgreSQL 16.
- **Infraestructura Azure:** Terraform, Azure Container Apps, Azure Container Registry y Azure Database for PostgreSQL Flexible Server.

## Requisitos

- Docker Desktop con Docker Compose.
- Node.js 18 o superior y npm para desarrollar el frontend con Vite.
- Terraform 1.8+, Azure CLI y una suscripción Azure para desplegar la infraestructura.

## Inicio local

Desde la raíz, inicia la aplicación y la base de datos:

```bash
docker compose up --build
```

La interfaz queda en `http://localhost:8082` y PostgreSQL en `localhost:5432`. Compose conserva los datos en el volumen `postgres-data`. Las credenciales predeterminadas (`nexus` / `nexus_local_only`) son solo para desarrollo.

Para desarrollar el frontend con Vite y recarga en caliente, inicia los contenedores:

```bash
docker compose up -d db app
```

En otra terminal:

```bash
cd frontend
npm install
npm run dev
```

Para detener los servicios:

```bash
docker compose down
```

Para borrar también el volumen de datos local:

```bash
docker compose down --volumes
```

## Configuración del frontend

El cliente Axios usa `VITE_API_URL` como URL base. Sin configuración, usa `http://localhost:8080/api`. Para apuntar al backend local, crea `frontend/.env`:

```dotenv
VITE_API_URL=http://localhost:8082/api
```

El backend aún no implementa esas rutas. El cliente adjunta el valor `nexus_token` como bearer token y lo elimina al recibir HTTP 401.

## Construir y probar

Compilar solo el frontend:

```bash
cd frontend
npm run build
npm run preview
```

La prueba existente de Spring Boot requiere que PostgreSQL esté disponible. Inicia la base y ejecuta las pruebas desde la raíz:

```bash
docker compose up -d db
./mvnw test
```

En Windows PowerShell, ejecuta `.\mvnw.cmd test` en lugar de `./mvnw test`.

## Despliegue en Azure

La carpeta [`infra/`](infra/main.tf) contiene Terraform modular: `modules/networking`, `modules/database` y `modules/container_app`. El workflow [`.github/workflows/deploy-azure.yml`](.github/workflows/deploy-azure.yml) usa OpenID Connect, guarda el estado remoto en Azure Blob Storage y construye la imagen en ACR. Según la política de esta suscripción, `East US` está bloqueado; Container Apps queda en `East US 2` y PostgreSQL en `Brazil South`, unidos por VNet peering privado.

La sesión Azure está asociada a **Azure for Students**. Microsoft ofrece USD 100 de crédito para los primeros 12 meses; verifica el saldo y vigencia en Azure Cost Management antes de aplicar. Container Apps usa Consumption con escala a cero y límite de una réplica. PostgreSQL usa el tamaño Burstable mínimo (B1ms), sin alta disponibilidad, pero sigue generando costo mientras está encendido. La guía para Terraform y GitHub Actions está en esta sección:

1. El almacenamiento remoto ya está preparado en `nexus-tfstate-rg` (`nexusstate`, contenedor `tfstate`).
2. El principal OIDC `nexus-app-github-actions` está configurado para `main`, con permisos limitados al grupo `nexus-app-rg` y al storage de estado.
3. GitHub Actions debe tener `AZURE_CLIENT_ID`, `AZURE_TENANT_ID`, `AZURE_SUBSCRIPTION_ID`, `AZURE_APP_LOCATION=eastus2`, `AZURE_DATABASE_LOCATION=brazilsouth`, `AZURE_RESOURCE_GROUP=nexus-app-rg`, `ACR_NAME=nexusai20261104jp`, `APP_NAME=nexus-ai`, `TF_STATE_RESOURCE_GROUP=nexus-tfstate-rg`, `TF_STATE_STORAGE_ACCOUNT=nexusstate`, `TF_STATE_CONTAINER=tfstate` y `TF_STATE_KEY=nexus.tfstate` como variables, y `POSTGRES_ADMIN_PASSWORD` como secreto.

Antes de ejecutar el workflow, verifica en Cost Management el crédito restante. `terraform apply` crea recursos facturables, especialmente PostgreSQL; el workflow no se ha ejecutado todavía.

## Estructura

```text
.
├── .github/workflows/       # Despliegue Azure mediante GitHub Actions
├── frontend/src/            # Aplicación React, cliente API y estilos
├── infra/main.tf            # Orquestación y entrada de Terraform
├── infra/modules/           # Red, base de datos y Container Apps
├── src/main/java/           # Aplicación Spring Boot
├── src/main/resources/      # Configuración del servidor y datasource
├── src/test/java/           # Pruebas del backend
├── docker-compose.yml       # Aplicación y PostgreSQL para desarrollo
├── Dockerfile               # Build multi-stage frontend + backend
└── pom.xml                  # Maven, Spring JDBC y driver PostgreSQL
```

## Preparación de Azure y GitHub Actions

La cuenta conectada es **Azure for Students**. La oferta incluye USD 100 de crédito durante los primeros 12 meses; el saldo disponible depende de la fecha de activación y del consumo previo. Confírmalo en Cost Management antes de desplegar. [Detalles oficiales de Azure for Students](https://azure.microsoft.com/en-us/pricing/offers/ms-azr-0170p).

La política de la suscripción solo permite `southcentralus`, `eastus2`, `chilecentral`, `brazilsouth` y `northcentralus`. Por eso se usará `eastus2` para Container Apps (East US exacto está bloqueado) y `brazilsouth` para PostgreSQL. Las VNets se conectan con peering privado; el tráfico de base entre regiones puede generar cargos de transferencia.

Ya se prepararon los grupos `nexus-app-rg` y `nexus-tfstate-rg`, el Storage Account `nexusstate` en Chile Central y su contenedor privado `tfstate`. El principal OIDC `nexus-app-github-actions` está asociado a la rama `main`; tiene permisos limitados al grupo de aplicación y al Storage Account del estado. El secreto `POSTGRES_ADMIN_PASSWORD` se guardó en GitHub Actions.

Antes de correr el workflow, configura estas **Actions variables** en `jeffersonmejia/nexus-app`:

- `AZURE_CLIENT_ID`: `cae4608b-45b2-4790-ae5f-bb1554947d4e`
- `AZURE_TENANT_ID`: `aaf01420-8f32-49e5-bcef-795340c1f96a`
- `AZURE_SUBSCRIPTION_ID`: `d5174d35-269a-49b5-86b4-207147698d77`
- `AZURE_APP_LOCATION`: `eastus2`
- `AZURE_DATABASE_LOCATION`: `brazilsouth`
- `AZURE_RESOURCE_GROUP`: `nexus-app-rg`
- `ACR_NAME`: `nexusai20261104jp`
- `APP_NAME`: `nexus-ai`
- `TF_STATE_RESOURCE_GROUP`: `nexus-tfstate-rg`
- `TF_STATE_STORAGE_ACCOUNT`: `nexusstate`
- `TF_STATE_CONTAINER`: `tfstate`
- `TF_STATE_KEY`: `nexus.tfstate`

El secreto requerido es `POSTGRES_ADMIN_PASSWORD` (ya creado). La ejecución del workflow crea la base de datos B1ms, ACR y Container Apps; B1ms y ACR pueden consumir crédito aun cuando la aplicación no reciba tráfico. No he ejecutado `terraform apply`.

## Almacenamiento y privacidad

Los datos académicos y documentos siguen guardándose en `localStorage`; no se sincronizan aún con el backend ni con PostgreSQL. Al cargar un PDF, el navegador extrae texto de hasta ocho páginas y conserva localmente los metadatos y el texto extraído. El archivo original no se envía al backend.
