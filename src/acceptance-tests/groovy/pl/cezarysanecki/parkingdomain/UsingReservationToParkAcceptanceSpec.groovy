package pl.cezarysanecki.parkingdomain

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.commons.EntityNotFound
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.parking.usecase.OccupyUsingReservationUseCase
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId
import pl.cezarysanecki.parkingdomain.reservation.usecase.ActivatingReservationsUseCase
import pl.cezarysanecki.parkingdomain.shared.TimeSlot
import pl.cezarysanecki.parkingdomain.shared.VehicleType

class UsingReservationToParkAcceptanceSpec extends BaseAcceptanceSpec {

  @Autowired
  RequestingFacade requestingFacade
  @Autowired
  ActivatingReservationsUseCase activatingReservationsUseCase
  @Autowired
  OccupyUsingReservationUseCase occupyUsingReservationUseCase

  def "one client can request reservation for another one to occupy parking spot"() {
    given:
      def parkingSpotId = addParkingSpot()
      def firstClientId = registerClient()
      def secondClientId = registerClient()

    when:
      def request = requestAndActivateReservation(firstClientId.value(), parkingSpotId)
    and:
      def result = occupyUsingReservationUseCase.run(new OccupantId(secondClientId.value()), new ReservationId(request.value()))

    then:
      result.isPresent()
  }

  def "reservation cannot be used second time even if requester did not use it"() {
    given:
      def parkingSpotId = addParkingSpot()
      def firstClientId = registerClient()
      def secondClientId = registerClient()
    and:
      def request = requestAndActivateReservation(firstClientId.value(), parkingSpotId)

    when: "second client uses the reservation"
      def firstUse = occupyUsingReservationUseCase.run(new OccupantId(secondClientId.value()), new ReservationId(request.value()))

    then:
      firstUse.isPresent()

    when: "requester tries to use the same reservation again"
      occupyUsingReservationUseCase.run(new OccupantId(firstClientId.value()), new ReservationId(request.value()))

    then:
      thrown(EntityNotFound)
  }

  private RequestId requestAndActivateReservation(UUID requesterId, ParkingSpotId parkingSpotId) {
    def timeSlot = TimeSlot.create(CURRENT_DATE, 10, 15)
    requestingFacade.createForAll(timeSlot)
    def request = requestingFacade.request(new RequesterId(requesterId), parkingSpotId, timeSlot, VehicleType.CAR).orElseThrow()
    requestingFacade.makeValidFor(CURRENT_DATE)
    dateProvider.passHours(9)
    dateProvider.passMinutes(1)
    activatingReservationsUseCase.run()
    return request
  }

}
