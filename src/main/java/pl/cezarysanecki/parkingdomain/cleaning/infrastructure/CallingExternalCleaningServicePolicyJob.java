package pl.cezarysanecki.parkingdomain.cleaning.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import pl.cezarysanecki.parkingdomain.cleaning.application.CallingExternalCleaningServicePolicy;
import pl.cezarysanecki.parkingdomain.commons.commands.Result;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
class CallingExternalCleaningServicePolicyJob implements Job {

    private final CallingExternalCleaningServicePolicy callingExternalCleaningServicePolicy;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.debug("=== JOB {} STARTED ===", getClass().getSimpleName());
        Result result = callingExternalCleaningServicePolicy.handleCleaningPolicy();
        if (result == Result.Rejection) {
            log.debug("Need to wait some time to call external service");
        }
        log.debug("=== JOB {} ENDED ===", getClass().getSimpleName());
    }

}
