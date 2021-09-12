package at.wrk.fmd.mls.message.sms;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import at.wrk.fmd.mls.message.sms.config.FilterProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.regex.Pattern;

public class PhoneNumberNormalizerTest {

    private PhoneNumberNormalizer normalizer;

    @BeforeEach
    public void init() {
        FilterProperties properties = new FilterProperties();
        properties.setDefaultCountryPrefix("+43");
        properties.setPatterns(Collections.singleton(Pattern.compile("\\+436\\d+")));
        normalizer = new PhoneNumberNormalizer(properties);
    }

    @Test
    public void numberWithWhitespaces_whitespacesAreRemoved() {
        String normalized = normalizer.normalize("+43 678 933 543");
        assertThat(normalized, equalTo("+43678933543"));
    }

    @Test
    public void numberWithSpecialCharacters_allSpecialCharactersRemovedExceptPlus() {
        String normalized = normalizer.normalize("+43 (678) 933 - 289 /#\\?");
        assertThat(normalized, equalTo("+43678933289"));
    }

    @Test
    public void numberStartingWithSingleZero_startingZeroReplaced() {
        String normalized = normalizer.normalize("0678 933 368");
        assertThat(normalized, equalTo("+43678933368"));
    }

    @Test
    public void numberStartingWithDoubleZero_startingZeroReplaced() {
        String normalized = normalizer.normalize("00 43 678 933 361");
        assertThat(normalized, equalTo("+43678933361"));
    }

    @Test
    public void null_returnsNull() {
        String normalized = normalizer.normalize(null);
        assertThat(normalized, equalTo(null));
    }

    @Test
    public void numberWithForeignCarrier_isFormatted() {
        String normalized = normalizer.normalize("+49 678 933 543");
        assertThat(normalized, equalTo("+49678933543"));
    }

    @Test
    public void numberNotMatchingValidPrefix_isFormatted() {
        String normalized = normalizer.normalize("+43 128 933 543");
        assertThat(normalized, equalTo("+43128933543"));
    }

    @Test
    public void invalidNumber_null() {
        String normalized = normalizer.normalize("98234");
        assertThat(normalized, equalTo(null));
    }

    @Test
    public void validNumber_isNotFiltered() {
        assertTrue(normalizer.filter("+43678933543"));
    }

    @Test
    public void null_isFiltered() {
        assertFalse(normalizer.filter(null));
    }

    @Test
    public void shortNumber_isFiltered() {
        assertFalse(normalizer.filter("+43676"));
    }

    @Test
    public void numberWithForeignCarrier_isFiltered() {
        assertFalse(normalizer.filter("+49678933543"));
    }

    @Test
    public void numberNotMatchingValidPrefix_isFiltered() {
        assertFalse(normalizer.filter("+43128933543"));
    }
}