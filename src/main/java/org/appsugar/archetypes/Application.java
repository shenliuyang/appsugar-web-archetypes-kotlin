package org.appsugar.archetypes;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@SpringBootApplication
@RestController
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private ReloadProperty reloadProperty;

    @GetMapping("/")
    public String reload() {
        InputStream in = null;
        in.mark(12);
        return reloadProperty.name;
    }

    @Data
    @Component
    @ConfigurationProperties("db")
    public static class ReloadProperty {
        private String name;
    }
}
