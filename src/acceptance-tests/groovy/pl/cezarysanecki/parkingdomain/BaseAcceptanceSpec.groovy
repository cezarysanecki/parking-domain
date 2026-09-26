package pl.cezarysanecki.parkingdomain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.cezarysanecki.parkingdomain._local.InMemoryRepositories
import pl.cezarysanecki.parkingdomain._local.LocalDateProvider
import pl.cezarysanecki.parkingdomain.management.client.ClientFacade
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId
import pl.cezarysanecki.parkingdomain.management.client.api.ClientType
import pl.cezarysanecki.parkingdomain.management.client.api.PhoneNumber
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotFacade
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import spock.lang.Specification

import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("local")
abstract class BaseAcceptanceSpec extends Specification {

  static final LocalDate CURRENT_DATE = LocalDate.of(2020, 10, 10)
  static final int DURING_OCCUPYING_HOURS = 10

  @Autowired
  ParkingSpotFacade parkingSpotFacade
  @Autowired
  ClientFacade clientFacade
  @Autowired
  LocalDateProvider dateProvider

  def setup() {
    InMemoryRepositories.clearAll()
    dateProvider.setCurrentDate(CURRENT_DATE)
  }

  // sets the clock to CURRENT_DATE + given time, hour >= 24 means the next day(s)
  void currentTimeIs(int hour, int minute = 0) {
    dateProvider.setCurrentDate(CURRENT_DATE)
    dateProvider.passMinutes(hour * 60 + minute)
  }

  ParkingSpotId addParkingSpot(
      ParkingSpotCapacity capacity = ParkingSpotCapacity.defaultCapacity(),
      ParkingSpotCategory category = ParkingSpotCategory.Gold) {
    return parkingSpotFacade.addParkingSpot(capacity, category).orElseThrow()
  }

  ClientId registerClient(
      ClientType clientType = ClientType.INDIVIDUAL,
      PhoneNumber phoneNumber = RandomTestUtils.randomPhoneNumber()) {
    return clientFacade.registerClient(clientType, phoneNumber).orElseThrow()
  }

}
