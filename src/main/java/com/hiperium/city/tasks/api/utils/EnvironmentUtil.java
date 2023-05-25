package com.hiperium.city.tasks.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiperium.city.tasks.api.logger.HiperiumLogger;
import com.hiperium.city.tasks.api.vo.AuroraSecretsVo;

import java.util.Objects;

public final class EnvironmentUtil {

    private static final HiperiumLogger LOGGER = HiperiumLogger.getLogger(EnvironmentUtil.class);

    private EnvironmentUtil() {
        // Empty constructor.
    }

    public static AuroraSecretsVo getAuroraSecretVO() throws JsonProcessingException {
        String auroraSecret = System.getenv("CITY_TASKS_DB_CLUSTER_SECRET");
        if (Objects.isNull(auroraSecret) || auroraSecret.isBlank()) {
            LOGGER.warn("Environment variable 'CITY_TASKS_DB_CLUSTER_SECRET' not found.");
            return null;
        }
        return new ObjectMapper().readValue(auroraSecret, AuroraSecretsVo.class);
    }

    public static String getIdpEndpoint() {
        String idpEndpoint = System.getenv("CITY_IDP_ENDPOINT");
        if (Objects.isNull(idpEndpoint) || idpEndpoint.isBlank()) {
            LOGGER.warn("Environment variable 'CITY_IDP_ENDPOINT' not found.");
        }
        return idpEndpoint;
    }

    public static String getTimeZone() {
        String timeZoneId = System.getenv("CITY_TASKS_TIME_ZONE");
        if (Objects.isNull(timeZoneId) || timeZoneId.isBlank()) {
            LOGGER.warn("Environment variable 'CITY_TASKS_TIME_ZONE' not found.");
        }
        return timeZoneId;
    }

    public static String getAwsRegion() {
        String awsRegion = System.getenv("AWS_DEFAULT_REGION");
        if (Objects.isNull(awsRegion) || awsRegion.isBlank()) {
            LOGGER.warn("AWS_DEFAULT_REGION not found. Using defaults.");
        }
        return awsRegion;
    }

    public static String getAwsAccessKey() {
        String awsAccessKey = System.getenv("AWS_ACCESS_KEY_ID");
        if (Objects.isNull(awsAccessKey) || awsAccessKey.isBlank()) {
            LOGGER.warn("AWS_ACCESS_KEY_ID not found. Using defaults.");
        }
        return awsAccessKey;
    }

    public static String getAwsSecretKey() {
        String awsSecretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        if (Objects.isNull(awsSecretKey) || awsSecretKey.isBlank()) {
            LOGGER.warn("AWS_SECRET_ACCESS_KEY not found. Using defaults.");
        }
        return awsSecretKey;
    }

    public static String getAwsEndpointOverride() {
        String endpointOverride = System.getenv("AWS_ENDPOINT_OVERRIDE");
        if (Objects.isNull(endpointOverride) || endpointOverride.isBlank()) {
            LOGGER.warn("AWS_ENDPOINT_OVERRIDE not found. Using defaults.");
        }
        return endpointOverride;
    }
}
