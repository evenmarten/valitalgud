# Valitalgud — Etapiviisiline testplaan

See fail sisaldab valmis prompte, mida saab anda **uuele Claude agendile**, et testida veebirakendust etapp-etapilt. Iga etapi prompt on **iseseisev** (copy-paste'itav) — agent ei eelda eelmiste etappide konteksti.

> **NB!** Käivita etapid järjekorras. Iga etapp eeldab, et eelmise etapi setup (DB, backend, frontend) on töökorras.

---

## Testimise lähenemine

Testime **alt üles**, et vead isoleeruksid kihti, kus nad tekivad:

1. **Keskkond** (Etapp 0) — kas DB, backend ja frontend üldse käivad
2. **Backend API** (Etapp 1) — kas endpointid vastavad õigesti (Swagger / curl), sõltumata frontendist
3. **Frontend UI** (Etapid 2–11) — kas vaated töötavad brauseris (golden path + servajuhud)
4. **Negatiivsed testid** (Etapp 12) — veahaldus, õiguste kontroll
5. **Regressioon** (Etapp 13) — täielik kasutajateekond otsast lõpuni

Iga etapp testib **golden path'i** (õnnestumise voog) JA **servajuhte** (vead, tühjad andmed, valed õigused).

---

## Keskkonna andmed (kehtib kõikidele etappidele)

| Komponent | Aadress |
|-----------|---------|
| Backend (Spring Boot) | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| Frontend (Vite) | `http://localhost:8081` |
| Andmebaas | PostgreSQL, host `localhost`, db `postgres`, user `postgres`, parool `student123` |

**Testkasutajad** (seemnefailist `backend/database/3_import.sql`, kõigi parool on `password`):

| E-post | Roll | Märkus |
|--------|------|--------|
| `john.doe@example.com` | USER | Registreerunud mitmele sündmusele |
| `admin@example.com` | ADMIN | Saab muuta/kustutada kõiki sündmusi |
| `organizer@example.com` | USER | On loonud 3 sündmust |
| `alice@example.com` | USER | |
| `bob@example.com` | USER | |

**NB!** Frontend salvestab sisselogitud kasutaja `localStorage` võtmesse `user`. Backend võtab kasutaja query-parameetrist `userId` (projektis pole JWT-d).

---

## Etapp 0 — Keskkonna ettevalmistus ja health check

```
Töötan täisstack ürituste halduse rakendusega "valitalgud" (Vue 3 frontend + Spring Boot backend + PostgreSQL).
Asukoht: /Users/a88/IdeaProjects/valitalgud

Sinu ülesanne: veendu, et testkeskkond on töökorras, ENNE kui hakkame funktsionaalsust testima.

Tee järgmist ja raporteeri iga sammu tulemus:
1. Kontrolli, kas PostgreSQL töötab ja andmebaas on seadistatud. Käivita järjekorras (kui pole juba tehtud):
   - backend/database/1_reset_database.sql
   - backend/database/2_create.sql
   - backend/database/3_import.sql
   Ühendus: host localhost, db postgres, user postgres, parool student123.
   Verifitseeri päringuga: SELECT u.id, u.status, c.email FROM users u JOIN contacts c ON c.user_id=u.id ORDER BY u.id;
   Oodatud: 5 kasutajat, kõik status=ACTIVE.
2. Kontrolli, et pordid 8080 ja 8081 on vabad VÕI neil jookseb täpselt üks instants:
   lsof -nP -iTCP:8080,8081 | grep LISTEN
   Kui on mitu Java või node protsessi, tapa kõik (lsof -ti:8080,8081 | xargs kill) ja käivita uuesti.
3. Käivita backend: cd backend && ./gradlew bootRun (taustal). Oota kuni "Started ValitalgudApplication".
4. Käivita frontend: cd frontend && npm run dev (taustal).
5. Health check:
   - curl http://localhost:8080/actuator/health → oodatud {"status":"UP"}
   - curl http://localhost:8080/api/cities → oodatud JSON massiiv linnadega (Tallinn, Tartu, Pärnu)
   - Ava brauseris http://localhost:8081/ → oodatud LandingPage "Welcome to the Event Management App!"

Raporteeri: mis töötab, mis ei tööta. Kui midagi ei käivitu, näita logi ja paku lahendust. ÄRA jätka teiste testidega enne kui kõik 5 punkti on rohelised.
```

---

## Etapp 1 — Backend API suitsutestid (Swagger / curl)

```
Töötan rakendusega "valitalgud" (/Users/a88/IdeaProjects/valitalgud). Backend jookseb http://localhost:8080, Swagger on http://localhost:8080/swagger-ui.html.

Sinu ülesanne: testi KÕIK backend endpointid läbi curl'iga (või Swagger UI kaudu). See on suitsutest — kontrollime, et iga endpoint vastab õige HTTP staatusega ja mõistliku kehaga, sõltumata frontendist.

Testkasutajad (parool kõigil "password"): john.doe@example.com (USER, id ilmselt 1), admin@example.com (ADMIN, id 2), organizer@example.com (USER, id 3).

Testi need endpointid ja raporteeri iga kohta HTTP staatus + kas vastus on mõistlik:

GET (avalikud / lugemine):
1. GET /api/cities → 200, linnade massiiv
2. GET /api/skill-tags → 200, tagide massiiv
3. GET /api/products → 200, toodete massiiv
4. GET /api/events → 200, sündmuste massiiv (ainult is_cancelled=false)
5. GET /api/events?cityId=1 → 200, filtreeritud Tallinna järgi
6. GET /api/events/1?userId=1 → 200, sündmuse detailid
7. GET /api/events/1/edit?userId=1 → 200, vormi eeltäitmise andmed (sh skillTagIds, isCancelled)
8. GET /api/my-events?userId=1&filter=ALL_FUTURE → 200, massiiv (võib olla tühi [])
9. GET /api/my-organized-events?userId=3 → 200, organizer'i 3 sündmust
10. GET /api/calendar?userId=1&month=10&year=2026 → 200
11. GET /api/profile/1 → 200, profiili andmed

POST (kirjutamine):
12. POST /api/login {"email":"john.doe@example.com","password":"password"} → 200, {userId, firstName, role...}
13. POST /api/events?userId=1 koos kehtiva CreateEventDto-ga (kuupäev tulevikus, endTime>startTime, maxParticipants>0, kehtiv cityId) → 201, EventResponseDto + Location päis

Märgi iga endpoint ✓ või ✗. Kui mõni vastab valesti, näita täpne curl käsk, oodatud vs tegelik vastus. Kogu raport alla 300 sõna.
```

---

## Etapp 2 — Autentimine (Register + Login)

```
Töötan rakendusega "valitalgud". Frontend: http://localhost:8081, backend: http://localhost:8080.
Kasuta brauseri automatiseerimist (Claude in Chrome) frontendi testimiseks.

Sinu ülesanne: testi sisselogimist ja registreerimist brauseris.

GOLDEN PATH:
1. Ava http://localhost:8081/login
2. Logi sisse: john.doe@example.com / password → oodatud: suunatakse /events lehele, navbar muutub (Logout nähtav)
3. Kontrolli DevTools → Application → Local Storage → võti "user" sisaldab {userId, firstName, lastName, role}
4. Logout navbar-ist → suunatakse /login lehele, localStorage "user" võti kustub
5. Mine /register, loo uus kasutaja unikaalse e-postiga (nt test.kasutaja+RANDOM@example.com), parool min 8 tähemärki, korda parooli → oodatud: 201, suunatakse /login lehele
6. Logi sisse äsja loodud kasutajaga → peab õnnestuma

SERVAJUHUD (oodatud veateated, MITTE crash):
7. Login vale parooliga (john.doe@example.com / vale) → "Vale email või parool"
8. Login tühjade väljadega → "Palun täitke kõik väljad"
9. Login olematu e-postiga → "Vale email või parool"
10. Register juba olemasoleva e-postiga (john.doe@example.com) → "See e-post on juba kasutusel"
11. Register liiga lühikese parooliga (<8) → veateade
12. Register mittekattuvate paroolidega → veateade

NB! Brauseri Password Manager võib parooli "password" autofillida valesti — kirjuta paroolid KÄSITSI, vajadusel kasuta Incognito akent.

Raporteeri iga punkti tulemus (✓/✗) ja screenshot ebaõnnestumistest.
```

---

## Etapp 3 — Avaleht ja navigatsioon

```
Töötan rakendusega "valitalgud". Frontend: http://localhost:8081. Kasuta brauseri automatiseerimist.

Sinu ülesanne: testi avalehte ja navigatsiooniriba.

1. Ava http://localhost:8081/ (väljalogituna — kustuta localStorage "user" kui vaja)
   → oodatud: LandingPage pealkirjaga "Welcome to the Event Management App!" + 3 demo sündmuse kaarti (TECH/JAZZ/SPORT), mis EI OLE klõpsatavad
2. Kontrolli, et navbar on nähtav (Homepage, Shop, jne)
3. Klõpsa navbar "Homepage" → jääb / lehele (ei suuna /login)
4. Klõpsa navbar "Shop" → /shop ShopView avaneb (avalik, ei nõua login)
5. Logi sisse (john.doe@example.com / password), naase avalehele "/" → LandingPage on endiselt avatud (EI suuna automaatselt mujale)
6. Testi navbar lingid sisselogituna: Events → /events, My Events → /my-events, Calendar → /calendar, Profile → /profile, Cart → /cart
7. Proovi URL-i otse: http://localhost:8081/events väljalogituna → oodatud: suunatakse /unauthorized ("Event list can't be viewed...")

Raporteeri iga lingi/route tulemus. Märgi kui mõni navbar nupp viib valesse kohta või katki.
```

---

## Etapp 4 — Sündmuste sirvimine ja detailvaade

```
Töötan rakendusega "valitalgud". Frontend: http://localhost:8081. Kasuta brauseri automatiseerimist.
Logi esmalt sisse: john.doe@example.com / password.

Sinu ülesanne: testi sündmuste loendit (EventsView) ja detailvaadet (EventDetailsView).

EventsView (/events):
1. Ava /events → oodatud: sündmuste kaardid (seemnes on 3: Tehnoloogiakonverents, Maraton, Jazz Festival)
2. Iga kaart näitab: pealkiri, kuupäev, linn, kirjeldus, tagid, osalejate arv
3. Testi filter "Linn" dropdown → vali Tallinn → loend filtreerub
4. Testi filter "Oskuse-tag" dropdown → vali tag → loend filtreerub
5. Testi filter "Alates kuupäevast" → vali kuupäev → loend filtreerub
6. Tühista filtrid → kõik sündmused tagasi

EventDetailsView (/events/:id):
7. Klõpsa kaardil "View Details" → /events/{id} avaneb
8. Kontrolli detailid: pealkiri, kirjeldus, organisaator, kuupäev/aeg, asukoht, osalejate arv, tagid
9. Testi registreerumist sündmusele (kui UI seda lubab): vali staatus (LÄHEB/VÕIB-OLLA/EI LÄHE) → salvesta
10. Kontrolli kommentaare: loe olemasolevaid, lisa uus kommentaar
11. Tühista registreerumine (kui UI lubab)

SERVAJUHT:
12. Ava olematu sündmus: /events/99999 → oodatud: veateade "Sündmust ei leitud", mitte crash

Raporteeri iga punkt. Eriti jälgi, kas osalejate arv ja registreerumise staatus kuvatakse õigesti.
```

---

## Etapp 5 — Sündmuse loomine (3-astmeline wizard)

```
Töötan rakendusega "valitalgud". Frontend: http://localhost:8081. Kasuta brauseri automatiseerimist.
Logi sisse: organizer@example.com / password.

Sinu ülesanne: testi 3-astmelist sündmuse loomise vormi (CreateEventView, /events/create).

GOLDEN PATH:
1. Mine /events lehele, klõpsa "Loo uus sündmus" → /events/create avaneb
2. Kontrolli, et üleval on progress stepper (1. samm Andmed / 2. samm Pilt / 3. samm Kinnitus)
3. SAMM 1 (Andmed): täida pealkiri, kirjeldus, vali linn dropdown'ist, aadress, kuupäev (TULEVIKUS), algusaeg, lõpuaeg (hilisem kui algus), max osalejaid (>0), vali 1-2 oskuse-tagi. Klõpsa "Edasi"
4. SAMM 2 (Pilt): sisesta banner-pildi URL (nt https://picsum.photos/600/300) → eelvaade peaks ilmuma. Klõpsa "Edasi"
5. SAMM 3 (Kinnitus): kontrolli, et kõik sisestatud andmed kuvatakse õigesti. Klõpsa "Loo sündmus"
6. Oodatud: suunatakse /my-events lehele, sündmus on loodud
7. Verifitseeri: mine /my-organized-events → uus sündmus on nimekirjas staatusega "Aktiivne"

SERVAJUHUD (samm 1 valideerimine peab takistama edasiminekut):
8. Proovi "Edasi" tühjade väljadega → veateade "Palun täitke kõik kohustuslikud väljad"
9. Pane lõpuaeg varasemaks kui algusaeg → veateade "Lõpuaeg peab olema hilisem kui algusaeg"
10. Pane kuupäev minevikku ja jõua sammu 3 → "Loo sündmus" → oodatud: backend tagastab vea, suunatakse tagasi sammu 1 veateatega "Sündmuse kuupäev peab olema tulevikus"
11. Testi "Tagasi" nuppe sammude vahel — andmed peavad säilima

Raporteeri iga samm. Jälgi eriti, kas progress stepper näitab õiget sammu ja kas andmed säilivad sammude vahel.
```

---

## Etapp 6 — Sündmuse muutmine ja tühistamine

```
Töötan rakendusega "valitalgud". Frontend: http://localhost:8081. Kasuta brauseri automatiseerimist.
Logi sisse: organizer@example.com / password (see kasutaja on loonud sündmusi).

Sinu ülesanne: testi sündmuse muutmist (EditEventView) ja tühistamist.

GOLDEN PATH (muutmine):
1. Mine /my-organized-events → klõpsa ühe sündmuse "Muuda" nupul → /events/{id}/edit avaneb
2. Kontrolli, et vorm on EELTÄIDETUD olemasolevate andmetega
3. Kontrolli, et "Linn" dropdown on disabled (ei saa muuta)
4. Muuda pealkirja ja kirjeldust → klõpsa "Salvesta"
5. Oodatud: suunatakse /my-events, muudatus salvestatud
6. Verifitseeri /my-organized-events → pealkiri muutunud

GOLDEN PATH (tühistamine):
7. Ava mõni sündmus muutmiseks → klõpsa "Tühista sündmus" → kinnitusmodaal avaneb
8. Klõpsa "Jah, tühista" → oodatud: suunatakse /my-events
9. Verifitseeri /my-organized-events → sündmuse staatus on nüüd "Tühistatud" (punane badge)
10. Verifitseeri /events (avalik loend) → tühistatud sündmust EI kuvata

SERVAJUHUD:
11. Muuda max osalejaid arvuks, mis on VÄIKSEM kui hetkel registreerunute arv → "Salvesta" → oodatud veateade "Maksimaalne osalejate arv ei saa olla väiksem kui registreerunute arv"
12. Õiguste test: logi sisse alice@example.com / password, proovi otse URL-i /events/{organizer'i sündmuse id}/edit ja salvestada → oodatud: 403, veateade "Teil pole õigust seda sündmust muuta"
13. ADMIN test: logi sisse admin@example.com / password, proovi muuta võõra kasutaja sündmust → peab ÕNNESTUMA (ADMIN tohib)

Raporteeri iga punkt + screenshot ebaõnnestumistest.
```

---

## Etapp 7 — Minu sündmused (registreerumised)

```
Töötan rakendusega "valitalgud". Frontend: http://localhost:8081. Kasuta brauseri automatiseerimist.
Logi sisse: john.doe@example.com / password (see kasutaja on registreerunud mitmele sündmusele).

Sinu ülesanne: testi MyEventsView (/my-events) — sündmused, millele kasutaja on registreerunud.

1. Ava /my-events → oodatud: kaardid registreerunud sündmustega + registreerumise staatuse badge (LÄHEB roheline / VÕIB-OLLA kollane / EI LÄHE hall)
2. Testi 3 tab'i:
   - "Sel nädalal" (THIS_WEEK) → ainult selle nädala sündmused
   - "Tulevased" (UPCOMING) → ainult homsest alates
   - "Kõik" (ALL_FUTURE) → kõik tänasest alates
3. Kontrolli, et MÖÖDUNUD sündmusi EI kuvata üheski tab-is
4. Kontrolli, et TÜHISTATUD sündmusi EI kuvata
5. Klõpsa "View event" mõnel kaardil → suunab /events/{id} detailvaatesse
6. Klõpsa "Minu loodud sündmused" nuppu (paremal üleval) → suunab /my-organized-events lehele

SERVAJUHT:
7. Logi sisse kasutajaga, kes pole ühelegi sündmusele registreerunud (nt äsja loodud test-kasutaja) → oodatud: "Sa pole veel registreerunud ühelegi sündmusele" (tühi olek, mitte crash)

NB! Tänane kuupäev on oluline — kui seemnes olevad sündmused on minevikus, võivad kõik tab'id olla tühjad. Sel juhul loo esmalt tulevikukuupäevaga sündmus ja registreeru sellele (Etapp 5 + 4), siis testi siin.

Raporteeri iga tab'i tulemus ja kas filtreerimine töötab õigesti.
```

---

## Etapp 8 — Minu loodud sündmused (haldus)

```
Töötan rakendusega "valitalgud". Frontend: http://localhost:8081. Kasuta brauseri automatiseerimist.
Logi sisse: organizer@example.com / password.

Sinu ülesanne: testi MyOrganizedEventsView (/my-organized-events) — kasutaja loodud sündmuste haldustabel.

1. Ava /my-organized-events → oodatud: TABEL veergudega Pealkiri / Kuupäev / Linn / Staatus / Osalejaid / Tegevused
2. Kontrolli, et näidatakse AINULT selle kasutaja loodud sündmusi
3. Kontrolli staatuse badge värve: Aktiivne (roheline), Lõppenud (hall), Tühistatud (punane)
4. Kontrolli osalejate vormingut: "25/100" (current/max) või ainult arv kui max=null
5. Testi filtreid (linn dropdown, oskuse-tag dropdown, kuupäev) + "Filtreeri" nupp → tabel filtreerub
6. Testi tegevusnuppe igal real:
   - "Detail" → /events/{id} detailvaade
   - "Muuda" → /events/{id}/edit vorm
   - "Kustuta sündmus" → kinnitusmodaal → "Jah, kustuta" → sündmus tühistatakse (is_cancelled=true), tabel värskendub, staatus muutub "Tühistatud"
7. Klõpsa "Loo uus sündmus" → /events/create

SERVAJUHT:
8. Logi sisse kasutajaga, kes pole ühtegi sündmust loonud (nt alice@example.com) → "Sa pole veel ühtegi sündmust loonud" (tühi olek)

Raporteeri iga punkt. Jälgi eriti, et filtrid saadavad cityId/skillTagId (mitte nimesid) ja et staatus arvutatakse õigesti.
```

---

## Etapp 9 — E-pood, ostukorv, checkout

```
Töötan rakendusega "valitalgud". Frontend: http://localhost:8081. Kasuta brauseri automatiseerimist.
E-pood on AVALIK — ei nõua sisselogimist.

Sinu ülesanne: testi e-poe voogu (ShopView → CartView → CheckoutView → OrderSuccessView).

GOLDEN PATH:
1. Ava /shop (väljalogituna) → oodatud: toodete kaardid (seemnes 5: Water Bottle, Scarf jne)
2. Klõpsa toote "Details" → küljepaneel avaneb tootedetailidega, kogusevalikuga (+/-)
3. Lisa toode ostukorvi (Add to Cart) → kinnitusteade "Toode lisatud ostukorvi!"
4. Lisa veel paar erinevat toodet
5. Ava /cart → oodatud: ostukorvi read koos kogus, hind, reasumma
6. Muuda mõne toote kogust ostukorvis → summa uueneb
7. Eemalda toode ostukorvist → rida kaob
8. Klõpsa checkout → /checkout vorm avaneb
9. Täida arve-andmed (nimi, aadress, e-post jne) → esita tellimus
10. Oodatud: 201, suunatakse /order-success lehele tellimuse kinnitusega

SERVAJUHUD:
11. Proovi checkout tühja ostukorviga → veateade "Ostukorv on tühi"
12. Esita tellimus puuduvate kohustuslike väljadega → veateade
13. Lisa ostukorvi rohkem tooteid kui laos on → veateade "Laos pole piisavalt tooteid" (kui valideerimine olemas)

NB! Ostukorv hoitakse localStorage võtmes "cart". Kontrolli, et see püsib lehe värskendamisel.

Raporteeri iga samm ja kas summad arvutatakse õigesti.
```

---

## Etapp 10 — Kalender

```
Töötan rakendusega "valitalgud". Frontend: http://localhost:8081. Kasuta brauseri automatiseerimist.
Logi sisse: john.doe@example.com / password.

Sinu ülesanne: testi CalendarView (/calendar).

1. Ava /calendar → oodatud: kuukalender, mille päevadel on sündmused, on visuaalselt esile tõstetud
2. Liigu kuude vahel (eelmine/järgmine kuu nupud) → kalender uueneb, GET /api/calendar kutsutakse õige month/year parameetriga
3. Klõpsa päeval, millel on sündmus → kuvatakse selle päeva sündmuste loend (GET /api/calendar/day)
4. Klõpsa sündmusel → suunab /events/{id} detailvaatesse
5. Kontrolli, et TÜHISTATUD sündmusi kalendris ei näidata

SERVAJUHT:
6. Liigu kuule, millel pole ühtegi sündmust → kalender kuvatakse tühjana, ilma crash'ita

Raporteeri: kas kalender renderdub, kas kuude vahetamine töötab, kas päeva-vaade näitab õigeid sündmusi.
```

---

## Etapp 11 — Profiil

```
Töötan rakendusega "valitalgud". Frontend: http://localhost:8081. Kasuta brauseri automatiseerimist.
Logi sisse: john.doe@example.com / password.

Sinu ülesanne: testi ProfileView (/profile).

1. Ava /profile → oodatud: kasutaja andmed (nimi, e-post, telefon jne) on kuvatud
2. Muuda profiili välju (nt telefon, kirjeldus) → salvesta → oodatud: muudatus salvestatud, kinnitusteade
3. Värskenda leht → muudatus püsib
4. Testi parooli muutmist (kui UI olemas): vana parool + uus parool → salvesta
5. Logi välja ja sisse uue parooliga → peab õnnestuma (TAASTA seejärel parool "password" tagasi, et teised testid töötaksid)

SERVAJUHUD:
6. Salvesta profiil kohustusliku välja tühjana → veateade
7. Parooli muutmine vale vana parooliga → veateade

NB! Kui muudad parooli, TAASTA see lõpus "password" peale tagasi (UPDATE users SET password='password' WHERE id=1), muidu järgnevad testid ja teised agendid ei saa john.doe'ga sisse.

Raporteeri iga punkt.
```

---

## Etapp 12 — Veahaldus ja õiguste kontroll (negatiivsed testid)

```
Töötan rakendusega "valitalgud". Backend: http://localhost:8080, frontend: http://localhost:8081.

Sinu ülesanne: testi süsteemi veahaldust ja õiguste kontrolli. Need on NEGATIIVSED testid — kontrollime, et süsteem käitub vigade korral korrektselt (õige HTTP staatus + ApiError keha {code, message}), mitte ei kuku kokku.

Testi curl'iga (backend) JA brauseris (frontend reaktsioon):

AUTENTIMINE:
1. POST /api/login tühja kehaga → 400 MISSING_CREDENTIALS
2. POST /api/login vale parooliga → 401 INVALID_CREDENTIALS
3. GET /api/events/1 ILMA userId parameetrita → 401 NOT_AUTHENTICATED (või Spring 400, kui param required)

ÕIGUSED:
4. PUT /api/events/{id}?userId={võõras} → 403 NOT_EVENT_OWNER
5. DELETE /api/events/{id}?userId={võõras} → 403 NOT_EVENT_OWNER
6. ADMIN (userId=2) tohib muuta võõrast sündmust → 200

OLEMATUD RESSURSID:
7. GET /api/events/99999?userId=1 → 404 EVENT_NOT_FOUND
8. PUT /api/events/99999?userId=1 → 404 EVENT_NOT_FOUND
9. DELETE /api/events/99999?userId=1 → 404 EVENT_NOT_FOUND

VALIDEERIMINE:
10. POST /api/events?userId=1 minevikukuupäevaga → 400 INVALID_EVENT_DATE
11. POST /api/events?userId=1 endTime<=startTime → 400 INVALID_EVENT_TIME_RANGE
12. POST /api/events?userId=1 maxParticipants=0 → 400 INVALID_PARTICIPANTS_COUNT
13. POST /api/events?userId=1 olematu cityId → 400 CITY_NOT_FOUND
14. POST /api/events?userId=1 olematu skillTagId → 400 SKILL_TAG_NOT_FOUND
15. GET /api/my-events?userId=1&filter=VALE → 400 INVALID_FILTER
16. GET /api/my-organized-events ILMA userId → 400 MISSING_USER_ID

FRONTEND ROUTING:
17. Brauseris tundmatu URL (/api/foobar või /xyz) → suunatakse /unauthorized või /404
18. Kaitstud route (/events) väljalogituna → /unauthorized

Kontrolli, et IGA veavastus sisaldab {code, message} keha (ApiError). Raporteeri tabelina: endpoint | oodatud | tegelik | ✓/✗.
```

---

## Etapp 13 — Regressioon / täielik kasutajateekond

```
Töötan rakendusega "valitalgud". Frontend: http://localhost:8081. Kasuta brauseri automatiseerimist.

Sinu ülesanne: läbi TÄIELIK kasutajateekond otsast lõpuni, nagu päris kasutaja. See on lõplik regressioonitest — kontrollime, et kõik osad töötavad KOOS.

Salvesta kogu teekond GIF-ina (gif_creator), et saaks üle vaadata.

TEEKOND:
1. Ava avaleht / (väljalogituna) → näe demo sündmusi
2. Registreeri uus kasutaja /register kaudu (unikaalne e-post)
3. Logi sisse äsja loodud kasutajaga
4. Sirvi sündmusi /events, filtreeri linna järgi
5. Ava sündmuse detail, registreeru sellele (LÄHEB)
6. Loo OMA uus sündmus (3-astmeline wizard, tulevikukuupäev)
7. Vaata /my-organized-events → oma loodud sündmus on seal "Aktiivne"
8. Muuda oma sündmust (pealkiri)
9. Vaata /my-events → registreerunud sündmus on seal õige staatusega
10. Külasta e-poodi /shop, lisa toode ostukorvi
11. Vaata ostukorvi /cart, tee checkout, esita tellimus → /order-success
12. Vaata kalendrit /calendar
13. Uuenda profiili /profile
14. Tühista oma loodud sündmus (/my-organized-events → Kustuta)
15. Logout

Pärast teekonda raporteeri:
- Kas kõik 15 sammu õnnestusid? (✓/✗ iga samm)
- Kas mõni samm crashis, näitas valet andmeid või suunas valesse kohta?
- Kas localStorage olek (user, cart) püsis õigesti läbi teekonna?
- Üldhinnang: kas rakendus on demo-valmis?

Lisaks: jälgi kogu teekonna jooksul brauseri Console't (read_console_messages) — raporteeri KÕIK JS errorid või võrgu (4xx/5xx) vead, mis tekkisid.
```

---

## Raporti formaat (kõikide etappide jaoks)

Iga agent peaks lõpetama etapi raportiga:

```
## Etapp N raport
- Testitud: <mitu punkti>
- Õnnestus: <N> ✓
- Ebaõnnestus: <N> ✗
- Blokeerivad probleemid: <nimekiri kriitilistest vigadest>
- Mittekriitilised tähelepanekud: <nimekiri>
- Screenshotid/GIF-id: <failinimed kui tehtud>
- Soovitus: kas võib järgmise etapi juurde liikuda?
```

---

## Märkused testijatele

- **Andmebaasi seis on oluline.** Mõned testid eeldavad tulevikukuupäevaga sündmusi. Seemnes (`3_import.sql`) olevad sündmused on kuupäevadega 2023, seega minevikus. Kui MyEvents/Calendar/Events tunduvad tühjad, loo esmalt tulevikukuupäevaga sündmusi.
- **Üks instants korraga.** Kui näed ootamatut käitumist (nt ACCOUNT_BLOCKED kuigi DB ütleb ACTIVE), kontrolli, et jookseb täpselt üks backend ja üks frontend (`lsof -nP -iTCP:8080,8081 | grep LISTEN`). Mitu instantsi serveerivad stale koodi/andmeid.
- **Password Manager.** Chrome võib parooli "password" autofillida valesti. Kirjuta paroolid käsitsi või kasuta Incognito.
- **Pärast destruktiivseid teste taasta seis.** Kui kustutad/muudad seemneandmeid, jooksuta `1_reset → 2_create → 3_import` uuesti, et järgmised etapid alustaksid puhtalt.
- **localStorage.** Frontend hoiab `user` (auth) ja `cart` (ostukorv) localStorage-is. Väljalogimise testimiseks kustuta `user` võti.
