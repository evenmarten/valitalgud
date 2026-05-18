# EventDetailsView — Sündmuse üksikasjad

**Vaade:** `EventDetailsView.vue`
**Route:** `/events/:id`
**Tüüp:** Backend
**Staatus:** To Do

## Kontekst

Sündmuse üksikasjade vaade kuvab ühe konkreetse sündmuse täielikku infot — banner pildi, pealkirja, kuupäeva ja aja, asukoha, lühikirjelduse, oskuste-tagid ning organisaatori andmed. Kasutaja saab muuta oma registreerumise staatust (`LÄHEB`, `VÕIB-OLLA`, `EI LÄHE`) ning lisada/lugeda kommentaare. Vaade nõuab autentimist. Lehel on kaks tegevusnuppu: "Kinnita" (registreerumise kinnitamiseks) ja "Muuda valikut" (juba registreerunud kasutajale staatuse muutmiseks).

## Mocki vaade

![EventDetailsView mock](../../../mock%20pildid/EventDetailsView.vue.png)

## Endpointide ülevaade

| # | Meetod | Tee | Otstarve |
|---|--------|-----|----------|
| 1 | `GET` | `/api/events/{id}` | Hangi sündmuse üksikasjad |
| 2 | `POST` | `/api/events/{id}/register` | Registreeru sündmusele või muuda staatust |
| 3 | `DELETE` | `/api/events/{id}/register` | Tühista registreerumine |
| 4 | `GET` | `/api/events/{id}/comments` | Hangi sündmuse kommentaarid |
| 5 | `POST` | `/api/events/{id}/comments` | Lisa uus kommentaar |

---

## 1. `GET /api/events/{id}`

**Kontroller:** `EventController.java`
**Auth:** Jah (nõuab sisselogimist)

### Response Body — `EventDetailsResponseDto.java`

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `eventId` | `Long` | `events.id` |
| `title` | `String` | `events.title` |
| `description` | `String` | `events.description` |
| `city` | `String` | `cities.name` |
| `address` | `String` | `events.address` |
| `eventDate` | `LocalDate` | `events.event_date` |
| `startTime` | `LocalTime` | `events.start_time` |
| `endTime` | `LocalTime` | `events.end_time` |
| `maxParticipants` | `Integer` | `events.max_participants` |
| `currentParticipants` | `Integer` | `COUNT(registrations.id WHERE status = 'LAHEB')` |
| `skillTags` | `List<String>` | `skill_tags.name` (läbi `event_skill_tags`) |
| `bannerImageUrl` | `String` | `events.banner_image_url` |
| `organizerId` | `Long` | `events.organizer_id` |
| `organizerName` | `String` | `contacts.full_name` (organisaatori) |
| `organizerEmail` | `String` | `contacts.email` (organisaatori) |
| `userRegistrationStatus` | `String` | `registrations.status` praegusele kasutajale (`LAHEB` / `VOIB_OLLA` / `EI_LAHE` / `null` kui pole registreerunud) |

**Näidis:**
```json
{
  "eventId": 1,
  "title": "Suur Tehnoloogiakonverents",
  "description": "Aastane konverents...",
  "city": "Tallinn",
  "address": "Näidis tänav 123",
  "eventDate": "2023-10-26",
  "startTime": "09:00",
  "endTime": "17:00",
  "maxParticipants": 100,
  "currentParticipants": 42,
  "skillTags": ["IT", "Development"],
  "bannerImageUrl": "http://...",
  "organizerId": 5,
  "organizerName": "Tech Events OÜ",
  "organizerEmail": "organizer@example.com",
  "userRegistrationStatus": null
}
```

---

## 2. `POST /api/events/{id}/register`

**Kontroller:** `RegistrationController.java`
**Auth:** Jah (nõuab sisselogimist)

### Request Body — `RegistrationDto.java`

| Väli | Tüüp | Kirjeldus |
|------|------|-----------|
| `status` | `String` | Üks väärtustest: `LAHEB`, `VOIB_OLLA`, `EI_LAHE` |

**Näidis:**
```json
{
  "status": "LAHEB"
}
```

### Response Body — `RegistrationResponseDto.java`

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `registrationId` | `Long` | `registrations.id` |
| `eventId` | `Long` | `registrations.event_id` |
| `userId` | `Long` | `registrations.user_id` |
| `status` | `String` | `registrations.status` |
| `registeredAt` | `LocalDateTime` | `registrations.registered_at` |

**Näidis:**
```json
{
  "registrationId": 17,
  "eventId": 1,
  "userId": 12,
  "status": "LAHEB",
  "registeredAt": "2024-01-15T10:30:00"
}
```

> **Eeldus:** Kui kasutajal juba on registreering sündmusele, siis sama endpoint kasutab `UNIQUE (user_id, event_id)` piirangut ja teeb `UPDATE` operatsiooni (staatuse muutmiseks). Esmaregistreerimisel teeb `INSERT`. Endpoint on idempotentne.

---

## 3. `DELETE /api/events/{id}/register`

**Kontroller:** `RegistrationController.java`
**Auth:** Jah (nõuab sisselogimist)

### Request Body

Puudub.

### Response Body

204 No Content (keha puudub).

> Tühistab kasutaja registreerumise sündmusele (kustutab `registrations` kirje).

---

## 4. `GET /api/events/{id}/comments`

**Kontroller:** `CommentController.java`
**Auth:** Jah (nõuab sisselogimist)

### Response Body — `List<CommentResponseDto>`

`CommentResponseDto.java`:

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `commentId` | `Long` | `comments.id` |
| `authorName` | `String` | `contacts.full_name` (kommentaari autorilt) |
| `authorId` | `Long` | `comments.user_id` |
| `content` | `String` | `comments.content` |
| `createdAt` | `LocalDateTime` | `comments.created_at` |

**Näidis:**
```json
[
  {
    "commentId": 1,
    "authorName": "Alice Smith",
    "authorId": 7,
    "content": "I'm excited about the speakers this year! Will there be a live stream option?",
    "createdAt": "2024-01-12T14:25:00"
  },
  {
    "commentId": 2,
    "authorName": "Bob Johnson",
    "authorId": 9,
    "content": "Great question, Alice! I'm also hoping for a live stream.",
    "createdAt": "2024-01-14T09:10:00"
  }
]
```

> Kommentaarid tagastatakse sorteeritud `created_at DESC` järjekorras (uusimad esimesena).

---

## 5. `POST /api/events/{id}/comments`

**Kontroller:** `CommentController.java`
**Auth:** Jah (nõuab sisselogimist)

### Request Body — `CreateCommentDto.java`

| Väli | Tüüp | Kirjeldus |
|------|------|-----------|
| `content` | `String` | Kommentaari tekst (kohustuslik, max 1000 tähemärki) |

**Näidis:**
```json
{
  "content": "Põnev üritus, tulen kindlasti!"
}
```

### Response Body — `CommentResponseDto.java`

Vt eelpool punkt 4 (`CommentResponseDto`).

**Näidis:**
```json
{
  "commentId": 25,
  "authorName": "Mari Maasikas",
  "authorId": 12,
  "content": "Põnev üritus, tulen kindlasti!",
  "createdAt": "2024-01-15T11:00:00"
}
```

---

## Veahaldus

| Olukord | Exception klass | ErrorResponse enum | HTTP staatus | Sõnum kasutajale |
|---------|-----------------|--------------------|--------------|------------------|
| Sündmust ei leitud | `NotFoundException` | `EVENT_NOT_FOUND` | 404 | "Sündmust ei leitud" |
| Kasutaja ei ole sisse logitud | `UnauthorizedException` | `UNAUTHORIZED` | 401 | "Palun logi sisse" |
| Vale või puuduv registreerumise staatus | `BadRequestException` | `INVALID_REGISTRATION_STATUS` | 400 | "Vale registreerumise staatus" |
| Sündmus on täis (`currentParticipants >= maxParticipants`) | `ConflictException` | `EVENT_FULL` | 409 | "Sündmus on osalejatega täidetud" |
| Sündmus on tühistatud | `ConflictException` | `EVENT_CANCELLED` | 409 | "Sündmus on tühistatud" |
| Kommentaari sisu puudub või on tühi | `BadRequestException` | `COMMENT_CONTENT_REQUIRED` | 400 | "Kommentaari sisu on kohustuslik" |
| Kommentaar on liiga pikk (üle 1000 tähemärki) | `BadRequestException` | `COMMENT_TOO_LONG` | 400 | "Kommentaar on liiga pikk (max 1000 tähemärki)" |
| Üritatakse muuta või kustutada võõrast registreerumist | `ForbiddenException` | `NOT_ALLOWED` | 403 | "Sul ei ole õigust seda toimingut teha" |

> **Märkus veahalduse kohta:**
> Kontrolli, kas vajalikud `ErrorResponse` enum kirjed ja exception klassid juba eksisteerivad:
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/error/ErrorResponse.java`
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/exception/`
>
> Puuduvate enum kirjete puhul lisa need `ErrorResponse`-i. Puuduvate exception klasside puhul loo uus klass `exception/` paketti ja registreeri see `RestExceptionHandler`-is.

## Andmebaas

Seotud tabelid: `events`, `cities`, `skill_tags`, `event_skill_tags`, `registrations`, `comments`, `users`, `contacts`

- `events` — põhitabel sündmuse andmetega
- `events.city_id` → `cities.id` — linna nime saamiseks
- `event_skill_tags` (vahetabel) → `skill_tags` — sündmuse oskuste-tagide saamiseks
- `events.organizer_id` → `users.id` → `contacts` — organisaatori nime ja e-posti saamiseks
- `registrations` — sündmusele registreerumiste tabel:
  - `UNIQUE (user_id, event_id)` tagab, et üks kasutaja saab sama sündmuse jaoks olla ainult ühe staatusega
  - Registreerumise loomine/uuendamine kasutab UPSERT loogikat
- `comments` — sündmuse kommentaaride tabel:
  - `comments.event_id` → `events.id`
  - `comments.user_id` → `users.id` → `contacts.full_name` (autori nime jaoks)
  - Sorteeritakse `created_at DESC`

> **Eeldus:** "Sündmus on täis" kontroll tehakse ainult siis, kui kasutaja proovib registreeruda staatusega `LAHEB` ja `events.max_participants` ei ole `NULL`. Staatused `VOIB_OLLA` ja `EI_LAHE` ei loe kohta täis ning ei kuulu max_participants kontrolli.

## Navigatsioon (frontend kontekst)

| Tegevus | Sihtkoht |
|---------|----------|
| Header: "Homepage" | `/` (`LandingPage.vue`) |
| Header: "Profile" | `/profile` (`ProfileView.vue`) |
| Header: "Events" | `/events` (`EventsView.vue`) |
| Header: "My Events" | `/my-events` (`MyEventsView.vue`) |
| Header: "Calendar" | `/calendar` (`CalendarView.vue`) |
| Header: "Logout" | `localStorage.clear()` + `redirect → /` |
| Header: "Cart" | `/cart` (`CartView.vue`) |
| Nupp "Kinnita" | `POST /api/events/{id}/register` |
| Nupp "Muuda valikut" | `POST /api/events/{id}/register` (uuendab staatust) |
| Nupp "Post" (kommentaari all) | `POST /api/events/{id}/comments` |

## Vastuvõtu kriteeriumid

- [ ] `GET /api/events/{id}` tagastab 200 OK koos `EventDetailsResponseDto` kehaga
- [ ] `GET /api/events/{id}` tagastab 404 koos `EVENT_NOT_FOUND` veaga, kui sündmust ei leitud
- [ ] `POST /api/events/{id}/register` tagastab 200 OK (uuendus) või 201 Created (esmane registreerumine)
- [ ] `POST /api/events/{id}/register` tagastab 409 koos `EVENT_FULL` veaga, kui sündmus on täis (ainult staatuse `LAHEB` puhul)
- [ ] `POST /api/events/{id}/register` tagastab 400 koos `INVALID_REGISTRATION_STATUS` veaga vale staatuse puhul
- [ ] `DELETE /api/events/{id}/register` tagastab 204 No Content edukal tühistamisel
- [ ] `GET /api/events/{id}/comments` tagastab 200 OK koos `List<CommentResponseDto>` kehaga (sorteeritud `created_at DESC`)
- [ ] `POST /api/events/{id}/comments` tagastab 201 Created koos uue `CommentResponseDto` kehaga
- [ ] `POST /api/events/{id}/comments` tagastab 400 koos `COMMENT_CONTENT_REQUIRED` veaga tühja sisu puhul
- [ ] Kõik endpointid tagastavad 401 koos `UNAUTHORIZED` veaga väljalogitud kasutajale
- [ ] DTO klassid (`EventDetailsResponseDto`, `RegistrationDto`, `RegistrationResponseDto`, `CommentResponseDto`, `CreateCommentDto`) on loodud vastavate kontrollerite `dto/` pakettidesse
- [ ] Controller, Service, Repository kihid on eraldatud
- [ ] Kontrolleri meetoditel on `@Operation` ja `@ApiResponses` annotatsioonid (sh veavastused `ApiError` skeemiga)
- [ ] Swagger UI kaudu on kõik endpointid nähtavad ja testitavad
