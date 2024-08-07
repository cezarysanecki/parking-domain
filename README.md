[![Build](https://github.com/cezarysanecki/parking-domain/actions/workflows/maven.yml/badge.svg)](https://github.com/cezarysanecki/parking-domain/actions/workflows/maven.yml)
[![codecov](https://codecov.io/gh/cezarysanecki/parking-domain/graph/badge.svg?token=IWUFNCKYCN)](https://codecov.io/gh/cezarysanecki/parking-domain)

![Parking domain](./docs/public/logo.png)

# Parking Domain - [contest 100 commits](https://100commitow.pl/)

> Choosing to learn about the domain before implementing is the optimal decision you can make.

The Parking Domain project aims to demonstrate the evolution from a rudimentary and limited model to a more
sophisticated solution. I'm committed to undertaking this challenge and seeing if I can successfully navigate it. 😎

This is just experiment which serves solely for **educational purposes**. While I will define the MVP and other
necessary aspects, it's important to note that this domain is not the ultimate goal itself.

**Let jump into the code and see what will happen!**

## Used frameworks/libraries/tools

Main:

- [Java 21](https://openjdk.org/projects/jdk/21/)
- [Maven](https://maven.apache.org/)
- [Spring Boot 3](https://spring.io/projects/spring-boot)
    - [Web](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html)
    - [JPA](https://spring.io/projects/spring-data-jpa)
- [Lombok](https://projectlombok.org/)
- [Quartz](https://www.quartz-scheduler.org/)

Tests:

- [H2 database](https://www.h2database.com/html/main.html)
- [vavr](https://docs.vavr.io/)
- [Awaitility](http://www.awaitility.org/)

Tools:

- [Bruno](https://www.usebruno.com/)

## Lessons learnt

1. _Ideal code does not exist!_
1. _Make analysis before coding!_
1. _Tests are great, but when you are prototyping they are slowing you down!_

## Assumptions

1. Duplication of chosen logic is sometimes good f.ex. validation of exceeding parking spot space (stable requirement).

## FAQ

_(Cricket...)_

### Is it production-ready?

Nope. At this stage, it's purely conceptual. We'll see where this experiment takes us. Undoubtedly, there's much to
learn along the way, for both of us!

### Why parking domain?

Don't know. I experimented with implementing the voting and vaccination domains, deliberately writing ugly code for
educational purposes. However, the outcome left me feeling discouraged. I've shared this experience
on [my blog \[PL\]](https://cezarysanecki.pl/2024/02/13/prezentacja-prostej-domeny/). I'm hopeful that coding an anemic
model for the parking domain won't result in the same challenges as my previous attempts. Interestingly, I came across
this domain in one of the [DevMentors YouTube videos](https://www.youtube.com/@DevMentorsPL/videos), and I thought, "Why
not give it a try?"

### Why Java and Spring?

Because it's my "native" background, and my goal isn't to learn a new language. I aim to learn how to model a domain
with existing legacy code and explore tools from the JVM ecosystem (but not exclusively).

## MVP

- [X] 3-layer app with anemic model (without tests)
    - [X] Occupation logic
    - [X] Requests logic
    - [X] Reservations logic
- [X] Refactoring to add unit tests
- [X] Looking for deep model (modules)
    - [X] Do simple Event Storming session
- [ ] Add other types of requirements
    - [ ] Fee logic
    - [ ] Cleaning
    - [ ] Maintenance
    - [ ] Loyalty points
    - [ ] Customer recovery
- [ ] Add CQRS
- [ ] ...

## Business context

This software is being developed for a client who owns a parking facility near a shopping center and offices. Presently,
anyone can park there for a small fee. However, this setup is frustrating for both the business and its clients. The
business earns only a modest amount, while clients struggle to find parking due to constant occupations.

The business aims to address this situation by implementing software that enables users to reserve parking spots for
specific times of the day. Additionally, the software will optimize parking utilization by allowing multiple small
vehicles to occupy a single parking spot. Further requirements are outlined below.

### Parking

- Parking is available **since 5am until 1am**
  - Clients can only occupy parking spots **until 12pm**
- There is technical break **since 1am until 5am**
- Parking spots have their own identity
- Parking spots are assigned to groups: BRONZE, SILVER, GOLD (attractiveness spot)
- Parking spots can be occupied (for now) by: cars, motorcycles and scooters
- Every parking spot has its own capacity (for now it is 4 units)
- On parking spot can park different combinations of vehicle types f.ex.
    - One vehicle (1 x 4)
    - Two motorcycles (2 x 2)
    - One motorcycles and two scooters (1 x 2 + 2 x 1)
    - Four scooters (4 x 1)

### Requesting parking spot

- There is limit for requests according to client type:
    - individual - only one
    - business - at most five
- Client can request any parking spot
    - from class (BRONZE, SILVER, GOLD)
    - for specified size (1, 2, 4 units)
    - or can request any randomly
- Request is not valid reservation
- Request can be made, edited, cancelled until they become valid (become reservations)
    - first part of day - becoming valid at 5am
    - second part of day - becoming valid at 5pm
- Requests can be made for
    - first part of day (5:00-17:00)
    - second part of day (18:00-24:00)
    - whole day (5:00-24:00)

### Reservations

- Reservation can be extended to entire day
    - if it was first part of day reservation
    - if current spot has no reservation for evening
    - if current reserved parking spot is not available then there will be proposed another one from the same class

### Reservation fulfillment

- If a client fails to fulfill a reservation:
    - he will be charged for the reservation
    - his loyalty points will be reduced
- If a client fully completes a reservation then he will earn loyalty points.

### Encourage to free reserved parking spot

- Clients who are on reserved chosen parking spot
    - notify them that they should free parking spot
    - give them discount for current parking

### Cleaning parking spots

- Parking spots can be cleaned during technical break
- Cleaning service is called when >50% parking spots are considered dirty
  - It means 20 releases

### Repair/Maintenance of parking spots

- It may not be as widespread as cleaning, but occasionally, we may need to change the parking surface
- Clients can report a malfunction or issue with a parking spot
    - They will receive loyalty points
    - If they are given access to the broken spot, they should also be provided access to another parking spot from the
      available bank

### Fee

- Standard usage of parking spot:
    - 15 minutes - no charge
    - 1 hour - 5PLN
    - 2 hours - 9PLN
    - 4 hours - 14PLN
    - whole day - 50PLN
- Reservations:
  - first part of day - 30PLM
  - second part of day - 20PLM
  - whole day - 40PLM
- Discount:
    - 1-10% for:
        - reporting issues
        - being loyal client
          - park for more than half of the days in a month
          - fulfilling reservations
        - recommendation

### Loyalty points

- Clients can receive loyalty points for reporting issues or being "long-term" client
- "Long-term" client is someone who park for more than half of the days in a month
- Loyalty points can be redeemed to receive gifts

## Analysis

### Timeline model [PL] - Overall view

![](./docs/public/timeline_model.png)

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
