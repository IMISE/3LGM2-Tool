package de.imise.tool3lgm.event;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.event.ActionEvent;
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
    ACTION_IMPORT_SUBMODEL,
    ACTION_IMPORT_MODEL,
    ACTION_IMPORT_DATA,
    // export
    ACTION_EXPORT_GRAPHIC,
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
    painting,
    standard,
    browser,
    showSubModelsInBrowserSideBySide,
    switch_perspective,
    ACTION_GRAPH_SHOW_SINGLE_LAYER_PERSPECTIVE,
    ACTION_GRAPH_SHOW_THREE_LAYER_PERSPECTIVE,
    ACTION_ACTIVATE_DOMAIN_LAYER,
    ACTION_ACTIVATE_LOGICAL_TOOL_LAYER,
    ACTION_ACTIVATE_PHYSICAL_TOOL_LAYER,
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
    ACTION_ANALYSIS_OPEN_REPOSITORY,
    ACTION_ANALYSIS_OPEN_EDITOR,
    ACTION_ANALYSIS_RESET_RESULT,
    ACTION_ANALYSIS_REDUNDANCY,
    ACTION_ANALYSIS_DATA_AVAILABILITY,
    OPTION_CHECK_CONSISTENCY,
    create_submodel,
    SIMPLE_REDUNDNANCY_ANALYSIS,

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
    HIDE_UNASSOCIATED,
    UNHIDE_ALL,
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
        return Tool3lgmConstants.getKeyStroke(this);
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
