# Plan dostosowania kodu do README

> Źródłem prawdy jest [główny README](../../README.md). Ten dokument opisuje ogólnie, co trzeba
> zmienić w kodzie, żeby był z nim zgodny. To plan wysokiego poziomu, a nie specyfikacja
> implementacji.

## 1. Godziny działania parkingu

README: parking działa od 5:00 do 1:00, od 1:00 do 5:00 trwa przerwa techniczna, a zajmować
miejsca można tylko do 12pm.

Stan obecny: kod nigdzie nie sprawdza godzin działania.

Do zrobienia:
- Wprowadzić do domeny pojęcie godzin otwarcia i przerwy technicznej.
- Odrzucać zajmowanie miejsc (także z rezerwacją i bez konta) poza dozwolonymi godzinami.
- Wymusić koniec zajmowania o 12pm.
- Ostatnią godzinę (do 1:00) przeznaczyć na przypomnienia o zwolnieniu miejsca i odholowanie,
  np. wymuszone zwolnienie pozostałych zajęć.

> ⚠️ Do wyjaśnienia: „12pm” to po angielsku południe, ale z kontekstu (przypomnienia w ostatniej
> godzinie przed 1:00) wynika raczej północ. Trzeba to potwierdzić przed implementacją.

## 2. Sprzątanie w przerwie technicznej

README: miejsca parkingowe można sprzątać w przerwie technicznej (1:00–5:00).

Stan obecny: `calling-external-cleaning-service-policy-job` uruchamia się o 13:00.

Do zrobienia:
- Przestawić harmonogram wzywania serwisu sprzątającego na okno przerwy technicznej.
- Rozważyć regułę domenową, która nie pozwala wezwać sprzątania poza przerwą (dziś można to
  zrobić przez `/local/call-cleaning` o dowolnej porze).

## 3. Próg „brudnego” miejsca

README: serwis sprzątający jest wzywany, gdy 10 miejsc jest brudnych, *„It means 20 releases”*.

Stan obecny: miejsce jest brudne dopiero po 20 zwolnieniach **tego jednego miejsca**, więc
sprzątanie wymaga co najmniej 200 zwolnień.

Do zrobienia:
- Ustawić próg tak, żeby 10 brudnych miejsc odpowiadało 20 zwolnieniom (czyli miejsce jest
  brudne po 2 zwolnieniach).
- Poprawić testy akceptacyjne sprzątania, żeby sprawdzały tę regułę.

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
- Przepisać istniejące testy akceptacyjne i integracyjne na Spocka (albo przynajmniej pisać nowe
  w Spocku).
- Każdy punkt tego planu pokryć testami.

## 7. JPA

README: na liście technologii jest Spring Data JPA.

Stan obecny: dane zapisuje wyłącznie JOOQ, a z JPA używany jest tylko `EntityNotFoundException`.

> ⚠️ Do decyzji: przenieść część zapisu (np. moduł `management`) na JPA, czy usunąć JPA z README.
> README jest źródłem prawdy, ale ten punkt dotyczy wyboru narzędzi, a nie reguł biznesowych.

## Proponowana kolejność

1. Testy w Spocku dla obecnej logiki (punkt 6). To siatka bezpieczeństwa na kolejne zmiany.
2. Szybkie poprawki konfiguracji: próg brudnego miejsca i harmonogram sprzątania (punkty 2 i 3).
3. Godziny działania parkingu (punkt 1), po wyjaśnieniu „12pm”.
4. Opłaty i cennik (punkt 4).
5. Typy pojazdów (punkt 5).
6. Decyzja w sprawie JPA (punkt 7).
