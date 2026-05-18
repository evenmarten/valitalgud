# CalendarView — Kalendrivaade

**Vaade:** `CalendarView.vue`
**Route:** `/calendar`
**Tüüp:** Backend
**Staatus:** To Do

## Kontekst

Kalendrivaade kuvab valitud kuu päevad ruudustikus ning märgib täppidega need päevad, millel toimub vähemalt üks üritus. Kasutaja saab liikuda eelmisesse või järgmisse kuusse ning klõpsata ühel päeval, mille tulemusena laaditakse alumisse paneeli selle päeva sündmuste loend. Leht on kaitstud — sisselogimata kasutajad ei pääse siia (kontrollib globaalne interceptor). Iga sündmuse juures on nupp "Näita rohkem", mis suunab kasutaja sündmuse detailvaatesse (`EventDetailsView.vue`).

## Mocki vaade

![CalendarView mock](../../../mock%20pildid/CalendarView.vue.png)

## Endpointide ülevaade

| # | Meetod | Tee | Otstarve |
|---|--------|-----|----------|
| 1 | `GET` | `/api/calendar?month={month}&year={year}` | Tagasta kuu päevad koos sündmuste olemasolu märgisega |
| 2 | `GET` | `/api/calendar/day?date={YYYY-MM-DD}` | Tagasta valitud päeva sündmuste loend |

---

## 1. `GET /api/calendar`

**Kontroller:** `CalendarController.java`
**Auth:** Jah (sisselogitud kasutaja)

### Request — query parameetrid

| Väli | Tüüp | Kirjeldus |
|------|------|-----------|
| `month` | `Integer` | Kuu number (1–12) |
| `year` | `Integer` | Aasta (nt 2023) |

### Response Body — `CalendarResponseDto.java`

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `month` | `Integer` | Päringu parameeter |
| `year` | `Integer` | Päringu parameeter |
| `daysWithEvents` | `List<Integer>` | Tuletatud `events.event_date` põhjal (päevanumbrid, kus on vähemalt üks sündmus) |

**Näidis:**
```json
{
  "month": 11,
  "year": 2023,
  "daysWithEvents": [4, 9, 22, 21]
}
```

> Frontend kasutab `daysWithEvents` loendit, et joonistada täpid vastavate päevade alla kalendris.

## 2. `GET /api/calendar/day`

**Kontroller:** `CalendarController.java`
**Auth:** Jah (sisselogitud kasutaja)

### Request — query parameetrid

| Väli | Tüüp | Kirjeldus |
|------|------|-----------|
| `date` | `LocalDate` (ISO `YYYY-MM-DD`) | Päev, mille sündmused tagastatakse |

### Response Body — `List<EventResponseDto>` (`EventResponseDto.java`)

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `eventId` | `Long` | `events.id` |
| `title` | `String` | `events.title` |
| `startTime` | `String` (HH:mm) | `events.start_time` |
| `endTime` | `String` (HH:mm) | `events.end_time` |
| `city` | `String` | `cities.name` (JOIN `events.city_id`) |
| `description` | `String` | `events.description` (lühikirjeldus, vajadusel kärbitud) |

**Näidis:**
```json
[
  {
    "eventId": 1,
    "title": "Sündmus 1",
    "startTime": "09:00",
    "endTime": "11:00",
    "city": "Tallinn",
    "description": "Lühikirjeldus..."
  }
]
```

> Eeldus: kärbitud kirjelduse pikkus on ca 200 tähemärki; frontend võib täiendavalt kärpida. Tühi tulemus tagastatakse `[]` kujul (200 OK).

## Veahaldus

| Olukord | Exception klass | ErrorResponse enum | HTTP staatus | Sõnum kasutajale |
|---------|-----------------|--------------------|--------------|------------------|
| Kasutaja ei ole sisse logitud | `UnauthorizedException` | `NOT_AUTHENTICATED` | 401 | "Palun logige sisse" |
| Vale `month` või `year` (nt `month=13`) | `BadRequestException` | `INVALID_CALENDAR_PARAMS` | 400 | "Vigased kalendri parameetrid" |
| Vale `date` formaat | `BadRequestException` | `INVALID_DATE_FORMAT` | 400 | "Vigane kuupäeva formaat (oodatud YYYY-MM-DD)" |

> **Märkus veahalduse kohta:**
> Kontrolli, kas vajalikud `ErrorResponse` enum kirjed ja exception klassid juba eksisteerivad:
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/error/ErrorResponse.java`
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/exception/`
>
> Puuduvate enum kirjete puhul lisa need `ErrorResponse`-i. Puuduvate exception klasside puhul loo uus klass `exception/` paketti ja registreeri see `RestExceptionHandler`-is.

## Andmebaas

Seotud tabelid: `events`, `cities`

- `events.event_date` — selle järgi grupeeritakse sündmused kuu lõikes (`daysWithEvents`)
- `events.start_time`, `events.end_time` — kuvatakse päeva sündmuste loendis
- `events.city_id` → `cities.name` — kuvatakse linna nimi sündmuse juures
- `events.is_cancelled` — tühistatud sündmusi võib päringust välja jätta (eeldus: jätame välja)

> Eeldus: kalender kuvab kõikide kasutajate sündmusi, mitte ainult sisselogitud kasutaja omi (nupp "Minu loodud sündmused" on eraldi link, mis suunab `/my-organized-events` vaatesse).

## Navigatsioon (frontend kontekst)

| Tegevus | Sihtkoht |
|---------|----------|
| Nupp "Eelmine kuu" | `GET /api/calendar?month=<month-1>&year=<year>` (kui `month < 1`, vähendab `year`) |
| Nupp "Järgmine kuu" | `GET /api/calendar?month=<month+1>&year=<year>` (kui `month > 12`, suurendab `year`) |
| Päevanumbri klõps | `GET /api/calendar/day?date=YYYY-MM-DD` (täidab alumise paneeli) |
| Nupp "Näita rohkem" (sündmuse juures) | `/events/:id` (`EventDetailsView.vue`) |
| Nupp "Minu loodud sündmused" | `/my-organized-events` (`OrganizedEventsView.vue`) |
| Header "Homepage \| Profile \| Events \| My Events \| Calendar \| Logout \| Cart" | Vastavad route'id |

## Vastuvõtu kriteeriumid

- [ ] `GET /api/calendar` tagastab 200 OK koos `CalendarResponseDto` kehaga
- [ ] `GET /api/calendar/day` tagastab 200 OK koos `List<EventResponseDto>` kehaga (tühi tulemus = `[]`)
- [ ] Sisselogimata kasutaja saab 401 koos `NOT_AUTHENTICATED` veaga
- [ ] Vigased query parameetrid (`month < 1`, `month > 12`, vale kuupäevaformaat) tagastavad 400 vastava veakoodiga
- [ ] Tühistatud sündmusi ei tagastata
- [ ] DTO klassid (`CalendarResponseDto`, `EventResponseDto`) on loodud `controller/calendar/dto/` paketti
- [ ] Controller, Service, Repository kihid on eraldatud
- [ ] Kontrolleri meetoditel on `@Operation` ja `@ApiResponses` annotatsioonid (sh veavastused `ApiError` skeemiga)
- [ ] Swagger UI kaudu on endpointid nähtavad ja testitavad
