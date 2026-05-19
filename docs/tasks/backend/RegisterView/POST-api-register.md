# POST /api/register

**Kontroller:** `RegisterController.java`
**Tüüp:** Backend
**Staatus:** To Do

## Kontekst

Registreerimise vaade (`RegisterView.vue`, route `/register`) võimaldab uuel kasutajal luua konto, et hakata sündmusi avastama ja neil osalema. Vorm kogub täisnime, e-posti, parooli ja valikuliselt telefoni ning kirjelduse. Eduka registreerimise järel suunatakse kasutaja `/login` lehele. See on avalik endpoint — autentimist ei nõuta. Seotud endpoint: `POST /api/login` (`LoginView`), mis tagastab sama `LoginResponseDto` kuju.

## Mocki vaade

![RegisterView mock](../../../mock%20pildid/RegisterView.vue.png)

## API leping

| Väli | Väärtus |
|------|---------|
| Meetod | `POST` |
| Tee | `/api/register` |
| Auth | Ei (avalik endpoint) |

### Request Body — `RegisterDto.java`

> Schema: [`RegisterDto_schema.json`](../../dtos/schema/RegisterDto_schema.json)
> Näidis: [`RegisterDto_RegisterView_example.json`](../../dtos/examples/RegisterDto_RegisterView_example.json)

| Väli | Tüüp | Kirjeldus |
|------|------|-----------|
| `fullName` | `String` | Kasutaja täisnimi (kohustuslik) |
| `email` | `String` | Kasutaja e-post (kohustuslik, peab olema unikaalne) |
| `password` | `String` | Kasutaja parool (kohustuslik, vähemalt 8 tähemärki) |
| `phone` | `String` | Telefoninumber (vabatahtlik, võib olla `null`) |
| `description` | `String` | Lühike kirjeldus kasutaja kohta (vabatahtlik, võib olla `null`) |

> **Märkus:** `repeatPassword` välja backend ei saa — selle valideerib frontend enne saatmist. Samuti kontrollib frontend kõik (`***`) tähistatud kohustuslikud väljad ja parooli minimaalse pikkuse (≥ 8 tähemärki).

### Response Body — `LoginResponseDto.java`

> ℹ️ **Mock soovitab** `UserResponseDto.java` **või** `LoginResponseDto.java`. Kuna `LoginResponseDto` on juba `LoginView` jaoks defineeritud ja sisaldab samad kasutaja identifitseerimise väljad, kasuta sama DTO-d — ära loo uut `RegisterResponseDto`-t.

> Schema: [`LoginResponseDto_schema.json`](../../dtos/schema/LoginResponseDto_schema.json)
> Näidis: [`LoginResponseDto_RegisterView_example.json`](../../dtos/examples/LoginResponseDto_RegisterView_example.json)

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `userId` | `Integer` | `users.id` |
| `firstName` | `String` | `contacts.full_name` (esimene tühiku-eelne osa) |
| `middleName` | `String` | `contacts.full_name` (keskmine osa — `null` kui on ainult ees- ja perekonnanimi) |
| `lastName` | `String` | `contacts.full_name` (viimane tühiku-järgne osa) |
| `role` | `String` | `roles.name` (alati `USER` uue konto loomisel) |

> Frontend suunab kasutaja peale edukat registreerimist `/login` lehele.

## Veahaldus

| Olukord | Exception klass | ErrorResponse enum | HTTP staatus |
|---------|----------------|-------------------|--------------|
| `fullName`, `email` või `password` on `null` või tühi | `BadRequestException` | `MISSING_FIELDS` ✅ olemas | 400 |
| E-post on juba `contacts.email` veerus olemas | `ConflictException` ⚠️ puudub | `EMAIL_ALREADY_EXISTS` ⚠️ puudub | 409 |

> **Mida tuleb lisada / muuta:**
>
> **1. `ErrorResponse.java` — lisa uus kirje:**
> ```java
> EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "See e-post on juba kasutusel", HttpStatus.CONFLICT),
> ```
>
> **2. Loo `ConflictException.java`** `infrastructure/exception/` paketti (järgi olemasolevate klasside mustrit):
> ```java
> @Getter
> public class ConflictException extends RuntimeException {
>
>     private final ErrorResponse errorResponse;
>
>     public ConflictException(ErrorResponse errorResponse) {
>         super(errorResponse.getMessage());
>         this.errorResponse = errorResponse;
>     }
> }
> ```
>
> **3. Registreeri `RestExceptionHandler.java`-s:**
> ```java
> @ExceptionHandler(ConflictException.class)
> public ResponseEntity<ApiError> handleConflict(ConflictException ex) {
>     return buildResponse(ex.getErrorResponse());
> }
> ```
>
> **Olemasolevad kirjed (ei vaja muutmist):**
> - `MISSING_FIELDS` — ✅ `"Palun täitke kõik kohustuslikud väljad"`, HTTP 400

## Andmebaas

Seotud tabelid: `users`, `contacts`, `roles`

Kirjutamine toimub kahe tabelisse **ühe `@Transactional` teenusemeetodi raames** järgmises järjekorras:

1. **`users` tabel** — loo uus kasutajakirje:
   - `password` → BCrypt hash (`users.password`) — mitte avatekst
   - `role_id` → jäta andmebaasi DEFAULT `1` (vastab `roles.name = 'USER'`) — ära edasta käsitsi
   - `status` → sea **eksplitsiitselt** `'ACTIVE'` (vt DB hoiatus allpool)
   - `created_at` → automaatne (`DEFAULT now()`)

2. **`contacts` tabel** — loo kontaktikirje vastloodud kasutajale:
   - `user_id` → äsja loodud `users.id`
   - `full_name` → `RegisterDto.fullName`
   - `email` → `RegisterDto.email` — kontrolli unikaalsust **enne** salvestamist (`UNIQUE` piirang)
   - `phone` → `RegisterDto.phone` (NULL lubatud)

> ⚠️ **DB hoiatus — `users.status` vaikeväärtuse viga:**
> `2_create.sql`-is on `status` DEFAULT `'active'` (väiketähtedega), kuid `CHECK` piirang lubab ainult
> `'ACTIVE'`, `'PENDING_ACTIVATION'` või `'DELETED'` (suurtähtedega). Vaikeväärtus ei läbi piirangut.
> **Sea `status = 'ACTIVE'` alati eksplitsiitselt teenusekihis** — ära toetu vaikeväärtusele.

> ⚠️ **DB hoiatus — `description` väli puudub skeemist:**
> `contacts` tabelis ei ole `description` veergu. `RegisterDto.description` on UI vormis ja DTO-s olemas,
> kuid hetkel pole DB-s kohta selle salvestamiseks. Kaks lahendust:
> - **Soovituslik:** Lisa `3_import.sql` või uude migratsiooniskripti:
>   `ALTER TABLE contacts ADD COLUMN description varchar(500) NULL;`
> - **Ajutine:** Võta väli DTO-s vastu, kuid ära salvesta — lisa koodikommentaar, et ootab migrat​siooni.

## Pakettide struktuur

```
controller/
  register/
    RegisterController.java
    dto/
      RegisterDto.java                  ← Request body (uus)
      (LoginResponseDto asub controller/login/dto/ — kasuta sealt, ära kopeeri)
service/
  RegisterService.java
persistence/
  user/
    User.java                           ← JPA entiteet (users tabel)
    UserRepository.java
  contact/
    Contact.java                        ← JPA entiteet (contacts tabel)
    ContactRepository.java
    ContactMapper.java
infrastructure/
  exception/
    ConflictException.java              ← Uus klass (tuleb luua)
  error/
    ErrorResponse.java                  ← Lisa EMAIL_ALREADY_EXISTS kirje
    RestExceptionHandler.java           ← Registreeri ConflictException handler
```

## Vastuvõtu kriteeriumid

- [ ] `POST /api/register` tagastab `201 Created` koos `LoginResponseDto` kehaga
- [ ] Puuduvate kohustuslike väljade (`fullName`, `email`, `password`) puhul tagastab `400` koos `MISSING_FIELDS` veaga
- [ ] Juba kasutusel oleva e-posti puhul tagastab `409` koos `EMAIL_ALREADY_EXISTS` veaga
- [ ] Parool salvestatakse BCrypt hash kujul (`users.password`) — mitte avatekstina
- [ ] Parool ei ilmu kunagi vastuses
- [ ] `users.status` on seatud eksplitsiitselt `'ACTIVE'` (mitte DB vaikeväärtusele toetudes)
- [ ] `users.role_id` vaikeväärtus `1` (USER) — rolli ei edastata käsitsi
- [ ] `users` ja `contacts` kirjed luuakse ühes `@Transactional` meetodis
- [ ] `ConflictException` klass on loodud `exception/` paketti ja registreeritud `RestExceptionHandler`-is
- [ ] `EMAIL_ALREADY_EXISTS` kirje on lisatud `ErrorResponse` enumisse
- [ ] `RegisterDto` on loodud `controller/register/dto/` paketti
- [ ] `LoginResponseDto` (olemasolev `controller/login/dto/` alt) on kasutusele võetud — uut `RegisterResponseDto`-t ei looda
- [ ] Controller, Service, Repository kihid on eraldatud
- [ ] Kontrolleri meetodil on `@Operation` ja `@ApiResponses` annotatsioonid (sh veavastused `ApiError` skeemiga)
- [ ] Swagger UI kaudu on endpoint nähtav ja testitav
