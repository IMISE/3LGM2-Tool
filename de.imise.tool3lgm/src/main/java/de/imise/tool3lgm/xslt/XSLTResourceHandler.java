package de.imise.tool3lgm.xslt;

import static de.imise.tool3lgm.Tool3lgmConstants.RESOUCE_BASE_XSL_SCRIPT_DIR_NAME;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.LocaleDependentSubDirResourceHandler;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;

/**
 * @author AXS (14.08.2014)
 */
public class XSLTResourceHandler extends LocaleDependentSubDirResourceHandler {

    /** URL with standard-scripts for xslt-export */
    private final List<XSLTScript> standardXSLT;

    /**
     *
     */
    public XSLTResourceHandler() {
        this(getSelectedMetaModelDefinitionClass());
    }

    /**
     * @param classWithClassLoaderForXSLTResources
     */
    public XSLTResourceHandler(final Class<?> classWithClassLoaderForXSLTResources) {
        // Standard-XSLT-Scripte laden
        String[] scriptFileNames = getFileNames("xsl", RESOUCE_BASE_XSL_SCRIPT_DIR_NAME, Tool3lgm.class);

        standardXSLT = new ArrayList<>();
        for (String scriptName : scriptFileNames) {
            try {
                // Finger weg hiervon! Das stellt sicher, dass die XSLT-Scripte sowohl zur Entwicklungzeit als
                // auch nach dem Herausgeben im jar-File gefunden werden.
                ClassLoader classLoader = classWithClassLoaderForXSLTResources.getClassLoader();
                URL resource = classLoader.getResource(scriptName);
                XSLTScript xsltScript = new XSLTScript(resource);
                standardXSLT.add(xsltScript);
            } catch (Exception e) {
                // kann man ruhig ausgeben, denn wenn hier was schief geht, hat jemand Mist in die Resourcen eingefügt
                // und solte das sofort ändern
                e.printStackTrace();
            }
        }
    }

    /**
     * @return
     */
    private static Class<? extends MetaModelDefinition> getSelectedMetaModelDefinitionClass() {
        MetaModel selectedMetaModel = Static.getSelectedMetaModel();
        Class<? extends MetaModelDefinition> selectedMetaModelDefinitionClass = selectedMetaModel.getMetaModelDefinitionClass();
        return selectedMetaModelDefinitionClass;
    }

    /**
     * Liefert eine Kopie der Liste aller XSLT-Scripte.
     *
     * @return
     */
    public final List<XSLTScript> getStandardScripts() {
        return new ArrayList<>(standardXSLT);
    }

}
