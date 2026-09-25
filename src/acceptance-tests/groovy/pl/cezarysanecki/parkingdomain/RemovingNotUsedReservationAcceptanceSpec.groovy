package pl.cezarysanecki.parkingdomain

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.parking.usecase.OccupyUsingReservationUseCase
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId
import pl.cezarysanecki.parkingdomain.reservation.usecase.ActivatingReservationsUseCase
import pl.cezarysanecki.parkingdomain.reservation.usecase.RemovingNotUsedReservationsUseCase
import pl.cezarysanecki.parkingdomain.shared.SpotUnits
import pl.cezarysanecki.parkingdomain.shared.TimeSlot

class RemovingNotUsedReservationAcceptanceSpec extends BaseAcceptanceSpec {

  @Autowired
  RequestingFacade requestingFacade
  @Autowired
  ActivatingReservationsUseCase activatingReservationsUseCase
  @Autowired
  OccupyUsingReservationUseCase occupyUsingReservationUseCase
  @Autowired
  RemovingNotUsedReservationsUseCase removingNotUsedReservationsUseCase

  def "not used reservation is removed and cannot be used to occupy parking spot"() {
    given:
      def parkingSpotId = addParkingSpot()
      def clientId = registerClient()
      def timeSlot = TimeSlot.create(CURRENT_DATE, 10, 15)
    and:
      requestingFacade.createForAll(timeSlot)
      def request = requestingFacade.request(new RequesterId(clientId.value()), parkingSpotId, timeSlot, new SpotUnits(4)).orElseThrow()
      requestingFacade.makeValidFor(CURRENT_DATE)
    and: "reservation becomes active"
      dateProvider.passHours(9)
      dateProvider.passMinutes(1)
      activatingReservationsUseCase.run()

    when: "reservation is not used in time"
      dateProvider.passHours(1)
      dateProvider.passMinutes(15)
      removingNotUsedReservationsUseCase.run()

    then:
      noExceptionThrown()

    when: "client tries to use removed reservation"
      occupyUsingReservationUseCase.run(new OccupantId(clientId.value()), new ReservationId(request.value()))

    then: "generic Exception on purpose - concrete type (jakarta EntityNotFoundException) will change with JPA removal"
      thrown(Exception)
  }

}
