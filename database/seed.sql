-- MediSuite — Datos ficticios de demostración (v2, alineado a schema.sql v2)
-- 2 tenants (para probar aislamiento multi-tenant) con datos de ejemplo en cada tabla.
-- NINGÚN dato aquí es real. password_hash es un placeholder de formato, no un hash válido.

-- ─── Tenants demo ────────────────────────────────────────────────────────
INSERT INTO tenants (slug, commercial_name, legal_name, tax_id, country, plan, status, max_users, max_patients, max_doctors)
VALUES
    ('clinica-san-rafael', 'Clínica San Rafael', 'Clínica San Rafael S.A. de C.V.', '0614-000001-001-0', 'El Salvador', 'PRO', 'ACTIVE', 30, 2000, 15),
    ('clinica-santa-lucia', 'Clínica Santa Lucía', 'Clínica Santa Lucía S.A. de C.V.', '0614-000002-001-0', 'El Salvador', 'FREE', 'TRIAL', 10, 500, 5);

-- ─── Especialidades (v2) ─────────────────────────────────────────────────
INSERT INTO specialties (tenant_id, name) VALUES
    (1, 'Medicina General'),
    (1, 'Pediatría'),
    (1, 'Ginecología'),
    (2, 'Pediatría');

-- ─── Usuarios (tenant 1: Clínica San Rafael, id=1) ───────────────────────
INSERT INTO users (tenant_id, first_name, last_name, cif, email, password_hash, role) VALUES
    (1, 'Ana',    'Martínez', '04141201900101', 'ana.martinez.demo@medisuite.test',   '$2a$12$bDYB/iG64e7v8J1vqnT9g.RuF0G62xSyyFtFuaIP//IXLoZqn2MW2', 'DOCTOR'),
    (1, 'Carlos', 'Gómez',    '04141199001010', 'carlos.gomez.demo@medisuite.test',   '$2a$12$bDYB/iG64e7v8J1vqnT9g.RuF0G62xSyyFtFuaIP//IXLoZqn2MW2', 'NURSE'),
    (1, 'Beatriz','Reyes',    '04140198501234', 'beatriz.reyes.demo@medisuite.test',  '$2a$12$bDYB/iG64e7v8J1vqnT9g.RuF0G62xSyyFtFuaIP//IXLoZqn2MW2', 'ADMIN'),
    (1, 'Jorge',  'Alas',     '04141199505678', 'jorge.alas.demo@medisuite.test',     '$2a$12$bDYB/iG64e7v8J1vqnT9g.RuF0G62xSyyFtFuaIP//IXLoZqn2MW2', 'RECEPTIONIST');

-- ─── Usuarios (tenant 2: Clínica Santa Lucía, id=2 — para prueba de aislamiento) ──
INSERT INTO users (tenant_id, first_name, last_name, cif, email, password_hash, role) VALUES
    (2, 'Roberto', 'Cruz', '06140199003456', 'roberto.cruz.demo@medisuite.test', '$2a$12$bDYB/iG64e7v8J1vqnT9g.RuF0G62xSyyFtFuaIP//IXLoZqn2MW2', 'DOCTOR');

-- ─── Roles específicos (tenant 1) ────────────────────────────────────────
INSERT INTO doctors (tenant_id, user_id, specialty_id, license_number, available_schedule) VALUES
    (1, 1, 1, 'MED-2020-0456', '{"mon":["08:00-12:00","14:00-17:00"],"wed":["08:00-12:00"]}');

INSERT INTO nurses (tenant_id, user_id, shift, assigned_area) VALUES
    (1, 2, 'MATUTINO', 'Triaje');

INSERT INTO administrators (tenant_id, user_id, permission_level) VALUES
    (1, 3, 'FULL');

INSERT INTO receptionists (tenant_id, user_id, shift, assigned_office) VALUES
    (1, 4, 'MATUTINO', 'Recepción Principal');

-- Pacientes sin cuenta de usuario (gestionados por personal clínico — MVP)
INSERT INTO patients (tenant_id, first_name, last_name, cif, birth_date, phone, address, emergency_contact, blood_type, allergies) VALUES
    (1, 'Maria',  'Lopez',    '04141199009999', '1990-05-12', '7000-1111', 'Col. Escalón, San Salvador', 'Juan Lopez - 7000-2222',     'O+', 'Ninguna conocida'),
    (1, 'Pedro',  'Hernandez','04141198512345', '1985-11-03', '7000-3333', 'Santa Tecla, La Libertad',   'Ana Hernandez - 7000-4444',  'A-', 'Penicilina');

-- Rol específico (tenant 2 — datos mínimos para probar que tenant 1 no los ve)
INSERT INTO doctors (tenant_id, user_id, specialty_id, license_number, available_schedule) VALUES
    (2, 5, 4, 'MED-2019-0789', '{"tue":["09:00-13:00"]}');
INSERT INTO patients (tenant_id, first_name, last_name, cif, birth_date, phone, address, emergency_contact, blood_type, allergies) VALUES
    (2, 'Silvia', 'Portillo', '06141199907890', '2001-02-20', '7000-5555', 'Santa Ana', 'Marta Portillo - 7000-6666', 'B+', 'Ninguna conocida');

-- ─── Citas (tenant 1) — v2: con reservation_code ─────────────────────────
INSERT INTO appointments (tenant_id, patient_id, doctor_id, scheduled_at, status, reason, office, reservation_code) VALUES
    (1, 1, 1, '2026-08-01 09:00:00-06', 'SCHEDULED', 'Control de rutina', 'Consultorio 1', 'RSV-0001'),
    (1, 2, 1, '2026-08-01 10:00:00-06', 'WAITING',   'Dolor abdominal',   'Consultorio 1', 'RSV-0002');

-- ─── Expedientes (tenant 1) ───────────────────────────────────────────────
INSERT INTO medical_records (tenant_id, patient_id, general_notes) VALUES
    (1, 1, 'Paciente sin antecedentes relevantes.'),
    (1, 2, 'Paciente con alergia a penicilina registrada.');

-- ─── Signos vitales (tenant 1) ────────────────────────────────────────────
INSERT INTO vital_signs (tenant_id, medical_record_id, weight_kg, height_cm, blood_pressure, temperature_c, heart_rate, symptoms, priority) VALUES
    (1, 1, 68.5, 165.0, '120/80', 36.7, 72, 'Ninguno', 'LOW'),
    (1, 2, 74.2, 172.0, '130/85', 37.2, 88, 'Dolor abdominal leve', 'MEDIUM');

-- ─── Recetas (tenant 1) ───────────────────────────────────────────────────
INSERT INTO prescriptions (tenant_id, medical_record_id, doctor_id, medications, dosage, duration, instructions) VALUES
    (1, 2, 1, 'Butilhioscina 10mg', '1 tableta cada 8 horas', '3 días', 'Tomar con alimentos.');

-- ─── Inventario / Compras / Activos (tenant 1) ───────────────────────────
INSERT INTO products (tenant_id, name, category, unit_of_measure, current_stock, min_stock, unit_price) VALUES
    (1, 'Guantes de nitrilo (caja 100u)', 'Insumos médicos', 'caja', 45, 10, 8.50),
    (1, 'Butilhioscina 10mg (blíster 20u)', 'Medicamentos', 'blíster', 120, 20, 3.25);

INSERT INTO purchase_orders (tenant_id, supplier, status, total_amount) VALUES
    (1, 'Distribuidora Médica S.A.', 'PENDING', 450.00);

-- v2: líneas de detalle de la orden de compra
INSERT INTO purchase_order_items (tenant_id, purchase_order_id, product_id, quantity, unit_price, received_quantity) VALUES
    (1, 1, 1, 40, 8.50, 0),
    (1, 1, 2, 34, 3.25, 0);

INSERT INTO physical_assets (tenant_id, name, category, acquisition_value, acquired_on, status, location) VALUES
    (1, 'Tensiómetro digital', 'Equipo médico', 65.00, '2025-03-15', 'ACTIVE', 'Consultorio 1'),
    (1, 'Camilla de exploración', 'Mobiliario clínico', 180.00, '2024-09-01', 'ACTIVE', 'Consultorio 1');

-- ─── Auditoría (ejemplo de registro automático) ──────────────────────────
INSERT INTO audit_logs (tenant_id, user_id, action, entity_name, entity_id, data_after, ip_address) VALUES
    (1, 3, 'CREATE', 'patients', 1, '{"first_name":"Maria","last_name":"Lopez","cif":"04141199009999"}', '190.10.20.30');
