# RegisterView — Uue kasutaja registreerimine

**Vaade:** `RegisterView.vue`
**Route:** `/register`
**Tüüp:** Backend
**Staatus:** To Do

## Kontekst

Registreerimise vaade võimaldab uuel kasutajal luua konto, et hakata sündmusi avastama ja neil osalema. Vorm küsib täisnime, e-posti, parooli (kahekordse kinnitusega), telefoni (vabatahtlik) ja kirjelduse (vabatahtlik). Eduka registreerimise järel suunatakse kasutaja sisselogimise vaatesse (`/login`). Uued kasutajad luuakse alati staatusega `ACTIVE` ja rolliga `USER`.

## Mocki vaade

![RegisterView mock](../../../mock%20pildid/RegisterView.vue.png)

## Endpointide ülevaade

| # | Meetod | Tee | Otstarve |
|---|--------|-----|----------|
| 1 | `POST` | `/api/register` | Registreeri uus kasutaja |

---

## 1. `POST /api/register`

**Kontroller:** `RegisterController.java`
**Auth:** Ei (avalik endpoint)

### Request Body — `RegisterDto.java`

| Väli | Tüüp | Kirjeldus |
|------|------|-----------|
| `fullName` | `String` | Kasutaja täisnimi (kohustuslik) |
| `email` | `String` | Kasutaja e-post (kohustuslik, unikaalne) |
| `password` | `String` | Kasutaja parool (kohustuslik, vähemalt 8 tähemärki) |
| `phone` | `String` | Kasutaja telefoninumber (vabatahtlik) |
| `description` | `String` | Lühike kirjeldus kasutaja kohta (vabatahtlik) |

**Näidis:**
```json
{
  "fullName": "Mari Maasikas",
  "email": "mari.maasikas@ettevote.ee",
  "password": "********",
  "phone": "+372 555 1234",
  "description": "Räägi endast paar sõna..."
}
```

> **Eeldus:** Frontend valideerib enne saatmist, et kõik (*) tähistatud väljad on täidetud, et parool on vähemalt 8 tähemärki ja et `password` ja `repeatPassword` ühtivad. `repeatPassword` ei saadeta backendi.

### Response Body — `RegisterResponseDto.java`

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `userId` | `Long` | `users.id` |
| `fullName` | `String` | `contacts.full_name` |
| `email` | `String` | `contacts.email` |
| `role` | `String` | `roles.name` (alati `USER`) |
| `status` | `String` | `users.status` (alati `ACTIVE`) |

**Näidis:**
```json
{
  "userId": 12,
  "fullName": "Mari Maasikas",
  "email": "mari.maasikas@ettevote.ee",
  "role": "USER",
  "status": "ACTIVE"
}
```

> Frontend suunab kasutaja peale edukat registreerimist `/login` lehele.

## Veahaldus

| Olukord | Exception klass | ErrorResponse enum | HTTP staatus | Sõnum kasutajale |
|---------|-----------------|--------------------|--------------|------------------|
| Kohustuslik väli puudub | `BadRequestException` | `MISSING_REQUIRED_FIELD` | 400 | "Palun täitke kõik kohustuslikud väljad" |
| Parool on liiga lühike | `BadRequestException` | `PASSWORD_TOO_SHORT` | 400 | "Parool peab olema vähemalt 8 tähemärki" |
| Paroolide kordus ei ühti | `BadRequestException` | `PASSWORDS_DO_NOT_MATCH` | 400 | "Paroolid ei kattu" |
| E-post on juba kasutusel | `ConflictException` | `EMAIL_ALREADY_EXISTS` | 409 | "See e-post on juba kasutusel" |
| E-posti formaat on vale | `BadRequestException` | `INVALID_EMAIL_FORMAT` | 400 | "E-posti formaat on vale" |

> **Märkus veahalduse kohta:**
> Kontrolli, kas vajalikud `ErrorResponse` enum kirjed ja exception klassid juba eksisteerivad:
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/error/ErrorResponse.java`
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/exception/`
>
> Puuduvate enum kirjete puhul lisa need `ErrorResponse`-i. Puuduvate exception klasside puhul loo uus klass `exception/` paketti ja registreeri see `RestExceptionHandler`-is.

## Andmebaas

Seotud tabelid: `users`, `contacts`, `roles`

- `users.password` — salvestatakse BCrypt hash kujul, mitte avatekstina
- `users.role_id` — määratakse vaikimisi rolli `USER` viidaga (`roles.name = 'USER'`)
- `users.status` — määratakse väärtusele `ACTIVE` registreerimisel
- `contacts.user_id` → `users.id` — uus kontakt luuakse samaaegselt kasutajaga
- `contacts.full_name` — kasutaja täisnimi
- `contacts.email` — kasutaja e-post (`UNIQUE`, kontrollitakse enne sisestamist)
- `contacts.phone` — kasutaja telefoninumber (vabatahtlik)

> NB! Andmebaasi skeemis on `users` tabelis ainult `password`. E-post ja muud kontaktandmed asuvad `contacts` tabelis. Mõlemad kirjed tuleb luua ühe transaktsiooni raames.
>
> **Eeldus:** Väli `description` ei ole praegu andmebaasi skeemis kirjas — sellele tuleb kas lisada uus veerg `contacts` või `users` tabelisse, või jätta hetkel salvestamata (ainult vastu võtta). Soovitatav: lisada `contacts.description varchar(500) NULL` ja taotleda DB migratsiooni.

## Navigatsioon (frontend kontekst)

| Tegevus | Sihtkoht |
|---------|----------|
| Eduka registreerimise järel | `redirect → /login` |
| Nupp "Registreeru" | `POST /api/register` |
| Nupp "Home" | `/` (`LandingPage.vue`) |

## Vastuvõtu kriteeriumid

- [ ] `POST /api/register` tagastab 201 Created koos `RegisterResponseDto` kehaga
- [ ] Puuduvate kohustuslike väljade puhul tagastab 400 koos `MISSING_REQUIRED_FIELD` veaga
- [ ] Liiga lühikese parooli puhul tagastab 400 koos `PASSWORD_TOO_SHORT` veaga
- [ ] Juba registreeritud e-posti puhul tagastab 409 koos `EMAIL_ALREADY_EXISTS` veaga
- [ ] Parool salvestatakse alati BCrypt hash kujul, mitte avatekstina
- [ ] Parool ei tagastata kunagi vastuses
- [ ] Uus kasutaja luuakse alati staatusega `ACTIVE` ja rolliga `USER`
- [ ] `users` ja `contacts` kirjed luuakse ühe transaktsiooni raames
- [ ] DTO klassid (`RegisterDto`, `RegisterResponseDto`) on loodud `controller/register/dto/` paketti
- [ ] Controller, Service, Repository kihid on eraldatud
- [ ] Kontrolleri meetodil on `@Operation` ja `@ApiResponses` annotatsioonid (sh veavastused `ApiError` skeemiga)
- [ ] Swagger UI kaudu on endpoint nähtav ja testitav
