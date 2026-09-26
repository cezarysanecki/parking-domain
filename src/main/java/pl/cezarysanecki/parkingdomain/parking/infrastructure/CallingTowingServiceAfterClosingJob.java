package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import pl.cezarysanecki.parkingdomain.parking.usecase.CallingTowingServiceAfterClosingUseCase;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
class CallingTowingServiceAfterClosingJob implements Job {

  private final CallingTowingServiceAfterClosingUseCase useCase;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    log.debug("=== JOB {} STARTED ===", getClass().getSimpleName());

    useCase.run();

    log.debug("=== JOB {} ENDED ===", getClass().getSimpleName());
  }

}
