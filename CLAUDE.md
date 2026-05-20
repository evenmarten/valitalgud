# CLAUDE.md

See fail annab juhiseid Claude Code'ile (claude.ai/code) selles repositooriumis töötamiseks.

> **NB!** Kõik CLAUDE.md failid peavad alati jääma eestikeelseks. Ära tõlgi neid inglise keelde.

## Projekti ülevaade

Täisstack ürituste halduse rakendus (**valitalgud**) — Vue 3 frontend + Spring Boot backend + PostgreSQL andmebaas.

- Backendi juhised: `backend/CLAUDE.md`
- Frontendi juhised: `frontend/CLAUDE.md`

## Andmebaas (PostgreSQL)

Käivita SQL skriptid järjekorras `postgres` andmebaasi vastu:
1. `backend/database/1_reset_database.sql` — kustuta ja loo uuesti `public` skeem
2. `backend/database/2_create.sql` — loo tabelid
3. `backend/database/3_import.sql` — impordi algandmed

Ühendusandmed: host `localhost`, andmebaas `postgres`, kasutaja `postgres`, parool `student123`.

## Andmebaasskeem

15 tabelit `postgres` andmebaasi `public` skeemis. Kõik primaarvõtmed on `serial` (automaatselt kasvav täisarv).

**Kasutajad ja õigused**
- `roles` — rollid (`name` UNIQUE; algandmetes `USER`, `ADMIN`).
- `users` — kasutajad; `role_id` viitab `roles`-le (vaikimisi 1), `status IN ('ACTIVE', 'PENDING_ACTIVATION', 'DELETED')`.
- `contacts` — kasutaja kontaktandmed (1:1, `user_id` UNIQUE).

**Üritused**
- `cities` — linnad (`name` UNIQUE).
- `events` — üritused; `organizer_id` → `users`, `city_id` → `cities`.
- `skill_tags` — oskuste sildid (`name` UNIQUE).
- `event_skill_tags` — `events` ↔ `skill_tags` vahendajatabel.
- `registrations` — registreerumised; UNIQUE(`user_id`, `event_id`), `status IN ('LAHEB', 'VOIB_OLLA', 'EI_LAHE')`.
- `comments` — ürituse kommentaarid; `event_id` → `events`, `user_id` → `users`.

**Pood**
- `products` — tooted (hind, laoseis).
- `carts` — ostukorvid (üks kasutaja kohta, `user_id` UNIQUE).
- `cart_items` — ostukorvi read; UNIQUE(`cart_id`, `product_id`).
- `billings` — arve-/tarneandmed.
- `orders` — tellimused; `billing_id` → `billings`, `status IN ('PENDING', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED')`.
- `order_items` — tellimuse read (säilitab `product_name` ja `price_at_purchase` ostuhetkest).


## Workflow Orchestration\
\
### 1. Plan Mode Default\
- Enter plan mode for ANY non-trivial task (3+ steps or architectural decisions)\
- If something goes sideways, STOP and re-plan immediately\
- Use plan mode for verification steps, not just building\
- Write detailed specs upfront to reduce ambiguity\
  \
### 2. Subagent Strategy\
- Use subagents liberally to keep main context window clean\
- Offload research, exploration, and parallel analysis to subagents\
- For complex problems, throw more compute at it via subagents\
- One task per subagent for focused execution\
  \
### 3. Self-Improvement Loop\
- After ANY correction from the user: update tasks/lessons.md with the pattern\
- Write rules for yourself that prevent the same mistake\
- Ruthlessly iterate on these lessons until mistake rate drops\
- Review lessons at session start for relevant project\
  \
### 4. Verification Before Done\
- Never mark a task complete without proving it works\
- Diff behavior between main and your changes when relevant\
- Ask yourself: "Would a staff engineer approve this?"\
- Run tests, check logs, demonstrate correctness\
  \
### 5. Demand Elegance (Balanced)\
- For non-trivial changes: pause and ask "is there a more elegant way?"\
- If a fix feels hacky: "Knowing everything I know now, implement the elegant solution"\
- Skip this for simple, obvious fixes -- don't over-engineer\
- Challenge your own work before presenting it\
  \
### 6. Autonomous Bug Fixing\
- When given a bug report: just fix it. Don't ask for hand-holding\
- Point at logs, errors, failing tests -- then resolve them\
- Zero context switching required from the user\
- Go fix failing CI tests without being told how\
  \
## Task Management\
\
1. Plan First: Write plan to tasks/todo.md with checkable items\
2. Verify Plan: Check in before starting implementation\
3. Track Progress: Mark items complete as you go\
4. Explain Changes: High-level summary at each step\
5. Document Results: Add review section to tasks/todo.md\
6. Capture Lessons: Update tasks/lessons.md after corrections\
   \
## Core Principles\
\
- Simplicity First: Make every change as simple as possible. Impact minimal code.\
- No Laziness: Find root causes. No temporary fixes. Senior developer standards.\
- Minimal Impact: Only touch what's necessary. No side effects with new bugs.
