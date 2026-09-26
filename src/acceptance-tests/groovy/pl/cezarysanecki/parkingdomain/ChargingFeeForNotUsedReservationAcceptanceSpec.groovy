package pl.cezarysanecki.parkingdomain

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.parking.usecase.OccupyUsingReservationUseCase
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId
import pl.cezarysanecki.parkingdomain.reservation.usecase.ActivatingReservationsUseCase
import pl.cezarysanecki.parkingdomain.reservation.usecase.RemovingNotUsedReservationsUseCase
import pl.cezarysanecki.parkingdomain.shared.VehicleType
import pl.cezarysanecki.parkingdomain.shared.TimeSlot
import pl.cezarysanecki.parkingdomain.views.ViewFeesRepository

class ChargingFeeForNotUsedReservationAcceptanceSpec extends BaseAcceptanceSpec {

  @Autowired
  RequestingFacade requestingFacade
  @Autowired
  ActivatingReservationsUseCase activatingReservationsUseCase
  @Autowired
  RemovingNotUsedReservationsUseCase removingNotUsedReservationsUseCase
  @Autowired
  OccupyUsingReservationUseCase occupyUsingReservationUseCase
  @Autowired
  ViewFeesRepository viewFeesRepository

  def "client is charged 50 USD from price list for not used reservation"() {
    given:
      def clientId = registerClient()
      def request = requestAndActivateReservation(clientId, addParkingSpot())

    when:
      dateProvider.passHours(1)
      dateProvider.passMinutes(15)
      removingNotUsedReservationsUseCase.run()

    then:
      def fees = viewFeesRepository.queryFeesFor(clientId)
      fees.size() == 1
      with(fees.first()) {
        it.reservationId() == request.value()
        it.type() == "NOT_USED_RESERVATION"
        it.amount() == new BigDecimal("50.00")
        it.currency() == "USD"
        it.chargedAt() == dateProvider.now()
      }
  }

  def "client is #description when removing job runs #minutesAfterStart minutes after reservation start"() {
    given:
      def clientId = registerClient()
      requestAndActivateReservation(clientId, addParkingSpot())

    when:
      dateProvider.passMinutes(59 + minutesAfterStart)
      removingNotUsedReservationsUseCase.run()

    then:
      viewFeesRepository.queryFeesFor(clientId).size() == expectedFees

    where:
      minutesAfterStart || expectedFees | description
      14                || 0            | "not charged yet"
      16                || 1            | "charged"
  }

  def "client is not charged when reservation was used"() {
    given:
      def clientId = registerClient()
      def request = requestAndActivateReservation(clientId, addParkingSpot())

    when:
      occupyUsingReservationUseCase.run(new OccupantId(clientId.value()), new ReservationId(request.value()))
    and:
      dateProvider.passHours(1)
      dateProvider.passMinutes(15)
      removingNotUsedReservationsUseCase.run()

    then:
      viewFeesRepository.queryFees().isEmpty()
  }

  def "reservation already marked as not used is not charged again by the next job run"() {
    given:
      def clientId = registerClient()
      requestAndActivateReservation(clientId, addParkingSpot())

    when:
      dateProvider.passHours(1)
      dateProvider.passMinutes(15)
      removingNotUsedReservationsUseCase.run()
    and:
      dateProvider.passMinutes(1)
      removingNotUsedReservationsUseCase.run()

    then:
      viewFeesRepository.queryFeesFor(clientId).size() == 1
  }

  def "only owner of not used reservation is charged"() {
    given:
      def owner = registerClient()
      def otherClient = registerClient()
      requestAndActivateReservation(owner, addParkingSpot())

    when:
      dateProvider.passHours(1)
      dateProvider.passMinutes(15)
      removingNotUsedReservationsUseCase.run()

    then:
      viewFeesRepository.queryFeesFor(owner).size() == 1
      viewFeesRepository.queryFeesFor(otherClient).isEmpty()
  }

  private RequestId requestAndActivateReservation(ClientId clientId, parkingSpotId) {
    def timeSlot = TimeSlot.create(CURRENT_DATE, 10, 15)
    requestingFacade.createForAll(timeSlot)
    def request = requestingFacade.request(new RequesterId(clientId.value()), parkingSpotId, timeSlot, VehicleType.CAR).orElseThrow()
    requestingFacade.makeValidFor(CURRENT_DATE)
    dateProvider.passHours(9)
    dateProvider.passMinutes(1)
    activatingReservationsUseCase.run()
    return request
  }

}
