package de.imise.tool3lgm.gui.internalframe;

import java.beans.PropertyVetoException;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewParameter;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.gui.GraphViewPane;
import de.imise.tool3lgm.gui.GraphViewPaneFrameComponent;

/**
 * Erzeugt InternalFrame für 3lgm mit bestimmter Größe und und Lage. Es werden freie Stellen gesucht und eingfügt.
 */
public final class GraphViewInternalFrame extends ToolInternalFrame implements GraphViewPaneFrameComponent {

    private final InputGraphArea area;

    /**
     * @param doc
     */
    public GraphViewInternalFrame(final GraphDocument doc) {
        super(new GraphViewPane(doc));
        GraphViewPane graphViewPane = (GraphViewPane) viewPane;
        area = graphViewPane.getInputGraphArea();
        setClosable(true);
        updateTitle();
    }

    @Override
    public GraphViewPane getViewPane() {
        return (GraphViewPane) viewPane;
    }

    /**
     * @return
     */
    @Override
    public InputGraphArea getInputGraphArea() {
        return area;
    }

    @Override
    public void dispose() {
        super.dispose();
        area.dispose();
    }

    @Override
    public void dataChanged(final GraphDocument source) {
        area.revalidateRepaint();
    }

    @Override
    public void elementGraphicsChanged(final ElementContainer source) {
        area.revalidateRepaint();
    }

    @Override
    public void layoutChanged(final GraphDocument source) {
        area.layoutChanged();
    }

    @Override
    public void groupOrderChanged(final GraphDocument source) {
        area.revalidateRepaint();
    }

    @Override
    public void activeLayerChanged(final GraphDocument source) {
        area.revalidateRepaint();
    }

    @Override
    public void colorsChanged(final GraphDocument source) {
        area.revalidateRepaint();
    }

    @Override
    public void selectionChanged(final GraphDocument source) {
        area.revalidateRepaint();
    }

    @Override
    public void userFieldValueChanged(final UserFieldTarget userFieldTarget) {
        area.revalidateRepaint();
    }

    @Override
    public void elementNameChanged(final ElementContainer ec) {
        refreshElementContainer(ec);
    }

    /**
     * @param ec
     */
    private void refreshElementContainer(final ElementContainer ec) {
        GraphDocument ecDoc = ec.getGraphDocument();
        GraphDocument doc = getGraphDocument();
        ElementContainer thisEc = ecDoc == doc ? ec : ec.getElement().getContainer(doc);
        ec.refreshText();
        elementGraphicsChanged(thisEc);
        revalidate();
        repaint();
    }

    public GraphViewParameter getGraphViewParameter() {
        return area.getGraphViewParameter();
    }

    @Override
    public void setSelected(final boolean selected) {
        try {
            super.setSelected(selected);
        } catch (PropertyVetoException e) {
            //ignore the possible error
        }
    }

}
