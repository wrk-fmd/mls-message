package at.wrk.fmd.mls.message.sms.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application.message.sms.gateway")
@Getter
@Setter
class GatewayProperties {

    /**
     * The url of the SMS gateway
     */
    private String url;

    /**
     * The username used for HTTP Basic authentication
     */
    private String username;

    /**
     * The password used for HTTP Basic authentication
     */
    private String password;
}
