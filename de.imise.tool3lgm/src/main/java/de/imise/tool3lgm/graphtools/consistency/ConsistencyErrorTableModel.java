/**
 * 
 */
package de.imise.tool3lgm.graphtools.consistency;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.consistency.error.CardinalityError;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.util.NamedObjectContainer;

/**
 * @author AXS
 */
public class ConsistencyErrorTableModel extends DefaultTableModel {

    /**
     * @author astruebi
     */
    public static enum COL_NAMES {
        number, errorType, elementType, element, connectionType, description
    }

    /**
     * COMMENTME
     */
    private static final String ERROR_DESCRIPTION_SUFFIX = "_descrip";

    public ConsistencyErrorTableModel() {
        super();
        COL_NAMES[] colIdentifiers = COL_NAMES.values();
        Vector<String> colNames = new Vector<String>(colIdentifiers.length);
        for (COL_NAMES cn : colIdentifiers) {
            colNames.add(Tool3lgmConstants.getErrString(cn.toString()));
        }
        setColumnIdentifiers(colNames);
    }

    /**
     * @param dataVector
     */
    public void setErrors(ArrayList<CardinalityError> dataVector) {

        if (dataVector == null) {
            dataVector = new ArrayList<CardinalityError>(0);
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
            CardinalityError cardErr = dataVector.get(i);
            String errClassName = cardErr.getClass().getSimpleName();

            // Zeilennummer
            setValueAt(new Integer(i + 1), i, COL_NAMES.number.ordinal());

            // Fehlertyp
            NamedObjectContainer<CardinalityError> type = new NamedObjectContainer<CardinalityError>(cardErr, Tool3lgmConstants.getErrString(errClassName));
            setValueAt(type, i, COL_NAMES.errorType.ordinal());

            ModelElement me = cardErr.getModelElement();

            // Elementtyp
            setValueAt(ModelConstants.getDisplayableName(me.getClass()), i, COL_NAMES.elementType.ordinal());

            // Element
            setValueAt(me, i, COL_NAMES.element.ordinal());

            // Verbindungsart
            Class<? extends Kante> edgeClass = cardErr.getEdgeClass();
            setValueAt(ModelConstants.getFullForwardMetaAssociationName(edgeClass), i, COL_NAMES.connectionType.ordinal());

            // Beschreibung
            setValueAt(Tool3lgmConstants.getErrString(errClassName + ERROR_DESCRIPTION_SUFFIX), i, COL_NAMES.description.ordinal());
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
