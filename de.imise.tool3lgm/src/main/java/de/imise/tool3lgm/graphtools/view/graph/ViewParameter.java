package de.imise.tool3lgm.graphtools.view.graph;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;

/**
 * @author Thomas Rudert
 *         modelElement to hold view-informations about InputGraphArea during loading xml-file or init the GraphArea
 */
public class ViewParameter {

    public static final int INITIAL_VIEW_POSITION_X = 200;

    public static final int INITIAL_VIEW_POSITION_Y = 150;

    public static final int INITIAL_LAYER_GAP = 200;

    public static final int INITIAL_LAYER_ANGLE = 65;

    public static final double INITIAL_MIN_ZOOM = .8d;

    public static final double INITIAL_PAGE_SIZE_FACTOR = 1d;

    public static final int INITILA_ACTIVE_LAYER = ModelConstants.DOMAIN_LAYER;

    public int viewPositionX = INITIAL_VIEW_POSITION_X;
    public int viewPositionY = INITIAL_VIEW_POSITION_Y;
    public int layerGap = INITIAL_LAYER_GAP;
    public int layerAngle = INITIAL_LAYER_ANGLE;
    public boolean multiView = true;
    public double zoom = INITIAL_MIN_ZOOM;
    public double pageSizeFactor = INITIAL_PAGE_SIZE_FACTOR;
    public int activeLayer = INITILA_ACTIVE_LAYER;
    public boolean selected = false;

    public ViewParameter() {

    }

    public ViewParameter(final ViewParameter other) {
        adapt(other);
    }

    public void adapt(final ViewParameter other) {
        viewPositionX = other.viewPositionX;
        viewPositionY = other.viewPositionY;
        layerGap = other.layerGap;
        layerAngle = other.layerAngle;
        multiView = other.multiView;
        zoom = other.zoom;
        pageSizeFactor = other.pageSizeFactor;
    }

}
