package org.appsugar.archetypes.repository;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
@Slf4j
public class FlywayMigrationConfiguration {

    /**
     * Override default flyway initializer to do nothing
     */
    @Bean
    FlywayMigrationInitializer flywayInitializer(Flyway flyway) {
        //测试开始前, 先清除所有数据.再让jpa重建数据库
        return new FlywayMigrationInitializer(flyway, f -> {
            log.debug("prepare to clean all database ");
            f.clean();
        });
    }


    @Configuration
    @Slf4j
    public static class FlywayMigrater {
        /**
         *
         */
        @Autowired
        @SneakyThrows
        void delayedFlywayInitializer(ApplicationContext context, EntityManager em) {
            log.debug("prepare to migrate");
            //this help ddl to be done  before flyway insert test data
            em.createNativeQuery("");
            context.getBean(Flyway.class).migrate();
        }
    }


}
