package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.cell;

import java.util.Vector;

import javax.swing.DefaultCellEditor;

import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.WeightReplacer;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTable;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.WeightReplaceTableModel;
import de.imise.util.NamedObjectContainer;
import de.imise.util.swing.component.AlphabeticalComboBox;

public class WeightReplaceTableCell extends UserFieldActivatedTableCell {

    private final Class<? extends Edge> edgeClass;

    private final String modelElementHash;

    public WeightReplaceTableCell(final NamedObjectContainer<UserField> noc, final UserFieldTable table, final ModelElement me, final Class<? extends Edge> edgeClass, final int column) {
        super(noc, table, column);
        modelElementHash = me.getHashString();
        this.edgeClass = edgeClass;
    }

    @Override
    protected void initEditor(final int column) {
        Vector<NamedObjectContainer<UserField>> columnIdentifiers = (Vector<NamedObjectContainer<UserField>>) table.getColumnIdentifiers();
        UserField thisCellColumnIdentifier = columnIdentifiers.get(column).getObject();

        WeightReplacer replacer = extractReplacer(columnIdentifiers);

        //true im Konstuktor bedeutet, dass man einen Leerwert zur Auswahl hat. Der Leerwert
        //steht für "keine Ersetzung"
        AlphabeticalComboBox component = new AlphabeticalComboBox(false);
        for (NamedObjectContainer<UserField> container : columnIdentifiers) {
            UserField colUserField = container.getObject();
            NamedObjectContainer<UserField> replaceValue = null;
            if (thisCellColumnIdentifier != colUserField) {
                if (colUserField == null) {
                    replaceValue = WeightReplaceTableModel.getUniformlyDistributedValueContainer();
                } else {
                    replaceValue = WeightReplaceTableModel.getValueContainer(colUserField);
                }
            } else {
                replaceValue = WeightReplaceTableModel.getBlankValueContainer(colUserField);
            }
            component.addItem(replaceValue);
            //Item ggf. selektieren
            UserField userField = value.getObject();
            if (colUserField == null) {
                String replaceHash = replacer.getUniformDistributionReplacement(modelElementHash, edgeClass);
                if (replaceHash == null && userField == null) {
                    component.setSelectedItem(replaceValue);
                } else if (userField != null && userField.getHashCode().equals(replaceHash)) {
                    component.setSelectedItem(replaceValue);
                }
            } else {
                String userFieldHashToReplace = colUserField.getHashCode();
                String replaceHash = replacer.getReplacement(modelElementHash, userFieldHashToReplace);
                if (replaceHash == null && colUserField.equals(userField)) {
                    component.setSelectedItem(replaceValue);
                } else if (userField != null && userField.getHashCode().equals(replaceHash)) {
                    component.setSelectedItem(replaceValue);
                }
            }
        }
        editor = new DefaultCellEditor(component);
    }

    private WeightReplacer extractReplacer(final Vector<NamedObjectContainer<UserField>> columnIdentifiers) {
        UserFieldDefinitions definitions = null;
        for (NamedObjectContainer<UserField> container : columnIdentifiers) {
            UserField colUserField = container.getObject();
            if (colUserField != null) {
                definitions = colUserField.getDefinitions();
                break;
            }
        }
        return definitions != null ? definitions.getWeightReplacer() : null;
    }

    /**
     * Gibt den tatsächlichen Wert dieser Zelle wieder und speicher ihn unter {@link #value} ab. <br>
     * Falls der Wert im Editor <code>""</code> entspricht, wird ein neuer {@link NamedObjectContainer} mit {@link #userField} und
     * <code>"EMPTY_STRING"</code> zurückgegeben. Falls sich der Wert im Editor nicht auf BigDecimal parsen lässt, wird ein neuer
     * {@link NamedObjectContainer} mit {@link #userField} und <code>"NUMBER_FORMAT_ERROR"</code> zurückgegeben. Sonst wird ein neuer
     * {@link NamedObjectContainer} mit {@link #userField} und dem String im Editor zurückgegeben.
     * 
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    @Override
    public Object getCellEditorValue() {
        //hier muss wieder das Standardverhalten hergestellt werden, das die Oberklasse UserFieldActivatedTableCell
        //überschrieben hatte
        Object newValue = editor.getCellEditorValue();
        return newValue;
    }

    @Override
    protected void initRenderer() {
        super.initRenderer();
    }

    @Override
    protected void update() {
        if (value != null) {
            String valueString = value.toString();
            if (valueString.equals(UserField.EMPTY_STRING)) {
                text = RENDERER_EMPTY_STRING;
            } else {
                text = valueString;
            }
        }
    }

}
