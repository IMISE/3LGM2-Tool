/*
 * Created on 14.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput;

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
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTable;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableController;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableLayout;
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
public class DistributionWeightEditorPanel extends AbstractUserFieldEditorPanel {

    /**
     * Auswahlbox für den Kantentyp
     */
    private final AlphabeticalComboBox edgeBox = new AlphabeticalComboBox();

    /**
     * Auswahlbox für die Verteilungsgewichte des in <code>edgeBox</code> ausgewählten Kantentyps
     */
    private final AlphabeticalComboBox weightBox = new AlphabeticalComboBox();

    /**
     * Richtung der Kante, die in der <code>edgeBox</code> ausgewählt ist.
     */
    private int choosedEdgeDirection;

    //////////////////////////////////////////////////
    /// edgeBoxSelection und weightBoxSelection   ///
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
    private Object edgeBoxSelection;

    /**
     * Zuletzt ausgewähltes Element in der {@link #weightBox}
     */
    private Object weightBoxSelection;

    /**
     * Konstruktor
     * 
     * @param dialog Dialog, der dieses Panel enthält
     */
    public DistributionWeightEditorPanel(final UserFieldEditorDialog dialog) {
        super(dialog);
        initEdgeBox();
        initWeightBox();
    }

    /**
     * Initialisierung von <code>edgeBox</code>
     */
    private void initEdgeBox() {

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;

        edgeBox.addSeparator(Tool3lgmConstants.getResString("classification_weighting_path"));
        setEdgeBoxContent();
        setActionsForEdgeBox();
        add(edgeBox, constraints);
    }

    /**
     * Methode setzt den Inhalt der <code>edgeBox</code> Es werden nur Kantenklassen hinzugefügt, für die ein Verteilungsgewicht definiert ist.
     */
    private void setEdgeBoxContent() {
        UserFieldDefinitions definitions = getUserFieldDefinitions();
        for (int i = 0; i < ModelConstants.ALL_EDGES.length; i++) {
            // Falls Verteilungsgewichte exisitieren, füge Kantenklasse ein
            if (definitions.hasNumberFields(ModelConstants.ALL_EDGES[i])) {
                edgeBox.addItem(ModelConstants.ALL_EDGES[i], ModelConstants.getFullForwardMetaAssociationName(ModelConstants.ALL_EDGES[i]));
                edgeBox.addItem(ModelConstants.ALL_EDGES[i], ModelConstants.getFullBackwardMetaAssociationName(ModelConstants.ALL_EDGES[i]));
            }
            // sonst, füge nicht ein
        }
    }

    /**
     * Methode setzt die auszuführende Action, bei Änderung der Auswahl in der <code>edgeBox</code>. Werte im Table werden temporär übernommen und die
     * zur gewählten Kantenart gehörigen Verteilungsgewichte in die <code>weightBox</code> eingefügt.
     */
    private void setActionsForEdgeBox() {

        final DistributionWeightEditorPanel finalPanel = this;

        /*
         * Bei Änderung der Auswahl in edgeBox, werden in der weightBox die verfügbaren Verteilungsgewichte angezeigt
         */
        AbstractAction action = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Object o = edgeBox.getSelectedObject();
                if (!(o instanceof Class)) {
                    return;
                }
                stopEditing();
                takeOver();
                // Selektion für nächstes takeOver
                finalPanel.edgeBoxSelection = o;
                finalPanel.setWeightBoxContent();
                finalPanel.drawTable();
                finalPanel.distributeSelectionChangedEvent();
            }
        };
        edgeBox.setAction(action);
    }

    /**
     * Initialisiert die <code>weightBox</code>
     */
    private void initWeightBox() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 0;
        weightBox.addSeparator(Tool3lgmConstants.getResString("weightings"));
        setActionsForWeightBox();
        add(weightBox, constraints);
    }

    /**
     * Methode setzt die auszuführende Aktion, bei Änderung der Auswahl in <code>weightBox</code>. Werte im Table werden temporär übernommen und die
     * zum gewählten Verteilungsgewicht gehörigen Daten in einem neuen Table dargestellt.
     */
    private void setActionsForWeightBox() {
        final DistributionWeightEditorPanel finalPanel = this;
        // Bei Änderung in der weightBox, wird der zum gewählten Verteilungsgewicht und 
        // Kantentyp gehörige Table im Panel dargestellt.
        ItemListener il = new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {

                Object o = weightBox.getSelectedObject();
                if (o == null) {
                    return;
                }
                if (!o.getClass().isAssignableFrom(UserField.class) || !(edgeBox.getSelectedObject() instanceof Class)) {
                    return;
                }
                stopEditing();
                takeOver();

                // Selektion für nächstes takeOver
                finalPanel.weightBoxSelection = o;

                finalPanel.drawTable();
                finalPanel.distributeSelectionChangedEvent();
            }

        };

        weightBox.addItemListener(il);
    }

    /**
     * Setzt den Inhalt der <code>weigthBox</code>. Die für den in der <code>edgeBox</code> gewählten Kantentyp definierten Verteilungsgewichte werden
     * der <code>weigthBox</code> hinzugefügt.
     */
    private void setWeightBoxContent() {

        Class<? extends ModelElement> edgeClass = (Class<? extends ModelElement>) edgeBox.getSelectedObject();

        UserFieldDefinitions definitions = getUserFieldDefinitions();

        weightBox.removeAllItems();
        weightBox.addSeparator(Tool3lgmConstants.getResString("weightings"));

        for (UserField uf : definitions.getUserFields(edgeClass)) {
            if (uf.isClassificationUserField()) {
                weightBox.addItem(uf);
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
        if (edgeBoxSelection != null) {
            UserField selectedWeigth = (UserField) weightBoxSelection;
            Vector<Object> rowIdentifiers = tableModel.getRowIdentifiers();
            Vector<Object> columnIdentifiers = tableModel.getColumnIdentifiers();

            for (int i = 0; i < rowIdentifiers.size(); i++) {
                // Das ModelElement in der i-ten Zeile
                ModelElement rowElement = ((NamedObjectContainer<ModelElement>) rowIdentifiers.elementAt(i)).getObject();

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
                    ModelElement columnElement = ((NamedObjectContainer<ModelElement>) columnIdentifiers.elementAt(j)).getObject();

                    Kante edge = null;

                    // Richtung der Kante und die Kante selbst ermitteln
                    if (choosedEdgeDirection == Doppelkante.FORWARD) {
                        edge = columnElement.getEdgeTo(rowElement, ((Class<?>) edgeBoxSelection).asSubclass(Kante.class));

                        if (edge == null) {
                            edge = rowElement.getEdgeTo(columnElement, ((Class<?>) edgeBoxSelection).asSubclass(Kante.class));
                        }
                    }
                    if (edge == null && choosedEdgeDirection == Doppelkante.BACKWARD) {
                        edge = rowElement.getEdgeTo(columnElement, ((Class<?>) edgeBoxSelection).asSubclass(Kante.class));

                        if (edge == null) {
                            edge = columnElement.getEdgeTo(rowElement, ((Class<?>) edgeBoxSelection).asSubclass(Kante.class));
                        }
                    }

                    if (edge == null) {
                        continue;
                    }

                    if (newValue.equals(UserField.EMPTY_STRING)) {
                        // Neuen Wert setzen
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
    protected void drawTable() {
        table.removeFromLayoutContainer();
        if (weightBox.getSelectedObject() == null) {
            return;
        }
        Class<? extends Kante> selectedEdgeClass = ((Class<?>) edgeBox.getSelectedObject()).asSubclass(Kante.class);
        String selectedEdgeName = edgeBox.getSelectedItem().toString();
        choosedEdgeDirection = Doppelkante.FORWARD;
        if (!selectedEdgeName.equals(ModelConstants.getFullForwardMetaAssociationName(selectedEdgeClass))) {
            choosedEdgeDirection = Doppelkante.BACKWARD;
        }
        UserField selectedWeigthUserField = (UserField) weightBox.getSelectedObject();
        AbstractUserFieldTableModel uftm = new UserFieldWeightTableModel(getDialog().getGraphDocument(), selectedEdgeClass, choosedEdgeDirection, selectedWeigthUserField);
        UserFieldTableController tec = UserFieldTableController.getNewDistributionWeightTableController(uftm);
        super.modifyTable(uftm, tec);
    }

}
