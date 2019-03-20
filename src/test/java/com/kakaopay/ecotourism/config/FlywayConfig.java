package com.kakaopay.ecotourism.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
public class FlywayConfig {
    @Bean
    @Profile("test")
    public FlywayMigrationStrategy applyTestCustomStrategy() {
        return flyway -> {
            flyway.clean();
            flyway.migrate();
        };
    }
}
