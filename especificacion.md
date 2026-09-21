# DeCA Manager — Especificación técnica

Proyecto personal de aprendizaje. Simula la gestión de Documentos de Control
Administrativo electrónicos (DeCA) para una empresa de transporte pequeña,
siguiendo los requisitos de la Resolución de 5 de junio de 2026 de la
Dirección General de Transporte por Carretera y Ferrocarril.

**No es un producto comercial.** Es un ejercicio para practicar:
React + TypeScript → Spring Boot / REST → MySQL → Docker.

---

## 1. Visión y alcance

**Usuario:** uno solo, el gestor de la empresa (hace las veces de cargador
contractual o transportista).

**Flujo principal:**
El usuario entra → selecciona vehículo → rellena los datos del transporte →
genera el DeCA → obtiene PDF con QR → el documento queda almacenado y
consultable.

### Dentro del alcance (MVP)
- Gestión de vehículos (alta, edición, baja).
- Gestión de empresas (cargadores y transportistas, con NIF y domicilio).
- Creación de transportes con los datos obligatorios del art. 6 de la
  Orden FOM/2861/2012.
- Generación del PDF nativo con QR incrustado.
- URL única por documento que descarga el PDF directamente.
- Histórico de documentos con búsqueda sencilla.

### Fuera del alcance (por ahora)
- Firma electrónica.
- Multiempresa, roles y varios usuarios.
- Integración con tacógrafo.
- Despliegue en internet (todo corre en local o en la VM propia).

### Pendiente para fases posteriores del roadmap
- Modificaciones con trazabilidad (puntos 9-10 del roadmap).
- Autenticación (punto 11).

### Nota técnica — URL del QR
Si la URL es `localhost`, solo funcionará en el mismo ordenador. Para probar
el escaneo con el móvil, la URL debe apuntar a la IP de la VM en la red
local (ej. `http://192.168.1.50:8080/...`). Se deja configurable mediante
una variable de entorno (`DECA_BASE_URL`), sin tocar código.

---

## 2. Modelo de dominio

### Empresa
Representa tanto al cargador contractual como al transportista efectivo.

- id
- nombre / razón social
- nif
- domicilio
- teléfono (opcional)

### Vehiculo
- id
- matricula
- tipo (rígido, tractora, remolque...)
- matricula_remolque (opcional)
- activo (boolean — baja sin borrar histórico)

### Transporte
Núcleo de la aplicación. Cada registro es un servicio antes de generar el
DeCA.

- id
- cargador_id → Empresa
- transportista_id → Empresa
- vehiculo_id → Vehiculo
- fecha_operacion
- lugar_carga
- fecha_carga
- lugar_descarga
- fecha_descarga
- mercancia_naturaleza
- mercancia_peso (o volumen)
- notas (opcional)
- estado (BORRADOR / GENERADO)

### DocumentoDeca
Se crea al generar el DeCA a partir de un Transporte. Aquí vive la
trazabilidad.

- id
- transporte_id → Transporte
- url_publica (token único usado en la URL)
- ruta_pdf
- fecha_creacion (timestamp automático)
- fecha_modificacion (se actualiza al editar — fase 9-10)
- version (empieza en 1)

### Relaciones
```
Empresa (1) ──< (N) Transporte  [como cargador]
Empresa (1) ──< (N) Transporte  [como transportista]
Vehiculo (1) ──< (N) Transporte
Transporte (1) ── (1) DocumentoDeca
```

**Nota de diseño:** separar `Transporte` de `DocumentoDeca` permite en el
futuro (punto 9 del roadmap) tener varias versiones de documento para un
mismo transporte sin rediseñar el modelo — hoy es 1 a 1, pero la tabla ya
está preparada para 1 a N.

---

## 3. Base de datos (MySQL)

```sql
CREATE DATABASE IF NOT EXISTS deca_manager
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE deca_manager;

-- ========================
-- EMPRESA
-- ========================
CREATE TABLE empresa (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(150) NOT NULL,
    nif             VARCHAR(20)  NOT NULL,
    domicilio       VARCHAR(255),
    telefono        VARCHAR(30),
    creado_en       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_empresa_nif (nif)
) ENGINE=InnoDB;

-- ========================
-- VEHICULO
-- ========================
CREATE TABLE vehiculo (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    matricula           VARCHAR(15) NOT NULL,
    tipo                VARCHAR(50) NOT NULL,
    matricula_remolque  VARCHAR(15),
    activo              BOOLEAN DEFAULT TRUE,
    creado_en           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_vehiculo_matricula (matricula)
) ENGINE=InnoDB;

-- ========================
-- TRANSPORTE
-- ========================
CREATE TABLE transporte (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    cargador_id             BIGINT NOT NULL,
    transportista_id        BIGINT NOT NULL,
    vehiculo_id             BIGINT NOT NULL,
    fecha_operacion         DATE NOT NULL,
    lugar_carga             VARCHAR(255) NOT NULL,
    fecha_carga             DATE NOT NULL,
    lugar_descarga          VARCHAR(255) NOT NULL,
    fecha_descarga          DATE NOT NULL,
    mercancia_naturaleza    VARCHAR(150) NOT NULL,
    mercancia_peso          DECIMAL(10,2) NOT NULL,
    mercancia_unidad        VARCHAR(10) NOT NULL DEFAULT 'TM',
    notas                   TEXT,
    estado                  ENUM('BORRADOR','GENERADO') DEFAULT 'BORRADOR',
    creado_en               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transporte_cargador
        FOREIGN KEY (cargador_id) REFERENCES empresa(id),
    CONSTRAINT fk_transporte_transportista
        FOREIGN KEY (transportista_id) REFERENCES empresa(id),
    CONSTRAINT fk_transporte_vehiculo
        FOREIGN KEY (vehiculo_id) REFERENCES vehiculo(id)
) ENGINE=InnoDB;

-- ========================
-- DOCUMENTO_DECA
-- ========================
CREATE TABLE documento_deca (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    transporte_id       BIGINT NOT NULL,
    url_publica         VARCHAR(64) NOT NULL,
    ruta_pdf            VARCHAR(500) NOT NULL,
    fecha_creacion      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion  TIMESTAMP NULL,
    version             INT DEFAULT 1,

    CONSTRAINT fk_documento_transporte
        FOREIGN KEY (transporte_id) REFERENCES transporte(id),
    UNIQUE KEY uq_documento_url (url_publica)
) ENGINE=InnoDB;
```

**Decisiones de diseño:**
- `url_publica` es un token aleatorio (UUID), nunca el `id` autoincremental
  — evita URLs adivinables (`/deca/1`, `/deca/2`...).
- `mercancia_unidad` por defecto `TM` (toneladas), abierto a `KG`, `M3`, etc.
- `estado` en Transporte permite tener borradores sin DeCA generado.
- Sin índices de rendimiento por ahora (6-8 camiones, poco volumen) — se
  añaden con `ALTER TABLE` cuando hagan falta.

---

## 4. API REST

### EmpresaController — `/api/empresas`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/empresas` | Listar todas |
| GET | `/api/empresas/{id}` | Ver una |
| POST | `/api/empresas` | Crear |
| PUT | `/api/empresas/{id}` | Editar |
| DELETE | `/api/empresas/{id}` | Eliminar |

### VehiculoController — `/api/vehiculos`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/vehiculos` | Listar (`?activo=true` opcional) |
| GET | `/api/vehiculos/{id}` | Ver uno |
| POST | `/api/vehiculos` | Crear |
| PUT | `/api/vehiculos/{id}` | Editar |
| PATCH | `/api/vehiculos/{id}/baja` | Dar de baja (activo=false) |

### TransporteController — `/api/transportes`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/transportes` | Listar (filtros `?estado=`, `?fecha_desde=`, `?fecha_hasta=`) |
| GET | `/api/transportes/{id}` | Ver uno |
| POST | `/api/transportes` | Crear (queda en BORRADOR) |
| PUT | `/api/transportes/{id}` | Editar (solo si sigue en BORRADOR) |

### DecaController
| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/transportes/{id}/generar-deca` | Genera PDF + QR, crea `DocumentoDeca`, marca transporte GENERADO |
| GET | `/api/deca/{url_publica}` | **Endpoint público**, descarga directa del PDF (sin auth) |
| GET | `/api/documentos` | Listar histórico de documentos generados |
| GET | `/api/documentos/{id}` | Ver detalle de un documento |

### Detalle importante — `GET /api/deca/{url_publica}`
Único endpoint con comportamiento exigido por la normativa: debe devolver
el PDF directamente, sin login ni pantallas intermedias.

```
Content-Type: application/pdf
Content-Disposition: inline; filename="deca_082958.pdf"
```

**No** debe llevar el filtro de seguridad que sí llevará el resto de la API
cuando se añada Spring Security (punto 11 del roadmap). Debe quedar
explícitamente excluido con `permitAll()`.

---

## 5. Frontend (pantallas y flujo)

### 1. Dashboard / Listado de transportes (`/`)
- Tabla: fecha, cargador, transportista, vehículo, estado
- Botón "Nuevo transporte"
- Click en fila GENERADO → detalle del documento
- Click en fila BORRADOR → completar/editar

### 2. Formulario de transporte (`/transportes/nuevo`, `/transportes/{id}/editar`)
- Selects para cargador y transportista (con opción "crear nuevo" inline)
- Select para vehículo (solo activos)
- Fechas (operación, carga, descarga)
- Lugares (carga, descarga)
- Mercancía (naturaleza, peso, unidad)
- Notas (opcional)
- Botón "Guardar borrador" y "Generar DeCA" (valida campos obligatorios)

### 3. Vista de documento generado (`/documentos/{id}`)
- Datos del transporte (solo lectura)
- Vista previa del PDF embebida
- QR visible
- Botón "Descargar PDF"
- Botón "Copiar URL pública"
- Fecha de creación / última modificación

### 4. Gestión de vehículos (`/vehiculos`)
- Tabla con alta/edición/baja
- Formulario: matrícula, tipo, matrícula remolque

### Componentes reutilizables
- `<Table>` genérico con columnas configurables
- `<Select>` con carga async de opciones
- Formularios con validación — buen sitio para `react-hook-form` + `zod`

### Flujo de navegación
```
Dashboard → Nuevo transporte → Formulario → Guardar borrador
                                          ↓
                                   Generar DeCA
                                          ↓
                              Vista de documento (PDF + QR)
```

**Nota de alcance:** gestión de empresas no es página separada por ahora —
se crea inline desde el formulario de transporte (modal simple).

---

## 6. Estructura del proyecto

```
deca-manager/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/decamanager/
│   │   │   │   ├── DecaManagerApplication.java
│   │   │   │   ├── config/
│   │   │   │   │   └── SecurityConfig.java        # permitAll() en /api/deca/**
│   │   │   │   ├── empresa/
│   │   │   │   │   ├── Empresa.java
│   │   │   │   │   ├── EmpresaRepository.java
│   │   │   │   │   ├── EmpresaService.java
│   │   │   │   │   └── EmpresaController.java
│   │   │   │   ├── vehiculo/
│   │   │   │   │   ├── Vehiculo.java
│   │   │   │   │   ├── VehiculoRepository.java
│   │   │   │   │   ├── VehiculoService.java
│   │   │   │   │   └── VehiculoController.java
│   │   │   │   ├── transporte/
│   │   │   │   │   ├── Transporte.java
│   │   │   │   │   ├── TransporteRepository.java
│   │   │   │   │   ├── TransporteService.java
│   │   │   │   │   └── TransporteController.java
│   │   │   │   └── deca/
│   │   │   │       ├── DocumentoDeca.java
│   │   │   │       ├── DocumentoDecaRepository.java
│   │   │   │       ├── DecaService.java           # genera PDF + QR
│   │   │   │       ├── PdfGenerator.java
│   │   │   │       ├── QrGenerator.java
│   │   │   │       └── DecaController.java
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── application-docker.yml
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
│
├── frontend/
│   ├── src/
│   │   ├── main.tsx
│   │   ├── App.tsx
│   │   ├── api/
│   │   │   ├── client.ts
│   │   │   ├── empresas.ts
│   │   │   ├── vehiculos.ts
│   │   │   ├── transportes.ts
│   │   │   └── documentos.ts
│   │   ├── pages/
│   │   │   ├── Dashboard.tsx
│   │   │   ├── TransporteForm.tsx
│   │   │   ├── DocumentoDetalle.tsx
│   │   │   └── Vehiculos.tsx
│   │   ├── components/
│   │   │   ├── Table.tsx
│   │   │   ├── Select.tsx
│   │   │   └── EmpresaModal.tsx
│   │   └── types/
│   │       └── index.ts
│   ├── package.json
│   ├── vite.config.ts
│   └── Dockerfile
│
├── docker-compose.yml
└── README.md
```

**Criterio de organización del backend:** paquete por dominio
(`empresa/`, `vehiculo/`...) en vez de por capa. Con 4 entidades es más
legible navegar por funcionalidad.

**`deca/` es la única carpeta con lógica de negocio real** (generación
PDF/QR) — las otras tres son CRUD estándar.

---

## 7. Docker y entorno

### docker-compose.yml (raíz del proyecto)

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: deca-mysql
    restart: unless-stopped
    environment:
      MYSQL_DATABASE: deca_manager
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_USER: ${MYSQL_USER}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build: ./backend
    container_name: deca-backend
    restart: unless-stopped
    depends_on:
      mysql:
        condition: service_healthy
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/deca_manager
      SPRING_DATASOURCE_USERNAME: ${MYSQL_USER}
      SPRING_DATASOURCE_PASSWORD: ${MYSQL_PASSWORD}
      DECA_BASE_URL: ${DECA_BASE_URL}
    ports:
      - "8080:8080"
    volumes:
      - deca_pdfs:/app/documentos

  frontend:
    build: ./frontend
    container_name: deca-frontend
    restart: unless-stopped
    depends_on:
      - backend
    ports:
      - "5173:80"

volumes:
  mysql_data:
  deca_pdfs:
```

### .env (no se sube a git — añadir a .gitignore)

```
MYSQL_ROOT_PASSWORD=cambia_esto
MYSQL_USER=deca_user
MYSQL_PASSWORD=cambia_esto_tambien
DECA_BASE_URL=http://192.168.1.50:8080
```

`DECA_BASE_URL` es lo que se antepone al `url_publica` de cada documento
para construir la URL del QR. En local se prueba con `localhost`; para
escanear el QR desde el móvil, se cambia por la IP de la VM en la red
local y se reinicia el contenedor — sin tocar código.

### backend/Dockerfile (multi-stage)

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### frontend/Dockerfile (build + nginx)

```dockerfile
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json .
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
```

### Flujo de trabajo día a día

```bash
docker compose up -d --build   # levantar todo
docker compose logs -f backend # ver logs si algo falla
docker compose down            # parar
```

---

## Roadmap de desarrollo (incremental, sin prisa)

1. CRUD de transportes
2. Gestión de vehículos
3. Formulario de generación del documento
4. Validaciones
5. Generación de PDF
6. QR
7. URL pública del documento
8. Almacenamiento
9. Versionado/modificaciones
10. Trazabilidad
11. Autenticación
12. Despliegue