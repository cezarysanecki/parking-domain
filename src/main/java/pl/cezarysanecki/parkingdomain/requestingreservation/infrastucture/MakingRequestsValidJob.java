package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import io.vavr.collection.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.MakingReservationRequestsValid;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
class MakingRequestsValidJob implements Job {

    private final MakingReservationRequestsValid makingReservationRequestsValid;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.debug("=== JOB {} STARTED ===", getClass().getSimpleName());
        List<MakingReservationRequestsValid.Problem> results = makingReservationRequestsValid.makeValid();
        if (!results.isEmpty()) {
            results.forEach(
                    result -> log.error("There was problem with making request valid, problem: {}", result.reason())
            );
        }
        log.debug("=== JOB {} ENDED ===", getClass().getSimpleName());
    }

}
