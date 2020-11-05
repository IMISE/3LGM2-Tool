package de.imise.tool3lgm.gui.viewpane;

import java.awt.Component;
import java.awt.Container;
import java.util.List;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPane;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPaneFrameComponent;
import de.imise.tool3lgm.gui.viewpane.matrix.MatrixViewPaneFrameComponent;

/**
 * Interface of the component that contains all {@link ViewPaneFrameComponent}.
 *
 * @author AXS (16.05.2020)
 */
public interface ViewPaneFrameComponentParent {

    /**
     * @param doc
     * @return a list of all {@link ViewPaneFrameComponent} for the given
     *         {@link GraphDocument}
     */
    public List<ViewPaneFrameComponent> getViewPaneFrameComponents(GraphDocument doc);

    /**
     * @return a list of all {@link ViewPaneFrameComponent}
     */
    public List<ViewPaneFrameComponent> getAllViewPaneFrameComponents();

    /**
     * Removes all {@link ViewPaneFrameComponent} with the given
     * {@link GraphDocument}
     *
     * @param doc
     */
    public void removeViewPaneFrameComponents(GraphDocument doc);

    /**
     * Activates or creates and activates the the graph view for a given
     * {@link GraphDocument}.
     *
     * @param doc
     * @return the activated graph view for the {@link GraphDocument}
     */
    public GraphViewPane getGraphViewPane(GraphDocument doc);

    /**
     * @param viewPaneFrameComponent
     * @return <code>true</code> if this component is the selected or active
     *         component
     */
    public boolean isSelected(ViewPaneFrameComponent viewPaneFrameComponent);

    /**
     * Sets component with the given {@link ViewPaneFrameComponent} as the
     * selected or active component.
     *
     * @param viewPaneFrameComponent
     */
    public void setSelected(ViewPaneFrameComponent viewPaneFrameComponent);

    /**
     * @param doc
     * @return
     */
    public GraphViewPaneFrameComponent createGraphView(final GraphDocument doc);

    /**
     * Create new MatrixViewFrame and add it to parent GraphDocument
     *
     * @param doc Sub-Model as source for the MatrixView
     * @param titleIndex Index of the matrix view for this doc to display in the
     *            title
     * @param viewPaneToolbarManager
     * @return the created matrix component
     */
    public MatrixViewPaneFrameComponent createMatrixView(final GraphDocument doc, int titleIndex, ViewPaneToolbarManager viewPaneToolbarManager);

    /**
     * @return
     */
    public default boolean hasViewPaneFrameComponents() {
        List<ViewPaneFrameComponent> children = getAllViewPaneFrameComponents();
        return !children.isEmpty();
    }

    /**
     * @return the parent {@link Container} if this is a {@link Component}
     */
    public default Container getParent() {
        Container parent = null;
        if (this instanceof Component) {
            Component component = (Component) this;
            parent = component.getParent();
        }
        return parent;
    }

}
