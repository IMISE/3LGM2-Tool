package de.imise.tool3lgm.graphtools.dialog.element.panel;

import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractConsistencyError;

/**
 * Should be implemented by all panels that display errors
 * or where errors can be fixed.
 *
 * @author AXS (28.10.2020)
 */
public interface DisplayAndFixConsistencyErrorPanel {

    /**
     * This panel should return either itself or a sub-panel of itself
     * that displays the MetaPath of the error for the ModelElement of
     * the consistency error - i.e. the panel where the error could be
     * fixed. If it is not this panel or subpanel itself, then
     * <code>null</code> is returned.
     *
     * @param consistencyError
     * @return
     */
    public ElementDialogPanel getResponsiblePanelForConsistencyError(final AbstractConsistencyError consistencyError);

    /**
     * Adds a consistency error to the panel
     *
     * @param consistencyError
     * @return
     */
    public boolean addConsistencyError(final AbstractConsistencyError consistencyError);

    /**
     * Removes all consistency error from the panel
     */
    public void clearConsistencyErrors();

}
