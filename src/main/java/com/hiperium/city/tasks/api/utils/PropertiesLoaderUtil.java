package com.hiperium.city.tasks.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hiperium.city.tasks.api.logger.HiperiumLogger;
import com.hiperium.city.tasks.api.vo.AuroraSecretsVo;

import java.text.MessageFormat;
import java.util.Objects;

public final class PropertiesLoaderUtil {

    private static final HiperiumLogger LOGGER = HiperiumLogger.getLogger(PropertiesLoaderUtil.class);
    private static final String JDBC_SQL_CONNECTION = "jdbc:postgresql://{0}:{1}/{2}";

    private PropertiesLoaderUtil() {
        // Private constructor.
    }

    public static void loadProperties() throws JsonProcessingException {
        LOGGER.info("loadProperties() - BEGIN");
        setDatasourceConnection();
        setIdentityProviderEndpoint();
        setApplicationTimeZone();
        setAwsCredentials();
        setAwsEndpointOverride();
        LOGGER.info("loadProperties() - END");
    }

    public static void setDatasourceConnection() throws JsonProcessingException {
        AuroraSecretsVo auroraSecretVO = EnvironmentUtil.getAuroraSecretVO();
        if (Objects.nonNull(auroraSecretVO)) {
            String sqlConnection = MessageFormat.format(JDBC_SQL_CONNECTION, auroraSecretVO.host(),
                    auroraSecretVO.port(), auroraSecretVO.dbname());
            LOGGER.debug("JDBC Connection: {}", sqlConnection);
            // Set Datasource connection for JPA.
            System.setProperty("spring.datasource.url", sqlConnection);
            System.setProperty("spring.datasource.username", auroraSecretVO.username());
            System.setProperty("spring.datasource.password", auroraSecretVO.password());
            // Set Datasource connection for Quartz.
            System.setProperty("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.URL", sqlConnection);
            System.setProperty("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.user", auroraSecretVO.username());
            System.setProperty("spring.quartz.properties.org.quartz.dataSource.cityTasksQuartzDS.password", auroraSecretVO.password());
        }
    }

    public static void setIdentityProviderEndpoint() {
        String idpEndpoint = EnvironmentUtil.getIdpEndpoint();
        if (Objects.nonNull(idpEndpoint) && !idpEndpoint.isEmpty()) {
            LOGGER.debug("IdP URI: {}", idpEndpoint);
            System.setProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri", idpEndpoint);
        }
    }

    public static void setApplicationTimeZone() {
        String timeZoneId = EnvironmentUtil.getTimeZone();
        if (Objects.nonNull(timeZoneId) && !timeZoneId.isEmpty()) {
            LOGGER.debug("Time Zone: {}", timeZoneId);
            System.setProperty("city.tasks.time.zone", timeZoneId);
        }
    }

    public static void setAwsCredentials() {
        String awsAccessKey = EnvironmentUtil.getAwsAccessKey();
        if (Objects.nonNull(awsAccessKey) && !awsAccessKey.isEmpty()) {
            LOGGER.debug("AWS Access Key: {}", awsAccessKey);
            System.setProperty("aws.accessKeyId", awsAccessKey);
        }
        String awsSecretKey = EnvironmentUtil.getAwsSecretKey();
        if (Objects.nonNull(awsSecretKey) && !awsSecretKey.isEmpty()) {
            LOGGER.debug("AWS Secret Key: {}", awsSecretKey);
            System.setProperty("aws.secretKey", awsSecretKey);
        }
        String awsRegion = EnvironmentUtil.getAwsRegion();
        if (Objects.nonNull(awsRegion) && !awsRegion.isEmpty()) {
            LOGGER.debug("AWS Region: {}", awsRegion);
            System.setProperty("aws.region", awsRegion);
        }
    }

    public static void setAwsEndpointOverride() {
        String endpointOverride = EnvironmentUtil.getAwsEndpointOverride();
        if (Objects.nonNull(endpointOverride) && !endpointOverride.isEmpty()) {
            LOGGER.debug("AWS Endpoint-Override: {}", endpointOverride);
            System.setProperty("aws.dynamodb.endpoint-override", endpointOverride);
        }
    }
}
