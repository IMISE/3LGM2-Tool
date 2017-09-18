package de.imise.tool3lgm.tools;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.log.Log;

public class BrowseUtils {

    public static final void browseAbsoluteFile(final File file) {
        URI uri = file.toURI();
        browse(uri);
    }

    public static final void browseRelativeFile(final String relativePath) {
        File file = new File(Tool3lgmConstants.APPLICATION_DIR, relativePath);
        browseAbsoluteFile(file);
    }

    public static final void browseRelativeFileFromResource(final String resourceKey) {
        String relativePath = getResString(resourceKey);
        browseRelativeFile(relativePath);
    }

    public static final void browseUrlFromResource(final String resourceKey) {
        String resUrl = getResString(resourceKey);
        browse(resUrl);
    }

    public static final void browse(final String urlOrPath) {
        try {
            URI uri = new URI(urlOrPath);
            Desktop.getDesktop().browse(uri);
        } catch (Exception e) {
            try {
                File file = new File(urlOrPath);
                Desktop.getDesktop().open(file);
            } catch (Exception ex) {
                Log.show(Log.ERROR, getResString("FehlerAllgemein"), ex);
            }
        }
    }

    public static final void browse(final URI uri) {
        try {
            Desktop.getDesktop().browse(uri);
        } catch (Exception e) {
            Log.show(Log.ERROR, getResString("FehlerAllgemein"), e);
        }
    }

}