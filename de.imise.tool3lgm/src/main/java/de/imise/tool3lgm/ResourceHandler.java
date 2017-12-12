package de.imise.tool3lgm;

import static de.imise.tool3lgm.Tool3lgmConstants.ABSOLUTE_TOOL_JAR_PATH;
import static de.imise.tool3lgm.Tool3lgmConstants.APPLICATION_DIR;
import static de.imise.tool3lgm.Tool3lgmConstants.DEV_RESOURCE_DIR_NAME;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import de.imise.tool3lgm.userproperties.UserProperties;

public class ResourceHandler {

    public static final String DEV_RESOURCE_BASE_DIR_NAME = APPLICATION_DIR + DEV_RESOURCE_DIR_NAME;

    public ResourceHandler() {
    }

    /**
     * Liefert eine Liste der relativen Pfade aller Dateien mit der übergebenen Endung im Resourcenverzeichnis der aktuellen Locale. Werden für die
     * aktuelle Locale keine Dateien gefunden, werden die Pfade zu den englischen Dateien geladen.
     *
     * @return Liste aller Dateien mit der angegebenen Endung im angegebenen Resourcenverzeichnis
     */
    protected final String[] getFileNames(final String fileExtension, final String devTimeResourceBaseDirName, final String jarResourceBaseDirName) {
        // Zur Entwicklungszeit liegen die Dateien in einem Ordner -> Dateien von dort laden, ABER
        // bei Herausgabe des Tools liegen die Dateien in der jar-Datei im Resourcenpfad -> catch-Fall
        try {
            String language = UserProperties.getLocale().getLanguage();
            //            String baseDirName = DEV_RESOURCE_BASE_USERPROPERTIES_DIR_NAME;
            String path = devTimeResourceBaseDirName + language;
            File dir = new File(path);
            File[] files = dir.listFiles();
            // wenn für die Locale keine Scripte gefunden wurde -> lade die Englischen
            if (files.length == 0) {
                path = devTimeResourceBaseDirName + "en";
                dir = new File(path);
            }
            ArrayList<String> fileNameList = new ArrayList<>(files.length);
            for (int i = 0; i < files.length; i++) {
                String s = files[i].getCanonicalPath();
                if (!s.endsWith("." + fileExtension)) {
                    continue;
                }
                fileNameList.add(s.substring(DEV_RESOURCE_BASE_DIR_NAME.length()));
            }
            String[] fileNames = new String[fileNameList.size()];
            System.arraycopy(fileNameList.toArray(), 0, fileNames, 0, fileNames.length);
            return fileNames;
            // wenn der Ordner mit den Dateien nicht gefunden wurde, weil er sich sicherlich in der
            // herausgegebenen Jar-Datei versteckt -> lies die Dateien aus der Jar-Datei
        } catch (Exception e) {
            Enumeration<JarEntry> entries = null;
            JarFile jarFile = null;
            try {
                jarFile = new JarFile(ABSOLUTE_TOOL_JAR_PATH);
                entries = jarFile.entries();
            } catch (IOException e1) {
                // e1.printStackTrace();
            }
            String packagePattern = jarResourceBaseDirName + UserProperties.getLocale().getLanguage() + "/[^/]+\\." + fileExtension;
            List<JarEntry> jarEntries = new ArrayList<>();

            String[] fileNames = new String[0];

            if (entries != null) {
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    if (jarEntry.getName().matches(packagePattern)) {
                        jarEntries.add(jarEntry);
                    }
                }
                // wenn für die aktuelle Locale-Sprache keine Dateien gefunden wurden -> lade die Englischen
                if (jarEntries.size() == 0) {
                    packagePattern = jarResourceBaseDirName + "en/[^/]+\\." + fileExtension;
                    while (entries.hasMoreElements()) {
                        JarEntry jarEntry = entries.nextElement();
                        if (jarEntry.getName().matches(packagePattern)) {
                            jarEntries.add(jarEntry);
                        }
                    }
                }
                fileNames = new String[jarEntries.size()];

                for (int i = 0; i < fileNames.length; i++) {
                    fileNames[i] = jarEntries.get(i).toString();
                }
            }
            try {
                jarFile.close();
            } catch (Exception ex) {
                //mache nichts, egal ob NullPointer oder IOException
            }
            return fileNames;
        }
    }

}
