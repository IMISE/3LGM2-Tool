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

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Textfield;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea.PaintState;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.SHAPE;
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
    public static Color analysisColor = null;

    protected static int[] xs = new int[8];
    protected static int[] ys = new int[8];
    protected static int npoints = 0;

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
     * @param kc
     */
    private static void resize(final NodeContainer kc) {
        GraphElementLayout layout = kc.get3LGMLayout();
        if (layout != null) {
            if (kc.hasDefaultSize()) {
                Font font = kc.getFont();
                String text = kc.getText();
                HtmlLabelDimension htmlLabelDimension = HtmlLabelFunctions.getHtmlLabelDimension(font, text, layout.width);
                if (htmlLabelDimension.minWidth > layout.width) {
                    kc.setSize(htmlLabelDimension.minWidth, layout.height);
                }
                htmlLabelDimension = HtmlLabelFunctions.getHtmlLabelDimension(font, text, layout.width);
                if (htmlLabelDimension.preferredHeight > layout.height) {
                    kc.setSize(layout.width, htmlLabelDimension.preferredHeight);
                }
                //                System.err.println(layout.width + " " + layout.height);
                //                Sys.err1(HtmlLabelFunctions.getHtmlLabelDimension(kc.getFont(), kc.getText(), kc.get3LGMLayout().width));
            }
        }
    }

    public static final void render(final Graphics g, final NodeContainer kc, final GraphDocument doc) {
        ModelElement me = kc.getElement();
        if (!me.isPaintable() || !kc.isVisible()) {
            return;
        }
        //resize the initial container to enclose the html text
        resize(kc);
        Graphics2D gc = (Graphics2D) g;

        PaintState paintState = PaintState.REGULAR;
        Component parent = kc.getParent();
        if (parent instanceof LayerContainer) {
            paintState = ((LayerContainer) parent).getPaintState();
        }

        Color col = kc.getColor();
        if (col == null) {
            col = doc.getMapping().getStandardBackGroundColor(kc);
        }

        int x = kc.getX();
        int y = kc.getY();
        int width = kc.getWidth();
        int width_half = width / 2;
        int width_third = width / 3;
        int height = kc.getHeight();
        int height_half = height / 2;
        int xm = x - width_half;
        int ym = y - height_half;
        int xp = x + width_half + width % 2;
        int yp = y + height_half;

        ImageIcon img = (ImageIcon) kc.getIcon();
        int textPositionHorizontalSwingConstant = kc.getTextPositionHorizontal().getSwingConstant();
        int textPositionVerticalSwingConstant = kc.getTextPositionVertical().getSwingConstant();
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
        if (kc.getHorizontalAlignment() != horizontalAlignment) {
            kc.setHorizontalAlignment(horizontalAlignment);
        }
        if (kc.getVerticalAlignment() != verticalAlignment) {
            kc.setVerticalAlignment(verticalAlignment);
        }
        if (kc.getHorizontalTextPosition() != horizontalTextPostion) {
            kc.setHorizontalTextPosition(horizontalTextPostion);
        }
        if (kc.getVerticalTextPosition() != verticalTextPostion) {
            kc.setVerticalTextPosition(verticalTextPostion);
        }

        GraphElementLayout.SHAPE form = kc.getForm();
        if (form == null) {
            form = doc.getMapping().getStandardForm(kc);
        }

        Stroke str = gc.getStroke();
        if (TRANSIENT_OPTION_SHOW_EXPANSION_SIGN.is() && !kc.isExpanded()) {
            gc.setStroke(NOT_EXPANDED_BORDER_STROKE);
        }
        boolean isResult = doc.isAnalysisResult(kc);
        if (isResult || kc.isHighLight()) {
            gc.setStroke(FAT_STROKE);
        } else if (doc.getLastSelectedGraphVisibleNodeOrBendpoint() == kc) {
            gc.setStroke(MEDUIM_STROKE);
        }

        if (me instanceof Textfield && img == null && (kc.get3LGMLayout() == null || kc.get3LGMLayout().bg_color == null)) {
            g.translate(xm, ym);
            kc.paintSuperComponent(g);
            g.translate(-xm, -ym);
        } else if (img == null || isResult) {
            if (form == null) {
                g.setColor(col);
                g.fillRect(xm, ym, width, height);
                g.translate(xm, ym);
                kc.paintSuperComponent(g);
                g.translate(-xm, -ym);
                g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
                g.drawRect(xm, ym, width, height);
            } else {
                form.paint(g, kc, col, analysisColor, isResult, x, y, xm, ym, xp, yp, xs, ys, width, height, npoints);
            }
        } else /* if (img != null) */ {
            g.translate(xm, ym);
            kc.paintSuperComponent(g);
            g.translate(-xm, -ym);
        }

        SHAPE additionalGraphShape = me.getAdditionalGraphShape();
        if (additionalGraphShape != null) {
            //male additionalShape
            additionalGraphShape.paint(g, kc, col, analysisColor, isResult, x, y, xm, ym, xp, yp, xs, ys, width, height, npoints);
            //            Sys.err1(me + "   ->   " + additionalGraphShape);
        }

        // Symbol für Verlinkung mit Teilmodell
        if (kc.getNode().getAssociatedSzenID() != null && OPTION_SHOW_LINKED_WITH_SUBMODEL_SYMBOLS.is()) {
            g.drawImage(linkIcon, xm + 2, yp - 13, kc);
        }

        gc.setStroke(str);

        //wenn das Element selektiert ist -> schwarzen Rand drumrum und die 8 Anfasser zum ändern der Größe zeichnen
        if (kc.isSelected() && paintState != PaintState.WEBEXPORT) {

            //scharzes Rechteck um das Element zeichnen
            g.setColor(Color.black);
            if (me instanceof Textfield) {
                NodeContainer lastSelectedGraphVisibleNodeOrBendpoint = doc.getLastSelectedGraphVisibleNodeOrBendpoint();
                if (lastSelectedGraphVisibleNodeOrBendpoint == kc) {
                    gc.setStroke(MEDUIM_STROKE);
                }
            }
            g.drawRect(xm, ym, width, height);

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

        JLabel label = kc.getNorthLabel();

        if (label != null) {
            //          System.out.println(northLabel.getText() + "\nnorthLabel.height="+northLabel.getPreferredSize().height + " northLabel.width="+northLabel.getPreferredSize().width);
            int dy = ym - label.getPreferredSize().height - 1;
            g.translate(xm, dy);
            label.paint(g);
            g.translate(-xm, -dy);
        }
        label = kc.getEastLabel();
        if (label != null) {
            int dx = xm + width + 1;
            g.translate(dx, ym);
            label.paint(g);
            g.translate(-dx, -ym);
        }
        label = kc.getSouthLabel();
        if (label != null) {
            int dy = ym + height;
            g.translate(xm, dy);
            label.paint(g);
            g.translate(-xm, -dy);
        }
        label = kc.getWestLabel();
        if (label != null) {
            int dx = xm - label.getPreferredSize().width - 1;
            g.translate(dx, ym);
            label.paint(g);
            g.translate(-dx, -ym);
        }
        String[] additionalText = kc.getAdditionalTextRightDownLines();

        if (kc.getAdditionalTextRightDownLines() != null) {
            g.setColor(Color.black);
            Font font = kc.getFont();
            g.setFont(font);
            int fontHeight = font.getSize();
            for (int i = 0; i < additionalText.length;) {
                g.drawString(additionalText[i], xp, yp + fontHeight * ++i);
            }
        }
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
    public static final boolean isInside(final ElementContainer ec, final double xi, final double yi) {
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

        if (xd > width / 2.0 || yd > height / 2.0) {
            return false;
        }

        GraphElementLayout.SHAPE form = k.getForm();
        if (form == null) {
            form = k.getGraphDocument().getMapping().getStandardForm(k);
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

}
