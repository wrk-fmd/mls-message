package at.wrk.fmd.mls.message.sms.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder, GatewayProperties properties) {
        builder = builder.rootUri(properties.getUrl());
        if (properties.getUsername() != null && properties.getPassword() != null) {
            builder = builder.basicAuthentication(properties.getUsername(), properties.getPassword());
        }
        return builder.build();
    }
}
