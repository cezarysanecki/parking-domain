package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.CreatingReservationRequestTimeSlots;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
class CreatingreservationRequestsTimeSlotsJob implements Job {

    private final CreatingReservationRequestTimeSlots creatingReservationRequestTimeSlots;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.debug("=== JOB {} STARTED ===", getClass().getSimpleName());
        creatingReservationRequestTimeSlots.prepareNewTimeSlots();
        log.debug("=== JOB {} ENDED ===", getClass().getSimpleName());
    }

}
