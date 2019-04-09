package de.imise.util;

import java.io.File;
import java.io.IOException;

/**
 * @author AXS (9 Apr 2019)
 */
public final class ApplicationManager {

    /**
     * Gibt das Oberste Verzeichnis zurück, in dem sich Anwendungsdaten befinden, also das Installationsverzeichnis.<br>
     *
     * @return Pfad zur Anwendung
     */
    public static File getApplicationDir() {
        File f = null;
        try {
            f = new File(".").getCanonicalFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

}
