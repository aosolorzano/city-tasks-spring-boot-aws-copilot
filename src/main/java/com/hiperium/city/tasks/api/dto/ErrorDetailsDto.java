package com.hiperium.city.tasks.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class ErrorDetailsDto {

    public final ZonedDateTime errorDate;
    public final String requestedPath;
    public final String errorMessage;
    public final String errorCode;
}
