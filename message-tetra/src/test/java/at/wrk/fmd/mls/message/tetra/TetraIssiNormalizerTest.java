package at.wrk.fmd.mls.message.tetra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class TetraIssiNormalizerTest {

    private TetraIssiNormalizer normalizer;

    @Before
    public void init() {
        normalizer = new TetraIssiNormalizer();
    }

    @Test
    public void validIssi_returnIssi() {
        String issi = "2321009";
        String normalizedIssi = normalizer.normalize(issi);
        assertEquals("2321009", normalizedIssi);
    }

    @Test
    public void issiWithBlanks_returnTrimmedIssi() {
        String issi = " 232 1009";
        String normalizedIssi = normalizer.normalize(issi);
        assertEquals("2321009", normalizedIssi);
    }

    @Test
    public void emptyString_returnNull() {
        String issi = "  ";
        String normalizedIssi = normalizer.normalize(issi);
        assertNull(normalizedIssi);
    }

    @Test
    public void null_returnNull() {
        String normalizedIssi = normalizer.normalize(null);
        assertNull(normalizedIssi);
    }

    @Test
    public void issiContainsInvalidCharacters_returnNull() {
        String issi = "123aaa456";
        String normalizedIssi = normalizer.normalize(issi);
        assertNull(normalizedIssi);
    }
}