package pl.cezarysanecki.parkingdomain

import org.quartz.CronExpression
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import pl.cezarysanecki.parkingdomain.cleaning.usecase.CallingCleaningWhenSpotsDirtyUseCase
import pl.cezarysanecki.parkingdomain.commons.Result
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCategory
import pl.cezarysanecki.parkingdomain.parking.ParkingFacade
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.shared.SpotUnits

import java.time.LocalTime
import java.time.ZoneId

class CallingExternalCleaningAcceptanceSpec extends BaseAcceptanceSpec {

  @Autowired
  ParkingFacade parkingFacade
  @Autowired
  CallingCleaningWhenSpotsDirtyUseCase callingCleaningWhenSpotsDirtyUseCase

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

  private void nextDayAt(int hour, int minute) {
    dateProvider.setCurrentDate(CURRENT_DATE.plusDays(1))
    dateProvider.passMinutes(hour * 60 + minute)
  }

  private void makeTenParkingSpotsDirty() {
    def clientId = registerClient()
    10.times {
      def parkingSpotId = addParkingSpot(ParkingSpotCapacity.defaultCapacity(), ParkingSpotCategory.Silver)
      20.times {
        def occupationId = parkingFacade.occupy(new OccupantId(clientId.value()), parkingSpotId, new SpotUnits(4)).orElseThrow()
        parkingFacade.release(occupationId)
      }
    }
  }

}
