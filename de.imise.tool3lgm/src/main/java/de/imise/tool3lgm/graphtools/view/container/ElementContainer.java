package de.imise.tool3lgm.graphtools.view.container;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getSortedEdgeClasses;
import static de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.STANDARD_FONT;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.graph.ElementsLayoutDefinition;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.SpecialInfoLabel;

public abstract class ElementContainer extends JLabel implements Cloneable {

    /**
     * COMMENTME
     */
    protected int highlight = 0;

    /**
     * Diese Labels werden (wenn sie Text enthalten) an der jeweiligen Position beim Container dargestellt
     */
    protected SpecialInfoLabel northLabel, eastLabel, southLabel, westLabel;

    /**
     * COMMENTME
     */
    protected GraphDocument doc;

    /**
     * COMMENTME
     */
    protected ModelElement me;

    /**
     * COMMENTME
     */
    protected Container containerParent;

    /**
     * COMMENTME
     */
    protected GraphElementLayout layout = null, expandedLayout = null, nonExpandedLayout = null;

    /**
     * gibt an, ob dieses Element durch das Aufklappen seines (ggf. existierenden)
     * übergeordneten Elements sichtbar gemacht wurde
     */
    protected boolean expanded = true;

    /**
     * COMMENTME
     */
    protected Color frameColor = null;

    /**
     * COMMENTME
     */
    protected ImageIcon treeIcon = null;

    /**
     * COMMENTME
     */
    protected Color lastColor = null;

    /**
     *
     */
    public ElementContainer() {
        super();
        listenerList = null;
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
        setHorizontalTextPosition(CENTER);
        setVerticalTextPosition(BOTTOM);
    }

    /**
     * Wenn dieser Container an andere in der Grafik zusätzliche Infos schreiben möchte, dann muss er dieses Generator instanziieren.
     */
    protected AdditionalLabelTextGenerator additionalLabelTextGenerator = null;

    /**
     * @param me
     * @param doc
     */
    public ElementContainer(final ModelElement me, final GraphDocument doc) {
        this();
        this.me = me;
        if (doc != null) {
            if (!me.isUnique()) {
                this.doc = doc;
                me.setContainer(doc, this);
            } else {
                GDCollection gdcoll = doc.getCollection();
                this.doc = gdcoll.getMainGraphDocument();
                me.setContainer(this.doc, this);
            }
            if (me.hasLayout()) {
                expandedLayout = new GraphElementLayout();
                layout = expandedLayout;
                setFont(layout.getFont());
                frameColor = new Color(0, 0, 0, 255);
            }
        }
    }

    /**
     * @param ec
     * @param doc
     */
    public ElementContainer(final ElementContainer ec, final GraphDocument doc) {
        this(ec.getElement(), doc);
        boolean exp = ec.isExpanded();
        if (ec.getE3LGMLayout() != null) {
            expandedLayout = (GraphElementLayout) ec.getE3LGMLayout().clone();
        }
        if (ec.getNE3LGMLayout() != null) {
            nonExpandedLayout = (GraphElementLayout) ec.getNE3LGMLayout().clone();
        }
        if (exp) {
            layout = expandedLayout;
        } else {
            layout = nonExpandedLayout;
        }
        ec.setExpanded(exp);
    }

    /**
     * @param neu
     * @param layout
     * @param gd
     */
    public ElementContainer(final ModelElement neu, final GraphElementLayout layout, final GraphDocument gd) {
        this(neu, gd);
        expandedLayout = layout;
        this.layout = expandedLayout;
    }

    /**
     * @param parent
     */
    public void setParent(final Container parent) {
        if (containerParent != null) {
            if (containerParent != parent) {
                containerParent.remove(this);
            }
        }
        containerParent = parent;
    }

    @Override
    public Container getParent() {
        return containerParent;
    }

    /**
     * @param cloneModelElement
     * @param doc
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public ElementContainer clone(final boolean cloneModelElement, final GraphDocument doc) {
        ElementContainer retVal;
        try {
            retVal = getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Static.showErrorOutputDialog(getResString("FehlerAllgemein"), e);
            return null;
        }
        retVal.doc = doc;
        retVal.me = cloneModelElement ? (ModelElement) me.clone() : me;
        retVal.me.setContainer(retVal.doc, retVal);
        retVal.setVisible(isVisible());
        retVal.expanded = expanded;
        retVal.highlight = highlight;
        retVal.layout = (GraphElementLayout) (layout == null ? null : layout.clone());
        retVal.expandedLayout = (GraphElementLayout) (expandedLayout == null ? null : expandedLayout.clone());
        retVal.nonExpandedLayout = (GraphElementLayout) (nonExpandedLayout == null ? null : nonExpandedLayout.clone());
        retVal.set3LGMLayout(retVal.expanded ? retVal.expandedLayout : retVal.nonExpandedLayout);
        if (frameColor != null) {
            retVal.frameColor = new Color(frameColor.getRed(), frameColor.getGreen(), frameColor.getBlue(), frameColor.getAlpha());
        }
        return retVal;
    }

    /**
     * @return
     */
    public GraphDocument getGraphDocument() {
        return doc;
    }

    /**
     * @param me
     */
    public void setElement(final ModelElement me) {
        this.me = me;
        me.setContainer(doc, this);
    }

    /**
     * @return
     */
    public ModelElement getElement() {
        return me;
    }

    /**
     * @param layout
     */
    public void set3LGMLayout(final GraphElementLayout layout) {
        if (me.hasLayout() && layout != null) {
            this.layout = layout;
        }
    }

    /**
     * @param layout
     */
    public void setE3LGMLayout(final GraphElementLayout layout) {
        if (me.hasLayout()) {
            expandedLayout = layout;
        }
    }

    /**
     * @param layout
     */
    public void setNE3LGMLayout(final GraphElementLayout layout) {
        if (me.hasLayout()) {
            nonExpandedLayout = layout;
        }
    }

    /**
     * @return
     */
    public GraphElementLayout get3LGMLayout() {
        return layout;
    }

    /**
     * @return
     */
    public GraphElementLayout getE3LGMLayout() {
        return expandedLayout;
    }

    /**
     * @return
     */
    public GraphElementLayout getNE3LGMLayout() {
        return nonExpandedLayout;
    }

    /**
     * legt fest, ob dieses Element durch das Aufklappen seines (ggf. existierenden)
     * übergeordneten Elements sichtbar gemacht wurde
     */
    public void setExpanded(final boolean expanded) {
        if (expanded == this.expanded) {
            return;
        }
        if (this.expanded) {
            expandedLayout = layout;
        } else {
            nonExpandedLayout = layout;
        }
        this.expanded = expanded;
        if (this.expanded) {
            if (expandedLayout == null && nonExpandedLayout != null) {
                expandedLayout = (GraphElementLayout) nonExpandedLayout.clone();
            }
            layout = expandedLayout;
        } else {
            if (nonExpandedLayout == null && expandedLayout != null) {
                nonExpandedLayout = (GraphElementLayout) expandedLayout.clone();
            }
            layout = nonExpandedLayout;
        }
        for (Edge edge : me.getEdges()) {
            ElementContainer kc = edge.getContainer(doc);
            if (kc == null) {
                continue;
            }
            ((EdgeContainer) kc).computeBorderPoints();
            kc.repaint();
        }
    }

    @Override
    public final void setVisible(final boolean visible) {
        super.setVisible(visible);
        if (visible) {
            Set<Class<? extends Edge>> sortedEdgeClasses = getSortedEdgeClasses(me.getClass());
            if (sortedEdgeClasses != null) {
                for (Class<? extends Edge> edgeClass : sortedEdgeClasses) {
                    if (additionalLabelTextGenerator == null) {
                        additionalLabelTextGenerator = new AdditionalLabelTextGenerator(this, get3LGMLayout());
                    }
                    additionalLabelTextGenerator.writeNumberListToTargets(me.getConnectedElementsByEdge(edgeClass), doc);
                }
            }
        } else if (additionalLabelTextGenerator != null) {
            additionalLabelTextGenerator.deleteSpecialInfoFromTargets();
        }
        for (Class<? extends ModelElement> c : ModelConstants.getSlaveElementTypes(me.getClass())) {
            for (ElementContainer sC : me.getConnectedContainer(c, doc)) {
                sC.setVisible(visible);
            }
        }
    }

    /**
     * COMMENTME
     */
    static boolean paintingSurrogates = false;

    /**
     * @return
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * Erzeugt die angezeigte Bezeichnung. Für Node werden in eckigen Klammern durch Kommas getrennt
     * alle Szenarien aufgelistet, in denen sie ausser im momentan angezeigten noch vorkommen.
     *
     * @see java.awt.Component#toString()
     */
    @Override
    public String toString() {
        return me.isPaintable() && isVisible() && doc instanceof Szenario ? me.toString() : getResString("ausgebl") + " " + me.toString();
    }

    /* -------- GraphElementLayout - Funktionen aus dem ModelElement -------------- */

    /**
     * @return
     */
    public final String getFontName() {
        if (layout != null && layout.getFont() != null) {
            return layout.getFont().getName();
        }
        return null;
    }

    /**
     * @return
     */
    public final int getFontSize() {
        if (layout != null && layout.getFont() != null) {
            return layout.getFont().getSize();
        }
        return -1;
    }

    /**
     * @return
     */
    public final int getFontStyle() {
        if (layout != null && layout.getFont() != null) {
            return layout.getFont().getStyle();
        }
        return -1;
    }

    /**
     * @return
     */
    public final boolean hasStandardFont() {
        return layout == null || layout.getFont() == null; // || layout.font.equals(doc.getMapping().getStandardFont(me));
    }

    /**
     * @return
     */
    public final boolean isStandardFont(final Font font) {
        if (font == null) {
            return true;
        }
        return font.equals(doc.getMapping().getStandardFont(this));
    }

    /**
     *
     */
    public final void resetLayout() {
        if (layout != null) {
            layout.reset();
        }
        setFont(layout.getFont());
    }

    @Override
    public final void setFont(final Font font) {
        if (layout == null) {
            layout = new GraphElementLayout();
        }
        layout.setFont(font);
        super.setFont(font);
        //		addSpecialInfoToMyTargets(true);
    }

    @Override
    public final Font getFont() {
        if (layout != null && layout.getFont() != null) {
            return layout.getFont();
        }
        Font font = null;
        if (doc != null) {
            ElementsLayoutDefinition mapping = doc.getMapping();
            if (mapping != null) {
                font = mapping.getStandardFont(this);
            }
        }
        if (font == null) {
            font = STANDARD_FONT;
        }
        return font;
    }

    /**
     * Gibt die Form zurueck
     */
    public final GraphElementLayout.SHAPE getForm() {
        return layout == null ? null : layout.form;
    }

    /**
     * Setzt die Form
     *
     * @param form
     */
    public final void setForm(final GraphElementLayout.SHAPE form) {
        if (layout != null) {
            layout.form = form;
        }
    }

    /**
     * Setzt die Linienstaerke
     *
     * @param c
     */
    public final void setStrokeWidth(final int c) {
        if (layout == null) {
            return;
        }
        if (c < 6 && c > 0) {
            layout.line_thickness = c;
        }
    }

    /**
     * Gibt den Linienstil zurueck
     */
    public final int getLineStyle() {
        if (layout != null) {
            return layout.line_style;
        }
        return 0;
    }

    /**
     * Setzt den Linienstil
     *
     * @param c
     */
    public final void setLineStyle(final int c) {
        if (layout == null) {
            return;
        }
        if (c <= 1 && c >= 0) {
            layout.line_style = c;
        }
    }

    /**
     * Gibt die Linienstaerke zurueck
     */
    public final int getStrokeWidth() {
        if (layout != null) {
            return layout.line_thickness;
        }
        return 1;
    }

    /**
     * Setzt die Farbe des Objektes
     *
     * @param c
     */
    public final void setColor(final Color c) {
        if (layout == null) {
            return;
        }
        if (c != null) {
            layout.bg_color = new Color(c.getRed(), c.getGreen(), c.getBlue(), layout.bg_color != null ? layout.bg_color.getAlpha() : 255);
        } else {
            layout.bg_color = null;
        }
        //		addSpecialInfoToMyTargets(true);
    }

    /**
     * Gibt die Farbe zurueck
     */
    public final Color getColor() {
        if (layout != null) {
            return layout.bg_color;
        }
        return null;
    }

    /**
     * @return
     */
    public final Color getFrameColor() {
        //beim Einlesen werden die einzelnen layout-Werte einfach direkt gesetzt. Daher kann der Alphawert der Rahmensfarbe erst beim
        //Abfragen gesetzt werden
        if (frameColor != null && layout.bg_color != null && frameColor.getAlpha() != layout.bg_color.getAlpha()) {
            frameColor = new Color(frameColor.getRed(), frameColor.getGreen(), frameColor.getBlue(), layout.bg_color.getAlpha());
        }
        return frameColor;
    }

    /**
     * Setzt die Transparenz des Objektes
     *
     * @param alpha
     */
    public final void setAlpha(int alpha) {
        if (layout == null) {
            return;
        }
        if (layout.bg_color == null) {
            return;
        }
        if (alpha < 0) {
            alpha = 0;
        } else if (alpha > 255) {
            alpha = 255;
        }
        if (alpha >= 0 && alpha < 256) {
            layout.bg_color = new Color(layout.bg_color.getRed(), layout.bg_color.getGreen(), layout.bg_color.getBlue(), alpha);
        }
        frameColor = new Color(frameColor.getRed(), frameColor.getGreen(), frameColor.getBlue(), alpha);
    }

    /**
     * Gibt die Transparenz zurueck
     */
    public final int getAlpha() {
        if (layout != null) {
            return layout.bg_color != null ? layout.bg_color.getAlpha() : 255;
        }
        return 255;
    }

    /**
     * @return
     */
    public final int getValign() {
        if (layout != null) {
            return layout.valign;
        }
        return CENTER;
    }

    /**
     * @return
     */
    public final int getHalign() {
        if (layout != null) {
            return layout.halign;
        }
        return CENTER;
    }

    /**
     * @return
     */
    public final String getHashString() {
        return me.getHashString();
    }

    /**
     * @param _hashstring
     */
    public final void setHashString(final String _hashstring) {
        me.setHashString(_hashstring);
    }

    /**
     * @return
     */
    public final boolean isSelected() {
        return doc.isSelected(this);
    }

    /**
     *
     */
    public void refreshFont() {
        super.setFont(getFont());
    }

    @Override
    public final void setText(final String text) {
        if (me == null) {
            return;//System.err.println("me ist null \" " + text + "\"");
            //		} else if (me.isUnpaintable()) {
            //			System.err.println("me ist unpaintable \"" + text + "\" " + me.getClass().getSimpleName() + " " + me + " " + doc);
            //		} else if (layout == null) {
            //			System.err.println("layout ist null \"" + text + "\" " + me.getClass().getSimpleName() + " " + me + " " + doc);
        } else if (text != null && getWidth() < 35 && getHeight() < 30) {
            System.err.println("Element zu klein \"" + text + "\" " + me.getClass().getSimpleName() + " " + me + " " + doc);
        }

        super.setText(text);
    }

    /**
     * COMMENTME
     */
    static StringBuilder suffixBuf = new StringBuilder("");

    /**
     * COMMENTME
     */
    static StringBuilder textBuf = new StringBuilder("");

    /**
     *
     */
    public abstract void refreshText();

    /**
     * @param initialContainer
     * @return
     */
    public final List<ElementContainer> getSurrogateContainer() {
        return getSurrogateContainer(this);
    }

    /**
     * @param initialContainer
     * @return
     */
    private List<ElementContainer> getSurrogateContainer(final ElementContainer initialContainer) {
        List<ElementContainer> retVal = new ArrayList<>(1);
        if (isVisible()) {
            retVal.add(this);
            return retVal;
        }
        for (Edge edge : me.getEdges()) {
            if (edge instanceof CompositionEdge) {
                CompositionEdge comp = (CompositionEdge) edge;
                if (comp.getSlave() == me) {
                    ElementContainer ec = comp.getMaster().getContainer(initialContainer.getGraphDocument());
                    if (ec != null) {
                        retVal.addAll(ec.getSurrogateContainer(initialContainer));
                    }
                }
            }
        }
        if (retVal.size() > 0) {
            return retVal;
        }
        List<ElementContainer> all = me.getDirectParentContainer(doc);
        if (all.size() == 0) {
            retVal.add(this);
            return retVal;
        }
        for (ElementContainer ec : all) {
            if (ec == initialContainer) {
                retVal.add(this);
                continue;
            }
            if (ec.isVisible() && ec.isExpanded()) {
                retVal.add(this);
                return retVal;
            }
            retVal.addAll(ec.getSurrogateContainer(initialContainer));
        }
        return retVal;
    }

    /**
     * @param b
     */
    public void setHighLight(final boolean b) {
        if (b) {
            highlight++;
        } else if (highlight > 0) {
            highlight--;
            //		System.out.println(highlight + "\t" +me);
        }
    }

    /**
     * @return
     */
    public boolean isHighLight() {
        return highlight > 0;
    }

    /**
     * @return
     */
    public ImageIcon getTreeIcon() {
        return treeIcon;
    }

    /**
     * @param icon
     */
    public void setTreeIcon(final ImageIcon icon) {
        treeIcon = icon;
    }

    /**
     *
     */
    public void checkTreeIcon() {
        if (lastColor == null || !lastColor.equals(layout.bg_color)) {
            lastColor = layout.bg_color;
            Image image = Static.getMainFrame().createImage(14, 14);
            Graphics g = image.getGraphics();
            g.setColor(lastColor);
            g.fillRect(0, 0, 14, 14);
            treeIcon = new ImageIcon(image);
        }
    }

    /**
     * @return
     */
    public int layerFor() {
        return me.layerFor();
    }

    /**
     * @return
     */
    public LayerContainer getMyLayerContainer() {
        return doc.getLayer(layerFor());
    }

    //	##########################################################################################################
    //	 BEGINN Funktionen für Container als SpecialInfoTargets /////////////////////////////////////////////////////
    /**
     * @param ElementContainer infoOwner
     * @param info String
     * @param int preferredPosition
     */
    public void addSpecialInfoToThisContainer(final AdditionalLabelTextGenerator infoOwner, final String info, final int preferredPosition) {
        addSpecialInfoToThisContainer(infoOwner, info, preferredPosition, false);
    }

    /**
     * @param infoOwner
     * @param info
     */
    public void addSpecialInfoToThisContainer(final AdditionalLabelTextGenerator infoOwner, final String info) {
        addSpecialInfoToThisContainer(infoOwner, info, SwingConstants.NORTH, false);
    }

    /**
     * @param infoOwner
     * @param info
     * @param addInNewLine
     */
    public void addSpecialInfoToThisContainer(final AdditionalLabelTextGenerator infoOwner, final String info, final boolean addInNewLine) {
        addSpecialInfoToThisContainer(infoOwner, info, SwingConstants.NORTH, addInNewLine);
    }

    /**
     * @param infoOwner
     * @param info
     * @param preferredPosition
     * @param addInNewLine
     */
    public void addSpecialInfoToThisContainer(final AdditionalLabelTextGenerator infoOwner, final String info, final int preferredPosition, final boolean addInNewLine) {
        if (!me.isPaintable()) {
            return;
        }
        if (infoOwner == null) {
            return;
        }

        switch (preferredPosition) {
        case EAST:
            if (eastLabel == null) {
                eastLabel = new SpecialInfoLabel(infoOwner, info, addInNewLine);
            } else {
                eastLabel.add(infoOwner, info, addInNewLine);
            }
            break;
        case SOUTH:
            if (southLabel == null) {
                southLabel = new SpecialInfoLabel(infoOwner, info, addInNewLine);
            } else {
                southLabel.add(infoOwner, info, addInNewLine);
            }
            break;
        case WEST:
            if (westLabel == null) {
                westLabel = new SpecialInfoLabel(infoOwner, info, addInNewLine);
            } else {
                westLabel.add(infoOwner, info, addInNewLine);
            }
            break;
        default:
            if (northLabel == null) {
                northLabel = new SpecialInfoLabel(infoOwner, info, addInNewLine);
            } else {
                northLabel.add(infoOwner, info, addInNewLine);
            }
            break;
        }
    }

    /**
     * @param infoOwner
     */
    public void removeSpecialInfoFromThisContainer(final ElementContainer infoOwner) {
        removeSpecialInfoFromThisContainer(infoOwner, -1);
    }

    /**
     *
     */
    public void removeAllSpecialInfosFromThisContainer() {
        removeSpecialInfoFromThisContainer(null, -1);
    }

    /**
     * @param infoOwner
     * @param preferredPosition
     */
    public void removeSpecialInfoFromThisContainer(final Object infoOwner, final int preferredPosition) {
        if (!me.isPaintable()) {
            return;
        }

        //		System.out.println("removeSpecialInfoFromThisContainer from "+infoOwner + " from " + me);
        switch (preferredPosition) {
        case NORTH:
            if (northLabel != null) {
                northLabel.removeSpecialInfoParent(infoOwner);
                if (northLabel.getSpecialInfoOwnerAndTextSize() == 0) {
                    northLabel = null;
                }
            }
            break;
        case EAST:
            if (eastLabel != null) {
                eastLabel.removeSpecialInfoParent(infoOwner);
                if (eastLabel.getSpecialInfoOwnerAndTextSize() == 0) {
                    eastLabel = null;
                }
            }
            break;
        case SOUTH:
            if (southLabel != null) {
                southLabel.removeSpecialInfoParent(infoOwner);
                if (southLabel.getSpecialInfoOwnerAndTextSize() == 0) {
                    southLabel = null;
                }
            }
            break;
        case WEST:
            if (westLabel != null) {
                westLabel.removeSpecialInfoParent(infoOwner);
                if (westLabel.getSpecialInfoOwnerAndTextSize() == 0) {
                    westLabel = null;
                }
            }
            break;
        default:
            removeSpecialInfoFromThisContainer(infoOwner, GraphElementLayout.NORTH);
            removeSpecialInfoFromThisContainer(infoOwner, GraphElementLayout.WEST);
            removeSpecialInfoFromThisContainer(infoOwner, GraphElementLayout.EAST);
            removeSpecialInfoFromThisContainer(infoOwner, GraphElementLayout.SOUTH);
        }
    }

    /**
     * @return Returns the eastLabel.
     */
    public SpecialInfoLabel getEastLabel() {
        return eastLabel;
    }

    /**
     * @return Returns the northLabel.
     */
    public SpecialInfoLabel getNorthLabel() {
        return northLabel;
    }

    /**
     * @return Returns the southLabel.
     */
    public SpecialInfoLabel getSouthLabel() {
        return southLabel;
    }

    /**
     * @return Returns the westLabel.
     */
    public SpecialInfoLabel getWestLabel() {
        return westLabel;
    }

    //	 ENDE Funktionen für Container als SpecialInfoTargets ///////////////////////////////////////////////////////
    //	##########################################################################################################

}