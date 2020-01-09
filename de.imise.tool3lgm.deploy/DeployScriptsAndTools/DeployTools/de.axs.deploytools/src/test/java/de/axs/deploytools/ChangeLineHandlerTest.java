package de.axs.deploytools;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.testng.Assert;

public class ChangeLineHandlerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void extractVersionStringTest() {
        final String[] input = {
                "",
                "\"",
                "\"\"",
                "\"aaa\"",
                "3.3.9",
                "\"3.3.9\"",
                "\"3.3.9\";"
        };
        final String[] output = {
                "",
                "\"",
                "",
                "aaa",
                "3.3.9",
                "3.3.9",
                "3.3.9"
        };

        for (int i = 0; i < input.length; i++) {
            String newS = ChangeLineHandler.extractVersionString(input[i]);
            //System.out.println(input[i] + " -> " + newS + " -> " + output[i]);
            Assert.assertEquals(output[i], newS);
        }
    }

    @Test
    public void getIncreasedVersionStringTest() {
        final String[] input = {
                ".1",
                "0.1",
                "0.12",
                "aaa1.1bbb",
                "Version 1.2.999 (Beta)",
                "1234",
                "1234.",
                "AA Version of 3LGM2"
        };
        final String[] output = {
                ".2",
                "0.2",
                "0.13",
                "aaa1.2bbb",
                "Version 1.2.1000 (Beta)",
                "1235",
                "1235.",
                "AA Version of 4LGM2"
        };
        for (int i = 0; i < input.length; i++) {
            String newS = ChangeLineHandler.getIncreasedVersionString(input[i]);
            //System.out.println(input[i] + " -> " + newS + " -> " + output[i]);
            Assert.assertEquals(output[i], newS);
        }
    }

    @Test
    public void getIncreasedVersionStringExceptionTest() {
        String[] illegalVersions = {
                "",
                ".",
        };
        for (String illegalVersion : illegalVersions) {
            try {
                ChangeLineHandler.getIncreasedVersionString(illegalVersion);
                fail(illegalVersion + " is not an invalid version");
            } catch (Exception e) {
                assertThat(e).isInstanceOf(IllegalArgumentException.class).hasMessage(illegalVersion);
            }
        }
    }

    @Test
    public void FullFileWriteTest() {
        //        String fileName = "D:\\Eigene Projekte\\local.git\\3LGM_Tool\\de.imise.tool3lgm\\src\\main\\java\\de\\imise\\tool3lgm\\Tool3lgmConstants.java";
        //        String versionLineStart = "    public static final String TOOL_VERSION = \"";
        //        String versionLineEnd = "\";";
        //        String version = "3.3.9";
        //        new SetNewVersionHandler(fileName, versionLineStart, versionLineEnd, version);
    }
}
