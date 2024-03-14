package pl.cezarysanecki.parkingdomain.parkingview.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AvailableParkingSpotsView {

    @NonNull
    Set<AvailableParkingSpotView> availableParkingSpots;

}
