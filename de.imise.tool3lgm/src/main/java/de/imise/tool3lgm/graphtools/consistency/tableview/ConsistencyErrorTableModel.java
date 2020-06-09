/**
 *
 */
package de.imise.tool3lgm.graphtools.consistency.tableview;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.util.Collection;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.consistency.checker.ConsistencyChecker;
import de.imise.tool3lgm.graphtools.consistency.error.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.consistency.error.MissingPathError;
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
    public static enum ColumnNames {
        NUMBER,
        ERROR_TYPE,
        ELEMENT_TYPE,
        ELEMENT,
        CONNECTION_TYPE,
        DESCRIPTION;

        public String getDisplayableName() {
            return getResString("ColumnNames_" + name());
        }
    }

    /**
     *
     */
    public ConsistencyErrorTableModel() {
        super();
        ColumnNames[] colIdentifiers = ColumnNames.values();
        Vector<String> colNames = new Vector<>(colIdentifiers.length);
        for (ColumnNames cn : colIdentifiers) {
            colNames.add(cn.getDisplayableName());
        }
        setColumnIdentifiers(colNames);
    }

    /**
     * @param checker
     */
    public void setErrors(final ConsistencyChecker checker) {
        Collection<AbstractConsistencyError> errors = checker.getAllInconsistencies();
        dataVector.clear();
        setRowCount(errors.size());

        int i = 0;
        for (AbstractConsistencyError error : errors) {

            //ignore unfixable missingPath errors. They can be only fixed
            //with other fixable missingPathErrors (if defined)
            if (error instanceof MissingPathError) {
                if (!checker.isSolutionExecuteable(error)) {
                    int rowCount = getRowCount();
                    setRowCount(rowCount - 1);
                    continue;
                }
            }

            // Zeilennummer
            setValueAt(i + 1, i, ColumnNames.NUMBER);

            // Fehlertyp
            String errorTypeString = error.getTypeString();
            NamedObjectContainer<AbstractConsistencyError> type = new NamedObjectContainer<>(error, errorTypeString);
            setValueAt(type, i, ColumnNames.ERROR_TYPE);

            ModelElement me = error.getModelElement();

            // Elementtyp
            GDCollection gdcoll = error.getCollection();
            ElementsNameBuilder elementsNameBuilder = gdcoll.getElementsNameBuilder();
            Class<? extends ModelElement> elementClass = me.getClass();
            String displayableClassName = elementsNameBuilder.getDisplayableName(elementClass);
            setValueAt(displayableClassName, i, ColumnNames.ELEMENT_TYPE);

            // Element
            setValueAt(me, i, ColumnNames.ELEMENT);

            // Verbindungsart / Feld
            String errorFieldString = error.getErrorFieldString();
            setValueAt(errorFieldString, i, ColumnNames.CONNECTION_TYPE);

            // Beschreibung
            String errorDescription = error.getMessage();
            String errorDescriptionToolTip = error.getLongMessage();
            NamedObjectContainer<String> errorDescriptionContainer = new NamedObjectContainer<>(errorDescriptionToolTip, errorDescription);
            setValueAt(errorDescriptionContainer, i, ColumnNames.DESCRIPTION);

            i++;
        }
    }

    /**
     * @param aValue
     * @param row
     * @param columnIdentifier
     */
    private void setValueAt(final Object aValue, final int row, final ColumnNames columnIdentifier) {
        int column = columnIdentifier.ordinal();
        setValueAt(aValue, row, column);
    }

}
