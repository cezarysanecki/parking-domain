package pl.cezarysanecki.parkingdomain.parking

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.context.event.EventListener
import pl.cezarysanecki.parkingdomain.BaseIntegrationSpec
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId
import pl.cezarysanecki.parkingdomain.parking.api.ParkingSpotReleased
import pl.cezarysanecki.parkingdomain.shared.SpotUnits

import java.util.concurrent.ConcurrentHashMap

/**
 * Guards that JOOQ writes join the Spring transaction of a facade on the production setup:
 * a failure after the write (here: in a synchronous event listener) must roll the write back.
 * Uses release() on purpose - it does not depend on the clock (see docs/testing.md).
 */
@Import(FailingReleaseListener)
class ReleasingParkingSpotIsAtomicIntegrationSpec extends BaseIntegrationSpec {

  @Autowired
  ParkingFacade parkingFacade
  @Autowired
  ParkingRepository parkingRepository
  @Autowired
  OccupantRepository occupantRepository
  @Autowired
  OccupationRepository occupationRepository
  @Autowired
  FailingReleaseListener failingReleaseListener

  def "releasing parking spot frees occupied space"() {
    given:
      def parkingSpotId = new ParkingSpotId(UUID.randomUUID())
      def occupationId = occupy(parkingSpotId, new SpotUnits(2))

    when:
      def result = parkingFacade.release(occupationId)

    then:
      result.isPresent()
      parkingRepository.loadBy(parkingSpotId).occupiedSpace() == 0
  }

  def "releasing parking spot is rolled back when something fails later in the same transaction"() {
    given:
      def parkingSpotId = new ParkingSpotId(UUID.randomUUID())
      def occupationId = occupy(parkingSpotId, new SpotUnits(2))
    and:
      failingReleaseListener.failFor(parkingSpotId)

    when:
      parkingFacade.release(occupationId)

    then:
      thrown(SimulatedFailure)
    and:
      parkingRepository.loadBy(parkingSpotId).occupiedSpace() == 2
  }

  private OccupationId occupy(ParkingSpotId parkingSpotId, SpotUnits spotUnits) {
    def clientId = new ClientId(UUID.randomUUID())
    parkingRepository.saveNew(ParkingSpot.create(parkingSpotId, ParkingSpotCapacity.defaultCapacity()))
    occupantRepository.saveNew(Occupant.newOne(clientId))

    def occupationId = new OccupationId(UUID.randomUUID())
    occupationRepository.saveCheckingVersion(new Occupation(
        occupationId,
        occupantRepository.findBy(new OccupantId(clientId.value())),
        parkingRepository.loadBy(parkingSpotId),
        spotUnits))
    return occupationId
  }

  static class FailingReleaseListener {

    private final Set<ParkingSpotId> parkingSpotsToFailFor = ConcurrentHashMap.newKeySet()

    void failFor(ParkingSpotId parkingSpotId) {
      parkingSpotsToFailFor.add(parkingSpotId)
    }

    @EventListener
    void handle(ParkingSpotReleased event) {
      if (parkingSpotsToFailFor.contains(event.parkingSpotId())) {
        throw new SimulatedFailure()
      }
    }
  }

  static class SimulatedFailure extends RuntimeException {
    SimulatedFailure() {
      super("simulated failure after releasing parking spot")
    }
  }

}
