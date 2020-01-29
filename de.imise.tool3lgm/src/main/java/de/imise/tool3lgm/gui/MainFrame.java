package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;

import javax.help.CSH;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.WindowConstants;

import com.google.common.base.Strings;

import de.imise.tool3lgm.KeyStrokes;
import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmChangeListener;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.gui.menu.MenuBar;
import de.imise.tool3lgm.help.Help;
import de.imise.tool3lgm.userproperties.UserProperties.IntProperty;

/**
 * Hauptfenster der Anwendung. Das hier ist alles aus Tool3lgm herausgelöst, das bis dahin das Hauptfenster war. Aber es hat noch eine Menge
 * Controller-Funktionen. Daher die Trennung.
 *
 * @author AXS (9 Aug 2019)
 */
public class MainFrame extends JFrame implements Tool3lgmChangeListener, ComponentListener {

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

        contentPane = new MainFrameContentPane();
        getContentPane().add(contentPane);

        addAsToolChangeListener();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // Direkthilfe einschalten
        Help.getHelp().enableHelpKey(rootPane, "willkommen");

        menuBar = new MenuBar();
        CSH.setHelpIDString(menuBar, "uebersicht_menueleiste");
        KeyStrokes.registerPublicKeyStrokes(getRootPane());
        setJMenuBar(menuBar);
        addAsToolChangeListener();
        updateTitle();
        addComponentListener(this);
        restorePositionAndSizeFromUserProperties();
        setVisible(visible);
    }

    @Override
    public void dispose() {
        Static.getTool().close();
    }

    /**
     * Sets the corresponding UserPropertiy values for the screen index, the width and the height.
     */
    public void savePositionAndSizeInUserProperties() {
        Rectangle bounds = getBounds();
        IntProperty.PROPERTY_INT_MAINFRAME_SCREEN_POSX.set(bounds.x);
        IntProperty.PROPERTY_INT_MAINFRAME_SCREEN_POSY.set(bounds.y);
        IntProperty.PROPERTY_INT_MAINFRAME_SCREEN_WIDTH.set(bounds.width);
        IntProperty.PROPERTY_INT_MAINFRAME_SCREEN_HEIGHT.set(bounds.height);
    }

    /**
     * Restores the screen index, the width and the height from the corresponding UserPropertiy values.
     */
    private void restorePositionAndSizeFromUserProperties() {
        int frameX = IntProperty.PROPERTY_INT_MAINFRAME_SCREEN_POSX.get();
        int frameY = IntProperty.PROPERTY_INT_MAINFRAME_SCREEN_POSY.get();
        int frameWidth = IntProperty.PROPERTY_INT_MAINFRAME_SCREEN_WIDTH.get();
        int frameHeight = IntProperty.PROPERTY_INT_MAINFRAME_SCREEN_HEIGHT.get();
        Rectangle screenBounds = getGraphicsConfiguration().getBounds();
        if (frameWidth < 20 || frameHeight < 20) { //don't allow windows smaller 20 in one dimension
            setBounds(screenBounds);
        } else {
            setBounds(frameX, frameY, frameWidth, frameHeight);
        }
    }

    /**
     *
     */
    private void updateTitle() {
        GDCollection gdcoll = Static.getSelectedGDCollection();
        String metaModelName = null;
        if (gdcoll != null) {
            MetaModel metaModel = gdcoll.getMetaModel();
            MetaModelContext metaModelContext = metaModel.getMetaModelContext();
            metaModelName = metaModelContext.getMetaModelDisplayName();
        }
        String title = getResString("tool3lgm");
        if (!Strings.isNullOrEmpty(metaModelName)) {
            title += " " + getResString("tool3lgm_title_extension") + " " + metaModelName;
        }
        setTitle(title);
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
    private void addCollection(final GDCollection gdcoll) {
        contentPane.addCollection(gdcoll);
        Static.setProgressDialogStatusLabel("create_frame", gdcoll.getMainGraphDocument().getTitle());
        createGraphFrame(gdcoll.getMainGraphDocument());

        for (int i = 0; i < gdcoll.getSzenarioCount(); i++) {
            Szenario szen = gdcoll.getSzenario(i);
            Static.setProgressDialogStatusLabel("create_frame", szen.getTitle());
            createGraphFrame(szen);
        }
    }

    /**
     * @param doc
     * @return
     */
    private InternalGraphFrame createGraphFrame(final GraphDocument doc) {
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
    public JInternalFrame[] getAllFrames() {
        return contentPane.getAllFrames();
    }

    /**
     * @return Returns the activeFrame.
     */
    public AbstractInternalFrame getActiveFrame() {
        return contentPane.getActiveFrame();
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

    //    /** Holt den erstbesten Frame in den Vordergrund (und damit das dazugehörige Doc) */
    //    private void selectLastFrame() {
    //        contentPane.selectLastFrame();
    //    }
    //
    ////////////////////////////
    // Tool3lgmChangeListener //
    ////////////////////////////

    @Override
    public void model_change_model_opened(final GraphDocument source) {
        GDCollection gdcoll = source.getCollection();
        addCollection(gdcoll);
    }

    @Override
    public void model_change_model_closed(final GraphDocument source) {
        GDCollection gdcoll = source.getCollection();
        contentPane.closeAllFramesAndTabs(gdcoll);
        LastAndNextViewManager.selectLastFrame();
    }

    @Override
    public void model_change_model_saved(final GraphDocument source) {
        updateTitle();
    }

    @Override
    public void model_change_selected_szenario_changed(final GraphDocument source) {
        updateTitle();
        contentPane.setCurrentDoc(source, source != null);
    }

    @Override
    public void model_change_szenario_added(final GraphDocument source) {
        try {
            contentPane.createGraphFrame(source);
        } catch (Exception e) {
            //dieses try-catch muss sein, weil beim Anlegen eines neuen Modells immer auch ein Szenario
            //geaddet wird, für dessen Collection es aber noch keinen ModelBrowser gibt, so dass es nicht
            //geht. Bei neuen Modellen werden GraphFrames in der Funktion addCollection(GDCollection)
            //hinzugefügt.
        }
    }

    @Override
    public void model_change_szenario_removed(final GraphDocument source) {
        contentPane.closeFrame(source);
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

    ///////////////////////
    // ComponentListener //
    ///////////////////////

    @Override
    public void componentResized(final ComponentEvent e) {
        savePositionAndSizeInUserProperties();
    }

    @Override
    public void componentMoved(final ComponentEvent e) {
        savePositionAndSizeInUserProperties();
    }

    @Override
    public void componentShown(final ComponentEvent e) {
        //do nothing
    }

    @Override
    public void componentHidden(final ComponentEvent e) {
        //do nothing
    }

}
