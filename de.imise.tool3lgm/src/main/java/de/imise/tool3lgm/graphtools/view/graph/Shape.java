package de.imise.tool3lgm.graphtools.view.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * All standard forms. The string representation is also in the resources as a
 * key. The position of the enum entry is psied in the XML representation of the
 * model. I.e. who changes the order here, changes the layout of the elements in
 * models, which were created before.
 */
public enum Shape {
    rechteck {
        @Override
        public void paint(Graphics g, NodeContainer kc, Color col, Color analysisColor, Boolean isResult, int x, int y, int xm, int ym, int xp, int yp, int[] xs, int[] ys, int width, int height, int npoints) {
            g.setColor(col);
            g.fillRect(xm, ym, width, height);
            paintTextWithOffset(g, kc, this, xm, ym);
            g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
            g.drawRect(xm, ym, width, height);
        }
    },
    oval {
        @Override
        public void paint(Graphics g, NodeContainer kc, Color col, Color analysisColor, Boolean isResult, int x, int y, int xm, int ym, int xp, int yp, int[] xs, int[] ys, int width, int height, int npoints) {
            g.setColor(col);
            g.fillOval(xm, ym, width, height);
            paintTextWithOffset(g, kc, this, xm, ym);
            g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
            g.drawOval(xm, ym, width, height);
        }
    },
    dreieck {
        @Override
        public void paint(Graphics g, NodeContainer kc, Color col, Color analysisColor, Boolean isResult, int x, int y, int xm, int ym, int xp, int yp, int[] xs, int[] ys, int width, int height, int npoints) {
            xs[0] = xm;
            xs[1] = x;
            xs[2] = xp;
            ys[0] = ym + height;
            ys[1] = yp - height;
            ys[2] = ym + height;
            npoints = 3;
            g.setColor(col);
            g.fillPolygon(xs, ys, npoints);
            paintTextWithOffset(g, kc, this, xm, ym);
            g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
            g.drawPolygon(xs, ys, npoints);
        }
    },
    rundeck {
        @Override
        public void paint(Graphics g, NodeContainer kc, Color col, Color analysisColor, Boolean isResult, int x, int y, int xm, int ym, int xp, int yp, int[] xs, int[] ys, int width, int height, int npoints) {
            int cornerSize = roundRectCorner(width, height);
            g.setColor(col);
            g.fillRoundRect(xm, ym, width, height, cornerSize, cornerSize);
            paintTextWithOffset(g, kc, this, xm, ym);
            g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
            g.drawRoundRect(xm, ym, width, height, cornerSize, cornerSize);
        }
    },
    rhombus {
        @Override
        public void paint(Graphics g, NodeContainer kc, Color col, Color analysisColor, Boolean isResult, int x, int y, int xm, int ym, int xp, int yp, int[] xs, int[] ys, int width, int height, int npoints) {
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
            paintTextWithOffset(g, kc, this, xm, ym);
            g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
            g.drawPolygon(xs, ys, npoints);
        }
    },
    tonne {
        @Override
        public void paint(Graphics g, NodeContainer kc, Color col, Color analysisColor, Boolean isResult, int x, int y, int xm, int ym, int xp, int yp, int[] xs, int[] ys, int width, int height, int npoints) {
            int height_half = height / 2;
            g.setColor(col);
            g.fillArc(xm, ym, width, height_half + 1, 180, -180);
            g.fillArc(xm, y, width, height_half, 180, 180);
            g.fillRect(xm, y - height / 4, width, height_half);
            if (kc != null) {
                paintTextWithOffset(g, kc, this, xm, ym);
                g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
            } else {
                g.setColor(analysisColor);
            }
            g.drawArc(xm, y, width, height_half, 180, 180);
            g.drawLine(xm, y - height / 4, xm, y + height / 4);
            g.drawLine(xp, y - height / 4, xp, y + height / 4);
            g.drawOval(xm, ym, width, height_half);
        }
    },
    wabe {
        @Override
        public void paint(Graphics g, NodeContainer kc, Color col, Color analysisColor, Boolean isResult, int x, int y, int xm, int ym, int xp, int yp, int[] xs, int[] ys, int width, int height, int npoints) {
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
            paintTextWithOffset(g, kc, this, xm, ym);
            g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
            g.drawPolygon(xs, ys, npoints);
        }
    },
    ordner {
        @Override
        public void paint(Graphics g, NodeContainer kc, Color col, Color analysisColor, Boolean isResult, int x, int y, int xm, int ym, int xp, int yp, int[] xs, int[] ys, int width, int height, int npoints) {
            int width_third = width / 3;
            g.setColor(col);
            g.fillRect(xm, ym, width_third, height);
            g.fillRect(xm + width_third, ym, width_third, height);
            g.fillRect(xm + 2 * width_third, ym, width_third, height);
            if (kc != null) {
                paintTextWithOffset(g, kc, this, xm, ym);
                g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
            } else {
                g.setColor(analysisColor);
            }
            g.drawRect(xm, ym, width_third, height);
            g.drawRect(xm + width_third, ym, width_third, height);
            g.drawRect(xm + 2 * width_third, ym, width_third, height);
        }
    },
    rectangle_dashed {
        @Override
        public void paint(Graphics g, NodeContainer kc, Color col, Color analysisColor, Boolean isResult, int x, int y, int xm, int ym, int xp, int yp, int[] xs, int[] ys, int width, int height, int npoints) {
            g.setColor(col);
            g.fillRect(xm, ym, width, height);
            paintTextWithOffset(g, kc, this, xm, ym);
            g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
            Graphics2D gc = (Graphics2D) g;
            Stroke stroke = gc.getStroke();
            gc.setStroke(GraphElementLayout.NORMAL_STROKE_DASHED);
            g.drawRect(xm, ym, width, height);
            gc.setStroke(stroke);
        }
    };

    /**
     * Minimum distance between the HTML text and the margin, if the text was
     * aligned to the margin. This value should be a value that fits the
     * {@link GraphElementLayout#STANDARD_WIDTH} and
     * {@link GraphElementLayout#STANDARD_HEIGHT}.
     */
    public static int DEFAULT_HTML_LABEL_INSETS = 20;

    /**
     * @return the insets of the label inside the shape
     */
    public int getLabelInsets() {
        return DEFAULT_HTML_LABEL_INSETS;
    }

    /**
     * returns a unified corner size for rectangles, that are too small, a
     * smaller corner size is returned
     *
     * @param width
     * @param height
     * @return cornerSize
     */
    private static int roundRectCorner(int width, int height) {
        int min = Math.min(width, height);
        int cornerSize = GraphElementLayout.STANDARD_ROUND_RECT_CONER_SIZE;
        if (min < cornerSize * 2) { //very small roundrect get the half corner size, all the others the default
            cornerSize /= 2;
        }
        return cornerSize;
    }

    /**
     * paints the Label of the element with an offset this method is mainly used
     * to create a margin to the borders of the model element
     *
     * @param g
     * @param kc
     * @param offset
     * @param xm
     * @param ym
     */
    private static void paintTextWithOffset(Graphics g, NodeContainer kc, Shape shape, int xm, int ym) {
        //if the whohle height is too small for the offset -> reduce the offset
        //if the whole height is great enough -> take the original shape offset
        Dimension size = kc.getSize();
        Font font = kc.getFont();
        int fontSize = font.getSize();
        int offset = size.height - fontSize;
        offset = Math.max(0, offset);
        int shapeOffset = shape.getLabelInsets();
        offset = Math.min(offset, shapeOffset);

        int axisOffset = offset / 2;
        int reducedSizeHeight = size.height - offset;
        Dimension reducedSize = reducedSizeHeight <= fontSize ? size : new Dimension(size.width - offset, size.height - offset);
        if (reducedSize == size) {
            axisOffset = 0;
        }
        g.translate(xm + axisOffset, ym + axisOffset);
        kc.setSize(reducedSize);
        kc.paintSuperComponent(g);
        kc.setSize(size);
        g.translate(-(xm + axisOffset), -(ym + axisOffset));
    }

    /**
     * Paints the shape
     *
     * @param g
     * @param kc
     * @param col
     * @param analysisColor
     * @param isResult
     * @param x
     * @param y
     * @param xm
     * @param ym
     * @param xp
     * @param yp
     * @param xs
     * @param ys
     * @param width
     * @param height
     * @param npoints
     */
    public abstract void paint(Graphics g, NodeContainer kc, Color col, Color analysisColor, Boolean isResult, int x, int y, int xm, int ym, int xp, int yp, int[] xs, int[] ys,
            int width, int height, int npoints);

}
