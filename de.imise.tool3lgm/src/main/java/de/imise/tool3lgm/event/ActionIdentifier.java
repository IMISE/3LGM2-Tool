package de.imise.tool3lgm.event;

import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.MissingResourceException;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import de.imise.tool3lgm.Tool3lgmConstants;

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
    import_submodel {
        @Override
        public String toString() {
            return SUBMODEL;
        }
    },
    import_model {
        @Override
        public String toString() {
            return WHOLE_MODEL;
        }
    },

    import_data {
        @Override
        public String toString() {
            return DATA;
        }
    },
    // export
    export_graphic,
    export_xslt,
    export_submodel {
        @Override
        public String toString() {
            return SUBMODEL;
        }
    },
    export_web {
        @Override
        public String toString() {
            return WEB;
        }
    },
    export_data {
        @Override
        public String toString() {
            return DATA;
        }
    },
    print,
    exit,

    // edit
    undo,
    redo,
    search,
    select_all,
    copy,
    cut,
    paste,
    clear_clipboard,
    MODEL_ACTION_REMOVE_CHILDS,
    remove_from_submodel,
    remove_from_model,
    remove,

    // view
    // toolbar
    painting,
    standard,
    browser,
    showSubModelsInBrowserSideBySide,
    switch_perspective,
    one_layer_perspective,
    three_layer_perspective,
    domain_layer,
    logical_tool_layer,
    physical_tool_layer,
    settings,
    matrix,

    // layout
    global_layout,
    // layerLayout
    layer_reset_color,
    layer_change_color,
    // layerTransparency
    layer_no_transparency,
    layer_semi_transparency,
    layer_full_transparency,
    // elementLayout
    font,
    element_change_color,
    // elementTransparency
    element_no_transparency,
    element_semi_transparency,
    element_full_transparency,
    element_reset_color,
    element_reset_transparency,
    reset_font,
    // reset_shape,
    // reset_size,
    reset_all,
    // icon
    no_icon,
    choose_icon,
    // textAlignment
    // textAlignmentHorizontal
    text_left,
    text_center_horizontal,
    text_right,
    // textAlignmentVertical
    text_top,
    text_center_vertical,
    text_bottom,
    // level
    highest,
    increase,
    decrease,
    lowest,

    // elementAlignment
    // elementAlignmentHorizontal
    element_left,
    element_center_horizontal,
    element_right,
    // elementAlignmentVertical
    element_top,
    element_center_vertical,
    element_bottom,
    element_width,
    element_height,
    element_width_and_height,

    // submodels
    new_submodel,
    remove_submodel,
    rename_submodel,

    // analysis
    repository,
    analysis_editor,
    reset_result,
    redundancy_analysis,
    data_availability,
    consistency_check,
    create_submodel,
    configurational_redundancy,
    data_redundancy,

    // options
    // General
    removeWarning,
    consider_parents,
    show_hierarchical,
    // browserOptions
    submodel_specific,
    show_multiple_browsers,
    show_userdefinded_properties,
    // graphicOptions
    paintEdgesOnlyForSelectedElements,
    analysis_color,
    rendering_options,
    useRaster,
    showRaster,
    signify_linked_elements,
    // unused_interfaces,
    HIDE_UNASSOCIATED_INTERFACES,
    UNHIDE_ALL_INTERFACES,
    automatic_coloring,
    show_tooltips,
    signify_coarsement,
    auto_move_children,
    rmi_settings,
    // localeOptions
    german,
    english,

    // extras
    userfields,
    attribute_editor,
    activate_calculation,
    automatic_etmt_assignment,

    // window
    models_parallel_arrangement,
    overlapping_arrangement,

    // help
    help,
    direct_help,
    evaluation,
    online_help,
    lgm_online,
    example,
    model_library,
    about,
    import_license,

    // context
    property_dialog,

    link_has_part,
    link_is_part_of,
    link_is_used_by,
    link_is_updated_by,
    link_uses,
    link_updates,
    link_owns_interface,
    link_owns_dbs,
    link_sends_to,
    link_receives_from,
    link_is_connected_with,

    unlink_hasPart,
    unlink_isPartOf,
    unlink_is_used_by,
    unlink_is_updated_by,
    unlink_uses,
    unlink_updates,
    unlink_ownsInterface,
    unlink_ownsDBS,
    unlink_sendsTo,
    unlink_receivesFrom,
    unlink_isConnectedWith,

    take_over_in_submodel,
    link_with_submodel,

    set_element_visible,
    set_element_invisible,

    expand_element,
    collapse_element,

    element_analysis,

    join,

    create_textfield,

    set_configurations_visible,
    set_configurations_invisible,

    internal,
    verification,
    interactive,
    commandline,
    show_queue,
    test

    ;
    // KEYS
    private static final String SUBMODEL = "submodel";
    private static final String WHOLE_MODEL = "whole_model";
    private static final String DATA = "data";
    private static final String WEB = "web";

    /**
     * Liefert den zur {@link Locale} <code>l</code> gehörigen Identifier.
     * <p>
     * Per Konvention wird hier der Identifier zurückgegeben, der dem Namen von <code>l</code> im Englischen in Kleinbuchstaben entspricht. Bsp.:
     * {@link Locale#GERMAN} --> {@link #german}
     * 
     * @param l
     * @return
     */
    static ActionIdentifier getIdentifierFor(final Locale l) {
        return valueOf(l.getDisplayName(Locale.ENGLISH).trim().toLowerCase());
    }

    /**
     * Gibt den command-<code>String</code> des {@link ActionEvent}s wieder, das beim Auslösen der
     * durch <code>identifier</code> identifizierten {@link StaticAction} entsteht.
     * 
     * @param actionCommand
     */
    String getActionCommand() {
        return null;
        // return Tool3lgmConstants.getActionCommand(name());
    }

    /**
     * Gibt den {@link KeyStroke} zurück, der die durch diesen Identifier identifizierte {@link StaticAction} auslöst.
     * 
     * @return
     */
    KeyStroke getKeyStroke() {
        return Tool3lgmConstants.getKeyStroke(this);
    }

    /**
     * Gibt das große Icon für die durch diesen Identifier identifizierte {@link StaticAction} wieder
     * 
     * @return
     */
    Icon getLargeIcon() {
        return null;
        // return Tool3lgmConstants.getIcon(name().concat(Tool3lgmConstants.LARGE_ICON_SUFFIX));
    }

    /**
     * Gibt ausführliche Beschreibung der durch diesen Identifier identifizierten {@link StaticAction} zurück. (Zur Verwendung bei Hilfe)
     * 
     * @param description
     */
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
    Icon getSmallIcon() {
        return null;
        // return Tool3lgmConstants.getIcon(name().concat(Tool3lgmConstants.SMALL_ICON_SUFFIX));
    }

    /**
     * Gibt den anzuzeigenden Text für die durch diesen Identifier identifizierte {@link StaticAction} wieder.
     * 
     * @param
     */
    String getText() {
        try {
            return Tool3lgmConstants.getResString(toString());
        } catch (MissingResourceException e) {
            e.printStackTrace();
            return toString();
        }
    }
}
