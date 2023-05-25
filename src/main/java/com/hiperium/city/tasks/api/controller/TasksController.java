package com.hiperium.city.tasks.api.controller;

import com.hiperium.city.tasks.api.dto.TaskCriteriaDto;
import com.hiperium.city.tasks.api.dto.TaskDto;
import com.hiperium.city.tasks.api.logger.HiperiumLogger;
import com.hiperium.city.tasks.api.service.TasksService;
import com.hiperium.city.tasks.api.utils.BeanValidationUtil;
import com.hiperium.city.tasks.api.utils.TaskUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(TaskUtil.TASKS_PATH)
public class TasksController {

    private static final HiperiumLogger LOGGER = HiperiumLogger.getLogger(TasksController.class);

    private final TasksService tasksService;

    public TasksController(TasksService tasksService) {
        this.tasksService = tasksService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<TaskDto> findAll() {
        LOGGER.debug("findAll() - START");
        return this.tasksService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<TaskDto> find(@RequestBody TaskCriteriaDto criteriaDto) {
        LOGGER.debug("find() - START: {}", criteriaDto);
        BeanValidationUtil.validateBean(criteriaDto);
        return this.tasksService.find(criteriaDto);
    }

    @GetMapping("/getTemplate")
    @ResponseStatus(HttpStatus.OK)
    public TaskCriteriaDto getTemplateBody() {
        return TaskUtil.getTaskCriteriaTemplate();
    }
}
