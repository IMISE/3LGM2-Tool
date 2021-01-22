package de.imise.tool3lgm.graphtools.userfield;

import static de.imise.tool3lgm.Tool3lgmConstants.RESOUCE_BASE_DEFAULT_USERPROPERTIES_DIR_NAME;

import java.net.URL;

import de.imise.tool3lgm.LocaleDependentSubDirResourceHandler;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.log.Log;

/**
 * @author AXS (14.06.2017)
 */
public class UserfieldResourceHandler extends LocaleDependentSubDirResourceHandler {

    private UserfieldResourceHandler(final GDCollection gdcoll) {
        String[] defaultUserpropertiesFileNames = getFileNames("ufd", RESOUCE_BASE_DEFAULT_USERPROPERTIES_DIR_NAME, Tool3lgm.class);

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
