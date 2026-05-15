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
