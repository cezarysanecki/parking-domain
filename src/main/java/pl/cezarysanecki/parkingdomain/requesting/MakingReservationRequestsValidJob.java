package pl.cezarysanecki.parkingdomain.requesting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.time.Instant;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
class MakingReservationRequestsValidJob implements Job {

  private final MakingReservationRequestsValid makingReservationRequestsValid;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    Instant date = jobExecutionContext.getFireTime().toInstant();

    log.debug("=== JOB {} STARTED ===", getClass().getSimpleName());
    makingReservationRequestsValid.makeAllValidSince(date);
    log.debug("=== JOB {} ENDED ===", getClass().getSimpleName());
  }

}
