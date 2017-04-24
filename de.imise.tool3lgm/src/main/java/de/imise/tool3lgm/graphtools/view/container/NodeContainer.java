package de.imise.tool3lgm.graphtools.view.container;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.Szenario;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.node.Prozess;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.KnotenRenderer;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.tools.LGMTreeNode;

/**
 * @author N.N.
 * @create Very long time ago
 */
public class NodeContainer extends ElementContainer/* implements GraphDocumentListener, InTransactionListener */ {

    /**
     * COMMENTME
     */
    public static final int MIN_X_SIZE = 15;
    /**
     * COMMENTME
     */
    public static final int MIN_Y_SIZE = 15;

    /**
     * COMMENTME
     */
    public static final int MAX_X_SIZE = 2000;

    /**
     * COMMENTME
     */
    public static final int MAX_Y_SIZE = 2000;

    //dieser Wert steht für alles Mögliche zur Verfügung (daher der unspezifische Name)
    //Momentane Verwenung:
    //	- AufgabenContainer:	Anzahl ihrer redundanten Konfigs
    //	- ObjekttypContainer:	Anzahl der DBS und Dokumentensammlungen, in denen sie redundant gespeichert werden
    private int variable;

    /**
     * COMMENTME
     */
    private JLabel linkLabel = null;

    //
    /**
     * String, der unten links neben den Knoten geschrieben werden
     * (z.B. an Aufgaben und Objekttypen Redundanzfaktoren...)
     */
    private String[] additionalTextRightDownLines;

    /**
     * COMMENTME
     */
    private String additionalTextRightDown;

    /**
     * COMMENTME
     */
    protected LGMTreeNode treeNode = null;

    /**
     *
     */
    public NodeContainer() {
        super();
        init();
    }

    /**
     * @param neu
     * @param gd
     */
    public NodeContainer(final Knoten neu, final GraphDocument gd) {
        super(neu, gd);
        init();
    }

    /**
     * @param neu
     * @param l
     * @param gd
     */
    public NodeContainer(final Knoten neu, final GraphElementLayout l, final GraphDocument gd) {
        super(neu, l, gd);
        init();
    }

    /**
     * @param alt
     * @param gd
     */
    public NodeContainer(final NodeContainer alt, final GraphDocument gd) {
        super(alt, gd);
        init();
    }

    /**
     *
     */
    private void init() {
        if (doc == null) {
            return;
        }
        if (me instanceof Prozess) {
            //			registerAsGraphDocumentListener();
            if (layout == null) {
                layout = new GraphElementLayout();
            }
            if (layout.getFont() == null) {
                layout.setFont(new Font(GraphElementLayout.STANDARD_FONT_NAME, GraphElementLayout.STANDARD_FONT_STYLE, GraphElementLayout.STANDARD_FONT_SIZE));
            }
            if (layout.bg_color == null) {
                lastColor = Prozess.farben[((Prozess) me).color];
                layout.bg_color = lastColor;
            }
            Image image = Static.getMainFrame().createImage(14, 14);
            Graphics g = image.getGraphics();
            g.setColor(layout.bg_color);
            g.fillRect(0, 0, 14, 14);
            treeIcon = new ImageIcon(image);
            //			specialInfoTargets = new ArrayList<ElementContainer>();
            //			specialInfoTargets.addAll(me.getConnectedContainer(Aufgabe.class, doc, null, Doppelkante.FORWARD, false));
        }

        if (me != null) {
            linkWithSzenario(((Knoten) me).getAssociatedDoc() != null);
        }
    }

    @Override
    public ElementContainer clone(final boolean cloneModelElement, final GraphDocument _doc) {
        NodeContainer retVal;
        try {
            retVal = (NodeContainer) super.clone(cloneModelElement, _doc);
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            return null;
        }
        retVal.init();
        return retVal;
    }

    /**
     * Setzt die Koordinaten des Knotens.
     *
     * @see java.awt.Component#setLocation(int, int)
     */
    @Override
    public final void setLocation(final int x, final int y) {
        if (me.isUnpaintable()) {
            return;
        }
        layout.x = x;
        layout.y = y;
    }

    /**
     * Setzt die Koordinaten des Knotens.
     *
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public final void setCoordinates(final int x, final int y, final int w, final int h) {
        if (layout == null) {
            return;
        }
        setLocation(x, y);
        setSize(w, h);
    }

    /**
     * Gibt die X-Koordinate des Mittelpunktes zurueck
     *
     * @see javax.swing.JComponent#getX()
     */
    @Override
    public int getX() {
        if (me.isUnpaintable()) {
            return 0;
            //		if (me instanceof AufOrgKombination) {
            //			AbstractInternalFrame frame = Tool3lgm.tool.getActiveFrame();
            //			if (!(frame instanceof ToolInternalFrame))
            //				return 0;
            //			GraphDocument doc = frame.getGraphDocument();
            //			if (doc != null) {
            //				ArrayList<ElementContainer> aufg = me.getConnectedContainer(Aufgabe.class, doc);
            //				if (aufg.size() > 0)
            //					return ((NodeContainer) aufg.get(0)).getX();
            //			}
            //			return 0;
            //		}
        }

        return layout.x;
    }

    /**
     * Gibt die Y-Koordinate des Mittelpunktes zurueck
     *
     * @see javax.swing.JComponent#getY()
     */
    @Override
    public int getY() {
        if (me.isUnpaintable()) {
            return 0;
            //		if (me instanceof AufOrgKombination) {
            //			AbstractInternalFrame frame = Tool3lgm.tool.getActiveFrame();
            //			if (!(frame instanceof ToolInternalFrame))
            //				return 0;
            //			GraphDocument doc = frame.getGraphDocument();
            //			if (doc != null) {
            //				ArrayList<ElementContainer> aufg = me.getConnectedContainer(Aufgabe.class, doc);
            //				if (aufg.size() > 0)
            //					return ((NodeContainer) aufg.get(0)).getY();
            //			}
            //			return 0;
            //		}
        }

        return layout.y;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    /**
     * @param name
     * @param ImageTable
     */
    public void setIcon(final String name, final Hashtable<String, byte[]> ImageTable) {
        if (me.isUnpaintable()) {
            return;
        }
        if (name == null || name.trim().equals("") || name.equals("none") || name.equals("null")) {
            layout.icon = null;
            super.setIcon(null);
            return;
        }
        ImageIcon icon = null;
        try {
            icon = new ImageIcon(ImageTable.get(name));
        } catch (Exception ex) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), ex);
            JOptionPane.showMessageDialog(null, Tool3lgmConstants.getResString("icon_kaputt"), Tool3lgmConstants.getResString("fehler"), JOptionPane.ERROR_MESSAGE);
        }
        if (icon != null) {
            layout.icon = name;
        } else {
            layout.icon = null;
        }
        super.setIcon(icon);
    }

    /**
     * @return
     */
    public String getIconString() {
        if (me.isUnpaintable()) {
            return null;
        }
        return layout.icon;
    }

    /**
     * Gibt die Breite zurueck
     *
     * @see javax.swing.JComponent#getWidth()
     */
    @Override
    public int getWidth() {
        if (me.isUnpaintable()) {
            return 0;
        }
        return layout.width;
    }

    /**
     * Gibt die Hoehe zurueck
     *
     * @see javax.swing.JComponent#getHeight()
     */
    @Override
    public int getHeight() {
        if (me.isUnpaintable()) {
            return 0;
        }
        return layout.height;
    }

    /**
     * Setzt die Breite des Objektes, bei Symmetrie auch die Hoehe.
     *
     * @param w
     */
    public final void setWidth(final int w) {
        setSize(w, getHeight());
    }

    /**
     * Setzt die Hoehe des Objektes, bei Symmetrie auch die Breite.
     *
     * @param h
     */
    public final void setHeight(final int h) {
        setSize(getWidth(), h);
    }

    /**
     * Setzt die Groesse des Knotens. Bei Symmetrie geht die Breite vor.
     *
     * @see java.awt.Component#setSize(int, int)
     */
    @Override
    public final void setSize(final int w, final int h) {
        //		System.err.println(w + " " + h);
        if (me.isUnpaintable()) {
            return;
        }
        if (w >= MIN_X_SIZE && w <= MAX_X_SIZE) {
            layout.width = w;
        }
        if (h >= MIN_Y_SIZE && h <= MAX_Y_SIZE) {
            layout.height = h;
        }
        super.setSize(layout.width, layout.height);
        //Die Beschriftung muss aktualisiert werden, falls die Elementgröße die Minimalgröße überschreitet ab der die Beschriftung
        //dargestellt werden soll bzw. unterschreitet, aber der die Beschriftung nicht mehr dargestellt werden soll
        refreshText();
    }

    /**
     * Setzt die Groesse des Knotens für die Buttons
     *
     * @param w
     * @param h
     */
    public final void setSizeForButtons(final int w, final int h) {
        if (me.isUnpaintable()) {
            return;
        }
        layout.width = w;
        layout.height = h;
        refreshText();
    }

    //	public final void setSymmetric(boolean b) {
    //        ((Knoten)me).setSymmetric(b);
    //        if (((Knoten)me).Symmetric()) layout.height=layout.width;
    //		refreshText();
    //	}

    /**
     * @return
     */
    public Knoten getKnoten() {
        return (Knoten) me;
    }

    @Override
    public void set3LGMLayout(final GraphElementLayout l) {
        if (!me.hasLayout()) {
            return;
        }
        super.set3LGMLayout(l);
        setIcon(l.icon, doc.getCollection().getIconTable());
    }

    /**
     * @return
     */
    public boolean hasParent() {
        return me.hasDirectParentContainer(doc);
    }

    /**
     * @param doc
     * @return
     */
    public boolean hasParent(final GraphDocument doc) {
        return me.hasDirectParentContainer(doc);
    }

    /**
     * @return
     */
    public boolean hasPart() {
        return me.hasDirectPartContainer(doc);
    }

    @Override
    public boolean isVisible() {
        boolean retVal = true;
        //		if (! UserProperties.isShowUnusedInterfaces()) {
        //			if (me.getClass() == Bausteinschnittstelle.class) {
        //				ArrayList<ElementContainer> elements = me.getConnectedContainer(Bausteinschnittstelle.class, doc);
        //				retVal = ((elements != null) && (elements.size() > 0));
        //			}
        //		}

        return retVal && super.isVisible();
    }

    /**
     * Verannlasst das Zeichen des Containers über die übergeordnete Implementierung.
     *
     * @param g
     */
    public final void paintSuperComponent(final Graphics g) {
        super.paintComponent(g);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        KnotenRenderer.render(g, this, doc);
    }

    /**
     * @param node
     */
    public void setTreeNode(final LGMTreeNode node) {
        treeNode = node;
    }

    /**
     * @return
     */
    public LGMTreeNode getTreeNode() {
        return treeNode;
    }

    /**
     *
     */
    public void setIcon() {
        if (layout != null) {
            setIcon(layout.icon, doc.getCollection().getIconTable());
        }
    }

    @Override
    public void setExpanded(final boolean exp) {
        super.setExpanded(exp);
        setIcon();
        refreshText();
    }

    //### Beginn GraphDocumentListener #######################################################################################

    //
    //	public void dataChanged(GraphDocument source,int pid){
    //		dataChanged(source);
    //	}
    //
    //	public void dataChanged(GraphDocument source){
    //		if ((me instanceof Prozess) && (isVisible()) && (doc instanceof Szenario)){
    //			deleteSpecialInfoFromMyTargets();
    //			specialInfoTargets = me.getConnectedContainer(Aufgabe.class,doc,PrzAufVerbindung.class,Doppelkante.FORWARD, false);
    //			addSpecialInfoToMyTargets(false);
    //		}
    //	}
    //
    //	public void elementGraphicsChanged(GraphDocument source, ElementContainer element){
    ////		deleteSpecialInfoFromMyTargets();
    ////		addSpecialInfoToMyTargets(true);
    //	}
    //	public void layoutChanged(GraphDocument source){}
    //	public void elementAdded(GraphDocument source, ElementContainer element){}
    //	public void elementDeleted(GraphDocument source, ElementContainer element){}
    //	public void groupOrderChanged(GraphDocument source){}
    //	public void activeLayerChanged(GraphDocument source){}
    //	public void colorsChanged(GraphDocument source){}
    //	public void selectionChanged(GraphDocument source){}
    //
    //
    //	public void registerAsGraphDocumentListener () {
    //		doc.addGraphDocumentListener(this);
    //	}
    //
    //	public void deregisterAsGraphDocumentListener () {
    //		doc.removeGraphDocumentListener(this);
    //	}

    //### Ende GraphDocumentListener #######################################################################################

    /**
     * @param linked
     */
    public void linkWithSzenario(final boolean linked) {
        if (linked) {
            if (linkLabel == null) {
                linkLabel = new JLabel(Tool3lgmConstants.getIcon("link.gif"));
                //				setIcon(Tool3lgmConstants.getIcon("link.bmp"));
                add(linkLabel);
            }
        } else if (linkLabel != null) {
            remove(linkLabel);
            linkLabel = null;
        }
    }

    @Override
    public void refreshText() {
        if (me.isUnpaintable()) {
            return;
        }
        if (layout == null) {
            return;
        }

        if (layout.width < 35 && layout.height < 30) {
            setText(null);
        } else {
            setText(me.getHTMLName());
        }
    }

    @Override
    public String toString() {
        String nameWithSzens = me.getNameWithSzens();
        if (!me.isUnpaintable() && !isVisible() && doc instanceof Szenario) {
            nameWithSzens = Tool3lgmConstants.getResString("ausgebl") + " " + nameWithSzens;
        }
        if (additionalTextRightDownLines == null) {
            return nameWithSzens;
        }

        StringBuilder sb = new StringBuilder(nameWithSzens);
        sb.append("      (");
        sb.append(additionalTextRightDown);
        sb.append(")");
        return sb.toString();
    }

    /**
     * @return Returns the additionalTextRightDownLines.
     */
    public String[] getAdditionalTextRightDownLines() {
        return additionalTextRightDownLines;
    }

    /**
     * @return Returns the additionalTextRightDown.
     */
    public String getAdditionalTextDown() {
        return additionalTextRightDown;
    }

    public void setAdditionalTextRightDown(final String string) {
        if (string == null) {
            additionalTextRightDown = null;
            additionalTextRightDownLines = null;
            return;
        }
        StringBuilder sb = new StringBuilder(string);
        //alle Zeilenumbrüche auf ein einfaches '\n' setzen, also '\r' einfach löschen
        for (int i = sb.length(); i > 1;) {
            if (sb.charAt(--i) == '\r') {
                sb.delete(i, i + 1);
            }
        }
        additionalTextRightDown = sb.toString();
        StringTokenizer st = new StringTokenizer(additionalTextRightDown, "\n", false);
        int i = st.countTokens();
        if (additionalTextRightDownLines == null || additionalTextRightDownLines.length != i) {
            additionalTextRightDownLines = new String[i];
        }
        i = 0;
        while (st.hasMoreTokens()) {
            additionalTextRightDownLines[i++] = st.nextToken();
        }
    }

    /**
     * @return
     */
    public int getVariable() {
        return variable;
    }

    /**
     * @param f
     */
    public void setVariable(final int f) {
        variable = f;
    }

}
