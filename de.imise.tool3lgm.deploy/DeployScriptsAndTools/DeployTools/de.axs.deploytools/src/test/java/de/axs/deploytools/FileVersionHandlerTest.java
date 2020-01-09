package de.axs.deploytools;

import org.testng.annotations.Test;

public class FileVersionHandlerTest {

    @Test
    public void test() {
        String ISS_FILE = "D:\\Work\\3LGM_Deploy\\Innosetup_Tool3lgm.iss";
        String ISS_VERSION_LINE_START = "#define MyAppVersion \"";
        String ISS_VERSION_LINE_END = "\"";
        String originalFileName = "C:\\Program Files\\cygwin64\\home\\Ich\\3LGM2Tool.tar.gz";
        String destinationDir = "D:\\Eigene Projekte\\Dropbox\\3LGM2Download";

        //new FileVersionHandler(ISS_FILE, ISS_VERSION_LINE_START, ISS_VERSION_LINE_END, originalFileName, destinationDir);

    }
}
