# Plan dostosowania kodu do README

> Źródłem prawdy jest [główny README](../../README.md). Ten dokument opisuje ogólnie, co trzeba
> zmienić w kodzie, żeby był z nim zgodny. To plan wysokiego poziomu, a nie specyfikacja
> implementacji.

## Podjęte decyzje

| Temat | Decyzja |
|---|---|
| „12pm” w README | Chodzi o **północ (24:00)**. W README trzeba poprawić zapis na „12am / midnight”. |
| Próg brudnego miejsca | Miejsce jest brudne po **20 zwolnieniach tego miejsca** (zostaje jak w kodzie). Sprzątanie wzywamy przy 10 brudnych miejscach. |
| JPA | **Usunąć** JPA z README i z `pom.xml`. Jedyną warstwą zapisu zostaje JOOQ. |
| Spock | Przepisać **wszystkie** testy na Spocka (akceptacyjne i integracyjne) i dodać testy jednostkowe. |
| Godzina sprzątania | Serwis sprzątający wzywamy o **1:30**, w przerwie technicznej. Uzasadnienie jest w README. |
| Pojazdy po północy | Między 0:00 a 1:00 **przypomnienie** o zwolnieniu miejsca, a o 1:00 **wymuszone zwolnienie** (odholowanie). |

## 1. Godziny działania parkingu

README: parking działa od 5:00 do 1:00, od 1:00 do 5:00 trwa przerwa techniczna, a zajmować
miejsca można tylko do 12pm.

Stan obecny: kod nigdzie nie sprawdza godzin działania.

Do zrobienia:
- Wprowadzić do domeny pojęcie godzin otwarcia i przerwy technicznej.
- Odrzucać zajmowanie miejsc (także z rezerwacją i bez konta) poza dozwolonymi godzinami.
- Wymusić koniec zajmowania o północy (24:00).
- Między 0:00 a 1:00 wysyłać przypomnienia o zwolnieniu miejsca.
- O 1:00 wymuszać zwolnienie wszystkich pozostałych zajęć (odholowanie).
- W README poprawić „until 12pm” na „until 12am (midnight)”.

Zrobione: godziny są stałymi w `shared.ParkingOpeningHours`. Zajmować (także bez konta i z
rezerwacją) można tylko 5:00–24:00, między 0:00 a 1:00 job wysyła przypomnienia o zwolnieniu
miejsca, a o 1:00 job zwalnia siłą pozostałe zajęcia (`Reason.PARKING_CLOSED`, bez opłaty).
README mówi teraz „until 12am (midnight)”.

## 2. Sprzątanie w przerwie technicznej

README: miejsca parkingowe można sprzątać w przerwie technicznej (1:00–5:00).

Stan obecny: `calling-external-cleaning-service-policy-job` uruchamia się o 13:00.

Do zrobienia:
- Przestawić cron `calling-external-cleaning-service-policy-job` z `0 0 13 * * ?` na `0 30 1 * * ?`
  (1:30).
- W README, w sekcji *Cleaning parking spots*, dopisać godzinę i uzasadnienie:
  ```
  - External cleaning service is called at 1:30am
  - [WARNING] It may be harder to find a cleaning company, because not every company is willing to work at night
  ```
- Rozważyć regułę domenową, która nie pozwala wezwać sprzątania poza przerwą (dziś można to
  zrobić przez `/local/call-cleaning` o dowolnej porze).

Zrobione: job sprzątania rusza o 1:30, a `CleaningFacade.callCleaning()` zwraca `Rejection` poza
przerwą techniczną [1:00, 5:00), więc sprzątania nie da się wezwać w godzinach pracy parkingu (także
przez `/local/call-cleaning`). Godziny przerwy pochodzą z `ParkingOpeningHours.isTechnicalBreak`.

## 3. Próg „brudnego” miejsca

README: serwis sprzątający jest wzywany, gdy 10 miejsc jest brudnych, *„It means 20 releases”*.

Decyzja: miejsce jest brudne po **20 zwolnieniach tego miejsca**, czyli tak jak było w kodzie
(`number-of-drives-away-to-consider-parking-spot-dirty: 20`). Sprzątanie wzywamy przy 10 brudnych
miejscach.

Do zrobienia:
- Zostawić próg 20 i pokryć go testami na granicy (19 → czyste, 20 → brudne) w obu implementacjach.

Zrobione: miejsce jest brudne po 20 zwolnieniach (`>=`, w obu implementacjach). Próg pilnują testy
jednostkowe, akceptacyjne i `DirtyParkingSpotIntegrationSpec` (Postgres/JOOQ).

## 4. Opłaty

README: za niewykorzystaną rezerwację klient płaci 50$, a opłaty powinny wynikać z cennika.

Stan obecny: `FeeEventHandler` tylko loguje kwotę `50.00` i nic nie nalicza.

Do zrobienia:
- Faktycznie naliczać i zapisywać opłatę za niewykorzystaną rezerwację.
- Wprowadzić cennik (choćby z jedną pozycją), z którego brana jest kwota.

## 5. Typy pojazdów

README: miejsca zajmują samochody (4 jednostki), motocykle (2) i skutery (1).

Stan obecny: API przyjmuje dowolne `spotUnits` (potęgę dwójki) i nie zna typów pojazdów.

Do zrobienia:
- Wprowadzić typ pojazdu, z którego wynika liczba jednostek.
- Przejść w API i zgłoszeniach z surowych jednostek na typ pojazdu.

## 6. Testy

README: testy jednostkowe i integracyjne, pisane w **Spocku**, z Testcontainers.

Stan obecny: w repo nie ma żadnego pliku `.groovy`, a testy są w JUnit 5. `src/test/java` nie ma
testów jednostkowych, a Spock jest tylko w `pom.xml`.

Do zrobienia:
- Dodać testy jednostkowe logiki domenowej w Spocku (`ParkingSpot`, `Occupant`, `Requester`,
  `SpotUnits`, `TimeSlot`, reguły sprzątania i opłat).
- Przepisać wszystkie istniejące testy na Spocka: 8 akceptacyjnych i 1 integracyjny
  (z Testcontainers).
- Skonfigurować kompilację Groovy dla wszystkich zestawów źródeł (`src/test`, `src/integration`,
  `src/acceptance-tests`).
- Każdy punkt tego planu pokryć testami.

Zrobione: testy są w Spocku (jednostkowe, akceptacyjne, integracyjne). Konwencje, których trzymają
się kolejne punkty, są w [docs/testing.md](../testing.md).

## 7. JPA

README: na liście technologii jest Spring Data JPA.

Stan obecny: dane zapisuje wyłącznie JOOQ, a z JPA używany jest tylko `EntityNotFoundException`.

Do zrobienia:
- Usunąć `spring-boot-starter-data-jpa` z `pom.xml` i sekcję `spring.jpa` z konfiguracji.
- Zastąpić `jakarta.persistence.EntityNotFoundException` własnym wyjątkiem.
- Upewnić się, że transakcje nadal działają (menedżer transakcji dla JDBC/JOOQ).
- Usunąć JPA z listy technologii w README.

Zrobione: JPA usunięte, jedyną warstwą zapisu jest JOOQ. Not-found rzuca własny wyjątek
`commons.EntityNotFound`, a transakcjami w profilu domyślnym zarządza `JdbcTransactionManager`.
To, że zapisy JOOQ należą do transakcji fasady, sprawdza `ReleasingParkingSpotIsAtomicIntegrationSpec`.

## Do zrobienia później (follow-upy)

- `maven-resources-plugin` ma `outputDirectory` i `resources` ustawione na poziomie pluginu, więc
  `testResources` kopiuje `src/main/resources` do `target/classes`, a `src/test/resources` nigdy
  nie trafia na classpath testów. Trzeba przenieść konfigurację do execution `copy-resources`
  (osobna zmiana, bo dotyczy budowania zasobów produkcyjnych).
- `InMemoryCleaningRepository` korzysta ze statycznej `InMemoryRepositories.CLEANING_DATABASE`
  współdzielonej z kontekstem akceptacyjnym; rozważyć lokalną mapę (zmiana w `src/main`; naturalny
  moment: zadania 2/3 o sprzątaniu).

## Proponowana kolejność

1. Testy w Spocku dla obecnej logiki (punkt 6). To siatka bezpieczeństwa na kolejne zmiany.
2. Szybkie poprawki konfiguracji: próg brudnego miejsca i harmonogram sprzątania (punkty 2 i 3).
3. Godziny działania parkingu (punkt 1).
4. Opłaty i cennik (punkt 4).
5. Typy pojazdów (punkt 5).
6. Usunięcie JPA (punkt 7). Można to zrobić wcześniej, bo jest niezależne.
