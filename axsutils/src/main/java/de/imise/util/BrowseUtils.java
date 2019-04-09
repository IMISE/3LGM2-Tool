package de.imise.util;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;

/**
 * Stellt Funktionen zum Öffnen von Dateien, Verzeichnissen oder Webseiten bereit.
 *
 * @author AXS (9 Apr 2010)
 */
public class BrowseUtils {

    /**
     * Öffnet die Datei oder das Verzeichnis mit dem zugehörigen Standard-Systemprogramm.
     *
     * @param file Pfad zur Datei oder dem Verzeichnis
     */
    public static final void browseAbsoluteFile(final File file) {
        URI uri = file.toURI();
        browse(uri);
    }

    /**
     * Öffnet die Datei, deren absoluter Pfad sich aus dem Pfad der Application und dem übergebenen relativen Pfad ergibt.
     *
     * @param relativePath
     */
    public static final void browseApplicationPathRelativeFile(final String relativePath) {
        File file = new File(ApplicationManager.getApplicationDir(), relativePath);
        browseAbsoluteFile(file);
    }

    /**
     * Versucht den übergebenen String erst als URI-Link zu öffnen. Ist das keine valide URI wird versucht den String als Datei- oder Verzeichnispfad
     * zu öffen. Klappt das auch nicht, wird vor den String ein "http://" gestellt und dann nochmal als versucht, ihn als Webseite zu öffnen. geht das
     * auch nicht, passiert gar nichts (keine Fehlermeldung oder Exception!).
     *
     * @param urlOrPath
     */
    public static final void browse(final String urlOrPath) {
        try {
            URI uri = new URI(urlOrPath);
            browse(uri);
        } catch (Exception e) {
            try {
                File file = new File(urlOrPath);
                Desktop.getDesktop().open(file);
            } catch (Exception ex) {
                try {
                    URI uri = new URI("http://" + urlOrPath);
                    browse(uri);
                } catch (Exception exx) {
                    //Fehlermdeldung anzeigen?
                }
            }
        }
    }

    /**
     * Öffnet die übergebene URI, je nachdem was es ist als Datei, als Verzeichnis oder als Webseite.
     *
     * @param uri
     */
    public static final void browse(final URI uri) {
        try {
            Desktop.getDesktop().browse(uri);
        } catch (Exception e) {
            //Fehlermdeldung anzeigen?
        }
    }

}