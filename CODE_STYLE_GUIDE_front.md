# CODE_STYLE_GUIDE.md

> Juhend õpilasele: kuidas kirjutada uut koodi samas stiilis nagu bank40back projektis.

---

## 1. Üldine koodistiil

Õpetaja kood on **lihtne, lühike ja selge**. Iga klass teeb ühte asja, iga meetod teeb ühte asja.

- Kood on otsekohene – loetakse nagu lause
- Puudub tarbetu keerukus
- Üks klass = üks vastutus
- Kasutatakse Lomboki annotatsioone, et vältida boilerplate koodi
- Meetodid on lühikesed – tavaliselt 1–10 rida

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

Projekt on jagatud kihtideks, kus igal kihil on oma selge roll:

| Kiht | Pakett | Roll |
|---|---|---|
| Controller | `controller/` | Võtab vastu HTTP päringuid, tagastab vastused |
| Service | `service/` | Sisaldab äriloogikat, koordineerib tööd |
| Repository | `persistence/repository/` | Suhtleb andmebaasiga |
| Entity | `persistence/entity/` | Esindab andmebaasi tabelit Java objektina |
| DTO | `controller/[ressurss]/dto/` | Andmete edastamine API kaudu |
| Mapper | `persistence/mapper/` | Teisendab entity DTO-ks ja vastupidi |
| Exception | `infrastructure/exception/` | Kohandatud veaklassid |
| Error handling | `infrastructure/` | Globaalne veakäsitlus |

**Mida ei tohiks kihtide vahel segada:**
- Controllerisse ei kirjutata äriloogikat
- Servicesse ei kirjutata SQL-päringueid otse
- Repository ei tea, mis formaadis andmed väljapoole lähevad

---

## 3. Service-klassi stiil

Service-klass on projekti süda – siin asub äriloogika.

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

**Mida tähele panna:**

- `@Service` – märgib klassi Spring service'iks
- `@RequiredArgsConstructor` – Lombok loob konstruktori kõigile `final` väljadele
- `private final` – kõik sõltuvused on `final` (dependency injection konstruktori kaudu)
- Meetodid on lühikesed ja konkreetse eesmärgiga

**Miks äriloogika peaks olemas service-kihis, mitte controlleris?**

Controller vastutab ainult HTTP päringute vastu võtmise ja vastuste tagastamise eest. Kui äriloogika on controlleris, muutub see raskeks testida ja muuta. Service-klass saab kasutada mitmeid repository klasse ja koordineerida keerukat loogikat – controller ei peaks seda tegema.

---

## 4. Meetodite loomise põhimõtted

Iga meetod teeb **ühte asja** ja teeb seda selgelt.

- Meetodi nimi väljendab tegevust
- Meetod on lühike – üldiselt alla 10 rea
- Sisu liigub loogilises järjekorras: võta andmed → tee midagi → tagasta tulemus

**Näited headest meetodinimedest projekti stiilis:**

```java
// Andmete küsimine
public CustomerResponseDto getCustomer(Integer id) { ... }
public List<LocationResponseDto> getLocations() { ... }

// Loomine
public void createAccount(AccountRequestDto dto) { ... }

// Uuendamine
public void updateBalance(Integer accountId, BigDecimal amount) { ... }

// Kustutamine
public void deleteUser(Integer userId) { ... }

// Otsimine
public Location findById(Integer id) { ... }

// Teisendamine
public LocationResponseDto toResponseDto(Location location) { ... }
public LocationDto toDto(Location location) { ... }
```

**Millal kasutada milliseid tegusõnu:**

| Tegusõna | Millal kasutada |
|---|---|
| `get` | Tagastab ühe konkreetse asja, eeldab et see eksisteerib |
| `find` | Otsib – võib tagastada `Optional` või `null` |
| `create` | Loob uue kirje |
| `update` | Muudab olemasolevat kirjet |
| `delete` | Kustutab kirje |
| `check` / `validate` | Kontrollib tingimust, ei tagasta objekti |
| `to` | Teisendab objekti teiseks (mapper meetodid) |

---

## 5. Meetodite nimede lihtsus

Hea meetodinimi on **lühike, selge ja arusaadav** – ka ilma kommentaarideta.

**Halvem:**
```java
performUserAuthenticationAndReturnLoginResponse(...)
```

**Parem:**
```java
login(...)
```

---

**Halvem:**
```java
retrieveCustomerDataFromDatabaseByCustomerId(...)
```

**Parem:**
```java
getCustomer(...)
```

---

**Halvem:**
```java
convertUserEntityIntoLoginResponseDtoObject(...)
```

**Parem:**
```java
toLoginResponseDto(...)
```

**Miks nimed peaksid olema lihtsad?**

- Nime lugedes peaks kohe aru saama, mida meetod teeb
- Liiga pikk nimi tähendab sageli, et meetod teeb liiga palju asju
- Hea nimi asendab kommentaari
- Sisemist loogikat ei pea nimesse panema – selleks on meetodi keha

---

## 6. DTO-de kasutamine

DTO (Data Transfer Object) on lihtsalt andmekandja – objekt, millega andmeid liigutatakse API kaudu sisse ja välja.

**Miks mitte tagastada entity otse?**

Entity on seotud andmebaasiga ja võib sisaldada tundlikku infot (paroole, sisemisi ID-sid). DTO lubab täpselt kontrollida, mis andmed välja lähevad.

**Naming pattern:**

```java
// Sisend (kasutaja saadab serverile)
public class LocationRequestDto { ... }

// Väljund (server tagastab kasutajale)
public class LocationResponseDto { ... }

// Detailvaade
public class LocationDetailDto { ... }

// Valikute loend (nt dropdown)
public class CityOptionDto { ... }
```

**DTO klass näeb välja nii:**

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

- `@Data` – Lombok genereerib getterid, setterid, equals, hashCode, toString
- `@AllArgsConstructor` – konstruktor kõigi väljadega
- `@NoArgsConstructor` – tühi konstruktor (vajalik JSON deserializatsiooniks)

---

## 7. Mapperite kasutamine

Mapper teisendab entity DTO-ks ja vastupidi. Projektis kasutatakse **MapStruct** raamatukogu.

**Miks eraldi mapper?**

Teisendamise loogika võib muutuda mahukaks. Kui see on controlleris või service-meetodis, muutub kood raskesti loetavaks. Mapper on eraldi koht, kus on selgelt näha, kuidas üks objekt teiseks muutub.

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

**Mapper meetodite nimed:**

| Meetod | Tähendus |
|---|---|
| `toDto` | Entity → DTO |
| `toResponseDto` | Entity → Response DTO |
| `toEntity` | DTO → Entity |
| `toLoginResponseDto` | User entity → Login response |
| `toOptionDto` | Entity → Dropdown valik |
| `updateLocation` | Uuendab olemasolevat entity-t DTO põhjal |

---

## 8. Repository kasutamine

Repository vastutab ainult andmebaasiga suhtlemise eest. Muud loogika ei kuulu siia.

```java
public interface LocationRepository extends JpaRepository<Location, Integer> {

    @Query("select l from Location l where l.status = :status")
    List<Location> findActiveLocationsBy(String status);

    @Query("select count(l) > 0 from Location l where l.name = :name")
    boolean locationExistsBy(String name);

    @Modifying
    @Transactional
    @Query("delete from Location l where l.id = :id")
    void deleteLocationBy(Integer id);
}
```

**Nimetamise konventsioon:**

- `findBy...` – otsib kirjeid tingimuse järgi
- `existsBy...` – kontrollib, kas kirje eksisteerib
- `deleteBy...` – kustutab kirjeid tingimuse järgi

**Optional kasutamine:**

Kui kirje võib puududa, kasutab repository `Optional<T>`:

```java
Optional<User> findByUsername(String username);
```

Service-kihis käsitletakse seda nii:

```java
User user = userRepository.findByUsername(username)
    .orElseThrow(() -> new UsernameNotFoundException(INCORRECT_CREDENTIALS));
```

---

## 9. Exception ja error handling

Projektis on kohandatud exception klassid, mis kirjeldavad täpselt, mis läks valesti.

**Exception klass:**

```java
@Getter
public class PrimaryKeyNotFoundException extends RuntimeException {
    private final String message;
    private final Integer errorCode;

    public PrimaryKeyNotFoundException(ErrorResponse errorResponse) {
        this.message = errorResponse.getMessage();
        this.errorCode = errorResponse.getErrorCode();
    }
}
```

**Error response enum:**

```java
public enum ErrorResponse {
    INCORRECT_CREDENTIALS("Vale kasutajanimi või parool", 111),
    USER_NOT_FOUND("Kasutajat ei leitud", 112);

    private final String message;
    private final Integer errorCode;
}
```

**Globaalne veakäsitlus:**

```java
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PrimaryKeyNotFoundException.class)
    public ResponseEntity<ApiError> handlePrimaryKeyNotFoundException(PrimaryKeyNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiError(ex.getMessage(), ex.getErrorCode()));
    }
}
```

**Miks kohandatud exception klassid?**

- Ühtlane veakäsitlus kogu projektis
- Veakoodid on ühes kohas, mitte laiali koodis
- Klient saab struktureeritud vastuse (message + errorCode)
- Lihtne lisada uusi veaklasse samas stiilis

---

## 10. Importide ja annotatsioonide stiil

Annotatsioonid paiknevad alati klassi kohal, üksteise järel:

```java
@Service
@RequiredArgsConstructor
@Transactional
public class LoginService { ... }
```

```java
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LocationController { ... }
```

**Springi annotatsioonid:**

| Annotatsioon | Kus kasutatakse |
|---|---|
| `@RestController` | Controller klassid |
| `@Service` | Service klassid |
| `@Repository` | Repository klassid (tavaliselt pole vaja, kui extends JpaRepository) |
| `@RequestMapping` | Baas-URL controller klassil |
| `@GetMapping`, `@PostMapping` jne | Üksikud endpoint meetodid |

**Lomboki annotatsioonid:**

| Annotatsioon | Kus kasutatakse |
|---|---|
| `@RequiredArgsConstructor` | Service ja controller klassid |
| `@Getter` | Entity ja exception klassid |
| `@Setter` | Entity klassid |
| `@Data` | DTO klassid |
| `@Builder` | Klassid, kus vajalik builder pattern |

**Static import:**

Projektis kasutatakse static import'e enum väärtuste jaoks, et kood oleks loetavam:

```java
import static ee.bcs.bank40back.Status.ACTIVE;

// Kasutamine meetodis
location.setStatus(ACTIVE);
// mitte
location.setStatus(Status.ACTIVE);
```

---

## 11. Klasside ja pakettide nimetamine

Klassi nimi peab kohe näitama, mille eest klass vastutab.

**Klassid:**

| Klass | Roll |
|---|---|
| `LoginService` | Sisselogimise äriloogika |
| `UserRepository` | Kasutajate andmebaasipäringud |
| `LocationMapper` | Asukoha entity ↔ DTO teisendus |
| `LocationResponseDto` | Asukoha andmed API vastuses |
| `PrimaryKeyNotFoundException` | Kirjet ei leitud andmebaasist |
| `ErrorResponse` | Veakoodid ja -teated enum-ina |
| `RestExceptionHandler` | Globaalne veakäsitlus |

**Paketid:**

```
controller/
  city/
    dto/
  location/
    dto/
  login/
    dto/
service/
persistence/
  entity/
  repository/
  mapper/
infrastructure/
  exception/
```

Iga ressurss (city, location, login) on omaette paketi all controlleris. Kõik entity-d, repository-d ja mapper-id on `persistence/` all.

---

## 12. Koodi lugemise järjekord

Kui sa avad uue projekti ja ei tea, kust alustada, järgi seda järjekorda:

1. **Alusta controllerist** – vaata, millised endpoint-id on olemas ja milliseid URL-e nad kuulavad
2. **Mine service-klassi** – controller kutsub service-meetodit; leia see meetod
3. **Vaata repository-t** – service kasutab repository-t andmete saamiseks
4. **Vaata DTO-sid** – mis andmed tulevad sisse ja mis lähevad välja?
5. **Vaata mapperit** – kuidas entity muutub DTO-ks?
6. **Vaata exception klasse** – mis vead võivad tekkida?
7. **Vaata RestExceptionHandler-it** – kuidas vead kasutajale tagastatakse?

---

## 13. Uue funktsionaalsuse lisamise muster

Näide: lisame võimaluse uut asukohta luua.

**1. Loo Request DTO:**
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationRequestDto {
    private String locationName;
    private Integer cityId;
}
```

**2. Lisa controllerisse endpoint:**
```java
@PostMapping("/locations")
public void createLocation(@RequestBody LocationRequestDto dto) {
    locationService.createLocation(dto);
}
```

**3. Lisa service-meetod:**
```java
public void createLocation(LocationRequestDto dto) {
    Location location = locationMapper.toEntity(dto);
    location.setStatus(ACTIVE);
    locationRepository.save(location);
}
```

**4. Lisa mapper-meetod:**
```java
Location toEntity(LocationRequestDto dto);
```

**5. Lisa vajadusel exception:**
```java
// Kui city ei eksisteeri
cityRepository.findById(dto.getCityId())
    .orElseThrow(() -> new PrimaryKeyNotFoundException(CITY_NOT_FOUND));
```

**Kontrolli lõpus:**
- Kas igal kihil on oma vastutus?
- Kas meetodid on lühikesed?
- Kas nimed on lihtsad ja arusaadavad?

---

## 14. Praktilised reeglid õpilasele

- **Üks meetod = üks tegevus** – kui meetod teeb kahte asja, jaga see kaheks
- **Controllerisse ei kirjutata äriloogikat** – controller ainult võtab vastu ja tagastab
- **Service juhib loogikat** – otsused, kontrollid ja arvutused käivad siin
- **Repository suhtleb andmebaasiga** – mujal SQL-i ega JPQL-i ei kirjutata
- **Mapper teisendab objekte** – entity ↔ DTO teisendus on mapperi töö
- **DTO liigub API kaudu** – entity ei läheta API-le otse
- **Meetodi nimi olgu lihtne** – `login`, mitte `performUserAuthentication`
- **Ära tee meetodit liiga pikaks** – kui kood on pikem kui ekraan, jaga see väiksemateks
- **Kasuta olemasoleva projekti stiili** – vaata kuidas teised klassid on kirjutatud
- **Ära leiuta uut mustrit** – kui projektis on muster olemas, kasuta seda

---

## 15. Hea ja halb näide

### Näide 1: Liiga pikk meetodinimi

**Halb:**
```java
public LoginResponseDto performUserAuthenticationAndReturnLoginResponseDto(String username, String password) {
    ...
}
```

**Parem:**
```java
public LoginResponseDto login(String username, String password) {
    ...
}
```

---

### Näide 2: Äriloogika controlleris

**Halb:**
```java
@PostMapping("/login")
public LoginResponseDto login(@RequestBody LoginRequestDto dto) {
    User user = userRepository.findByUsername(dto.getUsername())
        .orElseThrow(() -> new RuntimeException("Vale kasutajanimi"));
    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
        throw new RuntimeException("Vale parool");
    }
    return new LoginResponseDto(user.getId(), user.getUsername());
}
```

**Parem:**
```java
@PostMapping("/login")
public LoginResponseDto login(@RequestBody LoginRequestDto dto) {
    return loginService.login(dto);
}
```

---

### Näide 3: Entity tagastamine DTO asemel

**Halb:**
```java
@GetMapping("/locations")
public List<Location> getLocations() {
    return locationRepository.findAll();
}
```

**Parem:**
```java
@GetMapping("/locations")
public List<LocationResponseDto> getLocations() {
    return locationService.getLocations();
}
```

Entity tagastamine avalikustab andmebaasi struktuuri ja võib paljastada tundlikke andmeid.

---

### Näide 4: Liiga pikk service-meetod

**Halb:**
```java
public void createLocation(LocationRequestDto dto) {
    if (dto.getLocationName() == null || dto.getLocationName().isBlank()) {
        throw new RuntimeException("Nimi on kohustuslik");
    }
    Optional<City> city = cityRepository.findById(dto.getCityId());
    if (city.isEmpty()) {
        throw new RuntimeException("Linn ei leitud");
    }
    Location location = new Location();
    location.setName(dto.getLocationName());
    location.setCity(city.get());
    location.setStatus("A");
    locationRepository.save(location);
}
```

**Parem:**
```java
public void createLocation(LocationRequestDto dto) {
    City city = findCity(dto.getCityId());
    Location location = locationMapper.toEntity(dto);
    location.setCity(city);
    location.setStatus(ACTIVE);
    locationRepository.save(location);
}

private City findCity(Integer cityId) {
    return cityRepository.findById(cityId)
        .orElseThrow(() -> new PrimaryKeyNotFoundException(CITY_NOT_FOUND));
}
```

---

## 16. Kokkuvõte

Hea kood on **lihtne ja loetav**.

Projekti stiili peamised põhimõtted:

- **Lihtsus** – iga klass ja meetod teeb ühte asja, lihtsalt
- **Loetavus** – kood loetakse nagu lause, ilma seletusteta
- **Ühtlane stiil** – kõik klassid on kirjutatud samas mustris
- **Väikesed meetodid** – lühike meetod on lihtne mõista ja testida
- **Selged vastutused** – iga kiht teab oma rolli, ei sega teiste töösse
- **Olemasoleva mustri järgimine** – enne uut koodi kirjutamist vaata, kuidas sarnane asi juba tehtud on

> Kui kahtled, vaata kuidas õpetaja sarnase asja lahendas – ja tee samamoodi.

---

*Juhend on koostatud bank40back projekti koodianalüüsi põhjal.*