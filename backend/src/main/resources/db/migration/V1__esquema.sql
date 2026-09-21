-- ========================
-- EMPRESA
-- ========================
CREATE TABLE empresa (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL,
    nif         VARCHAR(20)  NOT NULL,
    domicilio   VARCHAR(255),
    telefono    VARCHAR(30),
    creado_en   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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
    activo              BOOLEAN NOT NULL DEFAULT TRUE,
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
    estado                  VARCHAR(10) NOT NULL DEFAULT 'BORRADOR',
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
    version             INT NOT NULL DEFAULT 1,

    CONSTRAINT fk_documento_transporte
        FOREIGN KEY (transporte_id) REFERENCES transporte(id),
    UNIQUE KEY uq_documento_url (url_publica)
) ENGINE=InnoDB;
