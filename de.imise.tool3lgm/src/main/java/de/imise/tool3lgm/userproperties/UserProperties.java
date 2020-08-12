package de.imise.tool3lgm.userproperties;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.collections4.map.Flat3Map;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.event.action.ChangeLocaleAction;
import de.imise.tool3lgm.event.action.UserPropertyBooleanChangeAction;
import de.imise.util.io.FileHandler;
import de.imise.util.swing.event.ActionSource;

/**
 * @author AXS
 *         created on 16.08.2007
 */
public class UserProperties extends AbstractUserProperties {

    /** Pfad zur Default-Datei in den Ressourcen mit den Optionen für einen Benutzer */
    private static final URL DEFAULT_USER_INFO_FILE = ClassLoader.getSystemResource("DefaultUserProperties");

    /** Pfad zur Datei mit den Optionen eines Benutzers */
    private static final File USER_INFO_FILE = new File(System.getProperty("user.home"), ".tool3lgm2UserInfo");

    /**
     * Liest die Benutzeroptionen ein.<br>
     * Je nachdem, ob bereits eine Datei mit Optionen im Home-Pfad des Benutzers existiert, wird diese geladen,
     * ansonsten werden die Standardeinstellungen aus den Ressourcen geladen.
     */
    public static final void init() {
        initDefaults();
        readUserInfo();
    }

    /**
     * Setzt für alle Properties die Defaults
     */
    private static void initDefaults() {
        for (BooleanProperty property : BooleanProperty.values()) {
            put(property, property.getDefault());
        }
        for (IntProperty property : IntProperty.values()) {
            put(property, property.getDefault());
        }
    }

    /**
     * Sets the value for property to the value
     *
     * @param property
     * @param value
     * @return the old value
     */
    private static boolean set(final BooleanProperty property, final boolean value) {
        Object oldValue = put(property, value);
        return oldValue == null ? false : Boolean.valueOf(oldValue.toString());
    }

    /**
     * @param property
     * @return the boolean value of this Property
     */
    private static boolean is(final BooleanProperty property) {
        String value = properties.getProperty(property.toString());
        return value == null ? property.getDefault() : Boolean.parseBoolean(value);
    }

    /**
     * Sets the value of the property to the value
     *
     * @param property
     * @param value
     * @return the old value
     */
    private static int set(final IntProperty property, final int value) {
        Object oldValue = put(property, value);
        return oldValue == null ? -1 : Integer.valueOf(oldValue.toString());
    }

    /**
     * @param property
     * @return the int value of the Property
     */
    private static final int get(final IntProperty property) {
        String value = properties.getProperty(property.toString());
        return value == null ? property.getDefault() : Integer.parseInt(value);
    }

    /**
     * Sets the value of the property to the value
     *
     * @param property
     * @param value
     * @return the old value
     */
    private static String set(final StringProperty property, final String value) {
        Object oldValue = put(property, value);
        return oldValue == null ? null : oldValue.toString();
    }

    /**
     * @param property
     * @return the String value of the Property
     */
    private static final String get(final StringProperty property) {
        String value = properties.getProperty(property.toString());
        return value == null ? null : value;
    }

    /**
     * Map, die für StringProperties, die eine Liste bilden, angibt, wie viele Elemente in der Liste enthalten sind.
     */
    //Da es im Moment nur 2 Listenschlüssel gibt, die eine Lsite bilden, kann man hier eine Flat3Map nehmen (siehe StringProperty.getMaxListSize())
    private static final Map<StringProperty, Integer> listKeyToListSize = new Flat3Map<>();

    /**
     * Fügt den ListValue in die bestehende Liste ganz am Anfang ein. Wenn der Wert schon in der Liste vorkommt,
     * wird er an den Anfang verschoben.
     *
     * @param property
     * @param value
     * @return letzten Wert, der ganz vorne stand
     */
    public static String addListValue(final StringProperty property, final String value) {
        //wenn das gar kein Listenwert ist -> einfach den einzigen Wert für den Key setzen
        int maxSize = property.getMaxListSize();
        if (maxSize == 1) {
            return set(property, value);
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
            //gib des jetzt zweiten Listenwert als alten Wert zurück
            return size > 1 ? properties.getProperty(propertyName + "1") : null;
        }
    }

    /**
     * Fügt für eine StringProperty, die eine Liste bildet, einen Liste von Werten hinzu
     *
     * @param property
     * @param values
     */
    public static void setListValues(final StringProperty property, final List<String> values) {
        remove(property);
        for (int i = values.size() - 1; i >= 0; i--) {
            addListValue(property, values.get(i));
        }
    }

    /**
     * Entfernt eine StringProperty aus den Properties und löscht eventuell vorhandene Listenwerte
     *
     * @param property
     */
    public static void remove(final StringProperty property) {
        String propertyName = property.toString();
        Integer sizeI = listKeyToListSize.get(property);
        if (sizeI == null) {
            properties.remove(propertyName);
            return;
        }
        int size = sizeI == null ? 0 : sizeI;
        for (int i = 0; i < size; i++) {
            properties.remove(propertyName + i);
        }
        listKeyToListSize.remove(property);
    }

    /**
     * Liefert für eine StringProperty, die eine Liste bildet, die Liste aller Werte-Strings dieser Property.
     *
     * @param property
     * @return Liste aller Werte-Strings der Property
     */
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
     * Liest die benutzerspezifischen Informationen aus dem Benutzer-Home-Verzeichnis
     * oder die Defaultdatei aus den Ressourcen.
     */
    private static void readUserInfo() {
        File userInfoFile = USER_INFO_FILE;
        //wird true, wenn die Default-Benutzereinstellungen aus den Ressourcen geladen wurden
        boolean isDefault = false;
        if (!userInfoFile.canRead()) {
            FileHandler.copyFile(DEFAULT_USER_INFO_FILE, userInfoFile);
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

    /**
     * Speichert die Properties in der Datei
     */
    public static void save() {
        try {
            if (!USER_INFO_FILE.exists()) {
                USER_INFO_FILE.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(USER_INFO_FILE);
            //vor dem Speichern alle transienten Properties entfernen und danach wieder hinzufügen
            Properties transientProperties = new Properties();
            for (Object key : properties.keySet()) {
                if (isTransient(key)) {
                    transientProperties.put(key, properties.get(key));
                }
            }
            for (Object key : transientProperties.keySet()) {
                properties.remove(key);
            }
            properties.store(out, "Tool3lgm-Version " + Tool3lgmConstants.TOOL_VERSION);
            properties.putAll(transientProperties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Alle User-Optionen, die sich durch einen Boolean repräsentieren lassen.
     *
     * @author AXS (9 Aug 2017)
     */
    public static enum BooleanProperty implements ActionSource {
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
        /** Über CompositionEdges verbundene Elemente im ModellBrowser unterordnen */
        OPTION_SUBORDINATE_COMPOSITION_ELEMENTS_IN_MODEL_BROWSER,
        /** Zeige Benutzerdefnierte Eigenschaften im ModellBrowser */
        OPTION_SHOW_USER_DEFINED_PROPERTIES_IN_MODEL_BROWSER,
        /** Show also the template elements in model browser (and not only in the template browser) */
        OPTION_SHOW_TEMPLATE_ELEMENTS_IN_MODEL_BROWSER,
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
        /** Kennzahlberechnung an/aus */
        OPTION_ENABLE_CLASSIFICATION_NUMBER_CALCULATION,
        /** Show consistency table */
        OPTION_SHOW_CONSISTENCY_TABLE,
        /** Ask user to show the consistency table if new consistency occur */
        TRANSIENT_OPTION_ASK_SHOW_CONSISTENCY_TABLE,
        /** Warnung vor dem Löschen von Elementen aus dem Gesamtmodell */
        OPTION_SHOW_REMOVE_WARNING,

        /** Die Toolbar zum Zeichnen von Elementen an/aus */
        OPTION_SHOW_PAINTING_TOOLBAR,
        /** Standardtoolbar unter dem Menu an/aus */
        OPTION_SHOW_STANDARD_TOOLBAR,
        /** ModelBrowser an/aus */
        OPTION_SHOW_MODEL_BROWSER,
        /** TemplateBrowser an/aus */
        OPTION_SHOW_TEMPLATE_BROWSER,
        /** TemplateBrowser an/aus */
        OPTION_SHOW_VIEW_COMPONENT_TITLES,
        /** Beim Start den Abfrgadedialog anzeigen, mit dem man das Metamodell wählen kann an/aus */
        OPTION_SHOW_CHOOSE_METAMODEL_DIALOG,

        /**
         * Schaltet einige Editieroptionen frei, die im normalen Modus verborgen sind. Das ist z.B. dafür gedacht, dass man in diesem Modus IheActors
         * ändern kann, was ein normaler Benutzer nicht können soll.
         */
        OPTION_ENABLE_EXPERT_MODE,

        /**
         * Zusammengeklappte Elemente werden speziell gezeichnet.<br>
         * Diese Option wird absichtlich <b>nicht </b> gespeichert und ist zu Beginn immer eingeschaltet.
         */
        TRANSIENT_OPTION_SHOW_EXPANSION_SIGN,

        /**
         * Interne Option die das zusätzliche Zeichenen der Rechtecke um die selektierten Elemente in der
         * Grafik ein- oder ausschalten kann. Nur für Debug-Zwecke.
         */
        TRANSIENT_OPTION_DEBUG_GRAPH;

        /**
         * Alle BooleanProperties, deren Default-Wert <code>true</code> ist
         */
        private static final Set<BooleanProperty> DEFAULT_TRUE_PROERTIES = ImmutableSet.of(OPTION_SHOW_LINKED_WITH_SUBMODEL_SYMBOLS, OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS, OPTION_GRAPH_MOVE_SUBELEMENTS, OPTION_SHOW_PAINTING_TOOLBAR,
                OPTION_SHOW_STANDARD_TOOLBAR, OPTION_SHOW_VIEW_COMPONENT_TITLES, OPTION_SHOW_MODEL_BROWSER, OPTION_ENABLE_SUBMODEL_BROWSER, OPTION_SHOW_PART_OF_HIERARCHY, OPTION_SUBORDINATE_COMPOSITION_ELEMENTS_IN_MODEL_BROWSER, OPTION_USE_PROPERTY_COLORS,
                OPTION_USE_RASTER, OPTION_ASSIGN_CONFIGURATION_COLORS, OPTION_SHOW_REMOVE_WARNING, OPTION_SHOW_CHOOSE_METAMODEL_DIALOG, TRANSIENT_OPTION_SHOW_EXPANSION_SIGN, TRANSIENT_OPTION_ASK_SHOW_CONSISTENCY_TABLE);

        /**
         * @return Default-Wert dieser Property
         */
        public boolean getDefault() {
            return DEFAULT_TRUE_PROERTIES.contains(this);
        }

        /**
         * ChangeAction dieser Property
         */
        private UserPropertyBooleanChangeAction action;

        @Override
        public UserPropertyBooleanChangeAction createAction() {
            //Keine der Options hat eine InteractiveAction. Also kann man hier einfach direkt
            //die Action instanziieren und muss nicht über die default-Implementierung gehen.
            //Will man über die default-Implementierung gehen, muss man die Funktion getActionClass()
            //überscheiben und die UserPropertyBooleanChangeAction.class zurück geben. Das funktioniert
            //ganz genauso, wie das hier, außer dass hier der Konstuktor direkt und nicht über Reflection
            //aufgerufen wird und Eclipse und der Compiler diesen Aufruf als direkt Code-Referenz erkennen.
            if (action == null) {
                action = new UserPropertyBooleanChangeAction(this);
            }
            return action;
        }

        /**
         * Sets the value for this property to the value
         *
         * @param value
         * @return the old value
         */
        public boolean set(final boolean value) {
            return UserProperties.set(this, value);
        }

        /**
         * @return the boolean value, this option is set to <code>true</code> in the UserProperties
         */
        public boolean is() {
            return UserProperties.is(this);
        }

        /**
         * @return the boolean value, this option is set to <code>false</code> in the UserProperties
         */
        public boolean isNot() {
            return !is();
        }

        /**
         * @param event
         * @return <code>true</code> if the event is a change event for this property
         */
        public boolean isChanged(final PropertyChangeEvent event) {
            return UserProperties.isPropertyChange(this, event);
        }

    }

    /**
     * Alle User-Optionen, die sich durch einen Integer repräsentieren lassen.
     *
     * @author AXS (9 Aug 2017)
     */
    public static enum IntProperty {
        PROPERTY_INT_MAINFRAME_SCREEN_POSX,
        PROPERTY_INT_MAINFRAME_SCREEN_POSY,
        PROPERTY_INT_MAINFRAME_SCREEN_WIDTH,
        PROPERTY_INT_MAINFRAME_SCREEN_HEIGHT,

        PROPERTY_INT_MAINFRAME_EXTENDED_STATE {
            @Override
            public int getDefault() {
                return JFrame.NORMAL;
            }
        },

        PROPERTY_INT_MODELBRWOSER_GRAPHVIEW_DIVIDER_LOCATION,
        PROPERTY_INT_GRAPHVIEW_TEMPLATEBROWSER_DIVIDER_LOCATION,
        PROPERTY_INT_GRAPHVIEW_CONSISTENCY_TABLE_DIVIDER_LOCATION,

        PROPERTY_INT_RASTER_WIDTH {
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
        PROPERTY_INT_RENDER_SETTINGS {
            @Override
            public int getDefault() {
                return 137;
            }
        },
        PROPERTY_INT_RMI_PORT {
            @Override
            public int getDefault() {
                return 1099;
            }
        };

        /** Liefert den Default-Wert dieser Property */
        public int getDefault() {
            return -1;
        }

        /**
         * Sets the value of the this property to the value
         *
         * @param value
         * @return the old value
         */
        public int set(final int value) {
            return UserProperties.set(this, value);
        }

        /**
         * @param property
         * @return the int value of this Property
         */
        public final int get() {
            return UserProperties.get(this);
        }
    }

    /**
     * Alle User-Optionen, die sich durch einen String repräsentieren lassen.
     *
     * @author AXS (9 Aug 2017)
     */
    public static enum StringProperty {
        LOCALE,
        WORKING_DIRECTORY,
        ICON_PATH,
        META_MODEL,
        MODEL_CATEGORY,
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

        /**
         * Sets the value of this property to the value
         *
         * @param property
         * @param value
         * @return the old value
         */
        public String set(final String value) {
            return UserProperties.set(this, value);
        }

        /**
         * @param property
         * @return the String value of this Property
         */
        public final String get() {
            return UserProperties.get(this);
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
        Locale[] locales = ChangeLocaleAction.getInstalledLanguages();
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

    ///////////////////
    // xslSearchDirs //
    ///////////////////

    /** Liste mit Verzeichnissen, in denen XSL-Skripte gesucht werden; */
    private static final List<File> xslSearchDirs = new ArrayList<File>() {
        @Override
        public boolean add(final File o) {
            if (contains(o)) {
                return true;
            }
            return super.add(o);
        }
    };

    /** @return Kopie der Liste aller Verzeichnisse, in denen nach XSLT-Scripten gesucht wird */
    public static List<File> getXSLSearchDirs() {
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

    /**
     * setzt das Standardverzeichnis zum Laden und Speichern von Modellen und
     * Exportdateien
     *
     * @param path File mit Pfandangabe
     */
    public static void setWorkingDirectory(final File path) {
        File readableDirectory = FileHandler.getReadableDirectory(path);
        if (readableDirectory == null) {
            return;
        }
        set(StringProperty.WORKING_DIRECTORY, readableDirectory.toString());
    }

    /**
     * gibt das Standardverzeichnis zum Laden und Speichern von Modellen und
     * Exportdateien zurueck
     *
     * @return File des Standardverzeichnisses
     */
    public static File getWorkingDirectory() {
        String workingDirectoryName = get(StringProperty.WORKING_DIRECTORY);
        File workingDirectory = FileHandler.getReadableDirectory(workingDirectoryName);
        if (workingDirectory == null) {
            FileSystemView.getFileSystemView().getDefaultDirectory();
        }
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

}
