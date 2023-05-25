package com.hiperium.city.tasks.api.service;

import com.hiperium.city.tasks.api.dto.TaskCriteriaDto;
import com.hiperium.city.tasks.api.dto.TaskDto;
import reactor.core.publisher.Flux;

public interface TasksService {

    Flux<TaskDto> find(TaskCriteriaDto task);

    Flux<TaskDto> findAll();
}
