package de.imise.tool3lgm;

import static de.imise.tool3lgm.Static.tool;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.Tool3lgmConstants.registerPublicKeyStrokes;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.help.CSH;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.twatd.licensing.TwatdLicenseLibrary;

import de.imise.tool3lgm.Tool3lgmConstants.FileFilterType;
import de.imise.tool3lgm.graphtools.consistency.ConsistencyChecker;
import de.imise.tool3lgm.graphtools.consistency.ModelCleaner;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.RMIErrorPanel;
import de.imise.tool3lgm.graphtools.matrixview.MatrixViewInternalFrame;
import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.Knickpunkt;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionFileHandler;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentListener;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.userfield.UserfieldResourceHandler;
import de.imise.tool3lgm.graphtools.view.browser.ModelBrowserPanel;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.ViewParameter;
import de.imise.tool3lgm.gui.AbstractInternalFrame;
import de.imise.tool3lgm.gui.InternalFrameToolbarManager;
import de.imise.tool3lgm.gui.InternalGraphFrame;
import de.imise.tool3lgm.gui.StatusBar;
import de.imise.tool3lgm.gui.ToolBar;
import de.imise.tool3lgm.gui.ToolSplashScreen;
import de.imise.tool3lgm.gui.menu.ContextGenerator;
import de.imise.tool3lgm.gui.menu.MenuBar;
import de.imise.tool3lgm.help.Help;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.rmi.Tool3lgmServer;
import de.imise.tool3lgm.rmi.Tool3lgmServerImpl;
import de.imise.tool3lgm.tools.BrowseUtils;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.Alphabetical;
import de.imise.util.io.FileNameExtensionFilterAndFileFilter;
import de.imise.util.swing.dialog.ExtendedFileChooser;
import de.imise.util.swing.dialog.ProgressDialog;

/** Hauptklasse der Anwendung 3lgm */
public class Tool3lgm extends JFrame implements WindowListener, InternalFrameListener, GraphDocumentListener {

    //Als allerstes muss aus der Main-Funktion ausßerhalb dieser Klasse diese init()-Funktion
    //aufgerufen werden, damit alle statischen Elemente einmal initialisert werden. Diese Funktion
    //muss unbedingt ganz oben stehen!
    public static final void init() {
    }

    /**
     * In den <code>SplashScreen</code> die lokalisierten Informationen schreiben
     */
    static {
        ToolSplashScreen.update();
    }

    /**
     * Schaltet allerlei Ausgaben an
     */
    public static final boolean DEBUG = false;

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

    private final InternalFrameToolbarManager internalFrameToolbarManager = new InternalFrameToolbarManager(workarea);

    /** contain all windows of opened documents (JDesktopPane is a container used to create a multiple-document interface or a virtual desktop) */
    private final JDesktopPane desktop;

    /** InternalFrame in desktop, which has the focus */
    private AbstractInternalFrame activeFrame = null;

    /** alle GDCollections */
    private final List<GDCollection> collections = new ArrayList<>();

    /** Position of divider betweeen the tree and the graph view in pixel from the left side */
    int dividerLocation = 200;

    /** Progress-Dialog */
    private ProgressDialog progressDialog;

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
     * COMMENTME
     */
    private final Properties licenseItems = new Properties();

    /**
     * Wenn der Baukasten mit RMI gestartet werden soll, wird <code>activateRMI</code> ausgeführt.
     *
     * @param args
     * @return true, wenn der Baukasten erfolgreich den RMI starten konnte. Wenn Fehler aufgetreten sind, false.
     */
    static boolean activateRMI(final String args[], final boolean visible) {

        // Der port, auf dem die RMI-Registry lauschen soll
        int regPort = Registry.REGISTRY_PORT;

        // Versuchen auf dem voreingestellten Port die Registry zu starten
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1");
            // hole den vom User eingestellen RegistryPort
            String regValue = UserProperties.getRMIRegistryPort().trim();

            // hier wird geprüft, ob der Wert ungleich "" ist und mittels regulären Ausdruck, ob nur Ziffern enthalten sind.
            if (!regValue.equals("") && regValue.matches("\\d*")) {
                regPort = Integer.parseInt(regValue);
            }

            // Falls ein Fehler mit dem RegistryPort auftritt, wird hier der alte Portwert gesichert.
            int oldRegPort = regPort;

            // Wenn der RMI-Service erfolgreich gestartet werden konnte, wird <code>bound</code> true
            boolean bound = false;

            Remote remote = null;

            // Im Fehlerfall soll der Dialog angezeigt werden, den den user entscheiden lässt, wie das weitere Vorgehen sein soll.
            boolean showErrorDialog = true;

            // Es wird solange versucht den RMI-Service zu starten, bis ein freier Port gefunden wurde oder der User einen freien eingegeben hat.
            while (!bound) {

                try {
                    registry.list();
                } catch (Exception ex) {
                    try {
                        if (Registry.REGISTRY_PORT != regPort) {
                            registry = LocateRegistry.createRegistry(regPort);
                        } else {
                            registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                        }
                    } catch (Exception e) {
                    }
                }
                try {
                    //TODO:############# auf jeden Fall wieder reinnehmen!!!
                    remote = Naming.lookup("//127.0.0.1:" + regPort + "/Tool3lgmServer");
                } catch (Exception innerEx) {
                }
                // Wenn der RMI-Service noch nicht läuft, wird hier weiter gemacht.
                if (remote == null || !(remote instanceof Tool3lgmServer)) {

                    // Wenn der Baukasten schon läuft, wird kein neuer instanziiert, sonst schon.
                    if (tool == null) {
                        tool = new Tool3lgm();
                        menuBar = new MenuBar();
                        CSH.setHelpIDString(menuBar, "uebersicht_menueleiste");
                        registerPublicKeyStrokes(tool.getRootPane());
                        tool.setJMenuBar(menuBar);
                        tool.setVisible(visible);
                        tool.toolbar.selectedDocChanged();
                    }

                    // Hier ist die kritische Stelle. Das Rebind schlägt fehl, wenn ein fremder Service den Port belegt, auf dem der Baukasten lauschen soll.
                    try {
                        // System.err.println("try port: "+regPort);
                        Naming.rebind("//127.0.0.1:" + regPort + "/Tool3lgmServer", new Tool3lgmServerImpl(Static.tool));

                        // Wenn der RMI-Server erfolgreich gestartet werden konnte, wird bound true.
                        // Wenn nicht, ist eine Exception geflogen und ist in die catch () gesprungen. bound wurde nicht true.
                        bound = true;

                        // Wenn der alte regPort ungleich dem neuen ist, wird der neue gespeichert und beim nächsten Programmstart als Standard-Port angewandt.
                        if (regPort != oldRegPort) {
                            UserProperties.setRMIRegistryPort("" + regPort);
                            JOptionPane.showMessageDialog(Static.tool, getResString("rmiNewRegPortIs") + " " + regPort);
                        }

                    } catch (RemoteException e) {
                        // e.printStackTrace();
                        // Sollte der RMI-Server nicht auf dem aktuellen eingestellten regPort lauschen können:
                        // Wenn ein Fehler aufgetreten ist und nicht schon nach einem neuen Port gesucht wird, wird der RMI-FehlerDialog angezeigt.
                        RMIErrorPanel rmip = new RMIErrorPanel();

                        if (showErrorDialog) {
                            if (JOptionPane.showOptionDialog(Static.tool, rmip, getResString("rmiError"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
                                if (rmip.isRmiAutoNextFreePortCheckBox()) {
                                    // Es wird ein neue Port bis 65500 gesucht, wenn bis dahin keiner frei ist, wird wieder beim standardPort begonnen
                                    if (regPort < 65500) {
                                        regPort++;
                                    } else {
                                        regPort = 1099;
                                    }
                                    showErrorDialog = false;
                                }
                                // Wenn der Benutzer einen Port eingegeben hat, wird er gesichtert und versucht darauf zu verbinden.
                                // Sollte en fehler auftreten, wir dder Errordialog wieder angezeigt.
                                else {
                                    regPort = Integer.parseInt(rmip.getRmiRegistryPortTextFieldValue());
                                    UserProperties.setRMIRegistryPort("" + regPort);
                                }
                            }
                        }
                        // Wenn schon nach einem neuen Port gescuht wird, wird der ErrorDialog nicht nochmal angezeigt sondern gleich hier weiter gemacht:
                        else {
                            // Es wird ein neuer Port bis 65500 gesucht, wenn bis dahin keiner frei ist, wird wieder beim standardPort begonnen
                            if (regPort < 65500) {
                                regPort++;
                            } else {
                                regPort = 1099;
                            }

                        }
                    }
                    // e.printStackTrace();

                } else {
                    // Wenn schon eine Instanz des Tools läuft, wird hier hergesprungen.
                    // <code>bound</code> muss auf true gesetzt werden, dmit die Schleife beendet werden kann.
                    bound = true;
                }

            }

            remote = Naming.lookup("//127.0.0.1:" + regPort + "/Tool3lgmServer");
            if (remote == null) {
                Log.show(Log.FATAL, "RMI registration failed", new Exception("RMI registration failed"));
                return false;
            }
            // Wenn der RMI-Service erfolgreich auf dem regPort lauscht, wird hier weiter gemacht.
            // Der RMI-Server steht für RMI-Aufrufe bereit.

            Tool3lgmServer tool3lgmServer = (Tool3lgmServer) remote;
            if (args.length != 0) {
                String[] params = new String[args.length - 1];
                for (int i = 0; i < params.length; i++) {
                    params[i] = args[i + 1];
                }
                tool3lgmServer.processCommand(args[0], params);
            }
        } catch (Exception ex) {
            System.err.println(ex);
            Log.show(Log.FATAL, "RMI registration failed", ex);
            return false;
        }
        return true;
    }

    /**
     * constructor
     *
     * @see java.lang.Object#Object()
     */
    private Tool3lgm() {

        licenseItems.put("g",
                "174068207532402095185811980123523436538604490794561350978495831040599953488455823147851597408940950725307797094915759492368300574252438761037084473467180148876118103083043754985190983472601550494691329488083395492313850000361646482644608492304078721818959999056496097769368017749273708962006689187956744210730");
        licenseItems.put("p",
                "178011905478542266528237562450159990145232156369120674273274450314442865788737020770612695252123463079567156784778466449970650770920727857050009668388144034129745221171818506047231150039301079959358067395348717066319802262019714966524135060945913707594956514672855690606794135837542707371727429551343320695239");
        licenseItems.put("q", "864205495604807476120572616017955259175325408501");
        licenseItems.put("y",
                //Key der bis Version 3.2 Beta benutzt wurde
                //"14300627371230228950169601901505470128925284120125584820959612014086342028994560433627164468322417150724888119951832411281916807062298007963159103404336774085891061191128715953217021296723250723500408671825275650987665439945908447793990133826618450011753407968612194841395971300289629133573910203377535518349");
                //Key ab Version 3.2
                "50130353173738973728122117307050982303325240535281983843066456949452369361589860322851251253694135519121623652865168517743221242969658663504859944420599589281036527851748542030299809821557170181016869364580930185375665744420760918986875024780360963498505654004927815814220720267469598144682653844939860413572");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setIconImage(Tool3lgmConstants.getIcon("toolIcon.gif").getImage());

        //den Hauptframe in die Mitte setzen
        setLocationRelativeTo(null);
        setTitle(getResString("tool3lgm"));
        //den Hauptframe initialisieren, damit die JOption-Panes der Lizenzanfrage an der richtigen Stelle sind.
        setVisible(true);
        checkLicenses();
        setVisible(false);
        setLocation(0, 0);

        contextGenerator = new ContextGenerator();
        modelBrowserPanel = new ModelBrowserPanel();

        /* table of defaults for Swing components */
        UIDefaults defaults = UIManager.getDefaults();
        defaults.put("FileChooser.openButtonText", getResString("open"));
        defaults.put("FileChooser.cancelButtonText", getResString("cancel"));
        defaults.put("FileChooser.filesOfTypeLabelText", getResString("filesOfTypeLabelText"));
        defaults.put("FileChooser.fileNameLabelText", getResString("fileNameLabelText"));
        defaults.put("FileChooser.lookInLabelText", getResString("lookInLabelText"));

        defaults.put("ColorChooser.cancelText", getResString("cancel"));
        defaults.put("ColorChooser.sampleText", getResString("sampleText"));
        defaults.put("ColorChooser.rgbGreenText", getResString("green"));
        defaults.put("ColorChooser.previewText", getResString("previewText"));
        defaults.put("ColorChooser.rgbRedText", getResString("red"));
        defaults.put("ColorChooser.resetText", getResString("resetText"));
        defaults.put("ColorChooser.rgbBlueText", getResString("blue"));
        defaults.put("ColorChooser.swatchesNameText", getResString("swatchesNameText"));
        defaults.put("ColorChooser.swatchesRecentText", getResString("swatchesRecentText"));

        defaults.put("OptionPane.okButtonText", getResString("ok"));
        defaults.put("OptionPane.cancelButtonText", getResString("cancel"));
        defaults.put("OptionPane.noButtonText", getResString("no"));
        defaults.put("OptionPane.yesButtonText", getResString("yes"));

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

        setCheckConsistencyState(UserProperties.isCheckConsistency());

        addWindowListener(this);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // Direkthilfe einschalten
        Help.getHelp().enableHelpKey(rootPane, "willkommen");
        // Direkthilfe für die einzelnen Baukastenteile
        CSH.setHelpIDString(modelBrowserPanel, "uebersicht_modellbrowser");
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
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(this, getResString("load_model") + " " + file.getName(), true);
            }
            if (progressDialog != null) {
                progressDialog.setStatusLabelText(getResString("read_progress"));
            }
            update(getGraphics());
            boolean retVal = fileHandler.loadFromRAF();
            return retVal;
        } catch (Exception e) {
            Log.show(Log.FATAL, getResString("FehlerAllgemein"), e);
            Object[] buttons = new Object[] {
                    getResString("ok")
            };
            JOptionPane.showOptionDialog(this, getResString("oeffnenfehler") + "\n" + file.getPath() + "\n" + e.getMessage(), getResString("tool3lgm"), JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, buttons, null);
            closeProgressDialog();
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
                closeProgressDialog();
                return false;
            }
        } else if (open == true) {
            ExtendedFileChooser chooser = new ExtendedFileChooser(null, UserProperties.getWorkingDirectory());
            chooser.setMultiSelectionEnabled(false);
            FileNameExtensionFilter[] lgmFileFilter = Tool3lgmConstants.getFileNameExtensionFilters(FileFilterType.LGM3, FileFilterType.LGM3_ZIP, FileFilterType.LGM3_UNZIPPED);
            if (chooser.showOpenDialog(this, false, lgmFileFilter) == ExtendedFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
                progressDialog = new ProgressDialog(this, getResString("load_model") + " " + file.getName(), true);
                UserProperties.setWorkingDirectory(file);
                chooser.setVisible(false);
                if (!loadFile(file, gdcoll)) {
                    if (progressDialog != null) {
                        progressDialog.dispose();
                        progressDialog = null;
                    }
                    return false;
                }
            } else {
                return false;
            }
        }

        if (progressDialog != null) {
            progressDialog.setStatusLabelText(getResString("finish_progress"));
        }

        modelBrowserPanel.addCollection(gdcoll);

        collections.add(gdcoll);
        gdcoll.addGraphDocumentListener(this);

        if (progressDialog != null) {
            progressDialog.setStatusLabelText(getResString("create_frame") + gdcoll.getMainGraphDocument().getTitle());
        }

        createMainFrame(gdcoll.getMainGraphDocument());

        LGMGraphDocument selectedDoc = gdcoll.getMainGraphDocument();
        for (int i = 0; i < gdcoll.getSzenarioCount(); i++) {
            Szenario szen = gdcoll.getSzenario(i);
            if (progressDialog != null) {
                progressDialog.setStatusLabelText(getResString("create_frame") + szen.getTitle());
            }
            if (szen.getViewParameter() == null && i == 0) {
                selectedDoc = szen;
            } else if (szen.getViewParameter() != null && szen.getViewParameter().selected) {
                selectedDoc = szen;
            }
            createSzenarioFrame(szen);
        }

        //vor dem Selektieren des aktuellen Teilmodells alle nicht behebbaren Fehler löschen
        ConsistencyChecker.clearUnfixableErrors(gdcoll);
        setSelectedDoc(selectedDoc, true);
        gdcoll.setChanged(false);
        System.gc();
        closeProgressDialog();

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
     * @param gdc
     *            {@link GDCollection} to print or <code>null</code> if all {@link GDCollection}s should be printed
     */
    public final void printStatistic(final GDCollection gdc, final boolean useElements, final boolean alphabetic) {
        List<GDCollection> collections = this.collections;
        if (gdc != null) {
            collections = new ArrayList<>();
            collections.add(gdc);
        }
        for (GDCollection gdcoll : collections) {
            GraphDocument mainDoc = gdcoll.getMainGraphDocument();

            List<ElementContainer> allContainer = useElements ? null : mainDoc.getElementContainer(ModelElement.class, true, alphabetic);
            List<ModelElement> allElements = useElements ? mainDoc.getModelItems(ModelElement.class, true, alphabetic) : null;

            Map<Class<? extends ModelElement>, Integer> class2ElementCount = new HashMap<>();
            Map<Class<? extends ModelElement>, Integer> class2ContainerCountFromGraphDocuments = new HashMap<>();
            Map<Class<? extends ModelElement>, Integer> class2ContainerCountFromModelElements = new HashMap<>();
            //für alle ElementContainer im MainDoc = alle, die es gibt!
            for (Object ecOrMe : useElements ? allElements : allContainer) {
                //Anzahl der Modellelemente im Gesamtmodell hochzählen
                ModelElement me = useElements ? (ModelElement) ecOrMe : ((ElementContainer) ecOrMe).getElement();
                Class<? extends ModelElement> meClass = me.getClass();

                Integer count = class2ElementCount.get(meClass);
                count = count == null ? new Integer(1) : new Integer(count.intValue() + 1);
                class2ElementCount.put(meClass, count);

                List<GraphDocument> docs = new ArrayList<>(gdcoll.getSzenarioCount() + 1);
                for (Szenario szen : gdcoll.getSzenarios()) {
                    docs.add(szen);
                }
                docs.add(mainDoc);

                //Anzahl der ElementContainer der Modellelemente im Gesamtmodell hochzählen
                for (GraphDocument doc : docs) {
                    for (LayerContainer lcc : doc.getLayers()) {
                        for (ElementContainer layerEc : lcc.getKnickpunkte()) {
                            if (layerEc.getElement() == me) {
                                count = class2ContainerCountFromGraphDocuments.get(meClass);
                                count = count == null ? new Integer(1) : new Integer(count.intValue() + 1);
                                class2ContainerCountFromGraphDocuments.put(meClass, count);
                            }
                        }
                        for (ElementContainer layerEc : lcc.getKnoten()) {
                            if (layerEc.getElement() == me) {
                                count = class2ContainerCountFromGraphDocuments.get(meClass);
                                count = count == null ? new Integer(1) : new Integer(count.intValue() + 1);
                                class2ContainerCountFromGraphDocuments.put(meClass, count);
                            }
                        }
                        for (ElementContainer layerEc : lcc.getKanten()) {
                            if (layerEc.getElement() == me) {
                                count = class2ContainerCountFromGraphDocuments.get(meClass);
                                count = count == null ? new Integer(1) : new Integer(count.intValue() + 1);
                                class2ContainerCountFromGraphDocuments.put(meClass, count);
                            }
                        }
                    }
                }
                count = class2ContainerCountFromModelElements.get(meClass);
                count = count == null ? new Integer(me.getContainerCount()) : new Integer(count.intValue() + me.getContainerCount());
                class2ContainerCountFromModelElements.put(meClass, count);
            }

            int nodeCount = 0, nodeContFromDoc = 0, nodeContFromMe = 0;
            int edgeCount = 0, edgeContFromDoc = 0, edgeContFromMe = 0;
            int bendCount = 0, bendContFromDoc = 0, bendContFromMe = 0;

            System.err.println("Modellstatistik: " + gdcoll.getName());
            System.err.println("-------------------------------------");
            List<Class<? extends ModelElement>> classList = new ArrayList<>(class2ElementCount.keySet());
            Alphabetical.sort(classList);
            //für jede Elementklasse
            for (Class<? extends ModelElement> elementClass : classList) {
                //				Integer integer = class2ElementCount.get(elementClass);
                //				int count = integer == null ? 0 : integer.intValue();
                //				integer = class2ContainerCountFromGraphDocuments.get(elementClass);
                //				int contFromDoc = integer == null ? 0 : integer.intValue();
                //				integer = class2ContainerCountFromModelElements.get(elementClass);
                //				int contFromMe = integer == null ? 0 : integer.intValue();

                int count = class2ElementCount.get(elementClass).intValue();
                int contFromDoc = class2ContainerCountFromGraphDocuments.get(elementClass).intValue();
                int contFromMe = class2ContainerCountFromModelElements.get(elementClass).intValue();

                System.err.print(elementClass.getName() + ": " + count + " " + contFromDoc + " " + contFromMe);
                if (contFromMe != contFromDoc) {
                    System.err.println(" <----------");
                } else {
                    System.err.println();
                }
                if (Knickpunkt.class.isAssignableFrom(elementClass)) {
                    bendCount += count;
                    bendContFromDoc += contFromDoc;
                    bendContFromMe += contFromMe;
                } else if (Node.class.isAssignableFrom(elementClass)) {
                    nodeCount += count;
                    nodeContFromDoc += contFromDoc;
                    nodeContFromMe += contFromMe;
                } else if (Edge.class.isAssignableFrom(elementClass)) {
                    edgeCount += count;
                    edgeContFromDoc += contFromDoc;
                    edgeContFromMe += contFromMe;
                }
            }
            System.err.println("Node      " + nodeCount + " " + nodeContFromDoc + " " + nodeContFromMe);
            System.err.println("Kanten      " + edgeCount + " " + edgeContFromDoc + " " + edgeContFromMe);
            System.err.println("Knickpunkte " + bendCount + " " + bendContFromDoc + " " + bendContFromMe);

            System.err.println();
        }
    }

    /**
     * @param maindoc
     * @return
     */
    private AbstractInternalFrame createMainFrame(final LGMGraphDocument maindoc) {
        InputGraphArea area = new InputGraphArea(maindoc);
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
        if (szenario.getViewParameter() == null) {
            setWorkArea(frame);
            szenario.getCollection().setActiveLayer(4);
        } else {
            setWorkArea(frame, szenario.getViewParameter());
        }

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
     * @param InputGraphArea to set
     */
    public void setWorkArea(final InternalGraphFrame frame) {
        InputGraphArea bgp = frame.getInputGraphArea();
        bgp.setLayerAngle(65);
        bgp.setInterLayerSpace(200);
        frame.getScrollPane().getViewport().setViewPosition(new Point(200, 150));
    }

    /**
     * set parameters of InputGraphArea to standard
     *
     * @param InputGraphArea
     *            to set
     */
    public void setWorkArea(final InternalGraphFrame frame, final ViewParameter view) {
        InputGraphArea bgp = frame.getInputGraphArea();
        bgp.setMultiView(view.multiView);
        frame.getGraphDocument().getCollection().setActiveLayer(view.layer);
        bgp.setMultiViewLayerAngle(view.layerAngle);
        bgp.setMultiViewLayerGap(view.layerGap);
        bgp.setZoom(view.zoom);
        frame.getScrollPane().getViewport().setViewPosition(new Point(view.x, view.y));
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

        //das doc kann null sein, wenn eine Datei geladen wird und das ModelBrowserPanel grade mit den
        //geladenen Teilmodellen gefüllt wird. Im ModelBrowserPanel wird bei jedem Hinzufügen eines
        //Teilmodell-Tabs immer diese Funktion hier aufgerufen.
        //Es kann auch null sein, wenn das letzte Modell geschlossen wurde
        if (doc == null) {
            setCheckConsistencyState(UserProperties.isCheckConsistency());
            toolbar.selectedDocChanged();
            if (consistencyChecker != null && UserProperties.isCheckConsistency()) {
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

        if (UserProperties.isCheckConsistency()) {
            if (consistencyChecker == null) {
                consistencyChecker = new ConsistencyChecker(gdcoll);
            } else {
                consistencyChecker.changeContext(gdcoll);
            }
        }
        setCheckConsistencyState(UserProperties.isCheckConsistency());

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
        //als erstes diese Option setzen, da sie in den folgenden Funktionen abgefragt wird
        UserProperties.setCheckConsistency(state);
        GDCollection gdcoll = getSelectedGDCollection();
        if (gdcoll == null) {
            state = false;
        }
        if (!state) {
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
                getResString("yes"),
                getResString("no"),
                getResString("cancel")
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

        progressDialog = new ProgressDialog(this, getResString("close_model") + " " + gdcoll.getName(), true);

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
            closeProgressDialog();
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
        UserProperties.addUsedFile(fileHandler.getFile());

        collections.remove(gdcoll);

        System.gc();

        closeProgressDialog();

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
        if (!checkLicenses()) {
            return false;
        }
        /* GDCollection zum ausgewähtlen Frame */
        GraphDocument doc = getSelectedDoc();
        if (doc == null) {
            return false;
        }
        GDCollection gdcoll = doc.getCollection();
        new ModelCleaner(gdcoll).cleanModel();
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
     * Benennt selektiertes Szenario um
     */
    public void renameSzenario() {
        GraphDocument doc = getSelectedDoc();
        if (doc == null || !(doc instanceof Szenario)) {
            return;
        }
        Szenario sz = (Szenario) doc;
        if (!sz.getCollection().renameSzenario(sz)) {
            return;
        }
        InternalGraphFrame frame = sz.getFrame();
        if (frame != null) {
            frame.updateTitle();
        }

        GraphDocument mainDoc = sz.getCollection().getMainGraphDocument();

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
            internalFrameToolbarManager.updateToolBar();
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
        doc.addGraphDocumentListener(internalFrameToolbarManager);
        internalFrameToolbarManager.updateToolBar();
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
        graphDocument.removeGraphDocumentListener(internalFrameToolbarManager);
        activeFrame = null;
        internalFrameToolbarManager.updateToolBar();
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
            UserProperties.addUsedFile(gdcoll.getFile());
        }

        new File(Tool3lgmConstants.getClipboardPath()).delete();

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
    }

    @Override
    public void layoutChanged(final GraphDocument source) {
        source.getCollection().setChanged(true);

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
    public final InternalFrameToolbarManager getInternalFrameToolBarManager() {
        return internalFrameToolbarManager;
    }

    /**
     * erstellt einen neuen ProgressDialog mit dem Hauotfenster als owner
     */
    public void showProgressDialog() {
        showProgressDialog(this);
    }

    /**
     * erstellt einen neuen ProgressDialog mit dem übergebenen Fenster als owner
     */
    public void showProgressDialog(final JFrame owner) {
        closeProgressDialog();
        progressDialog = new ProgressDialog(owner, true);
    }

    /**
     * erstellt einen neuen ProgressDialog mit dem übergebenen Dialog als owner
     */
    public void showProgressDialog(final JDialog owner) {
        closeProgressDialog();
        progressDialog = new ProgressDialog(owner, true);
    }

    /**
     * setzt einen neuen Titel des ProgressDialog, sofern dieser überhaupt existiert;
     * ansonsten passiert nichts
     *
     * @param xmlText String mit dem neuen Titel
     */
    public void setProgressDialogTitle(final String text) {
        if (progressDialog == null) {
            return;
        }
        progressDialog.setTitle(text);
    }

    /**
     * schließt den ProgressDialog, sofern dieser überhaupt existiert;
     * ansonsten passiert nichts
     */
    public void closeProgressDialog() {
        if (progressDialog == null) {
            return;
        }
        progressDialog.dispose();
        progressDialog = null;
    }

    /**
     * setzt einen neuen Stautstext des ProgressDialog, sofern dieser überhaupt existiert;
     * ansonsten passiert nichts
     *
     * @param xmlText String mit neuen Statustext
     */
    public void setProgressDialogStatusLabel(final String text) {
        if (progressDialog == null) {
            return;
        }
        progressDialog.setStatusLabelText(text);
    }

    /** (De-)Aktiviert den ModelBrowser */
    public void showModelBrowser(final boolean b) {
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
     * @param eventCode
     */
    public void distributeOptionChange(final int eventCode) {
        for (int i = 0; i < collections.size(); i++) {
            GDCollection col = collections.get(i);
            col.distribute(eventCode);
        }
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
                        getResString("hyperlink"),
                        getResString("submodel"),
                        getResString("cancel")
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
     * Überprüft, ob die übergeben Datei eine gültige Lizenzdatei ist.
     *
     * @param licenseFile
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws SignatureException
     * @throws IOException
     */
    private final String getLicenseError(final File licenseFile) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, IOException, SocketException {
        if (!TwatdLicenseLibrary.verifyLicenseFile(licenseFile, licenseItems)) {
            return getResString("invalidLicense");
        }
        Properties licenseItems = TwatdLicenseLibrary.readLicenseItems(licenseFile);
        if (!TwatdLicenseLibrary.checkExpiryDate(new Date(Long.parseLong((String) licenseItems.get("expiryDate"))))) {
            return getResString("licenseExpired");
        }
        String licenseHostName = licenseItems.get("hostName").toString();
        if (licenseHostName.length() > 0) {
            boolean correctHostName = true;
            try {
                correctHostName = TwatdLicenseLibrary.checkHostName(licenseHostName);
            } catch (UnknownHostException uhe) {
                correctHostName = false;
            }
            if (!correctHostName) {
                return getResString("wrongHost_1") + licenseHostName + getResString("wrongHost_2");
            }
        }
        return null;
    }

    /**
     * Importiert eine Lizenzdatei in das Anwendungsverzeichnis
     */
    public void importLicenseFile() {
        // FileChooser, mit FileFilter für Lizenzdateien (Endung "lic")
        ExtendedFileChooser fileChooser = new ExtendedFileChooser(null);
        FileNameExtensionFilter licenseFileFilter = Tool3lgmConstants.getFileNameExtensionFilter(FileFilterType.LIC);
        fileChooser.setFileSelectionMode(ExtendedFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);

        if (fileChooser.showOpenDialog(this, false, licenseFileFilter) == ExtendedFileChooser.APPROVE_OPTION) {
            File licenseFile = fileChooser.getSelectedFile();
            // in diese Datei wird die ausgwählte Datei kopiert
            File importedLicenseFile = new File(Tool3lgmConstants.APPLICATION_DIR, licenseFile.getName());
            try {
                String licenseError = getLicenseError(licenseFile);
                if (licenseError != null) {
                    JOptionPane.showMessageDialog(this, licenseError + "\n\n" + getResString("licenseNotImported"), getResString("invalidLicense"), JOptionPane.WARNING_MESSAGE);
                } else {
                    // Lizenzdatei vom Quellort ins Installationsverzeichnis kopieren
                    // (nicht kopieren, wenn Quell und Ziel-Verzeichnis gleich sind. Das ist
                    // der Fall, wenn Benutzer den gepackten Lizenzkey nach dem Erscheinen der
                    // Abfrage ausgerechnet schon an den richtigen Zielort entpackt haben))
                    if (!licenseFile.equals(importedLicenseFile)) {
                        FileOutputStream fos = new FileOutputStream(importedLicenseFile);
                        FileInputStream fis = new FileInputStream(licenseFile);
                        byte[] bytes = new byte[fis.available()];
                        fis.read(bytes);
                        fos.write(bytes);
                        fis.close();
                        fos.close();
                    }
                    JOptionPane.showMessageDialog(this, getResString("licenseImported"), getResString("validLicense"), JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                //Hier kann es eigentlich nur noch eine Zugriffsverweigerung geben
                String error = getResString("licenseWriteAccesDenied1") + Tool3lgmConstants.APPLICATION_DIR.getAbsoluteFile() + getResString("licenseWriteAccesDenied2");
                JOptionPane.showMessageDialog(this, error, getResString("licenseWriteAccesDenied"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * @return
     */
    public final boolean checkLicenses() {
        if (true) {
            return true;
        }
        try {
            //            if (TwatdLicenseLibrary.checkHostName("imise.uni-leipzig.de", "medizin.uni-leipzig.de", "AAA2011")) {
            //                return true;
            //            }
        } catch (Exception e) {
        }
        FileNameExtensionFilterAndFileFilter licenseFileFilter = new FileNameExtensionFilterAndFileFilter(Tool3lgmConstants.getFileNameExtensionFilter(FileFilterType.LIC), false);
        File[] licenseFilesArray = Tool3lgmConstants.APPLICATION_DIR.listFiles(licenseFileFilter);
        List<File> licenseFiles = new ArrayList<>();
        for (File licenseFile : licenseFilesArray) {
            if (licenseFile.isFile()) {
                licenseFiles.add(licenseFile);
            }
        }
        if (licenseFiles.size() > 0) {
            String[] errorMessages = new String[licenseFiles.size()];
            for (int i = 0; i < licenseFiles.size(); i++) {
                try {
                    if ((errorMessages[i] = getLicenseError(licenseFiles.get(i))) == null) {
                        return true;
                    }
                } catch (Exception ex) {
                    errorMessages[i] = ex.getMessage();
                }
            }
            String errorMessage = errorMessages.length > 1 ? getResString("multipleInvalidLicenses") + "\n\n" : "";
            for (int i = 0; i < errorMessages.length; i++) {
                errorMessage += licenseFiles.get(i).getName() + ": " + errorMessages[i] + "\n\n";
            }
            JOptionPane.showMessageDialog(this, errorMessage, getResString("invalidLicense"), JOptionPane.ERROR_MESSAGE);
        }
        int answer = JOptionPane.showConfirmDialog(this, getResString("lizenzfrage"), getResString("tool3lgm"), JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            importLicenseFile();
            return checkLicenses();
        }
        return false;
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
            doc.distributeEvent(GraphDocument.SELECTION_CHANGED, PID);
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
            doc.distributeEvent(GraphDocument.SELECTION_CHANGED, PID);
        } else {
            String[] newParams = new String[params.length + 1];
            newParams[0] = command;
            System.arraycopy(params, 0, newParams, 1, params.length);
            processCommand("open", newParams);
        }
    }

}