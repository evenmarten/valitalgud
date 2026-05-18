# LoginView — Sisselogimine

**Vaade:** `LoginView.vue`
**Route:** `/login`
**Tüüp:** Backend
**Staatus:** To Do

## Kontekst

Sisselogimise vaade kasutajale, kes juba on registreeritud. Vorm võtab e-posti ja parooli. Eduka sisselogimise järel salvestatakse kasutaja andmed `localStorage`-isse ja kasutaja suunatakse ürituste lehele (`/events`). Lehelt saab navigeerida ka registreerimise vaatesse (`/register`) ja avalehele (`/`).

## Mocki vaade

![LoginView mock](../../../mock%20pildid/LoginView.vue.png)

## Endpointide ülevaade

| # | Meetod | Tee | Otstarve |
|---|--------|-----|----------|
| 1 | `POST` | `/api/login` | Logi kasutaja sisse |

---

## 1. `POST /api/login`

**Kontroller:** `LoginController.java`
**Auth:** Ei (avalik endpoint)

### Request Body — `LoginDto.java`

| Väli | Tüüp | Kirjeldus |
|------|------|-----------|
| `email` | `String` | Kasutaja e-post |
| `password` | `String` | Kasutaja parool (avatekstina, võrreldakse hash'iga) |

**Näidis:**
```json
{
  "email": "mari.maasikas@ettevote.ee",
  "password": "********"
}
```

### Response Body — `LoginResponseDto.java`

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `userId` | `Long` | `users.id` |
| `firstName` | `String` | `contacts.full_name` (esimene osa) |
| `middleName` | `String` | `contacts.full_name` (keskmine osa, võib olla `null`) |
| `lastName` | `String` | `contacts.full_name` (viimane osa) |
| `role` | `String` | `roles.name` (`USER` või `ADMIN`) |

**Näidis:**
```json
{
  "userId": 1,
  "firstName": "Mari",
  "middleName": null,
  "lastName": "Maasikas",
  "role": "USER"
}
```

> Frontend salvestab vastuse `localStorage`-isse ja suunab kasutaja `/events` lehele.

## Veahaldus

| Olukord | Exception klass | ErrorResponse enum | HTTP staatus | Sõnum kasutajale |
|---------|-----------------|--------------------|--------------|------------------|
| Email või parool puudub | `BadRequestException` | `MISSING_CREDENTIALS` | 400 | "Palun täitke kõik väljad" |
| Vale email või parool | `UnauthorizedException` | `INVALID_CREDENTIALS` | 401 | "Vale email või parool" |
| Konto on blokeeritud (`status = DELETED` või muu mitte-`ACTIVE`) | `ForbiddenException` | `ACCOUNT_BLOCKED` | 403 | "Teie konto on blokeeritud. Pöörduge administraatori poole." |

> **Märkus veahalduse kohta:**
> Kontrolli, kas vajalikud `ErrorResponse` enum kirjed ja exception klassid juba eksisteerivad:
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/error/ErrorResponse.java`
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/exception/`
>
> Puuduvate enum kirjete puhul lisa need `ErrorResponse`-i. Puuduvate exception klasside puhul loo uus klass `exception/` paketti ja registreeri see `RestExceptionHandler`-is.

## Andmebaas

Seotud tabelid: `users`, `contacts`, `roles`

- `users.password` — võrdle päringus saadud parooliga (eelistatult BCrypt hash)
- `users.status` — peab olema `ACTIVE`, muidu 403
- `users.role_id` → `roles.name` — määrab vastuses tagastatava rolli
- `contacts.user_id` → `users.id` — kontakt sisaldab nime (full_name), e-posti (peab vastama päringule)

> NB! Andmebaasi skeemis on `users` tabelis ainult `password`, mitte `email`. E-post asub `contacts.email` veerus (`UNIQUE`). Otsing tuleb teha `contacts` tabeli kaudu.

## Navigatsioon (frontend kontekst)

| Tegevus | Sihtkoht |
|---------|----------|
| Eduka sisselogimise järel | `redirect → /events` |
| Nupp "Loo kasutaja" | `/register` (`RegisterView.vue`) |
| Nupp "Home" | `/` (`LandingPage.vue`) |

`localStorage`-isse salvestatakse vastuse väljad: `userId`, `firstName`, `middleName`, `lastName`, `role`.

## Vastuvõtu kriteeriumid

- [ ] `POST /api/login` tagastab 200 OK koos `LoginResponseDto` kehaga
- [ ] Puuduvate väljade puhul tagastab 400 koos `MISSING_CREDENTIALS` veaga
- [ ] Vale e-posti või parooli puhul tagastab 401 koos `INVALID_CREDENTIALS` veaga
- [ ] Blokeeritud konto puhul tagastab 403 koos `ACCOUNT_BLOCKED` veaga
- [ ] Parool ei tagastata kunagi vastuses
- [ ] DTO klassid (`LoginDto`, `LoginResponseDto`) on loodud `controller/login/dto/` paketti
- [ ] Controller, Service, Repository kihid on eraldatud
- [ ] Kontrolleri meetodil on `@Operation` ja `@ApiResponses` annotatsioonid (sh veavastused `ApiError` skeemiga)
- [ ] Swagger UI kaudu on endpoint nähtav ja testitav
