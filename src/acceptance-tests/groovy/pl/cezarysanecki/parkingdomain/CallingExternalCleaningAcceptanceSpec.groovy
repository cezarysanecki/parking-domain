package pl.cezarysanecki.parkingdomain

import org.quartz.CronExpression
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import pl.cezarysanecki.parkingdomain.cleaning.CleaningFacade
import pl.cezarysanecki.parkingdomain.cleaning.usecase.CallingCleaningWhenSpotsDirtyUseCase
import pl.cezarysanecki.parkingdomain.commons.Result
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.shared.SpotUnits
import pl.cezarysanecki.parkingdomain.views.ViewCleaningRepository

import java.time.LocalTime
import java.time.ZoneId

class CallingExternalCleaningAcceptanceSpec extends BaseAcceptanceSpec {

  @Autowired
  ParkingFacade parkingFacade
  @Autowired
  CallingCleaningWhenSpotsDirtyUseCase callingCleaningWhenSpotsDirtyUseCase
  @Autowired
  CleaningFacade cleaningFacade
  @Autowired
  ViewCleaningRepository viewCleaningRepository

  @Value('${job.calling-external-cleaning-service-policy-job.cron-expression}')
  String cleaningJobCronExpression

  def "call cleaning during technical break if there is required number of dirty parking spots"() {
    given:
      makeTenParkingSpotsDirty()
    and:
      nextDayAt(1, 30)

    when:
      def result = callingCleaningWhenSpotsDirtyUseCase.run()

    then:
      result == Result.Success
  }

  def "cannot call cleaning outside technical break even if there is required number of dirty parking spots"() {
    given:
      makeTenParkingSpotsDirty()
    and:
      nextDayAt(13, 0)

    when:
      def result = callingCleaningWhenSpotsDirtyUseCase.run()

    then:
      result == Result.Rejection
  }

  def "cleaning job is scheduled at 1:30am, during technical break"() {
    given:
      def cron = new CronExpression(cleaningJobCronExpression)
      def midnight = Date.from(CURRENT_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant())

    when:
      def nextFire = cron.getNextValidTimeAfter(midnight).toInstant().atZone(ZoneId.systemDefault())

    then:
      nextFire.toLocalDate() == CURRENT_DATE
      nextFire.toLocalTime() == LocalTime.of(1, 30)
  }

  def "parking spot released #releases time(s) is dirty: #dirty"() {
    given:
      def clientId = registerClient()
      def parkingSpotId = addParkingSpot()

    when:
      occupyAndRelease(clientId, parkingSpotId, releases)

    then:
      cleaningFacade.getDirtyParkingSpots().contains(parkingSpotId) == dirty
    and:
      viewCleaningRepository.queryCleaning().parkingSpotsExceedingThreshold() == (dirty ? 1 : 0)

    where:
      releases || dirty
      19       || false
      20       || true
      21       || true
  }

  def "#dirtySpots parking spot(s) released 20 times and #cleanSpots released 19 times make #dirtySpots parking spot(s) dirty"() {
    given:
      def clientId = registerClient()

    when:
      dirtySpots.times { occupyAndRelease(clientId, addParkingSpot(), 20) }
      cleanSpots.times { occupyAndRelease(clientId, addParkingSpot(), 19) }

    then:
      cleaningFacade.getDirtyParkingSpots().size() == dirtySpots
    and:
      viewCleaningRepository.queryCleaning().parkingSpotsExceedingThreshold() == dirtySpots

    where:
      dirtySpots | cleanSpots
      9          | 1
      10         | 0
  }

  private void nextDayAt(int hour, int minute) {
    dateProvider.setCurrentDate(CURRENT_DATE.plusDays(1))
    dateProvider.passMinutes(hour * 60 + minute)
  }

  private void makeTenParkingSpotsDirty() {
    def clientId = registerClient()
    10.times { occupyAndRelease(clientId, addParkingSpot(), 20) }
  }

  private void occupyAndRelease(ClientId clientId, ParkingSpotId parkingSpotId, int times) {
    times.times {
      def occupationId = parkingFacade.occupy(new OccupantId(clientId.value()), parkingSpotId, new SpotUnits(4)).orElseThrow()
      parkingFacade.release(occupationId).orElseThrow()
    }
  }

}
