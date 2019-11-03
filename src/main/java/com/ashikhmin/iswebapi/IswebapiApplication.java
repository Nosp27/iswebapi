package com.ashikhmin.iswebapi;

import com.ashikhmin.controller.FacilityController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.function.Supplier;

@EntityScan(basePackages = "com.ashikhmin.model")
@EnableJpaRepositories("com.ashikhmin.model")
@SpringBootApplication
@ComponentScan(basePackageClasses = FacilityController.class)
public class IswebapiApplication {
    public static void main(String[] args) {
        SpringApplication.run(IswebapiApplication.class, args);
    }

    public static Supplier<RuntimeException> valueError(String message) {
        return () -> new RuntimeException(message);
    }
}
