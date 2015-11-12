package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model;

import java.util.List;
import java.util.Vector;

import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.util.NamedObjectContainer;

public class FractionValueSumTableModel extends AbstractUserFieldTableModel {

    public FractionValueSumTableModel(final GraphDocument doc) {
        super(doc);
    }

    public FractionValueSumTableModel(final ModelElement me, final Class<? extends Kante> edgeClass, final boolean edgeForwardDirection, final boolean showTopLevel, final boolean showInner, final boolean showLeafs) {
        super(me.getCollection().getMainGraphDocument());
        setData(me, edgeClass, edgeForwardDirection, showTopLevel, showInner, showLeafs);
    }

    /**
     * Entfernt je nach Werten der übergebenen boolean-Varibalen alle Elemente, die nicht dazu passen aus der übergebenen Liste
     * 
     * @param elements
     */
    private void removeNotVisibleHierarchyElements(final List<? extends ModelElement> elements, final boolean showTopLevel, final boolean showInner, final boolean showLeafs) {
        for (int i = elements.size(); i <= 0; i--) {
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

    /**
     * Erstellt und setzt Kennzahlen-Modeldaten
     */
    private void setData(final ModelElement me, final Class<? extends Kante> edgeClass, final boolean edgeForwardDirection, final boolean showTopLevel, final boolean showInner, final boolean showLeafs) {
        //alle Teilwersummen Userfields des Elementes holen
        // Ermitteln der UserFields zu elementClass
        Class<? extends ModelElement> elementClass = me.getClass();
        List<UserField> fractionValueSumUserFields = definitions.getFractionValueSumUserFields(elementClass, edgeClass);

        Class<? extends ModelElement> fractionValueSumSourceClass = edgeForwardDirection ? Kante.getEndClass(edgeClass) : Kante.getStartClass(edgeClass);

        int direction = edgeForwardDirection ? Doppelkante.FORWARD : Doppelkante.BACKWARD;
        //alle mit dem Element über die Kante verbundenen Elemente holen
        List<ModelElement> modelElements = me.getConnectedElements(fractionValueSumSourceClass, edgeClass, direction);

        removeNotVisibleHierarchyElements(modelElements, showTopLevel, showInner, showLeafs);

        if (modelElements.size() == 0 || fractionValueSumUserFields.size() == 0) {
            modelElements.clear();
            fractionValueSumUserFields.clear();
        }

        Vector<NamedObjectContainer<?>> rowIdentifiers = new Vector<NamedObjectContainer<?>>(modelElements.size());
        // RowHeader aufbauen
        for (ModelElement rowElement : modelElements) {
            NamedObjectContainer<ModelElement> rowElementContainer = new NamedObjectContainer<ModelElement>(rowElement, rowElement.getName());
            rowIdentifiers.add(rowElementContainer);
        }

        Vector<NamedObjectContainer<?>> columnIdentifiers = new Vector<NamedObjectContainer<?>>(fractionValueSumUserFields.size());
        // ColumnHeader aufbauen
        for (UserField userField : fractionValueSumUserFields) {
            NamedObjectContainer<UserField> columnUserFieldContainer = new NamedObjectContainer<UserField>(userField, userField.getName());
            columnIdentifiers.add(columnUserFieldContainer);
        }

        //DataVector aufbauen
        Object[][] data = new Object[modelElements.size()][fractionValueSumUserFields.size()];
        for (int i = 0; i < data.length; i++) {
            ModelElement fractionValueSource = modelElements.get(i);
            for (int j = 0; j < data[0].length; j++) {
                UserField userField = fractionValueSumUserFields.get(j);
                String value = definitions.getPartValueSumSinglePartResults().get(me, userField, fractionValueSource);
                data[i][j] = new NamedObjectContainer<UserField>(userField, value);
            }
        }
        //Daten setzen
        setDataVector(data, columnIdentifiers, rowIdentifiers);
    }
}
