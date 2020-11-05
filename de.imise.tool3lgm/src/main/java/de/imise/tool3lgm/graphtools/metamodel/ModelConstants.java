package de.imise.tool3lgm.graphtools.metamodel;

import de.imise.tool3lgm.Tool3lgmConstants;

/**
 * @author N.N., AXS
 */
public final class ModelConstants {

    //Bei Gelegenheit mal ersetzen (Das ist aber schon etwas mehr Arbeit)
    //public enum LAYER {NO_LAYER, PHYSICAL_LAYER, INTER_LOGICAL_PHYSICAL_LAYER, LOGICAL_LAYER, INTER_DOMAIN_LOGICAL_LAYER, DOMAIN_LAYER};
    public static final int NO_LAYER = -1;

    public static final int PHYSICAL_LAYER = 0;

    public static final int INTER_LOGICAL_PHYSICAL_LAYER = 1;

    public static final int LOGICAL_LAYER = 2;

    public static final int INTER_DOMAIN_LOGICAL_LAYER = 3;

    public static final int DOMAIN_LAYER = 4;

    public static final int[] LAYERS = {
            PHYSICAL_LAYER, INTER_LOGICAL_PHYSICAL_LAYER, LOGICAL_LAYER, INTER_DOMAIN_LOGICAL_LAYER, DOMAIN_LAYER
    };

    public static final int[] VISIBLE_LAYERS = {
            DOMAIN_LAYER, LOGICAL_LAYER, PHYSICAL_LAYER
    };

    public static final int MIN_LAYER_INDEX = PHYSICAL_LAYER;

    public static final int MAX_LAYER_INDEX = DOMAIN_LAYER;

    public static final int LAYER_COUNT = LAYERS.length;

    public static final boolean isInterLayer(final int layerIndex) {
        return layerIndex % 2 == 1;
    }

    /** Short-Name für den beginn des HashStrings bei allen Kanten */
    public static final String EDGE_SHORT_NAME = "DLK";

    /**
     * Short-Name der zurückgegeben wird, wenn die an
     * <code>getShortName(Class)</code> übergebene Klasse weder eine gültige
     * Node noch Kantenklasse ist.
     */
    public static final String NO_MODEL_ELEMENT_SHORT_NAME = "NME";

    public static final String PLURAL_NAME_RES_KEY_SUFFIX = "_p";

    public static final String getVisibleLayerName(final int layer) {
        String resKey = "layer";
        int reskeyLayerNumber = -1;
        for (int i = 0; i < VISIBLE_LAYERS.length; i++) {
            if (layer == VISIBLE_LAYERS[i]) {
                reskeyLayerNumber = i + 1;
                break;
            }
        }
        //das auskommentierte geht eigentlich genausogut, aber das hier ist lesbarer
        //        int visibleLayers = LAYER_COUNT / 2 + 1; // = 3
        //        // 4 = 1 -> 4 / 2 = 2 - visibleLayers = -1 * -1 = 1
        //        // 2 = 2 -> 2 / 2 = 1 - visibleLayers = -2 * -1 = 2
        //        // 0 = 3 -> 0 / 2 = 0 - visibleLayers = -3 * -1 = 3
        //        int reskeyLayerNumber = -(layer / 2 - visibleLayers);
        try {
            return Tool3lgmConstants.getResString(resKey + reskeyLayerNumber);
        } catch (Exception e) {
            return Tool3lgmConstants.getResString(resKey) + reskeyLayerNumber;
        }
    }

    /**
     * Standardrückgabewert bei Fehlern = -1 ;
     */
    public static final int STANDARD_ERROR_INT_VALUE = -1;

}