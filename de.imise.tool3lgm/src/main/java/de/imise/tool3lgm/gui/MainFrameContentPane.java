package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_CHECK_CONSISTENCY;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ENABLE_EXPERT_MODE;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_MODEL_BROWSER;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_PAINTING_TOOLBAR;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_STANDARD_TOOLBAR;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_TEMPLATE_BROWSER;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.help.CSH;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.consistency.checker.ConsistencyChecker;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.newmatrixview.MatrixViewInternalFrame;
import de.imise.tool3lgm.graphtools.view.browser.ModelBrowserPanel;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.ViewParameter;
import de.imise.tool3lgm.graphtools.view.template.TemplateBrowserPanel;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * @author AXS (6 Aug 2019)
 */
public class MainFrameContentPane extends JPanel implements PropertyChangeListener, InternalFrameListener, ComponentListener {

    /** Panel with verticalSplitPane and werkzeugleiste */
    private final JPanel workarea = new JPanel();

    /** ToolBar with general tools */
    private final MainFrameToolBar mainFrameToolbar = new MainFrameToolBar();

    /** parent der Toolbar Workarea == unten, mainFrameToolbar = oben in der Haupt-Toolbar */
    private final Container graphFrameToolbarParent = workarea; // auch unten wie beim MatrixView
    //private final Container graphFrameToolbarParent = mainFrameToolbar; //oben in der HauptToolbar
    private final Container matrixFrameToolbarParent = workarea;

    /** Aktualisiert die Toolbar je nach Kontext des aktiven Frames */
    private final InternalFrameToolbarManager internalFrameToolbarManager = new InternalFrameToolbarManager(graphFrameToolbarParent, matrixFrameToolbarParent);

    /** splitted pane with modelBrowserPanel on the left and desktop on the right */
    private final JSplitPane leftSplitPane;

    /** splitted pane with desktop on the left and TemplateBrowserPanel on the right */
    private JSplitPane rightSplitPane;

    /** splitted pane with modelBrowserPanel and the graph on the top and the error table bottom */
    private JSplitPane bottomSplitPane;

    /** panel to hold one or more modelBrowsers */
    private final ModelBrowserPanel modelBrowserPanel;

    /** panel to hold one or more modelBrowsers */
    private TemplateBrowserPanel templateBrowserPanel;

    /** contain all windows of opened documents (JDesktopPane is a container used to create a multiple-document interface or a virtual desktop) */
    private final JDesktopPane desktop;

    /** if the desktop size changes the frames will be resized too */
    private int desktopWidth = -1;
    private int desktopHeight = -1;

    /** InternalFrame in desktop, which has the focus */
    private AbstractInternalFrame activeFrame = null;

    /** Position of divider betweeen the tree and the graph view in pixel from the left side */
    private int leftDividerLocation = getToolkit().getScreenSize().width / 5;

    /** Position of divider betweeen the tree and the graph view in pixel from the left side */
    private int rightDividerLocation = getToolkit().getScreenSize().width - leftDividerLocation;

    /**
     * Diese Variable wird in <code>setSelectedDoc(LGMGraphDocument, boolean)</code> gebraucht,
     * um beim Aktivieren eines Matix-Fensters zwar den dazugehörigen ModelBrowser in den
     * Vordergrund zu bringen (wenn er noch nicht im Vordergrund ist), aber nicht den Grafischen
     * View des Teilmodells, weil ja dann das Matrix-Fenster sofort nicht mehr im Vordergrund
     * wäre.
     */
    private boolean activateGraphView = true;

    /**
     *
     */
    public MainFrameContentPane() {
        workarea.setLayout(new BorderLayout());
        modelBrowserPanel = new ModelBrowserPanel();

        desktop = new JDesktopPane();
        desktop.addComponentListener(this); //resize desktop -> resize frames

        JScrollPane desktopscroll = new JScrollPane(desktop);
        leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, modelBrowserPanel, desktopscroll);
        leftSplitPane.setOneTouchExpandable(true);
        leftSplitPane.setDividerSize(10);
        leftSplitPane.setDividerLocation(leftDividerLocation);

        // Direkthilfe für die einzelnen Baukastenteile
        CSH.setHelpIDString(modelBrowserPanel, "uebersicht_modellbrowser");
        checkConsistencyTableVisibility();
        checkTemplateBrowserVisibility();

        setLayout(new BorderLayout());
        add(workarea, BorderLayout.CENTER);
        //add(new StatusBar(), BorderLayout.SOUTH);

        setShowStandardToolbar();
        UserProperties.addPropertyChangeListener(this);
    }

    /**
     * @return das gerade aktive Interne Fenster
     */
    public final AbstractInternalFrame getActiveFrame() {
        return activeFrame;
    }

    /**
     *
     */
    private void setShowStandardToolbar() {
        if (OPTION_SHOW_STANDARD_TOOLBAR.is()) {
            add(mainFrameToolbar, BorderLayout.NORTH);
        } else {
            remove(mainFrameToolbar);
        }
        workarea.revalidate();
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (OPTION_CHECK_CONSISTENCY.isChanged(evt)) {
            checkConsistencyTableVisibility();
        } else if (OPTION_SHOW_TEMPLATE_BROWSER.isChanged(evt)) {
            checkTemplateBrowserVisibility();
        } else if (OPTION_SHOW_MODEL_BROWSER.isChanged(evt)) {
            checkModelBrowserVisibility();
        } else if (OPTION_SHOW_PAINTING_TOOLBAR.isChanged(evt)) {
            internalFrameToolbarManager.setToolBarVisibility();
        } else if (OPTION_SHOW_STANDARD_TOOLBAR.isChanged(evt)) {
            setShowStandardToolbar();
        } else if (OPTION_ENABLE_EXPERT_MODE.isChanged(evt)) {
            modelBrowserPanel.updateModelBrowsers();
        }
    }

    /**
     * Je nachdem, ob in den UserProperties die Konsitenzprüfung ein- oder ausgeschaltet ist, wird sie hier durchgeführt und die Fehlertabelle
     * angezeigt.
     *
     * @return <code>true</code>, wenn dei Konsistenzprüfung durchgeführt und angezeigt wurde
     */
    private void checkConsistencyTableVisibility() {
        boolean isCheckConsistency = OPTION_CHECK_CONSISTENCY.is() && Static.getSelectedGDCollection() != null;
        ConsistencyChecker consistencyChecker = ConsistencyChecker.getConsistencyChecker();
        JSplitPane topComponent = rightSplitPane != null ? rightSplitPane : leftSplitPane;
        if (!isCheckConsistency) {
            consistencyChecker.resetConsistencyDefinition();
            if (topComponent.getParent() == workarea) {
                return;
            }
            if (bottomSplitPane != null) {
                workarea.remove(bottomSplitPane);
            }
            workarea.add(topComponent, BorderLayout.CENTER);
        } else {
            if (bottomSplitPane == null) {
                bottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topComponent, new JScrollPane(consistencyChecker.getErrorTable()));
                bottomSplitPane.setOneTouchExpandable(true);
                bottomSplitPane.setDividerSize(10);
                bottomSplitPane.setDividerLocation(workarea.getHeight() / 4 * 3);
                workarea.add(bottomSplitPane, BorderLayout.CENTER);
            }
        }
        revalidate();
        repaint();
    }

    /** (De-)Aktiviert den TemplateBrowser */
    private final void checkTemplateBrowserVisibility() {
        boolean isCheckConsistency = OPTION_CHECK_CONSISTENCY.is() && Static.getSelectedGDCollection() != null;
        if (OPTION_SHOW_TEMPLATE_BROWSER.is()) {
            if (rightSplitPane != null) {
                return; //das Ding ist nur null, wenn der templateBroweser nicht angezeigt wird
            }
            if (templateBrowserPanel == null) {
                templateBrowserPanel = new TemplateBrowserPanel();
            }
            rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, templateBrowserPanel);
            rightSplitPane.setOneTouchExpandable(true);
            rightSplitPane.setDividerSize(10);
            rightSplitPane.setDividerLocation(rightDividerLocation);
            if (!isCheckConsistency) {
                workarea.add(rightSplitPane, BorderLayout.CENTER);
            } else {
                int dividerLocation = bottomSplitPane.getDividerLocation();
                bottomSplitPane.setTopComponent(rightSplitPane);
                bottomSplitPane.setDividerLocation(dividerLocation);
            }
        } else {
            if (rightSplitPane == null) {
                return;
            }
            rightDividerLocation = rightSplitPane.getDividerLocation();
            workarea.remove(rightSplitPane);
            if (!isCheckConsistency) {
                workarea.add(leftSplitPane, BorderLayout.CENTER);
            } else {
                int dividerLocation = bottomSplitPane.getDividerLocation();
                bottomSplitPane.setTopComponent(leftSplitPane);
                bottomSplitPane.setDividerLocation(dividerLocation);
            }
            rightSplitPane = null;
        }
        revalidate();
        repaint();
    }

    /** (De-)Aktiviert den ModelBrowser */
    private final void checkModelBrowserVisibility() {
        if (OPTION_SHOW_MODEL_BROWSER.is()) {
            leftSplitPane.setLeftComponent(modelBrowserPanel);
            leftSplitPane.setDividerLocation(leftDividerLocation);
        } else {
            leftDividerLocation = leftSplitPane.getDividerLocation();
            leftSplitPane.remove(leftSplitPane.getLeftComponent());
        }
        revalidate();
    }

    /**
     * Fügt dem ModelBrowser das übergebene Modell hinzu
     *
     * @param gdcoll Modell, das hinzugefügt werden soll
     */
    public void addCollection(final GDCollection gdcoll) {
        modelBrowserPanel.addCollection(gdcoll);
        gdcoll.addAllTransactionsListener(new LGMChangeListenerSimple() {
            @Override
            public void modelOrSzenarioNameChanged(final GraphDocument source) {
                modelBrowserPanel.updateModelBrowsers();
            }
        });
    }

    @Override
    public void setCursor(final Cursor cursor) {
        if (activeFrame != null) {
            activeFrame.setCursor(cursor);
        }
        super.setCursor(cursor);
    }

    /**
     * @param doc
     * @return
     */
    public InternalGraphFrame createGraphFrame(final GraphDocument doc) {
        InputGraphArea area = new InputGraphArea(doc);
        InternalGraphFrame frame = new InternalGraphFrame(desktop, area, doc);
        modelBrowserPanel.addGraphDocument(doc);
        frame.addInternalFrameListener(this);
        frame.setBounds(desktop.getBounds());
        if (doc instanceof Szenario) {
            setWorkArea(frame);
        }
        desktop.add(frame);
        frame.setVisible(true);
        return frame;
    }

    /**
     * Create new MatrixViewFrame and add it to parent GraphDocument
     *
     * @param doc Sub-Model as source for the MatrixView
     * @return boolean with true, if methode run successful
     */
    public boolean createTableInternalFrame(final GraphDocument doc) {
        if (doc == null) {
            return false;
        }
        int nextMatrixViewTitleIndex = getNextMatrixViewTitleIndex(doc);
        MatrixViewInternalFrame matrixView = new MatrixViewInternalFrame(doc, internalFrameToolbarManager, nextMatrixViewTitleIndex);
        matrixView.setBounds(desktop.getBounds());
        desktop.add(matrixView);
        matrixView.addInternalFrameListener(this);
        matrixView.setVisible(true);
        desktop.setSelectedFrame(matrixView);
        return true;
    }

    /**
     * @param doc
     * @return
     */
    private int getNextMatrixViewTitleIndex(final GraphDocument doc) {
        JInternalFrame[] frames = desktop.getAllFrames();
        int max = 1;
        for (int i = 0; i < frames.length; i++) {
            if (frames[i] instanceof MatrixViewInternalFrame) {
                MatrixViewInternalFrame matrixFrame = (MatrixViewInternalFrame) frames[i];
                if (matrixFrame.getGraphDocument() == doc) {
                    if (matrixFrame.titleIndex >= max) {
                        max = matrixFrame.titleIndex + 1;
                    }
                }
            }
        }
        return max;
    }

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
        boolean multiView = view.multiView;
        bgp.setMultiView(multiView);
        frame.getGraphDocument().getCollection().setActiveLayer(view.activeLayer);
        bgp.setMultiViewLayerAngle(view.layerAngle);
        bgp.setMultiViewLayerGap(view.layerGap);
        bgp.setZoom(view.zoom);
        frame.getScrollPane().getViewport().setViewPosition(new Point(view.viewPositionX, view.viewPositionY));
    }

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
    public void setCurrentDoc(final GraphDocument doc, final boolean activateGraphView) {
        //das doc kann null sein, wenn eine Datei geladen wird und das ModelBrowserPanel grade mit den
        //geladenen Teilmodellen gefüllt wird. Im ModelBrowserPanel wird bei jedem Hinzufügen eines
        //Teilmodell-Tabs immer diese Funktion hier aufgerufen.
        //Es kann auch null sein, wenn das letzte Modell geschlossen wurde
        if (doc == null) {
            return;
        }

        //wenn der interne Frame mit dem grafischen View in den Vordergrund geholt werden soll,
        if (activateGraphView) {
            //wenn nicht grade vorher ein Matrix-View aktiviert wurde (nur dann wäre die globale Variable==false)
            if (this.activateGraphView) {
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
                this.activateGraphView = true;
            }
            //wenn ein Matrix-View nach vorne geholt wurde und somit nicht der grafische View aktiviert
            //werden soll -> statisch diesen Fakt merken, so dass beim Wechel des ModelBrowsers, nicht
            //doch der GraphView nach vorne geholt wird
        } else {
            this.activateGraphView = false;
        }

        modelBrowserPanel.setCurrentDoc(doc);
        //beim nächsten Konextwechsel auch das nach Vorne holen des grafischen Views wieder einschalten
        this.activateGraphView = true;

        checkConsistencyTableVisibility();
    }

    /**
     * return all InternalFrames at desktop
     *
     * @return JInternalFrame[]
     */
    public final AbstractInternalFrame[] getAllFrames() {
        JInternalFrame[] intFrames = desktop.getAllFrames();
        AbstractInternalFrame[] frames = new AbstractInternalFrame[intFrames.length];
        System.arraycopy(intFrames, 0, frames, 0, intFrames.length);
        return frames;
    }

    /**
     * @param szen
     * @return
     */
    private final AbstractInternalFrame findFirstInternalFrame(final GraphDocument szen) {
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
    public final void closeFrame(final GraphDocument szen) {
        AbstractInternalFrame frame = findFirstInternalFrame(szen);
        while (frame != null) {
            LastAndNextViewManager.removeWindow(frame);
            frame.dispose();
            frame = findFirstInternalFrame(szen);
        }
        modelBrowserPanel.removeGraphDocument(szen);
    }

    /**
     * @param gdcoll
     */
    public void closeAllFramesAndTabs(final GDCollection gdcoll) {
        modelBrowserPanel.removeGraphDocument(gdcoll.getMainGraphDocument());
        closeFrame(gdcoll.getMainGraphDocument());
        for (Szenario szen : gdcoll.getSzenarios()) {
            closeFrame(szen);
        }
    }

    /**
     * ordnet alle InternalFrames neu an (überlappt)
     */
    public void reorderFramesWithOverlap() {
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

    /**
     * ordnet alle InternalFrames neu an (nebeneinander)
     */
    public void reorderFramesSideBySide() {
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

    /////////////////////////
    // InternalFrameListener //
    ///////////////////////////

    @Override
    public void internalFrameClosing(final InternalFrameEvent e) {
    }

    @Override
    public void internalFrameClosed(final InternalFrameEvent e) {
        JInternalFrame[] frames = desktop.getAllFrames();
        AbstractInternalFrame frame = (AbstractInternalFrame) e.getSource();
        workarea.revalidate();
        LastAndNextViewManager.removeWindow(frame);
        if (frames.length == 0) {
            activeFrame = null;
        }
        internalFrameToolbarManager.updateToolBar();
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
        GraphDocument doc = activeFrame.getGraphDocument();
        doc.addClosedTransactionsListener(internalFrameToolbarManager);
        internalFrameToolbarManager.updateToolBar();
        //wenn es ein Grafikfenster aktiviert wurde, soll es intern auch in den Vordergrund geholt werden. Bei
        //allen anderen Fenstern (Matrix-Sicht-Fenster), soll dieses Fenster im Vordergrund bleiben.
        setCurrentDoc(doc, activeFrame instanceof InternalGraphFrame);
        try {
            activeFrame.setSelected(true);
        } catch (Exception ex) {
            Log.show(Log.FATAL, getResString("FehlerAllgemein"), ex);
        }
        workarea.revalidate();
        workarea.repaint();
        LastAndNextViewManager.addWindow(activeFrame);
        mainFrameToolbar.update();
    }

    @Override
    public void internalFrameDeactivated(final InternalFrameEvent e) {
        GraphDocument doc = activeFrame.getGraphDocument();
        doc.removeClosedTransactionsListener(internalFrameToolbarManager);
        activeFrame = null;
        internalFrameToolbarManager.updateToolBar();
        mainFrameToolbar.update();
    }

    //////////////////////////////////////////////
    // resize desktop -> resize internal frames //
    //////////////////////////////////////////////

    /**
     * @return all internal frames with the 0 position and max width of the desktop
     */
    private Iterable<JInternalFrame> getFramesWithMaxSize() {
        Set<JInternalFrame> framesWithMaxSize = new HashSet<>();
        for (JInternalFrame frame : desktop.getAllFrames()) {
            Point location = frame.getLocation();
            if (location.x == 0 && location.y == 0) {
                Rectangle frameBounds = frame.getBounds();
                if (frameBounds.width == desktopWidth && frameBounds.height == desktopHeight) {
                    framesWithMaxSize.add(frame);
                }
            }
        }
        return framesWithMaxSize;
    }

    /**
     * Resize all given frames to the maximum with of the desktop
     *
     * @param frames
     */
    private void setFramesToMaxSize(final Iterable<JInternalFrame> frames) {
        for (JInternalFrame frame : frames) {
            Rectangle frameBounds = frame.getBounds();
            frameBounds.width = desktopWidth;
            frameBounds.height = desktopHeight;
            frame.setBounds(frameBounds);
        }
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        Object source = e.getSource();
        if (source == desktop) {
            if (desktopWidth != -1) {
                Iterable<JInternalFrame> framesWithMaxSize = getFramesWithMaxSize();
                desktopWidth = desktop.getWidth();
                desktopHeight = desktop.getHeight();
                setFramesToMaxSize(framesWithMaxSize);
            } else {
                desktopWidth = desktop.getWidth();
                desktopHeight = desktop.getHeight();
            }
        }
    }

    @Override
    public void componentMoved(final ComponentEvent e) {
        //do nothing
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
