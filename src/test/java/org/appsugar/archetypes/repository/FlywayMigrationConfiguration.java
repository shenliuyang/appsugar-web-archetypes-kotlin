package org.appsugar.archetypes.repository;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
public class FlywayMigrationConfiguration {

    /**
     * Override default flyway initializer to do nothing
     */
    @Bean
    FlywayMigrationInitializer flywayInitializer(Flyway flyway) {
        //测试开始前, 先清除所有数据.再让jpa重建数据库
        return new FlywayMigrationInitializer(flyway, Flyway::clean);
    }


    @Configuration
    public static class FlywayMigrater {
        /**
         *
         */
        @Autowired
        void delayedFlywayInitializer(Flyway flyway, EntityManager emf) {
            flyway.migrate();
        }
    }


}
