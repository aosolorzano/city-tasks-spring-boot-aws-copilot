package com.hiperium.city.tasks.api.config;

import com.hiperium.city.tasks.api.logger.HiperiumLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    private static final HiperiumLogger LOGGER = HiperiumLogger.getLogger(SecurityConfiguration.class);

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        LOGGER.debug("securityFilterChain() - START");
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(withDefaults()));
        return http.build();
    }

    // Used for Spring Native compatibility with "WebFluxSecurityConfiguration" component.
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        LOGGER.debug("jwtDecoder() - START: {}", this.issuerUri);
        return ReactiveJwtDecoders.fromIssuerLocation(this.issuerUri);
    }
}
