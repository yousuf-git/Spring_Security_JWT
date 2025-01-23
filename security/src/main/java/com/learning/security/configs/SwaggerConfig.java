// Contains the Swagger configuration for the application

package com.learning.security.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("Spring Security Authentication and Authorization by JWT")
                    
                    .version("v1.0.0")
                    
                    .description("This is a template Spring Boot RESTful microservice using Spring Security and JWT Token")
                    
                    .contact(new Contact().name("M. Yousuf").email("yousuf.work09@gmail.com").url("https://github.com/yousuf-git"))
                    
                    );

    }
}
