# EditEventView — Muuda sündmust

**Vaade:** `EditEventView.vue`
**Route:** `/events/:id/edit`
**Tüüp:** Backend
**Staatus:** To Do

## Kontekst

Vorm sündmuse muutmiseks. Vormi avamisel laaditakse olemasoleva sündmuse andmed (`GET /api/events/{id}`), linnade loend (`GET /api/cities`) ja oskuse-tagide loend (`GET /api/skill-tags`). Vorm on visuaalselt sarnane `CreateEventView`-iga, kuid väljad on eeltäidetud ja `maxParticipants` ei tohi olla väiksem kui hetkel registreerunud osalejate arv (`currentParticipants`). Sündmuse muutmist (`PUT /api/events/{id}`) ja tühistamist (`DELETE /api/events/{id}` — pehme kustutus `is_cancelled = true`) saab teha ainult sündmuse looja või ADMIN. Eduka salvestamise või tühistamise järel suunatakse kasutaja `/my-organized-events` lehele.

## Mocki vaade

![EditEventView mock](../../../mock%20pildid/EditEventView.vue.png)

## Endpointide ülevaade

| # | Meetod | Tee | Otstarve |
|---|--------|-----|----------|
| 1 | `GET` | `/api/events/{id}` | Tagasta sündmuse andmed vormi eeltäitmiseks |
| 2 | `GET` | `/api/cities` | Tagasta linnade loend (sama mis `CreateEventView`) |
| 3 | `GET` | `/api/skill-tags` | Tagasta oskuse-tagide loend (sama mis `CreateEventView`) |
| 4 | `PUT` | `/api/events/{id}` | Uuenda sündmust |
| 5 | `DELETE` | `/api/events/{id}` | Tühista sündmus (pehme kustutus) |

---

## 1. `GET /api/events/{id}`

**Kontroller:** `EventController.java`
**Auth:** Jah (sisselogitud kasutaja); muutmiseks lisaks omanik või ADMIN — kontrollitakse `PUT`/`DELETE` puhul

### Response Body — `EventResponseDto.java` (laiendatud vormi eeltäitmise versioon)

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `eventId` | `Long` | `events.id` |
| `title` | `String` | `events.title` |
| `description` | `String` | `events.description` |
| `cityId` | `Long` | `events.city_id` |
| `address` | `String` | `events.address` |
| `date` | `LocalDate` | `events.event_date` |
| `startTime` | `String` (`HH:mm`) | `events.start_time` |
| `endTime` | `String` (`HH:mm`) | `events.end_time` |
| `maxParticipants` | `Integer` | `events.max_participants` |
| `currentParticipants` | `Integer` | Tuletatud: `registrations` arv `event_id = id` ja `status = 'LAHEB'` |
| `skillTagIds` | `List<Long>` | `event_skill_tags.skill_tag_id` (kõik selle sündmuse jaoks) |
| `bannerImageUrl` | `String` | `events.banner_image_url` |
| `organizerId` | `Long` | `events.organizer_id` |
| `isCancelled` | `Boolean` | `events.is_cancelled` |

**Näidis:**
```json
{
  "eventId": 10,
  "title": "Muudetud Sündmus",
  "description": "Uuendatud kirjeldus",
  "cityId": 1,
  "address": "Muudetud tänav 1",
  "date": "2024-01-02",
  "startTime": "11:00",
  "endTime": "13:00",
  "maxParticipants": 60,
  "currentParticipants": 12,
  "skillTagIds": [1, 3],
  "bannerImageUrl": "http://...",
  "organizerId": 1,
  "isCancelled": false
}
```

## 2. `GET /api/cities`

Sama, mis `CreateEventView.md` punktis 1.

## 3. `GET /api/skill-tags`

Sama, mis `CreateEventView.md` punktis 2.

## 4. `PUT /api/events/{id}`

**Kontroller:** `EventController.java`
**Auth:** Jah; lisaks peab olema sündmuse omanik (`events.organizer_id = currentUser.id`) või ADMIN

### Request Body — `UpdateEventDto.java`

| Väli | Tüüp | Kohustuslik | Kirjeldus |
|------|------|-------------|-----------|
| `title` | `String` | Jah | Sündmuse pealkiri |
| `description` | `String` | Ei | Sündmuse kirjeldus |
| `address` | `String` | Jah | Sündmuse aadress |
| `date` | `LocalDate` | Jah | Sündmuse kuupäev |
| `startTime` | `String` (`HH:mm`) | Jah | Algusaeg |
| `endTime` | `String` (`HH:mm`) | Jah | Lõpuaeg (peab olema hilisem kui `startTime`) |
| `maxParticipants` | `Integer` | Jah | Maksimaalne osalejate arv (EI tohi olla väiksem kui `currentParticipants`) |
| `skillTagIds` | `List<Long>` | Ei | Valitud oskuse-tagide id-d |
| `bannerImageUrl` | `String` | Ei | Banner-pildi URL |

**Näidis:**
```json
{
  "title": "Muudetud Sündmus",
  "description": "Uuendatud kirjeldus",
  "address": "Muudetud tänav 1",
  "date": "2024-01-02",
  "startTime": "11:00",
  "endTime": "13:00",
  "maxParticipants": 60,
  "skillTagIds": [1, 3],
  "bannerImageUrl": "http://..."
}
```

> Eeldus: `cityId` jääb muutmata (vorm ei luba linna muuta — see on visuaalselt nähtav, kuid dropdown on edit-vormis ainult informatiivne). Kui frontend saadab `cityId`, võib backend seda samuti aktsepteerida ja valideerida `cities` tabeli vastu.

### Response Body — `EventResponseDto.java`

Sama struktuur nagu punktis 1 (uuendatud väärtustega).

## 5. `DELETE /api/events/{id}`

**Kontroller:** `EventController.java`
**Auth:** Jah; lisaks peab olema sündmuse omanik või ADMIN

> Pehme kustutus: backend märgib sündmuse staatuseks `is_cancelled = true` (rida ei kustutata füüsiliselt).

### Response

`204 No Content` või `200 OK` koos `EventResponseDto`-ga, kus `isCancelled = true`. Eeldus: kasutame `204 No Content` (kuna frontend teeb pärast `redirect → /my-organized-events`).

## Veahaldus

| Olukord | Exception klass | ErrorResponse enum | HTTP staatus | Sõnum kasutajale |
|---------|-----------------|--------------------|--------------|------------------|
| Sündmust ei leitud | `NotFoundException` | `EVENT_NOT_FOUND` | 404 | "Sündmust ei leitud" |
| Kasutaja ei ole sisse logitud | `UnauthorizedException` | `NOT_AUTHENTICATED` | 401 | "Palun logige sisse" |
| Kasutaja pole omanik ega ADMIN | `ForbiddenException` | `NOT_EVENT_OWNER` | 403 | "Teil pole õigust seda sündmust muuta/tühistada" |
| Kohustuslik väli puudub või on vigane | `BadRequestException` | `INVALID_EVENT_DATA` | 400 | "Palun täitke kõik väljad õigesti" |
| `endTime <= startTime` | `BadRequestException` | `INVALID_EVENT_TIME_RANGE` | 400 | "Lõpuaeg peab olema hilisem kui algusaeg" |
| `maxParticipants < currentParticipants` | `BadRequestException` | `MAX_PARTICIPANTS_BELOW_CURRENT` | 400 | "Maksimaalne osalejate arv ei saa olla väiksem kui registreerunute arv" |
| `date` on minevikus | `BadRequestException` | `INVALID_EVENT_DATE` | 400 | "Sündmuse kuupäev peab olema tulevikus" |
| Viidatud `skillTagIds` sisaldab tundmatut id-d | `BadRequestException` | `SKILL_TAG_NOT_FOUND` | 400 | "Valitud oskuse-tagi ei leitud" |

> **Märkus veahalduse kohta:**
> Kontrolli, kas vajalikud `ErrorResponse` enum kirjed ja exception klassid juba eksisteerivad:
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/error/ErrorResponse.java`
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/exception/`
>
> Puuduvate enum kirjete puhul lisa need `ErrorResponse`-i. Puuduvate exception klasside puhul loo uus klass `exception/` paketti ja registreeri see `RestExceptionHandler`-is.

## Andmebaas

Seotud tabelid: `events`, `cities`, `skill_tags`, `event_skill_tags`, `registrations`, `users`, `contacts`

- `events` — uuendatakse väljad päringu põhjal; `is_cancelled` lülitatakse `true` peale `DELETE` korral
- `event_skill_tags` — uuendamisel kustuta vanad read selle sündmuse jaoks ja lisa uued (`skillTagIds` põhjal)
- `registrations` — `currentParticipants` arvutamiseks loendatakse read, kus `event_id = id` ja `status = 'LAHEB'`
- `events.organizer_id` → `users.id` — kontrolli, et sisselogitud kasutaja on omanik (või kontrolli rolli `roles.name = 'ADMIN'`)

> Eeldus: kogu uuendamine toimub ühe `@Transactional` teenusmeetodi sees (events uuendamine + event_skill_tags asendamine).

## Navigatsioon (frontend kontekst)

| Tegevus | Sihtkoht |
|---------|----------|
| Vormi avamisel | `GET /api/events/:id` + `GET /api/cities` + `GET /api/skill-tags` |
| Nupp "Salvesta" | `PUT /api/events/:id` |
| Nupp "Tühista sündmus" | Avab kinnitusmodaali; kinnitusel `DELETE /api/events/:id` |
| Peale õnnestumist (mõlemal nupul) | `redirect → /my-organized-events` (`OrganizedEventsView.vue`) |
| Header "Homepage \| Profile \| Events \| My Events \| Calendar \| Logout \| Cart" | Vastavad route'id |

## Vastuvõtu kriteeriumid

- [ ] `GET /api/events/{id}` tagastab 200 OK koos `EventResponseDto` kehaga (sh `currentParticipants` ja `skillTagIds`)
- [ ] `PUT /api/events/{id}` tagastab 200 OK koos uuendatud `EventResponseDto` kehaga
- [ ] `DELETE /api/events/{id}` tagastab 204 No Content ja seab `is_cancelled = true`
- [ ] Sisselogimata kasutaja saab 401 koos `NOT_AUTHENTICATED` veaga
- [ ] Kui kasutaja ei ole omanik ega ADMIN, tagastab 403 koos `NOT_EVENT_OWNER` veaga
- [ ] Olematu sündmuse korral tagastab 404 koos `EVENT_NOT_FOUND` veaga
- [ ] `maxParticipants < currentParticipants` korral tagastab 400 koos `MAX_PARTICIPANTS_BELOW_CURRENT` veaga
- [ ] DTO klassid (`UpdateEventDto`, `EventResponseDto`) on loodud `controller/event/dto/` paketti
- [ ] Controller, Service, Repository kihid on eraldatud
- [ ] Uuendamine on `@Transactional` (events + event_skill_tags asendamine atomaarselt)
- [ ] Kontrolleri meetoditel on `@Operation` ja `@ApiResponses` annotatsioonid (sh veavastused `ApiError` skeemiga)
- [ ] Swagger UI kaudu on endpointid nähtavad ja testitavad
