package de.imise.tool3lgm.gui;

import java.awt.Component;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewParameter;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;

/**
 * Interface for a panel for the graph view of a Szenario or the
 * main {@link GraphDocument}.
 *
 * @author AXS (10.05.2020)
 */
public interface GraphViewContainer extends ViewContainer {

    /**
     * @return the {@link GraphDocument} this GraphViewContainer is displaying
     */
    @Override
    public default GraphDocument getGraphDocument() {
        InputGraphArea inputGraphArea = getInputGraphArea();
        return inputGraphArea.getGraphDocument();
    }

    /**
     * @return the component that contains this. The parent component of this
     *         should be a {@link ViewContainerFrameComponent}.
     */
    public default ViewContainerFrameComponent getFrameComponent() {
        Component parent = getParent();
        while (parent != null) {
            if (parent instanceof ViewContainerFrameComponent) {
                return (ViewContainerFrameComponent) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    /**
     * @return if this component is the soelected or active component in the
     *         corresponding {@link ViewContainerFrameComponentParent}
     */
    public default boolean isSelected() {
        ViewContainerFrameComponent graphViewContainer = getFrameComponent();
        return graphViewContainer != null && graphViewContainer.isSelected();
    }

    /**
     * Sets this component selected or active in its {@link ViewContainerFrameComponentParent}
     */
    public default void setSelected() {
        ViewContainerFrameComponent graphViewContainer = getFrameComponent();
        if (graphViewContainer != null) {
            graphViewContainer.setSelected();
        }
    }

    /**
     * @return the {@link GraphViewParameter} of this graph view
     */
    public GraphViewParameter getGraphViewParameter();

    /**
     * @return the InputGraphArea this GraphViewContainer is displaying
     */
    public InputGraphArea getInputGraphArea();

    /**
     * @return the name of this view. Default is the name of the {@link GraphDocument}
     */
    @Override
    public default String getName() {
        GraphDocument doc = getGraphDocument();
        String name = doc.toString();
        return name;
    }

    /**
     * @return the name of this view. Default is "Name of GDCollection" + " - " +
     *         "Name of GraphDocument" + " (" + "Name of MetaModel" + ")";
     */
    @Override
    public default String getFullName() {
        GDCollection gdcoll = getCollection();
        String gdcollName = gdcoll.getName();
        String docName = getGraphDocument().getTitle();
        MetaModelContext metaModelContext = gdcoll.getMetaModelContext();
        String metaModelDisplayName = metaModelContext.getMetaModelDisplayName();
        String fullName = gdcollName + " - " + docName + "   (" + metaModelDisplayName + ")";
        return fullName;
    }

}