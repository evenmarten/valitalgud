# CLAUDE.md

See fail annab juhiseid Claude Code'ile (claude.ai/code) selles repositooriumis töötamiseks.

> **NB!** Kõik CLAUDE.md failid peavad alati jääma eestikeelseks. Ära tõlgi neid inglise keelde.

## Projekti ülevaade

Täisstack ürituste halduse rakendus (**valitalgud**) — Vue 3 frontend + Spring Boot backend + PostgreSQL andmebaas.

- Backendi juhised: `backend/CLAUDE.md`
- Frontendi juhised: `frontend/CLAUDE.md`

## Andmebaas (PostgreSQL)

Käivita SQL skriptid järjekorras `postgres` andmebaasi vastu:
1. `docs/database/1_reset_database.sql` — kustuta ja loo uuesti `valitalgud` skeem
2. `docs/database/2_create.sql` — loo tabelid
3. `docs/database/3_import.sql` — impordi algandmed

Ühendusandmed: host `localhost`, andmebaas `postgres`, kasutaja `postgres`, parool `student123`.

## Andmebaasskeem

Viis tabelit `valitalgud` PostgreSQL skeemis: `users`, `events`, `tags`, `event_tags` (vahendajatabel), `registrations`. Kõik primaarvõtmed on UUID-d (`gen_random_uuid()`). Registreerumise staatus on piiratud väärtustega `LAHEB | VOIB_OLLA | EI_LAHE`. Kasutaja roll on piiratud väärtustega `USER | ADMIN`.
