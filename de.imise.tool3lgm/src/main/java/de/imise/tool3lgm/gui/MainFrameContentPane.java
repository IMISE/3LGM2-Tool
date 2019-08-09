package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_CHECK_CONSISTENCY;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ENABLE_EXPERT_MODE;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_MODEL_BROWSER_SHOW;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_PAINTING_TOOLBAR;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_STANDARD_TOOLBAR;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.help.CSH;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.consistency.ConsistencyChecker;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.newmatrixview.MatrixViewInternalFrame;
import de.imise.tool3lgm.graphtools.view.browser.ModelBrowserPanel;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.ViewParameter;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * @author AXS (6 Aug 2019)
 */
public class MainFrameContentPane extends JPanel implements PropertyChangeListener, InternalFrameListener {

    /** ToolBar with general tools */
    private final ToolBar toolbar = new ToolBar();

    /** Aktualisiert die Toolbar je nach Kontext des aktiven Frames */
    private final GraphAreaToolbarManager toolbarManager = new GraphAreaToolbarManager(toolbar);

    /** Panel with verticalSplitPane and werkzeugleiste */
    private final JPanel workarea = new JPanel();

    /** splitted pane with modelBrowserPanel on the left and desktop on the right */
    private final JSplitPane verticalSplitPane;

    /** splitted pane with modelBrowserPanel and the graph on the top and the error table bottom */
    private JSplitPane horizontalSplitPane;

    /** panel to hold one or more modelBrowsers */
    private final ModelBrowserPanel modelBrowserPanel;

    /** contain all windows of opened documents (JDesktopPane is a container used to create a multiple-document interface or a virtual desktop) */
    private final JDesktopPane desktop;

    /** InternalFrame in desktop, which has the focus */
    private AbstractInternalFrame activeFrame = null;

    /** Position of divider betweeen the tree and the graph view in pixel from the left side */
    private int dividerLocation = getToolkit().getScreenSize().width / 5;

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

        JScrollPane desktopscroll = new JScrollPane(desktop);
        verticalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, modelBrowserPanel, desktopscroll);
        verticalSplitPane.setOneTouchExpandable(true);
        verticalSplitPane.setDividerSize(10);
        verticalSplitPane.setDividerLocation(dividerLocation);

        // Direkthilfe für die einzelnen Baukastenteile
        CSH.setHelpIDString(modelBrowserPanel, "uebersicht_modellbrowser");
        setCheckConsistencyState();

        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(workarea, BorderLayout.CENTER);
        //add(new StatusBar(), BorderLayout.SOUTH);

        setShowStandardToolbar();
        UserProperties.addPropertyChangeListener(this);
    }

    /**
     *
     */
    public void selectedDocChanged() {
        toolbar.selectedDocChanged();
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
            add(toolbar, BorderLayout.NORTH);
        } else {
            remove(toolbar);
        }
        workarea.revalidate();
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (OPTION_CHECK_CONSISTENCY.isChanged(evt)) {
            setCheckConsistencyState();
        } else if (OPTION_MODEL_BROWSER_SHOW.isChanged(evt)) {
            showModelBrowser();
        } else if (OPTION_SHOW_PAINTING_TOOLBAR.isChanged(evt)) {
            toolbarManager.setToolBarVisibility();
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
    private boolean setCheckConsistencyState() {
        boolean state = OPTION_CHECK_CONSISTENCY.is();
        GDCollection gdcoll = Static.getSelectedGDCollection();
        if (gdcoll == null) {
            state = false;
        }
        ConsistencyChecker consistencyChecker = ConsistencyChecker.getConsistencyChecker();
        if (!state) {
            consistencyChecker.resetConsistencyDefinition();
            if (verticalSplitPane.getParent() == workarea) {
                return state;
            }
            if (horizontalSplitPane != null) {
                workarea.remove(horizontalSplitPane);
            }
            workarea.add(verticalSplitPane, BorderLayout.CENTER);
            horizontalSplitPane = null;
            // falls vorher schonmal die Konsistenzprüfung eingeschaltet war -> Listener zur
            // Tabellenaktualisierung wieder entfernen
            consistencyChecker.changeContext(null);
        } else {
            if (horizontalSplitPane == null) {
                horizontalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, verticalSplitPane, new JScrollPane(consistencyChecker.getErrorTable()));
                horizontalSplitPane.setOneTouchExpandable(true);
                horizontalSplitPane.setDividerSize(10);
                horizontalSplitPane.setDividerLocation(workarea.getHeight() / 4 * 3);
                workarea.add(horizontalSplitPane, BorderLayout.CENTER);
            }
            consistencyChecker.changeContext(gdcoll);
        }
        revalidate();
        repaint();
        return state;
    }

    /** (De-)Aktiviert den ModelBrowser */
    private final void showModelBrowser() {
        if (OPTION_MODEL_BROWSER_SHOW.is()) {
            verticalSplitPane.setLeftComponent(modelBrowserPanel);
            verticalSplitPane.setDividerLocation(dividerLocation);
        } else {
            dividerLocation = verticalSplitPane.getDividerLocation();
            verticalSplitPane.remove(verticalSplitPane.getLeftComponent());
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
    public AbstractInternalFrame createFrame(final GraphDocument doc) {
        InputGraphArea area = new InputGraphArea(doc);
        InternalGraphFrame frame = new InternalGraphFrame(desktop, area, doc);
        modelBrowserPanel.addGraphDocument(doc);
        frame.addInternalFrameListener(this);
        Rectangle bounds = desktop.getBounds();
        if (doc instanceof Szenario) {
            bounds.height = bounds.height - 32;
            setWorkArea(frame);
        }
        frame.setBounds(bounds);
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
        MatrixViewInternalFrame matrixView = new MatrixViewInternalFrame(_graphDocument, toolbarManager);
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
     * Wechselt den Kontext auf das übergebene Teilmodell. In jedem Fall wird der <code>ModelBrowser</code> des aktivierten Teilmodells in den
     * Vordergrund gebracht.
     *
     * @param doc
     *            Teilmodell, in dessen Kontext gewechselt werden soll
     * @param activateGraphView
     *            Wenn <code>true</code> ist, wird auch das dazugehörige
     *            Grafikfenster in den Vordergrund geholt, sonst nicht.
     */
    public void setSelectedDoc(final GraphDocument doc, final boolean activateGraphView) {
        //das doc kann null sein, wenn eine Datei geladen wird und das ModelBrowserPanel grade mit den
        //geladenen Teilmodellen gefüllt wird. Im ModelBrowserPanel wird bei jedem Hinzufügen eines
        //Teilmodell-Tabs immer diese Funktion hier aufgerufen.
        //Es kann auch null sein, wenn das letzte Modell geschlossen wurde
        if (doc == null) {
            boolean isCheckConsistency = setCheckConsistencyState();
            toolbar.selectedDocChanged();
            if (isCheckConsistency) {
                ConsistencyChecker consistencyChecker = ConsistencyChecker.getConsistencyChecker();
                consistencyChecker.changeContext(null);
            }
            return;
        }

        //Die folgenden beiden Zeilen nicht aktivieren. Sie sind auskommentiert
        //stehen geblieben, damit nicht irgendwann mal einer auf die Idee  kommt,
        //über diesen Weg optimieren zu wollen
        //if (doc == oldDoc)
        //return;

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

        //wenn sich das im ModelBrowser ausgewählte Teimodell geändert hat
        if (doc != ModelBrowserPanel.getSelectedDoc()) {
            //aktiviere es. Dabei wird diese Funktion auch noch einmal aufgerufen und je nachdem
            //wie die this.activateGraphView gerade steht, wird der GraphView nach vorne geholt
            //oder eben nicht
            modelBrowserPanel.setSelectedDoc(doc);
            //wenn sich das Teilmodell nicht geändert hat
        } else {
            //beim nächsten Konextwechsel auch das nach Vorne holen des grafischen Views wieder einschalten
            this.activateGraphView = true;
        }

        setCheckConsistencyState();
        //TODO: das sollte der ContextGenerator als Listener mitbekommen (CONTEXT_CHANGED oder sowas)
        Static.contextGenerator.changeContext((LGMGraphDocument) doc);
        toolbar.selectedDocChanged();
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

    //TODO: auch das hier sollte ziemlich sicher über einen Listener laufen!
    /**
     * reagiert auf ein umbenanntes Szenario
     */
    public void szenarioRenamed(final Szenario szen) {
        InternalGraphFrame frame = szen.getFrame();
        if (frame != null) {
            frame.updateTitle();
        }
        modelBrowserPanel.updateTitle(szen);
    }

    /**
     *
     */
    public void updateFrameTitles() {
        JInternalFrame[] allFrames = desktop.getAllFrames();
        for (JInternalFrame frame : allFrames) {
            if (frame instanceof InternalGraphFrame) {
                ((InternalGraphFrame) frame).updateTitle();
            }
        }
    }

    /**
     *
     */
    public void selectLastFrame() {
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
        if (!(frame instanceof MatrixViewInternalFrame)) {
            workarea.revalidate();
        }

        toolbar.removeWindow(frame);

        if (frames.length == 0) {
            activeFrame = null;
            toolbarManager.updateToolBar();
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
        GraphDocument doc = activeFrame.getGraphDocument();
        doc.addGraphDocumentListener(toolbarManager);
        toolbarManager.updateToolBar();
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
        toolbar.addWindow(activeFrame);
    }

    @Override
    public void internalFrameDeactivated(final InternalFrameEvent e) {
        GraphDocument graphDocument = activeFrame.getGraphDocument();
        graphDocument.removeGraphDocumentListener(toolbarManager);
        activeFrame = null;
        toolbarManager.updateToolBar();
    }

    /**
     * @param gdcoll
     */
    public void modelRenamed(final GDCollection gdcoll) {
        String name = gdcoll.getName();
        for (AbstractInternalFrame f : Static.getAllFrames()) {
            if (f.getCollection().equals(gdcoll)) {
                f.setTitle(name);
                modelBrowserPanel.updateTitle(gdcoll);
            }
        }
    }

}
