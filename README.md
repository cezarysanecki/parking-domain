![Parkign domain](./docs/public/logo.png)

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

## Business context

### Parking on parking spots

- Basic functionality for parking
- Parking is available 24/7
- Specified parking spot can be occupied (need to track them)
- Parking spots can be occupied by: cars, motorcycles, bikes and scooters
- To optimize occupation of parking spots assume that:
  - 1 parking spot can be occupied by **1 car**
  - 1 parking spot can be occupied by **2 motorcycles**
  - 1 parking spot can be occupied by **3 bicycles or scooters**
- We cannot mix vehicles types on one parking spot, unless they are in the same above group

## Inspirations

- [DevMentors YouTube video](https://www.youtube.com/@DevMentorsPL/videos)
- [Library by example](https://github.com/ddd-by-examples/library)
