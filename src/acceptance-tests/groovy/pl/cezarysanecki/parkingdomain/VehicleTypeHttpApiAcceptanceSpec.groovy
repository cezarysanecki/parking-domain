package pl.cezarysanecki.parkingdomain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade
import pl.cezarysanecki.parkingdomain.shared.TimeSlot

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

class VehicleTypeHttpApiAcceptanceSpec extends BaseAcceptanceSpec {

  @Autowired
  WebApplicationContext webApplicationContext
  @Autowired
  RequestingFacade requestingFacade

  MockMvc mockMvc

  ParkingSpotId parkingSpotId

  def setup() {
    // occupy during the day, not at midnight set by BaseAcceptanceSpec
    dateProvider.passHours(10)
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    parkingSpotId = addParkingSpot()
  }

  def "occupying parking spot accepts vehicle type"() {
    when:
      def status = postJson("/parking/occupy", """
          {"occupantId": "${registerClient().value()}", "parkingSpotId": "${parkingSpotId.value()}", "vehicleType": "CAR"}
      """)

    then:
      status == 200
  }

  def "occupying parking spot without account accepts vehicle type"() {
    when:
      def status = postJson("/parking/occupy-without-account", """
          {"phoneNumber": "${RandomTestUtils.randomPhoneNumber()}", "parkingSpotId": "${parkingSpotId.value()}", "vehicleType": "MOTORCYCLE"}
      """)

    then:
      status == 200
  }

  def "making request accepts vehicle type"() {
    given:
      requestingFacade.createForAll(TimeSlot.create(CURRENT_DATE, 10, 15))

    when:
      def status = postJson("/requesting/request", """
          {"requesterId": "${registerClient().value()}", "parkingSpotId": "${parkingSpotId.value()}",
           "from": "2020-10-10T10:00:00", "to": "2020-10-10T15:00:00", "vehicleType": "SCOOTER"}
      """)

    then:
      status == 200
  }

  def "#endpoint rejects request with #description"() {
    when:
      def status = postJson(endpoint, body.replace("PARKING_SPOT_ID", parkingSpotId.value().toString()))

    then:
      status == 400

    where:
      endpoint                          | description                         | body
      "/parking/occupy"                 | "unknown (lowercase) vehicle type"  | '{"occupantId": "' + UUID.randomUUID() + '", "parkingSpotId": "PARKING_SPOT_ID", "vehicleType": "car"}'
      "/parking/occupy"                 | "raw spot units instead of type"    | '{"occupantId": "' + UUID.randomUUID() + '", "parkingSpotId": "PARKING_SPOT_ID", "spotUnits": 2}'
      "/parking/occupy-without-account" | "raw spot units instead of type"    | '{"phoneNumber": "123456789", "parkingSpotId": "PARKING_SPOT_ID", "spotUnits": 2}'
      "/requesting/request"             | "raw spot units instead of type"    | '{"requesterId": "' + UUID.randomUUID() + '", "parkingSpotId": "PARKING_SPOT_ID", "from": "2020-10-10T10:00:00", "to": "2020-10-10T15:00:00", "spotUnits": 4}'
  }

  private int postJson(String url, String body) {
    return mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(body))
        .andReturn().response.status
  }

}
