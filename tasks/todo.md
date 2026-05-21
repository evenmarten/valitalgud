# Valitalgud — Projekti ülesannete nimekiri

Täisstack ürituste halduse rakendus: Vue 3 + Spring Boot + PostgreSQL.

---

## FAAS 0 — Andmebaas ja projekt setup

- [ ] **0.1** Käivita SQL skriptid järjekorras: `1_reset_database.sql` → `2_create.sql` → `3_import.sql`
- [ ] **0.2** Kontrolli, et kõik 5 tabelit on `valitalgud` skeemis (users, events, tags, event_tags, registrations)
- [ ] **0.3** Kontrolli backend ühendust andmebaasiga (`application.properties` — host, db, user, parool)
- [ ] **0.4** Kontrolli, et Vue 3 frontend käivitub (`npm run dev`)
- [ ] **0.5** Kontrolli, et Spring Boot backend käivitub ilma vigadeta

---

## FAAS 1 — Backend: Alus ja infrastruktuur

### 1.1 Projekti seadistus
- [ ] **1.1.1** Lisa `pom.xml`-i vajalikud sõltuvused: Spring Web, Spring Data JPA, PostgreSQL Driver, Lombok, Spring Security (bcrypt), p6spy (logimiseks)
- [ ] **1.1.2** Seadista `application.properties`: andmebaasi ühendus, JPA dialekt, logi tase
- [ ] **1.1.3** Loo `CorsConfig.java` — luba Vue dev server päringuid (`http://localhost:5173`)
- [ ] **1.1.4** Loo globaalne `GlobalExceptionHandler.java` (@ControllerAdvice) — käsitle 400, 401, 403, 404, 409, 500

### 1.2 Entiteedid (JPA @Entity)
- [ ] **1.2.1** `User.java` — id (serial), passwordHash, roleId, status (ACTIVE|PENDING_ACTIVATION|DELETED), createdAt
- [ ] **1.2.2** `Role.java` — id, name (USER|ADMIN)
- [ ] **1.2.3** `Contact.java` — id, userId (FK→users), fullName, email, phone
- [ ] **1.2.4** `City.java` — id, name (unique)
- [ ] **1.2.5** `Event.java` — id, organizerId (FK→users), cityId (FK→cities), title, description, address, eventDate, startTime, endTime, maxParticipants, bannerImageUrl, isCancelled, createdAt
- [ ] **1.2.6** `SkillTag.java` — id, name (unique)
- [ ] **1.2.7** `EventSkillTag.java` — composite PK (eventId, skillTagId)
- [ ] **1.2.8** `Registration.java` — id, userId (FK→users), eventId (FK→events), status (LAHEB|VOIB_OLLA|EI_LAHE), registeredAt; unique(userId, eventId)
- [ ] **1.2.9** `Comment.java` — id, eventId (FK→events), userId (FK→users), content, createdAt
- [ ] **1.2.10** `Product.java` — id, name, description, price, imageUrl, stockQuantity, createdAt
- [ ] **1.2.11** `Cart.java` — id, userId (FK→users), createdAt, updatedAt; unique(userId)
- [ ] **1.2.12** `CartItem.java` — id, cartId (FK→carts), productId (FK→products), quantity; unique(cartId, productId)
- [ ] **1.2.13** `Order.java` — id, userId (FK→users nullable), billingId (FK→billings), status, subtotal, shipping, tax, total, createdAt
- [ ] **1.2.14** `OrderItem.java` — id, orderId, productId (nullable), productName, quantity, priceAtPurchase
- [ ] **1.2.15** `Billing.java` — id, firstName, lastName, company, country, street, postalCode, city, phone, email

### 1.3 Repositooriumid (Spring Data JPA)
- [ ] **1.3.1** `UserRepository.java`
- [ ] **1.3.2** `ContactRepository.java`
- [ ] **1.3.3** `EventRepository.java` — custom queries: findByFilters, findByOrganizerId, findUpcoming
- [ ] **1.3.4** `RegistrationRepository.java` — findByUserIdAndFutureEvents, findByEventId
- [ ] **1.3.5** `CommentRepository.java` — findByEventIdOrderByCreatedAt
- [ ] **1.3.6** `ProductRepository.java`
- [ ] **1.3.7** `OrderRepository.java`
- [ ] **1.3.8** `CityRepository.java`
- [ ] **1.3.9** `SkillTagRepository.java`

---

## FAAS 2 — Backend: Autentimine ja kasutajad

### 2.1 DTOd
- [ ] **2.1.1** `LoginDto.java` — email, password
- [ ] **2.1.2** `LoginResponseDto.java` — userId, firstName, middleName, lastName, role
- [ ] **2.1.3** `RegisterDto.java` — fullName, email, password, phone, description
- [ ] **2.1.4** `UserProfileDto.java` — userId, firstName, lastName, email, role, phoneNumber, description
- [ ] **2.1.5** `UpdateProfileDto.java` — firstName, lastName, phoneNumber, description
- [ ] **2.1.6** `ChangePasswordDto.java` — currentPassword, newPassword, confirmNewPassword
- [ ] **2.1.7** `SuccessMessageDto.java` — message

### 2.2 Teenused ja kontrollerid
- [ ] **2.2.1** `AuthController.java`:
  - `POST /api/login` → kontrolli email+parool, tagasta LoginResponseDto; vead: 400, 401, 403
  - `POST /api/register` → loo kasutaja (ACTIVE, USER role), tagasta 201; vead: 400, 409
- [ ] **2.2.2** `UserController.java`:
  - `GET /api/user/profile` → tagasta sisseloginud kasutaja andmed
  - `PUT /api/user/profile` → uuenda firstName, lastName, phone, description
  - `PUT /api/user/password` → vaheta parool (bcrypt kontroll)
  - `DELETE /api/user/profile` → kustuta konto (kaskaadimine: tühista aktiivsed sündmused, eemalda registreerimised)
- [ ] **2.2.3** `AuthService.java` — paroolide räsimine (BCrypt), kasutaja valideerimine
- [ ] **2.2.4** Loo lihtne sessioonimehhanism (userId päringu headeris `X-User-Id` — JWT asemele, lihtsustusena)

---

## FAAS 3 — Backend: Sündmused

### 3.1 DTOd
- [ ] **3.1.1** `EventResponseDto.java` — eventId, title, description, city, address, date, startTime, endTime, maxParticipants, currentParticipants, skillTags, bannerImageUrl, organizerId, organizerName
- [ ] **3.1.2** `EventDetailsResponseDto.java` — kõik EventResponseDto väljad + organizerEmail, userRegistrationStatus
- [ ] **3.1.3** `CreateEventDto.java` — title, description, cityId, address, date, startTime, endTime, maxParticipants, skillTagIds, bannerImageUrl
- [ ] **3.1.4** `UpdateEventDto.java` — samad väljad kui CreateEventDto
- [ ] **3.1.5** `OrganizedEventResponseDto.java` — eventId, title, date, city, status (Aktiivne|Lõppenud|Tühistatud), currentParticipants, maxParticipants
- [ ] **3.1.6** `RegisterEventDto.java` — status (GOING|MAYBE|NOT_GOING)

### 3.2 Kontrollerid
- [ ] **3.2.1** `EventController.java`:
  - `GET /api/events` — filtrid: ?cityId=&skillTagIds=&date= → tagasta ainult aktiivsed sündmused
  - `GET /api/events/:id` — ühe sündmuse detailid + kasutaja registreerimise staatus
  - `POST /api/events` — loo sündmus (organizerId = sisseloginud kasutaja)
  - `PUT /api/events/:id` — uuenda (ainult looja või admin)
  - `DELETE /api/events/:id` — tühista (is_cancelled = true, andmed säiluvad)
  - `GET /api/my-events?filter=THIS_WEEK|UPCOMING|ALL_FUTURE` — tulevased sündmused kus kasutaja on registreerunud
  - `GET /api/my-organized-events` — kasutaja loodud sündmused
- [ ] **3.2.2** `RegistrationController.java`:
  - `POST /api/events/:id/register` — registreeri (staatus LAHEB|VOIB_OLLA|EI_LAHE)
  - `DELETE /api/events/:id/unregister` — tühista registreerimine
- [ ] **3.2.3** `CommentController.java`:
  - `GET /api/events/:id/comments` — lae kommentaarid (kronoloogiliselt)
  - `POST /api/events/:id/comments` — lisa kommentaar

### 3.3 Abiteenus
- [ ] **3.3.1** `CityController.java`: `GET /api/cities` → tagasta linnade nimekiri
- [ ] **3.3.2** `SkillTagController.java`: `GET /api/skill-tags` → tagasta tagide nimekiri

---

## FAAS 4 — Backend: Kalender

- [ ] **4.1** `CalendarController.java`:
  - `GET /api/calendar?month=11&year=2023` → tagasta `{ month, year, daysWithEvents: [4,9,22] }` — ainult kasutaja registreeritud sündmused
  - `GET /api/calendar/day?date=2023-11-04` → tagasta selle päeva sündmused
- [ ] **4.2** `CalendarResponseDto.java` — month, year, daysWithEvents (List\<Integer\>)

---

## FAAS 5 — Backend: E-pood ja tellimused

### 5.1 DTOd
- [ ] **5.1.1** `ProductResponseDto.java` — productId, name, description, price, imageUrl
- [ ] **5.1.2** `CreateOrderDto.java` — firstName, lastName, companyName, country, streetAddress, postalCode, city, phone, email, items[{productId, quantity}]
- [ ] **5.1.3** `OrderResponseDto.java` — orderId, totalAmount, createdAt

### 5.2 Kontrollerid
- [ ] **5.2.1** `ProductController.java`:
  - `GET /api/products` — kõik tooted
  - `GET /api/products/:id` — ühe toote detailid
- [ ] **5.2.2** `OrderController.java`:
  - `POST /api/orders` — loo tellimus (billing + order_items salvestatakse), tagasta 201 + OrderResponseDto

---

## FAAS 6 — Frontend: Alus ja infrastruktuur

### 6.1 Projekti seadistus
- [ ] **6.1.1** Installi sõltuvused: `axios`, `vue-router@4`
- [ ] **6.1.2** Seadista `router/index.js` — kõik routes koos `meta.requiresAuth` märkimisega:
  - Avalikud: `/`, `/login`, `/register`, `/shop`, `/shop/*`, `/cart`, `/checkout`, `/order-success`, `/404`
  - Kaitstud (requiresAuth: true): `/events`, `/events/*`, `/my-events`, `/calendar`, `/profile`, `/my-organized-events`
- [ ] **6.1.3** Lisa Vue Router globaalne `beforeEach` guard — kontrolli localStorage userId; suuna `/unauthorized` kui kaitstud leht ja pole sisse logitud
- [ ] **6.1.4** Loo `src/auth/auth.js` — `getUser()`, `isLoggedIn()`, `login(data)`, `logout()` (localStorage operatsioonid)
- [ ] **6.1.5** Loo `src/api-services/axiosInstance.js` — base URL (`http://localhost:8080`), globaalne 401 interceptor (localStorage clear + redirect `/unauthorized`)
- [ ] **6.1.6** Loo `src/components/common/AppHeader.vue` — dünaamiline menüü (sisseloginud vs. väljalogitud), kasuta igal lehel

### 6.2 API teenused (üks fail = üks kontroller)
- [ ] **6.2.1** `authService.js` — login, register
- [ ] **6.2.2** `userService.js` — getProfile, updateProfile, changePassword, deleteProfile
- [ ] **6.2.3** `eventService.js` — getEvents, getEvent, createEvent, updateEvent, cancelEvent, getMyEvents, getOrganizedEvents
- [ ] **6.2.4** `registrationService.js` — register, unregister
- [ ] **6.2.5** `commentService.js` — getComments, postComment
- [ ] **6.2.6** `calendarService.js` — getCalendarMonth, getCalendarDay
- [ ] **6.2.7** `productService.js` — getProducts, getProduct
- [ ] **6.2.8** `orderService.js` — createOrder
- [ ] **6.2.9** `cityService.js` — getCities
- [ ] **6.2.10** `skillTagService.js` — getSkillTags

---

## FAAS 7 — Frontend: Vaated (Views)

### 7.1 Avalikud vaated (autentimine ei nõuta)
- [ ] **7.1.1** `LandingPage.vue` (`/`) — tutvustustekst, dünaamiline header, nupud: "Logi sisse" / "Loo kasutaja" / "Sündmused" / "E-pood"
- [ ] **7.1.2** `LoginView.vue` (`/login`) — vorm (email, parool), POST /api/login, localStorage salvestamine, redirect /events; vead: 400, 401, 403
- [ ] **7.1.3** `RegisterView.vue` (`/register`) — vorm (fullName, email, password, repeatPassword, phone, description), frontend valideerimine (repeatPassword), POST /api/register, redirect /login; vead: 400, 409
- [ ] **7.1.4** `UnauthorizedView.vue` (`/unauthorized`) — teade + nupud "Logi sisse" / "Loo kasutaja" / "Avalehele"
- [ ] **7.1.5** `ErrorView.vue` (`/404` + catch-all) — 404 leht, nupp "Avalehele"

### 7.2 Kaitstud vaated — kasutaja
- [ ] **7.2.1** `ProfileView.vue` (`/profile`) — GET /api/user/profile, kuva andmed, 3 nuppu mis avavad modaale
- [ ] **7.2.2** `EditProfileModal.vue` — PUT /api/user/profile; eeltäida andmed, ilma email väljata
- [ ] **7.2.3** `ChangePasswordModal.vue` — PUT /api/user/password; 3 välja, frontend confirm kontroll
- [ ] **7.2.4** `DeleteConfirmationModal.vue` — DELETE /api/user/profile; hoiatustekst, peale õnnestumist localStorage.clear() + redirect /

### 7.3 Kaitstud vaated — sündmused
- [ ] **7.3.1** `EventsView.vue` (`/events`) — GET /api/events, filtrid (linn dropdown, skill-tag checkboxid, kuupäev), kaardid koos "Vaata detaile" nuppudega; nupp "Loo sündmus"
- [ ] **7.3.2** `EventDetailsView.vue` (`/events/:id`) — GET /api/events/:id, registreerimise staatuse loogika (LÄHEB/VÕIB-OLLA/EI LÄHE + Kinnita/Muuda valik), kommentaaride sektsioon (GET + POST)
- [ ] **7.3.3** `MyEventsView.vue` (`/my-events`) — GET /api/my-events, 3 tabi (Sel nädalal/Tulevased/Kõik), kaardid; nupp "Minu loodud sündmused"
- [ ] **7.3.4** `CalendarView.vue` (`/calendar`) — GET /api/calendar, kuva kuu, märgi päevad millel on sündmused; päeva klõps → GET /api/calendar/day → näita sündmused allosas; eelmine/järgmine kuu navigatsioon
- [ ] **7.3.5** `CreateEventView.vue` (`/events/create`) — POST /api/events, frontend valideerimine (endTime > startTime, date tulevikus, maxParticipants > 0), linnad ja tagid dropdown/checkboxid; redirect /my-organized-events
- [ ] **7.3.6** `EditEventView.vue` (`/events/:id/edit`) — GET + PUT /api/events/:id, eeltäida vorm, "Tühista sündmus" avab kinnitusmodaali → DELETE; vead: 400, 403, 404
- [ ] **7.3.7** `OrganizedEventsView.vue` (`/my-organized-events`) — GET /api/my-organized-events, tabel (pealkiri, kuupäev, asukoht, staatus, osalejad, nupud Detail/Muuda)

### 7.4 Avalikud vaated — e-pood
- [ ] **7.4.1** `ShopView.vue` (`/shop`) — GET /api/products, ruudustik (3 reas), parempoolne detailide paneel (v-if selectedProduct), ostukorv localStorage'is; nupud: "Lisa korvi" (qty+1), "Detailid" (laeb detailid), koguse valija paremal paneelil, "X" peidab paneeli
- [ ] **7.4.2** `CartView.vue` (`/cart`) — loeb localStorage võtit "cart", tabel (toode, hind, kogus ±, kokku, x), Tellimuse kokkuvõte (vahesumma + transport 5€ + käibemaks 8% + kokku); "Mine kassasse" ainult kui ostukorv pole tühi
- [ ] **7.4.3** `CheckoutView.vue` (`/checkout`) — POST /api/orders, arvelduse vorm (kohustuslikud väljad + valideerimine), kui ostukorv tühi → suuna /cart; peale POST → ostukorvi tühjendamine + suuna /order-success
- [ ] **7.4.4** `OrderSuccessView.vue` (`/order-success`) — õnnestumise kinnitus, nupud "Jätka ostlemist" (/shop) ja "Avalehele" (/); kui jõutakse ilma tellimuseta → suuna /shop

---

## FAAS 8 — Integratsioonitestimine

- [ ] **8.1** Testi autentimise voogu: register → login → localStorage → logout
- [ ] **8.2** Testi sündmuse loomist ja redigeerimist (looja vs. mitte-looja)
- [ ] **8.3** Testi registreerimise staatuse muutmist sündmusel
- [ ] **8.4** Testi kalendri kuvamist (ainult kasutaja registreeritud sündmused)
- [ ] **8.5** Testi e-poe voogu: sirvida → lisa korvi → checkout → tellimus
- [ ] **8.6** Testi Router guard'e: kaitstud URL ilma loginita → /unauthorized
- [ ] **8.7** Testi 401 globaalset interceptorit (token aegunud → localStorage clear + redirect)
- [ ] **8.8** Testi profili kustutamist (kaskaadimine backendis)

---

## FAAS 9 — Viimistlus

- [ ] **9.1** Kontrolli kõigi veateadete korrektset kuvamist (400, 401, 403, 404, 409, 500)
- [ ] **9.2** Kontrolli dünaamilist headerit (sisseloginud vs. väljalogitud) igal lehel
- [ ] **9.3** Kontrolli, et "Events" nupp LandingPage'il suunab väljalogitud kasutaja /unauthorized lehele
- [ ] **9.4** Kontrolli, et e-pood (/shop, /cart, /checkout) töötab ilma sisselogimiseta
- [ ] **9.5** Kontrolli, et ostukorv säilub localStorage'is läbi sessioonide

---

## API kokkuvõte (kiirviide)

| Meetod | URL | Kirjeldus |
|--------|-----|-----------|
| POST | /api/login | Sisselogimine |
| POST | /api/register | Registreerimine |
| GET | /api/user/profile | Profil |
| PUT | /api/user/profile | Profili muutmine |
| PUT | /api/user/password | Parooli vahetus |
| DELETE | /api/user/profile | Konto kustutamine |
| GET | /api/events | Sündmuste nimekiri (filtritega) |
| GET | /api/events/:id | Sündmuse detailid |
| POST | /api/events | Loo sündmus |
| PUT | /api/events/:id | Muuda sündmust |
| DELETE | /api/events/:id | Tühista sündmus |
| POST | /api/events/:id/register | Registreeri sündmusele |
| DELETE | /api/events/:id/unregister | Tühista registreerimine |
| GET | /api/events/:id/comments | Kommentaarid |
| POST | /api/events/:id/comments | Lisa kommentaar |
| GET | /api/my-events | Minu tulevased sündmused |
| GET | /api/my-organized-events | Minu korraldatud sündmused |
| GET | /api/calendar | Kuu kalender |
| GET | /api/calendar/day | Päeva sündmused |
| GET | /api/cities | Linnade nimekiri |
| GET | /api/skill-tags | Tagide nimekiri |
| GET | /api/products | Tooted |
| GET | /api/products/:id | Toote detailid |
| POST | /api/orders | Loo tellimus |
| GET | /api/counties | Maakondade nimekiri |

---

## Ülesanne: Maakonnad + linna/maakonna filtreerimine (2026-05-21)

### Eesmärk
Lisada andmebaasi 15 linna ja 15 maakonda. Sündmusel on lisaks linnale ka maakond
(`county_id` veerg `events` tabelis). Kõik filtrid, kus on linna valik, peavad lubama
filtreerida kas ainult linna, ainult maakonna või mõlema järgi (kaks sõltumatut valikut →
3 sisukat varianti + "kõik"). Sündmuse loomise vormi lisada maakonna valik.

### Andmemudeli otsus (lõplik — normaliseeritud)
Eraldi `counties` tabel + `cities.county_id` FK (NOT NULL). Iga linn kuulub ühte maakonda;
`events` viitab AINULT linnale (`city_id`), maakond tuletatakse linna kaudu (`event.city.county`).
Põhjus: Eestis on linn↔maakond fikseeritud hierarhia → vastuolu (Tartu Harjumaal) on võimatu,
liiasus puudub, üks tõeallikas. (Esimene mustand kasutas `events.county_id` sõltumatut veergu —
see lubas vastuolulisi andmeid, seega normaliseerisime.)

### Sammud
- [ ] **SQL** `2_create.sql`: lisa `counties` tabel, `events.county_id` veerg + FK + indeks
- [ ] **SQL** `3_import.sql`: 15 linna + 15 maakonda, sündmustele `county_id`
- [ ] **Backend** uus `county` ressurss: `County`, `CountyRepository`, `CountyMapper`,
      `CountyResponseDto`, `CountyController` (GET /api/counties), `CountyService`
- [ ] **Backend** `Event` entiteet: lisa `@ManyToOne County county`
- [ ] **Backend** `EventMapper`: county mappingud (response + details + ignore loomisel/uuendamisel)
- [ ] **Backend** `EventRepository.findFilteredEvents`: lisa `countyId` parameeter + tingimus
- [ ] **Backend** `EventService`: `countyId` filtris, county valideerimine+määramine loomisel
- [ ] **Backend** DTO-d: `CreateEventDto.countyId`, `EventResponseDto.countyId/county`,
      `EventDetailsResponseDto.county`, `OrganizedEventResponseDto.county`
- [ ] **Backend** `EventController` + `MyOrganizedEventsController` + `MyEventsController`: `countyId`
- [ ] **Backend** `MyEventsService` + `RegistrationRepository.findMyEventsBy` + projektsioon: county
- [ ] **Backend** `ErrorResponse`: `COUNTY_NOT_FOUND`
- [ ] **Frontend** `CountyService.js`
- [ ] **Frontend** `EventsView`, `MyEventsView`, `MyOrganizedEventsView`: maakonna filter
- [ ] **Frontend** `CreateEventView`: maakonna valik + valideerimine + kinnitusvaade
- [ ] **Frontend** `EditEventView`: maakond (lukus, nagu linn)
- [ ] **Verify**: `./gradlew build`, `npm run build`, käivita SQL skriptid andmebaasi vastu

### Review (valmis)
**Andmebaas:** `counties` tabel (15 maakonda), `cities.county_id` FK (15 linna, kõik seotud).
`events` säilitab ainult `city_id`. SQL skriptid (`2_create.sql`, `3_import.sql`) uuendatud ja
andmebaasi vastu käivitatud (reset → create → import).

**Backend:** uus `county` ressurss (`GET /api/counties`); `City` entiteet → `@ManyToOne County`;
`CityResponseDto` annab `countyId`+`county`; `EventMapper` tuletab maakonna `city.county` kaudu;
`findFilteredEvents` ja `findMyEventsBy` filtreerivad maakonda läbi `cities.county_id` JOIN-i;
`OrganizedEventResponseDto`/`MyEventResponseDto` saavad maakonna; `countyId` lisatud kõigi
sündmuste-loendite controlleritesse/teenustesse.

**Frontend:** `CountyService.js`; maakonna filter `EventsView`/`MyEventsView`/`MyOrganizedEventsView`
(linna ja maakonna rippmenüü sõltumatud → 3 varianti); loomisvormis maakond → piirab linnade valikut
(saadab ainult `cityId`); muutmisvaates linn+maakond lukus.

**Verifitseeritud (live curl):** `/api/counties`=15, `/api/cities`=15 (countyId tuletatud),
filter linn=2, maakond=2, mõlemad kooskõlas=2, mõlemad vastuolus=0 (AND korrektne), loomine ilma
countyId-ta tuletab maakonna õigesti. Backend `compileJava` ✓, frontend `npm run build` ✓.

**NB! Mitteseotud:** `ValitalgudbackApplicationTests` ebaõnnestub (`@SpringBootTest` ei leia
konfiguratsiooni, sest testi pakett on `ee.valiit.valitalgudback`, mitte `ee.bcs.valitalgud`) —
see on eelnevalt olemas olnud viga, ei puuduta seda ülesannet.