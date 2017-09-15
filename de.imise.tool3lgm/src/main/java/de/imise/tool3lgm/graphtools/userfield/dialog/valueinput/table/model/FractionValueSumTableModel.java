package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model;

import static de.imise.tool3lgm.graphtools.metamodel.Edge.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.FORWARD;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.getStartClass;

import java.util.List;
import java.util.Vector;

import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.util.NamedObjectContainer;

public class FractionValueSumTableModel extends AbstractUserFieldTableModel {

    public FractionValueSumTableModel(final GraphDocument doc) {
        super(doc);
    }

    public FractionValueSumTableModel(final ModelElement me, final Class<? extends Edge> edgeClass, final boolean edgeForwardDirection, final boolean showTopLevel, final boolean showInner, final boolean showLeafs) {
        super(me.getCollection().getMainGraphDocument());
        setData(me, edgeClass, edgeForwardDirection, showTopLevel, showInner, showLeafs);
    }

    /**
     * Entfernt je nach Werten der übergebenen boolean-Varibalen alle Elemente, die nicht dazu passen aus der übergebenen Liste
     *
     * @param elements
     */
    private void removeNotVisibleHierarchyElements(final List<? extends ModelElement> elements, final boolean showTopLevel, final boolean showInner, final boolean showLeafs) {
        int elementsSize = elements.size();
        if (elementsSize > 0) {
            for (int i = elementsSize; i <= 0; i--) {
                ModelElement other = elements.get(i);
                boolean stay = false;
                if (showTopLevel && !other.hasDirectParentContainer(doc)) { // Top-Level-E. anfügen
                    stay = true;
                } else if (showInner && other.hasDirectParentContainer(doc) && other.hasDirectPartContainer(doc)) { // Innere E. anfügen
                    stay = true;
                } else if (showLeafs && !other.hasDirectPartContainer(doc)) { // Blatt-E. anfügen
                    stay = true;
                }
                if (!stay) {
                    elements.remove(i);
                }
            }
        }
    }

    /**
     * Erstellt und setzt Kennzahlen-Modeldaten
     */
    private void setData(final ModelElement me, final Class<? extends Edge> edgeClass, final boolean edgeForwardDirection, final boolean showTopLevel, final boolean showInner, final boolean showLeafs) {
        //alle Teilwersummen Userfields des Elementes holen
        // Ermitteln der UserFields zu elementClass
        Class<? extends ModelElement> elementClass = me.getClass();
        List<UserField> fractionValueSumUserFields = definitions.getFractionValueSumUserFields(elementClass, edgeClass);

        Class<? extends ModelElement> fractionValueSumSourceClass = edgeForwardDirection ? getEndClass(edgeClass) : getStartClass(edgeClass);

        int direction = edgeForwardDirection ? FORWARD : BACKWARD;
        //alle mit dem Element über die Edge verbundenen Elemente holen
        List<ModelElement> modelElements = me.getConnectedElements(fractionValueSumSourceClass, edgeClass, direction, true);

        removeNotVisibleHierarchyElements(modelElements, showTopLevel, showInner, showLeafs);

        if (modelElements.size() == 0 || fractionValueSumUserFields.size() == 0) {
            modelElements.clear();
            fractionValueSumUserFields.clear();
        }

        Vector<NamedObjectContainer<?>> rowIdentifiers = new Vector<>(modelElements.size());
        // RowHeader aufbauen
        for (ModelElement rowElement : modelElements) {
            NamedObjectContainer<ModelElement> rowElementContainer = new NamedObjectContainer<>(rowElement, rowElement.getName());
            rowIdentifiers.add(rowElementContainer);
        }

        Vector<NamedObjectContainer<?>> columnIdentifiers = new Vector<>(fractionValueSumUserFields.size());
        // ColumnHeader aufbauen
        for (UserField userField : fractionValueSumUserFields) {
            NamedObjectContainer<UserField> columnUserFieldContainer = new NamedObjectContainer<>(userField, userField.getName());
            columnIdentifiers.add(columnUserFieldContainer);
            //der folgende Aufruf stellt sicher, dass immer die aktuellen Werte für die Teilwerte berechnte werden. Beim Abfragen des Wertes des UserFields
            //wird die Map mit den Teilwerten gefüllt, die unten beim Aufbau des Datenvektors abfragt wird.
            userField.getValue(me);
        }

        //DataVector aufbauen
        Object[][] data = new Object[modelElements.size()][fractionValueSumUserFields.size()];
        for (int i = 0; i < data.length; i++) {
            ModelElement fractionValueSource = modelElements.get(i);
            for (int j = 0; j < data[0].length; j++) {
                UserField userField = fractionValueSumUserFields.get(j);
                String value = definitions.getPartValueSumSinglePartResults().get(me, userField, fractionValueSource);
                data[i][j] = new NamedObjectContainer<>(userField, value);
            }
        }
        //Daten setzen
        setDataVector(data, columnIdentifiers, rowIdentifiers);
    }
}
