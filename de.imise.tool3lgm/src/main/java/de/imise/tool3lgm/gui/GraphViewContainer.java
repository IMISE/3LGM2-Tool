package de.imise.tool3lgm.gui;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.ViewParameter;

/**
 * Interface for a graph view of a Szenario.
 *
 * @author AXS (10.05.2020)
 */
public interface GraphViewContainer extends GraphDocumentOwner {

    /**
     * @return the {@link GraphDocument} this GraphViewContainer is displaying
     */
    @Override
    public default GraphDocument getGraphDocument() {
        InputGraphArea inputGraphArea = getInputGraphArea();
        return inputGraphArea.getGraphDocument();
    }

    /**
     * @return the {@link ViewParameter} of this graph view
     */
    public ViewParameter getViewParameter();

    /**
     * @return <code>true</code> if this component is the selected or focused component
     */
    public boolean isSelected();

    /**
     * @param selected
     */
    public void setSelected(boolean selected);

    /**
     * @return the InputGraphArea this GraphViewContainer is displaying
     */
    public InputGraphArea getInputGraphArea();

    /**
     * @return the name of this view. Default is the name of the {@link GraphDocument}
     */
    public default String getName() {
        GraphDocument doc = getGraphDocument();
        String name = doc.toString();
        return name;
    }

    /**
     * @return the name of this view. Default is "Name of GDCollection" + " - " +
     *         "Name of GraphDocument" + " (" + "Name of MetaModel" + ")";
     */
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