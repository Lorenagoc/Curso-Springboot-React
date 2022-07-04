package com.groupsoftware.minhasfinancas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableWebMvc
public class MinhasfinancasApplication implements WebMvcConfigurer {

    // Define de onde poderemos fazer requisições para essa aplicação
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        // Aberto para todos, pois está em ambiente de desenvolvimento
        registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }

    public static void main(String[] args) {
        SpringApplication.run(MinhasfinancasApplication.class, args);
    }

}
