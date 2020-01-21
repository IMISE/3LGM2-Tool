package de.imise.tool3lgm.graphtools.dialog;

import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_END_ELEMENT_TYPE;
import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;
import javax.swing.Icon;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableDefinition;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTablePanel;
import de.imise.tool3lgm.graphtools.dialog.panel.DescriptedSingleConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.DoubleMeaningEdgePanel;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.MutipleCompositionPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.PathConnectionLeafPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.PathConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.TabbedPanel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPathCreator;
import de.imise.util.swing.component.TabbedPane;

/**
 * Eigenschaftsdialog für Modellelemnte, also Node und Kanten.<br>
 *
 * @author N.N., AXS
 */
public class ElementPropertyDialog extends AbstractElementPropertyDialog implements ActionListener, LGMChangeListenerSimple {

    /**
     * @param modelElement
     * @param gdcoll
     */
    public ElementPropertyDialog(final ModelElement modelElement, final GDCollection gdcoll) {
        super(modelElement, gdcoll);
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

    /** Das zuletzt über die Funktion {@link ElementPropertyDialog#addTabbedPanel(Class)} hinzugefügte TabbedPanel */
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
            addEdgePanel(true, searchElementClass, edgeClasses[0]);
        } else {
            SimpleMetaPath metaPath = createSimpleMetaPath(searchElementClass, edgeClasses);
            PathConnectionPanel panel = new PathConnectionPanel(this, LABEL_LAST_EDGE_CONNECTION_NAME, metaPath);
            lastCreatedTabbedPanel.addTab(panel);
        }
    }

    //////////////////////////////////////////////////////
    // DescripPanel ( = General-Panel) -> add SubPanels //
    //////////////////////////////////////////////////////

    @SafeVarargs
    public final void addDescripDescriptedPanel(final Class<? extends Edge>... edgeClasses) {
        addDescripDescriptedPanel(PanelLabelOption.LABEL_END_ELEMENT_TYPE, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripDescriptedPanel(final PanelLabelOption panelLabelOption, final Class<? extends Edge>... edgeClasses) {
        addDescripDescriptedPanel(panelLabelOption, null, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripDescriptedPanel(final PanelLabelOption panelLabelOption, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        addDescripPanel(panelLabelOption, true, searchElementClass, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripPanel(final Class<? extends Edge>... edgeClasses) {
        addDescripPanel((Class<? extends ModelElement>) null, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripPanel(@Nullable final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        addDescripPanel(PanelLabelOption.LABEL_END_ELEMENT_TYPE, searchElementClass, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripPanel(final PanelLabelOption panelLabelOption, final Class<? extends Edge>... edgeClasses) {
        addDescripPanel(panelLabelOption, null, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripPanel(final PanelLabelOption panelLabelOption, @Nullable final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        addDescripPanel(panelLabelOption, false, searchElementClass, edgeClasses);
    }

    @SafeVarargs
    public final void addDescripPanel(final PanelLabelOption panelLabelOption, final boolean showDescription, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        SimpleMetaPath metaPath = createSimpleMetaPath(searchElementClass, edgeClasses);
        addDescripPanel(panelLabelOption, showDescription, metaPath);
    }

    public final void addDescripPanel(final PanelLabelOption panelLabelOption, final boolean showDescription, final SimpleMetaPath metaPath) {
        MetaModel metaModel = modelElement.getMetaModel();
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

    public final void addEdgePanel(final Class<? extends Edge> edgeClass) {
        addEdgePanel(false, null, edgeClass);
    }

    public final void addEdgePanel(final PanelLabelOption panelLabelOption, final Class<? extends Edge> edgeClass) {
        addEdgePanel(panelLabelOption, false, null, edgeClass);
    }

    @SafeVarargs
    public final void addPanel(final Class<? extends Edge>... edgeClasses) {
        addPathConnectionPanel(LABEL_END_ELEMENT_TYPE, edgeClasses);
    }

    public void addEdgePanel(final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass) {
        addEdgePanel(false, searchElementClass, edgeClass);
    }

    @SafeVarargs
    public final void addPathConnectionPanel(final Class<? extends Edge>... edgeClasses) {
        addPathConnectionPanel((Class<? extends ModelElement>) null, edgeClasses);
    }

    @SafeVarargs
    public final void addPathConnectionPanel(final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        addPathConnectionPanel(LABEL_END_ELEMENT_TYPE, searchElementClass, edgeClasses);
    }

    @SafeVarargs
    public final void addPathConnectionPanel(final PanelLabelOption panelLabelOption, final Class<? extends Edge>... edgeClasses) {
        addPathConnectionPanel(panelLabelOption, null, edgeClasses);
    }

    @SafeVarargs
    public final void addPathConnectionPanel(final PanelLabelOption panelLabelOption, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        SimpleMetaPath metaPath = createSimpleMetaPath(searchElementClass, edgeClasses);
        MetaModel metaModel = modelElement.getMetaModel();
        if (metaModel.isVisible(metaPath)) {
            boolean singleConnection = metaPath.isSingleConnection();
            //            System.err.print(metaPath.getEndClass().getSimpleName() + ": ");
            boolean editable = metaPath.isCreatable(false);
            //            System.err.print(editable + " -> ");
            editable &= !metaPath.isFirstPathElementDependent();
            //            System.err.print(editable + " -> ");
            editable &= !metaPath.isLastPathElementDependent();
            //            System.err.println(editable);
            if (editable || !singleConnection) {
                addTab(new PathConnectionPanel(this, panelLabelOption, false, metaPath));
            } else {
                addDescriptedSingleConnectionPanel(edgeClasses);
            }
        }
    }

    ///////////////////
    // PathLeafPanel //
    ///////////////////

    @SafeVarargs
    public final void addPathConnectionLeafPanel(final Class<? extends Edge>... edgeClasses) {
        addPathConnectionLeafPanel(LABEL_END_ELEMENT_TYPE, edgeClasses);
    }

    @SafeVarargs
    public final void addPathConnectionLeafPanel(final PanelLabelOption panelLabelOption, final Class<? extends Edge>... edgeClasses) {
        SimpleMetaPath metaPath = createSimpleMetaPath(null, edgeClasses);
        addTab(new PathConnectionLeafPanel(this, panelLabelOption, metaPath));
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

    private void addEdgePanel(final boolean add2SubTab, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass) {
        addEdgePanel(LABEL_END_ELEMENT_TYPE, add2SubTab, searchElementClass, edgeClass);
    }

    private void addEdgePanel(final PanelLabelOption panelLabelOption, final boolean add2SubTab, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass) {
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
            panel2Add = new MutipleCompositionPanel(this, panelLabelOption, searchElementClass, compositionEdgeClass);
        } else if (MetaModel.isDoubleMeaningEdge(edgeClass)) {
            panel2Add = new DoubleMeaningEdgePanel(this, panelLabelOption, searchElementClass, edgeClass);
            //Kanten die nicht doppeltdeutig sind, aber dieselben Elementarten verbinden und in beide Richtungen unterschiedlich heißen, müssen auch in beiden Richtungen angeboten werden
        } else if (Edge.getStartClass(edgeClass) == Edge.getEndClass(edgeClass) && metaModel.isDirectedEdge(edgeClass)) {
            panel2Add = new DoubleMeaningEdgePanel(this, panelLabelOption, searchElementClass, edgeClass);
        } else {
            panel2Add = new PathConnectionPanel(this, panelLabelOption, metaPath);
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
