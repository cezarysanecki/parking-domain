package pl.cezarysanecki.parkingdomain.vehicle.model;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RegistrationNumber {

    @NonNull
    String value;

    public static RegistrationNumber of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("registration number cannot be empty");
        }
        return new RegistrationNumber(value.trim());
    }

}
