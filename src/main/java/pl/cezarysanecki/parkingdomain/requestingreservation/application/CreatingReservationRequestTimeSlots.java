package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.collection.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotTimeSlotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ParkingSpotReservationRequestsTemplateRepository;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.time.LocalDate;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents.ReservationRequestsCreated;

@Slf4j
@RequiredArgsConstructor
public class CreatingReservationRequestTimeSlots {

    private final DateProvider dateProvider;
    private final ParkingSpotReservationRequestsRepository parkingSpotReservationRequestsRepository;
    private final ParkingSpotReservationRequestsTemplateRepository parkingSpotReservationRequestsTemplateRepository;

    public void prepareNewTimeSlots() {
        parkingSpotReservationRequestsRepository.removeAll();

        LocalDate tomorrow = dateProvider.tomorrow();
        io.vavr.collection.List<ReservationRequestsCreated> events = parkingSpotReservationRequestsTemplateRepository.findAll()
                .flatMap(template -> Stream.of(
                                TimeSlot.createTimeSlotAtUTC(tomorrow, 7, 17),
                                TimeSlot.createTimeSlotAtUTC(tomorrow, 18, 23))
                        .map(timeSlot -> new ReservationRequestsCreated(
                                template.parkingSpotId(),
                                template.templateId(),
                                ParkingSpotTimeSlotId.newOne(),
                                timeSlot))
                        .toList());


        events.forEach(parkingSpotReservationRequestsRepository::publish);
    }

}
