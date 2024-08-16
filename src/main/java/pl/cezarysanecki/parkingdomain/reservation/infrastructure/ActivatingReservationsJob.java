package pl.cezarysanecki.parkingdomain.reservation.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import pl.cezarysanecki.parkingdomain.reservation.usecase.ActivatingReservationsUseCase;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
class ActivatingReservationsJob implements Job {

  private final ActivatingReservationsUseCase useCase;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    log.debug("=== JOB {} STARTED ===", getClass().getSimpleName());

    useCase.run();

    log.debug("=== JOB {} ENDED ===", getClass().getSimpleName());
  }

}
