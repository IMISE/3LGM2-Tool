package de.imise.tool3lgm.graphtools.dialog.element;

import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_END_ELEMENT_TYPE;
import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.annotation.Nullable;
import javax.swing.Icon;
import javax.swing.JPanel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption;
import de.imise.tool3lgm.graphtools.dialog.element.panel.ConnectedElementsTableDefinition;
import de.imise.tool3lgm.graphtools.dialog.element.panel.ConnectedElementsTablePanel;
import de.imise.tool3lgm.graphtools.dialog.element.panel.DescriptedSingleConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.element.panel.DoubleMeaningEdgePanel;
import de.imise.tool3lgm.graphtools.dialog.element.panel.InstanciationPathPanel;
import de.imise.tool3lgm.graphtools.dialog.element.panel.MultiPanelElementDialogPanel;
import de.imise.tool3lgm.graphtools.dialog.element.panel.MutipleCompositionPanel;
import de.imise.tool3lgm.graphtools.dialog.element.panel.PathConnectionLeafPanel;
import de.imise.tool3lgm.graphtools.dialog.element.panel.PathConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.element.panel.TabbedPanel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPathCreator;
import de.imise.util.swing.component.tab.ReorderableTabbedPane;

/**
 * Eigenschaftsdialog für Modellelemnte, also Node und Kanten.<br>
 *
 * @author N.N., AXS
 */
public class ElementPropertyDialog extends AbstractElementPropertyDialog implements ActionListener {

    /**
     * @param modelElement
     */
    public ElementPropertyDialog(final ModelElement modelElement) {
        super(modelElement);
    }

    /**
     * Wenn der Dialog von keiner Unterklasse des ModelElements aus seinem Default-Zustand (DescripPanel + evtl. Benutzerdef. Eigenschaften) geändert
     * wurde, dann werden hier automatisch für alle Kanten des Elementes passende Panels hinzugefügt.
     */
    public void extendDefaultDialog() {
        if (isUnchangedDefaultDialog()) {
            MetaModel metaModel = getMetaModel();
            Class<? extends ModelElement> elementClass = getModelElementClass();
            for (Class<? extends Edge> edgeClass : metaModel.getEdgeTypes(elementClass)) {
                addEdgePanel(edgeClass);
            }
        }
    }

    //////////////////////////////
    // this -> add Panel as Tab //
    //////////////////////////////

    @Override
    protected void addTab(final String title, final Icon icon, final Component component) {
        if (component instanceof DescriptedSingleConnectionPanel) {
            DescriptedSingleConnectionPanel panel = (DescriptedSingleConnectionPanel) component;
            panel.addSelf();
        }
        super.addTab(title, icon, component);
    }

    //////////////////////////////////////////////////////
    // DescripPanel ( = General-Panel) -> add SubPanels //
    //////////////////////////////////////////////////////

    /**
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addDescripDescriptedPanel(final Class<? extends Edge>... edgeClasses) {
        addDescripDescriptedPanel(PanelLabelOption.LABEL_END_ELEMENT_TYPE, edgeClasses);
    }

    /**
     * @param panelLabelOption
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addDescripDescriptedPanel(final PanelLabelOption panelLabelOption, final Class<? extends Edge>... edgeClasses) {
        addDescripDescriptedPanel(panelLabelOption, null, edgeClasses);
    }

    /**
     * @param panelLabelOption
     * @param searchElementClass
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addDescripDescriptedPanel(final PanelLabelOption panelLabelOption, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        addDescripPanel(panelLabelOption, true, searchElementClass, edgeClasses);
    }

    /**
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addDescripPanel(final Class<? extends Edge>... edgeClasses) {
        addDescripPanel((Class<? extends ModelElement>) null, edgeClasses);
    }

    /**
     * @param searchElementClass
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addDescripPanel(@Nullable final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        addDescripPanel(LABEL_END_ELEMENT_TYPE, searchElementClass, edgeClasses);
    }

    /**
     * @param panelLabelOption
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addDescripPanel(final PanelLabelOption panelLabelOption, final Class<? extends Edge>... edgeClasses) {
        addDescripPanel(panelLabelOption, null, edgeClasses);
    }

    /**
     * @param panelLabelOption
     * @param searchElementClass
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addDescripPanel(final PanelLabelOption panelLabelOption, @Nullable final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        addDescripPanel(panelLabelOption, false, searchElementClass, edgeClasses);
    }

    /**
     * @param panelLabelOption
     * @param showDescription
     * @param searchElementClass
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addDescripPanel(final PanelLabelOption panelLabelOption, final boolean showDescription, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        SimpleMetaPath metaPath = createSimpleMetaPath(searchElementClass, edgeClasses);
        addDescripPanel(panelLabelOption, showDescription, metaPath);
    }

    /**
     * @param panelLabelOption
     * @param showDescription
     * @param metaPath
     */
    public final void addDescripPanel(final PanelLabelOption panelLabelOption, final boolean showDescription, final SimpleMetaPath metaPath) {
        MetaModel metaModel = getMetaModel();
        if (metaModel.isVisible(metaPath)) {
            if (metaPath.isSingleConnection()) {
                if (showDescription) {
                    descripPanel.addDescriptedSingleConnectionPanel(panelLabelOption, metaPath);
                } else {
                    descripPanel.addSingleConnectionPanel(panelLabelOption, metaPath);
                }
            } else {
                descripPanel.addListPanel(panelLabelOption, metaPath);
            }
        }
    }

    //////////////////////////////////////
    // MultiPanel -> add SubPanels //
    //////////////////////////////////////

    /** The last added Panel that is an subclass of {@link MultiPanelElementDialogPanel} (except the general) */
    private MultiPanelElementDialogPanel lastAddedMultiPanel;

    /**
     * @param elementClass
     */
    public final void addMultiPanel(final Class<? extends ModelElement> elementClass) {
        ElementsNameBuilder elementsNameBuilder = getElementsNameBuilder();
        String displayablePluralName = elementsNameBuilder.getDisplayablePluralName(elementClass);
        addMultiPanel(displayablePluralName);
    }

    /**
     * @param nameResKey
     */
    public final void addMultiPanel(final String nameResKey) {
        lastAddedMultiPanel = new MultiPanelElementDialogPanel(this);
        String tabTitle = Tool3lgmConstants.getResStringWithoutError(nameResKey);
        lastAddedMultiPanel.setName(tabTitle);
        addTab(lastAddedMultiPanel);
    }

    /**
     * @param simpleMetaPath
     */
    public final void addMultiPanelDescriptedSingleConnectionPanel(final SimpleMetaPath simpleMetaPath) {
        lastAddedMultiPanel.addDescriptedSingleConnectionPanel(simpleMetaPath);
    }

    /**
     * @param panelLabelOption
     * @param simpleMetaPath
     */
    public final void addMultiPanelDescriptedSingleConnectionPanel(final PanelLabelOption panelLabelOption, final SimpleMetaPath simpleMetaPath) {
        lastAddedMultiPanel.addDescriptedSingleConnectionPanel(panelLabelOption, simpleMetaPath);
    }

    /**
     * @param panelLabelOption
     * @param simpleMetaPath
     */
    public final void addMultiPanelSingleConnectionPanel(final PanelLabelOption panelLabelOption, final SimpleMetaPath simpleMetaPath) {
        lastAddedMultiPanel.addSingleConnectionPanel(panelLabelOption, simpleMetaPath);
    }

    /**
     * @param panelLabelOption
     * @param simpleMetaPath
     */
    public final void addMultiPanelListPanel(final PanelLabelOption panelLabelOption, final SimpleMetaPath simpleMetaPath) {
        lastAddedMultiPanel.addListPanel(panelLabelOption, simpleMetaPath);
    }

    /**
     * @param edgeClass
     */
    public final void addMultiPanelEdgePanel(final Class<? extends Edge> edgeClass) {
        addMultiPanelEdgePanel((Class<? extends ModelElement>) null, edgeClass);
    }

    /**
     * @param searchElementClass
     * @param edgeClass
     */
    public final void addMultiPanelEdgePanel(final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass) {
        addMultiPanelEdgePanel(LABEL_END_ELEMENT_TYPE, searchElementClass, edgeClass);
    }

    /**
     * @param panelLabelOption
     * @param edgeClass
     */
    public final void addMultiPanelEdgePanel(final PanelLabelOption panelLabelOption, final Class<? extends Edge> edgeClass) {
        addMultiPanelEdgePanel(panelLabelOption, null, edgeClass);
    }

    /**
     * @param titleLabelOption
     * @param westLabelOption
     * @param searchElementClass
     * @param edgeClass
     */
    public final void addMultiPanelEdgePanel(final PanelLabelOption titleLabelOption, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass) {
        AbstractPathConnectionPanel addableEdgePanel = getAddableEdgePanel(titleLabelOption, titleLabelOption, searchElementClass, edgeClass); //das westLabel ist hier egal -> einfach auf dasselbe wie den title setzen
        lastAddedMultiPanel.addPanel(addableEdgePanel);
    }

    //////////////////////////////////
    // TabbedPanel -> add SubPanels //
    //////////////////////////////////

    /** Das zuletzt über die Funktion {@link ElementPropertyDialog#addTabbedPanel(Class)} hinzugefügte TabbedPanel */
    private TabbedPanel lastAddedTabbedPanel;

    /**
     * @param elementClass
     */
    public final void addTabbedPanel(final Class<? extends ModelElement> elementClass) {
        ElementsNameBuilder elementsNameBuilder = getElementsNameBuilder();
        String displayablePluralName = elementsNameBuilder.getDisplayablePluralName(elementClass);
        addTabbedPanel(displayablePluralName);
    }

    /**
     * @param nameResKey
     */
    public final void addTabbedPanel(final String nameResKey) {
        lastAddedTabbedPanel = new TabbedPanel(this);
        String tabTitle = Tool3lgmConstants.getResStringWithoutError(nameResKey);
        lastAddedTabbedPanel.setName(tabTitle);
        addTab(lastAddedTabbedPanel);
    }

    /**
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addTabbedPanelPathConnectionPanel(final Class<? extends Edge>... edgeClasses) {
        addTabbedPanelPathConnectionPanel(null, edgeClasses);
    }

    /**
     * @param searchElementClass
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addTabbedPanelPathConnectionPanel(final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        if (edgeClasses.length == 1) {
            addEdgePanel(true, searchElementClass, edgeClasses[0]);
        } else {
            SimpleMetaPath metaPath = createSimpleMetaPath(searchElementClass, edgeClasses);
            PathConnectionPanel panel = new PathConnectionPanel(this, LABEL_LAST_EDGE_CONNECTION_NAME, edgeClasses.length > 1 ? LABEL_END_ELEMENT_TYPE : LABEL_LAST_EDGE_CONNECTION_NAME, metaPath);
            lastAddedTabbedPanel.addTab(panel);
        }
    }

    /////////////////////
    // DescriptedPanel //
    /////////////////////

    /**
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addDescriptedSingleConnectionPanel(final Class<? extends Edge>... edgeClasses) {
        SimpleMetaPath simpleMetaPath = createSimpleMetaPath(null, edgeClasses);
        addDescriptedSingleConnectionPanel(simpleMetaPath);
    }

    /**
     * @param metaPath
     */
    public final void addDescriptedSingleConnectionPanel(final MetaPath metaPath) {
        DescriptedSingleConnectionPanel panel = new DescriptedSingleConnectionPanel(this, metaPath);
        addTab(panel);
    }

    ///////////////
    // PathPanel //
    ///////////////

    /**
     * @param edgeClass
     */
    public final void addEdgePanel(final Class<? extends Edge> edgeClass) {
        addEdgePanel(false, null, edgeClass);
    }

    /**
     * @param titleLabelOption
     * @param westLabelOption
     * @param edgeClass
     */
    public final void addEdgePanel(final PanelLabelOption titleLabelOption, final PanelLabelOption westLabelOption, final Class<? extends Edge> edgeClass) {
        addEdgePanel(titleLabelOption, westLabelOption, false, null, edgeClass);
    }

    /**
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addPanel(final Class<? extends Edge>... edgeClasses) {
        addPathConnectionPanel(LABEL_END_ELEMENT_TYPE, LABEL_LAST_EDGE_CONNECTION_NAME, edgeClasses);
    }

    /**
     * @param searchElementClass
     * @param edgeClass
     */
    public void addEdgePanel(final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass) {
        addEdgePanel(false, searchElementClass, edgeClass);
    }

    /**
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addPathConnectionPanel(final Class<? extends Edge>... edgeClasses) {
        addPathConnectionPanel((Class<? extends ModelElement>) null, edgeClasses);
    }

    /**
     * @param edgeClasses
     */
    public final void addPathConnectionPanel(final MetaPath metaPath) {
        addPathConnectionPanel(metaPath, false);
    }

    /**
     * @param metaPath
     * @param showMultipleConnectionEvenIfMetaPathIsSingleConnection
     */
    public final void addPathConnectionPanel(final MetaPath metaPath, final boolean showMultipleConnectionEvenIfMetaPathIsSingleConnection) {
        addPathConnectionPanel(LABEL_END_ELEMENT_TYPE, LABEL_LAST_EDGE_CONNECTION_NAME, metaPath, showMultipleConnectionEvenIfMetaPathIsSingleConnection);
    }

    /**
     * @param searchElementClass
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addPathConnectionPanel(final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        addPathConnectionPanel(LABEL_END_ELEMENT_TYPE, LABEL_LAST_EDGE_CONNECTION_NAME, searchElementClass, edgeClasses);
    }

    /**
     * @param titleLabelOption
     * @param westLabelOption
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addPathConnectionPanel(final PanelLabelOption titleLabelOption, final PanelLabelOption westLabelOption, final Class<? extends Edge>... edgeClasses) {
        addPathConnectionPanel(titleLabelOption, westLabelOption, null, edgeClasses);
    }

    /**
     * @param titleLabelOption
     * @param westLabelOption
     * @param searchElementClass
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addPathConnectionPanel(final PanelLabelOption titleLabelOption, final PanelLabelOption westLabelOption, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        SimpleMetaPath metaPath = createSimpleMetaPath(searchElementClass, edgeClasses);
        addPathConnectionPanel(titleLabelOption, westLabelOption, metaPath);
    }

    /**
     * @param titleLabelOption
     * @param westLabelOption
     * @param metaPath
     */
    public final void addPathConnectionPanel(final PanelLabelOption titleLabelOption, final PanelLabelOption westLabelOption, final MetaPath metaPath) {
        addPathConnectionPanel(titleLabelOption, westLabelOption, metaPath, false);
    }

    /**
     * @param titleLabelOption
     * @param westLabelOption
     * @param metaPath
     * @param showMultipleConnectionEvenIfMetaPathIsSingleConnection
     */
    public final void addPathConnectionPanel(final PanelLabelOption titleLabelOption, final PanelLabelOption westLabelOption, final MetaPath metaPath, final boolean showMultipleConnectionEvenIfMetaPathIsSingleConnection) {
        MetaModel metaModel = getMetaModel();
        if (metaModel.isVisible(metaPath)) {
            boolean singleConnection = !showMultipleConnectionEvenIfMetaPathIsSingleConnection && metaPath.isSingleConnection();
            //            System.err.print(metaPath.getEndClass().getSimpleName() + ": ");
            boolean editable = metaPath.isCreatable(false);
            //            System.err.print(editable + " -> ");
            editable &= !metaPath.isFirstPathElementDependent();
            //            System.err.print(editable + " -> ");
            editable &= !metaPath.isLastPathElementDependent();
            //            System.err.println(editable);
            if (editable || !singleConnection) {
                JPanel panel2Add = InstanciationPathPanel.getInstanciationPathPanel(this, metaPath);
                if (panel2Add == null) {
                    panel2Add = new PathConnectionPanel(this, titleLabelOption, westLabelOption, false, metaPath);
                }
                addTab(panel2Add);
            } else {
                addDescriptedSingleConnectionPanel(metaPath);
            }
        }
    }

    ///////////////////
    // PathLeafPanel //
    ///////////////////

    /**
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addPathConnectionLeafPanel(final Class<? extends Edge>... edgeClasses) {
        addPathConnectionLeafPanel(LABEL_END_ELEMENT_TYPE, LABEL_LAST_EDGE_CONNECTION_NAME, edgeClasses);
    }

    /**
     * @param titleLabelOption
     * @param westLabelOption
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addPathConnectionLeafPanel(final PanelLabelOption titleLabelOption, final PanelLabelOption westLabelOption, final Class<? extends Edge>... edgeClasses) {
        SimpleMetaPath metaPath = createSimpleMetaPath(null, edgeClasses);
        addTab(new PathConnectionLeafPanel(this, titleLabelOption, westLabelOption, metaPath));
    }

    //    @SafeVarargs
    //    private final void addPathConnectionInfoPanel(final Class<? extends Edge>... edgeClasses) {
    //        SimpleMetaPath metaPath = createSimpleMetaPath(null, edgeClasses);
    //        System.err.println(metaPath.getAllMetaPathsName());
    //        System.err.println("\t" + metaPath.isCreatable(false) + "\t" + metaPath.isCreatable(true) + "\t" + metaPath.isLastPathElementDependent() + "\t" + metaPath.isLastPathElementNeededForExistence() + "\t" + metaPath.isSingleConnection());
    //        System.err.println();
    //        PathConnectionPanel panel = new PathConnectionPanel(this, false, metaPath);
    //        addTab(panel);
    //    }
    //
    ///////////////
    // EdgePanel //
    ///////////////

    /**
     * @param add2SubTab
     * @param searchElementClass
     * @param edgeClass
     */
    private void addEdgePanel(final boolean add2SubTab, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass) {
        addEdgePanel(LABEL_END_ELEMENT_TYPE, LABEL_LAST_EDGE_CONNECTION_NAME, add2SubTab, searchElementClass, edgeClass);
    }

    /**
     * @param titleLabelOption
     * @param westLabelOption
     * @param searchElementClass
     * @param edgeClass
     * @return
     */
    private final AbstractPathConnectionPanel getAddableEdgePanel(final PanelLabelOption titleLabelOption, final PanelLabelOption westLabelOption, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass) {
        SimpleMetaPath metaPath = createSimpleMetaPath(searchElementClass, edgeClass);
        //Wenn sich ein Pfad für diese Elementart nicht anlegen lässt -> Panel nicht adden. Das ist der Fall, wenn Kanten einer Oberklasse
        //für eine Unterklasse nicht mehr gelten (z.B. Service-Metamodell: ApplicationSystem -> ApplicationSystem_IheActorInstance_Edge soll
        //für IheActorInstances nicht angezeigt werden, da IheActorInstances keine IheActorInstances untergordnet werden können.
        if (metaPath == null) {
            return null;
        }
        MetaModel metaModel = getMetaModel();
        Class<? extends ModelElement> meClass = getModelElementClass();
        if (!MetaModel.isStartOrEndClass(edgeClass, meClass)) { //checken, wenn das Panel aus einer Oberklasse kommt, ob diese Kante in der Unterklasse überhaupt noch vorkommt (siehe removed edges in Metamodel)
            //System.err.println(searchElementClass.getSimpleName() + " removed " + edgeClass.getSimpleName() or invalid path definition);
            return null;
        }
        if (!metaModel.isVisible(metaPath)) {
            return null;
        }
        AbstractPathConnectionPanel panel2Add = null;
        if (MetaModel.isComposition(edgeClass)) {
            Class<? extends CompositionEdge> compositionEdgeClass = edgeClass.asSubclass(CompositionEdge.class);
            panel2Add = new MutipleCompositionPanel(this, titleLabelOption, westLabelOption, searchElementClass, compositionEdgeClass);
        } else if (MetaModel.isDoubleMeaningEdge(edgeClass)) {
            panel2Add = new DoubleMeaningEdgePanel(this, titleLabelOption, searchElementClass, edgeClass);
            //Kanten die nicht doppeltdeutig sind, aber dieselben Elementarten verbinden und in beide Richtungen unterschiedlich heißen, müssen auch in beiden Richtungen angeboten werden
        } else if (Edge.getStartClass(edgeClass) == Edge.getEndClass(edgeClass) && metaModel.isDirectedEdge(edgeClass)) {
            panel2Add = new DoubleMeaningEdgePanel(this, titleLabelOption, searchElementClass, edgeClass);
        } else {
            panel2Add = new PathConnectionPanel(this, titleLabelOption, westLabelOption, metaPath);
        }
        return panel2Add;
    }

    /**
     * @param titleLabelOption
     * @param westLabelOption
     * @param add2SubTab
     * @param searchElementClass
     * @param edgeClass
     */
    private final void addEdgePanel(final PanelLabelOption titleLabelOption, final PanelLabelOption westLabelOption, final boolean add2SubTab, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass) {
        AbstractPathConnectionPanel panel2Add = getAddableEdgePanel(titleLabelOption, westLabelOption, searchElementClass, edgeClass);
        if (panel2Add != null) {
            if (add2SubTab) {
                lastAddedTabbedPanel.addTab(panel2Add);
            } else {
                addTab(panel2Add);
            }
        }
    }

    /**
     * @param tableDefinition
     * @param metaPathStepWithPathName
     * @param edgeClasses
     */
    @SafeVarargs
    public final void addTablePanel(final ConnectedElementsTableDefinition tableDefinition, final int metaPathStepWithPathName, final Class<? extends Edge>... edgeClasses) {
        try {
            MetaModel metaModel = getMetaModel();
            Class<? extends ModelElement> modelElementClass = getModelElementClass();
            SimpleMetaPath simpleMetaPath = SimpleMetaPathCreator.createSimpleMetaPath(metaModel, modelElementClass, metaPathStepWithPathName, edgeClasses);
            addTablePanel(tableDefinition, simpleMetaPath);
        } catch (Exception e) {
            //if in the createSimpleMetaPath(...) an error occurs then the tool should not crash
            e.printStackTrace();
        }
    }

    /**
     * @param tableDefinition
     * @param simpleMetaPath
     */
    public final void addTablePanel(final ConnectedElementsTableDefinition tableDefinition, final SimpleMetaPath simpleMetaPath) {
        ConnectedElementsTablePanel connectedElementsTablePanel = new ConnectedElementsTablePanel(this, tableDefinition, simpleMetaPath);
        addTab(connectedElementsTablePanel);
    }

    @Override
    public void windowActivated(final WindowEvent e) {
        removeEmptyTabs();
        super.windowActivated(e);
    }

    /**
     *
     */
    private void removeEmptyTabs() {
        for (int i = getTabCount() - 1; i >= 0; i--) {
            Component comp = getTabComponentAt(i);
            if (isEmptyTabbedPanel(comp)) {
                removeTab(i);
            }
        }
    }

    /**
     * @param comp
     * @return
     */
    private boolean isEmptyTabbedPanel(Component comp) {
        if (comp instanceof TabbedPanel) {
            TabbedPanel tabbedPanel = (TabbedPanel) comp;
            if (tabbedPanel.getComponentCount() == 1) {
                comp = tabbedPanel.getComponent(0);
                if (tabbedPanel.getComponent(0) instanceof ReorderableTabbedPane) {
                    ReorderableTabbedPane tabbedPane = (ReorderableTabbedPane) comp;
                    int componentCount = tabbedPane.getComponentCount();
                    return componentCount == 0;
                }
            }
        }
        return false;
    }

}
