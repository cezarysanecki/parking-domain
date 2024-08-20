package pl.cezarysanecki.parkingdomain.occupationreleasenotification.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import pl.cezarysanecki.parkingdomain.occupationreleasenotification.usecase.NotifyingAboutReleasingOccupationUseCase;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
class NotifyingAboutReleasingJob implements Job {

  private final NotifyingAboutReleasingOccupationUseCase notifyingAboutReleasingOccupationUseCase;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    log.debug("=== JOB {} STARTED ===", getClass().getSimpleName());

    notifyingAboutReleasingOccupationUseCase.run();

    log.debug("=== JOB {} ENDED ===", getClass().getSimpleName());
  }

}
