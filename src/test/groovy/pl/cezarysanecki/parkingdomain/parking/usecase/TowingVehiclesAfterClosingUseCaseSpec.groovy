package pl.cezarysanecki.parkingdomain.parking.usecase

import pl.cezarysanecki.parkingdomain.commons.date.DateProvider
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotForceReleased
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

class TowingVehiclesAfterClosingUseCaseSpec extends Specification {

  DateProvider dateProvider = Stub()
  ParkingFacade parkingFacade = Mock()
  RemoveOccupationByForceUseCase removeOccupationByForceUseCase = Mock()

  def useCase = new TowingVehiclesAfterClosingUseCase(dateProvider, parkingFacade, removeOccupationByForceUseCase)

  def "all remaining occupations are released by force because parking is closed"() {
    given:
      def first = OccupationId.newOne()
      def second = OccupationId.newOne()
      dateProvider.now() >> ZonedDateTime.of(LocalDate.of(2020, 10, 10), LocalTime.of(1, 0), DateProvider.ZONE_OFFSET).toInstant()
      parkingFacade.findAllOccupations() >> [first, second]

    when:
      def result = useCase.run()

    then:
      1 * removeOccupationByForceUseCase.run(first, ParkingSpotForceReleased.Reason.PARKING_CLOSED) >> true
      1 * removeOccupationByForceUseCase.run(second, ParkingSpotForceReleased.Reason.PARKING_CLOSED) >> true
      0 * removeOccupationByForceUseCase._
      result == 2
  }

}
