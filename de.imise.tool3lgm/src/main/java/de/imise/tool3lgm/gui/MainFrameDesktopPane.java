package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ENABLE_EXPERT_MODE;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_CONSISTENCY_TABLE;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_MODEL_BROWSER;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_PAINTING_TOOLBAR;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_STANDARD_TOOLBAR;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_TEMPLATE_BROWSER;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_VIEW_COMPONENT_TITLES;
import static de.imise.tool3lgm.userproperties.UserProperties.IntProperty.PROPERTY_INT_GRAPHVIEW_CONSISTENCY_TABLE_DIVIDER_LOCATION;
import static de.imise.tool3lgm.userproperties.UserProperties.IntProperty.PROPERTY_INT_GRAPHVIEW_TEMPLATEBROWSER_DIVIDER_LOCATION;
import static de.imise.tool3lgm.userproperties.UserProperties.IntProperty.PROPERTY_INT_MODELBRWOSER_GRAPHVIEW_DIVIDER_LOCATION;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import javax.help.CSH;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmChangeListener;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.consistency.ModelValidator;
import de.imise.tool3lgm.graphtools.consistency.SuggestShowConsistencyTableHandler;
import de.imise.tool3lgm.graphtools.consistency.tableview.ConsistencyErrorTablePane;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.browser.ModelBrowserPanel;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewParameter;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.graphtools.view.template.TemplateBrowserPanel;
import de.imise.tool3lgm.gui.internalframe.MainFrameDesktopInternalFramesPane;
import de.imise.tool3lgm.gui.tabbedframe.MainFrameDesktopTabbedPane;
import de.imise.tool3lgm.gui.viewpane.ViewPane;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponent;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponentListener;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponentParent;
import de.imise.tool3lgm.gui.viewpane.ViewPaneToolbarManager;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPane;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPaneFrameComponent;
import de.imise.tool3lgm.gui.viewpane.matrix.MatrixViewPaneFrameComponent;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.IntProperty;

/**
 * @author AXS (6 Aug 2019)
 */
public final class MainFrameDesktopPane extends JPanel implements PropertyChangeListener, ViewPaneFrameComponentListener, Tool3lgmChangeListener, LGMChangeListenerSimple {

    /** Panel with verticalSplitPane and werkzeugleiste */
    private final JPanel workarea = new JPanel();

    /** ToolBar with general tools */
    private final MainFrameToolBar mainFrameToolbar = new MainFrameToolBar();

    /** parent der Toolbar Workarea == unten, mainFrameToolbar = oben in der Haupt-Toolbar */
    private final Container graphFrameToolbarParent = workarea; // auch unten wie beim MatrixView
    //private final Container graphFrameToolbarParent = mainFrameToolbar; //oben in der HauptToolbar
    private final Container matrixFrameToolbarParent = workarea;

    /** Aktualisiert die Toolbar je nach Kontext des aktiven Frames */
    private final ViewPaneToolbarManager viewPaneToolbarManager = new ViewPaneToolbarManager(graphFrameToolbarParent, matrixFrameToolbarParent);

    /** splitted pane with modelBrowserPanel on the left and desktop on the right */
    private final JSplitPane leftSplitPane;

    /** splitted pane with desktop on the left and TemplateBrowserPanel on the right */
    private JSplitPane rightSplitPane;

    /** splitted pane with modelBrowserPanel and the graph on the top and the error table bottom */
    private JSplitPane bottomSplitPane;

    /** panel to hold one or more modelBrowsers */
    private final ModelBrowserPanel modelBrowserPanel;

    /** contain all windows of opened documents (JDesktopPane is a container used to create a multiple-document interface or a virtual desktop) */
    private final ViewPaneFrameComponentParent desktop;

    /** Frame component at desktop, which has the focus */
    private ViewPaneFrameComponent activeFrame = null;

    /** View component for the templates */
    private TemplateBrowserPanel templateBrowserPanel;

    /** The panel that displays the consistency error table */
    private JPanel consistencyErrorTableBorderPanel;

    /** The scroll pane with the consistency error table */
    private ConsistencyErrorTablePane consistencyErrorTablePane;

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
    public MainFrameDesktopPane() {
        workarea.setLayout(new BorderLayout());
        modelBrowserPanel = new ModelBrowserPanel();

        if (Tool3lgm.DESKTOP_WITH_TABS_INSTEAD_OF_INTERNAL_FRAMES) {
            desktop = new MainFrameDesktopTabbedPane();
        } else {
            desktop = new MainFrameDesktopInternalFramesPane();
        }
        leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, modelBrowserPanel, (JComponent) desktop);

        leftSplitPane.setOneTouchExpandable(true);
        leftSplitPane.setDividerSize(10);

        // Direkthilfe für die einzelnen Baukastenteile
        CSH.setHelpIDString(modelBrowserPanel, "uebersicht_modellbrowser");

        checkViewComponentsVisibility();
        setLayout(new BorderLayout());
        add(workarea, BorderLayout.CENTER);
        //add(new StatusBar(), BorderLayout.SOUTH);

        setShowStandardToolbar();
        UserProperties.addPropertyChangeListener(this);
        addAsToolChangeListener();
    }

    /**
     * @return das gerade aktive Interne Fenster
     */
    public final ViewPaneFrameComponent getActiveFrame() {
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
        if (OPTION_SHOW_CONSISTENCY_TABLE.isChanged(evt)) {
            checkViewComponentsVisibility();
        } else if (OPTION_SHOW_TEMPLATE_BROWSER.isChanged(evt)) {
            checkViewComponentsVisibility();
        } else if (OPTION_SHOW_MODEL_BROWSER.isChanged(evt)) {
            checkViewComponentsVisibility();
        } else if (OPTION_SHOW_VIEW_COMPONENT_TITLES.isChanged(evt)) {
            updateTitledBorders();
        } else if (OPTION_SHOW_PAINTING_TOOLBAR.isChanged(evt)) {
            viewPaneToolbarManager.setToolBarVisibility();
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
     * @return
     */
    private boolean isShowConsistencyTable() {
        return OPTION_SHOW_CONSISTENCY_TABLE.is();
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
        if (isShowConsistencyTable() && bottomSplitPane != null && bottomSplitPane.isVisible()) {
            int dividerLocation = bottomSplitPane.getDividerLocation();
            IntProperty.PROPERTY_INT_GRAPHVIEW_CONSISTENCY_TABLE_DIVIDER_LOCATION.set(dividerLocation);
        }
    }

    /**
     * Restores the screen index, the width and the height from the corresponding UserPropertiy values.
     */
    public void restorePositionAndSizeFromUserProperties() {
        setDividerLocation(OPTION_SHOW_MODEL_BROWSER.is(), PROPERTY_INT_MODELBRWOSER_GRAPHVIEW_DIVIDER_LOCATION, leftSplitPane, 0.2d, true);
        setDividerLocation(OPTION_SHOW_TEMPLATE_BROWSER.is(), PROPERTY_INT_GRAPHVIEW_TEMPLATEBROWSER_DIVIDER_LOCATION, rightSplitPane, 0.8d, true);
        setDividerLocation(isShowConsistencyTable(), PROPERTY_INT_GRAPHVIEW_CONSISTENCY_TABLE_DIVIDER_LOCATION, bottomSplitPane, 0.7d, false);
    }

    /**
     * @param dividerVisibleProperty
     * @param dividerLocationProperty
     * @param splitPane
     * @param mainFramePart
     * @param width
     */
    private void setDividerLocation(final boolean propertyValeShow, final IntProperty dividerLocationProperty, final JSplitPane splitPane, final double mainFramePart, final boolean width) {
        if (propertyValeShow && splitPane != null) {
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
     *
     */
    private void checkViewComponentsVisibility() {
        //the order is relevant!
        checkModelBrowserVisibility();
        checkConsistencyTableVisibility();
        checkTemplateBrowserVisibility();
        updateTitledBorders();
    }

    /**
     * Je nachdem, ob in den UserProperties die Konsitenzprüfung ein- oder ausgeschaltet ist, wird sie hier durchgeführt und die Fehlertabelle
     * angezeigt.
     *
     * @return <code>true</code>, wenn dei Konsistenzprüfung durchgeführt und angezeigt wurde
     */
    private void checkConsistencyTableVisibility() {
        boolean isCheckConsistency = isShowConsistencyTable();
        ModelValidator modelValidator = ModelValidator.getModelValidator();
        JSplitPane topComponent = rightSplitPane != null ? rightSplitPane : leftSplitPane;
        if (!isCheckConsistency) {
            modelValidator.resetConsistencyDefinition();
            if (topComponent.getParent() == workarea) {
                return;
            }
            if (bottomSplitPane != null) {
                workarea.remove(bottomSplitPane);
                bottomSplitPane = null;
                consistencyErrorTableBorderPanel = null;
                if (consistencyErrorTablePane != null) {
                    consistencyErrorTablePane.dispose();
                }
                consistencyErrorTablePane = null;
            }
            workarea.add(topComponent, BorderLayout.CENTER);
        } else {
            if (bottomSplitPane == null) {
                consistencyErrorTableBorderPanel = new JPanel(new BorderLayout());
                bottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topComponent, consistencyErrorTableBorderPanel);
                bottomSplitPane.setOneTouchExpandable(true);
                bottomSplitPane.setDividerSize(10);
                workarea.add(bottomSplitPane, BorderLayout.CENTER);
                restorePositionAndSizeFromUserProperties();
            }
        }
        //remove the table of the consistency error view
        if (consistencyErrorTableBorderPanel != null) {
            if (Static.getSelectedDoc() == null) {
                if (consistencyErrorTablePane != null) {
                    consistencyErrorTableBorderPanel.remove(consistencyErrorTablePane);
                    consistencyErrorTablePane.dispose(); //remove the ConsistencyErrorTableGenerator as PropertyChangeListener of global ModelValidator
                    consistencyErrorTablePane = null;
                }
            } else {
                if (consistencyErrorTablePane == null) {
                    consistencyErrorTablePane = new ConsistencyErrorTablePane(); //adds the ConsistencyErrorTableGenerator as PropertyChangeListener of global ModelValidator
                    consistencyErrorTableBorderPanel.add(consistencyErrorTablePane, BorderLayout.CENTER);
                }
            }
        }
        revalidate();
        repaint();
    }

    /** (De-)Aktiviert den TemplateBrowser */
    private final void checkTemplateBrowserVisibility() {
        boolean isCheckConsistency = isShowConsistencyTable();
        //show template browser
        if (OPTION_SHOW_TEMPLATE_BROWSER.is()) {
            if (rightSplitPane != null) {
                return; //das Ding ist nur null, wenn der templateBroweser nicht angezeigt wird
            }
            templateBrowserPanel = new TemplateBrowserPanel();
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
    private GraphViewPaneFrameComponent createGraphView(final GraphDocument doc) {
        GraphViewPaneFrameComponent graphView = desktop.createGraphView(doc);
        if (doc instanceof Szenario) {
            setWorkArea(graphView);
        }
        if (doc instanceof Szenario) {
            setBettterDefaultZoom(graphView);
        }
        return graphView;
    }

    /**
     * Create new MatrixViewFrame and add it to parent GraphDocument
     *
     * @param doc Sub-Model as source for the MatrixView
     * @return boolean with true, if methode run successful
     */
    public boolean createMatrixView(final GraphDocument doc) {
        int nextMatrixViewTitleIndex = getNextMatrixViewTitleIndex(doc);
        MatrixViewPaneFrameComponent matrixView = desktop.createMatrixView(doc, nextMatrixViewTitleIndex, viewPaneToolbarManager);
        return matrixView != null;
    }

    /**
     * @param doc
     * @return
     */
    private int getNextMatrixViewTitleIndex(final GraphDocument doc) {
        List<ViewPaneFrameComponent> frames = desktop.getViewPaneFrameComponents(doc);
        int max = 1;
        for (int i = 0; i < frames.size(); i++) {
            ViewPaneFrameComponent frame = frames.get(i);
            if (frame instanceof MatrixViewPaneFrameComponent) {
                MatrixViewPaneFrameComponent matrixFrame = (MatrixViewPaneFrameComponent) frame;
                GraphDocument frameDoc = matrixFrame.getGraphDocument();
                if (frameDoc == doc) {
                    int titleIndex = matrixFrame.getTitleIndex();
                    if (titleIndex >= max) {
                        max = titleIndex + 1;
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
    private void setWorkArea(final GraphViewPaneFrameComponent frame) {
        Szenario szenario = (Szenario) frame.getGraphDocument();
        GraphViewParameter graphViewParameter = szenario.getGraphViewParameter();
        InputGraphArea inputGraphArea = frame.getInputGraphArea();
        boolean multiView = graphViewParameter.multiView;
        inputGraphArea.setMultiView(multiView);
        GraphDocument doc = frame.getGraphDocument();
        GDCollection gdcoll = doc.getCollection();
        gdcoll.setActiveLayer(graphViewParameter.activeLayer);
        inputGraphArea.setMultiViewLayerAngle(graphViewParameter.layerAngle);
        inputGraphArea.setMultiViewLayerGap(graphViewParameter.layerGap);
        inputGraphArea.setZoom(graphViewParameter.zoom);
        ViewPane viewPane = frame.getViewPane();
        JScrollPane scrollPane = viewPane.getScrollPane();
        JViewport viewport = scrollPane.getViewport();
        Point viewPosition = new Point(graphViewParameter.viewPositionX, graphViewParameter.viewPositionY);
        viewport.setViewPosition(viewPosition);
    }

    /**
     * @param frame
     */
    private void setBettterDefaultZoom(final GraphViewPaneFrameComponent frame) {
        //raise default zoom to fill the whole screen
        InputGraphArea inputGraphArea = frame.getInputGraphArea();
        double zoom = inputGraphArea.getZoom();
        //if the zoom is equals to the initial zoom -> adjust zoom to max width
        boolean adjustZoom = zoom == GraphViewParameter.INITIAL_MIN_ZOOM;
        //if the model file is the example model file -> -> adjust zoom to max width
        if (!adjustZoom) {
            GDCollection gdcoll = frame.getCollection();
            File file = gdcoll.getFile();
            adjustZoom = Tool3lgmConstants.EXAMPLE_MODEL_FILE.equals(file);
        }

        if (adjustZoom) {
            ViewPane viewPane = frame.getViewPane();
            JScrollPane scrollPane = viewPane.getScrollPane();
            JViewport viewport = scrollPane.getViewport();
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
            zoom = Math.max(zoom, GraphViewParameter.INITIAL_MIN_ZOOM);
            inputGraphArea.setZoom(zoom);
        }
    }

    @Override
    public void activateOrCreateGraphView(final GraphDocument doc) {
        setCurrentDoc(doc, true);
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
            checkViewComponentsVisibility();
            return;
        }

        //wenn der interne Frame mit dem grafischen View in den Vordergrund geholt werden soll,
        if (activateGraphView) {
            //wenn nicht grade vorher ein Matrix-View aktiviert wurde (nur dann wäre die globale Variable==false)
            if (this.activateGraphView) {
                ViewPaneFrameComponent graphViewPaneFrameComponent = getGraphViewPaneFrameComponent(doc);
                //den richtigen Frame nach vorne holen
                if (!graphViewPaneFrameComponent.isSelected()) {
                    try {
                        graphViewPaneFrameComponent.setSelected();
                    } catch (Exception ex) {
                        Log.show(Log.FATAL, getResString("FehlerAllgemein"), ex);
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

        Static.setSelectedDoc(doc);
        modelBrowserPanel.addGraphDocumentAndSetSelected(doc);
        //beim nächsten Konextwechsel auch das nach Vorne holen des grafischen Views wieder einschalten
        this.activateGraphView = true;

        checkViewComponentsVisibility();
    }

    /**
     * return all ViewPaneFrameComponents at desktop
     *
     * @return ViewPaneFrameComponent[]
     */
    public final List<ViewPaneFrameComponent> getAllFrames() {
        List<ViewPaneFrameComponent> allViewPaneFrameComponents = desktop.getAllViewPaneFrameComponents();
        return allViewPaneFrameComponents;
    }

    /**
     * @param szen
     * @return the graphViewParameter of the current graph view if the view is
     *         closed the stored view parameters of the szenario
     */
    public GraphViewParameter getGraphViewParameter(final Szenario szen) {
        GraphViewPane graphViewPane = desktop.getGraphViewPane(szen);
        GraphViewParameter graphViewParameter = graphViewPane != null ? graphViewPane.getGraphViewParameter() : szen.getGraphViewParameter();
        return graphViewParameter;
    }

    /**
     * @param doc
     * @return the frame component of the view pane that contains the
     *         graph of the {@link GraphDocument} if exists or <code>null</code>
     */
    public final ViewPaneFrameComponent getGraphViewPaneFrameComponent(final GraphDocument doc) {
        GraphViewPane graphViewPane = desktop.getGraphViewPane(doc);
        ViewPaneFrameComponent graphViewPaneFrameComponent = graphViewPane == null ? null : graphViewPane.getFrameComponent();
        if (graphViewPaneFrameComponent == null) {
            graphViewPaneFrameComponent = createGraphView(doc);
        }
        return graphViewPaneFrameComponent;
    }

    /**
     * @param szen
     */
    public final void closeFrames(final GraphDocument szen) {
        desktop.removeViewPaneFrameComponents(szen);
        modelBrowserPanel.removeGraphDocument(szen);
    }

    /**
     * @param gdcoll
     */
    public void closeAllFramesAndTabs(final GDCollection gdcoll) {
        LGMGraphDocument mainDoc = gdcoll.getMainDoc();
        modelBrowserPanel.removeGraphDocument(mainDoc);
        closeFrames(mainDoc);
        for (Szenario szen : gdcoll.getSzenarios()) {
            closeFrames(szen);
        }
        //The function closeAllFramesAndTabs(GDCollection) is
        //only called if a model is closed. At this point
        //Static.getselectedDoc() already returns the new
        //selected GraphDocument. If this is null because
        //the last open model (=GDCollection) was closed, so
        //the next call removes the consistency table or
        //updates it to the next open model if not null.
        checkViewComponentsVisibility();
    }

    ////////////////////////////////////
    // ViewPaneFrameComponentListener //
    ////////////////////////////////////

    @Override
    public void viewClosing(final ViewPaneFrameComponent source) {
        //Sys.err1(source);
        //before closing -> store all view parameter in the szenario view parameter (inclusive view position)
        ViewPaneFrameComponent viewPaneFrameComponent = source;
        ViewPane viewPane = viewPaneFrameComponent.getViewPane();
        if (viewPane instanceof GraphViewPane) {
            GraphViewPane graphViewPane = (GraphViewPane) viewPane;
            GraphDocument doc = graphViewPane.getGraphDocument();
            if (doc instanceof Szenario) {
                Szenario szen = (Szenario) doc;
                GraphViewParameter graphViewParameter = graphViewPane.getGraphViewParameter();
                szen.adaptGraphViewParameter(graphViewParameter);
            }
        }
    }

    @Override
    public void viewClosed(final ViewPaneFrameComponent source) {
        //Sys.err1(source);
        LastAndNextViewManager.removeWindow(source);
        if (!desktop.hasViewPaneFrameComponents()) {
            activeFrame = null;
        }
        workarea.revalidate();
        viewPaneToolbarManager.updateToolBar();
        updateTitledBorders();
    }

    @Override
    public void viewActivated(final ViewPaneFrameComponent source) {
        ViewPaneFrameComponent oldActiveFrame = activeFrame;
        activeFrame = source;
        //Sys.err1(oldActiveFrame + "\n" + activeFrame);
        if (oldActiveFrame != activeFrame) {
            GraphDocument doc = activeFrame.getGraphDocument();
            doc.addClosedTransactionsListener(viewPaneToolbarManager);
            viewPaneToolbarManager.updateToolBar();
            //wenn ein Grafikfenster aktiviert wurde, soll es intern auch in den Vordergrund geholt werden. Bei
            //allen anderen Fenstern (Matrix-Sicht-Fenster), soll dieses Fenster im Vordergrund bleiben.
            setCurrentDoc(doc, false);
            workarea.revalidate();
            workarea.repaint();
            LastAndNextViewManager.addWindow(activeFrame);
            mainFrameToolbar.update();
        }
    }

    @Override
    public void viewDeactivated(final ViewPaneFrameComponent source) {
        if (activeFrame == null) {
            activeFrame = source;
        }
        //Sys.err1(activeFrame);
        GraphDocument doc = activeFrame.getGraphDocument();
        doc.removeClosedTransactionsListener(viewPaneToolbarManager);
        activeFrame = null;
        viewPaneToolbarManager.updateToolBar();
        mainFrameToolbar.update();
    }

    @Override
    public void model_change_model_opened(final GraphDocument source) {
        //add this panel to to every opened model as change listener
        //to catch the selection changed events for the template browser
        source.addAllTransactionsListener(this);
        SuggestShowTemplateBrowserHandler.suggestShowTemplateBrowser();
    }

    @Override
    public void model_change_selected_szenario_changed(final GraphDocument source) {
        //if the selected model changed -> update the selection in the
        //template browser
        selectionChanged(source);
    }

    @Override
    public void selectionChanged(final GraphDocument source) {
        //on every selection changed event in the model
        //the selection in the template browser mus be updated
        if (templateBrowserPanel != null) {
            templateBrowserPanel.updateSelection(source);
        }
    }

    @Override
    public void dataChanged(final GraphDocument source) {
        SuggestShowConsistencyTableHandler.suggestShowConsistencyTable();
    }

    private void updateTitledBorders() {
        if (OPTION_SHOW_VIEW_COMPONENT_TITLES.is()) {
            addTitledBorders();
        } else {
            removeTitledBorders();
        }
        if (templateBrowserPanel != null) {
            templateBrowserPanel.updateComponents();
        }
    }

    /**
     *
     */
    private void addTitledBorders() {
        //show the the graph view border only if there is no active model
        //        if (!desktop.hasViewPaneFrameComponents()) {
        createTitledBorder(desktop, "PANEL_LABEL_GRAPH_VIEW_TITLE");
        //        } else {
        //            removeTitledBorder(desktop);
        //        }
        createTitledBorder(modelBrowserPanel, "PANEL_LABEL_MODEL_BROWSER_TITLE");
        createTitledBorder(templateBrowserPanel, "PANEL_LABEL_TEMPLATE_BROWSER_TITLE");
        createTitledBorder(consistencyErrorTableBorderPanel, "PANEL_LABEL_CONSISTENCY_TABLE_TITLE");
    }

    /**
     *
     */
    private void removeTitledBorders() {
        removeTitledBorder(desktop);
        removeTitledBorder(modelBrowserPanel);
        removeTitledBorder(templateBrowserPanel);
        removeTitledBorder(consistencyErrorTableBorderPanel);
    }

    /**
     * @param component
     * @param titleResKey
     */
    private static final void createTitledBorder(final ViewPaneFrameComponentParent component, final String titleResKey) {
        createTitledBorder((JComponent) component, titleResKey);
    }

    /**
     * @param component
     * @param titleResKey
     */
    private static final void createTitledBorder(final JComponent component, final String titleResKey) {
        if (component != null) {
            //create only new titled border if there is not already a titled border
            Border border = component.getBorder();
            if (border != null && border instanceof TitledBorder) {
                return;
            }
            String borderTitle = Tool3lgmConstants.getResString(titleResKey);
            TitledBorder titledBorder = BorderFactory.createTitledBorder(borderTitle);
            component.setBorder(titledBorder);
        }
    }

    /**
     * @param component
     */
    private static final void removeTitledBorder(final ViewPaneFrameComponentParent component) {
        removeTitledBorder((JComponent) component);
    }

    /**
     * @param component
     */
    private static final void removeTitledBorder(final JComponent component) {
        if (component != null) {
            component.setBorder(null);
        }
    }

}
