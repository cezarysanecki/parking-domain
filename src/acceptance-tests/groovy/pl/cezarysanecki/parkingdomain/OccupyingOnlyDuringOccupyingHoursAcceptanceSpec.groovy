package pl.cezarysanecki.parkingdomain

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.parking.usecase.OccupyUsingReservationUseCase
import pl.cezarysanecki.parkingdomain.parking.usecase.OccupyingWithoutAccountUseCase
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId
import pl.cezarysanecki.parkingdomain.reservation.usecase.ActivatingReservationsUseCase
import pl.cezarysanecki.parkingdomain.shared.VehicleType
import pl.cezarysanecki.parkingdomain.shared.TimeSlot
import pl.cezarysanecki.parkingdomain.views.ViewCurrentStateOfClientRepository

class OccupyingOnlyDuringOccupyingHoursAcceptanceSpec extends BaseAcceptanceSpec {

  @Autowired
  ParkingFacade parkingFacade
  @Autowired
  OccupyingWithoutAccountUseCase occupyingWithoutAccountUseCase
  @Autowired
  OccupyUsingReservationUseCase occupyUsingReservationUseCase
  @Autowired
  RequestingFacade requestingFacade
  @Autowired
  ActivatingReservationsUseCase activatingReservationsUseCase
  @Autowired
  ViewCurrentStateOfClientRepository viewCurrentStateOfClientRepository

  def "occupying parking spot at #hour:#minute is #description"() {
    given:
      def parkingSpotId = addParkingSpot()
      def clientId = registerClient()
    and:
      currentTimeIs(hour, minute)

    when:
      def result = parkingFacade.occupy(new OccupantId(clientId.value()), parkingSpotId, VehicleType.CAR)

    then:
      result.isPresent() == allowed

    where: // hour 24+ = next day
      hour | minute || allowed
      4    | 59     || false
      5    | 0      || true
      23   | 59     || true
      24   | 0      || false

      description = allowed ? "allowed" : "rejected"
  }

  def "cannot occupy parking spot without account after midnight and client is not registered"() {
    given:
      def parkingSpotId = addParkingSpot()
    and:
      currentTimeIs(24, 30)

    when:
      def result = occupyingWithoutAccountUseCase.run(RandomTestUtils.randomPhoneNumber(), parkingSpotId, VehicleType.CAR)

    then:
      result.isEmpty()
      viewCurrentStateOfClientRepository.queryAll().isEmpty()
  }

  def "cannot use reservation during technical break and reservation stays valid for opening hours"() {
    given:
      def parkingSpotId = addParkingSpot()
      def clientId = registerClient()
      def timeSlot = TimeSlot.create(CURRENT_DATE, 5, 17)
    and:
      requestingFacade.createForAll(timeSlot)
      def request = requestingFacade.request(new RequesterId(clientId.value()), parkingSpotId, timeSlot, VehicleType.CAR).orElseThrow()
      requestingFacade.makeValidFor(CURRENT_DATE)
    and:
      currentTimeIs(4, 1)
      activatingReservationsUseCase.run()

    when:
      def duringBreak = occupyUsingReservationUseCase.run(new OccupantId(clientId.value()), new ReservationId(request.value()))

    then:
      duringBreak.isEmpty()

    when:
      currentTimeIs(5)
      def afterOpening = occupyUsingReservationUseCase.run(new OccupantId(clientId.value()), new ReservationId(request.value()))

    then:
      afterOpening.isPresent()
  }

}
