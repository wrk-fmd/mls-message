package at.wrk.fmd.mls.message.tetra;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Slf4j
@Component
public class TetraIssiNormalizer {

    private static final Pattern ALLOWED = Pattern.compile("\\d+");

    public String normalize(String number) {
        if (number == null || number.isBlank()) {
            return null;
        }

        // Ignore whitespace
        number = number.replace(" ", "");

        // Only allow digits
        return ALLOWED.matcher(number).matches() ? number : null;
    }
}
