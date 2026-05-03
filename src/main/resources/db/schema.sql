CREATE SCHEMA IF NOT EXISTS finanzas;

CREATE TABLE IF NOT EXISTS finanzas.usuario (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    correo VARCHAR(180) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL DEFAULT 'USUARIO_ESTANDAR',
    moneda VARCHAR(3) NOT NULL DEFAULT 'COP',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ultimo_acceso TIMESTAMPTZ NULL,
    CONSTRAINT ck_usuario_rol CHECK (
        rol IN (
            'USUARIO_ESTANDAR',
            'ADMINISTRADOR'
        )
    ),
    CONSTRAINT ck_usuario_moneda CHECK (char_length(moneda) = 3)
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_usuario_correo_lower ON finanzas.usuario (LOWER(correo));

CREATE TABLE IF NOT EXISTS finanzas.cuenta (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    saldo NUMERIC(14, 2) NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_cuenta_tipo CHECK (
        tipo IN (
            'EFECTIVO',
            'BANCO',
            'AHORRO',
            'OTRO'
        )
    )
);

CREATE TABLE IF NOT EXISTS finanzas.usuario_cuenta (
    usuario_id BIGINT NOT NULL,
    cuenta_id BIGINT NOT NULL,
    permiso VARCHAR(20) NOT NULL DEFAULT 'PROPIETARIO',
    fecha_asignacion TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (usuario_id, cuenta_id),
    CONSTRAINT fk_usuario_cuenta_usuario FOREIGN KEY (usuario_id) REFERENCES finanzas.usuario (id) ON DELETE RESTRICT,
    CONSTRAINT fk_usuario_cuenta_cuenta FOREIGN KEY (cuenta_id) REFERENCES finanzas.cuenta (id) ON DELETE RESTRICT,
    CONSTRAINT ck_usuario_cuenta_permiso CHECK (
        permiso IN ('PROPIETARIO', 'COLABORADOR')
    )
);

CREATE TABLE IF NOT EXISTS finanzas.categoria (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NULL,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    origen VARCHAR(20) NOT NULL,
    icono VARCHAR(80) NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_categoria_usuario FOREIGN KEY (usuario_id) REFERENCES finanzas.usuario (id) ON DELETE RESTRICT,
    CONSTRAINT ck_categoria_tipo CHECK (
        tipo IN ('INGRESO', 'EGRESO', 'AMBOS')
    ),
    CONSTRAINT ck_categoria_origen CHECK (
        origen IN ('SISTEMA', 'USUARIO')
    ),
    CONSTRAINT ck_categoria_origen_usuario CHECK (
        (
            origen = 'SISTEMA'
            AND usuario_id IS NULL
        )
        OR (
            origen = 'USUARIO'
            AND usuario_id IS NOT NULL
        )
    )
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_categoria_sistema_nombre_lower ON finanzas.categoria (LOWER(nombre))
WHERE
    usuario_id IS NULL
    AND activa = TRUE;

CREATE UNIQUE INDEX IF NOT EXISTS uq_categoria_usuario_nombre_lower ON finanzas.categoria (usuario_id, LOWER(nombre))
WHERE
    usuario_id IS NOT NULL
    AND activa = TRUE;

CREATE TABLE IF NOT EXISTS finanzas.transaccion (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    cuenta_id BIGINT NOT NULL,
    categoria_id BIGINT NULL,
    monto NUMERIC(14, 2) NOT NULL,
    descripcion VARCHAR(255) NULL,
    tipo VARCHAR(20) NOT NULL,
    fecha TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_transaccion_usuario FOREIGN KEY (usuario_id) REFERENCES finanzas.usuario (id) ON DELETE RESTRICT,
    CONSTRAINT fk_transaccion_cuenta FOREIGN KEY (cuenta_id) REFERENCES finanzas.cuenta (id) ON DELETE RESTRICT,
    CONSTRAINT fk_transaccion_categoria FOREIGN KEY (categoria_id) REFERENCES finanzas.categoria (id) ON DELETE RESTRICT,
    CONSTRAINT ck_transaccion_monto_positivo CHECK (monto > 0),
    CONSTRAINT ck_transaccion_tipo CHECK (
        tipo IN (
            'INGRESO',
            'EGRESO',
            'TRANSFERENCIA'
        )
    )
);

CREATE TABLE IF NOT EXISTS finanzas.presupuesto (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    monto_global_limite NUMERIC(14, 2) NULL,
    periodo VARCHAR(20) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR',
    fecha_creacion TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_presupuesto_usuario FOREIGN KEY (usuario_id) REFERENCES finanzas.usuario (id) ON DELETE RESTRICT,
    CONSTRAINT ck_presupuesto_periodo CHECK (
        periodo IN (
            'MENSUAL',
            'TRIMESTRAL',
            'SEMESTRAL',
            'ANUAL'
        )
    ),
    CONSTRAINT ck_presupuesto_estado CHECK (
        estado IN (
            'BORRADOR',
            'ACTIVO',
            'VENCIDO',
            'CERRADO'
        )
    ),
    CONSTRAINT ck_presupuesto_monto_global CHECK (
        monto_global_limite IS NULL
        OR monto_global_limite > 0
    )
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_presupuesto_activo_usuario ON finanzas.presupuesto (usuario_id)
WHERE
    estado = 'ACTIVO';

CREATE TABLE IF NOT EXISTS finanzas.presupuesto_categoria (
    id BIGSERIAL PRIMARY KEY,
    presupuesto_id BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,
    monto_limite NUMERIC(14, 2) NOT NULL,
    monto_ejecutado NUMERIC(14, 2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_presupuesto_categoria_presupuesto FOREIGN KEY (presupuesto_id) REFERENCES finanzas.presupuesto (id) ON DELETE CASCADE,
    CONSTRAINT fk_presupuesto_categoria_categoria FOREIGN KEY (categoria_id) REFERENCES finanzas.categoria (id) ON DELETE RESTRICT,
    CONSTRAINT uq_presupuesto_categoria UNIQUE (presupuesto_id, categoria_id),
    CONSTRAINT ck_presupuesto_categoria_limite CHECK (monto_limite > 0),
    CONSTRAINT ck_presupuesto_categoria_ejecutado CHECK (monto_ejecutado >= 0)
);