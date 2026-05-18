# MyEventsView — Sündmused, milles osalen

**Vaade:** `MyEventsView.vue`
**Route:** `/my-events`
**Tüüp:** Backend
**Staatus:** To Do

## Kontekst

Vaade kuvab sisselogitud kasutajale need sündmused, millele ta on registreerunud (staatus `LAHEB`, `VOIB_OLLA` või `EI_LAHE`). Filtreerimine toimub kolme tab-i kaudu: "Sel nädalal" (`THIS_WEEK`), "Tulevased" (`UPCOMING`) ja "Kõik" (`ALL_FUTURE`). Backend tagastab AINULT tulevased sündmused (tingimus: `event_date >= TODAY`), sõltumata filtrist. Iga sündmuse juures on registreerumise staatus (LÄHEB / VÕIB-OLLA / EI LÄHE) ja nupp "View event", mis suunab detailvaatesse. Lehe paremas ülanurgas on nupp "Minu loodud sündmused", mis suunab kasutaja `/my-organized-events` lehele.

## Mocki vaade

![MyEventsView mock](../../../mock%20pildid/MyEventsView.vue.png)

## Endpointide ülevaade

| # | Meetod | Tee | Otstarve |
|---|--------|-----|----------|
| 1 | `GET` | `/api/my-events?filter={THIS_WEEK\|UPCOMING\|ALL_FUTURE}` | Tagasta sisselogitud kasutaja registreerumised filtreeritult |

---

## 1. `GET /api/my-events`

**Kontroller:** `MyEventsController.java`
**Auth:** Jah (sisselogitud kasutaja) — kasutaja `id` võetakse autentitud kontekstist

### Request — query parameetrid

| Väli | Tüüp | Kohustuslik | Kirjeldus |
|------|------|-------------|-----------|
| `filter` | `String` (enum) | Jah | `THIS_WEEK` \| `UPCOMING` \| `ALL_FUTURE` |

> Filtri tähendused:
> - `THIS_WEEK` — sündmused, mille `event_date` jääb käesoleva nädala vahemikku (esmaspäev–pühapäev) ja on `>= TODAY`
> - `UPCOMING` — sündmused, mille `event_date > TODAY` (st alates homsest)
> - `ALL_FUTURE` — kõik sündmused, mille `event_date >= TODAY`

### Response Body — `List<MyEventResponseDto>` (`MyEventResponseDto.java`)

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `eventId` | `Long` | `events.id` |
| `title` | `String` | `events.title` |
| `date` | `LocalDate` | `events.event_date` |
| `location` | `String` | `cities.name` (JOIN `events.city_id`) |
| `description` | `String` | `events.description` (lühikirjeldus) |
| `userRegistrationStatus` | `String` | `registrations.status` (`LAHEB` \| `VOIB_OLLA` \| `EI_LAHE`) |

**Näidis:**
```json
[
  {
    "eventId": 1,
    "title": "Suur Tehnoloogiakonverents",
    "date": "2023-10-26",
    "location": "Tallinn",
    "description": "Aastane konverents...",
    "userRegistrationStatus": "LAHEB"
  }
]
```

> Eeldus: vastuse väljad on samad kõikide filtrite jaoks; backend rakendab filtri päringus, frontend ei tee täiendavat filtreerimist. Tühi tulemus tagastatakse `[]` kujul (200 OK).

> Eeldus: tühistatud sündmusi (`events.is_cancelled = true`) ei tagastata.

## Veahaldus

| Olukord | Exception klass | ErrorResponse enum | HTTP staatus | Sõnum kasutajale |
|---------|-----------------|--------------------|--------------|------------------|
| Kasutaja ei ole sisse logitud | `UnauthorizedException` | `NOT_AUTHENTICATED` | 401 | "Palun logige sisse" |
| Vigane või puuduv `filter` parameeter | `BadRequestException` | `INVALID_FILTER` | 400 | "Vigane filtri väärtus (lubatud: THIS_WEEK, UPCOMING, ALL_FUTURE)" |

> **Märkus veahalduse kohta:**
> Kontrolli, kas vajalikud `ErrorResponse` enum kirjed ja exception klassid juba eksisteerivad:
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/error/ErrorResponse.java`
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/exception/`
>
> Puuduvate enum kirjete puhul lisa need `ErrorResponse`-i. Puuduvate exception klasside puhul loo uus klass `exception/` paketti ja registreeri see `RestExceptionHandler`-is.

## Andmebaas

Seotud tabelid: `registrations`, `events`, `cities`

- `registrations.user_id` — võrdsus sisselogitud kasutaja `id`-ga
- `registrations.status` — kuvatakse vastuses (`userRegistrationStatus`)
- `registrations.event_id` → `events.id` — JOIN sündmuse andmete saamiseks
- `events.event_date` — filtri rakendamise alus (`>= TODAY` kõikide filtrite puhul, lisaks nädala vahemik `THIS_WEEK` puhul)
- `events.is_cancelled` — tühistatud sündmusi ei tagastata
- `events.city_id` → `cities.name` — kuvatakse `location` väljal

> Eeldus: päring tehakse repositooriumi meetodiga, mis koostab JPQL/`@Query` põhjal vastava `WHERE` tingimuse filtri jaoks (3 erinevat meetodit või üks meetod parameetritega).

## Navigatsioon (frontend kontekst)

| Tegevus | Sihtkoht |
|---------|----------|
| Tab "Sel nädalal" | `GET /api/my-events?filter=THIS_WEEK` |
| Tab "Tulevased" | `GET /api/my-events?filter=UPCOMING` |
| Tab "Kõik" | `GET /api/my-events?filter=ALL_FUTURE` |
| Nupp "View event" | `/events/:id` (`EventDetailsView.vue`) |
| Nupp "Minu loodud sündmused" | `/my-organized-events` (`OrganizedEventsView.vue`) — kuvab kõiki sisselogitud kasutaja loodud sündmusi |
| Header "Homepage \| Profile \| Events \| My Events \| Calendar \| Logout \| Cart" | Vastavad route'id |

## Vastuvõtu kriteeriumid

- [ ] `GET /api/my-events?filter=THIS_WEEK` tagastab 200 OK koos `List<MyEventResponseDto>` kehaga (tühi tulemus = `[]`)
- [ ] `GET /api/my-events?filter=UPCOMING` tagastab ainult sündmused, mille `event_date > TODAY`
- [ ] `GET /api/my-events?filter=ALL_FUTURE` tagastab kõik sündmused, mille `event_date >= TODAY`
- [ ] Möödunud kuupäevaga sündmusi ei tagastata üheski filtris
- [ ] Tühistatud sündmusi (`is_cancelled = true`) ei tagastata
- [ ] Sisselogimata kasutaja saab 401 koos `NOT_AUTHENTICATED` veaga
- [ ] Vigase filtri väärtuse puhul tagastab 400 koos `INVALID_FILTER` veaga
- [ ] DTO klass (`MyEventResponseDto`) on loodud `controller/myevents/dto/` paketti
- [ ] Controller, Service, Repository kihid on eraldatud
- [ ] Kontrolleri meetodil on `@Operation` ja `@ApiResponses` annotatsioonid (sh veavastused `ApiError` skeemiga)
- [ ] Swagger UI kaudu on endpoint nähtav ja testitav
