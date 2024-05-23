package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.ExchangingReservationRequestsTimeSlots;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
class ExchangingReservationRequestsTimeSlotsJob implements Job {

    private final DateProvider dateProvider;
    private final ExchangingReservationRequestsTimeSlots exchangingReservationRequestsTimeSlots;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.debug("=== JOB {} STARTED ===", getClass().getSimpleName());
        exchangingReservationRequestsTimeSlots.exchangeTimeSlots(dateProvider.tomorrow());
        log.debug("=== JOB {} ENDED ===", getClass().getSimpleName());
    }

}
