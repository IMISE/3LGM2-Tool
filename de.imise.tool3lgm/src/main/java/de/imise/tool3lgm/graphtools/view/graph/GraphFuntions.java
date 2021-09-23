package de.imise.tool3lgm.graphtools.view.graph;

import java.awt.Dimension;

import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * @author AXS (23.09.2021)
 */
public class GraphFuntions {

    /**
     * Berechnet die Position untergeordneter Elemente auf einem
     * Oberelementcontainer.
     *
     * @param master Oberelementcontainer auf dem untergeordnete Elemente
     *            positioniert werden sollen
     * @return
     */
    public static final Dimension calculateAddictPosition(final NodeContainer master) {
        int addictedCount = -1;
        ModelElement me = master.getElement();
        for (Edge edge : me.getEdges()) {
            if (edge instanceof CompositionEdge) {
                ModelElement slave = ((CompositionEdge) edge).getSlave();
                if (slave != me && slave.isPaintable()) {
                    addictedCount++;
                }
            }
        }
    
        int x = master.getX();
        int y = master.getY();
        int w = master.getWidth();
        int h = master.getHeight();
    
        Dimension retVal = new Dimension(x, y);
    
        switch (addictedCount % 36) {
        case 14:
            retVal.width = x - w / 8;
            retVal.height = y - h / 2;
            break;
        case 28:
            retVal.width = x - w / 2;
            retVal.height = y - h / 2 + h / 8;
            break;
        case 8:
            retVal.width = x - w / 4;
            retVal.height = y - h / 2;
            break;
        case 20:
            retVal.width = x - w / 2 + w / 8;
            retVal.height = y - h / 2;
            break;
        case 0:
            retVal.width = x - w / 2;
            retVal.height = y - h / 4;
            break;
        case 15:
            retVal.width = x + w / 8;
            retVal.height = y + h / 2;
            break;
        case 29:
            retVal.width = x + w / 2;
            retVal.height = y + h / 2 - h / 8;
            break;
        case 9:
            retVal.width = x + w / 4;
            retVal.height = y + h / 2;
            break;
        case 21:
            retVal.width = x + w / 2 - w / 8;
            retVal.height = y + h / 2;
            break;
        case 1:
            retVal.width = x + w / 2;
            retVal.height = y + h / 4;
            break;
        case 16:
            retVal.width = x - w / 8;
            retVal.height = y + h / 2;
            break;
        case 30:
            retVal.width = x - w / 2;
            retVal.height = y + h / 2 - h / 8;
            break;
        case 10:
            retVal.width = x - w / 4;
            retVal.height = y + h / 2;
            break;
        case 22:
            retVal.width = x - w / 2 + w / 8;
            retVal.height = y + h / 2;
            break;
        case 2:
            retVal.width = x - w / 2;
            retVal.height = y + h / 4;
            break;
        case 17:
            retVal.width = x + w / 8;
            retVal.height = y - h / 2;
            break;
        case 31:
            retVal.width = x + w / 2;
            retVal.height = y - h / 2 + h / 8;
            break;
        case 11:
            retVal.width = x + w / 4;
            retVal.height = y - h / 2;
            break;
        case 23:
            retVal.width = x + w / 2 - w / 8;
            retVal.height = y - h / 2;
            break;
        case 3:
            retVal.width = x + w / 2;
            retVal.height = y - h / 4;
            break;
        case 18:
            retVal.width = x - w / 2;
            retVal.height = y - h / 2;
            break;
        case 32:
            retVal.width = x - w / 16;
            retVal.height = y - h / 2;
            break;
        case 12:
            retVal.width = x - w / 2;
            retVal.height = y + h / 2;
            break;
        case 24:
            retVal.width = x - w / 2;
            retVal.height = y - h / 8;
            break;
        case 4:
            retVal.width = x - w / 2;
            retVal.height = y;
            break;
        case 19:
            retVal.width = x + w / 2;
            retVal.height = y + h / 2;
            break;
        case 33:
            retVal.width = x + w / 16;
            retVal.height = y + h / 2;
            break;
        case 13:
            retVal.width = x + w / 2;
            retVal.height = y - h / 2;
            break;
        case 25:
            retVal.width = x + w / 2;
            retVal.height = y - h / 8;
            break;
        case 5:
            retVal.width = x + w / 2;
            retVal.height = y;
            break;
        case 26:
            retVal.width = x - w / 2;
            retVal.height = y + h / 8;
            break;
        case 6:
            retVal.width = x;
            retVal.height = y + h / 2;
            break;
        case 27:
            retVal.width = x + w / 2;
            retVal.height = y + h / 8;
            break;
        case 7:
            retVal.width = x;
            retVal.height = y - h / 2;
            break;
        case 34:
            retVal.width = x - w / 16;
            retVal.height = y + h / 2;
            break;
        case 35:
            retVal.width = x + w / 16;
            retVal.height = y - h / 2;
            break;
        }
    
        return retVal;
    }

}
