package de.imise.util.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.List;

/**
 * Diese Klasse stellt allgemeine Funktionen für den Umgang mit Dateien zur Verfügung.
 * 
 * @author AXS
 * @created 17.04.06
 */
public class FileHandler {

    /**
     * Liefert <code>true</code> zurück, wenn das übergebene {@link File} beschreibbar ist.
     * Sollte die Datei noch nicht existieren und <code>testonly</code> ist <code>false</code>,
     * dann wird die Datei angelegt.
     * Achtung: Es werden immer Dateien und keine Verzeichnisse angelegt.
     * 
     * @param file
     *            Datei deren Beschreibbarkeit getestet werden soll
     * @param testonly
     *            wenn <code>false</code> wird die Datei angelegt, wenn sie noch nicht existiert
     * @return
     *         <code>true</code> wenn das übergebene {@link File} geschrieben werden kann
     */
    public static boolean guaranteeWriteableFile(final File file, final boolean testonly) {
        if (file == null) {
            return false;
        }
        if (file.canWrite()) {
            return true;
        }
        //file ist nicht beschreibbar, aber existiert
        if (file.exists()) {
            return false;
        }
        //file existiert nicht
        File dir = new File(file.getParent());
        try {
            //wenn das Verzeichnis noch nicht vorhanden ist und sich nicht anlegen lässt
            if (!dir.exists() && !dir.mkdirs()) {
                return false;
            }
            file.createNewFile();
            if (testonly) {
                file.delete();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Liefert <code>true</code> zurück, wenn das übergebene {@link File} beschreibbar ist.
     * Sollte die Datei noch nicht existieren, wird sie angelegt. <br>
     * Achtung: Es werden immer Dateien und keine Verzeichnisse angelegt.
     * 
     * @param file
     * @return
     */
    public static boolean guaranteeWriteableFile(final File file) {
        return guaranteeWriteableFile(file, false);
    }

    /**
     * Kopiert alle Daten aus <code>source</code> in <code>dest</code>.
     * 
     * @param source Eingangsdatenstrom
     * @param dest Ausgangsdatenstrom
     * @return
     */
    public static final boolean copy(final InputStream source, final OutputStream dest) {
        boolean ok = true;
        try {
            byte[] buffer = new byte[0xFFFF];
            for (int len; (len = source.read(buffer)) != -1;) {
                dest.write(buffer, 0, len);
            }
        } catch (IOException e) {
            ok = false;
        } finally {
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e) {
                }
            }
            if (dest != null) {
                try {
                    dest.close();
                } catch (IOException e) {
                }
            }
        }
        return ok;
    }

    /**
     * Kopiert eine Datei in eine andere.
     * Existiert die Zieldatei, wird sie überschrieben.
     * 
     * @param source - die Quelldatei-URL
     * @param dest - die Zieldatei
     * @return true, wenn das Kopieren geklappt hat
     */
    public static final boolean copyFile(final URL source, final File dest) {
        dest.getParentFile().mkdirs();
        try {
            InputStream is = source.openStream();
            OutputStream os = new FileOutputStream(dest);
            return copy(is, os);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Kopiert eine Datei in eine andere.
     * Existiert die Zieldatei, wird sie überschrieben.
     * 
     * @param source - die Quelldatei-URL
     * @param dest - die Zieldatei
     * @return true, wenn das Kopieren geklappt hat
     */
    public static final boolean copyFile(final InputStream source, final File dest) {
        dest.getParentFile().mkdirs();
        try {
            OutputStream target = new FileOutputStream(dest);
            return copy(source, target);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Kopiert eine Datei in eine andere.
     * Existiert die Zieldatei, wird sie überschrieben.
     * 
     * @param source - die Quelldatei
     * @param dest - die Zieldatei
     * @return true, wenn das Kopieren geklappt hat
     */
    public static boolean copyFile(final File source, final File dest) {
        dest.getParentFile().mkdirs();
        try {
            return copy(new FileInputStream(source), new FileOutputStream(dest));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Schreibt den String in die Datei
     * 
     * @param file
     * @param content
     * @return
     */
    public static boolean writeFile(final File file, final String content) {
        try {
            int len = 8192;
            StringReader reader = new StringReader(content);
            FileWriter writer = new FileWriter(file);
            char[] chars = new char[len];
            while (true) {
                int charsRead = reader.read(chars);
                if (charsRead > 0) {
                    writer.write(chars, 0, charsRead);
                }
                if (charsRead < len) {
                    break;
                }
            }
            reader.close();
            writer.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @param file
     * @param in
     * @throws IOException
     */
    public static void writeFile(final File file, final InputStream in) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = reader.readLine();
        while (line != null) {
            writer.write(line);
            writer.newLine();
            line = reader.readLine();
        }
        writer.close();
        reader.close();
    }

    /**
     * Liest den Inhalt von <code>file</code> in einen String aus und gibt diesen wieder.
     * 
     * @param file
     * @return
     */
    public static String readFile(final File file) {

        FileReader fr = null;
        File[] files = null;
        String toString = "";

        files = splitFile(file);

        for (File f : files) {
            try {
                fr = new FileReader(f);
                char[] chars = new char[(int) f.length()];
                fr.read(chars);
                fr.close();
                toString = toString.concat(String.copyValueOf(chars));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return toString;
    }

    /**
     * Noch nicht korrekt implementiert!
     * <p>
     * Gibt einfach ein Array wieder, das ausschließlich aus <code>file</code> besteht, falls die zulässige Zeichenanzahl nicht überschritten wird. <br>
     * Sonst wird eine Exception geworfen.
     * <p>
     * Ziel: Zerlegt <code>file</code> in einzelne Dateien, sodass die Anzahl ihrer Zeichen kleiner als {@link Integer#MAX_VALUE} ist. Damit können
     * dann alle Zeichen der einzelnen Dateien in jeweils einem <code>charArray</code> erfasst werden.
     * 
     * @param file
     * @return
     */
    @Deprecated
    public static File[] splitFile(final File file) {
        // TODO: FST: korrekt implementieren
        if (file.length() <= Integer.MAX_VALUE) { // kein splitten notwendig
            return new File[] {
                file
            };
        }
        throw new IllegalArgumentException("Die Datei: " + file.getName() + " überschreitet die zulässige Größe");
    }

    /**
     * Erzeugt eine tmporäre Datei
     * 
     * @param prefix
     *            Dateiname
     * @param suffix
     *            Dateiendung
     */
    public static File createTempFile(final String prefix, final String suffix) {

        File tmpFile = null;

        try {
            tmpFile = File.createTempFile(prefix, suffix);
            tmpFile.deleteOnExit();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return tmpFile;
    }

    /**
     * Traversiert das gesamte Verzeichnis <code>parent</code> und gibt eine Liste aller
     * enthaltenen Dateien wieder.<br>
     * Es werden dabei nur Dateien mit dem spezifizierten Suffix in die Liste übernommen.<br>
     * Diese Dateien werden an die übergebene Liste angefügt, welche am Ende der Traversierung
     * zurückgegeben wird.
     * 
     * @param parent
     *            zu durchsuchendes Verzeichnis
     * @param fileExtension
     *            Dateisuffix (z.B. <code>.java</code>)
     * @param allFiles
     *            zu füllende Liste
     * @return
     */
    public static List<File> traverse(final File parent, final String fileExtension, final List<File> allFiles) {

        File[] children = parent.listFiles();

        if (children == null || children.length == 0) {
            return allFiles;
        }

        for (File child : children) {
            if (child.getName().endsWith(fileExtension)) {
                allFiles.add(child);
            } else if (child.isDirectory()) {
                traverse(child, fileExtension, allFiles);
            }
        }
        return allFiles;
    }

    private static int nextNotExistingFileCounter = 1;

    /**
     * Liefert die nächste Datei im angegebenen Pfad, die noch nicht existiert.
     * Wird der Counter nicht restartet, dann wird vom letzten Index der beim
     * Aufruf dieser Funktion ermittelt wurde weitergesucht.
     * 
     * @param filePath
     * @param extension
     * @param restartCounter
     * @return
     */
    public static File getNextNotExistingFile(final String filePath, String extension, final boolean restartCounter) {
        if (filePath == null || filePath.trim().equals("")) {
            return null;
        }
        File f = null;
        if (extension == null) {
            extension = "";
        }
        if (restartCounter) {
            nextNotExistingFileCounter = 1;
        }
        StringBuilder sb = new StringBuilder();
        while (f == null || f.exists()) {
            sb.setLength(0);
            sb.append(filePath);
            sb.append(++nextNotExistingFileCounter);
            sb.append(extension);
            f = new File(sb.toString());
        }
        return f;
    }

}
