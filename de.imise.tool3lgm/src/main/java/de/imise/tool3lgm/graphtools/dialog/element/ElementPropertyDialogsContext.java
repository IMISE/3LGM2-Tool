package de.imise.tool3lgm.graphtools.dialog.element;

import java.awt.Dialog.ModalityType;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.DummyGDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.tooltip.ElementToolTipProvider;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.util.ToolTipProvider;

/**
 * Diese Klasse verwaltet statisch die geöffneten Dialoge der ModellElemente.
 * Diese Funktionalität war urpsünglich in ModelConstants.
 *
 * @author AXS (30 Apr 2019)
 */
public class ElementPropertyDialogsContext {

    /**
     *
     */
    private final static ElementPropertyDialogsContext context = new ElementPropertyDialogsContext();

    /** List of all opened dialogs */
    private final List<ElementPropertyDialog> dialogs = new ArrayList<>();

    /** The tooltip provier */
    private final ToolTipProvider toolTipProvider;

    /**
     *
     */
    public ElementPropertyDialogsContext() {
        PropertyChangeListener optionsChangeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                if (UserProperties.isPropertyChange(BooleanProperty.OPTION_MARK_INCONSISTENT_ELEMENTS, event)) {
                    for (ElementPropertyDialog dialog : dialogs) {
                        if (dialog instanceof ErrorDecoratedElementPropertyDialog) {
                            ErrorDecoratedElementPropertyDialog errorDecoratedDialog = (ErrorDecoratedElementPropertyDialog) dialog;
                            errorDecoratedDialog.updateDisplayedErrors();
                        }
                    }
                }
            }
        };
        UserProperties.addPropertyChangeListener(optionsChangeListener);
        toolTipProvider = new ElementToolTipProvider();
    }

    /**
     * @return the toolTipProvider
     */
    public static final ToolTipProvider getToolTipProvider() {
        return context.toolTipProvider;
    }

    /**
     * Überprueft, ob fuer ein Objekt schon ein Dialog existiert und gibt diesen
     * ggf. zurück
     *
     * @param obj Dialog zu diesem Objekt
     * @return ModelElement obj, wenn schon ein Dialog existiert, null sonst
     */
    public static ElementPropertyDialog hasOpenDialog(final ModelElement obj) {
        return context.hasOpenDialogInternal(obj);
    }

    /**
     * Überprueft, ob fuer ein Objekt schon ein Dialog existiert und gibt diesen
     * ggf. zurück
     *
     * @param obj Dialog zu diesem Objekt
     * @return ModelElement obj, wenn schon ein Dialog existiert, null sonst
     */
    private ElementPropertyDialog hasOpenDialogInternal(final ModelElement obj) {
        for (ElementPropertyDialog dialog : dialogs) {
            if (obj == dialog.getModelElement()) {
                return dialog;
            }
        }
        return null;
    }

    /**
     * Liefert eine Dialog zum übergebenen Element. Dieser Dialog wird nen
     * angelegt, wenn nicht schon ein Dialog dieses Elementes in der Liste der
     * geöffneten Dialoge ist.
     *
     * @param me ModelElement fpr das der Dialog geöffnet werden soll
     * @return
     */
    public static ElementPropertyDialog getDialog(final ModelElement me) {
        ElementPropertyDialog dialog = ElementPropertyDialogsContext.hasOpenDialog(me);
        if (dialog == null) {
            dialog = me.getNewPropertyDialogInsance();
            context.dialogs.add(dialog);
            context.addTooltipMouseListeners(dialog);
        }
        GDCollection gdcoll = me.getCollection();
        if (gdcoll instanceof DummyGDCollection) {
            dialog.setModalityType(ModalityType.APPLICATION_MODAL);
        }
        return dialog;
    }

    /**
     * entfernt einen Dialog aus dem ArrayList mit allen geoeffneten Dialogen
     *
     * @param modelElement Element dessen Dialog aus dem ArrayList entfernt
     *            werden soll
     */
    public static final void removeDialog(final ModelElement modelElement) {
        for (int n = 0; n < context.dialogs.size(); n++) {
            ElementPropertyDialog dialog = context.dialogs.get(n);
            if (modelElement == dialog.getModelElement()) {
                context.dialogs.remove(n--);
            }
        }
    }

    /**
     * Liefert rteu, wenn es wenigstens einen geöffneten Dialog gibt.
     *
     * @return
     */
    public static boolean hasOpenDialogs() {
        return !context.dialogs.isEmpty();
    }

    /**
     * Liefert die Anzahl der geöffneten Dialoge.
     *
     * @return
     */
    public static int getDialogCount() {
        return context.dialogs.size();
    }

    /**
     * Liefert den {@link ElementPropertyDialog}, der in der Liste der
     * geöffneten Dialoge am angegebene Index steht.
     *
     * @param index
     * @return
     */
    public static ElementPropertyDialog getDialog(final int index) {
        return context.dialogs.get(index);
    }

    /**
     * Liefert ein Iterable-Objekt für alle geöffneten Dialoge
     *
     * @return
     */
    public static Iterable<ElementPropertyDialog> iterateDialogs() {
        return context.dialogs;
    }

    /**
     * Schließt die Dialoge aller Elemente des übergebenen GraphDocuments
     *
     * @param doc
     */
    public static void closeAllDialogs(final GraphDocument doc) {
        for (int n = context.dialogs.size() - 1; n >= 0; n--) {
            ElementPropertyDialog dialog = context.dialogs.get(n);
            // wenn der Dialog zum zu schließenden Modell gehört
            if (doc.isMyElement(dialog.getModelElement())) {
                // alle Änderungen der geöffneten Dialoge zurück rollen
                dialog.cancel();
                // in pd.cancel() wird die dialogs.size() um -1 geändert
                n--;
            }
        }
    }

    /**
     * @param dialog
     */
    private void addTooltipMouseListeners(final ElementPropertyDialog dialog) {
        Collection<JComponent> toolTipTargets = dialog.getToolTipTargets();
        for (JComponent toolTipTarget : toolTipTargets) {
            if (toolTipTarget != null) {
                toolTipProvider.addToolTipMouseListeners(toolTipTarget);
            }
        }
    }

}
