package pl.cezarysanecki.parkingdomain.views.parking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AvailableParkingSpotsView {

    @NonNull
    Set<AvailableParkingSpotView> availableParkingSpots;

}
