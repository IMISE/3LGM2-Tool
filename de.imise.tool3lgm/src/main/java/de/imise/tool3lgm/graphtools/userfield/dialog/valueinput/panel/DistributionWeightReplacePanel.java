package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.AbstractAction;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorDialog;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTable;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableController;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.layout.UserFieldTableLayout;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.AbstractUserFieldTableModel;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.UserFieldFormulaWeightReplacerTableModel;
import de.imise.util.NamedObjectContainer;
import de.imise.util.swing.component.AlphabeticalComboBox;

/**
 * Panel zur Darstellung und Eingabe der Ersetzung von Kantengweichten durch andere Kantengewichte.
 * <p>
 * Die Dateneingabe erfolgt für jede Klasse von ModelElementen und jede für sie definierte Kantenklasse separat - die Auswahl der ElementKlasse
 * erfolgt in der enthaltenen <code>elementClassBox</code>, die Auswahl der Kantenklasse in der <code>EdgeClassBox</code>.
 * 
 * @author AXS
 * @create 02.09.2015
 */
public class DistributionWeightReplacePanel extends AbstractUserFieldEditorPanel {

    /**
     * Auswahlbox für den Elementtyp
     */
    private final AlphabeticalComboBox elementClassBox = new AlphabeticalComboBox();

    /**
     * Auswahlbox für die Kantenklasse
     */
    private final AlphabeticalComboBox edgeClassBox = new AlphabeticalComboBox();

    /**
     * Richtung der Kante, die in der <code>edgeBox</code> ausgewählt ist.
     */
    private int choosedEdgeDirection;

    ////////////////////////////////////////////////////////////
    /// elementClassBoxSelection und egdeClassBoxSelection   ///
    ///	sind notwendig, damit beim temporären                ///
    /// takeover(), d.h., beim Ändern der Auswahl            ///
    /// in einer der beiden ComboBoxes die Werte             ///
    /// unter dem vorher ausgewählten Verteilungs-           ///
    /// gewicht abgespeichert werden und nicht               ///
    /// unter dem neu ausgewählten				             ///
    ////////////////////////////////////////////////////////////

    /**
     * Zuletzt ausgewähltes Element in der {@link #elementClassBox}
     */
    private Object elementClassBoxSelection;

    /**
     * Zuletzt ausgewähltes Element in der {@link #egdeClassBox}
     */
    private Object edgeClassBoxSelection;

    /**
     * Konstruktor
     * 
     * @param dialog Dialog, der dieses Panel enthält
     */
    public DistributionWeightReplacePanel(final UserFieldEditorDialog dialog) {
        super(dialog);
        initElementClassBox();
        initEdgeClassBox();
    }

    /**
     * Initialisierung von <code>edgeBox</code>
     */
    private void initElementClassBox() {

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;

        //        userFieldEditor_element_type                        Elementart
        //        userFieldEditor_edge_type                           Kantenart

        elementClassBox.addSeparator(Tool3lgmConstants.getResString("userFieldEditor_element_type"));
        setElementClassBoxContent();
        setActionsForElementClassBox();
        add(elementClassBox, constraints);
    }

    /**
     * Methode setzt den Inhalt der <code>elementClassBox</code> Es werden nur Elementklassen hinzugefügt, die Kanten mit mind.
     * einem definierten Verteilungsgewicht besitzen.
     */
    private void setElementClassBoxContent() {
        UserFieldDefinitions definitions = getUserFieldDefinitions();
        loop: for (int i = 0; i < ModelConstants.ALL_ELEMENTS.length; i++) {
            Class<? extends ModelElement> elementClass = ModelConstants.ALL_ELEMENTS[i];
            Class<? extends Kante>[] edgeTypes = ModelConstants.getEdgeTypes(elementClass);
            //mind. eine Kantenklase mit Kennzahlen oder Kennzahlformeln?
            for (int k = 0; k < edgeTypes.length; k++) {
                Class<? extends Kante> edgeClass = ModelConstants.ALL_EDGES[k];
                if (definitions.hasNumberFields(edgeClass)) {
                    elementClassBox.addItem(elementClass, ModelConstants.getDisplayableName(elementClass));
                    continue loop;
                }
            }
        }
    }

    /**
     * Methode setzt die auszuführende Action, bei Änderung der Auswahl in der <code>edgeBox</code>. Werte im Table werden temporär übernommen und die
     * zur gewählten Kantenart gehörigen Verteilungsgewichte in die <code>weightBox</code> eingefügt.
     */
    private void setActionsForElementClassBox() {

        final DistributionWeightReplacePanel finalPanel = this;

        //Bei Änderung der Auswahl in elementClassBox, werden in der edgeClassBox die verfügbaren 
        //Verteilungsgewichte angezeigt
        AbstractAction action = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Object o = elementClassBox.getSelectedObject();
                if (!(o instanceof Class)) {
                    return;
                }
                stopEditing();
                takeOver();
                // Selektion für nächstes takeOver
                finalPanel.elementClassBoxSelection = o;
                finalPanel.setEdgeClassBoxContent();
                finalPanel.drawTable();
                finalPanel.distributeSelectionChangedEvent();
            }
        };
        elementClassBox.setAction(action);
    }

    /**
     * Initialisiert die <code>weightBox</code>
     */
    private void initEdgeClassBox() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 0;
        edgeClassBox.addSeparator(Tool3lgmConstants.getResString("userFieldEditor_edge_type"));
        setActionsForEdgeClassBox();
        add(edgeClassBox, constraints);
    }

    /**
     * Methode setzt die auszuführende Aktion, bei Änderung der Auswahl in <code>edgeClassBox</code>.
     * Werte im Table werden temporär übernommen und die zur gewählten Kante gehörigen
     * Verteilungsgweichte mit den neuen Dten in einem neuen Table dargestellt.
     */
    private void setActionsForEdgeClassBox() {
        final DistributionWeightReplacePanel finalPanel = this;
        // Bei Änderung in der weightBox, wird der zum gewählten Verteilungsgewicht und 
        // Kantentyp gehörige Table im Panel dargestellt.
        ItemListener il = new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {

                Object o = edgeClassBox.getSelectedObject();
                if (o == null) {
                    return;
                }
                if (!Kante.class.isAssignableFrom(o.getClass())) {
                    return;
                }
                stopEditing();
                takeOver();

                // Selektion für nächstes takeOver
                finalPanel.edgeClassBoxSelection = o;

                finalPanel.drawTable();
                finalPanel.distributeSelectionChangedEvent();
            }

        };

        edgeClassBox.addItemListener(il);
    }

    /**
     * Setzt den Inhalt der <code>edgeClassBox</code>. Die für den in der <code>edgeBox</code> gewählten Kantentyp definierten Verteilungsgewichte
     * werden
     * der <code>weigthBox</code> hinzugefügt.
     */
    private void setEdgeClassBoxContent() {
        @SuppressWarnings("unchecked")
        Class<? extends ModelElement> elementClass = (Class<? extends ModelElement>) elementClassBox.getSelectedObject();
        UserFieldDefinitions definitions = getUserFieldDefinitions();
        edgeClassBox.removeAllItems();

        edgeClassBox.addSeparator(Tool3lgmConstants.getResString("userFieldEditor_edge_type"));

        Class<? extends Kante>[] edgeTypes = ModelConstants.getEdgeTypes(elementClass);
        //mind. eine Kantenklase mit Kennzahlen oder Kennzahlformeln?
        for (int k = 0; k < edgeTypes.length; k++) {
            Class<? extends Kante> edgeClass = ModelConstants.ALL_EDGES[k];
            if (definitions.hasNumberFields(edgeClass)) {
                elementClassBox.addItem(edgeClass, ModelConstants.getFullForwardMetaAssociationName(edgeClass));
                //                elementClassBox.addItem(edgeClass, ModelConstants.getFullBackwardMetaAssociationName(edgeClass));
            }
        }
    }

    @Override
    protected void takeOver() {
        if (!(table.getModel() instanceof AbstractUserFieldTableModel)) {
            return;
        }
        AbstractUserFieldTableModel tableModel = (AbstractUserFieldTableModel) table.getModel();
        if (tableModel.dataChanged() == false) {
            return;
        }
        GraphDocument doc = getDialog().getGraphDocument();
        if (elementClassBoxSelection != null) {
            Class<? extends Kante> selectedEdgeClass = (Class<? extends Kante>) edgeClassBoxSelection;
            Vector<Object> rowIdentifiers = tableModel.getRowIdentifiers();
            Vector<Object> columnIdentifiers = tableModel.getColumnIdentifiers();
            UserFieldDefinitions definitions = doc.getUserFieldDefinitions();
            for (int i = 0; i < rowIdentifiers.size(); i++) {
                // Das ModelElement in der i-ten Zeile
                ModelElement rowElement = ((NamedObjectContainer<ModelElement>) rowIdentifiers.elementAt(i)).getObject();
                String rowElementHash = rowElement.getHashString();
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

                    // Das UserField in der j-ten Spalte
                    UserField columnUserField = ((NamedObjectContainer<UserField>) columnIdentifiers.elementAt(j)).getObject();
                    String columnUserFieldHash = columnUserField.getHashCode();

                    if (newValue.equals(UserField.EMPTY_STRING)) {
                        // Neuen Wert setzen
                        int pid = getDialog().getTransactionID();
                        doc.setUserFieldWeightReplacement(rowElementHash, columnUserFieldHash, userFieldHashReplacement, pid);
                        doc.setUserFieldValue(edge.getHashString(), selectedWeigth.getHashCode(), newValue, getDialog().getTransactionID());
                    } else {
                        try {
                            Double.parseDouble(newValue);
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
    protected UserFieldTable initTable() {
        UserFieldTableLayout uftl = new UserFieldTableLayout();
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
    protected void drawTable() {
        table.removeFromLayoutContainer();
        if (edgeClassBox.getSelectedObject() == null) {
            return;
        }
        Class<? extends Kante> selectedEdgeClass = ((Class<?>) edgeBox.getSelectedObject()).asSubclass(Kante.class);
        String selectedEdgeName = edgeBox.getSelectedItem().toString();
        choosedEdgeDirection = Doppelkante.FORWARD;
        if (!selectedEdgeName.equals(ModelConstants.getFullForwardMetaAssociationName(selectedEdgeClass))) {
            choosedEdgeDirection = Doppelkante.BACKWARD;
        }
        UserField selectedWeigthUserField = (UserField) weightBox.getSelectedObject();
        AbstractUserFieldTableModel uftm = new UserFieldFormulaWeightReplacerTableModel(getDialog().getGraphDocument(), selectedEdgeClass, choosedEdgeDirection, selectedWeigthUserField);
        UserFieldTableController tec = UserFieldTableController.getNewDistributionWeightTableController(uftm);
        super.modifyTable(uftm, tec);
    }

}
