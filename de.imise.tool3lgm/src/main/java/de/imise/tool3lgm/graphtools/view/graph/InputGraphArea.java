package de.imise.tool3lgm.graphtools.view.graph;

import static de.imise.tool3lgm.Static.contextGenerator;
import static de.imise.tool3lgm.Static.getMainFrame;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.LAYERS;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.MAX_LAYER_INDEX;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.MIN_LAYER_INDEX;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.NO_LAYER;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.isInterLayer;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.SubordinationEdge.SUPER_TO_SUB_DIRECTION;
import static de.imise.tool3lgm.graphtools.model.GDCommands.INVALID_BENDPOINT_INDEX;
import static de.imise.tool3lgm.graphtools.model.GDCommands.INVALID_ID_STRING;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.ELEMENT_GRAPHICS_CHANGED;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_GRAPH_MOVE_SUBELEMENTS;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_PAINT_EDGES_ONLY_FOR_SELECTED_ELEMENTS;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_USE_RASTER;
import static de.imise.tool3lgm.userproperties.UserProperties.IntProperty.PROPERTY_INT_RASTER_WIDTH;
import static java.awt.Cursor.DEFAULT_CURSOR;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;
import java.util.Set;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.gui.MainFrame;
import de.imise.tool3lgm.gui.menu.RegularContextGenerator;
import de.imise.util.ReflectionUtils;
import de.imise.util.event.InputEvents;
import de.imise.util.math.Maths;

/**
 * COMMENTME
 *
 * @author N.N., AXS
 */
public final class InputGraphArea extends BasicGraphArea implements MouseListener, MouseMotionListener, MouseWheelListener {

    /**
     * Toleranz in Pixel, die im Abstand zwischen 2 Knickpunten in beiden
     * Dimensionen unterschritten sein muss, damit sie als übereinander leigend
     * gelten.
     */
    private static final int BENDPOINT_OVERLAY_TOLERANCE = 5;

    /**
     * Mit diesem Wert wird bestimmt, ob ein Knickpunkt auf die Linie zwischen
     * seinen ihn umgebenden Knickpunkten verschoben wurde. Es ist die
     * Abweichung zw. dem Cosinus des Winkels zwischen der Linie vom
     * Vorgängerknickpunkt des verschobenen Knickpunktes und dem verschobenen
     * Knickpunkt selbst sowie der Linie zwischen Vorgänger und Nachfolger.
     * Unterschreitet der Kosinus diesen Wert, gilt der verschobene Knickpunkt
     * als auf der Linie liegend.
     */
    private static final double BENDPOINT_LINE_DIFFERENCE_ANGLE_IN_DEG = 0.0002;

    /**
     * X- und Y-Koordinate, an die auf dieser Komponente geklickt wurde.
     */
    private int xin = 0, yin = 0;

    /**
     * Für jede der 5 Ebenen eine X und Y-Koordinate in die die
     * Input-Koordinaten je nach dargestelltem Ausschnitt, Neigung usw. der
     * Ebene umgerechnet wird.
     */
    private final int[] xreal = new int[LAYERS.length], yreal = new int[LAYERS.length];

    /**
     * Im Grunde das gleiche wie <code>xreal</code> und <code>yreal</code>. Beim
     * Draggen auf Kanten und der Entstehung der neuen Knickpunkte muss man aber
     * die Koordinaten kennen, bei denen die Maus vor dem Draggen war, sonst
     * haut die Positionsbestimmung des neuen Knickpunktes nicht hin.
     */
    private final int[] lastXreal = new int[LAYERS.length], lastYreal = new int[LAYERS.length];

    /**
     * Wenn Node verschoben werden, dann grenzen diese Koordinaten den minimalen
     * Bereich ein, in dem alle Elemente liegen. Dies ist der Bereich, in dem
     * die Elemente liegen würden, wenn ohne Raster verschoben wird. ACHTUNG:
     * <code>height</code> und <code>width</code> dieses Rechtecks sind nicht
     * die wirkliche Weite sondern die Koordinaten des Punktes der rechten
     * unteren Ecke.
     */
    public static Rectangle grabbedElementsRealRect;

    /**
     * Wenn Node verschoben werden, dann grenzen diese Koordinaten den minimalen
     * Bereich ein, in dem alle Elemente liegen. Dies ist der Bereich, in dem
     * die Elemente liegen würden, wenn mit Raster verschoben wird. ACHTUNG:
     * <code>height</code> und <code>width</code> dieses Rechtecks sind nicht
     * die wirkliche Weite sondern die Koordinaten des Punktes der rechten
     * unteren Ecke.
     */
    public static Rectangle grabbedElementsRasteredRect;

    /**
     * Wenn Node verschoben werden, dann grenzen diese Koordinaten den minimalen
     * Bereich ein, in dem alle Elemente liegen. Dies ist der Bereich, in dem
     * die Elemente und alle ihre evtl. nicht selektierten, aber bei der "mit
     * Teilelementen verschieben"-Option ebenfalls verschobenen Elemente.
     * ACHTUNG: <code>height</code> und <code>width</code> dieses Rechtecks sind
     * nicht die wirkliche Weite sondern die Koordinaten des Punktes der rechten
     * unteren Ecke.
     */
    public static Rectangle grabbedElementsFullRect;

    /** <code>true</code>, wenn die linke Maustaste gedrückt wurde */
    private boolean left_button = false;

    /** <code>true</code>, wenn die rechte Maustaste gedrückt wurde */
    private boolean right_button = false;

    /**
     * <code>true</code>, wenn ein Element mit beim Mausklick getroffen wurde
     */
    private boolean grabbed = false;

    /**
     * <code>true</code>, wenn bei einem Element auf einen Resize-Button
     * geklickt wurde
     */
    private boolean sized = false;

    /**
     * Elementklasse, die angelegt werden soll, wenn die beim Klick neue
     * Elemente erzeugt werden sollen
     */
    private Class<? extends Node> mouse_makes_node;

    /**
     * <code>true</code>, wenn beim Mausklick eine Edge angelegt werden soll
     */
    private boolean mouse_makes_edge = false;

    /** <code>true</code>, wenn die Maus gedragged wird */
    private boolean mouse_dragged = false;

    /** Postion of the mouse before the current drag step */
    private Point lastDragPosition = null;

    /** Element, das angeklickt wurde */
    private ElementContainer clickedEc;

    /**
     * COMMENTME
     */
    private boolean was_selected = false;

    /**
     * COMMENTME
     *
     * @param doc
     */
    public InputGraphArea(final GraphDocument doc) {
        super(doc);
        if (doc instanceof Szenario) {
            addMouseListener(this);
            addMouseMotionListener(this);
            addMouseWheelListener(this);
            MainFrame mainFrame = getMainFrame();
            mainFrame.addMouseListener(this);
            xin = 0;
            yin = 0;
            grabbed = false;
            sized = false;
            setMouseClickCreatesNode(null);
            check_size();
        }
    }

    // --- kleine Hilfsmethoden --- Anfang ---
    @Override
    public void setMultiView(final boolean b) {
        multiView = b;
        findIncludingRectangles();
        super.setMultiView(b);
    }
    // --- kleine Hilfsmethoden --- Ende ---

    // --- Methoden zur Statusveraenderung --- Anfang ---
    /**
     * COMMENTME
     *
     * @param k
     */
    public final void setMouseClickCreatesNode(final Class<? extends Node> k) {
        if (k == null) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } else {
            mouse_makes_edge = false;
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        }
        mouse_makes_node = k;
    }

    /**
     * COMMENTME
     *
     * @param b
     */
    public final void setMouseCreatesEdge(final boolean b) {
        if (b) {
            mouse_makes_node = null;
        }
        mouse_makes_edge = b;
        if (b) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public final Class<? extends Node> getMouseMakesNodeClass() {
        return mouse_makes_node;
    }

    public final boolean isMouseMakesEdge() {
        return mouse_makes_edge;
    }

    // --- Methoden zur Statusveraenderung --- Ende ---

    // --- Methoden der Rueckberechnung von Koordinaten --- Anfang ---
    /**
     * @param initLastAsReal
     */
    private final void computeRealCoordinates(final boolean initLastAsReal) {
        if (!initLastAsReal) {
            for (int layerIndex : LAYERS) {
                if (!isInterLayer(layerIndex)) {
                    lastXreal[layerIndex] = xreal[layerIndex];
                    lastYreal[layerIndex] = yreal[layerIndex];
                }
            }
        }
        int visibleLayerCount = 0; // das hier ist nur dazu, damit man die Stelle findet, wenn die InterLayer weg sind
        for (int layerIndex : LAYERS) {
            if (!isInterLayer(layerIndex)) {
                visibleLayerCount++;
            }
        }
        int indexOffset = -visibleLayerCount / 2;
        double y = (yin - middleY) / zoom;
        double x = (xin - middleX) / zoom;
        for (int layerIndex : LAYERS) {
            if (!isInterLayer(layerIndex)) {
                yreal[layerIndex] = (int) ((y + indexOffset++ * layerGap) / y_y_factor);
                xreal[layerIndex] = (int) (x - yreal[layerIndex] * y_x_factor);
            }
        }
        if (initLastAsReal) {
            for (int layerIndex : LAYERS) {
                if (!isInterLayer(layerIndex)) {
                    lastXreal[layerIndex] = xreal[layerIndex];
                    lastYreal[layerIndex] = yreal[layerIndex];
                }
            }
        }
    }

    /**
     * Berechnet in jeder Richtung die minimalen und maximalen Koordinaten von
     * der übergebenen {@link Rectangle} und dem {@link ElementContainer}. Wird
     * als {@link Rectangle} <code>null</code> übergeben, dann kommt ein neues
     * {@link Rectangle}-Objekt zurück, ansonsten wird das bestehende zurück
     * gegeben. Die Weite und Höhe des {@link Rectangle} geben Koordinaten an
     * und nicht die Weite und Höhe im eigentlichen Sinne
     *
     * @param rect {@link Rectangle}, die verändert wird, falls die Koordinaten
     *            des übergebenen {@link ElementContainer}s außerhalb der vorher
     *            bestehenden Dimasion lagen
     * @param ec {@link ElementContainer}, dessen Koordinaten in der übergebenen
     *            {@link Rectangle} liegen sollen
     * @return die übergebenen {@link Rectangle} oder wenn <code>null</code>
     *         übergeben wurde eine neue {@link Rectangle}
     */
    private static final Rectangle getIncludingRectangle(final Rectangle rect, final ElementContainer ec) {
        int w = ec.getWidth();
        int h = ec.getHeight();
        int x = ec.getX();
        int y = ec.getY();
        int realx1 = x - (w >> 1);
        int realy1 = y - (h >> 1);
        int realx2 = realx1 + w;
        int realy2 = realy1 + h;
        return getIncludingRectangle(rect, realx1, realy1, realx2, realy2);
    }

    /**
     * /** Berechnet in jeder Richtung die minimalen und maximalen Koordinaten
     * von der übergebenen {@link Rectangle} und dem {@link ElementContainer}.
     * Wird als {@link Rectangle} <code>null</code> übergeben, dann kommt ein
     * neues {@link Rectangle}-Objekt zurück, ansonsten wird das bestehende
     * zurück gegeben. Die Weite und Höhe des {@link Rectangle} geben
     * Koordinaten an und nicht die Weite und Höhe im eigentlichen Sinne
     *
     * @param rect {@link Rectangle}, die verändert wird, falls die Koordinaten
     *            des übergebenen {@link ElementContainer}s außerhalb der vorher
     *            bestehenden Dimasion lagen
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
     * Füllt die 3 {@link Rectangle} {@link #grabbedElementsFullRect},
     * {@link #grabbedElementsRasteredRect} und {@link #grabbedElementsRealRect}
     * mit den Koordinaten in Abhängigkeit von der Selektion. Wenn eine
     * Einzelebenenansicht eingeschaltet ist, dann werden nur Elemente der
     * aktuellen Ebene einbezogen sonst alle.
     */
    private void findIncludingRectangles() {
        boolean singleSelection = szenario.isSingleSelection();
        if (singleSelection && clickedEc instanceof BendpointContainer) {
            //bei einzelnen KnickpunktContainern ist nur der Mittelpunkt das selektierte Rechteck
            grabbedElementsRealRect = new Rectangle(clickedEc.getX(), clickedEc.getY(), clickedEc.getX(), clickedEc.getY());
            grabbedElementsRasteredRect = new Rectangle(grabbedElementsRealRect);
            grabbedElementsFullRect = new Rectangle(grabbedElementsRealRect);
        } else if (clickedEc instanceof NodeContainer) {
            // bei KnotenContainern (einem oder mehrere) wird das die Selektion einschließende
            // Rechteck inklusive der evtl. mitzubewegenden Teilelemente berechnet
            grabbedElementsRealRect = null;
            grabbedElementsFullRect = null;
            GDCollection gdcoll = szenario.getCollection();
            int ebene = gdcoll.getActiveLayer();
            for (NodeContainer kc : szenario.getSelectedRealElementContainerIterable()) {
                if (!kc.isVisible() || !multiView && kc.layerFor() != ebene) {
                    continue;
                }
                grabbedElementsRealRect = getIncludingRectangle(grabbedElementsRealRect, kc);
                if (OPTION_GRAPH_MOVE_SUBELEMENTS.is()) {
                    for (ElementContainer ec : kc.getElement().getSubordinatedContainers(szenario)) {
                        ModelElement me = ec.getElement();
                        if (!me.isPaintable() || !ec.isVisible() || !multiView && kc.layerFor() != ebene) {
                            continue;
                        }
                        if (grabbedElementsFullRect == null) {
                            grabbedElementsFullRect = new Rectangle(grabbedElementsRealRect);
                        }
                        grabbedElementsFullRect = getIncludingRectangle(grabbedElementsFullRect, ec);
                        for (Edge edge : me.getEdgesWith(clickedEc.getElement())) {
                            EdgeContainer edgeC = edge.getContainer(szenario);
                            if (edgeC != null) {
                                for (BendpointContainer bc : edgeC.iterateBendpointContainers()) {
                                    grabbedElementsFullRect = getIncludingRectangle(grabbedElementsFullRect, bc);
                                }
                            }
                        }
                    }
                }
            }
            for (BendpointContainer kc : szenario.getSelectedBendpointContainerIterable()) {
                if (multiView || !multiView && kc.layerFor() == ebene) {
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

    // --- Methoden der Rueckberechnung von Koordinaten --- Ende ---

    // Methoden des MouseListener-Interfaces --- Anfang ---

    @Override
    @SuppressWarnings("deprecation")
    public final void mouseClicked(final MouseEvent e) {
        int clickCount = e.getClickCount();

        //only double clicks are relevant here
        if (clickCount <= 1) {
            return;
        }

        boolean controlKeyPressed = InputEvents.isOperatingSystemDependentCTRLorCMDorSHIFTdown(e);
        contextGenerator.setControlled(controlKeyPressed);

        ElementContainer ecUnderMouse = getElementContainerUnderMousePointer();
        if (ecUnderMouse != null) {
            szenario.select(ecUnderMouse, 0);
        }
        //ATTENTION DEPRECATION WARNING
        //Under no circumstances simply replace the deprecated expressions.
        //This is especially true for getModifiers() and getModifiersEx(),
        //because the non-deprecated function getModifiersEx() does not have
        //the same result in mouseClicked() but only in mousePressed()! But
        //here we are in mouseClicked().
        //Same Problem in SearchDialogResultTablePanel
        //int modifiers = e.getModifiersEx(); NO!!!!!!!!!!!
        int modifiers = e.getModifiers();
        // Verknüpftes Teilmodell öffnen
        int modifiersMask = modifiers & InputEvent.ALT_MASK;
        if (modifiersMask != 0) {
            // Component source, int id, long when, int modifiers,
            // int keyCode, char keyChar, int keyLocation
            dispatchEvent(new KeyEvent(this, KeyEvent.KEY_RELEASED, 0l, 0, KeyEvent.VK_ALT, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD));
            Static.getTool().changeToLinked(szenario);
            return;
        }
        // Teilobjekte zeigen oder verstecken
        if ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
            szenario.switchExpansionState(TransactionManager.STANDARD_PID);
            return;
        }
        // Knickpunkte bei Doppelklicks löschen
        if (ecUnderMouse instanceof BendpointContainer) {
            szenario.getCollection().removeBendpoint(((BendpointContainer) ecUnderMouse).getBendpoint(), TransactionManager.STANDARD_PID);
        } else if (ecUnderMouse != null) {
            ModelElement me = ecUnderMouse.getElement();
            me.showPropertyDialog();
        }
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        //we have to request the focus explicitely so that this
        //component really gets the CTRL + A (Select all) key
        //events after leaving the template browser. If we don't
        //request the focus here then the template browser
        //will execute the select all command.
        Object source = e.getSource();
        if (source instanceof Component) {
            Component c = (Component) source;
            c.requestFocus();
        }

        boolean controlKeyPressed = InputEvents.isOperatingSystemDependentCTRLorCMDorSHIFTdown(e);
        contextGenerator.setControlled(controlKeyPressed);

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
        for (int layerIndex = MAX_LAYER_INDEX; layerIndex >= MIN_LAYER_INDEX; layerIndex--) {
            if (isInterLayer(layerIndex)) {
                continue;
            }
            final GDCollection gdcoll = szenario.getCollection();
            final int activeLayerIndex = gdcoll.getActiveLayer();
            // Bei flacher Ansicht soll nur die aktive Schicht durchsucht werden.
            if (!multiView && layerIndex != activeLayerIndex) {
                continue;
            }
            final LayerContainer layer = szenario.getLayer(layerIndex);
            final int x = xreal[layerIndex];
            final int y = yreal[layerIndex];
            final int rasterWidth = PROPERTY_INT_RASTER_WIDTH.get();
            final int insertPositionX = Math.round(x / rasterWidth) * rasterWidth;
            final int insertPositionY = Math.round(y / rasterWidth) * rasterWidth;
            szenario.setNodeContainerInsertPosition(insertPositionX, insertPositionY);
            if (mouse_makes_edge && left_button) {
                szenario.deselectAll(false);
                clickedEc = chooseObject(layer, x, y);
                if (clickedEc != null) {
                    szenario.select(clickedEc, 0);
                    revalidateRepaint();
                    break;
                }
            } else if (mouse_makes_node != null && left_button) {
                if (isInPage(x, y)) {
                    if (szenario.getMetaModel().layerFor(mouse_makes_node) == layerIndex) {
                        szenario.createNodeAndContainer(mouse_makes_node, STANDARD_PID);
                        revalidateRepaint();
                    }
                }
            } else {
                // Jede einzelne der 3 Ebenen wird erstmal durchkaemmt.
                clickedEc = null;
                final RegularContextGenerator contextGenerator = Static.contextGenerator;
                // 1. Ob man in eine Hand eines Knotens getroffen hat
                clickedEc = chooseResizable(layer, x, y);
                if (clickedEc != null) {
                    contextGenerator.setElementContainer(clickedEc);
                    contextGenerator.setResizing(true);
                    if (left_button && layerIndex != activeLayerIndex) {
                        gdcoll.setActiveLayer(layerIndex);
                    }
                    setCursor(new Cursor(NodeRenderer.getLastResizeCursor()));
                    sized = true;

                    // when resizing elements, the original positions of the element and its subelements
                    // are saved while resizing, and the repositioning is calculated with these
                    // original positions
                    ModelElement clickedMe = clickedEc.getElement();
                    List<ElementContainer> subordinatedContainers = clickedMe.getConnectedContainers(doc, CompositionEdge.class, SUPER_TO_SUB_DIRECTION);
                    doc.setOriginalPositions(subordinatedContainers, clickedEc);

                    contextGenerator.processMouseEvent(left_button, right_button, this, xin, yin);
                    break;
                }
                // 2. Ob man in ein Objekt direkt getroffen hat: Node oder Edge
                clickedEc = null;
                clickedEc = chooseObject(layer, x, y);
                //System.out.println("    start context generating..." + System.currentTimeMillis());
                if (clickedEc != null) {
                    contextGenerator.setElementContainer(clickedEc);
                    contextGenerator.setElementClicked(true);
                    if (left_button && layerIndex != activeLayerIndex) {
                        gdcoll.setActiveLayer(layerIndex);
                    }
                    if (clickedEc.isSelected()) {
                        was_selected = true;
                        szenario.addToSelection(clickedEc, TransactionManager.STANDARD_PID);
                    } else {
                        contextGenerator.processMouseEvent(left_button, right_button, this, xin, yin);
                    }
                    findIncludingRectangles();
                    grabbed = true;
                    setCursor(new Cursor(Cursor.MOVE_CURSOR));
                    break;
                }
                // 3. Ob man die Ebene selbst getroffen hat
                {
                    contextGenerator.setLayerClicked(true);
                    if (isMultiView()) {
                        for (int j = layerIndex; j >= MIN_LAYER_INDEX; j--) {
                            if (!isInterLayer(j)) {
                                if (isInPage(xreal[j], yreal[j])) {
                                    if (j != activeLayerIndex) {
                                        gdcoll.setActiveLayer(j);
                                        layerIndex++;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    if (layerIndex == activeLayerIndex) {
                        left_sel_x = x;
                        left_sel_y = y;
                        right_sel_x = x;
                        right_sel_y = y;
                        if (left_button) {
                            mouse_selection = true;
                        }
                        contextGenerator.processMouseEvent(left_button, right_button, this, xin, yin);
                        break;
                    }
                }
            }
        }
        // System.err.println(" finished" + System.currentTimeMillis() / 100);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        lastDragPosition = null;

        if (mouse_selection) {
            if (left_sel_y != right_sel_y || left_sel_x != right_sel_x) {
                szenario.selectArea(left_sel_x, left_sel_y, right_sel_x, right_sel_y);
            }
            mouse_selection = false;
            return;
        }
        if (sized || grabbed) {
            setCursor(new Cursor(DEFAULT_CURSOR));
        }
        if (mouse_dragged) {
            szenario.finish_transaction(STANDARD_PID);
            szenario.distributeEvent(ELEMENT_GRAPHICS_CHANGED);
        }
        xin = e.getX();
        yin = e.getY();
        computeRealCoordinates(false);
        final RegularContextGenerator contextGenerator = Static.contextGenerator;
        if (mouse_makes_edge && left_button) {
            for (int layerIndex = MAX_LAYER_INDEX; layerIndex >= MIN_LAYER_INDEX; layerIndex--) {
                if (isInterLayer(layerIndex)) {
                    continue;
                }
                final LayerContainer layer = szenario.getLayer(layerIndex);
                if (!multiView && layer != szenario.getActiveLayer()) {
                    continue;
                }
                clickedEc = chooseObject(layer, xreal[layerIndex], yreal[layerIndex]);
                if (clickedEc != null) {
                    szenario.addToSelection(clickedEc, STANDARD_PID);
                    ElementContainer lastSelected = szenario.getLastSelected();
                    ModelElement lastSelectedElement = lastSelected.getElement();
                    Class<? extends ModelElement> lastSelectedClass = lastSelectedElement.getClass();
                    Set<Class<? extends ModelElement>> selectedRealElementClasses = szenario.getSelectedRealElementClasses();
                    if (!selectedRealElementClasses.isEmpty()) {
                        Class<? extends ModelElement> otherSelectedClass = ReflectionUtils.getCommonSuperClassOfClasses(selectedRealElementClasses);
                        Class<? extends Edge> edgeClass = RegularContextGenerator.requestCurrentEdgeType(szenario.getMetaModel(), lastSelectedClass, otherSelectedClass);
                        if (edgeClass != null) {
                            szenario.linkSelected(edgeClass, BACKWARD, STANDARD_PID);
                        }
                    }
                    revalidateRepaint();
                    break;
                }
            }
        } else if (mouse_makes_node != null && left_button) {
        } else {
            // Jede einzelne der 3 Ebenen wird erstmal durchkaemmt.
            for (int layerIndex = MAX_LAYER_INDEX; layerIndex >= MIN_LAYER_INDEX; layerIndex--) {
                if (isInterLayer(layerIndex)) {
                    continue;
                }
                final LayerContainer layer = szenario.getLayer(layerIndex);
                // Bei flacher Ansicht soll nur die aktive Schicht durchsucht werden.
                if (!multiView && layer != szenario.getActiveLayer()) {
                    continue;
                }
                // 1. Ob man in eine Hand eines Knotens getroffen hat
                if (contextGenerator.isResizing()) {
                    contextGenerator.setResizing(false);
                    sized = false;
                    doc.setOriginalPositions(null, null);
                    break;
                }
                // 2. Ob man in ein Objekt direkt getroffen hat: Node oder Edge
                if (contextGenerator.isElementClicked()) {
                    if (was_selected) {
                        was_selected = false;
                        if (!mouse_dragged) {
                            contextGenerator.processMouseEvent(left_button, right_button, this, xin, yin);
                        }
                    }
                    // wenn ein Knickpunkt gedraggt wurde
                    if (mouse_dragged && clickedEc != null && clickedEc instanceof BendpointContainer) {
                        //Prüfe ob er gelöscht werden soll. Das soll er, wenn er auf einer Linie
                        //zwischen den anderen Knickpunkten oder den Endpunkten der Edge liegt
                        BendpointContainer bc = (BendpointContainer) clickedEc;
                        Point pos = bc.getPosition();
                        Point prePos = bc.getPredecessorPosition();
                        Point postPos = bc.getSuccessorPosition();
                        //wenn der Knickpunkt auf seinen Vorgänger- oder Nachfolgerknickpunkt gedragged wurde -> lösche ihn
                        if (isSamePointInBendpointTolerance(pos, prePos, postPos)) {
                            removeBendpoint(szenario, bc);
                            //prüfe, ob der Knickpunkt mit einer gewissen Toleranz auf fast einer Linie mit seinen Außenpunkten
                            // liegt -> wenn ja -> lösche ihn
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
                            // dasselbe wie xOut nur für y
                            boolean yOut = pos.y < prePos.y && pos.y < postPos.y || pos.y > prePos.y && pos.y > postPos.y;
                            // System.err.println(xOut + " " + yOut);
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
                                // Cosinus des Winkels zw. a und c
                                double cosAlpha = (-(a * a) + b * b + c * c) / (2d * b * c);
                                //Wenn die Differenz der beiden Winkel die Toleranz unterschreitet = die Knickpunkte
                                //liegen nahezu auf einer Linie (der Cosinus ist 1, wenn der Winkel 0 Grad beträgt ->
                                // daher die Abweichung von 1 bestimmen)
                                if (Math.abs(1d - cosAlpha) < BENDPOINT_LINE_DIFFERENCE_ANGLE_IN_DEG) {
                                    // Lösche den aktuellen Knickpunkt
                                    removeBendpoint(szenario, bc);
                                }
                                //System.err.println("preX="+preX + "   preY="+preY + "   kpcX=" + kpcX + "   kpcY=" + kpcY + "   postX="+postX + "   postY="+postY );
                                //System.err.println("a="+a + "   b="+b + "   c=" + c);
                                //System.err.println(cosAlpha + "  " + BENDPOINT_LINE_DIFFERENCE_ANGLE_IN_DEG);
                                // System.err.println("-----------------------");
                            }
                        }
                    }
                    contextGenerator.setElementClicked(false);
                    grabbed = false;
                    break;
                }
                // 3. Ob man die Ebene selbst getroffen hat
                if (contextGenerator.isLayerClicked()) {
                    contextGenerator.setLayerClicked(false);
                    break;
                }
            } // loop
        } // else
        left_button = false;
        right_button = false;
        clickedEc = null;
        mouse_dragged = false;
        contextGenerator.setControlled(false);
    }

    @Override
    public final void mouseEntered(final MouseEvent e) {
    }

    @Override
    public final void mouseExited(final MouseEvent e) {
    }

    /**
     * @param szen
     * @param bc
     */
    private void removeBendpoint(final Szenario szen, final BendpointContainer bc) {
        GDCollection gdcoll = szenario.getCollection();
        Bendpoint bendpoint = bc.getBendpoint();
        gdcoll.removeBendpoint(bendpoint, STANDARD_PID);
    }

    /**
     * @return the first element container under the mouse pointer or
     *         <code>null</code> if there is no element container
     */
    private final ElementContainer getElementContainerUnderMousePointer() {
        ElementContainer returnContainer = null;
        if (multiView) {
            for (int c = MAX_LAYER_INDEX; c >= MIN_LAYER_INDEX; c--) {
                if (!isInterLayer(c)) {
                    LayerContainer lc = szenario.getLayer(c);
                    returnContainer = chooseObject(lc, xreal[c], yreal[c]);
                    if (returnContainer != null) {
                        break;
                    }
                }
            }
        } else {
            GDCollection gdcoll = szenario.getCollection();
            int layer = gdcoll.getActiveLayer();
            LayerContainer lc = szenario.getLayer(layer);
            returnContainer = chooseObject(lc, xreal[layer], yreal[layer]);
        }
        return returnContainer;
    }
    /**
     * @param x
     * @param y
     * @return
     */
    private final boolean isInPage(final int x, final int y) {
        int halfPageWidth = layerWidth / 2;
        int halfPageHeight = layerHeight / 2;
        return -halfPageWidth < x && x < halfPageWidth && -halfPageHeight < y && y < halfPageHeight;
    }

    /**
     * @param p1
     * @param p2
     * @param tolerance
     * @return
     */
    private static final boolean isSamePoint(final Point p1, final Point p2, final int tolerance) {
        return Math.abs(p1.x - p2.x) < tolerance && Math.abs(p1.y - p2.y) < tolerance;
    }

    /**
     * @param p
     * @param p1
     * @param p2
     * @return
     */
    private static final boolean isSamePointInBendpointTolerance(final Point p, final Point p1, final Point p2) {
        return isSamePoint(p1, p, BENDPOINT_OVERLAY_TOLERANCE) || isSamePoint(p2, p, BENDPOINT_OVERLAY_TOLERANCE);
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
        for (NodeContainer k : lc.getNodeContainersBackward()) {
            if (!k.getElement().isPaintable()) {
                continue;
            }
            if (!k.isVisible()) {
                continue;
            }
            if (k.isSelected() && NodeRenderer.getResizeCursor(k, x, y) != Cursor.DEFAULT_CURSOR) {
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
        if (OPTION_PAINT_EDGES_ONLY_FOR_SELECTED_ELEMENTS.isNot()) {
            for (counter = lc.getBendpointContainerCount() - 1; counter >= 0; counter--) {
                BendpointContainer k = lc.getBendpointContainer(counter);
                if (!k.getElement().isPaintable()) {
                    continue;
                }
                if (!k.isVisible()) {
                    continue;
                }
                if (NodeRenderer.isInside(k, x, y)) {
                    return k;
                }
            }
            for (EdgeContainer k : lc.getEdgeContainersBackward()) {
                Edge ka = k.getEdge();
                ModelElement s = ka.getStart();
                if (!s.isPaintable()) {
                    continue;
                }
                ModelElement e = ka.getEnd();
                if (!e.isPaintable()) {
                    continue;
                }
                ElementContainer sc = s.getContainer(szenario);
                if (sc == null || !sc.isVisible()) {
                    continue;
                }
                ElementContainer ec = e.getContainer(szenario);
                if (ec == null || !ec.isVisible()) {
                    continue;
                }
                if (k.isInside(x, y)) {
                    return k;
                }
            }
        }
        for (NodeContainer k : lc.getNodeContainersBackward()) {
            if (!k.getElement().isPaintable()) {
                continue;
            }
            if (!k.isVisible()) {
                continue;
            }
            if (NodeRenderer.isInside(k, x, y)) {
                return k;
            }
        }
        return null;
    }

    // --- Methoden des MouseMotionListener-Interfaces --- Anfang ---
    @Override
    public final void mouseDragged(final MouseEvent e) {
        if (mouse_makes_edge) {
            return;
        }
        xin = e.getX();
        yin = e.getY();

        computeRealCoordinates(false);
        GDCollection gdcoll = szenario.getCollection();
        int ebene = gdcoll.getActiveLayer();
        if (mouse_selection) {
            right_sel_x = xreal[ebene];
            right_sel_y = yreal[ebene];
            repaint();
            return;
        }
        if (Tool3lgmConstants.isPopupTrigger(e)) {
            contextGenerator.closeMenu();
            dragView(e);
            return;
        }
        if (!mouse_dragged) {
            szenario.start_transaction(STANDARD_PID);
            mouse_dragged = true;
        }
        if (clickedEc == null) {
            repaint();
            return;
        }
        if (sized) {
            NodeContainer kc = (NodeContainer) clickedEc;
            int xm = kc.getX();
            int ym = kc.getY();
            int w = kc.getWidth();
            int h = kc.getHeight();
            // float ratio = (float) w / (float) h;
            int u_bound, d_bound, r_bound, l_bound, xd, yd;
            // Beim Resizen rastern
            if (OPTION_USE_RASTER.is()) {
                float rasterWidth = PROPERTY_INT_RASTER_WIDTH.get();
                xreal[ebene] = (int) (Math.round(xreal[ebene] / rasterWidth) * rasterWidth);
                yreal[ebene] = (int) (Math.round(yreal[ebene] / rasterWidth) * rasterWidth);
            }
            // Die resize-Hände nicht aus der Zeichenfläche lassen
            if (xreal[ebene] < -layerWidth / 2) {
                xreal[ebene] = -layerWidth / 2;
            } else if (xreal[ebene] > layerWidth / 2) {
                xreal[ebene] = layerWidth / 2;
            }
            if (yreal[ebene] < -layerHeight / 2) {
                yreal[ebene] = -layerHeight / 2;
            } else if (yreal[ebene] > layerHeight / 2) {
                yreal[ebene] = layerHeight / 2;
            }
            //            boolean isMoveSubElements = OPTION_GRAPH_MOVE_SUBELEMENTS.set(false);
            switch (getCursor().getType()) {
            case Cursor.DEFAULT_CURSOR:
                break;
            case Cursor.W_RESIZE_CURSOR:
                r_bound = xm + w / 2;
                xd = r_bound - xreal[ebene];
                yd = h;
                if (xd >= NodeContainer.MIN_X_SIZE && xd <= NodeContainer.MAX_X_SIZE) {
                    szenario.moveNodeContainer(kc, r_bound - xd / 2, ym, xd, yd, STANDARD_PID);
                }
                break;
            case Cursor.N_RESIZE_CURSOR:
                d_bound = ym + h / 2;
                xd = w;
                yd = d_bound - yreal[ebene];
                if (yd >= NodeContainer.MIN_Y_SIZE && yd <= NodeContainer.MAX_Y_SIZE) {
                    szenario.moveNodeContainer(kc, xm, d_bound - yd / 2, xd, yd, STANDARD_PID);
                }
                break;
            case Cursor.E_RESIZE_CURSOR:
                l_bound = xm - w / 2;
                xd = xreal[ebene] - l_bound;
                yd = h;
                if (xd >= NodeContainer.MIN_X_SIZE && xd <= NodeContainer.MAX_X_SIZE) {
                    szenario.moveNodeContainer(kc, l_bound + xd / 2, ym, xd, yd, STANDARD_PID);
                }
                break;
            case Cursor.S_RESIZE_CURSOR:
                u_bound = ym - h / 2;
                xd = w;
                yd = yreal[ebene] - u_bound;
                if (yd >= NodeContainer.MIN_Y_SIZE && yd <= NodeContainer.MAX_Y_SIZE) {
                    szenario.moveNodeContainer(kc, xm, u_bound + yd / 2, xd, yd, STANDARD_PID);
                }
                break;
            case Cursor.SW_RESIZE_CURSOR:
                r_bound = xm + w / 2;
                u_bound = ym - h / 2;
                xd = Math.abs(r_bound - xreal[ebene]);
                yd = Math.abs(yreal[ebene] - u_bound);
                if (xd >= NodeContainer.MIN_X_SIZE && xd <= NodeContainer.MAX_X_SIZE && yd >= NodeContainer.MIN_Y_SIZE && yd <= NodeContainer.MAX_Y_SIZE) {
                    szenario.moveNodeContainer(kc, r_bound - xd / 2, u_bound + yd / 2, xd, yd, STANDARD_PID);
                }
                break;
            case Cursor.NW_RESIZE_CURSOR:
                r_bound = xm + w / 2;
                d_bound = ym + h / 2;
                xd = Math.abs(r_bound - xreal[ebene]);
                yd = Math.abs(d_bound - yreal[ebene]);
                if (xd >= NodeContainer.MIN_X_SIZE && xd <= NodeContainer.MAX_X_SIZE && yd >= NodeContainer.MIN_Y_SIZE && yd <= NodeContainer.MAX_Y_SIZE) {
                    szenario.moveNodeContainer(kc, r_bound - xd / 2, d_bound - yd / 2, xd, yd, STANDARD_PID);
                }
                break;
            case Cursor.NE_RESIZE_CURSOR:
                l_bound = xm - w / 2;
                d_bound = ym + h / 2;
                xd = Math.abs(l_bound - xreal[ebene]);
                yd = Math.abs(d_bound - yreal[ebene]);
                if (xd >= NodeContainer.MIN_X_SIZE && xd <= NodeContainer.MAX_X_SIZE && yd >= NodeContainer.MIN_Y_SIZE && yd <= NodeContainer.MAX_Y_SIZE) {
                    szenario.moveNodeContainer(kc, l_bound + xd / 2, d_bound - yd / 2, xd, yd, STANDARD_PID);
                }
                break;
            case Cursor.SE_RESIZE_CURSOR:
                l_bound = xm - w / 2;
                u_bound = ym - h / 2;
                xd = Math.abs(l_bound - xreal[ebene]);
                yd = Math.abs(u_bound - yreal[ebene]);
                if (xd >= NodeContainer.MIN_X_SIZE && xd <= NodeContainer.MAX_X_SIZE && yd >= NodeContainer.MIN_Y_SIZE && yd <= NodeContainer.MAX_Y_SIZE) {
                    szenario.moveNodeContainer(kc, l_bound + xd / 2, u_bound + yd / 2, xd, yd, STANDARD_PID);
                }
                break;
            default:
                break;
            }
            //            OPTION_GRAPH_MOVE_SUBELEMENTS.set(isMoveSubElements);
        }
        if (grabbed) {
            if (clickedEc instanceof NodeContainer) {
                int deltaX = lastXreal[ebene] - xreal[ebene];
                int deltaY = lastYreal[ebene] - yreal[ebene];
                //Anzahl der Pixel, bei denen ein Knickpunkt statt auf dem Raster auf der gleichen Höhe oder Weite einrasten soll,
                // die sein Vorgänger- oder Nachfolger auf der Edge haben
                int bendpointTolerance = 3;
                // wenn gerastert werden soll
                if (OPTION_USE_RASTER.is()) {
                    // X - Richtung
                    grabbedElementsRealRect.x -= deltaX;
                    grabbedElementsRealRect.width -= deltaX;
                    float rasterWidth = PROPERTY_INT_RASTER_WIDTH.get();
                    //einzelne Knickpunkte nicht nur direkt auf dem Raster docken, sondern auch auf Höhe und Weite ihrer Nachbarknickpunkte
                    boolean rasterElement = true;
                    if (clickedEc instanceof BendpointContainer && szenario.isSingleSelection()) {
                        BendpointContainer bc = (BendpointContainer) clickedEc;
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
                    // Y - Richtung
                    grabbedElementsRealRect.y -= deltaY;
                    grabbedElementsRealRect.height -= deltaY;
                    rasterElement = true;
                    if (clickedEc instanceof BendpointContainer && szenario.isSingleSelection()) {
                        BendpointContainer bc = (BendpointContainer) clickedEc;
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
                    int max_xdiff = layerWidth / 2;
                    if (grabbedElementsFullRect.x - deltaX < -max_xdiff) {
                        deltaX = grabbedElementsFullRect.x + max_xdiff;
                    }
                } else if (deltaX < 0) {
                    int max_xdiff = layerWidth / 2;
                    if (grabbedElementsFullRect.width - deltaX > max_xdiff) {
                        deltaX = grabbedElementsFullRect.width - max_xdiff;
                    }
                }
                if (deltaY > 0) {
                    int max_ydiff = layerHeight / 2;
                    if (grabbedElementsFullRect.y - deltaY < -max_ydiff) {
                        deltaY = grabbedElementsFullRect.y + max_ydiff;
                    }
                } else if (deltaY < 0) {
                    int max_ydiff = layerHeight / 2;
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
                if (multiView) {
                    szenario.moveSelectedNodeContainer(-deltaX, -deltaY, NO_LAYER, STANDARD_PID);
                } else {
                    szenario.moveSelectedNodeContainer(-deltaX, -deltaY, ebene, STANDARD_PID);
                }
                //wenn nicht irgendwelche Elemente gedragged werden mussten, kann hier nur noch auf einer Edge gedragged werden, da grabbed
                // nur bei Node oder Kanten true gesetzt wird
            } else /* if (ka instanceof EdgeContainer) */ {
                //der Einfügepunkt muss der Punkt sein, an dem die Maus vor dem Draggen war, denn sonst kann es vorkommen, dass bei mouseKlicked()
                //die Edge getroffen wurde (also grabbed==true ist), aber die Koordinaten bei mouseDragged() beim ersten Drag-Schritt ausßerhalb
                //des Kantenbereichs liegen und der Index des neuen Knickpunktes nicht korrekt bestimmt werden kann.
                //Da die Einfügeposition anhand der Koodinaten betimmt wird, wird -1 übergeben.
                String szenID = szenario.getID();
                ModelElement egde = clickedEc.getElement();
                String edgeID = egde.getID();
                clickedEc = gdcoll.insertBendingPoint(szenID, edgeID, INVALID_ID_STRING, lastXreal[ebene], lastYreal[ebene], INVALID_BENDPOINT_INDEX, STANDARD_PID);
                szenario.select(clickedEc, STANDARD_PID);
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
        // ElementContainer ec = getMouseOverElementContainer();
        // System.err.println(ec);
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        if (!e.isControlDown()) {
            Container parent = getParent();
            parent.dispatchEvent(e);
            return;
        }
        int wheelRotation = e.getWheelRotation();
        double zoomDiff = -0.05 * wheelRotation;
        double newZoom = zoom + zoomDiff;
        Dimension preferredSizeBeforeZoom = getPreferredSize();
        setZoom(newZoom);
        Dimension preferredSizeAfterZoom = getPreferredSize();
        centerToMouse(e, zoomDiff, preferredSizeBeforeZoom, preferredSizeAfterZoom);
    }

    /**
     * @return the viewport this component is contained in (=the parent)
     */
    private JViewport getViewport() {
        Container parent = getParent();
        JViewport viewport = null;
        if (parent instanceof JViewport) {
            viewport = (JViewport) parent;
        }
        return viewport;
    }

    /**
     * @param e
     * @param factor
     * @param preferredSizeBeforeZoom
     * @param preferredSizeAfterZoom
     */
    private void centerToMouse(final MouseEvent e, final double factor, final Dimension preferredSizeBeforeZoom, final Dimension preferredSizeAfterZoom) {
        JViewport viewport = getViewport();
        if (viewport != null) {
            int xDiff = preferredSizeAfterZoom.width - preferredSizeBeforeZoom.width;
            int yDiff = preferredSizeAfterZoom.height - preferredSizeBeforeZoom.height;

            Point viewPosition = viewport.getViewPosition();
            int x = viewPosition.x + xDiff / 2;
            int y = viewPosition.y + yDiff / 2;

            if (x < 0) {
                x = 0;
            }
            if (y < 0) {
                y = 0;
            }
            viewport.setViewPosition(new Point(x, y));
        }
    }

    /**
     * @param e
     */
    private void dragView(final MouseEvent e) {
        Point locationOnScreen = e.getLocationOnScreen();

        int xDiff = 0;
        int yDiff = 0;
        if (lastDragPosition != null) {
            xDiff = lastDragPosition.x - locationOnScreen.x;
            yDiff = lastDragPosition.y - locationOnScreen.y;
        }

        lastDragPosition = e.getLocationOnScreen();

        if (xDiff != 0 || yDiff != 0) {
            JViewport viewport = getViewport();
            if (viewport != null) {
                Container parent = viewport.getParent();
                JScrollPane scrollPane = (JScrollPane) parent;
                JScrollBar xScrollBar = scrollPane.getHorizontalScrollBar();
                JScrollBar yScrollBar = scrollPane.getVerticalScrollBar();
                int xScrollValue = xScrollBar.getValue();
                int yScrollValue = yScrollBar.getValue();
                xScrollValue += xDiff;
                yScrollValue += yDiff;
                int xMax = xScrollBar.getMaximum();
                xScrollValue = Maths.getValueInMinMax(xScrollValue, 0, xMax);
                int yMax = yScrollBar.getMaximum();
                yScrollValue = Maths.getValueInMinMax(yScrollValue, 0, yMax);
                xScrollBar.setValue(xScrollValue);
                yScrollBar.setValue(yScrollValue);
            }
        }
    }

    // --- Methoden des MouseMotionListener-Interfaces --- Ende ---
    /**
     * Entfernt dieses Panel aus den Listener/Listen, in denen es vorkommt.
     */
    public void dispose() {
        MainFrame mainFrame = getMainFrame();
        mainFrame.removeMouseListener(this);
    }

}
