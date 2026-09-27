package pl.cezarysanecki.parkingdomain.web

import pl.cezarysanecki.parkingdomain.shared.SpotUnits
import spock.lang.Specification

class VehicleTypeSpec extends Specification {

  def "#vehicleType occupies #units spot units"() {
    expect:
      vehicleType.spotUnits() == new SpotUnits(units)

    where:
      vehicleType            || units
      VehicleType.CAR        || 4
      VehicleType.MOTORCYCLE || 2
      VehicleType.SCOOTER    || 1
  }

}
