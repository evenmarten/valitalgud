# UnauthorizedView — Autoriseerimata juurdepääs

**Vaade:** `UnauthorizedView.vue`
**Route:** `/unauthorized`
**Tüüp:** Backend
**Staatus:** To Do

## Kontekst

Vaade, mis kuvatakse, kui sisselogimata kasutaja proovib navigeerida kaitstud lehele (nt `/events`, `/events/:id`, `/my-events`, `/calendar`, `/profile`, `/my-organized-events`). Vaade kuvab sõnumi "Event list can't be viewed because not being log[ged in]" ning kolm nuppu: "Login", "Create User" ja "Home". Suunamine `/unauthorized` lehele toimub Vue Router'i navigatsioonivalvuri (`router.beforeEach`) kaudu, mis kontrollib `localStorage` `userId` olemasolu.

## Mocki vaade

![UnauthorizedView mock](../../../mock%20pildid/UnauthorizedView.vue.png)

## Backend tegevus

**Backend endpointe ei vaja** — vaade kuvab ainult staatilist sisu ja navigatsiooninupud. Kogu autentimiskontroll toimub frontendis (Vue Router valvur kontrollib `localStorage.userId` olemasolu enne kaitstud route'idele navigeerimist).

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

Otsest andmebaasi pöördumist sellelt vaatelt ei tehta. Sisselogimise oleku kontroll toimub frontendis `localStorage` põhjal (`userId` võti).

## Navigatsioon (frontend kontekst)

**Kaitstud lehed** (suunavad siia, kui `localStorage.userId` puudub):
- `/events`, `/events/:id`
- `/my-events`
- `/calendar`
- `/profile`
- `/my-organized-events`

**Avalikud lehed** (ei suuna siia):
- `/` (LandingPage)
- `/login`, `/register`
- `/shop`, `/shop/:id`
- `/cart`, `/checkout`
- `/order-success`
- `/404` (ErrorView)

**Nupud sellel lehel:**

| Nupp | Sihtkoht | Filename |
|------|----------|----------|
| Login | `/login` | `LoginView.vue` |
| Create User | `/register` | `RegisterView.vue` |
| Home | `/` | `LandingPage.vue` |

**Header (dünaamiline):**

| Sisselogitud | Väljalogitud |
|--------------|--------------|
| `Homepage \| Profile \| Events \| My Events \| Calendar \| Logout \| Cart` | `Logi sisse \| Loo kasutaja \| Events \| E-pood \| Cart` |

> Eeldus: autentimine põhineb `localStorage.userId` olemasolul (vastavalt `LoginView` ülesandele, kus eduka sisselogimise järel salvestatakse `userId` localStorage'isse). Kui projekt liigub hiljem JWT/sessioonipõhisele autentimisele, tuleb valvuri loogikat vastavalt uuendada.

## Vastuvõtu kriteeriumid

- [ ] **Backend muudatusi pole vaja**
- [ ] Vue Router'i `beforeEach` valvur suunab sisselogimata kasutaja kaitstud route'ilt `/unauthorized` lehele
- [ ] Vaade kuvab sõnumi "Event list can't be viewed because not being logged in"
- [ ] Nupp "Login" suunab `/login` lehele
- [ ] Nupp "Create User" suunab `/register` lehele
- [ ] Nupp "Home" suunab `/` lehele
- [ ] Header kuvatakse dünaamiliselt vastavalt sisselogimisstaatusele
