package com.hiperium.city.tasks.api.repository.impl;

import com.hiperium.city.tasks.api.exception.ResourceNotFoundException;
import com.hiperium.city.tasks.api.logger.HiperiumLogger;
import com.hiperium.city.tasks.api.model.Device;
import com.hiperium.city.tasks.api.model.Task;
import com.hiperium.city.tasks.api.repository.DeviceRepository;
import com.hiperium.city.tasks.api.utils.DeviceUtil;
import com.hiperium.city.tasks.api.utils.enums.EnumDeviceOperation;
import com.hiperium.city.tasks.api.utils.enums.EnumDeviceStatus;
import com.hiperium.city.tasks.api.utils.enums.EnumResourceError;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Repository
public class DeviceRepositoryImpl implements DeviceRepository {

    private static final HiperiumLogger LOGGER = HiperiumLogger.getLogger(DeviceRepositoryImpl.class);

    private final DynamoDbAsyncClient dynamoDbAsyncClient;

    public DeviceRepositoryImpl(DynamoDbAsyncClient dynamoDbAsyncClient) {
        this.dynamoDbAsyncClient = dynamoDbAsyncClient;
    }

    public Mono<Boolean> updateStatusByTaskOperation(final Task task) {
        LOGGER.debug("updateStatusByTaskOperation(): {} - {}", task.getDeviceId(), task.getDeviceOperation());
        return this.findById(task.getDeviceId())
                .map(device -> {
                    if (EnumDeviceOperation.ACTIVATE.equals(task.getDeviceOperation())) {
                        device.setStatus(EnumDeviceStatus.ON);
                    } else {
                        device.setStatus(EnumDeviceStatus.OFF);
                    }
                    return device;
                })
                .flatMap(device -> Mono.fromFuture(
                        this.dynamoDbAsyncClient.putItem(DeviceUtil.putDeviceRequest(device))))
                .map(putItemResponse -> putItemResponse.sdkHttpResponse().isSuccessful());
    }

    public Mono<Device> findById(final String id) {
        LOGGER.debug("findById(): {}", id);
        return Mono.fromFuture(this.dynamoDbAsyncClient.getItem(DeviceUtil.getDeviceRequest(id)))
                .doOnNext(itemResponse -> {
                    if(!itemResponse.hasItem()) throw new ResourceNotFoundException(EnumResourceError.DEVICE_NOT_FOUND, id);
                })
                .map(DeviceUtil::getFromItemResponse);
    }
}
