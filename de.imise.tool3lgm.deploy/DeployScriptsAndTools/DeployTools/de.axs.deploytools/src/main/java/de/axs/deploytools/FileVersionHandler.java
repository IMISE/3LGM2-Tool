package de.axs.deploytools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileVersionHandler {

    private String versionSourceFileName = null;

    private String versionLineStart = null;

    private String versionLineEnd = null;

    private String originalFileName = null;

    private String fileNameEnding = null;

    private String destinationDir = null;

    //    private String newVersion = null;
    //
    //    private FileVersionHandler(final String versionSourceFileName, final String versionLineStart, final String versionLineEnd, final String newVersion) {
    //        super();
    //        this.versionSourceFileName = versionSourceFileName;
    //        this.versionLineStart = versionLineStart;
    //        this.versionLineEnd = versionLineEnd;
    //        this.newVersion = newVersion;
    //    }
    //

    /**
     * @param versionSourceFileName
     * @param versionLineStart
     * @param versionLineEnd
     * @param originalFileName
     * @param fileNameEnding
     * @param destinationDir
     */
    private FileVersionHandler(final String versionSourceFileName, final String versionLineStart, final String versionLineEnd, final String originalFileName, final String fileNameEnding, final String destinationDir) {
        super();
        this.versionSourceFileName = versionSourceFileName;
        this.versionLineStart = versionLineStart;
        this.versionLineEnd = versionLineEnd;
        this.originalFileName = originalFileName;
        this.fileNameEnding = fileNameEnding;
        this.destinationDir = destinationDir;
        renameAndMoveFile();
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        if (args.length == 6) {
            new FileVersionHandler(args[0], args[1], args[2], args[3], args[4], args[5]);
            //        } else {
            //            new FileVersionHandler(args[0], args[1], args[2], args[3]);
        }
    }

    private void renameAndMoveFile() {
        //aktuelleVersion aus der übergebenen VersionSourceDatei holen
        String version = getActualVersion();
        //originalen Dateinamen zerlegen
        int cutPoint = originalFileName.lastIndexOf(fileNameEnding);
        String fileNameStart = originalFileName.substring(0, cutPoint);
        String fileNameEnd = originalFileName.substring(cutPoint);
        //Version einfügen
        String newFileName = fileNameStart + "_V" + version + fileNameEnd;
        //Datei umbenennen
        File newFile = new File(newFileName);
        //Datei ins Zielverzeichnis verschieben
        Path sourcePath = Paths.get(originalFileName);
        Path targetPath = Paths.get(new File(destinationDir, newFile.getName()).getPath());
        try {
            Files.move(sourcePath, targetPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getActualVersion() {
        String versionString = null;
        try {
            File file = new File(versionSourceFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(versionLineStart)) {
                    versionString = line.substring(versionLineStart.length(), line.length() - versionLineEnd.length());
                    break;
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        versionString = versionString.replace(' ', '_');
        return versionString;
    }

}
