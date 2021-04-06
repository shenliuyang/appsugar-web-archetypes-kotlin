package org.appsugar.archetypes;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactoryBean;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = EntityGraphJpaRepositoryFactoryBean.class)
@RestController
public class Application {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Autowired
    private ReloadProperty reloadProperty;

    @GetMapping("/")
    public String reload() {
        return reloadProperty.name;
    }

    @Data
    @Component
    @ConfigurationProperties("db")
    public static class ReloadProperty {
        private String name;
    }
}
