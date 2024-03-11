package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Getter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class ReservationEntity {

    @Id
    Long id;
    UUID parkingSpotId;
    UUID vehicleId;
    int vehicleSizeUnit;

    ReservationEntity(UUID parkingSpotId, UUID vehicleId, int vehicleSizeUnit) {
        this.parkingSpotId = parkingSpotId;
        this.vehicleId = vehicleId;
        this.vehicleSizeUnit = vehicleSizeUnit;
    }

    boolean is(UUID parkingSpotId, UUID vehicleId) {
        return this.parkingSpotId.equals(parkingSpotId) &&
                this.vehicleId.equals(vehicleId);
    }

}
