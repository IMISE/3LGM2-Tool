package de.imise.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.imise.util.GitVersionInfoHandler.GitVersionInfo;

public class GitVersionInfoHandlerTest {

    @Test
    public void parseGitVersionTest() {
        GitVersionInfo versionInfo = GitVersionInfoHandler.parseGitVersion("Tool3LGM_Version_4.4.2_(dev)", "feature/4.4.1/\\#516_Analysen_anzeigen_f\\u00FCr_das_ausgew\\u00E4hlte_Metamodell", "e79dbc4", "109", "Tool3LGM_Version_", "(dev)");
        Assert.assertEquals(versionInfo.version, "4.4.2_(dev)");
        Assert.assertEquals(versionInfo.branch, "feature/4.4.1/\\#516_Analysen_anzeigen_f\\u00FCr_das_ausgew\\u00E4hlte_Metamodell");
        Assert.assertEquals(versionInfo.commit, "e79dbc4");
        Assert.assertEquals(versionInfo.commitCount, 109);
        Assert.assertTrue(versionInfo.isDevelopmentBuild());
        Assert.assertFalse(versionInfo.isReleaseBuild());

        versionInfo = GitVersionInfoHandler.parseGitVersion("Tool3LGM_Version_4.4.2", "feature/4.4.1/\\#516_Analysen_anzeigen_f\\u00FCr_das_ausgew\\u00E4hlte_Metamodell", "e79dbc4", "109", "Tool3LGM_Version_", "(dev)");
        Assert.assertEquals(versionInfo.version, "4.4.2");
        Assert.assertEquals(versionInfo.branch, "feature/4.4.1/\\#516_Analysen_anzeigen_f\\u00FCr_das_ausgew\\u00E4hlte_Metamodell");
        Assert.assertEquals(versionInfo.commit, "e79dbc4");
        Assert.assertEquals(versionInfo.commitCount, 109);
        Assert.assertFalse(versionInfo.isDevelopmentBuild());
        Assert.assertTrue(versionInfo.isReleaseBuild());
    }
}
