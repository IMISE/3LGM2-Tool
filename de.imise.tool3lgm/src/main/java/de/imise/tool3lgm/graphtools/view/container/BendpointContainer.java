/*
 * Created on 01.11.2004
 */
package de.imise.tool3lgm.graphtools.view.container;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;

/**
 * @author imi0wendt
 */
public class BendpointContainer extends NodeContainer {

    /**
     *
     */
    public BendpointContainer() {
        super();
    }

    public BendpointContainer(final Bendpoint kp, final GraphDocument gd) {
        super(kp, new GraphElementLayout(), gd);
        layout.bg_color = new Color(0, 0, 0, 0);
        layout.width = 10;
        layout.height = 10;
    }

    /**
     * @return
     */
    public Bendpoint getKnickpunktKnoten() {
        return (Bendpoint) me;
    }

    @Override
    public void paintComponent(final Graphics g) {
        if (isSelected()) {
            int x = getX();
            int y = getY();
            int width = getWidth();
            int width_half = width / 2;
            int height = getHeight();
            int height_half = height / 2;
            int xm = x - width_half;
            int ym = y - height_half;

            g.setColor(Color.black);
            g.drawRect(xm, ym, width, height);
            //g.fillRect(xm, ym, width, height);
        }
    }

    @Override
    public void refreshText() {
        //mache nichts
    }

    /**
     * Liefert den Punkt, an dem sich der Knickpunkt vor diesem befindet. Ist es der erste Knickpunkt dieser Edge, dann kommt der Startpunkt der
     * Edge zurück.
     *
     * @param bc
     * @return
     */
    public Point getPosition() {
        return new Point(getX(), getY());
    }

    /**
     * Liefert den Punkt, an dem sich der Knickpunkt vor diesem befindet. Ist es der erste Knickpunkt dieser Edge, dann kommt der Startpunkt der
     * Edge zurück.
     *
     * @param bc
     * @return
     */
    public Point getPredecessorPosition() {
        EdgeContainer edgeC = getKnickpunktKnoten().getOwner();
        int pos = edgeC.indexOfBendpointContainer(this);
        if (pos == -1) {
            return null;
        }
        if (pos == 0) {
            return new Point(edgeC.startx, edgeC.starty);
        }
        BendpointContainer bc = edgeC.getBendpointContainer(pos - 1);
        return new Point(bc.getX(), bc.getY());
    }

    /**
     * Liefert den Punkt, an dem sich der Knickpunkt nach diesem befindet. Ist es der letzte Knickpunkt dieser Edge, dann kommt der Endpunkt der
     * Edge zurück.
     *
     * @param bc
     * @return
     */
    public Point getSuccessorPosition() {
        EdgeContainer edgeC = getKnickpunktKnoten().getOwner();
        int pos = edgeC.indexOfBendpointContainer(this);
        if (pos == -1) {
            return null;
        }
        if (pos == edgeC.getBendpointContainerCount() - 1) {
            return new Point(edgeC.endx, edgeC.endy);
        }
        BendpointContainer bc = edgeC.getBendpointContainer(pos + 1);
        return new Point(bc.getX(), bc.getY());
    }

}
