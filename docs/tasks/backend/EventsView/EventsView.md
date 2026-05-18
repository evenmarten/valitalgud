# EventsView — Sündmuste loend

**Vaade:** `EventsView.vue`
**Route:** `/events`
**Tüüp:** Backend
**Staatus:** To Do

## Kontekst

Sündmuste loendi vaade kuvab kõik tulevased aktiivsed (`is_cancelled = false`) sündmused kaartide kujul. Sündmusi saab filtreerida linna, oskuste-tagi ja kuupäeva järgi (kõik filtreerimisparameetrid on vabatahtlikud). Iga sündmuse kaardil on pealkiri, kuupäev, linn, lühikirjeldus ja nupp "View Details", mis suunab kasutaja `/events/:id` lehele. Lehel on ka nupp "Loo uus sündmus", mis on nähtav ainult sisselogitud kasutajale. Vaade on kaitstud — väljalogitud kasutaja suunatakse `/unauthorized` lehele globalse interceptori abil.

## Mocki vaade

![EventsView mock](../../../mock%20pildid/EventsView.vue.png)

## Endpointide ülevaade

| # | Meetod | Tee | Otstarve |
|---|--------|-----|----------|
| 1 | `GET` | `/api/events` | Hangi sündmuste loend (toetab filtreid) |

---

## 1. `GET /api/events`

**Kontroller:** `EventController.java`
**Auth:** Jah (nõuab kehtivat sessiooni / kasutaja andmeid)

### Päringu parameetrid (query parameters)

| Väli | Tüüp | Kirjeldus |
|------|------|-----------|
| `cityId` | `Long` | Filter linna järgi (vabatahtlik) |
| `skillTagId` | `Long` | Filter oskuste-tagi järgi (vabatahtlik) |
| `fromDate` | `LocalDate` (ISO `yyyy-MM-dd`) | Filter alates kuupäevast (vabatahtlik) |

**Näidis:**
```
GET /api/events?cityId=1&skillTagId=2&fromDate=2024-01-01
```

### Response Body — `List<EventResponseDto>`

`EventResponseDto.java`:

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

**Näidis:**
```json
[
  {
    "eventId": 1,
    "title": "Suur Tehnoloogiakonverents",
    "description": "Aastane konverents, mis toob kokku tehnoloogiamaailma tipud.",
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
    "organizerName": "Tech Events OÜ"
  }
]
```

> Frontend renderdab iga vastuse kirje sündmuse kaardiks. Filtri muutmisel tehakse uus `GET` päring uuendatud parameetritega.

## Veahaldus

| Olukord | Exception klass | ErrorResponse enum | HTTP staatus | Sõnum kasutajale |
|---------|-----------------|--------------------|--------------|------------------|
| Kasutaja ei ole sisse logitud | `UnauthorizedException` | `UNAUTHORIZED` | 401 | "Palun logi sisse" |
| Filtri parameeter on vales formaadis (nt vale kuupäev) | `BadRequestException` | `INVALID_QUERY_PARAMETER` | 400 | "Filtri parameeter on vales formaadis" |
| Päring õnnestub, kuid sobivaid sündmusi ei leitud | — | — | 200 | Tagastatakse tühi list `[]` |

> **Märkus veahalduse kohta:**
> Kontrolli, kas vajalikud `ErrorResponse` enum kirjed ja exception klassid juba eksisteerivad:
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/error/ErrorResponse.java`
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/exception/`
>
> Puuduvate enum kirjete puhul lisa need `ErrorResponse`-i. Puuduvate exception klasside puhul loo uus klass `exception/` paketti ja registreeri see `RestExceptionHandler`-is.

## Andmebaas

Seotud tabelid: `events`, `cities`, `skill_tags`, `event_skill_tags`, `registrations`, `users`, `contacts`

- `events` — põhitabel, millest tagastatakse aktiivsed (`is_cancelled = false`) sündmused
- `events.city_id` → `cities.id` — linna nime lisamiseks vastusesse
- `event_skill_tags` (vahetabel) → `skill_tags` — sündmuse oskuste-tagide saamiseks
- `registrations` — kasutatakse `currentParticipants` arvutamiseks (`COUNT WHERE status = 'LAHEB'`)
- `events.organizer_id` → `users.id` → `contacts.full_name` — organisaatori nime saamiseks

> **Eeldus:** Filtri muutmisel tehakse alati uus `GET` päring (filtri väärtused saadetakse query parameetrina). Vaikimisi (ilma filtriteta) tagastatakse kõik aktiivsed sündmused, sorteeritud `event_date` järgi kasvavas järjekorras.

## Navigatsioon (frontend kontekst)

| Tegevus | Sihtkoht |
|---------|----------|
| Nupp "View Details" sündmuse kaardil | `/events/:id` (`EventDetailsView.vue`) |
| Nupp "Loo uus sündmus" (ainult sisselogitud kasutajatele) | `/events/create` (`CreateEventView.vue`) |
| Header: "Homepage" | `/` (`LandingPage.vue`) |
| Header: "Profile" | `/profile` (`ProfileView.vue`) |
| Header: "Events" | `/events` (käesolev) |
| Header: "My Events" | `/my-events` (`MyEventsView.vue`) |
| Header: "Calendar" | `/calendar` (`CalendarView.vue`) |
| Header: "Logout" | `localStorage.clear()` + `redirect → /unauthorized` |
| Header: "Cart" | `/cart` (`CartView.vue`) |

## Vastuvõtu kriteeriumid

- [ ] `GET /api/events` tagastab 200 OK koos `List<EventResponseDto>` kehaga
- [ ] Filtri parameetrid (`cityId`, `skillTagId`, `fromDate`) on vabatahtlikud ja toetatud
- [ ] Tagastatakse ainult aktiivsed sündmused (`is_cancelled = false`)
- [ ] Tagastatakse ainult need sündmused, mille `event_date >= fromDate` (kui filter on määratud)
- [ ] Vastuses kuvatakse iga sündmuse `currentParticipants` (registreerunute arv staatusega `LAHEB`)
- [ ] Väljalogitud kasutaja päringu puhul tagastab 401 koos `UNAUTHORIZED` veaga
- [ ] Sündmused on sorteeritud `event_date` järgi kasvavas järjekorras
- [ ] DTO klass `EventResponseDto` on loodud `controller/event/dto/` paketti
- [ ] Controller, Service, Repository kihid on eraldatud
- [ ] Kontrolleri meetodil on `@Operation` ja `@ApiResponses` annotatsioonid (sh veavastused `ApiError` skeemiga)
- [ ] Swagger UI kaudu on endpoint nähtav ja testitav
