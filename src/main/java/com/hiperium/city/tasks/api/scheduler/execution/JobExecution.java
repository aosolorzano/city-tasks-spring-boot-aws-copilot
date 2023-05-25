package com.hiperium.city.tasks.api.scheduler.execution;

import com.hiperium.city.tasks.api.logger.HiperiumLogger;
import com.hiperium.city.tasks.api.repository.DeviceRepository;
import com.hiperium.city.tasks.api.repository.TaskRepository;
import com.hiperium.city.tasks.api.utils.SchedulerUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JobExecution implements Job {

    private static final HiperiumLogger LOGGER = HiperiumLogger.getLogger(JobExecution.class);

    private final TaskRepository taskRepository;
    private final DeviceRepository deviceRepository;

    public JobExecution(TaskRepository taskRepository, DeviceRepository deviceRepository) {
        this.taskRepository = taskRepository;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public void execute(final JobExecutionContext context) {
        LOGGER.debug("execute() - START");
        final String jobId = context.getJobDetail().getJobDataMap().getString(SchedulerUtil.TASK_JOB_ID_DATA_KEY);
        Mono.just(jobId)
                .map(this.taskRepository::findByJobId)
                .flatMap(this.deviceRepository::updateStatusByTaskOperation)
                .subscribe(
                        result -> LOGGER.debug("execute() - Job executed successfully: {}", jobId),
                        error -> LOGGER.error("execute() - Error: {}", error.getMessage())
                );
    }
}

