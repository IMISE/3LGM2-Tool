package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model;

import java.util.ArrayList;

import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.PartOfBeziehung;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserField.Style;
import de.imise.util.NamedObjectContainer;

public class UserFieldWeightTableModel extends AbstractUserFieldTableModel {

    public UserFieldWeightTableModel(final GraphDocument doc) {
        super(doc);
    }

    public UserFieldWeightTableModel(final GraphDocument doc, final Class<? extends Kante> edgeClass, final int direction, final UserField field) {
        super(doc);
        setData(edgeClass, direction, field);
    }

    /**
     * Erstellt und setzt Verteilungsgewicht-Modeldaten
     * 
     * @param edgeClass
     * @param rowElementClasses
     * @param colElementClasses
     * @param field
     * @param direction Richtung in der die ausgwählte Kante zu lesen ist. In der Tabelle sthen die Startklassen der Kante in den Zeilen, wenn
     *            <code>DoubleTrace.FORWARD</code> übergeben wurde. Bei <code>DoubleTrace.BACKWARD</code> stehen die Endklassenelemente in den Zeilen.
     */
    public void setData(final Class<? extends Kante> edgeClass, final int direction, final UserField field) {
        ArrayList<ModelElement> allRowElements = doc.getModelItems(Kante.getStartClass(edgeClass), false, true);
        ArrayList<ModelElement> allColumnElements = doc.getModelItems(Kante.getEndClass(edgeClass), false, true);
        //alle Elemente entfernen, die keine Kante haben, an die ein Verteilungsgewicht gehängt werden könnte
        for (int i = allRowElements.size() - 1; i >= 0; i--) {
            ModelElement me = allRowElements.get(i);
            if (me.getEdges(edgeClass).isEmpty()) {
                allRowElements.remove(i);
            }
        }
        for (int i = allColumnElements.size() - 1; i >= 0; i--) {
            ModelElement me = allColumnElements.get(i);
            if (me.getEdges(edgeClass).isEmpty()) {
                allColumnElements.remove(i);
            }
        }

        ModelElement[] rowElements = new ModelElement[allRowElements.size()];
        ModelElement[] columnElements = new ModelElement[allColumnElements.size()];
        Object[][] temp_data = new Object[allRowElements.size()][allColumnElements.size()];

        // temp_data, rowElements, columnElements erstellen
        for (int r = 0; r < rowElements.length; r++) {
            ModelElement re = allRowElements.get(r);
            for (int c = 0; c < columnElements.length; c++) {
                ModelElement ce = allColumnElements.get(c);
                Kante edge = null;

                if (PartOfBeziehung.class.isAssignableFrom(edgeClass)) {
                    if (direction == Doppelkante.FORWARD) {
                        edge = ce.getEdgeTo(re, edgeClass);
                    } else {
                        edge = re.getEdgeTo(ce, edgeClass);
                    }
                } else {
                    ArrayList<Kante> edges = ce.getEdgesWith(re, edgeClass);
                    if (!edges.isEmpty()) {
                        edge = edges.get(0);
                    }
                }

                columnElements[c] = ce;
                rowElements[r] = re;

                if (edge == null) {
                    continue;
                }

                //die nicht editierbaren Formeln müssen gleich formatiert dargestellt werden
                String value = field.hasStyle(Style.CLASSIFICATION_NUMBER_FORMULA) ? field.getFormattedValue(edge, true) : field.getValue(edge);
                temp_data[r][c] = new NamedObjectContainer<UserField>(field, value);

            }
        }

        //Alphabetical.sort(columnElements);

        // RowHeader aufbauen
        Object[] rowIdentifiers = new Object[rowElements.length];
        for (int i = 0; i < rowIdentifiers.length; i++) {
            ModelElement me = rowElements[i];
            rowIdentifiers[i] = new NamedObjectContainer<ModelElement>(me, me.getName());
        }

        // ColumnHeader aufbauen
        Object[] columnIdentifiers = new Object[columnElements.length];
        for (int j = 0; j < columnIdentifiers.length; j++) {
            ModelElement me = columnElements[j];
            columnIdentifiers[j] = new NamedObjectContainer<ModelElement>(me, me.getName());
        }

        // DataVector aufbauen
        Object[][] data = new Object[rowIdentifiers.length][columnIdentifiers.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                if (temp_data[i][j] != null) {
                    data[i][j] = temp_data[i][j];
                }
            }
        }

        // Daten setzen
        this.setDataVector(data, columnIdentifiers, rowIdentifiers);
    }

    @Override
    public final boolean isCellEditable(final int row, final int col) {
        //wenn die Zelle einen Formelwert (Referenz-Funktion) darstellt, darf die Zelle nicht
        //editierbar sein
        Object value = getValueAt(row, col);
        if (value != null) {
            NamedObjectContainer<UserField> cellValue = (NamedObjectContainer<UserField>) value;
            UserField userField = cellValue.getObject();
            if (userField.hasStyle(Style.CLASSIFICATION_NUMBER_FORMULA)) {
                return false;
            }
        }
        return super.isCellEditable(row, col);
    }
}
