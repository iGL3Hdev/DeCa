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
- [x] Archivo `.http` con las pruebas guardadas (opcional)
- [x] Commit

## Fase 2 — Empresas
- [x] `empresa/`: Entity, Repository, DTOs, Service, Controller (mismo patrón)
- [x] Error 409 al borrar empresa con transportes (`flush()` + captura de `DataIntegrityViolationException`)
- [x] Probado con curl (15 casos OK)
- [x] Commit

## Fase 3 — Transportes
- [x] `EstadoTransporte` (enum, `@Enumerated(STRING)`) y `Entity` con `@ManyToOne` (FetchType.LAZY) a Empresa (cargador/transportista) y Vehiculo
- [x] Repository con `@Query` JPQL para filtros opcionales combinables
- [x] DTOs: `TransporteRequest` (ids) y `TransporteResponse` (con resúmenes anidados de empresa/vehículo)
- [x] Filtros `?estado=`, `?fechaDesde=`, `?fechaHasta=`
- [x] `PUT` solo si está en BORRADOR (409 en otro caso)
- [x] Probado con curl (13 casos OK: 201, 404 por ids inexistentes, 400 por peso 0, filtros, 409 en GENERADO)
- [x] Commit

## Fase 4 — Validaciones
- [x] Bean Validation en DTOs (obligatorios, peso > 0) — ya cubierto en fases 1-3
- [x] Regla fecha carga ≤ fecha descarga: validación a nivel de clase (`@FechasCoherentes` +
      `FechasCoherentesValidator`) sobre `TransporteRequest`
- [x] Fix en `GlobalExceptionHandler`: los errores de clase (`ObjectError`, sin campo) no los
      recogía `getFieldErrors()`; ahora también se vuelcan `getGlobalErrors()` al mapa `errores`
- [x] Probado con curl (fechaCarga > fechaDescarga → 400 con mensaje; mismo día y fechaCarga <
      fechaDescarga → 201; combinado con peso 0 → ambos errores en el mismo 400)
- [x] Commit

## Fase 5 — Frontend base
- [x] `npm create vite` (React + TS + ESLint) → `frontend/`
- [x] Proxy `/api` → :8080 en `vite.config.ts` (sin CORS en backend)
- [x] `types/index.ts`, `api/client.ts` (fetch + ApiError), `api/vehiculos.ts`
- [x] `components/Table.tsx` genérico
- [x] Pantalla Vehículos (`pages/Vehiculos.tsx`: alta, listado, baja)
- [x] Router en `App.tsx` (`/`, `/vehiculos`)
- [x] Probado en navegador: alta, duplicado (mensaje 409 del backend), baja
- [x] Commit

## Fase 6 — Frontend transportes
- [x] `react-hook-form` + `zod` + `@hookform/resolvers` instalados
- [x] Tipos y `api/empresas.ts`, `api/transportes.ts`
- [x] `components/Select.tsx` (`SelectAsync`, con `recargarSenal` para refrescar tras crear empresa)
- [x] `components/EmpresaModal.tsx` (crear empresa inline, callbacks `onCreada`/`onCerrar`)
- [x] `pages/TransporteForm.tsx`: esquema zod (equivalente a Bean Validation + `@FechasCoherentes`
      del backend vía `.refine`), `Controller` para los 3 selects, `register` para el resto
- [x] `pages/Dashboard.tsx` (listado de transportes + botón "Nuevo transporte")
- [x] Rutas en `App.tsx` (`/`, `/vehiculos`, `/transportes/nuevo`)
- [x] Probado en navegador: crear empresa inline, seleccionar vehículo, guardar transporte en
      BORRADOR, ver fila en Dashboard
- [x] Commit

## Fase 7 — PDF
- [x] Librería elegida: OpenPDF (API de alto nivel: párrafos/tablas/imágenes, encaja mejor que
      PDFBox de cara al QR incrustado de la Fase 8)
- [x] `deca.directorio-documentos` en `application.yml` + `DecaProperties`
      (`@ConfigurationProperties` + `@EnableConfigurationProperties`)
- [x] `PdfGenerator` (genera el PDF en memoria a partir de la entidad `Transporte`, con los datos
      del art. 6 de la Orden FOM/2861/2012: cargador, transportista, vehículo, fechas, lugares,
      mercancía y notas)
- [x] Verificado con un test temporal (generó un PDF real, revisado visualmente y luego
      eliminado) — el guardado en disco de verdad (`documentos/`) se hará en la Fase 8, dentro
      de `DecaService`, junto con el QR y el endpoint de generación
- [x] Commit

## Fase 8 — QR, URL pública y almacenamiento
- [x] Dependencia ZXing (`core` + `javase`)
- [x] `DocumentoDeca` (`@OneToOne` con Transporte, `unique=true`) + `DocumentoDecaRepository`
      (`findByUrlPublica`, `findByTransporteId`)
- [x] `QrGenerator` (ZXing → PNG en memoria) incrustado en el PDF (`PdfGenerator` amplia su firma
      con `imagenQr` + `urlPublica`)
- [x] `DecaService.generar()`: busca transporte, 409 si ya GENERADO, token UUID + `DECA_BASE_URL`,
      genera QR+PDF, guarda en disco (`guardarEnDisco`), marca transporte GENERADO, crea
      `DocumentoDeca`
- [x] `POST /api/transportes/{id}/generar-deca`
- [x] `GET /api/deca/{token}` (PDF inline, público — pendiente excluir con `permitAll()` en Fase 11)
- [x] `GET /api/documentos` y `/{id}`
- [x] Fix: `DocumentoDeca.fechaCreacion` quedaba `null` (Hibernate mandaba NULL explícito en el
      INSERT y pisaba el `DEFAULT CURRENT_TIMESTAMP` de MySQL) → se fija en el constructor Java
- [x] Probado con curl: generar DeCA (201, fechaCreacion real), 409 al regenerar, 404 transporte
      inexistente, descarga pública (200, Content-Type/Disposition correctos, QR visible y
      apuntando a la URL), 404 token inexistente, listado y detalle de documentos
- [x] `api/documentos.ts`, botón "Generar DeCA" en `Dashboard.tsx` (deshabilitado por fila mientras
      genera, navega a la ficha al terminar)
- [x] `pages/DocumentoDetalle.tsx` (datos, iframe con el PDF embebido, descargar, copiar URL
      pública con `navigator.clipboard`)
- [x] `pages/DocumentoPorTransporte.tsx` (ruta puente: busca el documento por `transporteId` y
      redirige con `<Navigate replace>`, para que "Ver documento" no quede en el historial)
- [x] Probado en navegador: generar DeCA desde el Dashboard, ver PDF embebido con QR, copiar URL,
      volver y comprobar transporte en GENERADO, "Ver documento" redirige correctamente
- [x] Commit

## Fase 8.5 — Estilo visual del frontend
Ahora mismo el frontend es HTML sin estilos (fondo negro por defecto del navegador, inputs y
selects sueltos). Dejarlo para después de cerrar toda la funcionalidad (Fases 7 y 8) y antes de
dockerizar, para no rehacer estilos sobre pantallas que aún pueden cambiar.
- [x] Enfoque: CSS propio (variables en `:root`, sin librería) — `index.css` (base/tabla/card/
      badges/modal/ficha) + `App.css` (nav)
- [x] Estilos base: paleta violeta, tipografía de sistema, `.card`, `.tabla` con hover, badges de
      estado (`.badge-borrador`/`.badge-generado`)
- [x] Vehículos: formulario en `.form-inline` (ancho contenido, no 100%) + tabla en `.card`
- [x] Dashboard: cabecera `.cabecera-pagina` (grid 3 columnas, título centrado + acción a la
      derecha en la misma fila), tabla en `.card`, badges de estado
- [x] TransporteForm: 3 cards temáticas con `.form-grid` (2 columnas), selects + botón "+ Nueva"
      en fila (`.campo-con-boton`)
- [x] EmpresaModal: estilos en línea sustituidos por `.modal-overlay`/`.modal-box`/`.modal-acciones`
- [x] DocumentoDetalle: card de datos (`.ficha-grid`) + card sin padding para el iframe del PDF
      (`.pdf-preview`)
- [x] Títulos (`h1`): chip centrado (fondo violeta suave, texto violeta, mayúsculas) — con
      excepción `.sin-mayusculas` para conservar "DeCA" con su tipografía mixta
- [x] Nav (`App.tsx`/`App.css`): marca a dos tonos, enlaces como pastillas con estado activo
      (`NavLink` + clase `.activo`)
- [x] Acciones de solo-texto convertidas a botones reales ("Ver documento", "← Volver al
      Dashboard") para consistencia visual
- [x] Comprobado visualmente en navegador tras cada cambio
- [x] Commit

## Fase 9 — Dockerización completa
- [x] `application-docker.yml` (perfil `docker`: datasource y `deca.base-url` sin default, exigidos
      por variable de entorno; `directorio-documentos` por defecto `/app/documentos`)
- [x] `backend/Dockerfile` (multi-stage: build con Maven+JDK, imagen final solo JRE Alpine)
- [x] `frontend/Dockerfile` (multi-stage: build con Node, imagen final nginx) + `nginx.conf`
      (proxy `/api/` → `http://backend:8080/api/`, `try_files` para que las rutas de React
      Router sobrevivan a un recargado de página)
- [x] `docker-compose.yml` con los 3 servicios (mysql/backend/frontend), puerto MySQL 3308,
      `depends_on: condition: service_healthy`, volúmenes `mysql_data` y `deca_pdfs`
- [x] `docker compose up -d --build`: los 3 contenedores arriba y sanos, flujo completo probado
      dentro de Docker (crear vehículo/transporte, generar DeCA, ver PDF)
- [x] Probado el QR desde el móvil con `DECA_BASE_URL=http://<IP_local>:8080` (fix de una errata
      en `.env`: faltaba `http://` y el puerto era 8000 en vez de 8080)
- [x] Datos de prueba limpiados (BD a 0, PDFs borrados del volumen `deca_pdfs`)
- [x] Commit

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
