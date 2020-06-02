package at.wrk.fmd.mls.message.tetra.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application.message.tetra.gateway")
@Getter
@Setter
class GatewayProperties {

    /**
     * The URL of the Tetra gateway
     */
    private String url;
}
