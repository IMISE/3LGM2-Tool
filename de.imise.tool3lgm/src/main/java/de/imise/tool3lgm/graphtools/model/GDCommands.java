package de.imise.tool3lgm.graphtools.model;

import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.TRANSIENT_OPTION_LOG_READABLE_UNOD_REDO_COMMANDS;

import javax.swing.KeyStroke;

import com.google.common.base.Objects;

import de.imise.tool3lgm.event.action.GraphDocumentAction;
import de.imise.tool3lgm.event.action.GraphFrameAction;
import de.imise.tool3lgm.event.action.GraphMultipleSelectedRealNodeOrBendpointAction;
import de.imise.tool3lgm.event.action.GraphSelectedRealNodeAction;
import de.imise.tool3lgm.event.action.ModelOptionAction;
import de.imise.tool3lgm.event.action.SelectedRealNodeAction;
import de.imise.tool3lgm.event.action.SelectionAction;
import de.imise.tool3lgm.event.action.SubmodelAction;
import de.imise.tool3lgm.event.action.SubmodelSelectionAction;
import de.imise.util.swing.event.ActionSource;
import de.imise.util.swing.event.ExtendedAction;

public enum GDCommands implements ActionSource {

    MODEL_ACTION_CREATE_NODE,
    MODEL_ACTION_DELETE,
    MODEL_ACTION_DELETE_FROM_SUBMODEL,
    MODEL_ACTION_DELETE_FROM_MODEL,
    MODEL_ACTION_LINK,
    MODEL_ACTION_UNLINK,
    MODEL_ACTION_CREATE_SUBORDINATED, //Das hier ist eine Kombination aus MODEL_ACTION_CREATE_NODE und MODEL_ACTION_SUBORDINATE. Hat keinen ResourceKey, weil die Actions nach dem Element benannt sind, das man unterordnet
    MODEL_ACTION_CREATE_INSTANCIATION, //Das hier ist eine Kombination aus MODEL_ACTION_CREATE_NODE und MODEL_ACTION_SUBORDINATE. Hat einen ResourceKey, weil die Actions nach dem Element benannt sind, das man unterordnet
    MODEL_ACTION_SUBORDINATE, //das ist eine interne ModelAction, d.h. sie wird nicht direkt vom Benutzer ausgelöst sondern nur über eine andere ModelAction
    MODEL_ACTION_SWAP_EDGE_POSITIONS,
    //9
    MODEL_ACTION_SET_ELEMENT_NAME,
    MODEL_ACTION_SET_ELEMENT_DESCRIPTION,
    MODEL_ACTION_SET_ELEMENT_SUBTYPE,
    //12
    MODEL_ACTION_SET_ELEMENT_SHAPE, //dieses Kommando wird nicht beachtet. Das ist auch in Ordnung, weil man den Shape für einzelne Elemente nicht ändern können sollte. Wir sind kein freies Modellierungstool :)
    MODEL_ACTION_SET_ELEMENT_POSITION,
    MODEL_ACTION_SET_ELEMENT_FONT,
    MODEL_ACTION_SET_ELEMENT_COLOR,
    MODEL_ACTION_SET_ELEMENT_ALPHA, //Für diesen Identifier gibt es keine Action, weil das über SetColor läuft. Man braucht ihn nur für das UNDO von MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_XXX
    MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_NONE,
    MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_HALF,
    MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_FULL,
    MODEL_ACTION_SET_ELEMENT_ICON_NONE,
    MODEL_ACTION_SET_ELEMENT_ICON,
    MODEL_ACTION_SET_ELEMENT_DEFAULT_COLOR,
    MODEL_ACTION_SET_ELEMENT_DEFAULT_TRANSPARENCY,
    MODEL_ACTION_SET_ELEMENT_DEFAULT_FONT,
    MODEL_ACTION_SET_ELEMENT_DEFAULT_FULL_LAYOUT,
    MODEL_ACTION_SET_ELEMENT_VISIBILITY_ON,
    MODEL_ACTION_SET_ELEMENT_VISIBILITY_OFF,
    //28
    MODEL_ACTION_SET_ELEMENTS_POSITION_HORIZONTAL_CENTER,
    MODEL_ACTION_SET_ELEMENTS_POSITION_HORIZONTAL_LEFT,
    MODEL_ACTION_SET_ELEMENTS_POSITION_HORIZONTAL_RIGHT,
    MODEL_ACTION_SET_ELEMENTS_POSITION_VERTICAL_BOTTOM,
    MODEL_ACTION_SET_ELEMENTS_POSITION_VERTICAL_CENTER,
    MODEL_ACTION_SET_ELEMENTS_POSITION_VERTICAL_TOP,
    MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH,
    MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_HEIGTH,
    MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH_AND_HEIGTH,
    MODEL_ACTION_SET_ELEMENTS_ALIGNMENT_GRID,
    //38
    MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL_LEFT,
    MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL_CENTER,
    MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL_RIGHT,
    MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL, //internal model action -> braucht keinen ResKey, da nur bei Undo-Redo gebraucht
    //42
    MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL_TOP,
    MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL_CENTER,
    MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL_BOTTOM,
    MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL, //internal model action -> braucht keinen ResKey, da nur bei Undo-Redo gebraucht
    //46
    MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_LEFT,
    MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_CENTER,
    MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_RIGHT,
    MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_JUSTIFY, //Blocksatz
    MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML, //internal model action -> braucht keinen ResKey, da nur bei Undo-Redo gebraucht
    //51
    MODEL_ACTION_ADOPT_SAME_COLOR,
    MODEL_ACTION_ADOPT_SAME_TRANSPARENCY,
    MODEL_ACTION_ADOPT_SAME_FONT,
    MODEL_ACTION_ADOPT_SAME_ICON,
    MODEL_ACTION_ADOPT_SAME_ALL,
    //56
    MODEL_ACTION_SET_ELEMENT_EXPANSION_ON,
    MODEL_ACTION_SET_ELEMENT_EXPANSION_OFF,
    //58
    MODEL_ACTION_SET_LAYER_INTERLAYER_CONNECTIONS_VISIBILITY_ON,
    MODEL_ACTION_SET_LAYER_INTERLAYER_CONNECTIONS_VISIBILITY_OFF,
    MODEL_ACTION_SET_LAYER_SHOW_CONNECTED_AS_NAME_EXTENSION_IN_GRAPH_ON,
    MODEL_ACTION_SET_LAYER_SHOW_CONNECTED_AS_NAME_EXTENSION_IN_GRAPH_OFF,
    MODEL_ACTION_SET_LAYER_COLOR,
    MODEL_ACTION_SET_LAYER_ALPHA, //Für diesen Identifier gibt es keine Action, weil das über SetColor läuft. Man braucht ihn nur für das UNDO von MODEL_ACTION_SET_LAYER_TRANSPARENCY_XXX
    MODEL_ACTION_SET_LAYER_TRANSPARENCY_NONE,
    MODEL_ACTION_SET_LAYER_TRANSPARENCY_HALF,
    MODEL_ACTION_SET_LAYER_TRANSPARENCY_FULL,
    MODEL_ACTION_SET_LAYER_DEFAULT_COLOR_AND_TRANSPARENCY,
    MODEL_ACTION_SET_LAYER_SIZE_FACTOR,
    //67
    MODEL_ACTION_MOVE_ORDER_ONE_POSITION_UP,
    MODEL_ACTION_MOVE_ORDER_ONE_POSITION_DOWN,
    MODEL_ACTION_MOVE_ORDER_TO_FIRST_POSITION,
    MODEL_ACTION_MOVE_ORDER_TO_LAST_POSITION,
    MODEL_ACTION_MOVE_ORDER, //internal model action -> braucht keinen ResKey, da nur bei Undo-Redo gebraucht
    //70
    MODEL_ACTION_INSERT_BENDING_POINT,
    //73
    MODEL_ACTION_ADD_USER_FIELD, //internal model action -> braucht keinen ResKey, da nur bei Undo-Redo gebraucht
    MODEL_ACTION_DELETE_USER_FIELD, //internal model action -> braucht keinen ResKey, da nur bei Undo-Redo gebraucht
    MODEL_ACTION_SET_USER_FIELD_VALUE,
    MODEL_ACTION_SET_USER_FIELD_WEIGHT_REPLACEMENT,
    MODEL_ACTION_SET_ELEMENT_OPTIONAL,
    //79
    MODEL_ACTION_CREATE_SUBMODEL,
    MODEL_ACTION_DELETE_SUBMODEL,
    MODEL_ACTION_DUPLICATE_SUBMODEL,
    MODEL_ACTION_RENAME_SUBMODEL,
    //84
    MODEL_ACTION_ADD_ELEMENT_TO_SUBMODEL, //internal model action -> braucht keinen ResKey, da nur bei Undo-Redo gebraucht
    MODEL_ACTION_ADD_SELECTED_TO_SUBMODEL,
    MODEL_ACTION_ADD_SELECTED_TO_NEW_SUBMODEL,
    MODEL_ACTION_ADD_SELECTED_TO_ALL_SUBMODELS,
    //88
    MODEL_ACTION_LINK_SELECTED_TO_NEW_SUBMODEL,
    MODEL_ACTION_LINK_SELECTED_TO_SUBMODEL,
    MODEL_ACTION_UNLINK_SELECTED_TO_SUBMODEL,
    MODEL_ACTION_LINK_ELEMENT_TO_SUBMODEL,
    MODEL_ACTION_SELECT_LINKED_SUBMODEL,
    //91
    MODEL_ACTION_JOIN_SELECTED,
    //92
    MODEL_ACTION_SET_ELEMENT_INTERLAYER_CONNECTIONS_VISIBILITY_ON,
    MODEL_ACTION_SET_ELEMENT_INTERLAYER_CONNECTIONS_VISIBILITY_OFF,
    MODEL_ACTION_SET_INTERLAYER_CONNECTIONS_VISIBILITY_ON,
    MODEL_ACTION_SET_INTERLAYER_CONNECTIONS_VISIBILITY_OFF,
    //96
    MODEL_ACTION_SET_ELEMENT_SHOW_CONNECTED_AS_NAME_EXTENSION_IN_GRAPH_ON,
    MODEL_ACTION_SET_ELEMENT_SHOW_CONNECTED_AS_NAME_EXTENSION_IN_GRAPH_OFF,
    MODEL_ACTION_SET_SHOW_CONNECTED_AS_NAME_EXTENSION_IN_GRAPH_ON,
    MODEL_ACTION_SET_SHOW_CONNECTED_AS_NAME_EXTENSION_IN_GRAPH_OFF,
    //100
    //Die Kommandos ab hier werden in LGMGraphDocument ausgewertet
    MODEL_ACTION_COPY,
    MODEL_ACTION_CUT,
    MODEL_ACTION_PASTE,
    //103
    MODEL_ACTION_HIDE_UNASSOCIATED,
    MODEL_ACTION_UNHIDE_ALL,
    //105
    //spezielle Kommandos
    MODEL_ACTION_COMMAND_LINE,
    MODEL_ACTION_PRINT_QUEUE,
    MODEL_ACTION_INTERNAL_CHECK_CONSISTENCY,
    MODEL_OPTION_GDCOLL_AUTOMATIC_MODE,
    MODEL_OPTION_GDOC_VERIFICATION_MODE,
    MODEL_ACTION_PRINT_MODEL_TO_CONSOLE;
    //110
    //Ungültige Werte für alle Kommandos
    public static final String INVALID_ID_STRING = "";
    public static final String INVALID_NAME = "";
    public static final String INVALID_DESCRIPTION = "";
    public static final int INVALID_BENDPOINT_INDEX = -1;
    public static final int INVALID_EDGE_INDEX = -1;
    public static final String INVALID_EDGE_CLASS_NAME = "";

    /** The action for this command */
    private ExtendedAction action;

    @Override
    public ExtendedAction createAction() {
        if (action == null) {
            action = ActionSource.super.createAction();

        }
        return action;
    }

    /**
     * @param keyStroke
     */
    public void setKeyStroke(KeyStroke keyStroke) {
        ExtendedAction action = createAction();
        action.setKeyStroke(keyStroke);
    }

    @Override
    public String toString() {
        //wenn lesbare Undo-Kommandos ausgegeben werden sollen
        if (TRANSIENT_OPTION_LOG_READABLE_UNOD_REDO_COMMANDS.is()) {
            //den normalen Kommando-String zurück geben
            return super.toString();
        }
        //den Index des Komandos als String zurück geben (der auch eindeutig, aber
        //viel kürzer ist als der Komandoname und somit nicht soviel Speicher verbraucht
        //beim Loggen der Undo-redo-Kommandos
        return Integer.toString(ordinal());
    }

    /**
     * @param command
     * @return
     */
    public static final boolean isModelOption(final GDCommands command) {
        return command.name().startsWith("MODEL_OPTION_");
    }

    /**
     * @return
     */
    public final boolean isModelOption() {
        return isModelOption(this);
    }

    /**
     *
     */
    private static final GDCommands[] VALUES = values();

    /**
     * @param s
     * @return <code>true</code> if this.toString() is equals to s
     */
    public boolean is(String s) {
        return Objects.equal(toString(), s);
    }

    /**
     * @return always the same instance of the values of this enum and not every
     *         time a new array like the original values() function
     */
    public static GDCommands[] getValues() {
        return VALUES;
    }

    static {
        ActionSource.putInteractive(GraphDocumentAction.class, MODEL_ACTION_CREATE_NODE);
        ActionSource.put(SelectionAction.class, MODEL_ACTION_COPY);
        ActionSource.put(SelectionAction.class, MODEL_ACTION_CUT);
        ActionSource.put(GraphDocumentAction.class, MODEL_ACTION_PASTE);
        ActionSource.put(SelectionAction.class, MODEL_ACTION_DELETE_FROM_MODEL);
        ActionSource.putInteractive(SelectionAction.class, MODEL_ACTION_DELETE); //dialog delete form submodel or from model
        ActionSource.put(SubmodelSelectionAction.class, MODEL_ACTION_DELETE_FROM_SUBMODEL);
        ActionSource.put(GraphFrameAction.class, MODEL_ACTION_SET_LAYER_DEFAULT_COLOR_AND_TRANSPARENCY);
        ActionSource.putInteractive(GraphFrameAction.class, MODEL_ACTION_SET_LAYER_COLOR); //dialog set color
        ActionSource.put(GraphFrameAction.class, MODEL_ACTION_SET_LAYER_TRANSPARENCY_NONE);
        ActionSource.put(GraphFrameAction.class, MODEL_ACTION_SET_LAYER_TRANSPARENCY_HALF);
        ActionSource.put(GraphFrameAction.class, MODEL_ACTION_SET_LAYER_TRANSPARENCY_FULL);
        ActionSource.put(GraphMultipleSelectedRealNodeOrBendpointAction.class, MODEL_ACTION_SET_ELEMENTS_POSITION_HORIZONTAL_LEFT);
        ActionSource.put(GraphMultipleSelectedRealNodeOrBendpointAction.class, MODEL_ACTION_SET_ELEMENTS_POSITION_HORIZONTAL_CENTER);
        ActionSource.put(GraphMultipleSelectedRealNodeOrBendpointAction.class, MODEL_ACTION_SET_ELEMENTS_POSITION_HORIZONTAL_RIGHT);
        ActionSource.put(GraphMultipleSelectedRealNodeOrBendpointAction.class, MODEL_ACTION_SET_ELEMENTS_POSITION_VERTICAL_TOP);
        ActionSource.put(GraphMultipleSelectedRealNodeOrBendpointAction.class, MODEL_ACTION_SET_ELEMENTS_POSITION_VERTICAL_CENTER);
        ActionSource.put(GraphMultipleSelectedRealNodeOrBendpointAction.class, MODEL_ACTION_SET_ELEMENTS_POSITION_VERTICAL_BOTTOM);
        ActionSource.put(GraphMultipleSelectedRealNodeOrBendpointAction.class, MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH);
        ActionSource.put(GraphMultipleSelectedRealNodeOrBendpointAction.class, MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_HEIGTH);
        ActionSource.put(GraphMultipleSelectedRealNodeOrBendpointAction.class, MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH_AND_HEIGTH);
        ActionSource.put(GraphMultipleSelectedRealNodeOrBendpointAction.class, MODEL_ACTION_SET_ELEMENTS_ALIGNMENT_GRID);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_ICON_NONE);
        ActionSource.putInteractive(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_ICON);//dialog set icon
        ActionSource.putInteractive(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_FONT); //dialog set font
        ActionSource.putInteractive(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_COLOR); //dialog set color
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_NONE);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_HALF);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_FULL);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL_LEFT);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL_CENTER);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL_RIGHT);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL_TOP);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL_CENTER);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL_BOTTOM);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_LEFT);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_CENTER);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_RIGHT);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_JUSTIFY);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_ADOPT_SAME_COLOR);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_ADOPT_SAME_TRANSPARENCY);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_ADOPT_SAME_FONT);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_ADOPT_SAME_ICON);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_ADOPT_SAME_ALL);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_DEFAULT_COLOR);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_DEFAULT_TRANSPARENCY);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_DEFAULT_FONT);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_SET_ELEMENT_DEFAULT_FULL_LAYOUT);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_MOVE_ORDER_TO_FIRST_POSITION);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_MOVE_ORDER_ONE_POSITION_UP);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_MOVE_ORDER_ONE_POSITION_DOWN);
        ActionSource.put(GraphSelectedRealNodeAction.class, MODEL_ACTION_MOVE_ORDER_TO_LAST_POSITION);
        ActionSource.putInteractive(GraphDocumentAction.class, MODEL_ACTION_CREATE_SUBMODEL); //diaalog submodel name
        ActionSource.putInteractive(SubmodelAction.class, MODEL_ACTION_DELETE_SUBMODEL); //dialog really delete
        ActionSource.put(SubmodelAction.class, MODEL_ACTION_DUPLICATE_SUBMODEL);
        ActionSource.putInteractive(SubmodelAction.class, MODEL_ACTION_RENAME_SUBMODEL); //dialog submodel name
        ActionSource.put(ModelOptionAction.class, MODEL_OPTION_GDOC_VERIFICATION_MODE);
        ActionSource.put(ModelOptionAction.class, MODEL_OPTION_GDCOLL_AUTOMATIC_MODE);
        ActionSource.put(SelectedRealNodeAction.class, MODEL_ACTION_ADD_SELECTED_TO_SUBMODEL);
        ActionSource.put(SelectedRealNodeAction.class, MODEL_ACTION_ADD_SELECTED_TO_NEW_SUBMODEL);
        ActionSource.put(SelectedRealNodeAction.class, MODEL_ACTION_ADD_SELECTED_TO_ALL_SUBMODELS);
        ActionSource.put(SelectedRealNodeAction.class, MODEL_ACTION_LINK_SELECTED_TO_NEW_SUBMODEL);
    }

}
