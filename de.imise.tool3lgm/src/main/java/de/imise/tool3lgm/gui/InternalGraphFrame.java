package de.imise.tool3lgm.gui;

import java.awt.Point;
import java.beans.PropertyVetoException;

import javax.swing.JViewport;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.ViewParameter;

/**
 * Erzeugt InternalFrame für 3lgm mit bestimmter Größe und und Lage. Es werden freie Stellen gesucht und eingfügt.
 */
public class InternalGraphFrame extends AbstractInternalFrame implements GraphViewContainer {

    /**
     * COMMENTME
     */
    private final InputGraphArea area;

    /**
     * @param doc
     */
    public InternalGraphFrame(final GraphDocument doc) {
        super(doc);
        setClosable(true);
        area = new InputGraphArea(doc);
        scrollPane.setViewportView(area);
        updateTitle();
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

    /**
     * Setzt den Titel des Frames
     */
    @Override
    public void updateTitle() {
        String fullName = getFullName();
        setTitle(fullName);
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

    /**
     * @return
     */
    public GraphDocument getSzenario() {
        return getGraphDocument();
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (doc == null ? 0 : doc.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        InternalGraphFrame other = (InternalGraphFrame) obj;
        if (doc == null) {
            if (other.doc != null) {
                return false;
            }
        } else if (!doc.equals(other.doc)) {
            return false;
        }
        return true;
    }

    @Override
    public ViewParameter getViewParameter() {
        ViewParameter viewParameter = area.getViewParameter();
        JViewport viewport = getViewport();
        Point viewPosition = viewport.getViewPosition();
        viewParameter.viewPositionX = viewPosition.x;
        viewParameter.viewPositionY = viewPosition.y;
        return viewParameter;
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
