package pl.cezarysanecki.parkingdomain.reservationeffectiveness;

import org.springframework.scheduling.annotation.Scheduled;

public class MakingReservationEffective {

    @Scheduled(fixedRate = 5_000L)
    public void makeReservationsEffective() {

    }

}
