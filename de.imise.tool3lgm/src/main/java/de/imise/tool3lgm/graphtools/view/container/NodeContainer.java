package de.imise.tool3lgm.graphtools.view.container;

import static de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.STANDARD_COLORS;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.NodeRenderer;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.log.Log;
import de.imise.util.Alphabetical.AlphabeticalSortTarget;
import de.imise.util.swing.NoopGraphics;

/**
 * @author N.N.
 * @create Very long time ago
 */
public class NodeContainer extends ElementContainer implements AlphabeticalSortTarget {

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

    /** Prefix for invisible graph elements in the tree */
    private static final String HIDDEN_CONTAINER_TREE_STRING_PREFIX = Tool3lgmConstants.getResString("ausgebl") + " ";

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
     * String, der unten links neben den Node geschrieben werden (z.B. an
     * Aufgaben und Objekttypen Redundanzfaktoren...)
     */
    private String[] additionalTextRightDownLines;

    /**
     * COMMENTME
     */
    private String additionalTextRightDown;

    /**
     * COMMENTME
     */
    protected ElementContainerTreeNode treeNode = null;

    /**
     *
     */
    public NodeContainer() {
        init();
    }

    /**
     * @param node
     * @param doc
     */
    public NodeContainer(final Node node, final GraphDocument doc) {
        super(node, doc);
        init();
    }

    /**
     * @param node
     * @param l
     * @param doc
     */
    public NodeContainer(final Node node, final GraphElementLayout l, final GraphDocument doc) {
        super(node, l, doc);
        init();
    }

    /**
     * @param nc
     * @param doc
     */
    public NodeContainer(final NodeContainer nc, final GraphDocument doc) {
        super(nc, doc);
        init();
    }

    /**
     *
     */
    private void init() {
        if (doc == null) {
            return;
        }
        MetaModel metaModel = doc.getMetaModel();
        if (metaModel.hasOrderedEdgeClassesToPaintable(me.getClass())) {
            //			registerAsGraphDocumentListener();
            if (layout == null) {
                layout = new GraphElementLayout(me);
            }
            if (layout.getFont() == null) {
                layout.setFont(new Font(GraphElementLayout.STANDARD_FONT_NAME, GraphElementLayout.STANDARD_FONT_STYLE, GraphElementLayout.STANDARD_FONT_SIZE));
            }
            if (layout.bg_color == null) {
                lastColor = STANDARD_COLORS[0];
                layout.bg_color = lastColor;
            }
            Image image = Static.getMainFrame().createImage(14, 14);
            Graphics g = image.getGraphics();
            g.setColor(layout.bg_color);
            g.fillRect(0, 0, 14, 14);
            treeIcon = new ImageIcon(image);
            //			specialInfoTargets = new ArrayList<>();
            //			specialInfoTargets.addAll(me.getConnectedContainer(Aufgabe.class, doc, null, Doppelkante.FORWARD, false));
        }

        if (me != null) {
            linkWithSzenario(((Node) me).getAssociatedSzenID() != null);
        }
    }

    @Override
    public final ElementContainer clone(final boolean cloneModelElement, final GraphDocument _doc) {
        NodeContainer retVal = (NodeContainer) super.clone(cloneModelElement, _doc);
        if (retVal != null) {
            retVal.init();
        }
        return retVal;
    }

    /**
     * Überträgt die Layout-Eigenschaften dieses Containes auf den übergebenen
     *
     * @param targetContainer
     */
    @Override
    public final void adaptLayout(final ElementContainer targetContainer) {
        super.adaptLayout(targetContainer);
        if (targetContainer instanceof NodeContainer) {
            ((NodeContainer) targetContainer).init();
        }
    }

    /**
     * Setzt die Koordinaten des Knotens.
     *
     * @see java.awt.Component#setLocation(int, int)
     */
    @Override
    public final void setLocation(final int x, final int y) {
        if (layout != null) {
            layout.x = x;
            layout.y = y;
        }
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
        return layout == null ? 0 : layout.x;
    }

    /**
     * Gibt die Y-Koordinate des Mittelpunktes zurueck
     *
     * @see javax.swing.JComponent#getY()
     */
    @Override
    public int getY() {
        return layout == null ? 0 : layout.y;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    /**
     * @param iconID
     * @param ImageTable
     */
    public void setIcon(final String iconID, final Map<String, byte[]> ImageTable) {
        if (layout == null) {
            return;
        }
        if (iconID == null || iconID.trim().equals("") || iconID.equals("none") || iconID.equals("null")) {
            layout.setIconID(null);
            super.setIcon(null);
            return;
        }
        ImageIcon icon = null;
        try {
            byte[] imageData = ImageTable.get(iconID);
            icon = new ImageIcon(imageData);
        } catch (Exception ex) {
            Log.show(Log.ERROR, getResString("FehlerAllgemein"), ex);
            JOptionPane.showMessageDialog(null, getResString("icon_kaputt"), getResString("fehler"), JOptionPane.ERROR_MESSAGE);
        }
        layout.setIconID(icon != null ? iconID : null);
        super.setIcon(icon);
    }

    /**
     * @return
     */
    public String getIconID() {
        return layout == null ? null : layout.getIconID();
    }

    /**
     * Gibt die Breite zurueck
     *
     * @see javax.swing.JComponent#getWidth()
     */
    @Override
    public int getWidth() {
        return layout == null ? 0 : layout.width;
    }

    /**
     * Gibt die Hoehe zurueck
     *
     * @see javax.swing.JComponent#getHeight()
     */
    @Override
    public int getHeight() {
        return layout == null ? 0 : layout.height;
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
        if (layout == null) {
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
        if (layout == null) {
            return;
        }
        layout.width = w;
        layout.height = h;
        refreshText();
    }

    //	public final void setSymmetric(boolean b) {
    //        ((Node)me).setSymmetric(b);
    //        if (((Node)me).Symmetric()) layout.height=layout.width;
    //		refreshText();
    //	}

    /**
     * @return
     */
    public Node getNode() {
        return (Node) me;
    }

    @Override
    public void set3LGMLayout(final GraphElementLayout l) {
        if (!me.hasLayout()) {
            return;
        }
        super.set3LGMLayout(l);
        setIcon(l.getIconID(), doc.getCollection().getIconTable());
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
     * This {@link Graphics} object comes from StackOverflow. It doesn't paint
     * anything. Painting to this non painting graphics just before painting to
     * the correct graphics object repairs the HTML SWING-BUG that after the
     * first paint the alignment of the HTML-labels are always bottom, if they
     * must be wrapped automatically to multiple. This bug seems to be created
     * by wrong font metrics in the very first paint of such a wrapped HTML
     * string.
     *
     * @see https://stackoverflow.com/questions/16227877/how-to-update-a-jcomponent-with-html-without-flickering
     */
    private static final Graphics NOOP_GRAPHICS = NoopGraphics.createNoopGraphics();

    /**
     * Paints the label.
     *
     * @param g
     */
    public final void paintSuperComponent(final Graphics g) {
        //first 'paint' wihtout really painting to correct the alignment of wrapped HTML lines
        //this painting to NOOP_GRAPHICS is a lot faster than painting to g
        super.paintComponent(NOOP_GRAPHICS);
        super.paintComponent(g);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        NodeRenderer.render(g, this, doc);
    }

    /**
     * @param node
     */
    public void setTreeNode(final ElementContainerTreeNode node) {
        treeNode = node;
    }

    /**
     * @return
     */
    public ElementContainerTreeNode getTreeNode() {
        return treeNode;
    }

    /**
     *
     */
    public void setIcon() {
        if (layout != null) {
            setIcon(layout.getIconID(), doc.getCollection().getIconTable());
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

    public boolean hideText() {
        return layout.width < 35 && layout.height < 30;
    }

    @Override
    public void refreshText() {
        if (layout == null) {
            return;
        }
        if (hideText()) {
            setText(null);
        } else {
            String htmlName = getHTMLName();
            setText(htmlName);
        }
    }

    @Override
    public final String toString() {
        String nameWithSzens = me.hasSzenarioContainer() ? me.getNameWithSzens() : me.toString();
        if (me.isPaintable() && !isVisible() && doc instanceof Szenario) {
            nameWithSzens = HIDDEN_CONTAINER_TREE_STRING_PREFIX + nameWithSzens;
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

    @Override
    public String getSecondSortString() {
        return getID();
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
        if (Strings.isNullOrEmpty(string)) {
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
