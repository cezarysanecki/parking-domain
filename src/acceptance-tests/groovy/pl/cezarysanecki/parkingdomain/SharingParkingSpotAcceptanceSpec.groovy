package pl.cezarysanecki.parkingdomain

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId
import pl.cezarysanecki.parkingdomain.parking.usecase.OccupyUsingReservationUseCase
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId
import pl.cezarysanecki.parkingdomain.reservation.usecase.ActivatingReservationsUseCase
import pl.cezarysanecki.parkingdomain.shared.SpotUnits
import pl.cezarysanecki.parkingdomain.shared.TimeSlot

/**
 * README: parking spot has 4 units and can be shared, f.ex. 1 x 4, 2 x 2, 1 x 2 + 2 x 1, 4 x 1.
 * Domain works on spot units only - vehicle types are translated to units in the web layer.
 */
class SharingParkingSpotAcceptanceSpec extends BaseAcceptanceSpec {

  static final SpotUnits FOUR = new SpotUnits(4)
  static final SpotUnits TWO = new SpotUnits(2)
  static final SpotUnits ONE = new SpotUnits(1)

  @Autowired
  ParkingFacade parkingFacade
  @Autowired
  RequestingFacade requestingFacade
  @Autowired
  ActivatingReservationsUseCase activatingReservationsUseCase
  @Autowired
  OccupyUsingReservationUseCase occupyUsingReservationUseCase

  def timeSlot = TimeSlot.create(CURRENT_DATE, 10, 15)

  def setup() {
    currentTimeIs(DURING_OCCUPYING_HOURS)
  }

  def "parking spot can be fully occupied by #units units"() {
    given:
      def parkingSpotId = addParkingSpot()

    when:
      def results = units.collect { occupy(parkingSpotId, it) }

    then:
      results.every { it.isPresent() }

    when: "spot is full"
      def oneMoreUnit = occupy(parkingSpotId, ONE)

    then: "even one more unit does not fit"
      oneMoreUnit.isEmpty()

    where:
      units << [
          [FOUR],
          [TWO, TWO],
          [TWO, ONE, ONE],
          [ONE, ONE, ONE, ONE]
      ]
  }

  def "#spotUnits units do not fit when #alreadyTaken units are already taken"() {
    given:
      def parkingSpotId = addParkingSpot()
      alreadyTaken.each { assert occupy(parkingSpotId, it).isPresent() }

    when:
      def result = occupy(parkingSpotId, spotUnits)

    then:
      result.isEmpty()

    where:
      alreadyTaken    | spotUnits
      [TWO, ONE]      | TWO
      [ONE, ONE, ONE] | TWO
  }

  def "parking spot can be requested as long as requested units fit"() {
    given:
      def parkingSpotId = addParkingSpot()
      requestingFacade.createForAll(timeSlot)

    when:
      def results = [TWO, ONE, ONE, FOUR].collect { request(parkingSpotId, it) }

    then:
      results*.isPresent() == [true, true, true, false]
  }

  def "occupying using reservation takes only units from request"() {
    given:
      def parkingSpotId = addParkingSpot()
      requestingFacade.createForAll(timeSlot)
      def requesterId = registerClient().value()
      def requestId = request(parkingSpotId, TWO, requesterId).orElseThrow()
      requestingFacade.makeValidFor(CURRENT_DATE)
      dateProvider.setCurrentDate(CURRENT_DATE)
      dateProvider.passHours(9)
      dateProvider.passMinutes(1)
      activatingReservationsUseCase.run()

    when:
      def result = occupyUsingReservationUseCase.run(new OccupantId(requesterId), new ReservationId(requestId.value()))

    then:
      result.isPresent()

    when: "the other half of the spot is still free"
      def fourUnits = occupy(parkingSpotId, FOUR)
      def twoUnits = occupy(parkingSpotId, TWO)

    then: "it fits 2 more units, but not 4"
      fourUnits.isEmpty()
      twoUnits.isPresent()
  }

  private Optional<OccupationId> occupy(ParkingSpotId parkingSpotId, SpotUnits spotUnits) {
    return parkingFacade.occupy(new OccupantId(registerClient().value()), parkingSpotId, spotUnits)
  }

  private Optional<RequestId> request(ParkingSpotId parkingSpotId, SpotUnits spotUnits, UUID requesterId = registerClient().value()) {
    return requestingFacade.request(new RequesterId(requesterId), parkingSpotId, timeSlot, spotUnits)
  }

}
