package com.madebawojo.nysc.ppa.clearance.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clearance Automation API")
                        .version("1.0.0")
                        .description("API documentation for Corper Clearance Automation Project @ ETZ")
                        .contact(new Contact()
                                .name("Adebawojo Mosopefoluwa")
                                .email("adebawojomosope@gmail.com")
                        )
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")
                        )
                ).components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}


//public OpenAPI customOpenAPI() {
//    return new OpenAPI()
//            .components(new Components()
//                    .addSecuritySchemes("bearerAuth", new SecurityScheme()
//                            .name("Authorization")
//                            .type(SecurityScheme.Type.APIKEY)
//                            .in(SecurityScheme.In.HEADER)
//                            .scheme("bearer")
//                            .bearerFormat("JWT")
//                    )
//            )
//            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
//}
//@Configuration
//public class SwaggerConfig {

//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//                .info(new Info().title("Clearance Automation API")
////                        .version("1.0")
////                        .description("API documentation for my Spring Boot application"))
//                .version("1.0.0")
//                .description("API documentation for Corper Clearance Automation Project @ ETZ")
//                // Adding the author information via the Contact object
//                .contact(new Contact()
//                        .name("Adebawojo Mosopefoluwa")
//                        .email("adebawojomosope@gmail.com")
////                        .url("https://www.example.com/contact")
//                )
//                .license(new License()
//                        .name("Apache 2.0")
//                        .url("http://springdoc.org"))
//                );
//
//    }


//}