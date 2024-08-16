package pl.cezarysanecki.parkingdomain.requesting.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade;

import java.time.Instant;
import java.time.LocalDate;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
class MakingRequestsValidJob implements Job {

  private final DateProvider dateProvider;
  private final RequestingFacade requestingFacade;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    log.debug("=== JOB {} STARTED ===", getClass().getSimpleName());
    LocalDate currentDay = dateProvider.currentDay();

    requestingFacade.makeValidFor(currentDay);

    log.debug("=== JOB {} ENDED ===", getClass().getSimpleName());
  }

}
