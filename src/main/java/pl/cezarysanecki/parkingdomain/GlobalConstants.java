package pl.cezarysanecki.parkingdomain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GlobalConstants {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ParkingSlot {

        public static final int AVAILABLE_SPACE = 4;

    }

}
