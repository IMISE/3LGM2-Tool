package de.imise.tool3lgm.graphtools.dialog;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableDefinition;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTablePanel;
import de.imise.tool3lgm.graphtools.dialog.panel.DescripPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.DescriptedSingleConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.DoubleMeaningEdgePanel;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogHeaderPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.MutipleCompositionPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.PathConnectionLeafPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.PathConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.StructurePanel;
import de.imise.tool3lgm.graphtools.dialog.panel.TabbedPanel;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionChangeType;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPathCreator;
import de.imise.tool3lgm.graphtools.undoredo.InTransactionListener;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.PropertyDialogUserFieldPanel;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.swing.component.TabbedPane;

/**
 * Eigenschaftsdialog für Modellelemnte, also Node und Kanten.<br>
 *
 * @author N.N., AXS
 */
public class ElementPropertyDialog extends AbstractTabbedPropertyDialog implements ActionListener, InTransactionListener {

    /**
     * COMMENTME
     */
    private final ModelElement modelElement;

    /**
     * COMMENTME
     */
    private final ElementDialogHeaderPanel headerPanel;

    /**
     * COMMENTME
     */
    static int lastWidth = -1;

    /**
     * COMMENTME
     */
    static int lastHeight = -1;

    private static final Dimension DEFAULT_SIZE = new Dimension(600, 500);

    /**
     * Wird im Konstruktor auf <code>true</code> gesetzt und nach dem ersten zeigen des Dialoges auf <code>false</code>. Damit kann sicher gestellt
     * werden, dass die Transaktion des Dialoges nur einmal am Anfang und nicht bei jedem weiteren Zeigen des Dialoges gestartet wird.
     */
    boolean opening = false;

    /**
     * Wird <code>true</code>, wenn der Ok oder der Cancel Button gedrückt wurde
     */
    boolean closing = false;

    private final DescripPanel descripPanel;

    /**
     * @param modelElement
     * @param gdcoll
     */
    public ElementPropertyDialog(final ModelElement modelElement, final GDCollection gdcoll) {

        super(gdcoll);

        setTitle(getResString("eigensch_dial"));

        getContentPane().setLayout(new BorderLayout());

        this.modelElement = modelElement;

        JComponent tabComponent = getTabComponent();
        tabComponent.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JPanel up = new JPanel(new GridLayout(1, 1));
        headerPanel = new ElementDialogHeaderPanel(this);
        up.add(headerPanel);
        update();

        descripPanel = new DescripPanel(this);
        addTab(getResString("general"), descripPanel);
        addPartOfStructurePanel();

        // wenn es mind ein Userfield für diese Klasse gibt -> zeige das USerFieldPanel
        if (doc.getCollection().getUserFieldDefinitions().hasUserFields(modelElement.getClass())) {
            addTab(getResString("userfields"), new PropertyDialogUserFieldPanel(this));
        }

        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new BorderLayout());

        JPanel bp = new JPanel();
        okButton.addActionListener(this);
        bp.add(okButton);
        applyButton.addActionListener(this);
        bp.add(applyButton);
        cancelButton.addActionListener(this);
        bp.add(cancelButton);
        if (helpButton != null) {
            bp.add(helpButton);
        }

        buttonpanel.add(bp, BorderLayout.EAST);

        getContentPane().add(up, BorderLayout.NORTH);
        getContentPane().add(tabComponent, BorderLayout.CENTER);
        getContentPane().add(buttonpanel, BorderLayout.SOUTH);

        addSizeOrPositionChangedListener();
        setSizeAndLocation();
        opening = true;
    }

    private void addSizeOrPositionChangedListener() {
        addComponentListener(new ComponentListener() {
            @Override
            public void componentHidden(final ComponentEvent e) {
            }

            @Override
            public void componentMoved(final ComponentEvent e) {
                dialogPositionOrSizeChanged();
            }

            @Override
            public void componentResized(final ComponentEvent e) {
                dialogPositionOrSizeChanged();
            }

            @Override
            public void componentShown(final ComponentEvent e) {
            }
        });
    }

    private void setSizeAndLocation() {
        pack();
        JFrame mainFrame = Static.getMainFrame();
        int xx = mainFrame.getX() + 100;
        int yy = mainFrame.getY() + 100;
        if (lastWidth == -1) {
            setLastWidth(DEFAULT_SIZE.width);
            setLastHeight(DEFAULT_SIZE.height);
        } else {
            for (int i = 0; i < ModelConstants.dialogs.size(); i++) {
                ElementPropertyDialog pd = ModelConstants.dialogs.get(i);
                if (pd.getLocation().x == xx && pd.getLocation().y == yy) {
                    xx += 20;
                    yy += 20;
                    i = -1;
                }
            }
            if (Toolkit.getDefaultToolkit().getScreenSize().width - xx < 150 || Toolkit.getDefaultToolkit().getScreenSize().height - yy < 150) {
                xx = mainFrame.getX() + 100;
                yy = mainFrame.getY() + 100;
            }
        }

        setLocation(xx, yy);
        setSize(lastWidth, lastHeight);
    }

    /**
     * Liefert alle {@link HasPartEdge}en, bei denen die Kindelemente auch wieder Kindelemente haben können (also wo die
     * Start- und Endklasse der PartOBeziehung gleich ist).
     *
     * @return
     */
    private List<Class<? extends HasPartEdge>> getRecursiveHasPartEdges() {
        List<Class<? extends HasPartEdge>> recursiveHasPartEdges = new ArrayList<>();
        Class<? extends ModelElement> elementClass = modelElement.getClass();
        Class<? extends Edge>[] edgeTypes = ModelConstants.getEdgeTypes(elementClass);
        for (Class<? extends Edge> edgeClass : edgeTypes) {
            if (ModelConstants.isRecursiveHasPartEdge(edgeClass)) {
                Class<? extends HasPartEdge> hasPartEdgeClass = edgeClass.asSubclass(HasPartEdge.class);
                if (HasPartEdge.isParentClass(hasPartEdgeClass, elementClass)) {
                    recursiveHasPartEdges.add(hasPartEdgeClass);
                }
            }
        }
        return recursiveHasPartEdges;
    }

    private void addPartOfStructurePanel() {
        List<Class<? extends HasPartEdge>> realPartOfs = getRecursiveHasPartEdges();
        if (realPartOfs.size() == 1) {
            StructurePanel structurePanel = new StructurePanel(this, realPartOfs.get(0));
            structurePanel.setName(getResString("strukt"));
            addTab(structurePanel);
        } else if (realPartOfs.size() > 1) {
            //TODO: hier könnte müsste man ein ExtraPanel mit dem Namen Struktur mit den Teil-Von-Beziehungen als UnterPanels anlegen, die mit der jeweiligen Edge benamt sind
        }
    }

    /**
     *
     */
    public void showDialog() {
        if (opening) {
            doc.start_transaction(getTransactionID());
            doc.addInTransactionListener(this);
            opening = false;
        }
        setVisible(true);
    }

    /**
     * @return
     */
    public ModelElement getModelElement() {
        return modelElement;
    }

    /**
     *
     */
    private void commit() {
        //alle Panels committen
        for (int m = 0; m < getTabCount(); m++) {
            Component comp = getTabComponentAt(m);
            if (comp instanceof ElementDialogPanel) {
                ((ElementDialogPanel) comp).commit();
            }
        }
        //alle anderen Dialoge updaten
        for (ElementPropertyDialog pd : ModelConstants.getDialogs()) {
            // this wird in update klargemacht...
            if (pd != this) {
                for (int m = 0; m < getTabCount(); m++) {
                    Component comp = getTabComponentAt(m);
                    if (comp instanceof ElementDialogPanel) {
                        ((ElementDialogPanel) comp).update();
                    }
                }
            }
        }
    }

    /**
     * @param doUpdate
     */
    public void commit(final boolean doUpdate) {
        // System.out.println("ElementPropertyDialog commit "+ doUpdate);
        commit();
        if (doUpdate) {
            update();
        } else {
            ElementDialogPanel selectedElementDialogPanel = getSelectedElementDialogPanel();
            if (selectedElementDialogPanel != null) {
                selectedElementDialogPanel.update();
            }
        }
        doc.finish_transaction(getTransactionID());
        doc.distributeEvent(GDCollectionChangeType.DATA_CHANGED, getTransactionID());
        doc.start_transaction(createNewTransactionID());
    }

    public void cancel() {
        doc.finish_transaction(getTransactionID());
        doc.undo(getTransactionID());
        close();
    }

    private void close() {
        ModelConstants.removeDialog(modelElement);
        doc.finish_transaction(getTransactionID());
        doc.removeInTransactionListener(this);
        dispose();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == okButton) {
            closing = true;
            commit(false);
            close();
        } else if (e.getSource() == cancelButton) {
            closing = true;
            cancel();
        } else if (e.getSource() == applyButton) {
            commit(true);
        }
        doc.select(modelElement.getContainer(doc), getTransactionID());
        doc.distributeEvent(GDCollectionChangeType.SELECTION_CHANGED, getTransactionID());
    }

    @Override
    protected void processWindowEvent(final WindowEvent e) {
        super.processWindowEvent(e);
        if (!closing && e.getID() == WindowEvent.WINDOW_CLOSING) {
            closing = true;
            cancel();
        }
    }

    // InTransactionListener Begin
    // ###################################################################################

    @Override
    public final void dataChanged(final GraphDocument source, final int pid) {
        update();
    }

    @Override
    public final void elementGraphicsChanged(final GraphDocument source, final ElementContainer element) {
    }

    @Override
    public final void elementNameChanged(final ElementContainer ec) {
        update();
    }

    @Override
    public final void userFieldValueChanged(final ElementContainer ec) {
        update();
    }

    // InTransactionListener Ende
    // ####################################################################################

    /**
     *
     */
    private void dialogPositionOrSizeChanged() {
        lastWidth = getWidth();
        lastHeight = getHeight();
    }

    /**
     *
     */
    public void update() {

        if (closing) {
            return;
        }

        headerPanel.update();
        for (int i = 0; i < getTabCount(); i++) {
            Component c = getTabComponentAt(i);
            if (c instanceof ElementDialogPanel) {
                ((ElementDialogPanel) c).update();
            }
        }
    }

    /**
     * @param lastHeight The lastHeight to set.
     */
    public static void setLastHeight(final int lastHeight) {
        ElementPropertyDialog.lastHeight = lastHeight;
    }

    /**
     * @param lastWidth The lastWidth to set.
     */
    public static void setLastWidth(final int lastWidth) {
        ElementPropertyDialog.lastWidth = lastWidth;
    }

    @Override
    public Dimension getDefaultSize() {
        return DEFAULT_SIZE;
    }

    ///////////////////////////////////////
    // DescriptionPanel -> add SubPanels //
    ///////////////////////////////////////

    @SafeVarargs
    public final SimpleMetaPath createSimpleMetaPath(@Nullable final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        return SimpleMetaPathCreator.createSimpleMetaPath(getModelElement().getClass(), searchElementClass, edgeClasses);
    }

    @Override
    protected void addTab(final String title, final Icon icon, final Component component) {
        if (component instanceof DescriptedSingleConnectionPanel) {
            DescriptedSingleConnectionPanel panel = (DescriptedSingleConnectionPanel) component;
            panel.addSelf();
        }
        super.addTab(title, icon, component);
    }

    private TabbedPanel lastCreatedTabbedPanel;

    public final void addTabbedPanel(final String nameResKey) {
        lastCreatedTabbedPanel = new TabbedPanel(this);
        lastCreatedTabbedPanel.setName(Tool3lgmConstants.getResStringWithoutError(nameResKey));
        addTab(lastCreatedTabbedPanel);
    }

    @SafeVarargs
    public final void addTabbedPanelPathConnectionPanel(final Class<? extends Edge>... edgeClasses) {
        addTabbedPanelPathConnectionPanel(null, edgeClasses);
    }

    @SafeVarargs
    public final void addTabbedPanelPathConnectionPanel(final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        if (edgeClasses.length == 1) {
            addEdgePanel(searchElementClass, edgeClasses[0], true);
        } else {
            lastCreatedTabbedPanel.addTab(new PathConnectionPanel(this, true, createSimpleMetaPath(searchElementClass, edgeClasses)));
        }
    }

    @SafeVarargs
    public final void addDescripSingleConnectionPanel(final Class<? extends Edge>... edgeClasses) {
        addDescripSingleConnectionPanel(null, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripSingleConnectionPanel(@Nullable final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        addDescripSingleConnectionPanel(false, searchElementClass, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripSingleConnectionPanel(final boolean labelLastEdgeName, final Class<? extends Edge>... edgeClasses) {
        addDescripSingleConnectionPanel(labelLastEdgeName, null, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripSingleConnectionPanel(final boolean labelLastEdgeName, @Nullable final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        SimpleMetaPath simpleMetaPath = createSimpleMetaPath(searchElementClass, edgeClasses);
        addDescripSingleConnectionPanel(labelLastEdgeName, simpleMetaPath);
    }

    public final void addDescripSingleConnectionPanel(final boolean labelLastEdgeName, final SimpleMetaPath simpleMetaPath) {
        if (Static.isExpertMode() || isVisible(simpleMetaPath)) {
            descripPanel.addSingleConnectionPanel(labelLastEdgeName, ModelConstants.isEditable(simpleMetaPath), simpleMetaPath);
        }
    }

    @SafeVarargs
    public final void addDescripDescriptedSingleConnectionPanel(final Class<? extends Edge>... edgeClasses) {
        addDescripDescriptedSingleConnectionPanel(false, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripDescriptedSingleConnectionPanel(final boolean labelLastEdgeName, final Class<? extends Edge>... edgeClasses) {
        addDescripDescriptedSingleConnectionPanel(labelLastEdgeName, null, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripDescriptedSingleConnectionPanel(final boolean labelLastEdgeName, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        descripPanel.addDescriptedSingleConnectionPanel(labelLastEdgeName, createSimpleMetaPath(searchElementClass, edgeClasses));
    }

    @SafeVarargs
    public final void addDescripListPanel(final Class<? extends Edge>... edgeClasses) {
        descripPanel.addListPanel(false, createSimpleMetaPath(null, edgeClasses));
    }

    @SafeVarargs
    public final void addDescripListPanel(final boolean labelLastEdgeName, final Class<? extends Edge>... edgeClasses) {
        descripPanel.addListPanel(labelLastEdgeName, createSimpleMetaPath(null, edgeClasses));
    }

    @SafeVarargs
    public final void addDescriptedSingleConnectionPanel(final Class<? extends Edge>... edgeClasses) {
        addTab(new DescriptedSingleConnectionPanel(this, createSimpleMetaPath(null, edgeClasses)));
    }

    @SafeVarargs
    public final void addPathConnectionPanel(final Class<? extends Edge>... edgeClasses) {
        addPathConnectionPanel(false, edgeClasses);
    }

    @SafeVarargs
    public final void addPathConnectionPanel(final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        addPathConnectionPanel(false, searchElementClass, edgeClasses);
    }

    @SafeVarargs
    public final void addPathConnectionPanel(final boolean labelLastEdgeName, final Class<? extends Edge>... edgeClasses) {
        addPathConnectionPanel(labelLastEdgeName, null, edgeClasses);
    }

    @SafeVarargs
    public final void addPathConnectionPanel(final boolean labelLastEdgeName, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        addTab(new PathConnectionPanel(this, labelLastEdgeName, true, createSimpleMetaPath(searchElementClass, edgeClasses)));
    }

    @SafeVarargs
    public final void addPathConnectionLeafPanel(final Class<? extends Edge>... edgeClasses) {
        addPathConnectionLeafPanel(false, edgeClasses);
    }

    @SafeVarargs
    public final void addPathConnectionLeafPanel(final boolean labelLastEdgeName, final Class<? extends Edge>... edgeClasses) {
        addTab(new PathConnectionLeafPanel(this, labelLastEdgeName, true, createSimpleMetaPath(null, edgeClasses)));
    }

    @SafeVarargs
    public final void addPathConnectionInfoPanel(final Class<? extends Edge>... edgeClasses) {
        addTab(new PathConnectionPanel(this, false, createSimpleMetaPath(null, edgeClasses)));
    }

    public void addEdgePanel(final Class<? extends Edge> edgeClass) {
        addEdgePanel(null, edgeClass);
    }

    public void addEdgePanel(final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass) {
        addEdgePanel(searchElementClass, edgeClass, false);
    }

    private void addEdgePanel(final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass, final boolean add2SubTab) {
        SimpleMetaPath metaPath = createSimpleMetaPath(searchElementClass, edgeClass);
        if (!isVisible(metaPath)) {
            return;
        }
        boolean editable = ModelConstants.isEditable(metaPath);
        ElementDialogPanel panel2Add = null;
        if (ModelConstants.isComposition(edgeClass)) {
            panel2Add = new MutipleCompositionPanel(this, editable, searchElementClass, edgeClass.asSubclass(CompositionEdge.class));
        } else if (ModelConstants.isDoubleMeaningEdge(edgeClass)) {
            panel2Add = new DoubleMeaningEdgePanel(this, editable, searchElementClass, edgeClass);
            //Kanten die nicht doppeltdeutig sind, aber dieselben Elementarten verbinden und in beide Richtungen unterschiedlich heißen, müssen auch in beiden Richtungen angeboten werden
        } else if (Edge.getStartClass(edgeClass) == Edge.getEndClass(edgeClass) && ModelConstants.isDirectedEdge(edgeClass)) {
            panel2Add = new DoubleMeaningEdgePanel(this, editable, searchElementClass, edgeClass);
        } else {
            panel2Add = new PathConnectionPanel(this, editable, metaPath);
        }
        if (add2SubTab) {
            lastCreatedTabbedPanel.addTab(panel2Add);
        } else {
            addTab(panel2Add);
        }
    }

    @SafeVarargs
    public final void addTablePanel(final ConnectedElementsTableDefinition tableDefinition, final int metaPathStepWithPathName, final Class<? extends Edge>... edgeClasses) {
        SimpleMetaPath simpleMetaPath = SimpleMetaPathCreator.createSimpleMetaPath(modelElement.getClass(), metaPathStepWithPathName, edgeClasses);
        addTablePanel(tableDefinition, simpleMetaPath);
    }

    public final void addTablePanel(final ConnectedElementsTableDefinition tableDefinition, final SimpleMetaPath simpleMetaPath) {
        addTablePanelInternal(tableDefinition, simpleMetaPath);
    }

    @SafeVarargs
    private final void addTablePanelInternal(final ConnectedElementsTableDefinition tableDefinition, final SimpleMetaPath... simpleMetaPaths) {
        boolean editable = true;
        Set<SimpleMetaPath> allDifferentSimpleMetaPaths = new HashSet<>();
        for (SimpleMetaPath simpleMetaPath : simpleMetaPaths) {
            Collection<SimpleMetaPath> simpleMetaPathsNonAbstract = SimpleMetaPathCreator.getSimpleMetaPathsNonAbstract(simpleMetaPath);
            allDifferentSimpleMetaPaths.addAll(simpleMetaPathsNonAbstract);
            if (editable) {
                for (SimpleMetaPath nonAbstractSimpleMetaPaths : simpleMetaPathsNonAbstract) {
                    if (!ModelConstants.isEditable(nonAbstractSimpleMetaPaths)) {
                        editable = false;
                        break;
                    }
                }
            }
        }
        addTab(new ConnectedElementsTablePanel(this, editable, tableDefinition, allDifferentSimpleMetaPaths));
    }

    /**
     * Liefert <code>true</code>, wenn ein Panel mit diesem Pfad angezeigt werden soll. Ob es angezeigt werden soll entscheidet sich anhand der
     * Zielklasse des Pfades. Ist diese nur im Expert-Mode anzuzeigen, der Modeus aber nicht an, dann sollte ein Panel mit diesem MetaPath nicht
     * angezeigt werden.
     *
     * @param metaPath
     * @return
     */
    private static final boolean isVisible(final SimpleMetaPath metaPath) {
        if (Static.isExpertMode()) {
            return true;
        }
        for (Class<? extends ModelElement> endClass : metaPath.getEndClasses()) {
            if (ModelConstants.isHiddenClass(endClass)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void windowActivated(final WindowEvent e) {
        removeEmptyTabs();
        super.windowActivated(e);
    }

    private void removeEmptyTabs() {
        for (int i = getTabCount() - 1; i >= 0; i--) {
            Component comp = getTabComponentAt(i);
            if (isEmptyTabbedPanel(comp)) {
                removeTab(i);
            }
        }
    }

    private boolean isEmptyTabbedPanel(Component comp) {
        if (comp instanceof TabbedPanel) {
            TabbedPanel tabbedPanel = (TabbedPanel) comp;
            if (tabbedPanel.getComponentCount() == 1) {
                comp = tabbedPanel.getComponent(0);
                if (tabbedPanel.getComponent(0) instanceof TabbedPane) {
                    TabbedPane tabbedPane = (TabbedPane) comp;
                    int componentCount = tabbedPane.getComponentCount();
                    return componentCount == 0;
                }
            }
        }
        return false;
    }

}
