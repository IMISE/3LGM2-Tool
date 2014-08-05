package de.imise.tool3lgm.graphtools.view.graph;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ContextGenerator;
import de.imise.tool3lgm.graphtools.GDCommands;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.Szenario;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.gui.Werkzeugleiste;
import de.imise.tool3lgm.tools.UnfloatableToolBar;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * COMMENTME
 * 
 * @author N.N., AXS
 */
public class InputGraphArea extends BasicGraphArea implements MouseListener, MouseMotionListener, MouseWheelListener {

    /**
     * Toleranz in Pixel, die im Abstand zwischen 2 Knickpunten in beiden Dimensionen unterschritten
     * sein muss, damit sie als übereinander leigend gelten.
     */
    private static final int BENDPOINT_OVERLAY_TOLERANCE = 5;

    /**
     * Mit diesem Wert wird bestimmt, ob ein Knickpunkt auf die Linie zwischen seinen ihn umgebenden Knickpunkten
     * verschoben wurde. Es ist die Abweichung zw. dem Cosinus des Winkels zwischen der Linie vom Vorgängerknickpunkt
     * des verschobenen Knickpunktes und dem verschobenen Knickpunkt selbst sowie der Linie zwischen Vorgänger und
     * Nachfolger. Unterschreitet der Kosinus diesen Wert, gilt der verschobene Knickpunkt als auf der Linie liegend.
     */
    private static final double BENDPOINT_LINE_DIFFERENCE_ANGLE_IN_DEG = 0.0002;

    /**
     * COMMENTME
     */
    private int xin = 0, yin = 0;

    /**
     * Für jede der 5 Ebenen eine X und Y-Koordinate in die die Input-Koordinaten je nach dargestelltem
     * Ausschnitt, Neigung usw. der Ebene umgerechnet wird.
     */
    private final int[] xreal = new int[ModelConstants.LAYERS.length], yreal = new int[ModelConstants.LAYERS.length];

    /**
     * Im Grunde das gleiche wie <code>xreal</code> und <code>yreal</code>. Beim Draggen auf Kanten und der Entstehung der
     * neuen Knickpunkte muss man aber die Koordinaten kennen, bei denen die Maus vor dem Draggen war, sonst haut die
     * Positionsbestimmung des neuen Knickpunktes nicht hin.
     */
    private final int[] lastXreal = new int[ModelConstants.LAYERS.length], lastYreal = new int[ModelConstants.LAYERS.length];

    /**
     * Wenn Knoten verschoben werden, dann grenzen diese Koordinaten den minimalen Bereich ein, in dem alle
     * Elemente liegen.
     * Dies ist der Bereich, in dem die Elemente liegen würden, wenn ohne Raster verschoben wird.
     * ACHTUNG: <code>height</code> und <code>width</code> dieses Rechtecks sind nicht die wirkliche Weite
     * sondern die Koordinaten des Punktes der rechten unteren Ecke.
     */
    public static Rectangle grabbedElementsRealRect;
    /**
     * Wenn Knoten verschoben werden, dann grenzen diese Koordinaten den minimalen Bereich ein, in dem alle
     * Elemente liegen.
     * Dies ist der Bereich, in dem die Elemente liegen würden, wenn mit Raster verschoben wird.
     * ACHTUNG: <code>height</code> und <code>width</code> dieses Rechtecks sind nicht die wirkliche Weite
     * sondern die Koordinaten des Punktes der rechten unteren Ecke.
     */
    public static Rectangle grabbedElementsRasteredRect;
    /**
     * Wenn Knoten verschoben werden, dann grenzen diese Koordinaten den minimalen Bereich ein, in dem alle
     * Elemente liegen.
     * Dies ist der Bereich, in dem die Elemente und alle ihre evtl. nicht selektierten, aber bei der
     * "mit Teilelementen verschieben"-Option ebenfalls verschobenen Elemente.
     * ACHTUNG: <code>height</code> und <code>width</code> dieses Rechtecks sind nicht die wirkliche Weite
     * sondern die Koordinaten des Punktes der rechten unteren Ecke.
     */
    public static Rectangle grabbedElementsFullRect;

    /**
     * COMMENTME
     */
    private boolean left_button = false, right_button = false;

    /**
     * COMMENTME
     */
    private boolean grabbed = false, sized = false, mouse_makes_knot = false, mouse_makes_trace = false, mouse_dragged = false;

    /**
     * COMMENTME
     */
    private ElementContainer ka;

    /**
     * COMMENTME
     */
    private Knoten next_knot;

    /**
     * COMMENTME
     */
    private boolean was_selected = false;

    /**
     * COMMENTME
     * 
     * @param gdoc
     */
    public InputGraphArea(final GraphDocument gdoc) {
        super(gdoc);

        if (gdoc instanceof Szenario) {
            addMouseListener(this);
            addMouseMotionListener(this);
            addMouseWheelListener(this);
            Tool3lgm.tool.getContentPane().addMouseListener(this);
        }

        xin = 0;
        yin = 0;
        grabbed = false;
        sized = false;
        setMouseMakesKnot(false);
        check_size();
    }

    // --- kleine Hilfsmethoden --- Anfang ---

    @Override
    public void setMultiViewEnabled(final boolean b) {
        multi_view = b;
        findIncludingRectangles();
        super.setMultiViewEnabled(b);
    }

    // --- kleine Hilfsmethoden --- Ende ---

    // --- Methoden zur Statusveraenderung --- Anfang ---

    /**
     * COMMENTME
     * 
     * @param k
     */
    public final void setNextKnot(final Knoten k) {
        if (k == null) {
            return;
        }
        next_knot = k;
        //              System.out.println("Klasse von k ist " + k.getClass().getName());
    }

    /**
     * COMMENTME
     * 
     * @return
     */
    public final Knoten getNextKnot() {
        return next_knot;
    }

    /**
     * COMMENTME
     * 
     * @param b
     */
    public final void setMouseMakesKnot(final boolean b) {
        if (b) {
            mouse_makes_trace = false;
        }
        mouse_makes_knot = b;
        if (b) {
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * COMMENTME
     * 
     * @param b
     */
    public final void setMouseMakesTrace(final boolean b) {
        if (b) {
            mouse_makes_knot = false;
        }
        mouse_makes_trace = b;
        if (b) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    // --- Methoden zur Statusveraenderung --- Ende ---

    // --- Methoden der Rueckberechnung von Koordinaten --- Anfang ---

    /**
     * COMMENTME
     */
    public final void computeRealCoordinates(final boolean initLastAsReal) {
        if (!initLastAsReal) {
            lastXreal[0] = xreal[0];
            lastYreal[0] = yreal[0];
            lastXreal[2] = xreal[2];
            lastYreal[2] = yreal[2];
            lastXreal[4] = xreal[4];
            lastYreal[4] = yreal[4];
        }
        yreal[0] = (int) (((yin - middle_y) / zoom - interLayerSpace) / y_y_factor);
        xreal[0] = (int) ((xin - middle_x) / zoom - yreal[0] * y_x_factor);

        yreal[2] = (int) ((yin - middle_y) / zoom / y_y_factor);
        xreal[2] = (int) ((xin - middle_x) / zoom - yreal[2] * y_x_factor);

        yreal[4] = (int) (((yin - middle_y) / zoom + interLayerSpace) / y_y_factor);
        xreal[4] = (int) ((xin - middle_x) / zoom - yreal[4] * y_x_factor);

        if (initLastAsReal) {
            lastXreal[0] = xreal[0];
            lastYreal[0] = yreal[0];
            lastXreal[2] = xreal[2];
            lastYreal[2] = yreal[2];
            lastXreal[4] = xreal[4];
            lastYreal[4] = yreal[4];
        }

    }

    /**
     * Berechnet in jeder Richtung die minimalen und maximalen Koordinaten von der übergebenen {@link Rectangle} und dem {@link ElementContainer}.
     * Wird als {@link Rectangle} <code>null</code> übergeben, dann kommt ein neues {@link Rectangle}-Objekt zurück, ansonsten wird das bestehende
     * zurück gegeben.
     * Die Weite und Höhe des {@link Rectangle} geben Koordinaten an und nicht die Weite und Höhe im eigentlichen
     * Sinne
     * 
     * @param rect
     *            {@link Rectangle}, die verändert wird, falls die Koordinaten des übergebenen {@link ElementContainer}s
     *            außerhalb der vorher bestehenden Dimasion lagen
     * @param ec
     *            {@link ElementContainer}, dessen Koordinaten in der übergebenen {@link Rectangle} liegen sollen
     * @return
     *         die übergebenen {@link Rectangle} oder wenn <code>null</code> übergeben wurde eine neue {@link Rectangle}
     */
    private static final Rectangle getIncludingRectangle(final Rectangle rect, final ElementContainer ec) {
        int realx1 = ec.getX() - (ec.getWidth() >> 1);
        int realy1 = ec.getY() - (ec.getHeight() >> 1);
        int realx2 = realx1 + ec.getWidth();
        int realy2 = realy1 + ec.getHeight();
        return getIncludingRectangle(rect, realx1, realy1, realx2, realy2);
    }

    /**
     * /**
     * Berechnet in jeder Richtung die minimalen und maximalen Koordinaten von der übergebenen {@link Rectangle} und dem {@link ElementContainer}.
     * Wird als {@link Rectangle} <code>null</code> übergeben, dann kommt ein neues {@link Rectangle}-Objekt zurück, ansonsten wird das bestehende
     * zurück gegeben.
     * Die Weite und Höhe des {@link Rectangle} geben Koordinaten an und nicht die Weite und Höhe im eigentlichen
     * Sinne
     * 
     * @param rect
     *            {@link Rectangle}, die verändert wird, falls die Koordinaten des übergebenen {@link ElementContainer}s
     *            außerhalb der vorher bestehenden Dimasion lagen
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private final static Rectangle getIncludingRectangle(Rectangle rect, final int x1, final int y1, final int x2, final int y2) {
        if (rect == null) {
            rect = new Rectangle(x1, y1, x2, y2);
        } else {
            if (x1 < rect.x) {
                rect.x = x1;
            }
            if (y1 < rect.y) {
                rect.y = y1;
            }
            if (rect.width < x2) {
                rect.width = x2;
            }
            if (rect.height < y2) {
                rect.height = y2;
            }
        }
        return rect;
    }

    /**
     * Füllt die 3 {@link Rectangle} {@link #grabbedElementsFullRect}, {@link #grabbedElementsRasteredRect} und {@link #grabbedElementsRealRect} mit
     * den Koordinaten in abhängigkeit von der Selektion. Wenn eine
     * Einzelebenenansicht eingeschaltet ist, dann werden nur Elemente der aktuellen Ebene einbezogen sonst alle.
     */
    private void findIncludingRectangles() {
        if (doc.isSingleSelection() && ka instanceof BendpointContainer) {
            //bei einzelnen KnickpunktContainern ist nur der Mittelpunkt das selektierte Rechteck
            grabbedElementsRealRect = new Rectangle(ka.getX(), ka.getY(), ka.getX(), ka.getY());
            grabbedElementsRasteredRect = new Rectangle(grabbedElementsRealRect);
            grabbedElementsFullRect = new Rectangle(grabbedElementsRealRect);
        } else if (ka instanceof NodeContainer) {
            // bei KnotenContainern (einem oder mehrere) wird das die Selektion einschließende
            // Rechteck inklusive der evtl. mitzubewegenden Teilelemente berechnet
            grabbedElementsRealRect = null;
            grabbedElementsFullRect = null;
            int ebene = doc.getCollection().getActiveLayer();
            for (NodeContainer kc : doc.getSelectedRealElementContainerIterable()) {
                if (!kc.isVisible() || !multi_view && kc.layerFor() != ebene) {
                    continue;
                }
                grabbedElementsRealRect = getIncludingRectangle(grabbedElementsRealRect, kc);
                if (UserProperties.isMoveSubelements()) {
                    for (ElementContainer ec : kc.getElement().getPartContainer(doc, true)) {
                        if (!ec.isVisible() || !multi_view && kc.layerFor() != ebene) {
                            continue;
                        }
                        if (grabbedElementsFullRect == null) {
                            grabbedElementsFullRect = new Rectangle(grabbedElementsRealRect);
                        }
                        grabbedElementsFullRect = getIncludingRectangle(grabbedElementsFullRect, ec);
                        for (Kante edge : ec.getElement().getEdgesWith(ka.getElement())) {
                            EdgeContainer edgeC = (EdgeContainer) edge.getContainer(doc);
                            if (edgeC != null) {
                                for (BendpointContainer bc : edgeC.getBendpointContainerList()) {
                                    grabbedElementsFullRect = getIncludingRectangle(grabbedElementsFullRect, bc);
                                }
                            }
                        }
                    }
                }
            }
            for (BendpointContainer kc : doc.getSelectedBendpointContainerIterable()) {
                if (multi_view || !multi_view && kc.layerFor() == ebene) {
                    grabbedElementsRealRect = getIncludingRectangle(grabbedElementsRealRect, kc);
                }
            }
            grabbedElementsRasteredRect = new Rectangle(grabbedElementsRealRect);
            if (grabbedElementsFullRect == null) {
                grabbedElementsFullRect = new Rectangle(grabbedElementsRealRect);
            } else {
                grabbedElementsFullRect = getIncludingRectangle(grabbedElementsFullRect, grabbedElementsRealRect.x, grabbedElementsRealRect.y, grabbedElementsRealRect.width, grabbedElementsRealRect.height);
            }
        }
    }

    /**
     * @return
     */
    private final ElementContainer getMouseOverElementContainer() {
        ElementContainer returnContainer = null;
        if (multi_view) {
            for (int c = 4; c >= 0; c -= 2) {
                returnContainer = chooseObject(doc.getLayer(c), xreal[c], yreal[c]);
                if (returnContainer != null) {
                    break;
                }
            }
        } else {
            int ebene = doc.getCollection().getActiveLayer();
            returnContainer = chooseObject(doc.getLayer(ebene), xreal[ebene], yreal[ebene]);
        }
        return returnContainer;
    }

    // --- Methoden der Rueckberechnung von Koordinaten --- Ende ---

    // Methoden des MouseListener-Interfaces --- Anfang ---

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public final void mouseClicked(final MouseEvent e) {
        if (e.getClickCount() <= 1) {
            return;
        }

        if ((e.getModifiers() & (InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK)) != 0) {
            ContextGenerator.setControlled(true);
        }

        ElementContainer ka = getMouseOverElementContainer();

        //		ElementContainer ka = null;
        //
        //		if (multi_view) {
        //			for (int c = 4; c >= 0; c -= 2) {
        //				ka = chooseObject(doc.getLayer(c), xreal[c], yreal[c]);
        //				if (ka != null) {
        //					break;
        //				}
        //			}
        //		} else {
        //			int ebene = doc.getCollection().getActiveLayer();
        //			ka = chooseObject(doc.getLayer(ebene), xreal[ebene], yreal[ebene]);
        //		}

        if (ka != null) {
            doc.select(ka, 0);
        }

        // Verknüpftes Teilmodell öffnen
        if ((e.getModifiers() & InputEvent.ALT_MASK) != 0) {
            //Component source, int id, long when, int modifiers,
            //int keyCode, char keyChar, int keyLocation
            dispatchEvent(new KeyEvent(this, KeyEvent.KEY_RELEASED, 0l, 0, KeyEvent.VK_ALT, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD));
            Tool3lgm.tool.changeToLinked(doc);
            return;
        }

        // Teilobjekte zeigen oder verstecken
        if ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
            doc.auf_zuklappen(TransactionManager.STANDARD_PID);
            return;
        }

        //Knickpunkte bei Doppelklicks löschen
        if (ka instanceof BendpointContainer) {
            doc.getCollection().removeBendpoint(((BendpointContainer) ka).getKnickpunktKnoten(), TransactionManager.STANDARD_PID);
        } else if (ka != null) {
            doc.showPropertyDialog(ka.getElement());
        }
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if ((e.getModifiers() & (InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK)) != 0) {
            ContextGenerator.setControlled(true);
        }
        xin = e.getX();
        yin = e.getY();

        computeRealCoordinates(true);

        if (Tool3lgmConstants.isPopupTrigger(e)) {
            right_button = true;
            left_button = false;
        } else {
            right_button = false;
            left_button = true;
        }

        for (int g = 4; g >= 0; g -= 2) {

            // Bei flacher Ansicht soll nur die aktive Schicht durchsucht werden.
            if (!multi_view && doc.getLayer(g) != doc.getActiveLayer()) {
                continue;
            }

            doc.setKnotInsertPosition(Math.round(xreal[g] / UserProperties.getRasterWidth()) * UserProperties.getRasterWidth(), Math.round(yreal[g] / UserProperties.getRasterWidth()) * UserProperties.getRasterWidth());

            if (mouse_makes_trace && left_button) {
                doc.deselectAll(false);
                ka = chooseObject(doc.getLayer(g), xreal[g], yreal[g]);
                if (ka != null) {
                    doc.select(ka, 0);
                    revalidate();
                    repaint();
                    break;
                }
            } else if (mouse_makes_knot && left_button) {
                if (-page_width / 2 < xreal[g] && page_width / 2 > xreal[g] && -page_height / 2 < yreal[g] && page_height / 2 > yreal[g]) {
                    if (next_knot.layerFor() == g) {
                        Tool3lgm.setLastActionPosition(xin + getX(), yin + getY());
                        doc.createKnotenWithContainer(next_knot.getClass(), TransactionManager.STANDARD_PID);
                        revalidate();
                        repaint();
                    }
                }
            } else {
                // Jede einzelne der 3 Ebenen wird erstmal durchkaemmt.
                ka = null;

                // 1. Ob man in eine Hand eines Knotens getroffen hat
                ka = chooseResizable(doc.getLayer(g), xreal[g], yreal[g]);
                if (ka != null) {
                    Tool3lgm.getContextGenerator().setModelElement(ka);
                    Tool3lgm.getContextGenerator().setResizing(true);

                    if (left_button && g != doc.getCollection().getActiveLayer()) {
                        doc.getCollection().setActiveLayer(g);
                    }

                    setCursor(new Cursor(KnotenRenderer.getLastResizeCursor()));

                    sized = true;
                    Tool3lgm.getContextGenerator().processMouseEvent(left_button, right_button, this, xin, yin);
                    break;
                }

                // 2. Ob man in ein Objekt direkt getroffen hat: Knoten oder Kante
                ka = null;
                ka = chooseObject(doc.getLayer(g), xreal[g], yreal[g]);
                //System.out.println("    start context generating..." + System.currentTimeMillis());
                if (ka != null) {

                    Tool3lgm.getContextGenerator().setModelElement(ka);
                    Tool3lgm.getContextGenerator().setElementGetroffen(true);

                    if (left_button && g != doc.getCollection().getActiveLayer()) {
                        doc.getCollection().setActiveLayer(g);
                    }

                    if (ka.isSelected()) {
                        was_selected = true;
                        doc.addToSelection(ka, TransactionManager.STANDARD_PID);
                    } else {
                        Tool3lgm.getContextGenerator().processMouseEvent(left_button, right_button, this, xin, yin);
                    }

                    findIncludingRectangles();
                    grabbed = true;
                    setCursor(new Cursor(Cursor.MOVE_CURSOR));

                    break;
                }

                // 3. Ob man die Ebene selbst getroffen hat

                {
                    Tool3lgm.getContextGenerator().setEbeneGetroffen(true);

                    if (isMultiViewEnabled()) {
                        for (int j = g; j >= 0; j -= 2) {
                            if (-page_width / 2 < xreal[j] && page_width / 2 > xreal[j] && -page_height / 2 < yreal[j] && page_height / 2 > yreal[j]) {
                                if (j != doc.getCollection().getActiveLayer()) {
                                    doc.getCollection().setActiveLayer(j);
                                    g = j + 2;
                                }
                                break;
                            }
                        }
                    }

                    if (g == doc.getCollection().getActiveLayer()) {
                        left_sel_x = xreal[g];
                        left_sel_y = yreal[g];
                        right_sel_x = xreal[g];
                        right_sel_y = yreal[g];
                        if (left_button) {
                            mouse_selection = true;
                        } else {
                            doc.setKnotInsertPosition(Math.round(xreal[g] / UserProperties.getRasterWidth()) * UserProperties.getRasterWidth(), Math.round(yreal[g] / UserProperties.getRasterWidth()) * UserProperties.getRasterWidth());
                        }

                        Tool3lgm.getContextGenerator().processMouseEvent(left_button, right_button, this, xin, yin);
                        break;
                    }
                }
            }
        }
        //			System.err.println(" finished" + System.currentTimeMillis() / 100);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (mouse_selection) {
            if (left_sel_y != right_sel_y || left_sel_x != right_sel_x) {
                doc.selectArea(left_sel_x, left_sel_y, right_sel_x, right_sel_y);
            }
            mouse_selection = false;
            return;
        }

        if (sized || grabbed) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        if (mouse_dragged) {
            doc.finish_transaction(TransactionManager.STANDARD_PID);
            doc.distributeEvent(GraphDocument.ELEMENT_GRAPHICS_CHANGED);
        }

        xin = e.getX();
        yin = e.getY();
        computeRealCoordinates(false);

        if (mouse_makes_trace && left_button) {
            for (int g = 4; g >= 0; g -= 2) {
                if (!multi_view && doc.getLayer(g) != doc.getActiveLayer()) {
                    continue;
                }

                ka = chooseObject(doc.getLayer(g), xreal[g], yreal[g]);
                if (ka != null) {
                    doc.addToSelection(ka, TransactionManager.STANDARD_PID);
                    doc.linkSelected(null, Doppelkante.FORWARD, TransactionManager.STANDARD_PID);
                    revalidate();
                    repaint();
                    break;
                }
            }
        } else if (mouse_makes_knot && left_button) {
        } else {
            // Jede einzelne der 3 Ebenen wird erstmal durchkaemmt.
            loop2: for (int k = 4; k >= 0; k -= 2) {
                // Bei flacher Ansicht soll nur die aktive Schicht durchsucht werden.
                if (!multi_view && doc.getLayer(k) != doc.getActiveLayer()) {
                    continue loop2;
                }

                // 1. Ob man in eine Hand eines Knotens getroffen hat
                if (Tool3lgm.getContextGenerator().getResizing()) {
                    Tool3lgm.getContextGenerator().setResizing(false);
                    sized = false;
                    break;
                }

                // 2. Ob man in ein Objekt direkt getroffen hat: Knoten oder Kante
                if (Tool3lgm.getContextGenerator().getElementGetroffen()) {
                    if (was_selected) {
                        was_selected = false;
                        if (!mouse_dragged) {
                            Tool3lgm.getContextGenerator().processMouseEvent(left_button, right_button, this, xin, yin);
                        }
                    }

                    //wenn ein Knickpunkt gedraggt wurde
                    if (mouse_dragged && ka != null && ka instanceof BendpointContainer) {
                        //Prüfe ob er gelöscht werden soll. Das soll er, wenn er auf einer Linie 
                        //zwischen den anderen Knickpunkten oder den Endpunkten der Kante liegt
                        BendpointContainer kpc = (BendpointContainer) ka;
                        Point pos = kpc.getPosition();
                        Point prePos = kpc.getPredecessorPosition();
                        Point postPos = kpc.getSuccessorPosition();

                        //wenn der Knickpunkt auf seinen Vorgänger- oder Nachfolgerknickpunkt gedragged
                        //wurde -> lösche ihn
                        if (Math.abs(prePos.x - pos.x) < BENDPOINT_OVERLAY_TOLERANCE && Math.abs(prePos.y - pos.y) < BENDPOINT_OVERLAY_TOLERANCE || Math.abs(postPos.x - pos.x) < BENDPOINT_OVERLAY_TOLERANCE
                                && Math.abs(postPos.y - pos.y) < BENDPOINT_OVERLAY_TOLERANCE) {
                            doc.getCollection().removeBendpoint(kpc.getKnickpunktKnoten(), TransactionManager.STANDARD_PID);
                            //prüfe, ob der Knickpunkt mit einer gewissen Toleranz auf fast einer Linie mit seinen Außenpunkten
                            //liegt -> wenn ja -> lösche ihn
                        } else {
                            //Koodinaten transformieren um immer in den positiven Bereich zu kommen
                            int minX = Math.min(prePos.x, pos.x);
                            minX = Math.min(minX, postPos.x);
                            if (minX < 0) {
                                prePos.x -= minX;
                                pos.x -= minX;
                                postPos.x -= minX;
                            }
                            int minY = Math.min(prePos.y, pos.y);
                            minY = Math.min(minY, postPos.y);
                            if (minY < 0) {
                                prePos.y -= minY;
                                pos.y -= minY;
                                postPos.y -= minY;
                            }

                            //wird true, wenn die X-Koordinate des Knickpunktes nach dem Draggen größer oder kleiner
                            //ist, als die Koordinaten der beiden Knickpunkte, zwischen denen sich der aktuell gedraggte befindet
                            boolean xOut = pos.x < prePos.x && pos.x < postPos.x || pos.x > prePos.x && pos.x > postPos.x;
                            //dasselbe wie xOut nur für y
                            boolean yOut = pos.y < prePos.y && pos.y < postPos.y || pos.y > prePos.y && pos.y > postPos.y;

                            //System.err.println(xOut + "  " + yOut);
                            //System.err.println("minX=" + minX + "   minY=" + minY);

                            //wenn der Knickpunkt mit beiden Koordinaten wirklich zw. den beiden ihn umgebenden Knickpunkten liegt
                            if (!(xOut && yOut)) {

                                double xDiff = prePos.x - pos.x;
                                double yDiff = prePos.y - pos.y;
                                //Strecke zw. Vorgängerknickpunkt und verschobenem Knickpunkt
                                double a = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
                                xDiff = pos.x - postPos.x;
                                yDiff = pos.y - postPos.y;
                                //Strecke zw. verschobenem Knickpunkt und Nachfolger
                                double b = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
                                xDiff = postPos.x - prePos.x;
                                yDiff = postPos.y - prePos.y;
                                //Strecke zw. Vorgängerknickpunkt und Nachfolger
                                double c = Math.sqrt(xDiff * xDiff + yDiff * yDiff);

                                //Cosinus des Winkels zw. a und c 
                                double cosAlpha = (-(a * a) + b * b + c * c) / (2d * b * c);

                                //Wenn die Differenz der beiden Winkel die Toleranz unterschreitet = die Knickpunkte 
                                //liegen nahezu auf einer Linie (der Cosinus ist 1, wenn der Winkel 0 Grad beträgt ->
                                //daher die Abweichung von 1 bestimmen)
                                if (Math.abs(1d - cosAlpha) < BENDPOINT_LINE_DIFFERENCE_ANGLE_IN_DEG) {
                                    //Lösche den aktuellen Knickpunkt
                                    //									doc.getCollection().removeBendpoint(kpc, TransactionManager.STANDARD_PID);
                                    doc.getCollection().removeBendpoint(kpc.getKnickpunktKnoten(), TransactionManager.STANDARD_PID);
                                }

                                //System.err.println("preX="+preX + "   preY="+preY + "   kpcX=" + kpcX + "   kpcY=" + kpcY + "   postX="+postX + "   postY="+postY );
                                //System.err.println("a="+a + "   b="+b + "   c=" + c);

                                //System.err.println(cosAlpha + "  " + BENDPOINT_LINE_DIFFERENCE_ANGLE_IN_DEG);
                                //System.err.println("-----------------------");
                            }
                        }

                    }

                    Tool3lgm.getContextGenerator().setElementGetroffen(false);

                    grabbed = false;
                    break;
                }

                // 3. Ob man die Ebene selbst getroffen hat
                if (Tool3lgm.getContextGenerator().getEbeneGetroffen()) {
                    Tool3lgm.getContextGenerator().setEbeneGetroffen(false);
                    break;
                }
            } //loop
        } //else

        left_button = false;
        right_button = false;
        ka = null;
        mouse_dragged = false;

        ContextGenerator.setControlled(false);
    }

    @Override
    public final void mouseEntered(final MouseEvent e) {
    }

    @Override
    public final void mouseExited(final MouseEvent e) {
    }

    // Methoden des MouseListener-Interfaces --- Ende ---

    /**
     * COMMENTME
     * 
     * @param lc
     * @param x
     * @param y
     * @return
     */
    private static final NodeContainer chooseResizable(final LayerContainer lc, final int x, final int y) {
        if (lc == null) {
            return null;
        }

        int counter;
        NodeContainer k;
        for (counter = lc.getKnotenCount() - 1; counter >= 0; counter--) {
            k = lc.getNodeContainer(counter);
            if (k.getKnoten().isUnpaintable()) {
                continue;
            }
            if (!k.isVisible()) {
                continue;
            }
            if (k.isSelected() && KnotenRenderer.getResizeCursor(k, x, y) != Cursor.DEFAULT_CURSOR) {
                return k;
            }
        }
        return null;
    }

    /**
     * COMMENTME
     * 
     * @param lc
     * @param x
     * @param y
     * @return
     */
    private final ElementContainer chooseObject(final LayerContainer lc, final int x, final int y) {
        if (lc == null) {
            return null;
        }
        int counter;
        for (counter = lc.getKnickpunkteCount() - 1; counter >= 0; counter--) {
            BendpointContainer k = lc.getBendpointContainer(counter);
            if (k.getElement().isUnpaintable()) {
                continue;
            }
            if (!k.isVisible()) {
                continue;
            }
            if (KnotenRenderer.isInside(k, x, y)) {
                return k;
            }
        }
        for (counter = lc.getKantenCount() - 1; counter >= 0; counter--) {
            EdgeContainer k = lc.getEdgeContainer(counter);
            Kante ka = k.getEdge();
            ModelElement s = ka.getStart();
            ModelElement e = ka.getEnd();
            ElementContainer sc = s.getContainer(doc);
            ElementContainer ec = e.getContainer(doc);
            if (s.isUnpaintable() || e.isUnpaintable() || sc == null || !sc.isVisible() || ec == null || !ec.isVisible()) {
                continue;
            }
            if (k.isInside(x, y)) {
                return k;
            }
        }
        for (counter = lc.getKnotenCount() - 1; counter >= 0; counter--) {
            NodeContainer k = lc.getNodeContainer(counter);
            if (k.getElement().isUnpaintable()) {
                continue;
            }
            if (!k.isVisible()) {
                continue;
            }
            if (KnotenRenderer.isInside(k, x, y)) {
                return k;
            }
        }
        return null;
    }

    // --- Methoden des MouseMotionListener-Interfaces --- Anfang ---

    @Override
    public final void mouseDragged(final MouseEvent e) {
        if (mouse_makes_trace) {
            return;
        }
        xin = e.getX();
        yin = e.getY();
        computeRealCoordinates(false);

        int ebene = doc.getCollection().getActiveLayer();

        if (mouse_selection) {
            right_sel_x = xreal[ebene];
            right_sel_y = yreal[ebene];
            repaint();
            return;
        }

        if (Tool3lgmConstants.isPopupTrigger(e)) {
            return;
        }
        if (!mouse_dragged) {
            doc.start_transaction(TransactionManager.STANDARD_PID);
            mouse_dragged = true;
        }

        final int PID = TransactionManager.STANDARD_PID;

        if (ka == null) {
            repaint();
            return;
        }

        if (sized) {

            NodeContainer kc = (NodeContainer) ka;

            int xm = kc.getX();
            int ym = kc.getY();
            int w = kc.getWidth();
            int h = kc.getHeight();

            //			float ratio = (float) w / (float) h;
            int u_bound, d_bound, r_bound, l_bound, xd, yd;

            //Beim Resizen rastern
            if (UserProperties.isUseRaster()) {
                float rasterWidth = UserProperties.getRasterWidth();
                xreal[ebene] = (int) (Math.round(xreal[ebene] / rasterWidth) * rasterWidth);
                yreal[ebene] = (int) (Math.round(yreal[ebene] / rasterWidth) * rasterWidth);
            }
            //Die resize-Hände nicht aus der Zeichenfläche lassen
            if (xreal[ebene] < -page_width / 2) {
                xreal[ebene] = -page_width / 2;
            } else if (xreal[ebene] > page_width / 2) {
                xreal[ebene] = page_width / 2;
            }
            if (yreal[ebene] < -page_height / 2) {
                yreal[ebene] = -page_height / 2;
            } else if (yreal[ebene] > page_height / 2) {
                yreal[ebene] = page_height / 2;
            }

            boolean isMoveSubElements = UserProperties.isMoveSubelements();
            UserProperties.setMoveSubelements(false);
            switch (getCursor().getType()) {
            case Cursor.DEFAULT_CURSOR:
                break;
            case Cursor.W_RESIZE_CURSOR:
                r_bound = xm + w / 2;
                xd = r_bound - xreal[ebene];
                yd = h;
                if (xd >= NodeContainer.MIN_X_SIZE && xd <= NodeContainer.MAX_X_SIZE) {
                    doc.coordinateKnot(kc, r_bound - xd / 2, ym, xd, yd, PID);
                }
                break;

            case Cursor.N_RESIZE_CURSOR:
                d_bound = ym + h / 2;
                xd = w;
                yd = d_bound - yreal[ebene];
                if (yd >= NodeContainer.MIN_Y_SIZE && yd <= NodeContainer.MAX_Y_SIZE) {
                    doc.coordinateKnot(kc, xm, d_bound - yd / 2, xd, yd, PID);
                }
                break;

            case Cursor.E_RESIZE_CURSOR:
                l_bound = xm - w / 2;
                xd = xreal[ebene] - l_bound;
                yd = h;
                if (xd >= NodeContainer.MIN_X_SIZE && xd <= NodeContainer.MAX_X_SIZE) {
                    doc.coordinateKnot(kc, l_bound + xd / 2, ym, xd, yd, PID);
                }
                break;

            case Cursor.S_RESIZE_CURSOR:
                u_bound = ym - h / 2;
                xd = w;
                yd = yreal[ebene] - u_bound;
                if (yd >= NodeContainer.MIN_Y_SIZE && yd <= NodeContainer.MAX_Y_SIZE) {
                    doc.coordinateKnot(kc, xm, u_bound + yd / 2, xd, yd, PID);
                }
                break;

            case Cursor.SW_RESIZE_CURSOR:
                r_bound = xm + w / 2;
                u_bound = ym - h / 2;
                xd = Math.abs(r_bound - xreal[ebene]);
                yd = Math.abs(yreal[ebene] - u_bound);
                if (xd >= NodeContainer.MIN_X_SIZE && xd <= NodeContainer.MAX_X_SIZE && yd >= NodeContainer.MIN_Y_SIZE && yd <= NodeContainer.MAX_Y_SIZE) {
                    doc.coordinateKnot(kc, r_bound - xd / 2, u_bound + yd / 2, xd, yd, PID);
                }
                break;

            case Cursor.NW_RESIZE_CURSOR:
                r_bound = xm + w / 2;
                d_bound = ym + h / 2;
                xd = Math.abs(r_bound - xreal[ebene]);
                yd = Math.abs(d_bound - yreal[ebene]);
                if (xd >= NodeContainer.MIN_X_SIZE && xd <= NodeContainer.MAX_X_SIZE && yd >= NodeContainer.MIN_Y_SIZE && yd <= NodeContainer.MAX_Y_SIZE) {
                    doc.coordinateKnot(kc, r_bound - xd / 2, d_bound - yd / 2, xd, yd, PID);
                }
                break;

            case Cursor.NE_RESIZE_CURSOR:
                l_bound = xm - w / 2;
                d_bound = ym + h / 2;
                xd = Math.abs(l_bound - xreal[ebene]);
                yd = Math.abs(d_bound - yreal[ebene]);
                if (xd >= NodeContainer.MIN_X_SIZE && xd <= NodeContainer.MAX_X_SIZE && yd >= NodeContainer.MIN_Y_SIZE && yd <= NodeContainer.MAX_Y_SIZE) {
                    doc.coordinateKnot(kc, l_bound + xd / 2, d_bound - yd / 2, xd, yd, PID);
                }
                break;

            case Cursor.SE_RESIZE_CURSOR:
                l_bound = xm - w / 2;
                u_bound = ym - h / 2;
                xd = Math.abs(l_bound - xreal[ebene]);
                yd = Math.abs(u_bound - yreal[ebene]);
                if (xd >= NodeContainer.MIN_X_SIZE && xd <= NodeContainer.MAX_X_SIZE && yd >= NodeContainer.MIN_Y_SIZE && yd <= NodeContainer.MAX_Y_SIZE) {
                    doc.coordinateKnot(kc, l_bound + xd / 2, u_bound + yd / 2, xd, yd, PID);
                }
                break;

            default:
                break;
            }
            UserProperties.setMoveSubelements(isMoveSubElements);
        }

        if (grabbed) {
            if (ka instanceof NodeContainer) {
                int deltaX = lastXreal[ebene] - xreal[ebene];
                int deltaY = lastYreal[ebene] - yreal[ebene];
                //Anzahl der Pixel, bei denen ein Knickpunkt statt auf dem Raster auf der gleichen Höhe oder Weite einrasten soll,
                //die sein Vorgänger- oder Nachfolger auf der Kante haben
                int bendpointTolerance = 3;

                //wenn gerastert werden soll
                if (UserProperties.isUseRaster()) {

                    //X - Richtung

                    grabbedElementsRealRect.x -= deltaX;
                    grabbedElementsRealRect.width -= deltaX;

                    float rasterWidth = UserProperties.getRasterWidth();

                    //einzelne Knickpunkte nicht nur direkt auf dem Raster docken, sondern auch auf Höhe und Weite ihrer Nachbarknickpunkte
                    boolean rasterElement = true;
                    if (ka instanceof BendpointContainer && doc.isSingleSelection()) {
                        BendpointContainer bc = (BendpointContainer) ka;
                        Point p = bc.getPredecessorPosition();
                        if (grabbedElementsRealRect.x >= p.x - bendpointTolerance && grabbedElementsRealRect.x <= p.x + bendpointTolerance) {
                            deltaX = grabbedElementsRasteredRect.x - p.x;
                            rasterElement = false;
                        } else {
                            p = bc.getSuccessorPosition();
                            if (grabbedElementsRealRect.x >= p.x - bendpointTolerance && grabbedElementsRealRect.x <= p.x + bendpointTolerance) {
                                deltaX = grabbedElementsRasteredRect.x - p.x;
                                rasterElement = false;
                            }
                        }
                    }

                    // nach links
                    if (deltaX > 0 && rasterElement) {
                        deltaX = (int) (Math.round(grabbedElementsRealRect.x / rasterWidth) * rasterWidth);
                        deltaX = grabbedElementsRasteredRect.x - deltaX;
                        //nach links verschoben -> laut Rundung soll er aber nach rechts versetzt werden -> nicht verschieben
                        if (deltaX < 0) {
                            deltaX = 0;
                            // nach rechts
                        }
                    } else if (deltaX < 0 && rasterElement) {
                        deltaX = (int) (Math.round(grabbedElementsRealRect.width / rasterWidth) * rasterWidth);
                        deltaX = grabbedElementsRasteredRect.width - deltaX;
                        //nach rechts verschoben -> laut Rundung soll er aber nach links versetzt werden -> nicht verschieben
                        if (deltaX > 0) {
                            deltaX = 0;
                        }
                    }

                    //Y - Richtung

                    grabbedElementsRealRect.y -= deltaY;
                    grabbedElementsRealRect.height -= deltaY;

                    rasterElement = true;
                    if (ka instanceof BendpointContainer && doc.isSingleSelection()) {
                        BendpointContainer bc = (BendpointContainer) ka;
                        Point p = bc.getPredecessorPosition();
                        if (grabbedElementsRealRect.y >= p.y - bendpointTolerance && grabbedElementsRealRect.y <= p.y + bendpointTolerance) {
                            deltaY = grabbedElementsRasteredRect.y - p.y;
                            rasterElement = false;
                        } else {
                            p = bc.getSuccessorPosition();
                            if (grabbedElementsRealRect.y >= p.y - bendpointTolerance && grabbedElementsRealRect.y <= p.y + bendpointTolerance) {
                                deltaY = grabbedElementsRasteredRect.y - p.y;
                                rasterElement = false;
                            }
                        }
                    }

                    // nach oben
                    if (deltaY > 0 && rasterElement) {
                        deltaY = (int) (Math.round(grabbedElementsRealRect.y / rasterWidth) * rasterWidth);
                        deltaY = grabbedElementsRasteredRect.y - deltaY;
                        if (deltaY < 0) {
                            deltaY = 0;
                            // nach unten
                        }
                    } else if (deltaY < 0 && rasterElement) {
                        deltaY = (int) (Math.round(grabbedElementsRealRect.height / rasterWidth) * rasterWidth);
                        deltaY = grabbedElementsRasteredRect.height - deltaY;
                        if (deltaY > 0) {
                            deltaY = 0;
                        }
                    }
                }
                if (deltaX > 0) {
                    int max_xdiff = page_width / 2;
                    if (grabbedElementsFullRect.x - deltaX < -max_xdiff) {
                        deltaX = grabbedElementsFullRect.x + max_xdiff;
                    }
                } else if (deltaX < 0) {
                    int max_xdiff = page_width / 2;
                    if (grabbedElementsFullRect.width - deltaX > max_xdiff) {
                        deltaX = grabbedElementsFullRect.width - max_xdiff;
                    }
                }
                if (deltaY > 0) {
                    int max_ydiff = page_height / 2;
                    if (grabbedElementsFullRect.y - deltaY < -max_ydiff) {
                        deltaY = grabbedElementsFullRect.y + max_ydiff;
                    }
                } else if (deltaY < 0) {
                    int max_ydiff = page_height / 2;
                    if (grabbedElementsFullRect.height - deltaY > max_ydiff) {
                        deltaY = grabbedElementsFullRect.height - max_ydiff;
                    }
                }

                grabbedElementsRasteredRect.x -= deltaX;
                grabbedElementsRasteredRect.width -= deltaX;
                grabbedElementsRasteredRect.y -= deltaY;
                grabbedElementsRasteredRect.height -= deltaY;

                grabbedElementsFullRect.x -= deltaX;
                grabbedElementsFullRect.width -= deltaX;
                grabbedElementsFullRect.y -= deltaY;
                grabbedElementsFullRect.height -= deltaY;

                if (multi_view) {
                    doc.moveSelectedNodeContainer(-deltaX, -deltaY, ModelConstants.NO_LAYER, PID);
                } else {
                    doc.moveSelectedNodeContainer(-deltaX, -deltaY, ebene, PID);
                }

                //wenn nicht irgendwelche Elemente gedragged werden mussten, kann hier nur noch auf einer Kante gedragged werden, da grabbed
                //nur bei Knoten oder Kanten true gesetzt wird
            } else /* if (ka instanceof EdgeContainer) */{
                //der Einfügepunkt muss der Punkt sein, an dem die Maus vor dem Draggen war, denn sonst kann es vorkommen, dass bei mouseKlicked()
                //die Kante getroffen wurde (also grabbed==true ist), aber die Koordinaten bei mouseDragged() beim ersten Drag-Schritt ausßerhalb
                //des Kantenbereichs liegen und der Index des neuen Knickpunktes nicht korrekt bestimmt werden kann.
                //Da die Einfügeposition anhand der Koodinaten betimmt wird, wird -1 übergeben.
                ka = doc.getCollection().insertBendingPoint(doc.getHashString(), ka.getElement().getHashString(), GDCommands.INVALID_HASH_STRING, lastXreal[ebene], lastYreal[ebene], GDCommands.INVALID_BENDPOINT_INDEX, TransactionManager.STANDARD_PID);
                doc.select(ka, TransactionManager.STANDARD_PID);
                findIncludingRectangles();
            }
        }
        repaint();
    }

    @Override
    public final void mouseMoved(final MouseEvent e) {
        if (left_button || right_button) {
            return;
        }
        xin = e.getX();
        yin = e.getY();
        computeRealCoordinates(false);
        //		ElementContainer ec = getMouseOverElementContainer();
        //		System.err.println(ec);
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        if (!e.isControlDown()) {
            getParent().dispatchEvent(e);
            return;
        }
        UnfloatableToolBar utb = Tool3lgm.tool.getWerkzeugleiste();
        double zoomStep = zoom - 0.05 * e.getWheelRotation();
        if (utb == null || !(utb instanceof Werkzeugleiste)) {
            setZoom(zoomStep);
        } else {
            ((Werkzeugleiste) utb).setZoom(zoomStep);
        }
    }

    // --- Methoden des MouseMotionListener-Interfaces --- Ende ---

    /**
     * Entfernt dieses Panel aus den Listener/Listen, in denen es vorkommt.
     */
    public void dispose() {
        Tool3lgm.tool.getContentPane().removeMouseListener(this);
    }

}
