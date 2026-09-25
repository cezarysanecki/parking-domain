package pl.cezarysanecki.parkingdomain

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId
import pl.cezarysanecki.parkingdomain.reservation.usecase.ActivatingReservationsUseCase
import pl.cezarysanecki.parkingdomain.shared.TimeSlot
import pl.cezarysanecki.parkingdomain.shared.VehicleType

class CannotOccupyReservedParkingSpotAcceptanceSpec extends BaseAcceptanceSpec {

  @Autowired
  ParkingFacade parkingFacade
  @Autowired
  RequestingFacade requestingFacade
  @Autowired
  ActivatingReservationsUseCase activatingReservationsUseCase

  def "cannot occupy parking spot if there is an active reservation"() {
    given:
      def parkingSpotId = addParkingSpot()
      def firstClientId = registerClient()
      def secondClientId = registerClient()
      def timeSlot = TimeSlot.create(CURRENT_DATE, 10, 15)

    when:
      requestingFacade.createForAll(timeSlot)
      requestingFacade.request(new RequesterId(firstClientId.value()), parkingSpotId, timeSlot, VehicleType.CAR).orElseThrow()
      requestingFacade.makeValidFor(CURRENT_DATE)
    and:
      dateProvider.passHours(9)
      dateProvider.passMinutes(1)
      activatingReservationsUseCase.run()
    and:
      def result = parkingFacade.occupy(new OccupantId(secondClientId.value()), parkingSpotId, VehicleType.CAR)

    then:
      result.isEmpty()
  }

}
