[![Build](https://github.com/cezarysanecki/parking-domain/actions/workflows/maven.yml/badge.svg)](https://github.com/cezarysanecki/parking-domain/actions/workflows/maven.yml)
[![codecov](https://codecov.io/gh/cezarysanecki/parking-domain/graph/badge.svg?token=IWUFNCKYCN)](https://codecov.io/gh/cezarysanecki/parking-domain)

![Parking domain](./docs/public/logo.png)

# Parking Domain - [contest 100 commits](https://100commitow.pl/)

Learning domain before implementation is the best choice that you can make.

Parking domain project is created to show how from naive and anemic model we can
evolve into better solution. Of course, I will try to do that! We will see if I can handle it. 😎

This is just experiment for **educational purposes**. Of course, I will define MVP etc., but this domain is
not the goal in itself.

**Let jump into the code and see what will happen!**

## Used technologies (for now)

- [Java 21](https://openjdk.org/projects/jdk/21/)
- [Spring Boot 3](https://spring.io/projects/spring-boot)
  - Web
  - JPA
- [Maven](https://maven.apache.org/)
- [H2 database](https://www.h2database.com/html/main.html)
- [Lombok](https://projectlombok.org/)
- [Bruno](https://www.usebruno.com/)
- [vavr](https://docs.vavr.io/)
- [Awaitility](http://www.awaitility.org/)

## FAQ

### Is it production-ready?

Nope. For now, it is just conception. We will see where we will get with this. For sure, I will learn a lot.
I hope, you also!

### Why parking domain?

Do not know exactly... I tried to implement voting and vaccination domain, but writing ugly code (for education purposes)
discouraged me from them. I wrote about that on [my blog \[PL\]](https://cezarysanecki.pl/2024/02/13/prezentacja-prostej-domeny/).
I hope that coding anemic model for parking domain will not end like previous examples. And I heard about this domain in
one of the [DevMentors YouTube video](https://www.youtube.com/@DevMentorsPL/videos)... and I think why not!

### Why Java and Spring?

Because it is my "native" background. And it is not my aim to learn new language. I want to learn how to model domain
having legacy code and learn tools from JVM ecosystem (but not only).

## MVP

- [X] 3-layer app with anemic model (without tests)
  - [X] Occupation logic
  - [X] Reservation logic
  - [ ] ~~Fee logic~~
- [ ] Refactoring to add unit tests
- [ ] Looking for deep model (modules)
  - [X] Do simple Event Storming session
- [ ] Add other types of requirements
  - [ ] Cleaning
  - [ ] Maintenance
  - [ ] Loyalty points
  - [ ] Customer recovery
- [ ] Add CQRS
- [ ] ...

## Business context

### Parking on parking spots

- Basic functionality for parking
- Parking is available 24/7
- Parking spots have their own identity
- Parking spots can be occupied (for now) by: cars, motorcycles and scooters
- Every parking spot has its own capacity (for now it is 4 units)
- On parking spot can park different combinations of vehicle types f.ex.
  - One vehicle (1 x 4)
  - Two motorcycles (2 x 2)
  - One motorcycles and two scooters (1 x 2 + 2 x 1)
  - Four scooters (4 x 1)

### Reservations

- Client can reserve:
  - any spot f.ex.
    - all space of spot for vehicle
    - half of spot for motorcycle
  - for additional fee chosen whole parking spot
- Client can reserve parking spot 
  - minimum one week earlier
  - maximum size hours before
- Reservation can last for 10 hours
- Clients reserve spots from main bank
- Reservations cannot exceed 50% of the available main bank

### Cleaning parking spots

- Because parking is available 24/7, we cannot schedule a technical break to clean the spots
- We need to track when the spots were last cleaned and mark them as "out of order" when a specified period of time has passed since the last cleaning
- When 5 spots are marked for cleaning, we request the cleaning service

### Repair/Maintenance of parking spots

- Almost the same as cleaning parking spots
- It may not be as widespread as cleaning, but occasionally, we may need to change the parking surface
- Clients can report a malfunction or issue with a parking spot
  - They will receive loyalty points 
  - If they are given access to the broken spot, they should also be provided access to another parking spot from the emergency bank

### Fee

- Standard usage of parking spot:
  - 15 minutes - free
  - 1 hour - 5PLN
  - 2 hours - 9PLN
  - 4 hours - 17PLN
  - one day - 40PLN
  - each successive day - 50PLN
- Subscription:
  - dedicated spot bank
  - exclusive parking spot
  - monthly - 1000PLN
- Discount:
  - 1-10% for:
    - reporting issues
    - being loyal client (park for more than half of the days in a month)
    - recommendation

### Loyalty points

- Clients can receive loyalty points for reporting issues or being "long-term" client
- "Long-term" client is someone who park for more than half of the days in a month - 3 months in a row
- Loyalty points can be redeemed to receive gifts

### Customer recovery

- A customer labeled as 'difficult' has the right to assert their innocence
- If the verifier accepts the justification, a discount should be provided to that person as compensation

## Event Storming session

### Registration new parking spot

![Registration new parking spot](/docs/public/es/es_registration_parking_spot.png)

There is possibility to register new parking spots in system f.ex. when we create new ones.
After registration, it lends in overall parking spots bank.

**Hot spot:** Need to consider if there is a need to provide parking spots for disabled people.

### Parking vehicle

![Parking vehicle](/docs/public/es/es_parking_vehicle.png)

Need to find available parking spot for specified vehicle type. If it fails we will try to find another one 
up to 3 times (because one of them could vanish from bank just a moment ago). If there is success just assign
this parking spot to vehicle.

**Hot spot:** What if there would like to park huge vehicle which requires f.ex. 2 parking spots? 
For now, we skip this case.

### Release parking spot

![Release parking spot](/docs/public/es/es_release_parking_spot_and_cleaning.png)

Simple scenario, if someone did not pay then there is refusal to release parking spot.
But if there was payment we put parking spot back to bank, add loyalty points to user and increase counter
responsible for parking spot's clearance. When parking spot clearance counter reach 5 then we mark this spot as needed 
clearance. Of course, it is removed from available bank. When at least 10 parking spots and these spots are free
(there are no vehicles on it) we call external cleaning service. Unless it was already called.

After cleaning, parking spots go back to available bank, and we tick off that external clearance has been done.

### Reservation

![Reserve parking spot](/docs/public/es/es_reservation.png)

We can reserve parking spot for specified date f.ex. to go to cinema. There are two conditions: is any parking spot
available and client is marked as unwanted. If not we reject possibility to reserve spot.

**Hot spot:** Can we reserve more than one parking spot (group reservation)?

### Reservation

![Reserve parking spot](/docs/public/es/es_releasing_reserved_parking_spot.png)

2 hours before a reservation, we inform clients that they need to vacate their spot.
We reserve another parking spot for them. If a client notifies us that they've freed up the parking spot, 
we reward them with loyalty points for their assistance. 1 hour before the reservation, 
we check if the spot has been released. If not, we relocate the vehicles and charge the users of those vehicles. 
If yes, we simply remove the parking spot from the available parking bank.

### Punishing client

![Punishing client](/docs/public/es/es_punishing_for_unused_reservation.png)

15 minutes after a reservation, we check if the client has not parked in the reserved spot. 
If not, we add this spot to the available bank and notify the client. If this is the 2nd or 3rd warning, 
we deduct loyalty points. If it's the 4th warning, we mark the client as unwanted.

## Educational goals

I would like to learn the following technologies/tools. I need to consider which of them to use 
in the current project during the MVP phase. Certainly, I will create an anemic model using a 3-layer architecture.

- Very ugly 3-layer app (anemic models)
- Refactoring
  - Unit/Integration tests (Protect business logic) + TestContainers if needed
  - Change logic to add more unit tests
  - Split domains/hexagonal architecture
- Save form vs Save state per field
- Security (OAuth, SSL, CORS etc.)
- Event Sourcing
- Microservices
  - Service discovery
  - Circuit breaker
  - Tenants?
- Functional approach
- Kubernetes/Docker
- Kotlin
- Kafka
- Read Model/CQRS
- Slack notifications
- JOOQ
- Micronaut/Quarkus
- HotWire
- Observability
- Profiling
- Documentation
- Bi-temporal event
- jMeter

## Scratches

![Scratch for parking](./docs/public/discovery_scratch.png)

## Inspirations

- [DevMentors YouTube video](https://www.youtube.com/@DevMentorsPL/videos)
- [Library by example](https://github.com/ddd-by-examples/library)
