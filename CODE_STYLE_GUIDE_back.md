# CODE_STYLE_GUIDE.md

> Juhend põhineb selle projekti tegelikul koodil. Eesmärk on, et sa saaksid kirjutada uut koodi samas stiilis nagu olemasolev projekt.

---

## 1. Üldine koodistiil

Koodi kirjutamisel järgitakse lihtsat ja loetavat stiili:

- **Üks meetod, üks tegevus.** Meetod teeb ühe asja ja teeb seda hästi.
- **Lühikesed meetodid.** Kui meetod läheb pikaks, jagatakse see väiksemateks abimeetoditeks.
- **Selged nimed.** Muutuja, meetodi ja klassi nimi ütleb kohe, millega tegu.
- **Kood loetakse ülalt alla.** Avalikud meetodid on üleval, privaatsed abimeetodid all.
- **Lomboki annotatsioonid** (`@Getter`, `@Setter`, `@Data`, `@RequiredArgsConstructor`) vähendavad korduva koodi hulka.

Näide hästi loetavast service-meetodist:

```java
public LocationDetailDto findLocationDetail(Integer locationId) {
    Location location = getValidLocationBy(locationId);
    LocationDetailDto locationDetailDto = locationMapper.toLocationDetailDto(location);
    handleAddImageData(locationDetailDto, locationId);
    addTransactionTypes(locationDetailDto, locationId);
    return locationDetailDto;
}
```

Meetodi sisu on nagu loend sammudest: leia, teisenda, lisa pilt, lisa tehingutüübid, tagasta. Iga samm on eraldi rida, mis on nimega abimeetod.

---

## 2. Projekti kihiline ülesehitus

Projekt on jaotatud nelja põhikihti. Igal kihil on oma selge roll.

```
controller/     — HTTP päringud ja vastused (DTOd)
service/        — äriloogika
persistence/    — andmebaasiga seotud kood (entiteedid, repositooriumid, mapperid)
infrastructure/ — ühised komponendid (erindid, veakäsitlus)
```

### Controller
- Võtab HTTP päringu vastu.
- Kutsub service-meetodit.
- Tagastab DTO-d (mitte kunagi entiteete).
- Äriloogikat controlleris **ei ole**.

### Service
- Sisaldab kogu äriloogikat.
- Kutsub repositooriume ja mappereid.
- Viskab erindeid, kui midagi läheb valesti.

### Repository
- Suhtleb andmebaasiga.
- Sisaldab JPQL päringuid `@Query` annotatsiooniga.
- Ei tea midagi äriloogikast.

### Persistence (entiteedid)
- JPA entiteedid, mis vastavad andmebaasi tabelitele.
- Annotatsioonid `@Entity`, `@Table`, `@Column`, `@ManyToOne` jne.
- Ainult `@Getter` ja `@Setter` — muud loogikat entiteedis pole.

### DTO
- Andmekandja klasside vahel.
- Kontrollerid näevad ainult DTOsid.
- Asuvad `controller/<domeen>/dto/` kaustas.

### Mapper
- Teisendab entiteeti DTO-ks ja vastupidi.
- MapStruct liides `@Mapper` annotatsiooniga.
- Asub `persistence/<domeen>/` kaustas.

### Infrastructure
- Kohandatud erindite klassid.
- `RestExceptionHandler` — püüab erindid kinni ja vormib HTTP vastuse.
- `ErrorResponse` enum — kõik veateated ja veakoodid ühes kohas.

---

## 3. Service-klassi stiil

Kõik service-klassid näevad välja umbes nii:

```java
@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    public List<CityOptionDto> findCities() {
        List<City> cities = cityRepository.findAll();
        List<CityOptionDto> cityOptionDtos = cityMapper.toCityOptionDtos(cities);
        return cityOptionDtos;
    }

    public City getValidCityBy(Integer cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new PrimaryKeyNotFoundException("cityId", cityId));
    }
}
```

Mida tähele panna:

- `@Service` — märgib klassi Spring komponendiks.
- `@RequiredArgsConstructor` — Lombok genereerib konstruktori kõikide `final` väljade jaoks. See on dependency injection.
- Kõik sõltuvused on `private final` — see tähendab, et Spring süstib need konstruktori kaudu.
- Meetodid on lühikesed ja konkreetsed.
- Äriloogika on service-s, mitte controlleris, sest controller peaks olema lihtsalt "värav" — ta saab päringu ja edastab selle service-le.

---

## 4. Meetodite loomise põhimõtted

Iga meetod peaks:

1. **Tegema ühe asja** — kui meetodil on rohkem kui üks selge eesmärk, jaga see kaheks.
2. **Olema loetava nimega** — nimi räägib, mida meetod teeb.
3. **Olema lühike** — umbes 5–15 rida on hea pikkus. Pikem meetod vajab jagamist.
4. **Liikuma loogilises järjekorras** — esimene rida teeb esimese sammu, teine teise jne.

Pikema loogika puhul kasutab õpetaja abimeetodeid. Avalik meetod kirjeldab suurt pilti, privaatsed meetodid teevad detailse töö:

```java
@Transactional
public void addLocation(LocationDto locationDto) {
    validateLocationNameIsAvailable(locationDto.getLocationName());
    Location location = createAndSaveLocation(locationDto);
    handleAddLocationImageData(locationDto, location);
    createAndSaveLocationTransactionTypes(location, locationDto.getTransactionTypes());
}
```

Iga rida on nagu lause: valideeri nimi, loo ja salvesta asukoht, lisa pilt, loo tehingutüübid.

---

## 5. Meetodite nimede lihtsus

Meetodi nimi peab olema **lühike ja arusaadav**. Nimi ei pea kirjeldama siselogiikat.

| Halb nimi | Parem nimi |
|---|---|
| `performUserAuthenticationAndReturnLoginResponse` | `login` |
| `retrieveAllCitiesFromDatabaseAndConvertToDto` | `findCities` |
| `convertUserEntityIntoLoginResponseDtoObject` | `toLoginResponseDto` |
| `checkIfLocationNameAlreadyExistsInDatabase` | `validateLocationNameIsAvailable` |

Selles projektis kasutatavad eesliited:

- **`get`** — tagastab kindla tulemuse, ei sisalda tingimuslikku loogikat. Nt `getValidLocationBy(Integer locationId)`.
- **`find`** — otsib ja tagastab tulemuse (võib olla list). Nt `findCities()`, `findLocations(Integer cityId)`.
- **`create`** — loob uue objekti. Nt `createLocation(LocationDto locationDto)`.
- **`add`** — lisab midagi olemasolevale. Nt `addLocation(LocationDto locationDto)`, `addTransactionTypes(...)`.
- **`update`** — uuendab olemasolevat. Nt `updateLocation(Integer locationId, LocationDto locationDto)`.
- **`delete`** — kustutab (selles projektis soft delete). Nt `deleteLocation(Integer locationId)`.
- **`validate`** — kontrollib tingimust, viskab erindi kui tingimus ei kehti. Nt `validateLocationNameIsAvailable(...)`.
- **`handle`** — sisaldab tingimuslikku loogikat ja muteerib DTO-d või entiteeti. Nt `handleAddLocationImageData(...)`.
- **`to`** — mapperis, teisendab ühest tüübist teise. Nt `toLocationDetailDto(...)`, `toCityOptionDtos(...)`.

---

## 6. DTO-de kasutamine

**DTO (Data Transfer Object)** on lihtne andmekandja klass. Ta liigub controlleri ja service vahel ning saabub või lahkub HTTP kaudu.

Miks DTO-d kasutatakse:
- **Entiteeti ei tagastata otse controllerist** — entiteet võib sisaldada tundlikke andmeid (nt parooli) või lisainfot, mida klient ei peaks nägema.
- **API vastus on selge** — DTO sisaldab täpselt need väljad, mida klient vajab.
- **Request ja response võivad erineda** — sissetulev `LocationDto` on erinev tagastatavast `LocationResponseDto`-st.

Nimetamise muster:

```
LocationDto             — päringuks saadetav (request)
LocationResponseDto     — nimekirja vastus
LocationDetailDto       — detailvaate vastus
LoginResponseDto        — sisselogimise vastus
CityOptionDto           — rippmenüü valik
TransactionTypeOptionDto — rippmenüü valik koos saadavuse lipuga
```

DTO klass kasutab Lomboki annotatsioone:

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private Integer userId;
    private String roleName;
}
```

- `@Data` — genereerib getterid, setterid, `equals`, `hashCode`, `toString`.
- `@AllArgsConstructor` — konstruktor kõikide väljadega.
- `@NoArgsConstructor` — tühi konstruktor (vajalik serialiseerimiseks).

**Muutujate nimetamine:** kasuta täistüüpi nime, mitte lühendeid.

```java
// Hea
LocationDetailDto locationDetailDto = locationMapper.toLocationDetailDto(location);

// Halb
LocationDetailDto dto = locationMapper.toLocationDetailDto(location);
```

---

## 7. Mapperite kasutamine

**Mapper** teisendab entiteedi DTO-ks või DTO entiteediks. MapStruct genereerib implementatsiooni automaatselt kompileerimise ajal.

Mapper on alati `interface` `@Mapper` annotatsiooniga:

```java
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface CityMapper {

    @Mapping(source = "id", target = "cityId")
    @Mapping(source = "name", target = "cityName")
    CityOptionDto toCityOptionDto(City city);

    List<CityOptionDto> toCityOptionDtos(List<City> cities);
}
```

Miks mapperit kasutatakse:
- Teisendamise loogika on koondatud ühte kohta.
- Servicel pole vaja teada, millised väljad kuhu lähevad.
- Nimemuutused (nt `id` -> `cityId`) on selgelt näha `@Mapping` annotatsioonides.

Kui välja väärtus tuleb arvutada, kasutatakse `expression`:

```java
@Mapping(expression = "java(Status.ACTIVE.getCode())", target = "status")
Location toLocation(LocationDto locationDto);
```

Kui välja ei tohiks mappida, kasutatakse `ignore = true`:

```java
@Mapping(ignore = true, target = "id")
@Mapping(ignore = true, target = "city")
```

---

## 8. Repository kasutamine

Repository on liides, mis laiendab `JpaRepository`. Ta vastutab andmebaasiga suhtlemise eest.

```java
public interface LocationRepository extends JpaRepository<Location, Integer> {

    @Query("select l from Location l where (0 = :cityId OR l.city.id = :cityId) AND l.status = 'A' order by l.city.name, l.name")
    List<Location> findActiveLocationsBy(Integer cityId);

    @Query("select (count(l) > 0) from Location l where l.name = :locationName")
    boolean locationExistsBy(String locationName);
}
```

Mida tähele panna:
- Kohandatud päringud kirjutatakse JPQL-iga `@Query` annotatsiooniga otse repositooriumi liidesele.
- Standardsed operatsioonid (`findById`, `save`, `findAll`, `saveAll`) tulevad `JpaRepository`-lt tasuta.
- Meetodid on nimetatud kirjeldavalt: `findActiveLocationsBy`, `locationExistsBy`, `deleteAllByLocationId`.

**`Optional` kasutamine** — kui tulemus võib olla tühi, tagastatakse `Optional`:

```java
Optional<User> findUserBy(String username, String password, String status);
```

Service-s käsitletakse seda `orElseThrow`-ga:

```java
userRepository.findUserBy(username, password, ACTIVE.getCode())
        .orElseThrow(() -> new ForbiddenException(INCORRECT_CREDENTIALS.getMessage(), INCORRECT_CREDENTIALS.getErrorCode()));
```

**ID järgi otsimine** peab olema `getValid<Entiteet>By(Integer <entiteet>Id)` meetodina vastava service klassi all:

```java
// LocationService-s
public Location getValidLocationBy(Integer locationId) {
    return locationRepository.findById(locationId)
            .orElseThrow(() -> new PrimaryKeyNotFoundException("locationId", locationId));
}

// CityService-s
public City getValidCityBy(Integer cityId) {
    return cityRepository.findById(cityId)
            .orElseThrow(() -> new PrimaryKeyNotFoundException("cityId", cityId));
}
```

---

## 9. Exception ja error handling

### Kohandatud erindid

Projektis on kolm erindi klassi:

- `ForbiddenException` — kasutajal pole õigust (403). Nt vale parool, nimi on juba võetud.
- `DataNotFoundException` — andmeid ei leitud (404). Nt ei leitud ühtegi asukohta.
- `PrimaryKeyNotFoundException` — konkreetne ID ei eksisteeri (404). Nt `locationId = 999` ei eksisteeri.

Kõik erindid laiendavad `RuntimeException`-i ja sisaldavad `message` ja `errorCode` välju:

```java
@Getter
public class ForbiddenException extends RuntimeException {
    private final String message;
    private final Integer errorCode;

    public ForbiddenException(String message, Integer errorCode) {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
    }
}
```

### ErrorResponse enum

Kõik veateated ja veakoodid on koondatud `ErrorResponse` enumisse. Ei kasutata "maagilisi" stringe koodis:

```java
@Getter
public enum ErrorResponse {
    INCORRECT_CREDENTIALS("Vale kasutajanimi või parool", 111),
    NO_LOCATION_FOUND("Ei leitud ühtegi pangaautomaati", 222),
    LOCATION_NAME_UNAVAILABLE("Sellise nimega pangaautomaadi asukoht on juba süsteemis olemas", 333);

    private final String message;
    private final Integer errorCode;
    // ...
}
```

Kasutamine service-s koos static impordiga:

```java
import static ee.bcs.bank40back.infrastructure.error.ErrorResponse.LOCATION_NAME_UNAVAILABLE;

throw new ForbiddenException(LOCATION_NAME_UNAVAILABLE.getMessage(), LOCATION_NAME_UNAVAILABLE.getErrorCode());
```

### RestExceptionHandler

`RestExceptionHandler` (`@ControllerAdvice`) püüab erindid kinni ja muudab need `ApiError` vastusteks:

```java
@ExceptionHandler
public ResponseEntity<ApiError> handleForbiddenException(ForbiddenException exception) {
    ApiError apiError = new ApiError();
    apiError.setMessage(exception.getMessage());
    apiError.setErrorCode(exception.getErrorCode());
    return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
}
```

---

## 10. Importide ja annotatsioonide stiil

### Annotatsioonide paigutus

Annotatsioonid kirjutatakse klassi või meetodi kohal, igaüks omal real:

```java
@Service
@RequiredArgsConstructor
public class LoginService {
```

```java
@Getter
@Setter
@Entity
@Table(name = "location", schema = "bank")
public class Location {
```

### Springi põhiannotatsioonid

- `@RestController` — kontrolleri klass, tagastab JSON vastuseid.
- `@RequestMapping("/api")` — baastee kõikidele endpointidele.
- `@Service` — service-klass.
- `@ControllerAdvice` — globaalne erindite käsitleja.

### HTTP meetodi annotatsioonid

- `@GetMapping("/path")` — GET päring.
- `@PostMapping("/path")` — POST päring.
- `@PutMapping("/path")` — PUT päring.
- `@DeleteMapping("/path")` — DELETE päring.

### Lomboki annotatsioonid

- `@RequiredArgsConstructor` — service ja controller klassidel, genereerib konstruktori `final` väljadele.
- `@Getter` + `@Setter` — entiteetidel, genereerib getterid ja setterid.
- `@Data` — DTO-del, genereerib kõik (getterid, setterid, equals, hashCode, toString).
- `@AllArgsConstructor` + `@NoArgsConstructor` — DTO-del.
- `@Getter` — `ErrorResponse` enumil ja exception klassidel.

### Static import

Kasutatakse `ErrorResponse` enumist lugemisel ja `Status` enumist lugemisel, et kood oleks loetavam:

```java
import static ee.bcs.bank40back.Status.ACTIVE;
import static ee.bcs.bank40back.infrastructure.error.ErrorResponse.INCORRECT_CREDENTIALS;

// Siis koodis:
ACTIVE.getCode()
INCORRECT_CREDENTIALS.getMessage()
```

---

## 11. Klasside ja pakettide nimetamine

### Klasside nimetamine

Klassi nimi näitab kohe, mis kihiga ja mis domeeniga on tegu:

| Klass | Roll |
|---|---|
| `LocationController` | HTTP endpoint location domeenis |
| `LocationService` | Äriloogika location domeenis |
| `LocationRepository` | Andmebaas location domeenis |
| `Location` | JPA entiteet |
| `LocationMapper` | Mapper location domeenis |
| `LocationDto` | Päringu DTO |
| `LocationResponseDto` | Vastuse DTO nimekirja jaoks |
| `LocationDetailDto` | Vastuse DTO detailvaate jaoks |
| `ForbiddenException` | 403 erind |
| `ErrorResponse` | Veakoodid ja -teated |
| `RestExceptionHandler` | Globaalne erindite käsitleja |

### Pakettide struktuur

```
ee.bcs.bank40back
├── controller/
│   ├── city/
│   │   ├── CityController.java
│   │   └── dto/
│   │       └── CityOptionDto.java
│   ├── location/
│   │   ├── LocationController.java
│   │   └── dto/
│   │       ├── LocationDto.java
│   │       ├── LocationDetailDto.java
│   │       └── LocationResponseDto.java
│   ├── login/
│   │   ├── LoginController.java
│   │   └── dto/
│   │       └── LoginResponseDto.java
│   └── common/
│       └── TransactionTypeOptionDto.java
├── service/
│   ├── CityService.java
│   ├── LocationService.java
│   ├── LoginService.java
│   └── TransactionTypeService.java
├── persistence/
│   ├── city/
│   │   ├── City.java
│   │   ├── CityMapper.java
│   │   └── CityRepository.java
│   └── location/
│       ├── Location.java
│       ├── LocationMapper.java
│       └── LocationRepository.java
└── infrastructure/
    ├── RestExceptionHandler.java
    ├── error/
    │   ├── ApiError.java
    │   └── ErrorResponse.java
    └── exception/
        ├── DataNotFoundException.java
        ├── ForbiddenException.java
        └── PrimaryKeyNotFoundException.java
```

---

## 12. Koodi lugemise järjekord

Kui tuled uude projekti, loe koodi selles järjekorras:

1. **Controller** — vaata, millised HTTP endpointid on olemas (`@GetMapping`, `@PostMapping` jne) ja millist service-meetodit kutsutakse.
2. **Service** — vaata, mida avalik meetod teeb. Loe rida-realt, mida iga samm teeb.
3. **Abimeetodid** — kui service-meetodis on `private` abimeetodid, loe need läbi alt poolt.
4. **Repository** — vaata, milliseid andmebaasipäringuid kasutatakse (`@Query` annotatsioonid).
5. **DTO-d** — vaata, millised väljad sisse tulevad ja välja lähevad.
6. **Mapper** — vaata, kuidas entiteedid DTO-deks teisendatakse (`@Mapping` annotatsioonid).
7. **Erindid ja ErrorResponse** — vaata, millised veaolukorrad on käsitletud.

**Näide:** Tahan aru saada, kuidas `/api/login` töötab.

1. `LoginController.login()` — võtab `username` ja `password`, kutsub `loginService.login()`.
2. `LoginService.login()` — otsib kasutajat repositooriumist, viskab erindi kui ei leia, teisendab mapperiga DTO-ks.
3. `UserRepository.findUserBy()` — JPQL päring kasutaja leidmiseks.
4. `LoginResponseDto` — väljad `userId` ja `roleName`.
5. `UserMapper.toLoginResponseDto()` — `user.id` -> `userId`, `user.role.name` -> `roleName`.
6. `ForbiddenException` + `INCORRECT_CREDENTIALS` — vale parooli puhul visatav erind.

---

## 13. Uue funktsionaalsuse lisamise muster

Olgu ülesanne lisada uus endpoint, mis tagastab pangaautomaadi asukoha nime ja linna nime ID järgi.

**Samm 1: Loo DTO**

```java
// controller/location/dto/LocationSummaryDto.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationSummaryDto implements Serializable {
    private String locationName;
    private String cityName;
}
```

**Samm 2: Lisa mapper-meetod**

```java
// LocationMapper.java
@Mapping(source = "name", target = "locationName")
@Mapping(source = "city.name", target = "cityName")
LocationSummaryDto toLocationSummaryDto(Location location);
```

**Samm 3: Lisa service-meetod**

```java
// LocationService.java
public LocationSummaryDto findLocationSummary(Integer locationId) {
    Location location = getValidLocationBy(locationId);
    return locationMapper.toLocationSummaryDto(location);
}
```

**Samm 4: Lisa controller-endpoint**

```java
// LocationController.java
@GetMapping("/atm/location/summary")
@Operation(summary = "Tagastab pangaautomaadi kokkuvõtliku info.")
public LocationSummaryDto findLocationSummary(@RequestParam Integer locationId) {
    return locationService.findLocationSummary(locationId);
}
```

**Samm 5: Vajadusel lisa ErrorResponse**

Kui tekib uus veaolukord, lisa see `ErrorResponse` enumisse:

```java
MY_NEW_ERROR("Mingi veateade", 444),
```

**Reeglid:**
- Iga kiht saab muutuse ainult siis, kui ta seda tõesti vajab.
- Ära pane äriloogikat controllerisse.
- Hoia meetodid lühikesed.
- Järgi olemasolevat nimetamisstiili.

---

## 14. Praktilised reeglid õpilasele

- **Üks meetod = üks tegevus.** Kui meetod teeb kahte asja, jaga see kaheks.
- **Ära kirjuta controllerisse äriloogikat.** Controller kutsub service-meetodit ja tagastab tulemuse.
- **Service juhib loogikat.** Kõik if-laused, valideerimised ja arvutused on service-s.
- **Repository suhtleb andmebaasiga.** Muud loogikat seal pole.
- **Mapper teisendab objekte.** Entiteedist DTO-ks ja vastupidi — mapper teeb seda.
- **DTO liigub API kaudu.** Entiteeti ei tagastata controllerist.
- **Meetodi nimi olgu lihtne ja arusaadav** — `findLocations`, mitte `getAllActiveLocationEntitiesAndConvertToResponseDtoList`.
- **Muutuja nimi peegeldab täistüüpi** — `LocationDetailDto locationDetailDto`, mitte `LocationDetailDto dto`.
- **Veateated kuuluvad `ErrorResponse` enumisse**, mitte koodi sisse kirjutatud suvalistesse stringidesse.
- **`getValid<X>By(Integer id)` muster** — kui otsid entiteeti ID järgi ja viskad erindi kui ei leia, kasuta seda mustrit.
- **`handle`-prefiks** — kui meetod sisaldab tingimust ja muteerib DTO-d või entiteeti, alusta nimega `handle`.
- **Ära leiuta uut mustrit**, kui projektis on muster juba olemas. Vaata olemasolevat koodi ja järgi seda.

---

## 15. Hea ja halb näide

### Näide 1: Meetodinimi

**Halb:**
```java
public LocationDetailDto retrieveLocationDetailInformationFromDatabaseByLocationIdAndBuildDto(Integer locationId) {
    // ...
}
```

**Hea:**
```java
public LocationDetailDto findLocationDetail(Integer locationId) {
    // ...
}
```

Nimi peab ütlema *mida* meetod teeb, mitte *kuidas* ta seda teeb.

---

### Näide 2: Äriloogika controlleris

**Halb:**
```java
@GetMapping("/atm/location")
public LocationDetailDto findLocationDetail(@RequestParam Integer locationId) {
    Location location = locationRepository.findById(locationId)
            .orElseThrow(() -> new RuntimeException("Not found"));
    LocationDetailDto dto = new LocationDetailDto();
    dto.setLocationName(location.getName());
    // ... rohkem loogikat ...
    return dto;
}
```

**Hea:**
```java
@GetMapping("/atm/location")
public LocationDetailDto findLocationDetail(@RequestParam Integer locationId) {
    return locationService.findLocationDetail(locationId);
}
```

Controller ainult kutsub service-meetodit. Kogu loogika on service-s.

---

### Näide 3: Entiteedi tagastamine controlleri kaudu

**Halb:**
```java
@GetMapping("/login")
public User login(@RequestParam String username, @RequestParam String password) {
    return userRepository.findUserBy(username, password, "A").orElseThrow(...);
}
```

**Hea:**
```java
@GetMapping("/login")
public LoginResponseDto login(@RequestParam String username, @RequestParam String password) {
    return loginService.login(username, password);
}
```

Tagastatakse `LoginResponseDto`, mis sisaldab ainult `userId` ja `roleName`. `User` entiteet sisaldab parooli ja muud sensitiivset infot, mida klient ei tohiks näha.

---

### Näide 4: Veateade koodi sisse kirjutatud

**Halb:**
```java
throw new ForbiddenException("Vale kasutajanimi või parool", 111);
```

**Hea:**
```java
throw new ForbiddenException(INCORRECT_CREDENTIALS.getMessage(), INCORRECT_CREDENTIALS.getErrorCode());
```

Enum tagab, et sama veateade ja veakood on alati ühes kohas. Kui veateateid muuta, muudetakse ainult ühes kohas.

---

### Näide 5: Liiga pikk service-meetod

**Halb:**
```java
public void addLocation(LocationDto locationDto) {
    boolean exists = locationRepository.locationExistsBy(locationDto.getLocationName());
    if (exists) {
        throw new ForbiddenException(LOCATION_NAME_UNAVAILABLE.getMessage(), LOCATION_NAME_UNAVAILABLE.getErrorCode());
    }
    City city = cityRepository.findById(locationDto.getCityId())
            .orElseThrow(() -> new PrimaryKeyNotFoundException("cityId", locationDto.getCityId()));
    Location location = locationMapper.toLocation(locationDto);
    location.setCity(city);
    locationRepository.save(location);
    if (!locationDto.getImageData().isBlank()) {
        LocationImage locationImage = locationImageMapper.toLocationImage(locationDto);
        locationImage.setLocation(location);
        locationImageRepository.save(locationImage);
    }
    // ... ja nii edasi
}
```

**Hea:**
```java
public void addLocation(LocationDto locationDto) {
    validateLocationNameIsAvailable(locationDto.getLocationName());
    Location location = createAndSaveLocation(locationDto);
    handleAddLocationImageData(locationDto, location);
    createAndSaveLocationTransactionTypes(location, locationDto.getTransactionTypes());
}
```

Avalik meetod annab ülevaate sammudest. Iga samm on oma abimeetodis.

---

## 16. Kokkuvõte

See projekt järgib selget ja lihtsat stiili:

- **Lihtsus** — koodi kirjutatakse nii lihtsalt kui võimalik. Keerukat koodi välditakse.
- **Loetavus** — koodi lugedes peaks saama aru, mida see teeb. Nimed on kirjeldavad, meetodid lühikesed.
- **Ühtlane stiil** — kõik service-klassid näevad ühte moodi välja, kõik kontrollerid näevad ühte moodi välja. Järgi olemasolevat mustrit.
- **Väikesed meetodid** — meetod, mis teeb ühe asja, on paremini testitav, loetavam ja muudetavam.
- **Selged vastutused** — controller ei tee service'i tööd, service ei tee repositooriumi tööd. Iga kiht teab oma rolli.
- **Olemasolevate mustrite järgimine** — enne uue koodi kirjutamist vaata, kuidas sarnane asi on juba tehtud. Kirjuta uus kood samas stiilis.

Kui sa ei tea, kuidas midagi kirjutada, vaata olemasolevat projektikoodi. Tõenäoliselt on seal juba sarnane näide olemas.