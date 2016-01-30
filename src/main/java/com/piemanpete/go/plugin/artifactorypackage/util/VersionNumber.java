package com.piemanpete.go.plugin.artifactorypackage.util;


import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Represent version number
 */
public final class VersionNumber implements Serializable, Comparable<VersionNumber> {
    private static final long serialVersionUID = -49284232392570856L;

    private final int major;
    private final int minor;
    private final int micro;
    private final int patch;
    private final String qualifier;

    private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(?:\\.(\\d+))?(?:(?:-|.)(.*))?$");

    public VersionNumber(int major, int minor, int micro, int patch, String qualifier) {
        this.major = major;
        this.minor = minor;
        this.micro = micro;
        this.patch = patch;
        this.qualifier = qualifier;
    }

    public static VersionNumber from(String version) {
        Matcher m = VERSION_PATTERN.matcher(Preconditions.checkNotNull(version, "version can't be null"));
        if (m.find()) {
            return new VersionNumber(number(m, 1), number(m, 2), number(m, 3), number(m, 4), m.group(5));
        }

        throw new IllegalArgumentException(String.format("unable to parse %s as a Version", version));
    }

    private static int number(Matcher m, int i) {
        String group = m.group(i);
        if (group != null) {
            return Integer.parseInt(group);
        }

        return 0;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getMicro() {
        return micro;
    }

    public int getPatch() {
        return patch;
    }

    public String getQualifier() {
        return qualifier;
    }

    @Override
    public int compareTo(VersionNumber o) {
        if (o == null) return -1;

        if (equals(o)) {
            return 0;
        } else if (notEqual(major, o.getMajor())) {
            return major - o.getMajor();
        } else if (notEqual(minor, o.getMinor())) {
            return minor - o.getMinor();
        } else if (notEqual(micro, o.getMicro())) {
            return micro - o.getMicro();
        } else if (notEqual(patch, o.getPatch())) {
            return patch - o.getPatch();
        }

        return compareQualifier(qualifier, o.getQualifier());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VersionNumber that = (VersionNumber) o;

        boolean baseVersionEquals =
                major == that.major &&
                minor == that.minor &&
                micro == that.micro &&
                patch == that.patch;

        if (baseVersionEquals) {
            if (Objects.equal(qualifier, that.qualifier)) {
                return true;
            }

            // empty qualifier and RELEASE are the same
            return MoreObjects.firstNonNull(qualifier, "RELEASE")
                    .equals(MoreObjects.firstNonNull(that.qualifier, "RELEASE"));
        }

        return false;
    }

    private boolean notEqual(int lhs, int rhs) {
        return lhs != rhs;
    }

    private int compareQualifier(String lhs, String rhs) {
        int leftQualifier = mapQualifierValue(lhs);
        int rightQualifier = mapQualifierValue(rhs);

        return leftQualifier - rightQualifier;
    }

    private int mapQualifierValue(String qualifier) {
        String value = MoreObjects.firstNonNull(qualifier, "");
        if (value.equals("SNAPSHOT")) {
            return 1;
        } else if (value.matches("RC\\d+")) {
            return 10 + Integer.parseInt(value.substring(2));
        }

        // typically be null, "", "RELEASE" or "GA" to represent released version
        return 1000;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(major, minor, micro, patch, qualifier);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("major", major)
                .add("minor", minor)
                .add("micro", micro)
                .add("patch", patch)
                .add("qualifier", qualifier)
                .toString();
    }
}
