package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.PartOfBeziehung;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserField.Style;
import de.imise.util.Alphabetical;
import de.imise.util.NamedObjectContainer;

public class UserFieldWeightTableModel extends AbstractUserFieldTableModel {

    public UserFieldWeightTableModel(final GraphDocument doc) {
        super(doc);
    }

    /**
     * @param doc
     * @param edgeClass
     * @param direction Richtung in der die ausgwählte Kante zu lesen ist. In der Tabelle sthen die Startklassen der Kante in den Zeilen, wenn
     *            <code>DoubleTrace.FORWARD</code> übergeben wurde. Bei <code>DoubleTrace.BACKWARD</code> stehen die Endklassenelemente in den Zeilen.
     * @param field
     * @param columnElement Wenn <code>null</code>, werden alle Spalten-Elemente angezeigt, die gefunden werden. Wenn ein Element übergeben wurde,
     *            dann wird nur die Spalte dieses Elementes angezeigt.
     */
    public UserFieldWeightTableModel(final GraphDocument doc, final Class<? extends Kante> edgeClass, final int direction, final UserField field, final ModelElement columnElement) {
        super(doc);
        if (columnElement == null) {
            setData(edgeClass, direction, field);
        } else {
            setData(edgeClass, direction, field, columnElement);
        }
    }

    /**
     * Erstellt und setzt Verteilungsgewicht-Modeldaten
     * 
     * @param edgeClass
     * @param direction Richtung in der die ausgwählte Kante zu lesen ist. In der Tabelle sthen die Startklassen der Kante in den Zeilen, wenn
     *            <code>DoubleTrace.FORWARD</code> übergeben wurde. Bei <code>DoubleTrace.BACKWARD</code> stehen die Endklassenelemente in den Zeilen.
     * @param field
     */
    private void setData(final Class<? extends Kante> edgeClass, final int direction, final UserField field) {
        Class<? extends ModelElement> rowElementClass = direction == Doppelkante.FORWARD ? Kante.getStartClass(edgeClass) : Kante.getEndClass(edgeClass);
        Class<? extends ModelElement> colElementClass = direction == Doppelkante.FORWARD ? Kante.getEndClass(edgeClass) : Kante.getStartClass(edgeClass);
        List<ModelElement> allRowElements = doc.getModelItems(rowElementClass, false, true);
        List<ModelElement> allColumnElements = doc.getModelItems(colElementClass, false, true);

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
                        edge = re.getEdgeTo(ce, edgeClass);
                    } else {
                        edge = ce.getEdgeTo(re, edgeClass);
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

        // RowHeader aufbauen
        Vector<NamedObjectContainer<?>> rowIdentifiers = new Vector<NamedObjectContainer<?>>(rowElements.length);
        for (int i = 0; i < rowElements.length; i++) {
            ModelElement me = rowElements[i];
            rowIdentifiers.add(new NamedObjectContainer<ModelElement>(me, me.getName()));
        }

        // ColumnHeader aufbauen
        Vector<NamedObjectContainer<?>> columnIdentifiers = new Vector<NamedObjectContainer<?>>(columnElements.length);
        for (int j = 0; j < columnElements.length; j++) {
            ModelElement me = columnElements[j];
            columnIdentifiers.add(new NamedObjectContainer<ModelElement>(me, me.getName()));
        }

        // DataVector aufbauen
        Object[][] data = new Object[rowIdentifiers.size()][columnIdentifiers.size()];
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

    /**
     * Erstellt und setzt Verteilungsgewicht-Modeldaten
     * 
     * @param edgeClass
     * @param direction Richtung in der die ausgwählte Kante zu lesen ist. In der Tabelle sthen die Startklassen der Kante in den Zeilen, wenn
     *            <code>DoubleTrace.FORWARD</code> übergeben wurde. Bei <code>DoubleTrace.BACKWARD</code> stehen die Endklassenelemente in den Zeilen.
     * @param field
     * @param columnElement Wenn <code>null</code>, werden alle Spalten-Elemente angezeigt, die gefunden werden. Wenn ein Element übergeben wurde,
     *            dann wird nur die Spalte dieses Elementes angezeigt.
     */
    private void setData(final Class<? extends Kante> edgeClass, final int direction, final UserField field, final ModelElement columnElement) {

        List<Kante> edges;
        if (PartOfBeziehung.class.isAssignableFrom(edgeClass)) {
            if (direction == Doppelkante.FORWARD) {
                edges = columnElement.getEdgesFrom(Kante.getStartClass(edgeClass), edgeClass);
            } else {
                edges = columnElement.getEdgesTo(Kante.getEndClass(edgeClass), edgeClass);
            }
        } else {
            edges = columnElement.getEdges(edgeClass);
        }

        List<ModelElement> allRowElements = Lists.newArrayList();
        for (Kante edge : edges) {
            ModelElement rowElement = edge.getOther(columnElement);
            allRowElements.add(rowElement);
        }
        Alphabetical.sort(allRowElements);

        List<ModelElement> allColumnElements = ImmutableList.of(columnElement);

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
                        edge = re.getEdgeTo(ce, edgeClass);
                    } else {
                        edge = ce.getEdgeTo(re, edgeClass);
                    }
                } else {
                    edges = ce.getEdgesWith(re, edgeClass);
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

        // RowHeader aufbauen
        Vector<NamedObjectContainer<?>> rowIdentifiers = new Vector<NamedObjectContainer<?>>(rowElements.length);
        for (int i = 0; i < rowElements.length; i++) {
            ModelElement me = rowElements[i];
            rowIdentifiers.add(new NamedObjectContainer<ModelElement>(me, me.getName()));
        }

        // ColumnHeader aufbauen
        Vector<NamedObjectContainer<?>> columnIdentifiers = new Vector<NamedObjectContainer<?>>(columnElements.length);
        for (int j = 0; j < columnElements.length; j++) {
            ModelElement me = columnElements[j];
            columnIdentifiers.add(new NamedObjectContainer<ModelElement>(me, me.getName()));
        }

        // DataVector aufbauen
        Object[][] data = new Object[rowIdentifiers.size()][columnIdentifiers.size()];
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
