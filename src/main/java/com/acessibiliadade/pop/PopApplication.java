package com.acessibiliadade.pop;

import com.acessibiliadade.pop.config.DotenvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PopApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PopApplication.class);
        app.addInitializers(new DotenvConfig());
        app.run(args);
    }
}