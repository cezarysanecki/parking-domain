package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotTimeSlotId;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository.NewParkingSpotReservationRequests;

@Slf4j
@RequiredArgsConstructor
public class CreatingParkingSpotReservationRequestsEventHandler {

    private final DateProvider dateProvider;
    private final ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository;

    @EventListener
    public void handle(ParkingSpotAdded event) {
        LocalDate tomorrow = dateProvider.tomorrow();

        List<NewParkingSpotReservationRequests> newOnes = Stream.of(
                        TimeSlot.createTimeSlotAtUTC(tomorrow, 7, 17),
                        TimeSlot.createTimeSlotAtUTC(tomorrow, 18, 23))
                .map(timeSlot -> new NewParkingSpotReservationRequests(
                        event.parkingSpotId(),
                        ParkingSpotTimeSlotId.newOne(),
                        event.capacity(),
                        timeSlot))
                .toList();

        newOnes.forEach(
                newOne -> {
                    log.debug("storing parking spot as reservation requests with id: {}", newOne.parkingSpotId());
                    parkingSpotReservationRequestsRepository.store(newOne);
                }
        );
    }

}
