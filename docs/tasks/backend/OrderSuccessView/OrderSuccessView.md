# OrderSuccessView — Eduka tellimuse kinnitus

**Vaade:** `OrderSuccessView.vue`
**Route:** `/order-success`
**Tüüp:** Backend
**Staatus:** To Do

## Kontekst

Eduka tellimuse kinnitusleht, mis kuvatakse pärast `CheckoutView` lehelt edukat tellimuse esitamist (`POST /api/orders` → 201 Created). Lehel kuvatakse ainult tervitustekst "Palju õnne eduka ostu puhul!" ning header navigatsioon. Leht on **avalik** (autentimist ei nõuta), kuid kui kasutaja jõuab siia ilma tellimust esitamata (st URL-i kaudu otse), suunatakse ta `/shop` lehele.

## Mocki vaade

![OrderSuccessView mock](../../../mock%20pildid/OrderSuccessView.vue.png)

## Backend tegevus

**Backend endpointe ei vaja** — vaade kuvab ainult staatilist sisu (õnnitlustekst) ja dünaamilist navigatsiooniheaderit. Tellimuse loomine toimub eelnevalt `CheckoutView` lehel (`POST /api/orders`); siia jõudes on tellimus juba salvestatud.

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

Otsest andmebaasi pöördumist sellelt vaatelt ei tehta. Tellimus on juba salvestatud `orders`, `order_items` ja `billings` tabelitesse `CheckoutView` lehel.

## Navigatsioon (frontend kontekst)

| Tegevus | Sihtkoht |
|---------|----------|
| Kui kasutaja jõuab siia ilma tellimust esitamata (nt otse URL-i kaudu) | `redirect → /shop` (`ShopView.vue`) |
| `vue-router` state | `router.push({ name: 'order-success', state: { orderId } })` — `orderId` edastatakse `CheckoutView`-st |
| Nupp "Jätka ostlemist" | `/shop` (`ShopView.vue`) |
| Nupp "Avalehele" | `/` (`LandingPage.vue`) |

**Header (dünaamiline):**

| Sisselogitud | Väljalogitud |
|--------------|--------------|
| `Homepage \| Profile \| Events \| My Events \| Calendar \| Logout \| Cart` | `Logi sisse \| Loo kasutaja \| Events \| E-pood \| Cart` |

> Eeldus: leht on tehniliselt avalik (ei nõua autentimist), kuna `CheckoutView` viib siia suunamise kaudu ja ostu saab teha ka külalisena, kui projekt seda lubab. Kui ärinõue on, et ainult sisse logitud kasutajad pääsevad ligi, tuleb lisada `router.beforeEnter` valvur.

## Eesmärk

Anda kasutajale visuaalne kinnitus, et tellimus läks läbi.

## Vastuvõtu kriteeriumid

- [ ] **Backend muudatusi pole vaja**
- [ ] Vaade kuvab teksti "Palju õnne eduka ostu puhul!"
- [ ] Header kuvatakse dünaamiliselt vastavalt sisselogimisstaatusele
- [ ] Otse URL-i (`/order-success`) kaudu jõudmisel ilma `state.orderId`-ta toimub suunamine `/shop` lehele
- [ ] Navigatsiooninupud (Avalehele, Jätka ostlemist) töötavad
