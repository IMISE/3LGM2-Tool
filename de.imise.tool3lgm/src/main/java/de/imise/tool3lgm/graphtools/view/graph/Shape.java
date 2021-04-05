package de.imise.tool3lgm.graphtools.view.graph;

import java.awt.Color;
import java.awt.Graphics;

import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * Alle Standardformen. Die String-Repräsentation steht als Schlüssel auch in
 * den Ressourcen. Die Position des Enum-Eintrages wird in der
 * XML-Repräsentation des Modells gepsiechert. D.h. wer hier die Reihenfolge
 * ändert, ändert das Layout der Elemente in Modellen, die vorher erstellt
 * wurden.
 */
public enum Shape {
    rechteck {
        @Override
        public void paint(final Graphics g, final NodeContainer kc, final Color col, final Color analysisColor, final Boolean isResult, final int x, final int y, final int xm, final int ym, final int xp, final int yp, final int[] xs, final int[] ys,
                final int width, final int height, final int npoints) {
            g.setColor(col);
            g.fillRect(xm, ym, width, height);
            g.translate(xm, ym);
            kc.paintSuperComponent(g);
            g.translate(-xm, -ym);
            g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
            g.drawRect(xm, ym, width, height);
        }
    },
    oval {
        @Override
        public void paint(final Graphics g, final NodeContainer kc, final Color col, final Color analysisColor, final Boolean isResult, final int x, final int y, final int xm, final int ym, final int xp, final int yp, final int[] xs, final int[] ys,
                final int width, final int height, final int npoints) {
            g.setColor(col);
            g.fillOval(xm, ym, width, height);
            g.translate(xm, ym);
            kc.paintSuperComponent(g);
            g.translate(-xm, -ym);
            g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
            g.drawOval(xm, ym, width, height);
        }
    },
    dreieck {
        @Override
        public void paint(final Graphics g, final NodeContainer kc, final Color col, final Color analysisColor, final Boolean isResult, final int x, final int y, final int xm, final int ym, final int xp, final int yp, final int[] xs, final int[] ys,
                final int width, final int height, int npoints) {
            xs[0] = xm;
            xs[1] = x;
            xs[2] = xp;
            ys[0] = ym + height;
            ys[1] = yp - height;
            ys[2] = ym + height;
            npoints = 3;
            g.setColor(col);
            g.fillPolygon(xs, ys, npoints);
            g.translate(xm, ym);
            kc.paintSuperComponent(g);
            g.translate(-xm, -ym);
            g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
            g.drawPolygon(xs, ys, npoints);
        }
    },
    rundeck {
        @Override
        public void paint(final Graphics g, final NodeContainer kc, final Color col, final Color analysisColor, final Boolean isResult, final int x, final int y, final int xm, final int ym, final int xp, final int yp, final int[] xs, final int[] ys,
                final int width, final int height, final int npoints) {
            int cornerSize = roundRectCorner(width, height);
            g.setColor(col);
            g.fillRoundRect(xm, ym, width, height, cornerSize, cornerSize);
            g.translate(xm, ym);
            kc.paintSuperComponent(g);
            g.translate(-xm, -ym);
            g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
            g.drawRoundRect(xm, ym, width, height, cornerSize, cornerSize);
        }
    },
    rhombus {
        @Override
        public void paint(final Graphics g, final NodeContainer kc, final Color col, final Color analysisColor, final Boolean isResult, final int x, final int y, final int xm, final int ym, final int xp, final int yp, final int[] xs, final int[] ys,
                final int width, final int height, int npoints) {
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
        }
    },
    tonne {
        @Override
        public void paint(final Graphics g, final NodeContainer kc, final Color col, final Color analysisColor, final Boolean isResult, final int x, final int y, final int xm, final int ym, final int xp, final int yp, final int[] xs, final int[] ys,
                final int width, final int height, final int npoints) {
            int height_half = height / 2;
            g.setColor(col);
            g.fillArc(xm, ym, width, height_half + 1, 180, -180);
            g.fillArc(xm, y, width, height_half, 180, 180);
            g.fillRect(xm, y - height / 4, width, height_half);
            g.translate(xm, ym);
            if (kc != null) {
                kc.paintSuperComponent(g);
                g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
            } else {
                g.setColor(analysisColor);
            }
            g.translate(-xm, -ym);
            g.drawArc(xm, y, width, height_half, 180, 180);
            g.drawLine(xm, y - height / 4, xm, y + height / 4);
            g.drawLine(xp, y - height / 4, xp, y + height / 4);
            g.drawOval(xm, ym, width, height_half);
        }
    },
    wabe {
        @Override
        public void paint(final Graphics g, final NodeContainer kc, final Color col, final Color analysisColor, final Boolean isResult, final int x, final int y, final int xm, final int ym, final int xp, final int yp, final int[] xs, final int[] ys,
                final int width, final int height, int npoints) {
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
        }
    },
    ordner {
        @Override
        public void paint(final Graphics g, final NodeContainer kc, final Color col, final Color analysisColor, final Boolean isResult, final int x, final int y, final int xm, final int ym, final int xp, final int yp, final int[] xs, final int[] ys,
                final int width, final int height, final int npoints) {
            int width_third = width / 3;
            g.setColor(col);
            g.fillRect(xm, ym, width_third, height);
            g.fillRect(xm + width_third, ym, width_third, height);
            g.fillRect(xm + 2 * width_third, ym, width_third, height);
            g.translate(xm, ym);
            if (kc != null) {
                kc.paintSuperComponent(g);
                g.setColor(isResult && analysisColor != null ? analysisColor : kc.getFrameColor());
            } else {
                g.setColor(analysisColor);
            }
            g.translate(-xm, -ym);
            g.drawRect(xm, ym, width_third, height);
            g.drawRect(xm + width_third, ym, width_third, height);
            g.drawRect(xm + 2 * width_third, ym, width_third, height);
        }
    };

    private static int roundRectCorner(final int width, final int height) {
        int min = Math.min(width, height);
        int cornerSize = (int) Math.ceil(min / 10);

        return cornerSize;
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
    public abstract void paint(final Graphics g, final NodeContainer kc, final Color col, final Color analysisColor, final Boolean isResult, final int x, final int y, final int xm, final int ym, final int xp, final int yp, final int[] xs, final int[] ys,
            final int width, final int height, final int npoints);

}