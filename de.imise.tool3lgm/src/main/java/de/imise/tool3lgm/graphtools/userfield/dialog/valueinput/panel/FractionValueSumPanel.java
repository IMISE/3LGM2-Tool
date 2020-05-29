package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getStartClass;
import static de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.AbstractElementTypeUserFieldEditorPanel.InsertType.AS_EDGE_BACKWARD;
import static de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.AbstractElementTypeUserFieldEditorPanel.InsertType.AS_EDGE_FORWARD;
import static de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.AbstractElementTypeUserFieldEditorPanel.InsertType.AS_EDGE_FORWARD_AND_BACKWARD;
import static de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.AbstractElementTypeUserFieldEditorPanel.InsertType.NO;

import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.AbstractAction;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitionsAnalyzer;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorDialog;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.AbstractUserFieldTableModel;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.FractionValueSumTableModel;
import de.imise.util.swing.component.AlphabeticalComboBox;

public class FractionValueSumPanel extends ClassificationNumberFormulaPanel {

    /**
     * Auswahlbox für ein konkretes Element, für das die Teilwertsummen angezigt werden sollen
     */
    private final AlphabeticalComboBox elementBox = new AlphabeticalComboBox();

    public FractionValueSumPanel(final UserFieldEditorDialog dialog, final String name) {
        super(dialog, Edge.class, name);
        initElementBox();
    }

    /**
     * Im Gegensatz zur Oberklasse wird hier nur true zürück gegeben, wenn die übergebene Elementklasse Formel-UserFields hat,
     * die eine einfache Teilwertsumme sind.
     *
     * @param elementClass Die <code>ElementClass</code>e, deren <code>UserField</code>s geprüft werden sollen.
     * @param definitions Die <code>UserFieldDefinition</code>s
     * @return Wenn mindestens ein <code>UserField</code> vom Typ Kennzahl (<code>UserField.CLASSIFICATION_NUMBER_FORMULA</code>) ist
     *         und die Formel selbst eine einfache Teilwertsumme ist
     */
    @Override
    protected final InsertType getInsertType(final Class<? extends ModelElement> elementClass, final UserFieldDefinitions definitions) {
        //nur Kanten zulassen
        InsertType insertType = NO;
        if (Edge.class.isAssignableFrom(elementClass)) {
            Class<? extends Edge> edgeClass = elementClass.asSubclass(Edge.class);
            //vorwärts
            Class<? extends ModelElement> startElementClass = getStartClass(edgeClass);
            //alle einfachen Teilwertsummen-UserFields holen, die für die startElementClass über die edgeClass definiert sind
            List<UserField> fractionValueSumUserFields = definitions.getFractionValueSumUserFields(startElementClass, edgeClass);
            if (!fractionValueSumUserFields.isEmpty()) {
                insertType = AS_EDGE_FORWARD;
            }
            //rückwärts
            Class<? extends ModelElement> endElementClass = getEndClass(edgeClass);
            //alle einfachen Teilwertsummen-UserFields holen, die für die endElementClass über die edgeClass definiert sind
            fractionValueSumUserFields = definitions.getFractionValueSumUserFields(endElementClass, edgeClass);
            if (!fractionValueSumUserFields.isEmpty()) {
                insertType = insertType == AS_EDGE_FORWARD ? AS_EDGE_FORWARD_AND_BACKWARD : AS_EDGE_BACKWARD;
            }
        }
        return insertType;
    }

    private Class<? extends Edge> getSelectedEdgeClass() {
        Object o = elementTypeBox.getSelectedObject();
        if (o instanceof Class) {
            return ((Class<?>) o).asSubclass(Edge.class);
        }
        return null;
    }

    @Override
    protected boolean hasSelectedItem() {
        boolean hasSelectedItem = super.hasSelectedItem();
        if (hasSelectedItem) {
            hasSelectedItem = hasSelectedElement();
        }
        return hasSelectedItem;
    }

    /**
     * @return <code>true</code>, if in the elementBox a ModelElement is selected
     */
    private boolean hasSelectedElement() {
        Object selectedElement = elementBox.getSelectedObject();
        boolean hasSelectedElement = selectedElement != null && selectedElement instanceof ModelElement;
        return hasSelectedElement;
    }

    @Override
    protected void initSelectFirstItem() {
        super.initSelectFirstItem();
        if (!hasSelectedElement()) {
            for (int i = 0; i < elementBox.getItemCount(); i++) {
                if (elementBox.getItemAt(i) instanceof ModelElement) {
                    elementBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    /**
     * Methode setzt die auszuführende Action, bei Änderung der Auswahl in der <code>elementTypeBox</code>.
     */
    @Override
    protected void setActionsForElementTypeBox() {
        super.setActionsForElementTypeBox();
        final FractionValueSumPanel finalPanel = this;

        /*
         * Bei Änderung der Auswahl in elementTypeBox, werden in der elementBox die verfügbaren Elemente angezeigt
         */
        AbstractAction action = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                //sobald sich in dieser Box die Selektion ändert, dann gleich in der anderen Box das erste Item auswählen
                finalPanel.setElementBoxContent();
                finalPanel.initSelectFirstItem();
                finalPanel.drawTable();
                finalPanel.distributeSelectionChangedEvent();
            }
        };
        elementTypeBox.setAction(action);
    }

    /**
     * Initialisiert die <code>weightBox</code>
     */
    private void initElementBox() {
        elementBox.setEnabled(false);
        setActionsForElementBox();
        addComponent(elementBox);
    }

    /**
     * @return <code>true</code>, wenn die selektierte Kantenklasse in Vorwärtsrichtung geselen wird, sonst <code>false</code>.
     */
    private boolean isSelectedEdgeDirectionForward() {
        Class<? extends Edge> selectedEdgeClass = getSelectedEdgeClass();
        ElementsNameBuilder elementsNameBuilder = dialog.getElementsNameBuilder();
        String fullForwardMetaAssociationName = elementsNameBuilder.getFullForwardMetaAssociationName(selectedEdgeClass);
        String selectedEdgeClassVisibleName = elementTypeBox.getSelectedItem().toString();
        boolean isSelectedEdgeDirectionForward = fullForwardMetaAssociationName.equals(selectedEdgeClassVisibleName);
        return isSelectedEdgeDirectionForward;
    }

    /**
     * Setzt den Inhalt der <code>weigthBox</code>. Die für den in der <code>edgeTypeBox</code> gewählten Kantentyp definierten Verteilungsgewichte
     * werden
     * der <code>weigthBox</code> hinzugefügt.
     */
    private void setElementBoxContent() {
        UserFieldDefinitions definitions = getUserFieldDefinitions();
        elementBox.removeAllItems();
        elementBox.addSeparator(getResString("userFieldEditor_element"));
        Class<? extends Edge> selectedEdgeClass = getSelectedEdgeClass();
        elementBox.setEnabled(selectedEdgeClass != null);
        if (elementBox.isEnabled()) {
            boolean selectedEdgeDirectionForward = isSelectedEdgeDirectionForward();
            Class<? extends ModelElement> elementClass = selectedEdgeDirectionForward ? getStartClass(selectedEdgeClass) : getEndClass(selectedEdgeClass);
            GraphDocument doc = definitions.getCollection().getMainDoc();
            List<ModelElement> elements = doc.getModelItems(elementClass, true, true);
            for (ModelElement me : elements) {
                elementBox.addItem(me);
            }
        }
    }

    /**
     * Methode setzt die auszuführende Aktion, bei Änderung der Auswahl in <code>weightBox</code>. Werte im Table werden temporär übernommen und die
     * zum gewählten Verteilungsgewicht gehörigen Daten in einem neuen Table dargestellt.
     */
    private void setActionsForElementBox() {
        final FractionValueSumPanel finalPanel = this;
        ItemListener il = e -> {
            finalPanel.drawTable();
            finalPanel.distributeSelectionChangedEvent();
        };

        elementBox.addItemListener(il);
    }

    @Override
    protected AbstractUserFieldTableModel getTableModel() {
        Class<? extends Edge> edgeClass = getSelectedEdgeClass();
        boolean edgeForwardDirection = isSelectedEdgeDirectionForward();
        ModelElement me = (ModelElement) elementBox.getSelectedObject();
        AbstractUserFieldTableModel uftm = new FractionValueSumTableModel(me, edgeClass, edgeForwardDirection, hierarchyTypeFilterPane.showTopLevel(), hierarchyTypeFilterPane.showInner(), hierarchyTypeFilterPane.showLeafs());
        return uftm;
    }

    @Override
    public boolean hasValues() {
        UserFieldDefinitions definitions = dialog.getUserFieldDefinitions();
        UserFieldDefinitionsAnalyzer analyzer = definitions.getAnalyzer();
        boolean hasValues = analyzer.hasSimpleFractionValueSums();
        return hasValues;
    }
}
