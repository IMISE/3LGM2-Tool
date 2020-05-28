package de.imise.tool3lgm.gui.viewpane;

import java.awt.Component;
import java.awt.Cursor;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;

/**
 * The component that surrounds resp. contains a single {@link ViewPane}.
 *
 * @author AXS (20.05.2020)
 */
public interface ViewPaneFrameComponent extends GraphDocumentOwner {

    /**
     * @return the contained {@link ViewPane}
     */
    public ViewPane getViewPane();

    @Override
    public default GraphDocument getGraphDocument() {
        ViewPane viewPane = getViewPane();
        return viewPane == null ? null : viewPane.getGraphDocument();
    }

    /**
     * @return
     */
    public Component getParent();

    /**
     *
     */
    public void setCursor(final Cursor cursor);

    /**
     * @return
     */
    public boolean isVisible();

    /**
     *
     */
    public void dispose();

    /**
     * @return the component that contains this. The direct or indirect parent
     *         component of this should be a {@link ViewPaneFrameComponentParent}.
     */
    public default ViewPaneFrameComponentParent getFrameComponentParent() {
        Component parent = getParent();
        while (parent != null) {
            if (parent instanceof ViewPaneFrameComponentParent) {
                return (ViewPaneFrameComponentParent) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    /**
     * @return if this component is the soelected or active component in the
     *         corresponding {@link ViewPaneFrameComponentParent}
     */
    public default boolean isSelected() {
        ViewPaneFrameComponentParent frameComponentParent = getFrameComponentParent();
        return frameComponentParent != null && frameComponentParent.isSelected(this);
    }

    /**
     * Sets this component selected or active in its {@link ViewPaneFrameComponentParent}
     */
    public default void setSelected() {
        ViewPaneFrameComponentParent frameComponentParent = getFrameComponentParent();
        if (frameComponentParent != null) {
            frameComponentParent.setSelected(this);
        }
    }

    /**
     * @return
     */
    public default String getName() {
        ViewPane viewPane = getViewPane();
        return viewPane.getName();
    }

    /**
     * @return
     */
    public default String getFullName() {
        ViewPane viewPane = getViewPane();
        return viewPane.getFullName();
    }

}
