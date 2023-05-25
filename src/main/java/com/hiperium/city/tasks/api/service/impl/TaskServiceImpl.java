package com.hiperium.city.tasks.api.service.impl;

import com.hiperium.city.tasks.api.exception.ResourceNotFoundException;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.repository.TaskRepository;
import com.hiperium.city.tasks.api.scheduler.TaskScheduler;
import com.hiperium.city.tasks.api.service.TaskService;
import com.hiperium.city.tasks.api.utils.enums.EnumResourceError;
import com.hiperium.city.tasks.api.utils.enums.EnumTaskStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.ZonedDateTime;
import java.util.Objects;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskScheduler taskScheduler;

    public TaskServiceImpl(TaskRepository taskRepository, TaskScheduler taskScheduler) {
        this.taskRepository = taskRepository;
        this.taskScheduler = taskScheduler;
    }

    public Mono<Task> create(final Task task) {
        return Mono.just(task)
                .doOnNext(this.taskScheduler::scheduleJob)
                .doOnNext(scheduledTask -> scheduledTask.setStatus(EnumTaskStatus.ACT))
                .doOnNext(scheduledTask -> scheduledTask.setCreatedAt(ZonedDateTime.now()))
                .doOnNext(scheduledTask -> scheduledTask.setUpdatedAt(ZonedDateTime.now()))
                .map(this.taskRepository::save);
    }

    public Mono<Task> update(final Task modifiedTask) {
        return this.findById(modifiedTask)
                .doOnNext(actualTask -> modifiedTask.setJobId(actualTask.getJobId()))
                .doOnNext(actualTask -> modifiedTask.setCreatedAt(actualTask.getCreatedAt()))
                .doOnNext(actualTask -> BeanUtils.copyProperties(modifiedTask, actualTask))
                .doOnNext(this.taskScheduler::rescheduleJob)
                .doOnNext(actualTask -> actualTask.setUpdatedAt(ZonedDateTime.now()))
                .map(this.taskRepository::save);
    }

    public Mono<Task> delete(final Task task) {
        return this.findById(task)
                .doOnNext(this.taskScheduler::unscheduleJob)
                .doOnNext(this.taskRepository::delete)
                .doOnNext(deletedTask -> {
                    deletedTask.setId(null);
                    deletedTask.setStatus(null);
                    deletedTask.setJobId(null);
                    deletedTask.setCreatedAt(null);
                    deletedTask.setUpdatedAt(null);
                });
    }

    private Mono<Task> findById(final Task task) {
        return Mono.fromSupplier(() -> this.taskRepository.findById(task.getId()))
                .filter(Objects::nonNull)
                .map(taskOptional -> taskOptional
                        .orElseThrow(() -> new ResourceNotFoundException(EnumResourceError.TASK_NOT_FOUND, task.getId())))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
