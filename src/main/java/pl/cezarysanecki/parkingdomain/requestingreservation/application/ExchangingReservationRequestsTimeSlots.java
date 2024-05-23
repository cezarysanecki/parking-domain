package pl.cezarysanecki.parkingdomain.requestingreservation.application;

import io.vavr.collection.List;
import io.vavr.collection.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplate;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplateRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotsRepository;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
public class ExchangingReservationRequestsTimeSlots {

    private final DateProvider dateProvider;
    private final ReservationRequestsTimeSlotsRepository reservationRequestsTimeSlotsRepository;
    private final ReservationRequestsTemplateRepository reservationRequestsTemplateRepository;

    public void exchangeTimeSlots() {
        if (reservationRequestsTimeSlotsRepository.containsAny()) {
            log.error("there are still reservation requests time slots, check it");
            return;
        }
        LocalDate tomorrow = dateProvider.tomorrow();

        List<TimeSlotToCreate> timeSlotsToCreate = reservationRequestsTemplateRepository.findAll()
                .flatMap(template -> Stream.of(
                                TimeSlot.createTimeSlotAtUTC(tomorrow, 7, 17),
                                TimeSlot.createTimeSlotAtUTC(tomorrow, 18, 23))
                        .map(timeSlot -> new TimeSlotToCreate(template, timeSlot)));
        log.debug("there is {} time slots to create", timeSlotsToCreate.size());

        timeSlotsToCreate.forEach(timeSlotToCreate -> reservationRequestsTimeSlotsRepository.saveNewUsing(
                timeSlotToCreate.template.templateId(), timeSlotToCreate.timeSlot));
    }

    record TimeSlotToCreate(
            ReservationRequestsTemplate template,
            TimeSlot timeSlot
    ) {
    }

}
