package pl.cezarysanecki.parkingdomain

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.management.client.api.ClientType
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId
import pl.cezarysanecki.parkingdomain.shared.SpotUnits
import pl.cezarysanecki.parkingdomain.shared.TimeSlot

class RequestingParkingSpotAcceptanceSpec extends BaseAcceptanceSpec {

  @Autowired
  RequestingFacade requestingFacade

  def timeSlot = TimeSlot.create(CURRENT_DATE, 10, 15)

  def "cannot request parking spot if capacity is exceeded"() {
    given:
      def parkingSpotId = addParkingSpot()
      def firstClientId = registerClient(ClientType.BUSINESS)
      def secondClientId = registerClient(ClientType.BUSINESS)

    when:
      requestingFacade.createForAll(timeSlot)
      requestingFacade.request(new RequesterId(firstClientId.value()), parkingSpotId, timeSlot, new SpotUnits(4))
      def result = requestingFacade.request(new RequesterId(secondClientId.value()), parkingSpotId, timeSlot, new SpotUnits(4))

    then:
      result.isEmpty()
  }

  def "can request parking spot if previous request has been cancelled"() {
    given:
      def parkingSpotId = addParkingSpot()
      def firstClientId = registerClient(ClientType.BUSINESS)
      def secondClientId = registerClient(ClientType.BUSINESS)

    when:
      requestingFacade.createForAll(timeSlot)
      def request = requestingFacade.request(new RequesterId(firstClientId.value()), parkingSpotId, timeSlot, new SpotUnits(4)).orElseThrow()
      requestingFacade.cancel(request)
      def result = requestingFacade.request(new RequesterId(secondClientId.value()), parkingSpotId, timeSlot, new SpotUnits(4))

    then:
      result.isPresent()
  }

  def "#clientType client #description"() {
    given:
      def firstParkingSpotId = addParkingSpot()
      def secondParkingSpotId = addParkingSpot()
      def clientId = registerClient(clientType)

    when:
      requestingFacade.createForAll(timeSlot)
      requestingFacade.request(new RequesterId(clientId.value()), firstParkingSpotId, timeSlot, new SpotUnits(4))
      def result = requestingFacade.request(new RequesterId(clientId.value()), secondParkingSpotId, timeSlot, new SpotUnits(4))

    then:
      result.isPresent() == secondRequestAccepted

    where:
      clientType            | secondRequestAccepted | description
      ClientType.INDIVIDUAL | false                 | "can have only one request"
      ClientType.BUSINESS   | true                  | "can have multiple requests"
  }

}
