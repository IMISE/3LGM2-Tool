/**
 *
 */
package de.imise.tool3lgm.graphtools.dialog.element;

import static de.imise.tool3lgm.Tool3lgmConstants.ERROR_ICON;

import java.awt.Component;
import java.util.Collection;

import de.imise.tool3lgm.graphtools.consistency.ModelValidator;
import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.dialog.element.panel.DisplayAndFixConsistencyErrorPanel;
import de.imise.tool3lgm.graphtools.dialog.element.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;

/**
 * Extends the dialog by adding error icons to the tabs, if you can fix an error
 * of the dialog element on this tab.
 *
 * @author AXS (28.10.2020)
 */
public final class ErrorDecoratedElementPropertyDialog extends ElementPropertyDialog {

    /**
     * @param modelElement
     */
    public ErrorDecoratedElementPropertyDialog(final ModelElement modelElement) {
        super(modelElement);
        updateDisplayedErrors();
    }

    /**
     * @param consistencyError
     * @return
     */
    public boolean isMyError(final AbstractConsistencyError consistencyError) {
        ModelElement errorElement = consistencyError.getModelElement();
        ModelElement dialogElement = getModelElement();
        return dialogElement == errorElement;
    }

    @Override
    public void update() {
        super.update();
        updateDisplayedErrors();
    }

    /**
     * Updates the error icons of the tabs
     */
    public final void updateDisplayedErrors() {
        clearPanelConsistencyErrors();
        if (BooleanProperty.OPTION_MARK_INCONSISTENT_ELEMENTS.is()) {
            setPanelConsistencyErrors();
        }
        updateConsistencyErrorIcons();
    }

    /**
     * Removes all consistency errors from the panels or subpanels
     */
    private void clearPanelConsistencyErrors() {
        for (int i = 0; i < tabbedPane.getComponentCount(); i++) {
            Component comp = tabbedPane.getComponent(i);
            if (comp instanceof DisplayAndFixConsistencyErrorPanel) {
                DisplayAndFixConsistencyErrorPanel panel = (DisplayAndFixConsistencyErrorPanel) comp;
                panel.clearConsistencyErrors();
            }
        }
    }

    /**
     * Sets the consistency errors to the panels and subpanels
     */
    private void setPanelConsistencyErrors() {
        GDCollection gdcoll = getCollection();
        ModelValidator modelValidator = gdcoll.getModelValidator();
        Collection<AbstractConsistencyError> consistencyErrors = modelValidator.getInconsistencies();
        for (AbstractConsistencyError consistencyError : consistencyErrors) {
            if (isMyError(consistencyError)) {
                for (int i = 0; i < getTabCount(); i++) {
                    Component comp = getTabComponentAt(i);
                    if (comp instanceof DisplayAndFixConsistencyErrorPanel) {
                        DisplayAndFixConsistencyErrorPanel panel = (DisplayAndFixConsistencyErrorPanel) comp;
                        panel.addConsistencyError(consistencyError);
                    }
                }
            }
        }
    }

    /**
     * Updates the tab icons. If the contained panel has resp. can fix an error,
     * this tab is marked with an error icon. If not no icon is set.
     */
    private void updateConsistencyErrorIcons() {
        for (int i = 0; i < getTabCount(); i++) {
            Component comp = getTabComponentAt(i);
            boolean showErrorIcon = false;
            if (comp instanceof ElementDialogPanel) {
                ElementDialogPanel panel = (ElementDialogPanel) comp;
                if (panel.hasConsistencyErrors()) {
                    showErrorIcon = true;
                }
            }
            setTabIcon(i, showErrorIcon ? ERROR_ICON : null);
        }
    }

    //    void p(final AbstractConsistencyError error) {
    //        // 'es' ist null, wenn für den Fehler keine Solution hinterlegt wurde. Das gilt nur
    //        // für Fehler, für die im Eigenschaftsdialog des Elementes dann ein zusätzlicher
    //        // Tab angezeigt werden soll, in dem man den Fehler beheben kann
    //        if (error instanceof AbstractPathError) {
    //            GDCollection gdcoll = error.getCollection();
    //            MetaModel metaModel = gdcoll.getMetaModel();
    //            ModelValidatorDefinition modelValidatorDefinition = metaModel.getModelValidatorDefinition();
    //            ErrorSolution es = modelValidatorDefinition.getSolution(error);
    //            ImageIcon icon = Tool3lgmConstants.getIcon("error.gif");
    //            if (es == null) {
    //                AbstractPathError pathError = (AbstractPathError) error;
    //                MetaPath metaPath = pathError.getMetaPath();
    //                ModelElement me = pathError.getModelElement();
    //                ElementPropertyDialog dialog = me.getPropertyDialog();
    //                int selectedTabIndex = dialog.selectTab(metaPath);
    //                if (selectedTabIndex < 0) {
    //                    Class<? extends ModelElement> errorConnectedClass = metaPath.getEndClass();
    //                    ElementsNameBuilder elementsNameBuilder = gdcoll.getElementsNameBuilder();
    //                    String tabName = metaPath.isSingleConnection() ? elementsNameBuilder.getDisplayableName(errorConnectedClass) : elementsNameBuilder.getDisplayablePluralName(errorConnectedClass);
    //                    //if the maximum cardinality is exceeded -> show always a multiple connection panel
    //                    dialog.addPathConnectionPanel(metaPath, error instanceof MaxCardinalityError);
    //                    dialog.setLastTabIcon(icon);
    //                    dialog.setLastTabTitle(tabName);
    //                    dialog.selectLastTab();
    //                } else {
    //                    dialog.setTabIcon(selectedTabIndex, icon);
    //                }
    //                dialog.showDialog();
    //            } else {
    //                Collection<ModelElement> solutionPropertyDialogElement = modelValidatorDefinition.getSolutionPropertyDialogElement(error);
    //                if (solutionPropertyDialogElement == null || solutionPropertyDialogElement.isEmpty()) {
    //                    return;
    //                }
    //                for (ModelElement connected : solutionPropertyDialogElement) {
    //                    ElementPropertyDialog dialog = connected.getPropertyDialog();
    //                    SimpleMetaPath panelMetaPath = es.getPanelMetaPath();
    //                    int selectedTabIndex = dialog.selectTab(panelMetaPath);
    //                    dialog.setTabIcon(selectedTabIndex, icon);
    //                    dialog.showDialog();
    //                }
    //            }
    //        } else if (error instanceof AbstractIDError) {
    //            ElementPropertyDialog dialog = error.getModelElement().getPropertyDialog();
    //            dialog.selectTab(PropertyDialogUserFieldPanel.class);
    //            dialog.showDialog();
    //        }
    //
    //    }
}
