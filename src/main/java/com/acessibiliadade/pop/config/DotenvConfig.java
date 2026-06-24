package com.acessibiliadade.pop.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import java.util.HashMap;
import java.util.Map;

public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        try {
            Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();
            
            Map<String, Object> envMap = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                envMap.put(entry.getKey(), entry.getValue());
                System.out.println("Carregado: " + entry.getKey());
            });
            
            environment.getPropertySources().addFirst(
                new MapPropertySource("dotenvProperties", envMap)
            );
            
            System.out.println("Arquivo .env carregado com sucesso!");
        } catch (Exception e) {
            System.out.println("Arquivo .env não encontrado. Usando variáveis de sistema.");
        }
    }
}