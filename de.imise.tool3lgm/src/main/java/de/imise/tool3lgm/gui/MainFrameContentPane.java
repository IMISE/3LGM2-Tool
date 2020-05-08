package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_CHECK_CONSISTENCY;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ENABLE_EXPERT_MODE;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_MODEL_BROWSER;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_PAINTING_TOOLBAR;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_STANDARD_TOOLBAR;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_TEMPLATE_BROWSER;
import static de.imise.tool3lgm.userproperties.UserProperties.IntProperty.PROPERTY_INT_GRAPHVIEW_CONSISTENCY_TABLE_DIVIDER_LOCATION;
import static de.imise.tool3lgm.userproperties.UserProperties.IntProperty.PROPERTY_INT_GRAPHVIEW_TEMPLATEBROWSER_DIVIDER_LOCATION;
import static de.imise.tool3lgm.userproperties.UserProperties.IntProperty.PROPERTY_INT_MODELBRWOSER_GRAPHVIEW_DIVIDER_LOCATION;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.help.CSH;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.consistency.checker.ConsistencyChecker;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.newmatrixview.MatrixViewInternalFrame;
import de.imise.tool3lgm.graphtools.view.browser.ModelBrowserPanel;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.ViewParameter;
import de.imise.tool3lgm.graphtools.view.template.TemplateBrowserPanel;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.tool3lgm.userproperties.UserProperties.IntProperty;

/**
 * @author AXS (6 Aug 2019)
 */
public final class MainFrameContentPane extends JPanel implements PropertyChangeListener, InternalFrameListener {

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
    private final MainFrameDesktopPane desktop;

    /** InternalFrame in desktop, which has the focus */
    private AbstractInternalFrame activeFrame = null;

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

        desktop = new MainFrameDesktopPane();

        leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, modelBrowserPanel, desktop);
        leftSplitPane.setOneTouchExpandable(true);
        leftSplitPane.setDividerSize(10);

        // Direkthilfe für die einzelnen Baukastenteile
        CSH.setHelpIDString(modelBrowserPanel, "uebersicht_modellbrowser");
        checkModelBrowserVisibility();
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
        } else {
            String propertyName = evt.getPropertyName();
            if (propertyName.equals("dividerLocation")) {
                savePositionAndSizeInUserProperties();
            }
        }
    }

    /**
     * Sets the corresponding UserPropertiy values for the screen index, the width and the height.
     */
    private void savePositionAndSizeInUserProperties() {
        if (OPTION_SHOW_MODEL_BROWSER.is() && leftSplitPane != null && leftSplitPane.isVisible()) {
            int dividerLocation = leftSplitPane.getDividerLocation();
            IntProperty.PROPERTY_INT_MODELBRWOSER_GRAPHVIEW_DIVIDER_LOCATION.set(dividerLocation);
        }
        if (OPTION_SHOW_TEMPLATE_BROWSER.is() && rightSplitPane != null && rightSplitPane.isVisible()) {
            int dividerLocation = rightSplitPane.getDividerLocation();
            IntProperty.PROPERTY_INT_GRAPHVIEW_TEMPLATEBROWSER_DIVIDER_LOCATION.set(dividerLocation);
        }
        if (OPTION_CHECK_CONSISTENCY.is() && bottomSplitPane != null && bottomSplitPane.isVisible()) {
            int dividerLocation = bottomSplitPane.getDividerLocation();
            IntProperty.PROPERTY_INT_GRAPHVIEW_CONSISTENCY_TABLE_DIVIDER_LOCATION.set(dividerLocation);
        }
    }

    /**
     * Restores the screen index, the width and the height from the corresponding UserPropertiy values.
     */
    private void restorePositionAndSizeFromUserProperties() {
        setDividerLocation(OPTION_SHOW_MODEL_BROWSER, PROPERTY_INT_MODELBRWOSER_GRAPHVIEW_DIVIDER_LOCATION, leftSplitPane, 0.2d, true);
        setDividerLocation(OPTION_SHOW_TEMPLATE_BROWSER, PROPERTY_INT_GRAPHVIEW_TEMPLATEBROWSER_DIVIDER_LOCATION, rightSplitPane, 0.8d, true);
        setDividerLocation(OPTION_CHECK_CONSISTENCY, PROPERTY_INT_GRAPHVIEW_CONSISTENCY_TABLE_DIVIDER_LOCATION, bottomSplitPane, 0.7d, false);
    }

    /**
     * @param dividerVisibleProperty
     * @param dividerLocationProperty
     * @param splitPane
     * @param mainFramePart
     * @param width
     */
    private void setDividerLocation(final BooleanProperty dividerVisibleProperty, final IntProperty dividerLocationProperty, final JSplitPane splitPane, final double mainFramePart, final boolean width) {
        if (dividerVisibleProperty.is() && splitPane != null) {
            int dividerLocation = dividerLocationProperty.get();
            int dividerSize = splitPane.getDividerSize();
            if (dividerLocation < dividerSize) {
                MainFrame mainFrame = Static.getMainFrame();
                if (mainFrame != null) {
                    int mainFrameSizeValue = width ? mainFrame.getWidth() : mainFrame.getHeight();
                    dividerLocation = (int) (mainFrameSizeValue * mainFramePart);
                }
            }
            splitPane.removePropertyChangeListener(this);
            splitPane.setDividerLocation(dividerLocation);
            splitPane.addPropertyChangeListener(this);
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
                bottomSplitPane = null;
            }
            workarea.add(topComponent, BorderLayout.CENTER);
        } else {
            if (bottomSplitPane == null) {
                bottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topComponent, new JScrollPane(consistencyChecker.getErrorTable()));
                bottomSplitPane.setOneTouchExpandable(true);
                bottomSplitPane.setDividerSize(10);
                workarea.add(bottomSplitPane, BorderLayout.CENTER);
                restorePositionAndSizeFromUserProperties();
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
            if (!isCheckConsistency) {
                workarea.add(rightSplitPane, BorderLayout.CENTER);
            } else {
                int dividerLocation = bottomSplitPane.getDividerLocation();
                bottomSplitPane.setTopComponent(rightSplitPane);
                bottomSplitPane.setDividerLocation(dividerLocation);
            }
            restorePositionAndSizeFromUserProperties();
        } else {
            if (rightSplitPane == null) {
                return;
            }
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
            restorePositionAndSizeFromUserProperties();
        } else {
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
    public void createGraphFrame(final GraphDocument doc) {
        InternalGraphFrame frame = new InternalGraphFrame(doc);
        modelBrowserPanel.addGraphDocument(doc);
        frame.addInternalFrameListener(this);
        frame.setBounds(desktop.getBounds());
        if (doc instanceof Szenario) {
            setWorkArea(frame);
        }
        desktop.add(frame);
        frame.setLocation(0, 0);
        if (doc instanceof Szenario) {
            setBettterDefaultZoom(frame);
        }
        frame.setVisible(true);
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
                GraphDocument frameDoc = matrixFrame.getGraphDocument();
                if (frameDoc == doc) {
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
        InputGraphArea inputGraphArea = frame.getInputGraphArea();
        boolean multiView = view.multiView;
        inputGraphArea.setMultiView(multiView);
        GraphDocument doc = frame.getGraphDocument();
        GDCollection gdcoll = doc.getCollection();
        gdcoll.setActiveLayer(view.activeLayer);
        inputGraphArea.setMultiViewLayerAngle(view.layerAngle);
        inputGraphArea.setMultiViewLayerGap(view.layerGap);
        inputGraphArea.setZoom(view.zoom);
        JScrollPane scrollPane = frame.getScrollPane();
        JViewport viewport = scrollPane.getViewport();
        Point viewPosition = new Point(view.viewPositionX, view.viewPositionY);
        viewport.setViewPosition(viewPosition);
    }

    /**
     * @param frame
     */
    private void setBettterDefaultZoom(final InternalGraphFrame frame) {
        //raise default zoom to fill the whole screen
        InputGraphArea inputGraphArea = frame.getInputGraphArea();
        double zoom = inputGraphArea.getZoom();
        //if the zoom is equals to the initial zoom -> adjust zoom to max width
        boolean adjustZoom = zoom == ViewParameter.INITIAL_MIN_ZOOM;
        //if the model file is the example model file -> -> adjust zoom to max width
        if (!adjustZoom) {
            GDCollection gdcoll = frame.doc.getCollection();
            File file = gdcoll.getFile();
            adjustZoom = Tool3lgmConstants.EXAMPLE_MODEL_FILE.equals(file);
        }

        if (adjustZoom) {
            JViewport viewport = frame.getViewport();
            Dimension viewportSize = viewport.getSize();
            int w = viewportSize.width - BasicGraphArea.GRAPH_BORDER.left - BasicGraphArea.GRAPH_BORDER.right;
            inputGraphArea.setZoom(1d);
            Dimension inputGraphAreaPreferredSize = inputGraphArea.getPreferredSize();
            int w1 = inputGraphAreaPreferredSize.width;
            inputGraphArea.setZoom(2d);
            inputGraphAreaPreferredSize = inputGraphArea.getPreferredSize();
            int w2 = inputGraphAreaPreferredSize.width;

            int wDiff = w2 - w1;
            zoom = (double) w / wDiff;
            //default zoom is never smaller then the initial zoom
            zoom = Math.max(zoom, ViewParameter.INITIAL_MIN_ZOOM);
            inputGraphArea.setZoom(zoom);
        }
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
        //without the following (usally redundant) assignment sometimes activeFrame is null here
        //and this throws an exception. Usally only the deactivated frame can be active Frame
        if (activeFrame == null) {
            activeFrame = (AbstractInternalFrame) e.getInternalFrame();
        }
        GraphDocument doc = activeFrame.getGraphDocument();
        doc.removeClosedTransactionsListener(internalFrameToolbarManager);
        activeFrame = null;
        internalFrameToolbarManager.updateToolBar();
        mainFrameToolbar.update();
    }

}
