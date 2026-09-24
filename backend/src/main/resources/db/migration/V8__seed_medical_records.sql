-- V8: Seed de expedientes médicos y signos vitales para pacientes de tenant 1
-- Idempotente: usa INSERT ... WHERE NOT EXISTS.

-- =====================================================================
-- EXPEDIENTES (medical_records) — 5 pacientes adicionales de tenant 1
-- =====================================================================

INSERT INTO medical_records (tenant_id, patient_id, created_on, general_notes, created_at, updated_at)
SELECT 1, p.id, '2026-01-10', 'Paciente con hipertensión arterial controlada.',
       NOW(), NOW()
FROM patients p WHERE p.dui = '04121990001234' AND p.tenant_id = 1
AND NOT EXISTS (SELECT 1 FROM medical_records mr WHERE mr.patient_id = p.id);

INSERT INTO medical_records (tenant_id, patient_id, created_on, general_notes, created_at, updated_at)
SELECT 1, p.id, '2026-02-15', 'Paciente diabético tipo 2. Control trimestral.',
       NOW(), NOW()
FROM patients p WHERE p.dui = '04121988005678' AND p.tenant_id = 1
AND NOT EXISTS (SELECT 1 FROM medical_records mr WHERE mr.patient_id = p.id);

INSERT INTO medical_records (tenant_id, patient_id, created_on, general_notes, created_at, updated_at)
SELECT 1, p.id, '2026-03-20', 'Control prenatal. Semana 24.',
       NOW(), NOW()
FROM patients p WHERE p.dui = '04121995009012' AND p.tenant_id = 1
AND NOT EXISTS (SELECT 1 FROM medical_records mr WHERE mr.patient_id = p.id);

INSERT INTO medical_records (tenant_id, patient_id, created_on, general_notes, created_at, updated_at)
SELECT 1, p.id, '2026-04-05', 'Alergia a la penicilina. Asma leve intermitente.',
       NOW(), NOW()
FROM patients p WHERE p.dui = '04121992003456' AND p.tenant_id = 1
AND NOT EXISTS (SELECT 1 FROM medical_records mr WHERE mr.patient_id = p.id);

INSERT INTO medical_records (tenant_id, patient_id, created_on, general_notes, created_at, updated_at)
SELECT 1, p.id, '2026-05-12', 'Hipercolesterolemia. Sin antecedentes quirúrgicos.',
       NOW(), NOW()
FROM patients p WHERE p.dui = '04121987007890' AND p.tenant_id = 1
AND NOT EXISTS (SELECT 1 FROM medical_records mr WHERE mr.patient_id = p.id);

-- =====================================================================
-- SIGNOS VITALES — un registro por cada expediente nuevo
-- =====================================================================

INSERT INTO vital_signs (tenant_id, medical_record_id, recorded_at, temperature_c,
                         heart_rate, blood_pressure, weight_kg, height_cm, symptoms, priority, created_at, updated_at)
SELECT 1, mr.id, '2026-09-10 14:30:00+00',
       37.1, 78, '130/85', 72.5, 162, 'Cefalea leve.', 'NORMAL', NOW(), NOW()
FROM medical_records mr
JOIN patients p ON mr.patient_id = p.id
WHERE p.dui = '04121990001234' AND p.tenant_id = 1
AND NOT EXISTS (SELECT 1 FROM vital_signs vs WHERE vs.medical_record_id = mr.id);

INSERT INTO vital_signs (tenant_id, medical_record_id, recorded_at, temperature_c,
                         heart_rate, blood_pressure, weight_kg, height_cm, symptoms, priority, created_at, updated_at)
SELECT 1, mr.id, '2026-09-12 09:00:00+00',
       36.8, 82, '120/80', 88.0, 175, 'Fatiga general.', 'NORMAL', NOW(), NOW()
FROM medical_records mr
JOIN patients p ON mr.patient_id = p.id
WHERE p.dui = '04121988005678' AND p.tenant_id = 1
AND NOT EXISTS (SELECT 1 FROM vital_signs vs WHERE vs.medical_record_id = mr.id);

INSERT INTO vital_signs (tenant_id, medical_record_id, recorded_at, temperature_c,
                         heart_rate, blood_pressure, weight_kg, height_cm, symptoms, priority, created_at, updated_at)
SELECT 1, mr.id, '2026-09-14 11:00:00+00',
       36.5, 90, '110/70', 65.0, 160, 'Sin síntomas. Control rutinario.', 'NORMAL', NOW(), NOW()
FROM medical_records mr
JOIN patients p ON mr.patient_id = p.id
WHERE p.dui = '04121995009012' AND p.tenant_id = 1
AND NOT EXISTS (SELECT 1 FROM vital_signs vs WHERE vs.medical_record_id = mr.id);

INSERT INTO vital_signs (tenant_id, medical_record_id, recorded_at, temperature_c,
                         heart_rate, blood_pressure, weight_kg, height_cm, symptoms, priority, created_at, updated_at)
SELECT 1, mr.id, '2026-09-16 16:00:00+00',
       37.5, 95, '140/90', 80.0, 170, 'Dificultad para respirar leve.', 'URGENTE', NOW(), NOW()
FROM medical_records mr
JOIN patients p ON mr.patient_id = p.id
WHERE p.dui = '04121992003456' AND p.tenant_id = 1
AND NOT EXISTS (SELECT 1 FROM vital_signs vs WHERE vs.medical_record_id = mr.id);

INSERT INTO vital_signs (tenant_id, medical_record_id, recorded_at, temperature_c,
                         heart_rate, blood_pressure, weight_kg, height_cm, symptoms, priority, created_at, updated_at)
SELECT 1, mr.id, '2026-09-18 08:30:00+00',
       36.9, 70, '125/82', 68.0, 165, 'Mareos ocasionales.', 'NORMAL', NOW(), NOW()
FROM medical_records mr
JOIN patients p ON mr.patient_id = p.id
WHERE p.dui = '04121987007890' AND p.tenant_id = 1
AND NOT EXISTS (SELECT 1 FROM vital_signs vs WHERE vs.medical_record_id = mr.id);
