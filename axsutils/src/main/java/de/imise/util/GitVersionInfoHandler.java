package de.imise.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author AXS + Yoshi (05.11.2020)
 */
public class GitVersionInfoHandler {

    /**
     * Git Version with tag and commit ID
     */
    public static class GitVersionInfo {

        /** The tag name */
        public String tag = "Unknown Version";

        /** The commit ID */
        public String commit = "Unknown Commit";
    }

    /**
     * Reads the git tag from the verion.info file and splits it into two
     * Strings, so that it fits the Splashscreen
     *
     * @param versionInfoFile
     * @return
     */
    public static final GitVersionInfo getGitVersionInfo(final File versionInfoFile) {
        return getGitVersionInfo(versionInfoFile, null);
    }

    /**
     * Reads the git tag from the verion.info file and splits it into two
     * Strings, so that it fits the Splashscreen
     *
     * @param versionInfoFile
     * @param ignoreTagPrefix if the value with the tag and commit starts with
     *            this prefix, then the result will not contain this part of the
     *            string
     * @return
     */
    public static final GitVersionInfo getGitVersionInfo(final File versionInfoFile, final String ignoreTagPrefix) {
        String gitTag = null;
        GitVersionInfo versionInfo = new GitVersionInfo();
        try (FileInputStream fileInputStream = new FileInputStream(versionInfoFile)) {
            Properties properties = new Properties();
            properties.load(fileInputStream);
            Object gitTagObject = properties.get("git.commit.id.describe");
            gitTag = gitTagObject == null ? null : gitTagObject.toString();
            int indexOfMinus = gitTag.indexOf("-");
            versionInfo.tag = gitTag.substring(0, indexOfMinus);
            if (ignoreTagPrefix != null && versionInfo.tag.startsWith(ignoreTagPrefix)) {
                versionInfo.tag = versionInfo.tag.substring(ignoreTagPrefix.length());
            }
            versionInfo.commit = gitTag.substring(indexOfMinus + 1);
        } catch (IOException e) {
        }
        return versionInfo;
    }

}
