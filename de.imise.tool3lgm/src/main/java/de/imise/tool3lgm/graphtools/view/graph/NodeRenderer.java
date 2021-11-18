package de.imise.tool3lgm.graphtools.view.graph;

import static de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.FAT_STROKE;
import static de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.MEDUIM_STROKE;
import static de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.NOT_EXPANDED_BORDER_STROKE;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_LINKED_WITH_SUBMODEL_SYMBOLS;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.TRANSIENT_OPTION_SHOW_EXPANSION_SIGN;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition;
import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition.AdditionalGraphShapeData;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Textfield;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionIconTable;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea.PaintState;
import de.imise.util.swing.component.HtmlLabelFunctions;
import de.imise.util.swing.component.HtmlLabelFunctions.HtmlLabelDimension;

/**
 * Die Klasse zeichnet grafische Elemente Funktionen bereit, um für Punkte zu
 * entscheiden, ob er sich am Rand eines grafischen Elementes befindet und wenn
 * ja, an welcher Position.<br>
 */
public final class NodeRenderer {

    /**
     * Breite der Boxen, die auf die Ecken und die Mitte der Seitenlinien von
     * selektierten Elementen gezeichnet werden, über die man die Größe des
     * Elementes per Draggen auf dieser Box verändern kann. Diese Zahl sollte
     * immer ungrade sein! Die anderen Variablen darunter ergeben sich aus
     * dieser Breite und bestimmen, wo sich der Pfeil in der Box befindet und
     * wie dick er dargestellt wird.
     */
    private static final int RESIZE_BOX_WIDTH = 9;
    private static final int HALF_RESIZE_BOX_WIDTH = RESIZE_BOX_WIDTH / 2;
    private static final int QUARTER_RESIZE_BOX_WIDTH = RESIZE_BOX_WIDTH / 4;
    private static final int SIXTH_PART_OF_RESIZE_BOX_WIDTH = RESIZE_BOX_WIDTH / 6;

    /**
     * Farbe mit der die Umrandungen von Analysergebnissen dargestellt werden
     */
    private static Color analysisColor = null;

    private static int[] xs = new int[8];
    private static int[] ys = new int[8];
    private static int npoints = 0;

    public static Image linkIcon = Tool3lgmConstants.getIcon("link.gif").getImage();

    /////////////////////////////////////////
    //Funktionen zum Rendern eines Knotens //
    /////////////////////////////////////////

    /**
     * Sets the size of the given {@link NodeContainer} that it fits the text in
     * the container. This happens only, if the container has an layout with the
     * inital size (default size). The resulting width is the maximum of the
     * default width/height and the width/height that is needed to enclose the
     * text.
     *
     * @param nc
     */
    private static void resizeToFitTheLabel(final NodeContainer nc) {
        GraphElementLayout layout = nc.get3LGMLayout();
        if (layout != null) {
            if (nc.hasDefaultSize()) {
                Shape shape = getShape(nc);
                int labelInsets = shape.getLabelInsets();
                Font font = nc.getFont();
                String text = nc.getText();
                HtmlLabelDimension htmlLabelDimension = HtmlLabelFunctions.getHtmlLabelDimension(font, text, layout.width);
                int minWidth = htmlLabelDimension.minWidth + labelInsets;
                if (minWidth > layout.width) {
                    nc.setSize(minWidth, layout.height);
                }
                htmlLabelDimension = HtmlLabelFunctions.getHtmlLabelDimension(font, text, layout.width);
                int minHeight = htmlLabelDimension.preferredHeight + labelInsets;
                if (minHeight > layout.height) {
                    nc.setSize(layout.width, minHeight);
                }
                //                System.err.println(layout.width + " " + layout.height);
                //                Sys.err1(HtmlLabelFunctions.getHtmlLabelDimension(nc.getFont(), nc.getText(), nc.get3LGMLayout().width));
            }
        }
    }

    /**
     * @param nc
     * @return the graph shape for the {@link NodeContainer}
     */
    private static final Shape getShape(final NodeContainer nc) {
        Shape shape = nc.getForm();
        if (shape == null) {
            GraphDocument doc = nc.getGraphDocument();
            DefaultElementsLayoutDefinition defaultElementsLayout = doc.getDefaultElementsLayout();
            shape = defaultElementsLayout.getStandardForm(nc);
        }
        return shape;
    }

    /**
     * @param g
     * @param nc
     * @param doc
     */
    public static final void render(final Graphics g, final NodeContainer nc, final GraphDocument doc) {
        ModelElement me = nc.getElement();
        if (!me.isPaintable() || !nc.isVisible()) {
            return;
        }
        //resize the initial container to enclose the html text
        resizeToFitTheLabel(nc);
        Graphics2D gc = (Graphics2D) g;

        PaintState paintState = PaintState.REGULAR;
        Component parent = nc.getParent();
        if (parent instanceof LayerContainer) {
            paintState = ((LayerContainer) parent).getPaintState();
        }

        Color col = nc.getColor();
        if (col == null) {
            col = doc.getDefaultElementsLayout().getStandardBackGroundColor(nc);
        }

        int x = nc.getX();
        int y = nc.getY();
        int width = nc.getWidth();
        int width_half = width / 2;
        int height = nc.getHeight();
        int height_half = height / 2;
        int xm = x - width_half;
        int ym = y - height_half;
        int xp = x + width_half + width % 2;
        int yp = y + height_half;

        String iconID = nc.getIconID();
        ImageIcon img = setScaledIcon(iconID, nc);
        int textPositionHorizontalSwingConstant = nc.getTextPositionHorizontal().getSwingConstant();
        int textPositionVerticalSwingConstant = nc.getTextPositionVertical().getSwingConstant();
        int horizontalAlignment;
        int verticalAlignment;
        int horizontalTextPostion;
        int verticalTextPostion;
        if (img != null) {
            horizontalAlignment = SwingConstants.CENTER;
            verticalAlignment = SwingConstants.CENTER;
            horizontalTextPostion = textPositionHorizontalSwingConstant;
            verticalTextPostion = textPositionVerticalSwingConstant;
        } else {
            horizontalAlignment = textPositionHorizontalSwingConstant;
            verticalAlignment = textPositionVerticalSwingConstant;
            horizontalTextPostion = SwingConstants.CENTER;
            verticalTextPostion = SwingConstants.CENTER;
        }
        if (nc.getHorizontalAlignment() != horizontalAlignment) {
            nc.setHorizontalAlignment(horizontalAlignment);
        }
        if (nc.getVerticalAlignment() != verticalAlignment) {
            nc.setVerticalAlignment(verticalAlignment);
        }
        if (nc.getHorizontalTextPosition() != horizontalTextPostion) {
            nc.setHorizontalTextPosition(horizontalTextPostion);
        }
        if (nc.getVerticalTextPosition() != verticalTextPostion) {
            nc.setVerticalTextPosition(verticalTextPostion);
        }

        Shape form = getShape(nc);

        Stroke str = gc.getStroke();
        if (TRANSIENT_OPTION_SHOW_EXPANSION_SIGN.is() && !nc.isExpanded()) {
            gc.setStroke(NOT_EXPANDED_BORDER_STROKE);
        }
        boolean isResult = doc.isAnalysisResult(nc);
        if (isResult || nc.isHighLight()) {
            gc.setStroke(FAT_STROKE);
        } else if (doc.getLastSelectedGraphVisibleNodeOrBendpoint() == nc) {
            gc.setStroke(MEDUIM_STROKE);
        }

        if (me instanceof Textfield && img == null && (nc.get3LGMLayout() == null || nc.get3LGMLayout().bg_color == null)) {
            g.translate(xm, ym);
            nc.paintSuperComponent(g);
            g.translate(-xm, -ym);
        } else if (img == null || isResult) {
            if (form == null) {
                g.setColor(col);
                g.fillRect(xm, ym, width, height);
                g.translate(xm, ym);
                nc.paintSuperComponent(g);
                g.translate(-xm, -ym);
                g.setColor(isResult && analysisColor != null ? analysisColor : nc.getFrameColor());
                g.drawRect(xm, ym, width, height);
            } else {
                form.paint(g, nc, col, analysisColor, isResult, x, y, xm, ym, xp, yp, xs, ys, width, height, npoints);
            }
        } else /* if (img != null) */ {
            g.translate(xm, ym);
            nc.paintSuperComponent(g);
            g.translate(-xm, -ym);
        }

        //paint additionalShape
        Shape additionalGraphShape = getAdditionalGraphShape(me);
        if (additionalGraphShape != null) {
            //In the original GraphLayout nodes have a width of 90 and height of 60 and
            //subordinated elements like databases have a height and witdh of 15
            //so if scaling is activated the scale factor should be something like 90:15
            //or 60:15
            //int scalingFactor = (int) Math.ceil(Math.min(width, height) / 64);
            int scalingFactor = 1; //at the moment without scaling
            int addShapeLength = Math.max(8, 16 * scalingFactor);
            int addShapeX = x + width / 2 - addShapeLength * 3 / 2;
            int addShapeY = y + height / 2 - addShapeLength;
            int addShapeXm = addShapeX;
            int addShapeYm = addShapeY - addShapeLength / 2;
            int addShapeXp = addShapeX + addShapeLength;
            int addShapeYp = addShapeY + addShapeLength / 2;

            additionalGraphShape.paint(g, null, Color.YELLOW, Color.BLACK, isResult, addShapeX, addShapeY, addShapeXm, addShapeYm, addShapeXp, addShapeYp, xs, ys, addShapeLength, addShapeLength, npoints);

        }

        // Symbol für Verlinkung mit Teilmodell
        if (nc.getNode().getAssociatedSzenID() != null && OPTION_SHOW_LINKED_WITH_SUBMODEL_SYMBOLS.is()) {
            g.drawImage(linkIcon, xm + 2, yp - 13, nc);
        }

        gc.setStroke(str);

        //wenn das Element selektiert ist -> schwarzen Rand drumrum und die 8 Anfasser zum ändern der Größe zeichnen
        if (nc.isSelected() && paintState != PaintState.WEBEXPORT) {

            //scharzes Rechteck um das Element zeichnen
            g.setColor(Color.black);
            if (me instanceof Textfield) {
                NodeContainer lastSelectedGraphVisibleNodeOrBendpoint = doc.getLastSelectedGraphVisibleNodeOrBendpoint();
                if (lastSelectedGraphVisibleNodeOrBendpoint == nc) {
                    gc.setStroke(MEDUIM_STROKE);
                }
            }
            if (!nc.hideText() && img != null) {
                int prefferedHeight = nc.getPreferredSize().height;
                int iconHeight = img.getIconHeight();
                int offset = (prefferedHeight - iconHeight) / 2;
                int iconHeightHalf = iconHeight / 2;
                y -= offset;
                if (width < height) {
                    ym = y - iconHeightHalf;
                    yp = y + iconHeightHalf;
                } else {
                    ym -= offset;
                    yp -= offset;
                }
                nc.setHeight(iconHeight);
                g.drawRect(xm, ym, width, iconHeight);
            } else {
                g.drawRect(xm, ym, width, height);
            }

            //Stroke wieder zurück setzen, falls das zuletzt selktierte ein Textfeld war
            gc.setStroke(str);

            //8 schwarze Rechtecke zeichen, auf die die Pfeile gemalt werden
            g.fillRect(xm - HALF_RESIZE_BOX_WIDTH, ym - HALF_RESIZE_BOX_WIDTH, RESIZE_BOX_WIDTH, RESIZE_BOX_WIDTH);
            g.fillRect(xm - HALF_RESIZE_BOX_WIDTH, yp - HALF_RESIZE_BOX_WIDTH, RESIZE_BOX_WIDTH, RESIZE_BOX_WIDTH);
            g.fillRect(xp - HALF_RESIZE_BOX_WIDTH, yp - HALF_RESIZE_BOX_WIDTH, RESIZE_BOX_WIDTH, RESIZE_BOX_WIDTH);
            g.fillRect(xp - HALF_RESIZE_BOX_WIDTH, ym - HALF_RESIZE_BOX_WIDTH, RESIZE_BOX_WIDTH, RESIZE_BOX_WIDTH);
            g.fillRect(xm - HALF_RESIZE_BOX_WIDTH, y - HALF_RESIZE_BOX_WIDTH, RESIZE_BOX_WIDTH, RESIZE_BOX_WIDTH);
            g.fillRect(xp - HALF_RESIZE_BOX_WIDTH, y - HALF_RESIZE_BOX_WIDTH, RESIZE_BOX_WIDTH, RESIZE_BOX_WIDTH);
            g.fillRect(x - HALF_RESIZE_BOX_WIDTH, yp - HALF_RESIZE_BOX_WIDTH, RESIZE_BOX_WIDTH, RESIZE_BOX_WIDTH);
            g.fillRect(x - HALF_RESIZE_BOX_WIDTH, ym - HALF_RESIZE_BOX_WIDTH, RESIZE_BOX_WIDTH, RESIZE_BOX_WIDTH);

            //die 8 weissen Pfeile malen
            g.setColor(Color.white);
            gc.setStroke(new BasicStroke(SIXTH_PART_OF_RESIZE_BOX_WIDTH));

            xs[0] = xm + QUARTER_RESIZE_BOX_WIDTH;
            xs[1] = xm - QUARTER_RESIZE_BOX_WIDTH;
            xs[2] = xp + QUARTER_RESIZE_BOX_WIDTH;
            xs[3] = xp - QUARTER_RESIZE_BOX_WIDTH;

            ys[0] = ym + QUARTER_RESIZE_BOX_WIDTH;
            ys[1] = ym - QUARTER_RESIZE_BOX_WIDTH;
            ys[2] = yp + QUARTER_RESIZE_BOX_WIDTH;
            ys[3] = yp - QUARTER_RESIZE_BOX_WIDTH;

            g.drawLine(xs[0], y, xs[1], y);
            g.drawLine(xs[1], y, xm, y - QUARTER_RESIZE_BOX_WIDTH);
            g.drawLine(xs[1], y, xm, y + QUARTER_RESIZE_BOX_WIDTH);

            g.drawLine(x, ys[0], x, ys[1]);
            g.drawLine(x, ys[1], x - QUARTER_RESIZE_BOX_WIDTH, ym);
            g.drawLine(x, ys[1], x + QUARTER_RESIZE_BOX_WIDTH, ym);

            g.drawLine(xs[3], y, xs[2], y);
            g.drawLine(xs[2], y, xp, y - QUARTER_RESIZE_BOX_WIDTH);
            g.drawLine(xs[2], y, xp, y + QUARTER_RESIZE_BOX_WIDTH);

            g.drawLine(x, ys[3], x, ys[2]);
            g.drawLine(x, ys[2], x - QUARTER_RESIZE_BOX_WIDTH, yp);
            g.drawLine(x, ys[2], x + QUARTER_RESIZE_BOX_WIDTH, yp);

            g.drawLine(xs[0], ys[3], xs[1], ys[2]);
            g.drawLine(xs[1], ys[2], xm + 1, ys[2]);
            g.drawLine(xs[1], ys[2], xs[1], yp - 1);

            g.drawLine(xs[0], ys[0], xs[1], ys[1]);
            g.drawLine(xs[1], ys[1], xm + 1, ys[1]);
            g.drawLine(xs[1], ys[1], xs[1], ym + 1);

            g.drawLine(xs[3], ys[0], xs[2], ys[1]);
            g.drawLine(xs[2], ys[1], xp - 1, ys[1]);
            g.drawLine(xs[2], ys[1], xs[2], ym + 1);

            g.drawLine(xs[3], ys[3], xs[2], ys[2]);
            g.drawLine(xs[2], ys[2], xp - 1, ys[2]);
            g.drawLine(xs[2], ys[2], xs[2], yp - 1);

        }

        JLabel label = nc.getNorthLabel();

        if (label != null) {
            //          System.out.println(northLabel.getText() + "\nnorthLabel.height="+northLabel.getPreferredSize().height + " northLabel.width="+northLabel.getPreferredSize().width);
            int dy = ym - label.getPreferredSize().height - 1;
            g.translate(xm, dy);
            label.paint(g);
            g.translate(-xm, -dy);
        }
        label = nc.getEastLabel();
        if (label != null) {
            int dx = xm + width + 1;
            g.translate(dx, ym);
            label.paint(g);
            g.translate(-dx, -ym);
        }
        label = nc.getSouthLabel();
        if (label != null) {
            int dy = ym + height;
            g.translate(xm, dy);
            label.paint(g);
            g.translate(-xm, -dy);
        }
        label = nc.getWestLabel();
        if (label != null) {
            int dx = xm - label.getPreferredSize().width - 1;
            g.translate(dx, ym);
            label.paint(g);
            g.translate(-dx, -ym);
        }
        String[] additionalText = nc.getAdditionalTextRightDownLines();

        if (nc.getAdditionalTextRightDownLines() != null) {
            g.setColor(Color.black);
            Font font = nc.getFont();
            g.setFont(font);
            int fontHeight = font.getSize();
            for (int i = 0; i < additionalText.length;) {
                g.drawString(additionalText[i], xp, yp + fontHeight * ++i);
            }
        }
    }

    /**
     * @param iconID
     * @param nc
     * @return
     */
    private static ImageIcon setScaledIcon(final String iconID, final NodeContainer nc) {
        ImageIcon icon = null;
        if (iconID != null) {
            GDCollection gdcoll = nc.getCollection();
            GDCollectionIconTable iconTable = gdcoll.getIconTable();
            icon = iconTable.getIcon(iconID);
            Image image = icon.getImage();
            float iconWidth = icon.getIconWidth();
            float iconHeight = icon.getIconHeight();
            float containerWidth = nc.getWidth();
            float containerHeight = nc.getHeight();
            if (containerWidth != iconWidth || containerHeight != iconHeight) {
                containerWidth = (int) (iconWidth / iconHeight * containerHeight);
                image = image.getScaledInstance((int) containerWidth, (int) containerHeight, Image.SCALE_SMOOTH);
                icon = new ImageIcon(image);
                nc.setIcon(icon);
            }
        }
        return icon;
    }

    /**
     * @param nc
     * @return
     */
    private static Shape getAdditionalGraphShape(final ModelElement me) {
        MetaModel metaModel = me.getMetaModel();
        GraphViewDefinition graphViewDefinition = metaModel.getGraphViewDefinition();
        AdditionalGraphShapeData additionalGraphShapeData = graphViewDefinition.getAdditionalGraphShapeData(me);
        if (additionalGraphShapeData != null) {
            MetaPath metaPath = additionalGraphShapeData.metaPath;
            List<ModelElement> connectedElements = metaPath.getConnectedElements(me);
            if (!connectedElements.isEmpty()) {
                return additionalGraphShapeData.shape;
            }
        }
        return null;
    }

    /**
     * @param ec
     * @param xi
     * @param yi
     * @return
     */
    public static final boolean isInside(final ElementContainer ec, final double xi, final double yi) {
        return isInside(ec, xi, yi, false);
    }

    /**
     * Gibt <code>true</code> zurück, wenn die übergebenen Koordinaten innerhalb
     * des Darstellungsbereiches des übergebenen Containers liegen, sonst
     * <code>false</code>.
     *
     * @param ec <code>ElementContainer</code>, für den geprüft wird, ob die
     *            Koordinaten in ihm liegen
     * @param xi X-Koordinate
     * @param yi Y-Koordinate
     * @return
     */
    public static final boolean isInside(final ElementContainer ec, final double xi, final double yi, final boolean checkIcon) {
        if (ec == null || !(ec instanceof NodeContainer) || !ec.isVisible()) {
            return false;
        }

        NodeContainer k = (NodeContainer) ec;
        double x = k.getX();
        double y = k.getY();
        double width = k.getWidth();
        double height = k.getHeight();
        double prozent;
        double xd = Math.abs(xi - x);
        double yd = Math.abs(yi - y);

        // if the modelelement contains an icon, the selectable field for the element
        // will be moved to the icon
        if (checkIcon) {
            if (!k.hideText() && k.getIcon() != null) {
                int prefferedHeight = k.getPreferredSize().height;
                ImageIcon img = (ImageIcon) k.getIcon();
                int iconHeight = img.getIconHeight();
                int offset = (prefferedHeight - iconHeight) / 2;
                y -= offset;
                height = iconHeight;
                yd = Math.abs(yi - y);
            }
        }

        if (xd > width / 2.0 || yd > height / 2.0) {
            return false;
        }

        Shape form = k.getForm();
        if (form == null) {
            form = k.getGraphDocument().getDefaultElementsLayout().getStandardForm(k);
        }

        switch (form) {
        case rechteck:
            return true;
        case rhombus:
            prozent = 1.01 - 2.0 * (xd / width); // 100% = Zentrum, 0% = am Rand
            if (yd > Math.round(prozent * height / 2.0)) {
                return false;
            }
            return true;

        case dreieck:
            prozent = 1.00 - 2.0 * (xd / width); // 100% = Zentrum, 0% = am Rand
            if (Math.abs(y + height / 2.0 - yi) > Math.round(prozent * height)) {
                return false;
            }
            return true;

        case oval:
            prozent = width / height;
            xd = Math.round(xd / prozent);
            if (Math.sqrt(xd * xd + yd * yd) > height / 2.0) {
                return false;
            }
            return true;

        case tonne:
            if (yd < height / 4.0) {
                return true; // wenn man mitten rein
            }
            prozent = height / 2.0 / width; // faktor fuer oben&unten
            xd = Math.round(xd * prozent);

            yd = Math.abs(y - height / 4.0 - yi);
            if (Math.sqrt(xd * xd + yd * yd) <= height / 4.0) {
                return true;
            }
            yd = Math.abs(y + height / 4.0 - yi);
            if (Math.sqrt(xd * xd + yd * yd) <= height / 4.0) {
                return true;
            }
            return false;

        case wabe:
            if (xd < width / 2.0) {
                return true;
            }
            return false;

        default:
            return true;
        }
    }

    /**
     * Letzte ausgewählte ResizeBox. Hier werden die Int-Werte der
     * korrespondierenden Cursor benutzt.
     */
    private static int lastResizeCursor = Cursor.DEFAULT_CURSOR;

    /**
     * @return lastResizeCursor
     */
    public static final int getLastResizeCursor() {
        return lastResizeCursor;
    }

    /**
     * Liefert den <code>int</code>-Code für Position, welche sich auf dem
     * Container an den übergebenen Koordinaten befindet.<br>
     * <p>
     * Rückgabewerte:
     * <ul>
     * <li><code>Cursor.DEFAULT_CURSOR</code>: Koordinaten sind ausserhalb eines
     * der anderen speziellen Grenzbereiche</li>
     * <li><code>Cursor.N_RESIZE_CURSOR</code>: Koordinaten sind im oberen
     * Grenzbereich</li>
     * <li><code>Cursor.NE_RESIZE_CURSOR</code>: Koordinaten sind im oberen
     * rechten Grenzbereich</li>
     * <li><code>Cursor.E_RESIZE_CURSOR</code>: Koordinaten sind im rechten
     * Grenzbereich</li>
     * <li><code>Cursor.SE_RESIZE_CURSOR</code>: Koordinaten sind im unteren
     * rechten Grenzbereich</li>
     * <li><code>Cursor.S_RESIZE_CURSOR</code>: Koordinaten sind im unteren
     * Grenzbereich</li>
     * <li><code>Cursor.SW_RESIZE_CURSOR</code>: Koordinaten sind im unteren
     * linken Grenzbereich</li>
     * <li><code>Cursor.W_RESIZE_CURSOR</code>: Koordinaten sind im linken
     * Grenzbereich</li>
     * <li><code>Cursor.NW_RESIZE_CURSOR</code>: Koordinaten sind im oberen
     * linken Grenzbereich</li>
     * </ul>
     * </p>
     *
     * @param container Container, für den Grenzbereich ermittelt werden soll
     * @param xi X-Koordinate
     * @param yi Y-Koordinate
     * @return <code>int</code>-Code des Grenzbereiches
     */
    public static final int getResizeCursor(final NodeContainer container, final int xi, final int yi) {
        if (!container.getElement().isPaintable()) {
            return Cursor.DEFAULT_CURSOR;
        }

        int x = container.getX();
        int y = container.getY();
        int half_width = container.getWidth() / 2;
        int half_height = container.getHeight() / 2;

        if (!container.hideText() && container.getIcon() != null) {
            int prefferedHeight = container.getPreferredSize().height;
            ImageIcon img = (ImageIcon) container.getIcon();
            int iconHeight = img.getIconHeight();
            int offset = (prefferedHeight - iconHeight) / 2;
            y -= offset;
            half_height = iconHeight / 2;
        }

        lastResizeCursor = Cursor.DEFAULT_CURSOR;

        if (xi >= x - half_width - HALF_RESIZE_BOX_WIDTH && xi <= x - half_width + HALF_RESIZE_BOX_WIDTH && yi >= y - HALF_RESIZE_BOX_WIDTH && yi <= y + HALF_RESIZE_BOX_WIDTH) {
            lastResizeCursor = Cursor.W_RESIZE_CURSOR;
        } else if (xi >= x - HALF_RESIZE_BOX_WIDTH && xi <= x + HALF_RESIZE_BOX_WIDTH && yi >= y - half_height - HALF_RESIZE_BOX_WIDTH && yi <= y - half_height + HALF_RESIZE_BOX_WIDTH) {
            lastResizeCursor = Cursor.N_RESIZE_CURSOR;
        } else if (xi >= x + half_width - HALF_RESIZE_BOX_WIDTH && xi <= x + half_width + HALF_RESIZE_BOX_WIDTH && yi >= y - HALF_RESIZE_BOX_WIDTH && yi <= y + HALF_RESIZE_BOX_WIDTH) {
            lastResizeCursor = Cursor.E_RESIZE_CURSOR;
        } else if (xi >= x - HALF_RESIZE_BOX_WIDTH && xi <= x + HALF_RESIZE_BOX_WIDTH && yi >= y + half_height - HALF_RESIZE_BOX_WIDTH && yi <= y + half_height + HALF_RESIZE_BOX_WIDTH) {
            lastResizeCursor = Cursor.S_RESIZE_CURSOR;
        } else if (xi >= x - half_width - HALF_RESIZE_BOX_WIDTH && xi <= x - half_width + HALF_RESIZE_BOX_WIDTH && yi >= y + half_height - HALF_RESIZE_BOX_WIDTH && yi <= y + half_height + HALF_RESIZE_BOX_WIDTH) {
            lastResizeCursor = Cursor.SW_RESIZE_CURSOR;
        } else if (xi >= x - half_width - HALF_RESIZE_BOX_WIDTH && xi <= x - half_width + HALF_RESIZE_BOX_WIDTH && yi >= y - half_height - HALF_RESIZE_BOX_WIDTH && yi <= y - half_height + HALF_RESIZE_BOX_WIDTH) {
            lastResizeCursor = Cursor.NW_RESIZE_CURSOR;
        } else if (xi >= x + half_width - HALF_RESIZE_BOX_WIDTH && xi <= x + half_width + HALF_RESIZE_BOX_WIDTH && yi >= y - half_height - HALF_RESIZE_BOX_WIDTH && yi <= y - half_height + HALF_RESIZE_BOX_WIDTH) {
            lastResizeCursor = Cursor.NE_RESIZE_CURSOR;
        } else if (xi >= x + half_width - HALF_RESIZE_BOX_WIDTH && xi <= x + half_width + HALF_RESIZE_BOX_WIDTH && yi >= y + half_height - HALF_RESIZE_BOX_WIDTH && yi <= y + half_height + HALF_RESIZE_BOX_WIDTH) {
            lastResizeCursor = Cursor.SE_RESIZE_CURSOR;
        }

        return lastResizeCursor; // Wenn es gar nicht anders geklappt hat
    }

    //  private static final int INVALID_ADAPTED_BORDER_POSITION_VALUE = Integer.MAX_VALUE;
    //
    //  private int adaptedBorderPositionX = INVALID_ADAPTED_BORDER_POSITION_VALUE;
    //  private int adaptedBorderPositionY = INVALID_ADAPTED_BORDER_POSITION_VALUE;
    //
    //  private D
    //
    //  public ResizeBox

    /**
     * @param c
     */
    public static void setAnalysisColor(final Color c) {
        analysisColor = c;
    }

    /**
     * @return
     */
    public static Color getAnalysisColor() {
        return analysisColor == null ? Color.black : analysisColor;
    }

}
