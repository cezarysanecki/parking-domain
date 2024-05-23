package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.collection.List;
import io.vavr.collection.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplateRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotRepository;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.time.LocalDate;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotEvent.ReservationRequestCreated;

@Slf4j
@RequiredArgsConstructor
public class ExchangingReservationRequestsTimeSlots {

    private final ReservationRequestsTimeSlotRepository reservationRequestsTimeSlotRepository;
    private final ReservationRequestsTemplateRepository reservationRequestsTemplateRepository;

    public void exchangeTimeSlots(LocalDate date) {
        if (reservationRequestsTimeSlotRepository.containsAny()) {
            log.error("there are still reservation requests time slots, check it");
            return;
        }

        List<ReservationRequestCreated> events = reservationRequestsTemplateRepository.findAll()
                .flatMap(template -> Stream.of(
                                TimeSlot.createTimeSlotAtUTC(date, 7, 17),
                                TimeSlot.createTimeSlotAtUTC(date, 18, 23))
                        .map(timeSlot -> new ReservationRequestCreated(
                                template.templateId(),
                                ReservationRequestsTimeSlotId.newOne(),
                                timeSlot
                        )));
        log.debug("there is {} time slots to create", events.size());

        events.forEach(reservationRequestsTimeSlotRepository::publish);
    }

}
