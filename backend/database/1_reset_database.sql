-- Kustutab public schema (mis põhimõtteliselt kustutab kõik tabelid)
DROP SCHEMA IF EXISTS public CASCADE;
-- Loob uue public schema
CREATE SCHEMA public;
-- Taastab vajalikud andmebaasi õigused
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;
