/**
 *
 */
package de.imise.tool3lgm.graphtools.consistency;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractError;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
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
        description
    }

    public ConsistencyErrorTableModel() {
        super();
        COL_NAMES[] colIdentifiers = COL_NAMES.values();
        Vector<String> colNames = new Vector<>(colIdentifiers.length);
        for (COL_NAMES cn : colIdentifiers) {
            colNames.add(Tool3lgmConstants.getErrString(cn.toString()));
        }
        setColumnIdentifiers(colNames);
    }

    /**
     * @param dataVector
     */
    public void setErrors(List<AbstractError> dataVector) {

        if (dataVector == null) {
            dataVector = new ArrayList<AbstractError>(0);
        }
        // System.err.println(dataVector.size());
        // System.err.println(this.dataVector.size());
        // System.err.println(getRowCount());
        // System.err.println("---");

        this.dataVector.clear();

        setRowCount(dataVector.size());

        // System.err.println(this.dataVector.size());
        // System.err.println(getRowCount());
        // System.err.println("---");

        int nullRows = 0;
        for (int i = 0; i < dataVector.size(); i++) {
            AbstractError error = dataVector.get(i);

            // Zeilennummer
            setValueAt(new Integer(i + 1), i, COL_NAMES.number.ordinal());

            // Fehlertyp
            NamedObjectContainer<AbstractError> type = new NamedObjectContainer<>(error, error.getTypeString());
            setValueAt(type, i, COL_NAMES.errorType.ordinal());

            ModelElement me = error.getModelElement();

            // Elementtyp
            setValueAt(ModelConstants.getDisplayableName(me.getClass()), i, COL_NAMES.elementType.ordinal());

            // Element
            setValueAt(me, i, COL_NAMES.element.ordinal());

            // Verbindungsart / Feld
            setValueAt(error.getErrorFieldString(), i, COL_NAMES.connectionType.ordinal());

            // Beschreibung
            setValueAt(error.getMessage(), i, COL_NAMES.description.ordinal());

        }
        // System.err.println(this.dataVector.size());
        // System.err.println(getRowCount());
        // System.err.println("---");

        setRowCount(getRowCount() - nullRows);

        // System.err.println(this.dataVector.size());
        // System.err.println(getRowCount());
        // System.err.println("---");
        // System.err.println();
        //
        // new Error().printStackTrace();

    }

}
