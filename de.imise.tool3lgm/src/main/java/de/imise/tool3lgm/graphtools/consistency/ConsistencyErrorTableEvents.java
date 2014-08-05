/**
 * 
 */
package de.imise.tool3lgm.graphtools.consistency;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.consistency.error.CardinalityError;
import de.imise.tool3lgm.graphtools.consistency.error.MaxCardinalityError;
import de.imise.tool3lgm.graphtools.consistency.error.MinCardinalityError;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
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

    private ArrayList<MinCardinalityError> minErrors;
    private ArrayList<MaxCardinalityError> maxErrors;
    private final ArrayList<ModelElement> selectedErrorElements = new ArrayList<ModelElement>();

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
            minErrors = new ArrayList<MinCardinalityError>();
            maxErrors = new ArrayList<MaxCardinalityError>();
            selectedErrorElements.clear();
            for (int r : rows) {
                NamedObjectContainer<CardinalityError> errContainer = (NamedObjectContainer<CardinalityError>) table.getValueAt(r, ConsistencyErrorTableModel.COL_NAMES.errorType.ordinal());
                CardinalityError cardErr = errContainer.getObject();
                if (cardErr instanceof MinCardinalityError) {
                    minErrors.add((MinCardinalityError) cardErr);
                } else if (cardErr instanceof MaxCardinalityError) {
                    maxErrors.add((MaxCardinalityError) cardErr);
                }
                selectedErrorElements.add(cardErr.getModelElement());
            }
            getPopupMenu().show(table, e.getX(), e.getY());
        }
    }

    /**
     * @return
     */
    private JPopupMenu getPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        // JMenuItem item = new JMenuItem(new
        // AbstractAction(Tool3lgmConstants.getResString("error_element_properties")){
        // @Override
        // public void actionPerformed(ActionEvent e) {
        // for (ModelElement me : selectedErrorElements)
        // me.getPropertyDialog(checker.getGDCollection()).showDialog();
        // }
        // });
        // menu.add(item);

        JMenuItem item = new JMenuItem(new AbstractAction(Tool3lgmConstants.getResString("error_element_solution_dialog")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                for (CardinalityError err : minErrors) {
                    checker.execSolution(err);
                }
                for (CardinalityError err : maxErrors) {
                    checker.execSolution(err);
                }
            }
        });
        menu.add(item);
        item = new JMenuItem(new AbstractAction(Tool3lgmConstants.getResString("error_element_delete")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                checker.getGDCollection().deleteElements(selectedErrorElements, TransactionManager.STANDARD_PID);
            }
        });
        menu.add(item);
        return menu;
    }

}
