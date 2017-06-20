package de.imise.tool3lgm.userproperties;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.swing.filechooser.FileSystemView;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.view.browser.ModelBrowserPanel;

/**
 * @author AXS
 *         created on 16.08.2007
 */
public class UserProperties {
    //TODO:AXS: die UserProperties sollten in zukunft über Properties.class gemanaged werden.

    //ACHTUNG: alles was hier auskommentiert ist, war nur mal zum Test, wie es über Properties laufen könnte

    //	static Properties properties = new Properties();

    //	static final File USER_PROPERTIES_FILE = new File(System.getProperty("user.home") + "/" + ".too3lgmUserInfo.xml");

    //	/**
    //	 * Stellt Property-Change-Funktionalität zur Verfügung. <br>
    //	 * Zu der Klasse <code>PropertyChangeSupport</code> werden alle Property-Change-Listener
    //	 * hinzugefügt und in <code>firePorpertyChange()</code> werden alle Listener benachrichtigt.
    //	 * /
    //	private static PropertyChangeSupport changeSupport = new PropertyChangeSupport(UserProperties.class);
    //
    //	///////////////////////////////////////////////////
    //	// Listener hinzufügen/entfernen/benachrichtigen //
    //	///////////////////////////////////////////////////
    //
    //	/**
    //	 * Fügt einen <code>PropertyChangeListener</code> hinzu
    //	 * @param listener
    //	 * /
    //	public static final void addPropertyChangeListener(PropertyChangeListener listener) {
    //		changeSupport.addPropertyChangeListener(listener);
    //	}
    //
    //	/**
    //	 * Entfernt einen <code>PropertyChangeListener</code>
    //	 * @param listener
    //	 * /
    //	public static final void removePropertyChangeListener(PropertyChangeListener listener) {
    //		changeSupport.removePropertyChangeListener(listener);
    //	}

    /**
     * Liest die Benutzeroptionen ein.<br>
     * Je nachdem, ob bereits eine Datei mit Optionen im Home-Pfad des Benutzers existiert, wird diese geladen,
     * ansonsten werden die Standardeinstellungen aus den Ressourcen geladen.
     */
    public static final void init() {
        UserPropertiesContentHandler.readUserInfo();

        //		properties.put("locale", getLocale().getLanguage());
        //		properties.put("rendering_hints", renderingHints.toString());
        //		properties.put("show_links", showL.toString());
        //
        //		FileOutputStream out;
        //		try {
        //			if (!USER_PROPERTIES_FILE.exists())
        //				USER_PROPERTIES_FILE.createNewFile();
        //			out = new FileOutputStream(USER_PROPERTIES_FILE);
        //			properties.storeToXML(out, "Kommentar von AXS");
        //		} catch (Exception e) {
        //			e.printStackTrace();
        //		}

    }

    /**
     * Speichert die aktuell eingestellten Benutzeroptionen in einer Datei im User-Home-Pfad.
     */
    public static final void save() {
        UserPropertiesContentHandler.writeUserInfo();
    }

    ////////////
    // Locale //
    ////////////

    /** Locale, die der Benutzer gewählt hat */
    private static Locale locale = setLocale(Locale.getDefault());

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
     * @param locale
     */
    public static final Locale setLocale(final Locale locale) {
        if (locale == null) {
            return UserProperties.locale;
        }
        Locale[] locales = Tool3lgmConstants.getInstalledLanguages();
        Locale l = Locale.ENGLISH;
        for (int i = 0; i < locales.length; i++) {
            if (locales[i].getLanguage().equals(locale.getLanguage())) {
                l = locales[i];
                break;
            }
        }
        UserProperties.locale = l;
        Locale.setDefault(l);
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

    ///////////////
    // showLinks //
    ///////////////

    /** kennzeichne ModelElement mit verknuepften Teilmodell */
    private static boolean showLinks = true;

    /** @return showLinks */
    public static boolean isShowLinks() {
        return showLinks;
    }

    /** @param b */
    public static void setShowLinks(final boolean b) {
        showLinks = b;
    }

    /////////////////
    // searchParts //
    /////////////////

    /** Beim Suchen untergeordnete Elemente berücksichtigen */
    private static boolean searchParts;

    /** @return searchParts */
    public static boolean isSearchParts() {
        return searchParts;
    }

    /** @param b */
    public static void setSearchParts(final boolean b) {
        searchParts = b;
    }

    ///////////////////
    // searchParents //
    ///////////////////

    /** Beim Suchen übergeordnete Elemente berücksichtigen */
    private static boolean searchParents;

    /** @return searchParts */
    public static boolean isSearchParents() {
        return searchParents;
    }

    /** @param b */
    public static void setSearchParents(final boolean b) {
        searchParents = b;
    }

    /////////////////////
    // moveSubElements //
    /////////////////////

    /** Unterelemene werden beim verschieben mitbewegt */
    private static boolean moveSubelements;

    /** @return moveSubelements */
    public static boolean isMoveSubelements() {
        return moveSubelements;
    }

    /** @param b */
    public static void setMoveSubelements(final boolean b) {
        moveSubelements = b;
    }

    ///////////////////////////
    // enableSubmodelBrowser //
    ///////////////////////////

    /** Modellbrowser für Teilmodelle zeigen nur Elemente des Teilmodells an. (bei <code>false</code> werden alle angezeigt) */
    private static boolean enableSubmodelBrowser;

    /** @return enableSubmodelBrowser */
    public static boolean isEnableSubmodelBrowser() {
        return enableSubmodelBrowser;
    }

    /** @param b */
    public static void setEnableSubmodelBrowser(final boolean b) {
        enableSubmodelBrowser = b;
    }

    /////////////////////////////////
    // showModelsInSeparateBrowser //
    /////////////////////////////////

    /** show ModelBrowsers sidy by side or all models in one browser */
    private static boolean showModelsInSeparateBrowser;

    /**
     * Wenn <code>true</code>, werden alle Modelbrowser nebeneinander angezeigt, bei <code>false</code> werden alle in einem TabPanel untergebracht.
     *
     * @return multipleModelBrowser from <code>ModelBrowserPanel</code>
     */
    public static boolean isShowModelsInSeparateBrowser() {
        return showModelsInSeparateBrowser;
    }

    /**
     * Wenn <code>true</code>, werden alle Modelbrowser nebeneinander angezeigt, bei <code>false</code> werden alle in einem TabPanel untergebracht.
     */
    public static void setShowModelsInSeparateBrowser(final boolean b) {
        showModelsInSeparateBrowser = b;
        Tool3lgm tool = Static.getTool();
        if (tool == null) {
            return;
        }
        ModelBrowserPanel mbp = tool.getModelBrowserPanel();
        if (mbp != null) {
            mbp.updateShowModelsInSeparateBrowser();
        }
    }

    /////////////////////////////
    // showSubModelsSideBySide //
    /////////////////////////////
    /** show SubModel sidy by side (with an slider) or always all */
    private static boolean showSubModelsInBrowserSideBySide = false;

    /**
     * Wenn <code>true</code>, werden die Tabs der Teilmodelle im Modelbrowser nebeneinander angezeigt
     * und nicht alle gleichzeitig untereinander.
     *
     * @return multipleModelBrowser from <code>ModelBrowserPanel</code>
     */
    public static boolean isShowSubModelsInBrowserSideBySide() {
        return showSubModelsInBrowserSideBySide;
    }

    /**
     * Wenn <code>true</code>, werden die Tabs der Teilmodelle im Modelbrowser nebeneinander angezeigt
     * und nicht alle gleichzeitig untereinander.
     *
     * @param b
     */
    public static void setShowSubModelsInBrowserSideBySide(final boolean b) {
        showSubModelsInBrowserSideBySide = b;
    }

    /////////////////////////////////////////////
    // showUserDefinedPropertiesInModelBrowser //
    /////////////////////////////////////////////

    /** Benutzerdefinierte Eigenschaften im Modellbrowser darstellen */
    private static boolean showUserDefinedPropertiesInModelBrowser;

    /** @return showUserDefinedProperties */
    public static boolean isShowUserDefinedPropertiesInModelBrowser() {
        return showUserDefinedPropertiesInModelBrowser;
    }

    /** @param b */
    public static void setShowUserDefinedPropertiesInModelBrowser(final boolean b) {
        showUserDefinedPropertiesInModelBrowser = b;
    }

    /////////////////////////
    // showPartOfHierarchy //
    /////////////////////////

    /** Modellbrwoser zeigen die Teil-Von-Hierarchie an */
    private static boolean showPartOfHierarchy;

    /** @return showPartOfHierarchy */
    public static boolean isShowPartOfHierarchy() {
        return showPartOfHierarchy;
    }

    /** @param b */
    public static void setShowPartOfHierarchy(final boolean b) {
        showPartOfHierarchy = b;
    }

    ///////////////////////////////////////
    // paintEdgesOnlyForSelectedElements //
    ///////////////////////////////////////

    /** Kanten werden nur für selektierte Elemente gespeichert */
    private static boolean paintEdgesOnlyForSelectedElements = false;

    /** @return paintEdgesOnlyForSelectedElements */
    public static boolean isPaintEdgesOnlyForSelectedElements() {
        return paintEdgesOnlyForSelectedElements;
    }

    /** @param b */
    public static void setPaintEdgesOnlyForSelectedElements(final boolean b) {
        paintEdgesOnlyForSelectedElements = b;
    }

    ///////////////////////
    // usePropertyColors //
    ///////////////////////

    /**
     * AXS: Das hier hat irgendwas mit der Farbe von benutzerdef. Eigenschaften im Baum zu tun.
     * Wenn die im Baum angezeigt werden, dann soll wohl ihre Farbe auf das dazugehörige ModelElement
     * im Baum übertragen werden. Wer auch immer das implementiert hat, hat es nicht zuende programmiert.
     * Mir ist nicht klar, wozu das gut sein könnte, da man für benutzerdef. Eigenschaften gar keine
     * Farbe setzen kann.
     */
    private static boolean usePropertyColors;

    /** @return usePropertyColors */
    public static boolean isUsePropertyColors() {
        return usePropertyColors;
    }

    /** @param usePropertyColors The usePropertyColors to set. */
    public static void setUsePropertyColors(final boolean b) {
        usePropertyColors = b;
    }

    ///////////////
    // useRaster //
    ///////////////

    /** Wenn <code>true</code> werden alle Verschiebungen in der Grafik auf einem Raster ausgeführt. */
    private static boolean useRaster;

    /** @return useRaster */
    public static boolean isUseRaster() {
        return useRaster;
    }

    /** @param b */
    public static void setUseRaster(final boolean b) {
        useRaster = b;
    }

    /** Wenn <code>true</code> wird in der Grafik das Raster gezeichnet. */
    private static boolean showRaster;

    /** @return showRaster */
    public static boolean isShowRaster() {
        return showRaster;
    }

    /** @param b */
    public static void setShowRaster(final boolean b) {
        showRaster = b;
    }

    /** Rasterweite */
    private static int rasterWidth = 5;

    /** @param b */
    public static void setRasterWidth(final int i) {
        rasterWidth = i;
    }

    /** @return rasterWidth */
    public static int getRasterWidth() {
        return rasterWidth;
    }

    ///////////////////////////////
    // assignConfigurationColors //
    ///////////////////////////////

    /** Jede Konfigutarion bekommt eine eigene Farbe und ist nicht nur schwarz */
    private static boolean assignConfigurationColors;

    /** @return assignConfigurationColors */
    public static boolean isAssignConfigurationColors() {
        return assignConfigurationColors;
    }

    /** @param b */
    public static void setAssignConfigurationColors(final boolean b) {
        assignConfigurationColors = b;
    }

    //////////////////
    // showToolTips //
    //////////////////

    /** zeige ToolTip mit Beschreibung der ModelElemente */
    private static boolean showToolTips;

    /** @return showToolTips */
    public static boolean isShowToolTips() {
        return showToolTips;
    }

    /** @param b */
    public static void setShowToolTips(final boolean value) {
        showToolTips = value;
    }

    ////////////////////////////
    // newSubmodelForAnalysis //
    ////////////////////////////

    /** Ergebniselemente einer XMLAnalyse werden in ein neues Teilmodell übernommen */
    private static boolean newSubmodelForAnalysis;

    /** @return newSubmodelForAnalysis */
    public static boolean isNewSubmodelForAnalysis() {
        return newSubmodelForAnalysis;
    }

    /** @param b */
    public static void setNewSubmodelForAnalysis(final boolean b) {
        newSubmodelForAnalysis = b;
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

    ////////////////////////////
    // showABKonfigRedundance //
    ////////////////////////////

    /**
     * Anwendungbaustein-Konfigurationsredundanz anzeigen.<br>
     * Diese Option wird nicht gespeichert, weil es vertretbar ist, dass der Benutzer die Option wieder
     * einschaltet, wenn ihn der Wert interessiert. Außerdem ist diese Option eher modellspezifisch
     * als eine globale Benutzeroption.
     */
    private static transient boolean showABKonfigRedundance;

    /** @return showABKonfigRedundance */
    public static boolean isShowABKonfigRedundance() {
        return showABKonfigRedundance;
    }

    /** @param b */
    public static void setShowABKonfigRedundance(final boolean b) {
        showABKonfigRedundance = b;
    }

    ////////////////////////
    // showDataRedundance //
    ////////////////////////

    /**
     * Datenredundanz anzeigen.<br>
     * Diese Option wird nicht gespeichert, weil es vertretbar ist, dass der Benutzer die Option wieder
     * einschaltet, wenn ihn der Wert interessiert. Außerdem ist diese Option eher modellspezifisch
     * als eine globale Benutzeroption.
     */
    private static boolean showDataRedundance;

    /** @return showDataRedundance */
    public static boolean isShowDataRedundance() {
        return showDataRedundance;
    }

    /** @param b */
    public static void setShowDataRedundance(final boolean b) {
        showDataRedundance = b;
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

    ////////////////////////
    // lastUsedModelFiles //
    ////////////////////////

    /** die zu letzt benutzen Dateien */
    private static final ArrayList<File> lastUsedModelFiles = new ArrayList<File>(Tool3lgmConstants.LAST_USED_MODEL_FILES_IN_MENU) {
        @Override
        public boolean add(final File o) {
            if (o == null) {
                return false;
            }
            remove(o);
            if (size() == Tool3lgmConstants.LAST_USED_MODEL_FILES_IN_MENU) {
                this.remove(Tool3lgmConstants.LAST_USED_MODEL_FILES_IN_MENU - 1);
            }
            add(0, o);
            return true;
        }
    };

    /**
     * gibt die zuletzt benutzen Datei zurück
     *
     * @return ArrayList mit File-Objekten
     */
    public static ArrayList<File> getLastUsedFiles() {
        return lastUsedModelFiles;
    }

    /**
     * fügt eine Datei zu der List mit den zuletzt geöffenten Datei hinzu
     *
     * @param file Datei, die benutzt wurde
     */
    public static void addUsedFile(final File file) {
        lastUsedModelFiles.add(file);
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
        return new ArrayList<File>(xslSearchDirs);
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

    //////////////////
    // userHomePath //
    //////////////////

    /** users home directory */
    private static File userHomePath = FileSystemView.getFileSystemView().getDefaultDirectory();

    /**
     * setzt das Standardverzeichnis zum Laden und Speichern von Modellen und
     * Exportdateien
     *
     * @param path File mit Pfandangabe
     */
    public static void setUserHomePath(final File path) {
        userHomePath = path;
    }

    /**
     * gibt das Standardverzeichnis zum Laden und Speichern von Modellen und
     * Exportdateien zurueck
     *
     * @return File des Standardverzeichnisses
     */
    public static File getUserHomePath() {
        return userHomePath;
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

    private static String RMIRegistryPort = "";

    /** @param b */
    public static void setRMIRegistryPort(final String b) {
        RMIRegistryPort = b;
    }

    /** @return RMIRegitryPort */
    public static String getRMIRegistryPort() {
        return RMIRegistryPort;
    }

    //////////////////////////////////////////////
    // Consistenzprüfung ein oder ausgeschaltet //
    //////////////////////////////////////////////

    /** Wenn <code>true</code>, wird die Konsitenz des aktuellen Modells überprüft. */
    private static boolean checkConsistency;

    /** @return checkConsistency */
    public static boolean isCheckConsistency() {
        return checkConsistency;
    }

    /** @param b */
    public static void setCheckConsistency(final boolean b) {
        checkConsistency = b;
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
