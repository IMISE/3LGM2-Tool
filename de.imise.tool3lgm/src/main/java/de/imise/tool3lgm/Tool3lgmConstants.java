package de.imise.tool3lgm;

import java.awt.Cursor;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.DebugGraphics;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.StringUtils;
import de.imise.util.collections.CollectionUtils;

/**
 * @author Thomas Rudert, AXS Klasse, die alle global benötigten Parameter, und deren Zugriffsmethoden, des Programms enthält
 */
public abstract class Tool3lgmConstants {

    /**
     * Mögliche Typen der FileFilter. Die Bezeichnungen der Bildtypen entsprechen exakt denen, die ImageCodec.createImageEncoder() als Typ-String
     * versteht. Für alle diese Typen gibt es
     * Ressourcen-Strings, deren Key für die Beschreibung sich über den zusammengebausten String aus
     * {@link Tool3lgmConstants#FILE_FILTER_RESOURCE_PREFIX} + {@link FileFilterType#toString()} ergbibt.
     * Für die Liste der akzeptierten Erweiterungen wird der gleiche Key-String gebildet und noch der
     * {@link Tool3lgmConstants#FILE_FILTER_RESOURCE_EXTENSION_POSTFIX} angehängt.
     */
    public static enum FileFilterType {
        LGM3,
        LGM3_ZIP,
        LGM3_UNZIPPED,
        USERFIELD,
        LIC,
        XSL,
        CSV
    }

    /** String with the version-identifier for Tool3lgm */
    // TODO _____###### ständig aktualisieren! UND DIE BEIDEN TODOS IN TOOL3LGM BEACHTEN!!!
    public static final String TOOL_VERSION = "3.3.10";

    /**
     * Wenn <code>true</code>, liefern die {@link GDCommands} in ihrer toString()-Methode den Namen des Kommandos zurück. Wenn <code>false</code>,
     * dann liefert die toString()-Methode die Nummer des
     * Kommandos in der values()-Liste der {@link GDCommands} als String. Man braucht die lesbaren Namen im Grunde nur zum Debuggen. Im Regelbetrieb
     * sollte der Parameter auf <code>false</code> stehen,
     * damit die Kommandos, von denen potenziell sehr viele im Undo-Redo-Stack geloggt werden, nicht so riesig werden.
     */
    public static final boolean LOG_READABLE_UNDO_REDO_COMMANDS = false;

    /** filename with path for internal clipboard */
    private static String clipboardPath = "/";

    /** Pfad zum Installationsverzeichnis der Anwendung */
    public static final File APPLICATION_DIR = getApplicationDir();

    public static final String RELATIVE_TOOL_JAR_PATH = "lib/tool3lgm.jar";

    /** Pfad zur Baukasten-Datei mit der Hauptklasse, wenn er ausgeliefert wird */
    public static final String ABSOLUTE_TOOL_JAR_PATH = APPLICATION_DIR.toURI().getPath() + RELATIVE_TOOL_JAR_PATH;

    /** Pfad ins Home-Verzeichnis des Benutzers */
    public static final String USER_HOME_DIR_NAME = System.getProperty("user.home");

    /** Name des Packages in dem alle Node-Klassen liegen, die allgemein gebraucht werden (Knickpunkte, Textfelder) */
    public static final String ELEMENTS_PACKAGE_NAME = Node.class.getPackage().getName() + ".";

    /** Name des Packages in dem die GDCollection liegt */
    public static final String GD_PACKAGE_NAME = GDCollection.class.getPackage().getName() + ".";

    /** path for temp-files */
    public static final String TEMP_PATH = USER_HOME_DIR_NAME + "/";

    /** Pfad zur Datei mit den Optionen eines Benutzers */
    public static final File USER_INFO_FILE = new File(USER_HOME_DIR_NAME, ".tool3lgm2UserInfo");

    /**
     * Datei-Endung für große Icons.
     * <p>
     * Verwendungsweise: <br>
     * <code>Icon largeIcon = getIcon(</code><em>iconNamePrefix</em><code> + LARGE_ICON_SUFFIX)</code> <br>
     * Bsp.: <br>
     * <code>Icon largeIcon = getIcon("UNDO") + LARGE_ICON_SUFFIX)</code> liefert das {@link Icon} mit dem Namen "UNDO_LARGE.gif" aus dem Icon-Ordner
     * in den Resourcen.
     */
    public static final String LARGE_ICON_SUFFIX = "_LARGE.gif";

    /**
     * Datei-Endung für kleine Icons.
     * <p>
     * Verwendungsweise: <br>
     * <code>Icon smallIcon = getIcon(</code><em>iconNamePrefix</em><code> + SMALL_ICON_SUFFIX)</code> Bsp.: <br>
     * <code>Icon smallIcon = getIcon("UNDO") + SMALL_ICON_SUFFIX)</code> liefert das {@link Icon} mit dem Namen "UNDO_SMALL.gif" aus dem Icon-Ordner
     * in den Resourcen.
     */
    public static final String SMALL_ICON_SUFFIX = "_SMALL.gif";

    public static final String SHORT_DESCRIPTION_SUFFIX = "_shortdescrip";

    public static final String LONG_DESCRIPTION_SUFFIX = "_longdescrip";

    /**
     * Name des Verzeichnisses in dem alle Icons liegen, die das Tool braucht.<br>
     * Diese können über die statische Methode <code>getIcon(String name)</code> geladen werden.
     */
    private static final String RESOURCE_ICON_DIR_NAME = "icon/";

    /** Name des Verzeichnisses in dem die lokalisierten Ressourcen ZUR ENTWICKLUNGSZEIT liegen bedinnend mit dem Hauptpackage */
    public static final String DEV_RESOURCE_DIR_NAME = "/src/main/resources/";
    /** Name des Verzeichnisses in dem die lokalisierten Ressourcen IN DEM JAR-FILE liegen bedinnend mit dem Hauptpackage */
    public static final String JAR_RESOURCE_DIR_NAME = "";

    /**
     * Name des Verzeichnisses, in dem die lokalisierten XSLT-Scripte in den Ordnern mit dem Sprachkürzel der akuellen <code>Locale</code> zu finden
     * sind.
     */
    public static final String RESOUCE_BASE_XSL_SCRIPT_DIR_NAME = "xslt/";

    /**
     * Name des Verzeichnisses, in dem die lokalisierten XSLT-Scripte in den Ordnern mit dem Sprachkürzel der akuellen <code>Locale</code> zu finden
     * sind.
     */
    public static final String RESOUCE_BASE_DEFAULT_USERPROPERTIES_DIR_NAME = "userfield/";

    /** Absoluter Name des Beispielmodells */
    public static final File EXAMPLE_MODEL_FILE = new File(APPLICATION_DIR, "Beispiel.z3lgm");

    /** Name des Verzeichnisses in dem die Ressourcen für den Webexport liegen */
    public static final String WEB_EXPORT_RESOURCE_DIR_NAME = "webexport/";

    // Anfang FileFilter

    /** Pfad zur Default-Datei in den Ressourcen mit den Optionen für einen Benutzer */
    public static final URL DEFAULT_USER_INFO_FILE = ClassLoader.getSystemResource("DefaultUserProperties");

    /** Anzahl der im Menü angezeigten zu letzt benutzen Dateien */
    public static final int LAST_USED_MODEL_FILES_IN_MENU = 10;

    /**
     * Anfang des ResourceString, mit dem bei jedem über die Funktion zu ladenden FileFilter der Key-String der Beschreibung und der
     * Dateierweiterungen beginnen muss.
     */
    public static final String FILE_FILTER_RESOURCE_PREFIX = "FILE_FILTER_";

    /** Ende des Key-Strings für die Dateiertweiterungen eines FileFilters */
    public static final String FILE_FILTER_RESOURCE_EXTENSION_POSTFIX = "_EXT";

    /** Mappt von einem {@link FileFilterType} auf den dazugehörigen {@link FileNameExtensionFilter} */
    private static final HashMap<FileFilterType, FileNameExtensionFilter> FILE_FILTER_TYPE_TO_FILENAME_EXTENSION_FILTER = new HashMap<>();

    // Ende FileFilter

    /**
     * Name des Verzeichnisses in dem alle lokalisierten Icons liegen, die das Tool braucht.<br>
     * Diese können über die statische Methode <code>getLocalizedIcon(String name)</code> geladen werden.
     */
    private static final String RESOURCE_LOCALIZED_ICON_PATH = RESOURCE_ICON_DIR_NAME + UserProperties.getLocale().getLanguage() + "/";

    /** BaseName der lokalisierten Haupt-Ressourcendateien */
    public static final String RESOURCE_BASE_NAME = "Tool3lgmResources";

    /** BaseName der lokalisierten Ressourcendateien eines Metamodells. Gesamtname ist dann z.B. "metamodel.tlgm_v3_0.MetamodelResources" */
    public static final String METAMODEL_RESOURCE_BASE_NAME = "MetamodelResources";

    // die beiden ResourceBundles laden. Davor unbedingt die UserProperties initialisieren, damit die richige Locale gesetzt ist

    static {
        UserProperties.init();
    }

    /**
     * Haupt-<code>ResoruceBundle</code> mit allen Resourcen außer ein paar speziellen Fehlermeldungen.<br>
     * Fehlermeldungen sollten in errorBundle abgelegt werden.
     */
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(RESOURCE_BASE_NAME);

    /**
     * ResourceBundle mit den Metamodell-spezifischen Daten
     */
    private static ResourceBundle metamodelBundle = ResourceBundle.getBundle(getMetamodelBundleName());

    private static String getMetamodelBundleName() {
        //das Metamodel-Resourcebundle liegt im resource-package unter demselben Pfad, wie die Metamodellklasse des Packages.
        //der ClassLoader, der das package lädt, erwartet relative Pfade ab dem Pfa dieser Klasse hier, die das Bundle lädt.
        //z.B. liegt das speziele Metamodel im package "de.imise.tool3lgm.metamodel.tlgm_v3_0". Diese Klasse Tool3lgmConstants
        //liegt im Hauptpackage "de.imise.tool3lgm". Das Resource-Bundle kann mit dem BundleName "metamodel.tlgm_v3_0.MetamodelResources"
        //geladen werden. Also muss man vom package-Namen des Metamodells den package-Namen der Tool3lgmConstants abziehen und den
        //vorgegebenen Bundle-Name "MetamodelResources" anhängen (mit Punkt dazwischen).
        String mainPackageName = Tool3lgmConstants.class.getPackage().getName();
        String metaModelPackageName = Tool3lgmMain.metaModelClass.getPackage().getName();
        String bundleName = metaModelPackageName.substring(mainPackageName.length() + 1) + "." + METAMODEL_RESOURCE_BASE_NAME;
        return bundleName;
    }

    /**
     * Name der Datei mit Analysen. Unter diesem Namen ex. die Standarddatei in den localisierten Resourcen. Wenn der Benutzer irgendeine XMLAnalyse
     * mal aufgerufen hat, dann gibt es mit diesem Namen
     * im APPLICATION_PATH eine Datei (wenn der Benutzer dort Schreibrecht hat) oder in seinem user.home-Pfad (wenn er im APPLICATION_PATH kein
     * Schreibrecht hat)
     */
    public static final String ANALYSEN_FILE_NAME = UserProperties.getLocale().getLanguage().equals("en") ? "Tool3lgm.analysis" : "Tool3lgm_" + UserProperties.getLocale().getLanguage() + ".analysis";

    /** Absoluter Pfad zur Datei mit den Standardanalysen in den Resourcen */
    public static final URL DEFAULT_ANALYSEN_RESOURCE_URL = ClassLoader.getSystemResource(ANALYSEN_FILE_NAME);

    /** Locale, mit der der Baukasten gestartet wurde. */
    public static final Locale START_LOCALE = UserProperties.getLocale();

    /**
     * Debug- Optionen fuer Swing-Komponenten; muss fuer Komponente mit setDebugGraphicsOption(int) gesetzt werden
     */
    private static int debugGraphicsOption = DebugGraphics.NONE_OPTION;

    /**
     * für die Sanduhr...
     */
    protected static Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR), waitCursor = new Cursor(Cursor.WAIT_CURSOR), handCursor = new Cursor(Cursor.HAND_CURSOR);

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

    /**
     * gibt String für die Dateiangabe des programminternen Zwischenspeichers zurück
     *
     * @return String mit Verzeichnis- und Dateiangabe des Zwischenspeichers
     */
    public static String getClipboardPath() {
        return clipboardPath;
    }

    /**
     * gibt die Debug-Option fuer Swing-Komponenten zurueck
     *
     * @return int
     */
    public static int getDebugGraphicsOption() {
        System.out.println(debugGraphicsOption);
        return debugGraphicsOption;
    }

    /**
     * Gibt fuer eine ArrayList von {@link NodeContainer}n oder {@link ModelElement}s einen String des Inhalts zurück. Ist insertNewLines==false, wird
     * eine kommaseparierte Liste zurückgegeben.
     *
     * @param ArrayList
     * @param boolean
     * @param boolean TODO:AXS:in eine eigene Klasse verlegen
     */
    public static String getElementListString(final List<?> list, final boolean showSzenarios, final boolean insertNewLines) {
        StringBuilder serversBuf = new StringBuilder();
        if (list == null) {
            return serversBuf.toString();
        }

        // diese Form mag blöd aussehen, aber so müssen nicht in jedem Schleifendurchlauf die Bedingungen neu geprüft werden
        if (showSzenarios) {
            if (insertNewLines) {
                // Namen der Container durch \n getrennt einfügen
                for (int i = 0; i < list.size(); i++) {
                    serversBuf.append(list.get(i).toString().replace('\n', ' '));
                    serversBuf.append('\n');
                }
            } else {
                // Namen der Container durch ein Leerzeichen und Komma getrennt einfügen
                return list.toString().replace('\n', ' ');
            }
        } else {
            String separator = insertNewLines ? "\n" : ", ";
            // Namen der Elemente durch \n oder ", " getrennt einfügen
            for (Object o : list) {
                if (o instanceof ElementContainer) {
                    o = ((ElementContainer) o).getElement();
                }
                serversBuf.append(o.toString().replace('\n', ' '));
                serversBuf.append(separator);
            }
        }
        // das zuletzt angehängten Zeichen (Komma+Leerzeichen oder Newline) nicht mit zurückgeben
        if (serversBuf.length() > 0) {
            if (insertNewLines) {
                return serversBuf.deleteCharAt(serversBuf.length() - 1).toString();
            }
            return serversBuf.deleteCharAt(serversBuf.length() - 2).toString();
        }
        return serversBuf.toString();
    }

    /**
     * Liefert <code>true</code>, wenn der übergebene String eine Extension eines {@link FileNameExtensionFilter} in
     * FILE_FILTER_TYPE_TO_FILENAME_EXTENSION_FILTER ist.
     *
     * @param extension
     * @return
     */
    public static boolean isExtension(final String extension) {
        for (FileFilterType extensionType : FileFilterType.values()) {
            FileNameExtensionFilter filter = getFileNameExtensionFilter(extensionType);
            String[] extensions = filter.getExtensions();
            if (CollectionUtils.arrayContains(extensions, extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert für den übergebenen {@link FileFilterType} den {@link FileNameExtensionFilter}
     *
     * @param filterName
     * @return
     */
    public static final FileNameExtensionFilter getFileNameExtensionFilter(final FileFilterType fileFilterType) {
        FileNameExtensionFilter filter = FILE_FILTER_TYPE_TO_FILENAME_EXTENSION_FILTER.get(fileFilterType);
        if (filter == null) {
            filter = new FileNameExtensionFilter(getResString(FILE_FILTER_RESOURCE_PREFIX + fileFilterType), StringUtils.tokenize(getResString(FILE_FILTER_RESOURCE_PREFIX + fileFilterType + FILE_FILTER_RESOURCE_EXTENSION_POSTFIX), " ", false));
            FILE_FILTER_TYPE_TO_FILENAME_EXTENSION_FILTER.put(fileFilterType, filter);
        }
        return filter;
    }

    /**
     * Liefert eine Liste von {@link FileNameExtensionFilter}.
     *
     * @param fileFilterType
     *            Array von Objecten, deren jeweilige toString()-Methode den Filternamen angibt, der an dieser Stelle im Rückgabearray stehen soll.
     * @return
     * @see #getFileNameExtensionFilter(FileFilterType)
     */
    public static final FileNameExtensionFilter[] getFileNameExtensionFilters(final FileFilterType... fileFilterType) {
        FileNameExtensionFilter[] returnFilters = new FileNameExtensionFilter[fileFilterType.length];
        for (int i = 0; i < fileFilterType.length; i++) {
            returnFilters[i] = getFileNameExtensionFilter(fileFilterType[i]);
        }
        return returnFilters;
    }

    /**
     * @return Hand cursor
     */
    public static Cursor getHandCursor() {
        return handCursor;
    }

    /**
     * gibt das spezifiziert ImageIcon aus dem Standard-Iconpfad zurück
     *
     * @param name
     * @return ImageIcon
     */
    public static ImageIcon getIcon(final String name) {
        return getImageIcon(RESOURCE_ICON_DIR_NAME + name);
    }

    /**
     * gibt das spezifiziert ImageIcon aus dem lokalisierten Iconpfad zurück
     *
     * @param name
     * @return ImageIcon
     */
    public static ImageIcon getLocalizedIcon(final String name) {
        ImageIcon retVal;
        try {
            retVal = getImageIcon(RESOURCE_LOCALIZED_ICON_PATH + name);
            // wenn für die Locale aus der Userproperties-Datei das gesucht lokalisierte Bild nicht vorkommt
        } catch (Exception e) {
            // Standardressourcen sind englisch
            retVal = getImageIcon(RESOURCE_ICON_DIR_NAME + "/en/");
        }
        return retVal;
    }

    /**
     * Versucht ein {@link ImageIcon} aus dem spezifizierten Verzeichnis zu laden und es wiederzugeben
     *
     * @param dir
     *            Verzeichnis des tatsächlichen Bilds
     * @return
     */
    private static ImageIcon getImageIcon(final String dir) {
        URL url = ClassLoader.getSystemClassLoader().getResource(dir);
        ImageIcon icon;
        if (url != null) {
            icon = new ImageIcon(url);
        } else {
            icon = new ImageIcon(dir);
        }
        if (icon.getIconWidth() == -1 && icon.getIconHeight() == -1) {
            return null;
        }
        return icon;
    }

    /**
     * Liefert alle <code>Locale</code>s, für die Resourcen hinterlegt wurden.<br>
     * Diese werden durch Auslesen der Dateien "Tool3lgmResources_LANGUAGECODE.properties" aus dem resource-Package ermittelt.
     * Es wird davon ausgegangen, dass auf jeden Fall englische Ressourcen existieren, die in der Datei
     * "Tool3lgmResources.properties" hinterlegt sind.<br>
     *
     * @return alle Locales, für die Ressourcen existieren
     */
    public static final Locale[] getInstalledLanguages() {
        StringBuilder sb = new StringBuilder(RESOURCE_BASE_NAME);
        // den Namen vervollständigen; die Zeichen an "XX" werden immer durch einen Ländercode ersetzt
        sb.append("_");
        String appendix = "XX";
        sb.append(appendix);
        // Positionen der Xe bestimmen
        int firstXIndex = sb.length() - appendix.length();
        // alle im System verfügbaren Locale-Sprachcodes holen (die sind immer 2 Zeichen lang)
        String[] allLocales = Locale.getISOLanguages();
        // Array für die gefundenen Ergebnislocales
        Locale[] allFoundLocales = new Locale[allLocales.length];
        // Anzahl der gefundenen Ergebnislocales
        int foundLocales = 0;
        // die erste immer auf Englisch setzen
        allFoundLocales[foundLocales++] = Locale.ENGLISH;

        Locale[] systemLocales = Locale.getAvailableLocales();

        // alle Locales durchprobieren und nach den Ressourcendateien suchen
        for (int i = 0; i < allLocales.length; i++) {
            sb.setCharAt(firstXIndex, allLocales[i].charAt(0));
            sb.setCharAt(firstXIndex + 1, allLocales[i].charAt(1));
            boolean found = false;
            try {
                ResourceBundle.getBundle(sb.toString());
                found = true;
            } catch (MissingResourceException e) {
            }
            // wenn ein ResoruceBundle für die aktuelle Sprache gefunden wurde
            if (found) {
                // Suche die Systemlocale zum gefundenen ResourceBundle
                for (int j = 0; j < systemLocales.length; j++) {
                    if (systemLocales[j].toString().equals(allLocales[i])) {
                        allFoundLocales[foundLocales++] = systemLocales[j];
                    }
                }
            }
        }
        Locale[] returnArray = new Locale[foundLocales];
        System.arraycopy(allFoundLocales, 0, returnArray, 0, foundLocales);
        return returnArray;
    }

    /**
     * wenn die Sanduhr abgelaufen ist...
     *
     * @return Default system cursor
     */
    public static Cursor getNormalCursor() {
        return normalCursor;
    }

    /**
     * Gets a string for the given key from this resource bundle or one of its parents.
     *
     * @param key
     *            String with key for resource or the key
     * @return String with value of resource
     */
    public static String getResString(final String key) {
        //das hier darf auf keinen Fall mit try-catch komplett umrandet werden, da mehrere Funktionen auf die
        //MissingResocureException regaieren (z.B. die Funktionen zum heraussuchen der Kantennamen bei
        //Kanten mit doppelter Bedeutung
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            return metamodelBundle.getString(key);
        }
    }

    /**
     * Gets a string for the given key from this resource bundle or one of its parents.
     * Wenn replacements übergeben werden, dann werden diese in den Originalstring eingesetzt.
     * Wo ersetzt werden soll wird durch den in geschweifte Klammern gesetzten Index des
     * einzusetztenden Replacements festgelegt.
     * Beispiel: der Res-String liefert "Alle {0} ersetzen" und als replacement wird ein
     * Parameter "Aufgaben" übergeben. Der Ergebnisstring ist dann "Alle Aufgaben ersetzen".
     *
     * @param key
     *            String with key for resource or the key
     * @param replacements
     *            Ersetzungen
     * @return String with value of resource
     */
    public static String getResString(final Object key, final String... replacements) {
        String s = getResString(key.toString());
        for (int i = 0; i < replacements.length; i++) {
            String currentReplacementMarker = "{" + i + "}";
            s = s.replace(currentReplacementMarker, replacements[i]);
        }
        return s;
    }

    /**
     * für die Sanduhr...
     *
     * @return Cursor that indicates a running process
     */
    public static Cursor getWaitCursor() {
        return waitCursor;
    }

    /**
     * Wenn nicht die linke Maustaste gedrückt wurde, wird true zurück gegeben.
     *
     * @param e
     * @return
     */
    public static final boolean isPopupTrigger(final MouseEvent e) {
        return (e.getModifiers() & InputEvent.BUTTON1_MASK) != InputEvent.BUTTON1_MASK;
    }

    /**
     * check, wheter thread can get the exclusive read/write-permission for a file
     *
     * @param f
     *            the file to check the permission
     * @return true, if thread can get exclusive read/write-permission for file TODO:AXS:prüfen was das hier soll
     */
    public static boolean lockSupportedByFileSystem(final File f) {
        return false;
        /*
         * try { File testFile = new File(f.getCanonicalPath() + "__3lgm_lock_test"); RandomAccessFile raf = new RandomAccessFile(testFile, "rw");
         * FileLock lock = raf.getChannel().tryLock();
         * lock.release(); raf.close(); testFile.delete(); } catch (Exception e) { return false; } return true;
         */}

    /**
     * setzt die Dateiangabe des programminternen Zwischenspeichers
     *
     * @param _path
     *            String mit Verzeichnis- und Dateiangabe des Zwischenspeichers
     */
    public static void setClipboardPath(final String _path) {
        clipboardPath = _path;
    }

    /**
     * setzt die Debug-Optionen fuer Swing-Komponenten
     *
     * @param LOG
     *            boolean; Ausgabe der Ereignisse?
     * @param FLASH
     *            boolean; Aufleuchten der Aenderungen?
     * @param BUFFERED
     *            boolean; Anzeige des Buffers?
     * @param flashTime
     *            int; Dauer des FLASH
     */
    public static void setDebugGraphicsOption(final boolean LOG, final boolean FLASH, final boolean BUFFERED, final int flashTime) {
        debugGraphicsOption = 0;
        if (LOG) {
            debugGraphicsOption |= DebugGraphics.LOG_OPTION;
        }
        if (FLASH) {
            debugGraphicsOption |= DebugGraphics.FLASH_OPTION;
            DebugGraphics.setFlashTime(flashTime);
        }
        if (BUFFERED) {
            debugGraphicsOption |= DebugGraphics.BUFFERED_OPTION;
        }
    }

}
