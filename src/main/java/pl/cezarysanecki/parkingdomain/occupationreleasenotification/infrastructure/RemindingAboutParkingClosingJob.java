package pl.cezarysanecki.parkingdomain.occupationreleasenotification.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import pl.cezarysanecki.parkingdomain.occupationreleasenotification.usecase.RemindingAboutParkingClosingUseCase;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
class RemindingAboutParkingClosingJob implements Job {

  private final RemindingAboutParkingClosingUseCase remindingAboutParkingClosingUseCase;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    log.debug("=== JOB {} STARTED ===", getClass().getSimpleName());

    remindingAboutParkingClosingUseCase.run();

    log.debug("=== JOB {} ENDED ===", getClass().getSimpleName());
  }

}
