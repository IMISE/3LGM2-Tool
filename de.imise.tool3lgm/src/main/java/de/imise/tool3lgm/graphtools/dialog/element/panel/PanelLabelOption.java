package de.imise.tool3lgm.graphtools.dialog.element.panel;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Options which Label should be presented for a panel.
 *
 * @author AXS (20.01.2020)
 */
public enum PanelLabelOption {
    /**
     * Indicator to label the panel with the end element type name from the
     * resources. If the meta path is a single connection meta path, the
     * singular will be shown as label. If not the plural.
     */
    LABEL_END_ELEMENT_TYPE,
    /**
     * Indicator to label the panel with the end element type name from the
     * resources in singular.
     */
    LABEL_END_ELEMENT_TYPE_SINGULAR,
    /**
     * Indicator to label the panel with the end element type name from the
     * resources in plural.
     */
    LABEL_END_ELEMENT_TYPE_PLURAL,
    /**
     * Indicator to label the panel with first edge element type name from the
     * resources. If the meta path is a single connection meta path, the
     * singular will be shown as label. If not the plural.
     */
    LABEL_FIRST_EDGE_ELEMENT_NAME {
        @Override
        public int getEdgeIndex() {
            return 0;
        }
    },
    /**
     * Indicator to label the panel with last edge element type name from the
     * resources. If the meta path is a single connection meta path, the
     * singular will be shown as label. If not the plural.
     */
    LABEL_LAST_EDGE_ELEMENT_NAME,
    /**
     * Indicator to label the panel with last edge element type name from the
     * resources in singular.
     */
    LABEL_LAST_EDGE_ELEMENT_NAME_SINGULAR,
    /**
     * Indicator to label the panel with last edge element type name from the
     * resources in plural.
     */
    LABEL_LAST_EDGE_ELEMENT_NAME_PLURAL,
    /**
     * Indicator to label the panel with directed name of the connection from
     * the resources.
     */
    LABEL_FIRST_EDGE_CONNECTION_NAME {
        @Override
        public int getEdgeIndex() {
            return 0;
        }
    },
    /**
     * Indicator to label the panel with directed name of the connection from
     * the resources.
     */
    LABEL_LAST_EDGE_CONNECTION_NAME,

    /**
     * Indicator to label the panel with directed name of the connection and the
     * type name of the connected element type from the resources.
     */
    LABEL_LAST_EDGE_CONNECTION_NAME_WITH_CONNECTED_ELEMENT_TYPE,

    /**
     * Indicator to label the panel with the start element type of the last edge
     * with the name from the resources. If the meta path is a single connection
     * meta path, the singular will be shown as label. If not the plural.
     */
    LABEL_LAST_EDGE_START_ELEMENT_TYPE,
    /**
     * Indicator to label the panel with the end element type of the last edge
     * with name from the resources in singular.
     */
    LABEL_LAST_EDGE_START_ELEMENT_TYPE_SINGULAR,
    /**
     * Indicator to label the panel with the end element type of the last edge
     * with name from the resources in plural.
     */
    LABEL_LAST_EDGE_START_ELEMENT_TYPE_PLURAL;

    /**
     * @return the index of the edge which should be taken to create the label.
     *         -1 means the last edge. If the value is >=0 then it is the index
     *         of the edge class in the metapath.
     */
    public int getEdgeIndex() {
        return -1;
    }

    /**  */
    private static final Set<PanelLabelOption> EDGE_ELEMENT_NAMES_OPTIONS = ImmutableSet.of(LABEL_FIRST_EDGE_ELEMENT_NAME, LABEL_LAST_EDGE_ELEMENT_NAME, LABEL_LAST_EDGE_ELEMENT_NAME_SINGULAR, LABEL_LAST_EDGE_ELEMENT_NAME_PLURAL);

    /**
     * @return
     */
    public boolean isEdgeElementNameOption() {
        return EDGE_ELEMENT_NAMES_OPTIONS.contains(this);
    }

    /**  */
    private static final Set<PanelLabelOption> EDGE_CONNECTION_NAMES_OPTIONS = ImmutableSet.of(LABEL_FIRST_EDGE_CONNECTION_NAME, LABEL_LAST_EDGE_CONNECTION_NAME, LABEL_LAST_EDGE_CONNECTION_NAME_WITH_CONNECTED_ELEMENT_TYPE);

    /**
     * @return
     */
    public boolean isEdgeConnectionNameOption() {
        return EDGE_CONNECTION_NAMES_OPTIONS.contains(this);
    }

    /**  */
    private static final Set<PanelLabelOption> SINGULAR_NAMES_OPTIONS = ImmutableSet.of(LABEL_END_ELEMENT_TYPE_SINGULAR, LABEL_LAST_EDGE_ELEMENT_NAME_SINGULAR, LABEL_LAST_EDGE_START_ELEMENT_TYPE_SINGULAR);

    /**
     * @return
     */
    public boolean isSingularNameOption() {
        return SINGULAR_NAMES_OPTIONS.contains(this);
    }

    /**  */
    private static final Set<PanelLabelOption> PLURAL_NAMES_OPTIONS = ImmutableSet.of(LABEL_END_ELEMENT_TYPE_PLURAL, LABEL_LAST_EDGE_ELEMENT_NAME_PLURAL, LABEL_LAST_EDGE_START_ELEMENT_TYPE_PLURAL);

    /**
     * @return
     */
    public boolean isPluralNameOption() {
        return PLURAL_NAMES_OPTIONS.contains(this);
    }

    /**  */
    private static final Set<PanelLabelOption> START_ELEMENT_NAMES_OPTIONS = ImmutableSet.of(LABEL_LAST_EDGE_START_ELEMENT_TYPE, LABEL_LAST_EDGE_START_ELEMENT_TYPE_SINGULAR, LABEL_LAST_EDGE_START_ELEMENT_TYPE_PLURAL);

    /**
     * @return
     */
    public boolean isStartElementNameOption() {
        return START_ELEMENT_NAMES_OPTIONS.contains(this);
    }

}