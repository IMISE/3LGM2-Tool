package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ConnectPathDialog;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.UnionMetaPath;
import de.imise.util.NamedObjectContainer;

/**
 * @author AXS (11 Mar 2019)
 */
public class ConnectedElementsTablePanel extends AbstractPathConnectionPanel {

    /** Die MetaPfade zu anderen Elementen in einem UnionMetaPath */
    protected final UnionMetaPath metaPaths;

    /** Die eigentliche Tabelle */
    private final ConnectedElementsTable table;

    /** Definition der Spalten der Tabelle */
    protected final ConnectedElementsTableColumnsDefinition columnsDefinition;

    /** Panel für Buttons Hinzufügen + Entfernen */
    private JPanel buttonpanel;

    /**
     * @param dialog
     * @param columnsDefinition
     * @param simpleMetaPaths
     */
    public ConnectedElementsTablePanel(final ElementPropertyDialog dialog, final boolean editable, final ConnectedElementsTableColumnsDefinition columnsDefinition, final SimpleMetaPath... simpleMetaPaths) {
        super(dialog, simpleMetaPaths[0]); // den muss es geben!
        metaPaths = new UnionMetaPath(simpleMetaPaths);
        this.columnsDefinition = columnsDefinition;
        table = new ConnectedElementsTable(dialog.getModelElement(), metaPaths, columnsDefinition, editable, mouseListener, dialog.getTransactionID());
        internalInit(editable);
    }

    private void internalInit(final boolean editable) {
        JScrollPane scrollPane = new JScrollPane(table);

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        JButton addButton = new JButton(getCreateNewElementAction());
        JButton removeButton = new JButton(getDisconnectAction());

        if (editable) {
            constraints.anchor = GridBagConstraints.CENTER;
            buttonpanel = new JPanel();
            buttonpanel.setLayout(new GridLayout(1, 2));
            buttonpanel.add(removeButton);
            buttonpanel.add(addButton);
            add(this, buttonpanel, constraints, 0, 2, 3, 1);
        }

        // add(this, viewButton, constraints, 2, 3, 1, 1);
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.anchor = GridBagConstraints.WEST;
        //        add(this, null, constraints, 0, 0, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, scrollPane, constraints, 0, 1, 3, 1);

        update();
    }

    /**
    *
    */
    public final LGMAction getCreateNewElementAction() {
        final Component dialogParent = this;
        return new LGMAction(getResString("addButtonText")) {
            @Override
            public void execute(final EventObject eo) {
                ConnectPathDialog connectPathDialog = new ConnectPathDialog(doc, metaPaths);
                boolean ok = connectPathDialog.createDialog(dialogParent);
                while (ok && !connectPathDialog.hasValidSelection()) {
                    ok = connectPathDialog.createDialog(dialogParent);
                }
                if (ok) {
                    SimpleMetaPath selectedPath = connectPathDialog.getSelectedPath();
                    ModelElement selectedEndElement = connectPathDialog.getSelectedEndElement();
                    doc.createPath(getModelElement(), selectedEndElement, selectedPath, true, dialog.getTransactionID());
                }
            }
        };
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben von Elementen aus dem
     * <code>srcTree</code> in den <code>targetTree</code> realisiert. Diese <code>LGMAction</code>
     * sollte an die "removeButtons" der Panels angefügt werden.
     */
    public final LGMAction getDisconnectAction() {
        LGMAction returnAction = new LGMAction("", Tool3lgmConstants.getIcon("arrow_right2.gif")) {
            @Override
            public void execute(final EventObject e) {
                //aus MultipleCompositionPanel
                //               TreePath[] selpaths = tree.getSelectionPaths();
                //               if (selpaths != null) {
                //                   for (int n = 0; n < selpaths.length; n++) {
                //                       // if(lomodel.getChildCount(loroot)>0) return;
                //                       LGMTreeNode node = (LGMTreeNode) selpaths[n].getLastPathComponent();
                //                       ElementContainer knot = (ElementContainer) node.getUserObject();
                //
                //                       ModelElement topLevelModelElement;
                //                       topLevelModelElement = getModelElement();
                //                       GDCollection gdcoll = getGraphDocument().getCollection();
                //                       gdcoll.unlink(topLevelModelElement, knot.getElement(), getLastEdgeClassInPath(), getTransactionID());
                //                   }
                //               }
            }
        };
        returnAction.putValue("Name", getResString("delete"));
        returnAction.putValue("SmallIcon", null);

        return returnAction;
    }

    private final boolean isRowSelected(final int row) {
        int[] selectedRows = table.getSelectedRows();
        for (int r : selectedRows) {
            if (r == row) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Object getSelection(final MouseEvent e) {
        Point clickedPoint = e.getPoint();
        JComponent source = (JComponent) e.getSource();
        //wenn die Source nicht der Table selbst sondern eine darin enthaltene Editor-Komponente ist -> relative Koordinaten des Editors im Table bestimmen
        if (source != table) {
            Point location = source.getLocation();
            clickedPoint.translate(location.x, location.y);
        }
        int clickedRow = table.rowAtPoint(clickedPoint);
        if (!isRowSelected(clickedRow)) {//das kann eintreten, wenn mit Rechts auf eine bisher nicht selektierte Zeile geklickt wurde
            table.addRowSelectionInterval(clickedRow, clickedRow);
        }
        int clickedColumn = table.columnAtPoint(clickedPoint);
        int[] selectedRows = table.getSelectedRows();
        List<ModelElement> selectedElements = new ArrayList<>();
        for (int row : selectedRows) {
            ModelElement selectedElement = getModelElementAt(row, clickedColumn);
            if (selectedElement != null && !ModelConstants.isHiddenClass(selectedElement.getClass())) {
                selectedElements.add(selectedElement);
            }
        }
        return selectedElements;
    }

    /**
     * @param row
     * @param col
     * @return
     */
    private ModelElement getModelElementAt(final int row, final int col) {
        Object selectedRowValue = table.getValueAt(row, col);
        if (selectedRowValue instanceof NamedObjectContainer) {
            NamedObjectContainer<?> noc = (NamedObjectContainer) selectedRowValue;
            selectedRowValue = noc.getObject();
        }
        return selectedRowValue instanceof ModelElement ? (ModelElement) selectedRowValue : null;
    }

    @Override
    public void update() {
        table.update();
    }

}
