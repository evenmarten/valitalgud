-- Kustutab public schema (mis põhimõtteliselt kustutab kõik tabelid)
DROP SCHEMA IF EXISTS valitalgud CASCADE;
CREATE SCHEMA valitalgud;
GRANT ALL ON SCHEMA valitalgud TO postgres;
GRANT ALL ON SCHEMA valitalgud TO PUBLIC;