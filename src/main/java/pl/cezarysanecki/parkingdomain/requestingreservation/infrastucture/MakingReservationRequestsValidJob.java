package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import pl.cezarysanecki.parkingdomain.requestingreservation.application.MakingReservationRequestsValid;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
class MakingReservationRequestsValidJob implements Job {

  private final MakingReservationRequestsValid makingReservationRequestsValid;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    log.debug("=== JOB {} STARTED ===", getClass().getSimpleName());
    makingReservationRequestsValid.makeValidSince();
    log.debug("=== JOB {} ENDED ===", getClass().getSimpleName());
  }

}
