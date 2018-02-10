package de.imise.tool3lgm.graphtools.model;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.SHAPE;

public enum GDCommands {

    MODEL_ACTION_CREATE_NODE,
    LINK,
    //	LINK_REVERSE,
    ADDICT,
    UNLINK,
    //	UNLINK_REVERSE,
    SWAP_EDGE_POSITIONS,
    CREATE_ADDICTED,

    SET_VISIBLE,

    SET_NAME,
    //TODO: Dieses Kommando wird im Moment nicht ausgeführt, also dispatch_command kann mit diesem Kommando nichts anfangen
    CHANGE_FORM,
    MODEL_ACTION_SET_ELEMENT_FONT,
    MODEL_ACTION_SET_ELEMENT_COLOR,
    MODEL_ACTION_SET_ELEMENT_ALPHA, //Für diesen Identifier gibt es keine Action, weil das über SetColor läuft. Man braucht ihn nur für das UNDO von MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_XXX
    MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_NONE,
    MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_HALF,
    MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_FULL,
    MODEL_ACTION_SET_ELEMENT_ICON_NONE,
    MODEL_ACTION_SET_LAYER_COLOR,
    MODEL_ACTION_SET_LAYER_ALPHA, //Für diesen Identifier gibt es keine Action, weil das über SetColor läuft. Man braucht ihn nur für das UNDO von MODEL_ACTION_SET_LAYER_TRANSPARENCY_XXX
    MODEL_ACTION_SET_LAYER_TRANSPARENCY_NONE,
    MODEL_ACTION_SET_LAYER_TRANSPARENCY_HALF,
    MODEL_ACTION_SET_LAYER_TRANSPARENCY_FULL,
    MODEL_ACTION_SET_LAYER_DEFAULT_COLOR_AND_TRANSPARENCY,
    CHANGE_LAYER_SIZE_FACTOR,
    CHANGE_GLOBAL_NAME,
    CHANGE_GLOBAL_MAPPING,
    //TODO: Dieses Kommando wird im Moment nicht ausgeführt, also dispatch_command kann mit diesem Kommando nichts anfangen
    HIDE_ELEM,
    //TODO: Dieses Kommando wird im Moment nicht ausgeführt, also dispatch_command kann mit diesem Kommando nichts anfangen
    UNHIDE_ELEM,
    MODEL_ACTION_MOVE_ORDER_ONE_POSITION_UP,
    MODEL_ACTION_MOVE_ORDER_ONE_POSITION_DOWN,
    MODEL_ACTION_MOVE_ORDER_TO_FIRST_POSITION,
    MODEL_ACTION_MOVE_ORDER_TO_LAST_POSITION,
    MODEL_ACTION_MOVE_ORDER, //internal model action -> braucht keinen ResKey, da nur bei Undo-Redo gebraucht

    MODEL_ACTION_SET_ELEMENT_DEFAULT_COLOR,
    MODEL_ACTION_SET_ELEMENT_DEFAULT_TRANSPARENCY,
    MODEL_ACTION_SET_ELEMENT_DEFAULT_FONT,
    MODEL_ACTION_SET_ELEMENT_DEFAULT_FULL_LAYOUT,

    COORDINATE_KNOT,
    AUFKLAPPEN,
    ZUKLAPPEN,

    INSERT_BENDING_POINT,
    MODEL_ACTION_CREATE_TEXTFIELD,

    MODEL_ACTION_SET_ELEMENT_ALIGNMENT_HORIZONTAL_CENTER,
    MODEL_ACTION_SET_ELEMENT_ALIGNMENT_HORIZONTAL_LEFT,
    MODEL_ACTION_SET_ELEMENT_ALIGNMENT_HORIZONTAL_RIGHT,
    MODEL_ACTION_SET_ELEMENT_ALIGNMENT_VERTICAL_BOTTOM,
    MODEL_ACTION_SET_ELEMENT_ALIGNMENT_VERTICAL_CENTER,
    MODEL_ACTION_SET_ELEMENT_ALIGNMENT_VERTICAL_TOP,
    MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH,
    MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_HEIGTH,
    MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH_AND_HEIGTH,

    MODEL_ACTION_SET_ELEMENT_LABEL_VALIGN_CENTER,
    MODEL_ACTION_SET_ELEMENT_LABEL_VALIGN_TOP,
    MODEL_ACTION_SET_ELEMENT_LABEL_VALIGN_BOTTOM,
    MODEL_ACTION_SET_ELEMENT_LABEL_HALIGN_CENTER,
    MODEL_ACTION_SET_ELEMENT_LABEL_HALIGN_LEFT,
    MODEL_ACTION_SET_ELEMENT_LABEL_HALIGN_RIGHT,
    MODEL_ACTION_SET_ELEMENT_LABEL_VALIGN, //internal model action -> braucht keinen ResKey, da nur bei Undo-Redo gebraucht
    MODEL_ACTION_SET_ELEMENT_LABEL_HALIGN, //internal model action -> braucht keinen ResKey, da nur bei Undo-Redo gebraucht

    SET_USER_FIELD_VALUE,
    SET_USER_FIELD_WEIGHT_REPLACEMENT,
    SET_DESCRIPTION,

    INTERACTIVE_MODE_OFF,
    INTERACTIVE_MODE_ON,
    VERIFY_OFF,
    VERIFY_ON,

    UNDO,
    REDO,

    SET_ICON,

    CHANGE_LINE_STYLE,

    CREATE_SZENARIO,
    REMOVE_SZENARIO,
    ADD_ELEMENT_TO_SZENARIO,
    ADD_SELECTED_TO_SZENARIO,
    SHOW_SZENARIO,
    MODEL_ACTION_DELETE,
    MODEL_ACTION_DELETE_FROM_SUBMODEL,
    MODEL_ACTION_DELETE_FROM_MODEL,

    JOIN_SELECTED,

    ADD_SELECTED_TO_NEW_SZENARIO,
    ADD_SELECTED_TO_ALL_SZENARIOS,
    LINK_SELECTED_TO_NEW_SZENARIO,
    LINK_SELECTED_TO_SZENARIO,
    LINK_ELEMENT_TO_SZENARIO,
    SELECT_LINKED_SZENARIO,

    //Die Kommandos ab hier werden in LGMGraphDocument ausgewertet
    MODEL_ACTION_COPY,
    MODEL_ACTION_CUT,
    MODEL_ACTION_PASTE,

    MODEL_ACTION_SHOW_ALL_LAYER_CONFIGS,
    MODEL_ACTION_HIDE_ALL_LAYER_CONFIGS,
    MODEL_ACTION_SHOW_ELEMENT_CONFIGS,
    MODEL_ACTION_HIDE_ELEMENT_CONFIGS,

    HIDE_UNASSOCIATED,
    UNHIDE_ALL,

    //spezielle Kommandos
    COMMAND_LINE,
    PRINT_QUEUE,
    CHECK_CONSISTENCY;

    //Ungültige Werte für alle Kommandos
    public static final int INVALID_POSITION_X = -1;
    public static final int INVALID_POSITION_Y = -1;
    public static final int INVALID_WIDTH = -1;
    public static final int INVALID_HEIGHT = -1;
    public static final int INVALID_COLOR_RGB = -1;
    public static final SHAPE INVALID_SHAPE = null;
    public static final String INVALID_HASH_STRING = "";
    public static final String INVALID_NAME = "";
    public static final String INVALID_DESCRIPTION = "";
    public static final int INVALID_BENDPOINT_INDEX = -1;
    public static final int INVALID_EDGE_INDEX = -1;
    public static final Class<? extends Edge> INVALID_EDGE_CLASS = null;
    public static final String INVALID_EDGE_CLASS_NAME = "";

    @Override
    public String toString() {
        //wenn lesbare Undo-Kommandos ausgegeben werden sollen
        if (Tool3lgmConstants.LOG_READABLE_UNDO_REDO_COMMANDS) {
            //den normalen Kommando-String zurück geben
            return super.toString();
        }
        //den Index des Komandos als String zurück geben (der auch eindeutig, aber
        //viel kürzer ist als der Komandoname und somit nicht soviel Speicher verbraucht
        //beim Loggen der Undo-redo-Kommandos
        return Integer.toString(ordinal());
    }

}
