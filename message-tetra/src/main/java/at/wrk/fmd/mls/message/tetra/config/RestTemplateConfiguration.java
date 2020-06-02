package at.wrk.fmd.mls.message.tetra.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder, GatewayProperties properties) {
        return builder
                .rootUri(properties.getUrl())
                .build();
    }
}
