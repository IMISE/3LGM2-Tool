/*
 * Created on 14.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.FORWARD;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.util.Vector;

import javax.swing.AbstractAction;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitionsAnalyzer;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorDialog;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTable;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableController;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.layout.UserFieldTableLayout;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.AbstractUserFieldTableModel;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.UserFieldWeightTableModel;
import de.imise.util.NamedObjectContainer;
import de.imise.util.swing.component.AlphabeticalComboBox;

/**
 * Panel zur Darstellung und Eingabe von Verteilungsgewichten für Kanten.
 * <p>
 * Die Dateneingabe erfolgt für jede Klasse von Kantenelementen und jedes für
 * sie definierte Verteilungsgewicht separat - die Auswahl der Klasse erfolgt in
 * der enthaltenen <code>edgeBox</code>, die Auswahl des Verteilungsgewichtes in
 * der <code>weightBox</code>.
 *
 * @author fstephan
 */
public class DistributionWeightEditorPanel extends AbstractElementTypeUserFieldEditorPanel {

    /**
     * Auswahlbox für die Verteilungsgewichte des in <code>edgeBox</code>
     * ausgewählten Kantentyps
     */
    private final AlphabeticalComboBox<UserField> weightBox = new AlphabeticalComboBox<>();

    /**
     * Auswahlbox über die man die Tabelle auf genau ein Spaltenelement
     * einschränken kann
     */
    private final AlphabeticalComboBox<Object> columnFilterBox = new AlphabeticalComboBox<>();

    /**
     * Richtung der Edge, die in der <code>edgeBox</code> ausgewählt ist.
     */
    private Direction choosedEdgeDirection;

    //////////////////////////////////////////////////
    /// elementTypeBoxSelection und weightBoxSelection   ///
    ///	sind notwendig, damit beim temporären      ///
    /// takeover(), d.h., beim Ändern der Auswahl  ///
    /// in einer der beiden ComboBoxes die Werte   ///
    /// unter dem vorher ausgewählten Verteilungs- ///
    /// gewicht abgespeichert werden und nicht     ///
    /// unter dem neu ausgewählten				   ///
    //////////////////////////////////////////////////

    /**
     * Zuletzt ausgewähltes Element in der {@link #edgeBox}
     */
    private Class<? extends Edge> elementTypeBoxSelection;

    /**
     * Zuletzt ausgewähltes Element in der {@link #weightBox}
     */
    private UserField weightBoxSelection;

    /**
     * Zuletzt ausgewähltes Element in der {@link #columnFilterBox}
     */
    private ModelElement columnFilterBoxSelection;

    /**
     * Konstruktor
     *
     * @param dialog Dialog, der dieses Panel enthält
     * @param name
     */
    public DistributionWeightEditorPanel(final UserFieldEditorDialog dialog, final String name) {
        super(dialog, Edge.class, UserField.Style.NUMBER_STYLES, name);
        initWeightBox();
        initColumnFilterBox();
    }

    /**
     * Methode setzt die auszuführende Action, bei Änderung der Auswahl in der
     * <code>edgeBox</code>. Werte im Table werden temporär übernommen und die
     * zur gewählten Kantenart gehörigen Verteilungsgewichte in die
     * <code>weightBox</code> eingefügt.
     */
    @Override
    protected void setActionsForElementTypeBox() {
        super.setActionsForElementTypeBox();
        final DistributionWeightEditorPanel finalPanel = this;

        /*
         * Bei Änderung der Auswahl in edgeBox, werden in der weightBox die
         * verfügbaren Verteilungsgewichte angezeigt
         */
        AbstractAction action = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                Object o = elementTypeBox.getSelectedObject();
                if (!(o instanceof Class)) {
                    return;
                }
                stopEditing();
                takeOver();
                // Selektion für nächstes takeOver
                finalPanel.elementTypeBoxSelection = ((Class<?>) o).asSubclass(Edge.class);
                //sobald sich in dieser Box die Selektion ändert, dann gleich in der anderen Box das erste Item auswählen
                finalPanel.setWeightBoxContent();
                finalPanel.initSelectFirstItem();
                finalPanel.columnFilterBoxSelection = null;
                finalPanel.drawTable();
                finalPanel.distributeSelectionChangedEvent();
            }
        };
        elementTypeBox.setAction(action);
    }

    /**
     * Initialisiert die <code>weightBox</code>
     */
    private void initWeightBox() {
        weightBox.setEnabled(false);
        setActionsForWeightBox();
        addComponent(weightBox);
    }

    /**
     * Methode setzt die auszuführende Aktion, bei Änderung der Auswahl in
     * <code>weightBox</code>. Werte im Table werden temporär übernommen und die
     * zum gewählten Verteilungsgewicht gehörigen Daten in einem neuen Table
     * dargestellt.
     */
    private void setActionsForWeightBox() {
        final DistributionWeightEditorPanel finalPanel = this;
        // Bei Änderung in der weightBox, wird der zum gewählten Verteilungsgewicht und
        // Kantentyp gehörige Table im Panel dargestellt.
        ItemListener il = e -> {

            Object o = weightBox.getSelectedObject();
            if (o == null) {
                return;
            }
            if (!o.getClass().isAssignableFrom(UserField.class) || !(elementTypeBox.getSelectedObject() instanceof Class)) {
                return;
            }
            stopEditing();
            takeOver();

            // Selektion für nächstes takeOver
            finalPanel.weightBoxSelection = (UserField) o;
            finalPanel.columnFilterBoxSelection = null;
            finalPanel.drawTable();
            finalPanel.distributeSelectionChangedEvent();
            finalPanel.setColumnFilterBoxContent();
        };

        weightBox.addItemListener(il);
    }

    /**
     * Setzt den Inhalt der <code>weigthBox</code>. Die für den in der
     * <code>edgeBox</code> gewählten Kantentyp definierten Verteilungsgewichte
     * werden der <code>weigthBox</code> hinzugefügt.
     */
    private void setWeightBoxContent() {

        UserFieldDefinitions definitions = getUserFieldDefinitions();

        weightBox.removeAllItems();
        weightBox.addSeparator(getResString("weighting"));

        for (UserField uf : definitions.getUserFields(elementTypeBoxSelection)) {
            if (uf.isNumberUserField()) {
                weightBox.addObject(uf);
            }
        }
        weightBox.setEnabled(true);
        columnFilterBox.removeAllItems();
        columnFilterBox.setEnabled(false);
    }

    private void initColumnFilterBox() {
        columnFilterBox.setEnabled(false);
        setActionsForColumnFilterBox();
        addComponent(columnFilterBox);
    }

    private void setActionsForColumnFilterBox() {
        final DistributionWeightEditorPanel finalPanel = this;
        // Bei Änderung in der weightBox, wird der zum gewählten Verteilungsgewicht und
        // Kantentyp gehörige Table im Panel dargestellt.
        ItemListener il = e -> {
            Object o = columnFilterBox.getSelectedObject();
            if (o != null && !(o instanceof ModelElement) || !(weightBox.getSelectedObject() instanceof UserField) || !(elementTypeBox.getSelectedObject() instanceof Class)) {
                return;
            }
            stopEditing();
            takeOver();

            // Selektion für nächstes takeOver
            finalPanel.columnFilterBoxSelection = (ModelElement) o;
            finalPanel.drawTable();
            finalPanel.distributeSelectionChangedEvent();
        };
        columnFilterBox.addItemListener(il);
    }

    private void setColumnFilterBoxContent() {
        Vector<?> columnIdentifiers = table.getColumnIdentifiers();
        columnFilterBox.removeAllItems();
        columnFilterBox.addSeparator(getResString("filter_column"));
        columnFilterBox.addObject(null, getResString("show_all_columns"));
        columnFilterBox.setSelectedIndex(1);
        columnFilterBox.addSeparator(false);
        if (columnIdentifiers != null) {
            for (Object columnIdentifier : columnIdentifiers) {
                columnFilterBox.addObject(columnIdentifier);
            }
        }
        columnFilterBox.setEnabled(true);
    }

    @Override
    public void takeOver() {
        if (!(table.getModel() instanceof AbstractUserFieldTableModel)) {
            return;
        }
        AbstractUserFieldTableModel tableModel = (AbstractUserFieldTableModel) table.getModel();
        if (tableModel.dataChanged() == false) {
            return;
        }
        GraphDocument doc = dialog.getMainDoc();
        if (elementTypeBoxSelection != null) {
            UserField selectedWeigth = weightBoxSelection;
            Vector<NamedObjectContainer<?>> rowIdentifiers = tableModel.getRowIdentifiers();
            Vector<NamedObjectContainer<?>> columnIdentifiers = tableModel.getColumnIdentifiers();

            for (int i = 0; i < rowIdentifiers.size(); i++) {
                // Das ModelElement in der i-ten Zeile
                ModelElement rowElement = (ModelElement) rowIdentifiers.elementAt(i).getObject();

                for (int j = 0; j < columnIdentifiers.size(); j++) {
                    String newValue = null;
                    NamedObjectContainer<?> noc = (NamedObjectContainer<?>) tableModel.getValueAt(i, j);
                    if (noc == null) {
                        continue;
                    }
                    // Wert an der Stelle (i,j)
                    newValue = noc.toString();
                    if (newValue == null) {
                        continue;
                    }
                    // Das ModelElement in der j-ten Spalte
                    ModelElement columnElement = (ModelElement) columnIdentifiers.elementAt(j).getObject();

                    Edge edge = getEdge(rowElement, columnElement);
                    if (edge == null) {
                        continue;
                    }
                    int pid = dialog.getTransactionID();
                    if (newValue.equals(UserField.EMPTY_STRING)) {
                        // Neuen Wert setzen
                        doc.setUserFieldValue(edge.getID(), selectedWeigth.getID(), newValue, pid);
                    } else {
                        try {
                            //Warum soll man hier keine falschen Werte setzen dürfen, die dann bei Berechnungen zu NUMBER_FORMAT_ERRORS werden
                            new BigDecimal(newValue);
                            // Neuen Wert setzen
                            doc.setUserFieldValue(edge.getID(), selectedWeigth.getID(), newValue, pid);
                        } catch (NumberFormatException nfe) {
                            continue;
                        }
                    }
                }
            }
        }

        tableModel.dataChanged(false);

        //Das reset durchführen kann auch ganz auf das Ende verlegt werden
        doc.getUserFieldDefinitions().initReset();
    }

    @Override
    protected UserFieldTableController getTableController(final AbstractUserFieldTableModel uftm) {
        return UserFieldTableController.getNewDistributionWeightTableController(uftm);
    }

    private Edge getEdge(final ModelElement rowElement, final ModelElement columnElement) {
        Edge edge = null;
        // Richtung der Edge und die Edge selbst ermitteln
        if (choosedEdgeDirection == FORWARD) {
            edge = columnElement.getEdgeTo(rowElement, elementTypeBoxSelection);

            if (edge == null) {
                edge = rowElement.getEdgeTo(columnElement, elementTypeBoxSelection);
            }
        }
        if (edge == null && choosedEdgeDirection == BACKWARD) {
            edge = rowElement.getEdgeTo(columnElement, elementTypeBoxSelection);

            if (edge == null) {
                edge = columnElement.getEdgeTo(rowElement, elementTypeBoxSelection);
            }
        }
        return edge;
    }

    @Override
    protected UserFieldTable initTable() {
        UserFieldTableLayout uftl = new UserFieldTableLayout(true);
        UserFieldTable table = new UserFieldTable(uftl);
        return table;
    }

    @Override
    protected Object constraintsForTable() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        return constraints;
    }

    @Override
    protected boolean hasSelectedItem() {
        boolean hasSelectedItem = weightBox.getSelectedObject() != null;
        return hasSelectedItem;
    }

    @Override
    protected void initSelectFirstItem() {
        super.initSelectFirstItem();
        //das hier darf man nicht durch hasSelectedItem() ersetzen, falls eine Unterklasse diese
        //Funktion überschreibt
        if (weightBox.getSelectedObject() == null) {
            //auch in der WeightBox steh an Index 0 immer ein Separator
            if (weightBox.getItemCount() > 1) {
                weightBox.setSelectedIndex(1);
            }
        }
    }

    @Override
    protected void drawTable() {
        table.removeFromLayoutContainer();
        if (!hasSelectedItem()) {
            return;
        }
        Class<? extends Edge> selectedEdgeClass = ((Class<?>) elementTypeBox.getSelectedObject()).asSubclass(Edge.class);
        String selectedEdgeName = elementTypeBox.getSelectedString();
        ElementsNameBuilder elementsNameBuilder = dialog.getElementsNameBuilder();
        choosedEdgeDirection = selectedEdgeName.equals(elementsNameBuilder.getFullForwardMetaAssociationName(selectedEdgeClass)) ? FORWARD : BACKWARD;
        UserField selectedWeigthUserField = weightBox.getSelectedObject();
        GraphDocument doc = dialog.getMainDoc();
        AbstractUserFieldTableModel uftm = new UserFieldWeightTableModel(doc, selectedEdgeClass, choosedEdgeDirection, selectedWeigthUserField, columnFilterBoxSelection);
        UserFieldTableController tec = UserFieldTableController.getNewDistributionWeightTableController(uftm);
        super.modifyTable(uftm, tec);
    }

    @Override
    public boolean hasValues() {
        UserFieldDefinitions definitions = dialog.getUserFieldDefinitions();
        UserFieldDefinitionsAnalyzer analyzer = definitions.getAnalyzer();
        boolean hasValues = analyzer.hasDistributionWeights();
        return hasValues;
    }
}
