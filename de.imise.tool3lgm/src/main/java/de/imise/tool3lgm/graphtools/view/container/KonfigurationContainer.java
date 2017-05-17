/*
 * Created on 16.06.2003
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.view.container;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.Konfiguration;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.gui.AbstractInternalFrame;
import de.imise.tool3lgm.gui.ToolInternalFrame;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * @author Thomas
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class KonfigurationContainer extends NodeContainer {

    public static Color[] farben = {
            Color.black,
            Color.blue,
            Color.cyan,
            Color.darkGray,
            Color.gray,
            Color.green,
            Color.magenta,
            Color.orange,
            Color.pink,
            Color.red,
            Color.yellow
    };
    public static int colorCounter = 0;

    public KonfigurationContainer() {
        super();
    }

    /**
     * @param neu
     * @param gd
     * @param map
     */
    public KonfigurationContainer(final Konfiguration neu, final GraphDocument gd) {
        super(neu, gd);
    }

    /**
     * @param alt
     * @param gd
     * @param map
     */
    public KonfigurationContainer(final KonfigurationContainer alt, final GraphDocument gd) {
        super(alt, gd);
    }

    /**
     * @param neu
     * @param l
     * @param gd
     * @param map
     */
    public KonfigurationContainer(final Konfiguration neu, final GraphElementLayout l, final GraphDocument gd) {
        super(neu, l, gd);
    }

    @Override
    public Object clone() {
        KonfigurationContainer retVal;
        try {
            retVal = (KonfigurationContainer) super.clone();
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            return null;
        }

        return retVal;
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 1;
    }

    @Override
    public int getHeight() {
        return 1;
    }

    double x_shift = 0;
    double y_shift = 0;

    public void setShift(final double x, final double y) {
        x_shift = x;
        y_shift = y;
    }

    @Override
    public final void paintComponent(final Graphics g) {
        if (!isVisible() && !isHighLight()) {
            return;
        }
        AbstractInternalFrame frame = Static.getActiveFrame();
        if (!(frame instanceof ToolInternalFrame)) {
            return;
        }

        Color elem_col = null;
        if (UserProperties.isAssignConfigurationColors()) {
            colorCounter = (colorCounter + 1) % farben.length;
            elem_col = farben[colorCounter];
        } else {
            elem_col = Color.black;
        }
        if (elem_col == null) {
            elem_col = Color.black;
        }
        g.setColor(elem_col);

        boolean multiView = ((ToolInternalFrame) frame).getInputGraphArea().isMultiViewEnabled();

        GraphDocument selectedDoc = frame.getGraphDocument();

        Konfiguration konf = (Konfiguration) getElement();
        ArrayList<ElementContainer> start = konf.getServerContainer(selectedDoc);
        ArrayList<ElementContainer> end = konf.getClientContainer(selectedDoc);

        for (int a = 0; a < start.size(); a++) {
            NodeContainer c1 = (NodeContainer) start.get(a);
            if (c1 == null) {
                continue;
            }
            ArrayList<ElementContainer> c1C = null;
            if (!c1.isVisible()) {
                c1C = c1.getSurrogateContainer();
            } else {
                c1C = new ArrayList<ElementContainer>(1);
                c1C.add(c1);
            }

            for (int b = 0; b < c1C.size(); b++) {
                NodeContainer kc1 = (NodeContainer) c1C.get(b);
                if (!kc1.isVisible()) {
                    continue;
                }
                if (isSelected()) {
                    g.setColor(Color.red);
                    g.fillRect(kc1.getX() - 10, kc1.getY() - 10, 20, 20);
                    g.setColor(elem_col);
                }
                if (multiView) {
                    Graphics2D gc = (Graphics2D) g;
                    Stroke s = gc.getStroke();

                    for (int c = 0; c < end.size(); c++) {
                        NodeContainer c2 = (NodeContainer) end.get(c);
                        if (c2 == null) {
                            continue;
                        }
                        ArrayList<ElementContainer> c2C = null;
                        if (!c2.isVisible()) {
                            c2C = c2.getSurrogateContainer();
                        } else {
                            c2C = new ArrayList<ElementContainer>(1);
                            c2C.add(c2);
                        }

                        for (int d = 0; d < c2C.size(); d++) {
                            NodeContainer kc2 = (NodeContainer) c2C.get(d);
                            if (!kc2.isVisible()) {
                                continue;
                            }
                            if (isHighLight()) {
                                g.setColor(Color.green);
                                gc.setStroke(meduimStroke);
                                g.drawLine(kc1.getX(), kc1.getY(), kc2.getX() + (int) x_shift, kc2.getY() + (int) y_shift);
                                gc.setStroke(s);
                                g.setColor(elem_col);
                            }

                            g.drawLine(kc1.getX(), kc1.getY(), kc2.getX() + (int) x_shift, kc2.getY() + (int) y_shift);
                            if (isSelected()) {
                                g.setColor(Color.red);
                                g.drawLine(kc1.getX(), kc1.getY(), kc2.getX() + (int) x_shift, kc2.getY() + (int) y_shift);
                                g.setColor(elem_col);
                            }
                        }
                    }
                }
            }
        }
    }
}
