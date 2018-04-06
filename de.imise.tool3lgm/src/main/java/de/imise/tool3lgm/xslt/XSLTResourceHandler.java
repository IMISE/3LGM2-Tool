package de.imise.tool3lgm.xslt;

import static de.imise.tool3lgm.Tool3lgmConstants.JAR_RESOURCE_DIR_NAME;
import static de.imise.tool3lgm.Tool3lgmConstants.RESOUCE_BASE_XSL_SCRIPT_DIR_NAME;

import java.util.ArrayList;

import de.imise.tool3lgm.LocaleDependingSubDirResourceHandler;

public class XSLTResourceHandler extends LocaleDependingSubDirResourceHandler {

    /** URL with standard-scripts for xslt-export */
    private final ArrayList<XSLTScript> standardXSLT;

    private static final String DEV_RESOURCE_BASE_XSL_DIR_NAME = DEV_RESOURCE_BASE_DIR_NAME + RESOUCE_BASE_XSL_SCRIPT_DIR_NAME;
    private static final String JAR_RESOURCE_BASE_XSL_DIR_NAME = JAR_RESOURCE_DIR_NAME + RESOUCE_BASE_XSL_SCRIPT_DIR_NAME;

    public XSLTResourceHandler() {
        // Standard-XSLT-Scripte laden
        String[] scriptFileNames = getFileNames("xsl", DEV_RESOURCE_BASE_XSL_DIR_NAME, JAR_RESOURCE_BASE_XSL_DIR_NAME);

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

}
