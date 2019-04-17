package de.imise.tool3lgm.graphtools.view.container;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState.DOUBLE;
import static de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState.FORWARD;
import static de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.FAT_STROKE;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.SelectableObject;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.NodeRenderer;
import de.imise.tool3lgm.log.Log;

/**
 * @author N.N
 * @create Long time ago
 */
public class EdgeContainer extends ElementContainer {

    /**
     *
     */
    final static float dash1[] = {
            10.0f
    };

    /**
     *
     */
    final static BasicStroke dashedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

    /**
     *
     */
    protected int startx = 0, starty = 0, endx = 0, endy = 0;

    /**
     *
     */
    public Polygon p1 = new Polygon(), p2 = new Polygon();

    /**
     *
     */
    protected boolean over_lapping = false;

    /**
     *
     */
    protected double rad1 = 0, rad2 = 0;

    /**
     * Liste aller Knickpunkte der Kante. Sie werden in dieser Reihenfolge zwischen das Start- und Endelement an diese Punktposition gezeichnet.
     */
    private List<Bendpoint> bendpoints;

    /**
     * Indiezes der im Moment selektierten Knickpunkte.
     */
    private List<Integer> selectedBendpoints;

    /**
     * Ein Punkt, der nur gleich ist, wenn er identisch ist!
     *
     * @author AXS (15 Apr 2019)
     */
    public class Bendpoint implements SelectableObject {

        public final EdgeContainer edgeContainer;

        public int x, y;

        public Bendpoint(final EdgeContainer edgeContainer, final int x, final int y) {
            this.edgeContainer = edgeContainer;
            this.x = x;
            this.y = y;
        }

    }

    /**
     *
     */
    public EdgeContainer() {
        super();
    }

    /**
     * @param neu
     * @param gd
     */
    public EdgeContainer(final Edge neu, final GraphDocument gd) {
        super(neu, gd);
        computeBorderPoints();
    }

    /**
     * @param neu
     * @param l
     * @param gd
     */
    public EdgeContainer(final Edge neu, final GraphElementLayout l, final GraphDocument gd) {
        super(neu, l, gd);
        computeBorderPoints();
    }

    /**
     * @param alt
     * @param gd
     */
    public EdgeContainer(final EdgeContainer alt, final GraphDocument gd) {
        super(alt, gd);
        computeBorderPoints();
    }

    @Override
    public final ElementContainer clone(final boolean cloneModelElement, final GraphDocument _doc) {
        EdgeContainer retVal;
        retVal = (EdgeContainer) super.clone(cloneModelElement, _doc);
        if (retVal != null) {
            retVal.startx = startx;
            retVal.starty = starty;
            retVal.endx = endx;
            retVal.endy = endy;
            retVal.over_lapping = over_lapping;
            retVal.p1 = new Polygon(p1.xpoints, p1.ypoints, 3);
            retVal.p2 = new Polygon(p2.xpoints, p2.ypoints, 3);
            if (bendpoints != null) {
                retVal.bendpoints.clear();
                if (_doc instanceof Szenario) {
                    for (int i = 0; i < bendpoints.size(); i++) {
                        Bendpoint p = bendpoints.get(i);
                        Bendpoint pNew = new Bendpoint(retVal, p.x, p.y);
                        retVal.setKnickpunkt(pNew, i);
                    }
                }
            }
        }
        return retVal;
    }

    /**
     * @param _k1
     * @param _k2
     * @param gd
     */
    public void setKnots(final Node _k1, final Node _k2, final GraphDocument gd) {
        getEdge().setKnots(_k1, _k2);
    }

    /**
     * @return
     */
    public Edge getEdge() {
        return me instanceof Edge ? (Edge) me : null;
    }

    @Override
    public int getX() {
        return Math.min(startx, endx) - 10;
    }

    @Override
    public int getY() {
        return Math.min(starty, endy) - 10;
    }

    @Override
    public int getWidth() {
        return Math.abs(endx - startx) + 20;
    }

    @Override
    public int getHeight() {
        return Math.abs(endy - starty) + 20;
    }

    // Komplett aus Edge
    /**
     * @return
     */
    public final int getStartX() {
        return startx;
    }

    /**
     * @return
     */
    public final int getStartY() {
        return starty;
    }

    /**
     * @return
     */
    public final int getEndX() {
        return endx;
    }

    /**
     * @return
     */
    public final int getEndY() {
        return endy;
    }

    public final boolean hasBendpoints() {
        return bendpoints != null && !bendpoints.isEmpty();
    }

    // Komplett aus Edge
    /**
     *
     */
    public void computeBorderPoints() {
        // String h = getHashString();
        // if (h.equals("KAN_1061988438094_1409") || h.equals("KAN_1061988438094_1480") || h.equals("KAN_1061988438094_1426"))
        // System.err.println("jetzte");

        //		 if (ModelConstants.layerFor(getEdge().getClass()) == 4) System.err.println("EdgeContainer -> computeBorderPoints(): " + getGraphDocument()); for (ElementContainer c :
        //		 getEdge().getContainerTable().values()) { EdgeContainer ec = (EdgeContainer) c; System.err.println(ec.getGraphDocument() + " " + ec.getHashString() + " " + ec.knickpunkte + " " +
        //		 ec.getEdge() + " " + ModelConstants.getFullBackwardMetaAssociationName(ec.getEdge().getClass()) + " " + ec.hashCode()); }

        ElementContainer kc1 = null;
        ElementContainer kc2 = null;
        try {
            Edge k;
            if ((k = getEdge()) != null) {
                ModelElement startElement = k.getStart();
                if (startElement != null) {
                    kc1 = startElement.getContainer(doc);
                }
                ModelElement endElement = k.getEnd();
                if (endElement != null) {
                    kc2 = endElement.getContainer(doc);
                }
            }
        } catch (NullPointerException e) {
            Log.show(Log.ERROR, getResString("FehlerAllgemein"), e);
        }
        if (kc1 == null || kc2 == null) {
            return;
        }

        if (hasBendpoints()) {
            // TODO:Aus irgend einem Grund sind beim Import von Teilmodellen hier
            // null-Elemente in der Liste knickpunkte
            // daher hat AXS hier mal das Löschen eingefügt. Eigentlich sollte das
            // aber nicht nötig sein, weil das nur sie Symptome abstellt
            for (int i = bendpoints.size() - 1; i >= 0; i--) {
                if (bendpoints.get(i) == null) {
                    bendpoints.remove(i);
                }
            }

            int i = bendpoints.size() - 1;
            int left_x = kc1.getX();
            int left_y = kc1.getY();
            Bendpoint bendpoint = bendpoints.get(0);
            int right_x = bendpoint.x;
            int right_y = bendpoint.y;

            int middle_x = right_x, middle_y = right_y;

            while (Math.abs(left_x - right_x) > 1 || Math.abs(left_y - right_y) > 1) {
                middle_x = (left_x + right_x) / 2;
                middle_y = (left_y + right_y) / 2;
                if (NodeRenderer.isInside(kc1, middle_x, middle_y)) {
                    left_x = middle_x;
                    left_y = middle_y;
                } else {
                    right_x = middle_x;
                    right_y = middle_y;
                }
            }
            startx = middle_x;
            starty = middle_y;

            left_x = kc2.getX();
            left_y = kc2.getY();
            right_x = bendpoints.get(i).x;
            right_y = bendpoints.get(i).y;

            middle_x = left_x;
            middle_y = left_y;

            while (Math.abs(left_x - right_x) > 1 || Math.abs(left_y - right_y) > 1) {
                middle_x = (left_x + right_x) / 2;
                middle_y = (left_y + right_y) / 2;
                if (NodeRenderer.isInside(kc2, middle_x, middle_y)) {
                    left_x = middle_x;
                    left_y = middle_y;
                } else {
                    right_x = middle_x;
                    right_y = middle_y;
                }
            }
            endx = middle_x;
            endy = middle_y;
        } else {
            int left_x = kc1.getX();
            int left_y = kc1.getY();
            int right_x = kc2.getX();
            int right_y = kc2.getY();

            int middle_x = left_x, middle_y = left_y;

            while (Math.abs(left_x - right_x) > 1 || Math.abs(left_y - right_y) > 1) {
                middle_x = (left_x + right_x) / 2;
                middle_y = (left_y + right_y) / 2;
                if (NodeRenderer.isInside(kc1, middle_x, middle_y)) {
                    left_x = middle_x;
                    left_y = middle_y;
                } else {
                    right_x = middle_x;
                    right_y = middle_y;
                }
            }
            startx = middle_x;
            starty = middle_y;

            left_x = kc2.getX();
            left_y = kc2.getY();
            right_x = kc1.getX();
            right_y = kc1.getY();

            while (Math.abs(left_x - right_x) > 1 || Math.abs(left_y - right_y) > 1) {
                middle_x = (left_x + right_x) / 2;
                middle_y = (left_y + right_y) / 2;
                if (NodeRenderer.isInside(kc2, middle_x, middle_y)) {
                    left_x = middle_x;
                    left_y = middle_y;
                } else {
                    right_x = middle_x;
                    right_y = middle_y;
                }
            }
            endx = middle_x;
            endy = middle_y;
        }

        int lstartx = 0;
        int lstarty = 0;
        int lendx = 0;
        int lendy = 0;
        if (hasBendpoints()) {
            int i = bendpoints.size() - 1;
            lstartx = bendpoints.get(i).x;
            lstarty = bendpoints.get(i).y;
            rad2 = Math.atan2(endy - lstarty, endx - lstartx) + Math.PI / 2;
            lendx = bendpoints.get(0).x;
            lendy = bendpoints.get(0).y;
            rad1 = Math.atan2(lendy - starty, lendx - startx) + Math.PI / 2;
        } else {
            rad2 = Math.atan2(endy - starty, endx - startx) + Math.PI / 2;
            rad1 = rad2;
        }

        if (NodeRenderer.isInside(kc1, endx, endy) || NodeRenderer.isInside(kc2, startx, starty)) {
            over_lapping = true;
        } else {
            over_lapping = false;
        }

        p1.reset();
        p2.reset();
        p1.addPoint(endx - 4, endy + 12);
        p1.addPoint(endx, endy);
        p1.addPoint(endx + 4, endy + 12);
        p2.addPoint(startx + 4, starty - 12);
        p2.addPoint(startx, starty);
        p2.addPoint(startx - 4, starty - 12);
    }

    /**
     * @return
     */
    public final boolean isOverLapping() {
        return over_lapping;
    }

    /**
     * COMMENTME
     */
    int x_shift = 0, y_shift = 0;

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
                Edge tmpKante = (Edge) me.clone();
                tmpKante.setKnots(startC.getElement(), endC.getElement(), false);
                EdgeContainer tmpC = new EdgeContainer(tmpKante, doc);
                tmpC.setColor(Color.gray);
                ((LayerContainer) containerParent).addTmpEdgeContainer(tmpC);
            }
        }
    }

    /**
     * Toleranz in Pixeln, mit der man neben die Edge klicken darf, wenn man sie per Mausklick auswählen will oder mit der entschieden wird, an
     * welcher Stelle ein neuer Knickpunkt hinzugefügt werden
     * muss.
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
        return getKnickpunktInsertIndex(x, y) >= 0;
    }

    /**
     * Liefert den Index, an dem in die Liste <code>knickpunkte</code> ein neuer Knickpunkt eingefügt werden würde, wenn der die übergebenen
     * Koordinaten besitzt.
     *
     * @param x
     * @param y
     * @return
     */
    public int getKnickpunktInsertIndex(final int x, final int y) {
        return getKnickpunktInsertIndex(x, y, TOLERANCE);
    }

    /**
     * Liefert den Index, an dem in die Liste <code>knickpunkte</code> ein neuer Knickpunkt eingefügt werden würde, wenn der die übergebenen
     * Koordinaten besitzt.
     *
     * @param x
     * @param y
     * @param tolerance
     * @return
     */
    private int getKnickpunktInsertIndex(final int x, final int y, final int tolerance) {
        int index = -1;
        if (!hasBendpoints()) {
            return index;
        }
        int startx = getStartX();
        int starty = getStartY();
        int numKKnots = bendpoints.size();
        for (int i = 0; i <= numKKnots; i++) {
            int endx = 0, endy = 0;
            if (i == numKKnots) {
                endx = getEndX();
                endy = getEndY();
            } else {
                endx = bendpoints.get(i).x;
                endy = bendpoints.get(i).y;
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
     * Setzt einen Knickpunkt an der richtigen Position in die Liste der Knickpunkte ein. Wird als Index -1 übergeben, dann wird der Index anhand der
     * Koordinaten berechnet.
     *
     * @param p
     * @param index
     */
    public void setKnickpunkt(final Bendpoint p, int index) {
        if (index == -1) {
            index = 0;
        }
        if (bendpoints == null) {
            bendpoints = new ArrayList<>();
        }
        while (index >= bendpoints.size()) {
            bendpoints.add(null);
        }
        // System.err.println("AXS_AXSsetKnickpunkt " + getGraphDocument());
        bendpoints.set(index, p);
    }

    /**
     * Fügt einen Knickpunkt an der richtigen Position in die Liste der Knickpunkte ein. Wird als Index -1 übergeben, dann wird der Index anhand der
     * Koordinaten berechnet.
     *
     * @param x
     * @param y
     * @param index
     */
    public Bendpoint addKnickpunkt(final int x, final int y, int index) {
        if (index < 0) {
            index = getKnickpunktInsertIndex(x, y);
        }
        if (index < 0) {
            index = 0;
        }
        Bendpoint p = new Bendpoint(this, x, y);
        if (bendpoints == null) {
            bendpoints = new ArrayList<>();
        }
        bendpoints.add(index, p);
        return p;
    }

    /**
     * @param kp
     */
    public void removeKnickpunkt(final Bendpoint p) {
        if (bendpoints != null) {
            int indexOf = bendpoints.indexOf(p);
            deselectBendpoint(indexOf); //falls er selektiertwar - auch dort entfernen
            if (indexOf == 0 && bendpoints.size() == 1) {
                bendpoints = null;
            } else {
                bendpoints.remove(indexOf);
            }
        }
    }

    private static final List<Bendpoint> EMPTY_BENDPOINT_LIST = ImmutableList.of();

    private static final List<Integer> EMPTY_INTEGER_LIST = ImmutableList.of();

    /**
     * @return
     */
    public Iterable<Bendpoint> iterateBendpointContainers() {
        return bendpoints != null ? bendpoints : EMPTY_BENDPOINT_LIST;
    }

    public int indexOfBendpointContainer(final Bendpoint bendpoint) {
        return bendpoints != null ? bendpoints.indexOf(bendpoint) : -1;
    }

    public int getBendpointContainerCount() {
        return bendpoints != null ? bendpoints.size() : 0;
    }

    public Bendpoint getBendpointContainer(final int index) {
        return bendpoints.get(index);
    }

    /**
     * @return
     */
    public Iterable<Integer> iterateSelectedBendpointIndices() {
        return selectedBendpoints != null ? selectedBendpoints : EMPTY_INTEGER_LIST;
    }

    public void selectBendpoint(final Bendpoint p) {
        if (selectedBendpoints == null) {
            selectedBendpoints = new ArrayList<>();
        }
        int index = bendpoints.indexOf(p);
        selectedBendpoints.add(index);
    }

    public void deselectBendpoint(final Bendpoint p) {
        int index = indexOfBendpointContainer(p);
        deselectBendpoint(index);
    }

    public void deselectBendpoint(final int selecedBendpointIndex) {
        if (selectedBendpoints != null) {
            int indexOfSelecedBendpointIndex = selectedBendpoints.indexOf(selecedBendpointIndex);
            if (indexOfSelecedBendpointIndex == 0 && selectedBendpoints.size() == 1) {
                clearSelectedBendpoints();
            } else if (selecedBendpointIndex >= 0) {
                selectedBendpoints.remove(indexOfSelecedBendpointIndex);
            }
        }
    }

    public int getSelectedBendpointCount() {
        return selectedBendpoints.size();
    }

    public void clearSelectedBendpoints() {
        selectedBendpoints = null;
    }

    public void moveSelectedBendpoints(final int deltaX, final int deltaY) {
        for (Integer selectedBendpointIndex : iterateSelectedBendpointIndices()) {
            Bendpoint bendpoint = getBendpointContainer(selectedBendpointIndex);
            bendpoint.x += deltaX;
            bendpoint.y += deltaY;
        }
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
            //bei Bausteinschnittsellen sollen keine surrogates gemalt werden. Vorher stand hier bei der 2. Bedingung: !(me instanceof KommBeziehung)
            if (createSurrogates && !(ModelConstants.isSlaveType(startClass) && ModelConstants.isSlaveType(endClass))) {
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
        startx = getStartX();
        starty = getStartY();

        int numKKnots = getBendpointContainerCount();
        for (int i = 0; i <= numKKnots; i++) {
            if (i == numKKnots) {
                endx = getEndX();
                endy = getEndY();
            } else {
                endx = bendpoints.get(i).x;
                endy = bendpoints.get(i).y;
            }

            boolean fatFrame = false;
            if (isResult || isHighLight) {
                fatFrame = true;
                gc.setStroke(FAT_STROKE);
            }

            if (!paintingSurrogates && isDashed()) {
                Stroke str = gc.getStroke();
                gc.setStroke(dashedStroke);
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
                    backward = edge instanceof HasPartEdge || !ModelConstants.isDirectedEdge(edge.getClass());
                }
                if (backward) {
                    gc.rotate(rad1, startx, starty);
                    // try {
                    g.drawPolygon(p2);
                    g.fillPolygon(p2);

                    // System.err.println("1.) " + i + " von " + numKKnots); System.err.println(p2 + " " + CollectionUtils.toString(p2.xpoints) + " " + CollectionUtils.toString(p2.ypoints));
                    // System.err.println(ModelConstants.getFullBackwardMetaAssociationName(dlk.getClass()) + ": start=" + dlk.getStart() + " -> end=" + dlk.getEnd()); } catch (Exception e) {
                    // System.err.println("1.) " + i + " von " + numKKnots + " ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"); System.err.println(p2 + " " +
                    // CollectionUtils.toString(p2.xpoints) + " " + CollectionUtils.toString(p2.ypoints)); System.err.println(getHashString() + " " +
                    // ModelConstants.getFullBackwardMetaAssociationName(dlk.getClass()) + ": start=" + dlk.getStart() + " -> end=" + dlk.getEnd()); }
                    gc.rotate(-rad1, startx, starty);
                }
            }
            if (i == numKKnots) {
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
                    g.drawPolygon(p1);
                    g.fillPolygon(p1);

                    // System.err.println("2.) " + i + " von " + numKKnots); System.err.println(p1 + " " + CollectionUtils.toString(p1.xpoints) + " " + CollectionUtils.toString(p1.ypoints));
                    // System.err.println(ModelConstants.getFullBackwardMetaAssociationName(dlk.getClass()) + ": start=" + dlk.getStart() + " -> end=" + dlk.getEnd()); } catch (Exception e) {
                    // System.err.println("2.) " + i + " von " + numKKnots + " #######################################################################################"); System.err.println(p1 + " " +
                    // CollectionUtils.toString(p1.xpoints) + " " + CollectionUtils.toString(p1.ypoints)); System.err.println(getHashString() + " " +
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
