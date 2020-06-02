package at.wrk.fmd.mls.message.sms;

import at.wrk.fmd.mls.message.sms.config.FilterProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PhoneNumberNormalizer {

    private final FilterProperties filter;

    @Autowired
    public PhoneNumberNormalizer(final FilterProperties filter) {
        this.filter = filter;
    }

    public String normalize(String number) {
        if (number == null || number.isBlank()) {
            // Fast-track: Ignore null and blank numbers
            return null;
        }

        if (number.startsWith("+")) {
            // Country prefix already present: Ignore everything except the first + and digits for the rest of the string
            return "+" + number.substring(1).replaceAll("\\D", "");
        }

        // Ignore everything except digits
        number = number.replaceAll("\\D", "");

        if (number.startsWith("000")) {
            // More than two 0 at beginning: Not clear what this is, consider it invalid
            return null;
        }

        if (number.startsWith("00")) {
            // Replace 00 with + to get a normalized country prefix
            return "+" + number.substring(2);
        }

        if (number.startsWith("0") && filter.getDefaultCountryPrefix() != null) {
            // Replace single 0 with default country prefix if set
            return filter.getDefaultCountryPrefix() + number.substring(1);
        }

        // Still no country prefix: Consider invalid
        return null;
    }

    public boolean filter(final String number) {
        if (number == null || number.length() < 7) {
            // Need at least '+' prefix and 6 numbers
            return false;
        }

        if (filter.getPatterns().isEmpty()) {
            // No additional filter patterns configured, allow everything
            return true;
        }

        // Check if any pattern matches
        return filter.getPatterns().stream().anyMatch(p -> p.matcher(number).matches());
    }
}
