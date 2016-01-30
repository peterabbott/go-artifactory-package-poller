package com.piemanpete.go.plugin.artifactorypackage.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class VersionNumberDataTest {

    @Parameterized.Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] {
                { "1.0.2-SNAPSHOT", new VersionNumber(1, 0, 2, 0, "SNAPSHOT") },
                { "1.0.2", new VersionNumber(1, 0, 2, 0, null) },
                { "13.1.22", new VersionNumber(13, 1, 22, 0, null) },
                { "12.3", new VersionNumber(12, 3, 0, 0, null) },
                { "15.1.2.3", new VersionNumber(15, 1, 2, 3, null) },
                { "12.3.0.FINAL", new VersionNumber(12, 3, 0, 0, "FINAL") },

        });
    }

    private final String input;
    private final VersionNumber expected;

    public VersionNumberDataTest(String input, VersionNumber expected){
        this.input = input;
        this.expected = expected;
    }


    @Test
    public void testParse() {
        assertThat(VersionNumber.from(input)).isEqualTo(expected);
    }
}