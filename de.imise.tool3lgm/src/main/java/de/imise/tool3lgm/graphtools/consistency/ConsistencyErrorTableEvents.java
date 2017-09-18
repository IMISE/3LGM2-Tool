/**
 *
 */
package de.imise.tool3lgm.graphtools.consistency;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import de.imise.tool3lgm.graphtools.consistency.error.AbstractError;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.util.NamedObjectContainer;

/**
 * @author AXS
 */
public class ConsistencyErrorTableEvents extends MouseAdapter {

    /**
     * Der Table, bei dem auf Ereignisse reagiert werden soll
     */
    private final JTable table;

    /**
     * 
     */
    private final ConsistencyChecker checker;

    private ArrayList<AbstractError> errors;
    private final ArrayList<ModelElement> selectedErrorElements = new ArrayList<>();

    /**
     * 
     */
    ConsistencyErrorTableEvents(final ConsistencyChecker checker, final JTable errorTable) {
        super();
        this.checker = checker;
        table = errorTable;
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (e.isPopupTrigger()) {
            int clickedRow = table.rowAtPoint(e.getPoint());
            table.addRowSelectionInterval(clickedRow, clickedRow);
            int[] rows = table.getSelectedRows();
            if (rows.length == 0) {
                return;
            }
            errors = new ArrayList<>();
            selectedErrorElements.clear();
            for (int r : rows) {
                NamedObjectContainer<AbstractError> errContainer = (NamedObjectContainer<AbstractError>) table.getValueAt(r, ConsistencyErrorTableModel.COL_NAMES.errorType.ordinal());
                AbstractError error = errContainer.getObject();
                errors.add(error);
                selectedErrorElements.add(error.getModelElement());
            }
            getPopupMenu().show(table, e.getX(), e.getY());
        } else if (e.getClickCount() > 1) {
            int clickedRow = table.rowAtPoint(e.getPoint());
            int column = ConsistencyErrorTableModel.COL_NAMES.errorType.ordinal();
            Object value = table.getValueAt(clickedRow, column);
            NamedObjectContainer<AbstractError> errContainer = (NamedObjectContainer<AbstractError>) value;
            AbstractError error = errContainer.getObject();
            checker.execSolution(error);
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
        // me.getPropertyDialog(checker.getGDCollection()).showDialog();
        // }
        // });
        // menu.add(item);

        JMenuItem item = new JMenuItem(new AbstractAction(getResString("error_element_solution_dialog")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                for (AbstractError err : errors) {
                    checker.execSolution(err);
                }
            }
        });
        menu.add(item);
        item = new JMenuItem(new AbstractAction(getResString("error_element_delete")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                checker.getGDCollection().deleteElements(selectedErrorElements, TransactionManager.STANDARD_PID);
            }
        });
        menu.add(item);
        return menu;
    }

}
