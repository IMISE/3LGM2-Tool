package de.imise.tool3lgm.graphtools.userfield;

import static de.imise.tool3lgm.Tool3lgmConstants.JAR_RESOURCE_DIR_NAME;
import static de.imise.tool3lgm.Tool3lgmConstants.RESOUCE_BASE_DEFAULT_USERPROPERTIES_DIR_NAME;

import java.net.URL;

import de.imise.tool3lgm.LocaleDependingSubDirResourceHandler;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.log.Log;

public class UserfieldResourceHandler extends LocaleDependingSubDirResourceHandler {

    private static final String DEV_RESOURCE_BASE_USERPROPERTIES_DIR_NAME = DEV_RESOURCE_BASE_DIR_NAME + RESOUCE_BASE_DEFAULT_USERPROPERTIES_DIR_NAME;

    private static final String JAR_RESOURCE_BASE_USERPROPERTIES_DIR_NAME = JAR_RESOURCE_DIR_NAME + RESOUCE_BASE_DEFAULT_USERPROPERTIES_DIR_NAME;

    private UserfieldResourceHandler(final GDCollection gdcoll) {
        String[] defaultUserpropertiesFileNames = getFileNames("ufd", DEV_RESOURCE_BASE_USERPROPERTIES_DIR_NAME, JAR_RESOURCE_BASE_USERPROPERTIES_DIR_NAME);
        UserFieldDefinitions definitions = gdcoll.getUserFieldDefinitions();
        for (String ufdFileName : defaultUserpropertiesFileNames) {
            try {
                //man muss über den ClassLoader gehen, um die vollständige URI zu erhalten
                ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
                URL resourceUrl = systemClassLoader.getResource(ufdFileName);
                UserFieldXMLParser.importDefinitions(resourceUrl, definitions);
            } catch (Exception e) {
                Log.show(Log.ERROR, "Exception #######################################################", e);
                // kann man ruhig ausgeben, denn wenn hier was schief geht, hat jemand Mist in die Resourcen eingefügt
                // und solte das sofort ändern
                e.printStackTrace();
            }
        }
    }

    public static final void loadDefaultUserfieldDefinition(final GDCollection gdcoll) {
        new UserfieldResourceHandler(gdcoll);
    }

}
