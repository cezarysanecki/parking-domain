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
import pl.cezarysanecki.parkingdomain.shared.TimeSlot
import pl.cezarysanecki.parkingdomain.shared.VehicleType

import static pl.cezarysanecki.parkingdomain.shared.VehicleType.CAR
import static pl.cezarysanecki.parkingdomain.shared.VehicleType.MOTORCYCLE
import static pl.cezarysanecki.parkingdomain.shared.VehicleType.SCOOTER

class OccupyingParkingSpotByVehicleTypesAcceptanceSpec extends BaseAcceptanceSpec {

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
    // occupy during the day, not at midnight set by BaseAcceptanceSpec
    dateProvider.passHours(10)
  }

  def "parking spot can be fully occupied by #vehicles"() {
    given:
      def parkingSpotId = addParkingSpot()

    when:
      def results = vehicles.collect { occupy(parkingSpotId, it) }

    then:
      results.every { it.isPresent() }

    when: "spot is full"
      def oneMoreScooter = occupy(parkingSpotId, SCOOTER)

    then: "even a scooter does not fit"
      oneMoreScooter.isEmpty()

    where:
      vehicles << [
          [CAR],
          [MOTORCYCLE, MOTORCYCLE],
          [MOTORCYCLE, SCOOTER, SCOOTER],
          [SCOOTER, SCOOTER, SCOOTER, SCOOTER]
      ]
  }

  def "#vehicleType does not fit when #alreadyParked already occupy the parking spot"() {
    given:
      def parkingSpotId = addParkingSpot()
      alreadyParked.each { assert occupy(parkingSpotId, it).isPresent() }

    when:
      def result = occupy(parkingSpotId, vehicleType)

    then:
      result.isEmpty()

    where:
      alreadyParked                | vehicleType
      [MOTORCYCLE, SCOOTER]        | MOTORCYCLE
      [SCOOTER, SCOOTER, SCOOTER]  | MOTORCYCLE
  }

  def "parking spot can be requested by vehicles as long as they fit"() {
    given:
      def parkingSpotId = addParkingSpot()
      requestingFacade.createForAll(timeSlot)

    when:
      def results = [MOTORCYCLE, SCOOTER, SCOOTER, CAR].collect { request(parkingSpotId, it) }

    then:
      results*.isPresent() == [true, true, true, false]
  }

  def "vehicle using reservation occupies only units of vehicle type from request"() {
    given:
      def parkingSpotId = addParkingSpot()
      requestingFacade.createForAll(timeSlot)
      def requesterId = registerClient().value()
      def requestId = request(parkingSpotId, MOTORCYCLE, requesterId).orElseThrow()
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
      def car = occupy(parkingSpotId, CAR)
      def motorcycle = occupy(parkingSpotId, MOTORCYCLE)

    then: "it fits a motorcycle, but not a car"
      car.isEmpty()
      motorcycle.isPresent()
  }

  private Optional<OccupationId> occupy(ParkingSpotId parkingSpotId, VehicleType vehicleType) {
    return parkingFacade.occupy(new OccupantId(registerClient().value()), parkingSpotId, vehicleType)
  }

  private Optional<RequestId> request(ParkingSpotId parkingSpotId, VehicleType vehicleType, UUID requesterId = registerClient().value()) {
    return requestingFacade.request(new RequesterId(requesterId), parkingSpotId, timeSlot, vehicleType)
  }

}
