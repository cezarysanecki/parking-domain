package pl.cezarysanecki.parkingdomain.reservation.effectiveness

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId
import pl.cezarysanecki.parkingdomain.commons.date.DateConfig
import pl.cezarysanecki.parkingdomain.commons.date.LocalDateProvider
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisherTestConfig
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId

import spock.lang.Specification

import java.time.Duration
import java.time.LocalDateTime

import static org.awaitility.Awaitility.await
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsFixture.anyClientId
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFixture.anyParkingSpotId
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationScheduleFixture.anyReservationId

@ActiveProfiles("local")
@SpringBootTest(classes = [ReservationScheduleConfig.class, EventPublisherTestConfig.class, DateConfig.class])
class CreatingReservationRequestEffectiveIT extends Specification {
  
  ClientId clientId = anyClientId()
  ParkingSpotId parkingSpotId = anyParkingSpotId()
  ReservationId reservationId = anyReservationId()
  
  @Autowired
  LocalDateProvider dateProvider
  @Autowired
  EventPublisher eventPublisher
  @Autowired
  ReservationToMakeEffectiveRepository reservationToMakeEffectiveRepository
  
  def "reservation should be announce to be effective"() {
    given:
      def now = LocalDateTime.of(2020, 10, 10, 13, 00)
      dateProvider.setCurrentDate(now)
    
    when:
      eventPublisher.publish reservationMade(new ReservationSlot(now.plusHours(10), 3))
    
    then:
      await()
          .atMost(Duration.ofSeconds(1))
          .untilAsserted(() -> {
            assert reservationToMakeEffectiveRepository.contains(reservationId)
          })
    
    when:
      dateProvider.passHours(4)
    
    then:
      await()
          .atMost(Duration.ofSeconds(1))
          .untilAsserted(() -> {
            assert reservationToMakeEffectiveRepository.contains(reservationId)
          })
    
    when:
      dateProvider.passHours(4)
    
    then:
      await()
          .atMost(Duration.ofSeconds(1))
          .untilAsserted(() -> {
            assert !reservationToMakeEffectiveRepository.contains(reservationId)
          })
  }
  
  ReservationMade reservationMade(ReservationSlot reservationSlot) {
    return new ReservationMade(parkingSpotId, clientId, reservationId, reservationSlot)
  }
  
}
