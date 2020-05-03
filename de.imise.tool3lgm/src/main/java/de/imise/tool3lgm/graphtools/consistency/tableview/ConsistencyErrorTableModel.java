/**
 *
 */
package de.imise.tool3lgm.graphtools.consistency.tableview;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.util.NamedObjectContainer;

/**
 * @author AXS
 */
public class ConsistencyErrorTableModel extends DefaultTableModel {

    /**
     * @author astruebi
     */
    public static enum COL_NAMES {
        number,
        errorType,
        elementType,
        element,
        connectionType,
        description;

        public String getDisplayableName() {
            return getResString("COL_NAMES_" + toString());
        }
    }

    /**
     *
     */
    public ConsistencyErrorTableModel() {
        super();
        COL_NAMES[] colIdentifiers = COL_NAMES.values();
        Vector<String> colNames = new Vector<>(colIdentifiers.length);
        for (COL_NAMES cn : colIdentifiers) {
            colNames.add(cn.getDisplayableName());
        }
        setColumnIdentifiers(colNames);
    }

    /**
     * @param dataVector
     */
    public void setErrors(Collection<AbstractConsistencyError> dataVector) {
        if (dataVector == null) {
            dataVector = new ArrayList<>(0);
        }
        this.dataVector.clear();
        setRowCount(dataVector.size());

        int i = 0;
        for (AbstractConsistencyError error : dataVector) {

            // Zeilennummer
            setValueAt(i + 1, i, COL_NAMES.number);

            // Fehlertyp
            String errorTypeString = error.getTypeString();
            NamedObjectContainer<AbstractConsistencyError> type = new NamedObjectContainer<>(error, errorTypeString);
            setValueAt(type, i, COL_NAMES.errorType);

            ModelElement me = error.getModelElement();

            // Elementtyp
            GDCollection gdcoll = error.getCollection();
            ElementsNameBuilder elementsNameBuilder = gdcoll.getElementsNameBuilder();
            Class<? extends ModelElement> elementClass = me.getClass();
            String displayableClassName = elementsNameBuilder.getDisplayableName(elementClass);
            setValueAt(displayableClassName, i, COL_NAMES.elementType);

            // Element
            setValueAt(me, i, COL_NAMES.element);

            // Verbindungsart / Feld
            String errorFieldString = error.getErrorFieldString();
            setValueAt(errorFieldString, i, COL_NAMES.connectionType);

            // Beschreibung
            String errorDescription = error.getMessage();
            String errorDescriptionToolTip = error.getLongMessage();
            NamedObjectContainer<String> errorDescriptionContainer = new NamedObjectContainer<>(errorDescriptionToolTip, errorDescription);
            setValueAt(errorDescriptionContainer, i, COL_NAMES.description);

            i++;
        }
    }

    /**
     * @param aValue
     * @param row
     * @param columnIdentifier
     */
    private void setValueAt(final Object aValue, final int row, final COL_NAMES columnIdentifier) {
        int column = columnIdentifier.ordinal();
        setValueAt(aValue, row, column);
    }

}
