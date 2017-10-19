package de.imise.tool3lgm.graphtools.view.graph;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;

/**
 * @author Thomas Rudert
 *         modelElement to hold view-informations about InputGraphArea during loading xml-file
 */
public class ViewParameter {

    public static final int INITIAL_X = 200;

    public static final int INITIAL_Y = 150;

    public static final int INITIAL_LAYER_GAP = 200;

    public static final int INITIAL_LAYER_ANGLE = 65;

    public static final double INITIAL_ZOOM = .8d;

    public static final double INITIAL_PAGE_SIZE_FACTOR = 1d;

    public static final int INITILA_ACTIVE_LAYER = ModelConstants.DOMAIN_LAYER;

    public int x = INITIAL_X;
    public int y = INITIAL_Y;
    public int layerGap = INITIAL_LAYER_GAP;
    public int layerAngle = INITIAL_LAYER_ANGLE;
    public boolean multiView = true;
    public double zoom = INITIAL_ZOOM;
    public double pageSizeFactor = INITIAL_PAGE_SIZE_FACTOR;
    public int activeLayer = INITILA_ACTIVE_LAYER;
    public boolean selected = false;
}
