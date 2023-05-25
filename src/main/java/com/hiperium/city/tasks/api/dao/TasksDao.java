package com.hiperium.city.tasks.api.dao;

import com.hiperium.city.tasks.api.dto.TaskCriteriaDto;
import com.hiperium.city.tasks.api.dto.TaskDto;

import java.util.List;

public interface TasksDao {

    List<TaskDto> find(TaskCriteriaDto customerCriteriaDTO);
}
