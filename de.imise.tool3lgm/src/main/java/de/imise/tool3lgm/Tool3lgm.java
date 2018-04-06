package de.imise.tool3lgm;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.help.CSH;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.imise.tool3lgm.Tool3lgmConstants.FileFilterType;
import de.imise.tool3lgm.graphtools.consistency.ConsistencyChecker;
import de.imise.tool3lgm.graphtools.consistency.ModelCleaner;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.matrixview.MatrixViewInternalFrame;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
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
import de.imise.tool3lgm.graphtools.userfield.UserfieldResourceHandler;
import de.imise.tool3lgm.graphtools.view.browser.ModelBrowserPanel;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.ViewParameter;
import de.imise.tool3lgm.gui.AbstractInternalFrame;
import de.imise.tool3lgm.gui.GraphAreaToolbarManager;
import de.imise.tool3lgm.gui.InternalGraphFrame;
import de.imise.tool3lgm.gui.StatusBar;
import de.imise.tool3lgm.gui.ToolBar;
import de.imise.tool3lgm.gui.menu.ContextGenerator;
import de.imise.tool3lgm.gui.menu.MenuBar;
import de.imise.tool3lgm.help.Help;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.tools.BrowseUtils;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.tool3lgm.userproperties.UserProperties.StringProperty;
import de.imise.util.swing.dialog.ExtendedFileChooser;

/** Hauptklasse der Anwendung 3lgm */
public class Tool3lgm extends JFrame implements WindowListener, InternalFrameListener, GraphDocumentListener, PropertyChangeListener {

    //Als allerstes muss aus der Main-Funktion ausßerhalb dieser Klasse diese init()-Funktion
    //aufgerufen werden, damit alle statischen Elemente einmal initialisert werden. Diese Funktion
    //muss unbedingt ganz oben stehen!
    public static final void init() {
    }

    /** Panel with verticalSplitPane and werkzeugleiste */
    private final JPanel workarea = new JPanel();

    /** splitted pane with modelBrowserPanel on the left and desktop on the right */
    private final JSplitPane verticalSplitPane;

    /** splitted pane with modelBrowserPanel and the graph on the top and the error table bottom */
    private JSplitPane horizontalSplitPane;

    /** panel to hold one or more modelBrowsers */
    private final ModelBrowserPanel modelBrowserPanel;

    /** Menü-Leiste des Tools */
    private static MenuBar menuBar;

    /** ToolBar with general tools */
    private final ToolBar toolbar;

    private final GraphAreaToolbarManager graphAreaToolbarManager = new GraphAreaToolbarManager(workarea);

    /** contain all windows of opened documents (JDesktopPane is a container used to create a multiple-document interface or a virtual desktop) */
    private final JDesktopPane desktop;

    /** InternalFrame in desktop, which has the focus */
    private AbstractInternalFrame activeFrame = null;

    /** alle GDCollections */
    private final List<GDCollection> collections = new ArrayList<>();

    /** Position of divider betweeen the tree and the graph view in pixel from the left side */
    int dividerLocation = 200;

    /** Holds the actual context and generates context menus */
    public static ContextGenerator contextGenerator;

    /** Checks the consistency of a model */
    private ConsistencyChecker consistencyChecker;

    /**
     * Postion, an der etwas passiert ist. Diese Position wird z. B. gesetzt, wenn der Benutzer irgendwohin mit der Maus klickt, um an
     * der entsprechenden Stelle einen Dialog auf gehen zu lassen.
     */
    private static Point lastActionPosition = null;

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

        //den Hauptframe in die Mitte setzen
        //setLocationRelativeTo(null);
        setTitle(getResString("tool3lgm"));
        //den Hauptframe initialisieren, damit die JOption-Panes der Lizenzanfrage an der richtigen Stelle sind.
        //        setVisible(true);
        //        LicenseHandler.checkLicenses();
        //        setVisible(false);
        //        setLocation(0, 0);

        contextGenerator = new ContextGenerator();
        modelBrowserPanel = new ModelBrowserPanel();

        //Rechteck, auf dem Screen bestimmen, Fenster maximal einnehmen können
        Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        Dimension screenSize = new Dimension(maxBounds.width, maxBounds.height);
        // TODO:____###### Größenänderung des Tools
        //        screenSize.height -= 400;
        //        screenSize.width -= 50;
        setSize(screenSize);

        //Arbeitsfläche mit SplitPane (rechts JDesktopPane für InternalFrame,
        // links ModelBrowser)
        desktop = new JDesktopPane();
        getContentPane().setLayout(new BorderLayout());
        workarea.setLayout(new BorderLayout());

        //Toolbar defienieren nicht verschiebbar
        toolbar = new ToolBar();

        getContentPane().add(toolbar, BorderLayout.NORTH);
        getContentPane().add(workarea, BorderLayout.CENTER);
        getContentPane().add(new StatusBar(), BorderLayout.SOUTH);

        JScrollPane desktopscroll = new JScrollPane(desktop);
        verticalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, modelBrowserPanel, desktopscroll);
        verticalSplitPane.setOneTouchExpandable(true);
        verticalSplitPane.setDividerSize(10);
        verticalSplitPane.setDividerLocation(dividerLocation);

        setCheckConsistencyState(UserProperties.is(BooleanProperty.OPTION_CHECK_CONSISTENCY));
        UserProperties.addPropertyChangeListener(this);

        addWindowListener(this);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // Direkthilfe einschalten
        Help.getHelp().enableHelpKey(rootPane, "willkommen");
        // Direkthilfe für die einzelnen Baukastenteile
        CSH.setHelpIDString(modelBrowserPanel, "uebersicht_modellbrowser");

        menuBar = new MenuBar();
        CSH.setHelpIDString(menuBar, "uebersicht_menueleiste");
        KeyStrokes.registerPublicKeyStrokes(getRootPane());
        setJMenuBar(menuBar);

        setShowStandardToolbar(UserProperties.is(BooleanProperty.OPTION_SHOW_STANDARD_TOOLBAR));
        showModelBrowser(UserProperties.is(BooleanProperty.OPTION_MODEL_BROWSER_SHOW));

        setVisible(visible);
        toolbar.selectedDocChanged();

    }

    /**
     * @return instance of ContextGenerator
     */
    public static ContextGenerator getContextGenerator() {
        return contextGenerator;
    }

    @Override
    public void setCursor(final Cursor cursor) {
        if (activeFrame != null) {
            activeFrame.setCursor(cursor);
        }
        super.setCursor(cursor);
    }

    /**
     * @param file
     * @param gdcoll
     * @return
     */
    private boolean loadFile(final File file, final GDCollection gdcoll) {
        try {
            GDCollectionFileHandler fileHandler = gdcoll.getFileHandler();
            if (!fileHandler.setFile(file)) {
                if (JOptionPane.showConfirmDialog(this, getResString("datei_gesperrt"), "", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return false;
                }
            }
            Static.showProgressDialog(true);
            Static.setProgressDialogTitle(getResString("load_model") + " " + file.getName());
            Static.setProgressDialogStatusLabel("read_progress");
            update(getGraphics());
            boolean retVal = fileHandler.loadFromRAF();
            return retVal;
        } catch (Exception e) {
            Log.show(Log.FATAL, getResString("FehlerAllgemein"), e);
            Object[] buttons = new Object[] {
                    getResString("ok")
            };
            JOptionPane.showOptionDialog(this, getResString("oeffnenfehler") + "\n" + file.getPath() + "\n" + e.getMessage(), getResString("tool3lgm"), JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, buttons, null);
            Static.closeProgressDialog();
            return false;
        }
    }

    /**
     * lädt ein 3LGM²-Dokument (legt die Teilmodell-Fenster an, aktualisiert das Fenster Menu und ModelBrowser)
     */
    public boolean openFile(final boolean open) {
        return openFile(open, null);
    }

    /**
     * Legt ein neues Modell an oder lädt ein bestehendes aus einer Datei.
     *
     * @param open
     *            wenn <code>true</code>, wird ein FileChooser geöffnet, über
     *            den der Benutzer zu ladende Modelldatei auswählen kann. Diese
     *            Option ist aber nue relevant, wenn <code>file</code> <code>null</code> ist.
     * @param file
     *            zu ladende Datei. Wenn <code>null</code> übergeben wird,
     *            wird eine neue Datei angelegt
     * @return die geöffnete Datei
     */
    public boolean openFile(final boolean open, File file) {
        GDCollection gdcoll = new GDCollection();
        //Standard-Userfield-Definition laden
        UserfieldResourceHandler.loadDefaultUserfieldDefinition(gdcoll);
        if (open == false && file == null) {
            gdcoll.createSzenario();
        } else if (file != null) {
            if (!loadFile(file, gdcoll)) {
                Static.closeProgressDialog();
                return false;
            }
        } else if (open == true) {
            ExtendedFileChooser chooser = new ExtendedFileChooser(null, UserProperties.getWorkingDirectory());
            chooser.setMultiSelectionEnabled(false);
            FileNameExtensionFilter[] lgmFileFilter = Tool3lgmConstants.getFileNameExtensionFilters(FileFilterType.LGM3, FileFilterType.LGM3_ZIP, FileFilterType.LGM3_UNZIPPED);
            if (chooser.showOpenDialog(this, false, lgmFileFilter) == ExtendedFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
                Static.showProgressDialog(true);
                UserProperties.setWorkingDirectory(file);
                chooser.setVisible(false);
                if (!loadFile(file, gdcoll)) {
                    Static.closeProgressDialog();
                    return false;
                }
            } else {
                return false;
            }
        }

        Static.setProgressDialogStatusLabel("finish_progress");
        modelBrowserPanel.addCollection(gdcoll);

        collections.add(gdcoll);
        gdcoll.addGraphDocumentListener(this);

        Static.setProgressDialogStatusLabel("create_frame", gdcoll.getMainGraphDocument().getTitle());
        createMainFrame(gdcoll.getMainGraphDocument());

        LGMGraphDocument selectedDoc = gdcoll.getMainGraphDocument();
        for (int i = 0; i < gdcoll.getSzenarioCount(); i++) {
            Szenario szen = gdcoll.getSzenario(i);
            Static.setProgressDialogStatusLabel("create_frame", szen.getTitle());
            if (i == 0) {
                selectedDoc = szen;
            }
            if (szen.getViewParameter().selected) {
                selectedDoc = szen;
            }
            createSzenarioFrame(szen);
        }

        //vor dem Selektieren des aktuellen Teilmodells alle nicht behebbaren Fehler löschen
        ConsistencyChecker.clearUnfixableErrors(gdcoll);
        setSelectedDoc(selectedDoc, true);
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

        return true;
    }

    /**
     * @param maindoc
     * @return
     */
    private AbstractInternalFrame createMainFrame(final LGMGraphDocument maindoc) {
        InputGraphArea area = new InputGraphArea();
        InternalGraphFrame frame = new InternalGraphFrame(desktop, area, maindoc);
        frame.setTitle(maindoc.getCollection().getName() + " - " + maindoc.getTitle());
        modelBrowserPanel.addGraphDocument(maindoc);
        frame.addInternalFrameListener(this);
        Rectangle bounds = desktop.getBounds();
        frame.setBounds(bounds);

        desktop.add(frame);
        frame.setVisible(true);

        return frame;
    }

    /* *** */

    /**
     * @param szenario
     * @return
     */
    public AbstractInternalFrame createSzenarioFrame(final Szenario szenario) {
        InputGraphArea area = new InputGraphArea(szenario);
        InternalGraphFrame frame = new InternalGraphFrame(desktop, area, szenario);
        frame.setTitle(szenario.getCollection().getName() + " - " + szenario.getTitle());
        modelBrowserPanel.addGraphDocument(szenario);
        frame.addInternalFrameListener(this);
        Rectangle bounds = desktop.getBounds();
        bounds.height = bounds.height - 32;
        frame.setBounds(bounds);
        setWorkArea(frame);
        desktop.add(frame);
        frame.setVisible(true);
        return frame;
    }

    /**
     * Create new MatrixViewFrame and add it to parent GraphDocument
     *
     * @author Thomas Rudert
     * @param _graphDocument
     *            parent
     * @return boolean with true, if methode run successful
     */
    public boolean createTableInternalFrame(final LGMGraphDocument _graphDocument) {
        if (_graphDocument == null) {
            return false;
        }
        MatrixViewInternalFrame matrixView = new MatrixViewInternalFrame(_graphDocument);
        String title = _graphDocument.getCollection().getName() + " - " + _graphDocument.getTitle() + " - " + getResString("matrix") + " #";

        matrixView.setTitle(title.concat(String.valueOf(countFramesWithSameTitle(title) + 1)));

        Rectangle bounds = desktop.getBounds();
        bounds.height = bounds.height - 39;
        matrixView.setBounds(bounds);
        desktop.add(matrixView);
        matrixView.addInternalFrameListener(this);
        matrixView.setVisible(true);
        desktop.setSelectedFrame(matrixView);
        return true;
    }

    private int countFramesWithSameTitle(final String title) {
        JInternalFrame[] frames = desktop.getAllFrames();
        int max = 0;
        for (int i = 0; i < frames.length; i++) {
            if (frames[i].getTitle().startsWith(title)) {
                try {
                    int temp = Integer.parseInt(frames[i].getTitle().substring(frames[i].getTitle().lastIndexOf("#") + 1));
                    max = temp > max ? temp : max;
                } catch (Exception e) {
                    Log.show(Log.FATAL, getResString("FehlerAllgemein"), e);
                }
            }
        }
        return max;
    }

    /* *** */

    /**
     * set parameters of InputGraphArea to standard
     *
     * @param InputGraphArea
     *            to set
     */
    private void setWorkArea(final InternalGraphFrame frame) {
        Szenario szenario = (Szenario) frame.getGraphDocument();
        ViewParameter view = szenario.getViewParameter();
        InputGraphArea bgp = frame.getInputGraphArea();
        bgp.setMultiView(view.multiView);
        frame.getGraphDocument().getCollection().setActiveLayer(view.activeLayer);
        bgp.setMultiViewLayerAngle(view.layerAngle);
        bgp.setMultiViewLayerGap(view.layerGap);
        bgp.setZoom(view.zoom);
        frame.getScrollPane().getViewport().setViewPosition(new Point(view.viewPositionX, view.viewPositionY));
        szenario.deleteViewParameter(); // die ViewParameter werden nur für diesen Init gebraucht -> danach können sie gelöscht werden
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
     * @return
     */
    public ConsistencyChecker getConsistencyChecker() {
        if (consistencyChecker == null) {
            GDCollection gdcoll = getSelectedGDCollection();
            if (gdcoll != null) {
                consistencyChecker = new ConsistencyChecker(gdcoll);
            }
        }
        return consistencyChecker;
    }

    /**
     * Diese Variable wird in <code>setSelectedDoc(LGMGraphDocument, boolean)</code> gebraucht,
     * um beim Aktivieren eines Matix-Fensters zwar den dazugehörigen ModelBrowser in den
     * Vordergrund zu bringen (wenn er noch nicht im Vordergrund ist), aber nicht den Grafischen
     * View des Teilmodells, weil ja dann das Matrix-Fenster sofort nicht mehr im Vordergrund
     * wäre.
     */
    private static boolean activateGraphView = true;

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
    void setSelectedDoc(final GraphDocument doc, final boolean activateGraphView) {
        if (ignoreDocSelection) {
            return;
        }
        boolean isCheckConsistency = UserProperties.is(BooleanProperty.OPTION_CHECK_CONSISTENCY);

        //das doc kann null sein, wenn eine Datei geladen wird und das ModelBrowserPanel grade mit den
        //geladenen Teilmodellen gefüllt wird. Im ModelBrowserPanel wird bei jedem Hinzufügen eines
        //Teilmodell-Tabs immer diese Funktion hier aufgerufen.
        //Es kann auch null sein, wenn das letzte Modell geschlossen wurde
        if (doc == null) {
            setCheckConsistencyState(isCheckConsistency);
            toolbar.selectedDocChanged();
            if (consistencyChecker != null && isCheckConsistency) {
                consistencyChecker.changeContext(null);
            }
            return;
        }

        //Die folgenden beiden Zeilen nicht aktivieren. Sie sind auskommentiert
        //stehen geblieben, damit nicht irgendwann mal einer auf die Idee  kommt,
        //über diesen Weg optimieren zu wollen
        //if (doc == oldDoc)
        //return;

        //das zu aktivierende Graphdocument und dessen Collection an die
        // richtige Position bringen
        GDCollection gdcoll = doc.getCollection();
        //die Collection des übergebenne doc als letzte in die Collection-Liste
        // bringen
        collections.remove(gdcoll);
        collections.add(gdcoll);
        //das aktive doc in der Collection selbst setzen
        gdcoll.setActiveGraphDocument(doc);

        //wenn der interne Frame mit dem grafischen View in den Vordergrund geholt werden soll,
        if (activateGraphView) {
            //wenn nicht grade vorher ein Matrix-View aktiviert wurde (nur dann wäre die statische
            //Variable==false)
            if (Tool3lgm.activateGraphView) {
                //den richtigen Frame nach vorne holen
                InternalGraphFrame frame = doc.getFrame();
                if (frame != null) {
                    if (!frame.isSelected()) {
                        try {
                            frame.setSelected(true);
                        } catch (Exception ex) {
                            Log.show(Log.FATAL, getResString("FehlerAllgemein"), ex);
                        }
                    }
                } else {
                    JInternalFrame oldframe = desktop.getSelectedFrame();
                    if (oldframe != null) {
                        if (oldframe.isSelected()) {
                            try {
                                oldframe.setSelected(false);
                            } catch (Exception ex) {
                                Log.show(Log.FATAL, getResString("FehlerAllgemein"), ex);
                            }
                        }
                    }
                }

                //wenn vorher ein Matrix-View diese Funktion ausgelöst hat, dann nur merken, dass beim
                //nächsten Kontextwechsel wieder auch der grafische View gewechselt werden soll
            } else {
                Tool3lgm.activateGraphView = true;
            }
            //wenn ein Matrix-View nach vorne geholt wurde und somit nicht der grafische View aktiviert
            //werden soll -> statisch diesen Fakt merken, so dass beim Wechel des ModelBrowsers, nicht
            //doch der GraphView nach vorne geholt wird
        } else {
            Tool3lgm.activateGraphView = false;
        }

        //wenn sich das im ModelBrowser ausgewählte Teimodell geändert hat
        if (doc != ModelBrowserPanel.getSelectedDoc()) {
            //aktiviere es. Dabei wird diese Funktion auch noch einmal aufgerufen und je nachdem
            //wie die Tool3lgm.activateGraphView gerade steht, wird der GraphView nach vorne geholt
            //oder eben nicht
            modelBrowserPanel.setSelectedDoc(doc);
            //wenn sich das Teilmodell nicht geändert hat
        } else {
            //beim nächsten Konextwechsel auch das nach Vorne holen des grafischen Views wieder einschalten
            Tool3lgm.activateGraphView = true;
        }

        if (isCheckConsistency) {
            if (consistencyChecker == null) {
                consistencyChecker = new ConsistencyChecker(gdcoll);
            } else {
                consistencyChecker.changeContext(gdcoll);
            }
        }
        setCheckConsistencyState(isCheckConsistency);
        getContextGenerator().changeContext((LGMGraphDocument) doc);
        toolbar.selectedDocChanged();
    }

    /**
     * Je nach Paramter wir die Konsitenzprüfung bei <code>true</code> ein und bei <code>false</code> ausgeschaltet. Das beinhaltet auch das Anzeigen
     * der Fehlertabelle.
     *
     * @param state
     *            wenn <code>true</code> wird die Konsistenzprüfung eingeschaltet, sonst wird sie abgeschaltet
     */
    public void setCheckConsistencyState(boolean state) {
        GDCollection gdcoll = getSelectedGDCollection();
        if (gdcoll == null) {
            state = false;
        }
        if (!state) {
            ConsistencyChecker checker = getConsistencyChecker();
            if (checker != null) {
                checker.resetConsistencyDefinition();
            }
            if (verticalSplitPane.getParent() == workarea) {
                return;
            }
            if (horizontalSplitPane != null) {
                workarea.remove(horizontalSplitPane);
            }
            workarea.add(verticalSplitPane, BorderLayout.CENTER);
            horizontalSplitPane = null;
            // falls vorher schonmal die Konsistenzprüfung eingeschaltet war -> Listener zur
            // Tabellenaktualisierung wieder entfernen
            if (consistencyChecker != null) {
                consistencyChecker.changeContext(null);
            }
        } else {
            if (horizontalSplitPane == null) {
                //wenn der consistencyChecker noch nicht initialisiert war, wird er es hier (man könnte die Zuweisung auch weg lassen)
                consistencyChecker = getConsistencyChecker();
                horizontalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, verticalSplitPane, new JScrollPane(consistencyChecker.getErrorTable()));
                horizontalSplitPane.setOneTouchExpandable(true);
                horizontalSplitPane.setDividerSize(10);
                horizontalSplitPane.setDividerLocation(workarea.getHeight() / 4 * 3);
                workarea.add(horizontalSplitPane, BorderLayout.CENTER);
            }
            consistencyChecker.changeContext(gdcoll);
        }
        workarea.revalidate();
        repaint();
    }

    private void setShowStandardToolbar(final boolean showStandardToolbar) {
        Container contentPane = getContentPane();
        ToolBar toolBar = getToolBar();
        if (showStandardToolbar) {
            contentPane.add(toolBar, BorderLayout.NORTH);
        } else {
            contentPane.remove(toolBar);
        }
        getWorkArea().revalidate();
    }

    /** ordnet alle InternalFrames neu an (überlappt) */
    public void fensterUeberlappen() {
        JInternalFrame[] frames = desktop.getAllFrames();
        Rectangle rect = desktop.getVisibleRect();
        double height = rect.getHeight();
        double width = rect.getWidth();
        int xOffset = 10, yOffset = 10;
        int openFrameCount = 0;
        for (int n = frames.length; n > 0; n--) {
            ++openFrameCount;
            double count = openFrameCount;
            if (height - yOffset * count < 50) {
                count = (height - 50) / yOffset;
            }
            frames[n - 1].setSize((int) width - xOffset * (int) count, (int) height - yOffset * (int) count);
            frames[n - 1].setLocation(xOffset * (int) count, yOffset * (int) count);
        }
        try {
            frames[frames.length - 1].setMaximum(false);
        } catch (java.beans.PropertyVetoException evt) {
            Log.show(Log.FATAL, getResString("FehlerAllgemein"), evt);
        }
    }

    /** ordnet alle InternalFrames neu an (nebeneinander) */
    public void fensterNebeneinander() {
        JInternalFrame[] frames = desktop.getAllFrames();
        try {
            frames[frames.length - 1].setMaximum(false);
        } catch (java.beans.PropertyVetoException evt) {
            Log.show(Log.FATAL, getResString("FehlerAllgemein"), evt);
        }
        Rectangle rect = desktop.getVisibleRect();
        double height = rect.getHeight();
        double width = rect.getWidth();
        int spalten, zeilen;
        double hilfe = Math.sqrt(frames.length);
        if ((int) hilfe * (int) hilfe == frames.length) {
            zeilen = (int) hilfe;
            spalten = (int) hilfe;
        } else {
            zeilen = (int) hilfe + 1;
            spalten = (int) hilfe;
        }
        int count = 0;
        for (int m = 0; m < zeilen - 1; m++) {
            for (int n = 0; n < spalten; n++) {
                frames[count].setBounds(0 + n * (int) width / spalten, 0 + m * (int) height / zeilen, (int) width / spalten, (int) height / zeilen);
                count++;
            }
        }
        int rest = frames.length - count;
        for (int k = count; k < frames.length; k++) {
            frames[k].setBounds(0 + (k - count) * (int) width / rest, (int) height / zeilen * (zeilen - 1), (int) width / rest, (int) height / zeilen);
        }
    }

    /**
     * @param szen
     * @return
     */
    public AbstractInternalFrame findFirstInternalFrame(final GraphDocument szen) {
        JInternalFrame[] frames = getAllFrames();
        AbstractInternalFrame frame = null;
        for (int c = 0; c < frames.length; c++) {
            if (((AbstractInternalFrame) frames[c]).getGraphDocument() == szen) {
                frame = (AbstractInternalFrame) frames[c];
                break;
            }
        }
        return frame;
    }

    /**
     * @param szen
     */
    public void closeFrame(final GraphDocument szen) {
        AbstractInternalFrame frame = findFirstInternalFrame(szen);
        while (frame != null) {
            toolbar.removeWindow(frame);
            if (frame instanceof MatrixViewInternalFrame) {
                frame.dispose();
            } else {
                //erst das dispose und erst dann als Tab removen, sonst haut
                // das Einfügen-Menü nicht mehr hin, weil
                //die Ereignisse internalFrameDeactivated(),
                // internalFrameClosed() und internalFrameActivated()
                //sonst in einer ungünstigen Reihenfolge kommen.
                frame.dispose();
                modelBrowserPanel.removeGraphDocument(frame.getGraphDocument());
            }
            frame = findFirstInternalFrame(szen);
        }
    }

    public void closeAllFramesAndTabs(final GDCollection gdcoll) {
        modelBrowserPanel.removeGraphDocument(gdcoll.getMainGraphDocument());
        closeFrame(gdcoll.getMainGraphDocument());
        for (Szenario szen : gdcoll.getSzenarios()) {
            closeFrame(szen);
        }
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

        List<ElementPropertyDialog> dialogs = ModelConstants.getDialogs();
        for (int n = 0; n < dialogs.size(); n++) {
            ElementPropertyDialog pd = dialogs.get(n);
            // wenn der Dialog zum zu schließenden Modell gehört
            if (selDoc.isMyElement(pd.getModelElement())) {
                // alle Änderungen der geöffneten Dialoge zurück rollen
                pd.cancel();
                // in pd.cancel() wird die dialogs.size() um -1 geändert
                n--;
            }
        }

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
        setSelectedDoc(ModelBrowserPanel.getSelectedDoc(), true);

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

        AbstractInternalFrame lastFrame = toolbar.getNextWindow();
        if (lastFrame == null) {
            lastFrame = toolbar.getPreviousWindow();
        }
        if (lastFrame != null) {
            try {
                lastFrame.setSelected(true);
            } catch (PropertyVetoException ex) {
            }
        }
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
        JInternalFrame[] allFrames = desktop.getAllFrames();
        for (JInternalFrame frame : allFrames) {
            if (frame instanceof InternalGraphFrame) {
                ((InternalGraphFrame) frame).updateTitle();
            }
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
        gdcoll.setChanged(false);
        return true;
    }

    /**
     * reagiert auf ein umbenanntes Szenario
     */
    public void szenarioRenamed(final Szenario szen) {
        InternalGraphFrame frame = szen.getFrame();
        if (frame != null) {
            frame.updateTitle();
        }
        getModelBrowserPanel().updateTitle(szen);
        GraphDocument mainDoc = szen.getCollection().getMainGraphDocument();
        //Alt = in allen Szenarions allen Elementen (und eben nicht allen Containern) den neuen Namen verpassen
        // for (ModelElement me : sz.getModelItems(ModelElement.class, true))
        for (ModelElement me : mainDoc.getModelItems(ModelElement.class, true)) {
            me.invalidateNameWithSzens();
        }

    }

    @Override
    public void internalFrameClosing(final InternalFrameEvent e) {
    }

    @Override
    public void internalFrameClosed(final InternalFrameEvent e) {
        JInternalFrame[] frames = desktop.getAllFrames();
        AbstractInternalFrame frame = (AbstractInternalFrame) e.getSource();
        if (!(frame instanceof MatrixViewInternalFrame)) {
            workarea.revalidate();
        }

        toolbar.removeWindow(frame);

        if (frames.length == 0) {
            activeFrame = null;
            graphAreaToolbarManager.updateToolBar();
            toolbar.repaint();

        }
    }

    @Override
    public void internalFrameOpened(final InternalFrameEvent e) {
    }

    @Override
    public void internalFrameIconified(final InternalFrameEvent e) {
    }

    @Override
    public void internalFrameDeiconified(final InternalFrameEvent e) {
    }

    @Override
    public void internalFrameActivated(final InternalFrameEvent e) {
        activeFrame = (AbstractInternalFrame) e.getInternalFrame();
        LGMGraphDocument doc = activeFrame.getGraphDocument();
        doc.addGraphDocumentListener(graphAreaToolbarManager);
        graphAreaToolbarManager.updateToolBar();
        //wenn es ein Grafikfenster aktiviert wurde, soll es intern auch in den Vordergrund geholt werden. Bei
        //allen anderen Fenstern (Matrix-Sicht-Fenster), soll dieses Fenster im Vordergrund bleiben.
        setSelectedDoc(doc, activeFrame instanceof InternalGraphFrame);
        try {
            activeFrame.setSelected(true);
        } catch (Exception ex) {
            Log.show(Log.FATAL, getResString("FehlerAllgemein"), ex);
        }
        toolbar.revalidate();
        toolbar.repaint();
        workarea.revalidate();
        workarea.repaint();
        activeLayerChanged(activeFrame.getGraphDocument());

        toolbar.addWindow(activeFrame);
    }

    @Override
    public void internalFrameDeactivated(final InternalFrameEvent e) {
        LGMGraphDocument graphDocument = activeFrame.getGraphDocument();
        graphDocument.removeGraphDocumentListener(graphAreaToolbarManager);
        activeFrame = null;
        graphAreaToolbarManager.updateToolBar();
    }

    @Override
    public void windowClosing(final WindowEvent e) {
        //man muss die Liste Clonen, da sie sich durch setSelectedDoc() ändert
        List<GDCollection> collections = new ArrayList<>(this.collections);
        //die letzte ist immer die aktive
        for (int i = collections.size() - 1; i >= 0; i--) {
            GDCollection gdcoll = collections.get(i);
            setSelectedDoc(gdcoll.getSelectedDoc(), true);
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
        contextGenerator.changeContext(getSelectedDoc());
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
    public void elementAdded(final GraphDocument source, final ElementContainer element) {
        source.getCollection().setChanged(true);
    }

    @Override
    public void elementDeleted(final GraphDocument source, final ElementContainer element) {
        source.getCollection().setChanged(true);
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
        JInternalFrame[] intFrames = desktop.getAllFrames();
        AbstractInternalFrame[] frames = new AbstractInternalFrame[intFrames.length];
        System.arraycopy(intFrames, 0, frames, 0, intFrames.length);
        return frames;
    }

    /**
     * return the toolbar of application
     *
     * @return ToolBar
     */
    public ToolBar getToolBar() {
        return toolbar;
    }

    /**
     * return the workarea of application
     *
     * @return JPanel
     */
    public JPanel getWorkArea() {
        return workarea;
    }

    /**
     * return the verticalSplitPane of application
     *
     * @return JSplitPane
     */
    public JSplitPane getVerticalSplitPane() {
        return verticalSplitPane;
    }

    /**
     * return the horizontalSplitPane of application
     *
     * @return JSplitPane
     */
    public JSplitPane getHorizontalSplitPane() {
        return horizontalSplitPane;
    }

    /**
     * return the internal frame toolbar
     *
     * @return UnfloatableToolBar
     */
    public final GraphAreaToolbarManager getGraphAreaToolBarManager() {
        return graphAreaToolbarManager;
    }

    /** (De-)Aktiviert den ModelBrowser */
    private void showModelBrowser(final boolean b) {
        if (b) {
            getVerticalSplitPane().setLeftComponent(getModelBrowserPanel());
            getVerticalSplitPane().setDividerLocation(200);
            getWorkArea().revalidate();
        } else {
            JSplitPane pane = getVerticalSplitPane();
            pane.remove(pane.getLeftComponent());
            getWorkArea().revalidate();
        }
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
     * @return
     */
    public ModelBrowserPanel getModelBrowserPanel() {
        return modelBrowserPanel;
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
        return activeFrame;
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
                    openFile(false, file);
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

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (UserProperties.isPropertyChange(BooleanProperty.OPTION_CHECK_CONSISTENCY, evt)) {
            setCheckConsistencyState(UserProperties.is(BooleanProperty.OPTION_CHECK_CONSISTENCY));
        } else if (UserProperties.isPropertyChange(BooleanProperty.OPTION_SHOW_PAINTING_TOOLBAR, evt)) {
            graphAreaToolbarManager.setToolBarVisible(UserProperties.is(BooleanProperty.OPTION_SHOW_PAINTING_TOOLBAR));
        } else if (UserProperties.isPropertyChange(BooleanProperty.OPTION_SHOW_STANDARD_TOOLBAR, evt)) {
            setShowStandardToolbar(UserProperties.is(BooleanProperty.OPTION_SHOW_STANDARD_TOOLBAR));
        } else if (UserProperties.isPropertyChange(BooleanProperty.OPTION_MODEL_BROWSER_SHOW, evt)) {
            showModelBrowser(UserProperties.is(BooleanProperty.OPTION_MODEL_BROWSER_SHOW));
        }
    }

}