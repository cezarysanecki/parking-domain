package pl.cezarysanecki.parkingdomain.parking;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.SpotUnits;

import java.util.Map;

@Value
class Occupations {

    @NonNull
    Map<Occupations, SpotUnits> occupations;

}
