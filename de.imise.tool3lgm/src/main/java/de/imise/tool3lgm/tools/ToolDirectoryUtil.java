package de.imise.tool3lgm.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.imise.util.io.FileHandler;

/**
 * Klasse für Zugriffe und Operationen auf den Baukasten-Dateien.
 * 
 * @author fstephan
 *         AXS:06.08.2012: Diese Klasse wurde nirgend und hoffentlich auch im Reporter nicht benutzt. Daher aheb ich
 *         Sie im neuen Projekt gelöscht und hier als deprecated gelassen
 */
@Deprecated
public class ToolDirectoryUtil {

    /**
     * Gibt alle Dateien im Bauskasten-Verzeichniss mit dem spezifizierten Suffix wieder. (Trenn-Punkt muss mit angegeben werden)
     * 
     * @param fileExtension
     *            Dateisuffix (z.B. <code>.java</code>)
     * @return
     */
    public static File[] getAllFiles(final String fileExtension) {
        File mainDirectory = new File("tool3lgm");
        List<File> fileList = traverse(mainDirectory, fileExtension, new ArrayList<File>());
        return fileList.toArray(new File[fileList.size()]);
    }

    /**
     * Traversiert das gesamte Verzeichnis <code>parent</code> und gibt eine Liste aller
     * enthaltenen Dateien wieder.
     * 
     * @param parent
     *            zu durchsuchendes Verzeichnis
     * @param fileExtension
     *            Dateisuffix (z.B. <code>.java</code>)
     * @param allFiles
     *            zu füllende Liste
     * @see FileHandler#traverse(File, String, List)
     * @return
     */
    private static List<File> traverse(final File parent, final String fileExtension, final List<File> allFiles) {
        return FileHandler.traverse(parent, fileExtension, allFiles);
    }
}
