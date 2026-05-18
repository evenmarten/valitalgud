# CheckoutView — Arveldus ja transport

**Vaade:** `CheckoutView.vue`
**Route:** `/checkout`
**Tüüp:** Backend
**Staatus:** To Do

## Kontekst

Tellimuse vormistamise vaade e-poes. Kasutaja, kes on lisanud tooted ostukorvi, täidab arveldus- ja kohaletoimetamise andmed (eesnimi, perekonnanimi, valikuline ettevõte, riik, tänav, postiindeks, linn, telefon, e-post) ning vajutab nupule "Pay". Kui kasutaja ei ole sisse logitud või ostukorv on tühi, suunatakse ta vastavalt `/login` või `/shop` lehele. Eduka tellimuse järel suunatakse `/order-success` lehele.

## Mocki vaade

![CheckoutView mock](../../../mock%20pildid/CheckoutView.vue.png)

## Endpointide ülevaade

| # | Meetod | Tee | Otstarve |
|---|--------|-----|----------|
| 1 | `POST` | `/api/orders` | Loo uus tellimus koos arveldusandmete ja toodetega |

---

## 1. `POST /api/orders`

**Kontroller:** `OrderController.java`
**Auth:** Jah (sisse logitud kasutaja) — kui kasutaja ei ole sisse logitud, suunatakse frontendis `/login` lehele.

### Request Body — `CreateOrderDto.java`

| Väli | Tüüp | Kirjeldus |
|------|------|-----------|
| `firstName` | `String` | Arveldaja eesnimi (kohustuslik) |
| `lastName` | `String` | Arveldaja perekonnanimi (kohustuslik) |
| `companyName` | `String` | Ettevõtte nimi (valikuline) |
| `country` | `String` | Riik (kohustuslik), nt "Eesti" |
| `street` | `String` | Tänav ja majanumber (kohustuslik) |
| `postalCode` | `String` | Postiindeks (kohustuslik) |
| `city` | `String` | Linn / asula (kohustuslik) |
| `phone` | `String` | Telefoninumber (kohustuslik) |
| `email` | `String` | E-posti aadress (kohustuslik) |
| `items` | `List<OrderItemDto>` | Tellimuse read (`productId`, `quantity`) |

**Näidis:**
```json
{
  "firstName": "Mari",
  "lastName": "Maasikas",
  "companyName": "Ettevõte OÜ",
  "country": "Eesti",
  "street": "Pargi tee 5",
  "postalCode": "10115",
  "city": "Tallinn",
  "phone": "+372 555 1234",
  "email": "mari@example.com",
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 3, "quantity": 1 }
  ]
}
```

### Response Body — `OrderResponseDto.java`

| Väli | Tüüp | Allikas (DB tabel.veerg) |
|------|------|--------------------------|
| `orderId` | `Integer` | `orders.id` |
| `totalAmount` | `BigDecimal` | `orders.total` |
| `status` | `String` | `orders.status` (nt `PENDING`) |
| `createdAt` | `String` (ISO-8601) | `orders.created_at` |

**Näidis:**
```json
{
  "orderId": 101,
  "totalAmount": 114.61,
  "status": "PENDING",
  "createdAt": "2026-05-18T15:30:00"
}
```

> Frontend kasutab vastuses tagastatud `orderId`-d, et suunata kasutaja `/order-success` lehele (vue-router `state.orderId` kaudu).

## Veahaldus

| Olukord | Exception klass | ErrorResponse enum | HTTP staatus | Sõnum kasutajale |
|---------|-----------------|--------------------|--------------|------------------|
| Mõni kohustuslik väli puudub või on tühi | `BadRequestException` | `MISSING_ORDER_FIELDS` | 400 | "Palun täitke kõik kohustuslikud väljad" |
| Vigane e-posti või postiindeksi formaat | `BadRequestException` | `INVALID_ORDER_FIELD_FORMAT` | 400 | "Mõni väli on vales vormingus" |
| Ostukorv on tühi (puuduvad `items`) | `BadRequestException` | `EMPTY_CART` | 400 | "Ostukorv on tühi" |
| Toodet ei leitud (`productId` ei eksisteeri) | `NotFoundException` | `PRODUCT_NOT_FOUND` | 404 | "Toodet ei leitud" |
| Laoseis ei ole piisav | `BadRequestException` | `INSUFFICIENT_STOCK` | 400 | "Toode pole laos piisavalt saadaval" |
| Kasutaja ei ole sisse logitud | `UnauthorizedException` | `NOT_AUTHENTICATED` | 401 | "Palun logige sisse" |
| Serveri ootamatu viga (nt andmebaasitõrge) | `RuntimeException` | `INTERNAL_SERVER_ERROR` | 500 | "Server is having issues. Try again later." |

> **Märkus veahalduse kohta:**
> Kontrolli, kas vajalikud `ErrorResponse` enum kirjed ja exception klassid juba eksisteerivad:
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/error/ErrorResponse.java`
> - `backend/src/main/java/ee/bcs/valitalgud/infrastructure/exception/`
>
> Puuduvate enum kirjete puhul lisa need `ErrorResponse`-i. Puuduvate exception klasside puhul loo uus klass `exception/` paketti ja registreeri see `RestExceptionHandler`-is.

## Andmebaas

Seotud tabelid: `orders`, `order_items`, `billings`, `products`, `users`

- `billings` — uue tellimuse jaoks luuakse alati uus arveldusrida (eesnimi, perekonnanimi, ettevõte, riik, tänav, postiindeks, linn, telefon, e-post).
- `orders.billing_id` → `billings.id` — viitab äsja loodud arveldusreale.
- `orders.user_id` → `users.id` — sisse logitud kasutaja ID (võib olla `NULL`, kui kustutatud, kuid loomisel kohustuslik).
- `orders.status` — algväärtus `PENDING` (lubatud: `PENDING`, `PAID`, `SHIPPED`, `DELIVERED`, `CANCELLED`).
- `orders.subtotal`, `orders.shipping`, `orders.tax`, `orders.total` — arvutatakse backendis tellimuse ridade alusel.
- `order_items` — iga `items[]` rida lisatakse koos `product_name` ja `price_at_purchase` snapshotiga (nii et hilisem hinnamuutus ei mõjuta tellimust).
- `products.stock_quantity` — vähendatakse iga tellitud koguse võrra; kontroll `stock_quantity >= quantity`.

> Eeldus: tellimuse loomisel ei kustutata kasutaja ostukorvi automaatselt selle endpointi raames — see toimub eraldi (nt frontend tühjendab `localStorage` ostukorvi pärast eduka vastuse saamist). Kui projekt vajab serveripoolset ostukorvi tühjendamist, tuleks see lisada eraldi sammuna.

## Navigatsioon (frontend kontekst)

| Tegevus | Sihtkoht |
|---------|----------|
| 201 Created (edukas tellimus) | `redirect → /order-success` (`router.push({ name: 'order-success', state: { orderId } })`) |
| 400 Bad Request | Jää lehele, kuva veateade vormi juures |
| 401 Unauthorized | `redirect → /login` (`LoginView.vue`) |
| 500 Internal Server Error | Kuva kasutajale "Server is having issues. Try again later." |
| Kui kasutaja pole sisse logitud (enne POST-i) | `redirect → /login` |
| Kui ostukorv on tühi (enne POST-i) | `redirect → /shop` |

**Header (dünaamiline):**

| Sisselogitud | Väljalogitud |
|--------------|--------------|
| `Homepage \| Profile \| Events \| My Events \| Calendar \| Logout \| Cart` | `Logi sisse \| Loo kasutaja \| Events \| E-pood \| Cart` |

## Vastuvõtu kriteeriumid

- [ ] `POST /api/orders` tagastab 201 Created koos `OrderResponseDto` kehaga
- [ ] Puuduvate kohustuslike väljade puhul tagastab 400 koos `MISSING_ORDER_FIELDS` veaga
- [ ] Tühja ostukorvi puhul tagastab 400 koos `EMPTY_CART` veaga
- [ ] Sisselogimata kasutaja puhul tagastab 401 koos `NOT_AUTHENTICATED` veaga
- [ ] Olematu `productId` puhul tagastab 404 koos `PRODUCT_NOT_FOUND` veaga
- [ ] Ebapiisava laoseisu puhul tagastab 400 koos `INSUFFICIENT_STOCK` veaga
- [ ] `billings` ja `orders` ja `order_items` kirjed luuakse ühe transaktsiooni raames (`@Transactional`)
- [ ] `products.stock_quantity` vähendatakse iga tellitud rea kohta
- [ ] DTO klassid (`CreateOrderDto`, `OrderItemDto`, `OrderResponseDto`) on loodud `controller/order/dto/` paketti
- [ ] Controller, Service, Repository kihid on eraldatud
- [ ] Kontrolleri meetodil on `@Operation` ja `@ApiResponses` annotatsioonid (sh veavastused `ApiError` skeemiga)
- [ ] Swagger UI kaudu on endpoint nähtav ja testitav
