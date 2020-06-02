package at.wrk.fmd.mls.message.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application.message")
@Getter
@Setter
public class MessageServiceProperties {

    /**
     * The type of recipient address this message service handles
     */
    private String type;
}
