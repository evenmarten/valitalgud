# ProfileView — Kasutaja profiil

**Vaade:** `ProfileView.vue`
**Route:** `/profile`
**Tüüp:** Backend
**Staatus:** To Do

## Kontekst

Profiilivaade näitab sisseloginud kasutaja andmeid (nimi, e-post, telefon, roll, kirjeldus) ning pakub kolme tegevust: `Edit Profile` (avab profiili muutmise vormi), `Change Password` (avab parooli vahetamise modaali) ja `Delete Account` (kustutab kasutaja konto kinnitusmodaali järel). Kõik tegevused vajavad sisselogimist ning toimivad ainult kasutaja enda andmete peal.

Eeldus: kuna `users` ja `contacts` tabelites ei ole eraldi välju `firstName`, `middleName`, `lastName` ega `description`, käsitleme `contacts.full_name` täisnimena ühe väljana. Vormi väljad `First Name`, `Middle Name`, `Last Name` koondatakse backendis üheks `full_name` stringiks (nt `"Mari Anna Maasikas"`). `Description` välja jaoks ei ole praegu andmebaasis veergu — see välja salvestamine eeldab täiendavat skeemi muudatust või jäetakse esialgu ignoreerimisele (frontend kuvab tühja tekstina).

## Mocki vaade

![ProfileView mock](../../../mock%20pildid/ProfileView.vue.png)

## Endpointide ülevaade

| # | Meetod | Tee | Otstarve |
|---|--------|-----|----------|
| 1 | `GET` | `/api/profile/{userId}` | Tagasta kasutaja profiili andmed |
| 2 | `PUT` | `/api/profile/{userId}` | Uuenda kasutaja profiili andmeid |
| 3 | `PUT` | `/api/profile/{userId}/password` | Vaheta kasutaja parool |
| 4 | `DELETE` | `/api/profile/{userId}` | Kustuta kasutaja konto |

---

## 1. `GET /api/profile/{userId}`

**Kontroller:** `ProfileController.java`
**Auth:** Jah (sisseloginud kasutaja saab vaadata ainult enda profiili, v.a `ADMIN`)

### Response Body — `ProfileResponseDto.java`

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `userId` | `Integer` | `users.id` |
| `fullName` | `String` | `contacts.full_name` |
| `email` | `String` | `contacts.email` |
| `phone` | `String` | `contacts.phone` (võib olla `null`) |
| `role` | `String` | `roles.name` (`USER` / `ADMIN`) |
| `description` | `String` | Eeldus: praegu tagastatakse `null` või tühi string (DB veergu pole) |

**Näidis:**
```json
{
  "userId": 1,
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "phone": "555-12-4567",
  "role": "USER",
  "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
}
```

---

## 2. `PUT /api/profile/{userId}`

**Kontroller:** `ProfileController.java`
**Auth:** Jah (kasutaja saab muuta ainult enda profiili)

### Request Body — `UpdateProfileDto.java`

| Väli | Tüüp | Kohustuslik | Kirjeldus |
|------|------|-------------|-----------|
| `firstName` | `String` | Jah | Eesnimi |
| `middleName` | `String` | Ei | Keskmine nimi (võib olla `null`) |
| `lastName` | `String` | Jah | Perekonnanimi |
| `email` | `String` | Jah | E-posti aadress (peab olema unikaalne `contacts.email` osas) |
| `phone` | `String` | Ei | Telefon |
| `description` | `String` | Ei | Vaba kirjeldus (eeldus: praegu ignoreeritakse, kuni DB veerg lisatakse) |

**Näidis:**
```json
{
  "firstName": "John",
  "middleName": null,
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "555-12-4567",
  "description": "Lorem ipsum dolor sit amet."
}
```

### Response Body — `ProfileResponseDto.java`

Sama struktuur nagu `GET` vastuses (vt eespool).

> Backend liidab `firstName`, `middleName`, `lastName` üheks `full_name` stringiks ja salvestab `contacts.full_name` veergu.

---

## 3. `PUT /api/profile/{userId}/password`

**Kontroller:** `ProfileController.java`
**Auth:** Jah (kasutaja saab muuta ainult enda parooli)

### Request Body — `ChangePasswordDto.java`

| Väli | Tüüp | Kohustuslik | Kirjeldus |
|------|------|-------------|-----------|
| `oldPassword` | `String` | Jah | Praegune parool (kontrollitakse) |
| `newPassword` | `String` | Jah | Uus parool (min 8 tähemärki) |
| `confirmNewPassword` | `String` | Jah | Uue parooli kinnitus, peab vastama `newPassword`-ile |

**Näidis:**
```json
{
  "oldPassword": "vanaParool123",
  "newPassword": "uusParool456",
  "confirmNewPassword": "uusParool456"
}
```

### Response Body

`200 OK` — keha pole (või `{ "message": "Parool muudetud" }`).

---

## 4. `DELETE /api/profile/{userId}`

**Kontroller:** `ProfileController.java`
**Auth:** Jah (kasutaja saab kustutada ainult enda konto, v.a `ADMIN`)

### Response Body

`204 No Content` — keha pole.

> Eeldus: kasutame pehmet kustutust, määrates `users.status = 'DELETED'`. Sellega säilib viiteterviklus (`events.organizer_id`, `comments.user_id`, `orders.user_id` jne) ja kasutaja ei saa enam sisse logida (vt `LoginView` veahaldus `ACCOUNT_BLOCKED`). Alternatiivina võib kasutada `DELETE FROM users` (`ON DELETE CASCADE` puhastab `contacts`, `carts`, `events`, `registrations`, `comments`), kuid see hävitab ka kasutaja korraldatud sündmused.

## Veahaldus

| Olukord | Exception klass | ErrorResponse enum | HTTP staatus | Sõnum kasutajale |
|---------|-----------------|--------------------|--------------|------------------|
| Kasutaja pole sisselogitud | `UnauthorizedException` | `NOT_AUTHENTICATED` | 401 | "Palun logige sisse" |
| Kasutaja proovib vaadata/muuta võõrast profiili | `ForbiddenException` | `NOT_PROFILE_OWNER` | 403 | "Teil pole õigust seda profiili muuta" |
| Kasutajat ei leitud | `NotFoundException` | `USER_NOT_FOUND` | 404 | "Kasutajat ei leitud" |
| Kohustuslik väli puudub | `BadRequestException` | `MISSING_FIELDS` | 400 | "Palun täitke kõik kohustuslikud väljad" |
| E-post on juba kasutusel | `ConflictException` | `EMAIL_ALREADY_EXISTS` | 409 | "See e-posti aadress on juba kasutusel" |
| Vigane e-posti formaat | `BadRequestException` | `INVALID_EMAIL_FORMAT` | 400 | "Vigane e-posti formaat" |
| Vana parool on vale | `BadRequestException` | `WRONG_OLD_PASSWORD` | 400 | "Praegune parool on vale" |
| Uus parool ja kinnitus ei ühti | `BadRequestException` | `PASSWORDS_DO_NOT_MATCH` | 400 | "Uued paroolid ei ühti" |
| Uus parool liiga lühike | `BadRequestException` | `PASSWORD_TOO_SHORT` | 400 | "Parool peab olema vähemalt 8 tähemärki" |

> **Märkus veahalduse kohta:**
> Kontrolli, kas vajalikud `ErrorResponse` enum kirjed ja exception klassid juba eksisteerivad:
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/error/ErrorResponse.java`
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/exception/`
>
> Puuduvate enum kirjete puhul lisa need `ErrorResponse`-i. Puuduvate exception klasside puhul loo uus klass `exception/` paketti ja registreeri see `RestExceptionHandler`-is.

## Andmebaas

Seotud tabelid: `users`, `contacts`, `roles`

- `users.id` — kasutaja identifikaator (URL-ist `{userId}`)
- `users.password` — BCrypt hash, vahetatakse `PUT .../password` endpoindi kaudu
- `users.status` — `DELETE` endpoint seab väärtuseks `DELETED` (pehme kustutus)
- `users.role_id` → `roles.name` — vastuses kuvatav roll
- `contacts.user_id` → `users.id` (UNIQUE) — iga kasutaja kohta üks kontakt
- `contacts.full_name` — frontend lõhub kuvamisel `firstName` / `middleName` / `lastName` osadeks
- `contacts.email` — UNIQUE, muutmisel kontrolli ainulaadsust
- `contacts.phone` — võib olla `null`

> NB! `description` välja jaoks puudub andmebaasis veerg. Esialgne implementatsioon võib selle vastuses tagastada `null`-ina ja muudatusel ignoreerida, või lisada `contacts.description` veerg eraldi skeemi muudatusena.

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
| Nupp `Edit Profile` | avab `Edit Profile` vormi (modaal või paneel) |
| Nupp `Change Password` | avab `Change Password` modaali |
| Nupp `Delete My Account` | avab kinnitusmodaali — kinnituse järel kutsub `DELETE /api/profile/{userId}` |
| Modaali nupp `Save Changes` (Edit Profile) | kutsub `PUT /api/profile/{userId}` |
| Modaali nupp `Save Changes` (Change Password) | kutsub `PUT /api/profile/{userId}/password` |
| Modaali nupp `Cancel` | sulgeb modaali ilma muudatusteta |

## Vastuvõtu kriteeriumid

- [ ] `GET /api/profile/{userId}` tagastab 200 OK koos `ProfileResponseDto` kehaga
- [ ] `PUT /api/profile/{userId}` uuendab `contacts.full_name`, `contacts.email`, `contacts.phone` ja tagastab uuendatud profiili
- [ ] `PUT /api/profile/{userId}/password` kontrollib vana parooli, võrdleb uut paroolide kinnitusega ja salvestab BCrypt hash'i
- [ ] `DELETE /api/profile/{userId}` seab `users.status = 'DELETED'` (pehme kustutus) ja tagastab 204
- [ ] Võõra profiili muutmise katse tagastab 403 `NOT_PROFILE_OWNER`
- [ ] Olemasoleva e-posti uuesti kasutamise katse tagastab 409 `EMAIL_ALREADY_EXISTS`
- [ ] Vale vana parooli puhul tagastab 400 `WRONG_OLD_PASSWORD`
- [ ] Paroolide mittevastavus tagastab 400 `PASSWORDS_DO_NOT_MATCH`
- [ ] Parool ei tagastata kunagi vastuses
- [ ] DTO klassid (`ProfileResponseDto`, `UpdateProfileDto`, `ChangePasswordDto`) on loodud `controller/profile/dto/` paketti
- [ ] Controller, Service, Repository kihid on eraldatud
- [ ] Kontrolleri meetoditel on `@Operation` ja `@ApiResponses` annotatsioonid (sh veavastused `ApiError` skeemiga)
- [ ] Swagger UI kaudu on endpointid nähtavad ja testitavad
