package de.imise.util;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Strings;

/**
 * @author AXS (9 Apr 2019)
 */
public final class ApplicationManager {

    /**
     * Gibt das Oberste Verzeichnis zurück, in dem sich Anwendungsdaten befinden, also das Installationsverzeichnis.<br>
     *
     * @param ignoreSubDir falls die Anwendung aus einem Unterverzeichnis heraus gestartet wird, dann wird dieses Unterverzeichnis hier abgeschnitten
     *            und nur das File auf das Oberverzeichnis zurück gegeben. Das passiert, wenn man die Applikation direkt über die jar-Datei z.B. aus
     *            dem lib-Verzeichnis startet. Dann muss man eins hoch.
     * @return Pfad zur Anwendung
     */
    public static File getApplicationDir(final String ignoreSubDir) {
        File f = null;
        try {
            f = new File(".").getCanonicalFile();
            if (!Strings.isNullOrEmpty(ignoreSubDir)) {
                String path = f.getAbsolutePath();
                if (path.endsWith(ignoreSubDir)) {
                    path = path.substring(0, path.length() - ignoreSubDir.length());
                    f = new File(path);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    /**
     * Gibt das Oberste Verzeichnis zurück, in dem sich Anwendungsdaten befinden, also das Installationsverzeichnis.<br>
     *
     * @return Pfad zur Anwendung
     */
    public static File getApplicationDir() {
        return getApplicationDir("");
    }

}
