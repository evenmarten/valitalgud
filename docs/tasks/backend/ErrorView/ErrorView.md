# ErrorView — Lehte ei leitud (404)

**Vaade:** `ErrorView.vue`
**Route:** `/:404` (ja `catch-all` / `pathMatch(.*)*`)
**Tüüp:** Backend
**Staatus:** To Do

## Kontekst

Vue Router'i `catch-all` vaade, mis kuvatakse, kui kasutaja navigeerib olematule URL-ile. Sisaldab pealkirja "Event Management App", teksti "Lehte ei leitud", lühikest selgitust "Vabandame, otsitud lehte ei eksisteeri." ning nuppu "Home". Leht on **avalik** ega nõua autentimist.

## Mocki vaade

![ErrorView mock](../../../mock%20pildid/ErrorView.vue.png)

## Backend tegevus

**Backend endpointe ei vaja** — vaade kuvab ainult staatilist sisu ja navigatsiooninupud. Vea käsitlemine toimub puhtalt Vue Router'i tasandil (`catch-all` route suunab kõik tundmatud URL-id siia).

## Endpointide ülevaade

Ei kohaldu — backend endpointe ei vaja.

## Veahaldus

Ei kohaldu — backend endpointe ei vaja.

> **Märkus veahalduse kohta:**
> Kontrolli, kas vajalikud `ErrorResponse` enum kirjed ja exception klassid juba eksisteerivad:
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/error/ErrorResponse.java`
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/exception/`
>
> Puuduvate enum kirjete puhul lisa need `ErrorResponse`-i. Puuduvate exception klasside puhul loo uus klass `exception/` paketti ja registreeri see `RestExceptionHandler`-is.

## Andmebaas

Otsest andmebaasi pöördumist sellelt vaatelt ei tehta.

## Navigatsioon (frontend kontekst)

| Tegevus | Sihtkoht |
|---------|----------|
| Tundmatu URL (`catch-all`) | `redirect → /404` (`ErrorView.vue`) |
| Nupp "Home" | `/` (`LandingPage.vue`) |

**Header (dünaamiline):**

| Sisselogitud | Väljalogitud |
|--------------|--------------|
| `Homepage \| Profile \| Events \| My Events \| Calendar \| Logout \| Cart` | `Logi sisse \| Loo kasutaja \| Events \| E-pood \| Cart` |

> Eeldus: vaade on alati avalik (ka sisse logimata kasutaja saab seda näha), kuna 404 võib juhtuda enne autentimist.

## Vastuvõtu kriteeriumid

- [ ] **Backend muudatusi pole vaja**
- [ ] Vue Router'i `catch-all` route suunab kõik tundmatud URL-id `ErrorView`-le
- [ ] Vaade kuvab pealkirja "Lehte ei leitud" ja selgituse "Vabandame, otsitud lehte ei eksisteeri."
- [ ] Header kuvatakse dünaamiliselt vastavalt sisselogimisstaatusele
- [ ] Nupp "Home" suunab kasutaja `/` (`LandingPage.vue`) lehele
