# CLAUDE.md — Backend

See fail annab Claude Code'ile juhiseid `backend/` kausta kohta (Spring Boot, Java).

> **NB!** See fail peab alati jääma eestikeelseks. Ära tõlgi seda inglise keelde.

## Käsud (käivita `backend/` kaustast)

```bash
./gradlew bootRun        # Käivita backend pordil 8080
./gradlew build          # Ehita JAR
./gradlew test           # Käivita kõik testid
./gradlew test --tests "ee.bcs.valitalgud.SomeTest"  # Käivita üksik test
```

Swagger UI on kättesaadav aadressil `http://localhost:8080/swagger-ui.html` rakenduse töötamise ajal.

## Arhitektuur

Baaspakett: `ee.bcs.valitalgud`

Kihtidega Spring Boot arhitektuur. Iga domeeni ressurss (nt `user`, `event`) saab oma alamapaketi igas kihis:

| Pakett | Roll |
|---|---|
| `controller/<ressurss>/` | REST kontrollerid (`@RestController`) |
| `controller/<ressurss>/dto/` | Päringu/vastuse DTO-d selle kontrolleri jaoks |
| `service/` | Äriloogika (`@Service`) |
| `persistence/<ressurss>/` | JPA entiteet, MapStruct mapperi liides, Spring Data repositoorium |
| `infrastructure/error/` | API veavastuse mudel |
| `infrastructure/exception/` | Kohandatud erindiklassid |

## Kaustade struktuur

```
backend/
└── src/
    ├── main/
    │   ├── java/
    │   │   └── ee.bcs.valitalgud/
    │   │       ├── controller/
    │   │       │   └── <ressurss>/         # nt user, event
    │   │       │       ├── dto/            # Päringu/vastuse DTO-d
    │   │       │       └── SomeController.java
    │   │       ├── infrastructure/
    │   │       │   ├── error/              # API veavastuse mudel
    │   │       │   └── exception/          # Kohandatud erindiklassid
    │   │       ├── persistence/
    │   │       │   └── <ressurss>/         # nt user, event
    │   │       │       ├── Entity.java
    │   │       │       ├── EntityMapper.java
    │   │       │       └── EntityRepository.java
    │   │       └── service/
    │   └── resources/                      # application.properties, spy.properties
    └── test/                               # Ühik- ja integratsioonitestid
```

## Olulised seadistused

- **MapStruct** haldab entiteet↔DTO teisendusi; mapperi liidesed kasutavad `@Mapper` annotatsiooni parameetriga `componentModel = "spring"` (seadistatud globaalselt `build.gradle`'is). Kaardistamata sihtväljad ignoreeritakse vaikselt.
- **P6Spy** mähib PostgreSQL draiveri arenduses, et logida vormindatud SQL stdout'i (`spy.properties`).
- JPA DDL on keelatud (`ddl-auto=none`); skeemi haldatakse ainult `docs/database/` SQL skriptide kaudu.

---

# Koodistiili juhend

> Juhend põhineb selle projekti tegelikul koodil. Eesmärk on, et sa saaksid kirjutada uut koodi samas stiilis nagu olemasolev projekt.

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

## 2. Kihtide vastutused

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

- `@Service` + `@RequiredArgsConstructor` — Lombok genereerib konstruktori kõikide `final` väljade jaoks (dependency injection).
- Kõik sõltuvused on `private final`.
- Meetodid on lühikesed ja konkreetsed.
- Äriloogika on service-s, mitte controlleris.

---

## 4. Meetodite loomise põhimõtted

1. **Üks tegevus** — kui meetodil on rohkem kui üks selge eesmärk, jaga see kaheks.
2. **Loetav nimi** — nimi räägib, mida meetod teeb.
3. **Lühike** — umbes 5–15 rida. Pikem meetod vajab jagamist.
4. **Loogiline järjekord** — esimene rida teeb esimese sammu, teine teise jne.

Pikema loogika puhul kasutab avalik meetod abimeetodeid — avalik meetod kirjeldab suurt pilti, privaatsed meetodid teevad detailse töö:

```java
@Transactional
public void addLocation(LocationDto locationDto) {
    validateLocationNameIsAvailable(locationDto.getLocationName());
    Location location = createAndSaveLocation(locationDto);
    handleAddLocationImageData(locationDto, location);
    createAndSaveLocationTransactionTypes(location, locationDto.getTransactionTypes());
}
```

---

## 5. Meetodite nimede eesliited

| Eesliide | Kasutus |
|---|---|
| `get` | Tagastab kindla tulemuse, ei sisalda tingimuslikku loogikat. Nt `getValidLocationBy(Integer locationId)` |
| `find` | Otsib ja tagastab tulemuse (võib olla list). Nt `findCities()` |
| `create` | Loob uue objekti. Nt `createLocation(LocationDto locationDto)` |
| `add` | Lisab midagi olemasolevale. Nt `addLocation(LocationDto locationDto)` |
| `update` | Uuendab olemasolevat. Nt `updateLocation(Integer locationId, LocationDto locationDto)` |
| `delete` | Kustutab (selles projektis soft delete). Nt `deleteLocation(Integer locationId)` |
| `validate` | Kontrollib tingimust, viskab erindi kui tingimus ei kehti. Nt `validateLocationNameIsAvailable(...)` |
| `handle` | Sisaldab tingimuslikku loogikat ja muteerib DTO-d või entiteeti. Nt `handleAddLocationImageData(...)` |
| `to` | Mapperis, teisendab ühest tüübist teise. Nt `toLocationDetailDto(...)` |

Nimereeglid:

| Halb nimi | Parem nimi |
|---|---|
| `performUserAuthenticationAndReturnLoginResponse` | `login` |
| `retrieveAllCitiesFromDatabaseAndConvertToDto` | `findCities` |
| `checkIfLocationNameAlreadyExistsInDatabase` | `validateLocationNameIsAvailable` |

---

## 6. DTO-de kasutamine

**DTO (Data Transfer Object)** on lihtne andmekandja klass, mis liigub controlleri ja service vahel.

- Entiteeti ei tagastata otse controllerist — entiteet võib sisaldada tundlikke andmeid.
- Request ja response võivad erineda: sissetulev `LocationDto` erineb tagastatavast `LocationDetailDto`-st.

Nimetamise muster:

```
LocationDto             — päringuks saadetav (request)
LocationResponseDto     — nimekirja vastus
LocationDetailDto       — detailvaate vastus
LoginResponseDto        — sisselogimise vastus
CityOptionDto           — rippmenüü valik
```

DTO klass:

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private Integer userId;
    private String roleName;
}
```

**Muutujate nimetamine:** kasuta täistüüpi nime, mitte lühendeid.

```java
// Hea
LocationDetailDto locationDetailDto = locationMapper.toLocationDetailDto(location);

// Halb
LocationDetailDto dto = locationMapper.toLocationDetailDto(location);
```

---

## 7. Mapperite kasutamine

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

- Nimemuutused (nt `id` -> `cityId`) on selgelt näha `@Mapping` annotatsioonides.
- Arvutuslik väärtus: `@Mapping(expression = "java(Status.ACTIVE.getCode())", target = "status")`
- Ignoreeritav väli: `@Mapping(ignore = true, target = "id")`

---

## 8. Repository kasutamine

```java
public interface LocationRepository extends JpaRepository<Location, Integer> {

    @Query("select l from Location l where (0 = :cityId OR l.city.id = :cityId) AND l.status = 'A' order by l.city.name, l.name")
    List<Location> findActiveLocationsBy(Integer cityId);

    @Query("select (count(l) > 0) from Location l where l.name = :locationName")
    boolean locationExistsBy(String locationName);
}
```

- Kohandatud päringud: JPQL `@Query` annotatsiooniga.
- Standardsed operatsioonid (`findById`, `save`, `findAll`) tulevad `JpaRepository`-lt.

**`Optional` kasutamine** service-s `orElseThrow`-ga:

```java
userRepository.findUserBy(username, password, ACTIVE.getCode())
        .orElseThrow(() -> new ForbiddenException(INCORRECT_CREDENTIALS.getMessage(), INCORRECT_CREDENTIALS.getErrorCode()));
```

**ID järgi otsimine** — `getValid<Entiteet>By(Integer <entiteet>Id)` muster vastava service klassi all:

```java
public Location getValidLocationBy(Integer locationId) {
    return locationRepository.findById(locationId)
            .orElseThrow(() -> new PrimaryKeyNotFoundException("locationId", locationId));
}
```

---

## 9. Exception ja error handling

### Erindiklassid

- `ForbiddenException` — kasutajal pole õigust (403).
- `DataNotFoundException` — andmeid ei leitud (404).
- `PrimaryKeyNotFoundException` — konkreetne ID ei eksisteeri (404).

Kõik laiendavad `RuntimeException`-i ja sisaldavad `message` ja `errorCode` välju:

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

Kõik veateated ja veakoodid koondatult — mitte "maagilisi" stringe koodis:

```java
@Getter
public enum ErrorResponse {
    INCORRECT_CREDENTIALS("Vale kasutajanimi või parool", 111),
    NO_LOCATION_FOUND("Ei leitud ühtegi pangaautomaati", 222);

    private final String message;
    private final Integer errorCode;
}
```

Kasutamine service-s koos static impordiga:

```java
import static ee.bcs.valitalgud.infrastructure.error.ErrorResponse.INCORRECT_CREDENTIALS;

throw new ForbiddenException(INCORRECT_CREDENTIALS.getMessage(), INCORRECT_CREDENTIALS.getErrorCode());
```

### RestExceptionHandler

`@ControllerAdvice` klass püüab erindid kinni ja muudab need `ApiError` vastusteks:

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

## 10. Annotatsioonide stiil

### Springi põhiannotatsioonid

- `@RestController` + `@RequestMapping("/api")` — kontrolleri klass.
- `@Service` — service-klass.
- `@ControllerAdvice` — globaalne erindite käsitleja.

### HTTP meetodi annotatsioonid

- `@GetMapping("/path")`, `@PostMapping("/path")`, `@PutMapping("/path")`, `@DeleteMapping("/path")`

### Lomboki annotatsioonid

- `@RequiredArgsConstructor` — service ja controller klassidel.
- `@Getter` + `@Setter` — entiteetidel.
- `@Data` + `@AllArgsConstructor` + `@NoArgsConstructor` — DTO-del.
- `@Getter` — `ErrorResponse` enumil ja exception klassidel.

### Static import

```java
import static ee.bcs.valitalgud.Status.ACTIVE;
import static ee.bcs.valitalgud.infrastructure.error.ErrorResponse.INCORRECT_CREDENTIALS;
```

---

## 11. Uue funktsionaalsuse lisamise muster

Samm-sammuline järjekord uue endpointi lisamisel:

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
@Mapping(source = "name", target = "locationName")
@Mapping(source = "city.name", target = "cityName")
LocationSummaryDto toLocationSummaryDto(Location location);
```

**Samm 3: Lisa service-meetod**

```java
public LocationSummaryDto findLocationSummary(Integer locationId) {
    Location location = getValidLocationBy(locationId);
    return locationMapper.toLocationSummaryDto(location);
}
```

**Samm 4: Lisa controller-endpoint**

```java
@GetMapping("/location/summary")
@Operation(summary = "Tagastab asukoha kokkuvõtliku info.")
public LocationSummaryDto findLocationSummary(@RequestParam Integer locationId) {
    return locationService.findLocationSummary(locationId);
}
```

**Samm 5: Vajadusel lisa ErrorResponse**

```java
MY_NEW_ERROR("Mingi veateade", 444),
```

Reeglid:
- Iga kiht saab muutuse ainult siis, kui ta seda tõesti vajab.
- Ära pane äriloogikat controllerisse.
- Järgi olemasolevat nimetamisstiili.

---

## 12. Praktilised reeglid

- **Üks meetod = üks tegevus.** Kui meetod teeb kahte asja, jaga see kaheks.
- **Ära kirjuta controllerisse äriloogikat.** Controller kutsub service-meetodit ja tagastab tulemuse.
- **Service juhib loogikat.** Kõik if-laused, valideerimised ja arvutused on service-s.
- **Repository suhtleb andmebaasiga.** Muud loogikat seal pole.
- **Mapper teisendab objekte.** Entiteedist DTO-ks — mapper teeb seda.
- **DTO liigub API kaudu.** Entiteeti ei tagastata controllerist.
- **Meetodi nimi olgu lihtne** — `findLocations`, mitte `getAllActiveLocationEntitiesAndConvertToResponseDtoList`.
- **Muutuja nimi peegeldab täistüüpi** — `LocationDetailDto locationDetailDto`, mitte `LocationDetailDto dto`.
- **Veateated kuuluvad `ErrorResponse` enumisse**, mitte koodi sisse kirjutatud stringidesse.
- **`getValid<X>By(Integer id)` muster** — ID järgi otsimisel, viskab erindi kui ei leia.
- **`handle`-prefiks** — kui meetod sisaldab tingimust ja muteerib DTO-d või entiteeti.
- **Ära leiuta uut mustrit**, kui projektis on muster juba olemas. Vaata olemasolevat koodi ja järgi seda.

---

## 13. Hea ja halb näide

### Meetodinimi

```java
// Halb
public LocationDetailDto retrieveLocationDetailInformationFromDatabaseByLocationIdAndBuildDto(Integer locationId) { ... }

// Hea
public LocationDetailDto findLocationDetail(Integer locationId) { ... }
```

### Äriloogika controlleris

```java
// Halb
@GetMapping("/location")
public LocationDetailDto findLocationDetail(@RequestParam Integer locationId) {
    Location location = locationRepository.findById(locationId).orElseThrow(...);
    LocationDetailDto dto = new LocationDetailDto();
    dto.setLocationName(location.getName());
    return dto;
}

// Hea
@GetMapping("/location")
public LocationDetailDto findLocationDetail(@RequestParam Integer locationId) {
    return locationService.findLocationDetail(locationId);
}
```

### Entiteedi tagastamine controllerist

```java
// Halb — tagastab User entiteedi, mis sisaldab parooli
@GetMapping("/login")
public User login(@RequestParam String username, @RequestParam String password) { ... }

// Hea — tagastab ainult vajalikud väljad
@GetMapping("/login")
public LoginResponseDto login(@RequestParam String username, @RequestParam String password) {
    return loginService.login(username, password);
}
```

### Veateated

```java
// Halb
throw new ForbiddenException("Vale kasutajanimi või parool", 111);

// Hea
throw new ForbiddenException(INCORRECT_CREDENTIALS.getMessage(), INCORRECT_CREDENTIALS.getErrorCode());
```

### Liiga pikk service-meetod

```java
// Halb — kõik loogika ühes meetodis
public void addLocation(LocationDto locationDto) {
    boolean exists = locationRepository.locationExistsBy(locationDto.getLocationName());
    if (exists) { throw new ForbiddenException(...); }
    City city = cityRepository.findById(locationDto.getCityId()).orElseThrow(...);
    Location location = locationMapper.toLocation(locationDto);
    location.setCity(city);
    locationRepository.save(location);
    if (!locationDto.getImageData().isBlank()) { ... }
}

// Hea — avalik meetod annab ülevaate sammudest
public void addLocation(LocationDto locationDto) {
    validateLocationNameIsAvailable(locationDto.getLocationName());
    Location location = createAndSaveLocation(locationDto);
    handleAddLocationImageData(locationDto, location);
    createAndSaveLocationTransactionTypes(location, locationDto.getTransactionTypes());
}
```
