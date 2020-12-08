package de.imise.util;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;

/**
 * Stellt Funktionen zum Öffnen von Dateien, Verzeichnissen oder Webseiten
 * bereit.
 *
 * @author AXS (9 Apr 2010)
 */
public class BrowseUtils {

    /**
     * Öffnet die Datei oder das Verzeichnis mit dem zugehörigen
     * Standard-Systemprogramm.
     *
     * @param file Pfad zur Datei oder dem Verzeichnis
     */
    public static final boolean browse(final File file) {
        URI uri = file.toURI();
        return browse(uri);
    }

    /**
     * Öffnet die Datei, deren absoluter Pfad sich aus dem Pfad der Application
     * und dem übergebenen relativen Pfad ergibt.
     *
     * @param relativePath
     */
    public static final boolean browseApplicationPathRelativeFile(final String relativePath) {
        File file = new File(ApplicationManager.getApplicationDir(), relativePath);
        return browse(file);
    }

    /**
     * Converts the passed object into a string (via String.valueOf(Object)). If
     * this string describes a file, the file will be loaded. Relative paths are
     * converted to absolute paths. If the file cannot be opened, the string is
     * interpreted as URI and opened. If this does not work either, the string
     * is preceded by "http://" and then again as an attempt to open it as a web
     * page. If this does not work either, <code>false</code> returns, otherwise
     * always <code>true</code>.
     *
     * @param urlOrPathObject the object intereted as file string or uri
     */
    public static final boolean browse(final Object urlOrPathObject) {
        String urlOrPath = null;
        if (urlOrPathObject instanceof File) {
            File file = (File) urlOrPathObject;
            if (browse((File) urlOrPathObject)) {
                return true;
            }
            urlOrPath = file.getPath();
        }
        if (urlOrPath == null) {
            urlOrPath = String.valueOf(urlOrPathObject);
        }
        try {
            File file = new File(urlOrPath);
            String fullPath = file.getAbsolutePath();
            file = new File(fullPath);
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            try {
                String fullUri = urlOrPath.contains("://") ? urlOrPath : "https://" + urlOrPath; // if no protocel -> https
                URI uri = new URI(fullUri);
                browse(uri);
            } catch (Exception ex) {
                try {
                    URI uri = new URI("http://" + urlOrPath);
                    browse(uri);
                } catch (Exception exx) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Öffnet die übergebene URI, je nachdem was es ist als Datei, als
     * Verzeichnis oder als Webseite.
     *
     * @param uri
     */
    public static final boolean browse(final URI uri) {
        try {
            Desktop.getDesktop().browse(uri);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}