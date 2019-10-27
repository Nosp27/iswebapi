package com.ashikhmin.iswebapi;

import com.ashikhmin.controller.ISAppController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = "com.ashikhmin.model")
@EnableJpaRepositories("com.ashikhmin.model")
@SpringBootApplication
@ComponentScan(basePackageClasses = ISAppController.class)
public class IswebapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(IswebapiApplication.class, args);
    }

}
