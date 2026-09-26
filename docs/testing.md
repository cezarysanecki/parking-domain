# Testy: konwencje

Wszystkie testy piszemy w **Spocku** (Groovy 4). W katalogach testowych nie ma kodu Java.
Całość uruchamia `mvn verify`, tak jak w CI. Surefire uruchamia wtedy testy jednostkowe,
akceptacyjne i integracyjne, a te ostatnie wymagają Dockera.

## Rodzaje testów

| Rodzaj | Katalog | Nazwa klasy | Spring | Dane |
|---|---|---|---|---|
| Jednostkowy | `src/test/groovy/<pakiet testowanej klasy>/` | `<Klasa>Spec` | nie | obiekty domenowe, `_InMemory`, mocki Spocka |
| Akceptacyjny | `src/acceptance-tests/groovy/pl/cezarysanecki/parkingdomain/` | `<Scenariusz>AcceptanceSpec extends BaseAcceptanceSpec` | tak, profil `local` | repozytoria in-memory |
| Integracyjny | `src/integration/groovy/<pakiet modułu>/` | `<Temat>IntegrationSpec extends BaseIntegrationSpec` | tak, profil `integration` | Postgres z Testcontainers |
| Pomocnicze | `src/test/groovy/pl/cezarysanecki/parkingdomain/` | np. `RandomTestUtils`, bez sufiksu `Spec` ani `Test` | | |

- Każda klasa z testami **musi** mieć nazwę kończącą się na `Spec`. Surefire uruchamia tylko
  `**/*Spec` i `**/*Test`, więc klasa o innej nazwie zostanie po cichu pominięta. Wzorzec `*Test`
  jest w konfiguracji tylko po to, żeby przypadkowy test nie zniknął bez śladu. Nie oznacza, że
  takie nazwy są dozwolone.
- Klasy bazowe są `abstract`, więc nie są uruchamiane.
- Wszystkie trzy katalogi kompiluje `gmavenplus-plugin` w jednej kompilacji. Specyfikacje
  akceptacyjne i integracyjne mogą więc używać pomocników z `src/test/groovy`. Nie dodajemy kodu
  Java do katalogów testowych. Gdyby był potrzebny, trzeba włączyć wspólną kompilację
  (`generateTestStubs`/`removeTestStubs`).
- Testy jednostkowe leżą w **tym samym pakiecie** co testowana klasa. Agregaty i repozytoria są
  package-private, a w ten sposób są dostępne bez zmian w kodzie produkcyjnym.

## Styl Spocka

- Nazwa cechy to zdanie po angielsku opisujące regułę biznesową. Używamy bloków
  `given`/`when`/`then` i `and:`. `expect` stosujemy tylko do czystych funkcji.
- Progi i granice zapisujemy w tabelach `where:`, a wynik oddzielamy `||`. Tabela zawsze
  zawiera próg−1, próg i próg+1.
- **`thrown()` obejmuje cały blok `when` razem z jego `and:`.** W `when` umieszczamy tylko to
  jedno wywołanie, które ma rzucić wyjątek. Całe przygotowanie trafia do `given:`/`and:` albo do
  osobnej pary `when`/`then: noExceptionThrown()`. Wywołanie `orElseThrow()` wewnątrz `when` z
  `thrown(Exception)` ukrywa błędy.
- Wynik logiczny sprawdzamy jawnie, np. `result == true`, a nie przez samo `result`, bo Groovy
  traktuje jako prawdę każdą niepustą wartość.
- Mockujemy (`Mock()`, `1 * x.call()`, `x.call() >> wartość`) tylko współpracowników spoza
  testowanej klasy, którzy mają efekty uboczne, czyli serwisy zewnętrzne i fasady. Obiektów
  domenowych nie mockujemy.
- Wartości konfiguracji (progi, minuty) trzymamy w testach jednostkowych jako stałe `static final`
  z komentarzem wskazującym klucz w `application.yaml`.
- Testy opisują **obecne** zachowanie. Zmiana reguły biznesowej oznacza zmianę testu w tym samym
  commicie.

## Testy akceptacyjne: `BaseAcceptanceSpec`

- Adnotacje `@SpringBootTest` i `@ActiveProfiles("local")`. Wszystkie specyfikacje akceptacyjne
  korzystają z **jednego** kontekstu Springa, który jest cache'owany. Nie dodajemy w nich
  `@MockBean`, `@TestPropertySource` ani `@DirtiesContext`. Jeśli potrzebne są inne właściwości,
  tworzymy osobną abstrakcyjną bazę z `@SpringBootTest(properties = [...])`.
- `setup()` robi dwie rzeczy. Czyści `InMemoryRepositories.clearAll()`, co usuwa też dane z
  `LocalInitialData`. Ustawia zegar na `CURRENT_DATE`, czyli 2020-10-10 00:00 w strefie
  systemowej. Upływ czasu symulujemy przez `dateProvider.passHours/passMinutes`. Nie używamy
  `Instant.now()`.
- Konkretną godzinę ustawiamy przez `currentTimeIs(hour, minute = 0)`. Ten pomocnik liczy czas
  bezwzględnie od `CURRENT_DATE`, a `hour >= 24` oznacza następny dzień, np. `currentTimeIs(25)`
  to 01:00 następnego dnia.
- **Miejsca można zajmować tylko w godzinach 05:00–24:00** (`ParkingOpeningHours`). Domyślne 00:00
  jest poza tym oknem. Każda specyfikacja, która zajmuje miejsce (zwykle, bez konta albo z
  rezerwacją), musi najpierw ustawić czas, np. `def setup() { currentTimeIs(DURING_OCCUPYING_HOURS) }`
  albo `currentTimeIs(h, m)` w danym kroku. Bez tego zajęcie zostanie odrzucone.
- Każdą nową mapę in-memory trzeba dopisać do `InMemoryRepositories.clearAll()`.
- Pomocniki: `addParkingSpot(capacity = 4, category = Gold)` i
  `registerClient(type = INDIVIDUAL, phone = losowy)`. Wspólny krok przenosimy do bazy, gdy
  używają go co najmniej dwie specyfikacje.
- Wstrzykujemy tylko publiczne API, czyli `*Facade` i `*UseCase`. Joby Quartz wywołujemy przez
  ich use case.
- Specyfikacji nie uruchamiamy równolegle, bo stan in-memory jest statyczny.

## Testy integracyjne: `BaseIntegrationSpec`

- Adnotacje `@SpringBootTest` i `@ActiveProfiles("integration")`. Działają repozytoria JOOQ,
  Liquibase i Postgres `postgres:16.4`.
- Kontener jest singletonem: startuje raz na JVM w bloku `static {}`, a sprząta go Ryuk.
  `@DynamicPropertySource` w bazie ustawia `spring.datasource.*`. Nie używamy
  `@Testcontainers`/`@Container` ani `@Shared` dla kontenera.
- Danych nie czyścimy globalnie. Każda cecha używa losowych `UUID`. Jeśli cecha potrzebuje
  pustej tabeli, czyści ją w swoim `setup()` przez `DSLContext`.
- Specyfikacje leżą w pakiecie modułu, żeby miały dostęp do package-private repozytoriów.
- W kontekście integracyjnym działa prawdziwy zegar (`ProductionDateProvider`). **Nie wołamy
  kodu zależnego od godziny**, np. `ParkingFacade.occupy*`, use case'ów zajmowania, przypomnień
  i odholowania, bo wynik zależałby od godziny uruchomienia testu (00:00–05:00 → odrzucenie).
  Zapis i odczyt sprawdzamy bezpośrednio na repozytoriach, jak w
  `OptimisticLockingForOccupationIntegrationSpec` i `FindingAllOccupationsIntegrationSpec`.

## Konfiguracja testów

`src/test/resources` **nie** trafia dziś do `target/test-classes`. To znany błąd konfiguracji
`maven-resources-plugin`, opisany w follow-upach w [planie](plan/README.md). Właściwości testowe
ustawiamy więc przez `@SpringBootTest(properties = ...)` albo `@DynamicPropertySource`. Nie
dodajemy konfiguracji testowej do `src/main/resources`. Jeśli potrzeba czegoś więcej, najpierw
naprawiamy plugin w osobnej zmianie.
