package pl.cezarysanecki.parkingdomain.client.reservationrequest.application;

import java.time.LocalDateTime;

public interface ClientReservationRequestCommand {

    LocalDateTime getWhen();

}
