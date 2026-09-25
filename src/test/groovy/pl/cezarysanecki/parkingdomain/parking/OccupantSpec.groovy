package pl.cezarysanecki.parkingdomain.parking

import pl.cezarysanecki.parkingdomain.commons.aggregates.Version
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId
import pl.cezarysanecki.parkingdomain.parking.api.OccupantId
import pl.cezarysanecki.parkingdomain.parking.api.OccupationId
import spock.lang.Specification

class OccupantSpec extends Specification {

  def "new occupant has the same id as client, no occupations and zero version"() {
    given:
      def clientId = new ClientId(UUID.randomUUID())

    when:
      def occupant = Occupant.newOne(clientId)

    then:
      occupant.occupantId() == new OccupantId(clientId.value())
      occupant.occupations().isEmpty()
      occupant.version() == Version.zero()
  }

  def "occupant with #currentOccupations current occupation(s) can occupy: #expected"() {
    given:
      def occupations = (0..<currentOccupations).collect { OccupationId.newOne() }
      def occupant = new Occupant(new OccupantId(UUID.randomUUID()), occupations, Version.zero())

    expect:
      occupant.canOccupy(OccupationId.newOne()) == expected

    where:
      currentOccupations || expected
      0                  || true
      1                  || false
      2                  || false
  }

}
