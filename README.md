# Valitalgud

Täisstack **ürituste- ja talguhalduse rakendus** koos integreeritud e-poe ja AI vestlusrobotiga. Inimesed saavad avastada ja korraldada talguid üle Eesti, registreeruda osalejaks, jälgida oma sündmusi kalendris ning osta kogukonna tooteid.

> Monorepo: `frontend/` (Vue 3 + Vite) + `backend/` (Spring Boot + PostgreSQL).

---

## Sisukord

- [Ülevaade](#ülevaade)
- [Tehnoloogiad](#tehnoloogiad)
- [Projekti struktuur](#projekti-struktuur)
- [Eeldused](#eeldused)
- [Seadistamine ja käivitamine](#seadistamine-ja-käivitamine)
- [Keskkonnamuutujad](#keskkonnamuutujad)
- [Andmebaas](#andmebaas)
- [Funktsionaalsus lehtede kaupa](#funktsionaalsus-lehtede-kaupa)
- [Globaalsed komponendid](#globaalsed-komponendid)
- [API ülevaade](#api-ülevaade)
- [Autentimine ja õigused](#autentimine-ja-õigused)
- [AI vestlusrobot (Pam)](#ai-vestlusrobot-pam)
- [Kontaktivorm (Resend)](#kontaktivorm-resend)
- [Koodistiil ja konventsioonid](#koodistiil-ja-konventsioonid)
- [Testimine](#testimine)
- [Mis on tehtud ja mida edasi teha](#mis-on-tehtud-ja-mida-edasi-teha)

---

## Ülevaade

Valitalgud koondab kolm funktsionaalset valdkonda ühte rakendusse:

1. **Sündmused / talgud** — avastamine (filtritega), detailvaade, registreerumine (lähen / võib-olla / ei lähe), korraldamine, kalender, kommentaarid.
2. **E-pood** — kogukonna tooted (riided, tarvikud), ostukorv, tellimuse vormistamine.
3. **Tugifunktsioonid** — AI vestlusrobot **Pam** (Groq), kontaktivorm (Resend), kasutajakontod ja profiilihaldus.

Frontend ja backend on lahti seotud: brauser suhtleb backendiga ainult `/api/**` REST-päringute kaudu (JSON).

---

## Tehnoloogiad

### Frontend (`frontend/`)
| Tehnoloogia | Versioon | Roll |
|---|---|---|
| Vue 3 | ^3.5 | UI raamistik (**Options API**, mitte Composition API) |
| Vite | ^8 | Build- ja arendusserver |
| Vue Router | ^5 | Marsruutimine (`createWebHistory`) |
| Pinia | ^3 | Olekuhaldus |
| Axios | ^1.14 | HTTP-päringud (`/api/**`) |
| Bootstrap | ^5.3 | UI-alus, üle kirjutatud neobrutalism-teemaga (`src/assets/main.css`) |
| @phosphor-icons/vue | ^2 | Ikoonid |

### Backend (`backend/`)
| Tehnoloogia | Versioon | Roll |
|---|---|---|
| Java | 21 | Keel (toolchain) |
| Spring Boot | 4.0.x | Web, Data JPA, Validation, Actuator |
| PostgreSQL | — | Andmebaas |
| MapStruct | 1.6.3 | Entiteet ↔ DTO teisendused |
| Lombok | — | Boilerplate'i vähendamine |
| springdoc-openapi | 3.0.3 | Swagger UI (`/swagger-ui.html`) |
| P6Spy | 3.9.1 | SQL-päringute logimine arenduses |
| spring-dotenv | 5.1.0 | `backend/.env` laadimine keskkonnamuutujatena |

**Välised teenused:** [Groq](https://groq.com) (AI vestlusrobot), [Resend](https://resend.com) (kontaktivormi meilid).

---

## Projekti struktuur

```
valitalgud/
├── CLAUDE.md                     # Juhised AI-agendile + projekti ülevaade
├── CODE_STYLE_GUIDE_back.md      # Backendi koodistiili juhend
├── CODE_STYLE_GUIDE_front.md     # Frontendi koodistiili juhend
├── README.md                     # (see fail)
├── backend/
│   ├── CLAUDE.md                 # Backend-spetsiifilised juhised
│   ├── .env(.example)            # API võtmed (Groq, Resend) — .env on .gitignore's
│   ├── build.gradle
│   ├── database/                 # SQL-skriptid (käivita järjekorras)
│   │   ├── 1_reset_database.sql
│   │   ├── 2_create.sql
│   │   └── 3_import.sql
│   └── src/main/
│       ├── java/ee/bcs/valitalgud/
│       │   ├── controller/<ressurss>/        # REST-kontrollerid + dto/
│       │   ├── service/                       # Äriloogika
│       │   ├── persistence/<ressurss>/        # Entity, Mapper, Repository
│       │   └── infrastructure/                # config/, error/, exception/
│       └── resources/
│           ├── application.properties
│           └── chatbot-context.md             # AI roboti Pam süsteemiviip
└── frontend/
    ├── CLAUDE.md                 # Frontend-spetsiifilised juhised
    └── src/
        ├── api-services/         # Axios moodulid (üks fail ressursi kohta)
        ├── auth/                 # Sisselogimise olek (localStorage), route-kaitsed
        ├── components/           # common/, forms/, modals/, tables/
        ├── navigation/           # AppNavbar, AppFooter, NavigationService
        ├── router/               # Marsruutide definitsioonid
        ├── views/                # Lehekomponendid (üks marsruudi kohta)
        └── assets/main.css       # Neobrutalism-teema (disainimuutujad --nb-*)
```

Backend järgib **kihilist arhitektuuri**: `controller → service → repository`. Iga domeeniressurss (nt `event`, `product`) saab oma alamkausta igas kihis. Vt detaile `backend/CLAUDE.md`.

---

## Eeldused

- **Java 21** (JDK)
- **Node.js** ^20.19 või >=22.12
- **PostgreSQL** (jooksev instants `localhost`-il)

---

## Seadistamine ja käivitamine

### 1. Andmebaas

Käivita SQL-skriptid **järjekorras** `postgres` andmebaasi vastu (nt `psql` või IntelliJ Database tööriistaga):

```bash
# 1_reset_database.sql  → kustutab ja loob 'public' skeemi uuesti
# 2_create.sql          → loob tabelid
# 3_import.sql          → impordib algandmed (kasutajad, sündmused, tooted jms)
```

Ühendusandmed (vt `backend/src/main/resources/application.properties`):

| Väli | Väärtus |
|---|---|
| host | `localhost` |
| andmebaas | `postgres` |
| kasutaja | `postgres` |
| parool | `student123` |

> Arenduses mähitakse PostgreSQL-i draiver P6Spy-ga (`jdbc:p6spy:postgresql://...`), et logida vormindatud SQL-i konsooli.

### 2. Backend

```bash
cd backend
# Lisa .env fail (vt allpool "Keskkonnamuutujad"), seejärel:
./gradlew bootRun        # käivitab backendi pordil 8080
```

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Tervisekontroll (Actuator): <http://localhost:8080/actuator/health>

### 3. Frontend

```bash
cd frontend
npm install
npm run dev              # käivitab arendusserveri pordil 8081
```

Vite suunab kõik `/api/**` päringud automaatselt aadressile `http://localhost:8080` (vt `vite.config.*`), seega CORS-i arenduses seadistama ei pea.

### Kasulikud käsud

```bash
# Backend
./gradlew build                  # ehita JAR
./gradlew test                   # käivita testid

# Frontend
npm run build                    # tootmise build
npm run lint                     # oxlint + eslint (vt märkust allpool)
npm run format                   # Prettier
```

---

## Keskkonnamuutujad

API võtmed loetakse `backend/.env` failist (laaditakse `spring-dotenv` kaudu). See fail on `.gitignore`-s — **ära commiti seda**. Näidis: `backend/.env.example`.

| Muutuja | Kohustuslik | Kirjeldus |
|---|---|---|
| `GROQ_API_KEY` | AI roboti jaoks | Groq API võti ([console.groq.com/keys](https://console.groq.com/keys)) |
| `RESEND_API_KEY` | Kontaktivormi jaoks | Resend API võti ([resend.com/api-keys](https://resend.com/api-keys)) |
| `CONTACT_TO_EMAIL` | Kontaktivormi jaoks | E-mail, kuhu kontaktivormi päringud saadetakse |
| `RESEND_FROM_EMAIL` | valikuline | Saatja aadress (vaikimisi `onboarding@resend.dev` testimiseks) |

> Pärast `.env` muutmist tuleb backend taaskäivitada — väärtused loetakse ainult käivitusel.

---

## Andmebaas

Skeem koosneb **15 tabelist** `public` skeemis. Kõik primaarvõtmed on `serial`. Täielik kirjeldus on `CLAUDE.md`-s ja `backend/database/2_create.sql`-is. Lühiülevaade:

**Kasutajad ja õigused**
- `roles` — rollid (`USER`, `ADMIN`)
- `users` — kasutajad (`role_id`, `status`: ACTIVE / PENDING_ACTIVATION / DELETED)
- `contacts` — kasutaja kontaktandmed (1:1)

**Üritused**
- `cities`, `events` (`organizer_id`, `city_id`)
- `skill_tags`, `event_skill_tags` (vahendajatabel)
- `registrations` — UNIQUE(`user_id`,`event_id`), staatus: LAHEB / VOIB_OLLA / EI_LAHE
- `comments` — sündmuse kommentaarid

**Pood**
- `products` (hind, `image_url`, laoseis), `carts`, `cart_items`
- `billings`, `orders` (staatus: PENDING…CANCELLED), `order_items` (säilitab `product_name` ja `price_at_purchase` ostuhetkest)

> JPA DDL on **välja lülitatud** (`ddl-auto=none`) — skeemi hallatakse ainult SQL-skriptidega. Tootepiltide jaoks hoitakse failitee (`/images/products/*.png`), mitte base64.

---

## Funktsionaalsus lehtede kaupa

Iga marsruut vastab ühele `views/` komponendile. **Avalik** = nähtav sisselogimata; **Kaitstud** = nõuab sisselogimist (suunab `/unauthorized`-i).

| Marsruut | Vaade | Ligipääs | Kirjeldus |
|---|---|---|---|
| `/` | `LandingPage` | Avalik | Avaleht: hero-sektsioon, eelseisvate sündmuste tutvustus, e-poe väljavalik (Nokamüts, Termopudel, Kapuutspusa), lõpp-CTA. Sisaldab Pam vestlusrobotit. |
| `/login` | `LoginView` | Avalik | Sisselogimine (e-post + parool). Sisselogitu suunatakse minema. |
| `/register` | `RegisterView` | Avalik | Konto loomine (täisnimi, e-post, parool). |
| `/register-success` | `RegisterSuccessView` | Avalik | Registreerumise kinnitusleht. |
| `/events` | `EventsView` | Avalik | Sündmuste loend filtritega (linn/maakond, oskuse-tagid, ajafilter: THIS_WEEK / UPCOMING / ALL_FUTURE). Iga kaart → "Näita rohkem". |
| `/events/:id` | `EventDetailsView` | Avalik | Sündmuse täisinfo: organisaator, aeg, koht, osalejate arv, oskuse-tagid. Registreerumine (lähen / võib-olla / ei lähe) ja kommentaarid (sisselogitule). |
| `/events/create` | `CreateEventView` | Kaitstud | Uue sündmuse loomise vorm. |
| `/events/:id/edit` | `EditEventView` | Kaitstud | Sündmuse muutmine (ainult omanik). |
| `/my-events` | `MyEventsView` | Kaitstud | Kasutaja registreerunud + korraldatud sündmused (vahelehed, osalejate vaade). |
| `/calendar` | `CalendarView` | Kaitstud | Kuukalender kasutaja sündmustega; päevaklikk näitab selle päeva sündmusi. |
| `/profile` | `ProfileView` | Kaitstud | Profiili vaatamine/muutmine, parooli vahetus, konto kustutamine (pehme kustutus). |
| `/shop` | `ShopView` | Avalik | E-pood: tootekaardid, hinna järgi sortimine, "Lisa korvi" + koguse muutmine, detailide külgpaneel (pildi suurendus klikiga). |
| `/cart` | `CartView` | Avalik | Ostukorv: read, kogused, kokkuvõte. |
| `/checkout` | `CheckoutView` | Avalik | Arveldusandmete vorm + tellimuse vormistamine (vähendab laoseisu). |
| `/order-success` | `OrderSuccessView` | Avalik | Tellimuse kinnitusleht. |
| `/contact` | `ContactView` | Avalik | Kontaktivorm (Nimi/Ettevõte, E-mail, Sõnum) → saadab meili Resend API kaudu. |
| `/unauthorized` | `UnauthorizedView` | Avalik | Kuvatakse, kui sisselogimata kasutaja proovib kaitstud lehte. |
| `/404` | `ErrorView` | Avalik | Vea/„ei leitud" leht; tundmatud marsruudid suunatakse siia. |

---

## Globaalsed komponendid

- **`AppNavbar`** (`navigation/`) — ülemine menüü: Avaleht, Sündmused (dropdown), e-pood, Kontakt, Ostukorv (live-loendur), Profiil / Logi sisse / Logi välja. Lingid kuvatakse rolli/sisselogimise järgi.
- **`AppFooter`** — globaalne jalus (renderdatakse `App.vue`-s kõigil lehtedel).
- **`ChatbotWidget`** (`components/common/`) — paremas alanurgas hõljuv **Pam** vestlusakna nupp.
- **`NavigationService`** (`navigation/`) — tsentraliseeritud `router.push` abimeetodid (nt `navigateToEvents()`).

---

## API ülevaade

Kõik endpointid on `/api` all. Täielik interaktiivne dokumentatsioon: **Swagger UI** (`/swagger-ui.html`).

| Domeen | Meetod & tee | Kirjeldus |
|---|---|---|
| **Auth** | `POST /api/login` | Logi sisse (e-post + parool) |
| | `POST /api/register` | Registreeri uus kasutaja |
| **Sündmused** | `GET /api/events` | Sündmuste loend (filtrid päringuparameetritena) |
| | `GET /api/events/{id}` | Sündmuse detailid |
| | `GET /api/events/{id}/edit` | Andmed muutmisvormi eeltäitmiseks |
| | `POST /api/events` | Loo sündmus |
| | `PUT /api/events/{id}` | Uuenda sündmust |
| | `DELETE /api/events/{id}` | Tühista sündmus (pehme kustutus) |
| **Registreerumine** | `POST /api/events/{id}/register` | Registreeru / muuda staatust |
| | `DELETE /api/events/{id}/register` | Tühista registreerumine |
| **Kommentaarid** | `GET /api/events/{id}/comments` | Sündmuse kommentaarid |
| | `POST /api/events/{id}/comments` | Lisa kommentaar (max 1000 tähemärki) |
| **Minu sündmused** | `GET /api/my-events` | Registreerunud sündmused |
| | `GET /api/my-organized-events` | Korraldatud sündmused |
| **Kalender** | `GET /api/calendar` | Kuu kalendriandmed |
| | `GET /api/calendar/day` | Päeva sündmused |
| **Profiil** | `GET /api/profile/{userId}` | Hangi profiil |
| | `PUT /api/profile/{userId}` | Uuenda profiili |
| | `PUT /api/profile/{userId}/password` | Vaheta parool |
| | `DELETE /api/profile/{userId}` | Kustuta konto (pehme) |
| **E-pood** | `GET /api/products` | Saadaolevad tooted (laoseis > 0) |
| | `GET /api/products/{id}` | Toote detailid |
| | `POST /api/cart/items` | Lisa toode ostukorvi |
| | `GET /api/cart` | Hangi ostukorv |
| | `PUT /api/cart/items/{cartItemId}` | Muuda rea kogust |
| | `DELETE /api/cart/items/{cartItemId}` | Eemalda rida |
| | `POST /api/orders` | Vormista tellimus |
| **Valikuandmed** | `GET /api/cities` | Linnad |
| | `GET /api/counties` | Maakonnad |
| | `GET /api/skill-tags` | Oskuse-tagid |
| **Tugi** | `POST /api/chat` | Sõnum AI robotile Pam |
| | `POST /api/contact` | Kontaktivormi päring (meil) |

Vead vormindatakse ühtselt (`infrastructure/error/`): iga viga kannab `code`, `message` ja HTTP-staatuse (vt `ErrorResponse` enum).

---

## Autentimine ja õigused

> ⚠️ **Praegune mudel on lihtsustatud (õppeprojekt).** See ei kasuta tokeneid/sessioone — sobib arenduseks, vajab tootmiseks tugevdamist (vt [edasised sammud](#mis-on-tehtud-ja-mida-edasi-teha)).

- `POST /api/login` tagastab kasutaja info (`userId`, `roleName`), mille frontend salvestab `localStorage`-i võtme `user` alla (`auth/auth.js`).
- **Marsruudikaitse** toimub kliendipoolselt: `router.beforeEach` kontrollib kaitstud marsruutide puhul `AuthHelper.isLoggedIn()` ja suunab vajadusel `/unauthorized`-i. Kaitstud: `my-events`, `calendar`, `profile`, `event-create`, `event-edit`.
- **Auto-logout:** globaalne Axios-interceptor (`main.js`) püüab 401-vastused — kui sessioon ei kehti (nt konto kustutatud), tühjendab `localStorage`-i ja suunab login-i (login-päring ise on välistatud).

---

## AI vestlusrobot (Pam)

- **Nimi:** Pam. Frontend-vidin `ChatbotWidget.vue` (paremal all). Päringud → `POST /api/chat` → `ChatService` → Groq (OpenAI-ühilduv API).
- **Süsteemiviip:** `backend/src/main/resources/chatbot-context.md`. Kogu faili sisu saadetakse mudelile „system" rollina. **Muudatuse jõustamiseks taaskäivita backend.**
- **Käitumiskaitse:** Pam vastab **ainult Valitalgude teemadel** (sündmused, talgud, registreerumine, korraldamine, e-pood). Ta keeldub teemavälistest küsimustest (üldteadmised, arvutused nagu „2+2", kodeerimine, arvamused) ja on kaitstud manipulatsiooni / jailbreak'i / prompt-injection'i vastu — neid reegleid hoitakse süsteemiviibas.
- **Seaded** (`application.properties`, `groq.*`): mudel, temperatuur, max-tokenid. API võti: `GROQ_API_KEY`.

---

## Kontaktivorm (Resend)

- `ContactView` (`/contact`) → `POST /api/contact` → `ContactService` → Resend API (`POST https://api.resend.com/emails`).
- **Meili pealkiri:** `Sissetulnud päring - {Nimi/Ettevõte}`. Sisu on brändi-stiilis HTML (+ tekstiversioon); `reply-to` on saatja e-mail, et saaks otse vastata. Kasutaja sisend on HTML-escape'itud.
- **Seadistus:** `RESEND_API_KEY`, `CONTACT_TO_EMAIL`, `RESEND_FROM_EMAIL` (vt keskkonnamuutujad). Testaadressiga `onboarding@resend.dev` lubab Resend saata ainult sinu enda konto e-mailile; suvalisele aadressile saatmiseks kinnita Resendis oma domeen.

---

## Koodistiil ja konventsioonid

Projektis on **eraldi koodistiili juhendid** — järgi neid uut koodi kirjutades:

- **Backend:** `backend/CLAUDE.md` ja `CODE_STYLE_GUIDE_back.md` — kihtide vastutused, meetodite nimetamine (`get`/`find`/`create`/`validate`/`handle`/`to`), DTO-muster, MapStruct, `ErrorResponse` enum.
- **Frontend:** `frontend/CLAUDE.md` ja `CODE_STYLE_GUIDE_front.md` — **Options API** (mitte Composition API), komponendi struktuur, `event-` eesliitega sündmused, `.then/.catch/.finally` + `handle`-meetodite muster, `beforeMount` andmete laadimiseks.

Lühireeglid: üks meetod = üks tegevus; controller ei sisalda äriloogikat; entiteeti ei tagastata API-st (ainult DTO); veateated `ErrorResponse` enumis.

---

## Testimine

```bash
cd backend
./gradlew test                                  # kõik testid
./gradlew test --tests "ee.bcs.valitalgud.SomeTest"   # üksik test
```

Testid asuvad `backend/src/test/`. Frontendil eraldi testikomplekti praegu pole.

---

## Mis on tehtud ja mida edasi teha

### Valmis
- ✅ Kasutajakontod (registreerimine, sisselogimine, profiil, parooli vahetus, konto kustutus)
- ✅ Sündmused: loend + filtrid, detailvaade, loomine/muutmine/tühistamine, registreerumine, kommentaarid
- ✅ Minu sündmused + kalender
- ✅ E-pood: tooted, ostukorv, checkout, tellimused
- ✅ AI vestlusrobot Pam (Groq) teemakaitsega
- ✅ Kontaktivorm (Resend)

### Soovitatavad edasised sammud
- 🔐 **Autentimine tootmistasemele:** liigu token-/sessioonipõhisele autentimisele ja **serveripoolsele õiguste kontrollile** (praegu kontrollitakse õigusi kliendipoolselt ja kasutaja-ID-d edastatakse kliendilt). Vaata üle paroolide turvaline hoidmine.
- 🧹 **Eemalda testiandmed enne üleandmist:** `LoginView.vue` ja `ContactView.vue` sisaldavad eeltäidetud välju kommentaariga `// TEMP: ... remove before delivery`.
- 🛠 **ESLint:** `npm run lint` `eslint`-osa kukub praegu läbi (puudub `eslint.config.js`) — kas lisa konfiguratsioon või eemalda samm. `oxlint` töötab.
- 📧 **Resend:** tootmises kinnita oma domeen, et saata päris saatjaaadressilt suvalisele e-mailile.
- 📄 **Pagineerimine** sündmuste ja toodete loenditele, kui andmemaht kasvab.
- 🖼 **Pildilaadimine** sündmustele/toodetele (praegu URL/failitee põhine).
- 🧪 **Testikatvuse laiendamine** (backend + frontendi testid).

---

> Lisajuhised AI-agendiga (Claude Code) töötamiseks: vt `CLAUDE.md` (juurkaustas) ning `backend/CLAUDE.md` ja `frontend/CLAUDE.md`.
