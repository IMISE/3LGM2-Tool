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
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.WeightReplacer;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorDialog;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTable;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableController;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.layout.AbstractUserFieldTableLayout;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.layout.WeightReplaceTableLayout;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.AbstractTableModel;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.WeightReplaceTableModel;
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
     * @param name
     */
    public DistributionWeightReplacePanel(final UserFieldEditorDialog dialog, final String name) {
        super(dialog, name);
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
                Class<? extends Kante> edgeClass = edgeTypes[k];
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
                if (!(o instanceof Class) || !Kante.class.isAssignableFrom((Class<?>) o)) {
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
            Class<? extends Kante> edgeClass = edgeTypes[k];
            if (definitions.hasNumberFields(edgeClass)) {
                edgeClassBox.addItem(edgeClass, ModelConstants.getFullForwardMetaAssociationName(edgeClass));
                //                edgeClassBox.addItem(edgeClass, ModelConstants.getFullBackwardMetaAssociationName(edgeClass));
            }
        }
    }

    @Override
    public void takeOver() {
        if (!(table.getModel() instanceof WeightReplaceTableModel)) {
            return;
        }
        WeightReplaceTableModel tableModel = (WeightReplaceTableModel) table.getModel();
        if (!tableModel.dataChanged()) {
            return;
        }
        GraphDocument doc = getDialog().getGraphDocument();
        if (edgeClassBoxSelection != null) {
            Vector<NamedObjectContainer<?>> rowIdentifiers = tableModel.getRowIdentifiers();
            Vector<NamedObjectContainer<?>> columnIdentifiers = tableModel.getColumnIdentifiers();
            for (int i = 0; i < rowIdentifiers.size(); i++) {
                // Das ModelElement in der i-ten Zeile
                ModelElement rowElement = (ModelElement) rowIdentifiers.elementAt(i).getObject();
                String rowElementHash = rowElement.getHashString();
                for (int j = 0; j < columnIdentifiers.size(); j++) {
                    NamedObjectContainer<UserField> noc = (NamedObjectContainer<UserField>) tableModel.getValueAt(i, j);
                    UserField replaceUserField = noc.getObject();
                    // Das UserField in der j-ten Spalte
                    UserField columnUserField = (UserField) columnIdentifiers.elementAt(j).getObject();
                    setReplacement(doc, rowElementHash, columnUserField, replaceUserField);
                }
            }
        }

        tableModel.dataChanged(false);

        //Das reset durchführen kann auch ganz auf das Ende verlegt werden
        doc.getUserFieldDefinitions().initReset();
    }

    /**
     * Setzt über das GraphDocument im Replacer ein neues Replacement
     * 
     * @param doc
     * @param rowElementHash
     * @param columnUserField
     * @param replaceUserField
     */
    private void setReplacement(final GraphDocument doc, final String rowElementHash, final UserField columnUserField, final UserField replaceUserField) {
        int pid = getDialog().getTransactionID();
        WeightReplacer replacer = doc.getCollection().getUserFieldDefinitions().getWeightReplacer();
        //wenn es NICHT das Gleichverteilungs-UserField ist, das ersetzt werden soll
        if (columnUserField != null) {
            String columnUserFieldHash = columnUserField.getHashCode();
            if (replaceUserField == null) { // es soll durch die Gleichverteilung ersetzt werden
                doc.setUserFieldWeightReplacement(rowElementHash, columnUserFieldHash, null, pid);
            } else { //es soll durch ein anderes UserField ersetzt bzw. gelöscht werden 
                String userFieldHashReplacement = replaceUserField.getHashCode();
                String oldReplacement = replacer.getReplacement(rowElementHash, columnUserFieldHash);
                //wenn tatsächlich einer Wert übergeben wurde
                //FAll 1: Es ist kein alter Wert gesetzt und es soll wieder der Leerwert gesetzt werden
                boolean setOldValue1 = oldReplacement == null && columnUserFieldHash.equals(userFieldHashReplacement);
                //Fall 2: es ist ein alter Wert gesetzt und es soll derselbe Wert nochmal gesetzt werden
                boolean setOldvalue2 = oldReplacement != null && oldReplacement.equals(userFieldHashReplacement);
                if (!setOldValue1 && !setOldvalue2) {
                    //wenn columnUserFieldHash == userFieldHashReplacement sein sollte, dann wird die Ersetzung gelöscht
                    //wenn die ungleich sind, wird die Ersetzung gesetzt
                    doc.setUserFieldWeightReplacement(rowElementHash, columnUserFieldHash, userFieldHashReplacement, pid);
                }
            }
        } else { //das Gleichverteilungs-UserField soll ersetzt werden
            Class<? extends Kante> selectedEdgeClass = (Class<? extends Kante>) edgeClassBoxSelection;
            String selectedEdgeClassName = selectedEdgeClass.getSimpleName();
            String replaceUserFieldHash = replaceUserField == null ? null : replaceUserField.getHashCode();
            String oldReplacement = replacer.getUniformDistributionReplacement(rowElementHash, selectedEdgeClass);
            //wenn tatsächlich einer Wert übergeben wurde
            //FAll 1: Es ist kein alter Wert gesetzt und es soll wieder der Leerwert gesetzt werden (beide null)
            boolean setOldValue1 = oldReplacement == replaceUserFieldHash;
            //Fall 2: es ist ein alter Wert gesetzt und es soll derselbe Wert nochmal gesetzt werden
            boolean setOldValue2 = oldReplacement != null && oldReplacement.equals(replaceUserFieldHash);
            if (!setOldValue1 && !setOldValue2) {
                //wenn replaceUserFieldHash == null sein sollte wird eine vorhandene Ersetzung gelöscht
                //wenn es nicht null ist, dann wird der neue Ersetzungshash gesetzt
                doc.setUserFieldWeightReplacement(rowElementHash, selectedEdgeClassName, replaceUserFieldHash, pid);
            }
        }
    }

    @Override
    protected UserFieldTable initTable() {
        AbstractUserFieldTableLayout uftl = new WeightReplaceTableLayout((Class<? extends Kante>) edgeClassBoxSelection);
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
        //keine Kantenklase ausgewählt -> nichts zu ersetzen
        if (edgeClassBox.getSelectedObject() == null) {
            return;
        }
        //selektierte Element- und Kantenklasse holen 
        Class<? extends ModelElement> elementClass = ((Class<?>) elementClassBox.getSelectedObject()).asSubclass(ModelElement.class);
        Class<? extends Kante> edgeClass = ((Class<?>) edgeClassBox.getSelectedObject()).asSubclass(Kante.class);
        //das Model damit initialisieren
        AbstractTableModel uftm = new WeightReplaceTableModel(getDialog().getGraphDocument(), elementClass, edgeClass);
        UserFieldTableController tec = UserFieldTableController.getNewDistributionWeightReplaceTableController(uftm);
        super.modifyTable(uftm, tec);
    }

}
