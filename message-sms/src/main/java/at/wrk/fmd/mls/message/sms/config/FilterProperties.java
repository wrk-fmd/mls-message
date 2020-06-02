package at.wrk.fmd.mls.message.sms.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.regex.Pattern;

@Component
@ConfigurationProperties(prefix = "application.message.sms.filter")
@Getter
@Setter
public class FilterProperties {

    /**
     * A list of Regex patterns used for matching valid recipients
     */
    private Collection<Pattern> patterns;

    /**
     * The default country prefix to use for recipients without one (e.g. "+43")
     */
    private String defaultCountryPrefix;
}
