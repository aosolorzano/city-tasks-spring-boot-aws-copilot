package com.hiperium.city.tasks.api.service.impl;

import com.hiperium.city.tasks.api.dao.TasksDao;
import com.hiperium.city.tasks.api.dto.TaskCriteriaDto;
import com.hiperium.city.tasks.api.dto.TaskDto;
import com.hiperium.city.tasks.api.repository.TaskRepository;
import com.hiperium.city.tasks.api.service.TasksService;
import com.hiperium.city.tasks.api.utils.EntityUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class TasksServiceImpl implements TasksService {

    private final TaskRepository taskRepository;
    private final TasksDao tasksDao;

    public TasksServiceImpl(TaskRepository taskRepository, TasksDao tasksDao) {
        this.taskRepository = taskRepository;
        this.tasksDao = tasksDao;
    }

    public Flux<TaskDto> findAll() {
        return Flux.fromStream(() -> this.taskRepository.findAll().stream())
                .map(EntityUtil::toTaskDto);
    }

    public Flux<TaskDto> find(final TaskCriteriaDto criteriaDto) {
        return Flux.fromStream(() -> this.tasksDao.find(criteriaDto).stream());
    }
}
