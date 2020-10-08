/**
 *
 */
package de.imise.tool3lgm.graphtools.consistency.tableview;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.gui.menu.ElementSelectionContextGenerator.addConnectMenuItems;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import de.imise.tool3lgm.graphtools.consistency.checker.ConsistencyChecker;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.consistency.error.MissingPathError;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.util.NamedObjectContainer;

/**
 * @author AXS
 */
public class ConsistencyErrorTableMouseListener extends MouseAdapter {

    /**
     * Der Table, bei dem auf Ereignisse reagiert werden soll
     */
    private final JTable table;

    /**
     *
     */
    private final ConsistencyChecker checker;

    /**
     *
     */
    private List<AbstractConsistencyError> errors;

    /**
     *
     */
    private final List<ModelElement> selectedErrorElements = new ArrayList<>();

    /**
     *
     */
    ConsistencyErrorTableMouseListener(final ConsistencyChecker checker, final JTable errorTable) {
        this.checker = checker;
        table = errorTable;
    }

    /**
     * @param e
     */
    private void showPopup(final MouseEvent e) {
        int clickedRow = table.rowAtPoint(e.getPoint());
        table.addRowSelectionInterval(clickedRow, clickedRow);
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            return;
        }
        errors = new ArrayList<>();
        selectedErrorElements.clear();
        for (int r : rows) {
            int errorColumnIndex = ConsistencyErrorTableModel.ColumnNames.ERROR_TYPE.ordinal();
            Object errorValue = table.getValueAt(r, errorColumnIndex);
            if (NamedObjectContainer.isInstanceWithType(errorValue, AbstractConsistencyError.class)) {
                @SuppressWarnings("unchecked") //it's checked!
                NamedObjectContainer<AbstractConsistencyError> errContainer = (NamedObjectContainer<AbstractConsistencyError>) errorValue;
                AbstractConsistencyError error = errContainer.getObject();
                errors.add(error);
                ModelElement me = error.getModelElement();
                selectedErrorElements.add(me);
            }
        }
        JPopupMenu popupMenu = getPopupMenu();
        popupMenu.show(table, e.getX(), e.getY());
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if (e.isPopupTrigger()) {
            showPopup(e);
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (e.isPopupTrigger()) {
            showPopup(e);
        } else if (e.getClickCount() > 1) {
            int clickedRow = table.rowAtPoint(e.getPoint());
            int column = ConsistencyErrorTableModel.ColumnNames.ERROR_TYPE.ordinal();
            Object value = table.getValueAt(clickedRow, column);
            if (NamedObjectContainer.isInstanceWithType(value, AbstractConsistencyError.class)) {
                @SuppressWarnings("unchecked") //it's checked!
                NamedObjectContainer<AbstractConsistencyError> errContainer = (NamedObjectContainer<AbstractConsistencyError>) value;
                AbstractConsistencyError error = errContainer.getObject();
                checker.execSolution(error);
            }
        }

    }

    /**
     * @return
     */
    private JPopupMenu getPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        // JMenuItem item = new JMenuItem(new
        // AbstractAction(getResString("error_element_properties")){
        // @Override
        // public void actionPerformed(ActionEvent e) {
        // for (ModelElement me : selectedErrorElements)
        // me.getPropertyDialog(checker.getCollection()).showDialog();
        // }
        // });
        // menu.add(item);

        //at least one solution must be available to show the "open element
        //dialog to remove the error".
        boolean solutionAvailable = false;
        for (AbstractConsistencyError error : errors) {
            if (checker.isSolutionExecuteable(error)) {
                solutionAvailable = true;
                break;
            }
        }
        if (solutionAvailable) {
            JMenuItem item = new JMenuItem(new AbstractAction(getResString("error_element_solution_dialog")) {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    for (AbstractConsistencyError err : errors) {
                        checker.execSolution(err);
                    }
                }
            });
            menu.add(item);
        }
        JMenuItem item = new JMenuItem(new AbstractAction(getResString("error_element_delete")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                GDCollection gdcoll = checker.getCollection();
                gdcoll.deleteElements(selectedErrorElements, TransactionManager.STANDARD_PID);
            }
        });
        menu.add(item);
        //das jetzt folgende ist eventuell nur bedingt allgemein gültig. Da es aber bei dem bis
        //jetzt einzigen MetaPfad immer nur dann angeboten werden sollte, wenn man auch den
        //Eigenschaftsdialog zur Fehlerbehebung öffnen können sollte, ist das hier erst einmal
        //ausreichend und müsste, falls MissingPathErrors hinzukommen, bei denen man den Pfad
        //unabhängig vom Eigenschaftsdialog-Öffnen-Könnnen anlegen können soll, geändert werden.
        if (solutionAvailable) {
            addConnectMenuItemsForMissingPathErrors(menu);
        }
        return menu;
    }

    /**
     * @param menu
     */
    private void addConnectMenuItemsForMissingPathErrors(final JPopupMenu menu) {
        //only if one error is selected in the table -> add the create path menu items
        if (errors.size() == 1) {
            AbstractConsistencyError consistencyError = errors.get(0);
            if (consistencyError instanceof MissingPathError) {
                MissingPathError missingPathError = (MissingPathError) consistencyError;
                ModelElement missingPathStartElement = missingPathError.getMissingPathStartElement();
                SimpleMetaPath errorCorrectingCreatableMetaPath = missingPathError.getErrorCorrectingCreatableMetaPath();
                Collection<ModelElement> missingElements = missingPathError.getMissingElements();
                addConnectMenuItems(menu, missingPathStartElement, errorCorrectingCreatableMetaPath, missingElements);
            }

        }
    }

}
