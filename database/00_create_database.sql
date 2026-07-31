-- MediSuite — Creación de la base de datos compartida
-- Ejecutar UNA SOLA VEZ, conectado con un rol que tenga privilegio CREATEDB (ej. hector).
-- Orden de ejecución de toda la carpeta database/:
--   1) 00_create_database.sql  (este archivo)
--   2) schema.sql              (las 17 tablas, v2 auditada)
--   3) seed.sql                (datos ficticios de prueba)
--
-- ⚠️ IMPORTANTE si usas DBeaver: el comando `\c` (cambiar de base) es exclusivo
-- de psql — en el editor SQL de DBeaver NO cambia de conexión, y el GRANT
-- terminaría corriendo contra la base equivocada sin avisar. Por eso este
-- archivo está dividido en dos pasos que deben ejecutarse POR SEPARADO.

-- ─── PASO 1 — ejecutar conectado a "neondb" (o cualquier base, no a clinica_dev) ──
CREATE DATABASE clinica_dev;

-- ─── PASO 2 — abrir un editor SQL NUEVO, conectado ya a "clinica_dev", y correr esto ──
-- (en DBeaver: click derecho en clinica_dev → SQL Editor → New SQL Script)
--
-- GRANT CREATE ON SCHEMA public
--     TO hector, bayron, vigil, flores, diaz, melgar, merino, ventura, fuentes, vasquez, nicole;

-- ─── Si lo corres con psql en vez de DBeaver, sí puedes hacerlo todo junto ──
-- psql "host=... user=hector dbname=neondb sslmode=require" -f database/00_create_database.sql
-- (psql sí soporta \c dentro de un script — pero como archivo separado, no pegado en DBeaver)
