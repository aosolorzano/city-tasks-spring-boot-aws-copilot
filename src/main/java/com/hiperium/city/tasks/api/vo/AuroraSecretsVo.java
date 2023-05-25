package com.hiperium.city.tasks.api.vo;

public record AuroraSecretsVo(String host, String port, String dbname,
                              String username, String password,
                              String engine, String dbClusterIdentifier) {
}
