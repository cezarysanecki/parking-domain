package pl.cezarysanecki.parkingdomain

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.occupationreleasenotification.usecase.NotifyingAboutReleasingOccupationUseCase
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId
import pl.cezarysanecki.parkingdomain.reservation.usecase.ActivatingReservationsUseCase
import pl.cezarysanecki.parkingdomain.shared.SpotUnits
import pl.cezarysanecki.parkingdomain.shared.TimeSlot

class AskingToReleaseParkingSpotAcceptanceSpec extends BaseAcceptanceSpec {

  @Autowired
  RequestingFacade requestingFacade
  @Autowired
  ParkingFacade parkingFacade
  @Autowired
  ActivatingReservationsUseCase activatingReservationsUseCase
  @Autowired
  NotifyingAboutReleasingOccupationUseCase notifyingAboutReleasingOccupationUseCase

  def "current occupant should be notified to release parking spot because there is a reservation"() {
    given:
      def parkingSpotId = addParkingSpot()
      def firstClientId = registerClient()
      def secondClientId = registerClient()
      def timeSlot = TimeSlot.create(CURRENT_DATE, 10, 15)

    when:
      requestingFacade.createForAll(timeSlot)
      requestingFacade.request(new RequesterId(secondClientId.value()), parkingSpotId, timeSlot, new SpotUnits(4)).orElseThrow()
      requestingFacade.makeValidFor(CURRENT_DATE)
    and:
      currentTimeIs(8)
      parkingFacade.occupy(new OccupantId(firstClientId.value()), parkingSpotId, new SpotUnits(4)).orElseThrow()
    and:
      currentTimeIs(9, 1)
      activatingReservationsUseCase.run()
    and:
      dateProvider.passMinutes(30)
      def result = notifyingAboutReleasingOccupationUseCase.run()

    then:
      result == 1
  }

}
