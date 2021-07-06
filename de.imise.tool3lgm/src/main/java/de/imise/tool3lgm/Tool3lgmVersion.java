package de.imise.tool3lgm;

import de.imise.util.GitVersionInfoHandler.GitVersionInfo;

public class Tool3lgmVersion implements Comparable<Tool3lgmVersion> {

    /**
     *
     */
    public int major;

    /**
     *
     */
    public int minor;

    /**
     *
     */
    public int patch;

    /**
     *
     */
    public String suffix;

    @Override
    public int compareTo(final Tool3lgmVersion o) {
        if (equals(o)) {
            return 0;
        }

        if (major != o.major) {
            return major - o.major;
        }
        if (minor != o.minor) {
            return minor - o.minor;
        }
        if (patch != o.patch) {
            return patch - o.patch;
        }
        String suffix1 = suffix == null ? "" : suffix;
        String suffix2 = o.suffix == null ? "" : o.suffix;
        return suffix2.compareTo(suffix1); // Version "1.2.3_dev" should be smaller than Version "1.2.3"
    }

    /**
     * @param version
     * @return <code>true</code> if this is a lower version than the specified
     *         version
     */
    public boolean isLowerThan(final Tool3lgmVersion other) {
        return compareTo(other) < 0;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch + (suffix == null ? "" : suffix);
    }

    /**
     * @param gitVersionInfo
     * @return
     */
    public static Tool3lgmVersion parseGitVersion(final GitVersionInfo gitVersionInfo) {
        return parseString(gitVersionInfo.version);
    }

    /**
     * Parses Version Strings "Tool3lgmVersion 1.2.3_dev" to major=1, minor=2,
     * patch=3 and suffix ="_dev". If a prefix exists (in the example
     * "Tool3lgmVersion " it will be ignored. The optional parts are only the
     * prefix and the suffix. The numbers must exist in this string.
     *
     * @param versionStr
     * @return <code>null</code> if the version string is invali or the parsed
     *         version if ot is valid.
     */
    public static Tool3lgmVersion parseString(final String versionStr) {
        Tool3lgmVersion ret = new Tool3lgmVersion();
        try {
            //major
            int splitIndex2 = versionStr.indexOf('.');
            int splitIndex1 = splitIndex2 - 1;
            //remove string prefixes
            while (splitIndex1 - 1 >= 0 && Character.isDigit(versionStr.charAt(splitIndex1 - 1))) {
                splitIndex1--;
            }

            String vpart = versionStr.substring(splitIndex1, splitIndex2);
            ret.major = Integer.parseInt(vpart);

            //minor
            splitIndex1 = splitIndex2 + 1;
            splitIndex2 = versionStr.indexOf('.', splitIndex1);
            vpart = versionStr.substring(splitIndex1, splitIndex2);
            ret.minor = Integer.parseInt(vpart);

            //patch
            int versionStringLength = versionStr.length();
            splitIndex1 = splitIndex2 + 1;
            splitIndex2++;
            while (splitIndex2 < versionStringLength && Character.isDigit(versionStr.charAt(splitIndex2))) {
                splitIndex2++;
            }
            vpart = versionStr.substring(splitIndex1, splitIndex2);
            ret.patch = Integer.parseInt(vpart);

            //suffix
            if (splitIndex2 < versionStringLength) {
                ret.suffix = versionStr.substring(splitIndex2);
            } else {
                ret.suffix = "";
            }
        } catch (Exception e) {
            return null;
        }
        return ret;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + major;
        result = prime * result + minor;
        result = prime * result + patch;
        result = prime * result + (suffix == null ? 0 : suffix.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Tool3lgmVersion other = (Tool3lgmVersion) obj;
        if (major != other.major) {
            return false;
        }
        if (minor != other.minor) {
            return false;
        }
        if (patch != other.patch) {
            return false;
        }
        if (suffix == null) {
            if (other.suffix != null) {
                return false;
            }
        } else if (!suffix.equals(other.suffix)) {
            return false;
        }
        return true;
    }

}
