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

## Vue komponendi struktuur (Options API)

### Üldine järjekord `<script>` sees

```js
export default {
  // 1. Komponendi nimi
  name: 'KomponendNimi',

  // 2. Alamkomponendid
  components: { Komponent1, Komponent2 },

  // 3. Props — sisendandmed vanemkomponendist
  props: {
    propNimi: {
      type: String,
      default: '',
    },
  },

  // 4. Emits — sündmused, mida komponent saadab välja
  emits: ['event-midagi-juhtus'],

  // 5. Data — komponendi reaktiivne sisemine olek
  data() {
    return {
      muutuja: '',
    }
  },

  // 6. Computed — arvutatud väärtused (sõltuvad data või props väljadest)
  computed: {
    arvutatudVäärtus() {
      return this.muutuja.toUpperCase()
    },
  },

  // 7. Methods — funktsioonid ja sündmuste käsitlejad
  methods: {
    teeMiddagi() {
      this.$emit('event-midagi-juhtus', this.muutuja)
    },
  },

  // 8. Lifecycle hook — beforeMount käivitub enne HTML-i renderdamist
  beforeMount() {
    this.laeAndmed()
  },
}
```

### Props kirjutamise reeglid

Lihtsatel propidel piisab tüübist:

```js
props: {
  locationName: String,
  selectedCityId: Number,
  isOpen: Boolean,
}
```

Kui on vaja vaikeväärtust või kohustuslikku välja:

```js
props: {
  isOpen: {
    type: Boolean,
    default: false,
  },
  firstOptionLabel: {
    type: String,
    default: '-- Kõik linnad --',
  },
}
```

### Emits ja sündmuste nimetamine

Kõik väljalähtuvad sündmused kirjutatakse `emits` massiivi.
Sündmuse nimi algab alati **`event-`** eesliitega:

```js
emits: ['event-modal-closed', 'event-location-deleted', 'event-new-city-selected']
```

### Data — algväärtuste struktuur

`data()` tagastab alati objekti. Keerukamad andmed (API vastused) kirjutatakse välja koos tühja struktuuriga, et Vue saaks reaktiivsuse seadistada:

```js
data() {
  return {
    successMessage: '',
    errorMessage: '',
    selectedCityId: 0,

    location: {
      cityId: 0,
      locationName: '',
      numberOfAtms: 1,
      imageData: '',
      transactionTypes: [
        {
          transactionTypeId: 0,
          transactionTypeName: '',
          isAvailable: false,
        },
      ],
    },

    errorResponse: {
      message: '',
      errorCode: 0,
    },
  }
},
```

### Methods — API päringute muster

API päringud käivad `.then()` / `.catch()` / `.finally()` ahelana.
Iga päringu vastus suunatakse eraldi `handle`-meetodisse:

```js
methods: {
  getLocations() {
    LocationService.sendGetAtmLocations(this.selectedCityId)
      .then((response) => this.handleGetLocationsResponse(response.data))
      .catch((error) => this.handleGetLocationsError(error))
      .finally()
  },

  handleGetLocationsResponse(locations) {
    this.locations = locations
  },

  handleGetLocationsError(error) {
    const statusCode = error.response.status
    this.errorResponse = error.response.data

    if (statusCode === 404 && this.errorResponse.errorCode === 222) {
      this.errorMessage = this.errorResponse.message
      this.locations = []
    } else {
      NavigationService.navigateToErrorView()
    }
  },
},
```

### Lifecycle hook

Andmete laadimine käib `beforeMount` sees (mitte `mounted`):

```js
beforeMount() {
  this.successMessage = this.$route.query.successMessage ?? ''
  this.getCities()
  this.getLocations()
},
```

### Template — sündmuste ja propide sidumine

**Propid** antakse alla `:`-ga (lühivorm `v-bind:`):
```html
<CitiesDropdown :cities="cities" :selected-city-id="selectedCityId" />
```

**Sündmused** kuulatakse `@`-ga (lühivorm `v-on:`):
```html
<LocationsTable @event-location-deleted="handleLocationDeleted" />
```

Lihtsad sündmused võib kirjutada otse template'i:
```html
@event-modal-closed="isInfoModalOpen = false"
@event-new-city-selected="location.cityId = $event"
```

### Täielik näidiskomponent

```vue
<template>
  <div class="container">
    <AlertError :error-message="errorMessage" />

    <CitiesDropdown
      :cities="cities"
      :selected-city-id="selectedCityId"
      @event-new-city-selected="handleCitySelected"
    />

    <button @click="save" class="btn btn-success">Salvesta</button>
  </div>
</template>

<script>
import AlertError from '@/components/alerts/AlertError.vue'
import CitiesDropdown from '@/components/CitiesDropdown.vue'
import CityService from '@/api-services/CityService.js'
import NavigationService from '@/navigation/NavigationService.js'

export default {
  name: 'NäidisView',
  components: { AlertError, CitiesDropdown },
  props: {
    startCityId: {
      type: Number,
      default: 0,
    },
  },
  emits: ['event-saved'],
  data() {
    return {
      errorMessage: '',
      selectedCityId: 0,
      cities: [],
    }
  },
  computed: {
    hasCity() {
      return this.selectedCityId !== 0
    },
  },
  methods: {
    getCities() {
      CityService.sendGetCitiesRequest()
        .then((response) => (this.cities = response.data))
        .catch(() => NavigationService.navigateToErrorView())
        .finally()
    },

    handleCitySelected(cityId) {
      this.selectedCityId = cityId
    },

    save() {
      if (!this.hasCity) {
        this.errorMessage = 'Vali linn'
        return
      }
      this.$emit('event-saved', this.selectedCityId)
    },
  },
  beforeMount() {
    this.selectedCityId = this.startCityId
    this.getCities()
  },
}
</script>
```

## Olulised seadistused

- Vite suunab kõik `/api/**` päringud aadressile `http://localhost:8080` — CORS seadistust arenduses ei ole vaja.
- `@` alias viitab `frontend/src/` kaustale.
- Globaalne Axios'e eksemplar on kättesaadav kui `this.$axios` (registreeritud `main.js`-s).
- Olekuhaldus toimub **Pinia** poodide kaudu.

---

# Koodistiili juhend (Java/Spring Boot)

> Juhend õpilasele: kuidas kirjutada uut koodi samas stiilis nagu valitalgud backendis.

## 1. Üldine koodistiil

Kood on **lihtne, lühike ja selge**. Iga klass teeb ühte asja, iga meetod teeb ühte asja.

- Kood on otsekohene — loetakse nagu lause
- Puudub tarbetu keerukus
- Üks klass = üks vastutus
- Kasutatakse Lomboki annotatsioone, et vältida boilerplate koodi
- Meetodid on lühikesed — tavaliselt 1–10 rida

Näide lühikesest service-meetodist:

```java
public List<CityOptionDto> getCityOptions() {
    return cityRepository.findAll()
        .stream()
        .map(cityMapper::toOptionDto)
        .toList();
}
```

Kood räägib ise enda eest. Kommentaare lisatakse ainult siis, kui midagi pole ilmne.

---

## 2. Projekti kihiline ülesehitus

| Kiht | Pakett | Roll |
|---|---|---|
| Controller | `controller/` | Võtab vastu HTTP päringuid, tagastab vastused |
| Service | `service/` | Sisaldab äriloogikat, koordineerib tööd |
| Repository | `persistence/<ressurss>/` | Suhtleb andmebaasiga |
| Entity | `persistence/<ressurss>/` | Esindab andmebaasi tabelit Java objektina |
| DTO | `controller/<ressurss>/dto/` | Andmete edastamine API kaudu |
| Mapper | `persistence/<ressurss>/` | Teisendab entity DTO-ks ja vastupidi |
| Exception | `infrastructure/exception/` | Kohandatud veaklassid |
| Error handling | `infrastructure/` | Globaalne veakäsitlus |

**Mida ei tohiks kihtide vahel segada:**
- Controllerisse ei kirjutata äriloogikat
- Servicesse ei kirjutata SQL-päringuid otse
- Repository ei tea, mis formaadis andmed väljapoole lähevad

---

## 3. Service-klassi stiil

```java
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public List<LocationResponseDto> getLocations() {
        return locationMapper.toResponseDtos(locationRepository.findAll());
    }
}
```

- `@Service` + `@RequiredArgsConstructor` — Lombok loob konstruktori kõigile `final` väljadele (dependency injection).
- Kõik sõltuvused on `private final`.
- Meetodid on lühikesed ja konkreetse eesmärgiga.
- Äriloogika on service-s, mitte controlleris.

---

## 4. Meetodite loomise põhimõtted

Iga meetod teeb **ühte asja** ja teeb seda selgelt.

- Meetodi nimi väljendab tegevust
- Meetod on lühike — üldiselt alla 10 rea
- Sisu liigub loogilises järjekorras: võta andmed → tee midagi → tagasta tulemus

**Millal kasutada milliseid tegusõnu:**

| Tegusõna | Millal kasutada |
|---|---|
| `get` | Tagastab ühe konkreetse asja, eeldab et see eksisteerib |
| `find` | Otsib — võib tagastada `Optional` või `null` |
| `create` | Loob uue kirje |
| `update` | Muudab olemasolevat kirjet |
| `delete` | Kustutab kirje |
| `validate` | Kontrollib tingimust, ei tagasta objekti |
| `handle` | Sisaldab tingimuslikku loogikat, muteerib DTO-d või entiteeti |
| `to` | Teisendab objekti teiseks (mapper meetodid) |

---

## 5. Meetodite nimede lihtsus

Hea meetodinimi on **lühike, selge ja arusaadav** — ka ilma kommentaarideta.

| Halb nimi | Parem nimi |
|---|---|
| `performUserAuthenticationAndReturnLoginResponse` | `login` |
| `retrieveCustomerDataFromDatabaseByCustomerId` | `getCustomer` |
| `convertUserEntityIntoLoginResponseDtoObject` | `toLoginResponseDto` |

- Nime lugedes peaks kohe aru saama, mida meetod teeb
- Liiga pikk nimi tähendab sageli, et meetod teeb liiga palju asju
- Hea nimi asendab kommentaari

---

## 6. DTO-de kasutamine

**DTO (Data Transfer Object)** on lihtsalt andmekandja — objekt, millega andmeid liigutatakse API kaudu sisse ja välja.

- Entiteeti ei tagastata otse controllerist — see võib sisaldada tundlikku infot (paroole, sisemisi ID-sid).

Nimetamise muster:

```
LocationDto / LocationRequestDto  — päringuks saadetav (request)
LocationResponseDto               — nimekirja vastus
LocationDetailDto                 — detailvaate vastus
CityOptionDto                     — rippmenüü valik
```

DTO klass:

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationResponseDto {
    private Integer locationId;
    private String locationName;
    private String cityName;
}
```

- `@Data` — getterid, setterid, equals, hashCode, toString
- `@AllArgsConstructor` + `@NoArgsConstructor` — konstruktorid (tühi on vajalik JSON deserializatsiooniks)

---

## 7. Mapperite kasutamine

```java
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {

    LocationResponseDto toResponseDto(Location location);

    List<LocationResponseDto> toResponseDtos(List<Location> locations);

    Location toEntity(LocationDto locationDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateLocation(LocationDto locationDto, @MappingTarget Location location);
}
```

| Meetod | Tähendus |
|---|---|
| `toResponseDto` | Entity → Response DTO |
| `toEntity` | DTO → Entity |
| `toOptionDto` | Entity → rippmenüü valik |
| `updateLocation` | Uuendab olemasolevat entity-t DTO põhjal |

---

## 8. Repository kasutamine

```java
public interface LocationRepository extends JpaRepository<Location, Integer> {

    @Query("select l from Location l where l.status = :status")
    List<Location> findActiveLocationsBy(String status);

    @Query("select count(l) > 0 from Location l where l.name = :name")
    boolean locationExistsBy(String name);
}
```

- Kohandatud päringud: JPQL `@Query` annotatsiooniga.
- Standardsed operatsioonid (`findById`, `save`, `findAll`) tulevad `JpaRepository`-lt.

**`Optional` kasutamine** service-s `orElseThrow`-ga:

```java
User user = userRepository.findByUsername(username)
    .orElseThrow(() -> new ForbiddenException(INCORRECT_CREDENTIALS.getMessage(), INCORRECT_CREDENTIALS.getErrorCode()));
```

---

## 9. Exception ja error handling

**Exception klassid:**

- `ForbiddenException` — kasutajal pole õigust (403)
- `DataNotFoundException` — andmeid ei leitud (404)
- `PrimaryKeyNotFoundException` — konkreetne ID ei eksisteeri (404)

```java
@Getter
public class PrimaryKeyNotFoundException extends RuntimeException {
    private final String message;
    private final Integer errorCode;

    public PrimaryKeyNotFoundException(String message, Integer errorCode) {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
    }
}
```

**ErrorResponse enum** — kõik veateated ühes kohas:

```java
@Getter
public enum ErrorResponse {
    INCORRECT_CREDENTIALS("Vale kasutajanimi või parool", 111),
    USER_NOT_FOUND("Kasutajat ei leitud", 112);

    private final String message;
    private final Integer errorCode;
}
```

Kasutamine koos static impordiga:

```java
import static ee.bcs.valitalgud.infrastructure.error.ErrorResponse.INCORRECT_CREDENTIALS;

throw new ForbiddenException(INCORRECT_CREDENTIALS.getMessage(), INCORRECT_CREDENTIALS.getErrorCode());
```

**RestExceptionHandler** (`@ControllerAdvice`) muudab erindid `ApiError` vastusteks:

```java
@ExceptionHandler(PrimaryKeyNotFoundException.class)
public ResponseEntity<ApiError> handlePrimaryKeyNotFoundException(PrimaryKeyNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiError(ex.getMessage(), ex.getErrorCode()));
}
```

---

## 10. Annotatsioonide stiil

**Springi annotatsioonid:**

| Annotatsioon | Kus kasutatakse |
|---|---|
| `@RestController` + `@RequestMapping("/api")` | Controller klassid |
| `@Service` | Service klassid |
| `@GetMapping`, `@PostMapping` jne | Üksikud endpoint meetodid |
| `@ControllerAdvice` | Globaalne veakäsitlus |

**Lomboki annotatsioonid:**

| Annotatsioon | Kus kasutatakse |
|---|---|
| `@RequiredArgsConstructor` | Service ja controller klassid |
| `@Getter` + `@Setter` | Entity klassid |
| `@Data` + `@AllArgsConstructor` + `@NoArgsConstructor` | DTO klassid |
| `@Getter` | ErrorResponse enum ja exception klassid |

**Static import** enum väärtuste jaoks:

```java
import static ee.bcs.valitalgud.Status.ACTIVE;
// Kasutamine: ACTIVE.getCode()  (mitte Status.ACTIVE.getCode())
```

---

## 11. Uue funktsionaalsuse lisamise muster

**Samm 1: Loo Request DTO**
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationRequestDto {
    private String locationName;
    private Integer cityId;
}
```

**Samm 2: Lisa controllerisse endpoint**
```java
@PostMapping("/locations")
public void createLocation(@RequestBody LocationRequestDto dto) {
    locationService.createLocation(dto);
}
```

**Samm 3: Lisa service-meetod**
```java
public void createLocation(LocationRequestDto dto) {
    City city = cityService.getValidCityBy(dto.getCityId());
    Location location = locationMapper.toEntity(dto);
    location.setCity(city);
    locationRepository.save(location);
}
```

**Samm 4: Lisa mapper-meetod**
```java
Location toEntity(LocationRequestDto dto);
```

**Samm 5: Vajadusel lisa ErrorResponse**
```java
MY_NEW_ERROR("Mingi veateade", 444),
```

Kontrolli lõpus:
- Kas igal kihil on oma vastutus?
- Kas meetodid on lühikesed?
- Kas nimed on lihtsad ja arusaadavad?

---

## 12. Praktilised reeglid

- **Üks meetod = üks tegevus** — kui meetod teeb kahte asja, jaga see kaheks
- **Controllerisse ei kirjutata äriloogikat** — controller ainult võtab vastu ja tagastab
- **Service juhib loogikat** — otsused, kontrollid ja arvutused käivad siin
- **Repository suhtleb andmebaasiga** — mujal SQL-i ega JPQL-i ei kirjutata
- **Mapper teisendab objekte** — entity ↔ DTO teisendus on mapperi töö
- **DTO liigub API kaudu** — entity ei lähe API-le otse
- **Meetodi nimi olgu lihtne** — `login`, mitte `performUserAuthentication`
- **Veateated kuuluvad `ErrorResponse` enumisse**, mitte koodi sisse kirjutatud stringidesse
- **Ära leiuta uut mustrit** — kui projektis on muster olemas, kasuta seda

---

## 13. Hea ja halb näide

### Äriloogika controlleris

```java
// Halb
@PostMapping("/login")
public LoginResponseDto login(@RequestBody LoginRequestDto dto) {
    User user = userRepository.findByUsername(dto.getUsername())
        .orElseThrow(() -> new RuntimeException("Vale kasutajanimi"));
    return new LoginResponseDto(user.getId(), user.getUsername());
}

// Hea
@PostMapping("/login")
public LoginResponseDto login(@RequestBody LoginRequestDto dto) {
    return loginService.login(dto);
}
```

### Entity tagastamine DTO asemel

```java
// Halb — avalikustab andmebaasi struktuuri
@GetMapping("/locations")
public List<Location> getLocations() {
    return locationRepository.findAll();
}

// Hea
@GetMapping("/locations")
public List<LocationResponseDto> getLocations() {
    return locationService.getLocations();
}
```

### Liiga pikk service-meetod

```java
// Halb — kõik loogika ühes meetodis
public void createLocation(LocationRequestDto dto) {
    Optional<City> city = cityRepository.findById(dto.getCityId());
    if (city.isEmpty()) { throw new RuntimeException("Linn ei leitud"); }
    Location location = new Location();
    location.setName(dto.getLocationName());
    location.setCity(city.get());
    location.setStatus("A");
    locationRepository.save(location);
}

// Hea — avalik meetod annab ülevaate sammudest
public void createLocation(LocationRequestDto dto) {
    City city = cityService.getValidCityBy(dto.getCityId());
    Location location = locationMapper.toEntity(dto);
    location.setCity(city);
    locationRepository.save(location);
}
```

### Veateade koodi sisse kirjutatud

```java
// Halb
throw new ForbiddenException("Vale kasutajanimi või parool", 111);

// Hea
throw new ForbiddenException(INCORRECT_CREDENTIALS.getMessage(), INCORRECT_CREDENTIALS.getErrorCode());
```
