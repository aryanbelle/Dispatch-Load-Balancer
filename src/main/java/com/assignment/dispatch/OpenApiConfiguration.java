package com.assignment.dispatch;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI dispatchLoadBalancerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dispatch Load Balancer API")
                        .description("REST API for submitting delivery orders and vehicles and generating a capacity-aware dispatch plan.")
                        .version("1.0.0"));
    }
}
