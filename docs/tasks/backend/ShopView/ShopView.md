# ShopView — E-poe tooted

**Vaade:** `ShopView.vue`
**Route:** `/shop`
**Tüüp:** Backend
**Staatus:** To Do

## Kontekst

E-poe avavaade kuvab toodete ruudustiku (`Our Products`). Iga toote kaart sisaldab pilti, nime, hinda ning nuppe `Add to Cart` ja `Details`. `Details` nupule klõpsates avaneb külgpaneel (`Product Details`), kus on toote täielik kirjeldus, koguse valija ja `Add to Cart` nupp. Backend tagastab kõigi saadaolevate toodete loetelu (`stock_quantity > 0`) ja üksiku toote detailid.

## Mocki vaade

![ShopView mock](../../../mock%20pildid/ShopView.vue.png)

## Endpointide ülevaade

| # | Meetod | Tee | Otstarve |
|---|--------|-----|----------|
| 1 | `GET` | `/api/products` | Tagasta kõik saadavalolevad tooted (toodete ruudustiku jaoks) |
| 2 | `GET` | `/api/products/{productId}` | Tagasta üksiku toote detailid (külgpaneeli jaoks) |
| 3 | `POST` | `/api/cart/items` | Lisa toode sisseloginud kasutaja ostukorvi |

---

## 1. `GET /api/products`

**Kontroller:** `ProductController.java`
**Auth:** Ei (avalik endpoint)

### Response Body — `List<ProductResponseDto>`

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `productId` | `Integer` | `products.id` |
| `name` | `String` | `products.name` |
| `price` | `BigDecimal` | `products.price` |
| `imageUrl` | `String` | `products.image_url` (võib olla `null`) |

**Näidis:**
```json
[
  {
    "productId": 1,
    "name": "Water Bottle",
    "price": 15.99,
    "imageUrl": "/images/products/water-bottle.png"
  },
  {
    "productId": 2,
    "name": "Scarf",
    "price": 25.00,
    "imageUrl": "/images/products/scarf.png"
  }
]
```

> Eeldus: tagastatakse ainult tooted, kus `stock_quantity > 0`. Kui soovitakse kuvada ka otsas olevaid tooteid, võib lisada query parameetri `includeOutOfStock=true`.

---

## 2. `GET /api/products/{productId}`

**Kontroller:** `ProductController.java`
**Auth:** Ei (avalik endpoint)

### Response Body — `ProductDetailsResponseDto.java`

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `productId` | `Integer` | `products.id` |
| `name` | `String` | `products.name` |
| `description` | `String` | `products.description` (võib olla `null`) |
| `price` | `BigDecimal` | `products.price` |
| `imageUrl` | `String` | `products.image_url` (võib olla `null`) |
| `stockQuantity` | `Integer` | `products.stock_quantity` |

**Näidis:**
```json
{
  "productId": 1,
  "name": "Generic Product Name",
  "description": "A short description of the product. Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
  "price": 15.99,
  "imageUrl": "/images/products/water-bottle.png",
  "stockQuantity": 42
}
```

---

## 3. `POST /api/cart/items`

**Kontroller:** `CartController.java`
**Auth:** Jah (sisseloginud kasutaja `userId` võetakse päringust)

### Request Body — `AddCartItemDto.java`

| Väli | Tüüp | Kohustuslik | Kirjeldus |
|------|------|-------------|-----------|
| `userId` | `Integer` | Jah | Sisseloginud kasutaja ID |
| `productId` | `Integer` | Jah | Lisatava toote ID |
| `quantity` | `Integer` | Jah | Kogus (peab olema `> 0` ja `<= stock_quantity`) |

**Näidis:**
```json
{
  "userId": 1,
  "productId": 1,
  "quantity": 2
}
```

### Response Body — `CartItemResponseDto.java`

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `cartItemId` | `Integer` | `cart_items.id` |
| `cartId` | `Integer` | `cart_items.cart_id` |
| `productId` | `Integer` | `cart_items.product_id` |
| `quantity` | `Integer` | `cart_items.quantity` |

**Näidis:**
```json
{
  "cartItemId": 10,
  "cartId": 3,
  "productId": 1,
  "quantity": 2
}
```

> Loogika: kui kasutajal pole veel ostukorvi (`carts.user_id`), siis loo uus. Kui `cart_items` juba sisaldab sama `(cart_id, product_id)` kombinatsiooni (UNIQUE constraint `AK_7`), siis suurenda olemasolevat `quantity` väärtust, mitte ära viska 409.

## Veahaldus

| Olukord | Exception klass | ErrorResponse enum | HTTP staatus | Sõnum kasutajale |
|---------|-----------------|--------------------|--------------|------------------|
| Toodet ei leitud | `NotFoundException` | `PRODUCT_NOT_FOUND` | 404 | "Toodet ei leitud" |
| `quantity <= 0` | `BadRequestException` | `INVALID_QUANTITY` | 400 | "Kogus peab olema suurem kui null" |
| `quantity > stock_quantity` | `BadRequestException` | `INSUFFICIENT_STOCK` | 400 | "Ladus pole piisavalt toodet" |
| Kasutaja pole sisselogitud | `UnauthorizedException` | `NOT_AUTHENTICATED` | 401 | "Palun logige sisse" |
| Puuduv `userId` või `productId` | `BadRequestException` | `MISSING_FIELDS` | 400 | "Palun täitke kõik kohustuslikud väljad" |

> **Märkus veahalduse kohta:**
> Kontrolli, kas vajalikud `ErrorResponse` enum kirjed ja exception klassid juba eksisteerivad:
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/error/ErrorResponse.java`
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/exception/`
>
> Puuduvate enum kirjete puhul lisa need `ErrorResponse`-i. Puuduvate exception klasside puhul loo uus klass `exception/` paketti ja registreeri see `RestExceptionHandler`-is.

## Andmebaas

Seotud tabelid: `products`, `carts`, `cart_items`, `users`

- `products.id` — toote identifikaator
- `products.name`, `products.description`, `products.price`, `products.image_url` — kuvatakse vaates
- `products.stock_quantity` — ainult `> 0` tooteid tagastatakse (eeldus); kasutatakse ka koguse valideerimisel
- `carts.user_id` (UNIQUE) — iga kasutaja kohta üks ostukorv; luuakse automaatselt esimese `Add to Cart` tegevuse käigus
- `cart_items.cart_id` → `carts.id` — ostukorvi sisu
- `cart_items.product_id` → `products.id` — viide tootele
- `cart_items.quantity` — peab olema `> 0` (DB `CHECK_5`)
- `cart_items` UNIQUE `(cart_id, product_id)` — sama toote uuesti lisamisel suurenda olemasolevat `quantity`-t

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
| Toote kaardi nupp `Add to Cart` | kutsub `POST /api/cart/items` `quantity = 1`-ga |
| Toote kaardi nupp `Details` | avab `Product Details` paneeli, kutsub `GET /api/products/{productId}` |
| Paneeli nupp `Add to Cart` | kutsub `POST /api/cart/items` valitud `quantity`-ga |

## Vastuvõtu kriteeriumid

- [ ] `GET /api/products` tagastab 200 OK koos `List<ProductResponseDto>` kehaga
- [ ] `GET /api/products/{productId}` tagastab 200 OK koos `ProductDetailsResponseDto` kehaga
- [ ] Olematu toote päring tagastab 404 `PRODUCT_NOT_FOUND`
- [ ] `POST /api/cart/items` loob ostukorvi, kui kasutajal seda veel pole
- [ ] Sama toote uuesti lisamine suurendab olemasoleva `cart_items.quantity` väärtust
- [ ] `quantity > stock_quantity` tagastab 400 `INSUFFICIENT_STOCK`
- [ ] `quantity <= 0` tagastab 400 `INVALID_QUANTITY`
- [ ] DTO klassid (`ProductResponseDto`, `ProductDetailsResponseDto`, `AddCartItemDto`, `CartItemResponseDto`) on loodud vastavalt `controller/product/dto/` ja `controller/cart/dto/` paketti
- [ ] Controller, Service, Repository kihid on eraldatud
- [ ] Kontrolleri meetoditel on `@Operation` ja `@ApiResponses` annotatsioonid (sh veavastused `ApiError` skeemiga)
- [ ] Swagger UI kaudu on endpointid nähtavad ja testitavad
