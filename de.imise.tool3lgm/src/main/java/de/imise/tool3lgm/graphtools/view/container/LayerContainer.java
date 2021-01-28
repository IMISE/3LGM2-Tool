package de.imise.tool3lgm.graphtools.view.container;

import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_PAINT_EDGES_ONLY_FOR_SELECTED_ELEMENTS;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_RASTER;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.TRANSIENT_OPTION_DEBUG_GRAPH;
import static de.imise.tool3lgm.userproperties.UserProperties.IntProperty.PROPERTY_INT_RASTER_WIDTH;
import static de.imise.util.GraphicsFunctions.drawPointRect;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea.PaintState;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.util.Alphabetical;
import de.imise.util.ReflectionUtils;
import de.imise.util.collections.CollectionUtils;

/**
 * @author Thomas (15.06.2003), AXS
 */
public class LayerContainer extends ElementContainer {

    /**
     *
     */
    private int layerNumber = -1;

    /**
     * Liste, aus der die Grafik aufgebaut wird (Reihenfolge der Elemente
     * bestimmt, welches zuerst gemalt wird)
     */
    private List<NodeContainer> graphNodeContainers;

    /**
     * Liste, aus der der Baum aufgebaut wird (Reihenfolge der Elemente wird
     * alphabetisch gehalten)
     */
    private List<NodeContainer> treeNodeContainers;

    /**
     *
     */
    private List<EdgeContainer> edgeContainers;

    /**
     *
     */
    private List<BendpointContainer> bendpointContainers;

    /**
     * Liste aller NodeContainer, fuer die die Kanten sortiert werden muessen
     * (momentan nur Prozesse)
     */
    private List<NodeContainer> numberedEdgesNodeContainer;

    /**
     *
     */
    private List<EdgeContainer> tmpEdgeContainer;

    /**
     * Stores during paint all nodes which are already painted (no bendpoints)
     */
    private final HashSet<ModelElement> paintedNodes = new HashSet<>();

    //Strings, die oben und unten geschrieben werden (z.B. an Aufgaben und Objekttypen Redundanzfaktoren...)
    private KeyObjectStringMap additionalTextAbove, additionalTextDown;

    private final boolean showInterLayerConnections = false;

    /**
     * Je nach State, werden einige Dinge (Raster + Selektionen) nicht
     * mitgezeichnet
     */
    private BasicGraphArea.PaintState paintState = PaintState.REGULAR;

    /**
     * @param me
     * @param doc
     * @param map
     */
    public LayerContainer(final ModelElement me, final GraphDocument doc, final int layerNumber) {
        super(me, doc);
        this.layerNumber = layerNumber;
        layout = new GraphElementLayout();
        init();
    }

    /**
     * @param ec
     * @param doc
     * @param map
     */
    public LayerContainer(final ElementContainer ec, final GraphDocument doc, final int layerNumber) {
        super(ec, doc);
        this.layerNumber = layerNumber;
        layout = new GraphElementLayout();
        init();
    }

    /**
     * @param me
     * @param layout
     * @param doc
     * @param map
     */
    public LayerContainer(final ModelElement me, final GraphElementLayout layout, final GraphDocument doc, final int layerNumber) {
        super(me, layout, doc);
        this.layerNumber = layerNumber;
        this.layout = new GraphElementLayout();
        init();
    }

    /**
     * Sortiert die alphabetische Liste der Node erneut. Das muss man machen, da
     * beim initialen einfügen noch nicht die Namen der zusammengesetzten
     * ETNTKombinationen bekannt sind, so dass sie beim laden in der Regel
     * falsch einsortiert wurden.
     */
    public void refreshAlpahbetical() {
        Alphabetical.sort(treeNodeContainers);
    }

    /**
     *
     */
    private void init() {
        if (doc instanceof Szenario) {
            graphNodeContainers = new ArrayList<>(100);
            treeNodeContainers = new ArrayList<>(100);
            edgeContainers = new ArrayList<>(100);
            bendpointContainers = new ArrayList<>(50);
        } else {
            graphNodeContainers = new ArrayList<>(500);
            treeNodeContainers = new ArrayList<>(500);
            edgeContainers = new ArrayList<>(500);
            bendpointContainers = new ArrayList<>(1000);
        }
        numberedEdgesNodeContainer = new ArrayList<>(10);
        tmpEdgeContainer = new ArrayList<>(100);
    }

    @Override
    protected boolean isFadedIn() {
        return true; //gilt als immer sichtbar -> toString bekommt nicht das "(ausgeblendet)" vorangstellt
    }

    /**
     * @return the paintState
     */
    public PaintState getPaintState() {
        return paintState;
    }

    /**
     * @param paintState the paintState to set
     */
    public void setPaintState(final PaintState paintState) {
        this.paintState = paintState;
    }

    /**
     * Zählt Alle Elemente der übergebenen Art. Die Klasse wird auf Idenittät
     * geprüft, nicht auf Zuweisungskompatibilität.
     *
     * @param elementClass
     * @return
     */
    public int countType(final Class<? extends ModelElement> elementClass) {
        int counter = 0;

        if (ReflectionUtils.isAssignable(elementClass, Node.class)) {
            counter += countType(graphNodeContainers, elementClass);
        }
        if (ReflectionUtils.isAssignable(elementClass, Edge.class)) {
            counter += countType(edgeContainers, elementClass);
        }
        if (ReflectionUtils.isAssignable(elementClass, Bendpoint.class)) {
            counter += countType(bendpointContainers, elementClass);
        }
        return counter;
    }

    private static int countType(final Iterable<? extends ElementContainer> iterableContainers, final Class<? extends ModelElement> elementClass) {
        int counter = 0;
        for (ElementContainer nc : iterableContainers) {
            ModelElement me = nc.getElement();
            if (me.getClass() == elementClass) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * @param ec
     */
    public void z_move_down(final ElementContainer ec) {
        boolean visible = ec.isVisible();
        remove(ec);
        add(ec, 0);
        ec.setVisible(visible);
    }

    /**
     * @param ec
     */
    public void z_move_up(final ElementContainer ec) {
        boolean visible = ec.isVisible();
        remove(ec);
        add(ec, graphNodeContainers.size());
        raiseSlaves(ec, 0);
        ec.setVisible(visible);
    }

    /**
     * @param ec
     * @param position
     */
    public void z_move(final ElementContainer ec, final int position) {
        if (!(ec instanceof NodeContainer)) {
            return;
        }
        if (position < 0 || position >= graphNodeContainers.size()) {
            return;
        }
        int index = indexOf(ec);
        if (index < 0 || index == position) {
            return;
        }
        boolean visible = ec.isVisible();
        remove(ec);
        add(ec, position);
        if (position > index) {
            raiseSlaves(ec, 0);
        }
        ec.setVisible(visible);
    }

    /**
     * @param ec
     */
    public void z_step_up(final ElementContainer ec) {
        z_move(ec, indexOf(ec) + 1);
    }

    /**
     * @param ec
     */
    public void z_step_down(final ElementContainer ec) {
        z_move(ec, indexOf(ec) - 1);
    }

    /**
     * @param ec
     * @param stufe
     */
    public final void raiseSlaves(final ElementContainer ec, int stufe) {
        if (!(ec instanceof NodeContainer)) {
            return;
        }

        NodeContainer kc = (NodeContainer) ec;

        int pos = indexOf(kc);
        if (pos < 0) {
            return;
        }
        ModelElement me = kc.getElement();
        for (Edge edge : me.getEdges()) {
            if (!(edge instanceof CompositionEdge)) {
                continue;
            }
            CompositionEdge co = (CompositionEdge) edge;
            ModelElement slave = co.getSlave();
            if (slave != me && indexOf(slave) >= 0) {
                // Umsortieren
                ElementContainer tmp = slave.getContainer(doc);
                if (!(tmp instanceof NodeContainer)) {
                    return;
                }
                while (indexOf(tmp) < indexOf(ec)) {
                    z_step_up(tmp);
                }

                // rekursiv die Unterelmente raisen
                if (slave.hasEdges()) {
                    stufe++;
                    raiseSlaves(slave.getContainer(doc), stufe);
                    stufe--;
                }
            }
        }
    }

    /**
     * COMMENTME
     */
    double x_shift = 0;
    /**
     * COMMENTME
     */
    double y_shift = 0;

    /**
     * @param x
     * @param y
     */
    public void setShift(final double x, final double y) {
        x_shift = x;
        y_shift = y;
    }

    /**
     * COMMENTME
     */
    boolean multiView = false;

    /**
     * @param b
     */
    public void setMultiView(final boolean b) {
        multiView = b;
    }

    /**
     * @return
     */
    public boolean getMultiView() {
        return multiView;
    }

    @Override
    public int getWidth() {
        return doc.getPageWidth();
    }

    @Override
    public int getHeight() {
        return doc.getPageHeight();
    }

    @Override
    protected void paintComponent(final Graphics g) {
        //        if (layerNumber == 0) {
        //            int page_width = getWidth();
        //            int page_height = getHeight();
        //            System.err.println("#########  Layer: x1=" + -page_width / 2 + "  y1=" + -page_height / 2 + "  x2=" + page_width / 2 + "  y2=" + page_height / 2);
        //        }
        if (layerNumber % 2 == 0) {
            int page_width = getWidth();
            int page_height = getHeight();

            g.setColor(getColor());
            g.fillRect(-page_width / 2, -page_height / 2, page_width, page_height);
            g.setColor(Color.black);
            g.drawRect(-page_width / 2, -page_height / 2, page_width, page_height);

            Graphics2D gc = (Graphics2D) g;

            if (this == doc.getActiveLayer() && paintState == PaintState.REGULAR) {
                Stroke currentStroke = gc.getStroke();
                gc.setStroke(GraphElementLayout.LAYER_STROKE_SELECTED);
                g.drawRect(-page_width / 2 + 1, -page_height / 2 + 1, page_width - 2, page_height - 2);
                gc.setStroke(currentStroke);
            }

            if (OPTION_SHOW_RASTER.is() && paintState != PaintState.WEBEXPORT) {
                paintRaster(gc, page_width, page_height);
            }

            //Diese Fallunterscheidung ist nur, um in dieser zeitkritischen Funktion nicht Zuweisungen doppelt zu machen
            if (additionalTextAbove != null && additionalTextDown != null) {
                g.setColor(Color.black);
                Font font = getFont();
                g.setFont(font);
                FontMetrics fm = getFontMetrics(font);
                String s = additionalTextAbove.getText();
                int stringWidth = fm.stringWidth(s);
                g.drawString(s, -stringWidth / 2, -page_height / 2);
                s = additionalTextDown.getText();
                stringWidth = fm.stringWidth(s);
                g.drawString(s, -stringWidth / 2, page_height / 2 + font.getSize());
            } else if (additionalTextAbove != null) {
                g.setColor(Color.black);
                Font font = getFont();
                g.setFont(font);
                String s = additionalTextAbove.getText();
                g.drawString(s, -getFontMetrics(font).stringWidth(s) / 2, -page_height / 2 - 5);
            } else if (additionalTextDown != null) {
                g.setColor(Color.black);
                Font font = getFont();
                g.setFont(font);
                String s = additionalTextDown.getText();
                g.drawString(s, -getFontMetrics(font).stringWidth(s) / 2, page_height / 2 + font.getSize() + 5);
            }
        }
    }

    private void paintRaster(final Graphics2D gc, final int page_width, final int page_height) {
        gc.setColor(Color.darkGray);
        int maxX = page_width / 2 + 1;
        int maxY = page_height / 2 + 1;
        int rasterWidth = PROPERTY_INT_RASTER_WIDTH.get();

        //              malt das Raster mit durchgezogenen Linien -> kann man für Kontrollzwecke wieder einblenden
        //              g.setColor(Color.lightGray);
        //              for (int x=0; x<maxX; x+=rasterWidth){
        //                  g.drawLine(x, -maxY, x, maxY);
        //                  g.drawLine(-x, -maxY, -x, maxY);
        //              }
        //              for (int y=0; y<maxY; y+=rasterWidth){
        //                  g.drawLine(-maxX, y, maxX, y);
        //                  g.drawLine(-maxX, -y, maxX, -y);
        //              }

        Stroke currentStroke = gc.getStroke();
        //kann man auch höher setzen
        float dashWidth = 1.0f;
        float dash[] = {
                dashWidth, rasterWidth - dashWidth
        };
        Stroke rasterStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0.0f, dash, 0.0f);
        int diff = (int) dashWidth / 2;

        int div = maxY / rasterWidth;
        maxY = div * rasterWidth + diff;
        gc.setStroke(rasterStroke);
        for (int x = 0; x < maxX; x += rasterWidth) {
            gc.drawLine(x, -maxY, x, maxY);
            gc.drawLine(-x, -maxY, -x, maxY);
        }
        //  Dies hier würde die Linien auch aus x-Richtung ziehen, was aber noch nicht ganz stimmt (leicht versetzt)
        //
        //              div = maxX/rasterWidth;
        //              maxX = div*rasterWidth+diff;
        //              gc.setStroke(rasterStroke);
        //              for (int y=0; y<maxY; y+=rasterWidth){
        //                  g.drawLine(-maxX, y, maxX, y);
        //                  g.drawLine(-maxX, -y, maxX, -y);
        //              }

        gc.setStroke(currentStroke);
    }

    /**
     * Ist oben auskommentiert und daher ungenutzt, da man es nur zum Debug
     * braucht
     *
     * @param g
     */
    private void paintCrossInTeMiddle(final Graphics g) {
        //malt ein großes Kreuz in den Mittelpunkt der Zeichenfläche
        int crossSize = 100;
        g.drawLine(-crossSize, -crossSize, crossSize, crossSize);
        g.drawLine(crossSize, -crossSize, -crossSize, crossSize);
    }

    @Override
    protected void paintChildren(final Graphics g) {
        //		synchronized (getTreeLock()) {
        tmpEdgeContainer.clear();
        paintedNodes.clear();
        boolean isPaintEdgesOnlyForSelectedElements = OPTION_PAINT_EDGES_ONLY_FOR_SELECTED_ELEMENTS.is();
        for (NodeContainer nc : graphNodeContainers) {
            //hier wird bei jedem Element nochmal geprüft, ob es in die Ebene passt. Wenn nicht, wird die
            //Ebenengröße hochgesetzt und das Zeichen neu angestoßen
            if (!doc.pageHasSize(nc)) {
                doc.setPageSizeFactor(-1.0);
                return;
            }
            ModelElement node = nc.getElement();
            for (Edge edge : node.getEdges()) {
                ElementContainer ec = edge.getContainer(doc);
                if (ec == null) {
                    continue;
                }
                paintedNodes.add(node);
                ModelElement other = edge.getOther(node);
                if (paintedNodes.contains(other)) {
                    boolean paintEdge = false;
                    if (isPaintEdgesOnlyForSelectedElements) {
                        ModelElement start = edge.getStart();
                        ElementContainer startContainer = start.getContainer(doc);
                        if (doc.isSelected(startContainer)) {
                            paintEdge = true;
                        } else {
                            ModelElement end = edge.getEnd();
                            ElementContainer endContainer = end.getContainer(doc);
                            if (doc.isSelected(endContainer)) {
                                paintEdge = true;
                            }
                        }
                    } else {
                        paintEdge = true;
                    }
                    if (paintEdge) {
                        ec.paint(g);
                        EdgeContainer edgeC = (EdgeContainer) ec;
                        for (BendpointContainer bc : edgeC.iterateBendpointContainers()) {
                            //hier wird bei jedem Knickpunkt nochmal geprüft, ob er in die Ebene passt. Wenn nicht, wird die
                            //Ebenengröße hochgesetzt und das Zeichen neu angestoßen
                            if (!doc.pageHasSize(bc)) {
                                doc.setPageSizeFactor(-1.0);
                                return;
                            }
                            bc.paint(g);
                        }
                    }
                }
            }
            nc.paint(g);
        }
        if (!isPaintEdgesOnlyForSelectedElements) {
            paintingSurrogates = true;
            for (EdgeContainer ec : tmpEdgeContainer) {
                ec.paint(g);
            }
            paintingSurrogates = false;
        }
        if (TRANSIENT_OPTION_DEBUG_GRAPH.is()) {
            paintDebugRectangles(g);
        }
    }

    /**
     * Paint the {@link InputGraphArea#grabbedElementsFullRect},
     * {@link InputGraphArea#grabbedElementsRasteredRect} and he
     * {@link InputGraphArea#grabbedElementsRealRect} to the layer.
     *
     * @param g
     */
    private void paintDebugRectangles(final Graphics g) {
        paintCrossInTeMiddle(g);
        drawPointRect(g, InputGraphArea.grabbedElementsFullRect, Color.red);
        drawPointRect(g, InputGraphArea.grabbedElementsRasteredRect, Color.green);
        drawPointRect(g, InputGraphArea.grabbedElementsRealRect, Color.blue);
    }

    @Override
    public final void refreshText() {
        // hier passiert nichts
    }

    /**
     * @param id
     * @return
     */
    public boolean containsID(final String id) {
        if (!doc.getCollection().isBulkMode()) {
            for (NodeContainer ec : graphNodeContainers) {
                if (ec.getID().equals(id)) {
                    return true;
                }
            }
            for (EdgeContainer ec : edgeContainers) {
                if (ec.getID().equals(id)) {
                    return true;
                }
            }
            for (BendpointContainer ec : bendpointContainers) {
                if (ec.getID().equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param kc
     */
    public void resetPositionOf(final NodeContainer kc) {
        if (treeNodeContainers.remove(kc)) {
            Alphabetical.insert(treeNodeContainers, kc);
        }
    }

    @Override
    public Component add(final Component comp) {
        return add(comp, -1);
    }

    @Override
    public Component add(final Component comp, final int pos) {
        if (containsID(((ElementContainer) comp).getID())) {
            return null;
        }
        //		comp = super.add(comp);
        if (comp instanceof BendpointContainer) {
            if (pos != -1) {
                bendpointContainers.add(pos, (BendpointContainer) comp);
            } else {
                bendpointContainers.add((BendpointContainer) comp);
            }
        } else if (comp instanceof EdgeContainer) {
            if (pos != -1) {
                edgeContainers.add(pos, (EdgeContainer) comp);
            } else {
                edgeContainers.add((EdgeContainer) comp);
            }
        } else {
            NodeContainer nc = (NodeContainer) comp;
            if (pos != -1) {
                graphNodeContainers.add(pos, nc);
            } else {
                graphNodeContainers.add(nc);
            }
            Alphabetical.insert(treeNodeContainers, nc);
            Node node = nc.getNode();
            MetaModel metaModel = doc.getMetaModel();
            if (metaModel.hasOrderedEdgeClassesToPaintable(node.getClass())) {
                numberedEdgesNodeContainer.add(nc);
            }
        }
        ((ElementContainer) comp).setParent(this);
        if (!(doc.getCollection().getSelectedDoc() instanceof Szenario)) {
            comp.setVisible(false);
        }

        return comp;
    }

    @Override
    public void remove(final Component comp) {
        if (comp == null) {
            return;
        }
        if (comp instanceof BendpointContainer) {
            bendpointContainers.remove(comp);
        } else if (comp instanceof EdgeContainer) {
            edgeContainers.remove(comp);
        } else {
            graphNodeContainers.remove(comp);
            treeNodeContainers.remove(comp);
            numberedEdgesNodeContainer.remove(comp);
        }
    }

    @Override
    public void removeAll() {
        graphNodeContainers.clear();
        treeNodeContainers.clear();
        edgeContainers.clear();
        bendpointContainers.clear();
        numberedEdgesNodeContainer.clear();
    }

    /**
     * Sortiert die EdgeContainer in der Liste aller EdgeContainer so um, dass
     * ihre Reihenfolge für alle NodeContainer mit Kanten, deren Reihenfolge
     * eine Bedeutung hat, der Reihenfolge der Kanten in ihrer ArrayList
     * connections entspricht.
     */
    public void sortEdgeContainers() {
        //fuer alle NodeContainer in numberedEdgesNodeContainer
        for (NodeContainer kc : numberedEdgesNodeContainer) {
            //fuer jede seiner Kanten
            for (Edge egde : kc.getElement().getEdges()) {
                //hole ihren Container
                EdgeContainer kantCont = (EdgeContainer) egde.getContainer(doc);
                //loesche ihn aus kanten
                edgeContainers.remove(kantCont);
                //fuege ihn am Ende wieder hinzu
                edgeContainers.add(kantCont);
            }
        }
    }

    /**
     * @param me
     * @return
     */
    public int indexOf(final ModelElement me) {
        return indexOf(me.getContainer(doc));
    }

    /**
     * @param ec
     * @return
     */
    public int indexOf(final ElementContainer ec) {
        return graphNodeContainers.indexOf(ec);
    }

    /**
     * @return
     */
    public List<NodeContainer> getNodeContainersAlphabetical() {
        return treeNodeContainers;
    }

    /**
     * @param list
     */
    public void addAllContainers(final List<ElementContainer> list) {
        //Die Reihenfolge der Listen ist Absicht, da diese Funktion insbesondere beim Löschen von Elementen gebraucht wird
        //und man ohne irgendwelche Konflikte erst Knickpunkte, dann Kanten und dann Knoten löschen kann
        list.addAll(bendpointContainers);
        list.addAll(edgeContainers);
        list.addAll(treeNodeContainers);
    }

    /**
     * @param list
     * @param alphabetical
     */
    public void addNodeContainers(final List<ElementContainer> list, final boolean alphabetical) {
        list.addAll(alphabetical ? treeNodeContainers : graphNodeContainers);
    }

    /**
     * @param list
     */
    public void addEdgeContainers(final List<ElementContainer> list) {
        list.addAll(edgeContainers);
    }

    /**
     * @param list
     */
    public void addBendpointContainers(final List<ElementContainer> list) {
        list.addAll(bendpointContainers);
    }

    public Iterable<NodeContainer> getGraphNodeContainers() {
        return () -> graphNodeContainers.listIterator();
    }

    public Iterable<NodeContainer> getNodeContainersBackward() {
        return CollectionUtils.getBackwardIterable(graphNodeContainers);
    }

    public Iterable<EdgeContainer> getEdgeContainers() {
        return () -> edgeContainers.listIterator();
    }

    public Iterable<EdgeContainer> getEdgeContainersBackward() {
        return CollectionUtils.getBackwardIterable(edgeContainers);
    }

    public Iterable<BendpointContainer> getBendpointContainers() {
        return () -> bendpointContainers.listIterator();
    }

    /**
     * @param i
     * @return
     */
    public NodeContainer getNodeContainer(final int i) {
        return getNodeContainersAlphabetical().get(i);
    }

    /**
     * @param i
     * @return
     */
    public EdgeContainer getEdgeContainer(final int i) {
        return edgeContainers.get(i);
    }

    /**
     * @param id
     * @return
     */
    public EdgeContainer getEdgeContainer(final String id) {
        for (EdgeContainer kc : edgeContainers) {
            if (kc.getID().equals(id)) {
                return kc;
            }
        }
        return null;
    }

    /**
     * @param i
     * @return
     */
    public BendpointContainer getBendpointContainer(final int i) {
        return bendpointContainers.get(i);
    }

    /**
     * @return
     */
    public int getNodeContainerCount() {
        return graphNodeContainers.size();
    }

    /**
     * @return
     */
    public int getEdgeContainerCount() {
        return edgeContainers.size();
    }

    /**
     * @return
     */
    public int getBendpointContainerCount() {
        return bendpointContainers.size();
    }

    /**
     * @param ec
     * @return
     */
    public boolean isMyElement(final ElementContainer ec) {
        if (ec instanceof BendpointContainer) {
            return bendpointContainers.contains(ec);
        }
        if (ec instanceof NodeContainer) {
            return graphNodeContainers.contains(ec);
        }
        if (ec instanceof EdgeContainer) {
            return edgeContainers.contains(ec);
        }
        return false;
    }

    /**
     * @param me
     * @return
     */
    public boolean isMyElement(final ModelElement me) {
        if (me instanceof Bendpoint) {
            for (ElementContainer ec : bendpointContainers) {
                if (ec.hasElement(me)) {
                    return true;
                }
            }
        } else if (me instanceof Node) {
            for (ElementContainer ec : graphNodeContainers) {
                if (ec.hasElement(me)) {
                    return true;
                }
            }
        } else if (me instanceof Edge) {
            for (ElementContainer ec : edgeContainers) {
                if (ec.hasElement(me)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return the showInterLayerConnections
     */
    public boolean isShowInterLayerConnections() {
        boolean show = false;
        for (NodeContainer ec : graphNodeContainers) {
            if (ec instanceof InterLayerConnectedNodeContainer) {
                InterLayerConnectedNodeContainer interLayerEc = (InterLayerConnectedNodeContainer) ec;
                if (!interLayerEc.isShowInterLayerConnections()) {
                    show = false; //if at least one inter layer connection ist not shown -> return false
                    break;
                }
                show = true; //only true if at least one inter layer connection is shown
            }
        }
        return show; //only true if all inter layer connections are shown
    }

    /**
     * (De-)Aktiviert das Anzeigen aller Interebenenbeziehungen
     *
     * @param showInterLayerConnections aktiviren / deaktivieren
     * @param doc aktives GraphDocument
     */
    public void setShowInterLayerConnections(final boolean showInterLayerConnections) {
        for (NodeContainer ec : graphNodeContainers) {
            setShowInterLayerConnections(showInterLayerConnections, ec);
        }
    }

    /**
     * (De-)Aktiviert das Anzeigen der Interebenenbeziehungen für den
     * spezifizierten {@link ElementContainer}
     *
     * @param showInterLayerConnections aktiviren / deaktivieren
     * @param doc aktives GraphDocument
     * @param ec Container, dessen Interebenenbeziehungen (de-)aktiviert werden
     *            sollen
     */
    public void setShowInterLayerConnections(final boolean showInterLayerConnections, final ElementContainer ec) {
        if (ec instanceof InterLayerConnectedNodeContainer) {
            ((InterLayerConnectedNodeContainer) ec).setShowInterLayerConnections(showInterLayerConnections);
        }
    }

    /**
     * @param kc
     */
    public void addTmpEdgeContainer(final EdgeContainer kc) {
        tmpEdgeContainer.add(kc);
        kc.setParent(this);
    }

    /**
     * @param key
     * @param string
     */
    public void setAdditionalTextAbove(final Object key, final String string) {
        if (additionalTextAbove == null) {
            additionalTextAbove = new KeyObjectStringMap();
        }
        additionalTextAbove.set(key, string);
    }

    /**
     * @param key
     */
    public void removeAdditionalTextAbove(final Object key) {
        if (additionalTextAbove == null) {
            return;
        }
        additionalTextAbove.remove(key);
    }

    /**
     * @param key
     * @param string
     */
    public void setAdditionalTextDown(final Object key, final String string) {
        if (additionalTextDown == null) {
            additionalTextDown = new KeyObjectStringMap();
        }
        additionalTextDown.set(key, string);
    }

    /**
     * Funktioniert wie eine Map, bei der die Werte aber in einer
     * Listenreihenfolge erhalten bleiben.
     */
    private class KeyObjectStringMap {
        List<Object> keyList = new ArrayList<>();
        List<String> stringList = new ArrayList<>();

        public void set(final Object key, final String s) {
            for (int i = 0; i < keyList.size(); i++) {
                if (keyList.get(i) == key) {
                    stringList.set(i, s);
                    return;
                }
            }
            keyList.add(key);
            stringList.add(s);
        }

        /**
         * @param key
         */
        public void remove(final Object key) {
            for (int i = 0; i < keyList.size(); i++) {
                if (keyList.get(i) == key) {
                    keyList.remove(i);
                    stringList.remove(i);
                    return;
                }
            }
        }

        /**
         * @return
         */
        public String getText() {
            if (additionalTextAbove == null) {
                return "";
            }
            StringBuilder retString = new StringBuilder();
            for (int i = 0; i < stringList.size(); i++) {
                retString.append(stringList.get(i));
                retString.append('\n');
            }
            return retString.toString();
        }
    }

    /**
     * @return Returns the layerNumber.
     */
    public int getLayerNumber() {
        return layerNumber;
    }

    public void printStatistics() {
        System.err.println("Layer " + layerNumber + "    nodeContainer: " + graphNodeContainers.size() + " -> " + ReflectionUtils.getCommonSuperClass(graphNodeContainers));
    }

}