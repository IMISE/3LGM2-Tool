package de.imise.tool3lgm.graphtools.dialog;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.DATA_CHANGED;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.SELECTION_CHANGED;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
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
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
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
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPathCreator;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.PropertyDialogUserFieldPanel;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.swing.component.TabbedPane;

/**
 * Eigenschaftsdialog für Modellelemnte, also Node und Kanten.<br>
 *
 * @author N.N., AXS
 */
public class ElementPropertyDialog extends AbstractTabbedPropertyDialog implements ActionListener, LGMChangeListenerSimple {

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

    /** Das Panel des Allgemein-Reiters */
    private final DescripPanel descripPanel;

    /** Das Panel für die benutzerdefinierten Eigenschaften */
    private final PropertyDialogUserFieldPanel propertyDialogUserFieldPanel;

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
            propertyDialogUserFieldPanel = new PropertyDialogUserFieldPanel(this);
            addTab(getResString("userfields"), propertyDialogUserFieldPanel);
        } else {
            propertyDialogUserFieldPanel = null;
        }

        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new BorderLayout());

        JPanel bp = new JPanel();
        okButton.addActionListener(this);
        bp.add(okButton);
        if (!isInfoDialog()) {
            applyButton.addActionListener(this);
            bp.add(applyButton);
            cancelButton.addActionListener(this);
            bp.add(cancelButton);
        }
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

    /**
     * @return <code>true</code> if this dialog only presents information but nothing is ediable/changeable:
     */
    public final boolean isInfoDialog() {
        if (gdcoll.getModelCategory() == ModelCategory.TEMPLATE) {
            return true;
        }
        if (Static.isExpertMode()) {
            return false;
        }
        Class<? extends ModelElement> dialogElementClass = modelElement.getClass();
        MetaModel metaModel = getMetaModel();
        return !metaModel.isEditable(dialogElementClass);
    }

    /**
     * Bleibt <code>true</code>, wenn keine Unterklasse den Dialog erweitert, sondern der Dialog nur aus dem Allgemein-Reiter besteht, auf dem auch
     * nichts durch eine Unterklasse hinzugefügt wurde.
     */
    private final boolean isUnchangedDefaultDialog() {
        if (!descripPanel.isUnchangedDefaultPanel()) {
            return false;
        }
        int tabCount = getTabCount();
        if (tabCount == 0 || tabCount > 2) {
            return false;
        }
        if (getTabComponentAt(0) != descripPanel) {
            return false;
        }
        if (tabCount == 2 && getTabComponentAt(1) != propertyDialogUserFieldPanel) {
            return false;
        }
        return true;
    }

    /**
     * Wenn der Dialog von keiner Unterklasse des ModelElements aus seinem Default-Zustand (DescripPanel + evtl. Benutzerdef. Eigenschaften) geändert
     * wurde, dann werden hier automatisch für alle Kanten des Elementes passende Panels hinzugefügt.
     */
    public void extendDefaultDialog() {
        if (isUnchangedDefaultDialog()) {
            MetaModel metaModel = getMetaModel();
            Class<? extends ModelElement> elementClass = modelElement.getClass();
            for (Class<? extends Edge> edgeClass : metaModel.getEdgeTypes(elementClass)) {
                addEdgePanel(edgeClass);
            }
        }
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
            for (int i = 0; i < ElemenPropertyDialogsContext.getDialogCount(); i++) {
                ElementPropertyDialog pd = ElemenPropertyDialogsContext.getDialog(i);
                if (pd.getLocation().x == xx && pd.getLocation().y == yy) {
                    xx += 20;
                    yy += 20;
                    i = -1;
                }
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
        MetaModel metaModel = modelElement.getMetaModel();
        Class<? extends Edge>[] edgeTypes = metaModel.getEdgeTypes(elementClass);
        for (Class<? extends Edge> edgeClass : edgeTypes) {
            if (metaModel.isRecursiveHasPartEdge(edgeClass)) {
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
            doc.addAllTransactionsListener(this);
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
        for (ElementPropertyDialog pd : ElemenPropertyDialogsContext.getDialogs()) {
            // this wird in update klargemacht...
            if (pd != this) {
                for (int m = 0; m < pd.getTabCount(); m++) {
                    Component comp = pd.getTabComponentAt(m);
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
        doc.distributeEvent(DATA_CHANGED, getTransactionID());
        doc.start_transaction(createNewTransactionID());
    }

    public void cancel() {
        doc.finish_transaction(getTransactionID());
        doc.undo(getTransactionID());
        close();
    }

    private void close() {
        ElemenPropertyDialogsContext.removeDialog(modelElement);
        doc.finish_transaction(getTransactionID());
        doc.removeAllTransactionsListener(this);
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
        doc.distributeEvent(SELECTION_CHANGED, getTransactionID());
    }

    @Override
    protected void processWindowEvent(final WindowEvent e) {
        super.processWindowEvent(e);
        if (!closing && e.getID() == WindowEvent.WINDOW_CLOSING) {
            closing = true;
            cancel();
        }
    }

    // GDCollectionChangeListener Begin
    // ###################################################################################

    @Override
    public final void dataChanged(final GraphDocument source) {
        update();
    }

    @Override
    public final void elementNameChanged(final ElementContainer ec) {
        update();
    }

    @Override
    public final void userFieldValueChanged(final UserFieldTarget userFieldTarget) {
        update();
    }

    // GDCollectionChangeListener Ende
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

    @SafeVarargs
    public final SimpleMetaPath createSimpleMetaPath(@Nullable final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        ModelElement me = getModelElement();
        return me.createSimpleMetaPath(searchElementClass, edgeClasses);
    }

    //////////////////////////////////
    // TabbedPanel -> add SubPanels //
    //////////////////////////////////

    @Override
    protected void addTab(final String title, final Icon icon, final Component component) {
        if (component instanceof DescriptedSingleConnectionPanel) {
            DescriptedSingleConnectionPanel panel = (DescriptedSingleConnectionPanel) component;
            panel.addSelf();
        }
        super.addTab(title, icon, component);
    }

    private TabbedPanel lastCreatedTabbedPanel;

    public final void addTabbedPanel(final Class<? extends ModelElement> elementClass) {
        ElementsNameBuilder elementsNameBuilder = modelElement.getElementsNameBuilder();
        String displayablePluralName = elementsNameBuilder.getDisplayablePluralName(elementClass);
        addTabbedPanel(displayablePluralName);
    }

    public final void addTabbedPanel(final String nameResKey) {
        lastCreatedTabbedPanel = new TabbedPanel(this);
        String tabTitle = Tool3lgmConstants.getResStringWithoutError(nameResKey);
        lastCreatedTabbedPanel.setName(tabTitle);
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
            SimpleMetaPath metaPath = createSimpleMetaPath(searchElementClass, edgeClasses);
            PathConnectionPanel panel = new PathConnectionPanel(this, true, metaPath);
            lastCreatedTabbedPanel.addTab(panel);
        }
    }

    //////////////////////////////////////////////////////
    // DescripPanel ( = General-Panel) -> add SubPanels //
    //////////////////////////////////////////////////////

    @SafeVarargs
    public final void addDescripDescriptedPanel(final Class<? extends Edge>... edgeClasses) {
        addDescripDescriptedPanel(false, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripDescriptedPanel(final boolean labelLastEdgeName, final Class<? extends Edge>... edgeClasses) {
        addDescripDescriptedPanel(labelLastEdgeName, null, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripDescriptedPanel(final boolean labelLastEdgeName, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        addDescripPanel(labelLastEdgeName, true, searchElementClass, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripPanel(final Class<? extends Edge>... edgeClasses) {
        addDescripPanel(null, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripPanel(@Nullable final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        addDescripPanel(false, searchElementClass, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripPanel(final boolean labelLastEdgeName, final Class<? extends Edge>... edgeClasses) {
        addDescripPanel(labelLastEdgeName, null, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripPanel(final boolean labelLastEdgeName, @Nullable final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        addDescripPanel(labelLastEdgeName, false, searchElementClass, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripPanel(final boolean labelLastEdgeName, final boolean showDescription, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        SimpleMetaPath metaPath = createSimpleMetaPath(searchElementClass, edgeClasses);
        addDescripPanel(labelLastEdgeName, showDescription, metaPath);
    }

    public final void addDescripPanel(final boolean labelLastEdgeName, final boolean showDescription, final SimpleMetaPath metaPath) {
        MetaModel metaModel = modelElement.getMetaModel();
        if (metaModel.isVisible(metaPath)) {
            if (metaPath.isSingleConnection()) {
                if (showDescription) {
                    descripPanel.addDescriptedSingleConnectionPanel(labelLastEdgeName, metaPath);
                } else {
                    descripPanel.addSingleConnectionPanel(labelLastEdgeName, metaPath);
                }
            } else {
                descripPanel.addListPanel(labelLastEdgeName, metaPath);
            }
        }
    }

    /////////////////////
    // DescriptedPanel //
    /////////////////////

    @SafeVarargs
    public final void addDescriptedSingleConnectionPanel(final Class<? extends Edge>... edgeClasses) {
        addTab(new DescriptedSingleConnectionPanel(this, createSimpleMetaPath(null, edgeClasses)));
    }

    ///////////////
    // PathPanel //
    ///////////////

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
        SimpleMetaPath metaPath = createSimpleMetaPath(searchElementClass, edgeClasses);
        addTab(new PathConnectionPanel(this, labelLastEdgeName, true, metaPath));
    }

    ///////////////////
    // PathLeafPanel //
    ///////////////////

    @SafeVarargs
    public final void addPathConnectionLeafPanel(final Class<? extends Edge>... edgeClasses) {
        addPathConnectionLeafPanel(false, edgeClasses);
    }

    @SafeVarargs
    public final void addPathConnectionLeafPanel(final boolean labelLastEdgeName, final Class<? extends Edge>... edgeClasses) {
        SimpleMetaPath metaPath = createSimpleMetaPath(null, edgeClasses);
        addTab(new PathConnectionLeafPanel(this, labelLastEdgeName, true, metaPath));
    }

    @SafeVarargs
    public final void addPathConnectionInfoPanel(final Class<? extends Edge>... edgeClasses) {
        SimpleMetaPath metaPath = createSimpleMetaPath(null, edgeClasses);
        PathConnectionPanel panel = new PathConnectionPanel(this, false, metaPath);
        addTab(panel);
    }

    ///////////////
    // EdgePanel //
    ///////////////

    public void addEdgePanel(final Class<? extends Edge> edgeClass) {
        addEdgePanel(null, edgeClass);
    }

    public void addEdgePanel(final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass) {
        addEdgePanel(searchElementClass, edgeClass, false);
    }

    private void addEdgePanel(final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass, final boolean add2SubTab) {
        SimpleMetaPath metaPath = createSimpleMetaPath(searchElementClass, edgeClass);
        //Wenn sich ein Pfad für diese Elementart nicht anlegen lässt -> Panel nicht adden. Das ist der Fall, wenn Kanten einer Oberklasse
        //für eine Unterklasse nicht mehr gelten (z.B. Service-Metamodell: ApplicationSystem -> ApplicationSystem_IheActorInstance_Edge soll
        //für IheActorInstances nicht angezeigt werden, da IheActorInstances keine IheActorInstances untergordnet werden können.
        if (metaPath == null) {
            return;
        }
        MetaModel metaModel = modelElement.getMetaModel();
        Class<? extends ModelElement> meClass = modelElement.getClass();
        if (!metaModel.isStartOrEndClass(edgeClass, meClass)) { //checken, wenn das Panel aus einer Oberklasse kommt, ob diese Kante in der Unterklasse überhaupt noch vorkommt (siehe removed edges in Metamodel)
            //System.err.println(searchElementClass.getSimpleName() + " removed " + edgeClass.getSimpleName() or invalid path definition);
            return;
        }
        if (!metaModel.isVisible(metaPath)) {
            return;
        }
        ElementDialogPanel panel2Add = null;
        if (MetaModel.isComposition(edgeClass)) {
            Class<? extends CompositionEdge> compositionEdgeClass = edgeClass.asSubclass(CompositionEdge.class);
            panel2Add = new MutipleCompositionPanel(this, searchElementClass, compositionEdgeClass);
        } else if (MetaModel.isDoubleMeaningEdge(edgeClass)) {
            panel2Add = new DoubleMeaningEdgePanel(this, searchElementClass, edgeClass);
            //Kanten die nicht doppeltdeutig sind, aber dieselben Elementarten verbinden und in beide Richtungen unterschiedlich heißen, müssen auch in beiden Richtungen angeboten werden
        } else if (Edge.getStartClass(edgeClass) == Edge.getEndClass(edgeClass) && metaModel.isDirectedEdge(edgeClass)) {
            panel2Add = new DoubleMeaningEdgePanel(this, searchElementClass, edgeClass);
        } else {
            boolean editable = !isInfoDialog() && metaPath.isCreatable(true);
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
        SimpleMetaPath simpleMetaPath = SimpleMetaPathCreator.createSimpleMetaPath(doc.getMetaModel(), modelElement.getClass(), metaPathStepWithPathName, edgeClasses);
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
                    if (!nonAbstractSimpleMetaPaths.isCreatable(true)) {
                        editable = false;
                        break;
                    }
                }
            }
        }
        addTab(new ConnectedElementsTablePanel(this, tableDefinition, allDifferentSimpleMetaPaths));
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
