package com.hiperium.city.tasks.api.repository;

import com.hiperium.city.tasks.api.model.Device;
import com.hiperium.city.tasks.api.model.Task;
import reactor.core.publisher.Mono;

public interface DeviceRepository {

    Mono<Boolean> updateStatusByTaskOperation(Task task);

    Mono<Device> findById(String id);

}
