package com.hiperium.city.tasks.api.utils;

import com.hiperium.city.tasks.api.dto.TaskDto;
import com.hiperium.city.tasks.api.model.Task;
import org.springframework.beans.BeanUtils;

public final class EntityUtil {

    private EntityUtil() {
        // Private constructor to hide the implicit public one.
    }

    public static Task toTask(TaskDto dto) {
        Task obj = new Task();
        BeanUtils.copyProperties(dto, obj);
        return obj;
    }

    public static TaskDto toTaskDto(Task obj) {
        TaskDto dto = new TaskDto();
        BeanUtils.copyProperties(obj, dto);
        return dto;
    }
}
