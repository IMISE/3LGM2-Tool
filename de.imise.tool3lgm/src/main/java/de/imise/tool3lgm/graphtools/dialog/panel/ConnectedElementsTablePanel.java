package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ConnectPathDialog;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.UnionMetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.NamedObjectContainer;
import de.imise.util.collections.CollectionUtils;

/**
 * Dieses Panel stellt einen oder mehrere Pfade ausgehend vom ModelElement des zugehörigen {@link ElementPropertyDialog} in einer Tabelle dar.
 *
 * @author AXS (11 Mar 2019)
 */
public class ConnectedElementsTablePanel extends AbstractPathConnectionPanel {

    /** Die MetaPfade zu anderen Elementen in einem UnionMetaPath */
    protected final UnionMetaPath metaPaths;

    /** Die eigentliche Tabelle */
    private final ConnectedElementsTable table;

    /** Definition der Spalten der Tabelle */
    protected final ConnectedElementsTableDefinition tableDefinition;

    /** Panel für Buttons Hinzufügen + Entfernen */
    private JPanel buttonpanel;

    /**
     * @param dialog
     * @param editable wenn <code>true</code>, dann kann man Elemente hinzufügen oder löschen und vorhandene ändern
     * @param tableDefinition Spaltendefinition (die zu den Pfaden passen sollte)
     * @param simpleMetaPath MetaPfade, der in der Tabelle dargestellt werden soll. Davon werden alle konkreten MetaPfade gebildet, wenn er abstrakte
     *            Kantenklassen enthält
     */
    public ConnectedElementsTablePanel(final ElementPropertyDialog dialog, final boolean editable, @Nonnull final ConnectedElementsTableDefinition tableDefinition, final Collection<SimpleMetaPath> simpleMetaPaths) {
        this(dialog, editable, tableDefinition, toArray(simpleMetaPaths));
    }

    /**
     * @param dialog
     * @param editable wenn <code>true</code>, dann kann man Elemente hinzufügen oder löschen und vorhandene ändern
     * @param tableDefinition Spaltendefinition (die zu den Pfaden passen sollte)
     * @param simpleMetaPaths MetaPfade, die in der Tabelle dargestellt werden sollen
     */
    private ConnectedElementsTablePanel(final ElementPropertyDialog dialog, final boolean editable, @Nonnull final ConnectedElementsTableDefinition tableDefinition, final SimpleMetaPath... simpleMetaPaths) {
        super(dialog, simpleMetaPaths[0]); // den muss es geben!
        metaPaths = new UnionMetaPath(simpleMetaPaths);
        this.tableDefinition = tableDefinition;
        table = new ConnectedElementsTable(dialog.getModelElement(), metaPaths, tableDefinition, editable, mouseListener, dialog.getTransactionID());

        //wenn in der columnsDefinion ein String als Resourcenschlüssel oder Tabellenname angegeben wurde, dann kommt hier irgendwas nicht leeres zurück
        String tableTabName = Tool3lgmConstants.getResStringWithoutError(tableDefinition.getTableResKeyOrName());
        if (!Strings.isNullOrEmpty(tableTabName)) { //Name dieses Panels und somit des Tabs ggf. ersetzen (super setzt den Namen des Endelementes der Pfade)
            setName(tableTabName);
        }

        internalInit(editable);
    }

    private static SimpleMetaPath[] toArray(final Collection<SimpleMetaPath> simpleMetaPaths) {
        SimpleMetaPath[] simpleMetaPathsArray = new SimpleMetaPath[simpleMetaPaths.size()];
        return simpleMetaPaths.toArray(simpleMetaPathsArray);
    }

    /**
     * @param editable wenn <code>true</code>, dann werden die Buttons zum hinzufügen oder Löschen angezeigt
     */
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

        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, scrollPane, constraints, 0, 1, 3, 1);

        update();
    }

    /**
     * Action des Hinzufügen-Buttons
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
     * Action für den Löschen Button. Es wird die letzte Kante des Pfades einer Zeile gelöscht. Elemente, die nur mit dieser Kante existieren können,
     * werden ebenfalls gelöscht und deren Kanten usw.
     */
    public final LGMAction getDisconnectAction() {
        final ConnectedElementsTable table = this.table;
        return new LGMAction(getResString("delete")) {
            @Override
            public void execute(final EventObject e) {
                List<Edge> selectedPathLastEdges = table.getSelectedPathsLastEdges();
                doc.getCollection().deleteElements(selectedPathLastEdges, doc, dialog.getTransactionID());
            }
        };
    }

    /**
     * Liefert <code>true</code>, wenn die Zeile mit dem übergebenen Index selektiert ist
     *
     * @param row
     * @return
     */
    private final boolean isRowSelected(final int row) {
        return CollectionUtils.arrayContains(table.getSelectedRows(), row);
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
     * Wenn in der Zelle mit dem Row- und Column-Index ein ModelElement, ein ElementContainer oder ein {@link NamedObjectContainer} mit einem
     * ModelElement oder ElementContainer steckt, dann wird dieses ModelElenent bzw. das ModelElement des ElementContainers zurück gegeben.
     * Ist es das alles nicht, kommt <code>null</code> zurück.
     *
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
        return selectedRowValue instanceof ModelElement ? (ModelElement) selectedRowValue : selectedRowValue instanceof ElementContainer ? ((ElementContainer) selectedRowValue).getElement() : null;
    }

    @Override
    public void update() {
        table.update();
    }

}
