package de.imise.tool3lgm.gui;

import java.awt.Component;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;

/**
 * The component that surrounds resp. contains a single {@link ViewContainer}.
 *
 * @author AXS (20.05.2020)
 */
public interface ViewContainerFrameComponent extends GraphDocumentOwner {

    /**
     * @return the contained {@link ViewContainer}
     */
    public ViewContainer getViewContainer();

    @Override
    public default GraphDocument getGraphDocument() {
        ViewContainer viewContainer = getViewContainer();
        return viewContainer == null ? null : viewContainer.getGraphDocument();
    }

    /**
     * @return
     */
    public Component getParent();

    /**
     * @return the component that contains this. The direct or indirect parent
     *         component of this should be a {@link ViewContainerFrameComponentParent}.
     */
    public default ViewContainerFrameComponentParent getFrameComponentParent() {
        Component parent = getParent();
        while (parent != null) {
            if (parent instanceof ViewContainerFrameComponentParent) {
                return (ViewContainerFrameComponentParent) parent;
            }
        }
        return null;
    }

    /**
     * @return if this component is the soelected or active component in the
     *         corresponding {@link ViewContainerFrameComponentParent}
     */
    public default boolean isSelected() {
        ViewContainerFrameComponentParent graphViewContainerParent = getFrameComponentParent();
        return graphViewContainerParent != null && graphViewContainerParent.isSelected(this);
    }

    /**
     * Sets this component selected or active in its {@link ViewContainerFrameComponentParent}
     */
    public default void setSelected() {
        ViewContainerFrameComponentParent graphViewContainerParent = getFrameComponentParent();
        if (graphViewContainerParent != null) {
            graphViewContainerParent.setSelected(this);
        }
    }

}
