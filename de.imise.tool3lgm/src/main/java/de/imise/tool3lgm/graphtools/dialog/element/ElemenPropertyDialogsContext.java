package de.imise.tool3lgm.graphtools.dialog.element;

import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;

/**
 * Diese Klasse verwaltet statisch die geöffneten Dialoge der ModellElemente. Diese Funktionalität war urpsünglich in ModelConstants.
 *
 * @author AXS (30 Apr 2019)
 */
public class ElemenPropertyDialogsContext {

    /**
     * Liste aller geöffneten Dialoge
     */
    private static final List<ElementPropertyDialog> dialogs = new ArrayList<>();

    /**
     * Überprueft, ob fuer ein Objekt schon ein Dialog existiert und gibt diesen ggf. zurück
     *
     * @param obj Dialog zu diesem Objekt
     * @return ModelElement obj, wenn schon ein Dialog existiert, null sonst
     */
    public static ElementPropertyDialog hasOpenDialog(final ModelElement obj) {
        for (ElementPropertyDialog dialog : dialogs) {
            if (obj == dialog.getModelElement()) {
                return dialog;
            }
        }
        return null;
    }

    /**
     * Liefert eine Dialog zum übergebenen Element. Dieser Dialog wird nen angelegt, wenn nicht schon ein Dialog dieses Elementes in der Liste der
     * geöffneten Dialoge ist.
     *
     * @param me
     *            ModelElement fpr das der Dialog geöffnet werden soll
     * @return
     */
    public static ElementPropertyDialog getDialog(final ModelElement me) {
        ElementPropertyDialog prop = ElemenPropertyDialogsContext.hasOpenDialog(me);
        if (prop == null) {
            prop = me.getNewPropertyDialogInsance();
            dialogs.add(prop);
        }
        return prop;
    }

    /**
     * Gibt Verctor mit allen geoeffneten Dialogen zurueck
     *
     * @return ArrayList mit allen geoeffneten Dialogen
     */
    public static final List<ElementPropertyDialog> getDialogs() {
        return dialogs;
    }

    /**
     * entfernt einen Dialog aus dem ArrayList mit allen geoeffneten Dialogen
     *
     * @param modelElement Element dessen Dialog aus dem ArrayList entfernt werden soll
     */
    public static final void removeDialog(final ModelElement modelElement) {
        for (int n = 0; n < dialogs.size(); n++) {
            ElementPropertyDialog dialog = dialogs.get(n);
            if (modelElement == dialog.getModelElement()) {
                dialogs.remove(n--);
            }
        }
    }

    /**
     * Liefert rteu, wenn es wenigstens einen geöffneten Dialog gibt.
     *
     * @return
     */
    public static boolean hasOpenDialogs() {
        return !dialogs.isEmpty();
    }

    /**
     * Liefert die Anzahl der geöffneten Dialoge.
     *
     * @return
     */
    public static int getDialogCount() {
        return dialogs.size();
    }

    /**
     * Liefert den {@link ElementPropertyDialog}, der in der Liste der geöffneten Dialoge am angegebene Index steht.
     *
     * @param index
     * @return
     */
    public static ElementPropertyDialog getDialog(final int index) {
        return dialogs.get(index);
    }

    /**
     * Liefert ein Iterable-Objekt für alle geöffneten Dialoge
     *
     * @return
     */
    public Iterable<ElementPropertyDialog> iterateDialogs() {
        return dialogs;
    }

    /**
     * Schließt die Dialoge aller Elemente des übergebenen GraphDocuments
     *
     * @param doc
     */
    public static void closeAllDialogs(final GraphDocument doc) {
        for (int n = 0; n < dialogs.size(); n++) {
            ElementPropertyDialog dialog = dialogs.get(n);
            // wenn der Dialog zum zu schließenden Modell gehört
            if (doc.isMyElement(dialog.getModelElement())) {
                // alle Änderungen der geöffneten Dialoge zurück rollen
                dialog.cancel();
                // in pd.cancel() wird die dialogs.size() um -1 geändert
                n--;
            }
        }
    }

}
