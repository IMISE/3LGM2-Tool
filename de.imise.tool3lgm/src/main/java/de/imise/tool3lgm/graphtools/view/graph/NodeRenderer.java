package de.imise.tool3lgm.graphtools.view.graph;

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

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.Textfeld;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea.PaintState;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * Die Klasse zeichnet grafische Elemente Funktionen bereit, um für Punkte zu entscheiden, ob er sich
 * am Rand eines grafischen Elementes befindet und wenn ja, an welcher Position.<br>
 */
public final class NodeRenderer {

    /**
     * Breite der Boxen, die auf die Ecken und die Mitte der Seitenlinien von selektierten
     * Elementen gezeichnet werden, über die man die Größe des Elementes per Draggen auf dieser
     * Box verändern kann. Diese Zahl sollte immer ungrade sein!
     * Die anderen Variablen darunter ergeben sich aus dieser Breite und bestimmen, wo sich der
     * Pfeil in der Box befindet und wie dick er dargestellt wird.
     */
    private static final int RESIZE_BOX_WIDTH = 9;
    private static final int HALF_RESIZE_BOX_WIDTH = RESIZE_BOX_WIDTH / 2;
    private static final int QUARTER_RESIZE_BOX_WIDTH = RESIZE_BOX_WIDTH / 4;
    private static final int SIXTH_PART_OF_RESIZE_BOX_WIDTH = RESIZE_BOX_WIDTH / 6;

    /** Farbe mit der die Umrandungen von Analysergebnissen dargestellt werden */
    public static Color analysisColor = null;

    protected static Stroke fatStroke = new BasicStroke(7);
    protected static Stroke meduimStroke = new BasicStroke(4);
    protected static Stroke neStroke = new BasicStroke(4, BasicStroke.JOIN_BEVEL, BasicStroke.CAP_BUTT, 1, new float[] {
        10
    }, 10);

    protected static int[] xs = new int[8];
    protected static int[] ys = new int[8];
    protected static int npoints = 0;

    public static Image linkIcon = Tool3lgmConstants.getIcon("link.gif").getImage();

    /////////////////////////////////////////
    //Funktionen zum Rendern eines Knotens //
    /////////////////////////////////////////

    public static final void render(final Graphics g, final NodeContainer kc, final GraphDocument doc) {
        if (kc.isUnpaintable() || !kc.isVisible()) {
            return;
        }

        Graphics2D gc = (Graphics2D) g;

        PaintState paintState = PaintState.REGULAR;
        Component parent = kc.getParent();
        if (parent instanceof LayerContainer) {
            paintState = ((LayerContainer) parent).getPaintState();
        }

        Color col = kc.getColor();
        if (col == null) {
            col = doc.getMapping().getStandardBackGroundColor(kc.getKnoten());
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
        if (img != null) {
        }

        GraphElementLayout.SHAPE form = kc.getForm();
        if (form == null) {
            form = doc.getMapping().getStandardForm(kc.getKnoten());
        }

        if (kc.getVerticalAlignment() != kc.getValign()) {
            kc.setVerticalAlignment(kc.getValign());
        }
        if (kc.getHorizontalAlignment() != kc.getHalign()) {
            kc.setHorizontalAlignment(kc.getHalign());
        }

        Stroke str = gc.getStroke();
        if (UserProperties.isShowExpansionSign() && !kc.isExpanded()) {
            gc.setStroke(neStroke);
        }
        boolean isResult = doc.isAnalysisResult(kc);
        if (isResult || kc.isHighLight()) {
            gc.setStroke(fatStroke);
        }

        if (kc.getElement() instanceof Textfeld && img == null && (kc.get3LGMLayout() == null || kc.get3LGMLayout().bg_color == null)) {
            g.translate(xm, ym);
            kc.paintSuperComponent(g);
            g.translate(-xm, -ym);
        } else if (img == null || isResult) {
            switch (form) {
            case rechteck:
                g.setColor(col);
                g.fillRect(xm, ym, width, height);
                g.translate(xm, ym);
                kc.paintSuperComponent(g);
                g.translate(-xm, -ym);
                g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
                g.drawRect(xm, ym, width, height);
                break;

            case dreieck:
                xs[0] = xm;
                xs[1] = x;
                xs[2] = xp;
                ys[0] = ym;
                ys[1] = yp;
                ys[2] = ym;
                npoints = 3;
                g.setColor(col);
                g.fillPolygon(xs, ys, npoints);
                g.translate(xm, ym);
                kc.paintSuperComponent(g);
                g.translate(-xm, -ym);
                g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
                g.drawPolygon(xs, ys, npoints);
                break;
            case oval:
                g.setColor(col);
                g.fillOval(xm, ym, width, height);
                g.translate(xm, ym);
                kc.paintSuperComponent(g);
                g.translate(-xm, -ym);
                g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
                g.drawOval(xm, ym, width, height);
                break;

            case rundeck:
                g.setColor(col);
                g.fillRoundRect(xm, ym, width, height, width / 4, height / 4);
                g.translate(xm, ym);
                kc.paintSuperComponent(g);
                g.translate(-xm, -ym);
                g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
                g.drawRoundRect(xm, ym, width, height, width / 4, height / 4);
                break;

            case rhombus:
                xs[0] = xm;
                xs[1] = x;
                xs[2] = xp;
                xs[3] = x;
                ys[0] = y;
                ys[1] = yp;
                ys[2] = y;
                ys[3] = ym;
                npoints = 4;
                g.setColor(col);
                g.fillPolygon(xs, ys, npoints);
                g.translate(xm, ym);
                kc.paintSuperComponent(g);
                g.translate(-xm, -ym);
                g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
                g.drawPolygon(xs, ys, npoints);
                break;

            case wabe:
                xs[0] = xm;
                xs[1] = x - width / 3;
                xs[2] = x + width / 3;
                xs[3] = xp;
                xs[4] = x + width / 3;
                xs[5] = x - width / 3;

                ys[0] = y;
                ys[1] = yp;
                ys[2] = yp;
                ys[3] = y;
                ys[4] = ym;
                ys[5] = ym;
                npoints = 6;

                g.setColor(col);
                g.fillPolygon(xs, ys, npoints);
                g.translate(xm, ym);
                kc.paintSuperComponent(g);
                g.translate(-xm, -ym);
                g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
                g.drawPolygon(xs, ys, npoints);
                break;

            case tonne:
                g.setColor(col);
                g.fillArc(xm, ym, width, height_half + 1, 180, -180);
                g.fillArc(xm, y, width, height_half, 180, 180);
                g.fillRect(xm, y - height / 4, width, height_half);
                g.translate(xm, ym);
                kc.paintSuperComponent(g);
                g.translate(-xm, -ym);
                g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
                g.drawArc(xm, y, width, height_half, 180, 180);
                g.drawLine(xm, y - height / 4, xm, y + height / 4);
                g.drawLine(xp, y - height / 4, xp, y + height / 4);
                g.drawOval(xm, ym, width, height_half);
                break;

            case ordner:
                g.setColor(col);
                g.fillRect(xm, ym, width_third, height);
                g.fillRect(xm + width_third, ym, width_third, height);
                g.fillRect(xm + 2 * width_third, ym, width_third, height);
                g.translate(xm, ym);
                kc.paintSuperComponent(g);
                g.translate(-xm, -ym);
                g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
                g.drawRect(xm, ym, width_third, height);
                g.drawRect(xm + width_third, ym, width_third, height);
                g.drawRect(xm + 2 * width_third, ym, width_third, height);
                break;

            default:
                g.setColor(col);
                g.fillRect(xm, ym, width, height);
                g.translate(xm, ym);
                kc.paintSuperComponent(g);
                g.translate(-xm, -ym);
                g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
                g.drawRect(xm, ym, width, height);
            }
        } else /* if (img != null) */{
            g.translate(xm, ym);
            kc.paintSuperComponent(g);
            g.translate(-xm, -ym);
        }

        // Symbol für Verlinkung mit Teilmodell
        if (UserProperties.isShowLinks() && kc.getKnoten().getAssociatedDoc() != null) {
            g.drawImage(linkIcon, xm + 2, yp - 13, kc);
        }

        gc.setStroke(str);

        //wenn das Element selektiert ist -> schwarzen Rand drumrum und die 8 Anfasser zum ändern der Größe zeichnen
        if (kc.isSelected() && paintState != PaintState.WEBEXPORT) {

            //scharzes Rechteck um das Element zeichnen
            g.setColor(Color.black);
            g.drawRect(xm, ym, width, height);

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
            //			System.out.println(northLabel.getText() + "\nnorthLabel.height="+northLabel.getPreferredSize().height + " northLabel.width="+northLabel.getPreferredSize().width);
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
     * des Darstellungsbereiches des übergebenen Containers liegen, sonst <code>false</code>.
     * 
     * @param ec <code>ElementContainer</code>, für den geprüft wird, ob die Koordinaten in ihm liegen
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
            form = k.getGraphDocument().getMapping().getStandardForm(k.getElement());
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
            if (Math.abs(y - height / 2.0 - yi) > Math.round(prozent * height)) {
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
     * Letzte ausgewählte ResizeBox. Hier werden die Int-Werte der korrespondierenden Cursor benutzt.
     */
    private static int lastResizeCursor = Cursor.DEFAULT_CURSOR;

    /**
     * @return lastResizeCursor
     */
    public static final int getLastResizeCursor() {
        return lastResizeCursor;
    }

    /**
     * Liefert den <code>int</code>-Code für Position, welche sich auf dem Container
     * an den übergebenen Koordinaten befindet.<br>
     * <p>
     * Rückgabewerte:
     * <ul>
     * <li><code>Cursor.DEFAULT_CURSOR</code>: Koordinaten sind ausserhalb eines der anderen speziellen Grenzbereiche</li>
     * <li><code>Cursor.N_RESIZE_CURSOR</code>: Koordinaten sind im oberen Grenzbereich</li>
     * <li><code>Cursor.NE_RESIZE_CURSOR</code>: Koordinaten sind im oberen rechten Grenzbereich</li>
     * <li><code>Cursor.E_RESIZE_CURSOR</code>: Koordinaten sind im rechten Grenzbereich</li>
     * <li><code>Cursor.SE_RESIZE_CURSOR</code>: Koordinaten sind im unteren rechten Grenzbereich</li>
     * <li><code>Cursor.S_RESIZE_CURSOR</code>: Koordinaten sind im unteren Grenzbereich</li>
     * <li><code>Cursor.SW_RESIZE_CURSOR</code>: Koordinaten sind im unteren linken Grenzbereich</li>
     * <li><code>Cursor.W_RESIZE_CURSOR</code>: Koordinaten sind im linken Grenzbereich</li>
     * <li><code>Cursor.NW_RESIZE_CURSOR</code>: Koordinaten sind im oberen linken Grenzbereich</li>
     * </ul>
     * </p>
     * 
     * @param container Container, für den Grenzbereich ermittelt werden soll
     * @param xi X-Koordinate
     * @param yi Y-Koordinate
     * @return <code>int</code>-Code des Grenzbereiches
     */
    public static final int getResizeCursor(final NodeContainer container, final int xi, final int yi) {
        if (container.getKnoten().isUnpaintable()) {
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

    //	private static final int INVALID_ADAPTED_BORDER_POSITION_VALUE = Integer.MAX_VALUE;
    //	
    //	private int adaptedBorderPositionX = INVALID_ADAPTED_BORDER_POSITION_VALUE;
    //	private int adaptedBorderPositionY = INVALID_ADAPTED_BORDER_POSITION_VALUE;
    //	
    //	private D
    //	
    //	public ResizeBox

}
