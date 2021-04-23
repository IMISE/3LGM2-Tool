package de.imise.tool3lgm;

import static de.imise.tool3lgm.Tool3lgmChangeListener.Tool3lgmChangeType.MODEL_CHANGE_MODEL_CLOSED;
import static de.imise.tool3lgm.Tool3lgmChangeListener.Tool3lgmChangeType.MODEL_CHANGE_MODEL_OPENED;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.SELECTION_CHANGED;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;

import java.awt.Cursor;
import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.imise.tool3lgm.Tool3lgmChangeListener.Tool3lgmChangeType;
import de.imise.tool3lgm.Tool3lgmConstants.FileFilterType;
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.graphtools.consistency.ModelValidatorDefinition;
import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialogsContext;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionFileHandler;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.model.template.TemplateLibrariesManager;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.gui.MainFrame;
import de.imise.tool3lgm.gui.MainFrameDesktopPane;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponent;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPaneFrameComponent;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.StringProperty;
import de.imise.util.BrowseUtils;
import de.imise.util.collections.CollectionUtils;
import de.imise.util.event.ListenerSupport;
import de.imise.util.swing.dialog.ExtendedFileChooser;

/** Die eigentliche Anwendung 3lgm */
public class Tool3lgm {

    /**
     * If <code>true</code> the {@link MainFrameDesktopPane} uses a
     * {@link JTabbedPane} instead of a {@link JDesktopPane} with
     * {@link JInternalFrame}s to sho the graph and matix views.
     */
    public static final boolean DESKTOP_WITH_TABS_INSTEAD_OF_INTERNAL_FRAMES = true;

    /** alle GDCollections */
    private final List<GDCollection> collections = new ArrayList<>();

    /** alle ChangeListener, die auf Toolereignisse reagieren müssen */
    private final ListenerSupport<Tool3lgmChangeListener> toolChangeListenerSupport = new ListenerSupport<>();

    /** Das Hauptfenster */
    private final MainFrame mainFrame;

    private final TemplateLibrariesManager templateLibrariesManager;

    /**
     * constructor
     *
     * @see java.lang.Object#Object()
     */
    Tool3lgm(final boolean visible) {

        Static.tool = this;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        // der templateLibrariesManager muss vor dem MainFrame initilaisiert werden, weil er vor dem
        // eventuell sichtbaren TemplateBrowser als Tool3lgmChangeListener registriert sein muuss, damit
        // er sich korrekt updated, bevor der TemplateBrowser beim selben Ereignis (selectedModelChanged)
        // auf ihn zugreift
        templateLibrariesManager = visible ? new TemplateLibrariesManager() : null;
        mainFrame = new MainFrame();
        mainFrame.setVisible(visible);

        //den Hauptframe in die Mitte setzen
        //setLocationRelativeTo(null);
        //den Hauptframe initialisieren, damit die JOption-Panes der Lizenzanfrage an der richtigen Stelle sind.
        //        setVisible(true);
        //        LicenseHandler.checkLicenses();
        //        setVisible(false);
        //        setLocation(0, 0);

    }

    /**
     * @param tcl
     */
    public final void addChangeListener(final Tool3lgmChangeListener tcl) {
        toolChangeListenerSupport.add(tcl);
    }

    /**
     * @param tcl
     */
    public final void removeChangeListener(final Tool3lgmChangeListener tcl) {
        toolChangeListenerSupport.remove(tcl);
    }

    /**
     * @param o
     * @return
     */
    public boolean isAddedToolChangeListener(final Object o) {
        return toolChangeListenerSupport.contains(o);
    }

    /**
     * @param changeType
     * @param source
     */
    public void distribute(final Tool3lgmChangeType changeType, final GraphDocument source) {
        changeType.deliverEvent(toolChangeListenerSupport, source);
    }

    /**
     * @param changeType
     * @param source
     */
    public void distribute(final Tool3lgmChangeType changeType, final GDCollection source) {
        distribute(changeType, source.getMainDoc());
    }

    /**
     * @param cursor
     */
    public void setCursor(final Cursor cursor) {
        mainFrame.setCursor(cursor);
    }

    /**
     * @return
     */
    public boolean hasVisibleMainFrame() {
        return mainFrame.isVisible();
    }

    /**
     * @return the templateLibrariesManager
     */
    public TemplateLibrariesManager getTemplateLibrariesManager() {
        return templateLibrariesManager;
    }

    /**
     * @param file
     * @return geladenes Model
     */
    private GDCollection loadFile(final File file) {
        GDCollection gdcoll = new GDCollection();
        try {
            GDCollectionFileHandler fileHandler = gdcoll.getFileHandler();
            if (!fileHandler.setFile(file)) {
                if (!hasVisibleMainFrame() || JOptionPane.showConfirmDialog(mainFrame, getResString("datei_gesperrt"), "", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return null;
                }
            }
            Static.showProgressDialog(true);
            Static.setProgressDialogTitle("load_model", file.getName());
            Static.setProgressDialogStatusLabel("read_progress");
            boolean retVal = fileHandler.loadFromRAF();
            return retVal ? gdcoll : null;
        } catch (Exception e) {
            Log.show(Log.FATAL, getResString("FehlerAllgemein"), e);
            Object[] buttons = new Object[] {
                    getResString("ok")
            };
            JOptionPane.showOptionDialog(mainFrame, getResString("oeffnenfehler") + "\n" + file.getPath() + "\n" + e.getMessage(), getResString("tool3lgm"), JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, buttons, null);
            Static.closeProgressDialog();
            return null;
        }
    }

    /**
     * lädt ein 3LGM²-Dokument (legt die Teilmodell-Fenster an, aktualisiert das
     * Fenster Menu und ModelBrowser)
     */
    public boolean createNewModel() {
        Tool3lgmModelType choosedModelType = Tool3lgmMetaModelContext.getNewModelType();
        if (choosedModelType == null) {
            return false;
        }
        return openModel(null, choosedModelType);
    }

    /**
     * Lädt eine Modell-Datei
     *
     * @param file die zu ladende Datei
     * @param fileChooserPathKey the identifier the {@link ExtendedFileChooser}
     *            needs to store and reopen the last used path
     * @return
     */
    public boolean openModelFile(final File file) {
        return openModelFile(file, null);
    }

    /**
     * Lädt eine Modell-Datei
     *
     * @param file die zu ladende Datei
     * @param fileChooserPathKey the identifier the {@link ExtendedFileChooser}
     *            needs to store and reopen the last used path
     * @return
     */
    public boolean openModelFile(File file, final Object fileChooserPathKey) {
        file = chooseModelFile(file, fileChooserPathKey);
        if (file != null && !file.isDirectory()) {
            try {
                return openModel(file, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * @param file
     * @return
     */
    /**
     * @param file
     * @param fileChooserPathKey the identifier the {@link ExtendedFileChooser}
     *            needs to store and reopen the last used path
     * @return
     */
    private File chooseModelFile(File file, final Object fileChooserPathKey) {
        if (file != null) {
            if (!file.isDirectory()) {
                return file;
            }
        }
        if (file == null || !file.isDirectory()) {
            file = UserProperties.getWorkingDirectory();
        }

        ExtendedFileChooser chooser = new ExtendedFileChooser(fileChooserPathKey, file);
        chooser.setMultiSelectionEnabled(false);
        FileNameExtensionFilter[] lgmFileFilter = Tool3lgmConstants.getFileNameExtensionFilters(FileFilterType.LGM3, FileFilterType.LGM3_ZIP, FileFilterType.LGM3_UNZIPPED);
        int chooserAnswer = chooser.showOpenDialog(mainFrame, false, lgmFileFilter);
        if (fileChooserPathKey == null) {
            UserProperties.setWorkingDirectory(chooser.getCurrentDirectory());
        }
        if (chooserAnswer == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            chooser.setVisible(false);
            return file;
        }
        return null;
    }

    /**
     * Legt ein neues Modell an oder lädt ein bestehendes aus einer Datei.
     *
     * @param file zu ladende Datei. Wenn <code>null</code> übergeben wird, wird
     *            eine neue Datei angelegt.
     * @param metaModelContext MetaModelContext der zu öffnenden Datei oder des
     *            neu anzulegenden Modells
     * @return <code>true</code>, wenn die Datei geöffnet werden konnte oder ein
     *         neues Modell angelegt wurde
     */
    private boolean openModel(final File file, final Tool3lgmModelType modelType) {
        GDCollection gdcoll;
        if (file == null) {
            gdcoll = new GDCollection(modelType, true);
            gdcoll.createSzenario();
        } else {
            gdcoll = loadFile(file);
            if (gdcoll == null) {
                Static.closeProgressDialog();
                return false;
            }
        }
        UserProperties.setWorkingDirectory(file);
        boolean openModel = openModel(gdcoll);
        //jede neue GDCollection wird mit bulk_mode true initialisiert, aber ab jetzt
        //sollen die UNDO-REDO-Kommandos wieder geloggt werden.
        gdcoll.setBulkMode(false);
        gdcoll.setAutomaticMode(false);
        Static.closeProgressDialog();
        return openModel;
    }

    /**
     * Legt ein neues Modell an oder lädt ein bestehendes aus einer Datei.
     *
     * @param file zu ladende Datei. Wenn <code>null</code> übergeben wird, wird
     *            eine neue Datei angelegt.
     * @param metaModelContext MetaModelContext der zu öffnenden Datei oder des
     *            neu anzulegenden Modells
     * @return <code>true</code>, wenn die Datei geöffnet werden konnte oder ein
     *         neues Modell angelegt wurde
     */
    public boolean openModel(final GDCollection gdcoll) {
        Static.setProgressDialogStatusLabel("finish_progress");
        collections.add(gdcoll);
        distribute(MODEL_CHANGE_MODEL_OPENED, gdcoll);
        //vor dem Selektieren des aktuellen Teilmodells alle nicht behebbaren Fehler löschen
        ModelValidatorDefinition modelValidatorDefinition = gdcoll.getModelValidatorDefinition();
        modelValidatorDefinition.clearUnfixableErrors(gdcoll);
        gdcoll.initSelectedDocByViewParameterFromFile();
        gdcoll.setUnchanged();
        System.gc();

        //		System.err.println();
        //		System.err.println("###########################################################################");
        //		printStatistic(gdcoll, true, false);
        //		System.err.println();
        //		printStatistic(gdcoll, false, false);
        //		System.err.println();
        //		printStatistic(gdcoll, true, true);
        //		System.err.println();
        //		printStatistic(gdcoll, false, true);
        //		System.err.println("###########################################################################");

        return true;
    }

    /**
     * @param withSystemExit if <code>false</code> System.exit(0) will not be
     *            called. Use this for tests where teh tool must be startet
     *            mustiple times.
     */
    public void close(final boolean withSystemExit) {
        if (withSystemExit) { //tests should not change the list
            //man muss die Liste Clonen, da sie sich durch setSelectedDoc() ändert
            List<GDCollection> collections = new ArrayList<>(this.collections);
            //die letzte ist immer die aktive
            for (int i = collections.size() - 1; i >= 0; i--) {
                GDCollection gdcoll = collections.get(i);
                setSelectedDoc(gdcoll.getSelectedDoc());
                if (!askUserCloseModel(gdcoll)) {
                    return;
                }
            }

            //Liste der zuletzt geöffneten Dateien merken
            for (int i = collections.size() - 1; i >= 0; i--) {
                GDCollection gdcoll = collections.get(i);
                try {
                    File file = gdcoll.getFile();
                    String path = file.getCanonicalPath();
                    UserProperties.addListValue(StringProperty.LAST_USED_MODEL_FILES, path);
                } catch (Exception ex) {
                }
            }
            UserProperties.save();
        }
        new File(Tool3lgmConstants.CLIPBOARD_PATH).delete();

        File temp = new File(Tool3lgmConstants.TEMP_PATH + "temp_3lgm_export_file.html");
        if (temp.exists()) {
            temp.delete();
        }

        if (withSystemExit) {
            System.exit(0);
        } else {
            mainFrame.disposeWithoutSystemExit();
            Static.tool = null;
        }
    }

    /**
     * @return Hauptfenster der Anwendung
     */
    public MainFrame getMainFrame() {
        return mainFrame;
    }

    /**
     *
     */
    public void openMatrixView() {
        LGMGraphDocument selectedDoc = getSelectedDoc();
        mainFrame.createMatrixView(selectedDoc);
    }

    /**
     * Liefert das aktuelle selektierte Modell
     *
     * @return
     */
    GDCollection getSelectedGDCollection() {
        int collectionCount = collections.size();
        if (collectionCount == 0) {
            return null;
        }
        return collections.get(collectionCount - 1);
    }

    /**
     * Liefert das Modell, das vor dem aktuell selektierten Modell selektiert
     * war
     *
     * @return
     */
    GDCollection getPreSelectedGDCollection() {
        int collectionCount = collections.size();
        if (collectionCount < 2) {
            return null;
        }
        return collections.get(collectionCount - 2);
    }

    /**
     * @return das aktive Teilmodell
     * @see #setSelectedDoc(GraphDocument)
     */
    LGMGraphDocument getSelectedDoc() {
        GDCollection gdcoll = getSelectedGDCollection();
        if (gdcoll == null) {
            return null;
        }
        return gdcoll.getSelectedDoc();
    }

    /**
     * @return Hauptmodell des aktuell selektierten Teilmodells
     */
    LGMGraphDocument getSelectedMainDoc() {
        GDCollection gdcoll = getSelectedGDCollection();
        if (gdcoll == null) {
            return null;
        }
        return gdcoll.getMainDoc();
    }

    /**
     * Wechselt den Kontext auf das übergebene Teilmodell. In jedem Fall wird
     * der <code>ModelBrowser</code> des aktivierten Teilmodells in den
     * Vordergrund gebracht.
     *
     * @param doc Teilmodell, in dessen Kontext gewechselt werden soll
     */
    void setSelectedDoc(final GraphDocument doc) {
        if (doc != null) {
            //das zu aktivierende Graphdocument und dessen Collection an die richtige Position bringen
            GDCollection gdcoll = doc.getCollection();
            if (gdcoll.getModelCategory() == ModelCategory.REGULAR) {
                //die Collection des übergebene doc als letzte in die Collection-Liste bringen
                boolean removed = collections.remove(gdcoll); // nur wenn das Ding da drin war, kommt true zurück. Template-Modelle dürfen hier nicht gesetzt werden -> nur hinzufügen, wenn es auch vorher drin war
                if (removed) {
                    collections.add(gdcoll);
                }
                //das aktive doc in der Collection selbst setzen
                gdcoll.setSelectedDoc(doc);
            }
        }
    }

    /**
     * Checks if at least one model with the given metamodel context is open
     *
     * @param metaModelContext
     * @return
     */
    public boolean hasOpenModels(final MetaModelContext metaModelContext) {
        for (GDCollection gdcoll : collections) {
            if (gdcoll.getMetaModelContext().equals(metaModelContext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Fragt den Benutzer, ob ein geändertes Modell gepsiechert werden soll.
     *
     * @param gdcoll
     * @return
     */
    private boolean askUserCloseModel(final GDCollection gdcoll) {
        if (!gdcoll.isChanged()) {
            return true;
        }
        Object[] buttons = new Object[] {
                getResString("yes"), getResString("no"), getResString("cancel")
        };
        File file = gdcoll.getFile();
        int answer = JOptionPane.showOptionDialog(getMainFrame(), getResString("speicherfrage") + "\n" + (file == null ? gdcoll.getName() : file.getName()), getResString("tool3lgm"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                buttons, null);

        if (answer == JOptionPane.YES_OPTION) {
            boolean retVal = fileSave(false);
            if (retVal == false || gdcoll.isChanged()) {
                return false;
            }
            //} else if (answer == JOptionPane.NO_OPTION) {
            //nothing to do...
        } else if (answer == JOptionPane.CANCEL_OPTION) {
            return false;
        }
        return true;
    }

    public boolean fileClose() {
        return fileClose(getSelectedDoc());
    }

    /**
     * close current collection
     */
    public boolean fileClose(GraphDocument selDoc) {
        //das gearde aktive Teilmodell holen
        //wenn keins aktiv ist -> raus
        if (selDoc == null) {
            return false;
        }
        //die Collection des aktiven Teilmodells holen
        GDCollection gdcoll = selDoc.getCollection();
        //das Hauptdokument holen
        selDoc = gdcoll.getMainDoc();

        Static.showProgressDialog(true);
        Static.setProgressDialogTitle("close_model", gdcoll.getName());

        ElementPropertyDialogsContext.closeAllDialogs(selDoc);

        if (!askUserCloseModel(gdcoll)) {
            Static.closeProgressDialog();
            return false;
        }

        //ab hier ist sicher, dass das Modell geschlossen werden soll
        collections.remove(gdcoll);

        gdcoll.simpleRemoveGraphDocuments();

        GDCollectionFileHandler fileHandler = gdcoll.getFileHandler();
        fileHandler.close();
        try {
            UserProperties.addListValue(StringProperty.LAST_USED_MODEL_FILES, fileHandler.getFile().getCanonicalPath());
        } catch (Exception e) {
        }

        distribute(MODEL_CHANGE_MODEL_CLOSED, selDoc);

        System.gc();

        Static.closeProgressDialog();
        return true;
    }

    /**
     * save the model, which have the focus to file
     *
     * @param saveAs, boolean with true if model, is to save with new filename
     * @return boolean with true if save was successful or save was cancelled
     */
    public boolean fileSave(boolean saveAs) {
        //	   long start = System.currentTimeMillis();
        //        if (!LicenseHandler.checkLicenses()) {
        //            return false;
        //        }
        /* GDCollection zum ausgewähtlen Frame */
        GraphDocument selectedDoc = getSelectedDoc();
        if (selectedDoc == null) {
            return false;
        }
        GDCollection gdcoll = selectedDoc.getCollection();
        GDCollectionFileHandler fileHandler = gdcoll.getFileHandler();
        saveAs = fileHandler.isReadOnly() || saveAs;
        if (saveAs) {
            if (!fileHandler.chooseFile()) {
                return true;
            }
        }
        File file = fileHandler.getFile();
        if (file == null) {
            return fileSave(true);
        }
        if (!saveToFile(gdcoll)) {
            return false;
        }
        System.gc();
        //		long end = System.currentTimeMillis();
        //		System.out.println("Time to write file " + datei.getName() + " (" + datei.length() + " Bytes): " + (end - start) + " Milliseconds");
        //
        //		System.err.println();
        //		System.err.println("###########################################################################");
        //		printStatistic(gdCollection, true, true);
        //		System.err.println();
        //		printStatistic(gdCollection, false, true);
        //		System.err.println("###########################################################################");
        return true;
    }

    public static final boolean saveToFile(final GDCollection gdcoll) {
        try {
            GDCollectionFileHandler fileHandler = gdcoll.getFileHandler();
            if (!fileHandler.saveToFile()) {
                return false;
            }
        } catch (Exception exp) {
            Log.show(Log.FATAL, getResString("FehlerAllgemein") + "\n" + exp, exp);
            return false;
        }
        gdcoll.setUnchanged();
        return true;
    }

    /**
     * return all frame components at desktop
     *
     * @return List<ViewPaneFrameComponent>
     */
    public List<ViewPaneFrameComponent> getAllFrames() {
        return mainFrame.getAllFrames();
    }

    /**
     * @return Returns the active frame component
     */
    public ViewPaneFrameComponent getActiveFrame() {
        return mainFrame.getActiveFrame();
    }

    /**
     * @return numbers of GDCollections in ArrayList collections
     */
    public int getCollectionCount() {
        return collections.size();
    }

    /**
     * return GDCollection with the specified index in ArrayList collections
     *
     * @param index int with index of collection in ArrayList collection
     * @return null if index < 0 or index >= collections.size(); otherwise the
     *         GDCollection with specified index
     */
    public GDCollection getCollection(final int index) {
        return index < 0 || index >= collections.size() ? null : (GDCollection) collections.get(index);
    }

    /**
     * @return Kopie der Liste der <code>GDCollection</code>s
     */
    public Iterable<GDCollection> iterableCollections() {
        return CollectionUtils.iterable(collections);
    }

    /**
     * @param doc
     */
    public void changeToLinked(final GraphDocument doc) {
        ModelElement me = doc.getLastSelected().getElement();
        if (!(me instanceof Node)) {
            return;
        }
        GraphViewPaneFrameComponent frame = null;
        List<ViewPaneFrameComponent> frames = getAllFrames();
        for (int i = 0; i < frames.size(); i++) {
            ViewPaneFrameComponent viewPaneFrameComponent = frames.get(i);
            if (viewPaneFrameComponent instanceof GraphViewPaneFrameComponent) {
                GraphViewPaneFrameComponent f = (GraphViewPaneFrameComponent) viewPaneFrameComponent;
                GraphDocument cd = f.getGraphDocument();
                if (cd instanceof Szenario && ((Szenario) cd).getID().equals(me.getAssociatedSzenID())) {
                    frame = f;
                }
            }
        }

        String hyperlink = me.getHyperlink();

        if (frame != null) {
            if (hyperlink != null) {
                Object[] buttons = new Object[] {
                        getResString("hyperlink"), getResString("submodel"), getResString("cancel")
                };
                int value = JOptionPane.showOptionDialog(null, getResString("link_oder_szen_frage"), getResString("tool3lgm"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[2]);
                if (value == JOptionPane.YES_OPTION) {
                    BrowseUtils.browse(hyperlink);
                } else if (value == JOptionPane.NO_OPTION) {
                    frame.setSelected();
                }
            } else {
                frame.setSelected();
            }
        } else if (hyperlink != null) {
            BrowseUtils.browse(hyperlink);
        }
    }

    /**
     * @param command
     * @param params
     */
    public void processCommand(final String command, final String[] params) {
        mainFrame.setState(Frame.NORMAL);
        mainFrame.toFront();
        if (command == null) {
            return;
        }
        if (command.equalsIgnoreCase("open")) {
            if (params == null || params.length < 1) {
                return;
            }

            // Leerzeichen, die in einer Dateiangabe vorkommen dürfen nicht als
            // Trennzeichen interpretiert werden
            StringBuilder path = new StringBuilder();
            outerLoop: for (int i = 0; i < params.length; i++) {
                path.append(params[i]);
                File file = new File(path.toString());
                if (file.length() != 0) {
                    for (int j = 0; j < getCollectionCount(); j++) {
                        GDCollection gdcoll = getCollection(j);
                        if (gdcoll.getFile().equals(file)) {
                            continue outerLoop;
                        }
                    }
                    openModelFile(file);
                    path.setLength(0);
                } else {
                    path.append(' ');
                }
            }
        } else if (command.equalsIgnoreCase("selectSubmodel")) {
            if (params == null || params.length < 1) {
                return;
            }
            List<ViewPaneFrameComponent> frames = getAllFrames();
            for (ViewPaneFrameComponent frame : frames) {
                if (frame.getGraphDocument().getID().equalsIgnoreCase(params[0])) {
                    frame.setSelected();
                }
            }
        } else if (command.equalsIgnoreCase("select")) {
            if (params == null || params.length < 1) {
                return;
            }
            LGMGraphDocument selectedMainDoc = getSelectedMainDoc();
            selectedMainDoc.start_transaction(STANDARD_PID, false);
            selectedMainDoc.deselectAll(true);
            for (int i = 0; i < params.length; i++) {
                ElementContainer ec = selectedMainDoc.findContainerCoded(params[i]);
                if (ec != null) {
                    selectedMainDoc.addToSelection(ec, STANDARD_PID);
                }
            }
            selectedMainDoc.finish_transaction(STANDARD_PID, false);
            selectedMainDoc.distributeEvent(SELECTION_CHANGED, STANDARD_PID);
        } else if (command.equalsIgnoreCase("selectByUserField")) {
            if (params == null || params.length < 2) {
                return;
            }
            LGMGraphDocument selectedMainDoc = getSelectedMainDoc();
            if (selectedMainDoc == null) {
                return;
            }
            selectedMainDoc.start_transaction(STANDARD_PID, false);
            selectedMainDoc.deselectAll(true);
            for (int i = 1; i < params.length; i++) {
                ElementContainer ec = selectedMainDoc.findElementWithUserField(params[0], params[i]).getContainer(selectedMainDoc);
                if (ec != null) {
                    selectedMainDoc.addToSelection(ec, STANDARD_PID);
                }
            }
            selectedMainDoc.finish_transaction(STANDARD_PID, false);
            selectedMainDoc.distributeEvent(SELECTION_CHANGED, STANDARD_PID);
        } else {
            String[] newParams = new String[params.length + 1];
            newParams[0] = command;
            System.arraycopy(params, 0, newParams, 1, params.length);
            processCommand("open", newParams);
        }
    }

}