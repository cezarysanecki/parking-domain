package pl.cezarysanecki.parkingdomain.reservation.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import pl.cezarysanecki.parkingdomain.reservation.usecase.RemovingNotUsedReservationsUseCase;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
class RemovingNotUsedReservationsJob implements Job {

  private final RemovingNotUsedReservationsUseCase useCase;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    log.debug("=== JOB {} STARTED ===", getClass().getSimpleName());

    useCase.run();

    log.debug("=== JOB {} ENDED ===", getClass().getSimpleName());
  }

}
