package de.imise.tool3lgm.gui.viewpane.graph;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewParameter;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.gui.viewpane.ViewPane;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponent;
import de.imise.util.swing.component.ParentComponentFinder;

/**
 * @author AXS (19.05.2020)
 */
public final class GraphViewPane extends ViewPane {

    /**
     *
     */
    private final InputGraphArea area;

    /**
     *
     */
    public GraphViewPane(final GraphDocument doc) {
        super(doc);
        area = new InputGraphArea(doc);
        scrollPane.setViewportView(area);
    }

    /**
     * @return
     */
    public GraphViewParameter getGraphViewParameter() {
        return area.getGraphViewParameter();
    }

    /**
     * @return
     */
    public InputGraphArea getInputGraphArea() {
        return area;
    }

    /**
     * @return the name of this view. Default is "Name of GDCollection" + " - " +
     *         "Name of GraphDocument" + " (" + "Name of MetaModel" + ")";
     */
    @Override
    public String getFullName() {
        GDCollection gdcoll = getCollection();
        String gdcollName = gdcoll.getName();
        String docName = getGraphDocument().getTitle();
        MetaModelContext metaModelContext = gdcoll.getMetaModelContext();
        String metaModelDisplayName = metaModelContext.getMetaModelDisplayName();
        String fullName = gdcollName + " - " + docName + "   (" + metaModelDisplayName + ")";
        return fullName;
    }

    /**
     * @return the component that contains this. The parent component of this
     *         should be a {@link ViewPaneFrameComponent}.
     */
    public ViewPaneFrameComponent getFrameComponent() {
        return ParentComponentFinder.getParent(this, ViewPaneFrameComponent.class);
    }

    //    /**
    //     * @return if this component is the soelected or active component in the
    //     *         corresponding {@link ViewPaneFrameComponentParent}
    //     */
    //    public boolean _isSelected() {
    //        ViewPaneFrameComponent graphViewFrameComponent = getFrameComponent();
    //        return graphViewFrameComponent != null && graphViewFrameComponent.isSelected();
    //    }
    //
    //    /**
    //     * Sets this component selected or active in its {@link ViewPaneFrameComponentParent}
    //     */
    //    public void _setSelected() {
    //        ViewPaneFrameComponent graphViewFrameComponent = getFrameComponent();
    //        if (graphViewFrameComponent != null) {
    //            graphViewFrameComponent.setSelected();
    //        }
    //    }
    //
    /**
     * @return the name of this view. Default is the name of the {@link GraphDocument}
     */
    @Override
    public String getName() {
        GraphDocument doc = getGraphDocument();
        String name = doc.toString();
        return name;
    }

}
