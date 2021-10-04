package de.imise.tool3lgm.graphtools.view.container;

import static de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState.DOUBLE;
import static de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState.FORWARD;
import static de.imise.tool3lgm.graphtools.view.graph.GraphFunctions.getClosestCoordinatesOnBorderOfContainerToOther;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.NodeRenderer;

/**
 * @author N.N
 * @create Long time ago
 */
public class EdgeContainer extends ElementContainer {

    /**  */
    protected Point start = new Point(0, 0);

    /**  */
    protected Point end = new Point(0, 0);

    /**  */
    public Polygon startArrow = new Polygon();

    /**  */
    public Polygon endArrow = new Polygon();

    /**  */
    protected boolean over_lapping = false;

    /**  */
    protected double rad1 = 0, rad2 = 0;

    /**
     * List of all {@link BendpointContainer} on this {@link EdgeContainer}
     */
    private final List<BendpointContainer> bendpoints = new ArrayList<>(1);

    /**
     *
     */
    public EdgeContainer() {
    }

    /**
     * @param edge
     * @param doc
     */
    public EdgeContainer(final Edge edge, final GraphDocument doc) {
        super(edge, doc);
        computeBorderPoints();
    }

    /**
     * @param edge
     * @param layout
     * @param doc
     */
    public EdgeContainer(final Edge edge, final GraphElementLayout layout, final GraphDocument doc) {
        super(edge, layout, doc);
        computeBorderPoints();
    }

    /**
     * @param edgeContainer
     * @param doc
     */
    public EdgeContainer(final EdgeContainer edgeContainer, final GraphDocument doc) {
        super(edgeContainer, doc);
        computeBorderPoints();
    }

    @Override
    public final ElementContainer clone(final boolean cloneModelElement, final GraphDocument _doc) {
        EdgeContainer retVal;
        retVal = (EdgeContainer) super.clone(cloneModelElement, _doc);
        if (retVal != null) {
            retVal.start = new Point(start);
            retVal.end = new Point(end);
            retVal.over_lapping = over_lapping;
            retVal.startArrow = new Polygon(startArrow.xpoints, startArrow.ypoints, 3);
            retVal.endArrow = new Polygon(endArrow.xpoints, endArrow.ypoints, 3);
            retVal.bendpoints.clear();
            if (_doc instanceof Szenario) {
                for (int i = 0; i < bendpoints.size(); i++) {
                    BendpointContainer knC = bendpoints.get(i);
                    BendpointContainer bpC = (BendpointContainer) knC.clone(true, _doc);
                    bpC.getElement().addEdge((Edge) me);
                    retVal.setBendpointContainer(bpC, i);
                }
            }
        }
        return retVal;
    }

    /**
     * @param startNode
     * @param endNode
     * @param doc
     */
    public void setNodes(final Node startNode, final Node endNode, final GraphDocument doc) {
        getEdge().setNodes(startNode, endNode);
    }

    /**
     * @return
     */
    public Edge getEdge() {
        return me instanceof Edge ? (Edge) me : null;
    }

    @Override
    public int getX() {
        return Math.min(start.x, end.x) - 10;
    }

    @Override
    public int getY() {
        return Math.min(start.y, end.y) - 10;
    }

    @Override
    public int getWidth() {
        return Math.abs(end.x - start.x) + 20;
    }

    @Override
    public int getHeight() {
        return Math.abs(end.y - start.y) + 20;
    }

    /**
     * @return
     */
    public ElementContainer getStartElementContainer() {
        return getStartOrEndContainer(true);
    }

    /**
     * @return
     */
    public ElementContainer getEndElementContainer() {
        return getStartOrEndContainer(false);
    }

    /**
     * @param start
     * @return
     */
    private final ElementContainer getStartOrEndContainer(final boolean start) {
        Edge edge = getEdge();
        if (edge != null) {
            ModelElement me = start ? edge.getStart() : edge.getEnd();
            if (me != null) {
                return me.getContainer(doc);
            }
        }
        return null;
    }

    /**
     *
     */
    public void computeBorderPoints() {
        ElementContainer ec1 = getStartElementContainer();
        ElementContainer ec2 = getEndElementContainer();

        if (ec1 == null || ec2 == null) {
            return;
        }

        // TODO:Aus irgend einem Grund sind beim Import von Teilmodellen hier
        // null-Elemente in der Liste knickpunkte
        // daher hat AXS hier mal das Löschen eingefügt. Eigentlich sollte das
        // aber nicht nötig sein, weil das nur sie Symptome abstellt
        for (int i = bendpoints.size() - 1; i >= 0; i--) {
            if (bendpoints.get(i) == null) {
                bendpoints.remove(i);
            }
        }

        boolean hasBendpoints = !bendpoints.isEmpty();

        //if there is no bendpoint then this container is the edge end container
        ElementContainer firstBendpointContainer = hasBendpoints ? bendpoints.get(0) : ec2;
        getClosestCoordinatesOnBorderOfContainerToOther(ec1, firstBendpointContainer, start);

        //if there is no bendpoint then this container is the edge start container
        ElementContainer lastBendpointContainer = hasBendpoints ? bendpoints.get(bendpoints.size() - 1) : ec1;
        getClosestCoordinatesOnBorderOfContainerToOther(ec2, lastBendpointContainer, end);

        rad2 = Math.atan2(end.y - lastBendpointContainer.getY(), end.x - lastBendpointContainer.getX()) + Math.PI / 2;
        rad1 = Math.atan2(firstBendpointContainer.getY() - start.y, firstBendpointContainer.getX() - start.x) + Math.PI / 2;

        over_lapping = NodeRenderer.isInside(ec1, end.x, end.y) || NodeRenderer.isInside(ec2, start.x, start.y);

        createArrow(startArrow, end, true);
        createArrow(endArrow, start, false);

    }

    /**
     * @param point the position the arrow points to
     * @param arrow the polygon to fill with the points that will be rendered to
     *            an arrow
     * @param forward
     */
    private static void createArrow(final Polygon arrow, final Point point, final boolean forward) {
        int minus = forward ? 1 : -1;
        arrow.reset();
        arrow.addPoint(point.x - 4, point.y + 12 * minus);
        arrow.addPoint(point.x, point.y);
        arrow.addPoint(point.x + 4, point.y + 12 * minus);
    }

    /**
     * @return
     */
    public final boolean isOverLapping() {
        return over_lapping;
    }

    /**  */
    int x_shift = 0;

    /**  */
    int y_shift = 0;

    /**
     * @param x
     * @param y
     */
    public void setShift(final int x, final int y) {
        x_shift = x;
        y_shift = y;
    }

    /**
     * @param kc1
     * @param kc2
     */
    private void createSurrogateContainers(final ElementContainer kc1, final ElementContainer kc2) {
        if (paintingSurrogates) {
            return;
        }
        if (kc1 == null || kc2 == null || containerParent == null) {
            return;
        }

        for (ElementContainer startC : kc1.getSurrogateContainer()) {
            for (ElementContainer endC : kc2.getSurrogateContainer()) {
                if (startC == endC) {
                    continue;
                }
                Edge tmpEdge = (Edge) me.clone();
                tmpEdge.setNodes(startC.getElement(), endC.getElement(), false);
                EdgeContainer tmpC = new EdgeContainer(tmpEdge, doc);
                tmpC.setColor(Color.gray);
                ((LayerContainer) containerParent).addTmpEdgeContainer(tmpC);
            }
        }
    }

    /**
     * Toleranz in Pixeln, mit der man neben die Edge klicken darf, wenn man sie
     * per Mausklick auswählen will oder mit der entschieden wird, an welcher
     * Stelle ein neuer Knickpunkt hinzugefügt werden muss.
     */
    public static final int TOLERANCE = 4;

    /**
     * @param x
     * @param y
     * @return
     */
    public boolean isInside(final int x, final int y) {
        if (!isVisible()) {
            return false;
        }
        return getBendpointInsertIndex(x, y) >= 0;
    }

    /**
     * Liefert den Index, an dem in die Liste <code>knickpunkte</code> ein neuer
     * Knickpunkt eingefügt werden würde, wenn der die übergebenen Koordinaten
     * besitzt.
     *
     * @param x
     * @param y
     * @return
     */
    public int getBendpointInsertIndex(final int x, final int y) {
        return getBendpointInsertIndex(x, y, TOLERANCE);
    }

    /**
     * Liefert den Index, an dem in die Liste <code>knickpunkte</code> ein neuer
     * Knickpunkt eingefügt werden würde, wenn der die übergebenen Koordinaten
     * besitzt.
     *
     * @param x
     * @param y
     * @param tolerance
     * @return
     */
    private int getBendpointInsertIndex(final int x, final int y, final int tolerance) {
        int index = -1;
        int startx = start.x;
        int starty = start.y;
        int bendpointCount = bendpoints.size();
        for (int i = 0; i <= bendpointCount; i++) {
            int endx = 0, endy = 0;
            if (i == bendpointCount) {
                endx = end.x;
                endy = end.y;
            } else {
                endx = bendpoints.get(i).getX();
                endy = bendpoints.get(i).getY();
            }
            if ((int) Line2D.ptSegDist(startx, starty, endx, endy, x, y) <= tolerance) {
                index = i;
                break;
            }
            startx = endx;
            starty = endy;
        }
        return index;
    }

    /**
     * Setzt einen Knickpunkt an der richtigen Position in die Liste der
     * Knickpunkte ein. Wird als Index -1 übergeben, dann wird der Index anhand
     * der Koordinaten berechnet.
     *
     * @param bc
     * @param index
     */
    public void setBendpointContainer(final BendpointContainer bc, int index) {
        if (index == -1) {
            index = 0;
        }
        while (index >= bendpoints.size()) {
            bendpoints.add(null);
        }
        // System.err.println("AXS_AXSsetKnickpunkt " + getGraphDocument());
        bendpoints.set(index, bc);
        Bendpoint bendpoint = bc.getBendpoint();
        bendpoint.setOwner(this);
    }

    /**
     * Fügt einen Knickpunkt an der richtigen Position in die Liste der
     * Knickpunkte ein. Wird als Index -1 übergeben, dann wird der Index anhand
     * der Koordinaten berechnet.
     *
     * @param bc
     * @param index
     */
    public void addBendpoint(final BendpointContainer bc, int index) {
        if (index < 0) {
            index = getBendpointInsertIndex(bc.layout.x, bc.layout.y);
        }
        if (index < 0) {
            index = 0;
        }
        bendpoints.add(index, bc);
        Bendpoint bendpoint = bc.getBendpoint();
        bendpoint.setOwner(this);
    }

    /**
     * @param kp
     */
    public void removeBendpoint(final Bendpoint kp) {
        bendpoints.remove(kp.getContainer(doc));
    }

    /**
     * @return
     */
    public Iterable<BendpointContainer> iterateBendpointContainers() {
        return bendpoints;
    }

    /**
     * @param bendpointContainer
     * @return
     */
    public int indexOfBendpointContainer(final BendpointContainer bendpointContainer) {
        return bendpoints.indexOf(bendpointContainer);
    }

    /**
     * @return
     */
    public int getBendpointContainerCount() {
        return bendpoints.size();
    }

    /**
     * @param index
     * @return
     */
    public BendpointContainer getBendpointContainer(final int index) {
        return bendpoints.get(index);
    }

    /**
     * @param bendpoint
     * @return
     */
    public int getIndexOfBendpoint(final Bendpoint bendpoint) {
        for (int i = 0; i < bendpoints.size(); i++) {
            BendpointContainer bc = bendpoints.get(i);
            if (bc != null) {
                Bendpoint bendpointAtIndex = bc.getBendpoint();
                if (bendpoint == bendpointAtIndex) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean isVisible() {
        return isVisible(false);
    }

    /**
     * @param createSurrogates
     * @return
     */
    public boolean isVisible(final boolean createSurrogates) {
        return isVisible(createSurrogates, false);
    }

    /**
     * @param createSurrogates
     * @return
     */
    public boolean isVisible(final boolean createSurrogates, final boolean ignoreOverlapping) {
        Edge edge = getEdge();
        ModelElement me1 = edge.getStart();
        if (me1 == null) {
            return false;
        }
        ModelElement me2 = edge.getEnd();
        if (me2 == null) {
            return false;
        }
        ElementContainer kc1 = me1.getContainer(doc);
        if (kc1 == null) {
            return false;
        }
        ElementContainer kc2 = me2.getContainer(doc);
        if (kc2 == null) {
            return false;
        }
        ModelElement k1 = kc1.getElement();
        if (!k1.isPaintable()) {
            return false;
        }
        ModelElement k2 = kc2.getElement();
        if (!k2.isPaintable()) {
            return false;
        }
        if (k1.layerFor() != k2.layerFor()) {
            return false;
        }
        if (!kc1.isVisible() || !kc2.isVisible()) {
            Class<? extends ModelElement> startClass = me1.getClass();
            Class<? extends ModelElement> endClass = me2.getClass();
            MetaModel metaModel = doc.getMetaModel();
            //bei Bausteinschnittsellen sollen keine surrogates gemalt werden. Vorher stand hier bei der 2. Bedingung: !(me instanceof KommBeziehung)
            if (createSurrogates && !(metaModel.isSlaveType(startClass) && metaModel.isSlaveType(endClass))) {
                createSurrogateContainers(kc1, kc2);
            }
            return false;
        }
        if (!ignoreOverlapping && isOverLapping()) {
            return false;
        }
        return true;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        if (!isVisible(true)) {
            return;
        }

        Graphics2D gc = (Graphics2D) g;

        Color elem_col = getColor();
        if (elem_col == null) {
            elem_col = Color.black;
        }
        g.setColor(elem_col);

        Stroke s = gc.getStroke();

        boolean isResult = doc.isAnalysisResult(this);
        boolean isHighLight = isHighLight();

        int startx = 0, starty = 0, endx = 0, endy = 0;
        startx = start.x;
        starty = start.y;

        int bendpointCount = bendpoints.size();
        for (int i = 0; i <= bendpointCount; i++) {
            if (i == bendpointCount) {
                endx = end.x;
                endy = end.y;
            } else {
                endx = bendpoints.get(i).getX();
                endy = bendpoints.get(i).getY();
            }

            boolean fatFrame = false;
            if (isResult || isHighLight) {
                fatFrame = true;
                gc.setStroke(GraphElementLayout.FAT_STROKE);
            }

            if (!paintingSurrogates && isDashed()) {
                Stroke str = gc.getStroke();
                gc.setStroke(GraphElementLayout.HAS_PART_EDGES_STROKE);
                g.drawLine(startx, starty, endx, endy);
                gc.setStroke(str);
            } else {
                g.drawLine(startx, starty, endx, endy);
            }

            if (i == 0) {
                Edge edge = getEdge();
                boolean backward; // außer bei DoubleMeaningEdges und HasPartEdges wird der Rückwärts-Pfeil auch bei unberichteten Kanten gezeichnet
                if (edge instanceof DoubleMeaningEdge) {
                    DoubleMeaningEdge doubleMeaningEdge = (DoubleMeaningEdge) edge;
                    ConnectionState connectionState = doubleMeaningEdge.getConnectionState();
                    backward = connectionState == BACKWARD || connectionState == DOUBLE;
                } else {
                    backward = edge instanceof HasPartEdge || !doc.getMetaModel().isDirectedEdge(edge.getClass());
                }
                if (backward) {
                    gc.rotate(rad1, startx, starty);
                    // try {
                    g.drawPolygon(endArrow);
                    g.fillPolygon(endArrow);

                    // System.err.println("1.) " + i + " von " + numKKnots); System.err.println(p2 + " " + CollectionUtils.toString(p2.xpoints) + " " + CollectionUtils.toString(p2.ypoints));
                    // System.err.println(ModelConstants.getFullBackwardMetaAssociationName(dlk.getClass()) + ": start=" + dlk.getStart() + " -> end=" + dlk.getEnd()); } catch (Exception e) {
                    // System.err.println("1.) " + i + " von " + numKKnots + " ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"); System.err.println(p2 + " " +
                    // CollectionUtils.toString(p2.xpoints) + " " + CollectionUtils.toString(p2.ypoints)); System.err.println(getID() + " " +
                    // ModelConstants.getFullBackwardMetaAssociationName(dlk.getClass()) + ": start=" + dlk.getStart() + " -> end=" + dlk.getEnd()); }
                    gc.rotate(-rad1, startx, starty);
                }
            }
            if (i == bendpointCount) {
                boolean forward; //alle Kanten sollen Vorwärts gemalt werden außer DoubleMeaning Edges und HasPart
                Edge edge = getEdge();
                if (edge instanceof DoubleMeaningEdge) {
                    DoubleMeaningEdge doubleMeaningEdge = (DoubleMeaningEdge) edge;
                    ConnectionState connectionState = doubleMeaningEdge.getConnectionState();
                    forward = connectionState == FORWARD || connectionState == DOUBLE;
                } else {
                    forward = !(edge instanceof HasPartEdge);
                }

                if (forward) {
                    gc.rotate(rad2, endx, endy);
                    // try {
                    g.drawPolygon(startArrow);
                    g.fillPolygon(startArrow);

                    // System.err.println("2.) " + i + " von " + numKKnots); System.err.println(p1 + " " + CollectionUtils.toString(p1.xpoints) + " " + CollectionUtils.toString(p1.ypoints));
                    // System.err.println(ModelConstants.getFullBackwardMetaAssociationName(dlk.getClass()) + ": start=" + dlk.getStart() + " -> end=" + dlk.getEnd()); } catch (Exception e) {
                    // System.err.println("2.) " + i + " von " + numKKnots + " #######################################################################################"); System.err.println(p1 + " " +
                    // CollectionUtils.toString(p1.xpoints) + " " + CollectionUtils.toString(p1.ypoints)); System.err.println(getID() + " " +
                    // ModelConstants.getFullBackwardMetaAssociationName(dlk.getClass()) + ": start=" + dlk.getStart() + " -> end=" + dlk.getEnd()); }
                    gc.rotate(-rad2, endx, endy);
                }
            }

            if (fatFrame) {
                gc.setStroke(s);
            }

            if (isSelected()) {
                g.setColor(Color.black);

                g.drawRect(startx - 5, starty - 5, 10, 10);
                g.fillRect(startx - 5, starty - 5, 10, 10);

                g.drawRect(endx - 5, endy - 5, 10, 10);
                g.fillRect(endx - 5, endy - 5, 10, 10);
            }

            // northLabel wird bei Kanten ins Zentrum verschoben
            if (northLabel != null) {
                // System.out.println(northLabel.getText() + "\n startx=" +
                // startx + " endx=" + endx + " starty=" + starty + "
                // endy="+endy + "
                // northLabel.height="+northLabel.getPreferredSize().height + "
                // northLabel.width="+northLabel.getPreferredSize().width);
                int dx = (startx + endx) / 2 - northLabel.getPreferredSize().width;
                int dy = (starty + endy) / 2 - northLabel.getPreferredSize().height;
                g.translate(dx, dy);
                northLabel.paint(g);
                g.translate(-dx, -dy);
            }
            if (southLabel != null) {
                int dx = (startx + endx) / 2;// +
                // southLabel.getPreferredSize().width;
                int dy = (starty + endy) / 2 - southLabel.getPreferredSize().height;
                g.translate(dx, dy);
                southLabel.paint(g);
                g.translate(-dx, -dy);
            }

            // Die 2 hier sind nicht getestet und sehr wahrscheinlich völlig Quatsch (die Koordinaten) if (eastLabel != null) { int dx = startx; g.translate(dx, ym); eastLabel.paint(g);
            // g.translate(-dx, -ym); } if (westLabel != null) { int dx = xm - westLabel.getPreferredSize().width - 1; g.translate(dx, ym); westLabel.paint(g); g.translate(-dx, -ym); }

            startx = endx;
            starty = endy;
        }
    }

    /**
     * @return
     */
    public boolean isDashed() {
        return me instanceof HasPartEdge;
    }

    @Override
    public final void refreshText() {
        // hier passiert nichts
    }

}
