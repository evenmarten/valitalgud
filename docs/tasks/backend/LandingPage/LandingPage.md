# LandingPage — Avaleht

**Vaade:** `LandingPage.vue`
**Route:** `/`
**Tüüp:** Backend
**Staatus:** To Do

## Kontekst

Avaleht on rakenduse esimene vaade, mis on avalik ja ei nõua autentimist. Sisselogitud kasutaja suunatakse automaatselt edasi ürituste lehele (`/events`). Avaleht tutvustab rakendust ning pakub sissepääsu voogu: sündmuste haldus (nõuab kontot) ning e-pood (avalik). Header/menüü kuvatakse dünaamiliselt — sisselogitud kasutajale näidatakse "Profile" / "Logout", välja logitud kasutajale "Logi sisse" / "Loo kasutaja". Nupud "E-pood" ja "Cart" on ALATI nähtavad (ka väljalogitud kasutajatele).

## Mocki vaade

![LandingPage mock](../../../mock%20pildid/LandingPage.vue.png)

## Endpointide ülevaade

LandingPage on puhtalt frontendi vaade ja **ei kasuta otseseid backend endpointe**. Vaade kuvab staatilist sisu (tervitustekst, navigatsiooninupud) ning suunab kasutaja edasi teistele vaadetele.

| # | Meetod | Tee | Otstarve |
|---|--------|-----|----------|
| — | — | — | Backend endpoint ei ole nõutud |

> **Eeldus:** Kuna pildi tekst märgib selgelt "API: N/A", "DTO: N/A", "Response: N/A" ja "Veaeelsed: N/A", siis see vaade ei vaja eraldi backend tööd. Selle taski raames veendutakse vaid, et CORS lubaks frontendi pärida muid endpointe ning et avalehe pärimine ei kutsu esile turvarakke (avalik route, ei vaja JWT-d).

## Veahaldus

| Olukord | Exception klass | ErrorResponse enum | HTTP staatus | Sõnum kasutajale |
|---------|-----------------|--------------------|--------------|------------------|
| Veaolukordi ei ole (vaade on staatiline) | — | — | — | — |

> **Märkus veahalduse kohta:**
> Kontrolli, kas vajalikud `ErrorResponse` enum kirjed ja exception klassid juba eksisteerivad:
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/error/ErrorResponse.java`
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/exception/`
>
> Puuduvate enum kirjete puhul lisa need `ErrorResponse`-i. Puuduvate exception klasside puhul loo uus klass `exception/` paketti ja registreeri see `RestExceptionHandler`-is.

## Andmebaas

LandingPage ei tee ühtegi andmebaasipäringut. Andmebaasi tabeleid otseselt ei puudutata.

> Loogika: kui kasutaja on juba sisse logitud (frontendis `localStorage` sisaldab `userId`), suunab Vue Router ta automaatselt `/events` lehele. Vastasel juhul jääb kasutaja avalehele.

## Navigatsioon (frontend kontekst)

| Tegevus | Sihtkoht | Tingimus |
|---------|----------|----------|
| Avalehe avamine sisselogitud kasutajana | `redirect → /events` | `localStorage.userId` olemas |
| Nupp "Logi sisse" | `/login` (`LoginView.vue`) | Kui pole sisse logitud |
| Nupp "Loo kasutaja" | `/register` (`RegisterView.vue`) | Kui pole sisse logitud |
| Nupp "E-pood" (alati nähtav) | `/shop` (`ShopView.vue`) | Alati |
| Nupp "Events" (alati nähtav) | `/events` (`EventsView.vue`) | Alati |

> **Eeldus:** "Events" nupp on alati nähtav, kuid kui kasutaja ei ole sisse logitud, peatab Vue Router guard ta enne `EventsView`-le jõudmist ning suunab `/unauthorized` lehele. E-pood on aga täielikult avalik, kus ka registreerimata kasutaja saab esemeid sirvida ja osta.

## Vastuvõtu kriteeriumid

- [ ] LandingPage on saadaval avaliku route `/` kaudu, ilma autentimiseta
- [ ] Backend ei pea selle vaate jaoks lisama uusi endpointe
- [ ] Backend CORS konfiguratsioon lubab frontendi serveri päringuid (sh avalehe staatilist sisu)
- [ ] Backend-i healthcheck endpoint (kui olemas) töötab ja annab teada, kas server jookseb
- [ ] Vue Router guard suunab sisselogitud kasutaja automaatselt `/events` lehele (frontendi ülesanne, kuid backend ei peata)
- [ ] LandingPage on Swagger dokumentatsioonis selgelt eristatud kui mitte-backendi vaade (kommentaar koodis või task dokumendis)
