INSERT INTO
    finanzas.usuario (
        nombre,
        correo,
        password_hash,
        rol,
        moneda,
        activo,
        fecha_registro,
        ultimo_acceso
    )
VALUES (
        'Administrador General',
        'admin@finanzas.com',
        '$2a$10$abcdefghijklmnopqrstuvabcdefghijklmnopqrstuvabcdefghijkl',
        'ADMINISTRADOR',
        'COP',
        TRUE,
        NOW(),
        NULL
    ),
    (
        'Usuario Demo',
        'usuario@finanzas.com',
        '$2a$10$zyxwvutsrqponmlkjihgfedcbazyxwvutsrqponmlkjihgfedcba',
        'USUARIO_ESTANDAR',
        'COP',
        TRUE,
        NOW(),
        NULL
    );

INSERT INTO
    finanzas.cuenta (
        nombre,
        tipo,
        saldo,
        activo,
        fecha_creacion
    )
VALUES (
        'Cuenta principal',
        'BANCO',
        1500000.00,
        TRUE,
        NOW()
    ),
    (
        'Billetera',
        'EFECTIVO',
        250000.00,
        TRUE,
        NOW()
    );

INSERT INTO
    finanzas.usuario_cuenta (
        usuario_id,
        cuenta_id,
        permiso,
        fecha_asignacion
    )
VALUES (1, 1, 'PROPIETARIO', NOW()),
    (2, 2, 'PROPIETARIO', NOW());

INSERT INTO
    finanzas.categoria (
        usuario_id,
        nombre,
        tipo,
        origen,
        icono,
        activa,
        fecha_creacion
    )
VALUES (
        NULL,
        'Salario',
        'INGRESO',
        'SISTEMA',
        'wallet',
        TRUE,
        NOW()
    ),
    (
        NULL,
        'Ventas',
        'INGRESO',
        'SISTEMA',
        'shopping-bag',
        TRUE,
        NOW()
    ),
    (
        NULL,
        'Alimentación',
        'EGRESO',
        'SISTEMA',
        'utensils',
        TRUE,
        NOW()
    ),
    (
        NULL,
        'Transporte',
        'EGRESO',
        'SISTEMA',
        'bus',
        TRUE,
        NOW()
    ),
    (
        NULL,
        'Vivienda',
        'EGRESO',
        'SISTEMA',
        'home',
        TRUE,
        NOW()
    ),
    (
        NULL,
        'Servicios',
        'EGRESO',
        'SISTEMA',
        'receipt',
        TRUE,
        NOW()
    ),
    (
        NULL,
        'Salud',
        'EGRESO',
        'SISTEMA',
        'heart',
        TRUE,
        NOW()
    ),
    (
        NULL,
        'Educación',
        'EGRESO',
        'SISTEMA',
        'book',
        TRUE,
        NOW()
    ),
    (
        NULL,
        'Otros',
        'AMBOS',
        'SISTEMA',
        'more-horizontal',
        TRUE,
        NOW()
    ) ON CONFLICT DO NOTHING;

SELECT setval (
        pg_get_serial_sequence ('finanzas.usuario', 'id'), COALESCE(
            (
                SELECT MAX(id)
                FROM finanzas.usuario
            ), 1
        ), true
    );

SELECT setval (
        pg_get_serial_sequence ('finanzas.cuenta', 'id'), COALESCE(
            (
                SELECT MAX(id)
                FROM finanzas.cuenta
            ), 1
        ), true
    );

SELECT setval (
        pg_get_serial_sequence ('finanzas.categoria', 'id'), COALESCE(
            (
                SELECT MAX(id)
                FROM finanzas.categoria
            ), 1
        ), true
    );

SELECT setval (
        pg_get_serial_sequence ('finanzas.transaccion', 'id'), COALESCE(
            (
                SELECT MAX(id)
                FROM finanzas.transaccion
            ), 1
        ), true
    );

SELECT setval (
        pg_get_serial_sequence ('finanzas.presupuesto', 'id'), COALESCE(
            (
                SELECT MAX(id)
                FROM finanzas.presupuesto
            ), 1
        ), true
    );

SELECT setval (
        pg_get_serial_sequence (
            'finanzas.presupuesto_categoria', 'id'
        ), COALESCE(
            (
                SELECT MAX(id)
                FROM finanzas.presupuesto_categoria
            ), 1
        ), true
    );