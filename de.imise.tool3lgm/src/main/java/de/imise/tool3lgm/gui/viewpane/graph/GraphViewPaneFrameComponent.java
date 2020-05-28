package de.imise.tool3lgm.gui.viewpane.graph;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponent;

/**
 * @author AXS (26.05.2020)
 */
public interface GraphViewPaneFrameComponent extends ViewPaneFrameComponent, LGMChangeListenerSimple {

    /**
     * @return
     */
    public InputGraphArea getInputGraphArea();

    @Override
    public default void dataChanged(final GraphDocument source) {
        InputGraphArea area = getInputGraphArea();
        area.revalidateRepaint();
    }

    @Override
    public default void elementGraphicsChanged(final ElementContainer source) {
        InputGraphArea area = getInputGraphArea();
        area.revalidateRepaint();
    }

    @Override
    public default void layoutChanged(final GraphDocument source) {
        InputGraphArea area = getInputGraphArea();
        area.layoutChanged();
    }

    @Override
    public default void groupOrderChanged(final GraphDocument source) {
        InputGraphArea area = getInputGraphArea();
        area.revalidateRepaint();
    }

    @Override
    public default void activeLayerChanged(final GraphDocument source) {
        InputGraphArea area = getInputGraphArea();
        area.revalidateRepaint();
    }

    @Override
    public default void colorsChanged(final GraphDocument source) {
        InputGraphArea area = getInputGraphArea();
        area.revalidateRepaint();
    }

    @Override
    public default void selectionChanged(final GraphDocument source) {
        InputGraphArea area = getInputGraphArea();
        area.revalidateRepaint();
    }

    @Override
    public default void userFieldValueChanged(final UserFieldTarget userFieldTarget) {
        InputGraphArea area = getInputGraphArea();
        area.revalidateRepaint();
    }

    @Override
    public default void elementNameChanged(final ElementContainer ec) {
        GraphDocument ecDoc = ec.getGraphDocument();
        GraphDocument doc = getGraphDocument();
        ElementContainer thisEc = ecDoc == doc ? ec : ec.getElement().getContainer(doc);
        ec.refreshText();
        elementGraphicsChanged(thisEc);
        revalidate();
        repaint();
    }

    public void revalidate();

    public void repaint();

    @Override
    public GraphViewPane getViewPane();

}
