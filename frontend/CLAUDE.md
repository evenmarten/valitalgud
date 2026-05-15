# CLAUDE.md — Frontend

See fail annab Claude Code'ile juhiseid `frontend/` kausta kohta (Vue 3, Vite).

> **NB!** See fail peab alati jääma eestikeelseks. Ära tõlgi seda inglise keelde.

## Käsud (käivita `frontend/` kaustast)

```bash
npm install              # Paigalda sõltuvused
npm run dev              # Käivita arendusserver pordil 8081
npm run build            # Tootmise build
npm run lint             # Lint ja automaatne parandus (oxlint + eslint)
npm run format           # Vorminda lähtekood Prettier'iga
```

## Arhitektuur

| Kaust | Roll |
|---|---|
| `views/` | Täislehe komponendid, mis on seotud ruuteri marsruutidega |
| `components/common/` | Ühised UI-elemendid (nupud, sildid, laadijad) |
| `components/forms/` | Vormi sisend- ja validatsioonikomponendid |
| `components/modals/` | Modaalakende komponendid |
| `components/tables/` | Tabelite kuvamise komponendid |
| `navigation/` | Navigatsiooniriba ja menüü komponendid |
| `api-services/` | Axios päringute moodulid — üks fail ressursi kohta |
| `auth/` | Sisselogimise olek, tokeni haldus, marsruudi kaitsed |
| `router/` | Vue Routeri marsruutide definitsioonid |

## Kaustade struktuur

```
frontend/
├── public/                 # Avalikud staatilised failid (kopeeritakse buildi)
└── src/
    ├── api-services/       # Axios API päringute teenused — üks fail ressursi kohta
    ├── assets/             # Staatilised ressursid (pildid, fondid jms)
    ├── auth/               # Autentimise loogika ja abifunktsioonid
    ├── components/
    │   ├── common/         # Üldkasutatavad elemendid (nupud, sildid, laadijad)
    │   ├── forms/          # Vormi komponendid (sisendid, validatsioon)
    │   ├── modals/         # Modaalakende komponendid
    │   └── tables/         # Tabelite komponendid
    ├── navigation/         # Navigatsiooniriba ja menüü komponendid
    ├── router/             # Vue Router marsruutide konfiguratsioon
    └── views/              # Lehekülgede komponendid (marsruutidega seotud)
```

## Vue koodistiil

Kasuta kõigis komponentides **Options API**-t. Ära kasuta Composition API-t (`setup()`, `<script setup>`, `ref`, `reactive` jms).

## Olulised seadistused

- Vite suunab kõik `/api/**` päringud aadressile `http://localhost:8080` — CORS seadistust arenduses ei ole vaja.
- `@` alias viitab `frontend/src/` kaustale.
- Globaalne Axios'e eksemplar on kättesaadav kui `this.$axios` (registreeritud `main.js`-s).
- Olekuhaldus toimub **Pinia** poodide kaudu.
