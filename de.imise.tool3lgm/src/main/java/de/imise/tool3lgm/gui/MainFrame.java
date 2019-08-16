package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.help.CSH;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.google.common.base.Strings;

import de.imise.tool3lgm.KeyStrokes;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.gui.menu.MenuBar;
import de.imise.tool3lgm.help.Help;
import de.imise.util.robot.ScreenRobot;

/**
 * Hauptfenster der Anwendung. Das hier ist alles aus Tool3lgm herausgelöst, das bis dahin das Hauptfenster war. Aber es hat noch eine Menge
 * Controller-Funktionen. Daher die Trennung.
 *
 * @author AXS (9 Aug 2019)
 */
public class MainFrame extends JFrame implements WindowListener {

    /** Menü-Leiste des Tools */
    private final MenuBar menuBar;

    /** Pane, das in das ContentPane dieses Frames gelegt wird */
    private final MainFrameContentPane contentPane;

    /**
     * @param visible
     */
    public MainFrame(final boolean visible) {
        setIconImage(Tool3lgmConstants.getIcon("toolIcon.gif").getImage());
        setTitle(null);

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

        contentPane.selectedDocChanged();

        setVisible(visible);
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
     * Fügt dem ModelBrowser das übergebene Modell hinzu
     *
     * @param gdcoll Modell, das hinzugefügt werden soll
     */
    public GraphDocument addCollection(final GDCollection gdcoll) {
        contentPane.addCollection(gdcoll);
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
        return selectedDoc;
    }

    /**
     * @param doc
     * @return
     */
    public InternalGraphFrame createFrame(final GraphDocument doc) {
        //TODO: das hier sollte von außen nicht augerufen werden, sondern das sollte über den (oder einen neuen anderen) GDCollectionChangeListener laufen (SZENARIO_ADDED)
        return contentPane.createGraphFrame(doc);
    }

    /**
     * Create new MatrixViewFrame and add it to parent GraphDocument
     *
     * @param doc Sub-Model as source for the MatrixView
     * @return boolean with true, if methode run successful
     */
    public boolean createTableInternalFrame(final LGMGraphDocument doc) {
        return contentPane.createTableInternalFrame(doc);
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
     * @return Returns the activeFrame.
     */
    public AbstractInternalFrame getActiveFrame() {
        return contentPane.getActiveFrame();
    }

    /**
     * Wechselt den Kontext auf das übergebene Teilmodell. In jedem Fall wird der <code>ModelBrowser</code> des aktivierten Teilmodells in den
     * Vordergrund gebracht.
     *
     * @param doc
     *            Teilmodell, in dessen Kontext gewechselt werden soll
     */
    public void setCurrentDoc(final GraphDocument doc) {
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

    //TODO: das hier sollte von außen nicht augerufen werden, sondern das sollte über den (oder einen neuen anderen) GDCollectionChangeListener laufen (SZENARIO_REMOVED)
    /**
     * @param szen
     */
    public void closeFrame(final GraphDocument szen) {
        contentPane.closeFrame(szen);
    }

    //TODO: das hier sollte von außen nicht augerufen werden, sondern das sollte über den (oder einen neuen anderen) GDCollectionChangeListener laufen (MODEL_CLOSED) (analog sollte es dann auch MODEL_OPENED geben)
    /**
     * @param gdcoll
     */
    public void closeAllFramesAndTabs(final GDCollection gdcoll) {
        contentPane.closeAllFramesAndTabs(gdcoll);
    }

    /** Holt den erstbesten Frame in den Vordergrund (und damit das dazugehörige Doc) */
    public void selectLastFrame() {
        contentPane.selectLastFrame();
    }

    ///////////////////
    // MouseListener //
    ///////////////////

    @Override
    public synchronized void addMouseListener(final MouseListener l) {
        contentPane.addMouseListener(l);
        super.addMouseListener(l);
    }

    @Override
    public synchronized void removeMouseListener(final MouseListener l) {
        contentPane.removeMouseListener(l);
        super.removeMouseListener(l);
    }

    ////////////////////
    // WindowListener //
    ////////////////////

    @Override
    public void windowClosing(final WindowEvent e) {
        Static.getTool().close();
    }

    @Override
    public void windowActivated(final WindowEvent e) {
        Static.contextGenerator.changeContext(Static.getSelectedDoc());
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

}
