/*
 * Created on 15.06.2003
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.view.container;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.Composition;
import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.Node;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea.PaintState;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.Alphabetical;
import de.imise.util.ReflectionUtils;

/**
 * @author Thomas
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LayerContainer extends ElementContainer {

    /**
     *
     */
    private static BasicStroke dick = new BasicStroke((float) 3.0);

    /**
     *
     */
    private static BasicStroke duenn = new BasicStroke((float) 1.0);

    /**
     *
     */
    private int layerNumber = -1;

    /**
     * Liste, aus der die Grafik aufgebaut wird (Reihenfolge der Elemente bestimmt, welches zuerst gemalt wird)
     */
    private List<NodeContainer> nodeContainer;

    /**
     * Liste, aus der der Baum aufgebaut wird (Reihenfolge der Elemente wird alphabetisch gehalten)
     */
    private List<NodeContainer> alphabeticalNodeContainer;

    /**
     *
     */
    private List<EdgeContainer> edgeContainer;

    /**
     *
     */
    private List<BendpointContainer> bendpointContainer;

    /**
     * sortElements enthaelt alle Node(Container), fuer die die Kanten sortiert werden muessen (momentan nur Prozesse)
     */
    private List<NodeContainer> numberedEdgesNodeContainer;

    /**
     *
     */
    private List<EdgeContainer> tmpEdgeContainer;

    //Strings, die oben und unten geschrieben werden (z.B. an Aufgaben und Objekttypen Redundanzfaktoren...)
    private KeyObjectStringMap additionalTextAbove, additionalTextDown;

    private boolean showInterLayerConnections = false;

    /**
     * Je nach State, werden einige Dinge (Raster + Selektionen) nicht mitgezeichnet
     */
    private BasicGraphArea.PaintState paintState = PaintState.REGULAR;

    /**
     * @param neu
     * @param gd
     * @param map
     */
    public LayerContainer(final ModelElement neu, final GraphDocument gd, final int layerNumber) {
        super(neu, gd);
        this.layerNumber = layerNumber;
        layout = new GraphElementLayout();
        init();
    }

    /**
     * @param alt
     * @param gd
     * @param map
     */
    public LayerContainer(final ElementContainer alt, final GraphDocument gd, final int layerNumber) {
        super(alt, gd);
        this.layerNumber = layerNumber;
        layout = new GraphElementLayout();
        init();
    }

    /**
     * @param neu
     * @param l
     * @param gd
     * @param map
     */
    public LayerContainer(final ModelElement neu, final GraphElementLayout l, final GraphDocument gd, final int layerNumber) {
        super(neu, l, gd);
        this.layerNumber = layerNumber;
        layout = new GraphElementLayout();
        init();
    }

    /**
     * Sortiert die alphabetische Liste der Node erneut. Das muss man machen, da beim initialen einfügen
     * noch nicht die Namen der zusammengesetzten ETNTKombinationen bekannt sind, so dass sie beim laden
     * in der Regel falsch einsortiert wurden.
     */
    public void refreshAlpahbetical() {
        Alphabetical.sort(alphabeticalNodeContainer);
    }

    /**
     *
     */
    private void init() {
        if (doc instanceof Szenario) {
            nodeContainer = new ArrayList<>(100);
            alphabeticalNodeContainer = new ArrayList<>(100);
            edgeContainer = new ArrayList<>(100);
            bendpointContainer = new ArrayList<>(50);
        } else {
            nodeContainer = new ArrayList<>(500);
            alphabeticalNodeContainer = new ArrayList<>(500);
            edgeContainer = new ArrayList<>(500);
            bendpointContainer = new ArrayList<>(1000);
        }
        numberedEdgesNodeContainer = new ArrayList<>(10);
        tmpEdgeContainer = new ArrayList<>(100);
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
     * @param elementClass
     * @return
     */
    public int countType(final Class<? extends ModelElement> elementClass) {
        int counter = 0;
        if (Node.class.isAssignableFrom(elementClass)) {
            for (int c = 0; c < getKnotenCount(); c++) {
                if (getNodeContainer(c).getKnoten().getClass() == elementClass) {
                    counter++;
                }
            }
        } else if (Edge.class.isAssignableFrom(elementClass)) {
            for (int c = 0; c < getKantenCount(); c++) {
                if (getEdgeContainer(c).getElement().getClass() == elementClass) {
                    counter++;
                }
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
        add(ec, nodeContainer.size());
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
        if (position < 0 || position > nodeContainer.size()) {
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
            if (!(edge instanceof Composition)) {
                continue;
            }
            Composition co = (Composition) edge;
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
        if (layerNumber != 1 && layerNumber != 3) {
            int page_width = getWidth();
            int page_height = getHeight();

            g.setColor(getColor());
            g.fillRect(-page_width / 2, -page_height / 2, page_width, page_height);
            g.setColor(Color.black);
            g.drawRect(-page_width / 2, -page_height / 2, page_width, page_height);

            Graphics2D gc = (Graphics2D) g;

            if (this == doc.getActiveLayer() && paintState == PaintState.REGULAR) {
                gc.setStroke(dick);
                g.drawRect(-page_width / 2 + 1, -page_height / 2 + 1, page_width - 2, page_height - 2);
                gc.setStroke(duenn);
            }

            if (UserProperties.isShowRaster() && paintState != PaintState.WEBEXPORT) {
                Stroke stk = gc.getStroke();
                g.setColor(Color.darkGray);
                int maxX = page_width / 2 + 1;
                int maxY = page_height / 2 + 1;
                int rasterWidth = UserProperties.getRasterWidth();

                //				malt das Raster mit durchgezogenen Linien -> kann man für Kontrollzwecke wieder einblenden
                //				g.setColor(Color.lightGray);
                //				for (int x=0; x<maxX; x+=rasterWidth){
                //					g.drawLine(x, -maxY, x, maxY);
                //					g.drawLine(-x, -maxY, -x, maxY);
                //				}
                //				for (int y=0; y<maxY; y+=rasterWidth){
                //					g.drawLine(-maxX, y, maxX, y);
                //					g.drawLine(-maxX, -y, maxX, -y);
                //				}

                //kann man auch höher setzen
                float dashWidth = 1.0f;
                float dash[] = {
                        dashWidth,
                        rasterWidth - dashWidth
                };
                Stroke rasterStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0.0f, dash, 0.0f);
                int diff = (int) dashWidth / 2;

                int div = maxY / rasterWidth;
                maxY = div * rasterWidth + diff;
                gc.setStroke(rasterStroke);
                for (int x = 0; x < maxX; x += rasterWidth) {
                    g.drawLine(x, -maxY, x, maxY);
                    g.drawLine(-x, -maxY, -x, maxY);
                }
                //	Dies hier würde die Linien auch aus x-Richtung ziehen, was aber noch nicht ganz stimmt (leicht versetzt)
                //
                //				div = maxX/rasterWidth;
                //				maxX = div*rasterWidth+diff;
                //				gc.setStroke(rasterStroke);
                //				for (int y=0; y<maxY; y+=rasterWidth){
                //					g.drawLine(-maxX, y, maxX, y);
                //					g.drawLine(-maxX, -y, maxX, -y);
                //				}

                gc.setStroke(stk);
            }
            //			malt ein großes Kreuz in den Mittelpunkt der Zeichenfläche
            //			int kreiz = 100;
            //			g.drawLine(-kreiz, -kreiz, kreiz, kreiz);
            //			g.drawLine(kreiz, -kreiz, -kreiz, kreiz);

            //Diese Fallunterschieidung ist nur, um in dieser zeitkritischen Funktion nicht Zuweisungen doppelt zu machen
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

    @Override
    protected void paintChildren(final Graphics g) {
        //		synchronized (getTreeLock()) {

        tmpEdgeContainer.clear();

        for (NodeContainer ec : nodeContainer) {
            ec.paint(g);
        }

        for (EdgeContainer ec : edgeContainer) {
            if (UserProperties.isPaintEdgesOnlyForSelectedElements()) {
                Edge edge = (Edge) ec.getElement();
                ModelElement start = edge.getStart();
                ElementContainer startContainer = start.getContainer(doc);
                if (doc.isSelected(startContainer)) {
                    ec.paint(g);
                } else {
                    ModelElement end = edge.getEnd();
                    ElementContainer endContainer = end.getContainer(doc);
                    if (doc.isSelected(endContainer)) {
                        ec.paint(g);
                    }
                }
            } else {
                ec.paint(g);
            }
        }

        if (!UserProperties.isPaintEdgesOnlyForSelectedElements()) {
            for (BendpointContainer ec : bendpointContainer) {
                ec.paint(g);
            }
            paintingSurrogates = true;
            for (EdgeContainer ec : tmpEdgeContainer) {
                ec.paint(g);
            }
            paintingSurrogates = false;
        }

        //		if (doc.isVerificationMode()){
        //			Rectangle r = InputGraphArea.grabbedElementsFullRect;
        //			if (r!=null) {
        //				g.setColor(Color.red);
        //				g.drawLine(r.x, r.y, r.x, r.height);
        //				g.drawLine(r.x, r.height, r.width, r.height);
        //				g.drawLine(r.width, r.height, r.width, r.y);
        //				g.drawLine(r.width, r.y, r.x, r.y);
        //			}
        //			r = InputGraphArea.grabbedElementsRasteredRect;
        //			if (r!=null) {
        //				g.setColor(Color.green);
        //				g.drawLine(r.x, r.y, r.x, r.height);
        //				g.drawLine(r.x, r.height, r.width, r.height);
        //				g.drawLine(r.width, r.height, r.width, r.y);
        //				g.drawLine(r.width, r.y, r.x, r.y);
        //			}
        //			r = InputGraphArea.grabbedElementsRealRect;
        //			if (r!=null) {
        //				g.setColor(Color.blue);
        //				g.drawLine(r.x, r.y, r.x, r.height);
        //				g.drawLine(r.x, r.height, r.width, r.height);
        //				g.drawLine(r.width, r.height, r.width, r.y);
        //				g.drawLine(r.width, r.y, r.x, r.y);
        //			}
        //
        //		}
    }

    @Override
    public final void refreshText() {
        // hier passiert nichts
    }

    /**
     * @param hashString
     * @return
     */
    public boolean containsHashString(final String hashString) {
        if (!doc.getCollection().isBulkMode()) {
            for (NodeContainer ec : nodeContainer) {
                if (ec.getHashString().equals(hashString)) {
                    return true;
                }
            }
            for (EdgeContainer ec : edgeContainer) {
                if (ec.getHashString().equals(hashString)) {
                    return true;
                }
            }
            for (BendpointContainer ec : bendpointContainer) {
                if (ec.getHashString().equals(hashString)) {
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
        if (alphabeticalNodeContainer.remove(kc)) {
            Alphabetical.insert(alphabeticalNodeContainer, kc);
        }
    }

    @Override
    public Component add(final Component comp) {
        return add(comp, -1);
    }

    @Override
    public Component add(final Component comp, final int pos) {
        if (containsHashString(((ElementContainer) comp).getHashString())) {
            return null;
        }
        //		comp = super.add(comp);
        if (comp instanceof BendpointContainer) {
            if (pos != -1) {
                bendpointContainer.add(pos, (BendpointContainer) comp);
            } else {
                bendpointContainer.add((BendpointContainer) comp);
            }
        } else if (comp instanceof EdgeContainer) {
            if (pos != -1) {
                edgeContainer.add(pos, (EdgeContainer) comp);
            } else {
                edgeContainer.add((EdgeContainer) comp);
            }
        } else {
            NodeContainer nc = (NodeContainer) comp;
            if (pos != -1) {
                nodeContainer.add(pos, nc);
            } else {
                nodeContainer.add(nc);
            }
            Alphabetical.insert(alphabeticalNodeContainer, nc);
            Node node = nc.getKnoten();
            if (ModelConstants.hasSortedEdges(node.getClass())) {
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
            bendpointContainer.remove(comp);
        } else if (comp instanceof EdgeContainer) {
            edgeContainer.remove(comp);
        } else {
            nodeContainer.remove(comp);
            alphabeticalNodeContainer.remove(comp);
            numberedEdgesNodeContainer.remove(comp);
        }
    }

    @Override
    public void removeAll() {
        nodeContainer.clear();
        alphabeticalNodeContainer.clear();
        edgeContainer.clear();
        bendpointContainer.clear();
        numberedEdgesNodeContainer.clear();
    }

    /**
     * Sortiert die KantenContainer in kanten so um, dass ihre Reihenfolge für alle Node(Container)
     * in sortKnot der Reihenfolge der Kanten in ihrer ArrayList connections entspricht.
     */
    public void sortKanten() {
        //fuer alle NodeContainer in numberedEdgesNodeContainer
        for (NodeContainer kc : numberedEdgesNodeContainer) {
            //fuer jede seiner Kanten
            for (Edge egde : kc.getElement().getEdges()) {
                //hole ihren Container
                EdgeContainer kantCont = (EdgeContainer) egde.getContainer(doc);
                //loesche ihn aus kanten
                edgeContainer.remove(kantCont);
                //fuege ihn am Ende wieder hinzu
                edgeContainer.add(kantCont);
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
        return nodeContainer.indexOf(ec);
    }

    /**
     * @return
     */
    public List<NodeContainer> getKnotenAlphabetical() {
        return alphabeticalNodeContainer;
    }

    /**
     * @return
     */
    public List<NodeContainer> getKnoten() {
        return nodeContainer;
    }

    /**
     * @return
     */
    public List<EdgeContainer> getKanten() {
        return edgeContainer;
    }

    /**
     * @return
     */
    public List<BendpointContainer> getKnickpunkte() {
        return bendpointContainer;
    }

    /**
     * @param i
     * @return
     */
    public NodeContainer getNodeContainer(final int i) {
        return nodeContainer.get(i);
    }

    /**
     * @param i
     * @return
     */
    public EdgeContainer getEdgeContainer(final int i) {
        return edgeContainer.get(i);
    }

    /**
     * @param hashString
     * @return
     */
    public EdgeContainer getEdgeContainer(final String hashString) {
        for (EdgeContainer kc : edgeContainer) {
            if (kc.getHashString().equals(hashString)) {
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
        return bendpointContainer.get(i);
    }

    /**
     * @return
     */
    public int getKantenCount() {
        return edgeContainer.size();
    }

    /**
     * @return
     */
    public int getKnotenCount() {
        return nodeContainer.size();
    }

    /**
     * @return
     */
    public int getKnickpunkteCount() {
        return bendpointContainer.size();
    }

    /**
     * @param ec
     * @return
     */
    public boolean isMyElement(final ElementContainer ec) {
        if (ec instanceof BendpointContainer) {
            return bendpointContainer.contains(ec);
        }
        if (ec instanceof NodeContainer) {
            return nodeContainer.contains(ec);
        }
        if (ec instanceof EdgeContainer) {
            return edgeContainer.contains(ec);
        }
        return false;
    }

    /**
     * @return the showInterLayerConnections
     */
    public boolean isShowInterLayerConnections() {
        return showInterLayerConnections;
    }

    /**
     * (De-)Aktiviert das Anzeigen aller Interebenenbeziehungen
     *
     * @param showInterLayerConnections
     *            aktiviren / deaktivieren
     * @param doc
     *            aktives GraphDocument
     */
    public void setShowAllInterLayerConnections(final boolean showInterLayerConnections) {
        this.showInterLayerConnections = showInterLayerConnections;
        for (NodeContainer ec : nodeContainer) {
            setShowInterLayerConnections(showInterLayerConnections, ec);
        }
    }

    /**
     * (De-)Aktiviert das Anzeigen der Interebenenbeziehungen für den spezifizierten {@link ElementContainer}
     *
     * @param showInterLayerConnections
     *            aktiviren / deaktivieren
     * @param doc
     *            aktives GraphDocument
     * @param ec
     *            Container, dessen Interebenenbeziehungen (de-)aktiviert werden sollen
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
     * Funktioniert wie eine Map, bei der die Werte aber in einer Listenreihenfolge erhalten bleiben.
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
        System.err.println("Layer " + layerNumber + "    nodeContainer: " + nodeContainer.size() + " -> " + ReflectionUtils.getCommonSuperClass(nodeContainer));
    }

}