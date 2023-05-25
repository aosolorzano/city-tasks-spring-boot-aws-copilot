package com.hiperium.city.tasks.api.scheduler.impl;

import com.hiperium.city.tasks.api.exception.ResourceNotFoundException;
import com.hiperium.city.tasks.api.exception.TaskSchedulerException;
import com.hiperium.city.tasks.api.logger.HiperiumLogger;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.scheduler.TaskScheduler;
import com.hiperium.city.tasks.api.utils.SchedulerUtil;
import com.hiperium.city.tasks.api.utils.TaskUtil;
import com.hiperium.city.tasks.api.utils.enums.EnumResourceError;
import com.hiperium.city.tasks.api.utils.enums.EnumSchedulerError;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class TaskSchedulerImpl implements TaskScheduler {

    private static final HiperiumLogger LOGGER = HiperiumLogger.getLogger(TaskSchedulerImpl.class);

    @Value("${city.tasks.time.zone}")
    private String timeZone;

    private final Scheduler scheduler;

    public TaskSchedulerImpl(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void scheduleJob(final Task task) {
        LOGGER.debug("scheduleJob() - BEGIN: {}", task.getName());
        task.setJobId(TaskUtil.generateJobId());
        JobDetail job = SchedulerUtil.createJobDetailFromTask(task);
        Trigger trigger = SchedulerUtil.createCronTriggerFromTask(task, this.timeZone);
        try {
            this.scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e, EnumSchedulerError.SCHEDULE_JOB_ERROR, task.getName());
        }
        LOGGER.debug("scheduleJob() - END");
    }

    public void rescheduleJob(final Task task) {
        LOGGER.debug("rescheduleJob() - BEGIN: {}", task.getId());
        Trigger currentTrigger = this.getCurrentTrigger(task);
        Trigger newTrigger = SchedulerUtil.createCronTriggerFromTask(task, this.timeZone);
        try {
            this.scheduler.rescheduleJob(currentTrigger.getKey(), newTrigger);
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e, EnumSchedulerError.RESCHEDULE_JOB_ERROR, task.getId());
        }
        LOGGER.debug("rescheduleJob() - END");
    }

    public void unscheduleJob(final Task task) {
        LOGGER.debug("unscheduleJob() - BEGIN: {}", task.getId());
        Trigger currentTrigger = this.getCurrentTrigger(task);
        try {
            this.scheduler.unscheduleJob(currentTrigger.getKey());
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e, EnumSchedulerError.UNSCHEDULE_JOB_ERROR, task.getId());
        }
        LOGGER.debug("unscheduleJob() - END");
    }

    private Trigger getCurrentTrigger(final Task task) {
        LOGGER.debug("getCurrentTrigger() - BEGIN: {}", task.getId());
        Trigger trigger = null;
        try {
            for (JobKey jobKey : this.scheduler.getJobKeys(GroupMatcher.jobGroupEquals(SchedulerUtil.TASK_GROUP_NAME))) {
                if (jobKey.getName().equals(task.getJobId())) {
                    TriggerKey triggerKey = TriggerKey.triggerKey(task.getJobId(), SchedulerUtil.TASK_GROUP_NAME);
                    trigger = this.scheduler.getTrigger(triggerKey);
                }
            }
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e, EnumSchedulerError.GET_CURRENT_TRIGGER_ERROR, task.getId());
        }
        if (Objects.isNull(trigger)) {
            throw new ResourceNotFoundException(EnumResourceError.TRIGGER_NOT_FOUND, task.getId());
        }
        LOGGER.debug("getTrigger() - END");
        return trigger;
    }
}
