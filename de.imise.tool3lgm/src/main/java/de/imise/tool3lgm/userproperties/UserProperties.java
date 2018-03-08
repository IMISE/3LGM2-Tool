package de.imise.tool3lgm.userproperties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.filechooser.FileSystemView;

import org.apache.commons.collections4.map.Flat3Map;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.util.io.FileHandler;

/**
 * @author AXS
 *         created on 16.08.2007
 */
public class UserProperties {

    static Properties properties = new Properties();

    /**
     * Stellt Property-Change-Funktionalität zur Verfügung. <br>
     * Zu der Klasse <code>PropertyChangeSupport</code> werden alle Property-Change-Listener
     * hinzugefügt und in <code>firePorpertyChange()</code> werden alle Listener benachrichtigt.
     */
    private static PropertyChangeSupport changeSupport = new PropertyChangeSupport(UserProperties.class);

    ///////////////////////////////////////////////////
    // Listener hinzufügen/entfernen/benachrichtigen //
    ///////////////////////////////////////////////////

    /**
     * Fügt einen <code>PropertyChangeListener</code> hinzu
     *
     * @param listener
     */
    public static final void addPropertyChangeListener(final PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Entfernt einen <code>PropertyChangeListener</code>
     *
     * @param listener
     */
    public static final void removePropertyChangeListener(final PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Sendet an alle PropertyChangeListener das Ereignis, dass sich etwas geändert hat
     *
     * @param property
     * @param oldValue
     * @param newValue
     */
    public static final void firePropertyChange(final Object property, final String oldValue, final String newValue) {
        changeSupport.firePropertyChange(property.toString(), oldValue, newValue);
    }

    /**
     * Prüft, ob das ChangeEvent für das übergebene Property-Objekt ausgelöst wurde
     *
     * @param property
     * @param event
     * @return
     */
    public static final boolean isPropertyChange(final Object property, final PropertyChangeEvent event) {
        return property.toString().equals(event.getPropertyName());
    }

    /**
     * Liest die Benutzeroptionen ein.<br>
     * Je nachdem, ob bereits eine Datei mit Optionen im Home-Pfad des Benutzers existiert, wird diese geladen,
     * ansonsten werden die Standardeinstellungen aus den Ressourcen geladen.
     */
    public static final void init() {
        initDefaults();
        readUserInfo();
    }

    private static void initDefaults() {
        for (BooleanProperty property : BooleanProperty.values()) {
            put(property, property.getDefault());
        }
        for (IntProperty property : IntProperty.values()) {
            put(property, property.getDefault());
        }
    }

    private static Object put(final Object key, final Object value) {
        String newValue = String.valueOf(value);
        Object oldValue = properties.put(key.toString(), newValue);
        firePropertyChange(key, oldValue == null ? null : oldValue.toString(), newValue);
        return oldValue;
    }

    public static boolean is(final BooleanProperty property) {
        String value = properties.getProperty(property.toString());
        return value == null ? property.getDefault() : Boolean.parseBoolean(value);
    }

    public static boolean set(final BooleanProperty property, final boolean value) {
        Object oldValue = put(property, value);
        return oldValue == null ? false : Boolean.valueOf(oldValue.toString());
    }

    //Da es nur 2 Listenschlüssel gibt, kann man hier eine Flat3Map nehmen
    private static final Map<StringProperty, Integer> listKeyToListSize = new Flat3Map<>();

    /**
     * Fügt den ListValue in die bestehende Liste ganz am Anfang ein. Wenn der Wert schon in der Liste vorkommt,
     * wird er an den Anfang verschoben.
     *
     * @param property
     * @param value
     */
    public static void addListValue(final StringProperty property, final String value) {
        //wenn das gar kein Listenwert ist -> einfach den einzigen Wert für den Key setzen
        int maxSize = property.getMaxListSize();
        if (maxSize == 1) {
            put(property, value);
        } else {
            String propertyName = property.toString();
            Integer sizeI = listKeyToListSize.get(property);
            int size = sizeI == null ? 0 : sizeI;
            int currentIndex = -1;
            for (int i = 0; i < size; i++) {
                String existingValue = properties.getProperty(propertyName + i);
                if (value.equals(existingValue)) {
                    currentIndex = i;
                    break;
                }
            }
            if (currentIndex < 0 && size < maxSize) {
                size++;
                listKeyToListSize.put(property, size);
            }
            String currentValue = value;
            int maxIndexToSwap = currentIndex < 0 ? size : currentIndex + 1;
            for (int i = 0; i < maxIndexToSwap; i++) {
                Object oldValue = put(propertyName + i, currentValue);
                currentValue = String.valueOf(oldValue);
            }
        }
    }

    public static void setListValues(final StringProperty property, final List<String> values) {
        remove(property);
        for (int i = values.size() - 1; i >= 0; i--) {
            addListValue(property, values.get(i));
        }
    }

    private static void remove(final StringProperty property) {
        String propertyName = property.toString();
        Integer sizeI = listKeyToListSize.get(propertyName);
        if (sizeI == null) {
            properties.remove(propertyName);
            return;
        }
        int size = sizeI == null ? 0 : sizeI;
        for (int i = 0; i < size; i++) {
            properties.remove(propertyName + i);
        }
        listKeyToListSize.remove(property.toString());
    }

    public static List<String> getListValues(final StringProperty property) {
        String propertyName = property.toString();
        Integer sizeI = listKeyToListSize.get(property);
        int size = sizeI == null ? 0 : sizeI;
        List<String> listValues = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            listValues.add(properties.getProperty(propertyName + i));
        }
        return listValues;
    }

    /**
     * Ließt die benutzerspezifischen Informationen aus dem Benutzer-Home-Verzeichnis
     * oder die Defaultdatei aus den Ressourcen.
     */
    private static void readUserInfo() {
        File userInfoFile = Tool3lgmConstants.USER_INFO_FILE;
        //wird true, wenn die Default-Benutzereinstellungen aus den Ressourcen geladen wurden
        boolean isDefault = false;
        if (!userInfoFile.canRead()) {
            FileHandler.copyFile(Tool3lgmConstants.DEFAULT_USER_INFO_FILE, userInfoFile);
            isDefault = true;
        }

        try {
            properties.load(new FileInputStream(userInfoFile));
        } catch (Exception exp) {
            //wenn die Datei nicht gelesen werden konnte und es sich nicht um die Standardeinstellungsdatei
            //handelt (dann hat irgendwer was in die Porperties-Datei des Benutzers geschrieben, was da nicht
            //reingehört -> Standarddatei laden)
            if (!isDefault) {
                userInfoFile.delete();
                readUserInfo();
            }
            //nicht loggen, da ToollgmConstants noch nicht da ist!
            //Log.show(Log.ERROR, "Exception while initilising user properties", exp);
            //exp.printStackTrace();
        }
        for (StringProperty stringProperty : StringProperty.values()) {
            if (stringProperty.getMaxListSize() > 1) {
                int entries = 0;
                String propertyName = stringProperty.toString();
                for (; entries < properties.size(); entries++) {
                    if (!properties.containsKey(propertyName + entries)) {
                        break;
                    }
                }
                if (entries > 0) {
                    listKeyToListSize.put(stringProperty, entries);
                }
            }
        }
        Enumeration<Object> keys = properties.keys();
        while (keys.hasMoreElements()) {
            Object propertyKey = keys.nextElement();
            Object value = properties.get(propertyKey);
            if (value == null || value.toString().isEmpty() || propertyKey.toString().startsWith("<?")) {
                properties.remove(propertyKey);
            }
        }
        Object localeLanguage = properties.get(StringProperty.LOCALE.toString());
        if (localeLanguage != null) {
            setLocale(localeLanguage.toString());
        }
    }

    public static void save() {
        try {
            if (!Tool3lgmConstants.USER_INFO_FILE.exists()) {
                Tool3lgmConstants.USER_INFO_FILE.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(Tool3lgmConstants.USER_INFO_FILE);
            properties.store(out, "Tool3lgm-Version " + Tool3lgmConstants.TOOL_VERSION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static enum BooleanProperty {
        /** Kennzeichne ModelElemente mit verknüpftem Teilmodell */
        OPTION_SHOW_LINKED_WITH_SUBMODEL_SYMBOLS,
        /** Elemente erben Eigenschaften ihrer Teile (diese Option ist nur in Ausnahmefällen sinnvoll) */
        OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS,
        /** Elemente erben Eigenschaften ihrer Oberelemente */
        OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS,
        /** Unterelemente werden in der Grafik mit den Oberelementen verschoben (beim Draggen) */
        OPTION_GRAPH_MOVE_SUBELEMENTS,
        /** Der Modelbrowser eines Teilmodells zeigt nur die Elemente im Teilmodell an und nicht immer alle Elemente des Gesamtmodells */
        OPTION_ENABLE_SUBMODEL_BROWSER,
        /** Jedes geöffnete Modell hat einen eigenen ModelBrowser, die alle nebeneinander angeordnet werden */
        OPTION_SHOW_MODELS_IN_SEPARATE_BROWSER,
        /** Zeige Benutzerdefnierte Eigenschaften im ModellBrowser */
        OPTION_SHOW_USER_DEFINED_PROPERTIES_IN_MODEL_BROWSER,
        /** Hänge alle Teilelemente in Bäumen unter ihre Oberelemente (true) oder ordne alle Elemente in einer flachen Liste an (false) */
        OPTION_SHOW_PART_OF_HIERARCHY,
        /** Kanten werden nur für selektierte Elemente in der Grafik gemalt (true) oder alle Kanten werde gezeichnet (false) */
        OPTION_PAINT_EDGES_ONLY_FOR_SELECTED_ELEMENTS,
        /**
         * Wenn man den UserProperties Farben gibt, werden sie im Baum Farbig geschrieben. Das ist aktuell nicht komplett implementiert.D.h. hierfür
         * gibt es keine ins Menü eingebundene Umschalt-Action und der Wert ist immer true
         */
        OPTION_USE_PROPERTY_COLORS,
        /** Raster in der Grafik an/aus */
        OPTION_USE_RASTER,
        /** Raster in der Grafik anzeigen */
        OPTION_SHOW_RASTER,
        /** Konfigurationen bunt oder alle schwarz */
        OPTION_ASSIGN_CONFIGURATION_COLORS,
        /** Analyseergebnisse werden automatisch in einem neuen Teilmodell eingefügt (true) oder nur in der Grafik hervorgehoben */
        OPTION_CREATE_NEW_SUBMODEL_FOR_ANALYSIS_RESULT,
        /** Medienbrüche werden in der Grafik angezeigt */
        OPTION_SHOW_MEDIUM_BREAKS,
        /** Kennzahlberechnung an/aus */
        OPTION_ENABLE_CLASSIFICATION_NUMBER_CALCULATION,
        /** Konsistenzcheck an/aus */
        OPTION_CHECK_CONSISTENCY,
        /** Warnung vor dem Löschen von Elementen aus dem Gesamtmodell */
        OPTION_SHOW_REMOVE_WARNING;

        private static final Set<BooleanProperty> DEFAULT_TRUE_PROERTIES = ImmutableSet.of(OPTION_SHOW_LINKED_WITH_SUBMODEL_SYMBOLS, OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS, OPTION_GRAPH_MOVE_SUBELEMENTS, OPTION_ENABLE_SUBMODEL_BROWSER,
                OPTION_SHOW_PART_OF_HIERARCHY, OPTION_USE_PROPERTY_COLORS, OPTION_USE_RASTER, OPTION_ASSIGN_CONFIGURATION_COLORS, OPTION_SHOW_REMOVE_WARNING);

        private boolean getDefault() {
            return DEFAULT_TRUE_PROERTIES.contains(this);
        }

    }

    public static enum IntProperty {
        RASTER_WIDTH {
            @Override
            public int getDefault() {
                return 5;
            }
        },
        /**
         * Bitpattern for Rendering-Hints (standard value: all bits are set to zero
         * bit0: ANTIALIASING
         * bit1: ALPHA_INTERPOLATION
         * bit2: COLOR_RENDERING
         * bit3: RENDERING
         * bit4: DITHERING
         * bit5: FRACTIONALMETRICS
         * bit6: INTERPOLATION
         * bit7: TEXT_ANTIALIASING
         */
        RENDERING_HINTS {
            @Override
            public int getDefault() {
                return 137;
            }
        },
        RMI_REGISTRY_PORT {
            @Override
            public int getDefault() {
                return 1099;
            }
        };
        public int getDefault() {
            return -1;
        }
    }

    public static enum StringProperty {
        LOCALE,
        WORKING_DIRECTORY,
        ICON_PATH,
        /** Liste der zuletzt benutzten ModellDateien */
        LAST_USED_MODEL_FILES {
            @Override
            public int getMaxListSize() {
                return Tool3lgmConstants.LAST_USED_MODEL_FILES_IN_MENU;
            }
        },
        /** Liste der zuletzt benutzten Verzeichnisse mit XSLT-Scripten */
        XSL_SEARCH_DIRS {
            @Override
            public int getMaxListSize() {
                return Integer.MAX_VALUE;
            }
        };
        /**
         * Wenn die Property eine Liste sein soll, dann muss sie eine ListSize > 1 haben.
         *
         * @return Größe der Liste dieser Property
         */
        public int getMaxListSize() {
            return 1;
        }
    }

    ////////////
    // Locale //
    ////////////

    /** Locale, die der Benutzer gewählt hat */
    private static Locale locale = setLocale(Locale.getDefault().getLanguage());

    /**
     * Liefert die eingestellte Locale. Hat der Benutzer sie nicht geändert, entspricht sie der des Systems.
     */
    public static final Locale getLocale() {
        return locale;
    }

    /**
     * Setzt die Locale, mit der alle Ressourcen geladen werden.<br>
     * Existieren für die Sprache dieser Locale keine Ressourcen, werden die englischen
     * Standardressorucen geladen.<br>
     * Das Umstellen der Locale hat nur Auswirkungen auf das sortieren der Elemente (siehe <code>Alphabetical.class</code>) und bei der Anzeige von
     * Dialogen aus dem util-Package.
     * Da das <code>ResourceBundle</code> für die vorher selektierte Sprache nicht neu geladen wird
     * und das Hauptfenster nicht zur Laufzeit neu initialisiert werden kann, wird das Umstellen der
     * locale dort erst nach einem Neustart sichtbar.
     *
     * @param laguage
     */
    public static final Locale setLocale(final String language) {
        if (language == null) {
            return UserProperties.locale;
        }
        Locale[] locales = Tool3lgmConstants.getInstalledLanguages();
        Locale l = Locale.ENGLISH;
        for (int i = 0; i < locales.length; i++) {
            if (locales[i].getLanguage().equals(language)) {
                l = locales[i];
                break;
            }
        }
        UserProperties.locale = l;
        Locale.setDefault(l);
        put(StringProperty.LOCALE, l.getLanguage());
        return l;
    }

    ////////////////////
    // renderingHints //
    ////////////////////

    /**
     * Bitpattern for Rendering-Hints (standard value: all bits are set to zero
     * bit0: ANTIALIASING
     * bit1: ALPHA_INTERPOLATION
     * bit2: COLOR_RENDERING
     * bit3: RENDERING
     * bit4: DITHERING
     * bit5: FRACTIONALMETRICS
     * bit6: INTERPOLATION
     * bit7: TEXT_ANTIALIASING
     */
    private static Integer renderingHints = Integer.valueOf(137);

    /**
     * return the current Bitpattern for Rendering-Hints
     * bit0: ANTIALIASING
     * bit1: ALPHA_INTERPOLATION
     * bit2: COLOR_RENDERING
     * bit3: RENDERING
     * bit4: DITHERING
     * bit5: FRACTIONALMETRICS
     * bit6: INTERPOLATION
     * bit7: TEXT_ANTIALIASING
     *
     * @return int with bit-pattern
     */
    public static int getRenderingHints() {
        return renderingHints.intValue();
    }

    /**
     * set new Bitpattern for Rendering-Hints
     * bit0: ANTIALIASING
     * bit1: ALPHA_INTERPOLATION
     * bit2: COLOR_RENDERING
     * bit3: RENDERING
     * bit4: DITHERING
     * bit5: FRACTIONALMETRICS
     * bit6: INTERPOLATION
     * bit7: TEXT_ANTIALIASING
     *
     * @param int with bit-pattern
     */
    public static void setRenderingHints(final int renderingHints) {
        UserProperties.renderingHints = Integer.valueOf(renderingHints);
    }

    /** Rasterweite */
    private static int rasterWidth = 7;

    /** @param b */
    public static void setRasterWidth(final int i) {
        rasterWidth = i;
    }

    /** @return rasterWidth */
    public static int getRasterWidth() {
        return rasterWidth;
    }

    ///////////////////////
    // showExpansionSign //
    ///////////////////////

    /**
     * Zusammengeklappte Elemente werden speziell gezeichnet.<br>
     * Diese Option wird absichtlich <b>nicht </b> gespeichert und ist zu Beginn immer
     * eingeschaltet.
     */
    private static transient boolean showExpansionSign = true;

    /** @return showExpansionSign */
    public static boolean isShowExpansionSign() {
        return showExpansionSign;
    }

    /** @param b */
    public static void setShowExpansionSign(final boolean b) {
        showExpansionSign = b;
    }

    //////////////////////
    // showMediumBreaks //
    //////////////////////

    /**
     * Medienbrüche anzeigen.<br>
     * Diese Option wird nicht gespeichert, weil es vertretbar ist, dass der Benutzer die Option wieder
     * einschaltet, wenn ihn der Wert interessiert. Außerdem ist diese Option eher modellspezifisch
     * als eine globale Benutzeroption.
     */
    private static boolean showMediumBreaks;

    /** @return showMediumBreaks */
    public static boolean isShowMediumBreaks() {
        return showMediumBreaks;
    }

    /** @param b */
    public static void setShowMediumBreaks(final boolean b) {
        showMediumBreaks = b;
    }

    ///////////////////
    // xslSearchDirs //
    ///////////////////

    /** Liste mit Verzeichnissen, in denen XSL-Skripte gesucht werden; */
    private static final ArrayList<File> xslSearchDirs = new ArrayList<File>() {
        @Override
        public boolean add(final File o) {
            if (contains(o)) {
                return true;
            }
            return super.add(o);
        }
    };

    /** @return Kopie der Liste aller Verzeichnisse, in denen nach XSLT-Scripten gesucht wird */
    public static ArrayList<File> getXSLSearchDirs() {
        return new ArrayList<>(xslSearchDirs);
    }

    public static boolean addXslSearchDir(final File file) {
        return xslSearchDirs.add(file);
    }

    public static void clearXslSearchDir() {
        xslSearchDirs.clear();
    }

    public static boolean addAllXslSearchDir(final Collection<File> fileList) {
        return xslSearchDirs.addAll(fileList);
    }

    //////////////////////
    // workingDirectory //
    //////////////////////

    /** users home directory */
    private static File workingDirectory = FileSystemView.getFileSystemView().getDefaultDirectory();

    /**
     * setzt das Standardverzeichnis zum Laden und Speichern von Modellen und
     * Exportdateien
     *
     * @param path File mit Pfandangabe
     */
    public static void setWorkingDirectory(final File path) {
        File directory = path.isDirectory() ? path : path.getParentFile();
        try {
            if (directory != null && directory.canRead()) {
                workingDirectory = directory;
            }
        } catch (Exception e) {
            // mache nichts -> behalte altes Verzeichnis
        }
    }

    /**
     * gibt das Standardverzeichnis zum Laden und Speichern von Modellen und
     * Exportdateien zurueck
     *
     * @return File des Standardverzeichnisses
     */
    public static File getWorkingDirectory() {
        return workingDirectory;
    }

    //////////////
    // iconPath //
    //////////////

    /** user path for importing icons */
    private static File iconPath = FileSystemView.getFileSystemView().getDefaultDirectory();

    /**
     * setzt das Standardverzeichnis Importieren von Icons
     *
     * @param path File mit Icon-Pfandangabe
     */
    public static void setIconPath(final File path) {
        iconPath = path;
    }

    /**
     * gibt das Standardverzeichnis zum importieren von Icons zurueck
     *
     * @return File des Icon-Standardverzeichnisses
     */
    public static File getIconPath() {
        return iconPath;
    }

    ///////////////////////////////////////////
    // enableClassificationNumberCalculation //
    ///////////////////////////////////////////

    /** Wenn <code>true</code>, werden die Werte von Kennzahlformeln neu berechnet, wenn Änderungen am Modell vorgenommen wurden. */
    private static boolean enableClassificationNumberCalculation;

    /** @return enableClassificationNumberCalculation */
    public static boolean isEnableClassificationNumberCalculation() {
        return enableClassificationNumberCalculation;
    }

    /** @param b */
    public static void setEnableClassificationNumberCalculation(final boolean b) {
        enableClassificationNumberCalculation = b;
    }

    ///////////////////////////////////////////
    // RMI - Funktion, aktivierung und Ports //
    ///////////////////////////////////////////

    private static int rmiRegistryPort = -1;

    /** @param b */
    public static void setRMIRegistryPort(final int port) {
        rmiRegistryPort = port;
    }

    /** @return RMIRegitryPort */
    public static int getRMIRegistryPort() {
        return rmiRegistryPort;
    }

    /**
     * Wenn <code>true</code>, wird vor dem Löschen von Elementen aus dem Hauptmodell
     * und allen Teilmodellen, eine Warnung angezeigt.
     */
    private static boolean showRemoveWarning = true;

    /** @return {@link #showRemoveWarning} */
    public static boolean isShowRemoveWarning() {
        return showRemoveWarning;
    }

    /** @param b */
    public static void setShowRemoveWarning(final boolean b) {
        showRemoveWarning = b;
    }

}
