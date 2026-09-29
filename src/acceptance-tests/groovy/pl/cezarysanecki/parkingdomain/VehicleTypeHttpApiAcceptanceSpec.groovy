package pl.cezarysanecki.parkingdomain

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import pl.cezarysanecki.parkingdomain.api.ParkingHttpApi
import pl.cezarysanecki.parkingdomain.api.ParkingHttpApi.MakeRequestBody
import pl.cezarysanecki.parkingdomain.api.ParkingHttpApi.OccupyParkingSpotBody
import pl.cezarysanecki.parkingdomain.api.ParkingHttpApi.OccupyParkingSpotWithoutAccountBody
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade
import pl.cezarysanecki.parkingdomain.shared.TimeSlot
import pl.cezarysanecki.parkingdomain.views.ViewFreeCurrentParkingSpotsRepository
import pl.cezarysanecki.parkingdomain.views.ViewFreeTimeSlotsRepository

/**
 * Vehicle type exists only in the HTTP API - the web layer translates it into spot units.
 */
class VehicleTypeHttpApiAcceptanceSpec extends BaseAcceptanceSpec {

  @Autowired
  WebApplicationContext webApplicationContext
  @Autowired
  ObjectMapper objectMapper
  @Autowired
  RequestingFacade requestingFacade
  @Autowired
  ViewFreeCurrentParkingSpotsRepository viewFreeCurrentParkingSpotsRepository
  @Autowired
  ViewFreeTimeSlotsRepository viewFreeTimeSlotsRepository

  ParkingHttpApi api
  ParkingSpotId parkingSpotId

  def setup() {
    currentTimeIs(DURING_OCCUPYING_HOURS)
    api = new ParkingHttpApi(MockMvcBuilders.webAppContextSetup(webApplicationContext).build(), objectMapper)
    parkingSpotId = addParkingSpot()
  }

  def "occupying parking spot by #vehicleType takes #units unit(s) of parking spot"() {
    when:
      def status = api.occupy(occupyBody(vehicleType: vehicleType)).status

    then:
      status == 200
      spaceLeftOnParkingSpot() == 4 - units

    where:
      vehicleType  || units
      "CAR"        || 4
      "MOTORCYCLE" || 2
      "SCOOTER"    || 1
  }

  def "occupying parking spot without account by vehicle type takes its units"() {
    when:
      def status = api.occupyWithoutAccount(occupyWithoutAccountBody(vehicleType: "MOTORCYCLE")).status

    then:
      status == 200
      spaceLeftOnParkingSpot() == 2
  }

  def "requesting parking spot by vehicle type takes its units in time slot"() {
    given:
      def timeSlot = TimeSlot.create(CURRENT_DATE, 10, 15)
      requestingFacade.createForAll(timeSlot)

    when:
      def status = api.makeRequest(makeRequestBody(vehicleType: "SCOOTER")).status

    then:
      status == 200
      spaceLeftInTimeSlot() == 3
  }

  def "occupying parking spot is rejected for #description"() {
    when:
      def status = api.occupy(occupyBody(changes)).status

    then:
      status == 400
      spaceLeftOnParkingSpot() == 4

    where:
      description                                | changes
      "unknown vehicle type"                     | [vehicleType: "car"]
      "missing vehicle type"                     | [vehicleType: null]
      "spot units sent instead of vehicle type"  | [vehicleType: null, spotUnits: 2]
  }

  def "occupying parking spot without account is rejected for #description"() {
    when:
      def status = api.occupyWithoutAccount(occupyWithoutAccountBody(changes)).status

    then:
      status == 400
      spaceLeftOnParkingSpot() == 4

    where:
      description                                | changes
      "unknown vehicle type"                     | [vehicleType: "car"]
      "spot units sent instead of vehicle type"  | [vehicleType: null, spotUnits: 2]
  }

  def "requesting parking spot is rejected for #description"() {
    given:
      requestingFacade.createForAll(TimeSlot.create(CURRENT_DATE, 10, 15))

    when:
      def status = api.makeRequest(makeRequestBody(changes)).status

    then:
      status == 400
      spaceLeftInTimeSlot() == 4

    where:
      description                                | changes
      "unknown vehicle type"                     | [vehicleType: "car"]
      "spot units sent instead of vehicle type"  | [vehicleType: null, spotUnits: 4]
  }

  private OccupyParkingSpotBody occupyBody(Map changes) {
    def body = new OccupyParkingSpotBody(
        occupantId: registerClient().value(),
        parkingSpotId: parkingSpotId.value(),
        vehicleType: "CAR")
    changes.each { field, value -> body[field] = value }
    return body
  }

  private OccupyParkingSpotWithoutAccountBody occupyWithoutAccountBody(Map changes) {
    def body = new OccupyParkingSpotWithoutAccountBody(
        phoneNumber: RandomTestUtils.randomPhoneNumber().value,
        parkingSpotId: parkingSpotId.value(),
        vehicleType: "CAR")
    changes.each { field, value -> body[field] = value }
    return body
  }

  private MakeRequestBody makeRequestBody(Map changes) {
    def body = new MakeRequestBody(
        requesterId: registerClient().value(),
        parkingSpotId: parkingSpotId.value(),
        from: CURRENT_DATE.atTime(10, 0),
        to: CURRENT_DATE.atTime(15, 0),
        vehicleType: "CAR")
    changes.each { field, value -> body[field] = value }
    return body
  }

  private int spaceLeftOnParkingSpot() {
    return viewFreeCurrentParkingSpotsRepository.queryParkingSpots()
        .find { it.parkingSpotId() == parkingSpotId.value() }
        .spaceLeft()
  }

  private int spaceLeftInTimeSlot() {
    return viewFreeTimeSlotsRepository.queryFreeTimeSlots()
        .find { it.parkingSpotId() == parkingSpotId.value() }
        .spaceLeft()
  }

}
