package pl.cezarysanecki.parkingdomain.requesting.parkingspot.infrastucture;

import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@Slf4j
@DisallowConcurrentExecution
class MakingRequestsValidJob implements Job {

    @Override
    public void execute(final JobExecutionContext jobExecutionContext) {
        log.debug("BLA BLA BLA BLA");
    }

}
