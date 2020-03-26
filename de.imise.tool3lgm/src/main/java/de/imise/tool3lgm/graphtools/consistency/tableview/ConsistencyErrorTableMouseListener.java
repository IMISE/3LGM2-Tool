/**
 *
 */
package de.imise.tool3lgm.graphtools.consistency.tableview;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

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

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.consistency.checker.ConsistencyChecker;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.consistency.error.MissingPathError;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.MetaPathFunctions;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SectionMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
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
        super();
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
            NamedObjectContainer<AbstractConsistencyError> errContainer = (NamedObjectContainer<AbstractConsistencyError>) table.getValueAt(r, ConsistencyErrorTableModel.COL_NAMES.errorType.ordinal());
            AbstractConsistencyError error = errContainer.getObject();
            errors.add(error);
            selectedErrorElements.add(error.getModelElement());
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
            int column = ConsistencyErrorTableModel.COL_NAMES.errorType.ordinal();
            Object value = table.getValueAt(clickedRow, column);
            NamedObjectContainer<AbstractConsistencyError> errContainer = (NamedObjectContainer<AbstractConsistencyError>) value;
            AbstractConsistencyError error = errContainer.getObject();
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
        // me.getPropertyDialog(checker.getCollection()).showDialog();
        // }
        // });
        // menu.add(item);

        JMenuItem item = new JMenuItem(new AbstractAction(getResString("error_element_solution_dialog")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                for (AbstractConsistencyError err : errors) {
                    checker.execSolution(err);
                }
            }
        });
        menu.add(item);
        item = new JMenuItem(new AbstractAction(getResString("error_element_delete")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                GDCollection gdcoll = checker.getCollection();
                gdcoll.deleteElements(selectedErrorElements, TransactionManager.STANDARD_PID);
            }
        });
        menu.add(item);
        addConnectMenuItemsForMissingPathErrors(menu);
        return menu;
    }

    /**
     * @param menu
     */
    private void addConnectMenuItemsForMissingPathErrors(final JPopupMenu menu) {
        if (errors.size() == 1) {
            AbstractConsistencyError consistencyError = errors.get(0);
            if (consistencyError instanceof MissingPathError) {
                MissingPathError missingPathError = (MissingPathError) consistencyError;
                ModelElement missingPathStartElement = missingPathError.getModelElement();
                SectionMetaPath metaPath = missingPathError.getMetaPath();
                int metaPathCount = metaPath.getSubMetaPathCount();
                if (metaPathCount == 2) {
                    AbstractMetaPath secondSubMetaPath = metaPath.getSubMetaPath(1);
                    if (secondSubMetaPath instanceof SimpleMetaPath) {
                        SimpleMetaPath missingMetaPath = (SimpleMetaPath) secondSubMetaPath;
                        List<ElementaryMetaPath> elementaryMetaPaths = missingMetaPath.getElementaryMetaPaths();
                        int createPathStartIndex = 0;
                        for (int i = 0; i < elementaryMetaPaths.size() - 1; i++) {
                            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(i);
                            ElementaryMetaPath nextElementaryMetaPath = elementaryMetaPaths.get(i + 1);
                            ElementaryMetaPath nextElementaryMetaPathOtherDirection = nextElementaryMetaPath.getOtherDirection();
                            if (elementaryMetaPath.equals(nextElementaryMetaPathOtherDirection)) {
                                createPathStartIndex = i + 1;
                            }
                        }
                        if (createPathStartIndex > 0) {
                            SimpleMetaPath subMetaPath = missingMetaPath.getSubPath(0, createPathStartIndex);
                            if (subMetaPath.isSingleConnection()) {
                                Collection<ModelElement> connected = MetaPathFunctions.getConnectedElements(missingPathStartElement, subMetaPath);
                                if (!connected.isEmpty()) {
                                    missingPathStartElement = connected.iterator().next();
                                    missingMetaPath = missingMetaPath.getSubPath(createPathStartIndex);
                                }
                            }
                        }
                        Collection<ModelElement> missingElements = missingPathError.getMissingElements();
                        Static.contextGenerator.addConnectMenuItems(menu, missingPathStartElement, missingMetaPath, missingElements);
                    }
                }
            }

        }
    }

}
