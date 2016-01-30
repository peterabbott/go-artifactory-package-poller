package com.piemanpete.go.plugin.artifactorypackage.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class VersionNumberTest {
    @Test
    public void testGreaterThan() {
        VersionNumber lhs = VersionNumber.from("12.0.1");
        VersionNumber rhs = VersionNumber.from("10.0.1");

        assertThat(lhs).usingDefaultComparator().isGreaterThan(rhs);
    }

    @Test
    public void testLessThan() {
        VersionNumber lhs = VersionNumber.from("1.0.1");
        VersionNumber rhs = VersionNumber.from("2.0.1");

        assertThat(lhs).usingDefaultComparator().isLessThan(rhs);
    }

    @Test
    public void testEquals() {
        VersionNumber lhs = VersionNumber.from("2.0.1");
        VersionNumber rhs = VersionNumber.from("2.0.1");

        assertThat(lhs).usingDefaultComparator().isEqualTo(rhs);
    }

    @Test
    public void testGreaterThanWithQualifier() {
        VersionNumber lhs = VersionNumber.from("12.0.1-SNAPSHOT");
        VersionNumber rhs = VersionNumber.from("12.0.0");

        assertThat(lhs).usingDefaultComparator().isGreaterThan(rhs);
    }

    @Test
    public void testGreaterThanWithPatch() {
        VersionNumber lhs = VersionNumber.from("12.0.1.2");
        VersionNumber rhs = VersionNumber.from("12.0.1");

        assertThat(lhs).usingDefaultComparator().isGreaterThan(rhs);
    }

    @Test
    public void testLessThanWithQualifier() {
        VersionNumber lhs = VersionNumber.from("12.0.1-SNAPSHOT");
        VersionNumber rhs = VersionNumber.from("12.0.1");

        assertThat(lhs).usingDefaultComparator().isLessThan(rhs);
    }

    @Test
    public void testEqualsWithQualifier() {
        VersionNumber lhs = VersionNumber.from("2.0.1-SNAPSHOT");
        VersionNumber rhs = VersionNumber.from("2.0.1-SNAPSHOT");

        assertThat(lhs).usingDefaultComparator().isEqualTo(rhs);
    }
}