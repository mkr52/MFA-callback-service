package com.example.mfacallbacks.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI (Swagger) documentation.
 * 
 * <p>This class configures the API documentation that will be available at:
 * <ul>
 *   <li>API Docs JSON: /v3/api-docs</li>
 *   <li>Swagger UI: /swagger-ui.html</li>
 * </ul>
 * 
 * <p>Features configured:
 * <ul>
 *   <li>API title, description, and version</li>
 *   <li>Contact information for API support</li>
 *   <li>License information</li>
 *   <li>JWT Bearer token authentication</li>
 * </ul>
 * 
 * <p>The generated documentation includes all controllers and models
 * with their respective API endpoints and data structures.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI documentation for the application.
     * 
     * @return OpenAPI configuration with API metadata, security schemes, and components
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MFA Callback Service API")
                        .description("Secure Spring Boot Callback Service for Multi-Factor Authentication Integration")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token from Transmit Security")));
    }
}
