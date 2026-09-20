-- F-04: Seed de datos demo — 5 médicos, 20 pacientes, 30 citas
-- Idempotente: usa ON CONFLICT DO NOTHING en todos los inserts.
-- Password hash = Demo2026! (BCrypt $2a$12$)

-- =====================================================================
-- ESPECIALIDADES — 3 nuevas en tenant 1
-- =====================================================================
INSERT INTO specialties (tenant_id, name, active)
VALUES
    (1, 'Cardiología',   true),
    (1, 'Neurología',    true),
    (1, 'Dermatología',  true)
ON CONFLICT DO NOTHING;

-- =====================================================================
-- USUARIOS MÉDICOS — 3 nuevos doctores en tenant 1
-- =====================================================================
INSERT INTO users (tenant_id, first_name, last_name, cif, email, password_hash,
                   role, active, failed_login_attempts, must_change_password)
VALUES
    (1, 'Luis',       'Hernández', '04141201800321', 'luis.hernandez.demo@medisuite.test',
     '$2a$12$bDYB/iG64e7v8J1vqnT9g.RuF0G62xSyyFtFuaIP//IXLoZqn2MW2',
     'DOCTOR', true, 0, false),
    (1, 'María',      'Fuentes',   '04141202100567', 'maria.fuentes.demo@medisuite.test',
     '$2a$12$bDYB/iG64e7v8J1vqnT9g.RuF0G62xSyyFtFuaIP//IXLoZqn2MW2',
     'DOCTOR', true, 0, false),
    (1, 'José',       'Ramírez',   '04141201900234', 'jose.ramirez.demo@medisuite.test',
     '$2a$12$bDYB/iG64e7v8J1vqnT9g.RuF0G62xSyyFtFuaIP//IXLoZqn2MW2',
     'DOCTOR', true, 0, false)
ON CONFLICT DO NOTHING;

-- =====================================================================
-- DOCTORES — 3 nuevos en tenant 1
-- =====================================================================
INSERT INTO doctors (tenant_id, user_id, specialty_id, license_number)
SELECT 1,
       u.id,
       (SELECT id FROM specialties WHERE name = 'Cardiología'  AND tenant_id = 1),
       'MED-2018-0321'
FROM users u WHERE u.email = 'luis.hernandez.demo@medisuite.test'
ON CONFLICT DO NOTHING;

INSERT INTO doctors (tenant_id, user_id, specialty_id, license_number)
SELECT 1,
       u.id,
       (SELECT id FROM specialties WHERE name = 'Neurología'   AND tenant_id = 1),
       'MED-2021-0567'
FROM users u WHERE u.email = 'maria.fuentes.demo@medisuite.test'
ON CONFLICT DO NOTHING;

INSERT INTO doctors (tenant_id, user_id, specialty_id, license_number)
SELECT 1,
       u.id,
       (SELECT id FROM specialties WHERE name = 'Dermatología' AND tenant_id = 1),
       'MED-2019-0234'
FROM users u WHERE u.email = 'jose.ramirez.demo@medisuite.test'
ON CONFLICT DO NOTHING;

-- =====================================================================
-- PACIENTES — 17 nuevos (15 tenant 1, 2 tenant 2) para llegar a 20
-- =====================================================================
INSERT INTO patients (tenant_id, dui, first_name, last_name, birth_date, phone, blood_type)
VALUES
    -- tenant 1 (15 pacientes)
    (1, '04121990001234', 'Sofía',     'Ramos',      '1990-03-15', '7890-1234', 'O+'),
    (1, '04121988005678', 'Miguel',    'Solís',      '1988-07-22', '7890-5678', 'A+'),
    (1, '04121995009012', 'Laura',     'Mendoza',    '1995-11-08', '7890-9012', 'B+'),
    (1, '04121992003456', 'Carlos',    'Molina',     '1992-04-30', '7891-3456', 'AB+'),
    (1, '04121987007890', 'Ana',       'García',     '1987-09-14', '7891-7890', 'O-'),
    (1, '06121998001234', 'Roberto',   'Flores',     '1998-01-25', '7892-1234', 'A-'),
    (1, '04121996005678', 'Carmen',    'Lima',       '1996-06-10', '7892-5678', 'B-'),
    (1, '04121991009012', 'José',      'Chávez',     '1991-12-03', '7892-9012', 'O+'),
    (1, '04121993003456', 'Patricia',  'Santos',     '1993-08-17', '7893-3456', 'A+'),
    (1, '04121989007890', 'Eduardo',   'Torres',     '1989-02-28', '7893-7890', 'B+'),
    (1, '04121985001234', 'Lucía',     'Martínez',   '1985-05-20', '7894-1234', 'O+'),
    (1, '04121994005678', 'Fernando',  'Cruz',       '1994-10-07', '7894-5678', 'A+'),
    (1, '04121997009012', 'Isabela',   'Rodríguez',  '1997-03-23', '7894-9012', 'AB-'),
    (1, '04121986003456', 'Marco',     'Vásquez',    '1986-07-11', '7895-3456', 'O-'),
    (1, '04121990007891', 'Diana',     'Fuentes',    '1990-12-19', '7895-7890', 'A+'),
    -- tenant 2 (2 pacientes)
    (2, '06121997001235', 'Andrés',    'Mejía',      '1997-04-05', '7896-1235', 'O+'),
    (2, '06121992005679', 'María',     'Orellana',   '1992-09-28', '7896-5679', 'B+')
ON CONFLICT DO NOTHING;

-- =====================================================================
-- CITAS — 28 nuevas para llegar a 30 (existen 2: RSV-0001 y RSV-0002)
-- Distribuidas entre los 4 doctores de tenant 1 + 1 de tenant 2.
-- Horarios en UTC (El Salvador = UTC-6): 14:00 UTC = 08:00 local
-- =====================================================================

-- Doctor Ana Martínez (tenant 1) — 6 nuevas citas
INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'ana.martinez.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04141199009999' AND tenant_id = 1),
       '2026-09-15 14:00:00+00', 'COMPLETED', 'Control mensual', 'RSV-0003'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0003');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'ana.martinez.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04141198512345' AND tenant_id = 1),
       '2026-09-16 15:00:00+00', 'COMPLETED', 'Seguimiento post-consulta', 'RSV-0004'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0004');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'ana.martinez.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121990001234' AND tenant_id = 1),
       '2026-09-17 14:00:00+00', 'COMPLETED', 'Primera consulta', 'RSV-0005'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0005');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'ana.martinez.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121988005678' AND tenant_id = 1),
       '2026-09-18 15:00:00+00', 'COMPLETED', 'Dolor de cabeza recurrente', 'RSV-0006'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0006');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'ana.martinez.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121995009012' AND tenant_id = 1),
       '2026-09-22 14:00:00+00', 'SCHEDULED', 'Control prenatal', 'RSV-0007'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0007');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'ana.martinez.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121992003456' AND tenant_id = 1),
       '2026-09-23 15:00:00+00', 'SCHEDULED', 'Chequeo general', 'RSV-0008'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0008');

-- Doctor Luis Hernández (tenant 1) — 6 citas
INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'luis.hernandez.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121987007890' AND tenant_id = 1),
       '2026-09-15 15:00:00+00', 'COMPLETED', 'Dolor en el pecho', 'RSV-0009'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0009');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'luis.hernandez.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '06121998001234' AND tenant_id = 1),
       '2026-09-16 14:00:00+00', 'COMPLETED', 'Palpitaciones frecuentes', 'RSV-0010'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0010');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'luis.hernandez.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121996005678' AND tenant_id = 1),
       '2026-09-17 15:00:00+00', 'COMPLETED', 'Presión arterial elevada', 'RSV-0011'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0011');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'luis.hernandez.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121991009012' AND tenant_id = 1),
       '2026-09-19 14:00:00+00', 'CANCELLED', 'Arritmia — paciente canceló', 'RSV-0012'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0012');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'luis.hernandez.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121993003456' AND tenant_id = 1),
       '2026-09-23 14:00:00+00', 'SCHEDULED', 'Control cardiaco periódico', 'RSV-0013'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0013');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'luis.hernandez.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121989007890' AND tenant_id = 1),
       '2026-10-01 15:00:00+00', 'SCHEDULED', 'Evaluación inicial', 'RSV-0014'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0014');

-- Doctor María Fuentes (tenant 1) — 7 citas
INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'maria.fuentes.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121985001234' AND tenant_id = 1),
       '2026-09-15 16:00:00+00', 'COMPLETED', 'Cefalea crónica', 'RSV-0015'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0015');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'maria.fuentes.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121994005678' AND tenant_id = 1),
       '2026-09-16 16:00:00+00', 'COMPLETED', 'Mareos frecuentes', 'RSV-0016'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0016');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'maria.fuentes.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121997009012' AND tenant_id = 1),
       '2026-09-17 16:00:00+00', 'COMPLETED', 'Migraña con aura', 'RSV-0017'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0017');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'maria.fuentes.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121986003456' AND tenant_id = 1),
       '2026-09-18 14:00:00+00', 'COMPLETED', 'Temblores en extremidades', 'RSV-0018'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0018');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'maria.fuentes.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121990007891' AND tenant_id = 1),
       '2026-09-22 15:00:00+00', 'SCHEDULED', 'Control neurológico', 'RSV-0019'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0019');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'maria.fuentes.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04141199009999' AND tenant_id = 1),
       '2026-09-23 16:00:00+00', 'SCHEDULED', 'Revisión post-tratamiento', 'RSV-0020'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0020');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'maria.fuentes.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04141198512345' AND tenant_id = 1),
       '2026-10-05 14:00:00+00', 'SCHEDULED', 'Seguimiento neurológico', 'RSV-0021'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0021');

-- Doctor José Ramírez (tenant 1) — 6 citas
INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'jose.ramirez.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121990001234' AND tenant_id = 1),
       '2026-09-15 17:00:00+00', 'COMPLETED', 'Acné severo', 'RSV-0022'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0022');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'jose.ramirez.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121988005678' AND tenant_id = 1),
       '2026-09-16 17:00:00+00', 'COMPLETED', 'Psoriasis en placas', 'RSV-0023'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0023');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'jose.ramirez.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121995009012' AND tenant_id = 1),
       '2026-09-18 16:00:00+00', 'COMPLETED', 'Dermatitis de contacto', 'RSV-0024'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0024');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'jose.ramirez.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121992003456' AND tenant_id = 1),
       '2026-09-18 17:00:00+00', 'SCHEDULED', 'Revisión de piel', 'RSV-0025'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0025');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'jose.ramirez.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '04121987007890' AND tenant_id = 1),
       '2026-09-22 16:00:00+00', 'SCHEDULED', 'Control crónico', 'RSV-0026'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0026');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 1,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'jose.ramirez.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '06121998001234' AND tenant_id = 1),
       '2026-10-02 14:00:00+00', 'SCHEDULED', 'Primera consulta dermatológica', 'RSV-0027'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0027');

-- Doctor Roberto Cruz (tenant 2) — 3 citas
INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 2,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'roberto.cruz.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '06141199907890' AND tenant_id = 2),
       '2026-09-15 14:00:00+00', 'COMPLETED', 'Control pediátrico', 'RSV-0028'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0028');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 2,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'roberto.cruz.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '06121997001235' AND tenant_id = 2),
       '2026-09-16 15:00:00+00', 'SCHEDULED', 'Primera consulta', 'RSV-0029'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0029');

INSERT INTO appointments (tenant_id, doctor_id, patient_id, scheduled_at, status, reason, reservation_code)
SELECT 2,
       (SELECT d.id FROM doctors d JOIN users u ON d.user_id = u.id WHERE u.email = 'roberto.cruz.demo@medisuite.test'),
       (SELECT id FROM patients WHERE dui = '06121992005679' AND tenant_id = 2),
       '2026-10-03 14:00:00+00', 'SCHEDULED', 'Revisión general', 'RSV-0030'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE reservation_code = 'RSV-0030');
