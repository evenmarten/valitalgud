# CreateEventView — Loo uus sündmus

**Vaade:** `CreateEventView.vue`
**Route:** `/events/create`
**Tüüp:** Backend
**Staatus:** To Do

## Kontekst

Vorm sisselogitud kasutajale uue sündmuse loomiseks. Vorm sisaldab pealkirja, kirjeldust, linna (dropdown valikust), aadressi, kuupäeva, algus- ja lõpuaega, maksimaalset osalejate arvu, oskuse-tagide multi-select valikut ja banner-pildi URL-i. Linnade ja oskuse-tagide loendid laaditakse vormi avamisel eraldi `GET` päringutega. Sündmuse loomine toimub `POST /api/events` kaudu; loomisel määratakse organiseerijaks sisselogitud kasutaja backend-i poolt. Eduka loomise järel suunatakse kasutaja `/my-organized-events` lehele.

## Mocki vaade

![CreateEventView mock](../../../mock%20pildid/CreateEventView.vue.png)

## Endpointide ülevaade

| # | Meetod | Tee | Otstarve |
|---|--------|-----|----------|
| 1 | `GET` | `/api/cities` | Tagasta kõik linnad (dropdown jaoks) |
| 2 | `GET` | `/api/skill-tags` | Tagasta kõik oskuse-tagid (multi-select jaoks) |
| 3 | `POST` | `/api/events` | Loo uus sündmus |

---

## 1. `GET /api/cities`

**Kontroller:** `CityController.java`
**Auth:** Jah (sisselogitud kasutaja)

### Response Body — `List<CityResponseDto>` (`CityResponseDto.java`)

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `id` | `Long` | `cities.id` |
| `name` | `String` | `cities.name` |

**Näidis:**
```json
[
  { "id": 1, "name": "Tallinn" },
  { "id": 2, "name": "Tartu" }
]
```

## 2. `GET /api/skill-tags`

**Kontroller:** `SkillTagController.java`
**Auth:** Jah (sisselogitud kasutaja)

### Response Body — `List<SkillTagResponseDto>` (`SkillTagResponseDto.java`)

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `id` | `Long` | `skill_tags.id` |
| `name` | `String` | `skill_tags.name` |

**Näidis:**
```json
[
  { "id": 1, "name": "Option 1" },
  { "id": 2, "name": "Option 2" }
]
```

## 3. `POST /api/events`

**Kontroller:** `EventController.java`
**Auth:** Jah (sisselogitud kasutaja) — organiseerija määratakse backend-i poolt sisselogitud kasutaja põhjal

### Request Body — `CreateEventDto.java`

| Väli | Tüüp | Kohustuslik | Kirjeldus |
|------|------|-------------|-----------|
| `title` | `String` | Jah | Sündmuse pealkiri (max 200) |
| `description` | `String` | Ei | Sündmuse kirjeldus (max 2000) |
| `cityId` | `Long` | Jah | Viide tabelisse `cities` |
| `address` | `String` | Jah | Sündmuse aadress (max 255) |
| `date` | `LocalDate` (`YYYY-MM-DD`) | Jah | Sündmuse kuupäev (peab olema tulevikus) |
| `startTime` | `String` (`HH:mm`) | Jah | Algusaeg |
| `endTime` | `String` (`HH:mm`) | Jah | Lõpuaeg (peab olema hilisem kui `startTime`) |
| `maxParticipants` | `Integer` | Jah | Maksimaalne osalejate arv (> 0) |
| `bannerImageUrl` | `String` | Ei | Banner-pildi URL (max 500) |
| `skillTagIds` | `List<Long>` | Ei | Valitud oskuse-tagide id-d (vahendajatabel `event_skill_tags`) |

**Näidis:**
```json
{
  "title": "Uus Sündmus",
  "description": "Sündmuse kirjeldus",
  "cityId": 1,
  "address": "Uus tänav 1",
  "date": "2024-01-01",
  "startTime": "10:00",
  "endTime": "12:00",
  "maxParticipants": 50,
  "bannerImageUrl": "http://example.com/new_banner.jpg",
  "skillTagIds": [1, 2]
}
```

### Response Body — `EventResponseDto.java`

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `eventId` | `Long` | `events.id` (just loodud) |
| `title` | `String` | `events.title` |
| `currentParticipants` | `Integer` | Tuletatud: `registrations` arv, kus `event_id = events.id` ja `status = 'LAHEB'` (uue sündmuse puhul `0`) |
| `organizerId` | `Long` | `events.organizer_id` (= sisselogitud kasutaja `id`) |
| `organizerName` | `String` | `contacts.full_name` (JOIN organiseerija `users.id` → `contacts.user_id`) |

**Näidis:**
```json
{
  "eventId": 10,
  "title": "Uus Sündmus",
  "currentParticipants": 0,
  "organizerId": 1,
  "organizerName": "Mari Maasikas"
}
```

> Eeldus: HTTP staatus 201 Created koos `Location` päisega `/api/events/{eventId}`.

## Veahaldus

| Olukord | Exception klass | ErrorResponse enum | HTTP staatus | Sõnum kasutajale |
|---------|-----------------|--------------------|--------------|------------------|
| Kohustuslik väli puudub või on vigane (pealkiri, linn, aadress, kuupäev, startTime, endTime, maxParticipants) | `BadRequestException` | `INVALID_EVENT_DATA` | 400 | "Palun täitke kõik väljad õigesti" |
| `endTime <= startTime` | `BadRequestException` | `INVALID_EVENT_TIME_RANGE` | 400 | "Lõpuaeg peab olema hilisem kui algusaeg" |
| `date` on minevikus | `BadRequestException` | `INVALID_EVENT_DATE` | 400 | "Sündmuse kuupäev peab olema tulevikus" |
| `maxParticipants <= 0` | `BadRequestException` | `INVALID_PARTICIPANTS_COUNT` | 400 | "Maksimaalne osalejate arv peab olema suurem kui 0" |
| Kasutaja ei ole sisse logitud | `UnauthorizedException` | `NOT_AUTHENTICATED` | 401 | "Palun logige sisse" |
| Viidatud `cityId` ei eksisteeri | `BadRequestException` | `CITY_NOT_FOUND` | 400 | "Valitud linna ei leitud" |
| Viidatud `skillTagIds` sisaldab tundmatut id-d | `BadRequestException` | `SKILL_TAG_NOT_FOUND` | 400 | "Valitud oskuse-tagi ei leitud" |

> **Märkus veahalduse kohta:**
> Kontrolli, kas vajalikud `ErrorResponse` enum kirjed ja exception klassid juba eksisteerivad:
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/error/ErrorResponse.java`
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/exception/`
>
> Puuduvate enum kirjete puhul lisa need `ErrorResponse`-i. Puuduvate exception klasside puhul loo uus klass `exception/` paketti ja registreeri see `RestExceptionHandler`-is.

## Andmebaas

Seotud tabelid: `events`, `cities`, `skill_tags`, `event_skill_tags`, `users`, `contacts`

- `events` — uus rida loodud sündmuse jaoks; `organizer_id` võetakse sisselogitud kasutaja kontekstist (mitte päringust)
- `events.city_id` → `cities.id` — kontrolli, et viidatud linn eksisteerib
- `event_skill_tags` — iga valitud `skillTagId` jaoks lisa rida `(event_id, skill_tag_id)`
- `skill_tags` — kontrolli, et iga viidatud `skill_tag_id` eksisteerib
- `events.is_cancelled` — uue sündmuse loomisel `false` (vaikeväärtus)
- `contacts.full_name` — vastuses tagastatud `organizerName` (JOIN `users.id` → `contacts.user_id`)

> Eeldus: kogu loomine toimub ühe `@Transactional` teenusmeetodi sees, et `events` ja `event_skill_tags` ridade lisamine toimuks atomaarselt.

## Navigatsioon (frontend kontekst)

| Tegevus | Sihtkoht |
|---------|----------|
| Vormi avamisel | `GET /api/cities` ja `GET /api/skill-tags` (linnade dropdown ja tagide multi-select täitmine) |
| Nupp "Loo sündmus" | `POST /api/events` |
| Eduka loomise järel | `redirect → /my-organized-events` (`OrganizedEventsView.vue`) |
| Header "Homepage \| Profile \| Events \| My Events \| Calendar \| Logout \| Cart" | Vastavad route'id |

## Vastuvõtu kriteeriumid

- [ ] `GET /api/cities` tagastab 200 OK koos linnade loendiga
- [ ] `GET /api/skill-tags` tagastab 200 OK koos oskuse-tagide loendiga
- [ ] `POST /api/events` tagastab 201 Created koos `EventResponseDto` kehaga
- [ ] Sisselogimata kasutaja saab 401 koos `NOT_AUTHENTICATED` veaga
- [ ] Vigaste väljade puhul tagastab 400 vastava `ErrorResponse` enumiga
- [ ] `organizer_id` määratakse alati backend-is sisselogitud kasutaja põhjal (mitte päringust)
- [ ] Tühistatud (`is_cancelled = true`) on uue sündmuse korral `false`
- [ ] DTO klassid (`CreateEventDto`, `EventResponseDto`, `CityResponseDto`, `SkillTagResponseDto`) on loodud vastavatesse `controller/<ressurss>/dto/` pakettidesse
- [ ] Controller, Service, Repository kihid on eraldatud
- [ ] Loomine toimub `@Transactional` teenusmeetodis (atomaarselt `events` + `event_skill_tags`)
- [ ] Kontrolleri meetoditel on `@Operation` ja `@ApiResponses` annotatsioonid (sh veavastused `ApiError` skeemiga)
- [ ] Swagger UI kaudu on endpointid nähtavad ja testitavad
