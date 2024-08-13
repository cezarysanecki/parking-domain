package pl.cezarysanecki.parkingdomain.management.client;

import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientType;
import pl.cezarysanecki.parkingdomain.management.client.api.PhoneNumber;

record Client(
    ClientId clientId,
    ClientType type,
    PhoneNumber phoneNumber) {
}
