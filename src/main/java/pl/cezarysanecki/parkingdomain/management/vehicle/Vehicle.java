package pl.cezarysanecki.parkingdomain.management.vehicle;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.management.client.ClientId;

import java.util.UUID;

@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class Vehicle {

    @NonNull
    VehicleId vehicleId;
    @NonNull
    SpotUnits spotUnits;
    @NonNull
    Make make;
    @NonNull
    Model model;
    @NonNull
    ClientId assignedTo;

    Vehicle(UUID vehicleId, int size, String make, String model, UUID clientId) {
        this(VehicleId.of(vehicleId), SpotUnits.of(size), new Make(make), new Model(model), ClientId.of(clientId));
    }

}

@Value
class Make {

    @NonNull
    String make;

    Make(String make) {
        if (make.isEmpty()) {
            throw new IllegalArgumentException("make cannot be empty");
        }
        this.make = make.trim();
    }

}

@Value
class Model {

    @NonNull
    String model;

    Model(String model) {
        if (model.isEmpty()) {
            throw new IllegalArgumentException("model cannot be empty");
        }
        this.model = model.trim();
    }

}
