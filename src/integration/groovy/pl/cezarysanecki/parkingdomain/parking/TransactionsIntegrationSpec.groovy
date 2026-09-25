package pl.cezarysanecki.parkingdomain.parking

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import pl.cezarysanecki.parkingdomain.BaseIntegrationSpec
import pl.cezarysanecki.parkingdomain.commons.EntityNotFound
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotCapacity
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId

class TransactionsIntegrationSpec extends BaseIntegrationSpec {

  @Autowired
  PlatformTransactionManager transactionManager
  @Autowired
  ParkingRepository parkingRepository

  def "uses JDBC transaction manager"() {
    expect:
      transactionManager instanceof DataSourceTransactionManager
  }

  def "rolls back JOOQ writes when transaction fails"() {
    given:
      def parkingSpotId = new ParkingSpotId(UUID.randomUUID())

    when:
      new TransactionTemplate(transactionManager).executeWithoutResult {
        parkingRepository.saveNew(ParkingSpot.create(parkingSpotId, ParkingSpotCapacity.defaultCapacity()))
        throw new IllegalStateException("boom")
      }

    then:
      thrown(IllegalStateException)

    when:
      parkingRepository.loadBy(parkingSpotId)

    then:
      thrown(EntityNotFound)
  }
}
