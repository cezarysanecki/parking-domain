package pl.cezarysanecki.parkingdomain

import org.springframework.beans.factory.annotation.Autowired
import pl.cezarysanecki.parkingdomain.management.client.api.ClientType
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId
import pl.cezarysanecki.parkingdomain.shared.TimeSlot
import pl.cezarysanecki.parkingdomain.shared.VehicleType

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
      requestingFacade.request(new RequesterId(firstClientId.value()), parkingSpotId, timeSlot, VehicleType.CAR)
      def result = requestingFacade.request(new RequesterId(secondClientId.value()), parkingSpotId, timeSlot, VehicleType.CAR)

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
      def request = requestingFacade.request(new RequesterId(firstClientId.value()), parkingSpotId, timeSlot, VehicleType.CAR).orElseThrow()
      requestingFacade.cancel(request)
      def result = requestingFacade.request(new RequesterId(secondClientId.value()), parkingSpotId, timeSlot, VehicleType.CAR)

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
      requestingFacade.request(new RequesterId(clientId.value()), firstParkingSpotId, timeSlot, VehicleType.CAR)
      def result = requestingFacade.request(new RequesterId(clientId.value()), secondParkingSpotId, timeSlot, VehicleType.CAR)

    then:
      result.isPresent() == secondRequestAccepted

    where:
      clientType            | secondRequestAccepted | description
      ClientType.INDIVIDUAL | false                 | "can have only one request"
      ClientType.BUSINESS   | true                  | "can have multiple requests"
  }

}
