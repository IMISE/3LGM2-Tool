/*
 * Created on 14.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel;

import static de.imise.tool3lgm.graphtools.elements.Kante.BACKWARD;
import static de.imise.tool3lgm.graphtools.elements.Kante.FORWARD;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.util.Vector;

import javax.swing.AbstractAction;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
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
 * Die Dateneingabe erfolgt für jede Klasse von Kantenelementen und jedes für sie definierte Verteilungsgewicht separat - die Auswahl der Klasse
 * erfolgt in der enthaltenen <code>edgeBox</code>, die Auswahl des Verteilungsgewichtes in der <code>weightBox</code>.
 *
 * @author fstephan
 */
public class DistributionWeightEditorPanel extends AbstractElementTypeUserFieldEditorPanel {

    /**
     * Auswahlbox für die Verteilungsgewichte des in <code>edgeBox</code> ausgewählten Kantentyps
     */
    private final AlphabeticalComboBox weightBox = new AlphabeticalComboBox();

    /**
     * Auswahlbox über die man die Tabelle auf genau ein Spaltenelement einschränken kann
     */
    private final AlphabeticalComboBox columnFilterBox = new AlphabeticalComboBox();

    /**
     * Richtung der Kante, die in der <code>edgeBox</code> ausgewählt ist.
     */
    private int choosedEdgeDirection;

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
    private Class<? extends Kante> elementTypeBoxSelection;

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
        super(dialog, Kante.class, UserField.Style.CLASSIFICATION_NUMBER_STYLES, name);
        initWeightBox();
        initColumnFilterBox();
    }

    /**
     * Methode setzt die auszuführende Action, bei Änderung der Auswahl in der <code>edgeBox</code>. Werte im Table werden temporär übernommen und die
     * zur gewählten Kantenart gehörigen Verteilungsgewichte in die <code>weightBox</code> eingefügt.
     */
    @Override
    protected void setActionsForElementTypeBox() {
        super.setActionsForElementTypeBox();
        final DistributionWeightEditorPanel finalPanel = this;

        /*
         * Bei Änderung der Auswahl in edgeBox, werden in der weightBox die verfügbaren Verteilungsgewichte angezeigt
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
                finalPanel.elementTypeBoxSelection = ((Class<?>) o).asSubclass(Kante.class);
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
     * Methode setzt die auszuführende Aktion, bei Änderung der Auswahl in <code>weightBox</code>. Werte im Table werden temporär übernommen und die
     * zum gewählten Verteilungsgewicht gehörigen Daten in einem neuen Table dargestellt.
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
     * Setzt den Inhalt der <code>weigthBox</code>. Die für den in der <code>edgeBox</code> gewählten Kantentyp definierten Verteilungsgewichte werden
     * der <code>weigthBox</code> hinzugefügt.
     */
    private void setWeightBoxContent() {

        UserFieldDefinitions definitions = getUserFieldDefinitions();

        weightBox.removeAllItems();
        weightBox.addSeparator(Tool3lgmConstants.getResString("weighting"));

        for (UserField uf : definitions.getUserFields(elementTypeBoxSelection)) {
            if (uf.isClassificationUserField()) {
                weightBox.addItem(uf);
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
        columnFilterBox.addSeparator(Tool3lgmConstants.getResString("filter_column"));
        columnFilterBox.addItem(new NamedObjectContainer<ModelElement>(null, Tool3lgmConstants.getResString("show_all_columns")));
        columnFilterBox.setSelectedIndex(1);
        columnFilterBox.addSeparator(false);
        if (columnIdentifiers != null) {
            for (Object columnIdentifier : columnIdentifiers) {
                columnFilterBox.addItem(columnIdentifier);
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
        GraphDocument doc = getDialog().getGraphDocument();
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

                    Kante edge = getEdge(rowElement, columnElement);
                    if (edge == null) {
                        continue;
                    }

                    if (newValue.equals(UserField.EMPTY_STRING)) {
                        // Neuen Wert setzen
                        doc.setUserFieldValue(edge.getHashString(), selectedWeigth.getHashCode(), newValue, getDialog().getTransactionID());
                    } else {
                        try {
                            //Warum soll man hier keine falschen Werte setzen dürfen, die dann bei Berechnungen zu NUMBER_FORMAT_ERRORS werden
                            new BigDecimal(newValue);
                            // Neuen Wert setzen
                            doc.setUserFieldValue(edge.getHashString(), selectedWeigth.getHashCode(), newValue, getDialog().getTransactionID());
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

    private Kante getEdge(final ModelElement rowElement, final ModelElement columnElement) {
        Kante edge = null;
        // Richtung der Kante und die Kante selbst ermitteln
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
        Class<? extends Kante> selectedEdgeClass = ((Class<?>) elementTypeBox.getSelectedObject()).asSubclass(Kante.class);
        String selectedEdgeName = elementTypeBox.getSelectedItem().toString();
        choosedEdgeDirection = FORWARD;
        if (!selectedEdgeName.equals(ModelConstants.getFullForwardMetaAssociationName(selectedEdgeClass))) {
            choosedEdgeDirection = BACKWARD;
        }
        UserField selectedWeigthUserField = (UserField) weightBox.getSelectedObject();
        AbstractUserFieldTableModel uftm = new UserFieldWeightTableModel(getDialog().getGraphDocument(), selectedEdgeClass, choosedEdgeDirection, selectedWeigthUserField, columnFilterBoxSelection);
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
