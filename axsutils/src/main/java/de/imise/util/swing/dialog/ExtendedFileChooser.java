package de.imise.util.swing.dialog;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import de.imise.util.io.FileHandler;

/**
 * Erweitert die Funktionalität des {@link JFileChooser} dahin, dass im Konstruktor FileSystemView
 * gesetzt und currentDirectory auf userPath gesetzt werden. Wenn der Rückgabewert von showDialog,
 * showOpenDialog bzw. showSaveDialog gleich APPROVE_OPTION ist, wird userPath auf currentDirectory
 * gesetzt. Alle Dialoge, die mit demselben <code>pathKey</code> initialisiert werden, starten immer
 * in dem zuletzt in einem solchen Dialog gewählten Pfad.
 * Außerdem wird beim Aufruf des Speichern-Dialoges und bestätigen des Benutzers mit dem Speichern-Button
 * immer sichergestellt, dass die zu speichernde Datei existiert und überschrieben werden darf.
 * Weiterhin: Wenn der aktuelle {@link FileFilter} während des Speicherns ein {@link FileNameExtensionFilter} ist
 * und der Dateiname keine Extension besitzt, die dieser {@link FileFilter} akzeptiert, dann wird die erste der
 * Extensions, die dieser Filter akzeptiert an die Datei angehängt.
 */
public class ExtendedFileChooser extends JFileChooser {

    /** Wenn kein Verzeichnis angegeben wurde, startet eine Instanz dieses Dialoges in diesem Verzeichnis */
    private static final File DEFAULT_PATH = FileSystemView.getFileSystemView().getDefaultDirectory();

    /**
     * Mappt von einem Key-Object auf einen Pfad. Je nachdem mit welchem Key-Object eine Instanz
     * dieser Klasse gestartet wurde, wird sich der zuletzt gewählte Pfad in dieser Map gemerkt.
     * Default ist das Key-Object <code>null</code>
     */
    private static final Map<Object, File> KEY_TO_PATH_MAP = new HashMap<>();

    /** Default Key für den letzten Pfad dieses Dialoges, wenn kein anderes Key-Object gesetzt wurde. */
    private Object pathKey = null;

    /** ResourceHandler für alle Instanzen */
    private static final DialogResourceHandler drh = new DialogResourceHandler(ExtendedFileChooser.class);

    private String fileName = null;

    /**
     * @param pathKey
     *            Anhand des PathKeys wird festgestellt in welchem Pfad ein Chooser mit diesem PathKey zuletzt geöffnet war
     *            und auf diese Weise wiederhersgestellt.
     */
    public ExtendedFileChooser(final Object pathKey) {
        this(pathKey, (File) null);
    }

    /**
     * @param pathKey
     *            Anhand des PathKeys wird festgestellt in welchem Pfad ein Chooser mit diesem PathKey zuletzt geöffnet war
     *            und auf diese Weise wiederhersgestellt.
     * @param defaultPath
     *            Wenn noch keine Pfad für den pathKey gefunden wird, wird der übergebene DefaulPath gesetzt. Ist der ungültig
     *            landet man im Hauptverzeichnis des Benutzers
     */
    public ExtendedFileChooser(final Object pathKey, final File defaultPath) {
        this(pathKey, defaultPath, null);
    }

    /**
     * @param pathKey
     *            Anhand des PathKeys wird festgestellt in welchem Pfad ein Chooser mit diesem PathKey zuletzt geöffnet war
     *            und auf diese Weise wiederhersgestellt.
     * @param fileName
     *            Name der Datei, der schon veriengestellt sein soll
     */
    public ExtendedFileChooser(final Object pathKey, final String fileName) {
        this(pathKey, null, fileName);
    }

    /**
     * @param pathKey
     *            Anhand des PathKeys wird festgestellt in welchem Pfad ein Chooser mit diesem PathKey zuletzt geöffnet war
     *            und auf diese Weise wiederhersgestellt.
     * @param defaultPath
     *            Wenn noch keine Pfad für den pathKey gefunden wird, wird der übergebene DefaulPath gesetzt. Ist der ungültig
     *            landet man im Hauptverzeichnis des Benutzers
     * @param fileName
     *            Name der Datei, der schon veriengestellt sein soll
     */
    public ExtendedFileChooser(final Object pathKey, final File defaultPath, final String fileName) {
        super();
        setPathKey(pathKey);
        setFileSystemView(FileSystemView.getFileSystemView());
        File path = KEY_TO_PATH_MAP.get(pathKey);
        if (path == null && defaultPath != null && defaultPath.isDirectory() && defaultPath.exists()) {
            path = defaultPath;
        }
        this.fileName = fileName;
        setCurrentDirectory(path == null ? DEFAULT_PATH : path);
    }

    /**
     * @param pathKey
     */
    public void setPathKey(final Object pathKey) {
        this.pathKey = pathKey;
    }

    /**
     * @return the pathKey
     */
    public Object getPathKey() {
        return pathKey;
    }

    /**
     * @param pathKey
     * @param path
     */
    public void setPath(final Object pathKey, final File path) {
        setPathKey(pathKey);
        KEY_TO_PATH_MAP.put(pathKey, path);
        setCurrentDirectory(path);
    }

    /**
     * @param path
     */
    public void setPath(final File path) {
        setPath(pathKey, path);
    }

    @Override
    public int showDialog(final Component parent, final String approveButtonText) throws HeadlessException {
        if (fileName != null && !fileName.trim().isEmpty()) {
            setSelectedFile(new File(fileName));
        }
        int returnValue = super.showDialog(parent, approveButtonText);
        setPath(pathKey, getCurrentDirectory());
        return returnValue;
    }

    /**
     * Setzt die FileFilter dieses Dialoges. Der erste FileFilter aus dem Array wird als aktiv gesetzt.
     *
     * @param showAllFileFilter
     * @param fileFilters
     */
    public void setFileFilters(final boolean showAllFileFilter, final FileNameExtensionFilter... fileFilters) {
        setAcceptAllFileFilterUsed(showAllFileFilter);
        if (fileFilters != null) {
            for (FileNameExtensionFilter fileFilter : fileFilters) {
                addChoosableFileFilter(fileFilter);
            }
            setFileFilter(fileFilters[0]);
        }
    }

    /**
     * @param parent
     * @param showAllFileFilter
     * @param fileFilters
     * @return
     */
    public int showOpenDialog(final Component parent, final boolean showAllFileFilter, final FileNameExtensionFilter... fileFilters) {
        setFileFilters(showAllFileFilter, fileFilters);
        return super.showOpenDialog(parent);
    }

    /**
     * @param parent
     * @param showAllFileFilter
     * @param fileFilters
     * @return
     */
    public int showSaveDialog(final Component parent, final boolean showAllFileFilter, final FileNameExtensionFilter... fileFilters) {
        return showSaveDialog(parent, null, showAllFileFilter, fileFilters);
    }

    /**
     * @param parent
     * @param title
     * @param showAllFileFilter
     * @param fileFilters
     * @return
     */
    public int showSaveDialog(final Component parent, final String title, final boolean showAllFileFilter, final FileNameExtensionFilter... fileFilters) {
        setDialogType(SAVE_DIALOG);
        if (title != null) {
            setDialogTitle(title);
        }
        setFileFilters(showAllFileFilter, fileFilters);

        boolean correctFileName = false;
        int returnValue = ERROR_OPTION;
        //der Dialog wird solange wiederholt, bis eine beschreibbare Datei ausgewählt wurde oder Abbrechen gedrückt wurde
        while (!correctFileName && returnValue != CANCEL_OPTION) {
            returnValue = showDialog(parent, null);

            //wenn nicht OK gedückt wurde -> raus
            if (returnValue != APPROVE_OPTION) {
                return returnValue;
            }

            //ausgewählte Datei holen
            File selectedFile = getSelectedFile();

            //Prüfen, ob ungültige Zeichen im Namen stehen
            correctFileName = false;
            try {
                correctFileName = selectedFile.getCanonicalPath().endsWith(selectedFile.getName());
            } catch (IOException e) {
            }
            if (!correctFileName) {
                MultipleOptionPane.showConfirmDialog(parent, drh.getResString("MESSAGE_SAVE_ERROR"), drh.getResString("MESSAGE_INVALID_CHARS"), MultipleOptionPane.DEFAULT_OPTION, MultipleOptionPane.ERROR_MESSAGE);
                continue;
            }

            //wenn die angegebene Datei noch nicht existiert prüfe, ob eine Extension angehängt werden sollte.
            FileFilter fileFilter = getFileFilter();
            if (fileFilter instanceof FileNameExtensionFilter) {
                String[] extensions = ((FileNameExtensionFilter) fileFilter).getExtensions();
                // wenn der angegebene Dateiname keine Extension hat, aber eine gültige Extension im FileFilter existiert
                if (extensions.length > 0) {
                    String newSelectedFileName = getSelectedFile().getPath();
                    boolean extensionFound = false;
                    //wenn der Dateiname keine der gültigen Extensions besitzt IMMER die erste Dateierweiterung des FileFilters anhängen
                    for (String extension : extensions) {
                        if (newSelectedFileName.endsWith(extension)) {
                            extensionFound = true;
                            break;
                        }
                    }
                    if (!extensionFound) {
                        setSelectedFile(new File(newSelectedFileName.concat(".").concat(extensions[0])));
                        selectedFile = getSelectedFile();
                    }
                }
            }

            //wenn die angegebene Datei bereits existiert
            if (selectedFile.exists()) {
                //wenn beschreibbar -> Fragen, ob drüberspeichern
                if (selectedFile.canWrite()) {
                    switch (JOptionPane.showConfirmDialog(parent, drh.getResString("MESSAGE_OVERWRITE_1") + selectedFile.getName() + drh.getResString("MESSAGE_OVERWRITE_2"))) {
                    case JOptionPane.YES_OPTION:
                        return APPROVE_OPTION;
                    case JOptionPane.NO_OPTION:
                        correctFileName = false;
                        continue;
                    case JOptionPane.CANCEL_OPTION:
                        return CANCEL_OPTION;
                    }
                    //wenn nicht beschreibbar -> Fehler und Dialog für Dateiauswahl wiederholen
                } else {
                    correctFileName = false;
                    showSaveErrorMessage(parent);
                    continue;
                }
            }

            //wenn sich die neue Datei nicht anlegen lässt oder doch aus irgendwelchen Gründen nicht beschreibbar ist
            if (!FileHandler.guaranteeWriteableFile(selectedFile)) {
                correctFileName = false;
                MultipleOptionPane.showConfirmDialog(parent, drh.getResString("MESSAGE_SAVE_ERROR"), drh.getResString("MESSAGE_CANT_WRITE"), MultipleOptionPane.DEFAULT_OPTION, MultipleOptionPane.ERROR_MESSAGE);
                continue;
            }
        }
        return returnValue;
    }

    @Override
    public int showSaveDialog(final Component parent) throws HeadlessException {
        return showSaveDialog(parent, (String) null, isAcceptAllFileFilterUsed(), (FileNameExtensionFilter[]) null);
    }

    /**
     * Zeigt einen Hinweisdialog an, dass die Datei nicht gespiechert werden konnte.
     *
     * @param parent
     */
    public static final void showSaveErrorMessage(final Component parent) {
        MultipleOptionPane.showConfirmDialog(parent, drh.getResString("MESSAGE_SAVE_ERROR"), drh.getResString("MESSAGE_CANT_WRITE"), MultipleOptionPane.DEFAULT_OPTION, MultipleOptionPane.ERROR_MESSAGE);
    }

    /**
     * Gibt den URL-String des übergebenen File-Objektes zurück oder <code>null</code>, wenn es sich nicht in eine gültige URL umwandelnd ließ.
     *
     * @param file
     * @return
     */
    public static String getUrl(final File file) {
        String url = null;
        if (file != null) {
            try {
                URI uri = file.toURI();
                URL url2 = uri.toURL();
                url = url2.toString();
            } catch (MalformedURLException e) {
            }
        }
        return url;
    }

    /**
     * @return
     */
    public static final File chooseFile() {
        return chooseFile(null);
    }

    /**
     * @return
     */
    public static final String chooseFileUrl() {
        return getUrl(chooseFile(null));
    }

    /**
     * @param pathKey
     * @return
     */
    public static final File chooseFile(final Object pathKey) {
        return chooseFile(null, pathKey);
    }

    /**
     * @param parent
     * @param pathKey
     * @return
     */
    public static final File chooseFile(final Component parent, final Object pathKey) {
        return chooseFile(parent, null, pathKey);
    }

    /**
     * @param parent
     * @param filter
     * @param pathKey
     * @return
     */
    public static final File chooseFile(final Component parent, final FileNameExtensionFilter filter, final Object pathKey) {
        return chooseFile(parent, filter, pathKey, false);
    }

    /**
     * @param parent
     * @param filter
     * @param pathKey
     * @param multiSelectionEnabled
     * @return
     */
    public static final File chooseFile(final Component parent, final FileNameExtensionFilter filter, final Object pathKey, final boolean multiSelectionEnabled) {
        ExtendedFileChooser chooser = new ExtendedFileChooser(pathKey);
        chooser.setMultiSelectionEnabled(multiSelectionEnabled);
        if (filter != null) {
            chooser.setFileFilters(true, filter);
        }
        File fileToOpen = null;
        if (chooser.showOpenDialog(parent) == ExtendedFileChooser.APPROVE_OPTION) {
            fileToOpen = chooser.getSelectedFile();
        }
        return fileToOpen;
    }

}