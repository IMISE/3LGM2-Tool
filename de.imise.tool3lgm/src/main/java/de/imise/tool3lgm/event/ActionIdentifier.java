package de.imise.tool3lgm.event;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.event.ActionEvent;
import java.util.MissingResourceException;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import de.imise.tool3lgm.KeyStrokes;

/**
 * Identifiers für {@link StaticAction}s
 * <p>
 * Über diese Identifier, können {@link StaticAction}s ihre Attribute bekommen.
 */
public enum ActionIdentifier {

    // file
    ACTION_NEW_MODEL,
    ACTION_OPEN_MODEL,
    ACTION_SAVE_MODEL,
    ACTION_SAVE_MODEL_AS,
    ACTION_CLOSE_MODEL,
    ACTION_SHOW_MODEL_DESCRIPTION_FRAME,
    // import
    ACTION_IMPORT_SUBMODEL,
    ACTION_IMPORT_MODEL,
    ACTION_IMPORT_DATA,
    // export
    ACTION_EXPORT_GRAPHIC,
    ACTION_EXPORT_GRAPHML_YED,
    ACTION_EXPORT_GRAPHML_YFILES,
    ACTION_EXPORT_XSLT,
    ACTION_EXPORT_SUBMODEL,
    ACTION_EXPORT_HTML,
    ACTION_EXPORT_DATA,
    //exit
    ACTION_EXIT,

    // edit
    ACTION_UNDO,
    ACTION_REDO,
    ACTION_SEARCH,
    ACTION_SELECT_ALL,

    // view
    // toolbar
    OPTION_SHOW_PAINTING_TOOLBAR,
    OPTION_SHOW_STANDARD_TOOLBAR,
    OPTION_MODEL_BROWSER_SHOW,
    ACTION_GRAPH_SHOW_SINGLE_LAYER_PERSPECTIVE,
    ACTION_GRAPH_SHOW_THREE_LAYER_PERSPECTIVE,
    ACTION_ACTIVATE_DOMAIN_LAYER,
    ACTION_ACTIVATE_LOGICAL_TOOL_LAYER,
    ACTION_ACTIVATE_PHYSICAL_TOOL_LAYER,
    ACTION_OPEN_GRAPH_VIEW_SETTINGS_DIALOG,
    ACTION_OPEN_MATRIX_VIEW,

    // analysis
    ACTION_ANALYSIS_OPEN_REPOSITORY,
    ACTION_ANALYSIS_OPEN_EDITOR,
    ACTION_ANALYSIS_RESET_RESULT,
    ACTION_ANALYSIS_REDUNDANCY,
    OPTIONS_SIMPLE_REDUNDANCY_ANALYSIS,

    //context
    ACTION_SHOW_ELEMENT_PROPERTY_DIALOG,

    // layout
    ACTION_OPEN_GLOBAL_LAYOUT_EDITOR,

    ACTION_ANALYSIS_CHOOSE_GRAPH_ANALYSIS_RESULT_COLOR,

    // extras
    ACTION_OPEN_USERFIELD_DEFINITION_DIALOG,
    ACTION_OPEN_USERFIELD_VALUE_EDITOR_DIALOG,

    // window
    models_parallel_arrangement,
    overlapping_arrangement,

    // help
    ACTION_OPEN_HELP_DIALOG,
    ACTION_ACTIVATE_DIRECT_HELP,
    ACTION_SHOW_INFORMATION_SYSTEM_EVALUATION_TUTORIAL,
    ACTION_SHOW_ONLINE_HELP,
    ACTION_SHOW_3LGM_WEB_SITE,
    ACTION_OPEN_EXAMPLE_MODEL_FILE,
    ACTION_OPEN_FILE_CHOSSER_IN_MODEL_LIBRARY,
    ACTION_OPEN_ABOUT_DIALOG,
    ACTION_IMPORT_LICENSE_FILE,

    ;

    /**
     * Gibt den command-<code>String</code> des {@link ActionEvent}s wieder, das beim Auslösen der
     * durch <code>identifier</code> identifizierten {@link StaticAction} entsteht.
     *
     * @param actionCommand
     */
    @Deprecated
    String getActionCommand() {
        return null;
        // return Tool3lgmConstants.getActionCommand(name());
    }

    /**
     * Gibt den {@link KeyStroke} zurück, der die durch diesen Identifier identifizierte {@link StaticAction} auslöst.
     *
     * @return
     */
    @Deprecated
    KeyStroke getKeyStroke() {
        return KeyStrokes.getKeyStroke(this);
    }

    /**
     * Gibt das große Icon für die durch diesen Identifier identifizierte {@link StaticAction} wieder
     *
     * @return
     */
    @Deprecated
    Icon getLargeIcon() {
        return null;
        // return Tool3lgmConstants.getIcon(name().concat(Tool3lgmConstants.LARGE_ICON_SUFFIX));
    }

    /**
     * Gibt ausführliche Beschreibung der durch diesen Identifier identifizierten {@link StaticAction} zurück. (Zur Verwendung bei Hilfe)
     *
     * @param description
     */
    @Deprecated
    String getLongDescription() {
        return null;
        // return
        // Tool3lgmConstants.getDescription(name().concat(Tool3lgmConstants.LONG_DESCRIPTION_SUFFIX));

    }

    /**
     * Gibt kurze Beschreibung der durch diesen Identifier identifizierten {@link StaticAction} zurück. (Zur Verwendung als Tooltip)
     *
     * @param description
     */
    @Deprecated
    String getShortDescription() {
        return null;
        // return
        // Tool3lgmConstants.getDescription(name().concat(Tool3lgmConstants.SHORT_DESCRIPTION_SUFFIX));
    }

    /**
     * Gibt das kleine Icon für die durch diesen Identifier identifizierte {@link StaticAction} wieder
     *
     * @return
     */
    @Deprecated
    Icon getSmallIcon() {
        return null;
        // return Tool3lgmConstants.getIcon(name().concat(Tool3lgmConstants.SMALL_ICON_SUFFIX));
    }

    /**
     * Gibt den anzuzeigenden Text für die durch diesen Identifier identifizierte {@link StaticAction} wieder.
     *
     * @param
     */
    @Deprecated
    String getText() {
        try {
            return getResString(toString());
        } catch (MissingResourceException e) {
            e.printStackTrace();
            return toString();
        }
    }
}
