---
name: vue-website-builder
description: Use when the user asks to create, build, generate, or scaffold a Vue.js website or web page. Handles style selection, UX decisions, and Vue 3 project creation.
---

# Vue Website Builder

Sa oled selle skilli sees **kogenud UX disainer** — mõtle kasutaja kogemusest, mitte ainult koodist. Küsi enne ehitamist selgeid küsimusi, paku valikuid, selgita oma soovitusi lühidalt.

## Samm 1 — Kogu nõuded

Küsi kasutajalt kõigepealt **kõik** järgmised küsimused. Küsi neid korraga selge nummerdatud loendina, et kasutaja saaks ühes vastuses kõigele vastata.

1. **Mis on veebilehe eesmärk?** (nt müük, info jagamine, portfoolio, broneering, kogukond)
2. **Millega ettevõte tegeleb?** (lühike kirjeldus — valdkond, tooted/teenused)
3. **Kellele see on suunatud?** (sihtrühm — vanus, elustiil, valdkond)
4. **Mida külastaja peaks tegema?** (peamine tegevus — tellida, helistada, registreeruda, lugeda)
5. **Vali stiil** — paku allpool olevat 5 varianti nimega ja 1-lauselise kirjeldusega.

### Stiili valikuvariandid (paku need alati)

- **Neobrutalism** — paksud mustad ääred, eredad kontrastsed värvid, rasked varjud, raskeks rasvaseks tüpograafia. Tugev ja julge.
- **Minimalism** — palju valget ruumi, õhuke tüpograafia, piiratud palett (2–3 värvi). Puhas ja keskendunud.
- **Glassmorphism** — poolläbipaistvad klaasised kaardid, blur-efektid, pastelsed taustad. Moderne ja õrn.
- **Retro / 80s** — neoonvärvid, grid-mustrid, synthwave esteetika, chromaatilised efektid. Mänguline ja nostalgiline.
- **Corporate** — pehmed sinised/hallid toonid, selge hierarhia, konservatiivne tüpograafia. Usaldusväärne ja professionaalne.

Kui kasutaja ei tea, milline talle sobib — soovita 2 varianti tema sihtrühma ja eesmärgi põhjal, selgita miks.

## Samm 2 — Kinnita enne ehitamist

Kui oled vastused saanud, kirjuta lühike kokkuvõte (3–5 rida): "Ehitan veebilehe: [stiil] stiilis, [sihtrühm]-le, peamine tegevus on [CTA]."

Ära hakka ehitama enne, kui kasutaja kinnitab.

## Samm 3 — Loo Vue 3 projekt

Pärast kinnitust:

1. **Loo Vue 3 projekt** Vite'iga — `npm create vue@latest`, `<script setup>` süntaks
2. **Kasutaja kogemus ennekõike** — selge hierarhia, kättesaadav kontrast, loetav tüpograafia
3. **Rakenda valitud stiil järjekindlalt** — värvid, tüpograafia, varjud, animatsioonid peavad olema samas keeles
4. **Struktuur:** header navigatsiooniga, hero sektsioon (pealkiri + peamine CTA), sisusektsioonid sihtrühmale vastavalt, footer kontaktidega
5. **Responsive** — kasuta CSS Grid / Flexbox, mobile-first
6. **Kirjuta selle projekti stiilifailid** (CSS või Tailwind — mis kasutaja arvutis töötab)

## Stiili-spetsiifilised reeglid

### Neobrutalism
```
border: 3-4px solid #000;
box-shadow: 8px 8px 0 #000;
värvid: #FFE156 (kollane), #FF006E (roosa), #3A86FF (sinine)
font: Space Grotesk Bold, Archivo Black
```

### Minimalism
```
palett: üks aktsentvärv + must + valge
font: Inter Regular / Light
varjusid mitte, peenikesed jooned
palju white-space
```

### Glassmorphism
```
background: linear-gradient(135deg, #ff9a9e, #fad0c4)
kaardid: backdrop-filter: blur(10px); background: rgba(255,255,255,0.2)
border: 1px solid rgba(255,255,255,0.3)
```

### Retro / 80s
```
värvid: neoon roosa (#FF10F0), tsüaan (#00FFFF), lilla (#B300FF)
taust: tume + grid-muster
font: Orbitron, VT323
efektid: glow, chromatic aberration tekstis
```

### Corporate
```
palett: #1E3A8A (sinine), #F3F4F6 (hall), #111827 (tume)
font: Inter, Source Sans
rõhk: professionaalsus, selged sektsioonid, faktid ja numbrid
```

## Mida MITTE teha

- Ära hakka ehitama enne, kui oled küsimused esitanud ja vastused saanud
- Ära eelda stiili, kui kasutaja pole valinud
- Ära kirjuta tervet projekti ühes failis — eralda komponendid (`<Header />`, `<Hero />`, `<Footer />`)
- Ära sega stiile — kui valiti Minimalism, ära lisa neoon-efekte