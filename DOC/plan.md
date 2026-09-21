# Plan de desarrollo — DeCA Manager

Guía paso a paso. Marca cada casilla al completarla.
Especificación de referencia: [especificacion.md](especificacion.md).

## Método de trabajo por paso
1. Objetivo → 2. Archivos a crear/tocar → 3. Código → 4. Cómo verificarlo → 5. Commit.
Al cerrar cada fase: checklist y commit.

## Decisiones tomadas
- Raíz del proyecto: `DeCa/` (con `backend/`, `frontend/`, `DOC/`), sin carpeta extra `deca-manager/`.
- MySQL en Docker, puerto externo **3308** (3306 y 3307 ocupados por otras conexiones locales).
  - Backend en local → `jdbc:mysql://localhost:3308/deca_manager`
  - Backend dentro de Docker → `jdbc:mysql://mysql:3306/deca_manager`
- Maven Wrapper (`./mvnw`) en lugar de instalar Maven.
- Spring Boot 4.1.1 (starters nuevos: `webmvc`, `flyway`; Jackson 3 → paquetes `tools.jackson`). Paquete raíz `com.decamanager`.
- Migraciones con Flyway (no `ddl-auto=update`).
- DTOs (records) por dominio; no exponer entidades JPA.
- Spring Security no se añade hasta la fase de autenticación.
- Frontend → API mediante proxy (Vite en dev, nginx en Docker), sin CORS.
- PDF: OpenPDF o PDFBox (decidir en Fase 7). QR: ZXing.

## Observaciones sobre la especificación
1. `SecurityConfig` aparece en la estructura, pero la autenticación es fase posterior.
2. Dockerfile frontend: `COPY package*.json ./` (la spec omite el `./`).
3. `version:` en docker-compose está obsoleto: no usarlo.
4. `DELETE` de empresa con transportes asociados (FK): devolver 409.
5. `.env`: cambiar sus valores tras el primer arranque exige `docker compose down -v`
   (MySQL solo lee las variables al crear el volumen).

---

## Fase 0 — Entorno y esqueleto
- [x] Comprobar JDK 21, Node 20, Docker, Git
- [x] Estructura de carpetas y `.gitignore`
- [x] `.env` y `.env.example`
- [x] `docker-compose.yml` solo con MySQL (puerto 3308)
- [x] MySQL healthy y conexión desde Workbench (`deca_user`)
- [ ] `README.md` y primer commit

## Fase 1 — Backend base + Vehículos
- [x] Generar proyecto en Spring Initializr (Boot 4.1.1; web, data-jpa, mysql, validation, flyway) → `backend/`
- [x] `application.yml` (conexión a :3308)
- [x] Migración `V1__esquema.sql` (4 tablas de la spec)
- [x] `vehiculo/`: Entity, Repository, DTOs, Service, Controller (`PATCH baja`, `?activo=`)
- [x] `@RestControllerAdvice` para errores (`common/GlobalExceptionHandler`, ProblemDetail)
- [x] Probar con curl (13 casos OK: 201, 400, 404, 409, filtros, baja)
- [ ] Archivo `.http` con las pruebas guardadas (opcional)
- [ ] Commit

## Fase 2 — Empresas
- [ ] `empresa/`: Entity, Repository, Service, Controller (mismo patrón)
- [ ] Error 409 al borrar empresa con transportes
- [ ] Commit

## Fase 3 — Transportes
- [ ] Entity con `@ManyToOne`, DTOs
- [ ] Filtros `?estado=`, `?fecha_desde=`, `?fecha_hasta=`
- [ ] `PUT` solo si está en BORRADOR
- [ ] Commit

## Fase 4 — Validaciones
- [ ] Bean Validation en DTOs (obligatorios, peso > 0, NIF)
- [ ] Regla fecha carga ≤ fecha descarga
- [ ] Commit

## Fase 5 — Frontend base
- [ ] `npm create vite` (React + TS) → `frontend/`
- [ ] Router, `api/client.ts`, `types/`, proxy a :8080
- [ ] Pantalla Vehículos (`Table`, formulario)
- [ ] Commit

## Fase 6 — Frontend transportes
- [ ] Dashboard (listado)
- [ ] `TransporteForm` con react-hook-form + zod
- [ ] `Select` async y `EmpresaModal`
- [ ] Commit

## Fase 7 — PDF
- [ ] Elegir librería (OpenPDF / PDFBox)
- [ ] `PdfGenerator` con datos del transporte
- [ ] Guardado en disco (`documentos/`)
- [ ] Commit

## Fase 8 — QR, URL pública y almacenamiento
- [ ] `QrGenerator` (ZXing) incrustado en el PDF
- [ ] Token UUID + `DECA_BASE_URL`
- [ ] `POST /api/transportes/{id}/generar-deca`
- [ ] `GET /api/deca/{token}` (PDF inline, público)
- [ ] `GET /api/documentos` y `/{id}`
- [ ] Front: `DocumentoDetalle` (PDF, QR, copiar URL)
- [ ] Commit

## Fase 9 — Dockerización completa
- [ ] `backend/Dockerfile` (multi-stage) y `application-docker.yml`
- [ ] `frontend/Dockerfile` + `nginx.conf` con proxy a `/api`
- [ ] `docker-compose.yml` con los 3 servicios
- [ ] Probar el QR desde el móvil con la IP local
- [ ] Commit

## Fase 10 — Tests
- [ ] JUnit + MockMvc (servicios y controladores clave)
- [ ] Opcional: Testcontainers, Vitest
- [ ] Commit

## Fuera del MVP (más adelante)
- Versionado y trazabilidad de documentos
- Autenticación con Spring Security (`permitAll` en `/api/deca/**`)
- Despliegue en la VM

## Verificación final del MVP
Crear transporte → generar DeCA → abrir la URL pública en el móvil y ver el PDF con el QR.
