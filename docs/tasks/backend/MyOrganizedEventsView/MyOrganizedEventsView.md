# MyOrganizedEventsView — Minu korraldatud sündmused

**Vaade:** `MyOrganizedEventsView.vue`
**Route:** `/my-organized-events`
**Tüüp:** Backend
**Staatus:** To Do

## Kontekst

Vaade näitab sisseloginud kasutaja poolt korraldatud sündmuste nimekirja tabelivormingus. Iga rida sisaldab sündmuse pealkirja, kuupäeva, asukohta, staatust (`Aktiivne` / `Lõppenud` / `Tühistatud`) ning tegevusnuppe (`Detail`, `Muuda`, `Kustuta sündmus`). Filtreerimine on võimalik linna, oskuse-tagi ja kuupäeva järgi. Backend tagastab AINULT need sündmused, mille `organizer_id` võrdub sisseloginud kasutaja `userId`-ga.

## Mocki vaade

![MyOrganizedEventsView mock](../../../mock%20pildid/MyOrganizedEventsView.vue.png)

## Endpointide ülevaade

| # | Meetod | Tee | Otstarve |
|---|--------|-----|----------|
| 1 | `GET` | `/api/my-organized-events` | Tagasta sisseloginud kasutaja korraldatud sündmuste nimekiri (toetab filtreid) |
| 2 | `DELETE` | `/api/events/{eventId}` | Kustuta või tühista korraldaja enda loodud sündmus |

---

## 1. `GET /api/my-organized-events`

**Kontroller:** `MyOrganizedEventsController.java`
**Auth:** Jah (sisseloginud kasutaja `userId` võetakse päringust või headerist)

### Päringuparameetrid (Query Params)

| Parameeter | Tüüp | Kohustuslik | Kirjeldus |
|------------|------|-------------|-----------|
| `userId` | `Integer` | Jah | Sisseloginud kasutaja ID (korraldaja) |
| `city` | `String` | Ei | Filtreeri linna nime järgi (`cities.name`) |
| `skillTag` | `String` | Ei | Filtreeri oskuse-tagi nime järgi (`skill_tags.name`) |
| `date` | `String` (ISO `YYYY-MM-DD`) | Ei | Filtreeri konkreetse kuupäeva järgi (`events.event_date`) |

### Response Body — `List<OrganizedEventResponseDto>`

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `eventId` | `Integer` | `events.id` |
| `title` | `String` | `events.title` |
| `date` | `String` (ISO `YYYY-MM-DD`) | `events.event_date` |
| `city` | `String` | `cities.name` (JOIN `events.city_id`) |
| `status` | `String` | Arvutatud: `Aktiivne` / `Lõppenud` / `Tühistatud` (vt loogika allpool) |
| `currentParticipants` | `Integer` | `COUNT(registrations)` kus `event_id = events.id` ja `status = 'LAHEB'` |
| `maxParticipants` | `Integer` | `events.max_participants` (võib olla `null`) |

**Staatuse arvutamise loogika:**
- `Tühistatud` — kui `events.is_cancelled = true`
- `Lõppenud` — kui `events.event_date < CURRENT_DATE`
- `Aktiivne` — kõikidel ülejäänud juhtudel (tulevikukuupäev või täna)

**Näidis:**
```json
[
  {
    "eventId": 1,
    "title": "Sündmus 1",
    "date": "2023-10-26",
    "city": "Tallinn",
    "status": "Aktiivne",
    "currentParticipants": 25,
    "maxParticipants": 100
  },
  {
    "eventId": 2,
    "title": "Sündmus 2",
    "date": "2023-11-15",
    "city": "Tartu",
    "status": "Aktiivne",
    "currentParticipants": 12,
    "maxParticipants": 50
  },
  {
    "eventId": 3,
    "title": "Sündmus 3",
    "date": "2023-09-01",
    "city": "Pärnu",
    "status": "Lõppenud",
    "currentParticipants": 40,
    "maxParticipants": 40
  }
]
```

> Frontend kuvab `currentParticipants/maxParticipants` formaadis (nt `25/100`). Kui `maxParticipants` on `null`, kuvab frontend ainult osalejate arvu.

---

## 2. `DELETE /api/events/{eventId}`

**Kontroller:** `MyOrganizedEventsController.java` (või jagatud `EventController.java`)
**Auth:** Jah — ainult sündmuse korraldaja (`organizer_id == userId`) tohib kustutada

### Päringuparameetrid

| Parameeter | Tüüp | Kohustuslik | Kirjeldus |
|------------|------|-------------|-----------|
| `eventId` (PathVariable) | `Integer` | Jah | Kustutatava sündmuse ID |
| `userId` (Query) | `Integer` | Jah | Sisseloginud kasutaja ID (õiguste kontrolliks) |

### Response Body

`204 No Content` — keha pole.

> Eeldus: kustutamine on füüsiline (`DELETE FROM events`). Kuna `events.is_cancelled` veerg on olemas, võib alternatiivse käitumisena lipu `true`-ks panna (pehme kustutus). Esialgu kasutame füüsilist kustutust ja kaskaadi (`registrations`, `comments`, `event_skill_tags` kustuvad `ON DELETE CASCADE` kaudu).

## Veahaldus

| Olukord | Exception klass | ErrorResponse enum | HTTP staatus | Sõnum kasutajale |
|---------|-----------------|--------------------|--------------|------------------|
| `userId` puudub päringust | `BadRequestException` | `MISSING_USER_ID` | 400 | "Kasutaja ID on kohustuslik" |
| Kasutaja pole sisselogitud (globaalne interceptor) | `UnauthorizedException` | `NOT_AUTHENTICATED` | 401 | "Palun logige sisse" |
| Kasutaja proovib kustutada võõrast sündmust | `ForbiddenException` | `NOT_EVENT_OWNER` | 403 | "Teil pole õigust seda sündmust kustutada" |
| Sündmust ei leitud | `NotFoundException` | `EVENT_NOT_FOUND` | 404 | "Sündmust ei leitud" |
| Vigane kuupäeva formaat filtris | `BadRequestException` | `INVALID_DATE_FORMAT` | 400 | "Vigane kuupäeva formaat. Kasuta YYYY-MM-DD" |

> **Märkus veahalduse kohta:**
> Kontrolli, kas vajalikud `ErrorResponse` enum kirjed ja exception klassid juba eksisteerivad:
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/error/ErrorResponse.java`
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/exception/`
>
> Puuduvate enum kirjete puhul lisa need `ErrorResponse`-i. Puuduvate exception klasside puhul loo uus klass `exception/` paketti ja registreeri see `RestExceptionHandler`-is.

## Andmebaas

Seotud tabelid: `events`, `cities`, `skill_tags`, `event_skill_tags`, `registrations`, `users`

- `events.organizer_id` → `users.id` — kasutaja korraldatud sündmuste filtreerimise alus
- `events.city_id` → `cities.id` — linna nime liitmine vastusesse ja linna filtri jaoks
- `events.event_date` — kuupäeva filtri ja staatuse arvutamise alus
- `events.is_cancelled` — `Tühistatud` staatuse alus
- `events.max_participants` — kuvatakse vastuses (võib olla `null`)
- `event_skill_tags` → `skill_tags` — oskuse-tagi filtreerimise jaoks (JOIN)
- `registrations.status = 'LAHEB'` — `currentParticipants` arvutuse alus (COUNT)

## Navigatsioon (frontend kontekst)

| Tegevus | Sihtkoht |
|---------|----------|
| Header — `Homepage` | `/` (`LandingPage.vue`) |
| Header — `Profile` | `/profile` (`ProfileView.vue`) |
| Header — `Events` | `/events` (`EventsView.vue`) |
| Header — `My Events` | `/my-events` (`MyEventsView.vue`) |
| Header — `Calendar` | `/calendar` (`CalendarView.vue`) |
| Header — `Logout` | tühjenda `localStorage`, suuna `/login` |
| Header — `Cart` | `/cart` (`CartView.vue`) |
| Nupp `Detail` | `/events/:id` (`EventDetailsView.vue`) |
| Nupp `Muuda` | `/events/:id/edit` (`EditEventView.vue`) |
| Nupp `Kustuta sündmus` | kutsub `DELETE /api/events/{eventId}`, refresh nimekiri |
| Nupp `Filtreeri` | kutsub `GET /api/my-organized-events` koos query parameetritega |

## Vastuvõtu kriteeriumid

- [ ] `GET /api/my-organized-events?userId=...` tagastab 200 OK koos `List<OrganizedEventResponseDto>` kehaga
- [ ] Tagastatakse AINULT sündmused, kus `events.organizer_id == userId`
- [ ] Filtrid `city`, `skillTag`, `date` toimivad eraldi ja kombinatsioonis
- [ ] Staatus arvutatakse korrektselt (`Aktiivne` / `Lõppenud` / `Tühistatud`)
- [ ] `currentParticipants` näitab AINULT `LAHEB` staatusega registreerumiste arvu
- [ ] `DELETE /api/events/{eventId}` tagastab 204 No Content edukal kustutusel
- [ ] Võõra sündmuse kustutamise katse tagastab 403 `NOT_EVENT_OWNER`
- [ ] Olematu sündmuse kustutus tagastab 404 `EVENT_NOT_FOUND`
- [ ] Puuduv `userId` tagastab 400 `MISSING_USER_ID`
- [ ] DTO klassid (`OrganizedEventResponseDto`) on loodud `controller/event/dto/` paketti
- [ ] Controller, Service, Repository kihid on eraldatud
- [ ] Kontrolleri meetoditel on `@Operation` ja `@ApiResponses` annotatsioonid (sh veavastused `ApiError` skeemiga)
- [ ] Swagger UI kaudu on endpointid nähtavad ja testitavad
