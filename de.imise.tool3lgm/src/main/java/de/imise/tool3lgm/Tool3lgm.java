package de.imise.tool3lgm;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.help.CSH;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Tool3lgmConstants.FileFilterType;
import de.imise.tool3lgm.graphtools.consistency.ConsistencyChecker;
import de.imise.tool3lgm.graphtools.consistency.ModelCleaner;
import de.imise.tool3lgm.graphtools.dialog.ElemenPropertyDialogsContext;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionChangeType;
import de.imise.tool3lgm.graphtools.model.GDCollectionFileHandler;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentListener;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.browser.ModelBrowserPanel;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.gui.AbstractInternalFrame;
import de.imise.tool3lgm.gui.InternalGraphFrame;
import de.imise.tool3lgm.gui.MainFrameContentPane;
import de.imise.tool3lgm.gui.menu.MenuBar;
import de.imise.tool3lgm.help.Help;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.StringProperty;
import de.imise.util.BrowseUtils;
import de.imise.util.robot.ScreenRobot;
import de.imise.util.swing.dialog.ExtendedFileChooser;

/** Hauptfenster der Anwendung 3lgm */
public class Tool3lgm extends JFrame implements WindowListener, GraphDocumentListener {

    /** Menü-Leiste des Tools */
    private final MenuBar menuBar;

    /** Pane, das in das ContentPane dieses Frames gelegt wird */
    private final MainFrameContentPane contentPane;

    /** alle GDCollections */
    private final List<GDCollection> collections = new ArrayList<>();

    /**
     * Postion, an der etwas passiert ist. Diese Position wird z. B. gesetzt, wenn der Benutzer irgendwohin mit der Maus klickt, um an
     * der entsprechenden Stelle einen Dialog auf gehen zu lassen.
     */
    private static Point lastActionPosition = null;

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
        setIconImage(Tool3lgmConstants.getIcon("toolIcon.gif").getImage());
        setTitle(null);

        //den Hauptframe in die Mitte setzen
        //setLocationRelativeTo(null);
        //den Hauptframe initialisieren, damit die JOption-Panes der Lizenzanfrage an der richtigen Stelle sind.
        //        setVisible(true);
        //        LicenseHandler.checkLicenses();
        //        setVisible(false);
        //        setLocation(0, 0);

        //Rechteck, auf dem Screen bestimmen, Fenster maximal einnehmen können
        Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        Dimension screenSize = new Dimension(maxBounds.width, maxBounds.height);
        setSize(screenSize);

        contentPane = new MainFrameContentPane();
        getContentPane().add(contentPane);

        addWindowListener(this);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // Direkthilfe einschalten
        Help.getHelp().enableHelpKey(rootPane, "willkommen");

        menuBar = new MenuBar();
        CSH.setHelpIDString(menuBar, "uebersicht_menueleiste");
        KeyStrokes.registerPublicKeyStrokes(getRootPane());
        setJMenuBar(menuBar);
        setVisible(visible);
        contentPane.selectedDocChanged();
    }

    /**
     * Liefert die Postion, an der etwas passiert ist. Diese Position wird z. B. gesetzt, wenn der Benutzer irgendwohin mit der Maus
     * klickt, um an der entsprechenden Stelle einen Dialog auf gehen zu lassen.
     *
     * @return
     */
    public static final Point getLastActionPosition() {
        return lastActionPosition;
    }

    /**
     * Setzt die Postion, an der etwas passiert ist. Diese Position wird z. B. gesetzt, wenn der Benutzer irgendwohin mit der Maus
     * klickt, um an der entsprechenden Stelle einen Dialog auf gehen zu lassen.
     *
     * @param x
     * @param y
     */
    public static final void setLastActionPosition(final int x, final int y) {
        Tool3lgm.lastActionPosition = new Point(x, y);
    }

    @Override
    public void setTitle(final String metaModelName) {
        String title = getResString("tool3lgm");
        if (!Strings.isNullOrEmpty(metaModelName)) {
            title += " " + getResString("tool3lgm_title_extension") + " " + metaModelName;
        }
        super.setTitle(title);
    }

    @Override
    public void setCursor(final Cursor cursor) {
        contentPane.setCursor(cursor);
        super.setCursor(cursor);
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
                if (JOptionPane.showConfirmDialog(this, getResString("datei_gesperrt"), "", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return null;
                }
            }
            Static.showProgressDialog(true);
            Static.setProgressDialogTitle(getResString("load_model") + " " + file.getName());
            Static.setProgressDialogStatusLabel("read_progress");
            update(getGraphics());
            boolean retVal = fileHandler.loadFromRAF();
            return retVal ? gdcoll : null;
        } catch (Exception e) {
            Log.show(Log.FATAL, getResString("FehlerAllgemein"), e);
            Object[] buttons = new Object[] {
                    getResString("ok")
            };
            JOptionPane.showOptionDialog(this, getResString("oeffnenfehler") + "\n" + file.getPath() + "\n" + e.getMessage(), getResString("tool3lgm"), JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, buttons, null);
            Static.closeProgressDialog();
            return null;
        }
    }

    /**
     * lädt ein 3LGM²-Dokument (legt die Teilmodell-Fenster an, aktualisiert das Fenster Menu und ModelBrowser)
     */
    public boolean createNewModel() {
        MetaModelContext choosedMetaModelContext = Tool3lgmMetaModelContext.getNewModelMetaModelContext();
        if (choosedMetaModelContext == null) {
            return false;
        }
        return openModel(null, choosedMetaModelContext);
    }

    /**
     * Lädt eine Modell-Datei
     *
     * @param file die zu ladende Datei
     * @return
     */
    public boolean openModelFile(File file) {
        file = chooseModelFile(file);
        if (file != null && !file.isDirectory()) {
            try {
                return openModel(file, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private File chooseModelFile(File file) {
        if (file != null) {
            if (!file.isDirectory()) {
                return file;
            }
        }
        if (file == null || !file.isDirectory()) {
            file = UserProperties.getWorkingDirectory();
        }

        ExtendedFileChooser chooser = new ExtendedFileChooser(null, file);
        chooser.setMultiSelectionEnabled(false);
        FileNameExtensionFilter[] lgmFileFilter = Tool3lgmConstants.getFileNameExtensionFilters(FileFilterType.LGM3, FileFilterType.LGM3_ZIP, FileFilterType.LGM3_UNZIPPED);
        int chooserAnswer = chooser.showOpenDialog(this, false, lgmFileFilter);
        UserProperties.setWorkingDirectory(chooser.getCurrentDirectory());
        if (chooserAnswer == ExtendedFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            chooser.setVisible(false);
            return file;
        }
        return null;
    }

    /**
     * Legt ein neues Modell an oder lädt ein bestehendes aus einer Datei.
     *
     * @param file
     *            zu ladende Datei. Wenn <code>null</code> übergeben wird, wird eine neue Datei angelegt.
     * @param metaModelContext
     *            MetaModelContext der zu öffnenden Datei oder des neu anzulegenden Modells
     * @return <code>true</code>, wenn die Datei geöffnet werden konnte oder ein neues Modell angelegt wurde
     */
    private boolean openModel(final File file, final MetaModelContext metaModelContext) {
        GDCollection gdcoll;
        if (file == null) {
            gdcoll = new GDCollection(metaModelContext);
            gdcoll.createSzenario();
        } else {
            gdcoll = loadFile(file);
            if (gdcoll == null) {
                Static.closeProgressDialog();
                return false;
            }
        }
        UserProperties.setWorkingDirectory(file);
        return openModel(gdcoll);
    }

    /**
     * Legt ein neues Modell an oder lädt ein bestehendes aus einer Datei.
     *
     * @param file
     *            zu ladende Datei. Wenn <code>null</code> übergeben wird, wird eine neue Datei angelegt.
     * @param metaModelContext
     *            MetaModelContext der zu öffnenden Datei oder des neu anzulegenden Modells
     * @return <code>true</code>, wenn die Datei geöffnet werden konnte oder ein neues Modell angelegt wurde
     */
    public boolean openModel(final GDCollection gdcoll) {
        Static.setProgressDialogStatusLabel("finish_progress");
        //TODO: das hier sollte ein PropertyChange sein, so dass das ContentPane als Listener darauf reagieren kann und nicht direkt die Funktion an das darin enthaltene WorkArea weiter leiten muss
        contentPane.addCollection(gdcoll);

        collections.add(gdcoll);
        gdcoll.addGraphDocumentListener(this);

        Static.setProgressDialogStatusLabel("create_frame", gdcoll.getMainGraphDocument().getTitle());
        createFrame(gdcoll.getMainGraphDocument());

        LGMGraphDocument selectedDoc = gdcoll.getMainGraphDocument();
        AbstractInternalFrame lastFrame = null;
        for (int i = 0; i < gdcoll.getSzenarioCount(); i++) {
            Szenario szen = gdcoll.getSzenario(i);
            Static.setProgressDialogStatusLabel("create_frame", szen.getTitle());
            if (i == 0) {
                selectedDoc = szen;
            }
            if (szen.getViewParameter().selected) {
                selectedDoc = szen;
            }
            lastFrame = createFrame(szen);
        }

        //vor dem Selektieren des aktuellen Teilmodells alle nicht behebbaren Fehler löschen
        ConsistencyChecker.clearUnfixableErrors(gdcoll);
        setSelectedDoc(selectedDoc);
        gdcoll.setChanged(false);
        System.gc();
        Static.closeProgressDialog();

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

        //Dieser Spass hier dient nur dazu, nach dem Öffnen einer Modelldatei nochmal ein
        //neu Zeichnen auszulösen, was nur mit einem Klick in den Frame zuverlässig passiert
        //Erst dadurch fällt der Swing-Bug mit der am Anfang nicht korrekt positionierten
        //Schrift nicht mehr auf.
        Point location = MouseInfo.getPointerInfo().getLocation();
        if (lastFrame != null && lastFrame.isVisible()) {
            Point locationOnScreen = lastFrame.getLocationOnScreen();
            Dimension size = lastFrame.getSize();
            ScreenRobot.setMouse(locationOnScreen.x + size.width / 2, locationOnScreen.y + size.height / 2);
            ScreenRobot.click();
        }
        ScreenRobot.setMouse(location);
        return true;
    }

    /**
     * @param doc
     * @return
     */
    public AbstractInternalFrame createFrame(final GraphDocument doc) {
        //TODO: das hier sollte von außen nicht augerufen werden, sondern das sollte über den (oder einen neuen anderen) GraphDocumentListener laufen (SZENARIO_ADDED)
        return contentPane.createFrame(doc);
    }

    /**
     *
     */
    public void openMatrixView() {
        contentPane.createTableInternalFrame(getSelectedDoc());
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
     * Liefert das Modell, das vor dem aktuell selektierten Modell selektiert war
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
     * Liefert das aktive Teilmodell
     *
     * @return selektiertes Teilmodell
     */
    LGMGraphDocument getSelectedDoc() {
        GDCollection gdcoll = getSelectedGDCollection();
        if (gdcoll == null) {
            return null;
        }
        return gdcoll.getSelectedDoc();
    }

    /**
     * Über diese Variable wird beim Schließen eines Modells die Selektion
     * der einzelnen Teilmodelle verhindert.
     */
    private boolean ignoreDocSelection = false;

    /**
     * Wechselt den Kontext auf das übergebene Teilmodell. In jedem Fall wird der <code>ModelBrowser</code> des aktivierten Teilmodells in den
     * Vordergrund gebracht.
     *
     * @param doc
     *            Teilmodell, in dessen Kontext gewechselt werden soll
     * @param activateGraphView
     *            Wenn <code>true</code> ist, wird auch das dazugehörige
     *            Grafikfenster in den Vordergrund geholt, sonst nicht.
     */
    void setSelectedDoc(final GraphDocument doc) {
        if (ignoreDocSelection) {
            return;
        }
        if (doc != null) {
            //das zu aktivierende Graphdocument und dessen Collection an die richtige Position bringen
            GDCollection gdcoll = doc.getCollection();
            //die Collection des übergebenne doc als letzte in die Collection-Liste bringen
            collections.remove(gdcoll);
            collections.add(gdcoll);
            //das aktive doc in der Collection selbst setzen
            gdcoll.setActiveGraphDocument(doc);
        }
        contentPane.setSelectedDoc(doc, doc != null);
    }

    /**
     * ordnet alle InternalFrames neu an (überlappt)
     */
    public void reorderFramesWithOverlap() {
        contentPane.reorderFramesWithOverlap();
    }

    /**
     * ordnet alle InternalFrames neu an (nebeneinander)
     */
    public void reorderFramesSideBySide() {
        contentPane.reorderFramesSideBySide();
    }

    //TODO: das hier sollte von außen nicht augerufen werden, sondern das sollte über den (oder einen neuen anderen) GraphDocumentListener laufen (SZENARIO_REMOVED)
    /**
     * @param szen
     */
    public void closeFrame(final GraphDocument szen) {
        contentPane.closeFrame(szen);
    }

    //TODO: das hier sollte von außen nicht augerufen werden, sondern das sollte über den (oder einen neuen anderen) GraphDocumentListener laufen (MODEL_CLOSED) (analog sollte es dann auch MODEL_OPENED geben)
    /**
     * @param gdcoll
     */
    public void closeAllFramesAndTabs(final GDCollection gdcoll) {
        contentPane.closeAllFramesAndTabs(gdcoll);
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
        int answer = JOptionPane.showOptionDialog(this, getResString("speicherfrage") + "\n" + (file == null ? gdcoll.getName() : file.getName()), getResString("tool3lgm"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons,
                null);

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
        selDoc = gdcoll.getMainGraphDocument();

        Static.showProgressDialog(true);
        Static.setProgressDialogTitle(getResString("close_model") + " " + gdcoll.getName());

        ElemenPropertyDialogsContext.closeAllDialogs(selDoc);

        if (!askUserCloseModel(gdcoll)) {
            Static.closeProgressDialog();
            return false;
        }

        //ab hier ist sicher, dass das Modell geschlossen werden soll
        gdcoll.simpleRemoveGraphDocuments();

        if (gdcoll.descriptionFrame != null) {
            gdcoll.descriptionFrame.dispose();
        }

        ignoreDocSelection = true;

        closeAllFramesAndTabs(gdcoll);

        ignoreDocSelection = false;
        setSelectedDoc(ModelBrowserPanel.getSelectedDoc());

        gdcoll.removeGraphDocumentListener(this);

        GDCollectionFileHandler fileHandler = gdcoll.getFileHandler();
        fileHandler.close();
        try {
            UserProperties.addListValue(StringProperty.LAST_USED_MODEL_FILES, fileHandler.getFile().getCanonicalPath());
        } catch (Exception e) {
        }

        collections.remove(gdcoll);

        System.gc();

        Static.closeProgressDialog();
        contentPane.selectLastFrame();
        return true;
    }

    /**
     * save the model, which have the focus to file
     *
     * @param saveAs,
     *            boolean with true if model, is to save with new filename
     * @return boolean with true if save was successful or save was cancelled
     */
    public boolean fileSave(boolean saveAs) {
        //	   long start = System.currentTimeMillis();
        //        if (!LicenseHandler.checkLicenses()) {
        //            return false;
        //        }
        /* GDCollection zum ausgewähtlen Frame */
        GraphDocument doc = getSelectedDoc();
        if (doc == null) {
            return false;
        }
        GDCollection gdcoll = doc.getCollection();
        GDCollectionFileHandler fileHandler = gdcoll.getFileHandler();
        saveAs = fileHandler.isReadOnly() || saveAs;
        if (saveAs) {
            if (!fileHandler.chooseFile()) {
                return true;
            }
        }
        File datei = fileHandler.getFile();
        if (datei == null) {
            return fileSave(true);
        }
        new ModelCleaner(gdcoll).cleanModel();
        if (!saveToFile(gdcoll)) {
            return false;
        }
        contentPane.updateFrameTitles();
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
        gdcoll.setChanged(false);
        return true;
    }

    //TODO: auch das hier sollte ziemlich sicher über einen Listener laufen!
    /**
     * Reagiert auf ein umbenanntes Modell
     *
     * @param gdcoll
     */
    public void modelRenamed(final GDCollection gdcoll) {
        contentPane.modelRenamed(gdcoll);
    }

    //TODO: auch das hier sollte ziemlich sicher über einen Listener laufen!
    /**
     * reagiert auf ein umbenanntes Szenario
     *
     * @param szen
     */
    public void szenarioRenamed(final Szenario szen) {
        contentPane.szenarioRenamed(szen);
    }

    @Override
    public void windowClosing(final WindowEvent e) {
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
        new File(Tool3lgmConstants.CLIPBOARD_PATH).delete();

        File temp = new File(Tool3lgmConstants.TEMP_PATH + "temp_3lgm_export_file.html");
        if (temp.exists()) {
            temp.delete();
        }

        UserProperties.save();
        System.exit(0);
    }

    @Override
    public void windowActivated(final WindowEvent e) {
        Static.contextGenerator.changeContext(getSelectedDoc());
    }

    @Override
    public void windowClosed(final WindowEvent e) {
    }

    @Override
    public void windowDeactivated(final WindowEvent e) {
    }

    @Override
    public void windowDeiconified(final WindowEvent e) {
    }

    @Override
    public void windowIconified(final WindowEvent e) {
    }

    @Override
    public void windowOpened(final WindowEvent e) {
    }

    @Override
    public void activeLayerChanged(final GraphDocument source) {
        source.getCollection().setChanged(true);
    }

    @Override
    public void dataChanged(final GraphDocument source) {
        source.getCollection().setChanged(true);
    }

    @Override
    public void elementGraphicsChanged(final GraphDocument source, final ElementContainer element) {
        source.getCollection().setChanged(true);
        repaint();
    }

    @Override
    public void layoutChanged(final GraphDocument source) {
        source.getCollection().setChanged(true);
        repaint();
    }

    @Override
    public void groupOrderChanged(final GraphDocument source) {
        source.getCollection().setChanged(true);
    }

    @Override
    public void colorsChanged(final GraphDocument source) {
        source.getCollection().setChanged(true);
    }

    @Override
    public void selectionChanged(final GraphDocument source) {
    }

    /**
     * return all InternalFrames at desktop
     *
     * @return JInternalFrame[]
     */
    public AbstractInternalFrame[] getAllFrames() {
        return contentPane.getAllFrames();
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
     * @return null if index < 0 or index >= collections.size(); otherwise the GDCollection with specified index
     */
    public GDCollection getCollection(final int index) {
        return index < 0 || index >= collections.size() ? null : (GDCollection) collections.get(index);
    }

    /**
     * @return Kopie der Liste der <code>GDCollection</code>s
     */
    public List<GDCollection> getCollections() {
        return new ArrayList<>(collections);
    }

    /**
     * @param doc
     */
    public void changeToLinked(final GraphDocument doc) {
        ModelElement me = doc.getLastSelected().getElement();
        if (!(me instanceof Node)) {
            return;
        }
        InternalGraphFrame frame = null;
        JInternalFrame[] frames = getAllFrames();
        for (int i = 0; i < frames.length; i++) {
            if (frames[i] instanceof InternalGraphFrame) {
                InternalGraphFrame f = (InternalGraphFrame) frames[i];
                GraphDocument cd = f.getGraphDocument();
                if (cd instanceof Szenario && ((Szenario) cd).getHashString().equals(me.getAssociatedDoc())) {
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
                    try {
                        frame.setSelected(true);
                    } catch (PropertyVetoException ex) {
                        Log.show(Log.ERROR, getResString("FehlerAllgemein"), ex);
                    }
                }
            } else {
                try {
                    frame.setSelected(true);
                } catch (PropertyVetoException ex) {
                    Log.show(Log.ERROR, getResString("FehlerAllgemein"), ex);
                }
            }
        } else if (hyperlink != null) {
            BrowseUtils.browse(hyperlink);
        }
    }

    /**
     * @return Returns the activeFrame.
     */
    public AbstractInternalFrame getActiveFrame() {
        return contentPane.getActiveFrame();
    }

    /**
     * @param command
     * @param params
     */
    public void processCommand(final String command, final String[] params) {
        setState(Frame.NORMAL);
        toFront();
        if (command == null) {
            return;
        } else if (command.equalsIgnoreCase("open")) {
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
            JInternalFrame[] frames = getAllFrames();
            for (int i = 0; i < frames.length; i++) {
                if (frames[i] instanceof AbstractInternalFrame) {
                    if (((AbstractInternalFrame) frames[i]).getGraphDocument().getHashString().equalsIgnoreCase(params[0])) {
                        try {
                            frames[i].setSelected(true);
                        } catch (PropertyVetoException e) {
                        }
                    }
                }
            }
        } else if (command.equalsIgnoreCase("select")) {
            if (params == null || params.length < 1) {
                return;
            }
            GraphDocument doc = getSelectedDoc().getCollection().getMainGraphDocument();
            if (doc == null) {
                return;
            }
            final int PID = TransactionManager.STANDARD_PID;
            doc.start_transaction(PID, false);
            doc.deselectAll(true);
            for (int i = 0; i < params.length; i++) {
                ElementContainer ec = doc.findContainerCoded(params[i]);
                if (ec != null) {
                    doc.addToSelection(ec, PID);
                }
            }
            doc.finish_transaction(PID, false);
            doc.distributeEvent(GDCollectionChangeType.SELECTION_CHANGED, PID);
        } else if (command.equalsIgnoreCase("selectByUserField")) {
            if (params == null || params.length < 2) {
                return;
            }
            GraphDocument doc = getSelectedDoc().getCollection().getMainGraphDocument();
            if (doc == null) {
                return;
            }
            final int PID = TransactionManager.STANDARD_PID;
            doc.start_transaction(PID, false);
            doc.deselectAll(true);
            for (int i = 1; i < params.length; i++) {
                ElementContainer ec = doc.findElementWithUserField(params[0], params[i]).getContainer(doc);
                if (ec != null) {
                    doc.addToSelection(ec, PID);
                }
            }
            doc.finish_transaction(PID, false);
            doc.distributeEvent(GDCollectionChangeType.SELECTION_CHANGED, PID);
        } else {
            String[] newParams = new String[params.length + 1];
            newParams[0] = command;
            System.arraycopy(params, 0, newParams, 1, params.length);
            processCommand("open", newParams);
        }
    }

}