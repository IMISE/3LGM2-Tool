package de.imise.tool3lgm.graphtools.userfield;

import static de.imise.tool3lgm.Tool3lgmConstants.JAR_RESOURCE_DIR_NAME;
import static de.imise.tool3lgm.Tool3lgmConstants.RESOUCE_BASE_DEFAULT_USERPROPERTIES_DIR_NAME;

import java.io.File;

import de.imise.tool3lgm.ResourceHandler;
import de.imise.tool3lgm.graphtools.model.GDCollection;

public class UserfieldResourceHandler extends ResourceHandler {

    private static final String DEV_RESOURCE_BASE_USERPROPERTIES_DIR_NAME = DEV_RESOURCE_BASE_DIR_NAME + RESOUCE_BASE_DEFAULT_USERPROPERTIES_DIR_NAME;

    private static final String JAR_RESOURCE_BASE_USERPROPERTIES_DIR_NAME = JAR_RESOURCE_DIR_NAME + RESOUCE_BASE_DEFAULT_USERPROPERTIES_DIR_NAME;

    private UserfieldResourceHandler(final GDCollection gdcoll) {
        String[] defaultUserpropertiesFileNames = getFileNames("ufd", DEV_RESOURCE_BASE_USERPROPERTIES_DIR_NAME, JAR_RESOURCE_BASE_USERPROPERTIES_DIR_NAME);
        UserFieldDefinitions definitions = gdcoll.getUserFieldDefinitions();
        for (String ufdFileName : defaultUserpropertiesFileNames) {
            try {
                //man muss über den ClassLoader gehen, um die vollständige URI zu erhalten
                File ufdFile = new File(ClassLoader.getSystemClassLoader().getResource(ufdFileName).toURI());
                UserFieldXMLParser.importDefinitions(ufdFile, definitions);
            } catch (Exception e) {
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
