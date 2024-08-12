package pl.cezarysanecki.parkingdomain.requesting.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.requesting.RequestingFacade;

import java.time.Instant;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
class CreatingRequestSectionsJob implements Job {

  private final DateProvider dateProvider;
  private final RequestingFacade requestingFacade;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    log.debug("=== JOB {} STARTED ===", getClass().getSimpleName());
    Instant date = dateProvider.now();

    requestingFacade.create(null, null);

    log.debug("=== JOB {} ENDED ===", getClass().getSimpleName());
  }

}
