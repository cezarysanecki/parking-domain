package pl.cezarysanecki.parkingdomain

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import pl.cezarysanecki.parkingdomain.api.ParkingHttpApi
import pl.cezarysanecki.parkingdomain.api.ParkingHttpApi.OccupyParkingSpotBody
import pl.cezarysanecki.parkingdomain.views.ViewFreeCurrentParkingSpotsRepository

class EntityNotFoundHttpApiAcceptanceSpec extends BaseAcceptanceSpec {

  @Autowired
  WebApplicationContext webApplicationContext
  @Autowired
  ObjectMapper objectMapper
  @Autowired
  ViewFreeCurrentParkingSpotsRepository viewFreeCurrentParkingSpotsRepository

  ParkingHttpApi api

  def setup() {
    api = new ParkingHttpApi(MockMvcBuilders.webAppContextSetup(webApplicationContext).build(), objectMapper)
  }

  def "occupying parking spot by unknown client answers 404 and leaves parking spot free"() {
    given:
      currentTimeIs(DURING_OCCUPYING_HOURS)
      def parkingSpotId = addParkingSpot()
      def unknownClientId = UUID.randomUUID()

    when:
      def response = api.occupy(new OccupyParkingSpotBody(
          occupantId: unknownClientId, parkingSpotId: parkingSpotId.value(), vehicleType: "CAR"))

    then:
      response.status == 404
      response.contentType == "application/problem+json"
      response.problem.title == "Not Found"
      response.problem.detail == "No occupant found with id " + unknownClientId
    and:
      viewFreeCurrentParkingSpotsRepository.queryParkingSpots()
          .find { it.parkingSpotId() == parkingSpotId.value() }
          .spaceLeft() == 4
  }

}
