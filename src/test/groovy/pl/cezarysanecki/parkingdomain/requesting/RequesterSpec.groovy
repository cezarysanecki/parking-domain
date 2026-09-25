package pl.cezarysanecki.parkingdomain.requesting

import pl.cezarysanecki.parkingdomain.commons.aggregates.Version
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId
import spock.lang.Specification

class RequesterSpec extends Specification {

  def "requester with limit #limit and #current request(s) can append next one: #expected"() {
    given:
      def requester = requesterWith(limit, current)

    expect:
      requester.canAppend(RequestId.newOne()) == expected

    where:
      limit | current || expected
      1     | 0       || true
      1     | 1       || false
      3     | 2       || true
      3     | 3       || false
  }

  def "requester cannot be created with more requests than limit"() {
    when:
      requesterWith(1, 2)

    then:
      def exception = thrown(IllegalStateException)
      exception.message == "current usage cannot exceed limit"
  }

  def "requester cannot be created with zero limit"() {
    when:
      requesterWith(0, 0)

    then:
      def exception = thrown(IllegalStateException)
      exception.message == "value of limit must be positive"
  }

  private static Requester requesterWith(int limit, int currentRequests) {
    def requests = (0..<currentRequests).collect { RequestId.newOne() }
    return new Requester(new RequesterId(UUID.randomUUID()), requests, limit, Version.zero())
  }

}
