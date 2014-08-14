package de.imise.tool3lgm.xslt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.userproperties.UserProperties;

public class XSLTResourceHandler {

    /** URL with standard-scripts for xslt-export */
    private final ArrayList<XSLTScript> standardXSLT;

    private static final String DEV_RESOURCE_BASE_DIR_NAME = Tool3lgmConstants.APPLICATION_DIR + Tool3lgmConstants.DEV_RESOURCE_DIR_NAME;
    private static final String DEV_RESOURCE_BASE_XSL_DIR_NAME = DEV_RESOURCE_BASE_DIR_NAME + Tool3lgmConstants.RESOUCE_BASE_XSL_SCRIPT_DIR_NAME;
    private static final String JAR_RESOURCE_BASE_XSL_DIR_NAME = Tool3lgmConstants.JAR_RESOURCE_DIR_NAME + Tool3lgmConstants.RESOUCE_BASE_XSL_SCRIPT_DIR_NAME;

    public XSLTResourceHandler() {
        // Standard-XSLT-Scripte laden
        String[] scriptFileNames = getXSLTScriptFileNames();

        standardXSLT = new ArrayList<XSLTScript>();
        for (String scriptName : scriptFileNames) {
            try {
                // Finger weg hiervon! Das stellt sicher, dass die XSLT-Scripte sowohl zur Entwicklungzeit als
                // auch nach dem Herausgeben im jar-File gefunden werden.
                standardXSLT.add(new XSLTScript(ClassLoader.getSystemClassLoader().getResource(scriptName)));
            } catch (Exception e) {
                // kann man ruhig ausgeben, denn wenn hier was schief geht, hat jemand Mist in die Resourcen eingefügt
                // und solte das sofort ändern
                e.printStackTrace();
            }
        }
    }

    /**
     * Liefert eine Kopie der Liste aller XSLT-Scripte.
     * 
     * @return
     */
    public final ArrayList<XSLTScript> getStandardScripts() {
        return new ArrayList<XSLTScript>(standardXSLT);
    }

    /**
     * Liefert eine Liste der relativen Pfade aller XSLT-Scripte im Resourcenverzeichnis der aktuellen Locale. Werden für die aktuelle Locale keine
     * Scripte gefunden, werden die englischen Scripte
     * geladen.
     * 
     * @return Liste aller Standard-XSLT-Scripte
     */
    private final String[] getXSLTScriptFileNames() {

        // Zur Entwicklungszeit liegen die Scripte in einem Ordner -> Scripte von dort laden, ABER
        // bei Herausgabe des Tools liegen die Scripte in der jar-Datei im Resourcenpfad -> catch-Fall
        try {
            String language = UserProperties.getLocale().getLanguage();
            String xslScriptBaseDirName = DEV_RESOURCE_BASE_XSL_DIR_NAME;
            String scriptPath = xslScriptBaseDirName + language;
            File dir = new File(scriptPath);
            File[] scripts = dir.listFiles();
            // wenn für die Locale keine Scripte gefunden wurde -> lade die Englischen
            if (scripts.length == 0) {
                scriptPath = xslScriptBaseDirName + "en";
                dir = new File(scriptPath);
            }
            ArrayList<String> scriptNameList = new ArrayList<String>(scripts.length);
            for (int i = 0; i < scripts.length; i++) {
                String s = scripts[i].getCanonicalPath();
                if (!s.endsWith(Tool3lgmConstants.XSL_SCRIPT_FILE_EXTENSION)) {
                    continue;
                }
                scriptNameList.add(s.substring(DEV_RESOURCE_BASE_DIR_NAME.length()));
            }
            String[] scriptNames = new String[scriptNameList.size()];
            System.arraycopy(scriptNameList.toArray(), 0, scriptNames, 0, scriptNames.length);
            return scriptNames;
            // wenn der Ordner mit den xsl-Dateien nicht gefunden wurde, weil er sich sicherlich in der
            // herausgegebenen Jar-Datei versteckt -> lies die Scripte aus der Jar-Datei
        } catch (Exception e) {

            Enumeration<JarEntry> entries = null;

            try {
                entries = new JarFile(Tool3lgmConstants.ABSOLUTE_TOOL_JAR_PATH).entries();
            } catch (IOException e1) {
                // e1.printStackTrace();
            }

            String packagePattern = JAR_RESOURCE_BASE_XSL_DIR_NAME + UserProperties.getLocale().getLanguage() + "/[^/]+\\.xsl";
            ArrayList<JarEntry> xslEntries = new ArrayList<JarEntry>();

            if (entries == null) {
                return new String[0];
            }

            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().matches(packagePattern)) {
                    xslEntries.add(jarEntry);
                }
            }
            // wenn für die aktuelle Locale-Sprache keine Scripte gefunden wurden -> lade die Englischen
            if (xslEntries.size() == 0) {
                packagePattern = JAR_RESOURCE_BASE_XSL_DIR_NAME + "en/[^/]+\\.xsl";
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    if (jarEntry.getName().matches(packagePattern)) {
                        xslEntries.add(jarEntry);
                    }
                }
            }

            String[] scriptNames = new String[xslEntries.size()];

            for (int i = 0; i < scriptNames.length; i++) {
                scriptNames[i] = xslEntries.get(i).toString();
            }

            return scriptNames;
        }

    }

}
