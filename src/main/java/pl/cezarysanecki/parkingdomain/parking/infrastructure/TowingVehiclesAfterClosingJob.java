package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import pl.cezarysanecki.parkingdomain.parking.usecase.TowingVehiclesAfterClosingUseCase;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
class TowingVehiclesAfterClosingJob implements Job {

  private final TowingVehiclesAfterClosingUseCase useCase;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    log.debug("=== JOB {} STARTED ===", getClass().getSimpleName());

    useCase.run();

    log.debug("=== JOB {} ENDED ===", getClass().getSimpleName());
  }

}
