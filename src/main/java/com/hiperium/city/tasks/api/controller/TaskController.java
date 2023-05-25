package com.hiperium.city.tasks.api.controller;

import com.hiperium.city.tasks.api.dto.TaskOperationDto;
import com.hiperium.city.tasks.api.dto.TaskResponseDto;
import com.hiperium.city.tasks.api.logger.HiperiumLogger;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.service.TaskService;
import com.hiperium.city.tasks.api.utils.BeanValidationUtil;
import com.hiperium.city.tasks.api.utils.EntityUtil;
import com.hiperium.city.tasks.api.utils.TaskUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestController
@RequestMapping(TaskUtil.TASK_PATH)
public class TaskController {

    private static final HiperiumLogger LOGGER = HiperiumLogger.getLogger(TaskController.class);

    @Value("${reto.time.zone.id:-05:00}")
    private String zoneId;

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<TaskResponseDto> taskOperation(@RequestBody @Valid TaskOperationDto operationDto) {
        LOGGER.debug("taskOperation() - START: {}", operationDto.getOperation());
        BeanValidationUtil.validateBean(operationDto.getTask());
        return (switch (operationDto.getOperation()) {
            case CREATE -> this.taskService.create(EntityUtil.toTask(operationDto.getTask()));
            case UPDATE -> this.taskService.update(EntityUtil.toTask(operationDto.getTask()));
            case DELETE -> this.taskService.delete(EntityUtil.toTask(operationDto.getTask()));
        }).map(this::getResponseDto);
    }

    @GetMapping("/getTemplate")
    @ResponseStatus(HttpStatus.OK)
    public TaskOperationDto getTaskOperationTemplate() {
        return TaskUtil.getTaskOperationTemplate();
    }

    private TaskResponseDto getResponseDto(Task task) {
        return TaskResponseDto.builder()
                .date(ZonedDateTime.now(ZoneId.of(this.zoneId)))
                .task(EntityUtil.toTaskDto(task))
                .build();
    }
}
