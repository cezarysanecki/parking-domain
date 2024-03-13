package pl.cezarysanecki.parkingdomain.parkingview.model;

import lombok.NonNull;
import lombok.Value;

import java.util.Set;

@Value
public class AvailableParkingSpotsView {

    @NonNull
    Set<AvailableParkingSpotView> availableParkingSpots;

}
