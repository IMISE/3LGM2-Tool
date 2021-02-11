package de.imise.tool3lgm.graphtools.model;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.event.action.GraphDocumentAction;
import de.imise.tool3lgm.event.action.GraphFrameAction;
import de.imise.tool3lgm.event.action.GraphMultipleSelectedRealNodeAction;
import de.imise.tool3lgm.event.action.GraphMultipleSelectedRealNodeOrBendpointAction;
import de.imise.tool3lgm.event.action.GraphSelectedRealNodeAction;
import de.imise.tool3lgm.event.action.ModelOptionAction;
import de.imise.tool3lgm.event.action.SelectedRealNodeAction;
import de.imise.tool3lgm.event.action.SelectionAction;
import de.imise.tool3lgm.event.action.SubmodelAction;
import de.imise.tool3lgm.event.action.SubmodelSelectionAction;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.SHAPE;
import de.imise.util.swing.event.ActionSource;
import de.imise.util.swing.event.ExtendedAction;

public enum GDCommands implements ActionSource {

    //the action classes in the constructor can be used to define
    //the enabled state of an button or menu item which starts
    //the action by executing the command. Only such actions
    //need an action class. All of these actions which open a dialog
    //sould have the boolean parameter interactive true (defalt is false).
    MODEL_ACTION_CREATE_NODE(GraphDocumentAction.class, true), //name input dialog
    MODEL_ACTION_DELETE(SelectionAction.class, true), //dialog delete from submodel or from model
    MODEL_ACTION_DELETE_FROM_SUBMODEL(SubmodelSelectionAction.class),
    MODEL_ACTION_DELETE_FROM_MODEL(SelectionAction.class),
    MODEL_ACTION_LINK,
    MODEL_ACTION_UNLINK,
    MODEL_ACTION_CREATE_ADDICTED, //combination of MODEL_ACTION_CREATE_NODE and MODEL_ACTION_ADDICT. Needs no ResourceKey, because the actions name is the name of the sub element type that should be created
    MODEL_ACTION_CREATE_INSTANCIATION, //same as MODEL_ACTION_CREATE_ADDICTED
    MODEL_ACTION_ADDICT, // internal action
    MODEL_ACTION_SWAP_EDGE_POSITIONS,
    //9
    MODEL_ACTION_SET_ELEMENT_NAME,
    MODEL_ACTION_SET_ELEMENT_DESCRIPTION,
    //11
    MODEL_ACTION_SET_ELEMENT_POSITION, // no action class needed drag action
    MODEL_ACTION_SET_ELEMENT_FONT(GraphSelectedRealNodeAction.class, true),
    MODEL_ACTION_SET_ELEMENT_COLOR(GraphSelectedRealNodeAction.class, true),
    MODEL_ACTION_SET_ELEMENT_ALPHA, //internal identifier for UNDO/REDO for the identifiers MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_XXX
    MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_NONE(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_HALF(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_FULL(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_SET_ELEMENT_ICON_NONE(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_SET_ELEMENT_ICON(GraphSelectedRealNodeAction.class, true), //dialog set icon
    MODEL_ACTION_SET_ELEMENT_DEFAULT_COLOR(GraphSelectedRealNodeAction.class, true), //dialog set color
    MODEL_ACTION_SET_ELEMENT_DEFAULT_TRANSPARENCY(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_SET_ELEMENT_DEFAULT_FONT(GraphSelectedRealNodeAction.class, true), //dialog set font
    MODEL_ACTION_SET_ELEMENT_DEFAULT_FULL_LAYOUT(GraphSelectedRealNodeAction.class),
    //24
    MODEL_ACTION_SET_ELEMENT_VISIBILITY_ON(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_SET_ELEMENT_VISIBILITY_OFF(GraphSelectedRealNodeAction.class),
    //26
    MODEL_ACTION_SET_ELEMENTS_POSITION_HORIZONTAL_CENTER(GraphMultipleSelectedRealNodeOrBendpointAction.class),
    MODEL_ACTION_SET_ELEMENTS_POSITION_HORIZONTAL_LEFT(GraphMultipleSelectedRealNodeOrBendpointAction.class),
    MODEL_ACTION_SET_ELEMENTS_POSITION_HORIZONTAL_RIGHT(GraphMultipleSelectedRealNodeOrBendpointAction.class),
    MODEL_ACTION_SET_ELEMENTS_POSITION_VERTICAL_BOTTOM(GraphMultipleSelectedRealNodeOrBendpointAction.class),
    MODEL_ACTION_SET_ELEMENTS_POSITION_VERTICAL_CENTER(GraphMultipleSelectedRealNodeOrBendpointAction.class),
    MODEL_ACTION_SET_ELEMENTS_POSITION_VERTICAL_TOP(GraphMultipleSelectedRealNodeOrBendpointAction.class),
    MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH(GraphMultipleSelectedRealNodeAction.class),
    MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_HEIGTH(GraphMultipleSelectedRealNodeAction.class),
    MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH_AND_HEIGTH(GraphMultipleSelectedRealNodeAction.class),
    MODEL_ACTION_SET_ELEMENTS_ALIGNMENT_GRID(GraphMultipleSelectedRealNodeAction.class, true), //grid rows + columns input dialog
    //36
    MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL_LEFT(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL_CENTER(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL_RIGHT(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL, //internal action for UNDO/REDO
    //40
    MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL_TOP(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL_CENTER(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL_BOTTOM(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL, //internal action for UNDO/REDO
    //44
    MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_LEFT(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_CENTER(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_RIGHT(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_JUSTIFY(GraphSelectedRealNodeAction.class), //Blocksatz
    MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML, //internal action for UNDO/REDO
    //49
    MODEL_ACTION_SET_ELEMENT_EXPANSION_ON, //potential toolbar action
    MODEL_ACTION_SET_ELEMENT_EXPANSION_OFF, //potential toolbar action
    //51
    MODEL_ACTION_SET_LAYER_INTERLAYER_CONNECTIONS_VISIBILITY_ON, //Initialized as toolbar action in the ActionLibrary with special enabled function
    MODEL_ACTION_SET_LAYER_INTERLAYER_CONNECTIONS_VISIBILITY_OFF, //Initialized as toolbar action in the ActionLibrary with special enabled function
    MODEL_ACTION_SET_LAYER_COLOR(GraphFrameAction.class, true), //color dialog
    MODEL_ACTION_SET_LAYER_ALPHA, //internal identifier for UNDO/REDO for the identifiers MODEL_ACTION_SET_LAYER_TRANSPARENCY_XXX
    MODEL_ACTION_SET_LAYER_TRANSPARENCY_NONE(GraphFrameAction.class),
    MODEL_ACTION_SET_LAYER_TRANSPARENCY_HALF(GraphFrameAction.class),
    MODEL_ACTION_SET_LAYER_TRANSPARENCY_FULL(GraphFrameAction.class),
    MODEL_ACTION_SET_LAYER_DEFAULT_COLOR_AND_TRANSPARENCY(GraphFrameAction.class),
    MODEL_ACTION_SET_LAYER_SIZE_FACTOR,
    //60
    MODEL_ACTION_MOVE_ORDER_ONE_POSITION_UP(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_MOVE_ORDER_ONE_POSITION_DOWN(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_MOVE_ORDER_TO_FIRST_POSITION(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_MOVE_ORDER_TO_LAST_POSITION(GraphSelectedRealNodeAction.class),
    MODEL_ACTION_MOVE_ORDER, //internal identifier for UNDO/REDO for the identifiers MODEL_ACTION_MOVE_ORDER_XXX
    //65
    MODEL_ACTION_INSERT_BENDING_POINT,
    //66
    MODEL_ACTION_SET_USER_FIELD_VALUE,
    MODEL_ACTION_SET_USER_FIELD_WEIGHT_REPLACEMENT,
    MODEL_ACTION_SET_ELEMENT_OPTIONAL,
    //69
    MODEL_ACTION_CREATE_SUBMODEL(GraphDocumentAction.class, true), //dialog submodel name
    MODEL_ACTION_DELETE_SUBMODEL(SubmodelAction.class, true), //dialog really delete
    MODEL_ACTION_RENAME_SUBMODEL(SubmodelAction.class, true), //dialog submodel name
    //72

    MODEL_ACTION_ADD_SELECTED_TO_SUBMODEL(SelectedRealNodeAction.class),
    MODEL_ACTION_ADD_SELECTED_TO_NEW_SUBMODEL(SelectedRealNodeAction.class),
    MODEL_ACTION_ADD_SELECTED_TO_ALL_SUBMODELS(SelectedRealNodeAction.class),
    MODEL_ACTION_ADD_ELEMENT_TO_SUBMODEL, //internal model action -> braucht keinen ResKey, da nur bei Undo-Redo gebraucht
    //76
    MODEL_ACTION_LINK_SELECTED_TO_NEW_SUBMODEL(SelectedRealNodeAction.class),
    MODEL_ACTION_LINK_SELECTED_TO_SUBMODEL, //potential button action
    MODEL_ACTION_UNLINK_SELECTED_TO_SUBMODEL, //potential button action
    MODEL_ACTION_LINK_ELEMENT_TO_SUBMODEL, //potential button action
    MODEL_ACTION_SELECT_LINKED_SUBMODEL, //potential button action
    //81
    MODEL_ACTION_JOIN_SELECTED, //potential button action
    //82
    MODEL_ACTION_SET_ELEMENT_INTERLAYER_CONNECTIONS_VISIBILITY_ON, //action defined in ActionLibrary
    MODEL_ACTION_SET_ELEMENT_INTERLAYER_CONNECTIONS_VISIBILITY_OFF, //action defined in ActionLibrary
    MODEL_ACTION_SET_INTERLAYER_CONNECTIONS_VISIBILITY_ON, //action defined in ActionLibrary
    MODEL_ACTION_SET_INTERLAYER_CONNECTIONS_VISIBILITY_OFF, //action defined in ActionLibrary
    //86
    //Die Kommandos ab hier werden in LGMGraphDocument ausgewertet
    MODEL_ACTION_COPY(SelectionAction.class),
    MODEL_ACTION_CUT(SelectionAction.class),
    MODEL_ACTION_PASTE(GraphDocumentAction.class),
    //89
    MODEL_ACTION_HIDE_UNASSOCIATED, //potential button action
    MODEL_ACTION_UNHIDE_ALL, //potential button action
    //91
    //spezielle Kommandos
    MODEL_ACTION_COMMAND_LINE,
    MODEL_ACTION_PRINT_QUEUE,
    MODEL_ACTION_INTERNAL_CHECK_CONSISTENCY,
    MODEL_OPTION_GDCOLL_AUTOMATIC_MODE(ModelOptionAction.class),
    MODEL_OPTION_GDOC_VERIFICATION_MODE(ModelOptionAction.class);
    //96
    //Ungültige Werte für alle Kommandos
    public static final int INVALID_POSITION_X = -1;
    public static final int INVALID_POSITION_Y = -1;
    public static final int INVALID_WIDTH = -1;
    public static final int INVALID_HEIGHT = -1;
    public static final int INVALID_COLOR_RGB = -1;
    public static final SHAPE INVALID_SHAPE = null;
    public static final String INVALID_ID_STRING = "";
    public static final String INVALID_NAME = "";
    public static final String INVALID_DESCRIPTION = "";
    public static final int INVALID_BENDPOINT_INDEX = -1;
    public static final int INVALID_EDGE_INDEX = -1;
    public static final String INVALID_EDGE_CLASS_NAME = "";

    /**
     *
     */
    private final Class<? extends GraphDocumentAction> actionClass;

    /**
     *
     */
    public final boolean interactive;

    /**
     * @param actionClass
     */
    private GDCommands() {
        this(null, false);
    }

    /**
     * @param actionClass
     */
    private GDCommands(final Class<? extends GraphDocumentAction> actionClass) {
        this(actionClass, false);
    }

    /**
     * @param actionClass
     * @param interactive
     */
    private GDCommands(final Class<? extends GraphDocumentAction> actionClass, final boolean interactive) {
        this.actionClass = actionClass;
        this.interactive = interactive;
    }

    @Override
    public Class<? extends ExtendedAction> getActionClass() {
        return actionClass;
    }

    @Override
    public boolean isInteractiveAction() {
        return interactive;
    }

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

    public static final boolean isModelOption(final GDCommands command) {
        return command.name().startsWith("MODEL_OPTION_");
    }

    public final boolean isModelOption() {
        return isModelOption(this);
    }

    private static final GDCommands[] VALUES = values();

    /**
     * @return always the same instance of the values of this enum and not every
     *         time a new array like the original values() function
     */
    public static GDCommands[] getValues() {
        return VALUES;
    }

}
