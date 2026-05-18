# CartView — Ostukorv

**Vaade:** `CartView.vue`
**Route:** `/cart`
**Tüüp:** Backend
**Staatus:** To Do

## Kontekst

Ostukorvi vaade näitab kasutaja praeguses ostukorvis olevaid tooteid (`Product`, `Price`, `Quantity`, `Total`) ning paremas paneelis `Order Summary` blokki — vahesumma, tarne, käibemaks (8%) ja kogusumma. Kasutaja saab muuta toote kogust (`+` / `-`), eemaldada toote (`x` ikoon) ning liikuda kassasse (`Proceed to Checkout`). Vaade on AVALIK ainult sisseloginud kasutajatele — kui kasutaja pole sisse logitud, suunatakse `/login` lehele.

Eeldus: tarne (`shipping`) ja käibemaksu määr (8%) arvutatakse esialgu backendis fikseeritud konstantidena (`SHIPPING = 5.00`, `TAX_RATE = 0.08`). Kui on dünaamiline tarne, peab seda eraldi modelleerima.

## Mocki vaade

![CartView mock](../../../mock%20pildid/CartView.vue.png)

## Endpointide ülevaade

| # | Meetod | Tee | Otstarve |
|---|--------|-----|----------|
| 1 | `GET` | `/api/cart` | Tagasta kasutaja ostukorv koos toodete ja kokkuvõttega |
| 2 | `PUT` | `/api/cart/items/{cartItemId}` | Uuenda ostukorvis oleva toote kogust |
| 3 | `DELETE` | `/api/cart/items/{cartItemId}` | Eemalda toode ostukorvist |

---

## 1. `GET /api/cart`

**Kontroller:** `CartController.java`
**Auth:** Jah (sisseloginud kasutaja `userId` query parameetrina)

### Päringuparameetrid

| Parameeter | Tüüp | Kohustuslik | Kirjeldus |
|------------|------|-------------|-----------|
| `userId` | `Integer` | Jah | Sisseloginud kasutaja ID |

### Response Body — `CartResponseDto.java`

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `cartId` | `Integer` | `carts.id` |
| `items` | `List<CartItemViewDto>` | (vt allpool) |
| `subtotal` | `BigDecimal` | Summeeri `price * quantity` üle kõikide ridade |
| `shipping` | `BigDecimal` | Konstant `5.00` (eeldus) |
| `tax` | `BigDecimal` | `subtotal * 0.08` (8%) |
| `total` | `BigDecimal` | `subtotal + shipping + tax` |

**`CartItemViewDto.java`:**

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `cartItemId` | `Integer` | `cart_items.id` |
| `productId` | `Integer` | `products.id` |
| `name` | `String` | `products.name` |
| `imageUrl` | `String` | `products.image_url` |
| `price` | `BigDecimal` | `products.price` |
| `quantity` | `Integer` | `cart_items.quantity` |
| `lineTotal` | `BigDecimal` | `products.price * cart_items.quantity` |

**Näidis:**
```json
{
  "cartId": 3,
  "items": [
    {
      "cartItemId": 10,
      "productId": 1,
      "name": "Water Bottle",
      "imageUrl": "/images/products/water-bottle.png",
      "price": 15.99,
      "quantity": 1,
      "lineTotal": 15.99
    },
    {
      "cartItemId": 11,
      "productId": 2,
      "name": "Scarf",
      "imageUrl": "/images/products/scarf.png",
      "price": 25.00,
      "quantity": 2,
      "lineTotal": 50.00
    },
    {
      "cartItemId": 12,
      "productId": 3,
      "name": "Gauntlets",
      "imageUrl": "/images/products/gauntlets.png",
      "price": 35.50,
      "quantity": 1,
      "lineTotal": 35.50
    }
  ],
  "subtotal": 101.49,
  "shipping": 5.00,
  "tax": 8.12,
  "total": 114.61
}
```

> Eeldus: kui kasutajal pole ostukorvi rida (puudub `carts` kirje), siis tagasta tühi ostukorv (`items: []`, `subtotal: 0`, `shipping: 0`, `tax: 0`, `total: 0`) staatusega 200 OK.

---

## 2. `PUT /api/cart/items/{cartItemId}`

**Kontroller:** `CartController.java`
**Auth:** Jah (kasutaja saab muuta ainult enda ostukorvi ridu)

### Request Body — `UpdateCartItemDto.java`

| Väli | Tüüp | Kohustuslik | Kirjeldus |
|------|------|-------------|-----------|
| `userId` | `Integer` | Jah | Sisseloginud kasutaja ID (õiguste kontrolliks) |
| `quantity` | `Integer` | Jah | Uus kogus (peab olema `> 0` ja `<= stock_quantity`) |

**Näidis:**
```json
{
  "userId": 1,
  "quantity": 3
}
```

### Response Body — `CartResponseDto.java`

Tagastatakse värskendatud kogu ostukorv (sama struktuur nagu `GET /api/cart`).

> Eeldus: kui frontend saadab `quantity = 0`, käsitleme seda kustutusena ja kutsume sama loogikat nagu `DELETE`. Alternatiivina võib see anda `400 INVALID_QUANTITY` vea ja frontend peab kasutama eraldi `DELETE` endpointi.

---

## 3. `DELETE /api/cart/items/{cartItemId}`

**Kontroller:** `CartController.java`
**Auth:** Jah (kasutaja saab eemaldada ainult enda ostukorvist)

### Päringuparameetrid

| Parameeter | Tüüp | Kohustuslik | Kirjeldus |
|------------|------|-------------|-----------|
| `cartItemId` (PathVariable) | `Integer` | Jah | Eemaldatava ostukorvi rea ID |
| `userId` (Query) | `Integer` | Jah | Sisseloginud kasutaja ID (õiguste kontrolliks) |

### Response Body

`200 OK` koos värskendatud `CartResponseDto`-ga (et frontend saaks kohe uuendatud kokkuvõtte kuvada), VÕI `204 No Content` ja frontend kutsub eraldi `GET /api/cart`-i.

> Eeldus: kasutame `200 OK` + `CartResponseDto`, et vältida lisapäringut frontendis.

## Veahaldus

| Olukord | Exception klass | ErrorResponse enum | HTTP staatus | Sõnum kasutajale |
|---------|-----------------|--------------------|--------------|------------------|
| Kasutaja pole sisselogitud | `UnauthorizedException` | `NOT_AUTHENTICATED` | 401 | "Palun logige sisse" |
| `userId` puudub | `BadRequestException` | `MISSING_USER_ID` | 400 | "Kasutaja ID on kohustuslik" |
| Ostukorvi rida ei leitud | `NotFoundException` | `CART_ITEM_NOT_FOUND` | 404 | "Ostukorvi rida ei leitud" |
| Ostukorvi rida ei kuulu kasutajale | `ForbiddenException` | `NOT_CART_OWNER` | 403 | "Teil pole õigust seda ostukorvi muuta" |
| `quantity <= 0` (kui ei käsitleta kustutusena) | `BadRequestException` | `INVALID_QUANTITY` | 400 | "Kogus peab olema suurem kui null" |
| `quantity > stock_quantity` | `BadRequestException` | `INSUFFICIENT_STOCK` | 400 | "Ladus pole piisavalt toodet" |

> **Märkus veahalduse kohta:**
> Kontrolli, kas vajalikud `ErrorResponse` enum kirjed ja exception klassid juba eksisteerivad:
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/error/ErrorResponse.java`
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/exception/`
>
> Puuduvate enum kirjete puhul lisa need `ErrorResponse`-i. Puuduvate exception klasside puhul loo uus klass `exception/` paketti ja registreeri see `RestExceptionHandler`-is.

## Andmebaas

Seotud tabelid: `carts`, `cart_items`, `products`, `users`

- `carts.user_id` (UNIQUE) → `users.id` — iga kasutaja kohta üks ostukorv
- `carts.updated_at` — uuendatakse iga `PUT` / `DELETE` / `POST cart_items` operatsiooni järel
- `cart_items.cart_id` → `carts.id` (ON DELETE CASCADE) — ostukorvi read
- `cart_items.product_id` → `products.id` (ON DELETE CASCADE) — viide tootele
- `cart_items.quantity` — peab olema `> 0` (DB `CHECK_5`); kontrolli ka `<= products.stock_quantity`
- `products.price` — kasutatakse `lineTotal`, `subtotal`, `tax`, `total` arvutamiseks
- `products.stock_quantity` — koguse valideerimise alus

> NB! `subtotal`, `tax`, `total` ei salvestata `carts` tabelisse — need arvutatakse iga `GET` päringu käigus dünaamiliselt. Tellimuse loomisel (`CheckoutView`) kantakse arvutatud väärtused üle `orders` tabelisse.

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
| Nupp `+` (rea kogus üles) | kutsub `PUT /api/cart/items/{cartItemId}` `quantity + 1`-ga |
| Nupp `-` (rea kogus alla) | kutsub `PUT /api/cart/items/{cartItemId}` `quantity - 1`-ga (kui 0, kutsub `DELETE`) |
| Nupp `x` (eemalda rida) | kutsub `DELETE /api/cart/items/{cartItemId}` |
| Nupp `Proceed to Checkout` | suunab `/checkout` (`CheckoutView.vue`) |
| Kui kasutaja pole sisselogitud | suuna `/login` (`LoginView.vue`) |
| Kui ostukorv on tühi | kuva teade "Ostukorv on tühi", peida `Proceed to Checkout` nupp |

## Vastuvõtu kriteeriumid

- [ ] `GET /api/cart?userId=...` tagastab 200 OK koos `CartResponseDto` kehaga
- [ ] Tühja ostukorvi puhul tagastatakse 200 OK koos `items: []` ja kõik kokkuvõtte väärtused = 0
- [ ] `subtotal`, `tax` (8%), `total` arvutatakse korrektselt iga päringu käigus
- [ ] `PUT /api/cart/items/{cartItemId}` uuendab kogust ja tagastab värskendatud ostukorvi
- [ ] `DELETE /api/cart/items/{cartItemId}` eemaldab rea ja tagastab värskendatud ostukorvi
- [ ] Võõra ostukorvi rea muutmise/kustutamise katse tagastab 403 `NOT_CART_OWNER`
- [ ] `quantity > stock_quantity` tagastab 400 `INSUFFICIENT_STOCK`
- [ ] `quantity <= 0` käsitletakse kas kustutusena VÕI tagastab 400 `INVALID_QUANTITY` (vali üks lähenemine ja dokumenteeri)
- [ ] `carts.updated_at` uueneb iga muudatuse järel
- [ ] DTO klassid (`CartResponseDto`, `CartItemViewDto`, `UpdateCartItemDto`) on loodud `controller/cart/dto/` paketti
- [ ] Controller, Service, Repository kihid on eraldatud
- [ ] Kontrolleri meetoditel on `@Operation` ja `@ApiResponses` annotatsioonid (sh veavastused `ApiError` skeemiga)
- [ ] Swagger UI kaudu on endpointid nähtavad ja testitavad
